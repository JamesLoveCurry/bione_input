package com.yusys.bione.frame.syslog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.syslog.entity.BioneLogLogin;
import com.yusys.bione.frame.syslog.repository.BioneLogLoginMybatisDao;
import com.yusys.bione.frame.syslog.web.vo.BioneLogLoginVO;
import com.yusys.bione.frame.syslog.web.vo.BioneLogUserVO;
import com.yusys.bione.frame.util.SplitStringBy1000;

@Service
@Transactional(readOnly = true)
public class BioneLogLoginBS extends BaseBS<BioneLogLogin> {
	@Autowired
	private BioneLogLoginMybatisDao loginDao;

	public Map<String, Object> list(Pager pager, String userName, String orgName, String startDate, String endDate) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(userName)){
			condition.put("userName", "%" + userName + "%");
		}
		if(!StringUtils.isEmpty(orgName)){
			condition.put("orgName", "%" + orgName + "%");
		}
		if(!StringUtils.isEmpty(startDate)){
			condition.put("startDate", startDate);
		}
		if(!StringUtils.isEmpty(endDate)){
			condition.put("endDate", endDate);
		}
		condition.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			List<String> curOrgNo = Lists.newArrayList();
			List<String> orgNos = Lists.newArrayList();
			// 获取用户授权机构
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			curOrgNo.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			orgNos.addAll(authOrgNos);
			// 获取辖内机构
			this.getAllChildBioneOrgNo(curOrgNo, orgNos);
			condition.put("orgNos", SplitStringBy1000.change(orgNos));
		}else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "N".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			return map;
		}
		PageHelper.startPage(pager);
		PageMyBatis<BioneLogLoginVO> vos = (PageMyBatis<BioneLogLoginVO>) loginDao.list(condition);
		map.put("Rows", vos.getResult());
		map.put("Total", vos.getTotalCount());
		return map;
	}
	
	private void getAllChildBioneOrgNo(List<String> orgs,List<String> allowOrgs) {
		if (orgs != null && orgs.size() > 0) {
			List<List<String>> orgLists = SplitStringBy1000.change(orgs);
			List<String> upOrgs = new ArrayList<String>();
			for(List<String> orgList : orgLists){
				String jql = "select org from BioneOrgInfo org where org.upNo in ?0 and org.logicSysNo = ?1";
				List<BioneOrgInfo> neworgs = this.baseDAO.findWithIndexParam(jql,
						orgList, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				if (neworgs != null && neworgs.size() > 0) {
					for (BioneOrgInfo org : neworgs) {
						if(!allowOrgs.contains(org.getOrgNo())){
							allowOrgs.add(org.getOrgNo());
							upOrgs.add(org.getOrgNo());
						}
					}
				}
			}
			if (upOrgs.size() > 0) {
				getAllChildBioneOrgNo(upOrgs, allowOrgs);
			}
		}
	}

	public List<BioneLogLoginVO> export(String userName, String orgName, String startDate, String endDate) {
		List<BioneLogLoginVO> vos = new ArrayList<BioneLogLoginVO>();
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(userName)){
			condition.put("userName", "%" + userName + "%");
		}
		if(!StringUtils.isEmpty(orgName)){
			condition.put("orgName", "%" + orgName + "%");
		}
		if(!StringUtils.isEmpty(startDate)){
			condition.put("startDate", startDate);
		}
		if(!StringUtils.isEmpty(endDate)){
			condition.put("endDate", endDate);
		}
		condition.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			List<String> curOrgNo = Lists.newArrayList();
			List<String> orgNos = Lists.newArrayList();
			// 获取用户授权机构
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			curOrgNo.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			orgNos.addAll(authOrgNos);
			// 获取辖内机构
			this.getAllChildBioneOrgNo(curOrgNo, orgNos);
			condition.put("orgNos", SplitStringBy1000.change(orgNos));
		}else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "N".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			return vos;
		}
		vos = loginDao.list(condition);
		if(vos!=null&&vos.size()>0){
			for(BioneLogLoginVO vo:vos){
				vo.setLoinDays(calTime(vo.getLoginTime()));
			}
		}
		return vos;
	}

	private String calTime(long time){
		StringBuilder str=new StringBuilder("");
		time = time / 1000;
		long second=time%60;
		time=time/60;
		long minute=time%60;
		time=time/60;
		long hour=time%24;
		long day=time/24;
		str.append(day).append("天").append(hour).append("时").append(minute).append("分").append(second).append("秒");
		return str.toString();
	}
	@Transactional(readOnly = false)
	public void save(BioneLogLogin login) {
		loginDao.save(login);
	}

	@Transactional(readOnly = false)
	public void update(BioneLogLogin login) {
		loginDao.update(login);
	}

	public Map<String, Object> userDetail(Pager pager, String userId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			List<String> curOrgNo = Lists.newArrayList();
			List<String> orgNos = Lists.newArrayList();
			// 获取用户授权机构
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			curOrgNo.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			orgNos.addAll(authOrgNos);
			// 获取辖内机构
			this.getAllChildBioneOrgNo(curOrgNo, orgNos);
			condition.put("orgNos", SplitStringBy1000.change(orgNos));
		}else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "N".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			return result;
		}
		PageHelper.startPage(pager);
		PageMyBatis<BioneLogUserVO> page = (PageMyBatis<BioneLogUserVO>) this.loginDao.userDetail(condition);
		result.put("Total", page.getTotalCount());
		result.put("Rows", page.getResult());
		return result;
	}

	@Transactional(readOnly = false)
	public void getLogLogin(String sessionId) {
		String jql = "update BioneLogLogin log set logoutTime =?0 where log.sessionId =?1 and log.loginTime = log.logoutTime";
		this.baseDAO.batchExecuteWithIndexParam(jql, System.currentTimeMillis(), sessionId);
		
	}
}
