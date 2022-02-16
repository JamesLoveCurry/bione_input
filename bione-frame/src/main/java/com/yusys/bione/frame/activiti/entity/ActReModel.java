package com.yusys.bione.frame.activiti.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the ACT_RE_MODEL database table.
 * 
 */
@Entity
@Table(name="ACT_RE_MODEL")
public class ActReModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_")
	private String id;

	@Column(name="CATEGORY_")
	private String category;

	@Column(name="CREATE_TIME_")
	private Timestamp createTime;

	@Column(name="DEPLOYMENT_ID_")
	private String deploymentId;

	@Column(name="EDITOR_SOURCE_EXTRA_VALUE_ID_")
	private String editorSourceExtraValueId;

	@Column(name="EDITOR_SOURCE_VALUE_ID_")
	private String editorSourceValueId;

	@Column(name="KEY_")
	private String key;

	@Column(name="LAST_UPDATE_TIME_")
	private Timestamp lastUpdateTime;

	@Column(name="META_INFO_")
	private String metaInfo;

	@Column(name="NAME_")
	private String name;

	@Column(name="REV_")
	private BigDecimal rev;

	@Column(name="TENANT_ID_")
	private String tenantId;

	@Column(name="VERSION_")
	private BigDecimal version;

	public ActReModel() {
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

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getDeploymentId() {
		return this.deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getEditorSourceExtraValueId() {
		return this.editorSourceExtraValueId;
	}

	public void setEditorSourceExtraValueId(String editorSourceExtraValueId) {
		this.editorSourceExtraValueId = editorSourceExtraValueId;
	}

	public String getEditorSourceValueId() {
		return this.editorSourceValueId;
	}

	public void setEditorSourceValueId(String editorSourceValueId) {
		this.editorSourceValueId = editorSourceValueId;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Timestamp getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getMetaInfo() {
		return this.metaInfo;
	}

	public void setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
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