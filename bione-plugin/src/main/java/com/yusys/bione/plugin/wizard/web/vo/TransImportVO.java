package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="模型转换信息")
public class TransImportVO implements Serializable,AnnotationValidable{
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "目标表英文名",relDs={"com.yusys.bione.frame.datamodel.entity.RptSysModuleInfo","setId","tableEnNm","SOURCE_ID = '1'"})
	private String setId;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "目标表字段名")
	private String colEnNm;
	
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "C", name = "来源表英文名",relDs={"com.yusys.bione.frame.datamodel.entity.RptSysModuleInfo","setId","tableEnNm","SOURCE_ID = '1'"})
	private String srcSetId;
	
	@ExcelColumn(index = "D", name = "来源表字段名")
	private String srcColEnNm;
	
	@ExcelColumn(index = "E", name = "转换规则")
	private String transRule;
	
	@ExcelColumn(index = "F", name = "WHERE条件")
	private String srcDataFilterCond;
	
	@ExcelColumn(index = "G", name = "备注")
	private String remark;

	private Integer excelRowNo;
	
	private String sheetName;

	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getSrcSetId() {
		return srcSetId;
	}

	public void setSrcSetId(String srcSetId) {
		this.srcSetId = srcSetId;
	}

	public String getTransRule() {
		return transRule;
	}

	public void setTransRule(String transRule) {
		this.transRule = transRule;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getColEnNm() {
		return colEnNm;
	}

	public void setColEnNm(String colEnNm) {
		this.colEnNm = colEnNm;
	}

	public String getSrcColEnNm() {
		return srcColEnNm;
	}

	public void setSrcColEnNm(String srcColEnNm) {
		this.srcColEnNm = srcColEnNm;
	}

	public Integer getExcelRowNo() {
		return excelRowNo;
	}

	public void setExcelRowNo(Integer excelRowNo) {
		this.excelRowNo = excelRowNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getSrcDataFilterCond() {
		return srcDataFilterCond;
	}

	public void setSrcDataFilterCond(String srcDataFilterCond) {
		this.srcDataFilterCond = srcDataFilterCond;
	}

}
