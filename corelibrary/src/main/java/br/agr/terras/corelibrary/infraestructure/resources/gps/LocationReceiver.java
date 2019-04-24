package br.agr.terras.corelibrary.infraestructure.resources.gps;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import br.agr.terras.aurora.log.Logger;

import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 21/10/16.
 */

@SuppressWarnings("MissingPermission")
public class LocationReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    private Location locationAtual;
    private GpsLocation.GpsListener gpsListener;

    public LocationReceiver(GpsLocation.GpsListener gpsListener) {
        this.gpsListener = gpsListener;
        googleApiClient = new GoogleApiClient.Builder(CORE.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //if (locationAtual == null)
        //  locationAtual = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        Logger.i("GoogleApiCLient conectado: " + googleApiClient.isConnected());
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Logger.e("Erro de conexão GPS: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        locationAtual = location;
        gpsListener.onLocationChanged(location);
    }

    void stop() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    public Location getLocation() {
        return locationAtual;
    }
}