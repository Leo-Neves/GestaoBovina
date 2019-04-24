package br.agr.terras.aurora.rest;

import java.util.UUID;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public class EnvioGSON<E> {	
	private transient Context context;
	public EnvioGSON(Context context){
		this.context = context;
		androidObject = new AndroidObject(context);
		requestid = UUID.randomUUID().toString();
	}
	
	public EnvioGSON(Context context,E data){
		this.context = context;
		androidObject = new AndroidObject(context);
		requestid = UUID.randomUUID().toString();
		this.data = data;
	}
	
	@SerializedName("cacheable")
	private boolean cacheable;
	
	@SerializedName("object_type")
	private String object_type;
	
	@SerializedName("requestid")
	private String requestid;
	
	@SerializedName("data")
	private E data;

	@SerializedName("auth")
	private Auth auth;

	public String getObject_type() {
		return object_type;
	}
	
	private AndroidObject androidObject;
	
	public AndroidObject getAndroidObject() {
		return androidObject;
	}
	
	private String android_model;
	
	public String getAndroid_model() {	
		android_model = android.os.Build.MODEL;
		return android_model;
	}	
	
	public void setAndroidObject(AndroidObject androidObject){
		this.androidObject =androidObject;
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

	public E getData() {
		return data;
	}

	public void setData(E data) {
		this.data = data;
	}

	public Auth getAuth() {
		return auth;
	}

	public void setAuth(Auth auth) {
		this.auth = auth;
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}
	
	
}
