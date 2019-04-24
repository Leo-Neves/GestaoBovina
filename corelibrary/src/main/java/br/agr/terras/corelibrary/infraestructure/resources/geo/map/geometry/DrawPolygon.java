package br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.resources.geo.OnMapMarkerClick;
import br.agr.terras.corelibrary.infraestructure.resources.geo.PointFunctions;
import br.agr.terras.corelibrary.infraestructure.resources.geo.PolygonFunctions;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.GoogleMapSetup;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker.PointMarker;

/**
 * Created by leo on 26/08/16.
 */
public class DrawPolygon {
    // Wrapper class for google polygon
    private static float STROKE_WIDTH = 5.0f;
    private static int STROKE_COLOR = Color.rgb(0, 255, 255);
    private static int FILL_COLOR = Color.argb(128, 187, 184, 182);
    private boolean selected = false;
    private boolean isSelfIntersected = false;

    private OnDrawCompleteListener onDrawCompleteListener;
    private OnPointAddedToPolygon onPointAddedToPolygon;
    private GoogleMap.OnMarkerDragListener onMarkerDragListener;
    private boolean autoComplete;

    private String key;

    private Polygon polygon = null;
    private Polyline polyline = null;
    private double limitRaio;
    private LatLng limiteCentro;
    private List<List<Linha>> limitLinhas;
    private List<List<LatLng>> limitPoints;
    private List<LatLng> limitsCentroide;
    private List<List<LatLng>> proibidosPoints;
    private List<List<Linha>> proibidosLinhas;
    private List<LatLng> proibidosCentroide;
    private int positionPoligonoLimiteMaisProximo = -1;
    private int positionPoligonoProibidoMaisProximo = -1;
    private boolean estaNoLimite = false;
    private boolean estaNoProibido = false;

    private List<PointMarker> markers;
    private GoogleMap map;

    private Integer markerSelected = null;

    private BitmapDescriptor iconSelected;
    private BitmapDescriptor icon;
    private int zIndex = 10;

    private AreaChangeListener areaChangeListener;
    private Marker textMarker = null;
    private String textMarkerString = "";
    private String id;

    public interface AreaChangeListener {

