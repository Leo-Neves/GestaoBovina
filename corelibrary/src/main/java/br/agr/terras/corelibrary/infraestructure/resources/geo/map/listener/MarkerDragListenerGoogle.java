package br.agr.terras.corelibrary.infraestructure.resources.geo.map.listener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by leo on 29/08/16.
 */
public class MarkerDragListenerGoogle implements GoogleMap.OnMarkerDragListener {
    private GoogleMap.OnMarkerDragListener drawDragListener;
    private GoogleMap.OnMarkerDragListener customDragListener;
    private GoogleMap.OnMarkerDragListener lineDragListener;

    public void setDrawDragListener(GoogleMap.OnMarkerDragListener drawDragListener) {
        this.drawDragListener = drawDragListener;
    }

    public void setCustomDragListener(GoogleMap.OnMarkerDragListener customDragListener) {
        this.customDragListener = customDragListener;
    }

    public void setLineDragListener(GoogleMap.OnMarkerDragListener lineDragListener) {
        this.lineDragListener = lineDragListener;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        if (drawDragListener!=null)
            drawDragListener.onMarkerDragStart(marker);
        if (customDragListener!=null)
            customDragListener.onMarkerDragStart(marker);
        if (lineDragListener!=null)
            lineDragListener.onMarkerDragStart(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if (drawDragListener!=null)
            drawDragListener.onMarkerDrag(marker);
        if (customDragListener!=null)
            customDragListener.onMarkerDrag(marker);
        if (lineDragListener!=null)
            lineDragListener.onMarkerDrag(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (drawDragListener!=null)
            drawDragListener.onMarkerDragEnd(marker);
        if (customDragListener!=null)
            customDragListener.onMarkerDragEnd(marker);
        if (lineDragListener!=null)
            lineDragListener.onMarkerDragEnd(marker);
    }
}
