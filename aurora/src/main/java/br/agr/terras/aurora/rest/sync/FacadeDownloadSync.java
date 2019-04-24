package br.agr.terras.aurora.rest.sync;

import android.content.Context;
import android.util.Log;

import java.util.List;

import br.agr.terras.aurora.entities.EntityAndroidDownload;
import br.agr.terras.aurora.entities.EntityWeb;
import br.agr.terras.aurora.repositories.RepositoryAndroidRestDownload;

/**
 * Created by leo on 27/08/15.
 */
public class FacadeDownloadSync<W extends EntityWeb,D extends EntityAndroidDownload>{
    RepositoryAndroidRestDownload<W,D> repository;

    public FacadeDownloadSync(RepositoryAndroidRestDownload<W,D> repository){
        this.repository = repository;
    }

    public void downloadFiles(Context context, String extensao){
        List<D> entities = repository.findAllWithValidUrl();
        Log.i("FacadeDownloadSync","Nº entidades: "+entities.size());
        for (EntityAndroidDownload entity:entities){
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.fromEntityAndroidDownload(context,entity,repository, extensao);
        }
    }
}
