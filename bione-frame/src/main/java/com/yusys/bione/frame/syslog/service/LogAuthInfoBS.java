package com.yusys.bione.frame.syslog.service;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.IAuthObject;
import com.yusys.bione.frame.syslog.entity.BioneLogAuthDetail;
import com.yusys.bione.frame.syslog.entity.BioneLogAuthInfo;
import com.yusys.bione.frame.syslog.web.vo.LogAuthInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class LogAuthInfoBS extends BaseBS<BioneLogAuthInfo> {

	@Autowired
	private LogAuthDetailBS detailBS;

	@Transactional(readOnly = false)
	public void saveLogs(BioneLogAuthInfo logInfo,
			List<BioneLogAuthDetail> logDetailList) {
		this.saveEntity(logInfo);
		if (logDetailList != null && logDetailList.size() > 0) {
			this.detailBS.saveEntityJdbcBatch(logDetailList);
//			for (BioneLogAuthDetail detail : logDetailList) {
//				this.detailBS.saveEntity(detail);
//			}
		}
	}

	public Map<String, Object> list(int pageFirstIndex, int pagesize,
			Map<String, Object> searchCondition, String sortname,
			String sortorder, String userName, String startDate, String endDate) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select info,user.userNo,user.userName,def.objDefName,def.beanName from BioneLogAuthInfo info, BioneUserInfo user,BioneAuthObjDef def " +
				"where info.operUser = user.userId and info.authObjNo = def.objDefNo and info.logicSysNo = :logicSysNo");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) searchCondition
				.get("params");
		
		if (!searchCondition.get("jql").equals("")) {
			jql.append(" and " + searchCondition.get("jql"));
		}
		if(!StringUtils.isEmpty(userName)){
			jql.append(" and user.userName like :userName");
			values.put("userName", "%" + userName + "%");
		}
		if(!StringUtils.isEmpty(startDate)){
			jql.append(" and info.operTime >= :startDate");
			values.put("startDate", Timestamp.valueOf(startDate + " 00:00:00"));
		}
		if(!StringUtils.isEmpty(endDate)){
			jql.append(" and info.operTime <= :endDate");
			values.put("endDate", Timestamp.valueOf(endDate + " 23:59:59"));
		}
		if(!StringUtils.isEmpty(sortname)){
			jql.append(" order by " + sortname + " " + sortorder);
		}
		
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		SearchResult<Object[]> result = this.baseDAO
				.findPageWithNameParam(pageFirstIndex, pagesize,
						jql.toString(), values);
		Map<String, Object> map = new HashMap<String, Object>();
		List<LogAuthInfoVO> list = new ArrayList<LogAuthInfoVO>();
		if(result.getResult() != null){
			for(Object[] object : result.getResult()){
				BioneLogAuthInfo auth = (BioneLogAuthInfo)object[0];
				IAuthObject authObj = SpringContextHolder.getBean(object[4].toString());
				String name = authObj.getAuthObjNameById(auth.getAuthObjId());
				LogAuthInfoVO vo = new LogAuthInfoVO(auth, object[1].toString(), object[2].toString(), object[3].toString(), name);
				list.add(vo);
			}
		}
		map.put("Rows", list);
		map.put("Total", result.getTotalCount());
		return map;
		
	}

	public List<BioneLogAuthDetail> detailList(String logId) {
		String jql = "select detail from BioneLogAuthDetail detail where detail.id.logId = ?0";
		List<BioneLogAuthDetail> list = this.baseDAO.findWithIndexParam(jql, logId);
		return list;
	}

}