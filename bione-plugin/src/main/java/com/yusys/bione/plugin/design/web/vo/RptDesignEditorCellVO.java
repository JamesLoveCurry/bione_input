package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;

@SuppressWarnings("serial")
public class RptDesignEditorCellVO extends RptDesignCellInfo{
	private String cellVal;
	
	private String content;	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCellVal() {
		return cellVal;
	}

	public void setCellVal(String cellVal) {
		this.cellVal = cellVal;
	}
	
	
}
