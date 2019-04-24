package br.agr.terras.corelibrary.infraestructure.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;

import java.io.File;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.infraestructure.resources.geo.ConvertTypes;
import br.agr.terras.corelibrary.infraestructure.shapefile.GeoErroListener;
import br.agr.terras.corelibrary.infraestructure.shapefile.files.dbf.DBF_File;
import br.agr.terras.corelibrary.infraestructure.shapefile.files.shp.shapeTypes.ShpMultiPoint;
import br.agr.terras.corelibrary.infraestructure.shapefile.files.shp.shapeTypes.ShpPoint;
import br.agr.terras.corelibrary.infraestructure.shapefile.files.shp.shapeTypes.ShpPolyLine;
import br.agr.terras.corelibrary.infraestructure.shapefile.files.shp.shapeTypes.ShpPolygon;
import br.agr.terras.corelibrary.infraestructure.shapefile.files.shp.shapeTypes.ShpShape;
import br.agr.terras.corelibrary.infraestructure.shapefile.shapeFile.ShapeFile;

/**
 * Created by leo on 16/01/17.
 */

public class CARUtils {
    private List<ShapeFile> shapeFiles;
    private GeoErroListener erroListener;

    private List<List<LatLng>> poligonos;
    private List<List<LatLng>> linhas;
    private List<List<LatLng>> pontos;

    private List<CARInfo> infoPoligonos;
    private List<CARInfo> infoLinhas;
    private List<CARInfo> infoPontos;

    private String[] nomesCamadas;
    private String[] nomesCamadasUppercase;
    private String protocoloCar;

    public CARUtils(String zipFile, GeoErroListener errorListener, String[] nomesCamadas, String[] camadasUppercase) {
        this.erroListener = errorListener;
        this.nomesCamadas = nomesCamadas;
        this.nomesCamadasUppercase = camadasUppercase;
        shapeFiles = new ArrayList<>();
        try {
            ZipUtils.extractFolder(zipFile);
            String rootPath = zipFile.substring(0, zipFile.lastIndexOf("."));
            Log.i(getClass().getSimpleName(), "rootPath: " + rootPath);
            File root = new File(rootPath);
            for (File camada : root.listFiles()) {
                Log.d(getClass().getSimpleName(), "Arquivo em root: " + camada.getName());
                if (camada.isDirectory() && !camada.getName().contains("MARCA") && camada.list().length > 0) {
                    String subPath = root.getPath() + File.separator + camada.getName() + File.separator + camada.list()[0];
                    Logger.d("Nome de um subarquivo: " + subPath);
                    ShapeFile shapeFile = new ShapeFile(subPath);
                    shapeFiles.add(shapeFile);
                }
            }
            ler();
            IOUtils.delete(root);

        } catch (Exception e) {
            e.printStackTrace();
            errorListener.erroInterpretarArquivo();
        }
    }

    public List<List<LatLng>> getPoligonos() {
        return poligonos;
    }

    public List<List<LatLng>> getLinhas() {
        return linhas;
    }

    public List<List<LatLng>> getPontos() {
        return pontos;
    }

    public List<CARInfo> getInfoPoligonos() {
        return infoPoligonos;
    }

    public List<CARInfo> getInfoLinhas() {
        return infoLinhas;
    }

    public List<CARInfo> getInfoPontos() {
        return infoPontos;
    }

