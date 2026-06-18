package com.unilasalle.carteirinha.digital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unilasalle.carteirinha.digital.entity.Instituicao;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Integer> {
    Optional<Instituicao> findByCnpj(String cnpj);
}