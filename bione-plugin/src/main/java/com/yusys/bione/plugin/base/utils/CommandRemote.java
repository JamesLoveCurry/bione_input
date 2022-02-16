package com.yusys.bione.plugin.base.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuchengtech.index.engine.to.RequestMsg;
import com.yuchengtech.index.engine.to.ResponseMsg;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.plugin.engine.entity.RptEngineServerInfo;

@Component
public class CommandRemote extends NettyClient<ResponseMsg> implements InitializingBean, DisposableBean {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 命令的类型
	 *	指标计算或校验：CommandRemoteType.INDEX
	 *  指标查询：CommandRemoteType.QUERY
	 * @author xupeng
	 *
	 */
	public enum CommandRemoteType {
		INDEX, QUERY;
	}

	/**
	 * 长链接
	 */
	public static String LongConnection = "1";

	/**
	 * 短链接
	 */
	public static String ShortConnection = "2";

	@Override
	public void afterPropertiesSet() throws Exception {
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		String nettyConnection = propertiesUtils.getProperty("nettyConnection");
		if (LongConnection.equals(nettyConnection)) {
			super.init();
		}
	}

	public void destroy() throws Exception {
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		String nettyConnection = propertiesUtils.getProperty("nettyConnection");
		if (LongConnection.equals(nettyConnection)) {
			super.close();
		}
	}

	private Object internalImpl(String params, CommandRemoteType type, boolean sync) {
		String str_msg = "\r\n\r\n★★★★★请求引擎报文★★★★★\r\n" + params + "\r\n★★★★★请求引擎报文★★★★★\r\n\r\n";
		logger.debug(str_msg);//显示提示报文,分析问题经常要在客户端使用xch，别再注销了!!

		// Server信息   支持 引擎多机 高可用部署
		List<RptEngineServerInfo> serverList = this.getEngineServerInfo(type);
		
		// 创建请求数据包
		RequestMsg requestMsg = null;
		if (params != null) {
			requestMsg = new RequestMsg();
			requestMsg.setRequestPar(params);
		}
		StringBuilder logBuilder = new StringBuilder();
		//多Server 处理 
		//Server正常通讯 直接 return 结果 如果异常通讯 循环下一个Server通讯
		for (RptEngineServerInfo serverInfo : serverList) {
			try {
				PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
				String nettyConnection = propertiesUtils.getProperty("nettyConnection");
				if (LongConnection.equals(nettyConnection)) {
					Object rtObj = sendMsg(serverInfo.getIpAddresses(), serverInfo.getPort(), requestMsg, sync, 
							serverInfo.getConnectTimeout(), serverInfo.getReadTimeout(), serverInfo.getMaxConnections());
					printlnObj(rtObj); //在控制如输出返回结果,如果太多，取截取前面的
					return rtObj; //
				}else{
					Map<String, Object> responseMap = Maps.newHashMap();
					sendMsgByShort(serverInfo.getIpAddresses(), serverInfo.getPort(), requestMsg, responseMap, sync, serverInfo.getConnectTimeout(), serverInfo.getReadTimeout());
					Object rtObj = responseMap.get("message");
					printlnObj(rtObj); //在控制如输出返回结果,如果太多，取截取前面的
					return rtObj;
				}
			} catch (Throwable e) {
				e.printStackTrace();
				if (logBuilder.length() > 0) {
					logBuilder.append(',');
				}
				logBuilder.append(serverInfo.getPort()).append(":").append(serverInfo.getPort());
			}
		}
		logBuilder.append(" 引擎服务失败，请检查服务状态");
		logger.error(logBuilder.toString());
		throw new IllegalStateException(logBuilder.toString());
	}

	/**
	 * Engine Server信息   支持 引擎多机 高可用部署
	 * 
	 * 每一个Server 需要配置两种类型 校验和 查询
	 * 
	 * @return List<RptEngineServerInfo>
	 */
	private List<RptEngineServerInfo> getEngineServerInfo(CommandRemoteType type) {
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-plugin/extension/index-config.properties");
		String typeStr = StringUtils.lowerCase(type.name());
		// 最大Server数
		int serverMaxNo=propertiesUtils.getInteger("server_max_no");
		// Server配置信息
		List<RptEngineServerInfo> serverList = Lists.newArrayList();
		
		for(int i = 1; i <= serverMaxNo; i++){
			//server_1_index_ip_address
			//server_1_query_ip_address
			String propertiesStr = "server_" + i + "_" + typeStr;
			// 读取配置文件信息
			String ipAddresses = propertiesUtils.getProperty(propertiesStr + "_ip_address");
			int port = propertiesUtils.getInteger(propertiesStr + "_port");
			int connectTimeout = propertiesUtils.getInteger(propertiesStr + "_connect_timeout");
			int readTimeout = propertiesUtils.getInteger(propertiesStr + "_read_timeout");
			int maxConnections = propertiesUtils.getInteger(propertiesStr + "_max_connections", 16);
			// 封装Server逻辑实体类
			RptEngineServerInfo serverInfo = new RptEngineServerInfo();
			serverInfo.setServerType(typeStr);
			serverInfo.setIpAddresses(ipAddresses);
			serverInfo.setPort(port);
			serverInfo.setConnectTimeout(connectTimeout);
			serverInfo.setReadTimeout(readTimeout);
			serverInfo.setMaxConnections(maxConnections);
			serverList.add(serverInfo);
		}
		
		return serverList;
	}

	//输出
	private void printlnObj(Object _obj) {
		String str_msg = null; //
		if (_obj == null) {
			str_msg = "返回报文为空!";
		} else {
			if (_obj instanceof ResponseMsg) {
				str_msg = ((ResponseMsg) _obj).getResponseResult(); //
			} else {
				str_msg = String.valueOf(_obj); //
			}
		}

		if (str_msg.length() > 1000) {
			str_msg = "返回内容太长,截取前1000位:" + str_msg.substring(0, 1000); //
		}

		String str_printmsg = "\r\n\r\n★★★★★引擎返回报文★★★★★\r\n" + str_msg + "\r\n★★★★★引擎返回报文★★★★★\r\n\r\n";
		logger.debug(str_printmsg);
	}

	/*
	 * 发送异步消息
	 *
	 * @param params 发送的JSON格式命令
	 *
	 * @param type
	 * 指定命令的类型，分别为指标计算或校验：CommandRemoteType.INDEX，指标查询：CommandRemoteType.QUERY
	 */
	public static final void sendAync(String params, CommandRemoteType type) {
		getInstance().internalImpl(params, type, false);
	}

	/*
	 * 发送同步消息，直到返回结果
	 *
	 * @param params 发送的JSON格式命令
	 *
	 * @param type
	 * 指定命令的类型，分别为指标计算或校验：CommandRemoteType.INDEX，指标查询：CommandRemoteType.QUERY
	 *
	 * @return Object 返回结果，格式分别为与引擎的约定字符串
	 */
	public static final Object sendSync(String params, CommandRemoteType type) {
		Object obj = getInstance().internalImpl(params, type, true);
		if (obj != null && (obj instanceof ResponseMsg)) {
			return ((ResponseMsg) obj).getResponseResult();
		}
		return null;
	}

	public static boolean testConnection(CommandRemoteType type) {
		return getInstance().internalImpl(null, type, true) == null;
	}

	private static CommandRemote getInstance() {
		return SpringContextHolder.getBean("commandRemote");
	}
}
