package com.unilasalle.carteirinha.digital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.StatusFoto;

public interface EstudanteRepository extends JpaRepository<Estudante, Integer> {
    Optional<Estudante> findByMatricula(String matricula);
    Optional<Estudante> findByCpf(String cpf);
    Optional<Estudante> findByEmail(String email);
    List<Estudante> findByStatusFoto(StatusFoto status);
    List<Estudante> findByAtivoTrue();

    @Query("SELECT e FROM Estudante e WHERE e.ativo IS TRUE AND (" +
           "LOWER(e.nomeCompleto) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(e.matricula)    LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(e.email)        LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "e.cpf LIKE CONCAT('%', :busca, '%'))")
    List<Estudante> buscarPorTexto(@Param("busca") String busca);
}