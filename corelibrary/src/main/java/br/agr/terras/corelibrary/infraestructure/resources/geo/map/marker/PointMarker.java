package br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker;

import android.graphics.Color;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by leo on 13/10/16.
 */
public class PointMarker {
    private Marker marker;
    private GroundOverlay groundOverlay;
    private Object id;
    private String idNativo;
    private int color = Color.WHITE;

    public PointMarker(Marker marker){
        this.marker = marker;
        idNativo = marker.getId();
    }

    public PointMarker(GroundOverlay groundOverlay){
        this.groundOverlay = groundOverlay;
        this.idNativo = groundOverlay.getId();
    }

    public void setId(Object id){
        this.id = id;
    }

    public Object getId() {
        return id;
    }

    public String getSnippet() {
        if (marker!=null)
            return marker.getSnippet();
        return null;
    }

    public Marker getMarker() {
        return marker;
    }

    public String getIdNativo() {
        return idNativo;
    }

    public void remove() {
        if (marker!=null)
            marker.remove();
        if (groundOverlay!=null)
            groundOverlay.remove();
    }

    public void setPosition(LatLng latLng) {
        if (marker!=null)
            marker.setPosition(latLng);
        if (groundOverlay!=null)
            groundOverlay.setPosition(latLng);
    }

    public LatLng getPosition() {
        if (marker!=null)
            return marker.getPosition();
        if (groundOverlay!=null)
            return groundOverlay.getPosition();
        return null;
    }

    public void setVisible(boolean visible) {
        if (marker!=null)
            marker.setVisible(visible);
        if (groundOverlay!=null)
            groundOverlay.setVisible(visible);
    }

    public boolean isVisible(){
        if (marker!=null)
            return marker.isVisible();
        if (groundOverlay!=null)
            return groundOverlay.isVisible();
        return false;
    }

    public void setColor(int color){
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
