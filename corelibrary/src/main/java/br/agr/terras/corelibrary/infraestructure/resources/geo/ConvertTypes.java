package br.agr.terras.corelibrary.infraestructure.resources.geo;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by leo on 16/03/16.
 */
public class ConvertTypes {

    public enum Geometria {
        POLYGON("Polygon"),
        MULTIPOLYGON("MultiPolygon"),
        MULTILINESTRING("MultiLineString"),
        LINESTRING("LineString"),
        MULTIPOINT("MultiPoint"),
        POINT("Point"),
        CIRCLE("Circle");
        private String tipo;
        private double raio;

        Geometria(String tipo) {
            this.tipo = tipo;
        }

        public static Geometria getTipo(String tipo) {
            if (tipo.equals(POLYGON.toString()))
                return POLYGON;
            if (tipo.equals(MULTIPOINT.toString()))
                return MULTIPOINT;
            if (tipo.equals(MULTIPOLYGON.toString()))
                return MULTIPOLYGON;
            if (tipo.equals(MULTILINESTRING.toString()))
                return MULTILINESTRING;
            if (tipo.equals(LINESTRING.toString()))
                return LINESTRING;
            if (tipo.equals(POINT.toString()))
                return POINT;
            if (tipo.equals(CIRCLE.toString()))
                return CIRCLE;
            return null;
        }

        public String toString() {
            return tipo;
        }
    }

