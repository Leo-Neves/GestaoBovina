package br.agr.terras.materialdroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leo on 22/02/16.
 */
public class SpinnerObsolete extends LinearLayout{
    private Context context;
    private View view;
    private Spinner spinner;
    private TextView titulo;
    private List<String> nomes;
    private List<Integer> valores;
    private List<Object> tags;
    private OnItemSelectedListener onItemSelectedListener;

    public SpinnerObsolete(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.component_spinner, this);
        spinner = (Spinner) view.findViewById(R.id.spinnerMaterial);
        titulo = (TextView) view.findViewById(R.id.textViewSpinnerMaterial);

        configureListener();
    }



    private void configureAdapter() {
        if (nomes != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, nomes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private void configureListener(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (onItemSelectedListener != null)
                    if (position >= 0){
                        onItemSelectedListener.onItemSelected(SpinnerObsolete.this, position);
                        onItemSelectedListener.onItemSelected(position);
                    }
                    else
                        onItemSelectedListener.onNothingSelected();
                salvar();
                spinner.setError(null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public Integer getSelectedItemValor() {
        try {
            return valores.get((spinner.getSelectedItemPosition()-1));
        } catch (Exception e) {
            return null;
        }
    }

    public String getSelectedItemNome() {
        try {
            return nomes.get((spinner.getSelectedItemPosition()-1));
        } catch (Exception e) {
            return null;
        }
    }

    public Object getSelectedItemTag(){
        try {
            return tags.get(spinner.getSelectedItemPosition()-1);
        } catch (Exception e) {
            return null;
        }
    }

    public void setNomes(List<String> nomes) {
        this.nomes = nomes;
        if (nomes != null) {
            if (nomes.size() > 0) {
                configureAdapter();
            }
        }
    }

    public void setValores(List<Integer> valores) {
        this.valores = valores;
    }

    public void setTags(List<Object> tags){
        this.tags = tags;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private void salvar(){
    }

    public void setPosition(Integer position) {
        if (position==null)
            spinner.setSelection(-1);
        else
            spinner.setSelection(position+1, true);
    }

    public void showError() {
        spinner.setError(context.getString(R.string.campo_obrigatorio));
    }

    public static abstract class OnItemSelectedListener {
        public void onItemSelected(int position){

        }

        public void onNothingSelected(){

        }

        public void onItemSelected(SpinnerObsolete spinner, int position){

        }
    }

    public void setEnabled(boolean enabled){
        spinner.setEnabled(enabled);
    }
}
