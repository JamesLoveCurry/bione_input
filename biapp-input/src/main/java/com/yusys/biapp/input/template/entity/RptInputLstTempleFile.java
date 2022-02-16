package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_LST_TEMPLE_FILE database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_TEMPLE_FILE")
public class RptInputLstTempleFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FILE_ID", unique=true, nullable=false, length=32)
	private String fileId;

	@Column(name="CREATE_DATE", length=20)
	private String createDate;

	@Column(name="FILE_PATH", length=500)
	private String filePath;
	
	@Column(name="FILE_NAME", length=200)
	private String fileName;

	@Column(name="OPER_USER", length=32)
	private String operUser;

	@Column(length=1)
	private String sts;

	@Column(name="TEMPLE_ID", nullable=false, length=32)
	private String templeId;

    public RptInputLstTempleFile() {
    }

	public String getFileId() {
		return this.fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getOperUser() {
		return this.operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public String getSts() {
		return this.sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}