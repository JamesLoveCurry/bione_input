package com.yusys.bione.plugin.rptbank.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_THEME_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_THEME_INFO")
@NamedQuery(name="RptIdxThemeInfo.findAll", query="SELECT r FROM RptIdxThemeInfo r")
public class RptIdxThemeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="THEME_ID")
	private String themeId;

	@Column(name="IS_DEFAULT")
	private String isDefault;

	
	private String remark;

	@Column(name="THEME_NM")
	private String themeNm;

	@Column(name="THEME_NO")
	private String themeNo;

	public RptIdxThemeInfo() {
	}

	public String getThemeId() {
		return this.themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public String getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getThemeNm() {
		return this.themeNm;
	}

	public void setThemeNm(String themeNm) {
		this.themeNm = themeNm;
	}

	public String getThemeNo() {
		return this.themeNo;
	}

	public void setThemeNo(String themeNo) {
		this.themeNo = themeNo;
	}

}