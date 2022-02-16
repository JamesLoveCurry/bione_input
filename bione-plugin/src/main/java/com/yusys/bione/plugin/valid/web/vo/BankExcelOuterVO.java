package com.yusys.bione.plugin.valid.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@ExcelSheet(index="0",name="大集中校验公式",firstRow=1)
public class BankExcelOuterVO implements Serializable,AnnotationValidable{
	
	private static final long serialVersionUID = 1L;

	@ExcelColumn(index = "A", name = "表单代码")
	private String rptNm;
	
	@ExcelColumn(index = "B", name = "逻辑关系", value={"==", "<=", "<", ">=", ">", "!="}, text={"1", "2", "3", "4", "5", "6"})
	private String oper;
	
	@ExcelColumn(index = "C", name = "左公式")
	private String leftExpression;
	
	@ExcelColumn(index = "D", name = "右公式")
	private String rightExpression;
	
	@ExcelColumn(index = "E", name = "是否匹配", value={"Y","N"},text={"已匹配","未匹配"})
	private String isCheck = "N";
	
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

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getLeftExpression() {
		return leftExpression;
	}

	public void setLeftExpression(String leftExpression) {
		this.leftExpression = leftExpression;
	}

	public String getRightExpression() {
		return rightExpression;
	}

	public void setRightExpression(String rightExpression) {
		this.rightExpression = rightExpression;
	}

	public String getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}


}
