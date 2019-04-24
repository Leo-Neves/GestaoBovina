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
import br.agr.terras.corelibrary.infraestructure.shapefile.TcxParser;

/**
 * Created by leo on 22/06/16.
 */

public class TcxUtils {
    private TcxParser tcxParser;
    private boolean error;

    public TcxUtils(File file, GeoErroListener errorListener) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            tcxParser = new TcxParser(inputStream);
            Log.i(getClass().getSimpleName(), "TcxPoints detected: "+ tcxParser.getPoints().size());
            if (tcxParser.getPoints().size()>1000){
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
        for (TcxParser.Trackpoint point : tcxParser.getPoints())
            pontos.add(new LatLng(point.getLat(), point.getLon()));
        return pontos;
    }

    public List<TcxParser.Trackpoint> getPontos() {
        return tcxParser.getPoints();
    }
}
