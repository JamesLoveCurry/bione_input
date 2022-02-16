package com.yusys.bione.plugin.rptmgr.web.vo;

import java.util.List;

import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleIdxRel;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrIdxFilterVO;


@SuppressWarnings("serial")
public class RptMgrModuleIdxRelVO extends RptMgrModuleIdxRel{
	private String colId;
	private String enNm;
	private List<RptMgrIdxFilterVO>  filters;

	private String indexNm;

	public List<RptMgrIdxFilterVO> getFilters() {
		return filters;
	}

	public void setFilters(List<RptMgrIdxFilterVO> filters) {
		this.filters = filters;
	}


	public String getColId() {
		return colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getEnNm() {
		return enNm;
	}

	public void setEnNm(String enNm) {
		this.enNm = enNm;
	}


}
