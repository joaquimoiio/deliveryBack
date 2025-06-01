package com.delivery.service.empresa;

import com.delivery.dto.empresa.FeedbackDTO;
import com.delivery.entity.*;
import com.delivery.entity.enums.StatusPedido;
import com.delivery.exception.BusinessException;
import com.delivery.exception.NotFoundException;
import com.delivery.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;

    public Page<FeedbackDTO> listarFeedbacksDaEmpresa(String emailEmpresa, Pageable pageable) {
        Empresa empresa = empresaRepository.findByEmail(emailEmpresa)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        return feedbackRepository.findByEmpresaId(empresa.getId(), pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public FeedbackDTO criarFeedback(FeedbackDTO feedbackDTO, String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        Pedido pedido = pedidoRepository.findById(feedbackDTO.getPedidoId())
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado"));

        // Verificações
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("Pedido não pertence ao cliente");
        }

        if (!pedido.getStatus().equals(StatusPedido.ENTREGUE)) {
            throw new BusinessException("Só é possível avaliar pedidos entregues");
        }

        if (feedbackRepository.existsByPedidoId(pedido.getId())) {
            throw new BusinessException("Pedido já foi avaliado");
        }

        Feedback feedback = new Feedback();
        feedback.setPedido(pedido);
        feedback.setCliente(cliente);
        feedback.setEmpresa(pedido.getEmpresa());
        feedback.setNota(feedbackDTO.getNota());
        feedback.setComentario(feedbackDTO.getComentario());

        feedback = feedbackRepository.save(feedback);
        return convertToDTO(feedback);
    }

    public EstatisticasFeedback obterEstatisticas(String emailEmpresa) {
        Empresa empresa = empresaRepository.findByEmail(emailEmpresa)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));

        EstatisticasFeedback stats = new EstatisticasFeedback();
        stats.setAvaliacaoMedia(feedbackRepository.findAvaliacaoMediaByEmpresaId(empresa.getId()));
        stats.setTotalAvaliacoes(feedbackRepository.countByEmpresaId(empresa.getId()));

        return stats;
    }

    private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setNomeCliente(feedback.getCliente().getNome());
        dto.setNota(feedback.getNota());
        dto.setComentario(feedback.getComentario());
        dto.setDataFeedback(feedback.getCreatedAt());
        dto.setPedidoId(feedback.getPedido().getId());
        return dto;
    }

    @Data
    public static class EstatisticasFeedback {
        private Double avaliacaoMedia;
        private Long totalAvaliacoes;
    }
}