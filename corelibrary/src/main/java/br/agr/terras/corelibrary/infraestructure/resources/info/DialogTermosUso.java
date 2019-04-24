package br.agr.terras.corelibrary.infraestructure.resources.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import br.agr.terras.corelibrary.R;
import br.agr.terras.materialdroid.Dialog;

/**
 * Created by edson on 10/06/16.
 */
public class DialogTermosUso {

    private View view;
    private Context context;

    public DialogTermosUso(LayoutInflater layoutInflater, Context context){
        this.context = context;
        view = layoutInflater.inflate(R.layout.dialog_termo_uso,null);
    }

    public void createDialog(){
        new Dialog.Builder(context)
                .title("Termos de Uso")
                .content("Lorem Ipsum, ipsum lorem")
                .callback(buttonCallback)
                .positiveText("Concordo")
                .negativeText("Não Concordo")
                .positiveColor(context.getResources().getColor(R.color.TerrasGreen))
                .negativeColor(context.getResources().getColor(R.color.TerrasGreen))
                .autoDismiss(false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    private Dialog.ButtonCallback buttonCallback = new Dialog.ButtonCallback() {
        @Override
        public void onPositive(Dialog dialog) {
            dialog.dismiss();
        }

        @Override
        public void onNegative(Dialog dialog) {
            dialog.dismiss();
        }
    };

}
