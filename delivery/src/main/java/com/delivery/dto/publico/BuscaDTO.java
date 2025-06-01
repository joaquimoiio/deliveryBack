package com.delivery.dto.publico;

import lombok.Data;
import java.util.List;

@Data
public class BuscaDTO {
    private String termo;
    private Long categoriaId;
    private Double latitude;
    private Double longitude;
    private Double raioKm;
    private Integer pagina;
    private Integer tamanho;
}