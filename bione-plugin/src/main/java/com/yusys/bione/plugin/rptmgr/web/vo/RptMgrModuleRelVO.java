package com.yusys.bione.plugin.rptmgr.web.vo;


import java.util.List;

import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleRel;


@SuppressWarnings("serial")
public class RptMgrModuleRelVO extends RptMgrModuleRel{
	
	private String setNm;
	private List<RptMgrModuleIdxRelVO> idxRels;
	public String getSetNm() {
		return setNm;
	}

	public void setSetNm(String setNm) {
		this.setNm = setNm;
	}

	public List<RptMgrModuleIdxRelVO> getIdxRels() {
		return idxRels;
	}

	public void setIdxRels(List<RptMgrModuleIdxRelVO> idxRels) {
		this.idxRels = idxRels;
	}
}
