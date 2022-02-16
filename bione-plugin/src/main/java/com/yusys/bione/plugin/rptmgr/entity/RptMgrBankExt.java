package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_BANK_EXT database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_BANK_EXT")
public class RptMgrBankExt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="DATA_PRECISION")
	private String dataPrecision;

	@Column(name="DATA_UNIT")
	private String dataUnit;

	@Column(name="DUTY_DEPT")
	private String dutyDept;

	@Column(name="GENERATE_TIME")
	private String generateTime;

	private String parties;

	@Column(name="REQUIRE_EXPLAIN")
	private String requireExplain;

	@Column(name="RPT_CURRTYPE")
	private String rptCurrtype;

	@Column(name="RPT_GENERATE_TYPE")
	private String rptGenerateType;


	@Column(name="RPT_USE")
	private String rptUse;

    public RptMgrBankExt() {
    }

	public String getRptId() {
		return this.rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getDataPrecision() {
		return this.dataPrecision;
	}

	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDataUnit() {
		return this.dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getDutyDept() {
		return this.dutyDept;
	}

	public void setDutyDept(String dutyDept) {
		this.dutyDept = dutyDept;
	}

	public String getGenerateTime() {
		return this.generateTime;
	}

	public void setGenerateTime(String generateTime) {
		this.generateTime = generateTime;
	}

	public String getParties() {
		return this.parties;
	}

	public void setParties(String parties) {
		this.parties = parties;
	}

	public String getRequireExplain() {
		return this.requireExplain;
	}

	public void setRequireExplain(String requireExplain) {
		this.requireExplain = requireExplain;
	}

	public String getRptCurrtype() {
		return this.rptCurrtype;
	}

	public void setRptCurrtype(String rptCurrtype) {
		this.rptCurrtype = rptCurrtype;
	}

	public String getRptGenerateType() {
		return this.rptGenerateType;
	}

	public void setRptGenerateType(String rptGenerateType) {
		this.rptGenerateType = rptGenerateType;
	}


	public String getRptUse() {
		return this.rptUse;
	}

	public void setRptUse(String rptUse) {
		this.rptUse = rptUse;
	}

}