package com.yusys.bione.plugin.rptfav.web.vo;

import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;

/**
 * 
 * @author sunym
 * 为修改页面获得文件夹名称
 *
 */

public class FolderNameVo extends RptFavQueryins{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String folderNm;
	
	private String userName;
	
	private String folderId;
	
	private String rptNum;

	public String getRptNum() {
		return rptNum;
	}

	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	public String getFolderNm() {
		return folderNm;
	}

	public void setFolderNm(String folderNm) {
		this.folderNm = folderNm;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	
	
	
	
}
