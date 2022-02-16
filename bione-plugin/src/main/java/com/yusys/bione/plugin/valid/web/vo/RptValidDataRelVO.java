package com.yusys.bione.plugin.valid.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.valid.entitiy.RptValidDataRel;

@SuppressWarnings("serial")
public class RptValidDataRelVO extends RptValidDataRel{
	
	private String groupId;
	private String groupType;
	private String indexNo;
	
	public RptValidDataRelVO() {
		
	}
	public RptValidDataRelVO(String groupId, String groupType, String indexNo, RptValidDataRel rel) {
		this.groupId = groupId;
		this.groupType = groupType;
		this.indexNo = indexNo;
		BeanUtils.copy(rel, this);
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupType() {
		return groupType;
	}
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	public String getIndexNo() {
		return indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
}
