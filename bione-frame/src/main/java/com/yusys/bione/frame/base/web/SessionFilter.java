package com.yusys.bione.frame.base.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.util.WebUtils;

import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;


public class SessionFilter implements Filter {
	
	//菜单功能Map在缓存中的名称
	private static final String METHOD_URL_CACHE_NAME = "method_url_cache_name";
	
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		//是否允许访问
		boolean isPermitted = true;
		
		// TODO Auto-generated method stub
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		request.getContentType();
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		// 登陆url
		String loginUrl = httpRequest.getContextPath() + "/login";
		// 超时处理，ajax请求超时设置超时状态，页面请求超时则返回提示并重定向
		String url = httpRequest.getRequestURI();
        String path = url.substring(url.lastIndexOf("/"));
        if(path.equals("/login")){
        	if (httpRequest.getHeader("x-requested-with") != null
					&& httpRequest.getHeader("x-requested-with")
							.equalsIgnoreCase("XMLHttpRequest")) {
				httpResponse.addHeader("sessionstatus", "timeOut");
				httpResponse.addHeader("loginPath", loginUrl);
			}
        }else {
    		// 当前请求的url
    		String requestUrl = WebUtils.getPathWithinApplication(httpRequest);
    		// 去掉URL后面的参数
    		requestUrl = StringUtils.substringBefore(requestUrl, "?");
    		isPermitted = isUrlPermitted(requestUrl);
    		if(!isPermitted) {
    			throw new UnauthorizedException();
    		}
        }
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/**
	 * 判断当前用户是否有该请求的权限
	 * @param currentPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isUrlPermitted(String currentPath) {
		//查看当前是否开启越权访问控制
		PropertiesUtils indexPro = PropertiesUtils.get("/bione-frame/index/index.properties");
		String isExceedAuthority = indexPro.getProperty("isExceedAuthority");
		//不开启直接返回通过
		if("N".equals(isExceedAuthority)) {
			return true;
		}
		//还未登录，先不进行控制
		if(null == BioneSecurityUtils.getCurrentUserInfo()) {
			return true;
		}
		//超级管理员不做控制
		if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			return true;
		}
		//从缓存中获取菜单功能Map
		HashMap<String, String> MethodMaps = (HashMap<String, String>) EhcacheUtils.get(METHOD_URL_CACHE_NAME, METHOD_URL_CACHE_NAME);
		if((null != MethodMaps) && (MethodMaps.size() > 0)) {
			//根据请求url获取到对应的菜单ID
			String menuId = MethodMaps.get(currentPath);
			if(StringUtils.isNotBlank(menuId)) {
				//判断该菜单id是否在用户授权的菜单立，不在就视为越权访问
				List<String> authMenuIdList = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4frame.AUTH_RES_DEF_ID_MENU);
				if(!authMenuIdList.contains(menuId)) {
					return false;
				}
			}
		}
		return true;
	}
}
