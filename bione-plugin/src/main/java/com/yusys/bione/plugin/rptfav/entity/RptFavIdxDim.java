package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_FAV_IDX_DIM database table.
 * 
 */
@Entity
@Table(name="RPT_FAV_IDX_DIM")
public class RptFavIdxDim implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptFavIdxDimPK id;
	
    public RptFavIdxDim() {
    }

	public RptFavIdxDimPK getId() {
		return this.id;
	}

	public void setId(RptFavIdxDimPK id) {
		this.id = id;
	}
	

}