package com.yusys.bione.plugin.rptfav.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_FAV_FOLDER database table.
 * 
 */
@Entity
@Table(name="RPT_FAV_FOLDER")
public class RptFavFolder implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FOLDER_ID", unique=true, nullable=false, length=32)
	private String folderId;

	@Column(name="FOLDER_NM", nullable=false, length=100)
	private String folderNm;

	@Column(name="UP_FOLDER_ID", length=32)
	private String upFolderId;

	@Column(name="USER_ID", length=32)
	private String userId;

    public RptFavFolder() {
    }

	public String getFolderId() {
		return this.folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFolderNm() {
		return this.folderNm;
	}

	public void setFolderNm(String folderNm) {
		this.folderNm = folderNm;
	}

	public String getUpFolderId() {
		return this.upFolderId;
	}

	public void setUpFolderId(String upFolderId) {
		this.upFolderId = upFolderId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}