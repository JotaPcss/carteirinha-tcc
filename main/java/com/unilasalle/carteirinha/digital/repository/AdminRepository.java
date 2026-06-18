package com.unilasalle.carteirinha.digital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unilasalle.carteirinha.digital.entity.Administrador;

public interface AdminRepository extends JpaRepository<Administrador, Integer> {
    Optional<Administrador> findByEmail(String email);
}