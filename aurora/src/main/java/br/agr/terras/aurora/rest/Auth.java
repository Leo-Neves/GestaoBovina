package br.agr.terras.aurora.rest;

import com.google.gson.annotations.SerializedName;

public class Auth{
	private String usuario;
	private String token;

	public Auth(String usuario, String token) {
		this.usuario = usuario;
		this.token = token;
	}

	public Auth() {

	}

	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}