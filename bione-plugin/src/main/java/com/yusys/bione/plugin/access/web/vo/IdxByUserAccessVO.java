package com.yusys.bione.plugin.access.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

@ExcelSheet(index="0",name="用户访问统计")
public class IdxByUserAccessVO {
	
	private String userId;
	@ExcelColumn(index="A",name="用户编号")
	private String userNo;
	@ExcelColumn(index="B",name="用户名称")
	private String userName;
	@ExcelColumn(index="C",name="机构")
	private String orgName;
	@ExcelColumn(index="D",name="访问次数")
	private String pv;

	@ExcelColumn(index="E",name="访问指标数")
	private String uv;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}

	public String getUv() {
		return uv;
	}

	public void setUv(String uv) {
		this.uv = uv;
	}
	
		
}
