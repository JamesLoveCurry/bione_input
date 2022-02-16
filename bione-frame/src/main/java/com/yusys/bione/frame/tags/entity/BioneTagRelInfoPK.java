package com.yusys.bione.frame.tags.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the BIONE_TAG_REL_INFO database table.
 * 
 */
@Embeddable
public class BioneTagRelInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TAG_ID")
	private String tagId;

	@Column(name="TAG_OBJ_ID")
	private String tagObjId;

	@Column(name="OBJ_ID")
	private String objId;

	@Column(name="USER_ID")
	private String userId;

	public BioneTagRelInfoPK() {
	}
	public String getTagId() {
		return this.tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	public String getTagObjId() {
		return this.tagObjId;
	}
	public void setTagObjId(String tagObjId) {
		this.tagObjId = tagObjId;
	}
	public String getObjId() {
		return this.objId;
	}
	public void setObjId(String objId) {
		this.objId = objId;
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
		if (!(other instanceof BioneTagRelInfoPK)) {
			return false;
		}
		BioneTagRelInfoPK castOther = (BioneTagRelInfoPK)other;
		return 
			this.tagId.equals(castOther.tagId)
			&& this.tagObjId.equals(castOther.tagObjId)
			&& this.objId.equals(castOther.objId)
			&& this.userId.equals(castOther.userId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.tagId.hashCode();
		hash = hash * prime + this.tagObjId.hashCode();
		hash = hash * prime + this.objId.hashCode();
		hash = hash * prime + this.userId.hashCode();
		
		return hash;
	}
}