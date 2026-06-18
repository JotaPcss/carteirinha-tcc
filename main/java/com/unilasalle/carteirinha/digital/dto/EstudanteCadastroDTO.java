package com.unilasalle.carteirinha.digital.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EstudanteCadastroDTO {
    @NotBlank @Size(max = 20)
    private String matricula;

    private Integer idCurso;

    @NotBlank @Size(max = 100)
    private String nomeCompleto;

    @NotBlank @Pattern(regexp = "\\d{11}")
    private String cpf;

    @NotBlank @Email
    private String email;
    
    @NotBlank
    private String senha;

	@NotNull @Past
    private LocalDate dataNascimento;

	public Integer getIdCurso() { return idCurso; }
	public void setIdCurso(Integer idCurso) { this.idCurso = idCurso; }

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
}