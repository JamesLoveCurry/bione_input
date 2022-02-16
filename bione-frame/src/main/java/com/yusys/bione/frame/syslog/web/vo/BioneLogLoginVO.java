package com.yusys.bione.frame.syslog.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
@ExcelSheet(index="0",name="用户活跃度统计")
public class BioneLogLoginVO {
	@ExcelColumn(index="A",name="用户账号")
	private String userNo;
	@ExcelColumn(index="B",name="用户名称")
	private String userName;
	@ExcelColumn(index="C",name="机构")
	private String orgName;
	@ExcelColumn(index="D",name="登录次数")
	private int loginNum;
	@ExcelColumn(index="E",name="在线时长")
	private String loinDays;
	private long loginTime;
	private String userId;
	
	
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLoinDays() {
		return loinDays;
	}
	public void setLoinDays(String loinDays) {
		this.loinDays = loinDays;
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
	public int getLoginNum() {
		return loginNum;
	}
	public void setLoginNum(int loginNum) {
		this.loginNum = loginNum;
	}
	public long getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}
	
	

}
