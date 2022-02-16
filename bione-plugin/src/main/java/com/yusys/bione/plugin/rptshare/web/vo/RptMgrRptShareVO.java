package com.yusys.bione.plugin.rptshare.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;

@SuppressWarnings("serial")
public class RptMgrRptShareVO extends RptMgrReportInfo{
	private String userId;

	public RptMgrRptShareVO(RptMgrReportInfo info,String userId){
		BeanUtils.copy(info, this);
		this.setUserId(userId);
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