        void updateArea(double acres);

    }
    public DrawPolygon(GoogleMap map, String key, String id) {
        this.markers = new ArrayList<PointMarker>();
        this.map = map;
        this.key = key;
        this.id = id;
        proibidosPoints = new ArrayList<>();
        proibidosCentroide = new ArrayList<>();
        proibidosLinhas = new ArrayList<>();
        limitPoints = new ArrayList<>();
        limitsCentroide = new ArrayList<>();
        limitLinhas = new ArrayList<>();
        iconSelected = BitmapDescriptorFactory.fromResource(R.drawable.selected_vertex);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.unselected_vertex);
    }

    public String getId() {
        return id;
    }

    // Custom functions
    public void select() {
        selected = true;
        selectLabel(true);
    }

    public void unselect() {
        selected = false;
        selectLabel(false);
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

    public void undo() {
        // Remove selected marker
        if (this.markers.size() != 0) {
            this.markers.get(markerSelected.intValue()).getMarker().remove();
            this.markers.remove(markerSelected.intValue());
            Integer newSelect = null;
            if (this.markers.size() != 0) {
                newSelect = markerSelected.intValue() - 1;
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

    public void complete() {
        // Remove all markers, and maybe change fill?
        for (int i = 0; i < this.markers.size(); i++) {
            this.markers.get(i).getMarker().remove();
        }
        this.markers.clear();
        markerSelected = null;
        if (onDrawCompleteListener != null)
            onDrawCompleteListener.onComplete();
    }

    public void edit() {
        if (polygon != null) {
            List<LatLng> points = polygon.getPoints();
            // Draw markers
            for (int i = 0; i < (points.size() - 1); i++) {
                String id = UUID.randomUUID().toString();
                PointMarker pointMarker = new PointMarker(map.addMarker(new MarkerOptions()
                        .position(points.get(i))
                        .icon(icon)
                        .draggable(true)
                        .anchor(0.5f, 0.5f)
                        .zIndex(zIndex)
                        .snippet(id)));
                pointMarker.setId(id);
                this.markers.add(pointMarker);
            }
            if (this.markers.size() > 0)
                selectMarker(this.markers.size() - 1);
        }
        updatePolyline();
    }

    public GoogleMap.OnMarkerDragListener getOnMarkerDragListener = new GoogleMap.OnMarkerDragListener() {
        private LatLng posicaoInicial;

        @Override
        public void onMarkerDragStart(Marker marker) {
            posicaoInicial = marker.getPosition();
            if (onMarkerDragListener != null)
                onMarkerDragListener.onMarkerDragStart(marker);
        }

        @Override
        public void onMarkerDrag(Marker marker) {
            updatePolyline();
            if (onMarkerDragListener != null)
                onMarkerDragListener.onMarkerDrag(marker);
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            verificarQualPoligonoMaisProximo(marker.getPosition());
            verificarQualLocalProibidoEstaAgora(marker.getPosition());
            if (!verificarSeEstaNoPoligono(marker.getPosition()) || verificarSeEstaEmLocalProibido(marker.getPosition())) {
                Logger.i("Após o drag, saiu de posição permitida\nModificada para: %s\nVoltará a ser: %s", marker.getPosition(), posicaoInicial);
                marker.setPosition(posicaoInicial);
            }
            Logger.i("Após o drag, não saiu de posição permitida\nModificada para: %s\nVoltará a ser: %s", marker.getPosition(), posicaoInicial);
            updatePolyline();
            if (onMarkerDragListener != null)
                onMarkerDragListener.onMarkerDragEnd(marker);
        }
    };

    public GoogleMap.OnMarkerClickListener getOnMarkerClickListener(final OnMapMarkerClick onMapMarkerClick) {

        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                logMarkerPosition(marker);
                for (int i = 0; i < markers.size(); i++) {
                    Marker m = markers.get(i).getMarker();
                    if (m.getId().equals(marker.getId())) {
                        if (i == 0 && markers.size() > 2 && !isSelfIntersected && autoComplete) {
                            updatePolygon();
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

    private void logMarkerPosition(Marker marker) {
        log(String.format("dragStart\t\tmarker lat: %f lon:%f", marker.getPosition().latitude, marker.getPosition().longitude));
        Projection projection = map.getProjection();
        Point p = projection.toScreenLocation(marker.getPosition());
        log(String.format("dragStart\t\tpoint x: %d y:%d", p.x, p.y));
    }

    public Boolean onMarkerClick(Marker marker) {
        Integer markerIndex = null;
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).equals(marker)) {
                markerIndex = i;
                break;
            }
        }
        if (markerIndex != null) {
            selectMarker(markerIndex);
            return true;
        } else {
            return false;
        }
    }

    public void delete() {
        this.remove();
        if (this.markers != null) {
            for (int i = 0; i < this.markers.size(); i++) {
                this.markers.get(i).getMarker().remove();
            }
            this.markers.clear();
        }
        if (this.polyline != null) this.polyline.remove();
        if (this.textMarker != null) this.textMarker.remove();
    }

    private void selectMarker(Integer markerIndex) {
        if (markerSelected != null) {
            Log.d("MarkerSelected:", Integer.toString(markerSelected));

            // Change icon of last selected marker
            Marker oldMarker = markers.get(markerSelected.intValue()).getMarker();
            this.markers.get(markerSelected.intValue()).getMarker().remove();
            this.markers.remove(markerSelected.intValue());
            String id = UUID.randomUUID().toString();
            PointMarker pointMarker = new PointMarker(map.addMarker(new MarkerOptions()
                    .position(oldMarker.getPosition())
                    .icon(icon)
                    .draggable(true)
                    .anchor(0.5f, 0.5f)
                    .snippet(id)));
            pointMarker.setId(id);
            this.markers.add(markerSelected.intValue(), pointMarker);
        }
        if (markerIndex != null || markerIndex < 0) {
            Log.d("MarkerIndex:", Integer.toString(markerIndex));
            // Change icon on new selected marker
            Marker oldMarker = this.markers.get(markerIndex.intValue()).getMarker();
            markers.get(markerIndex.intValue()).getMarker().remove();
            this.markers.remove(markerIndex.intValue());
            String id = UUID.randomUUID().toString();
            PointMarker pointMarker = new PointMarker(map.addMarker(new MarkerOptions()
                    .position(oldMarker.getPosition())
                    .icon(iconSelected)
                    .draggable(true)
                    .anchor(0.5f, 0.5f)
                    .snippet(id)));
            pointMarker.setId(id);
            this.markers.add(markerIndex.intValue(), pointMarker);

        }
        Log.d("MarkerSize:", Integer.toString(this.markers.size()));
        markerSelected = markerIndex;
    }

    public LatLng addPoint(LatLng point) {
        verificarQualPoligonoMaisProximo(point);
        verificarQualLocalProibidoEstaAgora(point);
        if (temLimiteParaDesenho() && !verificarSeEstaNoPoligono(point)) {
            point = moverParaPoligono(point);
        }
        if (temAreaProibidaParaDesenhar() && verificarSeEstaEmLocalProibido(point)) {
            point = sairDeLocalProibido(point);
        }
        if (limiteCentro !=null && SphericalUtil.computeDistanceBetween(point, limiteCentro)>=limitRaio){
            /*LatLng p3 = new LatLng(point.latitude, limiteCentro.longitude);
            double catetoOposto = SphericalUtil.computeDistanceBetween(point, p3);
            double hipotenusa = SphericalUtil.computeDistanceBetween(point, limiteCentro);
            double sen = catetoOposto/hipotenusa;
            double angulo = Math.toDegrees(Math.asin(sen));*/
            double heading = SphericalUtil.computeHeading(point, limiteCentro);
            LatLng point0 = SphericalUtil.computeOffset(limiteCentro, limitRaio*0.999, angulo(heading+180));
            /*LatLng point1 = SphericalUtil.computeOffset(limiteCentro, limitRaio*0.999, angulo(angulo));
            LatLng point2 = SphericalUtil.computeOffset(limiteCentro, limitRaio*0.999, angulo(angulo+90));
            LatLng point3 = SphericalUtil.computeOffset(limiteCentro, limitRaio*0.999, angulo(angulo+180));
            LatLng point4 = SphericalUtil.computeOffset(limiteCentro, limitRaio*0.999, angulo(angulo+270));
            log(String.format(MyDateUtils.getLocaleBR(), "P1: %s\nP2: %s\nP3: %s\nP4: %s\nCentro: %s\nHipotenusa: %f\nCateto oposto: %f\nSeno: %f\nÂngulo: %f\nHeading: %f", point1, point2, point3, point4, limiteCentro, hipotenusa, catetoOposto, sen, angulo, heading));
            LatLng pointA =  (SphericalUtil.computeDistanceBetween(point, point1)<=SphericalUtil.computeDistanceBetween(point, point2))?point1:point2;
            LatLng pointB =  (SphericalUtil.computeDistanceBetween(point, point3)<=SphericalUtil.computeDistanceBetween(point, point4))?point3:point4;
            point = (SphericalUtil.computeDistanceBetween(point, pointA)<=SphericalUtil.computeDistanceBetween(point, pointB))?pointA:pointB;*/
            point = point0;
        }
        boolean podeSerDesenhado = verificarSeEstaNoPoligono(point);
        if (podeSerDesenhado) {
            if (isSelfIntersected) {
                markers.get(markers.size() - 1).getMarker().remove();
                markers.remove(markers.size() - 1);
                markerSelected = markers.size() - 1;
                addMarker(point);
            } else {
                addMarker(point);
            }
            return point;
        }
        return null;
    }

    private double angulo(double angulo){
        if (angulo>180)
            return angulo(angulo-360);
        if (angulo<-180)
            return angulo(angulo+360);
        return angulo;
    }

    private LatLng moverParaPoligono(LatLng point) {
        LatLng ultimoMarker;
        if (estaNoLimite || markers.size() == 0)
            ultimoMarker = limitsCentroide.get(positionPoligonoLimiteMaisProximo);
        else
            ultimoMarker = markers.get(markers.size() - 1).getMarker().getPosition();
        Linha linha = new Linha("l1", point.latitude, point.longitude, ultimoMarker.latitude, ultimoMarker.longitude);
        estaNoLimite = false;
        for (Linha l : limitLinhas.get(positionPoligonoLimiteMaisProximo)) {
            if (l.intersect(linha)) {
                estaNoLimite = true;
                LatLng pontoDeInterseção = l.intersectionPoint(linha);
                Logger.i("Ponto de interseção:\n" + "Latitude: " + pontoDeInterseção.latitude + "\nLongitude: " + pontoDeInterseção.longitude);
                point = corrigirPontoDeIntersecao(pontoDeInterseção);
            }
        }
        return point;
    }

    private LatLng sairDeLocalProibido(LatLng point) {
        LatLng maisProximo = proibidosPoints.get(positionPoligonoProibidoMaisProximo).get(0);
        for (LatLng pontoProibido : proibidosPoints.get(positionPoligonoProibidoMaisProximo)) {
            if (SphericalUtil.computeDistanceBetween(pontoProibido, point) < SphericalUtil.computeDistanceBetween(maisProximo, point))
                maisProximo = pontoProibido;
        }
        point = corrigirPontoDeConflitoProibido(maisProximo);
        return point;
    }

    public LatLng editPoint(int position, LatLng newPoint) {
        //TODO verificar se este ponto está nos limites permitidos
        Marker marker = markers.get(position).getMarker();
        marker.setPosition(newPoint);
        updatePolyline();
        return newPoint;
    }

    private void addMarker(LatLng point) {
        int location = this.markers.size();
        if (markerSelected != null)
            location = markerSelected + 1;
        String id = UUID.randomUUID().toString();
        PointMarker marker = new PointMarker(map.addMarker(new MarkerOptions()
                .position(point)
                .icon(icon)
                .draggable(true)
                .anchor(0.5f, 0.5f)
                .snippet(id)));
        marker.setId(id);
        markers.add(location, marker);
        selectMarker(location);
        selected = true;
        updatePolyline();
        if (onPointAddedToPolygon != null)
            onPointAddedToPolygon.onAdd();

    }

    public void updatePolyline() {
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
            if (polygon != null) {
                // Remove polygon
                polygon.remove();
                polygon = null;
            }
            if (polyline != null) {
                // remove polyline
                polyline.remove();
                polyline = null;
            }
        } else {
            if (polyline == null) {
                // Create polyline
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.color(STROKE_COLOR);
                lineOptions.width(STROKE_WIDTH);
                lineOptions.zIndex(zIndex);
                for (int i = 0; i < arrayLoc.size(); i++) {
                    lineOptions.add(arrayLoc.get(i));
                }
                // Get back the mutable Polyline
                polyline = map.addPolyline(lineOptions);
            } else {
                // Update polyline
                // Get back the mutable Polyline
                polyline.setPoints(arrayLoc);
            }
            if (polygon != null) {
                // Remove polygon
                polygon.remove();
                polygon = null;
            }

        }
        //checkIntersection(arrayLoc);
    }

    public void updatePoints(List<LatLng> arrayLoc) {
        if (arrayLoc.size() <= 2) {
            if (polygon != null) {
                // Remove polygon
                polygon.remove();
                polygon = null;
            }
        } else {
            if (polygon == null) {
                // Create polygon
                if (polyline != null) {
                    // remove polyline
                    polyline.remove();
                    polyline = null;
                }
                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.fillColor(FILL_COLOR);
                polygonOptions.strokeWidth(STROKE_WIDTH);
                polygonOptions.strokeColor(STROKE_COLOR);
                polygonOptions.zIndex(zIndex);
                for (int i = 0; i < arrayLoc.size(); i++) {
                    polygonOptions.add(arrayLoc.get(i));
                }
                polygon = map.addPolygon(polygonOptions);
            } else {
                // Update polygon
                polygon.setPoints(arrayLoc);
            }
        }
    }


    public void updatePolygon() {
        List<LatLng> arrayLoc = new ArrayList<LatLng>();
        // Create polyline or update polyline
        if (polygon == null)
            for (int i = 0; i < this.markers.size(); i++) {
                Marker curMarker = this.markers.get(i).getMarker();
                LatLng curPos = curMarker.getPosition();
                arrayLoc.add(curPos);
            }
        else {
            arrayLoc = polygon.getPoints();
        }

        if (arrayLoc.size() <= 2) {
            if (polygon != null) {
                // Remove polygon
                polygon.remove();
                polygon = null;
            }
        } else {
            if (polygon == null) {
                // Create polygon
                if (polyline != null) {
                    // remove polyline
                    polyline.remove();
                    polyline = null;
                }
                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.fillColor(FILL_COLOR);
                polygonOptions.strokeWidth(STROKE_WIDTH);
                polygonOptions.strokeColor(STROKE_COLOR);
                polygonOptions.zIndex(zIndex);
                for (int i = 0; i < arrayLoc.size(); i++) {
                    polygonOptions.add(arrayLoc.get(i));
                }
                polygon = map.addPolygon(polygonOptions);
            } else {
                // Update polygon
                polygon.setPoints(arrayLoc);
            }
        }

        if (arrayLoc.size() < 3) {
            if (areaChangeListener != null)
                areaChangeListener.updateArea(0.0);
        } else {
            if (areaChangeListener != null)
                areaChangeListener.updateArea(SphericalUtil.computeArea(arrayLoc));
            Log.i(getClass().getSimpleName(), "Area: " + (int) (SphericalUtil.computeArea(arrayLoc) / 10000) + "ha");
        }

    }

    public void setLabel(String label) {
        setLabel(label, false);
    }

    public void setLabel(String label, Boolean selected) {
        if (this.textMarker != null) this.textMarker.remove();
        if (label == null) label = "No Name";
        this.textMarker = null;
        textMarkerString = label;

        if (this.polygon != null && this.polygon.getPoints().size() > 0) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (selected) {
                paint.setColor(Color.WHITE);
                paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
            } else {
                paint.setColor(Color.BLACK);
                paint.setShadowLayer(1f, 0f, 1f, Color.LTGRAY);
            }
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(20);
            paint.setStrokeWidth(12);

            Rect bounds = new Rect();
            paint.getTextBounds(label, 0, label.length(), bounds);

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = Bitmap.createBitmap(bounds.width() + 5, bounds.height(), conf); //TODO create blank new bitmap
            float x = 0;
            float y = -1.0f * bounds.top + (bitmap.getHeight() * 0.06f);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(label, x, y, paint);
            MarkerOptions options = new MarkerOptions();

            LatLng where;
            if (this.polygon.getPoints().size() == 1) {
                where = polygon.getPoints().get(0);
            } else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < polygon.getPoints().size(); i++) {
                    builder.include(polygon.getPoints().get(i));
                }
                // Have corners
                LatLngBounds boundingBox = builder.build();
                where = midPoint(boundingBox.northeast, boundingBox.southwest);
            }

            this.textMarker = map.addMarker(options.position(where).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        }
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    public void selectLabel(Boolean selected) {
        setLabel(textMarkerString, selected);
    }

    public void setTextMarker(Marker textMarker) {
        this.textMarker = textMarker;
    }

    public Marker getTextMarker() {
        return this.textMarker;
    }

    // Wrapper Functions
    public List<LatLng> getPoints() {
        log("Polygon == null: " + (polygon == null) + "\nmarkers size: " + markers.size());
        if (polygon != null) {
            return polygon.getPoints();
        } else {
            return null;
        }
    }

    public Polygon getPolygon() {
        log("Polygon == null: " + (polygon == null) + "\nmarkers size: " + markers.size());
        return polygon;
    }

    public List<PointMarker> getMarkers() {
        return markers;
    }

    public List<LatLng> getLatLngs() {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < this.markers.size(); i++) {
            points.add(this.markers.get(i).getMarker().getPosition());
        }
        if (this.markers.size() > 0) {
            points.add(this.markers.get(this.markers.size() - 1).getMarker().getPosition());
        }
        return points;
    }

    public List<List<LatLng>> getHoles() {
        if (polygon != null) {
            return polygon.getHoles();
        } else {
            return null;
        }
    }

    public void setPoints(List<LatLng> points) {
        if (polygon != null) polygon.setPoints(points);
    }

    public void remove() {
        if (polygon != null) polygon.remove();
        if (textMarker != null) textMarker.remove();
        if (polyline != null) polyline.remove();
        for (PointMarker pointMarker : markers)
            pointMarker.getMarker().remove();
        markers.clear();
        polygon = null;
        polyline = null;
        textMarker = null;
    }

    public void setVisible(boolean visible) {
        if (polygon != null) polygon.setVisible(visible);
        if (polyline != null) polyline.setVisible(visible);
    }

    public void setFillColor(int color) {
        if (polygon != null) polygon.setFillColor(color);
        FILL_COLOR = color;
    }

    public void setStrokeColor(int color) {
        if (polygon != null) polygon.setStrokeColor(color);
        if (polyline != null) polyline.setColor(color);
        STROKE_COLOR = color;
    }

    public void setIcons(Bitmap icon, Bitmap iconSelected) {
        this.icon = BitmapDescriptorFactory.fromBitmap(icon);
        this.iconSelected = BitmapDescriptorFactory.fromBitmap(iconSelected);
        if (markers != null)
            for (PointMarker marker : markers)
                marker.getMarker().setIcon(this.icon);
        if (markerSelected != null && markers!=null)
            markers.get(markerSelected).getMarker().setIcon(this.iconSelected);
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        if (polygon != null)
            polygon.setZIndex(zIndex);
        if (polyline != null)
            polyline.setZIndex(zIndex);
    }

    public void setAreaChangeListener(AreaChangeListener areaChangeListener) {
        this.areaChangeListener = areaChangeListener;
    }

    public void setLocaisProibidos(List<LatLng> limite) {
        List<Linha> linhas = new ArrayList<>();
        for (int i = 0; i < limite.size() - 1; i++) {
            LatLng p1 = limite.get(i);
            LatLng p2 = limite.get(i + 1);
            linhas.add(new Linha(String.valueOf(i), p1.latitude, p1.longitude, p2.latitude, p2.longitude));
        }
        if (limite.size() >= 3) {
            proibidosPoints.add(limite);
            proibidosLinhas.add(linhas);
            calcularCentroideDosProibidos(limite);
        }
    }

    public void setLimit(LatLng limitPonto, double limitRaio){
        this.limiteCentro = limitPonto;
        this.limitRaio = limitRaio;
    }

    public void setLimits(List<LatLng> limite) {
        List<Linha> linhas = new ArrayList<>();
        for (int i = 0; i < limite.size() - 1; i++) {
            LatLng p1 = limite.get(i);
            LatLng p2 = limite.get(i + 1);
            linhas.add(new Linha(String.valueOf(i), p1.latitude, p1.longitude, p2.latitude, p2.longitude));
        }
        if (limite.size() >= 3) {
            limitLinhas.add(linhas);
            limitPoints.add(limite);
            calcularCentroideDosLimites(limite);
        }
        if (markers.size() > 0)
            verificarQualPoligonoMaisProximo(markers.get(0).getMarker().getPosition());
        if (polyline != null && !polyline.getPoints().isEmpty())
            verificarQualPoligonoMaisProximo(polyline.getPoints().get(0));
    }

    public static LatLng midPoint(LatLng point1, LatLng point2) {
        double dLon = Math.toRadians(point2.longitude - point1.longitude);

        //convert to radians
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon1 = Math.toRadians(point1.longitude);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return (new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3)));
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isCompleted() {
        return markers.size() == 0 && polygon != null;
    }

    public boolean isSelfIntersected() {
        return isSelfIntersected;
    }

    private void checkIntersection(final List<LatLng> linePoints) {
        isSelfIntersected = false;
        if (linePoints.size() >= 4) {
            for (int i = 1; i < linePoints.size() - 1; i++) {
                Linha l1 = new Linha((i - 1) + "-" + i, linePoints.get(i - 1).latitude, linePoints.get(i - 1).longitude, linePoints.get(i).latitude, linePoints.get(i).longitude);
                for (int j = i + 2; j < linePoints.size(); j++) {
                    Linha l2 = new Linha((j - 1) + "-" + j, linePoints.get(j - 1).latitude, linePoints.get(j - 1).longitude, linePoints.get(j).latitude, linePoints.get(j).longitude);
                    if (l1.intersect(l2)) {
                        isSelfIntersected = true;
                        break;
                    }
                }
            }
        }
        if (polyline != null)
            polyline.setColor(isSelfIntersected ? GoogleMapSetup.COR_AREA_VERMELHA : STROKE_COLOR);
    }

    private class Linha {
        public String nome;
        public double x1;
        public double y1;
        public double x2;
        public double y2;

        public Linha(String nome, double x1, double y1, double x2, double y2) {
            this.nome = nome;
            this.x1 = /*Math.abs*/(x1);
            this.y1 = /*Math.abs*/(y1);
            this.x2 = /*Math.abs*/(x2);
            this.y2 = /*Math.abs*/(y2);
        }

        public boolean intersect(Linha l) {
            //log("-----Intersection");
            //log(String.format("\t\tLinha %s:x1: %f y1: %f x2: %f y2: %f", nome, x1, y1, x2, y2));
            //log(String.format("\t\tLinha %s:x1: %f y1: %f x2: %f y2: %f", l.nome, l.x1, l.y1, l.x2, l.y2));
            if (antiHorario(x1, y1, x2, y2, l.x1, l.y1) * antiHorario(x1, y1, x2, y2, l.x2, l.y2) > 0)
                return false;
            if (antiHorario(l.x1, l.y1, l.x2, l.y2, x1, y1) * antiHorario(l.x1, l.y1, l.x2, l.y2, x2, y2) > 0)
                return false;
            return true;
        }

        public LatLng intersectionPoint(Linha l) {
            double x3 = l.x1;
            double y3 = l.y1;
            double x4 = l.x2;
            double y4 = l.y2;
            double pLat = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
            double pLon = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
            return new LatLng(pLat, pLon);
        }

        private double antiHorario(double ax, double ay, double bx, double by, double cx, double cy) {
            double part1 = bx - ax;
            double part2 = cy - ay;
            double part3 = cx - ax;
            double part4 = by - ay;
            double antiHorario = (part1 * part2) - (part3 * part4);
            //log(String.format("\t\tparte1: %f parte2: %f parte3: %f parte4: %f\t\tAntiHorario: %f", part1, part2, part3, part4, antiHorario));
            return antiHorario;
        }
    }

    private boolean verificarSeEstaEmLocalProibido(LatLng position) {
        if (!temAreaProibidaParaDesenhar())
            return false;
        double x = position.longitude;
        double y = position.latitude;
        PointFunctions pointFunctions = new PointFunctions(x, y);
        for (int i = 0; i < proibidosPoints.size(); i++) {
            List<LatLng> poligonoProibido = proibidosPoints.get(i);
            List<PointFunctions> listPointFunctions = new ArrayList<PointFunctions>();
            for (LatLng point : poligonoProibido) {
                double a = point.longitude;
                double b = point.latitude;
                listPointFunctions.add(new PointFunctions(a, b));
            }
            PolygonFunctions polygonFunctions = PolygonFunctions.Builder().addVertexes(listPointFunctions).build();
            Logger.i("Está no poligono proibido: " + polygonFunctions.contains(pointFunctions));
            if (polygonFunctions.contains(pointFunctions)) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarSeEstaNoPoligono(LatLng position) {
        if (!temLimiteParaDesenho())
            return true;
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
        Logger.i("Está no poligono: " + polygonFunctions.contains(pointFunctions));
        return polygonFunctions.contains(pointFunctions);
    }

    private void verificarQualPoligonoMaisProximo(LatLng position) {
        if (!temLimiteParaDesenho())
            return;
        double menorDistancia = SphericalUtil.computeDistanceBetween(limitsCentroide.get(0), position);
        positionPoligonoLimiteMaisProximo = 0;
        for (int i = 0; i < limitsCentroide.size(); i++) {
            if (SphericalUtil.computeDistanceBetween(limitsCentroide.get(i), position) < menorDistancia)
                positionPoligonoLimiteMaisProximo = i;
        }
    }

    private void verificarQualLocalProibidoEstaAgora(LatLng position) {
        if (!temAreaProibidaParaDesenhar())
            return;
        double menorDistancia = SphericalUtil.computeDistanceBetween(proibidosCentroide.get(0), position);
        positionPoligonoProibidoMaisProximo = 0;
        for (int i = 0; i < proibidosCentroide.size(); i++) {
            if (SphericalUtil.computeDistanceBetween(proibidosCentroide.get(i), position) < menorDistancia)
                positionPoligonoProibidoMaisProximo = i;
        }
    }

    private boolean temLimiteParaDesenho() {
        return !limitPoints.isEmpty();
    }

    private boolean temAreaProibidaParaDesenhar() {
        return !(proibidosPoints.isEmpty());
    }

    private LatLng corrigirPontoForaDeCirculo(LatLng ponto, double raio) {
        LatLng try1 = new LatLng(ponto.latitude + 0.00001, ponto.longitude + 0.00001);
        LatLng try2 = new LatLng(ponto.latitude + 0.00001, ponto.longitude - 0.00001);
        LatLng try3 = new LatLng(ponto.latitude - 0.00001, ponto.longitude + 0.00001);
        LatLng try4 = new LatLng(ponto.latitude - 0.00001, ponto.longitude - 0.00001);
        boolean b1 = SphericalUtil.computeDistanceBetween(try1, ponto) < raio;
        if (b1) return try1;
        boolean b2 = SphericalUtil.computeDistanceBetween(try2, ponto) < raio;
        if (b2) return try2;
        boolean b3 = SphericalUtil.computeDistanceBetween(try3, ponto) < raio;
        if (b3) return try3;
        boolean b4 = SphericalUtil.computeDistanceBetween(try4, ponto) < raio;
        if (b4) return try4;
        Logger.e("Ponto de interseção incoerente");
        return ponto;
    }

    private LatLng corrigirPontoDeIntersecao(LatLng ponto) {
        LatLng try1 = new LatLng(ponto.latitude + 0.00001, ponto.longitude + 0.00001);
        LatLng try2 = new LatLng(ponto.latitude + 0.00001, ponto.longitude - 0.00001);
        LatLng try3 = new LatLng(ponto.latitude - 0.00001, ponto.longitude + 0.00001);
        LatLng try4 = new LatLng(ponto.latitude - 0.00001, ponto.longitude - 0.00001);
        boolean b1 = verificarSeEstaNoPoligono(try1);
        if (b1) return try1;
        boolean b2 = verificarSeEstaNoPoligono(try2);
        if (b2) return try2;
        boolean b3 = verificarSeEstaNoPoligono(try3);
        if (b3) return try3;
        boolean b4 = verificarSeEstaNoPoligono(try4);
        if (b4) return try4;
        Logger.e("Ponto de interseção incoerente");
        return ponto;
    }

    private LatLng corrigirPontoDeConflitoProibido(LatLng ponto) {
        LatLng try1 = new LatLng(ponto.latitude + 0.00001, ponto.longitude + 0.00001);
        LatLng try2 = new LatLng(ponto.latitude + 0.00001, ponto.longitude - 0.00001);
        LatLng try3 = new LatLng(ponto.latitude - 0.00001, ponto.longitude + 0.00001);
        LatLng try4 = new LatLng(ponto.latitude - 0.00001, ponto.longitude - 0.00001);
        boolean b1 = verificarSeEstaEmLocalProibido(try1);
        if (!b1) return try1;
        boolean b2 = verificarSeEstaEmLocalProibido(try2);
        if (!b2) return try2;
        boolean b3 = verificarSeEstaEmLocalProibido(try3);
        if (!b3) return try3;
        boolean b4 = verificarSeEstaEmLocalProibido(try4);
        if (!b4) return try4;
        Logger.e("Ponto de interseção incoerente");
        return ponto;
    }

    private void calcularCentroideDosLimites(List<LatLng> latLngList) {
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
        Logger.i("Total de centroides: " + limitsCentroide.size());
    }

    private void calcularCentroideDosProibidos(List<LatLng> latLngList) {
        double lat = 0;
        double lon = 0;
        int pointCount = latLngList.size();
        for (LatLng point : latLngList) {
            lat += point.latitude;
            lon += point.longitude;
        }
        lat = lat / pointCount;
        lon = lon / pointCount;
        proibidosCentroide.add(new LatLng(lat, lon));
        Logger.i("Total de centroides para polígonos proibidos: " + proibidosCentroide.size());
    }

    private void log(String log) {
        Log.i("TerrasGestor", log);
    }

    public void setOnDrawCompleteListener(OnDrawCompleteListener onDrawCompleteListener) {
        this.onDrawCompleteListener = onDrawCompleteListener;
    }

    public void setOnPointAddedToPolygon(OnPointAddedToPolygon onPointAddedToPolygon) {
        this.onPointAddedToPolygon = onPointAddedToPolygon;
    }

    public void setOnDragListener(GoogleMap.OnMarkerDragListener dragListener) {
        this.onMarkerDragListener = dragListener;
    }

    public interface OnDrawCompleteListener {
        void onComplete();
    }

    public interface OnPointAddedToPolygon {
        void onAdd();
    }
}
