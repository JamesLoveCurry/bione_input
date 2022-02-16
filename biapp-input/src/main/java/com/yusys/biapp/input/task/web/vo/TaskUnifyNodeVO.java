package com.yusys.biapp.input.task.web.vo;

import java.util.List;
import java.util.Map;

public class TaskUnifyNodeVO {

	private String taskNodeDefId;// 任务节点Id

	private String handleType;// 处理级别

	private String taskObjType;// 任务对象类型

	private String taskNodeNm;

	private String taskOrderno;

	private String orgNo;
	
	private String orgNm;
	
	private String nodeType;

	private List<Map<String, String>> taskObjIdMap;// 任务对象Id

	public String getOrgNm() {
        return orgNm;
    }

    public void setOrgNm(String orgNm) {
        this.orgNm = orgNm;
    }

    public String getTaskNodeDefId() {
		return taskNodeDefId;
	}

	public void setTaskNodeDefId(String taskNodeDefId) {
		this.taskNodeDefId = taskNodeDefId;
	}

	public String getHandleType() {
		return handleType;
	}

	public void setHandleType(String handleType) {
		this.handleType = handleType;
	}

	public String getTaskObjType() {
		return taskObjType;
	}

	public void setTaskObjType(String taskObjType) {
		this.taskObjType = taskObjType;
	}

	public List<Map<String, String>> getTaskObjIdMap() {
		return taskObjIdMap;
	}

	public void setTaskObjIdMap(List<Map<String, String>> taskObjIdMap) {
		this.taskObjIdMap = taskObjIdMap;
	}

	public String getTaskNodeNm() {
		return taskNodeNm;
	}

	public void setTaskNodeNm(String taskNodeNm) {
		this.taskNodeNm = taskNodeNm;
	}

	public String getTaskOrderno() {
		return taskOrderno;
	}

	public void setTaskOrderno(String taskOrderno) {
		this.taskOrderno = taskOrderno;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

}
