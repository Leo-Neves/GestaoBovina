package br.agr.terras.aurora.rest.sync;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import br.agr.terras.aurora.rest.Auth;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UploadREST {
	
	private UploadRESTServiceStragety uploadRESTServiceStrategy;
	
	public UploadREST(UploadRESTServiceStragety uploadRESTServiceStrategy){
		this.uploadRESTServiceStrategy = uploadRESTServiceStrategy;
	}
	
	public Response upload(String appVersion,String path,String uuid,Auth auth){
		Response response = new Response("url",404,"Arquivo nao encontrado",new ArrayList<Header>(),null);
		try{
			File filePhoto = new File(path);
			TypedFile typedFile = new TypedFile("application/octet-stream", filePhoto);
			response =  uploadRESTServiceStrategy.uploadFoto(appVersion,typedFile, uuid,auth.getUsuario(),auth.getToken());
		}catch (Exception e){
			Log.e(getClass().getSimpleName(),"Arquivo "+path+" nao encontrado");
		}
		return response;
	}
}
