package com.yusys.bione.frame.message.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.frame.message.entity.BioneMsgLog;

@SuppressWarnings("serial")
public class BioneMsgLogVO extends BioneMsgLog{

	public BioneMsgLogVO(BioneMsgLog info,String userName){
		BeanUtils.copy(info, this);
		this.setUserName(userName);
	}
	
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


}
