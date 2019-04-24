package pet.sansa.gestaobovina.entities;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Boi extends RealmObject {
    @PrimaryKey
    private String id;
    private String nome;
    private Date nascimento;
    private double peso = 0D;
    private String url;

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

    public Long getNascimento() {
        return nascimento!=null?nascimento.getTime():null;
    }

    public void setNascimento(Long nascimento) {
        this.nascimento = nascimento!=null?new Date(nascimento):null;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
}
