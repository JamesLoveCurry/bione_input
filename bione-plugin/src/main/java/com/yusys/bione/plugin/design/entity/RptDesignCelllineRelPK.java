package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_DESIGN_CELLLINE_REL database table.
 * 
 */
@Embeddable
public class RptDesignCelllineRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CELL_NO")
	private String cellNo;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="VER_ID")
	private BigDecimal verId;

	@Column(name="BUSI_LINE_ID")
	private String busiLineId;

    public RptDesignCelllineRelPK() {
    }
	
	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	public String getTemplateId() {
		return this.templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return 返回 verId。
	 */
	public BigDecimal getVerId() {
		return verId;
	}

	/**
	 * @param verId 设置 verId。
	 */
	public void setVerId(BigDecimal verId) {
		this.verId = verId;
	}

	public String getBusiLineId() {
		return this.busiLineId;
	}
	public void setBusiLineId(String busiLineId) {
		this.busiLineId = busiLineId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((busiLineId == null) ? 0 : busiLineId.hashCode());
		result = prime * result + ((cellNo == null) ? 0 : cellNo.hashCode());
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
		result = prime * result + ((verId == null) ? 0 : verId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptDesignCelllineRelPK other = (RptDesignCelllineRelPK) obj;
		if (busiLineId == null) {
			if (other.busiLineId != null)
				return false;
		} else if (!busiLineId.equals(other.busiLineId))
			return false;
		if (cellNo == null) {
			if (other.cellNo != null)
				return false;
		} else if (!cellNo.equals(other.cellNo))
			return false;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		if (verId == null) {
			if (other.verId != null)
				return false;
		} else if (!verId.equals(other.verId))
			return false;
		return true;
	}
    
	
}