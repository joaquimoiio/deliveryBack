package com.delivery.service.cliente;

import com.delivery.dto.cliente.ClienteDTO;
import com.delivery.entity.Cliente;
import com.delivery.exception.NotFoundException;
import com.delivery.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteDTO buscarPorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        return convertToDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizarPerfil(ClienteDTO clienteDTO, String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        cliente.setNome(clienteDTO.getNome());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEndereco(clienteDTO.getEndereco());
        cliente.setLatitude(clienteDTO.getLatitude());
        cliente.setLongitude(clienteDTO.getLongitude());

        cliente = clienteRepository.save(cliente);
        return convertToDTO(cliente);
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getUsuario().getEmail());
        dto.setCpf(cliente.getCpf());
        dto.setTelefone(cliente.getTelefone());
        dto.setEndereco(cliente.getEndereco());
        dto.setLatitude(cliente.getLatitude());
        dto.setLongitude(cliente.getLongitude());
        return dto;
    }
}