package com.yusys.bione.plugin.rptbank.web.vo;

import java.util.List;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfo;

@SuppressWarnings("serial")
public class RptIdxBankVO extends RptIdxBankInfo {

	private String mainNm;
	
	private String partNm;
	private List<RptIdxBankInfo> children;
	public RptIdxBankVO(){
	}
	public RptIdxBankVO(RptIdxBankInfo info){
		BeanUtils.copy(info,this);
	}
	
	public RptIdxBankVO(RptIdxBankInfo info,String mainNm,String partNm){
		BeanUtils.copy(info,this);
		this.setMainNm(mainNm);
		this.setPartNm(partNm);
	}
	
	public String getMainNm() {
		return mainNm;
	}

	public void setMainNm(String mainNm) {
		this.mainNm = mainNm;
	}

	public String getPartNm() {
		return partNm;
	}

	public void setPartNm(String partNm) {
		this.partNm = partNm;
	}
	
	public List<RptIdxBankInfo> getChildren() {
		if (children == null) {
			children = Lists.newArrayList();
		}
		return children;
	}

	public void setChildren(List<RptIdxBankInfo> children) {
		this.children = children;
	}
	
}
