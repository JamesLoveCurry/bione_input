package com.yusys.bione.plugin.rptvalid.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
public class IdxValidLogicVO implements Serializable,AnnotationValidable{
	
	private String checkId;
	private String dimTypes;
	private String relateDim;
	
	public IdxValidLogicVO() {
	}
	
	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getDimTypes() {
		return dimTypes;
	}

	public void setDimTypes(String dimTypes) {
		this.dimTypes = dimTypes;
	}

	public String getRelateDim() {
		return relateDim;
	}

	public void setRelateDim(String relateDim) {
		this.relateDim = relateDim;
	}
}
