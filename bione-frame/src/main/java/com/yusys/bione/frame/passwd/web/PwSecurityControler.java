package com.yusys.bione.frame.passwd.web;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.passwd.entity.BionePwdSecurityCfg;
import com.yusys.bione.frame.passwd.service.PwdSecurityCfgBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.lock.IUserLockValidator;
import com.yusys.bione.frame.security.lock.service.UserLockManageBS;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

@Controller
@RequestMapping("/bione/admin/pwsec")
public class PwSecurityControler {

	@Autowired
	private PwdSecurityCfgBS pscBS;

	@Autowired
	private UserLockManageBS lockManageBS;

	@Autowired
	private UserBS userBS;

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/passwd/passwd-security-cfg", "id", id);
	}

	@RequestMapping(value = "/{id}/load", method = RequestMethod.GET)
	@ResponseBody
	public BionePwdSecurityCfg getPsc(@PathVariable("id") String id) {
		BionePwdSecurityCfg model = pscBS.getEntityById(id);
		return model;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void create(BionePwdSecurityCfg model) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		model.setLastUpdateTime(now);
		model.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		if (model.getId() == null) {
			model.setId("1");
		}
		this.pscBS.updateEntity(model);
		// 更新配置会清除所有锁定记录
		IUserLockValidator lockValidator = SpringContextHolder.getBean(GlobalConstants4frame.USER_LOCK_VALIDATOR_NAME);
		if(lockValidator != null){
			lockValidator.clearAll();
		}
	}

	@RequestMapping(value = "/getOverdue.json", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getOverdue() {
		Map<String, Object> result = Maps.newHashMap();
		Boolean state = Boolean.TRUE;
		//
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		BioneUserInfo user = userBS.getEntityById(BioneUserInfo.class , bioneUser.getUserId());
		BionePwdSecurityCfg pwdSequrity = lockManageBS.getPwdSecurityCfg();
		if (pwdSequrity == null
				|| !GlobalConstants4frame.COMMON_YES.equals(pwdSequrity
						.getEnableSts())) {
			result.put("data", GlobalConstants4frame.COMMON_NO);
		} else {
			try {
				Timestamp now = new Timestamp(System.currentTimeMillis());
				Timestamp lastPwdUpdateTime = user.getLastPwdUpdateTime();
				/*
				 * @Revision 20130423150800-liuch
				 * 当lastPwdUpdateTime与lastUpdateTime同时为空时，
				 * 把这两个字段设置为当前时间，并更新到数据库。
				 */
				if (lastPwdUpdateTime == null) {
					if (user.getLastUpdateTime() != null) {
						lastPwdUpdateTime = user.getLastUpdateTime();
					} else {
						lastPwdUpdateTime = now; // 设置为当前时间
						user.setLastUpdateTime(now);
					}
					user.setLastPwdUpdateTime(lastPwdUpdateTime);
					userBS.updateEntity(user);
				}
				long differDay = (now.getTime() / 1000 - lastPwdUpdateTime
						.getTime() / 1000) / (24 * 60 * 60); // 相差天数
				long stdUseTime = pwdSequrity.getValidTime().longValue()
						* GlobalConstants4frame.COMMON_MONTH_DAYS; // 按30天计算
				if ((differDay - stdUseTime) >= 0) {
					result.put("data", GlobalConstants4frame.COMMON_YES);
				} else {
					result.put("data", GlobalConstants4frame.COMMON_NO);
				}
			} catch (Exception e) {
				state = Boolean.FALSE;
				result.put("msg", "获取用户密码已使用过的时间失败");
			}
		}
		result.put("success", state + "");
		return result;
	}
	
	@RequestMapping(value = "/getPwdComplex.json", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getPwdComplex() {
		Map<String, Object> result = Maps.newHashMap();
		Boolean state = Boolean.TRUE;
		try {
			BionePwdSecurityCfg pwdSecurity = (BionePwdSecurityCfg) lockManageBS.getPwdSecurityCfg();
			result.put("pwdSecurity", pwdSecurity);
		} 
		catch (Exception e) {
			state = Boolean.FALSE;
			result.put("msg", "获取系统安全策略失败");
		}
		result.put("success", state + "");
		return result;
	}
	
}
