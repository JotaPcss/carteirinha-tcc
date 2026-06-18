package com.unilasalle.carteirinha.digital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Carteirinha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarteirinha;

    @ManyToOne
    @JoinColumn(name = "id_estudante")
    private Estudante estudante;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    private Curso curso;

    private String qrCodeHash;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    private StatusCarteirinha status = StatusCarteirinha.PENDENTE;

    private Integer versao = 1;

    // getters e setters
    public Integer getIdCarteirinha() { return idCarteirinha; }
    public void setIdCarteirinha(Integer idCarteirinha) { this.idCarteirinha = idCarteirinha; }
    public Estudante getEstudante() { return estudante; }
    public void setEstudante(Estudante estudante) { this.estudante = estudante; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public String getQrCodeHash() { return qrCodeHash; }
    public void setQrCodeHash(String qrCodeHash) { this.qrCodeHash = qrCodeHash; }
    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
    public StatusCarteirinha getStatus() { return status; }
    public void setStatus(StatusCarteirinha status) { this.status = status; }
    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }
}