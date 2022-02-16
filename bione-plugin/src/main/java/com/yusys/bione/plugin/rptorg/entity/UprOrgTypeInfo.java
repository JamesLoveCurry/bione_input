package com.yusys.bione.plugin.rptorg.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the UPR_ORG_TYPE_INFO database table.
 * 
 */
@Entity
@Table(name="UPR_ORG_TYPE_INFO")
@NamedQuery(name="UprOrgTypeInfo.findAll", query="SELECT u FROM UprOrgTypeInfo u")
public class UprOrgTypeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private UprOrgTypeInfoPK id;

	@Column(name="END_DT")
	private String endDt;

	@Column(name="START_DT")
	private String startDt;

	@Column(name="TYPE_NAME")
	private String typeName;

	public UprOrgTypeInfo() {
	}

	public UprOrgTypeInfoPK getId() {
		return this.id;
	}

	public void setId(UprOrgTypeInfoPK id) {
		this.id = id;
	}

	public String getEndDt() {
		return this.endDt;
	}

	public void setEndDt(String endDt) {
		this.endDt = endDt;
	}

	public String getStartDt() {
		return this.startDt;
	}

	public void setStartDt(String startDt) {
		this.startDt = startDt;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}