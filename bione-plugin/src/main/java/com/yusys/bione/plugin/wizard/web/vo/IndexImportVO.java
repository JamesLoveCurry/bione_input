package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import com.yusys.bione.frame.validator.utils.enums.FieldTypes;

@SuppressWarnings("serial")
@ExcelSheet(index = "0",name="指标信息")
public class IndexImportVO implements Serializable, AnnotationValidable{
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "指标编码")
	private String indexNo;
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "B", name = "指标一级分类")
	private String firstCatalog;
	@ExcelColumn(index = "C", name = "指标二级分类")
	private String secondCatalog;
	@ExcelColumn(index = "D", name = "指标三级分类")
	private String thirdCatalog;
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "E", name = "指标名称")
	private String indexNm;
	@ExcelColumn(index = "F", name = "指标别名")
	private String indexUsualNm;
	@BioneFieldValid(nullable=true)
	@ExcelColumn(index = "G", name = "关联维度")
	private String dims;
	@BioneFieldValid(nullable=false,type=FieldTypes.DATESTR,dateFormats={"yyyyMMdd"})
	@ExcelColumn(index = "H", name = "启用时间")
	private String startDate;
	@BioneFieldValid(nullable=false,comboVals={"01", "02", "03", "04", "10", "11", "12"})
	@ExcelColumn(index = "I", name = "生成周期", value = { "01", "02", "03", "04", "10", "11", "12" }, text = { "日", "月", "季", "年", "周", "旬", "半年" })
	private String calcCycle;
	@BioneFieldValid(nullable=false,comboVals={"01", "02", "03", "04","05"})
	@ExcelColumn(index = "J", name = "指标类型", value = { "01", "02", "03", "04",
			"05" }, text = { "根指标", "组合指标", "派生指标", "泛化指标", "总账指标" })
	private String indexType;
	@ExcelColumn(index = "K", name = "数据集中文名")
	private String datasetCnNm;
	@ExcelColumn(index = "L", name = "数据集ID")
	private String datasetId;
	@ExcelColumn(index = "M", name = "来源指标")
	private String srcIndexNo;
	@ExcelColumn(index = "N", name = "指标公式")
	private String indexFormula;
	@BioneFieldValid(nullable=false,comboVals={"Y","N"})
	@ExcelColumn(index = "O", name = "是否可汇总", value = { "Y", "N" }, text = {
			"是", "否" })
	private String isSum;
	@BioneFieldValid(nullable=false,comboVals={"01", "02" ,"03"})
	@ExcelColumn(index = "P", name = "统计类型", value = { "01", "02", "03"}, text = {
			"通用","时点", "时期"})
	private String statType;
	@BioneFieldValid(nullable=true,comboVals={"Y","N"})
	@ExcelColumn(index = "Q", name = "是否落地", value = { "Y", "N" }, text = {
			"是", "否" })
	private String isSave;
	@BioneFieldValid(nullable=false,comboVals={"Y","N"})
	@ExcelColumn(index = "R", name = "指标状态", value = { "Y", "N" }, text = {
			"启用", "停用" })
	private String indexSts;
	
	@BioneFieldValid(nullable=false, comboVals={ "00", "01", "02","03", "04", "05","06", "07", "08", "09" })
	@ExcelColumn(index = "S", name = "业务类型", relDs={"com.yusys.bione.frame.variable.entity.BioneParamInfo","paramValue","paramName"," PARAM_TYPE_NO ='idxTaskType'"})
	private String busiType;
	
	@BioneFieldValid(nullable=true)
	@ExcelColumn(index = "T", name = "主管部门",relDs={"com.yusys.bione.frame.authobj.entity.BioneDeptInfo","deptId","deptName"})
	private String deptId;
	@BioneFieldValid(nullable=true,comboVals={"01", "02", "03"})
	@ExcelColumn(index = "U", name = "数据格式", value = { "01", "02", "03" }, text = {
			"金额", "比例","数值"  })
	private String dataType;
	@BioneFieldValid(nullable=true,comboVals={ "01", "02", "03","04","05" })
	@ExcelColumn(index = "V", name = "数据单位", value = { "01", "02", "03","04","05" }, text = {
			"元", "百","千","万","亿"  })
	private String dataUnit;
	@BioneFieldValid(nullable=true)
	@ExcelColumn(index = "W", name = "业务条线", relDs={"com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine","lineId","lineNm"})
	private String lineId;
	@ExcelColumn(index = "X", name = "业务分库", relDs={"com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo","busiLibId","busiLibNm"})
	private String busiLibId;
	@ExcelColumn(index = "Y", name = "业务定义")
	private String busiDef;
	@ExcelColumn(index = "Z", name = "业务口径")
	private String busiRule;
	@ExcelColumn(index = "AA", name = "联系人",relDs={"com.yusys.bione.frame.user.entity.BioneUserInfo","userId","userName"})
	private String userId;
	@ExcelColumn(index = "AB", name = "备注")
	private String remark;
	private int excelRowNo;
	private String sheetName;
	public String getFirstCatalog() {
		return firstCatalog;
	}

	public void setFirstCatalog(String firstCatalog) {
		this.firstCatalog = firstCatalog;
	}

	public String getSecondCatalog() {
		return secondCatalog;
	}

	public void setSecondCatalog(String secondCatalog) {
		this.secondCatalog = secondCatalog;
	}

	public String getThirdCatalog() {
		return thirdCatalog;
	}

	public void setThirdCatalog(String thirdCatalog) {
		this.thirdCatalog = thirdCatalog;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getIndexUsualNm() {
		return indexUsualNm;
	}

	public void setIndexUsualNm(String indexUsualNm) {
		this.indexUsualNm = indexUsualNm;
	}

	public String getDims() {
		return dims;
	}

	public void setDims(String dims) {
		this.dims = dims;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getCalcCycle() {
		return calcCycle;
	}

	public void setCalcCycle(String calcCycle) {
		this.calcCycle = calcCycle;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getDatasetCnNm() {
		return datasetCnNm;
	}

	public void setDatasetCnNm(String datasetCnNm) {
		this.datasetCnNm = datasetCnNm;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getSrcIndexNo() {
		return srcIndexNo;
	}

	public void setSrcIndexNo(String srcIndexNo) {
		this.srcIndexNo = srcIndexNo;
	}

	public String getIndexFormula() {
		return indexFormula;
	}

	public void setIndexFormula(String indexFormula) {
		this.indexFormula = indexFormula;
	}

	public String getIsSum() {
		return isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}

	public String getIndexSts() {
		return indexSts;
	}

	public void setIndexSts(String indexSts) {
		this.indexSts = indexSts;
	}


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}


	public String getBusiDef() {
		return busiDef;
	}

	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	}

	public String getBusiRule() {
		return busiRule;
	}

	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
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

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getBusiLibId() {
		return busiLibId;
	}

	public void setBusiLibId(String busiLibId) {
		this.busiLibId = busiLibId;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	
}
