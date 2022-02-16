package com.yusys.bione.plugin.datamodel.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_SYS_MODULE_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_SYS_MODULE_INFO")
public class RptSysModuleInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SET_ID")
	private String setId;

	@Column(name="CATALOG_ID")
	private String catalogId;

	private String remark;

	@Column(name="SET_NM")
	private String setNm;

	@Column(name="SOURCE_ID")
	private String sourceId;

	@Column(name="TABLE_EN_NM")
	private String tableEnNm;

	@Column(name="SET_TYPE")
	private String setType;
	
	@Column(name="SRC_DATA_FILTER_COND")
	private String srcDataFilterCond;
	
	@Column(name="BUSI_TYPE")
	private String busiType;
	
	@Column(name="DISPLAY_TABLE_NAME")
	private String displayTableName;
	
	@Column(name="IS_SPLIT")
	private String isSplit;
	
	@Column(name="PARENT_SET_ID")
	private String parentSetId;
	
	@Column(name="ORDER_NO")
	private BigDecimal orderNo;
	
	@Column(name="SYSTEM_NO")
	private String systemNo;
	
	public RptSysModuleInfo() {
	}
	
    public String getSrcDataFilterCond() {
		return srcDataFilterCond;
	}

	public void setSrcDataFilterCond(String srcDataFilterCond) {
		this.srcDataFilterCond = srcDataFilterCond;
	}


	public String getSetId() {
		return this.setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSetNm() {
		return this.setNm;
	}

	public void setSetNm(String setNm) {
		this.setNm = setNm;
	}

	public String getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getTableEnNm() {
		return tableEnNm;
	}

	public void setTableEnNm(String tableEnNm) {
		this.tableEnNm = tableEnNm;
	}

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}
	
	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	
	public String getDisplayTableName() {
		return displayTableName;
	}

	public void setDisplayTableName(String displayTableName) {
		this.displayTableName = displayTableName;
	}

	public String getIsSplit() {
		return isSplit;
	}

	public void setIsSplit(String isSplit) {
		this.isSplit = isSplit;
	}

	public String getParentSetId() {
		return parentSetId;
	}

	public void setParentSetId(String parentSetId) {
		this.parentSetId = parentSetId;
	}

	public BigDecimal getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(BigDecimal orderNo) {
		this.orderNo = orderNo;
	}

	public String getSystemNo() {
		return systemNo;
	}

	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((setNm == null) ? 0 : setNm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptSysModuleInfo other = (RptSysModuleInfo) obj;
		if (setNm == null) {
			if (other.setNm != null)
				return false;
		} else if (!setNm.equals(other.setNm))
			return false;
		return true;
	}
}