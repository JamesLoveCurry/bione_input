/**
 * 
 */
package com.yusys.bione.frame.security.lock.entity;

import java.io.Serializable;

/**
 * <pre>
 * Title:锁定信息相关BO对象
 * Description: 锁定信息相关BO对象 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class BioneLockInfoBO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean lockSts;  //锁定状态
	private int unlockRestHours = -1;  //剩余解锁小时
	private int restErrorTimes = -1; //剩余错误次数
	
	
	/**
	 * @return 返回 lockSts。
	 */
	public boolean getLockSts() {
		return lockSts;
	}
	/**
	 * @param lockSts 设置 lockSts。
	 */
	public void setLockSts(boolean lockSts) {
		this.lockSts = lockSts;
	}
	/**
	 * @return 返回 unlockRestHours。
	 */
	public int getUnlockRestHours() {
		return unlockRestHours;
	}
	/**
	 * @param unlockRestHours 设置 unlockRestHours。
	 */
	public void setUnlockRestHours(int unlockRestHours) {
		this.unlockRestHours = unlockRestHours;
	}
	/**
	 * @return 返回 restErrorTimes。
	 */
	public int getRestErrorTimes() {
		return restErrorTimes;
	}
	/**
	 * @param restErrorTimes 设置 restErrorTimes。
	 */
	public void setRestErrorTimes(int restErrorTimes) {
		this.restErrorTimes = restErrorTimes;
	}
	
}
