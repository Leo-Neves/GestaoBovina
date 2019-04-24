package br.agr.terras.corelibrary.infraestructure.resources.geo.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.CORE;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnCircleClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnLineClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnMapMarkerClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.DrawLine;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.DrawPolygon;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.PlotPolygon;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.ClusterRendererListener;
import br.agr.terras.corelibrary.infraestructure.resources.wms.WmsParametros;

/**
 * Created by leo on 04/10/16.
 */
public interface  MapSetup {
    int COR_TRANSPARENTE = Color.TRANSPARENT;
    int COR_AREA_CINZA = Color.parseColor("#cc666666");
    int COR_AREA_LARANJA = Color.parseColor("#CCFF6C00");
    int COR_AREA_VERDE = Color.parseColor("#cc4ae033");
    int COR_AREA_AMARELA = Color.parseColor("#aaf0ff00");
    int COR_AREA_VERMELHA = Color.parseColor("#cce00009");
    int COR_BORDA_AREA_BRANCA = Color.parseColor("#90FFFFFF");
    int COR_MARCADOR_VERDE = Color.parseColor("#ff439443");
    int COR_MARCADOR_AZUL = Color.parseColor("#4198d9");
    int COR_AZUL_CIANO = CORE.getContext().getResources().getColor(R.color.azul_ciano);
    int COR_MARKER_AZUL = CORE.getContext().getResources().getColor(R.color.marker_azul);
    int COR_MARKER_AZUL_ESCURO = CORE.getContext().getResources().getColor(R.color.marker_azul_escuro);
    int COR_AREA_AZUL_TRANSPARENTE = CORE.getContext().getResources().getColor(R.color.polygon_azul_transparente);
    int COR_MARKER_PRETO = CORE.getContext().getResources().getColor(android.R.color.black);

    String KEY_PROPRIEDADE = "propriedade";

    int MODE_SATTELITE = 1;
    int MODE_TERRAIN = 2;
    int MODE_WHITE = 3;
    int MODE_BLACK = 4;
    int MODE_HYBRID = 5;

    void setMyLocationEnabled(boolean enabled);


    void modeSattelite();


    void modeTerrain();


    void modeBlack();


    void modeWhite();

    void modeHybrid();


    void configureLastKnownLocation();


    void refreshMeMarker(Location location);

    void setAutoRefreshMyLocationEnabled(boolean enabled);

    void setFactoryCheckBoxCamadas(FactoryCheckBoxCamadas factoryCheckBoxCamadas);

    void setModeDebug(boolean enable);


    void setMapClickListener(GoogleMap.OnMapClickListener onMapClickListener);


    void setDrawDragListener(OnMapMarkerDrag onDrawMarkerDrag);

    void setMarkerDragListener(OnMapMarkerDrag onMapMarkerDrag);

    void setMapMarkerClick(OnMapMarkerClick onMapMarkerClick);


    void setOnMapDrawMarkerClick(OnMapMarkerClick onMapDrawMarkerClick);

    void setOnMapLineMarkerClick(OnMapMarkerClick onMapLineMarkerClick);

    void setOnPointAddedToLine(DrawLine.OnPointAddedToLine onPointAddedToLine);

    void setOnPointAddedToPolygon(DrawPolygon.OnPointAddedToPolygon onPointAddedToPolygon);

    void setLineClickListener(OnLineClick onLineClick);

    void setCircleClickListener(OnCircleClick onCircleClick);

    void moveMap(LatLng location);


    void zoomMap(float level);


    void clearMap();


    boolean isGpsOn(Activity activity);


    LatLng getMapPosition();


    boolean isModeDebug();


    void setPosition(Location location);

    Boolean hasPolygon(String key);


    void showPolygons(String key, List<String> the_geoms, int strokeColor, int strokeSize, int polygonColor, boolean zoom);

    void addCamada(String key, boolean mostrarCamada);

    void showCircle(String key, String the_geom, int strokeColor, int strokeSize, int polygonColor, boolean zoom);

    void showPolygon(String key, String the_geom, int strokeColor, int strokeSize, int polygonColor, boolean zoom);

