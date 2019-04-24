package br.agr.terras.aurora.rest;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by leo on 28/03/17.
 */

public class Body extends HashMap<String, String> {

    public Body() {
        super();
    }

    public String put(@NonNull String key, @NonNull String value) {
        return super.put(key, value);
    }

    public void putAuthorization(){
        put("Authorization", "Basic Y3VybDpjdXJs");
    }
}
