package com.yusys.bione.plugin.wizard.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

import java.io.Serializable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0", name = "用户信息")
public class UserImportVO implements Serializable, AnnotationValidable {

	@ExcelColumn(index = "A", name = "用户ID")
	private String userId;

	@BioneFieldValid(nullable = false)
	@ExcelColumn(index = "B", name = "用户帐号")
	private String userNo;

	@BioneFieldValid(nullable = false)
	@ExcelColumn(index = "C", name = "用户名称")
	private String userName;

	@ExcelColumn(index = "D", name = "客户经理柜员号")
	private String userAgname;

	@ExcelColumn(index = "E", name = "用户性别", value = {"1", "0"}, text = {"男", "女"}, combox = {"男", "女"})
	private String sex;

	@ExcelColumn(index = "F", name = "用户生日")
	private String birthday;

	@ExcelColumn(index = "G", name = "邮箱")
	private String email;

	@ExcelColumn(index = "H", name = "手机号码")
	private String mobile;

	@BioneFieldValid(nullable = false)
	@ExcelColumn(index = "I", name = "机构")
	private String orgNo;

	@ExcelColumn(index = "J", name = "电话号码")
	private String tel;

	@ExcelColumn(index = "K", name = "部门")
	private String deptName;

	@ExcelColumn(index = "L", name = "邮编")
	private String postCode;

	@BioneFieldValid(nullable = false)
	@ExcelColumn(index = "M", name = "用户状态", value = {"1", "0"}, text = {"启用", "停用"}, combox = {"启用", "停用"})
	private String userSts;

	@ExcelColumn(index = "N", name = "地址")
	private String addr;

	@ExcelColumn(index = "O", name = "是否管理者", value = {"Y", "N"}, text = {"是", "否"}, combox = {"是", "否"})
	private String isManager;

	@ExcelColumn(index = "P", name = "身份证号")
	private String idCard;

	@ExcelColumn(index = "Q", name = "备注")
	private String remark;

	private Integer excelRowNo;

	private String sheetName;

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

	public String getUserAgname() {
		return userAgname;
	}

	public void setUserAgname(String userAgname) {
		this.userAgname = userAgname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getUserSts() {
		return userSts;
	}

	public void setUserSts(String userSts) {
		this.userSts = userSts;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getExcelRowNo() {
		return excelRowNo;
	}

	public void setExcelRowNo(Integer excelRowNo) {
		this.excelRowNo = excelRowNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getIsManager() {
		return isManager;
	}

	public void setIsManager(String isManager) {
		this.isManager = isManager;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
}
