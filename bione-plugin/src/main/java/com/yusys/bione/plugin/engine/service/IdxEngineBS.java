package com.yusys.bione.plugin.engine.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.engine.entity.RptEngineIdxSt;
import com.yusys.bione.plugin.engine.entity.RptEngineIdxStPK;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.engine.repository.mybatis.EngineDao;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;

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
public class IdxEngineBS extends BaseBS<RptEngineIdxSt> {

	@Autowired
	EngineDao engineDao;

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public SearchResult<RptTaskInstanceInfo> getEngineIdxStsList(Pager pager,
			Map<String, Object> map) {
		PageHelper.startPage(pager);
		PageMyBatis<RptTaskInstanceInfo> page = (PageMyBatis<RptTaskInstanceInfo>) this.engineDao
				.getEngineIdxList(map);
		SearchResult<RptTaskInstanceInfo> results = new SearchResult<RptTaskInstanceInfo>();
		results.setResult(page.getResult());
		results.setTotalCount(page.getTotalCount());
		return results;
	}
	
	public List<RptTaskInstanceInfo> getEngineIdxPendingSts() {
		String jql = " select t from RptTaskInstanceInfo t where t.taskType in (?0) and t.parentTaskId is null and (t.sts = ?1 or t.sts = ?2) ";
		return this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDX,"02","01");
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
	public void saveOrUpdateEngineIdxSts(Map<String, Object> map) {
		String dataDate = (String) map.get("dataDate");
		String checkedIndexIds = (String) map.get("checkedIndexIds");
		String[] checkedIndexIdArr = StringUtils.split(checkedIndexIds, ',');
		for (int i = 0; i < checkedIndexIdArr.length; i++) {
			RptEngineIdxStPK rptEngineIdxStPK = new RptEngineIdxStPK();
			rptEngineIdxStPK.setDataDate(dataDate);
			rptEngineIdxStPK.setIndexNo(checkedIndexIdArr[i]);
			RptEngineIdxSt engineIdxSt = new RptEngineIdxSt();
			engineIdxSt.setId(rptEngineIdxStPK);
			engineIdxSt.setSts((String) map.get("sts"));
			if(map.get("fail")==null){
				engineIdxSt.setStartTime(new Timestamp(new Date().getTime()));
				engineIdxSt.setEndTime(null);
			}
			this.baseDAO.merge(engineIdxSt);
		}
	}

	public void runData(Map<String, Object> map) throws Exception {
		String dataDate = (String) map.get("dataDate");
		String checkedIndexIds = (String) map.get("checkedIndexIds");
		String isReRunsal = (String) map.get("isReRunsal");
		String[] checkedIndexIdArr = StringUtils.split(checkedIndexIds, ',');
		List<String> idxnos = Lists.newArrayList();
		for (int i = 0; i < checkedIndexIdArr.length; i++) {
			idxnos.add(checkedIndexIdArr[i]);
		}
		Map<String, Object> map_ = Maps.newHashMap();
		if (isReRunsal != null) {
			if (isReRunsal.equals("N")) {
				map_.put("RunType", GlobalConstants4plugin.ENGINE_RUN_TYPE_CALC);
			} else {
				map_.put("RunType", GlobalConstants4plugin.ENGINE_RUN_TYPE_INFLU);
			}
		} else {
			map_.put("RunType", GlobalConstants4plugin.ENGINE_RUN_TYPE_CALC);
		}
		map_.put("IndexNo", idxnos);
		map_.put("DataDate", dataDate);
		String json = JSON.toJSONString(map_);
        CommandRemote.sendAync(json, CommandRemoteType.INDEX);
	}

	public List<RptIdxInfo> getRptIdxInfosByParams(Map<String, Object> params) {
		return this.engineDao.getRptIdxInfosByParams(params);
	}
    
	public void updateRptEngineIdxSts(Map<String, Object> params){
		this.engineDao.updateRptEngineIdxSts(params);
	}
	
	public SearchResult<RptTaskInstanceInfo> getEngineChildIdxStsList(Pager pager,
			String taskNo, String instanceId) {
		Map<String, Object> param = Maps.newHashMap();
		List<String> taskTypes = new ArrayList<String>();
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDX);
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTIDX);
		param.put("taskTypes", taskTypes);
		param.put("taskNo", taskNo);
		PageHelper.startPage(pager);
		PageMyBatis<RptTaskInstanceInfo> page = (PageMyBatis<RptTaskInstanceInfo>) this.engineDao
				.getEngineChildIdxStsList(param);
		SearchResult<RptTaskInstanceInfo> results = new SearchResult<RptTaskInstanceInfo>();
		results.setResult(page.getResult());
		results.setTotalCount(page.getTotalCount());
		return results;
	}
}