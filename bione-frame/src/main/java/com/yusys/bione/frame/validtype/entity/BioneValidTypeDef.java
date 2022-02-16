package com.yusys.bione.frame.validtype.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the BIONE_VALID_TYPE_DEF database table.
 * 
 */
@Entity
@Table(name="BIONE_VALID_TYPE_DEF")
@NamedQuery(name="BioneValidTypeDef.findAll", query="SELECT b FROM BioneValidTypeDef b")
public class BioneValidTypeDef implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="OBJ_DEF_NO")
	private String objDefNo;

	@Column(name="BEAN_NAME")
	private String beanName;

	@Column(name="OBJ_DEF_NAME")
	private String objDefName;

	@Column(name="OBJ_DEF_ORDER")
	private BigDecimal objDefOrder;

	private String remark;

	public BioneValidTypeDef() {
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