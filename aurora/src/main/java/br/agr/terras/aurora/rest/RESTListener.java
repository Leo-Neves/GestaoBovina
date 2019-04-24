package br.agr.terras.aurora.rest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by leo on 30/11/16.
 */

public abstract class RESTListener {
    public void sucesso(Object... args){}
    public void erro(Object... args){}
    public void sucessoEErro(Object... args){}
    public void atualizarProgesso(int progresso, String mensagem){}

    public static EnvioListener restListenerToEnvioListener(final RESTListener restListener){
        return new EnvioListener() {
            @Override
            public void onError(Exception e, int code) {
                restListener.erro();
                restListener.sucessoEErro();
            }

            @Override
            public void onSuccess(JSONObject result) {
                restListener.sucesso();
                restListener.sucessoEErro();
            }

            @Override
            public void onSuccess(JSONArray result) {
                restListener.sucesso();
                restListener.sucessoEErro();
            }

            @Override
            public void onSuccess(String resposta) {
                restListener.sucesso();
                restListener.sucessoEErro();
            }
        };
    }
    public static RESTListener envioListenerToRESTListener(final EnvioListener envioListener){return new RESTListener() {
        @Override
        public void sucesso(Object... args) {
            envioListener.onSuccess("Success");
            envioListener.onSuccess(new JSONObject());
            envioListener.onSuccess(new JSONArray());
        }

        @Override
        public void erro(Object... args) {
            envioListener.onError(new Exception(), 0);
        }

        @Override
        public void sucessoEErro(Object... args) {
            envioListener.onError(new Exception(), 0);
            envioListener.onSuccess("Success");
            envioListener.onSuccess(new JSONObject());
            envioListener.onSuccess(new JSONArray());
        }
    };}
}
