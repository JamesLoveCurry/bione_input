package com.yusys.bione.frame.security.token;

import org.apache.shiro.authc.UsernamePasswordToken;

public class BioneAuthenticationToken extends UsernamePasswordToken {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 登录用户名
	 */
	private String username;

	/**
	 * 登录明文密码字符串
	 */
	private String passwordstr;

	/**
	 * 逻辑系统标识
	 */
	private String logicSysNo;
	
	/**
	 * 验证服务票据
	 */
	private String ticket;

	/**
	 * 验证登录票据
	 */
	private String tnt;
	
	/**
	 * 是否是超级用户
	 */
	private boolean isSuper;

	public BioneAuthenticationToken(final String username, final String passwordstr, final String logicSysNo,
			final boolean isSuper, final String ticket , final String tnt) {
		this.username = username;
		this.passwordstr = passwordstr;
		this.logicSysNo = logicSysNo;
		this.ticket = ticket;
		this.tnt = tnt;
		this.isSuper = isSuper;
		super.setPassword(passwordstr != null ? passwordstr.toCharArray() : null);
	}

	public BioneAuthenticationToken(final String username, final String passwordstr, final String logicSysNo,
			final boolean isSuper) {

		this(username, passwordstr, logicSysNo, isSuper, null , null);
	}

	public String getLogicSysNo() {
		return logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Object getPrincipal() {
		return getUsername();
	}

	public Object getCredentials() {
		return getPassword();
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getTnt() {
		return tnt;
	}

	public void setTnt(String tnt) {
		this.tnt = tnt;
	}

	public String getPasswordstr() {
		return passwordstr;
	}

	public void setPasswordstr(String passwordstr) {
		this.passwordstr = passwordstr;
	}

	public boolean getIsSuper() {
		return isSuper;
	}

	public void setIsSuper(boolean isSuper) {
		this.isSuper = isSuper;
	}

}
