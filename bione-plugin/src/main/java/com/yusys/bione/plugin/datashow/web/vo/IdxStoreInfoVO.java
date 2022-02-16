package com.yusys.bione.plugin.datashow.web.vo;

import java.util.List;

import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDim;
import com.yusys.bione.plugin.rptfav.web.vo.FavIdxDetailAndIndexNm;
import com.yusys.bione.plugin.rptfav.web.vo.FavIdxDimFilterAndName;

public class IdxStoreInfoVO{
	List<FavIdxDetailAndIndexNm> detailList;
	List<RptFavIdxDim> dimList;
	List<FavIdxDimFilterAndName> filterList;
	List<RptDimTypeInfo> dimTypeList;
	
	public List<RptDimTypeInfo> getDimTypeList() {
		return dimTypeList;
	}
	public void setDimTypeList(List<RptDimTypeInfo> dimTypeList) {
		this.dimTypeList = dimTypeList;
	}
	public List<FavIdxDetailAndIndexNm> getDetailList() {
		return detailList;
	}
	public void setDetailList(List<FavIdxDetailAndIndexNm> detailList) {
		this.detailList = detailList;
	}
	public List<RptFavIdxDim> getDimList() {
		return dimList;
	}
	public void setDimList(List<RptFavIdxDim> dimList) {
		this.dimList = dimList;
	}
	public List<FavIdxDimFilterAndName> getFilterList() {
		return filterList;
	}
	public void setFilterList(List<FavIdxDimFilterAndName> filterList) {
		this.filterList = filterList;
	}
	
	
}
