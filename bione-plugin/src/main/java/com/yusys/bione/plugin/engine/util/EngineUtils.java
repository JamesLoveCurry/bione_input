/**
 *
 */
package com.yusys.bione.plugin.engine.util;

import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;

/**
 * <pre>
 * Title:引擎相关工具类
 * Description: 引擎相关工具类
 * </pre>
 *
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 *
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class EngineUtils {

	// 刷新全部缓存（目前无用）
	public static void refreshAllCache() {
		RefreshType[] types = RefreshType.values();
		for (RefreshType typeTmp : types) {
			StringBuilder cmd = new StringBuilder("");
			cmd.append("{\"").append(typeTmp.toString()).append("\":\"\"}");
			try {
				CommandRemote.sendAync(cmd.toString(), CommandRemoteType.QUERY);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			//如果查询和跑数server不一致，需要同步刷新跑数server
			if(!judgeServer()) {
				try {
					CommandRemote.sendAync(cmd.toString(), CommandRemoteType.INDEX);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 刷新指标配置缓存
	public static void refreshIdxCfgCache() {
		try {
			StringBuilder cmd = new StringBuilder("");
			cmd.append("{\"").append(RefreshType.REFRESH_TYPE_CFG).append("\":\"\"}");
			CommandRemote.sendAync(cmd.toString(), CommandRemoteType.QUERY);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		//如果查询和跑数server不一致，需要同步刷新跑数server（只刷新server）
		if(!judgeServer()) {
			try {
				StringBuilder cmd = new StringBuilder("");
				cmd.append("{\"").append(RefreshType.REFRESH_TYPE_CFG_ONLYSERVER).append("\":\"\"}");
				CommandRemote.sendAync(cmd.toString(), CommandRemoteType.INDEX);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	// 刷新数据源缓存，数据源只有node需要刷新所以不用考虑跑数、查询server不一致的情况
	public static void refreshDsCfgCache() {
		StringBuilder cmd = new StringBuilder("");
		cmd.append("{\"").append(RefreshType.REFRESH_TYPE_DS).append("\":\"\"}");
		try {
			CommandRemote.sendAync(cmd.toString(), CommandRemoteType.QUERY);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// 刷新节点缓存
	public static void refreshNodeCfgCache() {
		StringBuilder cmd = new StringBuilder("");
		cmd.append("{\"").append(RefreshType.REFRESH_TYPE_NODE).append("\":\"\"}");
		try {
			CommandRemote.sendAync(cmd.toString(), CommandRemoteType.QUERY);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		//如果查询和跑数server不一致，需要同步刷新跑数server
		if(!judgeServer()) {
			try {
				CommandRemote.sendAync(cmd.toString(), CommandRemoteType.INDEX);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	// 刷新模版缓存（目前无用）
	public static void refreshModelCfgCache() {
		StringBuilder cmd = new StringBuilder("");
		cmd.append("{\"").append(RefreshType.REFRESH_TYPE_MODEL).append("\":\"\"}");
		try {
			CommandRemote.sendAync(cmd.toString(), CommandRemoteType.QUERY);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		//如果查询和跑数server不一致，需要同步刷新跑数server
		if(!judgeServer()) {
			try {
				CommandRemote.sendAync(cmd.toString(), CommandRemoteType.INDEX);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	// 刷新缓存（目前无用）
	public static void refreshIndexCfgCache() {
		StringBuilder cmd = new StringBuilder("");
		cmd.append("{\"").append(RefreshType.REFRESH_TYPE_INDEX).append("\":\"\"}");
		try {
			CommandRemote.sendAync(cmd.toString(), CommandRemoteType.QUERY);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		//如果查询和跑数server不一致，需要同步刷新跑数server
		if(!judgeServer()) {
			try {
				CommandRemote.sendAync(cmd.toString(), CommandRemoteType.INDEX);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断当前server节点是否一致
	 * @return
	 */
	private static boolean judgeServer() {
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-plugin/extension/index-config.properties");
		String serverIndexIp = propertiesUtils.getProperty("server_1_index_ip_address");
		String serverQueryIp = propertiesUtils.getProperty("server_1_query_ip_address");
		if(!serverIndexIp.equals(serverQueryIp)) {
			return false;
		}
		String serverIndexPort = propertiesUtils.getProperty("server_1_index_port");
		String serverQueryPort = propertiesUtils.getProperty("server_1_query_port");
		if(!serverIndexPort.equals(serverQueryPort)) {
			return false;
		}
		return true;
	}
}
