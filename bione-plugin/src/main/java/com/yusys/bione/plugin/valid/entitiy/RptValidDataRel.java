package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_DATA_REL database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_DATA_REL")
@NamedQuery(name="RptValidDataRel.findAll", query="SELECT r FROM RptValidDataRel r")
public class RptValidDataRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidDataRelPK id;

	public RptValidDataRel() {
	}

	public RptValidDataRelPK getId() {
		return this.id;
	}

	public void setId(RptValidDataRelPK id) {
		this.id = id;
	}

}