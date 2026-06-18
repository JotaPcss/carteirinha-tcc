package com.unilasalle.carteirinha.digital.service;

import com.unilasalle.carteirinha.digital.entity.Administrador;
import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.repository.AdminRepository;
import com.unilasalle.carteirinha.digital.repository.EstudanteRepository;
import com.unilasalle.carteirinha.digital.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EstudanteRepository estudanteRepository;
    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(EstudanteRepository estudanteRepository,
                       AdminRepository adminRepository,
                       JwtService jwtService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.estudanteRepository = estudanteRepository;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String autenticar(String login, String senhaRaw) {
        // 1) Tenta autenticar como estudante (login é a matrícula)
        Estudante estudante = estudanteRepository.findByMatricula(login).orElse(null);
        if (estudante != null && passwordEncoder.matches(senhaRaw, estudante.getSenha())) {
            return jwtService.generateToken(estudante.getMatricula(), "ESTUDANTE");
        }

        // 2) Tenta autenticar como administrador (login é o e-mail)
        Administrador admin = adminRepository.findByEmail(login).orElse(null);
        if (admin != null && passwordEncoder.matches(senhaRaw, admin.getSenha())) {
            return jwtService.generateToken(admin.getEmail(), "ADMIN");
        }

        throw new RuntimeException("Credenciais inválidas");
    }
}