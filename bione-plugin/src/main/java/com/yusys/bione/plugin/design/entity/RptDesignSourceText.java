package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_SOURCE_TEXT database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_SOURCE_TEXT")
public class RptDesignSourceText implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignSourceTextPK id;

	private String expression;

    public RptDesignSourceText() {
    }

	public RptDesignSourceTextPK getId() {
		return this.id;
	}

	public void setId(RptDesignSourceTextPK id) {
		this.id = id;
	}
	
	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

}