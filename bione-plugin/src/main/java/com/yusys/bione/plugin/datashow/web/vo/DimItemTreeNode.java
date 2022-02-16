package com.yusys.bione.plugin.datashow.web.vo;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;

public class DimItemTreeNode extends CommonTreeNode {
	private static final long serialVersionUID = 1L;
	public DimItemTreeNode() {
	}
	
	public DimItemTreeNode(RptDimItemInfo rptDimItemInfo) {
		this.setText(rptDimItemInfo.getDimItemNm());
		this.setId(rptDimItemInfo.getId().getDimItemNo());
		this.setUpId(rptDimItemInfo.getUpNo());
		this.setData(rptDimItemInfo);
	}
}
