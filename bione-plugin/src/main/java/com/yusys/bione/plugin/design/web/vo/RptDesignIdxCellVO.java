/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;
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
@ExcelSheet(index="3",name="单元格信息",firstRow=2,firstCol=2,titleFlag = true,insertType ="02",extType = "01",relInfo = {"cellNo","indexNm"})
public class RptDesignIdxCellVO implements Serializable{

	private static final long serialVersionUID = 2807002950378010523L;
	
	@ExcelColumn(index = "0", name = "单元格编号",colTitle = "指标单元格")
	private String cellNo;
	@ExcelColumn(index = "1", name = "单元格名称",colTitle = "指标单元格")
	private String cellNm;
	private String dataType;
	private String isUpt;
	private String isNull;
	private String dataLen;
	@ExcelColumn(index = "6", name = "显示格式",colTitle = "指标单元格",value={"01","02","03","04","05","06","07","08"},text={"金额","比例","数值","文本","日期","百分点","小数","比例无百分号"})
	private String displayFormat;
	@ExcelColumn(index = "7", name = "数据单位",colTitle = "指标单元格",value={"","00","01","02","03","04","05"},text={"模板单位","无单位","个","百","千","万","亿"})
	private String dataUnit;
	@ExcelColumn(index = "8", name = "数据精度",colTitle = "指标单元格")
	private String dataPrecision;
	@ExcelColumn(index = "15", name = "业务口径",colTitle = "指标单元格")
	private String caliberExplain;
	@ExcelColumn(index = "16", name = "技术口径",colTitle = "指标单元格")
	private String caliberTechnology;
	@ExcelColumn(index = "17", name = "备注说明",colTitle = "指标单元格")
	private String remark;
	private String rowId;
	private String colId;
	@ExcelColumn(index = "13", name = "人行编码",colTitle = "指标单元格")
	private String busiNo;
	// 指标属性
	private String allDims;
	private String factDims;
	@ExcelColumn(index = "14", name = "报表指标编号",colTitle = "指标单元格")
	private String realIndexNo;
	private String realIndexNm;
	@ExcelColumn(index = "2", name = "来源指标编号",colTitle = "指标单元格")
	private String indexNo;
	@ExcelColumn(index = "3", name = "来源指标名称",colTitle = "指标单元格")
	private String indexNm;
	private String indexVerId;
	@ExcelColumn(index = "4", name = "是否跑数汇总",colTitle = "指标单元格",value={"","Y","N"},text={"是","是","否"})
	private String isSum;
	@ExcelColumn(index = "18", name = "是否填报汇总",colTitle = "指标单元格",value={"","Y","N"},text={"是","是","否"})
	private String isFillSum;
	@ExcelColumn(index = "5", name = "是否落地",colTitle = "指标单元格",value={"","Y","N"},text={"是","是","否"})
	private String isSave;
	private String statType;
	private String measureNo;
	private String busiType;
	@ExcelColumn(index = "9", name = "计算规则",colTitle = "指标单元格",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxCalcRule","ruleId","ruleNm"})
	private String ruleId;
	@ExcelColumn(index = "10", name = "时间度量",colTitle = "指标单元格",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxTimeMeasure","timeMeasureId","measureNm"})
	private String timeMeasureId;
	@ExcelColumn(index = "11", name = "取值方式",colTitle = "指标单元格",relDs={"com.yusys.bione.plugin.rptidx.entity.RptIdxValType","modeId","modeNm"})
	private String modeId;
	@ExcelColumn(index = "12", name = "是否跨年",colTitle = "指标单元格",value={"","Y","N"},text={"","是","否"})
	private String isPassyear;
	private JSONArray filtInfos;
	private String dsId;
	private String isLock;

	public String getIsLock() {
		return isLock;
	}

	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}

	public String getIsFillSum() {
		return isFillSum;
	}

	public void setIsFillSum(String isFillSum) {
		this.isFillSum = isFillSum;
	}

	/**
	 * 
	 */
	public RptDesignIdxCellVO() {
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
	 * @return 返回 allDims。
	 */
	public String getAllDims() {
		return allDims;
	}

	/**
	 * @param allDims 设置 allDims。
	 */
	public void setAllDims(String allDims) {
		this.allDims = allDims;
	}

	/**
	 * @return 返回 factDims。
	 */
	public String getFactDims() {
		return factDims;
	}

	/**
	 * @param factDims 设置 factDims。
	 */
	public void setFactDims(String factDims) {
		this.factDims = factDims;
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
	 * @return 返回 indexNm。
	 */
	public String getIndexNm() {
		return indexNm;
	}

	/**
	 * @param indexNm 设置 indexNm。
	 */
	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	/**
	 * @return 返回 indexVerId。
	 */
	public String getIndexVerId() {
		return indexVerId;
	}

	/**
	 * @param indexVerId 设置 indexVerId。
	 */
	public void setIndexVerId(String indexVerId) {
		this.indexVerId = indexVerId;
	}

	/**
	 * @return 返回 isSum。
	 */
	public String getIsSum() {
		return isSum;
	}

	/**
	 * @param isSum 设置 isSum。
	 */
	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}

	/**
	 * @return 返回 measureNo。
	 */
	public String getMeasureNo() {
		return measureNo;
	}

	/**
	 * @param measureNo 设置 measureNo。
	 */
	public void setMeasureNo(String measureNo) {
		this.measureNo = measureNo;
	}

	/**
	 * @return 返回 filtInfos。
	 */
	public JSONArray getFiltInfos() {
		return filtInfos;
	}

	/**
	 * @param filtInfos 设置 filtInfos。
	 */
	public void setFiltInfos(JSONArray filtInfos) {
		this.filtInfos = filtInfos;
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
	 * @return 返回 busiType。
	 */
	public String getBusiType() {
		return busiType;
	}

	/**
	 * @param busiType 设置 busiType。
	 */
	public void setBusiType(String busiType) {
		this.busiType = busiType;
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

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	/**
	 * @return 返回 realIndexNm。
	 */
	public String getRealIndexNm() {
		return realIndexNm;
	}

	/**
	 * @param realIndexNm 设置 realIndexNm。
	 */
	public void setRealIndexNm(String realIndexNm) {
		this.realIndexNm = realIndexNm;
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

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getTimeMeasureId() {
		return timeMeasureId;
	}

	public void setTimeMeasureId(String timeMeasureId) {
		this.timeMeasureId = timeMeasureId;
	}

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	public String getIsPassyear() {
		return isPassyear;
	}

	public void setIsPassyear(String isPassyear) {
		this.isPassyear = isPassyear;
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
