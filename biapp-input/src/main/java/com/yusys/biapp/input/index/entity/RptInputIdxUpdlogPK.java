package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Embeddable
public class RptInputIdxUpdlogPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1957696509755889078L;

	@Column(name = "INDEX_ID")
	private String indexId;

	@Column(name = "DATA_DATE")
	private String dataDate;

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataDate == null) ? 0 : dataDate.hashCode());
		result = prime * result + ((indexId == null) ? 0 : indexId.hashCode());
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
		RptInputIdxUpdlogPK other = (RptInputIdxUpdlogPK) obj;
		if (dataDate == null) {
			if (other.dataDate != null)
				return false;
		} else if (!dataDate.equals(other.dataDate))
			return false;
		if (indexId == null) {
			if (other.indexId != null)
				return false;
		} else if (!indexId.equals(other.indexId))
			return false;
		return true;
	}
	
	
	
}