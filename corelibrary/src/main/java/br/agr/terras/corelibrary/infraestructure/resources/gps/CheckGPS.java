package br.agr.terras.corelibrary.infraestructure.resources.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;

import br.agr.terras.corelibrary.R;
import br.agr.terras.materialdroid.Dialog;
import br.agr.terras.materialdroid.utils.DialogAction;

public class CheckGPS {
	private static GpsLocation gpsLocation;
	private static Location location;

	public static void checkGPS(Activity activity){
		if (!isGPSEnabled(activity)){
			configureGPS(activity);
		}
	}

	public static boolean isGPSEnabled(Activity activity){
		LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		return !(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
	}

	public static void configureGPS(final Activity activity){
		Dialog dialog = new Dialog.Builder(activity)
				.cancelable(false)
				.canceledOnTouchOutside(false)
				.title(activity.getString(R.string.ligar_gps))
				.content(activity.getString(R.string.deseja_ligar_gps))
				.positiveText(activity.getString(R.string.sim))
				.negativeText(activity.getString(android.R.string.cancel))
				.onPositive(new Dialog.SingleButtonCallback() {
					@Override
					public void onClick(Dialog dialog, DialogAction which) {
						dialog.dismiss();
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						activity.startActivity(intent);
					}
				})
				.onNegative(new Dialog.SingleButtonCallback() {
					@Override
					public void onClick(Dialog dialog, DialogAction which) {
						dialog.dismiss();

					}
				})
				.build();
		dialog.show();
	}

	public static void startGPS(Activity activity){
		if (!isGPSEnabled(activity))
			return;
		checkGPS(activity);
		gpsLocation = new GpsLocation();
		gpsLocation.startGps(new GpsLocation.GpsListener() {
			@Override
			public void onLocationChanged(Location location) {
				CheckGPS.location = location;
			}
		});
		CheckGPS.location = gpsLocation.getLastKnowLocation();
	}

	public static void stopGPS(){
		if (gpsLocation !=null)
			gpsLocation.stopGps();
	}

	public static Location getLocation(){
		return location;
	}

	public static LatLng getLatLng(){
		if (location==null)
			return null;
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

}