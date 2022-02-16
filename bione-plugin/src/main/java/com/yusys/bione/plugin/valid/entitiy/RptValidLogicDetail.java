package com.yusys.bione.plugin.valid.entitiy;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_VALID_LOGIC_DETAIL database table.
 * 
 */
@Entity
@Table(name="RPT_VALID_LOGIC_DETAIL")
public class RptValidLogicDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptValidLogicDetailPK id;

	@Column(name="INDEX_VAL", precision=20, scale=5)
	private BigDecimal indexVal;

    public RptValidLogicDetail() {
    }

	public RptValidLogicDetailPK getId() {
		return this.id;
	}

	public void setId(RptValidLogicDetailPK id) {
		this.id = id;
	}
	
	public BigDecimal getIndexVal() {
		return this.indexVal;
	}

	public void setIndexVal(BigDecimal indexVal) {
		this.indexVal = indexVal;
	}

}