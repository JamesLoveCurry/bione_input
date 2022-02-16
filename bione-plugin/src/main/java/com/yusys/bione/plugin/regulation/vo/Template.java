package com.yusys.bione.plugin.regulation.vo;

/**
 * 模板，支持以templateId排序
 */
public class Template implements Comparable<Template> {
	
	private String templateId;
	
	private int verId;
	
	private String rptNm;

	private String rptNum;

	private String startDate;

	private String filePath;

	private String catalogId;
	
	private String rptId;
	
	public int compareTo(Template o) {
		return templateId.compareTo(o.getTemplateId());
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public int getVerId() {
		return verId;
	}

	public void setVerId(int verId) {
		this.verId = verId;
	}

	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String getRptNm) {
		this.rptNm = getRptNm;
	}

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getRptId() {
		return rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}
	
	
}
