package com.unilasalle.carteirinha.digital.entity;

import jakarta.persistence.*;

@Entity
public class Instituicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idInstituicao;
    private String nomeInstituicao;
    private String cnpj;
    private String logotipoUrl;
    private String emailContato;
	public Integer getIdInstituicao() {
		return idInstituicao;
	}
	public void setIdInstituicao(Integer idInstituicao) {
		this.idInstituicao = idInstituicao;
	}
	public String getNomeInstituicao() {
		return nomeInstituicao;
	}
	public void setNomeInstituicao(String nomeInstituicao) {
		this.nomeInstituicao = nomeInstituicao;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getLogotipoUrl() {
		return logotipoUrl;
	}
	public void setLogotipoUrl(String logotipoUrl) {
		this.logotipoUrl = logotipoUrl;
	}
	public String getEmailContato() {
		return emailContato;
	}
	public void setEmailContato(String emailContato) {
		this.emailContato = emailContato;
	}

}