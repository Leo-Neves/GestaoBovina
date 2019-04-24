package br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import br.agr.terras.corelibrary.infraestructure.resources.geo.Circulo;

/**
 * Created by leo on 29/08/16.
 */
public class PlotCircle {
    private Circle circle;
    private Object id;

    public PlotCircle(GoogleMap map, int strokeColor, int strokeSize, int fillColor, int zIndex, Circulo circulo){
        CircleOptions circleOptions = new CircleOptions()
            .center(new LatLng(circulo.getLatitude(), circulo.getLongitude()))
            .radius(circulo.getRaio())
            .strokeColor(strokeColor)
            .strokeWidth(strokeSize)
            .fillColor(fillColor)
            .zIndex(zIndex)
            .clickable(false);
        circle = map.addCircle(circleOptions);
    }

    public void setFillColor(int fillColor) {
        circle.setFillColor(fillColor);
    }

    public void setStrokeColor(int strokeColor) {
        circle.setStrokeColor(strokeColor);
    }

    public void setStrokeSize(int strokeSize) {
        circle.setStrokeWidth(strokeSize);
    }

    public void setCenter(LatLng center) {
        circle.setCenter(center);
    }

    public void setZIndex(int zIndex){
        circle.setZIndex(zIndex);
    }

    public LatLng getCenter(){
        return circle.getCenter();
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void remove() {
        circle.remove();
    }

    public Circle getCircle() {
        return circle;
    }
}
