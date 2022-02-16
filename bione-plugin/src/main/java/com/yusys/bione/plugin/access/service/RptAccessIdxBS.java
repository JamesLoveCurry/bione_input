package com.yusys.bione.plugin.access.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.plugin.access.repository.RptAccessIdxDao;
import com.yusys.bione.plugin.access.web.vo.IdxUserAccessVO;
import com.yusys.bione.plugin.access.web.vo.IdxUserInfoVO;
/**
 * 
 * <pre>
 * Title:指标访问统计事务
 * Description:
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptAccessIdxBS {
	@Autowired
	private RptAccessIdxDao rptAccessIdxDao;

	public Map<String, Object> getInfo(Pager page,String indexNm, String startAccess,String endAccess ) throws ParseException {
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(indexNm)){
			condition.put("indexNm", "%" + indexNm + "%");
		}
		if(!StringUtils.isEmpty(startAccess)){
			condition.put("startAccess", startAccess);
		}
		if(!StringUtils.isEmpty(endAccess)){
			condition.put("endAccess", endAccess);
		}
		
		PageHelper.startPage(page);
		PageMyBatis<IdxUserAccessVO> deptUsers = (PageMyBatis<IdxUserAccessVO>) this.rptAccessIdxDao.search(condition);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Total", deptUsers.getTotalCount());
		result.put("Rows", deptUsers.getResult());
		return result;
	}
	
	public Map<String, Object> findInfo(Pager pager,String indexNo,String indexVerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("indexNo", indexNo);
		map.put("indexVerId", indexVerId);
		PageHelper.startPage(pager);
		PageMyBatis<IdxUserInfoVO> vo = (PageMyBatis<IdxUserInfoVO>)this.rptAccessIdxDao.findInfo(map);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", vo.getResult());
		params.put("Total", vo.getTotalCount());
		return params;
		
	}

	//导出Execl
	public List<IdxUserAccessVO> export(String indexNm, String startAccess,
			String endAccess) {
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(indexNm)){
			condition.put("indexNm", "%" + indexNm + "%");
		}
		if(!StringUtils.isEmpty(startAccess)){
			condition.put("startAccess", startAccess);
		}
		if(!StringUtils.isEmpty(endAccess)){
			condition.put("endAccess", endAccess);
		}
		List<IdxUserAccessVO> list = (List<IdxUserAccessVO>) this.rptAccessIdxDao.search(condition);
		return list;
	}

	/**
	 * 指标访问统计：按照用户统计
	 * @param page
	 * @param userNo
	 * @param userName
	 * @param orgName
	 * @param startAccess
	 * @param endAccess
	 * @return
	 */
	public Map<String, Object> getUserGetInfo(Pager page, String userNo,
			String userName, String orgName, String startAccess,
			String endAccess) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(!StringUtils.isEmpty(userNo)){
			map.put("userNo", "%" + userNo + "%");
		}
		if(!StringUtils.isEmpty(userName)){
			map.put("userName", "%" + userName + "%");
		}
		if(!StringUtils.isEmpty(orgName)){
			map.put("orgName", "%" + orgName + "%");
		}
		if(!StringUtils.isEmpty(startAccess)){
			map.put("startAccess", startAccess);
		}
		if(!StringUtils.isEmpty(endAccess)){
			map.put("endAccess", endAccess);
		}
		
		PageHelper.startPage(page);
		PageMyBatis<IdxUserAccessVO> users = (PageMyBatis<IdxUserAccessVO>)this.rptAccessIdxDao.userSearch(map);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", users);
		params.put("Total", users.getTotalCount());
		return params;
	}


	public Map<String, Object> findUserDetail(Pager pager, String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		PageHelper.startPage(pager);
		PageMyBatis<IdxUserInfoVO> vo = (PageMyBatis<IdxUserInfoVO>)this.rptAccessIdxDao.findUserDetail(map);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", vo);
		params.put("Total", vo.getTotalCount());
		return params;
	}

	public List<IdxUserAccessVO> userExport(String userNo, String userName,
			String orgName, String startAccess, String endAccess) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(!StringUtils.isEmpty(userNo)){
			map.put("userNo", "%" + userNo + "%");
		}
		if(!StringUtils.isEmpty(userName)){
			map.put("userName", "%" + userName + "%");
		}
		if(!StringUtils.isEmpty(orgName)){
			map.put("orgName", "%" + orgName + "%");
		}
		if(!StringUtils.isEmpty(startAccess)){
			map.put("startAccess", startAccess);
		}
		if(!StringUtils.isEmpty(endAccess)){
			map.put("endAccess", endAccess);
		}
		
		List<IdxUserAccessVO> users = this.rptAccessIdxDao.userSearch(map);
		return users;
	}
}
