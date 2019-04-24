package br.agr.terras.corelibrary.infraestructure.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leo on 14/10/16.
 */
public class CsvUtils {

    public static JSONArray csvToJson(String csv) throws IOException, JSONException {
        JSONArray jsonArray = new JSONArray();
        List<String> objects = new ArrayList<>();
        BufferedReader bufReader = new BufferedReader(new StringReader(csv));
        String line = bufReader.readLine();
        objects.addAll(Arrays.asList(line.split(",")));
        while ((line=bufReader.readLine()) != null){
            JSONObject object = new JSONObject();
            List<String> linha = Arrays.asList(line.split(","));
            for (int i=0;i<objects.size();i++){
                object.put(objects.get(i), linha.get(i));
            }
            jsonArray.put(object);
        }
        return jsonArray;
    }
}
