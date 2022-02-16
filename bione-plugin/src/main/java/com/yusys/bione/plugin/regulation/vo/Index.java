package com.yusys.bione.plugin.regulation.vo;

import com.yusys.bione.plugin.regulation.enums.YesOrNo;

/**
 * 指标，支持以indexNo排序
 */
public class Index implements Comparable<Index> {
	
	private String indexNo;
	
	private int indexVerId;
	
	private String indexNm;

	private String templateId;

	private String[] srcIndexNoes;

	private YesOrNo isRptIndex;

	private String startDate;

	public int compareTo(Index o) {
		return indexNo.compareTo(o.getIndexNo());
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public int getIndexVerId() {
		return indexVerId;
	}

	public void setIndexVerId(int indexVerId) {
		this.indexVerId = indexVerId;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String[] getSrcIndexNoes() {
		return srcIndexNoes;
	}

	public void setSrcIndexNoes(String[] srcIndexNoes) {
		this.srcIndexNoes = srcIndexNoes;
	}

	public YesOrNo getIsRptIndex() {
		return isRptIndex;
	}

	public void setIsRptIndex(YesOrNo isRptIndex) {
		this.isRptIndex = isRptIndex;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}