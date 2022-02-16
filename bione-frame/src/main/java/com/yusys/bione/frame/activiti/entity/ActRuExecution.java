package com.yusys.bione.frame.activiti.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the ACT_RU_EXECUTION database table.
 * 
 */
@Entity
@Table(name="ACT_RU_EXECUTION")
public class ActRuExecution implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID_")
	private String id;

	@Column(name="ACT_ID_")
	private String actId;

	@Column(name="BUSINESS_KEY_")
	private String businessKey;

	@Column(name="CACHED_ENT_STATE_")
	private BigDecimal cachedEntState;

	@Column(name="IS_ACTIVE_")
	private BigDecimal isActive;

	@Column(name="IS_CONCURRENT_")
	private BigDecimal isConcurrent;

	@Column(name="IS_EVENT_SCOPE_")
	private BigDecimal isEventScope;

	@Column(name="IS_SCOPE_")
	private BigDecimal isScope;

	@Column(name="LOCK_TIME_")
	private Timestamp lockTime;

	@Column(name="NAME_")
	private String name;

	@Column(name="PROC_DEF_ID_")
	private String procDefId;

	@Column(name="REV_")
	private BigDecimal rev;

	@Column(name="SUSPENSION_STATE_")
	private BigDecimal suspensionState;

	@Column(name="TENANT_ID_")
	private String tenantId;

	//bi-directional many-to-one association to ActRuExecution
	@ManyToOne
	@JoinColumn(name="SUPER_EXEC_")
	private ActRuExecution actRuExecution1;

	//bi-directional many-to-one association to ActRuExecution
	@OneToMany(mappedBy="actRuExecution1")
	private List<ActRuExecution> actRuExecutions1;

	//bi-directional many-to-one association to ActRuExecution
	@ManyToOne
	@JoinColumn(name="PROC_INST_ID_")
	private ActRuExecution actRuExecution2;

	//bi-directional many-to-one association to ActRuExecution
	@OneToMany(mappedBy="actRuExecution2")
	private List<ActRuExecution> actRuExecutions2;

	//bi-directional many-to-one association to ActRuExecution
	@ManyToOne
	@JoinColumn(name="PARENT_ID_")
	private ActRuExecution actRuExecution3;

	//bi-directional many-to-one association to ActRuExecution
	@OneToMany(mappedBy="actRuExecution3")
	private List<ActRuExecution> actRuExecutions3;

	public ActRuExecution() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActId() {
		return this.actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}

	public String getBusinessKey() {
		return this.businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public BigDecimal getCachedEntState() {
		return this.cachedEntState;
	}

	public void setCachedEntState(BigDecimal cachedEntState) {
		this.cachedEntState = cachedEntState;
	}

	public BigDecimal getIsActive() {
		return this.isActive;
	}

	public void setIsActive(BigDecimal isActive) {
		this.isActive = isActive;
	}

	public BigDecimal getIsConcurrent() {
		return this.isConcurrent;
	}

	public void setIsConcurrent(BigDecimal isConcurrent) {
		this.isConcurrent = isConcurrent;
	}

	public BigDecimal getIsEventScope() {
		return this.isEventScope;
	}

	public void setIsEventScope(BigDecimal isEventScope) {
		this.isEventScope = isEventScope;
	}

	public BigDecimal getIsScope() {
		return this.isScope;
	}

	public void setIsScope(BigDecimal isScope) {
		this.isScope = isScope;
	}

	public Timestamp getLockTime() {
		return this.lockTime;
	}

	public void setLockTime(Timestamp lockTime) {
		this.lockTime = lockTime;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProcDefId() {
		return this.procDefId;
	}

	public void setProcDefId(String procDefId) {
		this.procDefId = procDefId;
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

	public ActRuExecution getActRuExecution1() {
		return this.actRuExecution1;
	}

	public void setActRuExecution1(ActRuExecution actRuExecution1) {
		this.actRuExecution1 = actRuExecution1;
	}

	public List<ActRuExecution> getActRuExecutions1() {
		return this.actRuExecutions1;
	}

	public void setActRuExecutions1(List<ActRuExecution> actRuExecutions1) {
		this.actRuExecutions1 = actRuExecutions1;
	}

	public ActRuExecution addActRuExecutions1(ActRuExecution actRuExecutions1) {
		getActRuExecutions1().add(actRuExecutions1);
		actRuExecutions1.setActRuExecution1(this);

		return actRuExecutions1;
	}

	public ActRuExecution removeActRuExecutions1(ActRuExecution actRuExecutions1) {
		getActRuExecutions1().remove(actRuExecutions1);
		actRuExecutions1.setActRuExecution1(null);

		return actRuExecutions1;
	}

	public ActRuExecution getActRuExecution2() {
		return this.actRuExecution2;
	}

	public void setActRuExecution2(ActRuExecution actRuExecution2) {
		this.actRuExecution2 = actRuExecution2;
	}

	public List<ActRuExecution> getActRuExecutions2() {
		return this.actRuExecutions2;
	}

	public void setActRuExecutions2(List<ActRuExecution> actRuExecutions2) {
		this.actRuExecutions2 = actRuExecutions2;
	}

	public ActRuExecution addActRuExecutions2(ActRuExecution actRuExecutions2) {
		getActRuExecutions2().add(actRuExecutions2);
		actRuExecutions2.setActRuExecution2(this);

		return actRuExecutions2;
	}

	public ActRuExecution removeActRuExecutions2(ActRuExecution actRuExecutions2) {
		getActRuExecutions2().remove(actRuExecutions2);
		actRuExecutions2.setActRuExecution2(null);

		return actRuExecutions2;
	}

	public ActRuExecution getActRuExecution3() {
		return this.actRuExecution3;
	}

	public void setActRuExecution3(ActRuExecution actRuExecution3) {
		this.actRuExecution3 = actRuExecution3;
	}

	public List<ActRuExecution> getActRuExecutions3() {
		return this.actRuExecutions3;
	}

	public void setActRuExecutions3(List<ActRuExecution> actRuExecutions3) {
		this.actRuExecutions3 = actRuExecutions3;
	}

	public ActRuExecution addActRuExecutions3(ActRuExecution actRuExecutions3) {
		getActRuExecutions3().add(actRuExecutions3);
		actRuExecutions3.setActRuExecution3(this);

		return actRuExecutions3;
	}

	public ActRuExecution removeActRuExecutions3(ActRuExecution actRuExecutions3) {
		getActRuExecutions3().remove(actRuExecutions3);
		actRuExecutions3.setActRuExecution3(null);

		return actRuExecutions3;
	}

}