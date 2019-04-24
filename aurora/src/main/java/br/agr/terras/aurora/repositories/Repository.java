package br.agr.terras.aurora.repositories;

import java.util.List;

public interface Repository<T> {
	void save(T object);
	void update(T object);
	void delete(T object);
	void deleteAll();
	T get(String id);
	List<T> findAll();
	List<T> findAllNaoExcluidos();
	
}
