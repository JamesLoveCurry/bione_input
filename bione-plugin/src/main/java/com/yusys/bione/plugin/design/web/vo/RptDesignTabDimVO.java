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
@ExcelSheet(index="3",name="单元格信息",firstRow=2,firstCol=2,titleFlag = true,insertType ="02",extType = "01",relInfo = {"cellNo","dimTypeNm"})
public class RptDesignTabDimVO implements Serializable{

	private static final long serialVersionUID = 2807002950378010523L;
	
	@ExcelColumn(index = "0", name = "单元格编号",colTitle = "维度列表单元格")
	private String cellNo;
	@ExcelColumn(index = "1", name = "单元格名称",colTitle = "维度列表单元格")
	private String cellNm;
	private String dataType;
	private String isUpt;
	private String isNull;
	private String dataLen;
	private String dataPrecision;
	private String displayFormat;
	private String dataUnit;
	private String caliberExplain;
	private String caliberTechnology;
	private String remark;
	private String rowId;
	private String colId;
	private String busiNo;
	//列表维度信息
	@ExcelColumn(index = "2", name = "维度类型编号",colTitle = "维度列表单元格")
	private String dimTypeNo;
	@ExcelColumn(index = "3", name = "维度类型名称",colTitle = "维度列表单元格")
	private String dimTypeNm;
	private String dimType;
	@ExcelColumn(index = "4", name = "是否合计",colTitle = "维度列表单元格",value={"","Y","N"},text={"否","是","否"})
	private String isTotal;
	private String dateFormat;
	@ExcelColumn(index = "5", name = "是否转码",colTitle = "维度列表单元格",value={"","Y","N"},text={"是","是","否"})
	private String isConver;
	@ExcelColumn(index = "6", name = "显示级别",colTitle = "维度列表单元格")
	private String displayLevel;
	private String extDirection;
	
	/**
	 * 
	 */
	public RptDesignTabDimVO() {
		super();
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

	/**
	 * @return 返回 dimTypeNo。
	 */
	public String getDimTypeNo() {
		return dimTypeNo;
	}

	/**
	 * @param dimTypeNo 设置 dimTypeNo。
	 */
	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	/**
	 * @return 返回 isTotal。
	 */
	public String getIsTotal() {
		return isTotal;
	}

	/**
	 * @param isTotal 设置 isTotal。
	 */
	public void setIsTotal(String isTotal) {
		this.isTotal = isTotal;
	}

	/**
	 * @return 返回 dimTypeNm。
	 */
	public String getDimTypeNm() {
		return dimTypeNm;
	}

	/**
	 * @param dimTypeNm 设置 dimTypeNm。
	 */
	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}

	/**
	 * @return 返回 dimType。
	 */
	public String getDimType() {
		return dimType;
	}

	/**
	 * @param dimType 设置 dimType。
	 */
	public void setDimType(String dimType) {
		this.dimType = dimType;
	}

	/**
	 * @return 返回 dateFormat。
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat 设置 dateFormat。
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return 返回 isConver。
	 */
	public String getIsConver() {
		return isConver;
	}

	/**
	 * @param isConver 设置 isConver。
	 */
	public void setIsConver(String isConver) {
		this.isConver = isConver;
	}

	/**
	 * @return 返回 displayLevel。
	 */
	public String getDisplayLevel() {
		return displayLevel;
	}

	/**
	 * @param displayLevel 设置 displayLevel。
	 */
	public void setDisplayLevel(String displayLevel) {
		this.displayLevel = displayLevel;
	}

	public String getExtDirection() {
		return extDirection;
	}

	public void setExtDirection(String extDirection) {
		this.extDirection = extDirection;
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
