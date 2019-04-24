package br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by leo on 04/08/16.
 */
public class ClusterMarker implements ClusterItem{
    private String key;
    private String id;
    private String nome;
    private LatLng position;
    private int cor;

    public ClusterMarker(String key, String id, LatLng position, String nome, int cor){
        this.key = key;
        this.id = id;
        this.nome = nome;
        this.cor = cor;
        this.position = position;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

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
