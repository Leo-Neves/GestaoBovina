package br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by leo on 04/08/16.
 */
public class ScaleDependenceMarker {
    private String key;
    private String id;
    private String nome;
    private LatLng position;
    private int cor;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }
}
