package br.agr.terras.corelibrary.infraestructure.resources.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import br.agr.terras.corelibrary.R;

public class MyLocationListener implements LocationListener {

	private double latitude;
	private double longitude;
	private float precisao;
	private double altitude;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
	// private static final long MIN_TIME = 1 * 60 * 1000; // 1 minute
	private static final long MIN_TIME = 2000; // 1 minute
	private LocationManager locationManager;
	private boolean providerEnabled = false;
	private boolean gpsTaken = false;
	private Location location;
	private UpdateGPSListener updateGPSListener;

	private boolean mock = false;
	public MyLocationListener(UpdateGPSListener updateGPSListener) {
		this.updateGPSListener = updateGPSListener;
		configuraGPS();
	}
	
	public MyLocationListener(UpdateGPSListener updateGPSListener,boolean mock) {
		this.updateGPSListener = updateGPSListener;
		this.mock = mock;
		configuraGPS();
	}

	public boolean gpsEstaAtivo() {
		return providerEnabled;
	}

	public boolean isGpsTaken() {
		return gpsTaken;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getPrecisao() {
		return precisao;
	}

	public double getAltitude() {
		return altitude;
	}

	String providerTest = "";

	public void configuraGPS() {
		if(mock){
			setupLocationManagerMock();
			iniciaGPSProvider();
		}else{
			gpsReal();
		}
	}

	private void setupLocationManagerMock() {
			locationManager = (LocationManager) updateGPSListener.getActivity()
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
					false, false, false, false, false, true, true, 0, 5);
			locationManager.setTestProviderEnabled(
					LocationManager.GPS_PROVIDER, true);
			Log.i("MyLocationListener", "GPS MOCK");
	}

	private void gpsReal() {
		Log.i("MyLocationListener", "GPS REAL");
		locationManager = (LocationManager) updateGPSListener.getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		providerEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (providerEnabled) {
			iniciaGPSProvider();
		}
	}

	public void pushLocation(double lat, double lon) {
		LocationManager lm = (LocationManager) updateGPSListener.getActivity()
				.getSystemService(Context.LOCATION_SERVICE);

		Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
		mockLocation.setLatitude(lat);
		mockLocation.setLongitude(lon);
		mockLocation.setAltitude(0);
		mockLocation.setTime(System.currentTimeMillis());
		mockLocation.setAccuracy(0f);
		lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
	}

	public void iniciaGPSProvider() {
		
		if (locationManager != null) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			this.location = location;
			if (location != null) {
				refreshLocation();
				Log.i("MyLocationListener", "refreshLocation");
			} else {
				Log.i("MyLocationListener", "sem location");
			}
		}
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	}

	private void refreshLocation() {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		precisao = location.getAccuracy();
		altitude = location.getAltitude();

		Log.i("MyLocationListener", "latitude: " + latitude + " longitude: "
				+ longitude);

		updateGPSListener.onLocationChanged(location);
		gpsTaken = true;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		refreshLocation();

	}

	public void removeUpdate() {
		locationManager.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Toast.makeText(updateGPSListener.getActivity(),"Enabled new provider "
		// + provider, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		// Toast.makeText(updateGPSListener.getActivity(),"Disabled provider " +
		// provider, Toast.LENGTH_SHORT).show();
	}

	public void exibirMensagemAguardeGps() {
		Toast.makeText(
				updateGPSListener.getActivity(),
				updateGPSListener.getActivity().getResources()
						.getString(R.string.aguarde_gps), Toast.LENGTH_SHORT)
				.show();
	}

}
