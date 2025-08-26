package com.dizzme.controller;

import com.dizzme.dto.ApiResponse;
import com.dizzme.dto.ClientDto;
import com.dizzme.dto.ClientUpdateRequest;
import com.dizzme.dto.PageResponse;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ClientDto>> getCurrentClient() {
        try {
            ClientDto client = clientService.getCurrentClient();
            return ResponseEntity.ok(ApiResponse.success(client));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ClientDto>> updateCurrentClient(@Valid @RequestBody ClientUpdateRequest request) {
        try {
            ClientDto client = clientService.updateClient(request);
            return ResponseEntity.ok(ApiResponse.success("Perfil atualizado com sucesso", client));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ClientDto>>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ClientDto> clientsPage = clientService.getAllClients(pageable);

        PageResponse<ClientDto> response = new PageResponse<>(
                clientsPage.getContent(),
                clientsPage.getNumber(),
                clientsPage.getSize(),
                clientsPage.getTotalElements(),
                clientsPage.getTotalPages(),
                clientsPage.isFirst(),
                clientsPage.isLast()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
