package com.yusys.bione.plugin.regulation.vo;

import java.util.List;
import java.util.Map;

public class ReportInfoDetailsVO {
	
	private String lineId; // 条线ID
	
	private String templateId;//模板ID
	
	private String tmpJson;//excel样式模板
	
	private String tmpRemark;//模板备注
	
	private List<Map<String,Object>> idxsArray;//指标单元格集合
	
	private List<Map<String,Object>> cellsArray;//一般单元格集合
	
	private List<Map<String,Object>> rowCols;//

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTmpJson() {
		return tmpJson;
	}

	public void setTmpJson(String tmpJson) {
		this.tmpJson = tmpJson;
	}

	public String getTmpRemark() {
		return tmpRemark;
	}

	public void setTmpRemark(String tmpRemark) {
		this.tmpRemark = tmpRemark;
	}

	public List<Map<String, Object>> getIdxsArray() {
		return idxsArray;
	}

	public void setIdxsArray(List<Map<String, Object>> idxsArray) {
		this.idxsArray = idxsArray;
	}

	public List<Map<String, Object>> getCellsArray() {
		return cellsArray;
	}

	public void setCellsArray(List<Map<String, Object>> cellsArray) {
		this.cellsArray = cellsArray;
	}

	public List<Map<String, Object>> getRowCols() {
		return rowCols;
	}

	public void setRowCols(List<Map<String, Object>> rowCols) {
		this.rowCols = rowCols;
	}
	
	

} 
