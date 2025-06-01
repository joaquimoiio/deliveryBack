package com.delivery.controller.empresa;

import com.delivery.dto.empresa.FeedbackDTO;
import com.delivery.service.empresa.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresa/feedbacks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeedbackEmpresaController {

    private final FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<Page<FeedbackDTO>> listarFeedbacks(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FeedbackDTO> feedbacks = feedbackService.listarFeedbacksDaEmpresa(authentication.getName(), pageable);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<FeedbackService.EstatisticasFeedback> obterEstatisticas(Authentication authentication) {
        FeedbackService.EstatisticasFeedback stats = feedbackService.obterEstatisticas(authentication.getName());
        return ResponseEntity.ok(stats);
    }
}