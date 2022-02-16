package com.yusys.bione.plugin.rptmgr.web.vo;


import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;

@SuppressWarnings("serial")
public class RptMgrInfoVO extends RptMgrReportInfo{


	private String dataPrecision;

	private String dataUnit;

	private String dutyDept;

	private String generateTime;

	private String parties;

	private String requireExplain;

	private String rptCurrtype;

	private String rptGenerateType;

	private String rptNum;

	private String rptUse;
	
	private String paramtmpId;

	private String rptSrcPath;

	private String searchPath;

	private String serverId;
	
	private String catalogNm;
	
	private String serverNm;
	
	private String paramtmpNm;
	
	//202002 lcy 增加明细类报表逻辑校验配置
	private String templateType;
	
	private String tmpVersionId;
	
	private String repId;
	
	private String verStartDate;

	public String getVerStartDate() {
		return verStartDate;
	}

	public void setVerStartDate(String verStartDate) {
		this.verStartDate = verStartDate;
	}

	public String getTmpVersionId() {
		return tmpVersionId;
	}

	public void setTmpVersionId(String tmpVersionId) {
		this.tmpVersionId = tmpVersionId;
	}

	public String getRepId() {
		return repId;
	}

	public void setRepId(String repId) {
		this.repId = repId;
	}

	public String getCatalogNm() {
		return catalogNm;
	}

	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}

	public String getServerNm() {
		return serverNm;
	}

	public void setServerNm(String serverNm) {
		this.serverNm = serverNm;
	}

	public String getParamtmpId() {
		return paramtmpId;
	}

	public void setParamtmpId(String paramtmpId) {
		this.paramtmpId = paramtmpId;
	}

	public String getRptSrcPath() {
		return rptSrcPath;
	}

	public void setRptSrcPath(String rptSrcPath) {
		this.rptSrcPath = rptSrcPath;
	}

	public String getSearchPath() {
		return searchPath;
	}

	public void setSearchPath(String searchPath) {
		this.searchPath = searchPath;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getDutyDept() {
		return dutyDept;
	}

	public void setDutyDept(String dutyDept) {
		this.dutyDept = dutyDept;
	}

	public String getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(String generateTime) {
		this.generateTime = generateTime;
	}

	public String getParties() {
		return parties;
	}

	public void setParties(String parties) {
		this.parties = parties;
	}

	public String getRequireExplain() {
		return requireExplain;
	}

	public void setRequireExplain(String requireExplain) {
		this.requireExplain = requireExplain;
	}

	public String getRptCurrtype() {
		return rptCurrtype;
	}

	public void setRptCurrtype(String rptCurrtype) {
		this.rptCurrtype = rptCurrtype;
	}

	public String getRptGenerateType() {
		return rptGenerateType;
	}

	public void setRptGenerateType(String rptGenerateType) {
		this.rptGenerateType = rptGenerateType;
	}

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public String getRptUse() {
		return rptUse;
	}

	public void setRptUse(String rptUse) {
		this.rptUse = rptUse;
	}

	public String getParamtmpNm() {
		return paramtmpNm;
	}

	public void setParamtmpNm(String paramtmpNm) {
		this.paramtmpNm = paramtmpNm;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
}
