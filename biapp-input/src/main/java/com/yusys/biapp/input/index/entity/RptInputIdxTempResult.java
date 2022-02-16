package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RPT_INPUT_IDX_TEMP_RESULT")
public class RptInputIdxTempResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7664333318348209844L;
	@Id
	@Column(name = "INPUT_DATA_ID")
	private String inputDataId;
	@Column(name = "INDEX_NO")
	private String indexNo;
	@Column(name = "DATA_DATE")
	private String dataDate;
	@Column(name = "ORG_NO")
	private String orgNo;
	@Column(name = "CURRENCY")
	private String currency;
	@Column(name = "DIM1")
	private String dim1;
	@Column(name = "DIM2")
	private String dim2;
	@Column(name = "DIM3")
	private String dim3;
	@Column(name = "DIM4")
	private String dim4;
	@Column(name = "DIM5")
	private String dim5;
	@Column(name = "DIM6")
	private String dim6;
	@Column(name = "DIM7")
	private String dim7;
	@Column(name = "DIM8")
	private String dim8;
	@Column(name = "DIM9")
	private String dim9;
	@Column(name = "DIM10")
	private String dim10;
	@Column(name = "TASK_INSTANCE_ID")
	private String taskInstanceId;
	@Column(name = "INDEX_VAL")
	private String indexVal;
	@Column(name = "TASK_ID")
	private String taskId;
	@Column(name = "OPER_USER")
	private String operUser;
	@Column(name = "OPER_TIME")
	private String operTime;
	@Column(name = "DATA_STS")
	private String dataSts;
	@Column(name = "OPER_OPINION")
	private String operOpinion;

	public String getInputDataId() {
		return inputDataId;
	}

	public void setInputDataId(String inputDataId) {
		this.inputDataId = inputDataId;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDim1() {
		return dim1;
	}

	public void setDim1(String dim1) {
		this.dim1 = dim1;
	}

	public String getDim2() {
		return dim2;
	}

	public void setDim2(String dim2) {
		this.dim2 = dim2;
	}

	public String getDim3() {
		return dim3;
	}

	public void setDim3(String dim3) {
		this.dim3 = dim3;
	}

	public String getDim4() {
		return dim4;
	}

	public void setDim4(String dim4) {
		this.dim4 = dim4;
	}

	public String getDim5() {
		return dim5;
	}

	public void setDim5(String dim5) {
		this.dim5 = dim5;
	}

	public String getDim6() {
		return dim6;
	}

	public void setDim6(String dim6) {
		this.dim6 = dim6;
	}

	public String getDim7() {
		return dim7;
	}

	public void setDim7(String dim7) {
		this.dim7 = dim7;
	}

	public String getDim8() {
		return dim8;
	}

	public void setDim8(String dim8) {
		this.dim8 = dim8;
	}

	public String getDim9() {
		return dim9;
	}

	public void setDim9(String dim9) {
		this.dim9 = dim9;
	}

	public String getDim10() {
		return dim10;
	}

	public void setDim10(String dim10) {
		this.dim10 = dim10;
	}

	public String getTaskInstanceId() {
		return taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getIndexVal() {
		return indexVal;
	}

	public void setIndexVal(String indexVal) {
		this.indexVal = indexVal;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public String getOperTime() {
		return operTime;
	}

	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}

	public String getDataSts() {
		return dataSts;
	}

	public void setDataSts(String dataSts) {
		this.dataSts = dataSts;
	}

	public String getOperOpinion() {
		return operOpinion;
	}

	public void setOperOpinion(String operOpinion) {
		this.operOpinion = operOpinion;
	}

}
