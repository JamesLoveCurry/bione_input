package com.yusys.biapp.input.index.web.vo;

import java.util.List;

public class DataInputVO {
	// 操作意见
	private String operOpinion;
	private String idxNo;
	private List<DimRowVO> dimRowList;

	public List<DimRowVO> getDimRowList() {
		return dimRowList;
	}

	public void setDimRowList(List<DimRowVO> dimRowList) {
		this.dimRowList = dimRowList;
	}

	public String getOperOpinion() {
		return operOpinion;
	}

	public void setOperOpinion(String operOpinion) {
		this.operOpinion = operOpinion;
	}

	public String getIdxNo() {
		return idxNo;
	}

	public void setIdxNo(String idxNo) {
		this.idxNo = idxNo;
	}


}
