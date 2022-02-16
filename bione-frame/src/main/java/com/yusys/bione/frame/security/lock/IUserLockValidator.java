/**
 * 
 */
package com.yusys.bione.frame.security.lock;


import java.sql.Timestamp;

import com.yusys.bione.frame.security.lock.entity.BioneLockInfoBO;

/**
 * <pre>
 * Title:用户锁定相关处理
 * Description: 用户锁定相关处理 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public interface IUserLockValidator {
	
	/**
	 * 获取指定用户在特定应用系统下是否是锁定状态
	 * @param userId
	 * @param logicSysNo
	 * @param currTime
	 * @param isSuperUser
	 * @return 锁定信息相关BO对象
	 */
	public BioneLockInfoBO getLockStsByUser(String userId , String logicSysNo , Timestamp currTime , boolean isSuperUser);

	/**
	 * 当用户名存在，却输入密码错误时的处理
	 * @param userNo
	 * @param logicSysNo
	 * @param userIp
	 * @param isSuperUser
	 * @return 锁定信息相关BO对象
	 */
	public  BioneLockInfoBO manageWhenPwdError(String userNo , String logicSysNo , String userIp , boolean isSuperUser);
	
	/**
	 * 按指定用户标识解锁
	 * @param userNo
	 * @param logicSysNo
	 * @param isSuperUser
	 */
	public void resetUserByNo(String userNo , String logicSysNo , boolean isSuperUser);
	
	/**
	 * 按指定用户id解锁
	 * @param userNo
	 * @param logicSysNo
	 * @param isSuperUser
	 */
	public void resetUserById(String userId , String logicSysNo , boolean isSuperUser);
	
	/**
	 * 全局清除用户锁定信息
	 */
	public void clearAll();
	
}
