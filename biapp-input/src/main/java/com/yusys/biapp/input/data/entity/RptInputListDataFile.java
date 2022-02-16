package com.yusys.biapp.input.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_LIST_DATA_FILE database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LIST_DATA_FILE")
public class RptInputListDataFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name="CASE_ID")
	private String caseId;

	@Column(name="FILE_NAME")
	private String fileName;

	@Column(name="FILE_PATH")
	private String filePath;

	@Column(name="IMPORT_DATE")
	private String importDate;

	@Column(name="IMPORT_MAN")
	private String importMan;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name="TEMPLE_ID")
	private String templeId;

    public RptInputListDataFile() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCaseId() {
		return this.caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getImportDate() {
		return this.importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

	public String getImportMan() {
		return this.importMan;
	}

	public void setImportMan(String importMan) {
		this.importMan = importMan;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

}