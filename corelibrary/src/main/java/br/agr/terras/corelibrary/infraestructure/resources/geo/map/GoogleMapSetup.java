package br.agr.terras.corelibrary.infraestructure.resources.geo.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.resources.geo.Circulo;
import br.agr.terras.corelibrary.infraestructure.resources.geo.ConvertTypes;
import br.agr.terras.corelibrary.infraestructure.resources.geo.Coordenada;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnCircleClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnLineClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnMapMarkerClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.DrawLine;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.DrawPolygon;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.PlotCircle;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.PlotLine;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.PlotPolygon;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.listener.CameraChangeListenerGoogle;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.listener.MarkerClickListenerGoogle;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.listener.MarkerDragListenerGoogle;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.ClusterMarker;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.ClusterRenderer;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.ClusterRendererListener;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.PointMarker;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.ScaleDependenceMarker;
import br.agr.terras.corelibrary.infraestructure.resources.gps.GpsLocation;
import br.agr.terras.corelibrary.infraestructure.resources.wms.WMSTileProvider;
import br.agr.terras.corelibrary.infraestructure.resources.wms.WmsParametros;
import br.agr.terras.corelibrary.infraestructure.utils.ImageUtils;

import static android.R.attr.id;
import static br.agr.terras.corelibrary.infraestructure.utils.ImageUtils.drawableToBitmap;

/**
 * Created by leo on 01/09/16.
 */
public class GoogleMapSetup implements MapSetup {
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private GoogleMap map;
    private ClusterManager<ClusterMarker> clusterManager;
    private ClusterRenderer clusterRenderer;
    private double defaultLat = -3.002;
    private double defaultLong = -47.500;
    private Polygon blackPolygon;
    private Polygon whitePolygon;
    private Circle meCircle;
    private Marker meMarker;
    private GpsLocation gpsLocation;
    private FactoryCheckBoxCamadas factoryCheckBoxCamadas;

    private boolean enableMyLocation = false;
    private boolean isModeDebug = false;

    private HashMap<String, List<PlotPolygon>> hashMapPlotPolygons;
    private HashMap<String, List<PlotLine>> hashMapPlotLines;
    private HashMap<String, List<PlotCircle>> hashMapPlotCircles;
    private HashMap<String, List<DrawPolygon>> hashMapDrawPolygons;
    private HashMap<String, List<DrawLine>> hashMapDrawLines;
    private HashMap<String, List<PointMarker>> hashMapMarkers;
    private HashMap<String, List<ClusterMarker>> hashMapClusters;
    private HashMap<String, List<List<LatLng>>> allPolygonsLatLngs;
    private HashMap<String, List<ScaleDependenceMarker>> hashMapScaleDependence;
    private HashMap<String, TileOverlay> hashMapWMS;

    private OnMapMarkerClick onMapMarkerClick;
    private OnMapMarkerClick onMapDrawMarkerClick;
    private OnMapMarkerClick onMapLineMarkerClick;
    private CameraChangeListenerGoogle cameraChangeListenerGoogle;
    private MarkerClickListenerGoogle markerClickListenerGoogle;
    private MarkerDragListenerGoogle markerDragListenerGoogle;

    private int currentMode = MODE_SATTELITE;

    private Location myLocation;
    private DrawPolygon.OnDrawCompleteListener onDrawCompleteListener;
    private DrawLine.OnLineCompleteListener onLineCompleteListener;
    private DrawLine.OnPointAddedToLine onPointAddedToLine;
    private DrawPolygon.OnPointAddedToPolygon onPointAddedToPolygon;
    private OnLineClick onLineClick;
    private GoogleMap.OnMarkerDragListener onDrawMarkerDrag;

    public GoogleMapSetup(Context context, GoogleMap map) {
        this.context = context;
        allPolygonsLatLngs = new HashMap<>();
        hashMapPlotPolygons = new HashMap<>();
        hashMapPlotLines = new HashMap<>();
        hashMapPlotCircles = new HashMap<>();
        hashMapDrawPolygons = new HashMap<>();
        hashMapDrawLines = new HashMap<>();
        hashMapMarkers = new HashMap<>();
        hashMapClusters = new HashMap<>();
        hashMapScaleDependence = new HashMap<>();
        hashMapWMS = new HashMap<>();
        setup(map);
    }

    @SuppressLint("MissingPermission")
    private void setup(GoogleMap map) {
        this.map = map;
        gpsLocation = new GpsLocation();
        clusterManager = new ClusterManager<>(context, map);
        clusterRenderer = new ClusterRenderer(map, clusterManager);
        clusterManager.setRenderer(clusterRenderer);
        clusterManager.setAlgorithm(new GridBasedAlgorithm<ClusterMarker>());
        cameraChangeListenerGoogle = new CameraChangeListenerGoogle(clusterManager);
        markerClickListenerGoogle = new MarkerClickListenerGoogle(clusterManager);
        markerDragListenerGoogle = new MarkerDragListenerGoogle();
        map.setOnCameraChangeListener(cameraChangeListenerGoogle);
//        map.setOnMarkerClickListener(markerClickListener);
        map.setOnMarkerClickListener(markerClickListenerGoogle);
        map.setOnMarkerDragListener(markerDragListenerGoogle);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        moveMap(new LatLng(defaultLat, defaultLong));
        zoomMap(12);
        createBlackBackground();
        createWhiteBackground();
        configureFirstMode();
        configureMarkerClickListener();
    }

    public void setMyLocationEnabled(boolean enabled) {
        enableMyLocation = enabled;
        if (enabled) {
            if (meMarker == null) {
                createMeMarker();
            }
        } else {
            if (meMarker != null)
                meMarker.remove();
            if (meCircle != null)
                meCircle.remove();
            if (gpsLocation != null)
                gpsLocation.stopGps();
        }
    }


    private void configureFirstMode() {
        switch (currentMode) {
            case MODE_BLACK:
                modeBlack();
                break;
            case MODE_SATTELITE:
                modeSattelite();
                break;
            case MODE_TERRAIN:
                modeTerrain();
                break;
            case MODE_WHITE:
                modeWhite();
                break;
            case MODE_HYBRID:
                modeHybrid();
                break;
        }
    }

    public void modeSattelite() {
        currentMode = MODE_SATTELITE;
        blackPolygon.setVisible(false);
        whitePolygon.setVisible(false);
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void modeTerrain() {
        currentMode = MODE_TERRAIN;
        blackPolygon.setVisible(false);
        whitePolygon.setVisible(false);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void modeBlack() {
        currentMode = MODE_BLACK;
        blackPolygon.setVisible(true);
        whitePolygon.setVisible(false);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void modeWhite() {
        currentMode = MODE_WHITE;
        blackPolygon.setVisible(false);
        whitePolygon.setVisible(true);
        map.setMapType(GoogleMap.MAP_TYPE_NONE);
    }

    public void modeHybrid() {
        currentMode = MODE_HYBRID;
        blackPolygon.setVisible(false);
        whitePolygon.setVisible(false);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void configureFirstLocation() {
        gpsLocation.startGps(new GpsLocation.GpsListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                refreshMeMarker(location);
                moveMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                zoomMap(16);
                gpsLocation.stopGps();
            }
        });
    }


    public void configureLastKnownLocation() {
        gpsLocation.startGps(new GpsLocation.GpsListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                refreshMeMarker(location);
                gpsLocation.stopGps();
            }
        });
    }

