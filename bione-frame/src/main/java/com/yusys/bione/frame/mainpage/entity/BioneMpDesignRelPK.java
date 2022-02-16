package com.yusys.bione.frame.mainpage.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_MP_DESIGN_REL database table.
 * 
 */
@Embeddable
public class BioneMpDesignRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DESIGN_ID")
	private String designId;

	@Column(name="USER_ID")
	private String userId;

	public BioneMpDesignRelPK() {
	}
	public String getDesignId() {
		return this.designId;
	}
	public void setDesignId(String designId) {
		this.designId = designId;
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BioneMpDesignRelPK)) {
			return false;
		}
		BioneMpDesignRelPK castOther = (BioneMpDesignRelPK)other;
		return 
			this.designId.equals(castOther.designId)
			&& this.userId.equals(castOther.userId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.designId.hashCode();
		hash = hash * prime + this.userId.hashCode();
		
		return hash;
	}
}