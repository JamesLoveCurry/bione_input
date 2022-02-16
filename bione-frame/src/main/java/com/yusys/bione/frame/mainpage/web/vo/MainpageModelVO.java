package com.yusys.bione.frame.mainpage.web.vo;

import java.math.BigDecimal;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignDetail;
import com.yusys.bione.frame.mainpage.entity.BioneMpModuleInfo;

public class MainpageModelVO {
	private String isDisplayLabel;
	
	private BigDecimal posNo;

	private String moduleName;

	private String modulePath;
	
	public MainpageModelVO(BioneMpDesignDetail detail, BioneMpModuleInfo model) {
		BeanUtils.copy(detail, this);
		BeanUtils.copy(model, this);
	}

	public String getIsDisplayLabel() {
		return isDisplayLabel;
	}

	public void setIsDisplayLabel(String isDisplayLabel) {
		this.isDisplayLabel = isDisplayLabel;
	}

	public BigDecimal getPosNo() {
		return posNo;
	}

	public void setPosNo(BigDecimal posNo) {
		this.posNo = posNo;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModulePath() {
		return modulePath;
	}

	public void setModulePath(String modulePath) {
		this.modulePath = modulePath;
	}

	
}
