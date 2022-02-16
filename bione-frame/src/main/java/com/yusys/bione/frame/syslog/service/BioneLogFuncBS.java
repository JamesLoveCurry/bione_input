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
import com.yusys.bione.frame.syslog.entity.BioneLogFunc;
import com.yusys.bione.frame.syslog.repository.BioneLogFuncMybatisDao;
import com.yusys.bione.frame.syslog.web.vo.BioneLogFuncVO;
import com.yusys.bione.frame.syslog.web.vo.BioneLogMenuVO;
import com.yusys.bione.frame.util.SplitStringBy1000;

@Service
@Transactional(readOnly = true)
public class BioneLogFuncBS extends BaseBS<Object>{

	@Autowired
	private BioneLogFuncMybatisDao logFuncMybatisDao;

	@Transactional(readOnly = false)
	public void saveLogFunc(BioneLogFunc  bioneLogFunc) {
		logFuncMybatisDao.saveLogFunc(bioneLogFunc);
	}

	public Map<String, Object> list(Pager pager, String funcName, String startDate, String endDate) {
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(funcName)){
			condition.put("funcName", "%" + funcName + "%");
		}
		if(!StringUtils.isEmpty(startDate)){
			condition.put("startDate", startDate);
		}
		if(!StringUtils.isEmpty(endDate)){
			condition.put("endDate", endDate);
		}
		condition.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		Map<String, Object> result = new HashMap<String, Object>();
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
		PageMyBatis<BioneLogFuncVO> page = (PageMyBatis<BioneLogFuncVO>) this.logFuncMybatisDao
				.list(condition);
		result.put("Total", page.getTotalCount());
		result.put("Rows", page.getResult());
		return result;
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
	
	public Map<String, Object> menuList(Pager pager, String menuId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("menuId", menuId);
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
		PageMyBatis<BioneLogMenuVO> page = (PageMyBatis<BioneLogMenuVO>) this.logFuncMybatisDao
				.menuList(condition);
		result.put("Total", page.getTotalCount());
		result.put("Rows", page.getResult());
		return result;
	}
	
	
	public BioneLogFuncMybatisDao getLogFuncMybatisDao() {
		return logFuncMybatisDao;
	}

	public void setLogFuncMybatisDao(BioneLogFuncMybatisDao logFuncMybatisDao) {
		this.logFuncMybatisDao = logFuncMybatisDao;
	}
	
	public List<BioneLogFuncVO> export(String funcName, String startDate,
			String endDate) {
		List<BioneLogFuncVO> vos = new ArrayList<BioneLogFuncVO>();
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(funcName)){
			condition.put("funcName", "%" + funcName + "%");
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
		vos= logFuncMybatisDao.list(condition);
		return vos;
	}

	

}