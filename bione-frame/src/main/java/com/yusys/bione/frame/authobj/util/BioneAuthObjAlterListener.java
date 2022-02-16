package com.yusys.bione.frame.authobj.util;

import java.util.Map;

/**
 * 跟踪用户信息、机构信息和角色信息的变更
 */
public interface BioneAuthObjAlterListener {

	public static final int OP_ADD = 1;
	
	public static final int OP_MODIFY = 2;
	
	public static final int OP_REMOVE = 3;

	/**
	 * 用户增、删、改
	 * 
	 * @param opType 操作类型，包括OP_ADD、OP_MODIFY 和 OP_REMOVE
	 * @param userId 用户ID
	 * @param userNo 用户号
	 * @param userName 用户名
	 * @param parameter 其他参数
	 */
	public void onAlterUser(int opType, String userId, String userNo, String userName, Map<String, String> parameter);
	
	/**
	 * 机构增、删、改
	 * 
	 * @param opType 操作类型，包括OP_ADD、OP_MODIFY 和 OP_REMOVE
	 * @param orgId 机构ID
	 * @param orgNo 机构号
	 * @param orgName 机构名称
	 * @param parameter 其他参数
	 */
	public void onAlterOrg(int opType, String orgId, String orgNo, String orgName, Map<String, String> parameter);

	/**
	 * 角色增、删、改
	 * 
	 * @param opType 操作类型，包括OP_ADD、OP_MODIFY 和 OP_REMOVE
	 * @param roleId 角色ID
	 * @param roleName 角色名称
	 * @param parameter 其他参数
	 */
	public void onAlterRole(int opType, String roleId, String roleName, Map<String, String> parameter);
}
