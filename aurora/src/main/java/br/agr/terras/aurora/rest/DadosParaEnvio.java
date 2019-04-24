package br.agr.terras.aurora.rest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by leo on 26/11/16.
 */

public class DadosParaEnvio {
    private String nameInput;
    private String nameFile;
    private String path;
    private byte[] data;
    private String type;

    public DadosParaEnvio(byte[] data ,String nameInput, String nameFile) {
        this.nameInput = nameInput;
        this.nameFile = nameFile;
        this.data = data;
    }

    public DadosParaEnvio(String path, String nameInput) {
        this.path = path;
        this.nameInput = nameInput;
        this.nameFile = path.substring(path.lastIndexOf("/")+1);
        setFile(new File(path));
    }

    public String getNameFile(){
        return nameFile;
    }

    public String getNameInput() {
        return nameInput;
    }

    public void setNameInput(String nameInput) {
        this.nameInput = nameInput;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] content) {
        this.data = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFile(File file){
        try {
            RandomAccessFile r = new RandomAccessFile(file.getPath(), "r");
            data = new byte[(int)r.length()];
            r.readFully(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile(){
        return new File(path);
    }

    @Override
    public String toString() {
        String dataSize = data.length<1000? data.length+"KB": data.length+"MB";
        return "path:\t"+ path +"\nnameInput:\t"+nameInput+"\ncontentLength:\t"+dataSize+"\nmimeType:\t"+type;
    }
}