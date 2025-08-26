package com.dizzme.repository;

import com.dizzme.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Client> findByActiveTrue();

    @Query("SELECT c FROM Client c WHERE c.role = 'CLIENT' ORDER BY c.createdAt DESC")
    Page<Client> findAllClients(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.active = true AND c.role = 'CLIENT'")
    Integer countActiveClients();

    @Query("SELECT c FROM Client c WHERE c.createdAt >= :date AND c.role = 'CLIENT'")
    List<Client> findRecentClients(@Param("date") LocalDateTime date);
}
