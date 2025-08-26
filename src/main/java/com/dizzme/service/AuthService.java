
package com.dizzme.service;

import com.dizzme.dto.AuthResponse;
import com.dizzme.dto.LoginRequest;
import com.dizzme.dto.RegisterRequest;
import com.dizzme.entity.Client;
import com.dizzme.entity.ClientRole;
import com.dizzme.exception.BusinessException;
import com.dizzme.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Authentication Service
@Service
public class AuthService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws BusinessException {
        if (clientRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já está em uso");
        }

        Client client = new Client(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        client = clientRepository.save(client);

        String token = jwtService.generateToken(client);

        return new AuthResponse(
                token,
                "Bearer",
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request) throws BusinessException {
        Client client = clientRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));

        if (!client.getActive()) {
            throw new BusinessException("Conta desativada");
        }

        if (!passwordEncoder.matches(request.password(), client.getPasswordHash())) {
            throw new BusinessException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(client);

        return new AuthResponse(
                token,
                "Bearer",
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getRole().name()
        );
    }

    @Transactional
    public AuthResponse createAdmin(RegisterRequest request) throws BusinessException {
        if (clientRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já está em uso");
        }

        Client adminClient = new Client(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        // Força o role como ADMIN
        adminClient.setRole(ClientRole.ADMIN);
        adminClient = clientRepository.save(adminClient);

        // Gerar token JWT para o admin criado
        String token = jwtService.generateToken(adminClient);

        return new AuthResponse(
                token,
                "Bearer",
                adminClient.getId(),
                adminClient.getName(),
                adminClient.getEmail(),
                adminClient.getRole().name()
        );
    }
}