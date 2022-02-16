package com.yusys.bione.plugin.yuformat.utils;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.PropertiesUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * 执行 shell 命令 工具类
 * 支持 检核引擎 多server 高可用部署
 */
public class ShellExecutorUtils {
	public static Log log = LogFactory.getLog(ShellExecutorUtils.class);
	/**
	 * 执行命令 入口
	 * @param
	 * @throws Exception
	 */
	public static void exec(String taskCmd,String properties) throws Exception {
		//调用主机参数
		RemoteShellPropertyVO sPVO = ShellExecutorUtils.getShellPropertyVO(properties);
		log.info("引擎访问方式:"+sPVO.getFlag());
		//引擎访问方式
		switch (sPVO.getFlag()) {
			case "on":
				//1. spark脚本和war包不在一个服务器上
				//1.1. 初始化 Server 调用器
				RemoteShellExecutor shellExecutor = new RemoteShellExecutor();
				
				if(sPVO.getSshIp().indexOf(";") > 0
						&& sPVO.getSshUserName().indexOf(";") > 0
							&& sPVO.getSshPassword().indexOf(";") > 0){
					//1.1.1  检核引擎 多server 高可用部署 优化
					String[] sshIPs = sPVO.getSshIp().split(";");
					String[] sshUserNms = sPVO.getSshUserName().split(";");
					String[] sshPassWd = sPVO.getSshPassword().split(";");
					//通信状态
					boolean isExecSuss = false;
					//循环多Server
					for(int i = 0; i < sshIPs.length; i++){
						// 返回结果   -1 执行失败   0 执行成功
						int rs = shellExecutor.exec(sshIPs[i], sshUserNms[i], sshPassWd[i], taskCmd);
						if(rs > -1){
							// 执行成功
							isExecSuss = true;
							break;
						}
					}
					//如果执行失败
					if(!isExecSuss){
						// 自定义异常类 实现略
						throw new Exception("调用远程机器失败！ " + sPVO.getSshIp()); 
					}
				}else{
					//1.1.2  单Server 调用
					shellExecutor.exec(sPVO.getSshIp(), sPVO.getSshUserName(), sPVO.getSshPassword(), taskCmd);
				}
				break;
			case "off":
				//2.spark脚本和war包在一个服务器上
				log.info("执行脚本命令:" + taskCmd);
				Runtime.getRuntime().exec(taskCmd);
				break;
			case "rsa":
				//3.实现免密登陆
				new RemoteShellExecutor().exec(sPVO, taskCmd); 
				break;
			default:
				break;
		}
	}
	/**
	 * @方法描述: 通过调用单条检核启动引擎
	 * @创建人: miaokx@yusys.com.cn
	 * @创建时间: 2021/4/26 11:34
	 * @Param: taskCmd
	 * @Param: properties
	 * @Param: params
	 * @return: void
	 */
	public static void execByRest(String taskCmd,String properties) throws Exception {
		//调用主机参数
		RemoteShellPropertyVO sPVO = ShellExecutorUtils.getShellPropertyVO(properties);
		JSONObject params = new JSONObject();
		params.put("batchshell", 1);
		// 异步
		params.put("sync", 0);
		params.put("shell_path", sPVO.getPath());
		String[] paramsArr = taskCmd.split(" ");
		for (String param: paramsArr) {
			if (param.contains("--")) {
				param = param.replaceAll("--", "");
				if (param.contains("=")) {
					String[] paramArr = param.split("=");
					params.put(paramArr[0], paramArr[1]);
				} else {
					params.put(param, "");
				}
			}
		}
		log.info("引擎访问方式:"+sPVO.getFlag());
		if ("rest".equalsIgnoreCase(sPVO.getFlag())) {
			String dataStr = HttpURLConnectionUtil.doPost(sPVO.getSingleUrl(), params.toString());
			if ("400".equals(dataStr) || "500".equals(dataStr) || "999".equals(dataStr)) {
				throw new Exception("调用url失败！ " + sPVO.getSingleUrl());
			}
		} else {
			exec(taskCmd, properties);
		}
	}
	/**
	 * 获取 引擎访问方式 及 远程服务器登录信息
	 * @return
	 */
	public static RemoteShellPropertyVO getShellPropertyVO(String properties) {
		// 20200330 在database.properties中新增shell.flag，用于控制访问引擎的方式。
		// flag是开关，on表示开，off表示关，rsa表示免密登录。当spark引擎和war包在同一台服务器上，配置为off，不在同一台服务器上配置为on
		PropertiesUtils pubUtil = PropertiesUtils.get(properties);
		// 获取shellFlag
		RemoteShellPropertyVO shellPropertyVO = new RemoteShellPropertyVO();
		shellPropertyVO.setFlag(pubUtil.getProperty("shell.flag"));
		log.info("shell.flag");
		shellPropertyVO.setSshIp(pubUtil.getProperty("shell.ip"));
		log.info("shell.ip");
		shellPropertyVO.setSshUserName(pubUtil.getProperty("shell.username"));
		shellPropertyVO.setSshPassword(pubUtil.getProperty("shell.password"));
		shellPropertyVO.setKnownHosts(pubUtil.getProperty("shell.known_hosts"));
		shellPropertyVO.setIdRsa(pubUtil.getProperty("shell.id_rsa"));
		log.info("shell.id_rsa");
		// 暂时注释掉
		shellPropertyVO.setSingleUrl(pubUtil.getProperty("check.data.single"));
		//log.info("check.data.single");
		shellPropertyVO.setPath(pubUtil.getProperty("shell.path"));
		log.info("shell.path");
		return shellPropertyVO;
	}

}
