package br.agr.terras.corelibrary.infraestructure.resources;

import android.app.Activity;
import android.app.Dialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GooglePlayServiceManager {
	private Activity activity;
	private Dialog dialogGooglePlayService;
	private boolean existeGooglePlay;
	private int erroCodeGoogPlayServices;
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	
	public GooglePlayServiceManager(Activity activity){
		this.activity = activity;
	}
	
	public boolean isGooglePlay() {
		erroCodeGoogPlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		
		if (erroCodeGoogPlayServices == ConnectionResult.SERVICE_MISSING
				|| erroCodeGoogPlayServices == ConnectionResult.SERVICE_DISABLED
				|| erroCodeGoogPlayServices == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
			existeGooglePlay = false;
			return existeGooglePlay;
		} else {
			existeGooglePlay = true;
			return existeGooglePlay;
		}
	}

	public void instalarGooglePlayServices() {

		dialogGooglePlayService = GooglePlayServicesUtil.getErrorDialog(erroCodeGoogPlayServices, activity,
                PLAY_SERVICES_RESOLUTION_REQUEST);
		dialogGooglePlayService.show();
	}
	
	public void closeDialog(){
		if (dialogGooglePlayService != null) {
			dialogGooglePlayService.dismiss();
		}
	}
}	
