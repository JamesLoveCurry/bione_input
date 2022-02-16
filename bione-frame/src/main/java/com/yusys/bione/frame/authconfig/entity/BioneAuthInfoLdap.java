package com.yusys.bione.frame.authconfig.entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "BIONE_AUTH_INFO_LDAP")
public class BioneAuthInfoLdap implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "AUTH_SRC_ID", unique = true, nullable = false, length = 32)
	private String authSrcId;
	
	@Column(name = "AUTH_TYPE_NO", nullable = false, length = 32)
	private String authTypeNo;
	
	@Column(name = "AUTH_SRC_NAME", length = 100)
	private String authSrcName;
	
	@Column(name = "IP_ADDRESS", length = 50)
	private String ipAddress;

	@Column(name = "BASE_PWD", length = 100)
	private String basePwd;

	@Column(name = "USERDN_TEMPLATE", length = 100)
	private String userdnTemplate;

	@Column(name = "BASE_NAME", length = 100)
	private String baseName;

	@Column(name = "SERVER_PORT", length = 5)
	private String serverPort;

	@Column(name = "USER_FIND_ATTR", length = 32)
	private String userFindAttr;

	public String getUserdnTemplate() {
		return userdnTemplate;
	}

	public void setUserdnTemplate(String userdnTemplate) {
		this.userdnTemplate = userdnTemplate;
	}

	public String getBasePwd() {
		return basePwd;
	}

	public void setBasePwd(String basePwd) {
		this.basePwd = basePwd;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getUserFindAttr() {
		return userFindAttr;
	}

	public void setUserFindAttr(String userFindAttr) {
		this.userFindAttr = userFindAttr;
	}

	public BioneAuthInfoLdap() {
	}

	public String getAuthTypeNo() {
		return this.authTypeNo;
	}

	public void setAuthTypeNo(String authTypeNo) {
		this.authTypeNo = authTypeNo;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getAuthSrcId() {
		return authSrcId;
	}

	public void setAuthSrcId(String authSrcId) {
		this.authSrcId = authSrcId;
	}

	public String getAuthSrcName() {
		return authSrcName;
	}

	public void setAuthSrcName(String authSrcName) {
		this.authSrcName = authSrcName;
	}

}