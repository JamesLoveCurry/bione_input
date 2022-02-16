package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_REWRITE_FIELD_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_REWRITE_FIELD_INFO")
public class RptInputRewriteFieldInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptInputRewriteFieldInfoPK id;

	@Column(name="FIELD_NAME")
	private String fieldName;

	@Column(name="IS_ID")
	private String isId;

	@Column(name="UPDATE_FIELD_NAME")
	private String updateFieldName;
	
	@Column(name="FIELD_TYPE")
	private String fieldType;

	@Column(name="UPDATE_FIELD_TYPE")
	private String updateFieldType;
	

    public RptInputRewriteFieldInfo() {
    }

	public RptInputRewriteFieldInfoPK getId() {
		return this.id;
	}

	public void setId(RptInputRewriteFieldInfoPK id) {
		this.id = id;
	}
	
	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getIsId() {
		return this.isId;
	}

	public void setIsId(String isId) {
		this.isId = isId;
	}

	public String getUpdateFieldName() {
		return this.updateFieldName;
	}

	public void setUpdateFieldName(String updateFieldName) {
		this.updateFieldName = updateFieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getUpdateFieldType() {
		return updateFieldType;
	}

	public void setUpdateFieldType(String updateFieldType) {
		this.updateFieldType = updateFieldType;
	}

}