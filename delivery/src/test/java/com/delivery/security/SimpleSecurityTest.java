package com.delivery.security;

import com.delivery.service.auth.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SimpleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== TESTES DE AUTENTICAÇÃO ====================

    @Test
    @DisplayName("Endpoints protegidos devem exigir autenticação")
    public void endpointsProtegidosDevemExigirAutenticacao() throws Exception {
        // Testar endpoints de cliente sem token
        mockMvc.perform(get("/api/cliente/perfil"))
                .andExpect(status().isUnauthorized());

        // Testar endpoints de empresa sem token
        mockMvc.perform(get("/api/empresa/perfil"))
                .andExpect(status().isUnauthorized());

        // Testar endpoints de admin sem token
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Endpoints públicos não devem exigir autenticação")
    public void endpointsPublicosNaoDevemExigirAutenticacao() throws Exception {
        // Testar login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request por dados inválidos, não unauthorized

        // Testar registro
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request por dados inválidos, não unauthorized

        // Testar busca pública
        mockMvc.perform(get("/api/publico/categorias"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/publico/busca/empresas"))
                .andExpect(status().isOk());
    }

    // ==================== TESTES DE AUTORIZAÇÃO ====================

    @Test
    @DisplayName("Cliente não deve acessar endpoints de empresa")
    public void clienteNaoDeveAcessarEndpointsDeEmpresa() throws Exception {
        String tokenCliente = jwtService.generateToken("cliente@test.com");

        mockMvc.perform(get("/api/empresa/perfil")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/empresa/produtos")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Empresa não deve acessar endpoints de cliente")
    public void empresaNaoDeveAcessarEndpointsDeCliente() throws Exception {
        String tokenEmpresa = jwtService.generateToken("empresa@test.com");

        mockMvc.perform(get("/api/cliente/perfil")
                        .header("Authorization", "Bearer " + tokenEmpresa))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/cliente/pedidos")
                        .header("Authorization", "Bearer " + tokenEmpresa))
                .andExpect(status().isForbidden());
    }

    // ==================== TESTES DE TOKENS JWT ====================

    @Test
    @DisplayName("Token inválido deve ser rejeitado")
    public void tokenInvalidoDeveSerRejeitado() throws Exception {
        mockMvc.perform(get("/api/cliente/perfil")
                        .header("Authorization", "Bearer token_invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Token sem Bearer deve ser rejeitado")
    public void tokenSemBearerDeveSerRejeitado() throws Exception {
        String token = jwtService.generateToken("test@test.com");

        mockMvc.perform(get("/api/cliente/perfil")
                        .header("Authorization", token)) // Sem "Bearer "
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Requisição sem header Authorization deve ser rejeitada")
    public void requisicaoSemHeaderAuthorizationDeveSerRejeitada() throws Exception {
        mockMvc.perform(get("/api/cliente/perfil"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== TESTES DE LOGIN ====================

    @Test
    @DisplayName("Login com dados inválidos deve falhar")
    public void loginComDadosInvalidosDeveFalhar() throws Exception {
        Map<String, String> loginInvalido = new HashMap<>();
        loginInvalido.put("email", "email_inexistente@test.com");
        loginInvalido.put("senha", "senha_errada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInvalido)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login sem email deve falhar")
    public void loginSemEmailDeveFalhar() throws Exception {
        Map<String, String> loginSemEmail = new HashMap<>();
        loginSemEmail.put("senha", "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginSemEmail)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login sem senha deve falhar")
    public void loginSemSenhaDeveFalhar() throws Exception {
        Map<String, String> loginSemSenha = new HashMap<>();
        loginSemSenha.put("email", "test@test.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginSemSenha)))
                .andExpect(status().isBadRequest());
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Registro com email inválido deve falhar")
    public void registroComEmailInvalidoDeveFalhar() throws Exception {
        Map<String, Object> registro = new HashMap<>();
        registro.put("email", "email_invalido");
        registro.put("senha", "123456");
        registro.put("tipoUsuario", "CLIENTE");
        registro.put("nome", "Test User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Registro com senha muito curta deve falhar")
    public void registroComSenhaCurtaDeveFalhar() throws Exception {
        Map<String, Object> registro = new HashMap<>();
        registro.put("email", "test@test.com");
        registro.put("senha", "123"); // Senha muito curta
        registro.put("tipoUsuario", "CLIENTE");
        registro.put("nome", "Test User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isBadRequest());
    }

    // ==================== TESTES DE CORS ====================

    @Test
    @DisplayName("CORS deve estar configurado corretamente")
    public void corsDeveEstarConfiguradoCorretamente() throws Exception {
        mockMvc.perform(options("/api/publico/categorias")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    // ==================== TESTES DE INJEÇÃO ====================

    @Test
    @DisplayName("Tentativas de SQL Injection devem ser bloqueadas")
    public void tentativasSqlInjectionDevemSerBloqueadas() throws Exception {
        String[] payloadsMaliciosos = {
                "'; DROP TABLE usuarios; --",
                "' OR '1'='1",
                "admin'--",
                "' OR 1=1#"
        };

        for (String payload : payloadsMaliciosos) {
            Map<String, String> login = new HashMap<>();
            login.put("email", payload);
            login.put("senha", "123456");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isBadRequest()); // Deve falhar na validação
        }
    }

    @Test
    @DisplayName("Tentativas de XSS devem ser bloqueadas")
    public void tentativasXssDevemSerBloqueadas() throws Exception {
        String[] payloadsXss = {
                "<script>alert('xss')</script>",
                "javascript:alert('xss')",
                "<img src=x onerror=alert('xss')>"
        };

        for (String payload : payloadsXss) {
            Map<String, String> login = new HashMap<>();
            login.put("email", payload);
            login.put("senha", "123456");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isBadRequest()); // Deve falhar na validação
        }
    }

    // ==================== TESTES DE RATE LIMITING (conceitual) ====================

    @Test
    @DisplayName("Múltiplas tentativas de login devem ser controladas")
    public void multiplasTentativasLoginDevemSerControladas() throws Exception {
        Map<String, String> loginInvalido = new HashMap<>();
        loginInvalido.put("email", "test@test.com");
        loginInvalido.put("senha", "senha_errada");

        // Simular múltiplas tentativas
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginInvalido)))
                    .andExpect(status().isUnauthorized());
        }

        // Nota: Aqui você implementaria rate limiting real na aplicação
        // e testaria se após muitas tentativas o sistema bloqueia temporariamente
    }

    // ==================== TESTES DE HEADERS DE SEGURANÇA ====================

    @Test
    @DisplayName("Headers de segurança devem estar presentes")
    public void headersDeSegurancaDevemEstarPresentes() throws Exception {
        mockMvc.perform(get("/api/publico/categorias"))
                .andExpect(status().isOk())
                // Verificar se headers básicos estão configurados
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"));
    }
}