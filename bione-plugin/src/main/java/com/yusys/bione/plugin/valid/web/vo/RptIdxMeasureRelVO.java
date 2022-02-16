package com.yusys.bione.plugin.valid.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;

@SuppressWarnings("serial")
public class RptIdxMeasureRelVO extends RptIdxMeasureRel{
	
	private String measureNm;

	public RptIdxMeasureRelVO(RptIdxMeasureRel rel, String measureNm) {
		BeanUtils.copy(rel, this);
		this.measureNm = measureNm;
	}

	public String getMeasureNm() {
		return measureNm;
	}

	public void setMeasureNm(String measureNm) {
		this.measureNm = measureNm;
	}
	
}
