package com.unilasalle.carteirinha.digital.repository;

import com.unilasalle.carteirinha.digital.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
}