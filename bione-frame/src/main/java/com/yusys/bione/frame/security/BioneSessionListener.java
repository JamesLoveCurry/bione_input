/**
 * 
 */
package com.yusys.bione.frame.security;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.syslog.entity.BioneLogLogin;
import com.yusys.bione.frame.syslog.service.BioneLogLoginBS;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class BioneSessionListener implements HttpSessionListener{


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent evt) {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent evt) {
		BioneLogLoginBS logloginBS = SpringContextHolder.getBean("bioneLogLoginBS");
		BioneLogLogin login =new BioneLogLogin();
		login.setSessionId(evt.getSession().getId());
		login.setLogoutTime(System.currentTimeMillis());
		logloginBS.update(login);
	}

}
