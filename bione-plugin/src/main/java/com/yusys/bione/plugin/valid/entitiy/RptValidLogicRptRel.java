package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_LOGIC_RPT_REL database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_LOGIC_RPT_REL")
public class RptValidLogicRptRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidLogicRptRelPK id;

    public RptValidLogicRptRel() {
    }

	public RptValidLogicRptRelPK getId() {
		return this.id;
	}

	public void setId(RptValidLogicRptRelPK id) {
		this.id = id;
	}
	
}