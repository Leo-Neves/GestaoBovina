package br.agr.terras.corelibrary.infraestructure.kml;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a KML Point. Contains a single coordinate.
 */
public class KmlPoint implements KmlGeometry<LatLng> {

    public static final String GEOMETRY_TYPE = "Point";

    private final LatLng mCoordinate;

    /**
     * Creates a new KmlPoint
     *
     * @param coordinate coordinate of the KmlPoint
     */
    public KmlPoint(LatLng coordinate) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        mCoordinate = coordinate;
    }

    /**
     * Gets the type of geometry
     *
     * @return type of geometry
     */
    @Override
    public String getGeometryType() {
        return GEOMETRY_TYPE;
    }


    /**
     * Gets the coordinates
     *
     * @return LatLng with the coordinate of the KmlPoint
     */
    public LatLng getGeometryObject() {
        return mCoordinate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(GEOMETRY_TYPE).append("{");
        sb.append("\n coordinates=").append(mCoordinate);
        sb.append("\n}\n");
        return sb.toString();
    }

    public static List<LatLng> containerToLatLngs(KmlContainer container){
        List<LatLng> listList = new ArrayList<>();
        for (KmlPlacemark placemark : container.getPlacemarks()){
            LatLng latLng = placemarkToLatLng(placemark);
            if (latLng!=null)
                listList.add(latLng);
        }
        for (KmlContainer c : container.getContainers()){
            List<LatLng> list = containerToLatLngs(c);
            if (list!=null && list.size()>0)
                listList.addAll(list);
        }
        return listList;
    }

    public static LatLng placemarkToLatLng(KmlPlacemark placemark){
        if (placemark.getGeometry().getGeometryType().equals(GEOMETRY_TYPE)){
            KmlPoint point = (KmlPoint) placemark.getGeometry();
            return  point.getGeometryObject();
        }
        return null;
    }
}
