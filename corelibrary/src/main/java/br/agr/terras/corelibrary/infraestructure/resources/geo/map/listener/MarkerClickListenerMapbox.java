package br.agr.terras.corelibrary.infraestructure.resources.geo.map.listener;

/**
 * Created by leo on 02/09/16.
 */
public class MarkerClickListenerMapbox /*implements MapboxMap.OnMarkerClickListener */{
    /*private MapboxMap.OnMarkerClickListener clusterListener;
    private MapboxMap.OnMarkerClickListener  drawListener;
    private MapboxMap.OnMarkerClickListener  customListener;

    public MarkerClickListenerMapbox(MapboxMap.OnMarkerClickListener clusterListener) {
        this.clusterListener = clusterListener;
    }

    public void setClusterListener(MapboxMap.OnMarkerClickListener  clusterListener){
        this.clusterListener = clusterListener;
    }

    public void setDrawListener(MapboxMap.OnMarkerClickListener drawListener) {
        this.drawListener = drawListener;
    }

    public void setCustomListener(MapboxMap.OnMarkerClickListener  customListener){
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
        return true;
    }*/
}
