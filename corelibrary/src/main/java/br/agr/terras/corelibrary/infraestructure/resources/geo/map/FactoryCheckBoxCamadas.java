package br.agr.terras.corelibrary.infraestructure.resources.geo.map;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.CORE;
import br.agr.terras.materialdroid.CheckBox;

/**
 * Created by leo on 09/11/16.
 */

public class FactoryCheckBoxCamadas {
    private LinearLayout camadasMapaParent;
    private LinearLayout linearLayoutCamadas;
    private List<String> camadas;

    public FactoryCheckBoxCamadas(View rootView){
        camadasMapaParent = (LinearLayout) rootView;
        linearLayoutCamadas = (LinearLayout) rootView.findViewById(R.id.linearLayoutCamadas);
        camadasMapaParent.setVisibility(View.GONE);
        camadas = new ArrayList<>();
    }

    public void limparCamadas(){
        linearLayoutCamadas.removeAllViews();
        camadasMapaParent.setVisibility(View.GONE);
        camadas.clear();
    }

    public void adicionarCamada(final MapSetup mapSetup, final String camada, int cor, boolean exibirCamada){
        if (camadas.contains(camada)){
            mapSetup.setVisible(camada, exibirCamada);
            return;
        }
        camadas.add(camada);
        LinearLayout layout = new LinearLayout(CORE.getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        CheckBox checkBox = new CheckBox(CORE.getContext());
        checkBox.setChecked(exibirCamada);
        checkBox.setToggleColor(cor);
        checkBox.setTextColor(Color.WHITE);
        checkBox.setText(camada);
        checkBox.setLines(1);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setLayoutParams(getLayoutParams());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mapSetup.setVisible(camada, isChecked);
            }
        });
        layout.addView(getBox(cor));
        layout.addView(checkBox);
        linearLayoutCamadas.addView(layout);
    }

    private View getBox(int cor){
        TextView box = new TextView(CORE.getContext());
        box.setTextColor(Color.TRANSPARENT);
        box.setBackgroundColor(cor);
        box.setText("O");
        box.setLayoutParams(getLayoutParams());
        box.setPadding(5,2,5,2);
        return box;
    }

    private LinearLayout.LayoutParams getLayoutParams(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        return params;
    }

    public void showHide() {
        camadasMapaParent.setVisibility(camadasMapaParent.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
    }
}

