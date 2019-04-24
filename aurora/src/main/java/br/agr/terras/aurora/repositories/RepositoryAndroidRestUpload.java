package br.agr.terras.aurora.repositories;

import java.util.Date;
import java.util.List;

import br.agr.terras.aurora.entities.EntityAndroidUpload;
import br.agr.terras.aurora.entities.EntityWeb;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.WhereCondition.StringCondition;

public abstract class RepositoryAndroidRestUpload<W extends EntityWeb, U extends EntityAndroidUpload>
		extends RepositoryAndroidRest<W, U> {

	public RepositoryAndroidRestUpload(AbstractDao<U, String> dao) {
		super(dao);
	}

	public void updateRestUploadCallback(U entityAndroid) {
		entityAndroid.setSincronizadoFoto(true);
		getDao().update(entityAndroid);
	}

	public List<U> findAllNaoSincronizadosFoto() {
		StringCondition sc = new StringCondition("(SINCRONIZADO_FOTO is NULL or SINCRONIZADO_FOTO = 0 )");
		StringCondition sc2 = new StringCondition("UPLOAD_PATH is not null and trim(UPLOAD_PATH) is not \"\"");
		StringCondition sc3 = new StringCondition("SINCRONIZADO = 1");
		return getDao().queryBuilder().where(sc,sc2, sc3).list();
	}
	
	public void saveWithFoto(U entityAndroid) {
		entityAndroid.setSincronizado(false);
		entityAndroid.setSincronizadoFoto(false);

		Date date = new Date();
		entityAndroid.setPre_excluido(false);
		getDao().insertOrReplace(entityAndroid);
	}
	
	public void updateWithFoto(U entityAndroid) {
		entityAndroid.setSincronizado(false);
		entityAndroid.setSincronizadoFoto(false);
		getDao().update(entityAndroid);
	}
	


}
