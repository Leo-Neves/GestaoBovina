package br.agr.terras.corelibrary.infraestructure.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.io.IOException;


/**
 * Created by leo on 11/01/16.
 */
public class EasyTask {
    private Context context;
    private int tempoCarregamentoMinimo = 300;
    private Carregamento carregamento;

    public EasyTask(Context context, Carregamento carregamento){
        this.context = context;
        this.carregamento = carregamento;
    }

    private class Carregando  extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            carregamento.antesDeCarregar();
        }

        @Override
        protected String doInBackground(Void... voids) {
            SystemClock.sleep(tempoCarregamentoMinimo/2);
            carregamento.duranteCarregamento();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            carregamento.depoisDeCarregado();
        }
    }

    private class DialogTask  extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            SystemClock.sleep(tempoCarregamentoMinimo/2);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Carregando carregamentoTask = new Carregando();
            carregamentoTask.execute();
        }
    }

    public void setTempoCarregamentoMinimo(int tempoCarregamentoMinimo){
        this.tempoCarregamentoMinimo = tempoCarregamentoMinimo;
    }


    public void iniciar(){
        DialogTask dialogTask = new DialogTask();
        dialogTask.execute();
    }

    public abstract static class Carregamento{
        public void antesDeCarregar(){

        }
        public void duranteCarregamento(){

        }
        public void depoisDeCarregado(){

        }
    }
}