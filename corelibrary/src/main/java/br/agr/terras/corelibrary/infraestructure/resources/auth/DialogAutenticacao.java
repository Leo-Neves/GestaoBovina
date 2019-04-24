package br.agr.terras.corelibrary.infraestructure.resources.auth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import br.agr.terras.corelibrary.R;
import br.agr.terras.materialdroid.Dialog;

/**
 * Created by leo on 23/06/16.
 */
public class DialogAutenticacao {
    private Context context;
    private View view;
    private Dialog dialog;

    public DialogAutenticacao(Context context){
        this.context = context;
        view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_autenticacao, null);
        dialog = new Dialog.Builder(context)
                .customView(view, false)
                .build();
    }

    public void show(){
        dialog.show();
    }
}
