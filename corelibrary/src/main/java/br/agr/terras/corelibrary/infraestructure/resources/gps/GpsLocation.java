package br.agr.terras.corelibrary.infraestructure.resources.gps;

/**
 * Created by leo on 21/10/16.
 */

import android.location.Location;

import br.agr.terras.aurora.log.Logger;

/**
 * Created by leo on 05/10/16.
 */
public class GpsLocation {
    private LocationReceiver locationReceiver;
    private Location location;
    public boolean enableLog;

    public void startGps(final GpsListener gpsListener) {
        locationReceiver = new LocationReceiver(new GpsListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpsListener.onLocationChanged(location);
                GpsLocation.this.location = location;
                if (enableLog)
                    Logger.d("GpsChanged\nLat: %f\tLon: %f\tAlt: %f\tAcc: %f",location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy());
            }
        });
        locationReceiver.onConnectionSuspended(33);
        location = locationReceiver.getLocation();
    }

    public void stopGps() {
        if (locationReceiver != null)
            locationReceiver.stop();
        locationReceiver = null;
    }

    public Location getLastKnowLocation() {
        return location;
    }

    public boolean isEnabled() {
        return locationReceiver != null;
    }

    public interface GpsListener {
        void onLocationChanged(Location location);
    }
}

