package com.yusys.bione.frame.authres.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_FUNC_METHODURL_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_FUNC_METHODURL_INFO")
public class BioneFuncMethodurlInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RID", unique=true, nullable=false, length=32)
	private String rId;

	@Column(name="FUNC_ID", length=32)
	private String funcId;
	
	@Column(name="MENU_ID", length=32)
	private String menuId;
	
	@Column(name="FUNC_NAME", length=200)
	private String funcName;

	@Column(name="NAV_PATH", nullable=false, length=200)
	private String navPath;
	
	@Column(name="METHOD_NAME", length=200)
	private String moduleName;
	
	@Column(name="METHOD_STS", nullable=false, length=5)
	private String methodSts;

	@Column(name="METHOD_URL", nullable=false, length=200)
	private String moduleUrl;

	@Column(length=300)
	private String remark;

	public String getrId() {
		return rId;
	}

	public void setrId(String rId) {
		this.rId = rId;
	}

	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getNavPath() {
		return navPath;
	}

	public void setNavPath(String navPath) {
		this.navPath = navPath;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getMethodSts() {
		return methodSts;
	}

	public void setMethodSts(String methodSts) {
		this.methodSts = methodSts;
	}

	public String getModuleUrl() {
		return moduleUrl;
	}

	public void setModuleUrl(String moduleUrl) {
		this.moduleUrl = moduleUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
}