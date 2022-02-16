package com.yusys.bione.frame.authobj.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

@Component
public class BioneAuthObjNotifier {

	public static final int OP_ADD = BioneAuthObjAlterListener.OP_ADD;
	
	public static final int OP_MODIFY = BioneAuthObjAlterListener.OP_MODIFY;
	
	public static final int OP_REMOVE = BioneAuthObjAlterListener.OP_REMOVE;

	private List<BioneAuthObjAlterListener> listenerList;
	
	public BioneAuthObjNotifier() {
		listenerList = new ArrayList<BioneAuthObjAlterListener>();
	}
	
	/**
	 * 注册监听用户信息、机构信息和角色信息的增、删、改
	 */
	public void register(BioneAuthObjAlterListener listener) {
		listenerList.add(listener);
	}

	/**
	 * 用户增、删、改
	 * 
	 * @param opType 操作类型，包括OP_ADD、OP_MODIFY 和 OP_REMOVE
	 * @param userInfo 用户对象
	 */
	public void alterUserNotify(int opType, BioneUserInfo userInfo) {
		for (int i = 0; i < listenerList.size(); i ++) {
			listenerList.get(i).onAlterUser(opType, userInfo.getUserId(),
					userInfo.getUserNo(), userInfo.getUserName(), null);
		}
	}

	/**
	 * 机构增、删、改
	 * 
	 * @param opType 操作类型，包括OP_ADD、OP_MODIFY 和 OP_REMOVE
	 * @param orgInfo 机构对象
	 */
	public void alterOrgNotify(int opType, BioneOrgInfo orgInfo) {
		for (int i = 0; i < listenerList.size(); i ++) {
			listenerList.get(i).onAlterOrg(opType, orgInfo.getOrgId(), orgInfo.getOrgNo(),
					orgInfo.getOrgName(), null);
		}
	}

	/**
	 * 角色增、删、改
	 * 
	 * @param opType 操作类型，包括OP_ADD、OP_MODIFY 和 OP_REMOVE
	 * @param roleInfo 角色对象
	 */
	public void alterRoleNotify(int opType, BioneRoleInfo roleInfo) {
		for (int i = 0; i < listenerList.size(); i ++) {
			listenerList.get(i).onAlterRole(opType, roleInfo.getRoleId(), roleInfo.getRoleName(), null);
		}
	}
}
