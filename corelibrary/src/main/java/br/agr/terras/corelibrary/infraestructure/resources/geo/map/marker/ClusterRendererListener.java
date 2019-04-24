package br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by leo on 21/02/17.
 */

public interface ClusterRendererListener {
    void renderSingle(ClusterMarker clusterMarker, MarkerOptions markerOptions, ClusterRenderer.DefaultRenderer defaultRenderer);
    void renderMultiple(List<ClusterMarker> clusterMarkers, MarkerOptions markerOptions, ClusterRenderer.DefaultRenderer defaultRenderer);
}
