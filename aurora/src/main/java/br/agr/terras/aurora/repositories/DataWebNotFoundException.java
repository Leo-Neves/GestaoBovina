package br.agr.terras.aurora.repositories;

public class DataWebNotFoundException extends RuntimeException{
	public DataWebNotFoundException(String table,Long webId){
		super("O webID "+webId+" não foi encontrado na tabela "+table+"!");
	}

}
