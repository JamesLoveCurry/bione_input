package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Embeddable
public class RptInputIdxCfgValidatePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6492148130238453742L;

	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="RULE_ID")
	private String ruleId;
	
	public RptInputIdxCfgValidatePK(){
		
	}
	public String getCfgId() {
		return cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	public String getRuleId() {
		return ruleId;
	}


	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cfgId == null) ? 0 : cfgId.hashCode());
		result = prime * result + ((ruleId == null) ? 0 : ruleId.hashCode());
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
		RptInputIdxCfgValidatePK other = (RptInputIdxCfgValidatePK) obj;
		if (cfgId == null) {
			if (other.cfgId != null)
				return false;
		} else if (!cfgId.equals(other.cfgId))
			return false;
		if (ruleId == null) {
			if (other.ruleId != null)
				return false;
		} else if (!ruleId.equals(other.ruleId))
			return false;
		return true;
	}

}