package com.yusys.biapp.input.data.web;

import com.yusys.bione.comp.service.BaseBS;

public class MPTRBS<T extends IMPTRNode> extends BaseBS<T> {
	// private String leftName = "lft";
	// private String rightName = "rgt";
	// private String idName = "id";
	// private String upIdName = "upId";
	
	public void setParam(String leftName, String rightName, String idName, String upIdName) {
		/*
		if (StringUtils.isNotEmpty(leftName)) {
			this.leftName = leftName;
		}
		if (StringUtils.isNotEmpty(rightName)) {
			this.rightName = rightName;
		}
		if (StringUtils.isNotEmpty(idName)) {
			this.idName = idName;
		}
		if (StringUtils.isNotEmpty(upIdName)) {
			this.upIdName = upIdName;
		}
		*/
	}
	

	public void setLeftName(String leftName) {
		setParam(leftName, null, null, null);
	}


	public void setRightName(String rightName) {
		setParam(null, rightName, null, null);
	}


	public void setIdName(String idName) {
		setParam(null, null, idName, null);
	}


	public void setUpIdName(String upIdName) {
		setParam(null, null, null, upIdName);
	}


	public void addNode(T parent, T node) {
		
	}
}
