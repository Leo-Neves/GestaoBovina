package br.agr.terras.corelibrary.infraestructure.resources.geo.map.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.CORE;
import br.agr.terras.corelibrary.infraestructure.asynctask.EasyTask;
import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.infraestructure.resources.layout.ScaleBar;
import br.agr.terras.materialdroid.SearchView;
import br.agr.terras.materialdroid.utils.searchview.sugestoes.SugestaoAdapter;

/**
 * Created by leo on 30/08/16.
 */
public class GoogleMapFragment extends MapFragment {
    private View rootView;
    private RelativeLayout containerFragment;
    private MapView mapView;
    private GoogleApiClient googleApiClient;
    private List<br.agr.terras.materialdroid.utils.searchview.sugestoes.model.Sugestao> suggestionList = new ArrayList<>();
    private List<PendingResult<PlaceBuffer>> places = new ArrayList<>();
    private boolean podeAtualizarLista = true;
    private String selectedPlace;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map_core, container, false);
            containerFragment = (RelativeLayout) rootView.findViewById(R.id.containerFragmentMapa);
            mapView = new MapView(getActivity());
            mapView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    GoogleMapFragment.this.googleMap = googleMap;
                    if (mapReadyListener != null && GoogleMapFragment.this.googleMap != null)
                        mapReadyListener.ready();
                }
            });
            criarScaleBar();
            containerFragment.addView(mapView);
            MapsInitializer.initialize(getActivity());
            mapView.onCreate(savedInstanceState);
            initComponents(rootView);
            configureSearchView();
        }
        return rootView;
    }


    @Override
    public Object getMapView() {
        return mapView;
    }

    private void criarScaleBar() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) getResources().getDisplayMetrics().xdpi + 20, 60);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_RIGHT);
        params.setMargins(0, 0, 110, 0);
        scaleBar = new ScaleBar(getActivity());
        scaleBar.setGoogleMap(googleMap);
        scaleBar.setLayoutParams(params);
        this.containerFragment.addView(scaleBar);
        scaleBar.setVisibility(View.VISIBLE);
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleBar.invalidate();
                Log.d(getClass().getSimpleName(), "Touch on map");
                return true;
            }
        });
    }

    private void configureSearchView() {
        searchView.setCloseSearchOnKeyboardDismiss(false);
        searchView.setOnQueryChangeListener(new SearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                refreshGooglePlaces(newQuery);
                Logger.i("onSearchTextChanged: " + newQuery);
            }
        });
        searchView.setOnBindSuggestionCallback(new SugestaoAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, br.agr.terras.materialdroid.utils.searchview.sugestoes.model.Sugestao item, int itemPosition) {
                Logger.i("onBindSuggestion");
            }
        });
        searchView.setOnSearchListener(new SearchView.OnSearchListener() {
            @Override
            public void onSugestaoClicada(br.agr.terras.materialdroid.utils.searchview.sugestoes.model.Sugestao searchSuggestion) {
                Sugestao sugestao = (Sugestao) searchSuggestion;
                CameraPosition newCamPos = new CameraPosition(sugestao.getLatLng(),
                        16,
                        googleMap.getCameraPosition().tilt, //use old tilt
                        googleMap.getCameraPosition().bearing); //use old bearing
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 1000, null);
            }

            @Override
            public void onSearchAction(String currentQuery) {

                Logger.i("onSearchAction");
            }
        });
        searchView.setOnFocusChangeListener(new SearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                Logger.i("onFocus");
            }

            @Override
            public void onFocusCleared() {
                Logger.i("onFocusCleared");
            }
        });
        searchView.setOnLeftMenuClickListener(new SearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                Logger.i("onMenuOpened");
            }

            @Override
            public void onMenuClosed() {
                Logger.i("onMenuClosed");
            }
        });
    }

    private void refreshGooglePlaces(final String search) {
        if (googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.disconnect();
        googleApiClient = new GoogleApiClient
                .Builder(CORE.getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        cancelaPesquisas();
                        Logger.i("StartingSearch...");
                        EasyTask easyTask = new EasyTask(CORE.getContext(), new EasyTask.Carregamento() {
                            @Override
                            public void duranteCarregamento() {
                                final String uuid = UUID.randomUUID().toString();
                                selectedPlace = uuid;
                                Logger.i("doInBackground");
                                LatLngBounds latLngBounds = new LatLngBounds.Builder().
                                        include(new LatLng(2, -80)).
                                        include(new LatLng(-35, -50)).build();
                                PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, search, latLngBounds, null);
                                AutocompletePredictionBuffer autocompletePredictions = result.await(20, TimeUnit.SECONDS);
                                Logger.i("trying to get result ok");
                                Status status = autocompletePredictions.getStatus();
                                if (status.isSuccess()) {
                                    suggestionList.clear();
                                    Logger.i("Deu certo sim a sincronização de " + search);
                                    for (final AutocompletePrediction prediction : autocompletePredictions) {
                                        Logger.w("Result de \"" + search + "\": " + prediction.getFullText(new StyleSpan(Typeface.NORMAL)));
                                        PendingResult<PlaceBuffer> pendingResult = Places.GeoDataApi.getPlaceById(googleApiClient, prediction.getPlaceId());
                                        pendingResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                                            @Override
                                            public void onResult(PlaceBuffer places) {
                                                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                    final Place place = places.get(0);
                                                    if (uuid.equals(selectedPlace)) {
                                                        Log.i(getClass().getSimpleName(), "Place found: " + place.getName());
                                                        Sugestao sugestao = new Sugestao(place.getLatLng(), place.getName().toString());
                                                        suggestionList.add(sugestao);
                                                        atualizarLista();
                                                    }
                                                } else {
                                                    Log.e(getClass().getSimpleName(), "Place not found");
                                                }
                                                places.release();
                                                places.close();
                                            }
                                        });
                                        places.add(pendingResult);

                                    }
                                    autocompletePredictions.close();
                                } else {
                                    Log.e(getClass().getSimpleName(), "não deu certo sincronização de " + search + "\n" + status.getStatusMessage());
                                    autocompletePredictions.release();
                                    autocompletePredictions.close();

                                }
                            }
                        });
                        easyTask.iniciar();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Logger.e("onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Logger.e("onConnectionFailed " + connectionResult.toString());
                    }
                })
                .build();
        googleApiClient.connect();

    }

    private void cancelaPesquisas() {
        for (PendingResult pendingResult : places) {
            pendingResult.cancel();
        }
        places.clear();
    }

    private void atualizarLista() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchView.swapSuggestions(suggestionList);
            }
        });
        podeAtualizarLista = true;
    }

    private class Sugestao extends br.agr.terras.materialdroid.utils.searchview.sugestoes.model.Sugestao {
        private LatLng latLng;

        public Sugestao(LatLng latLng, String description) {
            this.latLng = latLng;
            setTitulo(description);
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

    }

    @Override
    public GoogleMap getGoogleMap() {
        Log.i(getClass().getSimpleName(), "googleMap: " + googleMap);
        return googleMap;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null)
            mapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }
}
