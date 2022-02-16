package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the BIONE_MSG_LOG database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_LOG")
public class BioneMsgLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MSG_ID")
	private String msgId;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

	@Column(name="MSG_DETAIL")
	private String msgDetail;

	@Column(name="MSG_MODE")
	private String msgMode;

	@Column(name="MSG_TITLE")
	private String msgTitle;

	@Column(name="MSG_TYPE")
	private String msgType;

	@Column(name="RECEIVE_ADDRESS")
	private String receiveAddress;

	@Column(name="RECEIVE_USER")
	private String receiveUser;

	@Column(name="SEND_STS")
	private String sendSts;

	@Column(name="SEND_TIME")
	private Timestamp sendTime;

	@Column(name="SEND_TYPE_NO")
	private String sendTypeNo;

	@Column(name="SEND_USER")
	private String sendUser;

	@Column(name="VIEW_STS")
	private String viewSts;

	@Column(name="VIEW_TIME")
	private Timestamp viewTime;

    public BioneMsgLog() {
    }

	public String getMsgId() {
		return this.msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getMsgDetail() {
		return this.msgDetail;
	}

	public void setMsgDetail(String msgDetail) {
		this.msgDetail = msgDetail;
	}

	public String getMsgMode() {
		return this.msgMode;
	}

	public void setMsgMode(String msgMode) {
		this.msgMode = msgMode;
	}

	public String getMsgTitle() {
		return this.msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgType() {
		return this.msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getReceiveAddress() {
		return this.receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public String getReceiveUser() {
		return this.receiveUser;
	}

	public void setReceiveUser(String receiveUser) {
		this.receiveUser = receiveUser;
	}

	public String getSendSts() {
		return this.sendSts;
	}

	public void setSendSts(String sendSts) {
		this.sendSts = sendSts;
	}

	public Timestamp getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public String getSendTypeNo() {
		return this.sendTypeNo;
	}

	public void setSendTypeNo(String sendTypeNo) {
		this.sendTypeNo = sendTypeNo;
	}

	public String getSendUser() {
		return this.sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getViewSts() {
		return this.viewSts;
	}

	public void setViewSts(String viewSts) {
		this.viewSts = viewSts;
	}

	public Timestamp getViewTime() {
		return this.viewTime;
	}

	public void setViewTime(Timestamp viewTime) {
		this.viewTime = viewTime;
	}

}