    void showLine(String key, String the_geom, int strokeColor, int strokeSize, boolean zoom);


    void showInversePolygons(String key, String geojson, int strokeColor, int strokeSize, int polygonColor);

    List<LatLng> getMarkersLatLngs(String key);


    void deleteMarkerFromPolygon(String key, int position);


    void deleteMarker(String key, int position);

    void deleteMarkerById(String key, String id);


    void deleteVerticeFromPolygon(String key, String id, Integer position);


    void deleteVerticeFromLine(String key, String id, Integer position);

    void undoLastAddToPolygon(String key, String id);


    String getDrawedPolygon(String key);

    String getDrawedLine(String key);

    String getDrawedMarkers(String key);

    void setOnDrawCompleteListener(DrawPolygon.OnDrawCompleteListener onDrawCompleteListener);

    void setOnLineCompleteListener(DrawLine.OnLineCompleteListener onLineCompleteListener);

    void setDrawAutoComplete(String key, boolean autoComplete);

    boolean editPolygon(String key, String id);

    boolean editLine(String key, String id);

    void setDrawColors(String key, String id, int fillColor, int strokeColor);

    void setDrawIcons(String key, Bitmap icon, Bitmap iconSelected);

    void setProhibitedLimit(String key, List<List<LatLng>> limits);

    void setProhibitedLimit(String keyDrawPolygon, String keyPlotPolygon);

    void setDrawLimit(String key, List<List<LatLng>> limits);

    void setDrawLimit(String keyDrawPolygon, String keyPlotPolygon);

    void setLineLimit(String key, List<List<LatLng>> limits);

    LatLng drawPolygon(String key, String id, final LatLng latLng);

    LatLng drawPolygon(String key, String id, final LatLng latLng, int markerPosition);


    void drawPolygon(String key, int strokeColor, int strokeSize, int polygonColor, String titulo, int cor, LatLng latLng);


    LatLng drawLine(String key, String id, LatLng latLng);

    LatLng drawLine(String key, String keyGeometriaAtual, final LatLng latLng, int markerPosition);

    void zoom(String key, int padding);


    void zoom(String key);


    void zoom(int imovelClicado, String string, int padding);

    void zoomAnimated(int imovelClicado, String string, int padding);

    void zoomAnimated(String key, int padding);


    void zoomAnimated(String key);

    Integer checkPositionInPoligono(String key);

    boolean isInsidePoligono(LatLng position);

    Object isInsidePoligono(String key, LatLng position);


    boolean isInsideLinha(String key, LatLng position);

    void addMarker(String id, String key, String titulo, int cor, LatLng lngLatAlt);


    void addMarker(String key, String titulo, int cor, LatLng lngLatAlt);


    void addMarker(final String id, final String key, final LatLng lngLatAlt, int drawable);


    void addMarker(String id, String key, String title, LatLng latLng, int cor);

    void addMarker(final String id, final String key, String title, final LatLng lngLatAlt, int drawable, int cor);


    void addMarker(String id, String key, Bitmap bitmap, LatLng lngLatAlt);

    void moveMarker(String key, String id, Integer position, LatLng latLng);

    void setEnabledMarkers(String key, boolean enabled);


    void addCluster(String key, String id, LatLng lngLatAlt, String nome, int cor);


    void addClusterRenderer(String key, ClusterRendererListener renderer);

    void centerMeMarker();


    List<Double> getDistancesBetwwenMarkersAndMe(String key);

    boolean hasGeometry(String key);


    void remove(String key);


    void setCameraChangeListenerGoogle(GoogleMap.OnCameraChangeListener cameraChangeListener);


    void setZIndex(String key, int zIndex);


    String checkPositionInMarker(String key, int radius);


    Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String mText, int textColor);

    void finishPolygon(String key, String id);

    boolean isDrawedPolygon(String key, String id);

    boolean idDrawedLine(String key, String id);

    boolean isSelfIntersected(String key, String id);

    Object getMap();

    void remove(String key, String id);

    void setVisible(String key, boolean visible);

    PlotPolygon getPolygon(String key, int position);

    void addWMSLayer(String key, WmsParametros parametros);
}
