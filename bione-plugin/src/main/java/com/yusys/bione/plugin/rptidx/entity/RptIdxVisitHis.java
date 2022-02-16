package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_IDX_VISIT_HIS database table.
 * 
 */
@Entity
@Table(name="RPT_IDX_VISIT_HIS")
public class RptIdxVisitHis implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="HIS_ID", unique=true, nullable=false, length=32)
	private String hisId;

	@Column(name="ACCESS_DATE", length=8)
	private String accessDate;

	@Column(name="ACCESS_IP", nullable=false, length=100)
	private String accessIp;

	@Column(name="ACCESS_TIME")
	private Timestamp accessTime;

	@Column(name="INDEX_NO", length=32)
	private String indexNo;

	@Column(name="INDEX_VER_ID", precision=18)
	private BigDecimal indexVerId;

	@Column(name="USER_ID", nullable=false, length=32)
	private String userId;

    public RptIdxVisitHis() {
    }

	public String getHisId() {
		return this.hisId;
	}

	public void setHisId(String hisId) {
		this.hisId = hisId;
	}

	public String getAccessDate() {
		return this.accessDate;
	}

	public void setAccessDate(String accessDate) {
		this.accessDate = accessDate;
	}

	public String getAccessIp() {
		return this.accessIp;
	}

	public void setAccessIp(String accessIp) {
		this.accessIp = accessIp;
	}

	public Timestamp getAccessTime() {
		return this.accessTime;
	}

	public void setAccessTime(Timestamp accessTime) {
		this.accessTime = accessTime;
	}

	public String getIndexNo() {
		return this.indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public BigDecimal getIndexVerId() {
		return this.indexVerId;
	}

	public void setIndexVerId(BigDecimal indexVerId) {
		this.indexVerId = indexVerId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}