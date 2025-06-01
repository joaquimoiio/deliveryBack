package com.delivery.controller.cliente;

import com.delivery.dto.empresa.FeedbackDTO;
import com.delivery.service.empresa.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cliente/feedbacks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeedbackClienteController {

    private final FeedbackService feedbackService;

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<FeedbackDTO> criarFeedback(
            @PathVariable Long pedidoId,
            @Valid @RequestBody FeedbackDTO feedbackDTO,
            Authentication authentication) {

        feedbackDTO.setPedidoId(pedidoId);
        FeedbackDTO feedback = feedbackService.criarFeedback(feedbackDTO, authentication.getName());
        return ResponseEntity.ok(feedback);
    }
}