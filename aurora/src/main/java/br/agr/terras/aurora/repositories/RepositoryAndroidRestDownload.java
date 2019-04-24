package br.agr.terras.aurora.repositories;

import java.util.List;

import br.agr.terras.aurora.entities.EntityAndroidDownload;
import br.agr.terras.aurora.entities.EntityWeb;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.WhereCondition;

/**
 * Created by leo on 26/08/15.
 */
public abstract class RepositoryAndroidRestDownload<W extends EntityWeb, D extends EntityAndroidDownload>
        extends RepositoryAndroidRest<W, D>  {

    public RepositoryAndroidRestDownload(AbstractDao<D, String> dao) {
        super(dao);
    }

    public List<D> findAllWithValidUrl() {
        WhereCondition.StringCondition sc1 = new WhereCondition.StringCondition("FILE_URL IS NOT NULL");
        return getDao().queryBuilder().where(sc1).list();
    }

}
