package br.agr.terras.aurora.entities;

import java.util.Date;

public interface EntityWeb {
	Date getModified();
	Date getCreated();
	String getId();
	String getUuid();
	void setId(String id);
	void setCreated(Date date);
	void setModified(Date date);
}
