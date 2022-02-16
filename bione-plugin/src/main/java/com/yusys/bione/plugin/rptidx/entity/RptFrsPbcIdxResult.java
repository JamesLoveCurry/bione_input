package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_FRS_PBC_IDX_RESULT database table.
 * 
 */
@Entity
@Table(name="RPT_FRS_PBC_IDX_RESULT")
public class RptFrsPbcIdxResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptFrsPbcIdxResultPK id;

	@Column(name="INDEX_VAL", precision=20, scale=5)
	private BigDecimal indexVal;
	
	@Column(name="TEMPLATE_ID")
	private String templateId;

    public RptFrsPbcIdxResult() {
    }

	public RptFrsPbcIdxResultPK getId() {
		return this.id;
	}

	public void setId(RptFrsPbcIdxResultPK id) {
		this.id = id;
	}
	
	public BigDecimal getIndexVal() {
		return this.indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}