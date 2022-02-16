package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RPT_VALID_LOGIC_IDX_REL database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_LOGIC_DS_REL")
public class RptValidLogicDsRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidLogicDsRelPK id;
	
	
    public RptValidLogicDsRel() {
    }

	public RptValidLogicDsRelPK getId() {
		return this.id;
	}

	public void setId(RptValidLogicDsRelPK id) {
		this.id = id;
	}

	
}