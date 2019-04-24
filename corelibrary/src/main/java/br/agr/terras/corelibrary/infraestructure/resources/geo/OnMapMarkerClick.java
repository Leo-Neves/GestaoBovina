package br.agr.terras.corelibrary.infraestructure.resources.geo;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by leo on 10/09/15.
 */
public interface OnMapMarkerClick {
    void markerClicked(String id, String key, Integer position, Marker marker);
}
