package com.yusys.bione.plugin.access.service;

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
import com.yusys.bione.plugin.access.repository.RptAccessUserDao;
import com.yusys.bione.plugin.access.web.vo.RptByUserAccessVO;
import com.yusys.bione.plugin.access.web.vo.RptUserAccessVO;

/**
 * 
 * <pre>
 * Description:
 * </pre>
 * 
 * @author sunyuming sunym@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptAccessUserBS {
	@Autowired
	private RptAccessUserDao rptAccessUserDao;
	
	public Map<String, Object> getInfo(Pager page,String userNo,String userName,String orgName, String startAccess,String endAccess){
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
		PageMyBatis<RptByUserAccessVO> users = this.rptAccessUserDao.search(map);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", users);
		params.put("Total", users.getTotalCount());
		return params;
	}
	
	public List<RptByUserAccessVO> export(String userNo, String userName,String orgName, String startAccess, String endAccess ) {
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
		
		List<RptByUserAccessVO> users = this.rptAccessUserDao.search(map);
		
		return users;
	}

	public Map<String, Object> findInfo(Pager pager,String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		PageHelper.startPage(pager);
		PageMyBatis<RptUserAccessVO> vo = this.rptAccessUserDao.findInfo(map);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", vo);
		params.put("Total", vo.getTotalCount());
		return params;
	}


}
