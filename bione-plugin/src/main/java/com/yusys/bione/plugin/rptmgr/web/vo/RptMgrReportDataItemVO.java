package com.yusys.bione.plugin.rptmgr.web.vo;

import java.util.List;

import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportDataItem;

@SuppressWarnings("serial")
public class RptMgrReportDataItemVO extends RptMgrReportDataItem{
	private List<RptMgrRptitemIdxRelVO> Idxs;

	public List<RptMgrRptitemIdxRelVO> getIdxs() {
		return Idxs;
	}

	public void setIdxs(List<RptMgrRptitemIdxRelVO> Idxs) {
		this.Idxs = Idxs;
	}
}
