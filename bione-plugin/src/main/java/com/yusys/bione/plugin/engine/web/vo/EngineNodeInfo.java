package com.yusys.bione.plugin.engine.web.vo;

import java.math.BigDecimal;

import com.yusys.bione.plugin.engine.entity.RptEngineNodeInfo;


public class EngineNodeInfo extends RptEngineNodeInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BigDecimal activeCount;
	
	private BigDecimal freeCount;

	public BigDecimal getFreeCount() {
		return freeCount;
	}

	public void setFreeCount(BigDecimal freeCount) {
		this.freeCount = freeCount;
	}

	public BigDecimal getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(BigDecimal activeCount) {
		this.activeCount = activeCount;
	}
}