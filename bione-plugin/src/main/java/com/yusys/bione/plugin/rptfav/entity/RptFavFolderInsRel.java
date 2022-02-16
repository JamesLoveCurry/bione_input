package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_FAV_FOLDER_INS_REL database table.
 * 
 */
@Entity
@Table(name="RPT_FAV_FOLDER_INS_REL")
public class RptFavFolderInsRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptFavFolderInsRelPK id;

	@Column(name="INSTANCE_TYPE", length=10)
	private String instanceType;

    public RptFavFolderInsRel() {
    }

	public RptFavFolderInsRelPK getId() {
		return this.id;
	}

	public void setId(RptFavFolderInsRelPK id) {
		this.id = id;
	}
	
	public String getInstanceType() {
		return this.instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

}