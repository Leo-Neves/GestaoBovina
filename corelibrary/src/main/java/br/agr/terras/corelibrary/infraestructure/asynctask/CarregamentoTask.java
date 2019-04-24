package br.agr.terras.corelibrary.infraestructure.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.widget.TextView;

import br.agr.terras.corelibrary.R;
import br.agr.terras.materialdroid.Dialog;


/**
 * Created by leo on 11/01/16.
 */
public class CarregamentoTask {
    private Context context;
    private int tempoCarregamentoMinimo = 500;
    private Carregamento carregamento;
    private Dialog dialog;
    private int mensagemId = R.string.carregando;
    private TextView view;

    public CarregamentoTask(Context context, Carregamento carregamento){
        this.context = context;
        this.carregamento = carregamento;
        view = (TextView) ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_carregando, null).findViewById(R.id.textViewDialogCarregando);
        view.setText(mensagemId);
    }

    public CarregamentoTask setMensagem(int mensagemId) {
        this.mensagemId = mensagemId;
        view.setText(context.getString(mensagemId));
        return this;
    }

    public void refreshMensagem(String mensagem) {
        dialog.setContent(mensagem);
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
            dialog.dismiss();
        }
    }

    private class DialogTask  extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
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

    private void configureDialog(){
        dialog = new Dialog.Builder(context)
                .autoDismiss(false)
                .cancelable(false)
                //.customView(view, false)
                .content(mensagemId)
                .build();
    }

    public void iniciar(){
        configureDialog();
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

    public CarregamentoTask setTempoCarregamentoMinimo(int tempoCarregamentoMinimo){
        this.tempoCarregamentoMinimo = tempoCarregamentoMinimo;
        return this;
    }
}