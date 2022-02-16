package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the RPT_ENGINE_REPORT_STS database table.
 * 
 */
@Entity
@Table(name = "RPT_ENGINE_REPORT_STS")
public class RptEngineReportSts implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptEngineReportStsPK id;

	@Column(length = 10)
	private String sts;

	@Column(name = "END_TIME")
	private Timestamp endTime;

	@Column(name = "START_TIME")
	private Timestamp startTime;

	@Transient
	private String rptNm;

	@Transient
	private String startTimeStr;

	@Transient
	private String endTimeStr;

	@Transient
	private String rptNum;

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public RptEngineReportSts() {
	}

	public RptEngineReportStsPK getId() {
		return this.id;
	}

	public void setId(RptEngineReportStsPK id) {
		this.id = id;
	}

	public String getSts() {
		return this.sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((endTimeStr == null) ? 0 : endTimeStr.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((rptNm == null) ? 0 : rptNm.hashCode());
		result = prime * result + ((rptNum == null) ? 0 : rptNum.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result
				+ ((startTimeStr == null) ? 0 : startTimeStr.hashCode());
		result = prime * result + ((sts == null) ? 0 : sts.hashCode());
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
		RptEngineReportSts other = (RptEngineReportSts) obj;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (endTimeStr == null) {
			if (other.endTimeStr != null)
				return false;
		} else if (!endTimeStr.equals(other.endTimeStr))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (rptNm == null) {
			if (other.rptNm != null)
				return false;
		} else if (!rptNm.equals(other.rptNm))
			return false;
		if (rptNum == null) {
			if (other.rptNum != null)
				return false;
		} else if (!rptNum.equals(other.rptNum))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (startTimeStr == null) {
			if (other.startTimeStr != null)
				return false;
		} else if (!startTimeStr.equals(other.startTimeStr))
			return false;
		if (sts == null) {
			if (other.sts != null)
				return false;
		} else if (!sts.equals(other.sts))
			return false;
		return true;
	}

	
}