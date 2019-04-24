package br.agr.terras.aurora.entities;

import java.util.Date;

/**
 * Created by leandro on 10/03/15.
 */
public class IdList {
    private Long id;
    private Date modified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}
