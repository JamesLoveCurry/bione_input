package com.yusys.bione.frame.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <pre>
 * Title:平台自定义Authentication类
 * Description: 使shiro的Subject可以包含除用户名外更多的信息
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class BioneUser implements java.io.Serializable {

	private static final long serialVersionUID = 5838655360268384081L;

	private String userId;// 用户ID
	private String userNo;
	private String loginName;// 用户
	private String userName;// 用户名称
	private String orgNo;// 机构编号
	private String orgName;// 用户所属机构名称
	private String deptNo;// 部门编号
	private String deptName;// 用户所属部门名称
	private String currentLogicSysNo;// 当前登录的逻辑系统标识
	private boolean isSuperUser;// 是否为管理员用户，如果是管理员用户，登录所管理的逻辑系统时没有权限控制
	private String authTypeNo;// 认证模式 01本地认证-超级系统 02 本地认证-逻辑系统 03 UA认证 04 LDAP认证
								// 认证方式可以在认证信息管理模块中扩展
	private String ticket;// CAS服务器返回的服务票据(ST),用于跨域的单点登录，同时UA认证平台的加密后的用户密码也保存在该字段内
	private String tnt;// CAS服务器返回的登录票据
	private String pwdStr;
	private String lastUpdateTime; //最近更新时间
	
	private String isManager; //是否是二级管理员

	private String idCard;

	// 与用户关联的的授权对象 如机构、部门、角色、授权组等
	private Map<String, List<String>> authObjMap = new HashMap<String, List<String>>();

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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, List<String>> getAuthObjMap() {
		return authObjMap;
	}

	public void setAuthObjMap(Map<String, List<String>> authObjMap) {
		this.authObjMap = authObjMap;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCurrentLogicSysNo() {
		return currentLogicSysNo;
	}

	public void setCurrentLogicSysNo(String currentLogicSysNo) {
		this.currentLogicSysNo = currentLogicSysNo;
	}

	public boolean isSuperUser() {
		return isSuperUser;
	}

	public void setSuperUser(boolean isSuperUser) {
		this.isSuperUser = isSuperUser;
	}

	public String getAuthTypeNo() {
		return authTypeNo;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public void setAuthTypeNo(String authTypeNo) {
		this.authTypeNo = authTypeNo;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getPwdStr() {
		return pwdStr;
	}

	public void setPwdStr(String pwdStr) {
		this.pwdStr = pwdStr;
	}

	public String getTnt() {
		return tnt;
	}

	public void setTnt(String tnt) {
		this.tnt = tnt;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
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
