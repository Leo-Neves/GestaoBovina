package br.agr.terras.aurora.repositories;

import java.util.List;

import br.agr.terras.aurora.entities.EntityAndroid;
import br.agr.terras.aurora.entities.EntityWeb;

public interface RepositoryRest<W extends EntityWeb,A extends EntityAndroid> {
	void saveMultipleRest(List<A> entitiesAndroids);
	void saveRestCallback(A object);
	void updateRestCallback(A object);
	void deleteRestCallback(A object);
	List<A> findAllNaoSincronizadosUpdate();
	List<A> findAllNaoSincronizadosSave();
	List<A> findAllExcluidos();
	List<A> findAllNaoExcluidosWebs();
	List<A> findAllWebs();
	A getByWeb(W entityWeb);
	void updateAllToDeleteRest();
	void deleteAllExcluidosRest();
	void corrigeAndroidIDChaves();
	void corrigeWebIDChaves();
	A getLastWebId();
	A getLastModified();
}
