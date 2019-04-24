package pet.sansa.gestaobovina;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.agr.terras.aurora.log.Logger;
import pet.sansa.gestaobovina.entities.Boi;

public class SyncBois {
    private BoiRepository boiRepository;

    public SyncBois(){
        boiRepository = new BoiRepository();
    }

    public void baixarBois(final FirebaseListener listener){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference boisRef = database.getReference().child("bois");
        boisRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                List<Boi> bois = new ArrayList<>();
                while (iterator.hasNext()){
                    Boi boi = new Boi();
                    DataSnapshot boiSnapshot = iterator.next();
                    String id = boiSnapshot.child("id").getValue().toString();
                    String nome = boiSnapshot.child("nome").getValue().toString();
                    String url = boiSnapshot.child("url").getValue()==null?null:boiSnapshot.child("url").getValue().toString();
                    long dataNascimento = (long) boiSnapshot.child("nascimento").getValue();
                    long peso = (long) boiSnapshot.child("peso").getValue();
                    boi.setId(id);
                    boi.setNome(nome);
                    boi.setNascimento(dataNascimento);
                    boi.setPeso(peso);
                    boi.setUrl(url);
                    bois.add(boi);
                    Logger.i(id);
                }
                boiRepository.saveBois(bois);
                listener.sucesso();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.falha(null);
            }
        });

    }

    public void atualizarUrlBoi(String uuid, String url, final FirebaseListener listener){
        Boi boi = boiRepository.findById(uuid);
        final Boi boizinho = new Boi();
        boizinho.setId(uuid);
        boizinho.setNome(boi.getNome());
        boizinho.setPeso(boi.getPeso());
        boizinho.setNascimento(boi.getNascimento());
        boizinho.setUrl(url);
        adicionarBoi(boizinho, listener);
    }

    public void adicionarBoi(final Boi boi, final FirebaseListener listener){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference boisRef = database.getReference();
        boisRef.child("bois").child(boi.getId()).setValue(boi).addOnCompleteListener(MyApplication.getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    listener.sucesso();
                    boiRepository.saveBoi(boi);
                }else{
                    listener.falha("Falha ao salvar boi "+boi.getId());
                }
            }
        });
    }

    public void deletarBoi(final Boi boi, final BoiAdapter adapter) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference boisRef = database.getReference();
        boisRef.child("bois").child(boi.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                boiRepository.removeBoi(boi);
                adapter.atualizar();
            }
        });
    }

    public void enviarFoto(File file, final String uuid, final FirebaseListener listener){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Uri uri = Uri.fromFile(file);
        final StorageReference boiReference = storageReference.child("bois/"+uuid);
        boiReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        boiReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String downloadUrl = task.getResult().toString();
                                atualizarUrlBoi(uuid, downloadUrl, listener);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.falha(null);
                    }
                });
    }
}
