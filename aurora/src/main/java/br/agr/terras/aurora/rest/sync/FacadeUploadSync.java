package br.agr.terras.aurora.rest.sync;

import android.util.Log;

import java.util.List;

import br.agr.terras.aurora.entities.EntityAndroidUpload;
import br.agr.terras.aurora.entities.EntityWeb;
import br.agr.terras.aurora.repositories.RepositoryAndroidRestUpload;
import br.agr.terras.aurora.rest.Auth;
import retrofit.client.Response;

public class FacadeUploadSync<W extends EntityWeb,U extends EntityAndroidUpload> {
	
	RepositoryAndroidRestUpload<W,U> repository;
	UploadREST uploadREST;
	UploadRESTServiceStragety uploadRESTServiceStrategy;
	
	public FacadeUploadSync(UploadRESTServiceStragety uploadRESTServiceStrategy,RepositoryAndroidRestUpload<W,U> repository){
		this.uploadRESTServiceStrategy = uploadRESTServiceStrategy;
		this.repository = repository;
	}
	
	public void uploads(String appVersion,Auth auth){
		uploadREST = new UploadREST(uploadRESTServiceStrategy);
		List<U> listEntitiesAndroidUpload = repository.findAllNaoSincronizadosFoto();
		Log.i("FacapeUploadSync","Nº entidades para upload: "+listEntitiesAndroidUpload.size());
		for(U entityAndroidUpload : listEntitiesAndroidUpload){
			Response response = uploadREST.upload(appVersion,entityAndroidUpload.getUpload_path(), entityAndroidUpload.getUuid(),auth);
			Log.i("FacapeUploadSync","Uploading path: "+entityAndroidUpload.getUpload_path());
			Log.i("FacapeUploadSync","Uploading id: "+entityAndroidUpload.getUuid());
			if(response.getStatus()==201){
				repository.updateRestUploadCallback(entityAndroidUpload);
				Log.i("FacapeUploadSync", "Uploaded successful id:" + entityAndroidUpload.getUuid());
			}else{
				Log.e("FacapeUploadSync", "Maybe error when uploading id:" + entityAndroidUpload.getUuid());
			}
		}
	}
}
