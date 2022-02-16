package com.yusys.bione.frame.authconfig.entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "BIONE_AUTH_INFO_CAS")
public class BioneAuthInfoCAS implements Serializable {
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

	@Column(name = "SERVER_PORT", length = 5)
	private String serverPort;

	@Column(name = "SERVER_NAME", length = 100)
	private String serverName;

	@Column(name = "SERVER_PROTOCOL", length = 10)
	private String serverProtocol;

	public BioneAuthInfoCAS() {
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
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerProtocol() {
		return serverProtocol;
	}

	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
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