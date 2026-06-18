package com.unilasalle.carteirinha.digital.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    @Value("${app.upload.dir:uploads/fotos}")
    private String uploadDir;

    /**
     * Valida e salva a foto do estudante, retornando o nome do arquivo salvo.
     * O arquivo fica em {@code uploadDir/nomeArquivo}.
     */
    public String salvarFoto(MultipartFile file, String matricula) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("Arquivo muito grande. Tamanho máximo: 5 MB");
        }

        String extensao = extrairExtensao(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extensao.toLowerCase())) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido. Use: JPG, PNG ou WEBP");
        }

        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String nomeArquivo = matricula + "_" + UUID.randomUUID() + extensao;
        Path filePath = dirPath.resolve(nomeArquivo);
        Files.copy(file.getInputStream(), filePath);
        return nomeArquivo;
    }

    private String extrairExtensao(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
