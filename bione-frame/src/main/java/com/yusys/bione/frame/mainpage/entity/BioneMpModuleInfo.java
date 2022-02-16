package com.yusys.bione.frame.mainpage.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MP_MODULE_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_MP_MODULE_INFO")
public class BioneMpModuleInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MODULE_ID")
	private String moduleId;
	
	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name="LABEL_PATH")
	private String labelPath;

	@Column(name="MODULE_NAME")
	private String moduleName;

	@Column(name="MODULE_PATH")
	private String modulePath;

	@Column(name="PIC_PATH")
	private String picPath;

	private String remark;
	
	@Column(name="MODULE_TYPE")
	private String moduleType;
	
	@Column(name="CHART_TYPE")
	private String chartType;
	
	@Column(name="MODULE_DEFINE")
	private String moduleDefine;

    public BioneMpModuleInfo() {
    }

	public String getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getLabelPath() {
		return this.labelPath;
	}

	public void setLabelPath(String labelPath) {
		this.labelPath = labelPath;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModulePath() {
		return this.modulePath;
	}

	public void setModulePath(String modulePath) {
		this.modulePath = modulePath;
	}

	public String getPicPath() {
		return this.picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLogicSysNo() {
		return logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getModuleDefine() {
		return moduleDefine;
	}

	public void setModuleDefine(String moduleDefine) {
		this.moduleDefine = moduleDefine;
	}
}