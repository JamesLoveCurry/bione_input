package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_IDX_CFG_DETAIL")
public class RptInputIdxCfgDetail implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 742169478056775115L;

	@Id
	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="ORDER_NUM")
	private String orderNum;

	@Column(name="ORG_MODE")
	private String orgMode;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="CFG_NM")
	private String cfgNm;
	
	@Column(name="INDEX_NM")
	private String indexNm;


    public RptInputIdxCfgDetail() {
    }



	public String getCfgId() {
		return cfgId;
	}



	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}





	public String getIndexNo() {
		return indexNo;
	}



	public String getIndexNm() {
		return indexNm;
	}



	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}



	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}



	public String getOrderNum() {
		return orderNum;
	}



	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}



	public String getOrgMode() {
		return orgMode;
	}



	public void setOrgMode(String orgMode) {
		this.orgMode = orgMode;
	}



	public String getTemplateId() {
		return templateId;
	}



	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}



	public String getCfgNm() {
		return cfgNm;
	}



	public void setCfgNm(String cfgNm) {
		this.cfgNm = cfgNm;
	}


}