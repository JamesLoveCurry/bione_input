package com.yusys.biapp.input.logs.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;


/**
 * The persistent class for the RPT_INPUT_LST_VALIDATE_LOG database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_VALIDATE_LOG")
public class RptInputLstValidateLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=32)
	private String id;

	@Column(name="CASE_ID", length=32)
	private String caseId;

	@Column(name="DATA_FILE_ID", length=32)
	private String dataFileId;

	@Column(name="DUTY_USER", length=32)
	private String dutyUser;

	@Column(name="ORG_NO", length=32)
	private String orgNo;

	@Column(name="ROW_NUM", precision=12)
	private BigDecimal rowNum;

	@Column(name="RULE_NAME", length=100)
	private String ruleName;

	@Column(name="TEMPLE_ID", length=32)
	private String templeId;

	@Column(name="VALIDATE_RESULT", length=2000)
	private String validateResult;

	@Column(name="VALIDATE_TIME", length=20)
	private String validateTime;

	@Column(name="VALIDATE_TYPE", length=10)
	private String validateType;

    public RptInputLstValidateLog() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCaseId() {
		return this.caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getDataFileId() {
		return this.dataFileId;
	}

	public void setDataFileId(String dataFileId) {
		this.dataFileId = dataFileId;
	}

	public String getDutyUser() {
		return this.dutyUser;
	}

	public void setDutyUser(String dutyUser) {
		this.dutyUser = dutyUser;
	}

	public String getOrgNo() {
		return this.orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public BigDecimal getRowNum() {
		return this.rowNum;
	}

	public void setRowNum(BigDecimal rowNum) {
		this.rowNum = rowNum;
	}

	public String getRuleName() {
		return this.ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

	public String getValidateResult() {
		return this.validateResult;
	}

	public void setValidateResult(String validateResult) {
		this.validateResult = validateResult;
	}

	public String getValidateTime() {
		return this.validateTime;
	}

	public void setValidateTime(String validateTime) {
		this.validateTime = validateTime;
	}

	public String getValidateType() {
		return this.validateType;
	}

	public void setValidateType(String validateType) {
		this.validateType = validateType;
	}
	
	/**
	 * 生成一个校验结果对象
	 * 
	 * @param ruleInfo
	 *            校验规则
	 * @param validateResult
	 *            校验结果
	 * @return
	 */
	public static RptInputLstValidateLog createLog(RptInputListDataRuleInfo ruleInfo, String validateResult, int rowNum) {
		RptInputLstValidateLog log = new RptInputLstValidateLog();
		log.setValidateResult(validateResult);
		log.setRowNum(new BigDecimal(rowNum));
		if (ruleInfo != null) {
			log.setRuleName(ruleInfo.getRuleNm());
			log.setValidateType(ruleInfo.getRuleType());
		}
		return log;
	}
}