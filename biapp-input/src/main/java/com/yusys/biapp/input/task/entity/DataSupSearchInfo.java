package com.yusys.biapp.input.task.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_INPUT_LIST_TABLE_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_INPUT_LIST_TABLE_INFO")
public class DataSupSearchInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TABLE_ID", unique = true, nullable = false, length = 32)
	private String tableId;

	@Column(name = "SET_ID", length = 20)
	private String setId;

	@Column(name = "CREATE_DATE", length = 20)
	private String createDate;

	@Column(name = "CREATE_USER", length = 100)
	private String createUser;

	@Column(name = "DS_ID", length = 32)
	private String dsId;

	@Column(name = "LOGIC_SYS_NO", length = 32)
	private String logicSysNo;

	@Column(name = "TABLE_CN_NAME", length = 100)
	private String tableCnName;

	@Column(name = "TABLE_EN_NAME", length = 100)
	private String tableEnName;

	@Column(name = "TABLE_TYPE", length = 1)
	private String tableType;

	@Column(name = "TABLE_SPACE", length = 100)
	private String tableSpace;
	
	private String upId;
	private String type;
	
	public String getUpId() {
		return upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}

	public DataSupSearchInfo() {
	}
	
	public DataSupSearchInfo(String dsId, String tableEnName) {
		this.dsId = dsId;	this.tableEnName = tableEnName;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	
	
	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
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

	public String getTableCnName() {
		return this.tableCnName;
	}

	public void setTableCnName(String tableCnName) {
		this.tableCnName = tableCnName;
	}

	public String getTableEnName() {
		return this.tableEnName;
	}

	public void setTableEnName(String tableEnName) {
		this.tableEnName = tableEnName;
	}

	public String getTableType() {
		return this.tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

}