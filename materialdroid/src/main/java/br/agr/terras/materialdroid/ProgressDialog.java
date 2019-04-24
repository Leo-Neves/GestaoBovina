package br.agr.terras.materialdroid;

import android.content.Context;

/**
 * Created by leo on 13/04/16.
 */
public class ProgressDialog {
    Context context;
    private Dialog progressDialog;

    public ProgressDialog(Context context) {
        this.context = context;
        progressDialog = new Dialog.Builder(context)
                .autoDismiss(false)
                .content(context.getResources().getString(R.string.carregando))
                .progress(true, 100)
                .build();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public void dismiss() {
        progressDialog.dismiss();
    }

    public void show() {
        progressDialog.show();
    }
}