package com.yusys.bione.plugin.rptidx.web.vo;

import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaInfo;

@SuppressWarnings("serial")
public class IdxFormulaAndSrcIdxVO extends RptIdxFormulaInfo{
	private String srcIndexNo;
	
	private String dimNos;
	
	private String message;
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDimNos() {
		return dimNos;
	}

	public void setDimNos(String dimNos) {
		this.dimNos = dimNos;
	}

	public String getSrcIndexNo() {
		return srcIndexNo;
	}

	public void setSrcIndexNo(String srcIndexNo) {
		this.srcIndexNo = srcIndexNo;
	} 
	
	
	
}
