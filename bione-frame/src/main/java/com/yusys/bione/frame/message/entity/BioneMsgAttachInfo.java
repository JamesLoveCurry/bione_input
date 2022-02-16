package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the BIONE_MSG_ATTACH_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_ATTACH_INFO")
public class BioneMsgAttachInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ATTACH_ID")
	private String attachId;

	@Column(name="ATTACH_NAME")
	private String attachName;

	@Column(name="ATTACH_PATH")
	private String attachPath;

	@Column(name="ATTACH_SIZE")
	private BigDecimal attachSize;

	@Column(name="ATTACH_STS")
	private String attachSts;

	@Column(name="ATTACH_TYPE")
	private String attachType;

	private String remark;

    public BioneMsgAttachInfo() {
    }

	public String getAttachId() {
		return this.attachId;
	}

	public void setAttachId(String attachId) {
		this.attachId = attachId;
	}

	public String getAttachName() {
		return this.attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}

	public String getAttachPath() {
		return this.attachPath;
	}

	public void setAttachPath(String attachPath) {
		this.attachPath = attachPath;
	}

	public BigDecimal getAttachSize() {
		return this.attachSize;
	}

	public void setAttachSize(BigDecimal attachSize) {
		this.attachSize = attachSize;
	}

	public String getAttachSts() {
		return this.attachSts;
	}

	public void setAttachSts(String attachSts) {
		this.attachSts = attachSts;
	}

	public String getAttachType() {
		return this.attachType;
	}

	public void setAttachType(String attachType) {
		this.attachType = attachType;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}