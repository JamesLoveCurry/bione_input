package com.yusys.bione.plugin.valid.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRel;

@SuppressWarnings("serial")
public class RptValidLogicIdxRelVO extends RptValidLogicIdxRel{
	
	private String checkNm;

	public RptValidLogicIdxRelVO(RptValidLogicIdxRel rel, String checkNm) {
		BeanUtils.copy(rel, this);
		this.checkNm = checkNm;
	}

	public String getCheckNm() {
		return checkNm;
	}

	public void setCheckNm(String checkNm) {
		this.checkNm = checkNm;
	}
	
}
