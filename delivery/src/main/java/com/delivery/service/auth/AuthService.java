package com.delivery.service.auth;

import com.delivery.dto.auth.LoginRequest;
import com.delivery.dto.auth.RegisterRequest;
import com.delivery.dto.auth.TokenResponse;
import com.delivery.entity.Cliente;
import com.delivery.entity.Empresa;
import com.delivery.entity.base.Usuario;
import com.delivery.entity.enums.TipoUsuario;
import com.delivery.exception.BusinessException;
import com.delivery.repository.CategoriaRepository;
import com.delivery.repository.ClienteRepository;
import com.delivery.repository.EmpresaRepository;
import com.delivery.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtService.generateToken(usuario.getEmail());

        return new TokenResponse(token, usuario.getTipoUsuario(), usuario.getId(), usuario.getEmail());
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já está em uso");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTipoUsuario(request.getTipoUsuario());
        usuario.setAtivo(true);

        usuario = usuarioRepository.save(usuario);

        if (request.getTipoUsuario() == TipoUsuario.CLIENTE) {
            criarCliente(usuario, request);
        } else if (request.getTipoUsuario() == TipoUsuario.EMPRESA) {
            criarEmpresa(usuario, request);
        }

        String token = jwtService.generateToken(usuario.getEmail());
        return new TokenResponse(token, usuario.getTipoUsuario(), usuario.getId(), usuario.getEmail());
    }

    private void criarCliente(Usuario usuario, RegisterRequest request) {
        if (clienteRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já está em uso");
        }

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setTelefone(request.getTelefoneCliente());
        cliente.setEndereco(request.getEnderecoCliente());

        clienteRepository.save(cliente);
    }

    private void criarEmpresa(Usuario usuario, RegisterRequest request) {
        if (empresaRepository.existsByCnpj(request.getCnpj())) {
            throw new BusinessException("CNPJ já está em uso");
        }

        Empresa empresa = new Empresa();
        empresa.setUsuario(usuario);
        empresa.setNomeFantasia(request.getNomeFantasia());
        empresa.setCnpj(request.getCnpj());
        empresa.setTelefone(request.getTelefoneEmpresa());
        empresa.setEndereco(request.getEnderecoEmpresa());
        empresa.setDescricao(request.getDescricao());

        if (request.getCategoriaId() != null) {
            empresa.setCategoria(categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new BusinessException("Categoria não encontrada")));
        }

        empresaRepository.save(empresa);
    }
}