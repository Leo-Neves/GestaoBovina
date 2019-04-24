package br.agr.terras.corelibrary.infraestructure.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leo on 29/11/16.
 */

public class RestUtils {

    public static String getMimeType(String nomeDoArquivo){
        try {
            JSONArray jsonArray = new JSONArray(AssetsUtils.stringFromAssets("mimetypes.json"));
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                if (nomeDoArquivo.contains(object.getString("sufixo")))
                    return object.getString("mimetype");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "text/plain";
    }
}
