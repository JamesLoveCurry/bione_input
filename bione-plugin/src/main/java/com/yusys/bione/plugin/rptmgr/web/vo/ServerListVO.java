package com.yusys.bione.plugin.rptmgr.web.vo;

import com.yusys.bione.plugin.rptmgr.entity.RptMgrServerInfo;




public class ServerListVO extends RptMgrServerInfo {
	private static final long serialVersionUID = 1L;
	private String adapterNm;

	

	public String getAdapterNm() {
		return adapterNm;
	}

	public void setAdapterNm(String adapterNm) {
		this.adapterNm = adapterNm;
	}

}
