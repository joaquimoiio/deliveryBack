package com.delivery.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtils {

    private ResponseUtils() {
        // Utility class
    }

    /**
     * Cria uma resposta de sucesso com dados
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", "Operação realizada com sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta de sucesso com dados e mensagem customizada
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta de sucesso apenas com mensagem
     */
    public static ResponseEntity<Map<String, Object>> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta de erro com mensagem
     */
    public static ResponseEntity<Map<String, Object>> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Cria uma resposta de erro com status customizado
     */
    public static ResponseEntity<Map<String, Object>> error(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Cria uma resposta de erro com dados adicionais
     */
    public static ResponseEntity<Map<String, Object>> error(String message, Object errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Cria uma resposta de não encontrado
     */
    public static ResponseEntity<Map<String, Object>> notFound(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.notFound().build();
    }

    /**
     * Cria uma resposta de não autorizado
     */
    public static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Cria uma resposta de proibido
     */
    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Cria uma resposta de conflito
     */
    public static ResponseEntity<Map<String, Object>> conflict(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Cria uma resposta de erro interno do servidor
     */
    public static ResponseEntity<Map<String, Object>> internalServerError(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Cria uma resposta paginada
     */
    public static <T> ResponseEntity<Map<String, Object>> paginated(
            org.springframework.data.domain.Page<T> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", page.getContent());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page.getNumber());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("size", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());

        response.put("pagination", pagination);
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta paginada com mensagem customizada
     */
    public static <T> ResponseEntity<Map<String, Object>> paginated(
            org.springframework.data.domain.Page<T> page, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", page.getContent());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page.getNumber());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("size", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());

        response.put("pagination", pagination);
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta com dados de lista
     */
    public static <T> ResponseEntity<Map<String, Object>> list(java.util.List<T> list) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", list);
        response.put("total", list.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta com dados de lista e mensagem
     */
    public static <T> ResponseEntity<Map<String, Object>> list(java.util.List<T> list, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", list);
        response.put("total", list.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta de criação bem-sucedida
     */
    public static <T> ResponseEntity<Map<String, Object>> created(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", "Recurso criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cria uma resposta de criação bem-sucedida com mensagem customizada
     */
    public static <T> ResponseEntity<Map<String, Object>> created(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cria uma resposta de atualização bem-sucedida
     */
    public static <T> ResponseEntity<Map<String, Object>> updated(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", "Recurso atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta de exclusão bem-sucedida
     */
    public static ResponseEntity<Map<String, Object>> deleted() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Recurso removido com sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta de exclusão bem-sucedida com mensagem customizada
     */
    public static ResponseEntity<Map<String, Object>> deleted(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma resposta com dados de validação
     */
    public static ResponseEntity<Map<String, Object>> validationError(Map<String, String> errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Dados inválidos");
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Cria uma resposta com código de status customizado
     */
    public static <T> ResponseEntity<Map<String, Object>> custom(T data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", status.is2xxSuccessful());
        response.put("data", data);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Cria uma resposta vazia com apenas status
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Cria uma resposta de aceitação (para operações assíncronas)
     */
    public static ResponseEntity<Map<String, Object>> accepted(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}