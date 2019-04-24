package br.agr.terras.aurora.rest;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.aurora.rest.multipart.MultipartPost;
import br.agr.terras.aurora.rest.sync.RestSync;

/**
 * Created by leo on 16/09/16.
 */
public class EnvioArquivo {
    private RestSync restSync;
    private EnvioListener envioListener;
    private String url;
    private Header header;
    private Body body;
    private DadosParaEnvio arquivo;
    private String resposta;
    private int codigoResposta;

    public EnvioArquivo(String url, int codigoResposta, DadosParaEnvio arquivo, Body body, Header header, EnvioListener envioListener) {
        restSync = new RestSync(url);
        this.envioListener = envioListener;
        this.url = url;
        this.arquivo = arquivo;
        this.body = body==null?new Body():body;
        this.header = header==null?new Header():header;
        this.codigoResposta = codigoResposta;
    }

    public void sync() {
        Logger.i("Envio de " + arquivo.getPath() + " arquivos:" + url);
        RestTask restTask = new RestTask();
        restTask.execute();
    }

    private class RestTask extends AsyncTask<Void, Void, Integer>{
        String resposta;
        @Override
        protected Integer doInBackground(Void[] params) {
            MultipartPost multipartPost = new MultipartPost(url, arquivo, body, header);
            int codigo = multipartPost.enviar();
            resposta = multipartPost.getResponse();
            return codigo;
        }

        @Override
        protected void onPostExecute(Integer codigo) {
            if (codigo != codigoResposta)
                onRespostaError(new Exception("Error sending file. Code: "+codigo), codigo);
            else {
                try {
                    JSONObject jsonObject = new JSONObject(resposta);
                    onRespostaSuccess(jsonObject);
                }
                catch (JSONException e) {
                    onRespostaSuccess(resposta);
                }
            }
        }
    }

    private void onRespostaSuccess(JSONObject resposta) throws JSONException{
        Logger.i("Sucesso ao sincronizar com %s\n\n%s",url, resposta);
        this.resposta = resposta.toString();
        if (envioListener != null) {
            envioListener.onSuccess(resposta);
        }
    }

    private void onRespostaSuccess(String resposta){
        Logger.i("Sucesso ao sincronizar com %s\n\n%s",url, resposta);
        this.resposta = resposta;
        if (envioListener != null) {
            envioListener.onSuccess(resposta);
        }
    }

    private void onRespostaError(Exception e, int status) {
        Logger.e("Erro %d ao sincronizar com %s", status, url);
        if (envioListener != null)
            envioListener.onError(e, status);
    }

}

