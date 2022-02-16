package com.yusys.biapp.input.template.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_INPUT_LST_TEMPLE_RULE database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_LST_TEMPLE_RULE")
public class RptInputLstTempleRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=32)
	private String id;

	@Column(name="RULE_ID", nullable=false, length=32)
	private String ruleId;

	@Column(name="TEMPLE_ID", nullable=false, length=32)
	private String templeId;

    public RptInputLstTempleRule() {
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRuleId() {
		return this.ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getTempleId() {
		return this.templeId;
	}

	public void setTempleId(String templeId) {
		this.templeId = templeId;
	}

}