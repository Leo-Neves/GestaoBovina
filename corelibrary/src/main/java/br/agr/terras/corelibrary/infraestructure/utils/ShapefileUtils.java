package br.agr.terras.corelibrary.infraestructure.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.header.ShapeFileHeader;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.infraestructure.shapefile.GeoErroListener;
import br.agr.terras.corelibrary.infraestructure.shapefile.MultipatchManager;
import br.agr.terras.corelibrary.infraestructure.shapefile.MultipointManager;
import br.agr.terras.corelibrary.infraestructure.shapefile.PointManager;
import br.agr.terras.corelibrary.infraestructure.shapefile.PolygonManager;
import br.agr.terras.corelibrary.infraestructure.shapefile.PolylineManager;
import br.agr.terras.materialdroid.utils.storagechooser.utils.FileUtil;

/**
 * Created by leo on 12/12/16.
 */

public class ShapefileUtils {
    private File file;
    private ShapeFileReader reader;
    private GeoErroListener errorListener;
    private List<List<LatLng>> latLngs;

    private PointManager pointManager;
    private MultipointManager multipointManager;
    private PolygonManager polygonManager;
    private PolylineManager polylineManager;
    private MultipatchManager multipatchManager;
    public static char hemisferio = 'S';
    public static int zone = 22;

    public ShapefileUtils(File file, GeoErroListener errorListener) {
        this.errorListener = errorListener;
        init();
        checarTipoDeArquivo(file);
    }

    private void checarTipoDeArquivo(File file){
        try {
            ZipUtils.extractFolder(file.getPath());
            String rootPath = file.getPath().substring(0, file.getPath().length() - 4);
            Log.i(getClass().getSimpleName(), "rootPath: " + rootPath);
            File root = new File(rootPath);
            for (File arquivo : root.listFiles()) {
                Log.d(getClass().getSimpleName(), "Arquivo em root: " + arquivo.getName());
                if (!arquivo.isDirectory() && arquivo.getName().substring(arquivo.getName().lastIndexOf(".") + 1).equals("shp")) {
                    String subPath = root.getPath() + File.separator + arquivo.getName();
                    Logger.d("Nome de um subarquivo: " + subPath);
                    this.file = arquivo;
                    lerArquivo();
                }
            }
            FileUtil.deleteDir(root);
        }catch (ZipException z){
            errorListener.erroDescompactarArquivo();
        }catch (IOException e){
            e.printStackTrace();
            this.file = file;
            lerArquivo();
        }
    }

    private void lerArquivo(){
        try {
            carregarShape();
        } catch (IOException e) {
            e.printStackTrace();
            errorListener.erroEncontrarArquivo();
        } catch (InvalidShapeFileException e) {
            e.printStackTrace();
            errorListener.erroInterpretarArquivo();
        }
    }

    private void carregarShape() throws IOException, InvalidShapeFileException {
        FileInputStream inputStream = new FileInputStream(file);
        ValidationPreferences prefs = new ValidationPreferences();
        prefs.setMaxNumberOfPointsPerShape(25000);
        reader = new ShapeFileReader(inputStream, prefs);
        AbstractShape shape;
        Log.i(getClass().getSimpleName(), String.format(MyDateUtils.getLocaleBR(), "Header do shape %s", file.getPath()));
        ShapeFileHeader header = reader.getHeader();
        Logger.i("FileCode: " + header.getFileCode());
        Logger.i("Shapetype.getID(): " + header.getShapeType().getId());
        int i = 0;
        while ((shape = reader.next()) != null) {
            Log.d(getClass().getSimpleName(), "Shape " + i++ + "\nContentLength: " + shape.getHeader().getContentLength() + "\nRecord number: " + shape.getHeader().getRecordNumber());
            switch (shape.getShapeType()) {
                case POINT:
                    pointManager.addPoint(shape);
                    break;
                case POLYLINE:
                    polylineManager.addPolyline(shape);
                    break;
                case POLYGON:
                    polygonManager.addPolygon(shape);
                    break;
                case MULTIPOINT:
                    multipointManager.addMultipoint(shape);
                    break;
                case POINT_Z:
                    pointManager.addPointZ(shape);
                    break;
                case POLYLINE_Z:
                    polylineManager.addPolylineZ(shape);
                    break;
                case POLYGON_Z:
                    polygonManager.addPolygonZ(shape);
                    break;
                case MULTIPOINT_Z:
                    multipointManager.addMultipointZ(shape);
                    break;
                case POINT_M:
                    pointManager.addPointM(shape);
                    break;
                case POLYLINE_M:
                    polylineManager.addPolylineM(shape);
                    break;
                case POLYGON_M:
                    polygonManager.addPolygonM(shape);
                    break;
                case MULTIPOINT_M:
                    multipointManager.addMultipointM(shape);
                    break;
                case MULTIPATCH:
                    multipatchManager.addMultipatch(shape);
                    break;
            }
        }
        inputStream.close();
    }

    private void init() {
        pointManager = new PointManager();
        polygonManager = new PolygonManager();
        multipointManager = new MultipointManager();
        polylineManager = new PolylineManager();
        multipatchManager = new MultipatchManager();
    }

    public List<List<LatLng>> getPoligonos() {
        return polygonManager.getPolygons();
    }

    public List<List<LatLng>> getLinhas() {
        return polylineManager.getPolylines();
    }

    public List<List<LatLng>> getPontos() {
        List<List<LatLng>> points = multipointManager.getMultipoints();
        if (pointManager.getPoints().size() > 0)
            points.add(pointManager.getPoints());
        return points;
    }



}
