package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_SRC_REL_INFO database table.
 * 
 */
@Embeddable
public class RptIdxSrcRelInfoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="INDEX_VER_ID")
	private long indexVerId;

	@Column(name="SRC_INDEX_NO")
	private String srcIndexNo;
	
	@Column(name="SRC_MEASURE_NO")
	private String srcMeasureNo;
	
	public RptIdxSrcRelInfoPK() {
	}
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public long getIndexVerId() {
		return this.indexVerId;
	}
	public void setIndexVerId(long indexVerId) {
		this.indexVerId = indexVerId;
	}

	public String getSrcIndexNo() {
		return this.srcIndexNo;
	}

	public void setSrcIndexNo(String srcIndexNo) {
		this.srcIndexNo = srcIndexNo;
	}
	
	
	public String getSrcMeasureNo() {
		return srcMeasureNo;
	}
	public void setSrcMeasureNo(String srcMeasureNo) {
		this.srcMeasureNo = srcMeasureNo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptIdxSrcRelInfoPK other = (RptIdxSrcRelInfoPK) obj;
		if (indexNo == null) {
			if (other.indexNo != null)
				return false;
		} else if (!indexNo.equals(other.indexNo))
			return false;
		if (indexVerId != other.indexVerId)
			return false;
		if (srcIndexNo == null) {
			if (other.srcIndexNo != null)
				return false;
		} else if (!srcIndexNo.equals(other.srcIndexNo))
			return false;
		if (srcMeasureNo == null) {
			if (other.srcMeasureNo != null)
				return false;
		} else if (!srcMeasureNo.equals(other.srcMeasureNo))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((indexNo == null) ? 0 : indexNo.hashCode());
		result = prime * result + (int) (indexVerId ^ (indexVerId >>> 32));
		result = prime * result
				+ ((srcIndexNo == null) ? 0 : srcIndexNo.hashCode());
		result = prime * result
				+ ((srcMeasureNo == null) ? 0 : srcMeasureNo.hashCode());
		return result;
	}
}