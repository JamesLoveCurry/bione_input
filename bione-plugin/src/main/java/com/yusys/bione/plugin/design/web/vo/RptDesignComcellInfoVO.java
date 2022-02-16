package com.yusys.bione.plugin.design.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

@ExcelSheet(index="3",name="单元格信息",firstRow=2,firstCol=2,titleFlag = true,insertType ="02",extType = "01",relInfo = {"cellNo"})
@SuppressWarnings("serial")
public class RptDesignComcellInfoVO implements Serializable{
	
	@ExcelColumn(index = "0", name = "单元格编号",colTitle = "一般单元格")
	private String cellNo;
	
	private BigDecimal colId;

	private BigDecimal rowId;
	
	@ExcelColumn(index = "1", name = "单元格属性",colTitle = "一般单元格",relDs={"com.yusys.bione.plugin.design.entity.RptDesignComcellType","typeId","typeNm"})
	private String typeId;
	
	@ExcelColumn(index = "2", name = "单元格内容",colTitle = "一般单元格")
	private String content;

	public String getCellNo() {
		return cellNo;
	}

	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	public BigDecimal getColId() {
		return colId;
	}

	public void setColId(BigDecimal colId) {
		this.colId = colId;
	}

	public BigDecimal getRowId() {
		return rowId;
	}

	public void setRowId(BigDecimal rowId) {
		this.rowId = rowId;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	

}
