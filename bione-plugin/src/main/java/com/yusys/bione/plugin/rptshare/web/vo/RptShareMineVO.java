package com.yusys.bione.plugin.rptshare.web.vo;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class RptShareMineVO implements Serializable{
	private String shareId;
	private String objId;
	private String objName;
	private String objType;
	private String objUserId;
	private String objUserName;
	private Timestamp createTime;
	private String shareSts;
	
	
	
	public RptShareMineVO() {
	}

	public RptShareMineVO(String shareId,String objId,String objName,String objType,String objUserName,Timestamp createTime,String shareSts){
		this.setShareId(shareId);
		this.setObjType(objType);
		this.setObjName(objName);
		this.setCreateTime(createTime);
		this.setObjUserName(objUserName);
		this.setShareSts(shareSts);
		this.setObjId(objId);;
	}
	
	public RptShareMineVO(String shareId,String objId,String objName,String objType,String objUserId,String objUserName,Timestamp createTime,String shareSts){
		this.setShareId(shareId);
		this.setObjType(objType);
		this.setObjName(objName);
		this.setCreateTime(createTime);
		this.setObjUserId(objUserId);
		this.setObjUserName(objUserName);
		this.setShareSts(shareSts);
		this.setObjId(objId);;
	}
	
	public String getShareId() {
		return shareId;
	}
	public void setShareId(String shareId) {
		this.shareId = shareId;
	}
	public String getObjName() {
		return objName;
	}
	public void setObjName(String objName) {
		this.objName = objName;
	}
	public String getObjType() {
		return objType;
	}
	public void setObjType(String objType) {
		this.objType = objType;
	}
	public String getObjUserName() {
		return objUserName;
	}
	public void setObjUserName(String objUserName) {
		this.objUserName = objUserName;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getShareSts() {
		return shareSts;
	}
	public void setShareSts(String shareSts) {
		this.shareSts = shareSts;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getObjUserId() {
		return objUserId;
	}

	public void setObjUserId(String objUserId) {
		this.objUserId = objUserId;
	}
	
	
}
