package br.agr.terras.corelibrary.infraestructure.database;

import java.util.List;

public interface Repository<T> {
	void save(T object);
	void update(T object);
	void delete(T object);
	void deleteAll();
	T get(long id);
	List<T> findAll();
	
}
