package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_IDX_INFO database table.
 * 
 */
@Embeddable
public class RptIdxCfgPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="TEMPLATE_ID", unique=true, nullable=false, precision=32)
	private String templateId;
	
	@Column(name="VER_ID", unique=true, nullable=false, precision=18)
	private BigDecimal verId;
	
	@Column(name="CELL_NO", unique=true, nullable=false, length=32)
	private String cellNo;

	@Column(name="INDEX_NO", unique=true, nullable=false, length=32)
	private String indexNo;
	
    public RptIdxCfgPK() {
    }

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public BigDecimal getVerId() {
		return verId;
	}

	public void setVerId(BigDecimal verId) {
		this.verId = verId;
	}

	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
}