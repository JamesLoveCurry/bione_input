package com.yusys.bione.plugin.engine.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the RPT_ENGINE_NODE_INFO database table.
 * 
 */
@Entity
@Table(name="RPT_ENGINE_NODE_INFO")
public class RptEngineNodeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="NODE_ID", unique=true, nullable=false, length=32)
	private String nodeId;

	@Column(name="IP_ADDRESS", nullable=false, length=100)
	private String ipAddress;

	@Column(name="MAX_THREAD", precision=12)
	private BigDecimal maxThread;

	@Column(name="NODE_NM", nullable=false, length=100)
	private String nodeNm;

	@Column(name="NODE_STS", length=1)
	private String nodeSts;

	@Column(name="NODE_TYPE", length=10)
	private String nodeType;

	@Column(precision=12)
	private BigDecimal port;

	@Column(length=500)
	private String remark;

	@Column(name="TIMEOUT_TIME", precision=12)
	private BigDecimal timeoutTime;

    public RptEngineNodeInfo() {
    }

	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public BigDecimal getMaxThread() {
		return this.maxThread;
	}

	public void setMaxThread(BigDecimal maxThread) {
		this.maxThread = maxThread;
	}

	public String getNodeNm() {
		return this.nodeNm;
	}

	public void setNodeNm(String nodeNm) {
		this.nodeNm = nodeNm;
	}

	public String getNodeSts() {
		return this.nodeSts;
	}

	public void setNodeSts(String nodeSts) {
		this.nodeSts = nodeSts;
	}

	public String getNodeType() {
		return this.nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public BigDecimal getPort() {
		return this.port;
	}

	public void setPort(BigDecimal port) {
		this.port = port;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getTimeoutTime() {
		return this.timeoutTime;
	}

	public void setTimeoutTime(BigDecimal timeoutTime) {
		this.timeoutTime = timeoutTime;
	}

}