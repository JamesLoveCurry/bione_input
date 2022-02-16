package com.yusys.bione.frame.message.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the BIONE_MSG_NOTICE_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_MSG_NOTICE_INFO")
public class BioneMsgNoticeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ANNOUNCEMENT_ID")
	private String announcementId;

	@Column(name="ANNOUNCEMENT_DETAIL")
	private String announcementDetail;

	@Column(name="ANNOUNCEMENT_STS")
	private String announcementSts;

	@Column(name="ANNOUNCEMENT_TITLE")
	private String announcementTitle;

	@Column(name="ANNOUNCEMENT_TYPE")
	private String announcementType;

	@Column(name="EFFECTIVE__DATE")
	private Timestamp effectiveDate;

	@Column(name="EXPIRE_DATE")
	private Timestamp expireDate;

	@Column(name="LAST_UPDATE_TIME")
	private Timestamp lastUpdateTime;

	@Column(name="LAST_UPDATE_USER")
	private String lastUpdateUser;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;
	
	@Column(name="CREATE_USER")
	private String createUser;

    public BioneMsgNoticeInfo() {
    }

	public String getAnnouncementId() {
		return this.announcementId;
	}

	public void setAnnouncementId(String announcementId) {
		this.announcementId = announcementId;
	}

	public String getAnnouncementDetail() {
		return this.announcementDetail;
	}

	public void setAnnouncementDetail(String announcementDetail) {
		this.announcementDetail = announcementDetail;
	}

	public String getAnnouncementSts() {
		return this.announcementSts;
	}

	public void setAnnouncementSts(String announcementSts) {
		this.announcementSts = announcementSts;
	}

	public String getAnnouncementTitle() {
		return this.announcementTitle;
	}

	public void setAnnouncementTitle(String announcementTitle) {
		this.announcementTitle = announcementTitle;
	}

	public String getAnnouncementType() {
		return this.announcementType;
	}

	public void setAnnouncementType(String announcementType) {
		this.announcementType = announcementType;
	}

	public Timestamp getEffectiveDate() {
		return this.effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Timestamp getExpireDate() {
		return this.expireDate;
	}

	public void setExpireDate(Timestamp expireDate) {
		this.expireDate = expireDate;
	}

	public Timestamp getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdateUser() {
		return this.lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

}