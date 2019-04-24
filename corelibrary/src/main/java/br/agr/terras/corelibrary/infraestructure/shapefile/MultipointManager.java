package br.agr.terras.corelibrary.infraestructure.shapefile;

import com.google.android.gms.maps.model.LatLng;

import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.MultiPointMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.MultiPointPlainShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.MultiPointZShape;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.shapefile.conversion.CoordinateConversion;
import br.agr.terras.corelibrary.infraestructure.utils.ShapefileUtils;

/**
 * Created by leo on 13/12/16.
 */

public class MultipointManager {
    private List<List<LatLng>> listMultipoints;

    public MultipointManager() {
        this.listMultipoints = new ArrayList<>();
    }

    public void addMultipoint(AbstractShape shape){
        List<LatLng> multiPoints = new ArrayList<>();
        MultiPointPlainShape multipointShape = (MultiPointPlainShape) shape;
        for (int i=0; i<multipointShape.getNumberOfPoints(); i++){
            double longitude = multipointShape.getPoints()[i].getX();
            double latitude = multipointShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                multiPoints.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    multiPoints.add(latLng);
            }
        }
        listMultipoints.add(multiPoints);
    }

    public void addMultipointZ(AbstractShape shape){
        List<LatLng> multiPoints = new ArrayList<>();
        MultiPointZShape multipointShape = (MultiPointZShape) shape;
        for (int i=0; i<multipointShape.getNumberOfPoints(); i++){
            double longitude = multipointShape.getPoints()[i].getX();
            double latitude = multipointShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                multiPoints.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    multiPoints.add(latLng);
            }
        }
        listMultipoints.add(multiPoints);
    }

    public void addMultipointM(AbstractShape shape){
        List<LatLng> multiPoints = new ArrayList<>();
        MultiPointMShape multipointShape = (MultiPointMShape) shape;
        for (int i=0; i<multipointShape.getNumberOfPoints(); i++){
            double longitude = multipointShape.getPoints()[i].getX();
            double latitude = multipointShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                multiPoints.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    multiPoints.add(latLng);
            }
        }
        listMultipoints.add(multiPoints);
    }

    public List<List<LatLng>> getMultipoints() {
        return listMultipoints;
    }
}
