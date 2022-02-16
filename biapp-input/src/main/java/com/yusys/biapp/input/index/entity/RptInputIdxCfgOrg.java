package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_IDX_CFG_ORG")
public class RptInputIdxCfgOrg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4284738127631818345L;
	@EmbeddedId
	private RptInputIdxCfgOrgPK id;

	@Column(name = "ORG_NM")
	private String orgNm;

	public RptInputIdxCfgOrgPK getId() {
		return id;
	}

	public void setId(RptInputIdxCfgOrgPK id) {
		this.id = id;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}


}