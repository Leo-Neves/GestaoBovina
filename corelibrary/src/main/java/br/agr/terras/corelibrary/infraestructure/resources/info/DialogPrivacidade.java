package br.agr.terras.corelibrary.infraestructure.resources.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import br.agr.terras.corelibrary.R;
import br.agr.terras.materialdroid.Dialog;

/**
 * Created by edson on 10/06/16.
 */
public class DialogPrivacidade {

    private View view;
    private Context context;

    public DialogPrivacidade(LayoutInflater layoutInflater, Context context){
        this.context = context;
        view = layoutInflater.inflate(R.layout.dialog_privacidade,null);
    }

    public void createDialog(){
        new Dialog.Builder(context)
                .title(context.getResources().getString(R.string.politica_privacidade))
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
