package com.yusys.bione.plugin.wizard.web.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UploadResult implements Serializable{
	private String ehcacheId;
	private List<UploadResultInfo> info=null;
	private String fileName;
	public String getEhcacheId() {
		return ehcacheId;
	}
	public void setEhcacheId(String ehcacheId) {
		this.ehcacheId = ehcacheId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<UploadResultInfo> getInfo() {
		return info;
	}
	public void setInfo(List<UploadResultInfo> info) {
		this.info = info;
	}
	public void setInfo(String value ,String name) {
		if(info==null){
			info=new ArrayList<UploadResultInfo>();
			
		}
		UploadResultInfo i=new UploadResultInfo(name,value);
		info.add(i);
	}
}

