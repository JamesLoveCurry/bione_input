package com.yusys.bione.plugin.rptorg.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * The persistent class for the RPT_MGR_FRS_ORG database table.
 * 
 */
@Entity
@Table(name="RPT_ORG_INFO")
public class RptOrgInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptOrgInfoPK id;

	@Column(name="FINANCE_ORG_NO", nullable=false, length=32)
	private String financeOrgNo;

	@Column(name="MGR_ORG_NO", nullable=false, length=32)
	private String mgrOrgNo;

	@Column(name="ORG_NM", nullable=false, length=100)
	private String orgNm;

	@Column(name="ORG_SUM_TYPE", length=10)
	private String orgSumType;

	@Column(name="UP_ORG_NO", nullable=false, length=32)
	private String upOrgNo;
	
	@Column(name="NAMESPACE")
	private String namespace;

	@Column(name="IS_VIRTUAL_ORG")
	private String isVirtualOrg;
	
	@Column(name="ORG_CLASS")
	private String orgClass;
	
	@Column(name="ORG_LEVEL")
	private String orgLevel;
	
	@Column(name="IS_ORG_REPORT")
	private String isOrgReport;

	@Column(name="LEI_NO")
	private String leiNo;

	@Column(name="addr")
	private String addr;

	@Column(name="NAMESPACE")
	public String getNamespace() {
    	return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public RptOrgInfo() {
    }

	public RptOrgInfoPK getId() {
		return this.id;
	}

	public void setId(RptOrgInfoPK id) {
		this.id = id;
	}
	
	public String getFinanceOrgNo() {
		return this.financeOrgNo;
	}

	public void setFinanceOrgNo(String financeOrgNo) {
		this.financeOrgNo = financeOrgNo;
	}

	public String getMgrOrgNo() {
		return this.mgrOrgNo;
	}

	public void setMgrOrgNo(String mgrOrgNo) {
		this.mgrOrgNo = mgrOrgNo;
	}

	public String getOrgNm() {
		return this.orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getOrgSumType() {
		return this.orgSumType;
	}

	public void setOrgSumType(String orgSumType) {
		this.orgSumType = orgSumType;
	}

	public String getUpOrgNo() {
		return this.upOrgNo;
	}

	public void setUpOrgNo(String upOrgNo) {
		this.upOrgNo = upOrgNo;
	}

	public String getIsVirtualOrg() {
		return isVirtualOrg;
	}

	public void setIsVirtualOrg(String isVirtualOrg) {
		this.isVirtualOrg = isVirtualOrg;
	}

	public String getOrgClass() {
		return orgClass;
	}

	public void setOrgClass(String orgClass) {
		this.orgClass = orgClass;
	}

	public String getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}

	public String getIsOrgReport() {
		return isOrgReport;
	}

	public void setIsOrgReport(String isOrgReport) {
		this.isOrgReport = isOrgReport;
	}

	public String getLeiNo() {
		return leiNo;
	}

	public void setLeiNo(String leiNo) {
		this.leiNo = leiNo;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}
}