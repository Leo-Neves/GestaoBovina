package br.agr.terras.corelibrary.infraestructure.asynctask;

import org.json.JSONException;

import java.io.IOException;

public abstract class DelegateAsyncTask {
    public void onPreExecute(){

    }
    public void doInBackground(Void... x) throws JSONException, IOException, UsernamePasswordException{

    }
    public void onPostUpdate(String mensagem, boolean error){

    }
}
