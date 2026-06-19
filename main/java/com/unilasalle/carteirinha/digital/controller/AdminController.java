package com.unilasalle.carteirinha.digital.controller;

import com.unilasalle.carteirinha.digital.dto.EstudanteResponseDTO;
import com.unilasalle.carteirinha.digital.entity.Administrador;
import com.unilasalle.carteirinha.digital.entity.Curso;
import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.StatusFoto;
import com.unilasalle.carteirinha.digital.repository.AdminRepository;
import com.unilasalle.carteirinha.digital.repository.CursoRepository;
import com.unilasalle.carteirinha.digital.repository.EstudanteRepository;
import com.unilasalle.carteirinha.digital.service.CarteirinhaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final EstudanteRepository estudanteRepository;
    private final CursoRepository cursoRepository;
    private final CarteirinhaService carteirinhaService;
    private final AdminRepository adminRepository;

    public AdminController(EstudanteRepository estudanteRepository,
                           CursoRepository cursoRepository,
                           CarteirinhaService carteirinhaService,
                           AdminRepository adminRepository) {
        this.estudanteRepository = estudanteRepository;
        this.cursoRepository = cursoRepository;
        this.carteirinhaService = carteirinhaService;
        this.adminRepository = adminRepository;
    }

    /** Retorna dados do admin autenticado para o frontend identificar o papel. */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        String email = authentication.getName();
        Administrador admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado"));
        return ResponseEntity.ok(Map.of(
                "email", admin.getEmail(),
                "nome", admin.getNome(),
                "role", "ADMIN"
        ));
    }

    @GetMapping("/estudantes/pendentes")
    public ResponseEntity<List<EstudanteResponseDTO>> listarPendentes() {
        List<EstudanteResponseDTO> pendentes = estudanteRepository.findByStatusFoto(StatusFoto.PENDENTE)
                .stream().map(EstudanteResponseDTO::new).toList();
        return ResponseEntity.ok(pendentes);
    }

    @PutMapping("/estudantes/{id}/aprovar-foto")
    public ResponseEntity<?> aprovarFoto(@PathVariable Integer id, @RequestParam boolean aprovado) {
        Estudante estudante = estudanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

        if (aprovado) {
            estudante.setStatusFoto(StatusFoto.APROVADA);
            estudanteRepository.save(estudante);

            Integer idCurso = estudante.getIdCurso();
            Curso curso;
            if (idCurso != null) {
                curso = cursoRepository.findById(idCurso)
                        .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
            } else {
                curso = cursoRepository.findAll().stream()
                        .filter(Curso::getAtivo)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Nenhum curso ativo disponível"));
            }
            carteirinhaService.gerarCarteirinha(estudante, curso);
        } else {
            estudante.setStatusFoto(StatusFoto.REPROVADA);
            estudante.setUrlFoto(null);
            estudanteRepository.save(estudante);
        }
        return ResponseEntity.ok(Map.of("mensagem", "Status da foto atualizado"));
    }

    @PutMapping("/carteirinha/{id}/bloquear")
    public ResponseEntity<?> bloquearCarteirinha(@PathVariable Integer id) {
        carteirinhaService.bloquearCarteirinha(id);
        return ResponseEntity.ok(Map.of("mensagem", "Carteirinha bloqueada"));
    }

    // ── Gerenciamento de Usuários ────────────────────────────────────────────

    @GetMapping("/usuarios")
    public ResponseEntity<List<EstudanteResponseDTO>> listarUsuarios(
            @RequestParam(required = false) String busca) {
        List<Estudante> lista = (busca != null && !busca.isBlank())
                ? estudanteRepository.buscarPorTexto(busca)
                : estudanteRepository.findByAtivoTrue();
        List<EstudanteResponseDTO> dtos = lista.stream().map(e -> {
            String nomeCurso = e.getIdCurso() != null
                    ? cursoRepository.findById(e.getIdCurso()).map(Curso::getNomeCurso).orElse(null)
                    : null;
            return new EstudanteResponseDTO(e, nomeCurso);
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<EstudanteResponseDTO> obterUsuario(@PathVariable Integer id) {
        Estudante e = estudanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        String nomeCurso = e.getIdCurso() != null
                ? cursoRepository.findById(e.getIdCurso()).map(Curso::getNomeCurso).orElse(null)
                : null;
        return ResponseEntity.ok(new EstudanteResponseDTO(e, nomeCurso));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<EstudanteResponseDTO> atualizarUsuario(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> dados) {
        Estudante e = estudanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        if (dados.containsKey("nomeCompleto") && dados.get("nomeCompleto") != null)
            e.setNomeCompleto((String) dados.get("nomeCompleto"));
        if (dados.containsKey("email") && dados.get("email") != null)
            e.setEmail((String) dados.get("email"));
        if (dados.containsKey("idCurso") && dados.get("idCurso") != null)
            e.setIdCurso((Integer) dados.get("idCurso"));
        e.setDataAtualizacao(LocalDateTime.now());
        estudanteRepository.save(e);
        String nomeCurso = e.getIdCurso() != null
                ? cursoRepository.findById(e.getIdCurso()).map(Curso::getNomeCurso).orElse(null)
                : null;
        return ResponseEntity.ok(new EstudanteResponseDTO(e, nomeCurso));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> excluirUsuario(@PathVariable Integer id) {
        Estudante e = estudanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        e.setAtivo(false);
        e.setDataAtualizacao(LocalDateTime.now());
        estudanteRepository.save(e);
        return ResponseEntity.ok(Map.of("mensagem", "Estudante desativado com sucesso"));
    }
}
