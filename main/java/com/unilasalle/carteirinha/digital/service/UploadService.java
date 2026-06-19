package com.unilasalle.carteirinha.digital.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public class UploadService {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final Cloudinary cloudinary;

    public UploadService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}")    String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key",    apiKey,
                "api_secret", apiSecret,
                "secure",     true
        ));
    }

    /**
     * Faz upload da foto para o Cloudinary e retorna a URL segura (HTTPS).
     * O public_id inclui a matrícula para facilitar identificação no painel.
     */
    public String salvarFoto(MultipartFile file, String matricula) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Arquivo vazio");
        if (file.getSize() > MAX_SIZE_BYTES) throw new IllegalArgumentException("Arquivo muito grande. Máximo: 5 MB");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Tipo não permitido. Use: JPG, PNG ou WEBP");

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder",    "carteirinha",
                        "public_id", "foto_" + matricula,
                        "overwrite", true
                )
        );
        return (String) result.get("secure_url");
    }
}
