package com.yusys.bione.plugin.rptidx.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptidx.entity.RptIdxSimilarGrp;

@SuppressWarnings("serial")
public class RptIdxSimiGrpVO extends RptIdxSimilarGrp{

	private String indexNm;
	
	public RptIdxSimiGrpVO(RptIdxSimilarGrp grp,String indexNm){
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
