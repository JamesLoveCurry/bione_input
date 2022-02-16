package com.yusys.bione.plugin.yuformat.utils;

import java.io.Serializable;

/**
 * 远程服务器  连接参数
 * 20200330 在database.properties中新增shell.flag，用于控制访问引擎的方式。
 */
public class RemoteShellPropertyVO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 控制访问引擎的方式   flag是开关，on表示开，off表示关，rsa表示免密登录。当spark引擎和war包在同一台服务器上，配置为off，不在同一台服务器上配置为on
	 * shell.flag=off
	 */
	private String flag;
	/**
	 * 远程服务器  SSH登录IP
	 * shell.ip=192.168.251.176
	 */
	private String sshIp;
	/**
	 * 远程服务器  SSH登录名
	 * shell.username=engine
	 */
	private String sshUserName;
	/**
	 * 远程服务器  SSH登录密码
	 * shell.password=12345678
	 */
	private String sshPassword;
	/**
	 * 远程服务器  knownHosts路径
	 * shell.known_hosts=/home/engine/.ssh/known_hosts
	 */
	private String knownHosts;
	/**
	 * 远程服务器  id_rsa路径
	 * shell.id_rsa=/home/engine/.ssh/id_rsa
	 */
	private String idRsa;
	/**
	 * 远程服务器的请求路径
	 */
	private String singleUrl;
    /**
     * 调用脚本的路径
     */
	private String path;
	/**
	 * 终止检核任务的脚本路径
	 */
	private String killPath;
	/**
	 * 重启单条检核的脚本路径
	 */
	private String restartSinglePath;

	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getSshIp() {
		return sshIp;
	}
	public void setSshIp(String sshIp) {
		this.sshIp = sshIp;
	}
	public String getSshUserName() {
		return sshUserName;
	}
	public void setSshUserName(String sshUserName) {
		this.sshUserName = sshUserName;
	}
	public String getSshPassword() {
		return sshPassword;
	}
	public void setSshPassword(String sshPassword) {
		this.sshPassword = sshPassword;
	}
	public String getKnownHosts() {
		return knownHosts;
	}
	public void setKnownHosts(String knownHosts) {
		this.knownHosts = knownHosts;
	}
	public String getIdRsa() {
		return idRsa;
	}
	public void setIdRsa(String idRsa) {
		this.idRsa = idRsa;
	}

	public String getSingleUrl() {
		return singleUrl;
	}

	public void setSingleUrl(String singleUrl) {
		this.singleUrl = singleUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKillPath() {
		return killPath;
	}

	public void setKillPath(String killPath) {
		this.killPath = killPath;
	}

	public String getRestartSinglePath() {
		return restartSinglePath;
	}

	public void setRestartSinglePath(String restartSinglePath) {
		this.restartSinglePath = restartSinglePath;
	}
}
