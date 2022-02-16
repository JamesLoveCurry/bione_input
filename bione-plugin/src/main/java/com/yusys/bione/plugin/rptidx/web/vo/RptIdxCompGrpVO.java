package com.yusys.bione.plugin.rptidx.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCompGrp;

@SuppressWarnings("serial")
public class RptIdxCompGrpVO extends RptIdxCompGrp{

	private String indexNm;
	
	public RptIdxCompGrpVO(RptIdxCompGrp grp,String indexNm){
		BeanUtils.copy(grp, this);
		this.setIndexNm(indexNm);
	}
	
	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}
	
}
