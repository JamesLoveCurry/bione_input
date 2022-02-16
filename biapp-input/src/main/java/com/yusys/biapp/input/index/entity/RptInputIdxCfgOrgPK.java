package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Embeddable
public class RptInputIdxCfgOrgPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6492148130238453742L;

	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="ORG_NO")
	private String orgNo;
	
	public RptInputIdxCfgOrgPK(){
		
	}


	public String getCfgId() {
		return cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cfgId == null) ? 0 : cfgId.hashCode());
		result = prime * result + ((orgNo == null) ? 0 : orgNo.hashCode());
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
		RptInputIdxCfgOrgPK other = (RptInputIdxCfgOrgPK) obj;
		if (cfgId == null) {
			if (other.cfgId != null)
				return false;
		} else if (!cfgId.equals(other.cfgId))
			return false;
		if (orgNo == null) {
			if (other.orgNo != null)
				return false;
		} else if (!orgNo.equals(other.orgNo))
			return false;
		return true;
	}

}