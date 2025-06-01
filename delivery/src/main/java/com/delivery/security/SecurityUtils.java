package com.delivery.security;

import com.delivery.entity.enums.TipoUsuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

public class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * Obtém o usuário autenticado atual
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.empty();
    }

    /**
     * Verifica se o usuário atual está autenticado
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !isAnonymous(authentication);
    }

    /**
     * Verifica se o usuário atual é anônimo
     */
    public static boolean isAnonymous(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    /**
     * Verifica se o usuário atual tem uma role específica
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Verifica se o usuário atual é um cliente
     */
    public static boolean isCliente() {
        return hasRole(TipoUsuario.CLIENTE.name());
    }

    /**
     * Verifica se o usuário atual é uma empresa
     */
    public static boolean isEmpresa() {
        return hasRole(TipoUsuario.EMPRESA.name());
    }

    /**
     * Obtém todas as authorities do usuário atual
     */
    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return authentication.getAuthorities();
        }

        return java.util.Collections.emptyList();
    }

    /**
     * Obtém o tipo de usuário baseado nas roles
     */
    public static Optional<TipoUsuario> getCurrentUserType() {
        if (isCliente()) {
            return Optional.of(TipoUsuario.CLIENTE);
        } else if (isEmpresa()) {
            return Optional.of(TipoUsuario.EMPRESA);
        }

        return Optional.empty();
    }

    /**
     * Verifica se o email fornecido é o mesmo do usuário autenticado
     */
    public static boolean isCurrentUser(String email) {
        return getCurrentUserEmail()
                .map(currentEmail -> currentEmail.equals(email))
                .orElse(false);
    }

    /**
     * Verifica se o usuário tem permissão para acessar um recurso
     */
    public static boolean hasPermission(String resource, String action) {
        // Lógica básica de permissões - pode ser expandida
        if (!isAuthenticated()) {
            return false;
        }

        // Exemplos de permissões básicas
        switch (resource.toLowerCase()) {
            case "pedido":
                return isCliente() || isEmpresa();
            case "produto":
                return isEmpresa();
            case "relatorio":
                return isEmpresa();
            case "feedback":
                return isCliente() || isEmpresa();
            default:
                return false;
        }
    }

    /**
     * Obtém informações básicas do usuário atual
     */
    public static UserInfo getCurrentUserInfo() {
        return getCurrentUserEmail()
                .map(email -> {
                    UserInfo info = new UserInfo();
                    info.setEmail(email);
                    info.setTipoUsuario(getCurrentUserType().orElse(null));
                    info.setAuthenticated(true);
                    return info;
                })
                .orElse(new UserInfo());
    }

    /**
     * Classe para representar informações do usuário
     */
    public static class UserInfo {
        private String email;
        private TipoUsuario tipoUsuario;
        private boolean authenticated;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public TipoUsuario getTipoUsuario() {
            return tipoUsuario;
        }

        public void setTipoUsuario(TipoUsuario tipoUsuario) {
            this.tipoUsuario = tipoUsuario;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public void setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
        }
    }
}