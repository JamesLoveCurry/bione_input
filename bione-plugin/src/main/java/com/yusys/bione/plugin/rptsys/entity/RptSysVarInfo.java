package com.yusys.bione.plugin.rptsys.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * The persistent class for the BIONE_SYS_VAR_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_SYS_VAR_INFO")
public class RptSysVarInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="idGenerator")
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@Column(name="VAR_ID")
	private String varId;

	private String remark;
	
	@Column(name="DEF_TYPE")
	private String defType;
	
	@Column(name="VAR_NM")
	private String varNm;

	@Column(name="VAR_NO")
	private String varNo;

	@Column(name="VAR_VAL")
	private String varVal;
	
	@Column(name="SOURCE_ID")
	private String sourceId;
	
	@Column(name="VAR_TYPE")
	private String varType;

    public RptSysVarInfo() {
    }

	public String getVarId() {
		return varId;
	}

	public void setVarId(String varId) {
		this.varId = varId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDefType() {
		return defType;
	}

	public void setDefType(String defType) {
		this.defType = defType;
	}

	public String getVarNm() {
		return varNm;
	}

	public void setVarNm(String varNm) {
		this.varNm = varNm;
	}

	public String getVarNo() {
		return varNo;
	}

	public void setVarNo(String varNo) {
		this.varNo = varNo;
	}

	public String getVarVal() {
		return varVal;
	}

	public void setVarVal(String varVal) {
		this.varVal = varVal;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}


	
}