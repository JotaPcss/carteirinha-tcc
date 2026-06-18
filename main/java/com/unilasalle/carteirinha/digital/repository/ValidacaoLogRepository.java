package com.unilasalle.carteirinha.digital.repository;

import com.unilasalle.carteirinha.digital.entity.ValidacaoLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidacaoLogRepository extends JpaRepository<ValidacaoLog, Integer> {
}