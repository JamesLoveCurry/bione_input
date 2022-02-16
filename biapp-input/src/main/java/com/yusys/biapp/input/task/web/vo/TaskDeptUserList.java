package com.yusys.biapp.input.task.web.vo;

public class TaskDeptUserList {

    private String taskInstanceId;

    private String taskObjType;

    private String taskObjNm;

    private String taskNodeNm;

    private String nodeType;

    private String orgNm;

    private String startTime;

    public String getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(String taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public String getTaskObjType() {
        return taskObjType;
    }

    public void setTaskObjType(String taskObjType) {
        this.taskObjType = taskObjType;
    }

    public String getTaskObjNm() {
        return taskObjNm;
    }

    public void setTaskObjNm(String taskObjNm) {
        this.taskObjNm = taskObjNm;
    }

    public String getTaskNodeNm() {
        return taskNodeNm;
    }

    public void setTaskNodeNm(String taskNodeNm) {
        this.taskNodeNm = taskNodeNm;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getOrgNm() {
        return orgNm;
    }

    public void setOrgNm(String orgNm) {
        this.orgNm = orgNm;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
