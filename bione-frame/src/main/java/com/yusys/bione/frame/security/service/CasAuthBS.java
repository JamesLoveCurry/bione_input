package com.yusys.bione.frame.security.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.yusys.bione.frame.util.http.HttpClient;

/**
 * @author songxuefeng CAS认证服务类，支持连接CAS服务器获取granting和service票据
 */
@Service
public class CasAuthBS {

	// service地址，用来配合TNT凭证生成ST
	// 在传统cas认证过程中，service会在注销（/logout）时被用来销毁指定位置cookies，故service在cas服务端中有格式校验
	// 平台使用httpclient做的cas认证，service参数使用如下的静态常量代替。
	public static final String SERVICE_KEY = "http://localhost:8080/bione";

	public static final String ENCODE = "UTF-8";

	/**
	 * 用户的登录认证
	 * 
	 * @param server
	 *            CAS服务器地址
	 * @param username
	 * @param password
	 * @return ticketGrantingTicket 返回null则认为认证失败
	 */
	public String getTicketGrantingTicket(String server, String username,
			String password) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("username=").append(URLEncoder.encode(username, "UTF-8"));
		sb.append("&password=").append(URLEncoder.encode(password, "UTF-8"));
		Object obj = HttpClient.post(server, null, sb.toString(), "UTF-8", null);
		if (obj instanceof byte[]) {
			String response = new String((byte[])obj, "UTF-8");
			Matcher matcher = Pattern.compile("action=\".*/(.*?)\"")
					.matcher(response);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}

	/**
	 * 获取service ticket
	 * 
	 * @param server
	 *            cas服务地址
	 * @param ticketGrantingTicket
	 *            授权票据
	 * @param service
	 * @return serviceTicket 返回null则认为认证失败
	 */
	public String getServiceTicket(String server, String ticketGrantingTicket,
			String service) throws IOException {
		if (ticketGrantingTicket == null || ticketGrantingTicket.equals(""))
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append("service=").append(URLEncoder.encode(service, "UTF-8"));
		Object obj = HttpClient.post(server, null, sb.toString(), "UTF-8", null);
		return (obj instanceof byte[]) ? new String((byte[])obj, "UTF-8") : null;
	}

	/**
	 * 验证service ticket是否有效
	 * 
	 * @param serverUrl
	 * @param serviceTicket
	 * @param service
	 */
	public boolean validServiceTicket(String serverUrl, String serviceTicket,
			String service) throws IOException {
		if (StringUtils.isEmpty(serverUrl) || StringUtils.isEmpty(serviceTicket)) {
			return false;
		}
		if (StringUtils.isEmpty(service)) {
			service = SERVICE_KEY;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(serverUrl).append("?ticket=").append(serviceTicket);
		sb.append("&service=").append(URLEncoder.encode(service, ENCODE));
		Object obj = HttpClient.get(sb.toString(), null, null);
		return obj instanceof byte[];
	}

	/**
	 * 登出系统，销毁票据
	 * 
	 * @param server
	 * @param ticketGrantingTicket
	 */
	public void logout(String server, String ticketGrantingTicket)
			throws IOException {
		HttpClient.delete(server + "/" + ticketGrantingTicket, null);
	}
}