    private void ler() {
        poligonos = new ArrayList<>();
        linhas = new ArrayList<>();
        pontos = new ArrayList<>();
        infoPoligonos = new ArrayList<>();
        infoLinhas = new ArrayList<>();
        infoPontos = new ArrayList<>();
        try {
            for (ShapeFile shapeFile : shapeFiles) {
                Log.d(getClass().getSimpleName(), "lendo arquivo shape " + shapeFile.getSHP_shapeType().ID());
                for (int i = 0; i < shapeFile.getSHP_shapeCount(); i++) {
                    double[][] coordenadas = new double[shapeFile.getSHP_shapeCount()][2];
                    ShpShape shpShape = shapeFile.getFile_SHP().getShpShapes().get(i);
                    CARInfo carInfo = new CARInfo(shapeFile.getFile_DBF(), i);
                    if (shpShape.getShapeType().isTypeOfPolygon()) {
                        ShpPolygon polygon = (ShpPolygon) shpShape;
                        double d3array[][][] = polygon.getPointsAs3DArray();
                        int totalPoligonos = polygon.getNumberOfParts();
                        List<List<LatLng>> multiPolygon = new ArrayList<>();
                        for (int a = 0; a < totalPoligonos; a++) {
                            List<LatLng> polygonLatLng = new ArrayList<>();
                            for (int b = 0; b < d3array[a].length; b++) {
                                double longitude = d3array[a][b][0];
                                double latitude = d3array[a][b][1];
                                polygonLatLng.add(new LatLng(latitude, longitude));
                            }
                            multiPolygon.add(polygonLatLng);
                        }
                        poligonos.addAll(multiPolygon);
                        carInfo.montarJson(multiPolygon, ConvertTypes.Geometria.MULTIPOLYGON);
                        infoPoligonos.add(carInfo);
                        polygon.print();
                    } else if (shpShape.getShapeType().isTypeOfPolyLine()) {
                        ShpPolyLine polyLine = (ShpPolyLine) shpShape;
                        double d3array[][][] = polyLine.getPointsAs3DArray();
                        int totalLinhas = polyLine.getNumberOfParts();
                        List<List<LatLng>> multiLinhas = new ArrayList<>();
                        for (int a = 0; a < totalLinhas; a++) {
                            List<LatLng> polygonLatLng = new ArrayList<>();
                            for (int b = 0; b < d3array[a].length; b++) {
                                double longitude = d3array[a][b][0];
                                double latitude = d3array[a][b][1];
                                polygonLatLng.add(new LatLng(latitude, longitude));
                            }
                            multiLinhas.add(polygonLatLng);
                        }
                        linhas.addAll(multiLinhas);
                        carInfo.montarJson(multiLinhas, ConvertTypes.Geometria.MULTILINESTRING);
                        infoLinhas.add(carInfo);
                    } else if (shpShape.getShapeType().isTypeOfMultiPoint()) {
                        ShpMultiPoint multiPoint = (ShpMultiPoint) shpShape;
                        coordenadas = multiPoint.getPoints();
                        final List<LatLng> latLngs = doubleArrayToLatLngs(coordenadas);
                        pontos.add(latLngs);
                        carInfo.montarJson(new ArrayList<List<LatLng>>(){{add(latLngs);}}, ConvertTypes.Geometria.MULTIPOINT);
                        infoPontos.add(carInfo);
                    } else if (shpShape.getShapeType().isTypeOfPoint()) {
                        ShpPoint point = (ShpPoint) shpShape;
                        coordenadas[0] = point.getPoint();
                        final List<LatLng> latLngs = doubleArrayToLatLngs(coordenadas);
                        pontos.add(latLngs);
                        carInfo.montarJson(new ArrayList<List<LatLng>>(){{add(latLngs);}}, ConvertTypes.Geometria.MULTIPOINT);
                        infoPontos.add(carInfo);
                    }
                    if (ShapeFile.log)
                        Logger.i("Tamanho da string de camadas: " + carInfo.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            erroListener.erroInterpretarArquivo();
        }
    }

    public JsonObject getCar() {
        JsonObject car = new JsonObject();
        JsonObject origem = new JsonObject();
        origem.addProperty("codigoProtocolo", protocoloCar);
        car.add("origem", origem);
        JsonArray geo = new JsonArray();
        for (CARInfo carInfo : infoPoligonos) {
            JsonObject item = new JsonObject();
            item.addProperty("tipo", carInfo.camada);
            item.addProperty("area", carInfo.area);
            JsonObject geoJson = carInfo.json;
            item.add("geoJson", geoJson);
            geo.add(item);
        }
        car.add("geo", geo);
        return car;
    }

    private List<LatLng> doubleArrayToLatLngs(double[][] array) {
        List<LatLng> latLngs = new ArrayList<>();
        for (double[] lngLat : array)
            latLngs.add(new LatLng(lngLat[1], lngLat[0]));
        return latLngs;
    }

    private class CARInfo {
        private JsonObject json;
        private String protocoloCar;
        private String camada;
        private double area;
        private String theGeom;

        public CARInfo(DBF_File dbfFile, int i) throws UnsupportedEncodingException {
            for (int j = 0; j < dbfFile.getFields().length; j++) {
                String fieldName = dbfFile.getFields()[j].getName();
                if (fieldName.equals("recibo"))
                    protocoloCar = dbfFile.getContent()[i][j].trim();
                if (fieldName.equals("tema"))
                    camada = dbfFile.getContent()[i][j].trim();
                if (fieldName.equals("area"))
                    area = Double.parseDouble(dbfFile.getContent()[i][j].trim());
                CARUtils.this.protocoloCar = protocoloCar;
            }
            camada = new String(camada.getBytes("ISO-8859-1"), "UTF-8").trim();
            for (int j = 0; j < nomesCamadas.length; j++) {
                if (camada.equals(nomesCamadas[j])) {
                    camada = nomesCamadasUppercase[j];
                    break;
                }
            }
        }

        private void montarJson(List<List<LatLng>> latLngs, ConvertTypes.Geometria tipo) throws JSONException {
            theGeom = ConvertTypes.convertListLatLngsToTheGeom(latLngs, tipo);
            JsonReader jsonReader = new JsonReader(new StringReader(theGeom));
            JsonElement jsonElement = new JsonParser().parse(jsonReader);
            json = jsonElement.getAsJsonObject();
        }

    }
}
