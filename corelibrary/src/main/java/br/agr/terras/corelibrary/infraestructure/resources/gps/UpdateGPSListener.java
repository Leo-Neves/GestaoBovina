package br.agr.terras.corelibrary.infraestructure.resources.gps;

import android.app.Activity;
import android.location.Location;

public interface UpdateGPSListener {
	public void onLocationChanged(Location location);
	public Activity getActivity();
}
