package br.agr.terras.corelibrary.infraestructure.shapefile;

import com.google.android.gms.maps.model.LatLng;

import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.MultiPatchShape;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.shapefile.conversion.CoordinateConversion;
import br.agr.terras.corelibrary.infraestructure.utils.ShapefileUtils;

/**
 * Created by leo on 13/12/16.
 */

public class MultipatchManager {
    private List<List<LatLng>> listMultipatchs;

    public MultipatchManager() {
        this.listMultipatchs = new ArrayList<>();
    }

    public void addMultipatch(AbstractShape shape){
        List<LatLng> multiPatch = new ArrayList<>();
        MultiPatchShape multipointShape = (MultiPatchShape) shape;
        for (int i=0; i<multipointShape.getNumberOfPoints(); i++){
            double longitude = multipointShape.getPoints()[i].getX();
            double latitude = multipointShape.getPoints()[i].getY();
            LatLng latLng = new LatLng(latitude, longitude);
            if (longitude == latLng.longitude && latitude == latLng.latitude)
                multiPatch.add(latLng);
            else{
                CoordinateConversion coordinateConversion = new CoordinateConversion();
                double latlng[] = coordinateConversion.utm2LatLon(ShapefileUtils.zone, ShapefileUtils.hemisferio, longitude, latitude);
                latitude = latlng[0];
                longitude = latlng[1];
                latLng = new LatLng(latitude, longitude);
                if (longitude == latLng.longitude && latitude == latLng.latitude)
                    multiPatch.add(latLng);
            }
        }
        listMultipatchs.add(multiPatch);
    }
}
