package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_ENGINE_CHECK_STS database table.
 * 
 */
@Embeddable
public class RptEngineCheckStsPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="RPT_TEMPLATE_ID", unique=true, nullable=false, length=32)
	private String rptTemplateId;

	@Column(name="DATA_DATE", unique=true, nullable=false, length=8)
	private String dataDate;

	@Column(name="ORG_NO", unique=true, nullable=false, length=32)
	private String orgNo;
	
	@Column(name="CHECK_TYPE", unique=true, nullable=false, length=10)
	private String checkType;

	public String getRptTemplateId() {
		return rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((checkType == null) ? 0 : checkType.hashCode());
		result = prime * result
				+ ((dataDate == null) ? 0 : dataDate.hashCode());
		result = prime * result + ((orgNo == null) ? 0 : orgNo.hashCode());
		result = prime * result
				+ ((rptTemplateId == null) ? 0 : rptTemplateId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptEngineCheckStsPK other = (RptEngineCheckStsPK) obj;
		if (checkType == null) {
			if (other.checkType != null)
				return false;
		} else if (!checkType.equals(other.checkType))
			return false;
		if (dataDate == null) {
			if (other.dataDate != null)
				return false;
		} else if (!dataDate.equals(other.dataDate))
			return false;
		if (orgNo == null) {
			if (other.orgNo != null)
				return false;
		} else if (!orgNo.equals(other.orgNo))
			return false;
		if (rptTemplateId == null) {
			if (other.rptTemplateId != null)
				return false;
		} else if (!rptTemplateId.equals(other.rptTemplateId))
			return false;
		return true;
	}
	

}