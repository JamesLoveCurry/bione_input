package com.yusys.bione.plugin.rptfav.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.yusys.bione.plugin.datashow.web.vo.DimItemTreeNode;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimFilter;

@SuppressWarnings("serial")
public class FavIdxDimFilterAndName extends RptFavIdxDimFilter{
	public String dimTypeNm;
	public List<DimItemTreeNode> selectNodes = new ArrayList<DimItemTreeNode>();
	
	public List<DimItemTreeNode> getSelectNodes() {
		return selectNodes;
	}

	public void setSelectNodes(List<DimItemTreeNode> selectNodes) {
		this.selectNodes = selectNodes;
	}
	
	public String getDimTypeNm() {
		return dimTypeNm;
	}

	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}
	
}
