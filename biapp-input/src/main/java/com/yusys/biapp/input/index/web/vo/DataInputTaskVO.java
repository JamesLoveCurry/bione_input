package com.yusys.biapp.input.index.web.vo;

import java.util.List;

public class DataInputTaskVO {

	// 任务实例Id
	private String taskInstanceId;
	// 任务Id
	private String taskId;
	private String templateId;
	private String dataDate;
	private String inputType;

	private List<DataInputVO> dataInputList;

	public String getTaskInstanceId() {
		return taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public List<DataInputVO> getDataInputList() {
		return dataInputList;
	}

	public void setDataInputList(List<DataInputVO> dataInputList) {
		this.dataInputList = dataInputList;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

}