    private void createBlackBackground() {
        float delta = 0.1f;
        List points = Arrays.asList(new LatLng(90, -180),
                new LatLng(-90 + delta, -180 + delta),
                new LatLng(-90 + delta, 0),
                new LatLng(-90 + delta, 180 - delta),
                new LatLng(0, 180 - delta),
                new LatLng(90 - delta, 180 - delta),
                new LatLng(90 - delta, 0),
                new LatLng(90 - delta, -180 + delta),
                new LatLng(0, -180 + delta));
        PolygonOptions options = new PolygonOptions();
        options.addAll(points);
        options.fillColor(context.getResources().getColor(R.color.black_80));
        options.strokeColor(context.getResources().getColor(R.color.black_80));
        options.strokeWidth(5);
        options.zIndex(1);
        blackPolygon = map.addPolygon(options);
    }

    private void createWhiteBackground() {
        float delta = 0.1f;
        List points = Arrays.asList(new LatLng(90, -180),
                new LatLng(-90 + delta, -180 + delta),
                new LatLng(-90 + delta, 0),
                new LatLng(-90 + delta, 180 - delta),
                new LatLng(0, 180 - delta),
                new LatLng(90 - delta, 180 - delta),
                new LatLng(90 - delta, 0),
                new LatLng(90 - delta, -180 + delta),
                new LatLng(0, -180 + delta));
        PolygonOptions options = new PolygonOptions();
        options.addAll(points);
        options.fillColor(Color.WHITE);
        options.zIndex(1);
        whitePolygon = map.addPolygon(options);
    }

