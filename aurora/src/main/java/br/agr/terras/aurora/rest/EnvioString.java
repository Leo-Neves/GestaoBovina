package br.agr.terras.aurora.rest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import br.agr.terras.aurora.log.Logger;

/**
 * Created by leo on 16/09/16.
 */
public class EnvioString extends StringRequest{
    private static EnvioListener envioListener;
    private static JSONObject json;
    private static String url;
    private static boolean containsResult;
    private static Map<String, String> params;

    public EnvioString(int method, String url, Map<String, String> params, EnvioListener envioListener){
        super(method,url,respostaListener,errorListener);
        EnvioString.envioListener = envioListener;
        EnvioString.url = url;
        EnvioString.params = params;
    }

    public void sync(Context context){
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(this);
    }

    private static Response.Listener<String> respostaListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String jsonResponse) {
            int status = 500;
            try {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    Logger.i("Resposta de "+url);
                    Logger.json(jsonResponse);
                    onRespostaSuccess(jsonObject);
                }catch (JSONException e){
                    try {
                        JSONArray jsonArray = new JSONArray(jsonResponse);
                        Logger.i("Resposta de "+url);
                        Logger.json(jsonResponse);
                        onRespostaSuccess(jsonArray);
                    }catch (JSONException f){
                        status = 500;
                        Logger.e("Resposta de "+url);
                        Logger.e(jsonResponse);
                        onRespostaError(f, status);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e(jsonResponse);
                onRespostaError(e, status);
            }
        }
    };

    private static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
            onRespostaError(error, 500);
        }
    };

    private static void onRespostaSuccess(JSONObject jsonResponse) throws JSONException {
        envioListener.onSuccess(jsonResponse);
    }

    private static void onRespostaSuccess(JSONArray jsonArray) throws JSONException {
        envioListener.onSuccess(jsonArray);
    }

    private static void onRespostaError(Exception e, int status){
        Logger.e("Erro "+status+" ao sincronizar com "+url);
        envioListener.onError(e, status);
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Logger.i("Params: "+params);
        return params;
    }

}
