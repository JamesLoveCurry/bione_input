package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;

/**
 * 指标引擎 Server节点信息
 * 可支持多Server 高可用部署
 * 20200907 
 */
public class RptEngineServerInfo  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Server的类型
	 *	指标计算 或 校验：CommandRemoteType.INDEX
	 *  指标查询：CommandRemoteType.QUERY
	 * @author xupeng
	 *
	 */
	private String serverType;
	
	/**
	 * IP地址
	 */
	private String ipAddresses;
	
	/**
	 * 端口
	 */
	private int port;
	
	/**
	 * 连接Server  超时时间
	 */
	private int connectTimeout;
	
	/**
	 * 读取数据  超时时间
	 */
	private int readTimeout;
	
	/**
	 * 最大连接数
	 */
	private int maxConnections;
	
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getIpAddresses() {
		return ipAddresses;
	}
	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public int getMaxConnections() {
		return maxConnections;
	}
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
}
