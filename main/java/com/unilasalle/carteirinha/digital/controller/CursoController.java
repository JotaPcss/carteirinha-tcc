package com.unilasalle.carteirinha.digital.controller;

import com.unilasalle.carteirinha.digital.entity.Curso;
import com.unilasalle.carteirinha.digital.repository.CursoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoRepository cursoRepository;

    public CursoController(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        return cursoRepository.findAll().stream()
                .filter(Curso::getAtivo)
                .sorted((a, b) -> a.getNomeCurso().compareTo(b.getNomeCurso()))
                .map(c -> Map.<String, Object>of(
                        "idCurso", c.getIdCurso(),
                        "nomeCurso", c.getNomeCurso()
                ))
                .toList();
    }
}
