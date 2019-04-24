package br.agr.terras.corelibrary.infraestructure.resources.geo.map.fragment;

import android.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.FactoryCheckBoxCamadas;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.MapComponent;
import br.agr.terras.corelibrary.infraestructure.resources.layout.ScaleBar;
import br.agr.terras.materialdroid.FloatingActionButton;
import br.agr.terras.materialdroid.SearchView;

/**
 * Created by edson on 01/09/16.
 */
public class MapFragment extends Fragment{
    protected SearchView searchView;
    protected ScaleBar scaleBar;
    protected LinearLayout contentCamadas;
    protected FloatingActionButton fabEditarArea;
    protected FloatingActionButton fabDesfazer;
    protected FloatingActionButton fabTutorial;
    protected FloatingActionButton fabGPS;
    protected FloatingActionButton fabCamadas;
    protected FloatingActionButton fabAmpliar;

    public View.OnClickListener clickListenerEditar;
    public View.OnClickListener clickListenerDesfazer;
    public View.OnClickListener clickListenerTutorial;
    public View.OnClickListener clickListenerGPS;
    public View.OnClickListener clickListenerCamadas;
    public View.OnClickListener clickListenerAmpliar;

    protected MapComponent.MapReadyListener mapReadyListener;
    private View view;
    protected GoogleMap googleMap;

    public void initComponents(View view){
        this.view = view;
        contentCamadas = (LinearLayout) view.findViewById(R.id.camadasMapaParent);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        fabEditarArea = (FloatingActionButton) view.findViewById(R.id.fabEditar);
        fabDesfazer = (FloatingActionButton) view.findViewById(R.id.fabDesfazer);
        fabTutorial = (FloatingActionButton) view.findViewById(R.id.fabTutorial);
        fabGPS = (FloatingActionButton) view.findViewById(R.id.fabGPS);
        fabCamadas = (FloatingActionButton) view.findViewById(R.id.fabCamadas);
        fabAmpliar = (FloatingActionButton) view.findViewById(R.id.fabAmpliar);
        searchView.setVisibility(View.GONE);
        fabEditarArea.setVisibility(View.GONE);
        fabGPS.setVisibility(View.GONE);
        fabDesfazer.setVisibility(View.GONE);
        fabTutorial.setVisibility(View.GONE);
        fabCamadas.setVisibility(View.GONE);
        fabAmpliar.setVisibility(View.GONE);

        fabEditarArea.setOnClickListener(clickListenerEditar);
        fabDesfazer.setOnClickListener(clickListenerDesfazer);
        fabTutorial.setOnClickListener(clickListenerTutorial);
        fabGPS.setOnClickListener(clickListenerGPS);
        fabCamadas.setOnClickListener(clickListenerCamadas);
        fabAmpliar.setOnClickListener(clickListenerAmpliar);

    }

    public void showFabEditarArea(){
        fabEditarArea.setVisibility(View.VISIBLE);
    }

    public void showFabDesfazer(){
        fabDesfazer.setVisibility(View.VISIBLE);
    }

    public void showFabTutorial(){
        fabTutorial.setVisibility(View.VISIBLE);
    }

    public void showSearchView(){
        searchView.setVisibility(View.VISIBLE);
    }

    public void showFabCamadas(){ fabCamadas.setVisibility(View.VISIBLE);}

    public void showFabAmpliar(){ fabAmpliar.setVisibility(View.VISIBLE);}

    public void hideFabEditarArea(){
        fabEditarArea.setVisibility(View.GONE);
    }

    public void hideFabDesfazer(){
        fabDesfazer.setVisibility(View.GONE);
    }

    public void hideFabTutorial(){
        fabTutorial.setVisibility(View.GONE);
    }

    public void hideSearchView(){
        searchView.setVisibility(View.GONE);
    }

    public void hideFabCamadas(){ fabCamadas.setVisibility(View.GONE);}

    public void hideFabAmpliar(){ fabAmpliar.setVisibility(View.GONE);}

    public boolean isCamadasVisible(){
        return contentCamadas.getVisibility()==View.VISIBLE;
    }

    public FactoryCheckBoxCamadas getFactoryCheckBoxCamadas(){
        return new FactoryCheckBoxCamadas(contentCamadas);
    }

    public Object getMapView(){
        return null;
    }

    public void setMapReadyListener(MapComponent.MapReadyListener mapReadyListener){
        this.mapReadyListener = mapReadyListener;
    }


    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void showHideCamadas() {
        contentCamadas.setVisibility(contentCamadas.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
    }

    public void showScaleBar() {
        scaleBar.setVisibility(View.VISIBLE);
    }

    public void hideScaleBar(){
        scaleBar.setVisibility(View.GONE);
    }
}
