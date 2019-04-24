package br.agr.terras.corelibrary.infraestructure.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.acra.ACRA;
import org.json.JSONException;

import java.net.SocketException;

import br.agr.terras.aurora.rest.sync.CustomException;
import br.agr.terras.corelibrary.R;
import retrofit.RetrofitError;

public class LoadingAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private DelegateAsyncTask delegateAsyncTask;
    private ProgressDialog progressDialog;
    private boolean error = true;
    private boolean showDialog = true;

    public LoadingAsyncTask(Context context, DelegateAsyncTask delegateAsyncTask, boolean showDialog) {
        this.context = context;
        this.delegateAsyncTask = delegateAsyncTask;
        this.showDialog = showDialog;
        if (showDialog)
            this.progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        delegateAsyncTask.onPreExecute();
        if (showDialog)
            progressDialog.show();
    }


    @Override
    protected String doInBackground(Void... params) {
        String resultado;
            try {
                delegateAsyncTask.doInBackground();
                resultado = context.getString(R.string.sincronizado_com_sucesso);
                error = false;
            }catch (JSONException e) {
                e.printStackTrace();
                resultado = context.getResources().getString(R.string.aviso_falha_json);
                error = true;
                ACRA.getErrorReporter().handleSilentException(e);
            } catch (SocketException | RetrofitError e) {
                e.printStackTrace();
                resultado = context.getResources().getString(R.string.aviso_falha_servidor);
                error = true;
                ACRA.getErrorReporter().handleSilentException(e);
            }
            catch (UsernamePasswordException e){
                e.printStackTrace();
                resultado = context.getString(R.string.login_recusado);
                error = true;
                ACRA.getErrorReporter().handleSilentException(e);
            }
            catch (CustomException e){
                e.printStackTrace();
                resultado = context.getString(R.string.senha_alterada_na_web);
                error = true;
                ACRA.getErrorReporter().handleSilentException(e);
            } catch (Exception e) {
                e.printStackTrace();
                resultado = context.getResources().getString(R.string.aviso_falha_conexao);
                error = true;
                ACRA.getErrorReporter().handleSilentException(e);
            }
        return resultado;

    }

    @Override
    protected void onPostExecute(String result) {
        delegateAsyncTask.onPostUpdate(result, error);
        if(showDialog){
            dialogDismiss();
        }
    }

    public void showDialog(boolean showDialog){
        this.showDialog = showDialog;
    }
    public void dialogDismiss(){
        progressDialog.dismiss();
    }
}
