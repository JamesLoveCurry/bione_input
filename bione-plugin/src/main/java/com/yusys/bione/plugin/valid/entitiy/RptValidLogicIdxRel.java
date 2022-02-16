package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_LOGIC_IDX_REL database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_LOGIC_IDX_REL")
public class RptValidLogicIdxRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidLogicIdxRelPK id;

    public RptValidLogicIdxRel() {
    }

	public RptValidLogicIdxRelPK getId() {
		return this.id;
	}

	public void setId(RptValidLogicIdxRelPK id) {
		this.id = id;
	}
	
}