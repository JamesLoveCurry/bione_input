package com.yusys.bione.plugin.rptmgr.entity;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the RPT_MGR_REPORT_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_REPORT_INFO")
public class RptMgrReportInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RPT_ID")
	@ExcelColumn(index = "B", name = "报表编号")
	private String rptId;

	@Column(name="CATALOG_ID")
	private String catalogId;

	@Column(name="CFG_ID")
	private String cfgId;

	@Column(name="END_DATE")
	private String endDate;

	@Column(name="EXT_TYPE")
	private String extType;

	@Column(name="RANK_ORDER")
	private BigDecimal rankOrder;

	@Column(name="RPT_CYCLE")
	@ExcelColumn(index = "I", name = "生成频率")
	private String rptCycle;

	@Column(name="RPT_DESC")
	@ExcelColumn(index = "P", name = "报表描述")
	private String rptDesc;
	
	@Column(name="RPT_NM")
	private String rptNm;

	
	@Column(name="RPT_STS")
	@ExcelColumn(index = "N", name = "报表状态")
	private String rptSts;

	@Column(name="RPT_TYPE")
	private String rptType;

	@Column(name="SHOW_PRIORITY")
	private BigDecimal showPriority;

	@Column(name="START_DATE")
	private String startDate;
	
	@Column(name="CREATE_TIME")
	private Timestamp createTime;
	
	@Column(name="BUSI_TYPE")
	private String busiType;
	
	@ExcelColumn(index = "C", name = "报表名称")
	@Column(name="RPT_NUM")
	private String rptNum;
	
	@Column(name="INFO_RIGHTS")
	private String infoRights;
	
	@Column(name="DEF_SRC")
	private String defSrc;
	
	@Column(name="DEF_ORG")
	private String defOrg;
	
	@Column(name="DEF_USER")
	private String defUser;
	
	@Column(name="IS_CABIN")
	private String isCabin;
	
	@Column(name="LINE_ID")
	private String lineId;
	
	@Column(name="BUSI_LIB_ID")
	private String busiLibId;
	
	@Column(name="RPT_BUSI_NM")
	private String rptBusiNm;
	
	@Column(name="EXCEL_RUN")
	private String excelRun;

	@Column(name="REPORT_OBJ")
	private String reportObj;

    public RptMgrReportInfo() {
    }

	public String getRptId() {
		return this.rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getCatalogId() {
		return this.catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCfgId() {
		return this.cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getExtType() {
		return this.extType;
	}

	public void setExtType(String extType) {
		this.extType = extType;
	}

	public BigDecimal getRankOrder() {
		return this.rankOrder;
	}

	public void setRankOrder(BigDecimal rankOrder) {
		this.rankOrder = rankOrder;
	}

	public String getRptCycle() {
		return this.rptCycle;
	}

	public void setRptCycle(String rptCycle) {
		this.rptCycle = rptCycle;
	}

	public String getRptDesc() {
		return this.rptDesc;
	}

	public void setRptDesc(String rptDesc) {
		this.rptDesc = rptDesc;
	}

	public String getRptNm() {
		return this.rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	public String getRptSts() {
		return this.rptSts;
	}

	public void setRptSts(String rptSts) {
		this.rptSts = rptSts;
	}

	public String getRptType() {
		return this.rptType;
	}

	public void setRptType(String rptType) {
		this.rptType = rptType;
	}

	public BigDecimal getShowPriority() {
		return this.showPriority;
	}

	public void setShowPriority(BigDecimal showPriority) {
		this.showPriority = showPriority;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public String getInfoRights() {
		return infoRights;
	}

	public void setInfoRights(String infoRights) {
		this.infoRights = infoRights;
	}

	/**
	 * @return 返回 defSrc。
	 */
	public String getDefSrc() {
		return defSrc;
	}

	/**
	 * @param defSrc 设置 defSrc。
	 */
	public void setDefSrc(String defSrc) {
		this.defSrc = defSrc;
	}

	/**
	 * @return 返回 defOrg。
	 */
	public String getDefOrg() {
		return defOrg;
	}

	/**
	 * @param defOrg 设置 defOrg。
	 */
	public void setDefOrg(String defOrg) {
		this.defOrg = defOrg;
	}

	/**
	 * @return 返回 defUser。
	 */
	public String getDefUser() {
		return defUser;
	}

	/**
	 * @param defUser 设置 defUser。
	 */
	public void setDefUser(String defUser) {
		this.defUser = defUser;
	}

	public String getIsCabin() {
		return isCabin;
	}

	public void setIsCabin(String isCabin) {
		this.isCabin = isCabin;
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

	public String getRptBusiNm() {
		return rptBusiNm;
	}

	public void setRptBusiNm(String rptBusiNm) {
		this.rptBusiNm = rptBusiNm;
	}

	public String getExcelRun() {
		return excelRun;
	}

	public void setExcelRun(String excelRun) {
		this.excelRun = excelRun;
	}

	public String getReportObj() {
		return reportObj;
	}

	public void setReportObj(String reportObj) {
		this.reportObj = reportObj;
	}
}