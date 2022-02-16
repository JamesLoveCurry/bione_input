package com.yusys.biapp.input.task.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_ins database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_ins")
public class RptTskIns implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_INSTANCE_ID")
	private String taskInstanceId;

	@Column(name="DATA_DATE")
	private String dataDate;

	@Column(name="END_TIME")
	private Timestamp endTime;

	@Column(name="EXE_OBJ_ID")
	private String exeObjId;

	@Column(name="START_TIME")
	private Timestamp startTime;

	private String sts;
	
	@Column(name="IS_UPDATE")
	private String isUpdate;
	
	@Column(name="IS_CHECK")
	private String isCheck;

	@Column(name="TASK_ID")
	private String taskId;

	@Column(name="TASK_NM")
	private String taskNm;

	@Column(name="TASK_NODE_INSTANCE_ID")
	private String taskNodeInstanceId;

	@Column(name="TASK_OBJ_ID")
	private String taskObjId;

	@Column(name="TASK_TYPE")
	private String taskType;

	@Column(name="UP_TASK_INSTANCE_ID")
	private String upTaskInstanceId;

	@Column(name="LOGIC_DEL_NO")
	private String logicDelNo;
	
	@Column(name="LINE_ID")
	private String lineId;
	
	@Column(name="ORG_NO")
	private String orgNo;

	@Column(name="CREATE_USER")
	private String createUser;

	@Column(name="CREATE_ORG")
	private String createOrg;

	@Column(name="TASK_TITLE")
	private String taskTitle;
	
	@Column(name="LOAD_DATA_MARK")
	private String loadDataMark;//是否加载预增数据
	
	
    public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public RptTskIns() {
    }

	public String getTaskInstanceId() {
		return this.taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getDataDate() {
		return this.dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getExeObjId() {
		return this.exeObjId;
	}

	public void setExeObjId(String exeObjId) {
		this.exeObjId = exeObjId;
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

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskNm() {
		return this.taskNm;
	}

	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}

	public String getTaskNodeInstanceId() {
		return this.taskNodeInstanceId;
	}

	public void setTaskNodeInstanceId(String taskNodeInstanceId) {
		this.taskNodeInstanceId = taskNodeInstanceId;
	}

	public String getTaskObjId() {
		return this.taskObjId;
	}

	public void setTaskObjId(String taskObjId) {
		this.taskObjId = taskObjId;
	}

	public String getTaskType() {
		return this.taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getUpTaskInstanceId() {
		return this.upTaskInstanceId;
	}

	public void setUpTaskInstanceId(String upTaskInstanceId) {
		this.upTaskInstanceId = upTaskInstanceId;
	}

	public String getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}

	public String getLogicDelNo() {
		return logicDelNo;
	}

	public void setLogicDelNo(String logicDelNo) {
		this.logicDelNo = logicDelNo;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateOrg() {
		return createOrg;
	}

	public void setCreateOrg(String createOrg) {
		this.createOrg = createOrg;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getLoadDataMark() {
		return loadDataMark;
	}

	public void setLoadDataMark(String loadDataMark) {
		this.loadDataMark = loadDataMark;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataDate == null) ? 0 : dataDate.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((exeObjId == null) ? 0 : exeObjId.hashCode());
		result = prime * result + ((isCheck == null) ? 0 : isCheck.hashCode());
		result = prime * result + ((isUpdate == null) ? 0 : isUpdate.hashCode());
		result = prime * result
				+ ((logicDelNo == null) ? 0 : logicDelNo.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((sts == null) ? 0 : sts.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result
				+ ((taskInstanceId == null) ? 0 : taskInstanceId.hashCode());
		result = prime * result + ((taskNm == null) ? 0 : taskNm.hashCode());
		result = prime
				* result
				+ ((taskNodeInstanceId == null) ? 0 : taskNodeInstanceId
						.hashCode());
		result = prime * result
				+ ((taskObjId == null) ? 0 : taskObjId.hashCode());
		result = prime * result
				+ ((taskType == null) ? 0 : taskType.hashCode());
		result = prime
				* result
				+ ((upTaskInstanceId == null) ? 0 : upTaskInstanceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptTskIns other = (RptTskIns) obj;
		if (dataDate == null) {
			if (other.dataDate != null)
				return false;
		} else if (!dataDate.equals(other.dataDate))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (exeObjId == null) {
			if (other.exeObjId != null)
				return false;
		} else if (!exeObjId.equals(other.exeObjId))
			return false;
		if (isCheck == null) {
			if (other.isCheck != null)
				return false;
		} else if (!isCheck.equals(other.isCheck))
			return false;
		if (isUpdate == null) {
			if (other.isUpdate != null)
				return false;
		} else if (!isUpdate.equals(other.isUpdate))
			return false;
		if (logicDelNo == null) {
			if (other.logicDelNo != null)
				return false;
		} else if (!logicDelNo.equals(other.logicDelNo))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (sts == null) {
			if (other.sts != null)
				return false;
		} else if (!sts.equals(other.sts))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (taskInstanceId == null) {
			if (other.taskInstanceId != null)
				return false;
		} else if (!taskInstanceId.equals(other.taskInstanceId))
			return false;
		if (taskNm == null) {
			if (other.taskNm != null)
				return false;
		} else if (!taskNm.equals(other.taskNm))
			return false;
		if (taskNodeInstanceId == null) {
			if (other.taskNodeInstanceId != null)
				return false;
		} else if (!taskNodeInstanceId.equals(other.taskNodeInstanceId))
			return false;
		if (taskObjId == null) {
			if (other.taskObjId != null)
				return false;
		} else if (!taskObjId.equals(other.taskObjId))
			return false;
		if (taskType == null) {
			if (other.taskType != null)
				return false;
		} else if (!taskType.equals(other.taskType))
			return false;
		if (upTaskInstanceId == null) {
			if (other.upTaskInstanceId != null)
				return false;
		} else if (!upTaskInstanceId.equals(other.upTaskInstanceId))
			return false;
		return true;
	}
	
}