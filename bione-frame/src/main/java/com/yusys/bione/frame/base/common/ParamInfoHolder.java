/**
 * 
 */
package com.yusys.bione.frame.base.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.logicsys.service.LogicSysBS;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.frame.variable.entity.BioneParamTypeInfo;
import com.yusys.bione.frame.variable.service.ParamBS;
import com.yusys.bione.frame.variable.service.ParamTypeBS;
/**
 * 
 * <pre>
 * Title:系统参数信息持有者
 * Description: 将系统参数存入缓存，避免在获取逻辑系统列表时反复访问数据库，利用缓存框架集群支持功能，可以将逻辑系统信息在WEB集群中之间共享。
 * </pre>
 * 
 * @author songxf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ParamInfoHolder {

	private static Logger logger = LoggerFactory
			.getLogger(ParamInfoHolder.class);

	// 信息在缓存中的名称
	private static final String PARAMETER_CACHE_NAME = "param_cache_name";
	private static final String PARAMETER_CACHE_ENTITY = "param_cache_entity";
	// 信息在缓存中的key值
	private static CacheManager cacheManager;

	private static Object lock = new Object();
	static {

		cacheManager = SpringContextHolder.getBean("shiroCacheManager");
	}

	/**
	 * 更新参数信息，新增或者修改资源操作信息后需要调用此方法
	 * （目前没有提供前台配置功能，当手工修改数据库的信息后只有等待缓存过期，然后自动从数据库获取最新的的数据）
	 */
	public static void refreshParamInfo() {

		synchronized (lock) {

			initParamInfo();

		}
	}

	/**
	 * 清理逻辑系统信息
	 */
	public static void clearParamInfo() {

		synchronized (lock) {
			cacheManager.getCache(PARAMETER_CACHE_NAME).clear();
		}
	}

	/**
	 * 初始化逻辑系统信息并存放到缓存中
	 */
	public static void initParamInfo() {

		logger.info("缓存参数信息开始........");
		LogicSysBS logicSysBS = SpringContextHolder.getBean("logicSysBS");
		ParamTypeBS paramTypeBS = SpringContextHolder.getBean("paramTypeBS");
		ParamBS paramBS = SpringContextHolder.getBean("paramBS");
		List<BioneLogicSysInfo> logicSysList = logicSysBS.findLogicSysInfo();
		String logicSysNo = "";
		String paramTypeNo = "";
		for (BioneLogicSysInfo logicSysInfo : logicSysList) {
			logicSysNo = logicSysInfo.getLogicSysNo();
			List<BioneParamTypeInfo> paramTypeList = paramTypeBS
					.findParamTypeList(logicSysNo);
			Map<String, Map<String, String>> paramTypeMap = Maps.newLinkedHashMap();
			for (BioneParamTypeInfo paramTypeInfo : paramTypeList) {
				paramTypeNo = paramTypeInfo.getParamTypeNo();
				List<BioneParamInfo> paramList = paramBS.findParamBySysAndType(
						logicSysNo, paramTypeNo);
				Map<String, String> paramMap = Maps.newLinkedHashMap();
				for (BioneParamInfo paramInfo : paramList) {
					paramMap.put(paramInfo.getParamValue(),
							paramInfo.getParamName());
				}
				paramTypeMap.put(paramTypeNo, paramMap);
			}
			cacheManager.getCache(PARAMETER_CACHE_NAME).put(logicSysNo,
					paramTypeMap);
		}
		logger.info("缓存逻辑系统信息结束.");

	}

	/**
	 * 从缓存中获取逻辑系统的参数类型信息
	 * 
	 * @param logicSysNo
	 *            逻辑系统编号
	 * @param paramTypeNo
	 *            参数类型标识
	 * @return 参数Map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getParamInfo(String logicSysNo,
			String paramTypeNo) {

		Map<String, Map<String, String>> paramTypeMap = null;
		Map<String, String> paramMap = null;

		synchronized (lock) {
			paramTypeMap = (Map<String, Map<String, String>>) cacheManager
					.getCache(PARAMETER_CACHE_NAME).get(logicSysNo);
			if (paramTypeMap != null) {
				paramMap = paramTypeMap.get(paramTypeNo);
			}

			// 缓存过期后，重新初始化
			if (paramTypeMap == null || paramMap == null) {

				initParamInfo();
				paramTypeMap = (Map<String, Map<String, String>>) cacheManager
						.getCache(PARAMETER_CACHE_NAME).get(logicSysNo);
				paramMap = paramTypeMap.get(paramTypeNo);
			}
		}

		return paramMap;
	}

	/**
	 * 从缓存中获取逻辑系统的参数名称
	 * 
	 * @param logicSysNo
	 *            逻辑系统编号
	 * @param paramTypeNo
	 *            参数类型标识
	 * @return paramName 参数名称
	 */
	public static String getParamName(String logicSysNo, String paramTypeNo,
			String paramValue) {
		String paramName = "";
		Map<String, String> paramMap = getParamInfo(logicSysNo, paramTypeNo);
		if (paramMap != null) {
			paramName = paramMap.get(paramValue);
			if (paramName != null) {
				return paramName;
			}
		}
		return paramValue;
	}
	
	/**
	 * 从缓存中获取逻辑系统的参数 支持树形数据
	 * @param logicSysNo
	 * @param paramTypeNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<CommonTreeNode> getParamInfoTree(String logicSysNo,
			String paramTypeNo) {
		Map<String, List<BioneParamInfo>> paramTypeMap = null;
		List<BioneParamInfo> paramMap = null;
		synchronized (lock) {
			paramTypeMap = (Map<String, List<BioneParamInfo>>) cacheManager
					.getCache(PARAMETER_CACHE_ENTITY).get(logicSysNo);
			if (paramTypeMap != null) {
				paramMap = paramTypeMap.get(paramTypeNo);
			}

			// 缓存过期后，重新初始化
			if (paramTypeMap == null || paramMap == null) {

				initParamInfo();
				paramTypeMap = (Map<String, List<BioneParamInfo>>) cacheManager
						.getCache(PARAMETER_CACHE_ENTITY).get(logicSysNo);
				if(paramTypeMap!=null){
					paramMap = paramTypeMap.get(paramTypeNo);
				}
				
			}
		}

		return buildTree(paramMap);
	}
	
	private static List<CommonTreeNode> buildTree(List<BioneParamInfo> list) {
		List<CommonTreeNode> root = Lists.newArrayList();
		Map<String, CommonTreeNode> P = Maps.newHashMap();
		Map<String, List<CommonTreeNode> > hasP = Maps.newHashMap();
		if (list != null && list.size() > 0) {
			// 遍历所有实体
			for (BioneParamInfo info : list) {
				CommonTreeNode node = buildNode(info);
				String upNo = info.getUpNo();
				// 将根节点放入root
				if (upNo == null || "".equals(upNo) || "0".equals(upNo)) {
					root.add(node);
				}
				// 将当前节点存入P
				P.put(info.getParamId(), node);
				//// 查看该节点的父节点
				if (P.containsKey(upNo)) {
					// 父节点在P中存在
					P.get(upNo).addChildNode(node);
				} else {
					// 父节点在P中不存在，将该节点存入寻求父节点队列hasP,key为父节点ID
					if (hasP.containsKey(upNo)) {
						hasP.get(upNo).add(node);
					} else {
						List<CommonTreeNode> nodel = Lists.newArrayList();
						nodel.add(node);
						hasP.put(upNo, nodel);
					}
				}
				////查看该节点的子节点
				String paramId = info.getParamId();
				if (hasP.containsKey(paramId)) {
					node.getChildren().addAll(hasP.get(paramId));
				}
			}
			return root;
		}
		return null;
	}
	
	private static CommonTreeNode buildNode(BioneParamInfo info) {
		CommonTreeNode node = new CommonTreeNode();
		node.setId(info.getParamValue());
		node.setText(info.getParamName());
		return node;
	}
}
