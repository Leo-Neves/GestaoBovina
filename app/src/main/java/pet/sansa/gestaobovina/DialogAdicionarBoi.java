package pet.sansa.gestaobovina;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import pet.sansa.gestaobovina.entities.Boi;

public class DialogAdicionarBoi {
    private LinearLayout view;
    private EditText etNome, etPeso, etNascimento;

    public DialogAdicionarBoi(InicioActivity activity){
        view = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_boi, null, false);
        etNome = view.findViewById(R.id.etNome);
        etNascimento = view.findViewById(R.id.etNascimento);
        etPeso = view.findViewById(R.id.etPeso);
    }

    public View getView(){
        return view;
    }

    public Boi getBoi() {
        Boi boi = new Boi();
        boi.setId(UUID.randomUUID().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        try {
            Date nascimento = sdf.parse(etNascimento.getText().toString());
            boi.setNascimento(nascimento.getTime());
            double peso = Double.parseDouble(etPeso.getText().toString());
            boi.setPeso(peso);
        } catch (ParseException e) {
            etNascimento.setError("Data inválida");
        } catch (NumberFormatException n){
            etPeso.setError("Peso inválido");
        }
        boi.setNome(etNome.getText().toString().trim());
        if (etNome.getText().toString().trim().isEmpty()){
            etNome.setError("Campo obrigatório");
            boi.setNome(null);
        }
        if (boi.getNome()!=null && boi.getPeso()!=0 && boi.getNascimento()!=null)
            return boi;
        return null;
    }
}
