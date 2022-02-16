/**
 * 
 */
package com.yusys.bione.frame.base.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;

import com.yusys.bione.comp.utils.SpringContextHolder;

/**
 * <pre>
 * Title:XSS防御过滤器
 * Description: XSS防御过滤器 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class XssFilter implements Filter{

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if ("/".equals(httpRequest.getServletPath())) {
			ShiroFilterFactoryBean bean = SpringContextHolder.getBean(ShiroFilterFactoryBean.class);
			if (bean != null) {
				String loginUrl = bean.getLoginUrl();
				if (StringUtils.isNotEmpty(loginUrl) && ! "/".equals(loginUrl)) {
					((HttpServletResponse)response).sendRedirect(httpRequest.getContextPath() + loginUrl);
					return;
				}
			}
		}
		chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		
	}

}
