package com.yusys.bione.frame.logicsys.web.vo;

import java.text.SimpleDateFormat;

import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;


public class BioneLogicSysInfoVO extends BioneLogicSysInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String authTypeName;//认证名称
	@SuppressWarnings("unused")
	private String logicSysIconHtml;//图标显示
	private String lastUpdateTimeStr;//最后更新时间
	private String logicSysStsName;//是否启用s
	private String lastUpdateUserName;//用户名 中文
	private String systemVersion;
	private String cnCopyright;//中文版权
	private String enCopyright;//英文版权
	
	
	public String getCnCopyright() {
		return cnCopyright;
	}
	public void setCnCopyright(String cnCopyright) {
		this.cnCopyright = cnCopyright;
	}
	public String getEnCopyright() {
		return enCopyright;
	}
	public void setEnCopyright(String enCopyright) {
		this.enCopyright = enCopyright;
	}
	
	
	
	public String getAuthTypeName() {
		return authTypeName;
	}
	public void setAuthTypeName(String authTypeName) {
		this.authTypeName = authTypeName;
	}
	/*public String getLogicSysIconHtml() {
		if("".equals(logicSysIconHtml)||logicSysIconHtml==null){
			if(this.getLogicSysIcon()!=null&&!"".equals(this.getLogicSysIcon())){
				String basePath = Struts2Utils.getRequest().getContextPath();
				this.logicSysIconHtml = "<img src=\""+basePath+"/"+this.getLogicSysIcon()+"\" height='16' width='16'>";
			}else{
				this.logicSysIconHtml ="";
			}
			
		}
		return logicSysIconHtml;
	}*/
	public void setLogicSysIconHtml(String logicSysIconHtml) {
		
		this.logicSysIconHtml  = logicSysIconHtml;
	}
	public String getLastUpdateTimeStr() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		lastUpdateTimeStr = format.format(getLastUpdateTime());
		return lastUpdateTimeStr;
	}
	public void setLastUpdateTimeStr(String lastUpdateTimeStr) {
		this.lastUpdateTimeStr = lastUpdateTimeStr;
	}
	public String getLogicSysStsName() {
		if("1".equals(getLogicSysSts())){
			logicSysStsName="启用";
			return logicSysStsName;
		}else{
			logicSysStsName="停用";
			return logicSysStsName;
		}
	}
	public void setLogicSysStsName(String logicSysStsName) {
		this.logicSysStsName = logicSysStsName;
	}
	public String getLastUpdateUserName() {
		return lastUpdateUserName;
	}
	public void setLastUpdateUserName(String lastUpdateUserName) {
		this.lastUpdateUserName = lastUpdateUserName;
	}
	public String getSystemVersion() {
		return systemVersion;
	}
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	
}
