package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the RPT_INPUT_LST_TEMPLE_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_TEMPLE_INFO")
public class RptInputLstTempleInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TEMPLE_ID", unique=true, nullable=false, length=32)
	private String templeId;

	@Column(name="ALLOW_ADD", length=1)
	private String allowAdd;

	@Column(name="ALLOW_DELETE", length=1)
	private String allowDelete;

	@Column(name="ALLOW_INPUT_HIST", length=1)
	private String allowInputHist;

	@Column(name="ALLOW_INPUT_LOWER", length=1)
	private String allowInputLower;

	@Column(name="ALLOW_UPDATE", length=1)
	private String allowUpdate;

	@Column(name="CATALOG_ID", nullable=false, length=32)
	private String catalogId;

	@Column(name="DS_ID", length=32)
	private String dsId;

	@Column(name="DUTY_USER", length=100)
	private String dutyUser;
	
	@Column(name="DUTY_USER_DEPT", length = 32)
	private String dutyUserDept;

	@Column(name="ETL_TABLE", length=32)
	private String etlTable;

	@Column(name="IS_CHECK", length=1)
	private String isCheck;

	@Column(name="LOGIC_SYS_NO", length=32)
	private String logicSysNo;

	@Column(name="ORG_COLUMN", length=32)
	private String orgColumn;

	@Column(length=500)
	private String remark;

	@Column(name="TABLE_EN_NAME", length=100)
	private String tableEnName;

	@Column(name="TEMPLE_NAME", length=100)
	private String templeName;

	@Column(name="TEMPLE_STS", length=1)
	private String templeSts;
	
	@Column(name="CREATE_USER")
	private String createUser;
	
	@Column(name="CREATE_DATE")
	private String createDate;
	
	@Column(name="UNIT", length=10)
	private String unit;
	
	@Column(name="DEF_ORG")
	private String defOrg;

	@Column(name="DEF_ORG_NM")
	private String defOrgNm;
	
	@Column(name="DEF_SRC")
	private String defSrc;

	@Column(name="DEF_USER")
	private String defUser;
	@Transient
	private String defUserNm;
	
	@Column(name="IS_START")
	private String isStart;
	
	@Column(name="IS_SEND_NOTIFY")
	private String isSendNotify;
	
    public RptInputLstTempleInfo() {
    }

	public String getTempleId() {
		return this.templeId;
	}

	
	public String getDutyUserDept() {
		return dutyUserDept;
	}

	public void setDutyUserDept(String dutyUserDept) {
		this.dutyUserDept = dutyUserDept;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

	public String getAllowAdd() {
		return this.allowAdd;
	}

	public void setAllowAdd(String allowAdd) {
		this.allowAdd = allowAdd;
	}

	public String getAllowDelete() {
		return this.allowDelete;
	}

	public void setAllowDelete(String allowDelete) {
		this.allowDelete = allowDelete;
	}

	public String getAllowInputHist() {
		return this.allowInputHist;
	}

	public void setAllowInputHist(String allowInputHist) {
		this.allowInputHist = allowInputHist;
	}

	public String getAllowInputLower() {
		return this.allowInputLower;
	}

	public void setAllowInputLower(String allowInputLower) {
		this.allowInputLower = allowInputLower;
	}

	public String getAllowUpdate() {
		return this.allowUpdate;
	}

	public void setAllowUpdate(String allowUpdate) {
		this.allowUpdate = allowUpdate;
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getDutyUser() {
		return this.dutyUser;
	}

	public void setDutyUser(String dutyUser) {
		this.dutyUser = dutyUser;
	}

	public String getEtlTable() {
		return this.etlTable;
	}

	public void setEtlTable(String etlTable) {
		this.etlTable = etlTable;
	}

	public String getIsCheck() {
		return this.isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getOrgColumn() {
		return this.orgColumn;
	}

	public void setOrgColumn(String orgColumn) {
		this.orgColumn = orgColumn;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTableEnName() {
		return this.tableEnName;
	}

	public void setTableEnName(String tableEnName) {
		this.tableEnName = tableEnName;
	}

	public String getTempleName() {
		return this.templeName;
	}

	public void setTempleName(String templeName) {
		this.templeName = templeName;
	}

	public String getTempleSts() {
		return this.templeSts;
	}

	public void setTempleSts(String templeSts) {
		this.templeSts = templeSts;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDefOrg() {
		return defOrg;
	}

	public void setDefOrg(String defOrg) {
		this.defOrg = defOrg;
	}

	public String getDefSrc() {
		return defSrc;
	}

	public void setDefSrc(String defSrc) {
		this.defSrc = defSrc;
	}

	public String getDefUser() {
		return defUser;
	}

	public void setDefUser(String defUser) {
		this.defUser = defUser;
	}

	public String getIsStart() {
		return isStart;
	}

	public void setIsStart(String isStart) {
		this.isStart = isStart;
	}

	public String getIsSendNotify() {
		return isSendNotify;
	}

	public void setIsSendNotify(String isSendNotify) {
		this.isSendNotify = isSendNotify;
	}

	public String getDefOrgNm() {
		return defOrgNm;
	}

	public void setDefOrgNm(String defOrgNm) {
		this.defOrgNm = defOrgNm;
	}

	public String getDefUserNm() {
		return defUserNm;
	}

	public void setDefUserNm(String defUserNm) {
		this.defUserNm = defUserNm;
	}
	
}