package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_MGR_FRS_LINEFILL_CFG database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_FRS_LINEFILL_CFG")
public class RptMgrFrsLinefillCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptMgrFrsLinefillCfgPK id;

	@Column(name="FILLRPT_A_USER_ID")
	private String fillrptAUserId;

	@Column(name="FILLRPT_B_USER_ID")
	private String fillrptBUserId;

    public RptMgrFrsLinefillCfg() {
    }

	public RptMgrFrsLinefillCfgPK getId() {
		return this.id;
	}

	public void setId(RptMgrFrsLinefillCfgPK id) {
		this.id = id;
	}
	
	public String getFillrptAUserId() {
		return this.fillrptAUserId;
	}

	public void setFillrptAUserId(String fillrptAUserId) {
		this.fillrptAUserId = fillrptAUserId;
	}

	public String getFillrptBUserId() {
		return this.fillrptBUserId;
	}

	public void setFillrptBUserId(String fillrptBUserId) {
		this.fillrptBUserId = fillrptBUserId;
	}

}