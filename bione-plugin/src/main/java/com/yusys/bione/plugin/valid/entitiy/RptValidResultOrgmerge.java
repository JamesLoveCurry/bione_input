package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_RESULT_ORGMERGE database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_RESULT_ORGMERGE")
public class RptValidResultOrgmerge implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidResultOrgmergePK id;

	@Column(name="CHECK_TIME")
	private Timestamp checkTime;

	@Column(name="INDEX_VAL", precision=20, scale=5)
	private BigDecimal indexVal;

	@Column(name="LOWERLEVEL_TOTAL", precision=20, scale=5)
	private BigDecimal lowerlevelTotal;

	@Column(name="RPT_TEMPLATE_ID", length=32)
	private String rptTemplateId;

	@Column(length=10)
	private String unit;

    public RptValidResultOrgmerge() {
    }

	public RptValidResultOrgmergePK getId() {
		return this.id;
	}

	public void setId(RptValidResultOrgmergePK id) {
		this.id = id;
	}
	
	public Timestamp getCheckTime() {
		return this.checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public BigDecimal getIndexVal() {
		return this.indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}

	public BigDecimal getLowerlevelTotal() {
		return this.lowerlevelTotal;
	}

	public void setLowerlevelTotal(BigDecimal lowerlevelTotal) {
		this.lowerlevelTotal = lowerlevelTotal;
	}


	public String getRptTemplateId() {
		return rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}