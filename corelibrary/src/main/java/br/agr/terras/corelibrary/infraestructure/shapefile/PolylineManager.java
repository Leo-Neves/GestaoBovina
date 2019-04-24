package br.agr.terras.corelibrary.infraestructure.shapefile;

import com.google.android.gms.maps.model.LatLng;

import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineZShape;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.shapefile.conversion.CoordinateConversion;
import br.agr.terras.corelibrary.infraestructure.utils.ShapefileUtils;

/**
 * Created by leo on 13/12/16.
 */

public class PolylineManager {
    private List<List<LatLng>> listPolylines;

    public PolylineManager() {
        this.listPolylines = new ArrayList<>();
    }

    public void addPolyline(AbstractShape shape){
        List<LatLng> polyline = new ArrayList<>();
        PolylineShape polylineShape = (PolylineShape) shape;
        for (int i=0; i<polylineShape.getNumberOfPoints(); i++){
            double longitude = polylineShape.getPoints()[i].getX();
            double latitude = polylineShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                polyline.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    polyline.add(latLng);
            }
        }
        listPolylines.add(polyline);
    }

    public void addPolylineZ(AbstractShape shape){
        List<LatLng> polyline = new ArrayList<>();
        PolylineZShape polylineShape = (PolylineZShape) shape;
        for (int i=0; i<polylineShape.getNumberOfPoints(); i++){
            double longitude = polylineShape.getPoints()[i].getX();
            double latitude = polylineShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                polyline.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    polyline.add(latLng);
            }
        }
        listPolylines.add(polyline);
    }

    public void addPolylineM(AbstractShape shape){
        List<LatLng> polyline = new ArrayList<>();
        PolylineMShape polylineShape = (PolylineMShape) shape;
        for (int i=0; i<polylineShape.getNumberOfPoints(); i++){
            double longitude = polylineShape.getPoints()[i].getX();
            double latitude = polylineShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                polyline.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    polyline.add(latLng);
            }
        }
        listPolylines.add(polyline);
    }

    public List<List<LatLng>> getPolylines() {
        return listPolylines;
    }
}
