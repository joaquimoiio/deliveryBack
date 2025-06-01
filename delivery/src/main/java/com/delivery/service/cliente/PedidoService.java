package com.delivery.service.cliente;

import com.delivery.dto.cliente.PedidoDTO;
import com.delivery.entity.*;
import com.delivery.entity.enums.StatusPagamento;
import com.delivery.entity.enums.StatusPedido;
import com.delivery.exception.BusinessException;
import com.delivery.exception.NotFoundException;
import com.delivery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public Page<PedidoDTO> listarPedidosCliente(String emailCliente, Pageable pageable) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        return pedidoRepository.findByClienteId(cliente.getId(), pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<PedidoDTO> listarPedidosEmpresa(String emailEmpresa, Pageable pageable) {
        Empresa empresa = empresaRepository.findByEmail(emailEmpresa)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        return pedidoRepository.findByEmpresaId(empresa.getId(), pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public PedidoDTO criarPedido(PedidoDTO pedidoDTO, String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        Empresa empresa = empresaRepository.findById(pedidoDTO.getEmpresaId())
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEmpresa(empresa);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setStatusPagamento(StatusPagamento.PENDENTE); // ← Novo campo
        pedido.setFormaPagamento(pedidoDTO.getFormaPagamento());
        pedido.setObservacoes(pedidoDTO.getObservacoes());
        pedido.setEnderecoEntrega(pedidoDTO.getEnderecoEntrega());

        BigDecimal total = BigDecimal.ZERO;

        for (PedidoDTO.ItemPedidoDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

            // Verificar estoque
            if (produto.getEstoque() < itemDTO.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setSubtotal(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));

            total = total.add(item.getSubtotal());

            // Reduzir estoque
            produto.setEstoque(produto.getEstoque() - itemDTO.getQuantidade());
            produtoRepository.save(produto);

            if (pedido.getItens() == null) {
                pedido.setItens(new java.util.ArrayList<>());
            }
            pedido.getItens().add(item);
        }

        pedido.setTotal(total);
        pedido = pedidoRepository.save(pedido);

        return convertToDTO(pedido);
    }

    @Transactional
    public PedidoDTO atualizarStatus(Long pedidoId, StatusPedido novoStatus, String emailEmpresa) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

        // Verificar se o pedido pertence à empresa
        if (!pedido.getEmpresa().getUsuario().getEmail().equals(emailEmpresa)) {
            throw new BusinessException("Pedido não pertence à empresa");
        }

        pedido.setStatus(novoStatus);
        pedido = pedidoRepository.save(pedido);

        return convertToDTO(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoDTO buscarPedidoDoCliente(Long pedidoId, String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        Pedido pedido = pedidoRepository.findByIdWithItens(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("Pedido não pertence ao cliente");
        }

        return convertToDTO(pedido);
    }

    @Transactional(readOnly = true)
    public Page<PedidoDTO> listarPedidosClientePorStatus(String emailCliente, StatusPedido status, Pageable pageable) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        return pedidoRepository.findByClienteIdAndStatus(cliente.getId(), status, pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public PedidoDTO cancelarPedido(Long pedidoId, String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("Pedido não pertence ao cliente");
        }

        if (pedido.getStatus() != StatusPedido.PENDENTE && pedido.getStatus() != StatusPedido.CONFIRMADO) {
            throw new BusinessException("Não é possível cancelar este pedido");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido = pedidoRepository.save(pedido);

        return convertToDTO(pedido);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterEstatisticasCliente(String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        Map<String, Object> stats = new HashMap<>();

        Long totalPedidos = pedidoRepository.countByClienteId(cliente.getId());
        BigDecimal totalGasto = pedidoRepository.sumTotalByClienteId(cliente.getId());

        stats.put("totalPedidos", totalPedidos);
        stats.put("totalGasto", totalGasto != null ? totalGasto : BigDecimal.ZERO);

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> rastrearPedido(Long pedidoId, String emailCliente) {
        PedidoDTO pedido = buscarPedidoDoCliente(pedidoId, emailCliente);

        Map<String, Object> rastreamento = new HashMap<>();
        rastreamento.put("pedido", pedido);
        rastreamento.put("status", pedido.getStatus());
        rastreamento.put("dataUltimaAtualizacao", pedido.getDataPedido());

        return rastreamento;
    }

    @Transactional(readOnly = true)
    public PedidoDTO convertToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setEmpresaId(pedido.getEmpresa().getId());
        dto.setNomeEmpresa(pedido.getEmpresa().getNomeFantasia());
        dto.setTotal(pedido.getTotal());
        dto.setStatus(pedido.getStatus());
        dto.setStatusPagamento(pedido.getStatusPagamento()); // ← Novo campo
        dto.setFormaPagamento(pedido.getFormaPagamento());
        dto.setObservacoes(pedido.getObservacoes());
        dto.setEnderecoEntrega(pedido.getEnderecoEntrega());
        dto.setDataPedido(pedido.getCreatedAt());

        // Carregar itens dentro da transação
        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            List<PedidoDTO.ItemPedidoDTO> itensDTO = pedido.getItens().stream()
                    .map(item -> {
                        PedidoDTO.ItemPedidoDTO itemDTO = new PedidoDTO.ItemPedidoDTO();
                        itemDTO.setProdutoId(item.getProduto().getId());
                        itemDTO.setNomeProduto(item.getProduto().getNome());
                        itemDTO.setQuantidade(item.getQuantidade());
                        itemDTO.setPrecoUnitario(item.getPrecoUnitario());
                        itemDTO.setSubtotal(item.getSubtotal());
                        return itemDTO;
                    })
                    .toList();
            dto.setItens(itensDTO);
        }

        return dto;
    }

    @Transactional
    public PedidoDTO pagarPedido(Long pedidoId, String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        Pedido pedido = pedidoRepository.findByIdWithItens(pedidoId)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

        // Verificar se o pedido pertence ao cliente
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("Pedido não pertence ao cliente");
        }

        // Verificar se o pedido pode ser pago
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BusinessException("Apenas pedidos pendentes podem ser pagos");
        }

        if (pedido.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new BusinessException("Este pedido já foi pago");
        }

        // Simular pagamento - sempre aprova
        pedido.setStatusPagamento(StatusPagamento.PAGO);

        // Automaticamente confirma o pedido quando é pago
        pedido.setStatus(StatusPedido.CONFIRMADO);
        pedido = pedidoRepository.save(pedido);

        return convertToDTO(pedido);
    }
}