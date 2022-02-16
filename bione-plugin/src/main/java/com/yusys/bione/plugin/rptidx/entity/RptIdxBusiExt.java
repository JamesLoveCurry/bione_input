package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the RPT_IDX_BUSI_EXT database table.
 * 
 */
@Entity
@Table(name = "RPT_IDX_BUSI_EXT")
public class RptIdxBusiExt implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptIdxBusiExtPK id;

	@Column(name = "INDEX_USUAL_NM", nullable = false, length = 500)
	private String indexUsualNm;

	@Column(name = "BUSI_DEF")
	private String busiDef;

	@Column(name = "BUSI_RULE")
	private String busiRule;

	@Column(name = "DEF_DEPT", length = 500)
	private String defDept;

	@Column(name = "USE_DEPT", length = 500)
	private String useDept;

	@Column(name = "BUSI_MODEL")
	private String busiModel;

	public RptIdxBusiExt() {
	}

	public RptIdxBusiExtPK getId() {
		return id;
	}

	public void setId(RptIdxBusiExtPK id) {
		this.id = id;
	}

	public String getIndexUsualNm() {
		return indexUsualNm;
	}

	public void setIndexUsualNm(String indexUsualNm) {
		this.indexUsualNm = indexUsualNm;
	}

	public String getBusiDef() {
		return busiDef;
	}

	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	}

	public String getBusiRule() {
		return busiRule;
	}

	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}

	public String getDefDept() {
		return defDept;
	}

	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	public String getUseDept() {
		return useDept;
	}

	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}

	public String getBusiModel() {
		return busiModel;
	}

	public void setBusiModel(String busiModel) {
		this.busiModel = busiModel;
	}

}