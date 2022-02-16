package com.yusys.bione.plugin.rptdim.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_DIM_HISTORY_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_DIM_HISTORY_INFO")
public class RptDimHistoryInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "HIS_ID")
	private String hisId;

	@Column(name = "DIM_TYPE_NO")
	private String dimTypeNo;

	@Column(name = "START_DATE")
	private String startDate;

	@Column(name = "END_DATE")
	private String endDate;

	@Column(name = "VER_ID")
	private long veId;

	@Column(name = "HIS_INFO")
	private String hisInfo;

	public String getHisId() {
		return hisId;
	}

	public void setHisId(String hisId) {
		this.hisId = hisId;
	}

	public String getDimTypeNo() {
		return dimTypeNo;
	}

	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public long getVeId() {
		return veId;
	}

	public void setVeId(long veId) {
		this.veId = veId;
	}

	public String getHisInfo() {
		return hisInfo;
	}

	public void setHisInfo(String hisInfo) {
		this.hisInfo = hisInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dimTypeNo == null) ? 0 : dimTypeNo.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((hisId == null) ? 0 : hisId.hashCode());
		result = prime * result + ((hisInfo == null) ? 0 : hisInfo.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + (int) (veId ^ (veId >>> 32));
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
		RptDimHistoryInfo other = (RptDimHistoryInfo) obj;
		if (dimTypeNo == null) {
			if (other.dimTypeNo != null)
				return false;
		} else if (!dimTypeNo.equals(other.dimTypeNo))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (hisId == null) {
			if (other.hisId != null)
				return false;
		} else if (!hisId.equals(other.hisId))
			return false;
		if (hisInfo == null) {
			if (other.hisInfo != null)
				return false;
		} else if (!hisInfo.equals(other.hisInfo))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (veId != other.veId)
			return false;
		return true;
	}

}