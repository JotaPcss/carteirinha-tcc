package com.unilasalle.carteirinha.digital.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.unilasalle.carteirinha.digital.entity.Administrador;
import com.unilasalle.carteirinha.digital.entity.Curso;
import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.Instituicao;
import com.unilasalle.carteirinha.digital.entity.Modalidade;
import com.unilasalle.carteirinha.digital.entity.StatusFoto;
import com.unilasalle.carteirinha.digital.entity.Turno;
import com.unilasalle.carteirinha.digital.repository.AdminRepository;
import com.unilasalle.carteirinha.digital.repository.CursoRepository;
import com.unilasalle.carteirinha.digital.repository.EstudanteRepository;
import com.unilasalle.carteirinha.digital.repository.InstituicaoRepository;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    // Dados dos estudantes de teste (matrícula, nome, cpf, email, nascimento)
    private static final String[][] ESTUDANTES_TESTE = {
        {"20240001","Ana Paula Souza","11122233344","ana.souza@aluno.unilasalle.br","2002-03-15"},
        {"20240002","Bruno Lima","22233344455","bruno.lima@aluno.unilasalle.br","2001-07-22"},
        {"20240003","Carla Mendes","33344455566","carla.mendes@aluno.unilasalle.br","2003-01-10"},
        {"20240004","Diego Ferreira","44455566677","diego.f@aluno.unilasalle.br","2000-11-30"},
        {"20240005","Eduarda Costa","55566677788","eduarda.c@aluno.unilasalle.br","2002-06-05"},
        {"20240006","Fábio Araújo","66677788899","fabio.a@aluno.unilasalle.br","2001-09-18"},
        {"20240007","Gabriela Rocha","77788899900","gabriela.r@aluno.unilasalle.br","2003-04-25"},
        {"20240008","Henrique Alves","88899900011","henrique.a@aluno.unilasalle.br","2000-12-01"},
    };

    private static final String SENHA_TESTE = "123456";

    @Bean
    public CommandLineRunner initData(AdminRepository adminRepo,
                                      CursoRepository cursoRepo,
                                      InstituicaoRepository instituicaoRepo,
                                      EstudanteRepository estudanteRepo,
                                      BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            // ── Instituição ──────────────────────────────────────────────
            Instituicao instituicao;
            if (instituicaoRepo.count() == 0) {
                instituicao = new Instituicao();
                instituicao.setNomeInstituicao("Centro Universitário La Salle");
                instituicao.setCnpj("12345678000199");
                instituicaoRepo.save(instituicao);
            } else {
                instituicao = instituicaoRepo.findAll().get(0);
            }

            // ── Curso ────────────────────────────────────────────────────
            Curso curso;
            if (cursoRepo.count() == 0) {
                curso = new Curso();
                curso.setNomeCurso("Sistemas de Informação");
                curso.setInstituicao(instituicao);
                curso.setModalidade(Modalidade.PRESENCIAL);
                curso.setTurno(Turno.NOTURNO);
                curso.setDuracaoMeses(48);
                curso.setAtivo(true);
                cursoRepo.save(curso);
            } else {
                curso = cursoRepo.findAll().get(0);
            }

            // ── Administrador ────────────────────────────────────────────
            // Cria o admin na primeira vez; nas reinicializações seguintes,
            // corrige o hash caso esteja inválido (ex: inserido via SQL).
            Administrador admin = adminRepo.findByEmail("admin@unilasalle.br").orElse(null);
            if (admin == null) {
                admin = new Administrador();
                admin.setNome("Administrador Master");
                admin.setEmail("admin@unilasalle.br");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setAtivo(true);
                adminRepo.save(admin);
            } else if (!isBcryptHashValid(admin.getSenha(), "admin123", passwordEncoder)) {
                admin.setSenha(passwordEncoder.encode("admin123"));
                adminRepo.save(admin);
                System.out.println("[DataInitializer] Hash do admin corrigido.");
            }

            // ── Estudantes de teste ──────────────────────────────────────
            // Para cada matrícula de teste, cria se não existir OU corrige
            // o hash se estiver inválido (hashes fabricados manualmente).
            Integer idCurso = curso.getIdCurso();
            for (String[] d : ESTUDANTES_TESTE) {
                String matricula = d[0];
                Estudante est = estudanteRepo.findByMatricula(matricula).orElse(null);
                if (est == null) {
                    est = new Estudante();
                    est.setMatricula(matricula);
                    est.setNomeCompleto(d[1]);
                    est.setCpf(d[2]);
                    est.setEmail(d[3]);
                    est.setDataNascimento(LocalDate.parse(d[4]));
                    est.setSenha(passwordEncoder.encode(SENHA_TESTE));
                    est.setIdCurso(idCurso);
                    est.setStatusFoto(StatusFoto.SEM_FOTO);
                    est.setAtivo(true);
                    estudanteRepo.save(est);
                    System.out.println("[DataInitializer] Estudante criado: " + matricula);
                } else if (!isBcryptHashValid(est.getSenha(), SENHA_TESTE, passwordEncoder)) {
                    // Hash inválido (truncado ou fabricado) – reencripta
                    est.setSenha(passwordEncoder.encode(SENHA_TESTE));
                    estudanteRepo.save(est);
                    System.out.println("[DataInitializer] Hash corrigido: " + matricula);
                }
            }
        };
    }

    /**
     * Verifica com segurança se um hash BCrypt é válido para a senha informada.
     * Retorna false se o hash estiver malformado ou não corresponder.
     */
    private boolean isBcryptHashValid(String hash, String rawPassword, BCryptPasswordEncoder encoder) {
        if (hash == null || hash.length() < 60) return false; // BCrypt sempre tem 60 chars
        try {
            return encoder.matches(rawPassword, hash);
        } catch (Exception e) {
            return false;
        }
    }
}
