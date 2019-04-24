package br.agr.terras.corelibrary.infraestructure.utils;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.infraestructure.CORE;
import br.agr.terras.corelibrary.infraestructure.kml.KmlContainer;
import br.agr.terras.corelibrary.infraestructure.kml.KmlLayer;
import br.agr.terras.corelibrary.infraestructure.kml.KmlLineString;
import br.agr.terras.corelibrary.infraestructure.kml.KmlPlacemark;
import br.agr.terras.corelibrary.infraestructure.kml.KmlPoint;
import br.agr.terras.corelibrary.infraestructure.kml.KmlPolygon;
import br.agr.terras.corelibrary.infraestructure.shapefile.GeoErroListener;

/**
 * Created by leo on 13/12/16.
 */

public class KmlUtils {
    private File file;
    private KmlLayer kmlLayer;

    public KmlUtils(File file, GeoErroListener errorListener) {
        this.file = file;
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            kmlLayer = new KmlLayer(null, inputStream, CORE.getContext());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            errorListener.erroInterpretarArquivo();
        } catch (IOException e) {
            e.printStackTrace();
            errorListener.erroEncontrarArquivo();
        }
    }

    public List<List<LatLng>> getPoligonos() {
        List<List<LatLng>> poligonos = new ArrayList<>();
        for (KmlContainer container : kmlLayer.getContainers())
            poligonos.addAll(KmlPolygon.containerToLatLngsList(container));
        for (KmlPlacemark placemark : kmlLayer.getPlacemarks())
            poligonos.add(KmlPolygon.placemarkToLatLngs(placemark));
        return poligonos;
    }

    public List<List<LatLng>> getLinhas() {
        List<List<LatLng>> linhas = new ArrayList<>();
        for (KmlContainer container : kmlLayer.getContainers())
            linhas.addAll(KmlLineString.containerToLatLngsList(container));
        for (KmlPlacemark placemark : kmlLayer.getPlacemarks())
            linhas.add(KmlLineString.placemarkToLatLngs(placemark));
        return linhas;
    }

    public List<LatLng> getPontos() {
        final List<LatLng> pontos = new ArrayList<>();
        for (KmlContainer container : kmlLayer.getContainers())
            pontos.addAll(KmlPoint.containerToLatLngs(container));
        for (KmlPlacemark placemark : kmlLayer.getPlacemarks())
            pontos.add(KmlPoint.placemarkToLatLng(placemark));
        return pontos;
    }
}
