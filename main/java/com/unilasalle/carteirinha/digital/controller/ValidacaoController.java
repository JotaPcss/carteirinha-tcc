package com.unilasalle.carteirinha.digital.controller;

import com.unilasalle.carteirinha.digital.entity.Carteirinha;
import com.unilasalle.carteirinha.digital.entity.StatusCarteirinha;
import com.unilasalle.carteirinha.digital.entity.ValidacaoLog;
import com.unilasalle.carteirinha.digital.repository.CarteirinhaRepository;
import com.unilasalle.carteirinha.digital.repository.ValidacaoLogRepository;
import com.unilasalle.carteirinha.digital.util.AesEncryptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/validacao")
public class ValidacaoController {

    private final AesEncryptionService aesEncryptionService;
    private final CarteirinhaRepository carteirinhaRepository;
    private final ValidacaoLogRepository logRepository;

    @Value("${qr.code.validade.minutos:5}")
    private int validadeMinutos;

    public ValidacaoController(AesEncryptionService aesEncryptionService,
                               CarteirinhaRepository carteirinhaRepository,
                               ValidacaoLogRepository logRepository) {
        this.aesEncryptionService = aesEncryptionService;
        this.carteirinhaRepository = carteirinhaRepository;
        this.logRepository = logRepository;
    }

    @GetMapping("/verificar")
    public ResponseEntity<?> verificar(@RequestParam("token") String token,
                                       HttpServletRequest request) {
        try {
            // 1. Descriptografar o token
            String decrypted = aesEncryptionService.decrypt(token);
            String[] parts = decrypted.split("\\|");
            if (parts.length != 2) {
                return badRequest("Token inválido", token, request);
            }

            Integer carteirinhaId = Integer.parseInt(parts[0]);
            long timestamp = Long.parseLong(parts[1]);

            // 2. Verificar validade temporal (ex: 5 minutos)
            long agora = System.currentTimeMillis();
            if (agora - timestamp > validadeMinutos * 60 * 1000L) {
                registrarLog(token, request, "QR Code expirado", carteirinhaId);
                return ResponseEntity.badRequest().body(Map.of("valido", false, "motivo", "QR Code expirado"));
            }

            // 3. Buscar carteirinha
            Carteirinha carteirinha = carteirinhaRepository.findById(carteirinhaId)
                    .orElse(null);
            if (carteirinha == null) {
                registrarLog(token, request, "Carteirinha não encontrada", carteirinhaId);
                return ResponseEntity.status(404).body(Map.of("valido", false, "motivo", "Carteirinha não encontrada"));
            }

            // 4. Verificar status e data de validade
            if (carteirinha.getStatus() != StatusCarteirinha.ATIVA) {
                registrarLog(token, request, "Carteirinha " + carteirinha.getStatus().name(), carteirinhaId);
                return ResponseEntity.status(403).body(Map.of("valido", false, "motivo", "Carteirinha " + carteirinha.getStatus().name().toLowerCase()));
            }

            if (carteirinha.getDataValidade().isBefore(LocalDateTime.now().toLocalDate())) {
                registrarLog(token, request, "Carteirinha expirada", carteirinhaId);
                return ResponseEntity.status(403).body(Map.of("valido", false, "motivo", "Carteirinha expirada"));
            }

            // 5. Sucesso
            registrarLog(token, request, "SUCESSO", carteirinhaId);
            Map<String, Object> response = new HashMap<>();
            response.put("valido", true);
            response.put("nome", carteirinha.getEstudante().getNomeCompleto());
            response.put("curso", carteirinha.getCurso().getNomeCurso());
            response.put("validade", carteirinha.getDataValidade().toString());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            registrarLog(token, request, "Erro: " + e.getMessage(), null);
            return ResponseEntity.status(400).body(Map.of("valido", false, "motivo", "Token inválido"));
        }
    }

    private void registrarLog(String token, HttpServletRequest request, String resultado, Integer carteirinhaId) {
        ValidacaoLog log = new ValidacaoLog();
        log.setTokenUtilizado(token);
        log.setDataHora(LocalDateTime.now());
        log.setIpOrigem(request.getRemoteAddr());
        log.setResultado(resultado);
        log.setCarteirinhaId(carteirinhaId);
        logRepository.save(log);
    }

    private ResponseEntity<Map<String, Object>> badRequest(String mensagem, String token, HttpServletRequest request) {
        registrarLog(token, request, mensagem, null);
        return ResponseEntity.badRequest().body(Map.of("valido", false, "motivo", mensagem));
    }
}