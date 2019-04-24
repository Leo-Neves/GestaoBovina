package br.agr.terras.aurora.entities;

/**
 * Created by leo on 26/08/15.
 */
public interface EntityAndroidDownload extends EntityAndroid {
    String getFile_url();
    String getNome();
    void setFile_path(String file_path);
}
