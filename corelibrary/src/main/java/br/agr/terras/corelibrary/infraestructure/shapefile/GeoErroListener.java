package br.agr.terras.corelibrary.infraestructure.shapefile;

/**
 * Created by leo on 13/12/16.
 */

public abstract class GeoErroListener {
    public void erroEncontrarArquivo(){}

    public void erroInterpretarArquivo(){}

    public void erroArquivoMuitoGrande(){}

    public void erroDescompactarArquivo(){}
}
