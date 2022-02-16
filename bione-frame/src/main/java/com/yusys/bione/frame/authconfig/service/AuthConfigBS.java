package com.yusys.bione.frame.authconfig.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfo;

@Service
@Transactional(readOnly = true)
public class AuthConfigBS extends BaseBS<Object> {

	/**
	 * 
	 * @param ipAddress
	 * @param port
	 * @return
	 */
	public Boolean test(String ipAddress, String port) {
		return false;
	}

	/**
	 * 获取认证类型列表
	 * 
	 * @param pageFirstIndex
	 *            起始页
	 * @param pagesize
	 *            每页条数
	 * @param sortname
	 *            排序字段
	 * @param sortorder
	 *            排序方式
	 * @param searchCondition
	 *            查询条件
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneAuthInfo> findResults(int pageFirstIndex, int pagesize, String sortname, String sortorder,
			Map<String, Object> searchCondition) {
		StringBuilder jql = new StringBuilder();
		jql.append("select obj from BioneAuthInfo obj where 1=1");
		if (!searchCondition.get("jql").equals("")) {
			jql.append(" and " + searchCondition.get("jql"));
		}
		if (!StringUtils.isEmpty(sortname)) {
			jql.append(" order by obj." + sortname + " " + sortorder);
		}
		Map<String, ?> values = (Map<String, ?>) searchCondition.get("params");

		return this.baseDAO.findPageWithNameParam(pageFirstIndex, pagesize, jql.toString(), values);
	}

}
