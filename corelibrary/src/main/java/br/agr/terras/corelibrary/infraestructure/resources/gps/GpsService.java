package br.agr.terras.corelibrary.infraestructure.resources.gps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;

import br.agr.terras.corelibrary.R;

public class GpsService {
	private Activity activity;
	private LocationManager manager; 
	public GpsService(Activity activity){
		this.activity = activity;
		manager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );    
	}
	
	public boolean gpsAtivo(){
		return manager.isProviderEnabled( LocationManager.GPS_PROVIDER );	    
	}
	
	public void exibirConfiguracaoGps(){
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setMessage(activity.getResources().getString(R.string.gps_nao_habilitado))
	           .setCancelable(false)
	           .setPositiveButton(activity.getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton(activity.getResources().getString(R.string.nao), new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	
}
