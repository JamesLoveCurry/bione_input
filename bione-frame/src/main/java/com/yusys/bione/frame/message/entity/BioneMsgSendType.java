package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MSG_SEND_TYPE database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_SEND_TYPE")
public class BioneMsgSendType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SEND_TYPE_NO")
	private String sendTypeNo;

	@Column(name="BEAN_NAME")
	private String beanName;

	private String remark;

	@Column(name="SEND_TYPE_NAME")
	private String sendTypeName;

    public BioneMsgSendType() {
    }

	public String getSendTypeNo() {
		return this.sendTypeNo;
	}

	public void setSendTypeNo(String sendTypeNo) {
		this.sendTypeNo = sendTypeNo;
	}

	public String getBeanName() {
		return this.beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSendTypeName() {
		return this.sendTypeName;
	}

	public void setSendTypeName(String sendTypeName) {
		this.sendTypeName = sendTypeName;
	}

}