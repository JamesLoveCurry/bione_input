package com.yusys.bione.frame.logicsys.util;


/**
 * <pre>
 * Title: 逻辑系统列表生成工具类
 * Description: 负责生成逻辑系统选择列表
 * </pre>
 * 
 * @author songxf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 * </pre>
 */
public class LogicSysUtils {

	/**
	 * 生成逻辑系统导航的HTML
	 * 
	 * @param LogicSysInfoList
	 * @return
	 */
	/*public static String list2LogicSys(List<BioneLogicSysInfo> LogicSysInfoList) {
		String basePath = Struts2Utils.getRequest().getContextPath();
		StringBuilder logicSysHtml = new StringBuilder("");
		logicSysHtml.append("<div id=\"logicSys\" >\n");
		if (LogicSysInfoList != null) {
			for (BioneLogicSysInfo sysInfo : LogicSysInfoList) {
				logicSysHtml.append("<a href=\""+basePath+"/bione/logon?logicsysno="+sysInfo.getLogicSysNo()+"\">");
				logicSysHtml.append("<img src=\"" +sysInfo.getLogicSysIcon()+"\">\n");
				logicSysHtml.append("</a>");
			}
		}
		logicSysHtml.append("</div>\n");
		return logicSysHtml.toString();
	}*/
	
	
	/**
	 * 获取图片名称
	 * @param imgUrl
	 * @return
	 */
	public static String getImgName(String imgUrl){
		int index = imgUrl.lastIndexOf("/");
		return imgUrl.substring(index+1).trim();
	} 
}
