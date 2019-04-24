package br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.agr.terras.corelibrary.R;
import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnMapMarkerClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.PointFunctions;
import br.agr.terras.corelibrary.infraestructure.resources.geo.PolygonFunctions;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.PointMarker;

/**
 * Created by leo on 17/11/16.
 */

public class DrawLine {
    private static float STROKE_WIDTH = 7.0f;
    private static int STROKE_COLOR = Color.rgb(0, 255, 255);
    private boolean selected = false;
    private boolean isSelfIntersected = false;

    private OnLineCompleteListener onLineCompleteListener;
    private OnPointAddedToLine onPointAddedToLine;

    private String key;
    private String id;

    private Polyline polyline = null;
    private List<List<LatLng>> limitPoints;
    private List<PointMarker> markers;
    private List<LatLng> limitsCentroide;
    private GoogleMap map;
    private int positionPoligonoLimiteMaisProximo = -1;

    private Integer markerSelected = null;

    private BitmapDescriptor iconSelected;
    private BitmapDescriptor icon;

    private Marker textMarker = null;
    private String textMarkerString = "";
    private int zIndex = 10;
    private boolean autoComplete;

    public DrawLine(GoogleMap map, String key, String id) {
        this.markers = new ArrayList<>();
        this.map = map;
        this.key = key;
        this.id = id;
        iconSelected = BitmapDescriptorFactory.fromResource(R.drawable.selected_vertex);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.unselected_vertex);
    }

    public String getId() {
        return id;
    }

    private boolean verificarSeEstaNoPoligono(LatLng position) {
        List<PointFunctions> listPointFunctions = new ArrayList<PointFunctions>();
        double x = position.longitude;
        double y = position.latitude;
        PointFunctions pointFunctions = new PointFunctions(x, y);
        for (LatLng point : limitPoints.get(positionPoligonoLimiteMaisProximo)) {
            double a = point.longitude;
            double b = point.latitude;
            listPointFunctions.add(new PointFunctions(a, b));
        }
        PolygonFunctions polygonFunctions = PolygonFunctions.Builder().addVertexes(listPointFunctions).build();
        return polygonFunctions.contains(pointFunctions);
    }

    private void calcularCentroideDosLimites() {
        limitsCentroide = new ArrayList<>();
        for (List<LatLng> latLngList : limitPoints) {
            double lat = 0;
            double lon = 0;
            int pointCount = latLngList.size();
            for (LatLng point : latLngList) {
                lat += point.latitude;
                lon += point.longitude;
            }
            lat = lat / pointCount;
            lon = lon / pointCount;
            limitsCentroide.add(new LatLng(lat, lon));
        }
    }

    private void verificarPoligonoMaisProximo(LatLng position) {
        double menorDistancia = SphericalUtil.computeDistanceBetween(limitsCentroide.get(0), position);
        positionPoligonoLimiteMaisProximo = 0;
        for (int i = 0; i < limitsCentroide.size(); i++) {
            if (SphericalUtil.computeDistanceBetween(limitsCentroide.get(i), position) < menorDistancia)
                positionPoligonoLimiteMaisProximo = i;
        }
    }

    private void addMarker(LatLng point) {
        String id = UUID.randomUUID().toString();
        int location = this.markers.size();
        if (markerSelected != null)
            location = markerSelected + 1;
        PointMarker marker = new PointMarker(map.addMarker(new MarkerOptions().position(point).icon(icon).draggable(true).anchor(0.5f, 0.5f).snippet(id)));
        marker.setId(id);
        markers.add(location, marker);
        selectMarker(location);
        selected = true;
        updatePolyline();
        if (onPointAddedToLine != null)
            onPointAddedToLine.onAdd();
    }

    private void selectMarker(Integer markerIndex) {
        String id = UUID.randomUUID().toString();
        if (markerSelected != null) {
            Log.d("MarkerSelected:", Integer.toString(markerSelected));

            // Change icon of last selected marker
            Marker oldMarker = markers.get(markerSelected).getMarker();
            this.markers.get(markerSelected).getMarker().remove();
            this.markers.remove(markerSelected.intValue());
            PointMarker marker = new PointMarker(map.addMarker(new MarkerOptions().position(oldMarker.getPosition()).icon(icon).draggable(true).anchor(0.5f, 0.5f).snippet(id)));
            marker.setId(id);
            this.markers.add(markerSelected, marker);
        }
        if (markerIndex != null) {
            Log.d("MarkerIndex:", Integer.toString(markerIndex));
            // Change icon on new selected marker
            Marker oldMarker = this.markers.get(markerIndex).getMarker();
            markers.get(markerIndex).getMarker().remove();
            this.markers.remove(markerIndex.intValue());
            PointMarker marker = new PointMarker(map.addMarker(new MarkerOptions()
                    .position(oldMarker.getPosition())
                    .icon(iconSelected).draggable(true).anchor(0.5f, 0.5f).snippet(id)));
            marker.setId(id);
            this.markers.add(markerIndex, marker);

        }
        Log.d("MarkerSize:", Integer.toString(this.markers.size()));
        markerSelected = markerIndex;
    }

    private void updatePolyline() {
        List<LatLng> arrayLoc = new ArrayList<LatLng>();
        if (markers.size() == 0)
            positionPoligonoLimiteMaisProximo = -1;
        // Create polyline or update polyline
        for (int i = 0; i < this.markers.size(); i++) {
            Marker curMarker = this.markers.get(i).getMarker();
            LatLng curPos = curMarker.getPosition();
            arrayLoc.add(curPos);
        }

        if (arrayLoc.size() < 2) {
            if (polyline != null) {
                polyline.remove();
                polyline = null;
            }
        } else {
            if (polyline == null) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.color(STROKE_COLOR);
                lineOptions.width(STROKE_WIDTH);
                lineOptions.clickable(true);
                lineOptions.zIndex(zIndex);
                for (int i = 0; i < arrayLoc.size(); i++) {
                    lineOptions.add(arrayLoc.get(i));
                }
                polyline = map.addPolyline(lineOptions);
            } else {
                polyline.setPoints(arrayLoc);
            }
        }
    }

    public GoogleMap.OnMarkerDragListener getOnMarkerDragListener = new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {
        }

        @Override
        public void onMarkerDrag(Marker marker) {
            updatePolyline();
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            updatePolyline();
        }
    };

    public GoogleMap.OnMarkerClickListener getOnMarkerClickListener(final OnMapMarkerClick onMapMarkerClick) {

        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < markers.size(); i++) {
                    Marker m = markers.get(i).getMarker();
                    if (m.getId().equals(marker.getId())) {
                        if (i == 0 && markers.size() > 2 && !isSelfIntersected && autoComplete) {
                            complete();
                        } else {
                            if (onMapMarkerClick != null)
                                onMapMarkerClick.markerClicked(id, key, i, m);
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isCompleted() {
        return markers.size() == 0 && polyline != null;
    }

    public boolean isSelfIntersected() {
        return isSelfIntersected;
    }

    public void complete() {
        for (int i = 0; i < this.markers.size(); i++) {
            this.markers.get(i).getMarker().remove();
        }
        this.markers.clear();
        markerSelected = null;
        Logger.i("Polyline Draw finished:\n" + polyline.getId() + "\n" + polyline.getPoints());
        if (onLineCompleteListener != null)
            onLineCompleteListener.onComplete();
    }

    public void edit() {
        String id = UUID.randomUUID().toString();
        if (polyline != null) {
            List<LatLng> points = polyline.getPoints();
            // Draw markers
            for (int i = 0; i < (points.size()); i++) {
                PointMarker pointMarker = new PointMarker(map.addMarker(new MarkerOptions()
                        .position(points.get(i)).icon(icon).draggable(true).anchor(0.5f, 0.5f).snippet(id)));
                pointMarker.setId(id);
                this.markers.add(pointMarker);
            }
            if (this.markers.size() > 0)
                selectMarker(this.markers.size() - 1);
        }
        updatePolyline();
    }

    public LatLng addPoint(LatLng point) {
        if (limitPoints != null) {
            if (positionPoligonoLimiteMaisProximo == -1)
                verificarPoligonoMaisProximo(point);
            if (verificarSeEstaNoPoligono(point))
                addMarker(point);
        } else
            addMarker(point);
        return point;
    }

    public List<PointMarker> getMarkers() {
        return markers;
    }

    public List<LatLng> getPoints() {
        if (polyline != null) {
            return polyline.getPoints();
        } else {
            return null;
        }
    }

    public void deleteSelectedMarker(Integer position) {
        if (position < 0)
            position = 0;
        selectMarker(position);
        if (this.markers.size() != 0) {
            this.markers.get(markerSelected.intValue()).getMarker().remove();
            this.markers.remove(markerSelected.intValue());
            Integer newSelect = null;
            if (this.markers.size() != 0) {
//                newSelect = markerSelected.intValue() + 1;
                newSelect = markers.size() - 1;
                if (newSelect < 0) {
                    newSelect = 0;
                }
            }
            markerSelected = null;
            if (newSelect != null) {
                selectMarker(newSelect);
            }
            updatePolyline();
        }
    }

    public void setLimits(List<List<LatLng>> limits) {
        limitPoints = limits;
        calcularCentroideDosLimites();
        if (markers.size() > 0)
            verificarPoligonoMaisProximo(markers.get(0).getMarker().getPosition());
        if (polyline != null && !polyline.getPoints().isEmpty())
            verificarPoligonoMaisProximo(polyline.getPoints().get(0));
    }

    public void setOnLineCompleteListener(OnLineCompleteListener onLineCompleteListener) {
        this.onLineCompleteListener = onLineCompleteListener;
    }

    public void setOnPointAddedToLine(OnPointAddedToLine onPointAddedToLine) {
        this.onPointAddedToLine = onPointAddedToLine;
    }

    public void setVisible(boolean visible) {
        if (polyline != null) polyline.setVisible(visible);
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        if (polyline != null)
            polyline.setZIndex(zIndex);
    }

    public void setStrokeColor(int color) {
        if (polyline != null) polyline.setColor(color);
        STROKE_COLOR = color;
    }

    public void setIcons(Bitmap icon, Bitmap iconSelected) {
        this.icon = BitmapDescriptorFactory.fromBitmap(icon);
        this.iconSelected = BitmapDescriptorFactory.fromBitmap(iconSelected);
        if (markers != null)
            for (PointMarker marker : markers)
                marker.getMarker().setIcon(this.icon);
        if (markerSelected != null && markers != null)
            markers.get(markerSelected).getMarker().setIcon(this.iconSelected);
    }

    public void remove() {
        if (textMarker != null) textMarker.remove();
        if (polyline != null) polyline.remove();
        for (PointMarker pointMarker : markers)
            pointMarker.getMarker().remove();
        markers.clear();
        textMarker = null;
        polyline = null;
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public LatLng editPoint(int position, LatLng newPoint) {
        Marker marker = markers.get(position).getMarker();
        marker.setPosition(newPoint);
        updatePolyline();
        return newPoint;
    }

    public interface OnLineCompleteListener {
        void onComplete();
    }

    public interface OnPointAddedToLine {
        void onAdd();
    }
}
