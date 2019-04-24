package br.agr.terras.corelibrary.infraestructure.resources.geo.map.listener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by leo on 04/08/16.
 */
public class MarkerClickListenerGoogle implements GoogleMap.OnMarkerClickListener {
    private GoogleMap.OnMarkerClickListener  clusterListener;
    private GoogleMap.OnMarkerClickListener  drawListener;
    private GoogleMap.OnMarkerClickListener  customListener;
    private GoogleMap.OnMarkerClickListener lineListener;

    public MarkerClickListenerGoogle(GoogleMap.OnMarkerClickListener clusterListener) {
        this.clusterListener = clusterListener;
    }

    public void setClusterListener(GoogleMap.OnMarkerClickListener  clusterListener){
        this.clusterListener = clusterListener;
    }

    public void setDrawListener(GoogleMap.OnMarkerClickListener drawListener) {
        this.drawListener = drawListener;
    }

    public void setLineListener(GoogleMap.OnMarkerClickListener lineListener) {
        this.lineListener = lineListener;
    }

    public void setCustomListener(GoogleMap.OnMarkerClickListener  customListener){
        this.customListener = customListener;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (clusterListener!=null)
            clusterListener.onMarkerClick(marker);
        if (drawListener!=null)
            drawListener.onMarkerClick(marker);
        if (customListener!=null)
            customListener.onMarkerClick(marker);
        if (lineListener!=null)
            lineListener.onMarkerClick(marker);
        return true;
    }
}
