package com.yusys.bione.frame.passwd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the BIONE_PWD_SECURITY_CFG database table.
 * 
 */
@Entity
@Table(name="BIONE_PWD_SECURITY_CFG")
public class BionePwdSecurityCfg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name="DIFF_RECENT_HIS")
	private BigDecimal diffRecentHis;

	@Column(name="ENABLE_STS")
	private String enableSts;

	@Column(name="INC_LOWERCASE")
	private String incLowercase;

	@Column(name="INC_NUM")
	private String incNum;

	@Column(name="INC_SPECIAL")
	private String incSpecial;

	@Column(name="INC_UPPERCASE")
	private String incUppercase;

	@Column(name="LAST_UPDATE_TIME")
	private Timestamp lastUpdateTime;

	@Column(name="LAST_UPDATE_USER")
	private String lastUpdateUser;

	@Column(name="LOCK_TIME")
	private BigDecimal lockTime;

	@Column(name="LOCK_TYPE")
	private String lockType;

	@Column(name="MAX_ERROR_TIMES")
	private BigDecimal maxErrorTimes;

	@Column(name="MAX_LENGTH")
	private String maxLength;

	@Column(name="MIN_LENGTH")
	private String minLength;

	@Column(name="VALID_TIME")
	private BigDecimal validTime;

	@Column(name="IS_FIRST")
	private String isFirst;

    public BionePwdSecurityCfg() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getDiffRecentHis() {
		return this.diffRecentHis;
	}

	public void setDiffRecentHis(BigDecimal diffRecentHis) {
		this.diffRecentHis = diffRecentHis;
	}

	public String getEnableSts() {
		return this.enableSts;
	}

	public void setEnableSts(String enableSts) {
		this.enableSts = enableSts;
	}

	public String getIncLowercase() {
		return this.incLowercase;
	}

	public void setIncLowercase(String incLowercase) {
		this.incLowercase = incLowercase;
	}

	public String getIncNum() {
		return this.incNum;
	}

	public void setIncNum(String incNum) {
		this.incNum = incNum;
	}

	public String getIncSpecial() {
		return this.incSpecial;
	}

	public void setIncSpecial(String incSpecial) {
		this.incSpecial = incSpecial;
	}

	public String getIncUppercase() {
		return this.incUppercase;
	}

	public void setIncUppercase(String incUppercase) {
		this.incUppercase = incUppercase;
	}

	public Timestamp getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateUser() {
		return this.lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public BigDecimal getLockTime() {
		return this.lockTime;
	}

	public void setLockTime(BigDecimal lockTime) {
		this.lockTime = lockTime;
	}

	public String getLockType() {
		return this.lockType;
	}

	public void setLockType(String lockType) {
		this.lockType = lockType;
	}

	public BigDecimal getMaxErrorTimes() {
		return this.maxErrorTimes;
	}

	public void setMaxErrorTimes(BigDecimal maxErrorTimes) {
		this.maxErrorTimes = maxErrorTimes;
	}

	public String getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getMinLength() {
		return this.minLength;
	}

	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

	public BigDecimal getValidTime() {
		return this.validTime;
	}

	public void setValidTime(BigDecimal validTime) {
		this.validTime = validTime;
	}

	public String getIsFirst() {
		return isFirst;
	}

	public void setIsFirst(String isFirst) {
		this.isFirst = isFirst;
	}
}