    private void configureMarkerClickListener() {
        markerClickListenerGoogle.setCustomListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker markerClicked) {
                if (onMapMarkerClick != null && markerClicked.getId() != null) {
                    for (String string : hashMapMarkers.keySet()) {
                        for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                            if (hashMapMarkers.get(string).get(i).getId().equals(markerClicked.getSnippet())) {
                                onMapMarkerClick.markerClicked(hashMapMarkers.get(string).get(i).getSnippet(), string, i, hashMapMarkers.get(string).get(i).getMarker());
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        map.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener() {
            @Override
            public void onGroundOverlayClick(GroundOverlay marker) {
                if (onMapMarkerClick != null) {
                    for (String string : hashMapMarkers.keySet()) {
                        for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                            Log.i(getClass().getSimpleName(), string);
                            Log.i(getClass().getSimpleName(), hashMapMarkers.get(string).get(i).toString());
                            Log.i(getClass().getSimpleName(), hashMapMarkers.get(string).get(i).getId().toString());
                            Log.i(getClass().getSimpleName(), marker.getId());
                            if (hashMapMarkers.get(string).get(i).getIdNativo().equals(marker.getId())) {
                                onMapMarkerClick.markerClicked(hashMapMarkers.get(string).get(i).getId().toString(), string, i, null);
                                return;
                            }
                        }
                    }
                }
            }
        });
    }

    private void createMeMarker() {
        Location location = new Location("33");
        location.setLatitude(defaultLat);
        location.setLongitude(defaultLong);
        location.setAccuracy(200);
        myLocation = myLocation == null ? location : myLocation;
        CircleOptions co = new CircleOptions();
        co.center(new LatLng(location.getLatitude(), location.getLongitude()));
        co.radius(location.getAccuracy());
        co.fillColor(COR_MARKER_AZUL);
        co.strokeColor(COR_MARKER_AZUL_ESCURO);
        co.strokeWidth(2.0f);
        co.zIndex(5);
        meCircle = map.addCircle(co);
        MarkerOptions mo = new MarkerOptions();
        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.memarker_blue));
        mo.snippet("memarker");
        mo.anchor(0.5F, 0.5F);
        mo.infoWindowAnchor(0, 0);
        mo.position(new LatLng(location.getLatitude(), location.getLongitude()));
        meMarker = map.addMarker(mo);
        final PointMarker pointMarker = new PointMarker(meMarker);
        pointMarker.setId("memarker");
        hashMapMarkers.put("memarker", new ArrayList<PointMarker>() {{
            add(pointMarker);
        }});
    }

    public void refreshMeMarker(Location location) {
        myLocation = location;
        meCircle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        meCircle.setRadius(location.getAccuracy());
        meMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void setAutoRefreshMyLocationEnabled(boolean enabled) {
        if (enabled) {
            gpsLocation.startGps(new GpsLocation.GpsListener() {
                @Override
                public void onLocationChanged(Location location) {
                    myLocation = location;
                    refreshMeMarker(location);
                }
            });
        } else {
            gpsLocation.stopGps();
        }
    }

    @Override
    public void setFactoryCheckBoxCamadas(FactoryCheckBoxCamadas factoryCheckBoxCamadas) {
        this.factoryCheckBoxCamadas = factoryCheckBoxCamadas;
    }

    public void setModeDebug(boolean enabled) {
        isModeDebug = enabled;
        if (!enabled) {
            zoomMap(19);
        }
    }

    public void setMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        map.setOnMapClickListener(onMapClickListener);
    }

    public void setDrawDragListener(final OnMapMarkerDrag onDrawMarkerDrag) {
        this.onDrawMarkerDrag = new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (onDrawMarkerDrag != null && marker.getSnippet() != null) {
                    for (String string : hashMapMarkers.keySet()) {
                        for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                            if (hashMapMarkers.get(string).get(i).getId().equals(marker.getSnippet())) {
                                onDrawMarkerDrag.onDrag(hashMapMarkers.get(string).get(i).getSnippet(), string, i, hashMapMarkers.get(string).get(i).getMarker());
                            }
                        }
                    }
                }
            }
        };
        for (String key : hashMapDrawPolygons.keySet()) {
            List<DrawPolygon> list = hashMapDrawPolygons.get(key);
            for (int i = 0; i < list.size(); i++) {
                DrawPolygon drawPolygon = list.get(i);
                drawPolygon.setOnDragListener(this.onDrawMarkerDrag);
                list.set(1, drawPolygon);
                hashMapDrawPolygons.put(key, list);
            }
        }
    }

    public void setMarkerDragListener(final OnMapMarkerDrag onMapMarkerDrag) {
        markerDragListenerGoogle.setCustomDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (onMapMarkerDrag != null && marker.getSnippet() != null) {
                    for (String string : hashMapMarkers.keySet()) {
                        for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                            if (hashMapMarkers.get(string).get(i).getId().equals(marker.getSnippet())) {
                                onMapMarkerDrag.onDrag(hashMapMarkers.get(string).get(i).getSnippet(), string, i, hashMapMarkers.get(string).get(i).getMarker());
                            }
                        }
                    }
                }
            }
        });
    }

    public void setMapMarkerClick(OnMapMarkerClick onMapMarkerClick) {
        this.onMapMarkerClick = onMapMarkerClick;
    }

    public void setOnMapDrawMarkerClick(OnMapMarkerClick onMapDrawMarkerClick) {
        this.onMapDrawMarkerClick = onMapDrawMarkerClick;
    }

    public void setOnMapLineMarkerClick(OnMapMarkerClick onMapLineMarkerClick) {
        this.onMapLineMarkerClick = onMapLineMarkerClick;
    }

    public void setOnPointAddedToLine(DrawLine.OnPointAddedToLine onPointAddedToLine) {
        this.onPointAddedToLine = onPointAddedToLine;
    }

    public void setOnPointAddedToPolygon(DrawPolygon.OnPointAddedToPolygon onPointAddedToPolygon) {
        this.onPointAddedToPolygon = onPointAddedToPolygon;
    }

    @Override
    public void setLineClickListener(final OnLineClick onLineClick) {
        if (onLineClick != null)
            map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {
                    for (String key : hashMapDrawLines.keySet())
                        for (DrawLine drawLine : hashMapDrawLines.get(key))
                            if (drawLine.getPolyline() != null && drawLine.getPolyline().getId().equals(polyline.getId())) {
                                onLineClick.polylineClicked(key, drawLine.getId(), polyline);
                                break;
                            }
                }
            });
        else map.setOnPolylineClickListener(null);
    }

    @Override
    public void setCircleClickListener(final OnCircleClick onCircleClick) {
        for (String key : hashMapPlotCircles.keySet())
            for (PlotCircle plotCircle : hashMapPlotCircles.get(key))
                plotCircle.getCircle().setClickable(onCircleClick != null);
        if (onCircleClick != null)
            map.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    for (String key : hashMapPlotCircles.keySet())
                        for (PlotCircle plotCircle : hashMapPlotCircles.get(key)) {
                            Log.d(getClass().getSimpleName(), "Circle clicked: " + circle.getId() + " plotCircleId: " + plotCircle.getId() + " plotCircleInternalId: " + plotCircle.getCircle().getId());
                            if (plotCircle.getCircle().getId().equals(circle.getId())) {
                                onCircleClick.circleClicked(key, plotCircle.getId().toString(), circle);
                                break;
                            }
                        }
                }
            });
        else map.setOnCircleClickListener(null);
    }

    public void moveMap(LatLng location) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        map.moveCamera(center);
    }


    public void zoomMap(float level) {
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(level);
        map.animateCamera(zoom);
    }


    public void clearMap() {
        defaultLat = meMarker.getPosition().latitude;
        defaultLong = meMarker.getPosition().longitude;
        map.clear();
        clusterManager.clearItems();
        hashMapMarkers = new HashMap<>();
        hashMapClusters = new HashMap<>();
        hashMapPlotPolygons = new HashMap<>();
        hashMapPlotLines = new HashMap<>();
        hashMapPlotCircles = new HashMap<>();
        hashMapDrawPolygons = new HashMap<>();
        hashMapDrawLines = new HashMap<>();
        hashMapWMS = new HashMap<>();
        factoryCheckBoxCamadas.limparCamadas();
        createBlackBackground();
        createWhiteBackground();
        if (!(currentMode == MODE_BLACK)) {
            blackPolygon.setVisible(false);
        }
        if (!(currentMode == MODE_WHITE)) {
            whitePolygon.setVisible(false);
        }
        if (enableMyLocation)
            createMeMarker();
    }


    public boolean isGpsOn(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }


    public LatLng getMapPosition() {
        if (myLocation != null) {
            return new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
        return null;
    }

    public Object getMap() {
        return map;
    }


    public boolean isModeDebug() {
        return isModeDebug;
    }


    public void setPosition(Location location) {
        myLocation = location;
        refreshMeMarker(myLocation);
    }

    public Boolean hasPolygon(String key) {
        return getMarkersLatLngs(key).size() > 2;
    }

    @Override
    public void addCamada(String key, boolean mostrarCamada) {
        int corCamada = context.getResources().getColor(R.color.mdtp_accent_color);
        if (hashMapPlotPolygons.get(key) != null)
            for (PlotPolygon plotPolygon : hashMapPlotPolygons.get(key)) {
                corCamada = plotPolygon.getPolygon().getStrokeColor();
                break;
            }
        if (hashMapPlotLines.get(key) != null)
            for (PlotLine plotLine : hashMapPlotLines.get(key)) {
                corCamada = plotLine.getPolyline().getColor();
                break;
            }
        if (hashMapPlotCircles.get(key) != null)
            for (PlotCircle plotCircle : hashMapPlotCircles.get(key)) {
                corCamada = plotCircle.getCircle().getStrokeColor();
                break;
            }
        if (hashMapMarkers.get(key) != null)
            for (PointMarker marker : hashMapMarkers.get(key)) {
                corCamada = marker.getColor();
                break;
            }
        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                corCamada = drawPolygon.getPolygon().getStrokeColor();
        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                corCamada = drawLine.getPolyline().getColor();
        if (hashMapWMS.get(key) != null) {
            corCamada = COR_BORDA_AREA_BRANCA;
        }
        factoryCheckBoxCamadas.adicionarCamada(this, key, corCamada, mostrarCamada);
    }

    @Override
    public void showCircle(String key, String the_geom, int strokeColor, int strokeSize, int polygonColor, boolean zoom) {
        addCircle(key, ConvertTypes.convertTheGeomToCoordenadas(the_geom), strokeColor, strokeSize, polygonColor);
        if (zoom)
            zoom(key);
    }

    public void showPolygon(String key, String the_geom, int strokeColor, int strokeSize, int polygonColor, boolean zoom) {
        addPolygon(key, the_geom, null, strokeColor, strokeSize, polygonColor);
        if (zoom)
            zoom(key);
    }

    @Override
    public void showLine(String key, String the_geom, int strokeColor, int strokeSize, boolean zoom) {
        List<LatLng> latLngs = null;
        latLngs = ConvertTypes.convertTheGeomToLatLngs(the_geom);
        List<PlotLine> lines = hashMapPlotLines.get(key);
        if (lines == null)
            lines = new ArrayList<>();
        if (latLngs.size() > 0) {
            PlotLine line = new PlotLine(map, strokeColor, strokeSize, 2, latLngs);
            lines.add(line);
            hashMapPlotLines.put(key, lines);
        } else {
            hashMapPlotLines.put(key, null);
        }
    }

    public void showPolygons(String key, List<String> the_geoms, int strokeColor, int strokeSize, int polygonColor, boolean zoom) {
        for (String the_geom : the_geoms)
            addPolygon(key, the_geom, null, strokeColor, strokeSize, polygonColor);
        if (zoom)
            zoom(key);
    }

    private void addCircle(String key, List<Coordenada> coordenadas, int strokeColor, int strokeSize, int polygonColor) {
        List<PlotCircle> circles = hashMapPlotCircles.get(key);
        if (circles == null)
            circles = new ArrayList<>();
        if (coordenadas.size() > 0) {
            for (Coordenada coordenada : coordenadas) {
                PlotCircle circle = new PlotCircle(map, strokeColor, strokeSize, polygonColor, 2, (Circulo) coordenada);
                circle.setId(UUID.randomUUID().toString());
                circles.add(circle);
                hashMapPlotCircles.put(key, circles);
            }
        } else {
            hashMapPlotCircles.put(key, null);
        }
    }

    private void addPolygon(String key, String geojson, String geoJsonHoles, int strokeColor, int strokeSize, int polygonColor) {
        addPolygon(key, ConvertTypes.convertTheGeomToLatLngs(geojson),ConvertTypes.convertTheGeomToListLatLngsEasy(geoJsonHoles), strokeColor, strokeSize, polygonColor);
    }

    private void addPolygon(String key, List<LatLng> latLngs, List<List<LatLng>> holes, int strokeColor, int strokeSize, int polygonColor) {
        List<PlotPolygon> polygons = hashMapPlotPolygons.get(key);
        if (polygons == null)
            polygons = new ArrayList<>();
        if (latLngs.size() > 0) {
            PlotPolygon polygon = new PlotPolygon(map, strokeColor, strokeSize, polygonColor, 2, latLngs, holes);
            polygons.add(polygon);
            hashMapPlotPolygons.put(key, polygons);
        } else {
            hashMapPlotPolygons.put(key, null);
        }
    }

    @Override
    public void showInversePolygons(String key, String geojson,int strokeColor, int strokeSize, int polygonColor) {
        float delta = 0.1f;
        List<LatLng> points = Arrays.asList(new LatLng(90, -180),
                new LatLng(-90 + delta, -180 + delta),
                new LatLng(-90 + delta, 0),
                new LatLng(-90 + delta, 180 - delta),
                new LatLng(0, 180 - delta),
                new LatLng(90 - delta, 180 - delta),
                new LatLng(90 - delta, 0),
                new LatLng(90 - delta, -180 + delta),
                new LatLng(0, -180 + delta));
        addPolygon(key, points, ConvertTypes.convertTheGeomToListLatLngsEasy(geojson), strokeColor,  strokeSize,  polygonColor);
    }

    public List<LatLng> getMarkersLatLngs(String key) {
        List<LatLng> listaLatLngs = new ArrayList<>();
        if (hashMapMarkers.get(key) != null)
            for (PointMarker marker : hashMapMarkers.get(key)) {
                listaLatLngs.add(marker.getPosition());
            }
        return listaLatLngs;
    }

    private void clearPolygonsFromMap() {
        for (String string : hashMapPlotPolygons.keySet()) {
            if (hashMapPlotPolygons.get(string) != null) {
                int size = hashMapPlotPolygons.get(string).size();
                while (size > 0) {
                    hashMapPlotPolygons.get(string).get(size - 1).remove();
                    size--;
                }
            }
        }
    }


    public void deleteMarkerFromPolygon(String key, int position) {
        List<PointMarker> markerList = hashMapMarkers.get(key);
        markerList.get(position).remove();
        markerList.remove(position);
        hashMapMarkers.put(key, markerList);
        List<LatLng> listaLatLongs = new ArrayList<>();
        for (String string : hashMapMarkers.keySet()) {
            for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                listaLatLongs.add(hashMapMarkers.get(string).get(i).getPosition());
            }
        }
        clearPolygonsFromMap();
        addPolygon("", listaLatLongs,null,  COR_AREA_LARANJA, 3, COR_AREA_AMARELA);
    }


    public void deleteMarker(String key, int position) {
        List<PointMarker> markerList = hashMapMarkers.get(key);
        try {
            Marker marker = markerList.get(position).getMarker();
            marker.remove();
            markerList.remove(position);
            hashMapMarkers.put(key, markerList);
        } catch (IndexOutOfBoundsException e) {
            Log.e("MapSetup", "No markers to delete on position " + position);
        } catch (NullPointerException e) {
            Log.e("MapSetup", "List \"" + key + "\" do not exist!");
        }
    }

    @Override
    public void deleteMarkerById(String key, String id) {
        List<PointMarker> markerList = hashMapMarkers.get(key);
        try {
            for (int i = 0; i < markerList.size(); i++) {
                if (markerList.get(i).getId().equals(id)) {
                    markerList.get(i).remove();
                    markerList.remove(i);
                    hashMapMarkers.put(key, markerList);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("MapSetup", "No markers to delete with id " + id);
        } catch (NullPointerException e) {
            Log.e("MapSetup", "List \"" + key + "\" do not exist!");
        }
    }

    @Override
    public void deleteVerticeFromPolygon(String key, String id, Integer position) {
        DrawPolygon drawPolygon = null;
        for (DrawPolygon d : hashMapDrawPolygons.get(key))
            if (d.getId().equals(id))
                drawPolygon = d;
        if (drawPolygon.isSelected()) {
            drawPolygon.deleteSelectedMarker(position);
        }
    }

    @Override
    public void deleteVerticeFromLine(String key, String id, Integer position) {
        DrawLine drawLine = null;
        for (DrawLine d : hashMapDrawLines.get(key))
            if (d.getId().equals(id))
                drawLine = d;
        if (drawLine.isSelected()) {
            drawLine.deleteSelectedMarker(position);
        }
    }

    @Override
    public void undoLastAddToPolygon(String key, String id) {
        DrawPolygon drawPolygon = null;
        for (DrawPolygon d : hashMapDrawPolygons.get(key))
            if (d.getId().equals(id))
                drawPolygon = d;
        if (drawPolygon.isSelected())
            drawPolygon.undo();

    }

    public void finishPolygon(String key, String id) {
        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                if (drawPolygon.isSelected()) {
                    drawPolygon.updatePolygon();
                    drawPolygon.complete();
                }
        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawPolygon : hashMapDrawLines.get(key))
                if (drawPolygon.isSelected()) {
                    drawPolygon.complete();
                }
    }

    @Override
    public boolean isDrawedPolygon(String key, String id) {
        for (DrawPolygon d : hashMapDrawPolygons.get(key))
            if (d.getId().equals(id))
                return d.isCompleted();
        return false;
    }

    @Override
    public boolean idDrawedLine(String key, String id) {
        for (DrawLine d : hashMapDrawLines.get(key))
            if (d.getId().equals(id))
                return d.isCompleted();
        return false;
    }

    @Override
    public boolean isSelfIntersected(String key, String id) {
        for (DrawPolygon d : hashMapDrawPolygons.get(key))
            if (d.getId().equals(id))
                return d.isSelfIntersected();
        return false;
    }

    @Override
    public String getDrawedPolygon(String key) {
        String theGeom = null;
        List<List<LatLng>> list = new ArrayList<>();
        for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
            if (drawPolygon.getPoints() != null)
                list.add(drawPolygon.getPoints());
        if (list.isEmpty())
            return null;
        theGeom = ConvertTypes.convertListLatLngsToTheGeom(list, ConvertTypes.Geometria.MULTIPOLYGON);
        Log.i(getClass().getSimpleName(), "theGeom: " + theGeom == null ? "null" : theGeom);
        return theGeom;
    }

    @Override
    public String getDrawedLine(String key) {
        String theGeom = null;
        List<List<LatLng>> list = new ArrayList<>();
        for (DrawLine drawLine : hashMapDrawLines.get(key))
            if (drawLine.getPoints() != null)
                list.add(drawLine.getPoints());
        if (list.isEmpty())
            return null;
        theGeom = ConvertTypes.convertListLatLngsToTheGeom(list, ConvertTypes.Geometria.MULTILINESTRING);
        Log.i(getClass().getSimpleName(), "theGeom: " + theGeom == null ? "null" : theGeom);
        return theGeom;
    }

    @Override
    public String getDrawedMarkers(String key) {
        String theGeom = null;
        List<PointMarker> markers = hashMapMarkers.get(key);
        List<LatLng> points = new ArrayList<>();
        if (markers == null)
            return null;
        for (PointMarker pointMarker : markers)
            points.add(pointMarker.getPosition());
        theGeom = ConvertTypes.convertLatLngsToTheGeom(points, ConvertTypes.Geometria.MULTIPOINT);
        Log.i(getClass().getSimpleName(), "theGeom: " + theGeom);

        return theGeom;
    }

    public void setOnDrawCompleteListener(DrawPolygon.OnDrawCompleteListener onDrawCompleteListener) {
        this.onDrawCompleteListener = onDrawCompleteListener;
        for (String key : hashMapDrawPolygons.keySet()) {
            List<DrawPolygon> list = hashMapDrawPolygons.get(key);
            if (list != null)
                for (int i = 0; i < list.size(); i++) {
                    DrawPolygon drawPolygon = list.get(i);
                    drawPolygon.setOnDrawCompleteListener(onDrawCompleteListener);
                    list.set(i, drawPolygon);
                }
            hashMapDrawPolygons.put(key, list);
        }
    }

    @Override
    public void setOnLineCompleteListener(DrawLine.OnLineCompleteListener onLineCompleteListener) {
        this.onLineCompleteListener = onLineCompleteListener;
    }

    @Override
    public void setDrawAutoComplete(String key, boolean autoComplete) {
        if (hashMapDrawPolygons.get(key) != null) {
            List<DrawPolygon> list = hashMapDrawPolygons.get(key);
            for (int i = 0; i < list.size(); i++) {
                DrawPolygon drawPolygon = list.get(i);
                drawPolygon.setAutoComplete(autoComplete);
                list.set(i, drawPolygon);
            }
            hashMapDrawPolygons.put(key, list);
        }
        if (hashMapDrawLines.get(key) != null) {
            List<DrawLine> list = hashMapDrawLines.get(key);
            for (int i = 0; i < list.size(); i++) {
                DrawLine drawLine = list.get(i);
                drawLine.setAutoComplete(autoComplete);
                list.set(i, drawLine);
            }
            hashMapDrawLines.put(key, list);
        }
    }

    @Override
    public boolean editPolygon(String key, String id) {
        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                if (drawPolygon.getId().equals(id) && drawPolygon.isSelected()) {
                    drawPolygon.edit();
                    return true;
                }
        return false;
    }

    @Override
    public boolean editLine(String key, String id) {
        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                if (drawLine.getId().equals(id) && drawLine.isSelected()) {
                    drawLine.edit();
                    return true;
                }
        return false;
    }

    public void createDraw(String key, String id) {
        List<DrawPolygon> drawPolygons = hashMapDrawPolygons.get(key);
        DrawPolygon drawPolygon = new DrawPolygon(map, key, id);
        if (drawPolygons == null)
            drawPolygons = new ArrayList<>();
        for (DrawPolygon draw : drawPolygons)
            if (draw.getId().equals(id))
                drawPolygon = draw;
        if (!drawPolygons.contains(drawPolygon))
            drawPolygons.add(drawPolygon);
        hashMapDrawPolygons.put(key, drawPolygons);
    }

    @Override
    public LatLng drawPolygon(String key, String id, LatLng latLng) {
        return drawPolygon(key, id, latLng, -1);
    }

    @Override
    public void setDrawColors(String key, String id, int fillColor, int strokeColor) {
        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                if (drawPolygon.getId().equals(id) && drawPolygon.isSelected()) {
                    drawPolygon.setStrokeColor(strokeColor);
                    drawPolygon.setFillColor(fillColor);
                }
        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                if (drawLine.getId().equals(id) && drawLine.isSelected()) {
                    drawLine.setStrokeColor(strokeColor);
                }
    }

    public void setDrawIcons(String key, Bitmap icon, Bitmap iconSelected) {
        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                if (drawPolygon.getId().equals(id) && drawPolygon.isSelected()) {
                    drawPolygon.setIcons(icon, iconSelected);
                }
        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                if (drawLine.getId().equals(id) && drawLine.isSelected()) {
                    drawLine.setIcons(icon, iconSelected);
                }
    }

    @Override
    public void setProhibitedLimit(String key, List<List<LatLng>> limits) {
        List<DrawPolygon> drawPolygons = hashMapDrawPolygons.get(key);
        if (drawPolygons != null) {
            for (int i = 0; i < drawPolygons.size(); i++) {
                DrawPolygon drawPolygon = drawPolygons.get(i);
                for (List<LatLng> latLngs : limits)
                    drawPolygon.setLocaisProibidos(latLngs);
                drawPolygons.set(i, drawPolygon);
            }
            hashMapDrawPolygons.put(key, drawPolygons);
        }
    }

    @Override
    public void setProhibitedLimit(String keyDrawPolygon, String keyPlotPolygon) {
        final List<LatLng> limits = new ArrayList<>();
        if (hashMapMarkers.get(keyPlotPolygon) != null)
            for (PointMarker pointMarker : hashMapMarkers.get(keyPlotPolygon))
                limits.add(pointMarker.getPosition());
        if (hashMapPlotPolygons.get(keyPlotPolygon) != null)
            for (PlotPolygon polygon : hashMapPlotPolygons.get(keyPlotPolygon))
                limits.addAll(polygon.getLatLngs());
        List<DrawPolygon> drawPolygons = hashMapDrawPolygons.get(keyDrawPolygon);
        if (hashMapDrawPolygons.get(keyDrawPolygon) != null)
            for (int i = 0; i < drawPolygons.size(); i++) {
                DrawPolygon drawPolygon = drawPolygons.get(i);
                drawPolygon.setLocaisProibidos(limits);
                drawPolygons.set(i, drawPolygon);
            }
        hashMapDrawPolygons.put(keyDrawPolygon, drawPolygons);
    }

    @Override
    public void setDrawLimit(String key, List<List<LatLng>> limits) {
        List<DrawPolygon> drawPolygons = hashMapDrawPolygons.get(key);
        if (drawPolygons != null) {
            for (int i = 0; i < drawPolygons.size(); i++) {
                DrawPolygon drawPolygon = drawPolygons.get(i);
                for (List<LatLng> latLngs : limits){
                    Logger.i("limits: %s", latLngs);
                    drawPolygon.setLimits(latLngs);
                }
            }
            hashMapDrawPolygons.put(key, drawPolygons);
        }
    }

    @Override
    public void setDrawLimit(String keyDrawPolygon, String keyPlotPolygon) {
        List<LatLng> limits = new ArrayList<>();
        LatLng limit = null;
        double raio = 0D;
        if (hashMapMarkers.get(keyPlotPolygon) != null)
            for (PointMarker pointMarker : hashMapMarkers.get(keyPlotPolygon))
                limits.add(pointMarker.getPosition());
        if (hashMapPlotPolygons.get(keyPlotPolygon) != null)
            for (PlotPolygon polygon : hashMapPlotPolygons.get(keyPlotPolygon))
                limits.addAll(polygon.getLatLngs());
        if (hashMapPlotCircles.get(keyPlotPolygon) != null)
            for (PlotCircle circle : hashMapPlotCircles.get(keyPlotPolygon)) {
                limit = circle.getCenter();
                raio = circle.getCircle().getRadius();
            }
        List<DrawPolygon> drawPolygons = hashMapDrawPolygons.get(keyDrawPolygon);
        if (drawPolygons != null) {
            for (int i = 0; i < drawPolygons.size(); i++) {
                DrawPolygon drawPolygon = drawPolygons.get(i);
                if (limit != null && raio > 0)
                    drawPolygon.setLimit(limit, raio);
                else
                    drawPolygon.setLimits(limits);
            }
            hashMapDrawPolygons.put(keyDrawPolygon, drawPolygons);
        }
    }

    @Override
    public void setLineLimit(String key, List<List<LatLng>> limits) {
        List<DrawLine> drawLines = hashMapDrawLines.get(key);
        if (hashMapDrawLines.get(key) != null)
            for (int i = 0; i < drawLines.size(); i++) {
                DrawLine drawLine = drawLines.get(i);
                drawLine.setLimits(limits);
            }
        hashMapDrawLines.put(key, drawLines);
    }

    @Override
    public LatLng drawPolygon(String key, String id, LatLng latLng, int markerPosition) {
        DrawPolygon drawPolygon = null;
        int position = -1;
        List<DrawPolygon> list = hashMapDrawPolygons.get(key);
        if (hashMapDrawPolygons.get(key) == null)
            list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getId().equals(id)) {
                drawPolygon = list.get(i);
                position = i;
            }
        if (drawPolygon == null)
            drawPolygon = new DrawPolygon(map, key, id);
        if (markerPosition == -1)
            latLng = drawPolygon.addPoint(latLng);
        else
            latLng = drawPolygon.editPoint(markerPosition, latLng);
        drawPolygon.setOnDragListener(onDrawMarkerDrag);
        drawPolygon.setOnDrawCompleteListener(onDrawCompleteListener);
        drawPolygon.setOnPointAddedToPolygon(onPointAddedToPolygon);
        markerDragListenerGoogle.setDrawDragListener(drawPolygon.getOnMarkerDragListener);
        markerClickListenerGoogle.setDrawListener(drawPolygon.getOnMarkerClickListener(onMapDrawMarkerClick));
        if (position != -1)
            list.set(position, drawPolygon);
        else
            list.add(drawPolygon);
        hashMapDrawPolygons.put(key, list);
        hashMapMarkers.put(key, drawPolygon.getMarkers());
        return latLng;
    }

    public void drawPolygon(String key, int strokeColor, int strokeSize, int polygonColor, String titulo, int cor, LatLng latLng) {

        final Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).draggable(true).anchor(0.4f, 0.4f));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.memarker_green));

        List<PointMarker> markers = hashMapMarkers.get(key);
        if (markers == null)
            markers = new ArrayList<>();
        marker.setSnippet(UUID.randomUUID().toString());
        markers.add(new PointMarker(marker));
        hashMapMarkers.put(key, markers);
        if (hashMapMarkers.get(key).size() > 2) {
            List<LatLng> listaLatLongs = new ArrayList<>();
            for (String string : hashMapMarkers.keySet()) {
                for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                    listaLatLongs.add(hashMapMarkers.get(string).get(i).getPosition());
                }
            }
            clearPolygonsFromMap();
            addPolygon(key, listaLatLongs, null, strokeColor, strokeSize, polygonColor);
        }

    }

    @Override
    public LatLng drawLine(String key, String id, LatLng latLng) {
        return drawLine(key, id, latLng, -1);
    }

    @Override
    public LatLng drawLine(String key, String id, LatLng latLng, int markerPosition) {
        DrawLine drawLine = null;
        int position = -1;
        List<DrawLine> list = hashMapDrawLines.get(key);
        if (hashMapDrawLines.get(key) == null)
            list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getId().equals(id)) {
                drawLine = list.get(i);
                position = i;
            }
        if (drawLine == null)
            drawLine = new DrawLine(map, key, id);
        if (markerPosition == -1)
            latLng = drawLine.addPoint(latLng);
        else
            latLng = drawLine.editPoint(markerPosition, latLng);
        drawLine.setOnLineCompleteListener(onLineCompleteListener);
        drawLine.setOnPointAddedToLine(onPointAddedToLine);
        markerDragListenerGoogle.setLineDragListener(drawLine.getOnMarkerDragListener);
        markerClickListenerGoogle.setLineListener(drawLine.getOnMarkerClickListener(onMapDrawMarkerClick));
        if (position != -1)
            list.set(position, drawLine);
        else
            list.add(drawLine);
        hashMapDrawLines.put(key, list);
        hashMapMarkers.put(key, drawLine.getMarkers());
        return latLng;
    }

    public void zoom(String key, int padding) {
        zoom(-1, key, padding);
    }

    public void zoom(String key) {
        zoom(key, 50);
    }

    public void zoom(int position, String key, int padding) {
        List<Double> longitudes = new ArrayList<Double>();
        List<Double> latitudes = new ArrayList<Double>();

        if (hashMapPlotPolygons.get(key) != null)
            if (position != -1 && hashMapPlotPolygons.get(key).size() > position)
                for (LatLng point : hashMapPlotPolygons.get(key).get(position).getLatLngs()) {
                    longitudes.add(point.longitude);
                    latitudes.add(point.latitude);
                }
            else
                for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
                    for (LatLng point : polygon.getLatLngs()) {
                        longitudes.add(point.longitude);
                        latitudes.add(point.latitude);
                    }
                }

        if (hashMapPlotCircles.get(key) != null)
            if (position != -1 && hashMapPlotCircles.get(key).size() > position) {
                longitudes.add(hashMapPlotCircles.get(key).get(position).getCenter().longitude);
                latitudes.add(hashMapPlotCircles.get(key).get(position).getCenter().latitude);
            } else
                for (PlotCircle circle : hashMapPlotCircles.get(key)) {
                    longitudes.add(circle.getCenter().longitude);
                    latitudes.add(circle.getCenter().latitude);
                }

        if (hashMapPlotLines.get(key) != null)
            if (position != -1 && hashMapPlotLines.get(key).size() > position)
                for (LatLng point : hashMapPlotLines.get(key).get(position).getLatLngs()) {
                    longitudes.add(point.longitude);
                    latitudes.add(point.latitude);
                }
            else
                for (PlotLine line : hashMapPlotLines.get(key)) {
                    for (LatLng point : line.getLatLngs()) {
                        longitudes.add(point.longitude);
                        latitudes.add(point.latitude);
                    }
                }

        if (hashMapMarkers.get(key) != null)
            if (position != -1 && hashMapMarkers.get(key).size() > position) {
                LatLng point = hashMapMarkers.get(key).get(position).getPosition();
                longitudes.add(point.longitude);
                latitudes.add(point.latitude);
            } else
                for (PointMarker point : hashMapMarkers.get(key)) {
                    longitudes.add(point.getPosition().longitude);
                    latitudes.add(point.getPosition().latitude);
                }

        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                if (drawPolygon.getPoints() != null)
                    for (LatLng latLng : drawPolygon.getPoints()) {
                        latitudes.add(latLng.latitude);
                        longitudes.add(latLng.longitude);
                    }

        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                for (LatLng latLng : drawLine.getPoints()) {
                    latitudes.add(latLng.latitude);
                    longitudes.add(latLng.longitude);
                }

        if (longitudes.size() > 0) {
            LatLng northeast = new LatLng(Collections.max(latitudes), Collections.max(longitudes));
            LatLng southwest = new LatLng(Collections.min(latitudes), Collections.min(longitudes));
            LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
        }

    }

    public void zoomAnimated(int position, String key, int padding) {
        List<Double> longitudes = new ArrayList<Double>();
        List<Double> latitudes = new ArrayList<Double>();

        if (hashMapPlotPolygons.get(key) != null)
            if (position != -1)
                for (LatLng point : hashMapPlotPolygons.get(key).get(position).getLatLngs()) {
                    longitudes.add(point.longitude);
                    latitudes.add(point.latitude);
                }
            else
                for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
                    for (LatLng point : polygon.getLatLngs()) {
                        longitudes.add(point.longitude);
                        latitudes.add(point.latitude);
                    }
                }

        if (hashMapPlotCircles.get(key) != null)
            if (position != -1 && hashMapPlotCircles.get(key).size() > position) {
                longitudes.add(hashMapPlotCircles.get(key).get(position).getCenter().longitude);
                latitudes.add(hashMapPlotCircles.get(key).get(position).getCenter().latitude);
            } else
                for (PlotCircle circle : hashMapPlotCircles.get(key)) {
                    longitudes.add(circle.getCenter().longitude);
                    latitudes.add(circle.getCenter().latitude);
                }

        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                for (LatLng latLng : drawPolygon.getPoints()) {
                    latitudes.add(latLng.latitude);
                    longitudes.add(latLng.longitude);
                }

        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                for (LatLng latLng : drawLine.getPoints()) {
                    latitudes.add(latLng.latitude);
                    longitudes.add(latLng.longitude);
                }

        if (longitudes.size() > 0) {
            LatLng northeast = new LatLng(Collections.max(latitudes), Collections.max(longitudes));
            LatLng southwest = new LatLng(Collections.min(latitudes), Collections.min(longitudes));
            LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 500, 500, padding));
        }

    }

    public void zoomAnimated(String key, int padding) {
        zoomAnimated(-1, key, padding);
    }


    public void zoomAnimated(String key) {
        zoomAnimated(key, 50);
    }


    public Integer checkPositionInPoligono(String key) {
        if (hashMapPlotPolygons.get(key) == null) {
            return null;
        }
        for (int i = 0; i < hashMapPlotPolygons.get(key).size(); i++) {
            if (verificarSeEstaNoPoligono(hashMapPlotPolygons.get(key).get(i).getPolygon(), meMarker.getPosition())) {
                return i;
            }
        }
        return null;
    }

    @Override
    public boolean isInsidePoligono(LatLng position) {
        for (List<PlotPolygon> plotPolygonList : hashMapPlotPolygons.values()) {
            for (PlotPolygon polygon : plotPolygonList)
                if (verificarSeEstaNoPoligono(polygon.getPolygon(), position))
                    return true;
        }
        for (List<DrawPolygon> drawPolygons : hashMapDrawPolygons.values())
            for (DrawPolygon drawPolygon : drawPolygons) {
                if (drawPolygon.getPolygon() != null)
                    if (verificarSeEstaNoPoligono(drawPolygon.getPolygon(), position))
                        return true;
            }
        for (List<PlotCircle> plotCircles : hashMapPlotCircles.values())
            for (PlotCircle plotCircle : plotCircles)
                if (SphericalUtil.computeDistanceBetween(plotCircle.getCenter(), position) <= plotCircle.getCircle().getRadius())
                    return true;
        return false;
    }

    @Override
    public Object isInsidePoligono(String key, LatLng position) {
        Logger.i("Acessou isInsidePoligono!");
        if (hashMapPlotPolygons.get(key) != null) {
            Logger.i("Acessou PlotPolygon!");
            for (int i = 0; i < hashMapPlotPolygons.get(key).size(); i++) {
                PlotPolygon plotPolygon = hashMapPlotPolygons.get(key).get(i);
                if (verificarSeEstaNoPoligono(plotPolygon.getPolygon(), position))
                    return plotPolygon.getId();
            }
        }
        if (hashMapDrawPolygons.get(key) != null) {
            Logger.i("Quantidade de DrawPolygon: " + hashMapDrawPolygons.get(key).size());
            for (int i = 0; i < hashMapDrawPolygons.get(key).size(); i++){
                Logger.i("Está no polígono? " + verificarSeEstaNoPoligono(hashMapDrawPolygons.get(key).get(i).getPolygon(), position));
                if (verificarSeEstaNoPoligono(hashMapDrawPolygons.get(key).get(i).getPolygon(), position))
                    return hashMapDrawPolygons.get(key).get(i).getId();
            }
        }
        if (hashMapPlotCircles.get(key) != null) {
            Logger.i("Acessou PlotCircles!");
            for (int i = 0; i < hashMapPlotCircles.get(key).size(); i++) {
                PlotCircle plotCircle = hashMapPlotCircles.get(key).get(i);
                if (SphericalUtil.computeDistanceBetween(plotCircle.getCenter(), position) <= plotCircle.getCircle().getRadius())
                    return plotCircle.getId();
            }
        }
        return null;
    }

    @Override
    public boolean isInsideLinha(String key, LatLng position) {
        for (List<PlotLine> lines : hashMapPlotLines.values())
            for (PlotLine line : lines)
                return PolyUtil.isLocationOnPath(position, line.getLatLngs(), false, 50);
        if (hashMapDrawLines.get(key) != null)
            for (DrawLine line : hashMapDrawLines.get(key))
                if (line.getPoints() != null)
                    return PolyUtil.isLocationOnPath(position, line.getPoints(), false, 50);
        return false;
    }

    private boolean verificarSeEstaNoPoligono(Polygon poligono, LatLng position) {
        return poligono != null && PolyUtil.containsLocation(position, poligono.getPoints(), false);
    }


    public PlotPolygon getPolygon(String key, int position) {
        if (hashMapPlotPolygons.get(key) != null)
            return hashMapPlotPolygons.get(key).get(position);
        return null;
    }

    public void addMarker(String id, String key, String titulo, int cor, LatLng lngLatAlt) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setColor(cor);
        iconGenerator.setTextAppearance(R.style.iconGenText);
        Bitmap bitmapBackground = iconGenerator.makeIcon(titulo);
        addMarker(id, key, bitmapBackground, lngLatAlt);
    }

    public void addMarker(String key, String titulo, int cor, LatLng lngLatAlt) {
        addMarker(UUID.randomUUID().toString(), key, titulo, cor, lngLatAlt);
    }

    public void addMarker(final String id, final String key, final LatLng lngLatAlt, int drawable) {
        Marker marker = map.addMarker(new MarkerOptions().position(lngLatAlt));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(ImageUtils.drawableToBitmap(drawable)));
        addMarker(marker, id, key);
    }

    public void addMarker(String id, String key, String title, LatLng latLng, int cor) {
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions().position(latLng, 100);
        groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(textToBitmap(title, cor)));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(textToBitmap(title, cor)));
        markerOptions.anchor(0.5F, 0.5F);
        Marker marker = map.addMarker(markerOptions);
        addMarker(marker, id, key);
    }

    public void addMarker(final String id, final String key, String title, final LatLng lngLatAlt, int drawable, int cor) {
        Marker marker = map.addMarker(new MarkerOptions().position(lngLatAlt));
        Bitmap sourceBitmap = drawableToBitmap(drawable);
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth(), sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(cor, 1);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        /*View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.component_slider,null);
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setColor(android.R.color.transparent);
        iconGenerator.setTextAppearance(R.style.iconGenText);
        iconGenerator.setContentView(view);
        Bitmap resultBitmap = iconGenerator.makeIcon("Teste da armadilha");*/
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(drawTextToBitmap(context, resultBitmap, title, cor)));
        marker.setAnchor(0.5F, 1);
        addMarker(marker, id, key);

    }

    public void addMarker(String id, String key, Bitmap bitmap, LatLng lngLatAlt) {
        Marker marker = map.addMarker(new MarkerOptions().position(lngLatAlt));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        marker.setAnchor(0.5F, 1);
        addMarker(marker, id, key);
    }

    private void addMarker(Marker marker, String id, String key) {
        List<PointMarker> markers = hashMapMarkers.get(key);
        if (markers == null)
            markers = new ArrayList<>();
        if (id != null)
            marker.setSnippet(id);
        PointMarker pointMarker = new PointMarker(marker);
        pointMarker.setId(id);
        markers.add(pointMarker);
        hashMapMarkers.put(key, markers);
    }

    @Override
    public void moveMarker(String key, String id, Integer position, LatLng latLng) {
        if (hashMapMarkers.get(key) != null) {
            PointMarker marker = null;
            List<PointMarker> list = hashMapMarkers.get(key);
            if (position != null && list.size()>position)
                marker = list.get(position);
            if (id != null)
                for (PointMarker m : hashMapMarkers.get(key))
                    if (m.getId().equals(id))
                        marker = m;
            if (marker != null) {
                marker.setPosition(latLng);
                list.set(position, marker);
                hashMapMarkers.put(key, list);
            }
        }
    }

    private void addGroundOverlay(GroundOverlay groundOverlay, String id, String key) {
        List<PointMarker> markers = hashMapMarkers.get(key);
        if (markers == null)
            markers = new ArrayList<>();
        PointMarker pointMarker = new PointMarker(groundOverlay);
        pointMarker.setId(id);
        markers.add(pointMarker);
        hashMapMarkers.put(key, markers);
    }

    public void setEnabledMarkers(String key, boolean enabled) {
        if (hashMapMarkers.get(key) != null && hashMapMarkers.get(key).size() > 0) {
            boolean status = hashMapMarkers.get(key).get(0).isVisible();
            if (status != enabled)
                for (PointMarker marker : hashMapMarkers.get(key))
                    marker.setVisible(enabled);
        }
    }

    public void addCluster(String key, String id, LatLng lngLatAlt, String nome, int cor) {
        ClusterMarker markerInfo = new ClusterMarker(key, id, lngLatAlt, nome, cor);
        List<ClusterMarker> list = hashMapClusters.get(key);
        if (list == null)
            list = new ArrayList<>();
        clusterManager.addItem(markerInfo);
        list.add(markerInfo);
        hashMapClusters.put(key, list);
    }

    public void addClusterRenderer(String key, ClusterRendererListener renderer) {
        clusterRenderer.addRenderer(key, renderer);
    }

    public void centerMeMarker() {
        moveMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        zoomMap(19);
    }

    public List<Double> getDistancesBetwwenMarkersAndMe(String key) {
        List<Double> distances = new ArrayList<>();
        if (hashMapMarkers.get(key) != null)
            for (PointMarker marker : hashMapMarkers.get(key)) {
                double distance = SphericalUtil.computeDistanceBetween(marker.getPosition(), meMarker.getPosition());
                distances.add(distance);
                Log.i("MapSetup", "Distance betwween me and " + key + " - " + marker.getSnippet() + ": " + distance);
            }
        return distances;
    }

    @Override
    public boolean hasGeometry(String key) {
        return hashMapPlotPolygons.get(key) != null || hashMapMarkers.get(key) != null ||
                hashMapDrawPolygons.get(key) != null || hashMapClusters.get(key) != null ||
                hashMapPlotLines.get(key) != null;
    }

    public void remove(String key) {
        if (hashMapPlotPolygons.get(key) != null)
            for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
                polygon.remove();
            }
        if (hashMapPlotLines.get(key) != null)
            for (PlotLine line : hashMapPlotLines.get(key)) {
                line.remove();
            }
        if (hashMapPlotCircles.get(key) != null)
            for (PlotCircle circle : hashMapPlotCircles.get(key))
                circle.remove();
        if (hashMapMarkers.get(key) != null)
            for (PointMarker marker : hashMapMarkers.get(key))
                marker.remove();
        if (hashMapClusters.get(key) != null)
            for (ClusterMarker markerInfo : hashMapClusters.get(key)) {
                clusterManager.removeItem(markerInfo);
            }
        if (hashMapDrawPolygons.get(key) != null) {
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                drawPolygon.remove();
            hashMapDrawPolygons.remove(key);
        }
        if (hashMapDrawLines.get(key) != null) {
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                drawLine.remove();
            hashMapDrawLines.remove(key);
        }
        if (hashMapWMS.get(key) != null) {
            hashMapWMS.get(key).remove();
            hashMapWMS.remove(key);
        }
        hashMapPlotPolygons.remove(key);
        hashMapPlotCircles.remove(key);
        hashMapPlotLines.remove(key);
        hashMapMarkers.remove(key);
        hashMapClusters.remove(key);
    }

    @Override
    public void remove(String key, String id) {
        if (hashMapPlotPolygons.get(key) != null)
            for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
                if (polygon.getId().equals(id))
                    polygon.remove();
            }
        if (hashMapPlotLines.get(key) != null)
            for (PlotLine line : hashMapPlotLines.get(key)) {
                if (line.getId().equals(id))
                    line.remove();
            }
        if (hashMapPlotCircles.get(key) != null)
            for (PlotCircle circle : hashMapPlotCircles.get(key))
                if (circle.getId().equals(id))
                    circle.remove();
        if (hashMapMarkers.get(key) != null)
            for (PointMarker marker : hashMapMarkers.get(key))
                if (marker.getId().equals(id))
                    marker.remove();
        if (hashMapClusters.get(key) != null)
            for (ClusterMarker markerInfo : hashMapClusters.get(key)) {
                if (markerInfo.getId().equals(id))
                    clusterManager.removeItem(markerInfo);
            }
        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                if (drawPolygon.getId().equals(id))
                    drawPolygon.remove();

        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                if (drawLine.getId().equals(id))
                    drawLine.remove();
        if (hashMapWMS.get(key) != null)
            if (hashMapWMS.get(key).getId().equals(id))
                hashMapWMS.get(key).remove();
    }

    public void setVisible(String key, boolean visible) {
        if (hashMapPlotPolygons.get(key) != null)
            for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
                polygon.getPolygon().setVisible(visible);
            }
        if (hashMapPlotLines.get(key) != null)
            for (PlotLine line : hashMapPlotLines.get(key)) {
                line.getPolyline().setVisible(visible);
            }
        if (hashMapPlotCircles.get(key) != null)
            for (PlotCircle circle : hashMapPlotCircles.get(key)) {
                circle.getCircle().setVisible(visible);
            }
        if (hashMapMarkers.get(key) != null)
            for (PointMarker marker : hashMapMarkers.get(key))
                marker.setVisible(visible);
        if (hashMapDrawPolygons.get(key) != null) {
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                drawPolygon.setVisible(visible);
        }
        if (hashMapDrawLines.get(key) != null) {
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                drawLine.setVisible(visible);
        }
        if (hashMapWMS.get(key) != null) {
            hashMapWMS.get(key).setVisible(visible);
        }
    }

    public void setCameraChangeListenerGoogle(GoogleMap.OnCameraChangeListener cameraChangeListenerGoogle) {
        this.cameraChangeListenerGoogle.setCustomListener(cameraChangeListenerGoogle);
    }

    public void setZIndex(String key, int zIndex) {
        if (hashMapPlotPolygons.get(key) != null && hashMapPlotPolygons.get(key) != null)
            for (PlotPolygon polygon : hashMapPlotPolygons.get(key))
                polygon.setZIndex(zIndex);
        if (hashMapPlotLines.get(key) != null && hashMapPlotLines.get(key) != null)
            for (PlotLine line : hashMapPlotLines.get(key))
                line.setZIndex(zIndex);
        if (hashMapPlotCircles.get(key) != null)
            for (PlotCircle circle : hashMapPlotCircles.get(key))
                circle.setZIndex(zIndex);
        if (hashMapDrawPolygons.get(key) != null)
            for (DrawPolygon drawPolygon : hashMapDrawPolygons.get(key))
                drawPolygon.setZIndex(zIndex);
        if (hashMapDrawLines.get(key) != null)
            for (DrawLine drawLine : hashMapDrawLines.get(key))
                drawLine.setZIndex(zIndex);
    }

    public String checkPositionInMarker(String key, int radius) {
        if (hashMapMarkers.get(key) == null) {
            return null;
        }
        for (int i = 0; i < hashMapMarkers.get(key).size(); i++) {
            if (SphericalUtil.computeDistanceBetween(meMarker.getPosition(), hashMapMarkers.get(key).get(i).getPosition()) < radius) {
                return hashMapMarkers.get(key).get(i).getSnippet();
            }
        }
        return null;
    }

    private Bitmap textToBitmap(String label, int cor) {
        float scale = context.getResources().getDisplayMetrics().density;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(cor);
        paint.setShadowLayer(1 * scale, 0f, 1f, Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(18 * scale);
        paint.setStrokeWidth(10 * scale);

        Rect bounds = new Rect();
        String[] labels = label.split(("\\r?\\n"));
        String biggestLabel = labels[0];
        for (int i = 0; i < labels.length; i++)
            biggestLabel = biggestLabel.length() > labels[i].length() ? biggestLabel : labels[i];
        paint.getTextBounds(biggestLabel, 0, biggestLabel.length(), bounds);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        int width = bounds.width() + 15;
        int height = (bounds.height() * labels.length) + (int) (15 * scale);
        Bitmap bitmap = Bitmap.createBitmap(width, height, conf); //TODO create blank new bitmap
        float x = bitmap.getWidth() / 2;
        float y = -1.0f * bounds.top + (bitmap.getHeight() * 0.06f);

        Canvas canvas = new Canvas(bitmap);
        for (int i = 0; i < labels.length; i++)
            canvas.drawText(labels[i], x, y * (i + 1), paint);
        return bitmap;
    }

    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String mText, int textColor) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap transparentBitmap = Bitmap.createBitmap(bitmap.getWidth(), (int) (bitmap.getHeight() * 1), Bitmap.Config.ARGB_8888);
            //Canvas c = new Canvas(transparentBitmap);
            //c.drawARGB(0,0,0,0);
            Bitmap markerBitmap = ImageUtils.mergeBitmaps(transparentBitmap, bitmap);
            Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(textColor);
            // text size in pixels
            paint.setTextSize((int) (15 * scale));
            // text shadow
            paint.setShadowLayer(2f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (int) ((bitmap.getWidth() - bounds.width()) / 3.3);
            int y = (int) ((bitmap.getHeight() + bounds.height()) / 4.5);
            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }

    @Override
    public void addWMSLayer(String key, WmsParametros parametros) {
        if (hashMapWMS.get(key) != null)
            hashMapWMS.get(key).remove();
        WMSTileProvider wmsTileProvider = new WMSTileProvider(parametros);
        TileOverlay tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(wmsTileProvider));
        hashMapWMS.put(key, tileOverlay);
    }
}
