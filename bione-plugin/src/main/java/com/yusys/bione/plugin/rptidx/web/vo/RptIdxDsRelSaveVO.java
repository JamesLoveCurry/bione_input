package com.yusys.bione.plugin.rptidx.web.vo;

import java.util.List;

import com.yusys.bione.plugin.rptidx.entity.RptIdxDsDimFilter;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDsRel;

@SuppressWarnings("serial")
public class RptIdxDsRelSaveVO extends RptIdxDsRel{
	
	private List<RptIdxDsDimFilter> filters;

	public List<RptIdxDsDimFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<RptIdxDsDimFilter> filters) {
		this.filters = filters;
	}
}
