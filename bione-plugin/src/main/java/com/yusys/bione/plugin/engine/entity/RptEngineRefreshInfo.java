package com.yusys.bione.plugin.engine.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="RPT_TASK_REFRESH_INFO")
public class RptEngineRefreshInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="TASK_NO",unique = true,nullable = false)
    private String taskNo;

    @Column(name="TASK_NM")
    private String taskNm;

    @Column(name="INSTANCE_ID")
    private String instanceId;

    @Column(name="TASK_TYPE")
    private String taskType;

    @Column(name="STS")
    private String sts;

    @Column(name="START_TIME")
    private Timestamp startTime;

    @Column(name="END_TIME")
    private Timestamp endTime;

    @Column(name="PARENT_TASK_ID")
    private String parentTaskId;

    @Column(name="RUN_LOG")
    private String runLog;

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getTaskNm() {
        return taskNm;
    }

    public void setTaskNm(String taskNm) {
        this.taskNm = taskNm;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getRunLog() {
        return runLog;
    }

    public void setRunLog(String runLog) {
        this.runLog = runLog;
    }
}
