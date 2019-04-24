package br.agr.terras.corelibrary.infraestructure.resources.geo.map;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.fragment.GoogleMapFragment;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.fragment.MapBoxFragment;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.fragment.MapFragment;
import br.agr.terras.corelibrary.infraestructure.resources.geo.map.geometry.DrawPolygon;

/**
 * Created by leo on 30/08/16.
 */
public class MapComponent extends FrameLayout {
    private static final int TYPE_GOOGLE = 0;
    private static final int TYPE_MAPLY = 1;
    private static final int TYPE_MAPBOX = 2;
    private MapFragment mapFragment;
    private int type;
    private MapSetup mapSetup;
    private DrawPolygon.OnDrawCompleteListener onDrawCompleteListener;
    private DrawPolygon.OnDrawCompleteListener customOnDrawCompleteListener;
    private OnClickListener customUndoClickListener;
    private OnClickListener customEditClickListener;
    private OnClickListener customLocationClickListener;
    private OnClickListener customTourClickListener;
    private OnClickListener customCamadasClickListener;
    private OnClickListener customAmpliarClickListener;

    public MapComponent(Context context) {
        super(context);
        type = TYPE_GOOGLE;
        init(context);
    }

    public MapComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MapComponent);
        type = typedArray.getInt(R.styleable.MapComponent_mc_type, TYPE_GOOGLE);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        mapFragment = type == TYPE_GOOGLE ? new GoogleMapFragment() : new MapBoxFragment();
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getFragmentManager().beginTransaction().replace(getId(), mapFragment).commit();
            configureButtons();
        } else if (context instanceof Activity) {
            ((Activity) context).getFragmentManager().beginTransaction().replace(getId(), mapFragment).commit();
            configureButtons();
        }
    }

    private void configureButtons() {
        mapFragment.clickListenerEditar = new OnClickListener() {
            @Override
            public void onClick(View v) {
                //mapSetup.editPolygon();
                mapFragment.hideFabEditarArea();
                mapFragment.showFabDesfazer();
                if (customEditClickListener != null) customEditClickListener.onClick(v);
            }
        };
        mapFragment.clickListenerTutorial = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customTourClickListener != null) customTourClickListener.onClick(v);
            }
        };
        mapFragment.clickListenerDesfazer = new OnClickListener() {
            @Override
            public void onClick(View v) {
                //mapSetup.undoLastAddToPolygon();
                if (customUndoClickListener != null) customUndoClickListener.onClick(v);
            }
        };

        mapFragment.clickListenerGPS = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSetup.centerMeMarker();
                if (customLocationClickListener != null) customLocationClickListener.onClick(v);
            }
        };
        mapFragment.clickListenerCamadas = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.showHideCamadas();
                if (customCamadasClickListener != null) customCamadasClickListener.onClick(v);
            }
        };
        mapFragment.clickListenerAmpliar = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customAmpliarClickListener != null) customAmpliarClickListener.onClick(v);
            }
        };

        onDrawCompleteListener = new DrawPolygon.OnDrawCompleteListener() {
            @Override
            public void onComplete() {
                mapFragment.showFabEditarArea();
                mapFragment.hideFabDesfazer();
                if (customOnDrawCompleteListener != null)
                    customOnDrawCompleteListener.onComplete();
            }
        };

    }

    public MapSetup getMapSetup() {
        if (mapSetup == null)
            mapSetup = type == TYPE_GOOGLE ? new GoogleMapSetup(getContext(), ((GoogleMapFragment)mapFragment).getGoogleMap()) :/*new MapBoxSetup((MapboxMap)mapFragment.getMapView())*/null;
        mapSetup.setOnDrawCompleteListener(onDrawCompleteListener);
        mapSetup.setFactoryCheckBoxCamadas(mapFragment.getFactoryCheckBoxCamadas());
        return mapSetup;
    }

    public Object getMapView() {
        return mapFragment.getMapView();
    }

    public void setMapReadyListener(MapReadyListener mapReadyListener) {
        mapFragment.setMapReadyListener(mapReadyListener);
    }

    public void setLocationClickListener(OnClickListener onClickListener) {
        customLocationClickListener = onClickListener;
    }

    public void setUndoClickListener(OnClickListener onClickListener) {
        customUndoClickListener = onClickListener;
    }

    public void setEditClickListener(OnClickListener onClickListener) {
        customEditClickListener = onClickListener;
    }

    public void setTourClickListener(OnClickListener onClickListener) {
        customTourClickListener = onClickListener;
    }

    public void setCamadasClickListener(OnClickListener onClickListener) {
        customCamadasClickListener = onClickListener;
    }

    public void setAmpliarClickListener(OnClickListener onClickListener) {
        customAmpliarClickListener = onClickListener;
    }

    public void setOnDrawCompleteListener(DrawPolygon.OnDrawCompleteListener onDrawCompleteListener) {
        customOnDrawCompleteListener = onDrawCompleteListener;
    }

    public void setSearchViewVisible(boolean visible) {
        if (visible)
            mapFragment.showSearchView();
        else
            mapFragment.hideSearchView();
    }

    public void setCamadasVisibile(boolean visibile) {
        if (visibile)
            mapFragment.showFabCamadas();
        else
            mapFragment.hideFabCamadas();
    }

    public void setAmpliarVisibility(boolean visible) {
        if (visible)
            mapFragment.showFabAmpliar();
        else
            mapFragment.hideFabAmpliar();
    }

    public void setEditarVisible(boolean visible){
        if (visible)
            mapFragment.showFabEditarArea();
        else
            mapFragment.hideFabEditarArea();
    }

    public boolean isCamadasVisiveis(){
        return mapFragment.isCamadasVisible();
    }

    public void setScaleBarVisible(boolean visible) {
        if (visible)
            mapFragment.showScaleBar();
        else
            mapFragment.hideScaleBar();
    }

    public interface MapReadyListener {
        void ready();
    }

}
