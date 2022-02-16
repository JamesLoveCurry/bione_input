/**
 * 
 */
package com.yusys.bione.frame.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;

/**
 * <pre>
 * Title:安全认证的工具类
 * Description: 扩展shiro框架的SecurityUtils类
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class BioneSecurityUtils extends SecurityUtils {

	private static Logger logger = LoggerFactory
			.getLogger(BioneSecurityUtils.class);

	/**
	 * 返回当前用户的信息
	 * 
	 * @return
	 */
	public static BioneUser getCurrentUserInfo() {
		return (BioneUser) getSubject().getPrincipal();
	}

	/**
	 * 获取当前用户的用户ID
	 * 
	 * @return
	 */
	public static String getCurrentUserId() {

		return getCurrentUserInfo().getUserId();
	}
	/**
	 * 根据用户ID,获取当前用户有权限访问的某类资源的集合
	 * 
	 * @param resDefNo
	 *            资源标识
	 * @return 资源标识对象Id
	 */
	public static List<String> getResListByUserId(String resDefNo){
		AuthBS authBS = SpringContextHolder.getBean("authBS");
		String logicSysNo = getCurrentUserInfo().getCurrentLogicSysNo();
		// 查找用户具有的权限
		List<String> objIdList = Lists.newArrayList();
		objIdList.add(getCurrentUserId());
		List<String> resIdListOfUser = authBS.findAuthResIdListByType(
				logicSysNo, resDefNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER,
				objIdList);
	
		if(resIdListOfUser != null && resIdListOfUser.size() > 0){
			return resIdListOfUser;
		}else{
			return Lists.newArrayList();
		}
		
	}
	/**
	 * 获取用户有权限访问的某类资源的集合
	 * 
	 * @param resDefNo
	 *            资源标识
	 * @return
	 */
	public static List<String> getResIdListOfUser(String resDefNo) {

		List<String> resIdList = Lists.newArrayList();
		Set<String> resIdSet = new HashSet<String>();
		AuthBS authBS = SpringContextHolder.getBean("authBS");
		String logicSysNo = getCurrentUserInfo().getCurrentLogicSysNo();
		boolean isMsgRes = GlobalConstants4frame.AUTH_RES_DEF_ID_MSG.equals(resDefNo);
		Set<String> msgIdSet = Sets.newHashSet();
		
		// 查找用户具有的权限
		List<String> objIdList = Lists.newArrayList();
		objIdList.add(getCurrentUserId());
		
		if (isMsgRes) {
			List<String> list = authBS.findAuthMsgIdListByType(logicSysNo,
					GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER , objIdList);
			if (list != null) {
				msgIdSet.addAll(list);
			}
		} else {
			System.out.println(System.currentTimeMillis()*1000);
			List<String> resIdListOfUser = authBS.findAuthResIdListByType(
					logicSysNo, resDefNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER,
					objIdList);
			System.out.println(System.currentTimeMillis()*1000);
			if (resIdListOfUser != null) {
				resIdSet.addAll(resIdListOfUser);
			}
		}
		

		// 查找当前用户是否属于某个授权组
		List<String> authGrpIdList = authBS
				.findAuthGroupIdOfCurrentUser(logicSysNo);
		if (isMsgRes) {
			List<String> list = authBS.findAuthMsgIdListByType(logicSysNo,
					GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP , authGrpIdList);
			if (list != null) {
				msgIdSet.addAll(list);
			}
		} else {
			List<String> resIdListOfGrp = authBS.findAuthResIdListByType(
					logicSysNo, resDefNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP,
					authGrpIdList);
			if (resIdListOfGrp != null) {
				resIdSet.addAll(resIdListOfGrp);
			}
		}
		
		
		// 当前用户授权对象的集合
		Map<String, List<String>> userAuthObjMap = getCurrentUserInfo()
				.getAuthObjMap();
		Iterator<String> it = userAuthObjMap.keySet().iterator();

		String objDefNo = null;
		List<String> authObjIdList = null;
		while (it.hasNext()) {

			objDefNo = it.next();
			authObjIdList = userAuthObjMap.get(objDefNo);

			if (authObjIdList != null) {
				// 授权对象所拥有的资源访问集合
				if (isMsgRes) {
					List<String> list = authBS.findAuthMsgIdListByType(logicSysNo,
							objDefNo , authObjIdList);
					if (list != null) {
						msgIdSet.addAll(list);
					}
				} else {
					List<String> resIdOfAuthObj = authBS.findAuthResIdListByType(
							logicSysNo, resDefNo, objDefNo, authObjIdList);
					if (resIdOfAuthObj != null) {
						resIdSet.addAll(resIdOfAuthObj);
					}
				}
			}
		}
		resIdList.addAll(resIdSet);
		return isMsgRes ? Lists.newArrayList(msgIdSet) : resIdList;
	}
	
	/**
	 * 判断指定逻辑系统是否使用分级权限
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public static boolean checkIsHierarchicalAuth(String logicSysNo) {
		if (StringUtils.isEmpty(logicSysNo)) {
			// 若逻辑系统为空，获取当前逻辑系统
			logicSysNo = getCurrentUserInfo().getCurrentLogicSysNo();
		}
		boolean flag = false;
		if (GlobalConstants4frame.SUPER_LOGIC_SYSTEM.equals(logicSysNo)) {
			// 若是超级逻辑系统，不采用分级权限
			return flag;
		}
		if (getCurrentUserInfo().isSuperUser()) {
			// 若当前用户是逻辑系统管理员，不采用分级权限
			return flag;
		}
		// 目前暂使用配置文件的方式统一配置
		try {
			PropertiesUtils pUtils = PropertiesUtils.get(
					"bione-frame/security/security.properties");
			flag = pUtils.getBoolean("auth.isHierarchicalAuth");
		} catch (Exception e) {
			logger.warn("平台无法找到security.properties文件，不使用分级授权！");
		}
		return flag;
	}

	/**
	 * 在分级授权体系下，获取指定机构的权限内机构编号集合
	 * 
	 * @param curOrgNo
	 * @return
	 */
	public static List<String> getOrgsByHierarchicalAuth() {
		List<String> orgNos = new ArrayList<String>();
		BioneUser user = getCurrentUserInfo();
		String curOrgNo = user.getOrgNo();
		if (!StringUtils.isEmpty(curOrgNo)) {
			// 获取分级授权类型
			String levelType = "";
			try {
				PropertiesUtils pUtils = PropertiesUtils.get(
						"bione-frame/security/security.properties");
				levelType = pUtils.getProperty("auth.levelType");
			} catch (Exception e) {
				logger.warn("平台无法找到security.properties文件，不使用分级授权！");
				return orgNos;
			}
			AuthBS authBS = SpringContextHolder.getBean("authBS");
			List<String> childOrgNos = authBS.getChildOrgNos(curOrgNo , levelType);
			orgNos.addAll(childOrgNos);
		}
		return orgNos;
	}

	/**
	 * 获得加密后的密码字符串
	 * 
	 * @param password
	 *            密码
	 * @return String 加密后的密码
	 */
	public static String getHashedPasswordBase64(String password) {
		return new org.apache.shiro.crypto.hash.Sha256Hash(password,
				ByteSource.Util.bytes(GlobalConstants4frame.CREDENTIALS_SALT))
				.toBase64();
	}

	/******* Private Methods *******/

	/**
	 * @SuppressWarnings("unused")
	 * (在BIONE机构表添加namespace字段后，迭代获取子机构的方法被弃用)
	 * 迭代获取指定机构的下级机构编号
	 * 
	 * @param orgNo
	 * @param levelType
	 * @param queryTimes
	 * @param logicSysNo
	 * @param childOrgNos
	 * @return
	 */
	@SuppressWarnings("unused")
	private static List<String> getChildOrgs(List<String> orgNos,
			String levelType, Integer queryTimes, String logicSysNo,
			List<String> childOrgNos) {
		if (childOrgNos == null) {
			childOrgNos = new ArrayList<String>();
		}
		if (orgNos != null && orgNos.size() > 0) {
			if (GlobalConstants4frame.AUTH_LEVEL_TYPE_NEXT.equals(levelType)
					&& queryTimes == 1) {
				// 本级机构和下一级机构资源的授权权限
				// 已进行了一次查询，停止继续迭代
				return childOrgNos;
			}
			AuthBS authBS = SpringContextHolder.getBean("authBS");
			List<String> orgs = authBS.getOrgNosByParent(orgNos, logicSysNo);
			queryTimes++;
			if (orgs != null && orgs.size() > 0) {
				childOrgNos.addAll(orgs);
				// 迭代查询子机构
				childOrgNos.addAll(getChildOrgs(orgs, levelType, queryTimes,
						logicSysNo, childOrgNos));
			}
		}
		return childOrgNos;
	}

}
