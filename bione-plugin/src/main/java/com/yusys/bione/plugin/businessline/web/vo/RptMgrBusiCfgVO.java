package com.yusys.bione.plugin.businessline.web.vo;

import com.yusys.bione.plugin.businessline.entity.RptMgrBusiCfg;

@SuppressWarnings("serial")
public class RptMgrBusiCfgVO extends RptMgrBusiCfg{

	private String lineNm;
	private String idxSourceId;
	private String rptSourceId;
	public String getLineNm() {
		return lineNm;
	}
	public void setLineNm(String lineNm) {
		this.lineNm = lineNm;
	}
	public String getIdxSourceId() {
		return idxSourceId;
	}
	public void setIdxSourceId(String idxSourceId) {
		this.idxSourceId = idxSourceId;
	}
	public String getRptSourceId() {
		return rptSourceId;
	}
	public void setRptSourceId(String rptSourceId) {
		this.rptSourceId = rptSourceId;
	}
	
	
}
