package com.yusys.bione.plugin.valid.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@ExcelSheet(index="0",name="SQL Results",firstRow=1)
public class ExcelOuterVO implements Serializable,AnnotationValidable{
	
	private static final long serialVersionUID = 1L;

	@ExcelColumn(index = "A", name = "报表名称")
	private String rptNm;
	
	@ExcelColumn(index = "B", name = "版本号")
	private String version;
	
	@ExcelColumn(index = "C", name = "校验公式")
	private String expression;
	
	@ExcelColumn(index = "D", name = "CELL_FORMU_VIEW")
	private String ruls;
	
	@ExcelColumn(index = "F", name = "出错原因")
	private String reason;
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	private int excelRowNo;
	private String sheetName;
	
	@ExcelColumn(index = "E", name = "是否匹配", value={"Y","N"},text={"已匹配","未匹配"})
	private String isCheck = "N";
	

	public String getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}

	public int getExcelRowNo() {
		return excelRowNo;
	}

	public void setExcelRowNo(int excelRowNo) {
		this.excelRowNo = excelRowNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getRuls() {
		return ruls;
	}

	public void setRuls(String ruls) {
		this.ruls = ruls;
	}
	
}
