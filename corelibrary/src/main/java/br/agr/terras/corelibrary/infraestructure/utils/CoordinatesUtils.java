package br.agr.terras.corelibrary.infraestructure.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by leo on 02/09/16.
 */
public class CoordinatesUtils {

    /*public static LatLng latLngToMapBoxLatLng(com.google.android.gms.maps.model.LatLng latLng) {
        return new LatLng(latLng.latitude, latLng.longitude);
    }

    public static com.google.android.gms.maps.model.LatLng mapBoxLatLngToLatLng(LatLng latLng) {
        return new com.google.android.gms.maps.model.LatLng(latLng.getLatitude(), latLng.getLongitude());
    }

    public static ArrayList<LatLng> getCirclePoints(LatLng latLng, double radius) {
        int degreesBetweenPoints = 8; //45 sides
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = radius / 6371000.0; // earth radius in meters
        double centerLatRadians = latLng.getLatitude() * Math.PI / 180;
        double centerLonRadians = latLng.getLongitude() * Math.PI / 180;
        ArrayList<LatLng> latLngs = new ArrayList<>(); //array to hold all the points
        for (int index = 0; index < numberOfPoints; index++) {
            double degrees = index * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(Math.sin(centerLatRadians) * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians) * Math.cos(centerLatRadians),
                    Math.cos(distRadians) - Math.sin(centerLatRadians) * Math.sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            latLngs.add(point);
        }
        return latLngs;
    }*/

    public static String latitudeToGMS(LatLng latLng) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(MyDateUtils.getLocaleBR());
        String latString = Location.convert(latLng.latitude, Location.FORMAT_SECONDS);
        String[] latArray = latString.split(":");
//        String directionLat = "N";
//        if (Integer.parseInt(latArray[0]) < 0) {
//            directionLat = "S";
//            latArray[0] = (Integer.parseInt(latArray[0]) * -1) + "";
//        }
        latArray[2] = latArray[2].replaceAll("[,]", ".");
        String latFormat = String.format("%s° %s' %s\"", latArray[0], latArray[1], latArray[2]);
        return latFormat;
    }

    public static String longitudeToGMS(LatLng latLng) {
        String longString = Location.convert(latLng.longitude, Location.FORMAT_SECONDS);
        String[] longArray = longString.split(":");
        //TODO decidir se deve ou não colocar N S W L
        //String directionLong = "E";
        //if (Integer.parseInt(longArray[0]) < 0) {
        //    directionLong = "W";
        //    longArray[0] = (Integer.parseInt(longArray[0]) * -1) + "";
        //}
        longArray[2] = longArray[2].replaceAll("[,]", ".");
        String longFORMAT = String.format("%s° %s' %s\"", longArray[0], longArray[1], longArray[2]);
        return longFORMAT;
    }

    public static Double GMSToDecimal(String gms) {
        try{
            double grau = 0;
            double minuto = 0;
            double segundo = 0;
            int aux = 0;
            char[] text = gms.toCharArray();
            for (int i = 0; i < gms.length(); i++) {
                if (text[i] == 'º') {
                    grau = Double.parseDouble(gms.substring(aux, i));
                    aux = i + 2;
                }
                if (text[i]=='\''){
                    minuto = Double.parseDouble(gms.substring(aux, i));
                    aux = i+2;
                }
                if (text[i]=='\"'){
                    segundo = Double.parseDouble(gms.substring(aux, i));
                }
            }
            return grau+(minuto/60.0)+(segundo/3600.0);
        }catch (Exception e){
            return null;
        }
    }

    public static double GMSToDecimal(double grau, double minuto, double segundo){
        return grau+(minuto/60.0)+(segundo/3600.0);
    }

    public static LatLng getMediaDeLatLngs(List<LatLng> latLngs){
        double lat = 0;
        double lon = 0;
        double size = 0;
        for (LatLng latLng : latLngs){
            lat+=latLng.latitude;
            lon+=latLng.longitude;
            size++;
        }
        LatLng media = new LatLng(lat/size, lon/size);
        return media;
    }

    public static Location latLngToLocation(LatLng latLng){
        Location location  =new Location("33");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setAltitude(0D);
        location.setAccuracy(10F);
        return location;
    }

    public static LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
