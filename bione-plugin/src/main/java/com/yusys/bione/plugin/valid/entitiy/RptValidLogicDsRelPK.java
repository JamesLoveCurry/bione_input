package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the RPT_VALID_LOGIC_IDX_REL database table.
 * 
 */
@Embeddable
public class RptValidLogicDsRelPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="COLUMN_ID")
	private String columnId;

	@Column(name="CHECK_ID")
	private String checkId;

	@Column(name="TEMPLATE_ID")
	private String templateId;

	@Column(name="FORMULA_TYPE")
	private String formulaType;

	@Column(name="DIM_FILTER")
	private String dimFilter;

	@Column(name="COLUMN_TYPE")
	private String columnType;
	
    public RptValidLogicDsRelPK() {
    }
	public String getCheckId() {
		return this.checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getTemplateId() {
		return this.templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getFormulaType() {
		return this.formulaType;
	}
	public void setFormulaType(String formulaType) {
		this.formulaType = formulaType;
	}

	public String getColumnId() {
		return columnId;
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((checkId == null) ? 0 : checkId.hashCode());
		result = prime * result
				+ ((columnId == null) ? 0 : columnId.hashCode());
		result = prime * result
				+ ((formulaType == null) ? 0 : formulaType.hashCode());
		result = prime * result
				+ ((templateId == null) ? 0 : templateId.hashCode());
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
		RptValidLogicDsRelPK other = (RptValidLogicDsRelPK) obj;
		if (checkId == null) {
			if (other.checkId != null)
				return false;
		} else if (!checkId.equals(other.checkId))
			return false;
		if (columnId == null) {
			if (other.columnId != null)
				return false;
		} else if (!columnId.equals(other.columnId))
			return false;
		if (formulaType == null) {
			if (other.formulaType != null)
				return false;
		} else if (!formulaType.equals(other.formulaType))
			return false;
		if (templateId == null) {
			if (other.templateId != null)
				return false;
		} else if (!templateId.equals(other.templateId))
			return false;
		return true;
	}
	public String getDimFilter() {
		return dimFilter;
	}
	public void setDimFilter(String dimFilter) {
		this.dimFilter = dimFilter;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	
	
}