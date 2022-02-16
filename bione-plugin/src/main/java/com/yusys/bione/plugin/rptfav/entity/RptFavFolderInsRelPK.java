package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_FAV_FOLDER_INS_REL database table.
 * 
 */
@Embeddable
public class RptFavFolderInsRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="USER_ID", unique=true, nullable=false, length=32)
	private String userId;

	@Column(name="INSTANCE_ID", unique=true, nullable=false, length=32)
	private String instanceId;

	@Column(name="FOLDER_ID", unique=true, nullable=false, length=32)
	private String folderId;

    public RptFavFolderInsRelPK() {
    }
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getInstanceId() {
		return this.instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getFolderId() {
		return this.folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptFavFolderInsRelPK)) {
			return false;
		}
		RptFavFolderInsRelPK castOther = (RptFavFolderInsRelPK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.instanceId.equals(castOther.instanceId)
			&& this.folderId.equals(castOther.folderId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.instanceId.hashCode();
		hash = hash * prime + this.folderId.hashCode();
		
		return hash;
    }
}