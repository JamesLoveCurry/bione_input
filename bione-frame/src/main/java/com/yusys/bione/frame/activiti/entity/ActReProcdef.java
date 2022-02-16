package com.yusys.bione.frame.activiti.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the ACT_RE_PROCDEF database table.
 * 
 */
@Entity
@Table(name="ACT_RE_PROCDEF")
public class ActReProcdef implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_")
	private String id;

	@Column(name="CATEGORY_")
	private String category;

	@Column(name="DEPLOYMENT_ID_")
	private String deploymentId;

	@Column(name="DESCRIPTION_")
	private String description;

	@Column(name="DGRM_RESOURCE_NAME_")
	private String dgrmResourceName;

	@Column(name="HAS_GRAPHICAL_NOTATION_")
	private BigDecimal hasGraphicalNotation;

	@Column(name="HAS_START_FORM_KEY_")
	private BigDecimal hasStartFormKey;

	@Column(name="KEY_")
	private String key;

	@Column(name="NAME_")
	private String name;

	@Column(name="RESOURCE_NAME_")
	private String resourceName;

	@Column(name="REV_")
	private BigDecimal rev;

	@Column(name="SUSPENSION_STATE_")
	private BigDecimal suspensionState;

	@Column(name="TENANT_ID_")
	private String tenantId;

	@Column(name="VERSION_")
	private BigDecimal version;

	public ActReProcdef() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDeploymentId() {
		return this.deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDgrmResourceName() {
		return this.dgrmResourceName;
	}

	public void setDgrmResourceName(String dgrmResourceName) {
		this.dgrmResourceName = dgrmResourceName;
	}

	public BigDecimal getHasGraphicalNotation() {
		return this.hasGraphicalNotation;
	}

	public void setHasGraphicalNotation(BigDecimal hasGraphicalNotation) {
		this.hasGraphicalNotation = hasGraphicalNotation;
	}

	public BigDecimal getHasStartFormKey() {
		return this.hasStartFormKey;
	}

	public void setHasStartFormKey(BigDecimal hasStartFormKey) {
		this.hasStartFormKey = hasStartFormKey;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public BigDecimal getRev() {
		return this.rev;
	}

	public void setRev(BigDecimal rev) {
		this.rev = rev;
	}

	public BigDecimal getSuspensionState() {
		return this.suspensionState;
	}

	public void setSuspensionState(BigDecimal suspensionState) {
		this.suspensionState = suspensionState;
	}

	public String getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public BigDecimal getVersion() {
		return this.version;
	}

	public void setVersion(BigDecimal version) {
		this.version = version;
	}

}