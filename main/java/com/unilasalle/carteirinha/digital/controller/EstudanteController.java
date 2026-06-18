package com.unilasalle.carteirinha.digital.controller;

import com.unilasalle.carteirinha.digital.dto.EstudanteCadastroDTO;
import com.unilasalle.carteirinha.digital.dto.EstudanteResponseDTO;
import com.unilasalle.carteirinha.digital.entity.Carteirinha;
import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.StatusCarteirinha;
import com.unilasalle.carteirinha.digital.entity.StatusFoto;
import com.unilasalle.carteirinha.digital.repository.CarteirinhaRepository;
import com.unilasalle.carteirinha.digital.repository.CursoRepository;
import com.unilasalle.carteirinha.digital.repository.EstudanteRepository;
import com.unilasalle.carteirinha.digital.service.EstudanteService;
import com.unilasalle.carteirinha.digital.service.UploadService;
import com.unilasalle.carteirinha.digital.util.AesEncryptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/estudantes")
public class EstudanteController {

    @Value("${app.base.url:http://localhost:8080}")
    private String appBaseUrl;

    private final EstudanteService estudanteService;
    private final EstudanteRepository estudanteRepository;
    private final CursoRepository cursoRepository;
    private final UploadService uploadService;
    private final AesEncryptionService aesEncryptionService;
    private final CarteirinhaRepository carteirinhaRepository;

    public EstudanteController(EstudanteService estudanteService,
                               EstudanteRepository estudanteRepository,
                               CursoRepository cursoRepository,
                               UploadService uploadService,
                               AesEncryptionService aesEncryptionService,
                               CarteirinhaRepository carteirinhaRepository) {
        this.estudanteService = estudanteService;
        this.estudanteRepository = estudanteRepository;
        this.cursoRepository = cursoRepository;
        this.uploadService = uploadService;
        this.aesEncryptionService = aesEncryptionService;
        this.carteirinhaRepository = carteirinhaRepository;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<EstudanteResponseDTO> cadastrar(@Valid @RequestBody EstudanteCadastroDTO dto) {
        Estudante novo = estudanteService.cadastrar(dto);
        String nomeCurso = novo.getIdCurso() != null
                ? cursoRepository.findById(novo.getIdCurso()).map(c -> c.getNomeCurso()).orElse(null)
                : null;
        return ResponseEntity.status(201).body(new EstudanteResponseDTO(novo, nomeCurso));
    }

    @GetMapping("/me")
    public ResponseEntity<EstudanteResponseDTO> obterPerfil(Authentication authentication) {
        String matricula = authentication.getName();
        Estudante estudante = estudanteRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        String nomeCurso = estudante.getIdCurso() != null
                ? cursoRepository.findById(estudante.getIdCurso()).map(c -> c.getNomeCurso()).orElse(null)
                : null;
        return ResponseEntity.ok(new EstudanteResponseDTO(estudante, nomeCurso));
    }

    @PostMapping("/upload-foto")
    public ResponseEntity<?> uploadFoto(@RequestParam("foto") MultipartFile foto,
                                        Authentication authentication) {
        String matricula = authentication.getName();
        Estudante estudante = estudanteRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        try {
            String nomeFoto = uploadService.salvarFoto(foto, matricula);
            estudante.setUrlFoto("/uploads/fotos/" + nomeFoto);
            estudante.setStatusFoto(StatusFoto.PENDENTE);
            estudanteRepository.save(estudante);
            return ResponseEntity.ok(Map.of("mensagem", "Foto enviada, aguardando aprovação"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro ao salvar foto: " + e.getMessage()));
        }
    }

    @GetMapping("/carteirinha/qrcode")
    public ResponseEntity<?> getQrCode(Authentication authentication) {
        String matricula = authentication.getName();
        Estudante estudante = estudanteRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        Carteirinha carteirinha = carteirinhaRepository.findByEstudanteAndStatus(estudante, StatusCarteirinha.ATIVA)
                .orElseThrow(() -> new RuntimeException("Carteirinha ativa não encontrada"));

        String payload = carteirinha.getIdCarteirinha() + "|" + System.currentTimeMillis();
        String encryptedToken = aesEncryptionService.encrypt(payload);
        String qrCodeUrl = appBaseUrl + "/api/validacao/verificar?token=" + encryptedToken;

        Map<String, String> response = new HashMap<>();
        response.put("qrCodeUrl", qrCodeUrl);
        response.put("token", encryptedToken);
        if (carteirinha.getCurso() != null) {
            response.put("nomeCurso", carteirinha.getCurso().getNomeCurso());
        }
        return ResponseEntity.ok(response);
    }
}
