package com.delivery.dto.empresa;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackDTO {
    private Long id;
    private String nomeCliente;
    private Integer nota;
    private String comentario;
    private LocalDateTime dataFeedback;
    private Long pedidoId;
}