package com.yusys.bione.frame.activiti;

import java.util.List;
import java.util.Map;

/**
 * activiti组件的运行环境接口
 */
public interface ActivitiSupport {

	/**
	 * 获取当前登录用户ID
	 * 
	 * @return 当前登录用户ID
	 */
	String getCurrentUserId();

	/**
	 * 判断当前登录用户是否超级用户
	 * 
	 * @return 当前登录用户是否超级用户
	 */
	boolean isCurrentUserSuperUser();

	/**
	 * 根据任务角色获取执行候选人ID
	 * 
	 * @return 执行候选人ID列表
	 */
	List<String> calCandidateUser(String roleId, Map<String, Object> parameter);
}
