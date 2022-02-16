package com.yusys.biapp.input.dict.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the RPT_INPUT_LIST_DATA_DICT_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_INPUT_LIST_DATA_DICT_INFO")
public class RptInputListDataDictInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DICT_ID", unique = true, nullable = false, length = 32)
	private String dictId;

	@Column(name = "CATALOG_ID", nullable = false, length = 32)
	private String catalogId;

	@Column(name = "CREATE_TIME", length = 20)
	private String createTime;

	@Column(name = "CREATE_USER", length = 32)
	private String createUser;

	@Column(name = "DICT_NAME", length = 100)
	private String dictName;

	@Column(name = "DICT_TYPE", length = 10)
	private String dictType;

	@Column(name = "DS_ID", length = 32)
	private String dsId;

	@Column(name = "LOGIC_SYS_NO", length = 32)
	private String logicSysNo;

	@Column(name = "SHOW_TYPE", length = 10)
	private String showType;

	@Column(name = "SQL_TEXT", length = 500)
	private String sqlText;

	@Column(name = "STATIC_CONTENT", length = 500)
	private String staticContent;
	@Transient
	private String dsName;

	public RptInputListDataDictInfo() {
	}

	public String getDictId() {
		return this.dictId;
	}

	public void setDictId(String dictId) {
		this.dictId = dictId;
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getDictName() {
		return this.dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getDictType() {
		return this.dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getShowType() {
		return this.showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getSqlText() {
		return this.sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public String getStaticContent() {
		return this.staticContent;
	}

	public void setStaticContent(String staticContent) {
		this.staticContent = staticContent;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

}