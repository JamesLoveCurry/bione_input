package com.yusys.biapp.input.task.web.vo;

import java.math.BigDecimal;

import com.yusys.biapp.input.task.entity.RptTskInsLog;
import com.yusys.biapp.input.task.entity.RptTskNodeIns;
import com.yusys.bione.comp.utils.BeanUtils;

public class InputTskLogVO extends RptTskInsLog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String taskNodeName;
	
	private BigDecimal orderNo;
	
	private String nodeType;

	private String taskNodeInstanceId;
	
	private String userName;

	public InputTskLogVO(){
		
	}
	
	public InputTskLogVO(RptTskNodeIns ins,RptTskInsLog log,String userName ){
		BeanUtils.copy(log, this);
		this.setTaskNodeName(ins.getTaskNodeNm());
		String taskOrderno = ins.getTaskOrderno();
		BigDecimal orderNo = new BigDecimal(taskOrderno);
		this.setOrderNo(orderNo);
		this.setNodeType(ins.getNodeType());
		this.setTaskNodeInstanceId(ins.getTaskNodeInstanceId());
		this.setUserName(userName);
	}

	public String getTaskNodeName() {
		return taskNodeName;
	}

	public void setTaskNodeName(String taskNodeName) {
		this.taskNodeName = taskNodeName;
	}

	public BigDecimal getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(BigDecimal orderNo) {
		this.orderNo = orderNo;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getTaskNodeInstanceId() {
		return taskNodeInstanceId;
	}

	public void setTaskNodeInstanceId(String taskNodeInstanceId) {
		this.taskNodeInstanceId = taskNodeInstanceId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
}
