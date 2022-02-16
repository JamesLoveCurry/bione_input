package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_RESULT_LOGIC database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_RESULT_LOGIC")
public class RptValidResultLogic implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidResultLogicPK id;

	@Column(name="CHECK_TIME")
	private Timestamp checkTime;

	@Column(name="IS_PASS", length=1)
	private String isPass;

	@Column(name="RPT_TEMPLATE_ID", length=32)
	private String rptTemplateId;

	@Column(name="D_VAL")
	private BigDecimal dVal;
	
	@Column(name="LEFT_VAL")
	private BigDecimal leftVal;
	
	@Column(name="RIGHT_VAL")
	private BigDecimal rightVal;
	
    public RptValidResultLogic() {
    }

	public RptValidResultLogicPK getId() {
		return this.id;
	}

	public void setId(RptValidResultLogicPK id) {
		this.id = id;
	}
	
	public Timestamp getCheckTime() {
		return this.checkTime;
	}

	public void setCheckTime(Timestamp checkTime) {
		this.checkTime = checkTime;
	}

	public String getIsPass() {
		return this.isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	public String getRptTemplateId() {
		return rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}

	public BigDecimal getdVal() {
		return dVal;
	}

	public void setdVal(BigDecimal dVal) {
		this.dVal = dVal;
	}

	public BigDecimal getLeftVal() {
		return leftVal;
	}

	public void setLeftVal(BigDecimal leftVal) {
		this.leftVal = leftVal;
	}

	public BigDecimal getRightVal() {
		return rightVal;
	}

	public void setRightVal(BigDecimal rightVal) {
		this.rightVal = rightVal;
	}
}