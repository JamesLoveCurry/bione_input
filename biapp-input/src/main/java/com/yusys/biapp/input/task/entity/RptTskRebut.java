package com.yusys.biapp.input.task.entity;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="RPT_TSK_REBUT")
public class RptTskRebut implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="REBUT_ID", unique=true, nullable=false, length=32)
	private String rebutId;
	
	@Column(name="TASK_INSTANCE_ID", length=32)
	private String taskInstanceId;

	@Column(name="APPLY_DESC", length=500)
	private String applyDesc;

	@Column(name="APPLY_TIME")
	private Timestamp applyTime;

	@Column(name="APPLY_USER_NO", length=32)
	private String applyUserNo;

	@Column(name="COLLATE_TIME")
	private Timestamp collateTime;

	@Column(name="COLLATE_USER_NO", length=32)
	private String collateUserNo;

	@Column(name="REBUT_DESC", length=500)
	private String rebutDesc;

	@Column(length=1)
	private String sts;

    public RptTskRebut() {
    }

	public String getRebutId() {
		return rebutId;
	}

	public void setRebutId(String rebutId) {
		this.rebutId = rebutId;
	}

	public String getTaskInstanceId() {
		return this.taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getApplyDesc() {
		return this.applyDesc;
	}

	public void setApplyDesc(String applyDesc) {
		this.applyDesc = applyDesc;
	}

	public Timestamp getApplyTime() {
		return this.applyTime;
	}

	public void setApplyTime(Timestamp applyTime) {
		this.applyTime = applyTime;
	}

	public String getApplyUserNo() {
		return this.applyUserNo;
	}

	public void setApplyUserNo(String applyUserNo) {
		this.applyUserNo = applyUserNo;
	}

	public Timestamp getCollateTime() {
		return this.collateTime;
	}

	public void setCollateTime(Timestamp collateTime) {
		this.collateTime = collateTime;
	}

	public String getCollateUserNo() {
		return this.collateUserNo;
	}

	public void setCollateUserNo(String collateUserNo) {
		this.collateUserNo = collateUserNo;
	}

	public String getRebutDesc() {
		return this.rebutDesc;
	}

	public void setRebutDesc(String rebutDesc) {
		this.rebutDesc = rebutDesc;
	}

	public String getSts() {
		return this.sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

}