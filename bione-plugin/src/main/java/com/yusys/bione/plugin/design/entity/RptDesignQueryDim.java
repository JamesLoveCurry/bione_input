package com.yusys.bione.plugin.design.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_DESIGN_QUERY_DIM database table.
 * 
 */
@Entity
@Table(name="RPT_DESIGN_QUERY_DIM")
public class RptDesignQueryDim implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RptDesignQueryDimPK id;

	@Column(name="PUBLIC_DIM")
	private String publicDim;

	@Column(name="QUERY_DIM")
	private String queryDim;
	
	@Column(name="PARAM_TEMPLATE_ID")
	private String paramTemplateId;

    public RptDesignQueryDim() {
    }

	public RptDesignQueryDimPK getId() {
		return this.id;
	}

	public void setId(RptDesignQueryDimPK id) {
		this.id = id;
	}

	/**
	 * @return 返回 publicDim。
	 */
	public String getPublicDim() {
		return publicDim;
	}

	/**
	 * @param publicDim 设置 publicDim。
	 */
	public void setPublicDim(String publicDim) {
		this.publicDim = publicDim;
	}

	/**
	 * @return 返回 queryDim。
	 */
	public String getQueryDim() {
		return queryDim;
	}

	/**
	 * @param queryDim 设置 queryDim。
	 */
	public void setQueryDim(String queryDim) {
		this.queryDim = queryDim;
	}

	/**
	 * @return 返回 paramTemplateId。
	 */
	public String getParamTemplateId() {
		return paramTemplateId;
	}

	/**
	 * @param paramTemplateId 设置 paramTemplateId。
	 */
	public void setParamTemplateId(String paramTemplateId) {
		this.paramTemplateId = paramTemplateId;
	}
	
}