package br.agr.terras.aurora.rest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RespostaGSON<T> implements ParameterizedType{
	private String object_type;
	private String requestid;
	private String mensagem;
	private boolean status;
	private int statusCodeHttp;
	private T result;
	
	private Class<?> wrapped;

	 public RespostaGSON(Class<T> wrapped) {
	        this.wrapped = wrapped;
	    }
	 
	 public RespostaGSON(T result) {
		 this.result = result;
	 }
	 
	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	@Override
	public Type[] getActualTypeArguments() {
        return new Type[] {wrapped};
    }

	@Override
    public Type getRawType() {
        return RespostaGSON.class;
    }

	@Override
    public Type getOwnerType() {
        return null;
    }

	public int getStatusCodeHttp() {
		return statusCodeHttp;
	}

	public void setStatusCodeHttp(int statusCodeHttp) {
		this.statusCodeHttp = statusCodeHttp;
	}
    
	
	
}
