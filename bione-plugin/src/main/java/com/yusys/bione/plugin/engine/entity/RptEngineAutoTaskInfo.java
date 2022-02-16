package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ENGINE_AUTO_TASK_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_ENGINE_AUTO_TASK_INFO")
@NamedQuery(name="RptEngineAutoTaskInfo.findAll", query="SELECT r FROM RptEngineAutoTaskInfo r")
public class RptEngineAutoTaskInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptEngineAutoTaskInfoPK id;

	@Column(name="END_TIME")
	private Timestamp endTime;

	@Column(name="RETRY_TIMES")
	private BigDecimal retryTimes;

	@Column(name="START_TIME")
	private Timestamp startTime;

	private String sts;

	@Column(name="TYPE")
	private String type;

	public RptEngineAutoTaskInfo() {
	}

	public RptEngineAutoTaskInfoPK getId() {
		return this.id;
	}

	public void setId(RptEngineAutoTaskInfoPK id) {
		this.id = id;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getRetryTimes() {
		return this.retryTimes;
	}

	public void setRetryTimes(BigDecimal retryTimes) {
		this.retryTimes = retryTimes;
	}

	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public String getSts() {
		return this.sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}