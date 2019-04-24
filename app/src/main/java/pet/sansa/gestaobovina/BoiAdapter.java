package pet.sansa.gestaobovina;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import br.agr.terras.aurora.log.Logger;
import pet.sansa.gestaobovina.entities.Boi;

public class BoiAdapter extends RecyclerView.Adapter<BoiAdapter.BoiHolder> {
    private List<Boi> bois;
    private BoiRepository boiRepository;

    public BoiAdapter(){
        boiRepository = new BoiRepository();
        bois = boiRepository.findAllBois();
    }

    @NonNull
    @Override
    public BoiHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BoiHolder(MyApplication.getActivity().getLayoutInflater().inflate(R.layout.row_boi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoiHolder holder, int i) {
        final Boi boi  = bois.get(i);
        holder.idade.setText(String.format(Locale.ITALY,"%d meses",getIdadeEmMeses(new Date(boi.getNascimento()))));
        holder.nome.setText(boi.getNome());
        holder.peso.setText(String.format(Locale.ITALY, "%.2f Kg",boi.getPeso()));
        holder.deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SyncBois().deletarBoi(boi, BoiAdapter.this);
            }
        });
        holder.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.getActivity().dispatchTakePictureIntent(boi.getId());
            }
        });

            Picasso.get()
                    .load(boi.getUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.foto);


    }

    @Override
    public int getItemCount() {
        return bois.size();
    }

    public void atualizar(){
        bois = boiRepository.findAllBois();
        notifyDataSetChanged();
    }

    private int getIdadeEmMeses(Date dataNascimento){
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(dataNascimento);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        return diffMonth;
    }

    class BoiHolder extends RecyclerView.ViewHolder{
        ImageView foto;
        TextView nome;
        TextView idade;
        TextView peso;
        Button deletar;
        ImageButton camera;

        public BoiHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ivIcone);
            nome = itemView.findViewById(R.id.tvNome);
            idade = itemView.findViewById(R.id.tvIdade);
            peso = itemView.findViewById(R.id.tvPeso);
            deletar = itemView.findViewById(R.id.btnDeletar);
            camera = itemView.findViewById(R.id.btnCamera);
        }
    }
}
