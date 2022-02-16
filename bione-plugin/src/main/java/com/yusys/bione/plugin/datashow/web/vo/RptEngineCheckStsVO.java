package com.yusys.bione.plugin.datashow.web.vo;

import java.sql.Timestamp;

import javax.persistence.Column;


/**
 * The persistent class for the RPT_ENGINE_CHECK_STS database table.
 * 
 */
public class RptEngineCheckStsVO{

	@Column(name="DATA_DATE", unique=true, nullable=false, length=8)
	private String dataDate;

	@Column(name="RPT_TEMPLATE_ID", unique=true, nullable=false, length=32)
	private String rptTemplateId;
	
	@Column(name="ORG_NO", unique=true, nullable=false, length=32)
	private String orgNo;
	
	@Column(name="ORG_NM", unique=true, nullable=false, length=50)
	private String orgNm;
	
	@Column(name="CHECK_TYPE", unique=true, nullable=false, length=10)
	private String checkType;
	
	@Column(name="CHECK_STS", length=10)
	private String checkSts;
	
	@Column(name="START_TIME")
	private Timestamp startTime;
	
	@Column(name="END_TIME")
	private Timestamp endTime;

	@Column(name="TASK_ID", length=32)
	private String taskId;
	
	@Column(name="TASK_TYPE", length=10)
	private String taskType;
	
	@Column(name="INDEX_NO")
	private String indexNo;
	
	@Column(name="INDEX_NM")
	private String indexNm;
	
	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getRptTemplateId() {
		return rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the taskType
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	public String getCheckSts() {
		return checkSts;
	}

	public void setCheckSts(String checkSts) {
		this.checkSts = checkSts;
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
	
}