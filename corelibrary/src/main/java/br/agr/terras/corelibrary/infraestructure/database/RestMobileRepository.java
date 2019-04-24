package br.agr.terras.corelibrary.infraestructure.database;


public interface RestMobileRepository<T>{
	public T findByUuid(String uuid);
}
