package br.agr.terras.corelibrary.infraestructure.resources.geo;

import com.google.android.gms.maps.model.Polyline;

/**
 * Created by leo on 04/01/17.
 */

public interface OnLineClick {
    void polylineClicked(String key, String id, Polyline polyline);
}
