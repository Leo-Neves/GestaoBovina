package br.agr.terras.corelibrary.infraestructure.resources.geo.map.listener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

/**
 * Created by leo on 04/08/16.
 */
public class CameraChangeListenerGoogle implements GoogleMap.OnCameraChangeListener {
    private GoogleMap.OnCameraChangeListener clusterListener;
    private GoogleMap.OnCameraChangeListener customListener;

    public CameraChangeListenerGoogle(GoogleMap.OnCameraChangeListener clusterListener){
        this.clusterListener = clusterListener;
    }

    public void setClusterListener(GoogleMap.OnCameraChangeListener clusterListener){
        this.clusterListener = clusterListener;
    }

    public void setCustomListener(GoogleMap.OnCameraChangeListener customListener){
        this.customListener = customListener;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (clusterListener!=null)
            clusterListener.onCameraChange(cameraPosition);
        if (customListener!=null)
            customListener.onCameraChange(cameraPosition);
    }
}
