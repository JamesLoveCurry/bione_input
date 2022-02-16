package com.yusys.bione.plugin.modeltrans.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_SYS_MODULE_COL_TRANS database table.
 * 
 */
@Entity
@Table(name="RPT_SYS_MODULE_COL_TRANS")
@NamedQuery(name="RptSysModuleColTran.findAll", query="SELECT r FROM RptSysModuleColTran r")
public class RptSysModuleColTran implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptSysModuleColTranPK id;

	private String remark;

	@Column(name="SRC_COL_ID")
	private String srcColId;

	@Column(name="TRANS_RULE")
	private String transRule;

	public RptSysModuleColTran() {
	}

	public RptSysModuleColTranPK getId() {
		return this.id;
	}

	public void setId(RptSysModuleColTranPK id) {
		this.id = id;
	}


	public String getSrcColId() {
		return this.srcColId;
	}

	public void setSrcColId(String srcColId) {
		this.srcColId = srcColId;
	}

	public String getTransRule() {
		return this.transRule;
	}

	public void setTransRule(String transRule) {
		this.transRule = transRule;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}