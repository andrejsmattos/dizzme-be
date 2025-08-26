package com.dizzme.service;

import com.dizzme.dto.ClientDto;
import com.dizzme.dto.ClientUpdateRequest;
import com.dizzme.entity.Client;
import com.dizzme.exception.BusinessException;
import com.dizzme.repository.ClientRepository;
import com.dizzme.repository.SurveyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    public ClientDto getCurrentClient() throws BusinessException {
        Long clientId = getCurrentClientId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        return mapToDto(client);
    }

    @Transactional
    public ClientDto updateClient(ClientUpdateRequest request) throws BusinessException {
        Long clientId = getCurrentClientId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        client.setName(request.name());
        if (request.email() != null && !request.email().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(request.email())) {
                throw new BusinessException("Email já está em uso");
            }
            client.setEmail(request.email());
        }

        client = clientRepository.save(client);
        return mapToDto(client);
    }

    public Page<ClientDto> getAllClients(Pageable pageable) {
        return clientRepository.findAllClients(pageable)
                .map(this::mapToDto);
    }

    private ClientDto mapToDto(Client client) {
        Integer surveysCount = surveyRepository.countByClientId(client.getId());

        return new ClientDto(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getRole().name(),
                client.getActive(),
                client.getCreatedAt(),
                client.getUpdatedAt(),
                surveysCount
        );
    }

    private Long getCurrentClientId() throws BusinessException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return clientRepository.findByEmail(email)
                .map(Client::getId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }
}