package com.yusys.bione.frame.auth.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the BIONE_AUTH_OBJ_DEF database table.
 *   
 */
@Entity
@Table(name="BIONE_AUTH_OBJ_DEF")
public class BioneAuthObjDef implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="OBJ_DEF_NO")
	private String objDefNo;

	@Column(name="BEAN_NAME")
	private String beanName;

	@Column(name="IS_BUILTIN")
	private String isBuiltin;

	@Column(name="OBJ_DEF_NAME")
	private String objDefName;

	@Column(name="OBJ_DEF_ORDER")
	private BigDecimal objDefOrder;

	private String remark;

    public BioneAuthObjDef() {
    }

	public String getObjDefNo() {
		return this.objDefNo;
	}

	public void setObjDefNo(String objDefNo) {
		this.objDefNo = objDefNo;
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

	public String getObjDefName() {
		return this.objDefName;
	}

	public void setObjDefName(String objDefName) {
		this.objDefName = objDefName;
	}

	public BigDecimal getObjDefOrder() {
		return this.objDefOrder;
	}

	public void setObjDefOrder(BigDecimal objDefOrder) {
		this.objDefOrder = objDefOrder;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}