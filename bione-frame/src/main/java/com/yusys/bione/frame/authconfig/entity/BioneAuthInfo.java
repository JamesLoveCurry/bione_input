package com.yusys.bione.frame.authconfig.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the BIONE_AUTH_INFO database table.
 * 
 */
@Entity
@Table(name = "BIONE_AUTH_INFO")
public class BioneAuthInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "AUTH_TYPE_NO", unique = true, nullable = false, length = 32)
	private String authTypeNo;

	@Column(name = "AUTH_TYPE_NAME", length = 100)
	private String authTypeName;

	@Column(name = "BEAN_NAME", length = 100)
	private String beanName;

	@Column(name = "IS_BUILTIN", length = 1)
	private String isBuiltin;

	@Column(name = "IS_SSO", length = 1)
	private String isSso;

	@Column(length = 500)
	private String remark;

	public BioneAuthInfo() {
	}

	public String getAuthTypeNo() {
		return this.authTypeNo;
	}

	public void setAuthTypeNo(String authTypeNo) {
		this.authTypeNo = authTypeNo;
	}

	public String getAuthTypeName() {
		return this.authTypeName;
	}

	public void setAuthTypeName(String authTypeName) {
		this.authTypeName = authTypeName;
	}

	public String getBeanName() {
		return this.beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIsBuiltin() {
		return isBuiltin;
	}

	public void setIsBuiltin(String isBuiltin) {
		this.isBuiltin = isBuiltin;
	}

	public String getIsSso() {
		return isSso;
	}

	public void setIsSso(String isSso) {
		this.isSso = isSso;
	}

}