package br.agr.terras.aurora.rest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by edson on 09/12/16.
 */

public abstract class EnvioListener {
    public void onError(Exception e, int code) {
    }

    public void onSuccess(JSONObject result) {
    }

    public void onSuccess(JSONArray result) {
    }

    public void onSuccess(String resposta) {
    }

    public void onSuccess(Boolean resposta) {
    }
}
