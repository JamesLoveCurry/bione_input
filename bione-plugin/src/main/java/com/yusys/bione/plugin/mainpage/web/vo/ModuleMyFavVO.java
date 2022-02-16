package com.yusys.bione.plugin.mainpage.web.vo;

import java.sql.Timestamp;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRel;
import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;

public class ModuleMyFavVO {
	private String instanceId;
	private String instanceType;
	private String queryNm;
	private Timestamp createTime;
	
	public ModuleMyFavVO(RptFavFolderInsRel rel, RptFavQueryins q) {
		BeanUtils.copy(q, this);
		this.instanceType = rel.getInstanceType();
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

	public String getQueryNm() {
		return queryNm;
	}

	public void setQueryNm(String queryNm) {
		this.queryNm = queryNm;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
