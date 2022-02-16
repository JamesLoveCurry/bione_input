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
@Table(name = "RPT_INPUT_IDX_DIM_FILTER")
public class RptInputIdxDimFilter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7949436640661534788L;

	@EmbeddedId
	private RptInputIdxDimFilterPK id;

	@Column(name = "FILTER_MODE")
	private String filterMode;

	@Column(name = "FILTER_VAL")
	private String filterVal;

	@Column(name = "TEMPLATE_ID")
	private String templateId;

	@Column(name = "FILTER_EXPLAIN")
	private String filterExplain;
	
	@Column(name = "DIM_NM")
	private String dimNm;;
	
	public RptInputIdxDimFilter() {
	}

	public RptInputIdxDimFilterPK getId() {
		return id;
	}

	public void setId(RptInputIdxDimFilterPK id) {
		this.id = id;
	}

	public String getFilterMode() {
		return filterMode;
	}

	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}

	public String getFilterVal() {
		return filterVal;
	}

	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getFilterExplain() {
		return filterExplain;
	}

	public void setFilterExplain(String filterExplain) {
		this.filterExplain = filterExplain;
	}

	public String getDimNm() {
		return dimNm;
	}

	public void setDimNm(String dimNm) {
		this.dimNm = dimNm;
	}

}