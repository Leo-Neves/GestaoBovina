package br.agr.terras.aurora.rest;

import retrofit.RequestInterceptor;
import android.util.Base64;

public class ApiRequestInterceptor implements RequestInterceptor {

    private String usuario;
    private String senha;

    @Override
    public void intercept(RequestFacade requestFacade) {

        
            final String authorizationValue = encodeCredentialsForBasicAuthorization();
            requestFacade.addHeader("Authorization", authorizationValue);
            
    }

    private String encodeCredentialsForBasicAuthorization() {
        final String userAndPassword = usuario + ":" + senha;
        final int flags = 0;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), flags);
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