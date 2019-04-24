package br.agr.terras.corelibrary.infraestructure.resources.geo.map;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by leo on 22/12/16.
 */

public interface OnMapMarkerDrag {
    void onDrag(String id, String key, Integer position, Marker marker);
}
