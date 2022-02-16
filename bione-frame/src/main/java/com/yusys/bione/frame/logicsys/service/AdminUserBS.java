package com.yusys.bione.frame.logicsys.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.repository.jpa.JPAAnnotationMetadataUtil;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.logicsys.entity.BioneAdminUserInfo;

/**
 * 
 * <pre>
 * Title: 逻辑系统管理员操作类
 * Description: 添加、修改 逻辑系统管理员
 * </pre>
 * 
 * @author yunlei yunlei@yuchengtech.com
 * @version 1.00.00
 * 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AdminUserBS extends BaseBS<BioneAdminUserInfo>{

	/**
	 * 批量保存
	 * @param adminUsers
	 */
	@Transactional(readOnly = false)
	public void updateEntities(List<BioneAdminUserInfo> adminUsers){
		for (BioneAdminUserInfo bioneAdminUserInfo : adminUsers) {
			this.updateEntity(bioneAdminUserInfo);
		}
	}
	
	/**
	 * 保存 逻辑系统 管理员
	 * @param adminUsersOld
	 * @param adminUsers
	 */
	@Transactional(readOnly = false)
	public void saveLogicSysAdmin(String id,List<BioneAdminUserInfo> adminUsers){
		removeEntity(getAdminUserByLogicId(id));
		updateEntities(adminUsers);
	}
	
	/**
	 * 逻辑系统已有的管理员
	 * @param id
	 * @return
	 */
	public List<BioneAdminUserInfo> getAdminUserByLogicId(String id){
		return getEntityListByProperty(BioneAdminUserInfo.class, "id.logicSysId", id);
	}
	
	/**
	 * 获取与逻辑系统有关的全部实体
	 */
	public Map<String, Map<String, List<Object>>> getObjectListWidthLogicSysNo(List<String> entitiesName, String logicSysNo) {
		// JPA 元数据 工具类
		JPAAnnotationMetadataUtil metadataUtils = new JPAAnnotationMetadataUtil();
		// 最终结果集合
		Map<String, Map<String, List<Object>>> result = Maps.newHashMap();
		// 结果集合
		Map<String, List<Object>> entities = Maps.newHashMap();
		result.put(logicSysNo, entities);
		// 遍历所有已知实体, 获取数据
		for (String entityName : entitiesName) {
			Object obj = null;
			// 通过 字符串实例化实体对象
			try {
				obj = Class.forName(entityName).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (obj != null) {
				StringBuilder jql = new StringBuilder("select obj from " + entityName + " obj where 1=1");
				// 是否具有逻辑系统标识符字段(logicSysNo), 如果没有测导出全部.
				if (metadataUtils.get(obj.getClass()).getPropertyType("logicSysNo") != null) {
					// 判断是否为联合主键
					if (!metadataUtils.getIdPropertyType(obj).isEmeddable()) {
						jql.append(" and obj.logicSysNo = '" + logicSysNo + "'");
					} else {
						jql.append(" and obj." + metadataUtils.getIdPropertyName(obj) + ".logicSysNo = '" + logicSysNo + "'");
					}
				}
				entities.put(entityName, this.baseDAO.findWithNameParm(jql.toString(), null));
			}
		}
		return result;
	}

	/**
	 * 判断用户是否是管理员
	 * @param userId 用户ID
	 * @return 是管理员返回true,否则false
	 */
	public boolean checkIsUserBeAdmin(String userId){
		boolean flag = false;
		String jql = "SELECT user FROM BioneAdminUserInfo user WHERE user.id.userId=?0";
		List<BioneAdminUserInfo> adminUserList = this.baseDAO.findWithIndexParam(jql, userId);
		if (adminUserList != null && adminUserList.size() > 0) {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 判断用户是否是管理员
	 * @param userId 用户ID
	 * @return 是管理员返回true,否则false
	 */
	public boolean checkIsUserBeAdmin(String logicSysId, String userId){
		boolean flag = false;
		String jql = "SELECT user FROM BioneAdminUserInfo user WHERE user.id.logicSysId = ?0 and user.id.userId = ?1";
		List<BioneAdminUserInfo> adminUserList = this.baseDAO.findWithIndexParam(jql, logicSysId, userId);
		if (adminUserList != null && adminUserList.size() > 0) {
			flag = true;
		}
		return flag;
	}
}
