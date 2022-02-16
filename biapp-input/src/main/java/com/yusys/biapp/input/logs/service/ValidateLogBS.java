package com.yusys.biapp.input.logs.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
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
public class ValidateLogBS extends BaseBS<RptInputLstValidateLog> {
	
	@Autowired
	private TempleBS templeBS;
	
	public SearchResult<RptInputLstValidateLog> getSearch(int firstResult, int pageSize, String orderBy, String orderType, Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select s from RptInputLstValidateLog s where 1=1 ");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by s." + orderBy + " " + orderType);
		}
		@SuppressWarnings("unchecked")
		Map<String, String> values = (Map<String, String>) conditionMap.get("params");
		return this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
	}
	
	public SearchResult<RptInputLstValidateLog> getValidateLog(int firstResult, int pageSize,String templeId,String caseId) {
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templeId); //改动之前的原始代码
//		RptInputLstTempleInfo temp = this.templeBS.getAllEntityList(RptInputLstTempleInfo.class, orderByProperty, isDesc)
		
		// temp 是null  要向temp中添加
		/*for(){
			
		}*/
//		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) this.templeBS.getAllEntityList();// 改动的代码
//		RptInputLstTempleInfo temp = this.getSearch(firstResult, pageSize, orderBy, orderType, conditionMap);
		String jql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("templeId", templeId);
		paramMap.put("caseId", caseId);
		if(StringUtils.isNotBlank(temp.getOrgColumn())){
			paramMap.put("orgNo", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			jql = "select s from RptInputLstValidateLog s where templeId=:templeId and caseId=:caseId and orgNo=:orgNo order by rowNum asc";
		} else {
			jql = "select s from RptInputLstValidateLog s where templeId=:templeId and caseId=:caseId order by rowNum asc";
		}
		//List<RptInputLstValidateLog>logList =  validateLogDao.getRptInputLstValidateLog(paramMap);
		SearchResult<RptInputLstValidateLog> authObjDefList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), paramMap);
		//SearchResult<RptInputLstValidateLog> authObjDefList = new SearchResult<RptInputLstValidateLog>();
		//authObjDefList.setResult(logList) ;
		//authObjDefList.setTotalCount(logList.size());
		return authObjDefList;
	}
	public List<Object[]>  getValidateLog(String templeId,String caseId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("templeId", templeId);
		paramMap.put("caseId", caseId);
		String jql = "select validate_result as result,id from RPT_INPUT_LST_VALIDATE_LOG where temple_id='"+templeId+"' and case_id='"+caseId+"'";
		List<Object[]> logList = this.baseDAO.findByNativeSQLWithNameParam(jql.toString(), null);
		return logList;
	}
	public List<RptInputLstValidateLog> getLogsByFileId(String dataFileId) {
		return this.baseDAO.findByProperty(RptInputLstValidateLog.class, "dataFileId", dataFileId);
	}

	public List<RptInputLstValidateLog> getLogsByOrgCodeId(String caseId, String templeId, String orgCode) {
		if (StringUtils.isNotBlank(orgCode)) {
			return this.baseDAO.findByPropertys(RptInputLstValidateLog.class, new String[] { "caseId", "templeId", "orgNo" }, new Object[] { caseId, templeId, orgCode });
		} else {
			return this.baseDAO.findByPropertys(RptInputLstValidateLog.class, new String[] { "caseId", "templeId" }, new Object[] { caseId, templeId });
		}
	}
	
	@Transactional(readOnly = false)
	public void saveUdipValidateLog(List<RptInputLstValidateLog> udipValidateLog, RptInputLstValidateLog validateInfo) {
		for(RptInputLstValidateLog log : udipValidateLog){
			log.setId(RandomUtils.uuid2());
			log.setDutyUser(validateInfo.getDutyUser());
			log.setCaseId(validateInfo.getCaseId());
			log.setTempleId(validateInfo.getTempleId());
			log.setValidateTime(validateInfo.getValidateTime());
			log.setDataFileId(validateInfo.getDataFileId());
			log.setOrgNo(validateInfo.getOrgNo());
			log.setRowNum(log.getRowNum());
			if (log.getValidateResult().length() > 2000) {
				log.setValidateResult(log.getValidateResult().substring(0, 2000) + "...");
			}
			this.baseDAO.persist(log);
		}
	}
	
	@Transactional(readOnly = false)
	public void deleteUdipValidateLog(String dataFileId) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("dataFileId", dataFileId);
		StringBuffer jql = new StringBuffer("delete from RptInputLstValidateLog log where log.dataFileId =:dataFileId ");
		this.baseDAO.batchExecuteWithNameParam(jql.toString(), params);
	}
	
	@Transactional(readOnly = false)
	public void deleteUdipValidateLog(String caseId, String templeId, String orgCode) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("caseId", caseId);
		params.put("templeId", templeId);//修复无法正确删除校验结果 20191026
		StringBuffer jql = new StringBuffer("delete from RptInputLstValidateLog log where log.caseId =:caseId and log.templeId =:templeId ");
		if(StringUtils.isNotBlank(orgCode)) {
			params.put("orgCode", orgCode);
			jql.append("and log.orgNo =:orgCode ");
		}
		this.baseDAO.batchExecuteWithNameParam(jql.toString(), params);
		
	}
	/**
	 * 给校验日志实体赋值
	 * @param caseId
	 * @param dataFileId
	 * @param operator
	 * @param orgCode
	 * @param templeId
	 * @param validateDate
	 * @return
	 */
	public RptInputLstValidateLog getUdipValidateLog(String caseId, String dataFileId, String operator, String orgCode, String templeId, String validateDate) {
		RptInputLstValidateLog log = new RptInputLstValidateLog();
		log.setCaseId(caseId);
		log.setDataFileId(dataFileId);
		log.setDutyUser(operator);
		log.setTempleId(templeId);
		log.setValidateTime(validateDate);
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templeId);
		if (StringUtils.isNotBlank(temp.getOrgColumn())) {
			log.setOrgNo(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		} else {
			log.setOrgNo(orgCode);
		}
		return log;
	}
}
