package com.yusys.bione.plugin.rptfav.web.vo;

import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;

public class RptFavQueryinsVo extends RptFavQueryins{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	
	private String rptNum;

	public String getUserName() {
		return userName;
	}

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
}
