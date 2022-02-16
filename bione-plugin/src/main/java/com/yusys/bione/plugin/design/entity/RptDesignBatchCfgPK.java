package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the RPT_DESIGN_SOURCE_IDX database table.
 * 
 */
@Embeddable
public class RptDesignBatchCfgPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="VER_ID")
	private BigDecimal verId;

	/**
	 * @return 返回 cfgId。
	 */
	public String getCfgId() {
		return cfgId;
	}

	/**
	 * @param cfgId 设置 cfgId。
	 */
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	/**
	 * @return 返回 templateId。
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId 设置 templateId。
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cfgId == null) ? 0 : cfgId.hashCode());
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
		RptDesignBatchCfgPK other = (RptDesignBatchCfgPK) obj;
		if (cfgId == null) {
			if (other.cfgId != null)
				return false;
		} else if (!cfgId.equals(other.cfgId))
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