package br.agr.terras.aurora.entities;

public abstract class EntityAndroidDownloadUpload implements EntityAndroid{
    public abstract void setSincronizado_foto(Boolean sincronizadoFoto);
    public abstract Boolean getSincronizado_foto();
    public abstract void setUpload_path(String path);
    public abstract void setUpload_url(String url);
    public abstract String getUpload_path();
    public abstract String getUpload_url();
    public abstract void setDownload_url(String url);
    public abstract String getDownload_url();
    public abstract void setDownload_path(String path);
    public abstract String getDownload_path();
}
