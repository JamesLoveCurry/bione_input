package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_MODULE_REL database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_MODULE_REL")
public class RptMgrModuleRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrModuleRelPK id;

    public RptMgrModuleRel() {
    }

	public RptMgrModuleRelPK getId() {
		return this.id;
	}

	public void setId(RptMgrModuleRelPK id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptMgrModuleRel other = (RptMgrModuleRel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}