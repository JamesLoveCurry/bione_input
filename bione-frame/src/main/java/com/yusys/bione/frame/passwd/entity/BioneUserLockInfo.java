package com.yusys.bione.frame.passwd.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the BIONE_USER_LOCK_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_USER_LOCK_INFO")
public class BioneUserLockInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BioneUserLockInfoPK id;

	@Column(name="ERROR_TIMES")
	private BigDecimal errorTimes;

	@Column(name="LOCK_STS")
	private String lockSts;

	@Column(name="LOCK_TIME")
	private Timestamp lockTime;

	@Column(name="USER_IP")
	private String userIp;

    public BioneUserLockInfo() {
    }

	public BioneUserLockInfoPK getId() {
		return this.id;
	}

	public void setId(BioneUserLockInfoPK id) {
		this.id = id;
	}
	
	public BigDecimal getErrorTimes() {
		return this.errorTimes;
	}

	public void setErrorTimes(BigDecimal errorTimes) {
		this.errorTimes = errorTimes;
	}

	public String getLockSts() {
		return this.lockSts;
	}

	public void setLockSts(String lockSts) {
		this.lockSts = lockSts;
	}

	public Timestamp getLockTime() {
		return this.lockTime;
	}

	public void setLockTime(Timestamp lockTime) {
		this.lockTime = lockTime;
	}

	public String getUserIp() {
		return this.userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

}