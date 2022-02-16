package com.yusys.bione.plugin.engine.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.repository.mybatis.DataSourceDao;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.engine.entity.RptEngineProcess;
import com.yusys.bione.plugin.engine.entity.RptEngineRefreshInfo;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.engine.repository.mybatis.FrsEngineDao;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.valid.entitiy.RptEngineReportSts;
import com.yusys.bione.plugin.valid.entitiy.RptEngineReportStsPK;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class FrsRptEngineBS extends BaseBS<RptEngineReportSts> {
	@Autowired
	FrsEngineDao engineDao;

	@Autowired
	private DataSourceDao dataSourceDao;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public SearchResult<RptTaskInstanceInfo> getEngineRptStsList(Pager pager,
			Map<String, Object> map) {
		PageHelper.startPage(pager);
		PageMyBatis<RptTaskInstanceInfo> page = (PageMyBatis<RptTaskInstanceInfo>) this.engineDao
				.getEngineRptStsListNew(map);
		SearchResult<RptTaskInstanceInfo> results = new SearchResult<RptTaskInstanceInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}

	public List<CommonComboBoxNode> exeStsList() {

		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId("");
		node.setText("全部");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.RPT_ENGINE_IDX_STS_WAIT);
		node.setText("等待运行");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.RPT_ENGINE_IDX_STS_DOING);
		node.setText("正在执行");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.RPT_ENGINE_IDX_STS_FINISH);
		node.setText("执行完毕");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.RPT_ENGINE_IDX_STS_FAIL);
		node.setText("执行失败");
		nodes.add(node);
		return nodes;
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
//		if (isReRunsal != null) {
//			if (isReRunsal.equals("N")) {
//				map_.put("RunType", ENGINE_RUN_TYPE_CALC);
//			} else {
//				map_.put("RunType", ENGINE_RUN_TYPE_INFLU);
//			}
//		} else {
//			map_.put("RunType", ENGINE_RUN_TYPE_CALC);
//		}
		map_.put("RptId", rptIds);
		String checkedOrgIds = (String) map.get("checkedOrgIds");
		if(StringUtils.isNotEmpty(checkedOrgIds)){
			String[] checkedOrgIdArr = StringUtils.split(checkedOrgIds, ',');
			List<String> orgIds = Lists.newArrayList();
			for (int i = 0; i < checkedOrgIdArr.length; i++) {
				orgIds.add(checkedOrgIdArr[i]);
			}
			map_.put("OrgNo", orgIds);
		}
		map_.put("DataDate", dataDate);
		String json = JSON.toJSONString(map_);
		CommandRemote.sendAync(json, CommandRemoteType.INDEX );
	}
	
	public String runData(String rptTmpId,String dataDate,String orgNo){
		Map<String, Object> map_ = Maps.newHashMap();
		map_.put("RelRptTmpId", rptTmpId);
		map_.put("DataDate", dataDate);
		map_.put("OrgNo", orgNo);
		String json = JSON.toJSONString(map_);
		Object obj = null;
		String returnJson = "";
		try {
			obj = CommandRemote.sendSync(json,
					CommandRemoteType.QUERY);
		} catch (Throwable e) {
			logger.debug("查询引擎连接异常"+e);
			return "查询引擎连接异常"+e;
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else {
			logger.debug("查询引擎异常");
			return "引擎查询异常:"+obj.toString();
		}
		try {
			JSONObject jsonMap = JSON.parseObject(returnJson);
			String result = jsonMap.get("Code").toString();
			if (result.equals("0000")) {
				return "";
			} else {
				logger.error("send error returnJson = "+returnJson+"   ,  msg = "+jsonMap.get("Msg").toString());
				return jsonMap.get("Msg").toString();
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return "引擎查询异常:"+e.getMessage();
		}
	}

	public List<RptMgrReportInfo> getRptMgrReportInfosByParams(Map<String, Object> params) {
		return this.engineDao.getRptMgrReportInfosByParams(params);
	}
    
	public void updateRptEngineReportSts(Map<String, Object> params){
		this.engineDao.updateRptEngineReportSts(params);
	}
	
	public FrsEngineDao getEngineDao() {
		return engineDao;
	}

	public void setEngineDao(FrsEngineDao engineDao) {
		this.engineDao = engineDao;
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
	
	// 获取URl
	public BioneDriverInfo getURLData(String driverId) {
		BioneDriverInfo bioneLogicSysInfo = this.dataSourceDao.getURLDataByDriverId(driverId);
		return bioneLogicSysInfo;
	}
	
	public List<RptEngineProcess> getProcessByDsId(String dsId){
		return engineDao.getProcessByDsId(dsId);
	}
	
	public RptEngineProcess getProcessByTaskId(String taskId){
		return engineDao.getProcessByTaskId(taskId);
	}
	
	public void createProcess(List<RptEngineProcess>processList){
		for(RptEngineProcess rptEngineProcess : processList)
			engineDao.createProcess(rptEngineProcess);
	}
	
	public void deleteProcessByTaskId(List<String>taskList){
		Map<String,Object>param=Maps.newHashMap();
		param.put("taskList", taskList);
		engineDao.deleteProcessByTaskId(param);
	}
	
	public void deleteProcessByDsId(String dsId){
		engineDao.deleteProcessByDsId(dsId);
	}
	
	public void deleteProcessByTaskId(RptEngineProcess rptEngineProcess){
		engineDao.updateProcessByTaskId(rptEngineProcess);
	}
		
	public List<RptTaskInstanceInfo> getEngineRptPendingSts() {
		String jql = " select t from RptTaskInstanceInfo t where t.taskType in (?0) and t.parentTaskId is null and (t.sts = ?1 or t.sts = ?2) ";
		return this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTID,"02","01");
	}
	
	public RptTaskInstanceInfo getDetailTsk(String taskNo, String dataDate, String instanceId) {
		String jql = "select log from RptTaskInstanceInfo log where log.id.taskNo = ?0 and log.id.dataDate = ?1 and log.id.instanceId = ?2";
		RptTaskInstanceInfo info = this.baseDAO.findUniqueWithIndexParam(jql, taskNo, dataDate, instanceId);
		if (info != null) {
			try {
				JSONObject result = JSON.parseObject(StringUtils.remove(info.getRunLog(), '\\'));
				String runLog = getEngineLogSts(result.getString("Code")) + "\n";
				runLog += result.get("Msg");
				info.setRunLog(runLog);
			} catch (Exception e) {
				info.setRunLog("\n");
			}
		}
		return info;
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
	
	@SuppressWarnings("unchecked")
	public SearchResult<RptTaskInstanceInfo> getEngineChildRptStsList(Pager pager,
			String taskNo) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select t from RptTaskInstanceInfo t where t.taskType in :taskTypes and t.parentTaskId = :taskNo");
		Map<String, Object> values = (Map<String, Object>) pager.getSearchCondition().get("params");
		List<String> taskTypes = new ArrayList<String>();
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTID);
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTTMP);
		values.put("taskTypes", taskTypes);
		values.put("taskNo", taskNo);
		if (!pager.getSearchCondition().get("jql").equals(StringUtils.EMPTY)) {
			jql.append(" and " + pager.getSearchCondition().get("jql"));
		}
		if (!StringUtils.isEmpty(pager.getSortname())) {
			jql.append(" order by t." + pager.getSortname() + " " + pager.getSortorder());
		}
		return this.baseDAO.findPageWithNameParam(pager.getPageFirstIndex(),pager.getPagesize(), jql.toString(), values);
	}
	
	public String redoTask(String instanceId,String dataDate,String taskType){
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
			CommandRemote.sendAync(json, CommandRemoteType.INDEX );
		}catch (Throwable e) {
			logger.debug("重跑任务失败");
			return "重跑任务失败";
		}
		return "";
	}

	public List<RptEngineRefreshInfo> getRefreshSts(String refrType) {
		String jql = " select t from RptEngineRefreshInfo t where t.taskType = ?0 and t.sts = ?1";
		String taskType = "";
		switch (refrType) {
			case "noderef":
				taskType = "RefreshNodeInfo";
				break;
			case "dsref":
				taskType = "RefreshDSCache";
				break;
			case "confref":
				taskType = "RefreshConfCache";
				break;
			default:
			return null;
		}
		return this.baseDAO.findWithIndexParam(jql, taskType,"02");
	}
}