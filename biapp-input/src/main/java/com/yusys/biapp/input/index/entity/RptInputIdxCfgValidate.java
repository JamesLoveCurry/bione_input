package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Entity
@Table(name="RPT_INPUT_IDX_CFG_VALIDATE")
public class RptInputIdxCfgValidate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4284738127631818345L;
	@EmbeddedId
	private RptInputIdxCfgValidatePK id;
	public RptInputIdxCfgValidatePK getId() {
		return id;
	}
	public void setId(RptInputIdxCfgValidatePK id) {
		this.id = id;
	}
}