/**
 * 
 */
package com.yusys.bione.frame.security.lock.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.passwd.entity.BionePwdSecurityCfg;
import com.yusys.bione.frame.passwd.entity.BioneUserLockInfo;
import com.yusys.bione.frame.passwd.entity.BioneUserLockInfoPK;
import com.yusys.bione.frame.security.lock.IUserLockValidator;
import com.yusys.bione.frame.security.lock.entity.BioneLockInfoBO;
import com.yusys.bione.frame.security.lock.service.UserLockManageBS;

/**
 * <pre>
 * Title:以数据库持久化方式处理用户锁定相关动作
 * Description: 以数据库持久化方式处理用户锁定相关动作
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
@Component
public class UserLockDbValidator implements IUserLockValidator {

	// 该认证类管辖的逻辑系统认证类型
	private static List<String> acceptAuthNos = new ArrayList<String>();

	static {
		// 添加受理的认证类型
		// 本地认证-超级系统认证
		acceptAuthNos.add(GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER);
		// 本地认证-逻辑系统认证
		acceptAuthNos.add(GlobalConstants4frame.AUTH_TYPE_LOCAL_SYS);
	}

	private UserLockManageBS lockManageBS = SpringContextHolder
			.getBean("userLockManageBS");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yusys.bione.frame.security.lock.IUserLockValidator#
	 * getLockStsByUser(java.lang.String, java.lang.String)
	 */
	@Override
	public BioneLockInfoBO getLockStsByUser(String userId, String logicSysNo,
			Timestamp currTime, boolean isSuperUser) {
		BioneLockInfoBO lockBO = new BioneLockInfoBO();
		boolean lockFlag = false;
		lockBO.setLockSts(lockFlag);
		// 获取全局密码安全策略
		BionePwdSecurityCfg pwdCfg = lockManageBS.getPwdSecurityCfg();
		if (pwdCfg == null) {
			// 未配置本地密码安全策略
			lockBO.setLockSts(lockFlag);
			return lockBO;
		}
		if (!GlobalConstants4frame.COMMON_STATUS_VALID.equals(pwdCfg.getEnableSts())) {
			// 密码安全策略配置为暂不启用
			lockBO.setLockSts(lockFlag);
			return lockBO;
		} else {
			// 启用密码安全策略，进行用户锁定信息判断
			String authNo = lockManageBS.getSystemAuthNo(logicSysNo);
			if (!acceptAuthNos.contains(authNo)) {
				// 当前逻辑系统认证类型不在本地安全策略管理范畴内
				return lockBO;
			}
			if (GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(authNo)
					|| isSuperUser) {
				// 若是使用的超级逻辑系统认证，或者当前用户是逻辑系统管理员
				// 逻辑系统统一为超级逻辑系统
				logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
			}
			if (!StringUtils.isEmpty(userId)
					&& !StringUtils.isEmpty(logicSysNo)) {
				// 获取用户锁定信息
				BioneUserLockInfo lockInfo = lockManageBS.getLockInfoByUserId(
						userId, logicSysNo);
				if (lockInfo != null
						&& GlobalConstants4frame.COMMON_STATUS_VALID.equals(lockInfo
								.getLockSts())) {
					// 若该账户已被锁定
					lockFlag = true;
					// 剩余时间
					int restHours = -1;
					// 1、若锁定方式为【永久锁定】，返回不做任何操作
					if (GlobalConstants4frame.PWD_SECURITY_LOCK_TYPE_EVER
							.equals(pwdCfg.getLockType())) {
						lockBO.setLockSts(lockFlag);
						return lockBO;
					}
					// 2、若锁定方式为【限时锁定】，判断该用户是否应该解锁
					if (GlobalConstants4frame.PWD_SECURITY_LOCK_TYPE_TIMELIMIT
							.equals(pwdCfg.getLockType())) {
						// 通过安全策略中配置的锁定时间进行判断
						int diffHours = this.getHoursDiff(
								lockInfo.getLockTime(), currTime);
						if (pwdCfg.getLockTime() != null) {
							if (pwdCfg.getLockTime().intValue() <= 0
									|| pwdCfg.getLockTime().intValue() <= diffHours) {
								// 若定义的锁定时间<= 0，或当前用户实际锁定时间已满
								lockFlag = false;
								// 解锁用户
								lockManageBS.unlockByUser(userId, logicSysNo);
							} else {
								restHours = pwdCfg.getLockTime().intValue()
										- diffHours;
							}
						} else {
							// 未定义锁定时间
							lockFlag = false;
							// 解锁用户
							lockManageBS.unlockByUser(userId, logicSysNo);
						}
					}
					lockBO.setLockSts(lockFlag);
					lockBO.setUnlockRestHours(restHours);
				}
			}
		}
		return lockBO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yusys.bione.frame.security.lock.IUserLockValidator#
	 * manageWhenPwdError(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public BioneLockInfoBO manageWhenPwdError(String userNo, String logicSysNo,
			String userIp, boolean isSuperUser) {
		BioneLockInfoBO returnInfo = null;
		// 判断当前逻辑系统认证类型，是否需要做本地密码安全校验
		String authNo = lockManageBS.getSystemAuthNo(logicSysNo);
		if (!acceptAuthNos.contains(authNo)) {
			// 当前逻辑系统认证类型不在本地安全策略管理范畴内
			return returnInfo;
		}
		if (GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(authNo)
				|| isSuperUser) {
			// 若是使用的超级逻辑系统认证，或当前用户是逻辑系统管理员
			// 逻辑系统统一为超级逻辑系统
			logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		}
		// 获取全局密码安全策略
		BionePwdSecurityCfg pwdCfg = lockManageBS.getPwdSecurityCfg();
		if (pwdCfg != null
				&& GlobalConstants4frame.COMMON_STATUS_VALID.equals(pwdCfg
						.getEnableSts())) {
			// 若定义了密码安全策略，且是启用的
			// 获取用户id
			String userId = lockManageBS.getUserIdByNo(userNo, logicSysNo);
			if (!StringUtils.isEmpty(userId)) {
				BioneUserLockInfo lockInfo = lockManageBS.getLockInfoByUserId(
						userId, logicSysNo);
				if (lockInfo == null) {
					lockInfo = new BioneUserLockInfo();
					BioneUserLockInfoPK infoPK = new BioneUserLockInfoPK();
					infoPK.setLogicSysNo(logicSysNo);
					infoPK.setUserId(userId);
					lockInfo.setId(infoPK);
					lockInfo.setUserIp(userIp);
					// 第一次的错误次数当然为1
					lockInfo.setErrorTimes(new BigDecimal(1));
					// 根据安全策略中的最大错误次数，判断是否应该被锁定
					if (pwdCfg.getMaxErrorTimes() != null
							&& pwdCfg.getMaxErrorTimes().intValue() <= 1) {
						lockInfo.setLockSts(GlobalConstants4frame.COMMON_STATUS_VALID);
						// 锁定时才记录锁定时间
						lockInfo.setLockTime(new Timestamp(new Date().getTime()));
					} else {
						lockInfo.setLockSts(GlobalConstants4frame.COMMON_STATUS_INVALID);
					}
					lockManageBS.saveOrUpdateEntity(lockInfo);
				} else if (!GlobalConstants4frame.COMMON_STATUS_VALID.equals(lockInfo
						.getLockSts())) {
					// 该账户暂时未被锁定
					lockInfo.setUserIp(userIp);
					// 根据安全策略中的最大错误次数，判断是否应该被锁定
					Integer oldErrorTime = lockInfo.getErrorTimes() == null ? 0
							: lockInfo.getErrorTimes().intValue();
					if (pwdCfg.getMaxErrorTimes() != null
							&& pwdCfg.getMaxErrorTimes().intValue() <= (oldErrorTime + 1)) {
						lockInfo.setLockSts(GlobalConstants4frame.COMMON_STATUS_VALID);
						// 锁定时才记录锁定时间
						lockInfo.setLockTime(new Timestamp(new Date().getTime()));
						// 更新错误次数
						lockInfo.setErrorTimes(new BigDecimal(oldErrorTime + 1));
					} else {
						lockInfo.setLockSts(GlobalConstants4frame.COMMON_STATUS_INVALID);
						// 更新错误次数
						lockInfo.setErrorTimes(new BigDecimal(oldErrorTime + 1));
					}
					lockManageBS.saveOrUpdateEntity(lockInfo);
				}
				returnInfo = new BioneLockInfoBO();
				returnInfo.setLockSts(GlobalConstants4frame.COMMON_STATUS_VALID
						.equals(lockInfo.getLockSts()) ? true : false);
				if (pwdCfg.getMaxErrorTimes() != null
						&& lockInfo.getErrorTimes() != null) {
					returnInfo.setRestErrorTimes(pwdCfg.getMaxErrorTimes()
							.intValue() - lockInfo.getErrorTimes().intValue());
				} else {
					returnInfo.setRestErrorTimes(-1);
				}
			}
		}
		return returnInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yusys.bione.frame.security.lock.IUserLockValidator#unlockByUser
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void resetUserByNo(String userNo, String logicSysNo,
			boolean isSuperUser) {
		if (!StringUtils.isEmpty(userNo) && !StringUtils.isEmpty(logicSysNo)) {
			// 判断当前逻辑系统认证类型，是否需要做本地密码安全校验
			String authNo = lockManageBS.getSystemAuthNo(logicSysNo);
			if (acceptAuthNos.contains(authNo)) {
				// 只处理本地安全策略管理范畴内的逻辑系统
				if (GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(authNo)
						|| isSuperUser) {
					// 若是使用的超级逻辑系统认证，或者当前用户是逻辑系统管理员
					// 逻辑系统统一为超级逻辑系统
					logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
				}
				// 获取用户id
				String userId = lockManageBS.getUserIdByNo(userNo, logicSysNo);
				// 目前解锁动作是直接删除对应的锁定信息，所以相当于同时重置了密码错误次数
				lockManageBS.unlockByUser(userId, logicSysNo);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yusys.bione.frame.security.lock.IUserLockValidator#unlockByUser
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void resetUserById(String userId, String logicSysNo,
			boolean isSuperUser) {
		if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(logicSysNo)) {
			// 判断当前逻辑系统认证类型，是否需要做本地密码安全校验
			String authNo = lockManageBS.getSystemAuthNo(logicSysNo);
			if (acceptAuthNos.contains(authNo)) {
				// 只处理本地安全策略管理范畴内的逻辑系统
				if (GlobalConstants4frame.AUTH_TYPE_LOCAL_SUPER.equals(authNo)
						|| isSuperUser) {
					// 若是使用的超级逻辑系统认证，或者当前用户是逻辑系统管理员
					// 逻辑系统统一为超级逻辑系统
					logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
				}
				// 目前解锁动作是直接删除对应的锁定信息，所以相当于同时重置了密码错误次数
				lockManageBS.unlockByUser(userId, logicSysNo);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yusys.bione.frame.security.lock.IUserLockValidator#clearAll
	 * ()
	 */
	@Override
	public void clearAll() {
		// 清除全部用户锁定信息
		lockManageBS.clearLockInfos(null);
	}

	/* ##################### Private Methods ######################## */

	/**
	 * 获取当前时间与指定时间之间的小时间隔
	 * 
	 * @param beforeTime
	 * @return 间隔小时
	 */
	private Integer getHoursDiff(Timestamp beforeTime, Timestamp currTime) {
		Integer diffHours = -1;
		if (beforeTime != null && currTime != null) {
			diffHours = Integer.valueOf(Long.valueOf(
					((currTime.getTime() - beforeTime.getTime()) / 1000)
							/ (60 * 60)).toString());
		}
		return diffHours;
	}

	/**
	 * 判断某年是否是闰年
	 * 
	 * @param year
	 * @return true/false
	 */
	@SuppressWarnings("unused")
	private boolean isLeapYear(Integer year) {
		boolean flag = false;
		// 1、能被4整除
		if (year != null && year % 4 == 0) {
			flag = true;
			// 2、若能被100整除，需被400整除
			if (year % 100 == 0 && year % 400 != 0) {
				flag = false;
			}
		}
		return flag;
	}
}
