package com.yusys.bione.plugin.draw.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.draw.entity.RptDrawLogInfo;


@SuppressWarnings("serial")
public class RptDrawLogInfoVO extends RptDrawLogInfo {
	private String userName;

	public RptDrawLogInfoVO(RptDrawLogInfo log,String userName){
		BeanUtils.copy(log, this);
		this.setUserName(userName);
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
