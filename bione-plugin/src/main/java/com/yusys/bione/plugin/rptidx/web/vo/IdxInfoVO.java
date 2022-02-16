package com.yusys.bione.plugin.rptidx.web.vo;

import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;

@SuppressWarnings("serial")
public class IdxInfoVO extends RptIdxInfo{
	private String busiRule;
	private String busiDef;
	public String getBusiRule() {
		return busiRule;
	}
	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}
	public String getBusiDef() {
		return busiDef;
	}
	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	};
}
