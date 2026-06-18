package com.unilasalle.carteirinha.digital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ValidacaoLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLog;
    private String tokenUtilizado;
    private LocalDateTime dataHora;
    private String ipOrigem;
    private String resultado;
    private Integer carteirinhaId; // opcional, apenas referência
	public Integer getIdLog() {
		return idLog;
	}
	public void setIdLog(Integer idLog) {
		this.idLog = idLog;
	}
	public String getTokenUtilizado() {
		return tokenUtilizado;
	}
	public void setTokenUtilizado(String tokenUtilizado) {
		this.tokenUtilizado = tokenUtilizado;
	}
	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}
	public String getIpOrigem() {
		return ipOrigem;
	}
	public void setIpOrigem(String ipOrigem) {
		this.ipOrigem = ipOrigem;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public Integer getCarteirinhaId() {
		return carteirinhaId;
	}
	public void setCarteirinhaId(Integer carteirinhaId) {
		this.carteirinhaId = carteirinhaId;
	}

}