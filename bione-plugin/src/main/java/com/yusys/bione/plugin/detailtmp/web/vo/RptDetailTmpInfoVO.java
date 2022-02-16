package com.yusys.bione.plugin.detailtmp.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpInfo;

@SuppressWarnings("serial")
public class RptDetailTmpInfoVO extends RptDetailTmpInfo{
	private String catalogNm;

	public RptDetailTmpInfoVO(RptDetailTmpInfo info,String catalogNm){
		BeanUtils.copy(info, this);
		this.setCatalogNm(catalogNm);
	}
	public String getCatalogNm() {
		return catalogNm;
	}

	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}
	
}
