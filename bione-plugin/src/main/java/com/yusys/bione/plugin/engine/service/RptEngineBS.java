package com.yusys.bione.plugin.engine.service;


import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.SQLUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.design.entity.RptDesignSourceIdx;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.engine.repository.mybatis.EngineDao;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.valid.entitiy.RptEngineReportSts;
import com.yusys.bione.plugin.valid.entitiy.RptEngineReportStsPK;

/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author aman aman@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptEngineBS extends BaseBS<RptEngineReportSts> {
	@Autowired
	EngineDao engineDao;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public SearchResult<RptTaskInstanceInfo> getEngineRptStsList(Pager pager,
			Map<String, Object> map) {
		PageHelper.startPage(pager);
		PageMyBatis<RptTaskInstanceInfo> page = (PageMyBatis<RptTaskInstanceInfo>) this.engineDao
				.getEngineRptList(map);
		SearchResult<RptTaskInstanceInfo> results = new SearchResult<RptTaskInstanceInfo>();
		results.setResult(page.getResult());
		results.setTotalCount(page.getTotalCount());
		return results;
	}
	
	public List<RptTaskInstanceInfo> getEngineRptPendingSts() {
		String jql = " select t from RptTaskInstanceInfo t where t.taskType in (?0) and t.parentTaskId is null and (t.sts = ?1 or t.sts = ?2) ";
		return this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTID,"02","01");
	}
	
	@SuppressWarnings("unchecked")
	public SearchResult<RptTaskInstanceInfo> getEngineChildRptStsList(Pager pager,
			String taskNo, String instanceId) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select t from RptTaskInstanceInfo t where t.taskType in :taskTypes and t.id.taskNo = :taskNo");
		Map<String, Object> values = (Map<String, Object>) pager.getSearchCondition().get("params");
		List<String> taskTypes = new ArrayList<String>();
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTID);
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTTMP);
		values.put("taskTypes", taskTypes);
		values.put("taskNo", taskNo);
		String countJql = SQLUtils.buildCountSQL(jql);
		//先查询一下当前任务里面包含几张报表
		Long totalCount = (Long) this.baseDAO.getEntityManager().createQueryWithNameParam(countJql, values).getSingleResult();
		if(totalCount > 1) {//如果大于1张就走按照报表id查询的逻辑
			jql = new StringBuilder(1000);
			jql.append("select t from RptMgrReportInfo t where t.rptId = ?0");
			RptMgrReportInfo rpt = this.baseDAO.findUniqueWithIndexParam(jql.toString(), instanceId);
			if(null != rpt) {
				jql = new StringBuilder(1000);
				jql.append("select t from RptDesignSourceIdx t where t.id.templateId = ?0");
				List<RptDesignSourceIdx> idxList = this.baseDAO.findWithIndexParam(jql.toString(), rpt.getCfgId());
				if(null != idxList) {
					List<String> idxNos = new ArrayList<String>();
					for(RptDesignSourceIdx idx : idxList) {
						idxNos.add(idx.getIndexNo());
					}
					values.put("instanceId", idxNos);
					jql = new StringBuilder(1000);
					jql.append("select t from RptTaskInstanceInfo t where t.taskType in :taskTypes and t.id.instanceId in :instanceId and t.parentTaskId = :taskNo");
				}
			}
		}else {//任务记录数为1就走原有逻辑
			jql = new StringBuilder(1000);
			jql.append("select t from RptTaskInstanceInfo t where t.taskType in :taskTypes and t.parentTaskId = :taskNo");
		}
		if (!pager.getSearchCondition().get("jql").equals(StringUtils.EMPTY)) {
			jql.append(" and " + pager.getSearchCondition().get("jql"));
		}
		if (!StringUtils.isEmpty(pager.getSortname())) {
			jql.append(" order by t." + pager.getSortname() + " " + pager.getSortorder());
		}
		return this.baseDAO.findPageWithNameParam(pager.getPageFirstIndex(),pager.getPagesize(), jql.toString(), values);
	}

	public List<CommonComboBoxNode> exeStsList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId("");
		node.setText("全部");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.ENGINE_RUN_STATUS_WAITING);
		node.setText("等待运行");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.ENGINE_RUN_STATUS_RUNNING);
		node.setText("正在执行");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.ENGINE_RUN_STATUS_OK);
		node.setText("执行成功");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.ENGINE_RUN_STATUS_FAIL);
		node.setText("执行失败");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.ENGINE_RUN_STATUS_STOP);
		node.setText("手动停止");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.ENGINE_RUN_STATUS_TIME_OUT);
		node.setText("超时停止");
		nodes.add(node);
		return nodes;
	}

	@Transactional(readOnly = false)
	public void updateRptEngineReportCheckeSts(String dataDate) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("dataDate", dataDate);
		this.engineDao.updateRptEngineReportCheckeSts(map);
	}
	@Transactional(readOnly = false)
	public void updateRptEngineReportSumSts(String dataDate) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("dataDate", dataDate);
		this.engineDao.updateRptEngineReportSumSts(map);
	}

	@Transactional(readOnly = false)
	public void saveOrUpdateEngineRptSts(Map<String, Object> map) {
		String dataDate = (String) map.get("dataDate");
		String checkedRptIds = (String) map.get("checkedRptIds");
		String[] checkedRptIdArr = StringUtils.split(checkedRptIds, ',');
		for (int i = 0; i < checkedRptIdArr.length; i++) {
			RptEngineReportStsPK rptEngineRptStPK = new RptEngineReportStsPK();
			rptEngineRptStPK.setDataDate(dataDate);
			rptEngineRptStPK.setRptId(checkedRptIdArr[i]);
			RptEngineReportSts engineRptSt = new RptEngineReportSts();
			engineRptSt.setId(rptEngineRptStPK);
			engineRptSt.setSts((String) map.get("sts"));
			if(map.get("fail")==null){
				engineRptSt.setStartTime(new Timestamp(new Date().getTime()));
				engineRptSt.setEndTime(null);
			}
			this.baseDAO.merge(engineRptSt);
		}
	}
	
	public void runData(Map<String, Object> map) throws Exception {
		String dataDate = (String) map.get("dataDate");
		String checkedRptIds = (String) map.get("checkedRptIds");
		String[] checkedRptIdArr = StringUtils.split(checkedRptIds, ',');
		List<String> rptIds = Lists.newArrayList();
		for (int i = 0; i < checkedRptIdArr.length; i++) {
			rptIds.add(checkedRptIdArr[i]);
		}
		Map<String, Object> map_ = Maps.newHashMap();
		map_.put("RptId", rptIds);
		map_.put("DataDate", dataDate);
		String json = JSON.toJSONString(map_);
		CommandRemote.sendAync(json, CommandRemoteType.INDEX );
	}
	
	public String stopTask(String taskNo){
		String returnJson ="";
		Object obj = null;
		Map<String, Object> map_ = Maps.newHashMap();
		map_.put("KillTask", taskNo);
		String json = JSON.toJSONString(map_);
		try {
			obj = CommandRemote.sendSync( json, CommandRemoteType.INDEX );
		}catch (Throwable e) {
			logger.debug("引擎连接超时");
			return "引擎连接超时";
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else {
			logger.debug("停止任务失败");
			return "停止任务失败，";
		}
		String error = "";
		try {
			JSONObject jsonMap = JSON.parseObject(returnJson);
			String result = jsonMap.get("Code").toString();
			if ("0000".equals(result)) {
				
			} else {
				return "停止任务失败";
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return "停止任务失败";
		}
		return error;
	}
	
	public String redoTask(String instanceId,String dataDate,String taskType, String indexType){
		Map<String, Object> map_ = Maps.newHashMap();
		if(taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTID)){
			map_.put("RptId", instanceId);
		}
		if(taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTTMP)){
			map_.put("RptTmpId", instanceId);
		}
		if(taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDX)){
			map_.put("IndexNo", instanceId);
			if(StringUtils.isNotBlank(indexType)){ //基础指标进行影响跑数
				if(GlobalConstants4plugin.ROOT_INDEX.equals(indexType) || GlobalConstants4plugin.GENERIC_INDEX.equals(indexType)
						|| GlobalConstants4plugin.GENERIC_INDEX.equals(indexType)){
					map_.put("RunType", GlobalConstants4plugin.ENGINE_RUN_TYPE_INFLU);
				}
			}
		}
		map_.put("DataDate", dataDate);
		String json = JSON.toJSONString(map_);
		try {
			CommandRemote.sendAync(json, CommandRemoteType.INDEX );
		}catch (Throwable e) {
			logger.debug("重跑任务失败");
			return "重跑任务失败";
		}
		return "";
	}
	
	public String runTask(String instanceId,String dataDate,String taskType){
		String returnJson ="";
		Object obj = null;
		Map<String, Object> map_ = Maps.newHashMap();
		if(taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTID)){
			map_.put("RptId", instanceId);
		}
		if(taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTTMP)){
			map_.put("RptTmpId", instanceId);
		}
		if(taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDX)){
			map_.put("IndexNo", instanceId);
		}
		map_.put("DataDate", dataDate);
		String json = JSON.toJSONString(map_);
		try {
			obj = CommandRemote.sendSync(json, CommandRemoteType.INDEX );
		}catch (Throwable e) {
			logger.debug("引擎连接超时");
			return "引擎连接超时";
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else {
			logger.debug("报表跑数失败");
			return "报表跑数失败，";
		}
		String error = "";
		try {
			JSONObject jsonMap = JSON.parseObject(returnJson);
			String result = jsonMap.get("Code").toString();
			if ("0000".equals(result)) {
				
			} else {
				return "报表跑数失败";
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return "报表跑数失败";
		}
		return error;
	}

	public List<RptMgrReportInfo> getRptMgrReportInfosByParams(Map<String, Object> params) {
		return this.engineDao.getRptMgrReportInfosByParams(params);
	}
    
	public void updateRptEngineReportSts(Map<String, Object> params){
		this.engineDao.updateRptEngineReportSts(params);
	}
	
	public EngineDao getEngineDao() {
		return engineDao;
	}

	public void setEngineDao(EngineDao engineDao) {
		this.engineDao = engineDao;
	}
	
	public RptTaskInstanceInfo getDetailTsk(String taskNo, String dataDate, String instanceId) {
		String jql = "select log from RptTaskInstanceInfo log where log.id.taskNo = ?0 and log.id.dataDate = ?1 and log.id.instanceId = ?2";
		RptTaskInstanceInfo info = this.baseDAO.findUniqueWithIndexParam(jql, taskNo, dataDate, instanceId);
		if (info != null) {
			try {
				JSONObject result = JSON.parseObject(info.getRunLog());
				String runLog = getEngineLogSts(result.getString("Code")) + "\n";
				runLog += result.get("Msg");
				info.setRunLog(runLog);
			} catch (Exception e) {
				info.setRunLog("\n");
			}
		}
		return info;
	}
	
	public List<RptTaskInstanceInfo> getChildDetailTsk(String taskNo){
		return this.getEntityListByProperty(RptTaskInstanceInfo.class, "parentTaskId", taskNo);
	}
	
	private String getEngineLogSts(String code){
		if(GlobalConstants4plugin.RESULT_SUCCESS.equals(code)){
			return "成功";
		}
		if(GlobalConstants4plugin.RESULT_DATABASE_ERROR.equals(code)){
			return "数据库错误 ";
		}
		if(GlobalConstants4plugin.RESULT_INDEX_ERROR.equals(code)){
			return "指标配置错误";
		}
		if(GlobalConstants4plugin.RESULT_DIM_ERROR.equals(code)){
			return "维度配置错误";
		}
		if(GlobalConstants4plugin.RESULT_CACHE_ERROR.equals(code)){
			return "cache出错";
		}
		if(GlobalConstants4plugin.RESULT_COMPUTE_ERROR.equals(code)){
			return "计算出错";
		}
		if(GlobalConstants4plugin.RESULT_CHECK_ERROR.equals(code)){
			return "检核配置错误";
		}
		if(GlobalConstants4plugin.RESULT_ORG_ERROR.equals(code)){
			return "机构配置错误";
		}
		if(GlobalConstants4plugin.RESULT_NO_TASK_ERROR.equals(code)){
			return "没有找到任务";
		}
		if(GlobalConstants4plugin.RESULT_NODE_ERROR.equals(code)){
			return "节点配置错误";
		}
		if(GlobalConstants4plugin.RESULT_REBOOT_ERROR.equals(code)){
			return "引擎重启导致任务失败";
		}
		if(GlobalConstants4plugin.RESULT_JSON_ERROR.equals(code)){
			return "报文错误 ";
		}
		if(GlobalConstants4plugin.RESULT_FTP_ERROR.equals(code)){
			return "文件传输错误";
		}
		if(GlobalConstants4plugin.RESULT_KILL_ERROR.equals(code)){
			return "杀死任务失败";
		}
		
		if(GlobalConstants4plugin.RESULT_NETWORK_ERROR.equals(code)){
			return "网络错误";
		}
		if(GlobalConstants4plugin.RESULT_PART_SUCCESS.equals(code)){
			return "部分成功";
		}
		if(GlobalConstants4plugin.RESULT_UNKNOWN_ERROR.equals(code)){
			return "未知错误";
		}
		if(GlobalConstants4plugin.RESULT_UNREALIZED_ERROR.equals(code)){
			return "未实现的类";
		}
		if(GlobalConstants4plugin.RESULT_SYS_JAR_ERROR.equals(code)){
			return "缺少jar";
		}
		if(GlobalConstants4plugin.RESULT_SYS_UNDEF_INTERFACE.equals(code)){
			return "未定义的计算器";
		}
		if(GlobalConstants4plugin.RESULT_SYS_TIMEOUT.equals(code)){
			return "执行超时";
		}
		if(GlobalConstants4plugin.RESULT_PARAMETER_ERROR.equals(code)){
			return "参数错误";
		}
		if(GlobalConstants4plugin.RESULT_SYS_KILL.equals(code)){
			return "手动杀死";
		}
		if(GlobalConstants4plugin.RESULT_UNDEF_METHOD_ERROR.equals(code)){
			return "未实现的方法";
		}
		return  "";
	}
	
	/**
	 * 删除报表跑数任务记录
	 * @param taskNos
	 */
	public void deleteInstance(String taskNos) {
		JSONArray taskNoArray = JSONArray.parseArray(taskNos);
		//删除主任务
		String jql = "delete from RptTaskInstanceInfo info where info.id.taskNo in :taskNos";
		Map<String,Object> params = Maps.newHashMap();
		params.put("taskNos", taskNoArray);
		this.baseDAO.batchExecuteWithNameParam(jql, params);
		//删除子任务
		jql = "delete from RptTaskInstanceInfo info where info.parentTaskId in :taskNos";
		this.baseDAO.batchExecuteWithNameParam(jql, params);
	}

	public Map<String, Object> getRptSts(String dataDate,String checkedRptIds) {
		Map<String, Object> result = new HashMap<>();
		boolean status = false;
		String[] checkedRptIdArr = StringUtils.split(checkedRptIds, ',');
		Map<String, Object> param = new HashMap<>();
		param.put("dataDate",dataDate);
		StringBuilder msg = new StringBuilder("报表: ");
		for (int i = 0; i < checkedRptIdArr.length; i++) {
			param.put("rptId",checkedRptIdArr[i]);
			List<Map<String, Object>> list = this.engineDao.getRptSts(param);
			if(list != null && list.size() > 0){
				String rptNm = (String) list.get(0).get("RPT_NM");
				msg.append("【").append(rptNm).append("在");
				List<String> orgNms = new ArrayList<>();
				for (Map<String, Object> map : list) {
					status = true;
					orgNms.add((String) map.get("ORG_NM"));
				}
				String str = StringUtils.join(orgNms.toArray(), ",");
				msg.append(str).append("】");
			}
		}
		result.put("status",status);
		result.put("msg",msg.append("已提交。是否确认重跑?").toString());
		return result;
	}
}