package br.agr.terras.corelibrary.infraestructure.resources.geo;

import com.google.android.gms.maps.model.Circle;

/**
 * Created by leo on 18/05/17.
 */

public interface OnCircleClick {
    void circleClicked(String key, String id, Circle circle);
}
