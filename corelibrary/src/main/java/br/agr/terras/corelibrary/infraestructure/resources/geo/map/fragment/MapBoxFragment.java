package br.agr.terras.corelibrary.infraestructure.resources.geo.map.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import br.agr.terras.corelibrary.R;

/**
 * Created by leo on 01/09/16.
 */
public class MapBoxFragment extends MapFragment {
    private View rootView;
    private RelativeLayout containerFragment;
    /*private MapView mapView;
    private MapboxMap mapboxMap;*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_core, container, false);
        containerFragment = (RelativeLayout) rootView.findViewById(R.id.containerFragmentMapa);
        /*MapboxMapOptions options = new MapboxMapOptions()
                .styleUrl("mapbox://styles/mapbox/dark-v9")
                .camera(new CameraPosition.Builder()
                        .target(new LatLng(-1, -48))
                        .zoom(8)
                        .build());
        mapView = new MapView(getActivity(), options);
        mapView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        containerFragment.addView(mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                MapBoxFragment.this.mapboxMap = mapboxMap;
            }
        });
        if (mapReadyListener!=null)
            mapReadyListener.ready();
        initComponents(rootView);*/
        return rootView;
    }


    @Override
    public Object getMapView(){
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
