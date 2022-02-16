package com.yusys.biapp.input.task.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the rpt_fltsk_info database table.
 * 
 */
@Entity
@Table(name="rpt_tsk_info")
public class RptTskInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_ID")
	private String taskId;

	@Column(name="CHECK_STS")
	private String checkSts;

	@Column(name="EFFECT_DATE")
	private String effectDate;

	@Column(name="EXE_OBJ_ID")
	private String exeObjId;

	@Column(name="EXE_OBJ_TYPE")
	private String exeObjType;

	@Column(name="SUM_MODE")
	private String sumMode;

	@Column(name="TASK_DEADLINE")
	private String taskDeadline;

	@Column(name="TASK_DEF_ID")
	private String taskDefId;

	@Column(name="TASK_FREQ")
	private String taskFreq;

	@Column(name="TASK_NM")
	private String taskNm;

	@Column(name="TASK_STS")
	private String taskSts;

	@Column(name="TASK_TYPE")
	private String taskType;

	@Column(name="TRIGGER_ID")
	private String triggerId;

	@Column(name="TRIGGER_TYPE")
	private String triggerType;
	
	@Column(name="TRIGGER_NM")
	private String triggerNm;

	@Column(name="UP_TASK_ID")
	private String upTaskId;
	
	@Column(name="IS_PRE")
	private String isPre;

	@Column(name="DATE_OFFSET_AMOUNT")
	private Integer dateOffsetAmount;
	
	@Column(name="INVALID_DATE")
	private String invalidDate;
	
	@Column(name="IS_RELY_DATA")
	private String isRelyData;
	
	@Column(name="LOGIC_DEL_NO")
	private String logicDelNo;
	
	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="NODE_TYPE")
	private String nodeType;

	@Column(name="CREATE_USER")
	private String createUser;

	@Column(name="CREATE_ORG")
	private String createOrg;

	@Column(name="UPDATE_DATE")
	private Timestamp updateDate;
	
	@Column(name="AFTER_TASK_STS")
	private String afterTaskSts;
	
	@Column(name="AFTER_TASK_OBJ_ID")
	private String afterTaskObjId;
	
	private String taskTypeNm;
	
	private String exeObjNm;
	
    public RptTskInfo() {
    }

    
	public String getExeObjNm() {
		return exeObjNm;
	}


	public void setExeObjNm(String exeObjNm) {
		this.exeObjNm = exeObjNm;
	}


	public String getAfterTaskObjId() {
        return afterTaskObjId;
    }

    public void setAfterTaskObjId(String afterTaskObjId) {
        this.afterTaskObjId = afterTaskObjId;
    }

    public String getAfterTaskSts() {
        return afterTaskSts;
    }

    public void setAfterTaskSts(String afterTaskSts) {
        this.afterTaskSts = afterTaskSts;
    }

    public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCheckSts() {
		return this.checkSts;
	}

	public void setCheckSts(String checkSts) {
		this.checkSts = checkSts;
	}

	public String getEffectDate() {
		return this.effectDate;
	}

	public void setEffectDate(String effectDate) {
		this.effectDate = effectDate;
	}

	public String getUpExeObjId() {
		return this.exeObjId;
	}

	public void setUpExeObjId(String exeObjId) {
		this.exeObjId = exeObjId;
	}

	public String getExeObjType() {
		return this.exeObjType;
	}

	public void setExeObjType(String exeObjType) {
		this.exeObjType = exeObjType;
	}

	public String getSumMode() {
		return this.sumMode;
	}

	public void setSumMode(String sumMode) {
		this.sumMode = sumMode;
	}

	public String getTaskDeadline() {
		return this.taskDeadline;
	}

	public void setTaskDeadline(String taskDeadline) {
		this.taskDeadline = taskDeadline;
	}

	public String getTaskDefId() {
		return this.taskDefId;
	}

	public void setTaskDefId(String taskDefId) {
		this.taskDefId = taskDefId;
	}

	public String getTaskFreq() {
		return this.taskFreq;
	}

	public void setTaskFreq(String taskFreq) {
		this.taskFreq = taskFreq;
	}

	public String getTaskNm() {
		return this.taskNm;
	}

	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}

	public String getTaskSts() {
		return this.taskSts;
	}

	public void setTaskSts(String taskSts) {
		this.taskSts = taskSts;
	}

	public String getTaskType() {
		return this.taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTriggerId() {
		return this.triggerId;
	}

	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}

	public String getTriggerType() {
		return this.triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public String getIsPre() {
		return isPre;
	}

	public void setIsPre(String isPre) {
		this.isPre = isPre;
	}

	public Integer getDateOffsetAmount() {
		return dateOffsetAmount;
	}

	public void setDateOffsetAmount(Integer dateOffsetAmount) {
		this.dateOffsetAmount = dateOffsetAmount;
	}

	public String getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(String invalidDate) {
		this.invalidDate = invalidDate;
	}

	public String getUpTaskId() {
		return this.upTaskId;
	}

	public void setUpTaskId(String upTaskId) {
		this.upTaskId = upTaskId;
	}

	public String getIsRelyData() {
		return isRelyData;
	}

	public void setIsRelyData(String isRelyData) {
		this.isRelyData = isRelyData;
	}

	public String getLogicDelNo() {
		return logicDelNo;
	}

	public void setLogicDelNo(String logicDelNo) {
		this.logicDelNo = logicDelNo;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getExeObjId() {
		return exeObjId;
	}

	public void setExeObjId(String exeObjId) {
		this.exeObjId = exeObjId;
	}

	public String getTriggerNm() {
		return triggerNm;
	}

	public void setTriggerNm(String triggerNm) {
		this.triggerNm = triggerNm;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getTaskTypeNm() {
		return taskTypeNm;
	}

	public void setTaskTypeNm(String taskTypeNm) {
		this.taskTypeNm = taskTypeNm;
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



	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((checkSts == null) ? 0 : checkSts.hashCode());
		result = prime * result
				+ ((effectDate == null) ? 0 : effectDate.hashCode());
		result = prime * result
				+ ((exeObjType == null) ? 0 : exeObjType.hashCode());
		result = prime * result
				+ ((invalidDate == null) ? 0 : invalidDate.hashCode());
		result = prime * result + ((isPre == null) ? 0 : isPre.hashCode());
		result = prime * result
				+ ((isRelyData == null) ? 0 : isRelyData.hashCode());
		result = prime * result
				+ ((logicDelNo == null) ? 0 : logicDelNo.hashCode());
		result = prime * result + ((sumMode == null) ? 0 : sumMode.hashCode());
		result = prime * result
				+ ((taskDeadline == null) ? 0 : taskDeadline.hashCode());
		result = prime * result
				+ ((taskDefId == null) ? 0 : taskDefId.hashCode());
		result = prime * result
				+ ((taskFreq == null) ? 0 : taskFreq.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result + ((taskNm == null) ? 0 : taskNm.hashCode());
		result = prime * result + ((taskSts == null) ? 0 : taskSts.hashCode());
		result = prime * result
				+ ((taskType == null) ? 0 : taskType.hashCode());
		result = prime * result
				+ ((triggerId == null) ? 0 : triggerId.hashCode());
		result = prime * result
				+ ((triggerType == null) ? 0 : triggerType.hashCode());
		result = prime * result
				+ ((exeObjId == null) ? 0 : exeObjId.hashCode());
		result = prime * result
				+ ((upTaskId == null) ? 0 : upTaskId.hashCode());
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
		RptTskInfo other = (RptTskInfo) obj;
		if (checkSts == null) {
			if (other.checkSts != null)
				return false;
		} else if (!checkSts.equals(other.checkSts))
			return false;
		if (effectDate == null) {
			if (other.effectDate != null)
				return false;
		} else if (!effectDate.equals(other.effectDate))
			return false;
		if (exeObjType == null) {
			if (other.exeObjType != null)
				return false;
		} else if (!exeObjType.equals(other.exeObjType))
			return false;
		if (invalidDate == null) {
			if (other.invalidDate != null)
				return false;
		} else if (!invalidDate.equals(other.invalidDate))
			return false;
		if (isPre == null) {
			if (other.isPre != null)
				return false;
		} else if (!isPre.equals(other.isPre))
			return false;
		if (isRelyData == null) {
			if (other.isRelyData != null)
				return false;
		} else if (!isRelyData.equals(other.isRelyData))
			return false;
		if (logicDelNo == null) {
			if (other.logicDelNo != null)
				return false;
		} else if (!logicDelNo.equals(other.logicDelNo))
			return false;
		if (sumMode == null) {
			if (other.sumMode != null)
				return false;
		} else if (!sumMode.equals(other.sumMode))
			return false;
		if (taskDeadline == null) {
			if (other.taskDeadline != null)
				return false;
		} else if (!taskDeadline.equals(other.taskDeadline))
			return false;
		if (taskDefId == null) {
			if (other.taskDefId != null)
				return false;
		} else if (!taskDefId.equals(other.taskDefId))
			return false;
		if (taskFreq == null) {
			if (other.taskFreq != null)
				return false;
		} else if (!taskFreq.equals(other.taskFreq))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (taskNm == null) {
			if (other.taskNm != null)
				return false;
		} else if (!taskNm.equals(other.taskNm))
			return false;
		if (taskSts == null) {
			if (other.taskSts != null)
				return false;
		} else if (!taskSts.equals(other.taskSts))
			return false;
		if (taskType == null) {
			if (other.taskType != null)
				return false;
		} else if (!taskType.equals(other.taskType))
			return false;
		if (triggerId == null) {
			if (other.triggerId != null)
				return false;
		} else if (!triggerId.equals(other.triggerId))
			return false;
		if (triggerType == null) {
			if (other.triggerType != null)
				return false;
		} else if (!triggerType.equals(other.triggerType))
			return false;
		if (exeObjId == null) {
			if (other.exeObjId != null)
				return false;
		} else if (!exeObjId.equals(other.exeObjId))
			return false;
		if (upTaskId == null) {
			if (other.upTaskId != null)
				return false;
		} else if (!upTaskId.equals(other.upTaskId))
			return false;
		return true;
	}
}