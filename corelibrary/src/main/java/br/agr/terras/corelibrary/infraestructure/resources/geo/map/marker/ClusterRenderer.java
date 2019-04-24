package br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 04/08/16.
 */
public class ClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private HashMap<String, ClusterRendererListener> hashMapRenderers;

    public ClusterRenderer(GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(CORE.getContext(), map, clusterManager);
        hashMapRenderers = new HashMap<>();
    }

    public void addRenderer(String key, ClusterRendererListener renderer){
        hashMapRenderers.put(key, renderer);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        if (hashMapRenderers.get(item.getKey())!=null){
            hashMapRenderers.get(item.getKey()).renderSingle(item, markerOptions, new DefaultRenderer(item, markerOptions));
        }else{
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterMarker> cluster, MarkerOptions markerOptions) {
        ClusterMarker item = cluster.getItems().toArray(new ClusterMarker[cluster.getSize()])[0];
        List<ClusterMarker> clusterMarkers = new ArrayList<>(cluster.getItems());
        if (hashMapRenderers.get(item.getKey())!=null){
            hashMapRenderers.get(item.getKey()).renderMultiple(clusterMarkers, markerOptions, new DefaultRenderer(cluster, markerOptions));
        }else{
            super.onBeforeClusterRendered(cluster, markerOptions);
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return cluster.getSize() > 1;
    }

    public class DefaultRenderer{
        private ClusterMarker clusterMarker;
        private MarkerOptions markerOptions;
        private Cluster<ClusterMarker> clusterMarkerCluster;
        public DefaultRenderer(ClusterMarker clusterMarker, MarkerOptions markerOptions){
            this.clusterMarker = clusterMarker;
            this.markerOptions = markerOptions;
        }
        public DefaultRenderer(Cluster<ClusterMarker> clusterMarkerCluster, MarkerOptions markerOptions){
            this.clusterMarkerCluster = clusterMarkerCluster;
            this.markerOptions = markerOptions;
        }
        public void defaultRendererSingle(){
            ClusterRenderer.super.onBeforeClusterItemRendered(clusterMarker, markerOptions);
        }

        public void defaultRendererMultiple(){
            ClusterRenderer.super.onBeforeClusterRendered(clusterMarkerCluster, markerOptions);
        }
    }
}
