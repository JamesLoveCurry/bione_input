package com.yusys.bione.plugin.businesslib.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RPT_MGR_BUSI_LIB_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_BUSI_LIB_INFO")
@NamedQuery(name="RptMgrBusiLibInfo.findAll", query="SELECT r FROM RptMgrBusiLibInfo r")
public class RptMgrBusiLibInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="BUSI_LIB_ID")
	private String busiLibId;
	
	@Column(name="BUSI_LIB_NO")
	private String busiLibNo;

	@Column(name="BUSI_LIB_NM")
	private String busiLibNm;

	@Column(name="DS_ID")
	private String dsId;

	@Column(name="SET_ID")
	private String setId;

	private String remark;

	public RptMgrBusiLibInfo() {
		
	}

	public String getBusiLibId() {
		return this.busiLibId;
	}

	public void setBusiLibId(String busiLibId) {
		this.busiLibId = busiLibId;
	}

	public String getBusiLibNm() {
		return this.busiLibNm;
	}

	public void setBusiLibNm(String busiLibNm) {
		this.busiLibNm = busiLibNm;
	}

	public String getBusiLibNo() {
		return this.busiLibNo;
	}

	public void setBusiLibNo(String busiLibNo) {
		this.busiLibNo = busiLibNo;
	}

	public String getDsId() {
		return this.dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSetId() {
		return this.setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

}