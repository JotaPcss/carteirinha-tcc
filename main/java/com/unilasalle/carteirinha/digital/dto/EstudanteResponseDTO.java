package com.unilasalle.carteirinha.digital.dto;

import com.unilasalle.carteirinha.digital.entity.Estudante;
import com.unilasalle.carteirinha.digital.entity.StatusFoto;
import java.time.LocalDate;

public class EstudanteResponseDTO {
    private Integer idEstudante;
    private String matricula;
    private String nomeCompleto;
    private String email;
    private LocalDate dataNascimento;
    private String urlFoto;
    private StatusFoto statusFoto;
    private Boolean ativo;
    private String nomeCurso;

    public EstudanteResponseDTO(Estudante estudante) {
        this(estudante, null);
    }

    public EstudanteResponseDTO(Estudante estudante, String nomeCurso) {
        this.idEstudante  = estudante.getIdEstudante();
        this.matricula    = estudante.getMatricula();
        this.nomeCompleto = estudante.getNomeCompleto();
        this.email        = estudante.getEmail();
        this.dataNascimento = estudante.getDataNascimento();
        this.urlFoto      = estudante.getUrlFoto();
        this.statusFoto   = estudante.getStatusFoto();
        this.ativo        = estudante.getAtivo();
        this.nomeCurso    = nomeCurso;
    }

    public Integer getIdEstudante()      { return idEstudante; }
    public String getMatricula()         { return matricula; }
    public String getNomeCompleto()      { return nomeCompleto; }
    public String getEmail()             { return email; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getUrlFoto()           { return urlFoto; }
    public StatusFoto getStatusFoto()    { return statusFoto; }
    public Boolean getAtivo()            { return ativo; }
    public String getNomeCurso()         { return nomeCurso; }
}
