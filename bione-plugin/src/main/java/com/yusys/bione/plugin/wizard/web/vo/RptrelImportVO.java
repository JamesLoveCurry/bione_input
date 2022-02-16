package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="报表数据集映射")
public class RptrelImportVO implements Serializable{
	@ExcelColumn(index = "A", name = "报表编号")
	@BioneFieldValid(nullable=false)
	private String rptNum;
	@ExcelColumn(index = "B", name = "报表名称")
	private String rptNm;
	@ExcelColumn(index = "C", name = "数据集名称")
	private String setNm;
	@ExcelColumn(index = "D", name = "数据集物理名称")
	@BioneFieldValid(nullable=false)
	private String tableEnNm;
	@ExcelColumn(index = "E", name = "数据项名称")
	private String cnNm;
	@ExcelColumn(index = "F", name = "数据项物理名称")
	@BioneFieldValid(nullable=false)
	private String enNm;
	@ExcelColumn(index = "G", name = "数据项类型", value={"01","02"} ,text={"指标","维度"})
	@BioneFieldValid(nullable=false, comboVals={"01","02"})
	private String dataItemType;
	@ExcelColumn(index = "H", name = "维度名称")
	private String dimTypeNm;
	@ExcelColumn(index = "I", name = "来源指标代码")
	private String indexNo;
	@ExcelColumn(index = "J", name = "指标过滤条件")
	private String idxFilter;
	private String dimNo;
	private String dimNm;
	private String colType;
	private String filterVal;
	private int excelRowNo;
	private String sheetName;
	private Map<String, Object> idxFilterDetail;

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	public String getSetNm() {
		return setNm;
	}

	public void setSetNm(String setNm) {
		this.setNm = setNm;
	}

	public String getTableEnNm() {
		return tableEnNm;
	}

	public void setTableEnNm(String tableEnNm) {
		this.tableEnNm = tableEnNm;
	}

	public String getCnNm() {
		return cnNm;
	}

	public void setCnNm(String cnNm) {
		this.cnNm = cnNm;
	}

	public String getEnNm() {
		return enNm;
	}

	public void setEnNm(String enNm) {
		this.enNm = enNm;
	}

	public String getDataItemType() {
		return dataItemType;
	}

	public void setDataItemType(String dataItemType) {
		this.dataItemType = dataItemType;
	}

	public String getDimTypeNm() {
		return dimTypeNm;
	}

	public void setDimTypeNm(String dimTypeNm) {
		this.dimTypeNm = dimTypeNm;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getIdxFilter() {
		return idxFilter;
	}

	public void setIdxFilter(String idxFilter) {
		this.idxFilter = idxFilter;
		String str = idxFilter;
		Map<String, Object> mp = Maps.newLinkedHashMap();
		if (StringUtils.isNotEmpty(str)) {
			String arr[] = StringUtils.split(str, '\n');
			String key = null, item = null, val = null;
			String[] value = null;
			for (int i = 0; i < arr.length; i++) {
				item = arr[i];
				key = StringUtils.substringBefore(item, ":");
				val = StringUtils.substringAfter(item, ":");
				value = StringUtils.split(val, ',');
				mp.put(key, value);
			}
			this.idxFilterDetail = mp;
		}
	}
	
	public Map<String, Object> getIdxFilterDetail() {
		return idxFilterDetail;
	}

	public void setIdxFilterDetail(Map<String, Object> idxFilterDetail) {
		this.idxFilterDetail = idxFilterDetail;
	}

	public String getDimNo() {
		return dimNo;
	}

	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}

	public String getFilterVal() {
		return filterVal;
	}

	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
		setDataItemType(colType);
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

	public String getDimNm() {
		return dimNm;
	}

	public void setDimNm(String dimNm) {
		this.dimNm = dimNm;
	}
}
