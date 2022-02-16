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
import com.yusys.bione.plugin.access.repository.RptAccessReportDao;
import com.yusys.bione.plugin.access.web.vo.DeptUserAccessVO;
import com.yusys.bione.plugin.access.web.vo.RptUserAccessVO;
/**
 * 
 * <pre>
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
public class RptAccessReportBS {
	@Autowired
	private RptAccessReportDao rptAccessReportDao;

	//统计
	public Map<String, Object> getInfo(Pager page , String rptNm, String startAccess,String endAccess ) throws ParseException {
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(rptNm)){
			condition.put("rptNm", "%" + rptNm + "%");
		}
		if(!StringUtils.isEmpty(startAccess)){
			condition.put("startAccess", startAccess);
		}
		if(!StringUtils.isEmpty(endAccess)){
			condition.put("endAccess", endAccess);
		}
		
		PageHelper.startPage(page);
		PageMyBatis<DeptUserAccessVO> deptUsers = (PageMyBatis<DeptUserAccessVO>) this.rptAccessReportDao.search(condition);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Total", deptUsers.getTotalCount());
		result.put("Rows", deptUsers.getResult());
		return result;
	}

	//详情
	public Map<String, Object> findInfo(Pager pager,String rptId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rptId", rptId);
		PageHelper.startPage(pager);
		PageMyBatis<RptUserAccessVO> vo = this.rptAccessReportDao.findInfo(map);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("Rows", vo.getResult());
		params.put("Total", vo.getTotalCount());
		return params;
	}

	//导出Execl
	public List<DeptUserAccessVO> export(String rptNm, String startAccess,
			String endAccess) {
		Map<String, Object> condition = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(rptNm)){
			condition.put("rptNm", "%" + rptNm + "%");
		}
		if(!StringUtils.isEmpty(startAccess)){
			condition.put("startAccess", startAccess);
		}
		if(!StringUtils.isEmpty(endAccess)){
			condition.put("endAccess", endAccess);
		}
		List<DeptUserAccessVO> deptUsers = (List<DeptUserAccessVO>) this.rptAccessReportDao.search(condition);
		return deptUsers;
	}

}
