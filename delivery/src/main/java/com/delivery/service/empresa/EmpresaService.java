package com.delivery.service.empresa;

import com.delivery.dto.empresa.EmpresaDTO;
import com.delivery.dto.empresa.RelatorioDTO;
import com.delivery.entity.Empresa;
import com.delivery.entity.Pedido;
import com.delivery.exception.NotFoundException;
import com.delivery.repository.EmpresaRepository;
import com.delivery.repository.FeedbackRepository;
import com.delivery.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PedidoRepository pedidoRepository;
    private final FeedbackRepository feedbackRepository;

    public Page<EmpresaDTO> listarEmpresas(Pageable pageable) {
        return empresaRepository.findAllAtivas(pageable)
                .map(this::convertToDTO);
    }

    public Page<EmpresaDTO> buscarPorCategoria(Long categoriaId, Pageable pageable) {
        return empresaRepository.findByCategoriaId(categoriaId, pageable)
                .map(this::convertToDTO);
    }

    public Page<EmpresaDTO> buscar(String termo, Long categoriaId, Pageable pageable) {
        return empresaRepository.findByTermoAndCategoria(termo, categoriaId, pageable)
                .map(this::convertToDTO);
    }

    public List<EmpresaDTO> buscarPorLocalizacao(Double latitude, Double longitude, Double raioKm) {
        return empresaRepository.findByLocalizacao(latitude, longitude, raioKm)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public EmpresaDTO buscarPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));
        return convertToDTO(empresa);
    }

    public Empresa buscarPorEmail(String email) {
        return empresaRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Empresa não encontrada"));
    }

    public RelatorioDTO gerarRelatorio(String emailEmpresa, int mes, int ano) {
        Empresa empresa = buscarPorEmail(emailEmpresa);

        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDateTime inicioMes = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime fimMes = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        LocalDateTime inicioAno = LocalDateTime.of(ano, 1, 1, 0, 0, 0);
        LocalDateTime fimAno = LocalDateTime.of(ano, 12, 31, 23, 59, 59);

        RelatorioDTO relatorio = new RelatorioDTO();

        // Faturamento mensal
        BigDecimal faturamentoMensal = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(
                empresa.getId(), inicioMes, fimMes);
        relatorio.setFaturamentoMensal(faturamentoMensal != null ? faturamentoMensal : BigDecimal.ZERO);

        // Faturamento anual
        BigDecimal faturamentoAnual = pedidoRepository.sumTotalByEmpresaIdAndPeriodo(
                empresa.getId(), inicioAno, fimAno);
        relatorio.setFaturamentoAnual(faturamentoAnual != null ? faturamentoAnual : BigDecimal.ZERO);

        // Quantidade de pedidos
        Long pedidosMensal = pedidoRepository.countByEmpresaIdAndPeriodo(
                empresa.getId(), inicioMes, fimMes);
        relatorio.setQuantidadePedidosMensal(pedidosMensal.intValue());

        Long pedidosAnual = pedidoRepository.countByEmpresaIdAndPeriodo(
                empresa.getId(), inicioAno, fimAno);
        relatorio.setQuantidadePedidosAnual(pedidosAnual.intValue());

        // Ticket médio
        if (pedidosMensal > 0) {
            BigDecimal ticketMedio = faturamentoMensal.divide(BigDecimal.valueOf(pedidosMensal), 2, java.math.RoundingMode.HALF_UP);
            relatorio.setTicketMedio(ticketMedio);
        } else {
            relatorio.setTicketMedio(BigDecimal.ZERO);
        }

        // Avaliações
        Double avaliacaoMedia = feedbackRepository.findAvaliacaoMediaByEmpresaId(empresa.getId());
        relatorio.setAvaliacaoMedia(avaliacaoMedia != null ? avaliacaoMedia : 0.0);

        Long totalAvaliacoes = feedbackRepository.countByEmpresaId(empresa.getId());
        relatorio.setTotalAvaliacoes(totalAvaliacoes.intValue());

        return relatorio;
    }

    private EmpresaDTO convertToDTO(Empresa empresa) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setNomeFantasia(empresa.getNomeFantasia());
        dto.setEmail(empresa.getUsuario().getEmail());
        dto.setCnpj(empresa.getCnpj());
        dto.setTelefone(empresa.getTelefone());
        dto.setEndereco(empresa.getEndereco());
        dto.setLatitude(empresa.getLatitude());
        dto.setLongitude(empresa.getLongitude());
        dto.setLogoUrl(empresa.getLogoUrl());
        dto.setDescricao(empresa.getDescricao());
        dto.setAtivo(empresa.getAtivo());

        // Avaliação média
        Double avaliacao = feedbackRepository.findAvaliacaoMediaByEmpresaId(empresa.getId());
        dto.setAvaliacao(avaliacao != null ? avaliacao : 0.0);

        Long totalAvaliacoes = feedbackRepository.countByEmpresaId(empresa.getId());
        dto.setTotalAvaliacoes(totalAvaliacoes.intValue());

        return dto;
    }
}