package br.agr.terras.corelibrary.infraestructure.kml;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a KML LineString. Contains a single array of coordinates.
 */
public class KmlLineString implements KmlGeometry<List<LatLng>> {

    public static final String GEOMETRY_TYPE = "LineString";

    final ArrayList<LatLng> mCoordinates;

    /**
     * Creates a new KmlLineString object
     *
     * @param coordinates array of coordinates
     */
    public KmlLineString(ArrayList<LatLng> coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        mCoordinates = coordinates;
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
     * @return ArrayList of LatLng
     */
    public ArrayList<LatLng> getGeometryObject() {
        return mCoordinates;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(GEOMETRY_TYPE).append("{");
        sb.append("\n coordinates=").append(mCoordinates);
        sb.append("\n}\n");
        return sb.toString();
    }

    public static List<List<LatLng>> containerToLatLngsList(KmlContainer container){
        List<List<LatLng>> listList = new ArrayList<>();
        for (KmlPlacemark placemark : container.getPlacemarks()){
            List<LatLng> list = placemarkToLatLngs(placemark);
            if (list!=null)
                listList.add(list);
        }
        for (KmlContainer c : container.getContainers()){
            List<List<LatLng>> list = containerToLatLngsList(c);
            if (list!=null && list.size()>0)
                listList.addAll(list);
        }
        return listList;
    }

    public static List<LatLng> placemarkToLatLngs(KmlPlacemark placemark){
        if (placemark.getGeometry().getGeometryType().equals(GEOMETRY_TYPE)){
            KmlLineString lineString = (KmlLineString) placemark.getGeometry();
            return  lineString.getGeometryObject();
        }
        return null;
    }
}
