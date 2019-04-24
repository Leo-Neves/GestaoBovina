package br.agr.terras.aurora.repositories;

import java.util.Date;
import java.util.List;

import br.agr.terras.aurora.entities.EntityAndroid;
import br.agr.terras.aurora.entities.EntityAndroidDownloadUpload;
import br.agr.terras.aurora.entities.EntityWeb;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by leo on 04/02/16.
 */
public abstract class RepositoryAndroidRestDownloadUpload <D extends EntityAndroidDownloadUpload, W extends EntityWeb>
        extends RepositoryAndroidRest<W, D>  {

    public RepositoryAndroidRestDownloadUpload(AbstractDao<D, String> dao) {
        super(dao);
    }

    public List<D> findAllWithValidUrl() {
        WhereCondition.StringCondition sc = new WhereCondition.StringCondition("DOWNLOAD_URL IS NOT NULL");
        WhereCondition.StringCondition sc2 = new WhereCondition.StringCondition("DOWNLOAD_PATH IS NULL");
        return getDao().queryBuilder().where(sc, sc2).list();
    }

    public void updateRestUploadCallback(D entityAndroid) {
        entityAndroid.setSincronizado_foto(true);
        getDao().update(entityAndroid);
    }

    public List<D> findAllNaoSincronizadosFoto() {
        WhereCondition.StringCondition sc = new WhereCondition.StringCondition("SINCRONIZADO_FOTO = 0");
        WhereCondition.StringCondition sc2 = new WhereCondition.StringCondition("UPLOAD_PATH IS NOT NULL");
        WhereCondition.StringCondition sc3 = new WhereCondition.StringCondition("SINCRONIZADO = 1");
        return getDao().queryBuilder().where(sc,sc2,sc3).list();
    }

    public void saveWithFoto(D entityAndroid) {
        entityAndroid.setSincronizado(false);
        entityAndroid.setSincronizado_foto(false);

        Date date = new Date();
        getDao().insertOrReplace(entityAndroid);
    }

    public void updateWithFoto(D entityAndroid) {
        entityAndroid.setSincronizado(false);
        entityAndroid.setSincronizado_foto(false);
        getDao().update(entityAndroid);
    }
}