    public static String convertListCoordenadasToTheGeom(List<List<Coordenada>> list, Geometria tipo) {
        boolean isPoint = isPointOrMultiPointOrCircle(tipo.toString());
        JSONObject json = new JSONObject();
        try {
            json.put("type", tipo.toString());
            JSONArray multidimensionalArray = new JSONArray();
            JSONArray arrayOfArray = new JSONArray();
            JSONArray array = new JSONArray();
            JSONArray point = new JSONArray();
            for (List<Coordenada> latLngs : list) {
                array = new JSONArray();
                if (!isPoint && latLngs.size() > 3)
                    latLngs.add(latLngs.get(0));
                for (Coordenada coordenada : latLngs) {
                    point = new JSONArray();
                    point.put(coordenada.getLongitude());
                    point.put(coordenada.getLatitude());
                    point.put(coordenada.getAltitude());
                    array.put(point);
                    if (tipo.equals(Geometria.CIRCLE) && coordenada instanceof Circulo)
                        json.put("radius", ((Circulo) coordenada).getRaio());
                }
                arrayOfArray.put(array);
            }
            boolean isMultiPolygon = isMultiPolygon(tipo.toString());
            boolean isPolygonOrMultiLineString = isPolygonOrMultiLineString(tipo.toString());
            boolean isLineStringOrMultiPoint = isLineStringOrMultiPoint(tipo.toString());
            if (arrayOfArray.length() == 0)
                arrayOfArray.put(array);
            multidimensionalArray.put(arrayOfArray);
            json.put("coordinates", isMultiPolygon ? multidimensionalArray : isPolygonOrMultiLineString ? arrayOfArray : isLineStringOrMultiPoint ? array : point);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static String convertListLatLngsToTheGeom(List<List<LatLng>> latLngsList, Geometria tipo) {
        List<List<Coordenada>> listFull = new ArrayList<>();
        for (List<LatLng> latLngs : latLngsList) {
            List<Coordenada> coordenadas = new ArrayList<>();
            for (final LatLng latLng : latLngs)
                if (latLng != null)
                    coordenadas.add(createCoordenada(latLng.longitude, latLng.latitude, 0));
            listFull.add(coordenadas);
        }
        return convertListCoordenadasToTheGeom(listFull, tipo);
    }

    public static String convertLatLngsToTheGeom(List<LatLng> latLngs, Geometria tipo) {
        List<Coordenada> coordenadas = new ArrayList<>();
        for (final LatLng latLng : latLngs)
            if (latLng != null)
                coordenadas.add(createCoordenada(latLng.longitude, latLng.latitude, 0));
        return convertCoordenadasToTheGeom(coordenadas, tipo);
    }

    public static String convertCoordenadasToTheGeom(List<Coordenada> coordenadas, Geometria tipo) {
        if (!isPointOrMultiPointOrCircle(tipo.toString()) && coordenadas.size() > 3)
            coordenadas.add(coordenadas.get(0));
        JSONObject json = new JSONObject();
        try {
            json.put("type", tipo.toString());
            JSONArray multidimensionalArray = new JSONArray();
            JSONArray arrayOfArray = new JSONArray();
            JSONArray array = new JSONArray();
            JSONArray point = new JSONArray();
            for (Coordenada coordenada : coordenadas) {
                point = new JSONArray();
                point.put(coordenada.getLongitude());
                point.put(coordenada.getLatitude());
                point.put(coordenada.getAltitude());
                array.put(point);
                if (tipo.equals(Geometria.CIRCLE) && coordenada instanceof Circulo)
                    json.put("radius", ((Circulo) coordenada).getRaio());
            }
            boolean isMultiPolygon = isMultiPolygon(tipo.toString());
            boolean isPolygonOrMultiLineString = isPolygonOrMultiLineString(tipo.toString());
            boolean isLineStringOrMultiPoint = isLineStringOrMultiPoint(tipo.toString());
            arrayOfArray.put(array);
            multidimensionalArray.put(arrayOfArray);
            json.put("coordinates", isMultiPolygon ? multidimensionalArray : isPolygonOrMultiLineString ? arrayOfArray : isLineStringOrMultiPoint ? array : point);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    public static List<LatLng> convertTheGeomToLatLngsEasy(String theGeom) {
        return convertTheGeomToLatLngs(theGeom);
    }

    public static List<List<LatLng>> convertTheGeomToListLatLngsEasy(String theGeom) {
        return convertTheGeomToListLatLngs(theGeom);
    }

    private static List<List<LatLng>> convertTheGeomToListLatLngs(String theGeom) {
        List<List<Coordenada>> coordenadasList = convertTheGeomToListCoordenadas(theGeom);
        List<List<LatLng>> listFull = new ArrayList<>();
        for (List<Coordenada> latLngs : coordenadasList) {
            List<LatLng> coordenadas = new ArrayList<>();
            for (final Coordenada latLng : latLngs)
                coordenadas.add(new LatLng(latLng.getLatitude(), latLng.getLongitude()));
            listFull.add(coordenadas);
        }
        return listFull;
    }

    public static List<LatLng> convertTheGeomToLatLngs(String theGeom) {
        if (theGeom==null)
            return new ArrayList<>();
        List<Coordenada> coordenadas = convertTheGeomToCoordenadas(theGeom);
        List<LatLng> latLngs = new ArrayList<>();
        for (Coordenada coordenada : coordenadas)
            latLngs.add(new LatLng(coordenada.getLatitude(), coordenada.getLongitude()));
        return latLngs;
    }

    public static List<Coordenada> convertTheGeomToCoordenadas(String theGeom) {
        List<Coordenada> latLngs = new ArrayList<>();
        try {
            JSONObject geometry = new JSONObject(theGeom);
            String tipo = geometry.has("type") && geometry.getString("type") != null ? geometry.getString("type") : null;
            if (tipo != null && tipo.equals("MultiPolygon")) {
                JSONArray coordinates = ((JSONArray) ((JSONArray) geometry.getJSONArray("coordinates").get(0)).get(0));
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs.add(createCoordenada(lon, lat, alt));
                }
                if (!isPointOrMultiPointOrCircle(tipo) && latLngs.size() > 3 && latLngs.get(0).getLatitude() == latLngs.get(latLngs.size() - 1).getLatitude())
                    latLngs.remove(latLngs.size() - 1);
            } else if (tipo != null && tipo.equals("MultiLineString")) {
                JSONArray coordinates = ((JSONArray) (geometry.getJSONArray("coordinates").get(0)));
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs.add(createCoordenada(lon, lat, alt));
                }
                if (!isPointOrMultiPointOrCircle(tipo) && latLngs.size() > 3 && latLngs.get(0).getLatitude() == latLngs.get(latLngs.size() - 1).getLatitude())
                    latLngs.remove(latLngs.size() - 1);
            } else if (tipo != null && tipo.equals("Polygon")) {
                JSONArray coordinates = ((JSONArray) geometry.getJSONArray("coordinates").get(0));
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs.add(createCoordenada(lon, lat, alt));
                }
                if (!isPointOrMultiPointOrCircle(tipo) && latLngs.size() > 3 && latLngs.get(0).getLatitude() == latLngs.get(latLngs.size() - 1).getLatitude())
                    latLngs.remove(latLngs.size() - 1);
            } else if (tipo != null && tipo.equals("MultiPoint")) {
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs.add(createCoordenada(lon, lat, alt));
                }
            } else if (tipo != null && tipo.equals("LineString")) {
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs.add(createCoordenada(lon, lat, alt));
                }
                if (!isPointOrMultiPointOrCircle(tipo) && latLngs.size() > 3 && latLngs.get(0).getLatitude() == latLngs.get(latLngs.size() - 1).getLatitude())
                    latLngs.remove(latLngs.size() - 1);
            } else if (tipo != null && tipo.equals("Point")) {
                JSONArray point = geometry.getJSONArray("coordinates");
                double alt = 0;
                if (point.length() == 3)
                    alt = point.getDouble(2);
                double lat = point.getDouble(1);
                double lon = point.getDouble(0);
                latLngs.add(createCoordenada(lon, lat, alt));
            } else if (tipo != null && tipo.equals("Circle")) {
                JSONArray point = geometry.getJSONArray("coordinates");
                double alt = 0;
                if (point.length() == 3)
                    alt = point.getDouble(2);
                double lat = point.getDouble(1);
                double lon = point.getDouble(0);
                double raio = geometry.optDouble("radius", 0);
                latLngs.add(createCirculo(lon, lat, alt, raio));
            }
        } catch (JSONException ignored) {
            Log.e(ConvertTypes.class.getSimpleName(), String.format("Cannot parse geoJson %s", theGeom));
        }
        return latLngs;
    }

    private static List<List<Coordenada>> convertTheGeomToListCoordenadas(String theGeom) {
        List<List<Coordenada>> latLngs = new ArrayList<>();
        try {
            JSONObject geometry = new JSONObject(theGeom);
            String tipo = geometry.has("type") && geometry.getString("type") != null ? geometry.getString("type") : null;
            if (tipo != null && tipo.equals("MultiPolygon")) {
                for (int i = 0; i < geometry.getJSONArray("coordinates").length(); i++) {
                    JSONArray coordinates = geometry.getJSONArray("coordinates").getJSONArray(i).getJSONArray(0);
                    List<Coordenada> latLngs1 = new ArrayList<>();
                    for (int j = 0; j < coordinates.length(); j++) {
                        JSONArray point = (JSONArray) coordinates.get(j);
                        double alt = 0;
                        if (point.length() == 3)
                            alt = point.getDouble(2);
                        double lat = point.getDouble(1);
                        double lon = point.getDouble(0);
                        latLngs1.add(createCoordenada(lon, lat, alt));
                    }
                    if (!isPointOrMultiPointOrCircle(tipo) && latLngs1.size() > 3 && latLngs1.get(0).getLatitude() == latLngs1.get(latLngs1.size() - 1).getLatitude())
                        latLngs1.remove(latLngs1.size() - 1);
                    latLngs.add(latLngs1);
                }
            }
            if (tipo != null && tipo.equals("MultiLineString")) {
                for (int i = 0; i < geometry.getJSONArray("coordinates").length(); i++) {
                    JSONArray coordinates = geometry.getJSONArray("coordinates").getJSONArray(i);
                    List<Coordenada> latLngs1 = new ArrayList<>();
                    for (int j = 0; j < coordinates.length(); j++) {
                        JSONArray point = (JSONArray) coordinates.get(j);
                        double alt = 0;
                        if (point.length() == 3)
                            alt = point.getDouble(2);
                        double lat = point.getDouble(1);
                        double lon = point.getDouble(0);
                        latLngs1.add(createCoordenada(lon, lat, alt));
                    }
                    if (!isPointOrMultiPointOrCircle(tipo) && latLngs1.size() > 3 && latLngs1.get(0).getLatitude() == latLngs1.get(latLngs1.size() - 1).getLatitude())
                        latLngs1.remove(latLngs1.size() - 1);
                    latLngs.add(latLngs1);
                }
            } else if (tipo != null && tipo.equals("Polygon")) {
                JSONArray coordinates = ((JSONArray) geometry.getJSONArray("coordinates").get(0));
                List<Coordenada> latLngs1 = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs1.add(createCoordenada(lon, lat, alt));
                }
                if (!isPointOrMultiPointOrCircle(tipo) && latLngs1.size() > 3 && latLngs1.get(0).getLatitude() == latLngs1.get(latLngs1.size() - 1).getLatitude())
                    latLngs1.remove(latLngs1.size() - 1);
                latLngs.add(latLngs1);
            } else if (tipo != null && tipo.equals("MultiPoint")) {
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                List<Coordenada> latLngs1 = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs1.add(createCoordenada(lon, lat, alt));
                }
                latLngs.add(latLngs1);
            } else if (tipo != null && tipo.equals("LineString")) {
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                List<Coordenada> latLngs1 = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = (JSONArray) coordinates.get(i);
                    double alt = 0;
                    if (point.length() == 3)
                        alt = point.getDouble(2);
                    double lat = point.getDouble(1);
                    double lon = point.getDouble(0);
                    latLngs1.add(createCoordenada(lon, lat, alt));
                }
                if (!isPointOrMultiPointOrCircle(tipo) && latLngs1.size() > 3 && latLngs1.get(0).getLatitude() == latLngs1.get(latLngs1.size() - 1).getLatitude())
                    latLngs1.remove(latLngs1.size() - 1);
                latLngs.add(latLngs1);
            } else if (tipo != null && tipo.equals("Point")) {
                JSONArray point = geometry.getJSONArray("coordinates");
                List<Coordenada> latLngs1 = new ArrayList<>();
                double alt = 0;
                if (point.length() == 3)
                    alt = point.getDouble(2);
                double lat = point.getDouble(1);
                double lon = point.getDouble(0);
                latLngs1.add(createCoordenada(lon, lat, alt));
                latLngs.add(latLngs1);
            } else if (tipo != null && tipo.equals("Circle")) {
                JSONArray point = geometry.getJSONArray("coordinates");
                List<Coordenada> latLngs1 = new ArrayList<>();
                double alt = 0;
                if (point.length() == 3)
                    alt = point.getDouble(2);
                double lat = point.getDouble(1);
                double lon = point.getDouble(0);
                double raio = geometry.optDouble("radius", 0);
                latLngs1.add(createCirculo(lon, lat, alt, raio));
                latLngs.add(latLngs1);
            }
        } catch (JSONException | NullPointerException ignored) {
            Log.e(ConvertTypes.class.getSimpleName(), String.format("Cannot parse geoJson %s", theGeom));
        }
        return latLngs;
    }

    public static Geometria getTipoGeometria(String geoJson) {
        try {
            JSONObject geometry = new JSONObject(geoJson);
            if (geometry.has("type") && geometry.optString("type", null) != null) {
                return Geometria.getTipo(geometry.optString("type"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isPointOrMultiPointOrCircle(String tipo) {
        return tipo.contains("Point") || tipo.contains("Circle");
    }

    private static boolean isMultiPolygon(String tipo) {
        return tipo.equals("MultiPolygon");
    }

    private static boolean isPolygonOrMultiLineString(String tipo) {
        return tipo.equals("MultiLineString") || tipo.equals("Polygon");
    }

    private static boolean isLineStringOrMultiPoint(String tipo) {
        return tipo.equals("LineString") || tipo.equals("MultiPoint");
    }

    private static Coordenada createCoordenada(final double longitude, final double latitude, final double altitude) {
        return new Coordenada() {
            @Override
            public double getLatitude() {
                return latitude;
            }

            @Override
            public double getLongitude() {
                return longitude;
            }

            @Override
            public double getAltitude() {
                return altitude;
            }
        };
    }

    private static Circulo createCirculo(final double longitude, final double latitude, final double altitude, final double raio) {
        return new Circulo() {
            @Override
            public double getLatitude() {
                return latitude;
            }

            @Override
            public double getLongitude() {
                return longitude;
            }

            @Override
            public double getAltitude() {
                return altitude;
            }

            @Override
            public double getRaio() {
                return raio;
            }
        };
    }

}
