package com.yusys.biapp.input.index.web.vo;

import java.util.List;

import com.yusys.biapp.input.index.entity.RptInputIdxTemplate;

public class TemplateVO {
	
	private RptInputIdxTemplate template;
	private List<IdxCfgDetailVO> detailList;
	public RptInputIdxTemplate getTemplate() {
		return template;
	}
	public void setTemplate(RptInputIdxTemplate template) {
		this.template = template;
	}
	public List<IdxCfgDetailVO> getDetailList() {
		return detailList;
	}
	public void setDetailList(List<IdxCfgDetailVO> detailList) {
		this.detailList = detailList;
	}

	

}
