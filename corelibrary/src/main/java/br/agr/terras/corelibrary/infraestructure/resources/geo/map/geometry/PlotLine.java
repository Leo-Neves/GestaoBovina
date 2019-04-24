package br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by leo on 29/08/16.
 */
public class PlotLine {
    private Polyline polyline;
    private Object id;

    public PlotLine(GoogleMap map, int strokeColor, int strokeSize, int zIndex, List<LatLng> latLngs){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngs);
        polylineOptions.color(strokeColor);
        polylineOptions.width(strokeSize);
        polylineOptions.zIndex(zIndex);
        polyline = map.addPolyline(polylineOptions);
    }

    public void setStrokeColor(int strokeColor) {
        polyline.setColor(strokeColor);
    }

    public void setStrokeSize(int strokeSize) {
        polyline.setWidth(strokeSize);
    }

    public void setLatLngs(List<LatLng> latLngs) {
        polyline.setPoints(latLngs);
    }

    public void setZIndex(int zIndex){
        polyline.setZIndex(zIndex);
    }

    public List<LatLng> getLatLngs(){
        return polyline.getPoints();
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void remove() {
        polyline.remove();
    }

    public Polyline getPolyline() {
        return polyline;
    }
}
