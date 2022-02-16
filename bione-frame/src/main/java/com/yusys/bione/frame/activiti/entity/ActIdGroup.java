package com.yusys.bione.frame.activiti.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the ACT_ID_GROUP database table.
 * 
 */
@Entity
@Table(name="ACT_ID_GROUP")
public class ActIdGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_")
	private String id;

	@Column(name="NAME_")
	private String name;

	@Column(name="REV_")
	private BigDecimal rev;

	@Column(name="TYPE_")
	private String type;

	public ActIdGroup() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getRev() {
		return this.rev;
	}

	public void setRev(BigDecimal rev) {
		this.rev = rev;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}