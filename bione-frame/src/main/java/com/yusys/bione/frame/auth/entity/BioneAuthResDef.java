package com.yusys.bione.frame.auth.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the BIONE_AUTH_RES_DEF database table.
 * 
 */
@Entity
@Table(name="BIONE_AUTH_RES_DEF")
public class BioneAuthResDef implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RES_DEF_NO")
	private String resDefNo;

	@Column(name="BEAN_NAME")
	private String beanName;

	@Column(name="IS_BUILTIN")
	private String isBuiltin;

	private String remark;

	@Column(name="RES_DEF_ORDER")
	private BigDecimal resDefOrder;

	@Column(name="RES_NAME")
	private String resName;

    public BioneAuthResDef() {
    }

	public String getResDefNo() {
		return this.resDefNo;
	}

	public void setResDefNo(String resDefNo) {
		this.resDefNo = resDefNo;
	}

	public String getBeanName() {
		return this.beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getIsBuiltin() {
		return this.isBuiltin;
	}

	public void setIsBuiltin(String isBuiltin) {
		this.isBuiltin = isBuiltin;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getResDefOrder() {
		return this.resDefOrder;
	}

	public void setResDefOrder(BigDecimal resDefOrder) {
		this.resDefOrder = resDefOrder;
	}

	public String getResName() {
		return this.resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

}