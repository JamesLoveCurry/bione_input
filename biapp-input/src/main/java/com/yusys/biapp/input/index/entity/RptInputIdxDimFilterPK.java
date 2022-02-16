package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Embeddable
public class RptInputIdxDimFilterPK implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = -80986223223370664L;

	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="DIM_NO")
	private String dimNo;

	@Column(name="INDEX_NO")
	private String indexNo;
	
	public RptInputIdxDimFilterPK(){
		
	}


	public String getCfgId() {
		return cfgId;
	}


	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}


	public String getDimNo() {
		return dimNo;
	}


	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cfgId == null) ? 0 : cfgId.hashCode());
		result = prime * result + ((dimNo == null) ? 0 : dimNo.hashCode());
		return result;
	}

	public String getIndexNo() {
		return indexNo;
	}


	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptInputIdxDimFilterPK other = (RptInputIdxDimFilterPK) obj;
		if (cfgId == null) {
			if (other.cfgId != null)
				return false;
		} else if (!cfgId.equals(other.cfgId))
			return false;
		if (indexNo == null) {
			if (other.indexNo != null)
				return false;
		} else if (!indexNo.equals(other.indexNo))
			return false;
		if (dimNo == null) {
			if (other.dimNo != null)
				return false;
		} else if (!dimNo.equals(other.dimNo))
			return false;
		return true;
	}

}