package br.agr.terras.aurora.entities;

import java.util.Date;

public interface EntityAndroid {
	void setSincronizado(Boolean sincronizado);
	Boolean getSincronizado();
	void setExcluido(Boolean excluido);
	void setPre_excluido(Boolean pre_excluido);
	String getUuid();
	void setUuid(String id);
	void setCreated(Date date);
	void setModified(Date date);

	Date getModified();
	Date getCreated();
}
