package com.yusys.bione.frame.authobj.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * The persistent class for the BIONE_POSI_INFO database table.
 * 
 */
@Entity
@Table(name = "BIONE_POSI_INFO")
public class BionePosiInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "POSI_ID")
	private String posiId;

	@Column(name = "LAST_UPDATE_TIME")
	private Timestamp lastUpdateTime;

	@Column(name = "LAST_UPDATE_USER")
	private String lastUpdateUser;

	@Column(name = "LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name = "ORG_NO")
	private String orgNo;

	@Column(name = "POSI_NAME")
	private String posiName;

	@Column(name = "POSI_NO")
	private String posiNo;

	@Column(name = "POSI_STS")
	private String posiSts;

	private String remark;

	public BionePosiInfo() {
	}

	public String getPosiId() {
		return this.posiId;
	}

	public void setPosiId(String posiId) {
		this.posiId = posiId;
	}

	public Timestamp getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateUser() {
		return this.lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getOrgNo() {
		return this.orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getPosiName() {
		return this.posiName;
	}

	public void setPosiName(String posiName) {
		this.posiName = posiName;
	}

	public String getPosiNo() {
		return this.posiNo;
	}

	public void setPosiNo(String posiNo) {
		this.posiNo = posiNo;
	}

	public String getPosiSts() {
		return this.posiSts;
	}

	public void setPosiSts(String posiSts) {
		this.posiSts = posiSts;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}