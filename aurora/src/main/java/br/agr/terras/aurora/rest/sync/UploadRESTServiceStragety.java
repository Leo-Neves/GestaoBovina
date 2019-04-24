package br.agr.terras.aurora.rest.sync;

import retrofit.client.Response;
import retrofit.mime.TypedFile;


public interface UploadRESTServiceStragety {
	Response uploadFoto(String appVersion, TypedFile photo, String uuid, String usuario, String token);
}
