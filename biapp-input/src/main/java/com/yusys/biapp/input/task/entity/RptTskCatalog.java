package com.yusys.biapp.input.task.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="RPT_TSK_CATALOG")
public class RptTskCatalog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8009600586447901904L;

	/**
	 * 
	 */

	@Id
	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="CATALOG_NM")
	private String catalogNm;
	
	@Column(name="REMARK")
	private String remark;
	
	
	@Column(name="UP_NO")
	private String upNo;
	

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogNm() {
		return catalogNm;
	}

	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpNo() {
		return upNo;
	}

	public void setUpNo(String upNo) {
		this.upNo = upNo;
	}
	
	
	
	
}
