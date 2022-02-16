package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_INPUT_REWRITE_FIELD_INFO database table.
 * 
 */
@Embeddable
public class RptInputRewriteFieldInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DS_ID")
	private String dsId;

	@Column(name="TEMPLE_ID")
	private String templeId;

    public RptInputRewriteFieldInfoPK() {
    }
	public String getDsId() {
		return this.dsId;
	}
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}
	public String getTempleId() {
		return this.templeId;
	}
	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptInputRewriteFieldInfoPK)) {
			return false;
		}
		RptInputRewriteFieldInfoPK castOther = (RptInputRewriteFieldInfoPK)other;
		return 
			this.dsId.equals(castOther.dsId)
			&& this.templeId.equals(castOther.templeId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dsId.hashCode();
		hash = hash * prime + this.templeId.hashCode();
		
		return hash;
    }
}