package br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

/**
 * Created by leo on 29/08/16.
 */
public class PlotPolygon {
    private Polygon polygon;
    private Object id;

    public PlotPolygon(GoogleMap map, int strokeColor, int strokeSize, int fillColor, int zIndex, List<LatLng> latLngs, List<List<LatLng>> holes){
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(latLngs);
        polygonOptions.strokeColor(strokeColor);
        polygonOptions.strokeWidth(strokeSize);
        polygonOptions.fillColor(fillColor);
        polygonOptions.zIndex(zIndex);
        polygon = map.addPolygon(polygonOptions);
        polygon.setHoles(holes);
    }

    public void setFillColor(int fillColor) {
        polygon.setFillColor(fillColor);
    }

    public void setStrokeColor(int strokeColor) {
        polygon.setStrokeColor(strokeColor);
    }

    public void setStrokeSize(int strokeSize) {
        polygon.setStrokeWidth(strokeSize);
    }

    public void setLatLngs(List<LatLng> latLngs) {
        polygon.setPoints(latLngs);
    }

    public void setZIndex(int zIndex){
        polygon.setZIndex(zIndex);
    }

    public List<LatLng> getLatLngs(){
        return polygon.getPoints();
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void remove() {
        polygon.remove();
    }

    public Polygon getPolygon() {
        return polygon;
    }
}
