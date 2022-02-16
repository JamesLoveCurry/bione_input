package com.yusys.bione.frame.authobj.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the BIONE_ROLE_INFO_EXT database table.
 * 
 */
@Entity
@Table(name="BIONE_ROLE_INFO_EXT")
@NamedQuery(name="BioneRoleInfoExt.findAll", query="SELECT b FROM BioneRoleInfoExt b")
public class BioneRoleInfoExt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CODE")
	private String code;

	@Column(name="CODE_DESC")
	private String codeDesc;

	public BioneRoleInfoExt() {
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeDesc() {
		return this.codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

}