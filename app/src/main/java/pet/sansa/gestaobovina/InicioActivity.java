package pet.sansa.gestaobovina;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import br.agr.terras.aurora.log.Logger;
import pet.sansa.gestaobovina.entities.Boi;

import static pet.sansa.gestaobovina.Utils.createImageFile;

public class InicioActivity extends Activity {
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout layoutCarregando;
    private BoiAdapter boiAdapter;
    private DialogAdicionarBoi dialogAdicionarBoi;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        MyApplication.setActivity(this);
        layoutCarregando = findViewById(R.id.layoutCarregando);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager glm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(glm);
        auth = FirebaseAuth.getInstance();
        atualizarBois();
        configurarToolbar();
    }

    private void configurarToolbar() {
        toolbar.inflateMenu(R.menu.menu_inicio);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menuAdicionar:
                        adicionarBoi();
                        return true;
                    case R.id.menuSincronizar:
                        baixarBois();
                        return true;
                    case R.id.menuQrcode:
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(InicioActivity.this, new String[]{android.Manifest.permission.CAMERA}, 50);
                        }else{
                            Intent intent = new Intent(InicioActivity.this, QRCodeActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    case R.id.menuDashboard:
                        Intent intent = new Intent(InicioActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    private void atualizarBois() {
        SyncUsuario syncUsuario = new SyncUsuario(auth);
        syncUsuario.autenticar(new FirebaseListener() {
            @Override
            public void sucesso() {
                baixarBois();
                boiAdapter = new BoiAdapter();
                recyclerView.setAdapter(boiAdapter);
            }

            @Override
            public void falha(String erro) {
                exibirMensagemErro(erro);
            }
        });

    }

    private void baixarBois(){
        layoutCarregando.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        SyncBois syncBois = new SyncBois();
        syncBois.baixarBois(listenerBoiAtualizado);
    }

    private void adicionarBoi(){
        dialogAdicionarBoi = new DialogAdicionarBoi(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogAdicionarBoi.getView())
                .setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Boi boi = dialogAdicionarBoi.getBoi();
                        if (boi!=null){
                            layoutCarregando.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            SyncBois syncBois = new SyncBois();
                            syncBois.adicionarBoi(boi, listenerBoiAtualizado);
                        }else{
                            Toast.makeText(InicioActivity.this, "Campos não preenchidos corretamente", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setTitle("Novo Boi").create();
        dialog.show();

    }

    private void exibirMensagemErro(String erro){
        Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
    }

    private FirebaseListener listenerBoiAtualizado = new FirebaseListener() {
        @Override
        public void sucesso() {
            layoutCarregando.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            boiAdapter.atualizar();
        }

        @Override
        public void falha(String erro) {
            layoutCarregando.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Toast.makeText(InicioActivity.this, R.string.falha_conexao, Toast.LENGTH_LONG).show();
        }
    };

    static final int REQUEST_TAKE_PHOTO = 1;

    public void dispatchTakePictureIntent(String boiUuid) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(boiUuid);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Logger.e("Erro ao abrir câmera");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
           // Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            SyncBois syncBois = new SyncBois();
            syncBois.enviarFoto(new File(Utils.currentPhotoPath), Utils.currentUuid, listenerBoiAtualizado);
        }
    }
}
