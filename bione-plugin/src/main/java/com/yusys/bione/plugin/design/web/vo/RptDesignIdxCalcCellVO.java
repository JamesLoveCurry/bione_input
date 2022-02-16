/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

import java.io.Serializable;

/**
 * <pre>
 * Title:表间取数
 * Description: 表间取数 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@ExcelSheet(index="3",name="单元格信息",firstRow=2,firstCol=2,titleFlag = true,insertType ="02",extType = "01",relInfo = {"cellNo","formulaDesc"})
public class RptDesignIdxCalcCellVO implements Serializable{

	private static final long serialVersionUID = 2807002950378010523L;
	
	@ExcelColumn(index = "0", name = "单元格编号",colTitle = "表间取数单元格")
	private String cellNo;
	@ExcelColumn(index = "1", name = "单元格名称",colTitle = "表间取数单元格")
	private String cellNm;
	private String dataType;
	private String isUpt;
	private String isNull;
	private String dataLen;
	@ExcelColumn(index = "4", name = "数据精度",colTitle = "表间取数单元格")
	private String dataPrecision;
	@ExcelColumn(index = "5", name = "显示格式",colTitle = "表间取数单元格",value={"01","02","03","04","05","06","07"},text={"金额","比例","数值","文本","日期","百分点","小数"})
	private String displayFormat;
	@ExcelColumn(index = "3", name = "数据单位",colTitle = "表间取数单元格",value={"","00","01","02","03","04","05"},text={"模板单位","无单位","个","百","千","万","亿"})
	private String dataUnit;
	@ExcelColumn(index = "10", name = "业务口径",colTitle = "表间取数单元格")
	private String caliberExplain;
	@ExcelColumn(index = "11", name = "技术口径",colTitle = "表间取数单元格")
	private String caliberTechnology;
	@ExcelColumn(index = "12", name = "备注说明",colTitle = "表间取数单元格")
	private String remark;
	private String rowId;
	private String colId;
	private String busiNo;
	@ExcelColumn(index = "9", name = "报表指标编号",colTitle = "表间取数单元格")
	private String realIndexNo;  // 报表指标对应的指标编号
	@ExcelColumn(index = "2", name = "来源指标编号",colTitle = "表间取数单元格")
	private String indexNo; // 引用指标
	@ExcelColumn(index = "7", name = "公式配置信息",colTitle = "表间取数单元格")
	private String formulaContent;
	@ExcelColumn(index = "6", name = "公式信息",colTitle = "表间取数单元格")
	private String formulaDesc;
	private String formulaDims; 
	@ExcelColumn(index = "8", name = "是否跑数汇总",colTitle = "表间取数单元格",value={"Y","N"},text={"是","否"})
	private String isSum;
	@ExcelColumn(index = "13", name = "是否填报汇总",colTitle = "表间取数单元格",value={"Y","N"},text={"是","否"})
	private String isFillSum;
	private String isPassyear;
	private String indexVerId;
	/**
	 * 
	 */
	public RptDesignIdxCalcCellVO() {
		super();
	}

	
	public String getIsFillSum() {
		return isFillSum;
	}


	public void setIsFillSum(String isFillSum) {
		this.isFillSum = isFillSum;
	}


	/**
	 * @return 返回 cellNo。
	 */
	public String getCellNo() {
		return cellNo;
	}

	/**
	 * @param cellNo 设置 cellNo。
	 */
	public void setCellNo(String cellNo) {
		this.cellNo = cellNo;
	}

	/**
	 * @return 返回 cellNm。
	 */
	public String getCellNm() {
		return cellNm;
	}

	/**
	 * @param cellNm 设置 cellNm。
	 */
	public void setCellNm(String cellNm) {
		this.cellNm = cellNm;
	}

	/**
	 * @return 返回 dataType。
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType 设置 dataType。
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return 返回 isUpt。
	 */
	public String getIsUpt() {
		return isUpt;
	}

	/**
	 * @param isUpt 设置 isUpt。
	 */
	public void setIsUpt(String isUpt) {
		this.isUpt = isUpt;
	}

	/**
	 * @return 返回 isNull。
	 */
	public String getIsNull() {
		return isNull;
	}

	/**
	 * @param isNull 设置 isNull。
	 */
	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	/**
	 * @return 返回 displayFormat。
	 */
	public String getDisplayFormat() {
		return displayFormat;
	}

	/**
	 * @param displayFormat 设置 displayFormat。
	 */
	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	/**
	 * @return 返回 caliberExplain。
	 */
	public String getCaliberExplain() {
		return caliberExplain;
	}

	/**
	 * @param caliberExplain 设置 caliberExplain。
	 */
	public void setCaliberExplain(String caliberExplain) {
		this.caliberExplain = caliberExplain;
	}

	/**
	 * @return 返回 dataLen。
	 */
	public String getDataLen() {
		return dataLen;
	}

	/**
	 * @param dataLen 设置 dataLen。
	 */
	public void setDataLen(String dataLen) {
		this.dataLen = dataLen;
	}

	/**
	 * @return 返回 dataPrecision。
	 */
	public String getDataPrecision() {
		return dataPrecision;
	}

	/**
	 * @param dataPrecision 设置 dataPrecision。
	 */
	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	/**
	 * @return 返回 rowId。
	 */
	public String getRowId() {
		return rowId;
	}

	/**
	 * @param rowId 设置 rowId。
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	/**
	 * @return 返回 colId。
	 */
	public String getColId() {
		return colId;
	}

	/**
	 * @param colId 设置 colId。
	 */
	public void setColId(String colId) {
		this.colId = colId;
	}

	/**
	 * @return 返回 formulaContent。
	 */
	public String getFormulaContent() {
		return formulaContent;
	}

	/**
	 * @param formulaContent 设置 formulaContent。
	 */
	public void setFormulaContent(String formulaContent) {
		this.formulaContent = formulaContent;
	}

	/**
	 * @return 返回 formulaDesc。
	 */
	public String getFormulaDesc() {
		return formulaDesc;
	}

	/**
	 * @param formulaDesc 设置 formulaDesc。
	 */
	public void setFormulaDesc(String formulaDesc) {
		this.formulaDesc = formulaDesc;
	}

	/**
	 * @return 返回 realIndexNo。
	 */
	public String getRealIndexNo() {
		return realIndexNo;
	}

	/**
	 * @param realIndexNo 设置 realIndexNo。
	 */
	public void setRealIndexNo(String realIndexNo) {
		this.realIndexNo = realIndexNo;
	}

	/**
	 * @return 返回 indexNo。
	 */
	public String getIndexNo() {
		return indexNo;
	}

	/**
	 * @param indexNo 设置 indexNo。
	 */
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	/**
	 * @return 返回 formulaDims。
	 */
	public String getFormulaDims() {
		return formulaDims;
	}

	/**
	 * @param formulaDims 设置 formulaDims。
	 */
	public void setFormulaDims(String formulaDims) {
		this.formulaDims = formulaDims;
	}

	/**
	 * @return 返回 dataUnit。
	 */
	public String getDataUnit() {
		return dataUnit;
	}

	/**
	 * @param dataUnit 设置 dataUnit。
	 */
	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	/**
	 * @return 返回 busiNo。
	 */
	public String getBusiNo() {
		return busiNo;
	}

	/**
	 * @param busiNo 设置 busiNo。
	 */
	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getIsSum() {
		return isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}
	
	public String getCaliberTechnology() {
		return caliberTechnology;
	}

	public void setCaliberTechnology(String caliberTechnology) {
		this.caliberTechnology = caliberTechnology;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIsPassyear() {
		return isPassyear;
	}

	public void setIsPassyear(String isPassyear) {
		this.isPassyear = isPassyear;
	}

	public String getIndexVerId() {
		return indexVerId;
	}

	public void setIndexVerId(String indexVerId) {
		this.indexVerId = indexVerId;
	}
}
