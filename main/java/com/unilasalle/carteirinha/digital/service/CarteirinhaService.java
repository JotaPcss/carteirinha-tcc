package com.unilasalle.carteirinha.digital.service;

import com.unilasalle.carteirinha.digital.entity.Carteirinha;
import com.unilasalle.carteirinha.digital.entity.Curso;
import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.StatusCarteirinha;
import com.unilasalle.carteirinha.digital.repository.CarteirinhaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class CarteirinhaService {

    @Value("${carteirinha.validade.dias:365}")
    private int validadeDias;

    private final CarteirinhaRepository carteirinhaRepository;

    public CarteirinhaService(CarteirinhaRepository carteirinhaRepository) {
        this.carteirinhaRepository = carteirinhaRepository;
    }

    public Carteirinha gerarCarteirinha(Estudante estudante, Curso curso) {
        Carteirinha carteirinha = new Carteirinha();
        carteirinha.setEstudante(estudante);
        carteirinha.setCurso(curso);
        carteirinha.setDataEmissao(LocalDate.now());
        carteirinha.setDataValidade(LocalDate.now().plusDays(validadeDias));
        carteirinha.setStatus(StatusCarteirinha.ATIVA);
        carteirinha.setVersao(1);
        // O hash do QR será gerado depois separadamente
        return carteirinhaRepository.save(carteirinha);
    }

    public void bloquearCarteirinha(Integer id) {
        Carteirinha carteirinha = carteirinhaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteirinha não encontrada"));
        carteirinha.setStatus(StatusCarteirinha.BLOQUEADA);
        carteirinhaRepository.save(carteirinha);
    }
}