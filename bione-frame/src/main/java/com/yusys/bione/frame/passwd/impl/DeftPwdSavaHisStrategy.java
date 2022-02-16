package com.yusys.bione.frame.passwd.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.passwd.IPwdSavaHisStrategy;
import com.yusys.bione.frame.passwd.entity.BionePwdHis;
import com.yusys.bione.frame.security.lock.service.UserLockManageBS;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

/**
 * <pre>
 * Title: 保存历史密码的默认策略
 * Description: 保存历史密码的默认策略
 * </pre>
 * 
 * @author liucheng liucheng2@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：		  修改日期:     修改内容:
 * </pre>
 */
@Component
public class DeftPwdSavaHisStrategy implements IPwdSavaHisStrategy {

	@Autowired
	private UserBS userBS;

	@Autowired
	private UserLockManageBS lockManageBS;

	public String saveHis(String userId,String pwdCrypt) {

		BioneUserInfo userInfo = this.userBS.getEntityById(BioneUserInfo.class , userId);

		Timestamp now = new Timestamp(System.currentTimeMillis());

		String pwd_old = userInfo.getUserPwd();
		BionePwdHis pwdHis = new BionePwdHis();
		pwdHis.setId(RandomUtils.uuid2());
		pwdHis.setOldPwd(pwd_old);
		pwdHis.setNewPwd(pwdCrypt);
		pwdHis.setUserId(userId);
		pwdHis.setUpdateTime(now);
		pwdHis.setLogicSysNo(userInfo.getLogicSysNo());
		lockManageBS.saveOrUpdateEntity(pwdHis);

		return STATUS_NORMAL;
	}

	public boolean isPwdValid(String userId, String pwdCrypt,String diffRecentHis) {
		boolean flag = true;
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(pwdCrypt)
				|| StringUtils.isEmpty(diffRecentHis)) {
			// 数据不合法，无法进行校验
			return flag;
		}
		Integer diffRecentHisInt;
		try {
			diffRecentHisInt = Integer.valueOf(diffRecentHis);
		} catch (Exception e) {
			e.printStackTrace();
			// 数据不合法
			return flag;
		}
		BioneUserInfo userInfo = this.userBS.getEntityById(BioneUserInfo.class , userId);
		// 获取历史密码信息
		List<BionePwdHis> hiss = this.lockManageBS.getHisByUser(userId, userInfo.getLogicSysNo());
		if (hiss != null && hiss.size() > 0) {
			for (int i = 0; i < (diffRecentHisInt > hiss.size() ? hiss.size()
					: diffRecentHisInt); i++) {
				String oldPwd = hiss.get(i).getNewPwd();
				if (pwdCrypt.equals(oldPwd)) {
					// 与历史密码相同
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

}
