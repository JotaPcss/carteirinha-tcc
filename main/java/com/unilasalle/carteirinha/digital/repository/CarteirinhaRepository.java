package com.unilasalle.carteirinha.digital.repository;

import com.unilasalle.carteirinha.digital.entity.Carteirinha;
import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.StatusCarteirinha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarteirinhaRepository extends JpaRepository<Carteirinha, Integer> {
    Optional<Carteirinha> findByEstudanteAndStatus(Estudante estudante, StatusCarteirinha status);
}