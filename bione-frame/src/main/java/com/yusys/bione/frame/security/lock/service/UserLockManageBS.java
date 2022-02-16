/**
 * 
 */
package com.yusys.bione.frame.security.lock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.passwd.entity.BionePwdHis;
import com.yusys.bione.frame.passwd.entity.BionePwdSecurityCfg;
import com.yusys.bione.frame.passwd.entity.BioneUserLockInfo;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

/**
 * <pre>
 * Title:用户锁定相关处理BS
 * Description: 用户锁定相关处理BS
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
@Service
@Transactional(readOnly = true)
public class UserLockManageBS extends BaseBS<Object> {

	/**
	 * 通过逻辑系统标识和用户标识，获取用户锁定信息
	 * 
	 * @param logicSysNo
	 * @param userId
	 * @return
	 */
	public BioneUserLockInfo getLockInfoByUserId(String userId,
			String logicSysNo) {
		BioneUserLockInfo lockInfo = null;
		if (!StringUtils.isEmpty(logicSysNo) && !StringUtils.isEmpty(userId)) {
			String jql = "select lock from BioneUserLockInfo lock where lock.id.logicSysNo = ?0 and lock.id.userId = ?1";
			List<BioneUserLockInfo> lockInfos = this.baseDAO
					.findWithIndexParam(jql, logicSysNo, userId);
			if (lockInfos != null && lockInfos.size() > 0) {
				lockInfo = lockInfos.get(0);
			}
		}
		return lockInfo;
	}

	/**
	 * 通过账户标识找寻用户id
	 * 
	 * @param userNo
	 * @param logicSysNo
	 * @return
	 */
	public String getUserIdByNo(String userNo, String logicSysNo) {
		String userId = null;
		if (!StringUtils.isEmpty(userNo) && !StringUtils.isEmpty(logicSysNo)) {
			String jql = "select user from BioneUserInfo user where user.userNo = ?0 and user.logicSysNo = ?1 and user.userSts = ?2";
			List<BioneUserInfo> users = this.baseDAO.findWithIndexParam(jql,
					userNo, logicSysNo, GlobalConstants4frame.COMMON_STATUS_VALID);
			if (users != null && users.size() > 0) {
				userId = users.get(0).getUserId();
			}
		}
		return userId;
	}

	/**
	 * 获取本地密码安全策略
	 * 
	 * @return
	 */
	public BionePwdSecurityCfg getPwdSecurityCfg() {
		List<BionePwdSecurityCfg> cfgs = this
				.getEntityList(BionePwdSecurityCfg.class);
		if (cfgs != null && cfgs.size() > 0) {
			return cfgs.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 获取指定逻辑系统的认证方式
	 * 
	 * @return
	 */
	public String getSystemAuthNo(String logicSysNo) {
		String authNo = null;
		if (!StringUtils.isEmpty(logicSysNo)) {
			String jql = "select sys from BioneLogicSysInfo sys where sys.logicSysNo = ?0";
			List<BioneLogicSysInfo> syss = this.baseDAO.findWithIndexParam(jql,
					logicSysNo);
			if (syss != null && syss.size() > 0) {
				authNo = syss.get(0).getAuthTypeNo();
			}
		}
		return authNo;
	}

	/**
	 * 解锁用户
	 * 
	 * @param userId
	 * @param logicSysNo
	 */
	@Transactional(readOnly = false)
	public void unlockByUser(String userId, String logicSysNo) {
		if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(logicSysNo)) {
			// 直接删除用户锁定信息表对应记录
			String jql = "delete from BioneUserLockInfo lock where lock.id.logicSysNo = ?0 and lock.id.userId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, logicSysNo, userId);
		}
	}

	/**
	 * 解锁用户 可以解锁某应用系统下全部用户，也可以用来解锁全部应用系统用户
	 * 
	 * @param logicSysNo
	 */
	@Transactional(readOnly = false)
	public void clearLockInfos(String logicSysNo) {
		// 直接删除用户锁定信息表对应记录
		StringBuilder jql = new StringBuilder(
				"delete from BioneUserLockInfo lock where 1 = 1 ");
		Map<String, String> params = new HashMap<String, String>();
		if (!StringUtils.isEmpty(logicSysNo)) {
			jql.append(" and lock.id.logicSysNo = :logicSysNo");
			params.put("logicSysNo", logicSysNo);
		}
		this.baseDAO.batchExecuteWithNameParam(jql.toString(), params);
	}

	/**
	 * 获取指定用户的历史密码信息
	 * 
	 * @return
	 */
	public List<BionePwdHis> getHisByUser(String userId, String logicSysNo) {
		List<BionePwdHis> hiss = new ArrayList<BionePwdHis>();
		if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(logicSysNo)) {
			String jql = "select his from BionePwdHis his where his.userId = ?0 and his.logicSysNo = ?1 and his.updateTime is not null order by his.updateTime desc";
			hiss = this.baseDAO.findWithIndexParam(jql, userId, logicSysNo);
			if (hiss == null) {
				hiss = new ArrayList<BionePwdHis>();
			}
		}
		return hiss;
	}

}
