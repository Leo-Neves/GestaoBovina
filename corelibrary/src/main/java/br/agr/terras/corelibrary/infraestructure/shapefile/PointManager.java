package br.agr.terras.corelibrary.infraestructure.shapefile;

import com.google.android.gms.maps.model.LatLng;

import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PointMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PointShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PointZShape;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.shapefile.conversion.CoordinateConversion;
import br.agr.terras.corelibrary.infraestructure.utils.ShapefileUtils;

/**
 * Created by leo on 13/12/16.
 */

public class PointManager {
    private List<LatLng> listPoints;

    public PointManager(){
        listPoints = new ArrayList<>();
    }

    public void addPoint(AbstractShape shape){
        PointShape pointShape = (PointShape) shape;
        double longitude = pointShape.getX();
        double latitude = pointShape.getY();
        LatLng latLng = new LatLng(latitude, longitude);
        if (longitude == latLng.longitude && latitude == latLng.latitude)
            listPoints.add(latLng);
        else{
            CoordinateConversion coordinateConversion = new CoordinateConversion();
            double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
            latitude = latlng[0];
            longitude = latlng[1];
            latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                listPoints.add(latLng);
        }
    }

    public void addPointZ(AbstractShape shape){
        PointZShape pointShape = (PointZShape) shape;
        double longitude = pointShape.getX();
        double latitude = pointShape.getY();
        LatLng latLng = new LatLng(latitude, longitude);
        if (longitude == latLng.longitude && latitude == latLng.latitude)
            listPoints.add(latLng);
        else{
            CoordinateConversion coordinateConversion = new CoordinateConversion();
            double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
            latitude = latlng[0];
            longitude = latlng[1];
            latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                listPoints.add(latLng);
        }
    }

    public void addPointM(AbstractShape shape){
        PointMShape pointShape = (PointMShape) shape;
        double longitude = pointShape.getX();
        double latitude = pointShape.getY();
        LatLng latLng = new LatLng(latitude, longitude);
        if (longitude == latLng.longitude && latitude == latLng.latitude)
            listPoints.add(latLng);
        else{
            CoordinateConversion coordinateConversion = new CoordinateConversion();
            double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
            latitude = latlng[0];
            longitude = latlng[1];
            latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                listPoints.add(latLng);
        }
    }

    public List<LatLng> getPoints() {
        return listPoints;
    }
}
