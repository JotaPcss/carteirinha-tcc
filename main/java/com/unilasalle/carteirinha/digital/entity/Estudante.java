package com.unilasalle.carteirinha.digital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "estudante")
public class Estudante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstudante;

    @Column(unique = true, nullable = false, length = 20)
    private String matricula;

    @Column(nullable = false, length = 100)
    private String nomeCompleto;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(name = "id_curso")
    private Integer idCurso;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "url_foto", length = 255)
    private String urlFoto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_foto")
    private StatusFoto statusFoto = StatusFoto.PENDENTE;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(nullable = false)
    private String senha;   // armazenará o hash BCrypt

    private Boolean ativo = true;

    // Construtor padrão
    public Estudante() {}

    // Getters e Setters (todos públicos)
    public Integer getIdEstudante() { return idEstudante; }
    public void setIdEstudante(Integer idEstudante) { this.idEstudante = idEstudante; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }

    public StatusFoto getStatusFoto() { return statusFoto; }
    public void setStatusFoto(StatusFoto statusFoto) { this.statusFoto = statusFoto; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}