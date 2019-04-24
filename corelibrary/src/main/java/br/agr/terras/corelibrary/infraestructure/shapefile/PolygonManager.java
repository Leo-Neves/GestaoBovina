package br.agr.terras.corelibrary.infraestructure.shapefile;

import com.google.android.gms.maps.model.LatLng;

import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonZShape;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.infraestructure.shapefile.conversion.CoordinateConversion;
import br.agr.terras.corelibrary.infraestructure.utils.ShapefileUtils;

/**
 * Created by leo on 13/12/16.
 */

public class PolygonManager {
    private List<List<LatLng>> listPolygons;

    public PolygonManager() {
        this.listPolygons= new ArrayList<>();
    }

    public void addPolygon(AbstractShape shape){
        List<LatLng> polygon = new ArrayList<>();
        PolygonShape polygonShape = (PolygonShape) shape;
        for (int i=0; i<polygonShape.getNumberOfPoints(); i++){
            double longitude = polygonShape.getPoints()[i].getX();
            double latitude = polygonShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                polygon.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    polygon.add(latLng);
            }
            Logger.d("Latitude: %f\nLongitude: %f",latitude, longitude);
        }
        listPolygons.add(polygon);
    }

    public void addPolygonZ(AbstractShape shape){
        List<LatLng> polygon = new ArrayList<>();
        PolygonZShape polygonShape = (PolygonZShape) shape;
        for (int i=0; i<polygonShape.getNumberOfPoints(); i++){
            double longitude = polygonShape.getPoints()[i].getX();
            double latitude = polygonShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                polygon.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    polygon.add(latLng);
            }
        }
        listPolygons.add(polygon);
    }

    public void addPolygonM(AbstractShape shape){
        List<LatLng> polygon = new ArrayList<>();
        PolygonMShape polygonShape = (PolygonMShape) shape;
        for (int i=0; i<polygonShape.getNumberOfPoints(); i++){
            double longitude = polygonShape.getPoints()[i].getX();
            double latitude = polygonShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                polygon.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    polygon.add(latLng);
            }
        }
        listPolygons.add(polygon);
    }

    public List<List<LatLng>> getPolygons() {
        return listPolygons;
    }
}
