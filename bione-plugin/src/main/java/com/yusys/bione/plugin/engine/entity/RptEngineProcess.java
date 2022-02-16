package com.yusys.bione.plugin.engine.entity;

public class RptEngineProcess {
	  /*task_id    VARCHAR2(32) not null,
	  task_nm    VARCHAR2(500),
	  ds_id      VARCHAR2(32),
	  process_nm VARCHAR2(500),
	  datadate   VARCHAR2(10)*/
	
	private String taskId;
	private String taskNm;
	private String dsId;
	private String processNm;
	private String dataDate;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskNm() {
		return taskNm;
	}
	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}
	public String getDsId() {
		return dsId;
	}
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}
	public String getProcessNm() {
		return processNm;
	}
	public void setProcessNm(String processNm) {
		this.processNm = processNm;
	}
	public String getDataDate() {
		return dataDate;
	}
	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}
	
	
	
}
