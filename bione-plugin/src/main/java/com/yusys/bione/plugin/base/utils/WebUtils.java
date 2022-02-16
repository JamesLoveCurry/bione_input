package com.yusys.bione.plugin.base.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * <pre>
 *   Title:util类,提供字符串的处理,获取spring bean等方法.
 *   Description:  提供字符串的处理,获取spring bean等方法.
 * </pre>
 * 
 * @author 贠磊	yunlei@yuchengtech.com
 * @version 1.00.00
 * 
 * <pre>
 *  修改记录
 *     修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class WebUtils {

	public final static String SplitParas = "QWERT"; // 换行符号

	/**
	 * 获取用户IP
	 * @param request
	 * @return
	 */
	public static String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");// 防止用反向代理软件就不能获取到客户端的真实IP地址
	}
}
