package com.unilasalle.carteirinha.digital.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.unilasalle.carteirinha.digital.dto.EstudanteCadastroDTO;
import com.unilasalle.carteirinha.digital.entity.Curso;
import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.StatusFoto;
import com.unilasalle.carteirinha.digital.repository.CursoRepository;
import com.unilasalle.carteirinha.digital.repository.EstudanteRepository;

@Service
public class EstudanteService {

    private final EstudanteRepository estudanteRepository;
    private final CursoRepository cursoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public EstudanteService(EstudanteRepository estudanteRepository,
                            CursoRepository cursoRepository,
                            BCryptPasswordEncoder passwordEncoder) {
        this.estudanteRepository = estudanteRepository;
        this.cursoRepository = cursoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Estudante cadastrar(EstudanteCadastroDTO dto) {
        if (estudanteRepository.findByMatricula(dto.getMatricula()).isPresent())
            throw new IllegalArgumentException("Matrícula já cadastrada");
        if (estudanteRepository.findByCpf(dto.getCpf()).isPresent())
            throw new IllegalArgumentException("CPF já cadastrado");
        if (estudanteRepository.findByEmail(dto.getEmail()).isPresent())
            throw new IllegalArgumentException("E-mail já cadastrado");

        // Usa o curso informado no DTO ou o primeiro disponível
        Integer idCurso = dto.getIdCurso();
        if (idCurso == null) {
            idCurso = cursoRepository.findAll().stream()
                    .filter(Curso::getAtivo)
                    .map(Curso::getIdCurso)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Nenhum curso disponível"));
        }

        Estudante estudante = new Estudante();
        estudante.setMatricula(dto.getMatricula());
        estudante.setNomeCompleto(dto.getNomeCompleto());
        estudante.setCpf(dto.getCpf());
        estudante.setEmail(dto.getEmail());
        estudante.setSenha(passwordEncoder.encode(dto.getSenha()));
        estudante.setDataNascimento(dto.getDataNascimento());
        estudante.setIdCurso(idCurso);
        estudante.setStatusFoto(StatusFoto.SEM_FOTO);
        estudante.setAtivo(true);

        return estudanteRepository.save(estudante);
    }
}
