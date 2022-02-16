package com.yusys.biapp.input.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.data.entity.RptInputLstDataState;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title: 程序名称
 * Description: 功能描述
 * </pre>
 * @author xuguangyuan  xugy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class DataStateBS extends BaseBS<RptInputLstDataState> {

	/**
	 * 修改数据状态
	 * @param propName
	 * @param propVal
	 * @param taskStateSave
	 */
	@Transactional(readOnly=false)
	public void changerDataState(String templeId, String caseId, List<String> orgNo, String logicSysNo, String dataType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dataSts", dataType);
		paramMap.put("operUser", BioneSecurityUtils.getCurrentUserInfo().getLoginName());
		paramMap.put("operTime", DateUtils.getYYYY_MM_DD_HH_mm_ss());
		paramMap.put("caseId", caseId);
		paramMap.put("templeId", templeId);
		paramMap.put("orgNo", orgNo);
		String jql1 = "update RptInputLstDataState t set t.dataSts=:dataSts,t.operUser=:operUser ,t.operTime=:operTime where caseId=:caseId and templeId=:templeId and orgNo in :orgNo";
		this.baseDAO.batchExecuteWithNameParam(jql1, paramMap);
	}
}
