package com.yusys.bione.plugin.datashow.web.vo;

import java.util.List;

import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDetail;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimFilter;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;

public class FavIdxVO {
	private RptIdxInfo rptIdxInfo;
	private RptFavIdxDetail rptFavIdxDetail;
	private List<RptFavIdxDimFilter> rptFavIdxDimFilterList;

	public FavIdxVO() {
	}

	public FavIdxVO(RptIdxInfo rptIdxInfo, RptFavIdxDetail rptFavIdxDetail) {
		this.rptIdxInfo = rptIdxInfo;
		this.rptFavIdxDetail = rptFavIdxDetail;
	}

	public RptIdxInfo getRptIdxInfo() {
		return rptIdxInfo;
	}

	public void setRptIdxInfo(RptIdxInfo rptIdxInfo) {
		this.rptIdxInfo = rptIdxInfo;
	}

	public RptFavIdxDetail getRptFavIdxDetail() {
		return rptFavIdxDetail;
	}

	public void setRptFavIdxDetail(RptFavIdxDetail rptFavIdxDetail) {
		this.rptFavIdxDetail = rptFavIdxDetail;
	}

	public List<RptFavIdxDimFilter> getRptFavIdxDimFilterList() {
		return rptFavIdxDimFilterList;
	}

	public void setRptFavIdxDimFilterList(
			List<RptFavIdxDimFilter> rptFavIdxDimFilterList) {
		this.rptFavIdxDimFilterList = rptFavIdxDimFilterList;
	}

}
