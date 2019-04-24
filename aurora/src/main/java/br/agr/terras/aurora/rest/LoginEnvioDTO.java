package br.agr.terras.aurora.rest;

public class LoginEnvioDTO {

	private String usuario;
	private String senha;

	public LoginEnvioDTO(String usuario, String senha){
		setUsuario(usuario);
		setSenha(senha);
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

    
}