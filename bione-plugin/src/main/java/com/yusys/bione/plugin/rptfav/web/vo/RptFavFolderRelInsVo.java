package com.yusys.bione.plugin.rptfav.web.vo;

import java.sql.Timestamp;

/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 文件夹和文件的大VO
 * </pre>
 * @author sunyuming  sunym@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class RptFavFolderRelInsVo {
	
	private String folderId;
	//private String upFolderId;
	//private String folderNm;
	private String userId;
	private String instanceId;
	private String instanceType;
	private String QueryNm;
	private String remark;
	private String createUser;
	private Timestamp createTime;
	
	public String getQueryNm() {
		return QueryNm;
	}
	public void setQueryNm(String queryNm) {
		QueryNm = queryNm;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getInstanceType() {
		return instanceType;
	}
	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}
	
	
}
