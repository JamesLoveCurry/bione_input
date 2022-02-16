package com.yusys.bione.plugin.datashow.web.vo;

import com.yusys.bione.comp.common.CommonTreeNode;

public class IdxTreeNode extends CommonTreeNode {
	private boolean open = false;
	private static final long serialVersionUID = 1L;

	public void setRealId(String id) {
		getParams().put("realId", id);
	}
	
	public void setIndexVerId(String indexVerId) {
		getParams().put("indexVerId", indexVerId);
	}

	public void setType(String type) {
		getParams().put("type", type);
	}
	
	public void setIdxType(String idxType) {
		getParams().put("idxType", idxType);
	}
	
	public boolean getOpen() {
		return this.open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
}
