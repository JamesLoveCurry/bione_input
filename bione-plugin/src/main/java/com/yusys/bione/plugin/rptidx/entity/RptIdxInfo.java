package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yusys.bione.frame.excel.annotations.EmbeddedExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelColumn;

/**
 * The persistent class for the RPT_IDX_INFO database table.
 * 
 */
@Entity
@Table(name = "RPT_IDX_INFO")
public class RptIdxInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	@EmbeddedExcelColumn
	private RptIdxInfoPK id;

	@ExcelColumn(index = "E", name = "计算周期")
	@Column(name = "CALC_CYCLE")
	private String calcCycle;

	@ExcelColumn(index = "H", name = "数据类型")
	@Column(name = "DATA_TYPE")
	private String dataType;

	@ExcelColumn(index = "J", name = "数据精度")
	@Column(name = "DATA_PRECISION")
	private BigDecimal dataPrecision;
	
	@Column(name = "DATA_UNIT")
	private String dataUnit;

	@Column(name = "END_DATE")
	private String endDate;

	@Column(name = "INDEX_CATALOG_NO", nullable = false)
	private String indexCatalogNo;

	@ExcelColumn(index = "D", name = "指标名称")
	@Column(name = "INDEX_NM", nullable = false)
	private String indexNm;

	@ExcelColumn(index = "M", name = "指标状态")
	@Column(name = "INDEX_STS")
	private String indexSts;

	@ExcelColumn(index = "C", name = "指标类型")
	@Column(name = "INDEX_TYPE")
	private String indexType;

	@Column(name = "LAST_UPT_TIME")
	private Timestamp lastUptTime;

	@Column(name = "LAST_UPT_USER", nullable = false)
	private String lastUptUser;

	@Column(name = "IS_CABIN")
	private String isCabin;
	
	@Column(name = "REMARK")
	private String remark;

	@ExcelColumn(index = "N", name = "关联指标")
	@Column(name = "SRC_INDEX_NO", length = 2000)
	private String srcIndexNo;

	@Column(name = "SRC_SOURCE_ID")
	private String srcSourceId;

	@Column(name = "START_DATE")
	private String startDate;

	@Column(name = "IS_SUM")
	private String isSum;
	
	@Column(name = "IS_FILL_SUM")
	private String isFillSum;

	@Column(name = "IS_RPT_INDEX")
	private String isRptIndex;

	@Column(name = "SRC_INDEX_MEASURE")
	private String srcIndexMeasure;

	@ExcelColumn(index = "G", name = "业务口径")
	@Column(name = "BUSI_TYPE")
	private String busiType;

	@Column(name = "TEMPLATE_ID")
	private String templateId;

	@Column(name = "DEF_SRC")
	private String defSrc;

	@Column(name = "DEF_ORG")
	private String defOrg;

	@Column(name = "DEF_USER")
	private String defUser;
	
	@Column(name = "BUSI_NO")
	private String busiNo;
	
	@Column(name = "IS_SAVE")
	private String isSave;
	
	@Column(name = "STAT_TYPE")
	private String statType;
	
	@Column(name = "CREATE_DATE")
	private String createDate;

	@Transient
	private String srcIndexMeasureNm;
	
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "DEPT_ID")
	private String deptId;
	
	@Column(name = "SET_ID")
	private String setId;
	
	@Column(name = "LINE_ID")
	private String lineId;
	
	//--lcy 业务分库分表
	@Column(name = "BUSI_LIB_ID")
	private String busiLibId;
	
	public RptIdxInfo() {
	}

	public RptIdxInfoPK getId() {
		return this.id;
	}

	public void setId(RptIdxInfoPK id) {
		this.id = id;
	}

	public String getCalcCycle() {
		return this.calcCycle;
	}

	public void setCalcCycle(String calcCycle) {
		this.calcCycle = calcCycle;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataUnit() {
		return this.dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIndexCatalogNo() {
		return this.indexCatalogNo;
	}

	public void setIndexCatalogNo(String indexCatalogNo) {
		this.indexCatalogNo = indexCatalogNo;
	}

	public String getIndexNm() {
		return this.indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getIndexSts() {
		return this.indexSts;
	}

	public void setIndexSts(String indexSts) {
		this.indexSts = indexSts;
	}

	public String getIndexType() {
		return this.indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public Timestamp getLastUptTime() {
		return this.lastUptTime;
	}

	public void setLastUptTime(Timestamp lastUptTime) {
		this.lastUptTime = lastUptTime;
	}

	public String getLastUptUser() {
		return this.lastUptUser;
	}

	public void setLastUptUser(String lastUptUser) {
		this.lastUptUser = lastUptUser;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSrcIndexNo() {
		return this.srcIndexNo;
	}

	public void setSrcIndexNo(String srcIndexNo) {
		this.srcIndexNo = srcIndexNo;
	}

	public String getSrcSourceId() {
		return this.srcSourceId;
	}

	public void setSrcSourceId(String srcSourceId) {
		this.srcSourceId = srcSourceId;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getIsSum() {
		return isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}

	public String getIsFillSum() {
		return isFillSum;
	}

	public void setIsFillSum(String isFillSum) {
		this.isFillSum = isFillSum;
	}

	public String getIsRptIndex() {
		return isRptIndex;
	}

	public void setIsRptIndex(String isRptIndex) {
		this.isRptIndex = isRptIndex;
	}

	public String getSrcIndexMeasure() {
		return srcIndexMeasure;
	}

	public void setSrcIndexMeasure(String srcIndexMeasure) {
		this.srcIndexMeasure = srcIndexMeasure;
	}

	public String getSrcIndexMeasureNm() {
		return srcIndexMeasureNm;
	}

	public void setSrcIndexMeasureNm(String srcIndexMeasureNm) {
		this.srcIndexMeasureNm = srcIndexMeasureNm;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getDefSrc() {
		return defSrc;
	}

	public void setDefSrc(String defSrc) {
		this.defSrc = defSrc;
	}

	public String getDefOrg() {
		return defOrg;
	}

	public void setDefOrg(String defOrg) {
		this.defOrg = defOrg;
	}

	public String getDefUser() {
		return defUser;
	}

	public void setDefUser(String defUser) {
		this.defUser = defUser;
	}

	public String getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public BigDecimal getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getIsCabin() {
		return isCabin;
	}

	public void setIsCabin(String isCabin) {
		this.isCabin = isCabin;
	}


	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
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

	@Override
	public String toString() {
		return "RptIdxInfo [id=" + id + "]";
	}
}