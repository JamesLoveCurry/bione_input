package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_WARN_LEVEL database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_WARN_LEVEL")
public class RptValidWarnLevel implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidWarnLevelPK id;

	@Column(name="LEVEL_NM")
	private String levelNm;

	@Column(name="LEVEL_TYPE")
	private String levelType;

	@Column(name="MINUS_RANGE_VAL")
	private BigDecimal minusRangeVal;

	@Column(name="POSTIVE_RANGE_VAL")
	private BigDecimal postiveRangeVal;

	@Column(name="REMIND_COLOR")
	private String remindColor;
	
	@Column(name="IS_PASS_COND")
	private String isPassCond;
	
	@Column(name="INDEX_NO")
	private String indexNo;
	

    public String getIsPassCond() {
		return isPassCond;
	}

	public void setIsPassCond(String isPassCond) {
		this.isPassCond = isPassCond;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public RptValidWarnLevel() {
    }

	public RptValidWarnLevelPK getId() {
		return this.id;
	}

	public void setId(RptValidWarnLevelPK id) {
		this.id = id;
	}
	
	public String getLevelNm() {
		return this.levelNm;
	}

	public void setLevelNm(String levelNm) {
		this.levelNm = levelNm;
	}

	public String getLevelType() {
		return this.levelType;
	}

	public void setLevelType(String levelType) {
		this.levelType = levelType;
	}

	public BigDecimal getMinusRangeVal() {
		return this.minusRangeVal;
	}

	public void setMinusRangeVal(BigDecimal minusRangeVal) {
		this.minusRangeVal = minusRangeVal;
	}

	public BigDecimal getPostiveRangeVal() {
		return this.postiveRangeVal;
	}

	public void setPostiveRangeVal(BigDecimal postiveRangeVal) {
		this.postiveRangeVal = postiveRangeVal;
	}

	public String getRemindColor() {
		return this.remindColor;
	}

	public void setRemindColor(String remindColor) {
		this.remindColor = remindColor;
	}

}