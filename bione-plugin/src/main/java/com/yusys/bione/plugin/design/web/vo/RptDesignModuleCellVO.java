/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@ExcelSheet(index="3",name="单元格信息",firstRow=2,firstCol=2,titleFlag = true,insertType ="02",extType = "01",relInfo = {"cellNo","columnName"})
public class RptDesignModuleCellVO implements Serializable{

	private static final long serialVersionUID = 2807002950378010523L;
	
	@ExcelColumn(index = "0", name = "单元格编号",colTitle = "字段单元格")
	private String cellNo;
	@ExcelColumn(index = "1", name = "单元格名称",colTitle = "字段单元格")
	private String cellNm;
	private String dataType;
	private String isUpt;
	private String isNull;
	private String dataLen;
	@ExcelColumn(index = "14", name = "数据精度",colTitle = "字段单元格")
	private String dataPrecision;
	@ExcelColumn(index = "12", name = "显示格式",colTitle = "字段单元格",value={"01","02","03","04","05","06","07"},text={"金额","比例","数值","文本","日期","百分点","小数"})
	private String displayFormat;
	@ExcelColumn(index = "13", name = "数据单位",colTitle = "字段单元格",value={"","00","01","02","03","04","05"},text={"模板单位","无单位","个","百","千","万","亿"})
	private String dataUnit;
	@ExcelColumn(index = "14", name = "业务口径",colTitle = "字段单元格")
	private String caliberExplain;
	@ExcelColumn(index = "15", name = "技术口径",colTitle = "字段单元格")
	private String caliberTechnology;
	@ExcelColumn(index = "16", name = "备注说明",colTitle = "字段单元格")
	private String remark;
	private String rowId;
	private String colId;
	private String busiNo;
	//
	private String dsId;
	@ExcelColumn(index = "2", name = "模型名称",colTitle = "字段单元格")
	private String dsName;
	private String columnId;
	@ExcelColumn(index = "3", name = "字段名称",colTitle = "字段单元格")
	private String columnName;
	@ExcelColumn(index = "4", name = "是否扩展",colTitle = "字段单元格",value={"","Y","N"},text={"是","是","否"})
	private String isExt;
	@ExcelColumn(index = "5", name = "扩展方向",colTitle = "字段单元格",value={"01","02"},text={"行扩展","列扩展"})
	private String extDirection;
	@ExcelColumn(index = "6", name = "扩展方式",colTitle = "字段单元格",value={"01","02"},text={"插入","覆盖"})
	private String extMode;
	@ExcelColumn(index = "7", name = "是否排序",colTitle = "字段单元格",value={"Y","N"},text={"是","否"})
	private String isSort;
	@ExcelColumn(index = "8", name = "排序方式",colTitle = "字段单元格",value={"01","02"},text={"顺序","倒序"})
	private String sortMode;
	@ExcelColumn(index = "9", name = "排序顺序",colTitle = "字段单元格")
	private String sortOrder;
	@ExcelColumn(index = "10", name = "是否转码",colTitle = "字段单元格")
	private String isConver;
	@ExcelColumn(index = "11", name = "汇总方式",colTitle = "字段单元格",value={"00","01"},text={"不汇总","求和"})
	private String sumMode;
	private String isPk;
	private String isMerge;
	private String isMergeCol;
	
	/**
	 * 
	 */
	public RptDesignModuleCellVO() {
		super();
	}
	

	public String getIsMergeCol() {
		return isMergeCol;
	}


	public void setIsMergeCol(String isMergeCol) {
		this.isMergeCol = isMergeCol;
	}
	
	public String getIsMerge() {
		return isMerge;
	}


	public void setIsMerge(String isMerge) {
		this.isMerge = isMerge;
	}

	public String getIsPk() {
		return isPk;
	}


	public void setIsPk(String isPk) {
		this.isPk = isPk;
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
	 * @return 返回 dsId。
	 */
	public String getDsId() {
		return dsId;
	}

	/**
	 * @param dsId 设置 dsId。
	 */
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	/**
	 * @return 返回 columnId。
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * @param columnId 设置 columnId。
	 */
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	/**
	 * @return 返回 isExt。
	 */
	public String getIsExt() {
		return isExt;
	}

	/**
	 * @param isExt 设置 isExt。
	 */
	public void setIsExt(String isExt) {
		this.isExt = isExt;
	}

	/**
	 * @return 返回 extDirection。
	 */
	public String getExtDirection() {
		return extDirection;
	}

	/**
	 * @param extDirection 设置 extDirection。
	 */
	public void setExtDirection(String extDirection) {
		this.extDirection = extDirection;
	}

	/**
	 * @return 返回 extMode。
	 */
	public String getExtMode() {
		return extMode;
	}

	/**
	 * @param extMode 设置 extMode。
	 */
	public void setExtMode(String extMode) {
		this.extMode = extMode;
	}

	/**
	 * @return 返回 dsName。
	 */
	public String getDsName() {
		return dsName;
	}

	/**
	 * @param dsName 设置 dsName。
	 */
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	/**
	 * @return 返回 columnName。
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName 设置 columnName。
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
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
	 * @return 返回 isSort。
	 */
	public String getIsSort() {
		return isSort;
	}

	/**
	 * @param isSort 设置 isSort。
	 */
	public void setIsSort(String isSort) {
		this.isSort = isSort;
	}

	/**
	 * @return 返回 sortMode。
	 */
	public String getSortMode() {
		return sortMode;
	}

	/**
	 * @param sortMode 设置 sortMode。
	 */
	public void setSortMode(String sortMode) {
		this.sortMode = sortMode;
	}

	/**
	 * @return 返回 sortOrder。
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param sortOrder 设置 sortOrder。
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
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

	public String getIsConver() {
		return isConver;
	}

	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}

	public String getSumMode() {
		return sumMode;
	}

	public void setSumMode(String sumMode) {
		this.sumMode = sumMode;
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
}
