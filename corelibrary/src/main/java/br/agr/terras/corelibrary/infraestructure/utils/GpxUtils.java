package br.agr.terras.corelibrary.infraestructure.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.shapefile.GeoErroListener;
import br.agr.terras.corelibrary.infraestructure.shapefile.GpxParser;

/**
 * Created by leo on 13/12/16.
 */

public class GpxUtils {
    private GpxParser gpxParser;
    private boolean error;

    public GpxUtils(File file, GeoErroListener errorListener) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            gpxParser = new GpxParser(inputStream);
            Log.i(getClass().getSimpleName(), "GpxPoints detected: "+gpxParser.getPoints().size());
            if (gpxParser.getPoints().size()>1000){
                errorListener.erroArquivoMuitoGrande();
                error = true;
            }
        } catch (XmlPullParserException e) {
            error = true;
            e.printStackTrace();
            errorListener.erroInterpretarArquivo();
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
            errorListener.erroEncontrarArquivo();
        }
    }

    public boolean isError(){
        return error;
    }

    public List<LatLng> getLatLngs() {
        final List<LatLng> pontos = new ArrayList<>();
        for (GpxParser.TrkPt point : gpxParser.getPoints())
            pontos.add(new LatLng(point.getLat(), point.getLon()));
        return pontos;
    }

    public List<GpxParser.TrkPt> getPontos() {
        return gpxParser.getPoints();
    }
}
