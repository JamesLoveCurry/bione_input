package com.yusys.bione.plugin.valid.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;

@ExcelSheet(index="0",name="SQL Results",firstRow=1)
public class LogicAndTemplateVO extends RptValidCfgextLogic implements Comparable<LogicAndTemplateVO>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rptTemplateId;
	
	@ExcelColumn(index = "F", name = "替换后左公式")
	private String changedLE;
	
	@ExcelColumn(index = "G", name = "替换后右公式")
	private String changedRE;
	
	@ExcelColumn(index = "H", name = "替换后整个公式")
	private String changedExpreesion;
	
	@ExcelColumn(index = "I", name = "另外一种形式的校验公式")
	private String anotherFormExpression;
	
	@ExcelColumn(index = "J", name = "公式左右互换")
	private String expressonRL;
	
	@ExcelColumn(index = "K", name = "另外一种公式左右互换")
	private String anotherExpressionRL;

	@ExcelColumn(index = "A", name = "报表名称")
	private String rptNum;
	
	@ExcelColumn(index = "L", name = "是否匹配", value={"Y","N"},text={"已匹配","未匹配"})
	private String isCheck;
	
	@ExcelColumn(index = "M", name = "出错原因")
	private String reason;
	
	public String getExpressonRL() {
		return expressonRL;
	}

	public void setExpressonRL(String expressonRL) {
		this.expressonRL = expressonRL;
	}

	public String getAnotherExpressionRL() {
		return anotherExpressionRL;
	}

	public void setAnotherExpressionRL(String anotherExpressionRL) {
		this.anotherExpressionRL = anotherExpressionRL;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setAnotherFormExpression(String anotherFormExpression) {
		this.anotherFormExpression = anotherFormExpression;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAnotherFormExpression() {
		return anotherFormExpression;
	}


	public String getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public String getChangedExpreesion() {
		return changedExpreesion;
	}

	public void setChangedExpreesion(String changedExpreesion) {
		this.changedExpreesion = changedExpreesion;
	}

	public String getChangedLE() {
		return changedLE;
	}

	public void setChangedLE(String changedLE) {
		this.changedLE = changedLE;
	}

	public String getChangedRE() {
		return changedRE;
	}

	public void setChangedRE(String changedRE) {
		this.changedRE = changedRE;
	}

	public String getRptTemplateId() {
		return rptTemplateId;
	}

	public void setRptTemplateId(String rptTemplateId) {
		this.rptTemplateId = rptTemplateId;
	}
	
	public LogicAndTemplateVO(RptValidCfgextLogic logic, String templateId, String rptNum){
		this.rptTemplateId = templateId;
		this.setBusiExplain(logic.getBusiExplain());
		this.setCheckId(logic.getCheckId());
		this.setEndDate(logic.getEndDate());
		this.setExpressionDesc(logic.getExpressionDesc());
		this.setFloatVal(logic.getFloatVal());
		this.setIsPre(this.getIsPre());
		this.setIsSelfDef(logic.getIsSelfDef());
		this.setLeftExpression(logic.getLeftExpression());
		this.setLogicOperType(logic.getLogicOperType());
		this.setRightExpression(logic.getRightExpression());
		this.setStartDate(logic.getStartDate());
		this.setRptNum(rptNum);
	}

	@Override
	public int compareTo(LogicAndTemplateVO o) {
		if(this.getChangedLE().compareTo(o.getChangedLE()) == 0)
			return this.changedRE.compareTo(o.getChangedRE());
		return this.getChangedLE().compareTo(o.getChangedLE());//以左公式来排序
	}
	
}
