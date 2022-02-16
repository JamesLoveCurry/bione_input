package com.yusys.bione.plugin.engine.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.design.repository.RptTmpDAO;
import com.yusys.bione.plugin.engine.entity.RptEngineAutoTaskInfo;
import com.yusys.bione.plugin.engine.entity.RptEngineAutoTaskInfoPK;
import com.yusys.bione.plugin.engine.web.vo.RptEngineTaskVO;

@Service
@Transactional(readOnly = true)
public class RptEngineTaskInfoBS extends BaseBS<Object> {
	
	@Autowired
	public RptTmpDAO rptTmpDAO;
	
	// 节点类型
	@SuppressWarnings("unused")
	private String rootTreeType = "01";
	@SuppressWarnings("unused")
	private String folderTreeType = "02";

	// 数据模型树
	private String catalogType = "01";
	private String moduleType = "02";

	// 树根节点图标
	private String treeRootIcon = "/images/classics/icons/bricks.png";
	// 树文件夹节点图标
	private String treeFolderIcon = "/images/classics/icons/f1.gif";
	// 数据模型节点图标
	private String moduleNodeIcon = "/images/classics/icons/database_table.png";
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> module(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		String jql =  " select new com.yusys.bione.plugin.engine.web.vo.RptEngineTaskVO(tsk, mol.setNm) "
				    + " from RptEngineAutoTaskInfo tsk, RptSysModuleInfo mol where tsk.type =:tskType and tsk.id.taskId = mol.setId ";
		
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}
		jql += " order by tsk.startTime desc ";
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		values.put("tskType", "00");//00为数据模型
		SearchResult<RptEngineTaskVO> results = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		map.put("Rows", results.getResult());//返回list
		map.put("Total", results.getTotalCount());
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> report(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		String jql =  " select new com.yusys.bione.plugin.engine.web.vo.RptEngineTaskVO(tsk, rpt.rptNm) "
				    + " from RptEngineAutoTaskInfo tsk, RptMgrReportInfo rpt where tsk.type =:tskType and tsk.id.taskId = rpt.cfgId ";
		
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}
		jql += " order by tsk.startTime desc ";
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		values.put("tskType", "02");//报表
		SearchResult<RptEngineTaskVO> results = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		map.put("Rows", results.getResult());//返回list
		map.put("Total", results.getTotalCount());
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> index(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap, String tskType) {
		Map<String, Object> map = new HashMap<String, Object>();
		String jql =  " select new com.yusys.bione.plugin.engine.web.vo.RptEngineTaskVO(tsk, idx.indexNm) "
				    + " from RptEngineAutoTaskInfo tsk ,RptIdxInfo idx where tsk.type =:tskType and idx.endDate =:endDate and tsk.id.taskId = idx.id.indexNo ";
		
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}
		jql += " order by tsk.startTime desc ";
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		values.put("tskType", tskType);
		values.put("endDate", "29991231");
		SearchResult<RptEngineTaskVO> results = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		map.put("Rows", results.getResult());//返回list
		map.put("Total", results.getTotalCount());
		return map;
	}
	
	public Map<String, String> update(String selectedObj) {
		Map<String, String> rstMap = new HashMap<String, String>();
		if(StringUtils.isNotBlank(selectedObj)){
			String[] objs = StringUtils.split(selectedObj, ",");
			if(null != objs && objs.length > 0) {
				String jql = " update RptEngineAutoTaskInfo tsk set tsk.sts = :sts where (";
				Map<String, String> params = new HashMap<String, String>();
				for(int i = 0;i < objs.length;i++){
					String[] obj = StringUtils.split(objs[i], "@");
					if(i == 0){
						jql += " (tsk.id.dataDate = :dataDate"+ i +" and tsk.id.taskId = :taskId"+ i +")";
					}else{
						jql += " or (tsk.id.dataDate = :dataDate"+ i +" and tsk.id.taskId = :taskId"+ i +")";
					}
					params.put("dataDate"+ i, obj[0]);
					params.put("taskId"+ i, obj[1]);
				}
				jql += ")";
				params.put("sts", "2");//就绪
				this.baseDAO.batchExecuteWithNameParam(jql, params);
			}
		}
		rstMap.put("msg", "ok");
		return rstMap;
	}
	
	@Transactional(readOnly=false)
	public Map<String, Object> saveOrUpdateAutoTask(String dataDate, String checkedTaskIds,String taskType) {
		Map<String, Object> rstMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(checkedTaskIds)){
			String[] objs = StringUtils.split(checkedTaskIds, ",");
			if(null != objs && objs.length > 0) {
				for(int i = 0;i < objs.length;i++){
					String sql = " select t.task_id from RPT_ENGINE_AUTO_TASK_INFO t where t.data_date =?0"
							+ " and t.task_id =?1 and t.type =?2"; 
					List<Object[]> list = this.baseDAO.findByNativeSQLWithIndexParam(sql, dataDate,objs[i],taskType);
					if(list.size()>0){
						Map<String, String> params = new HashMap<String, String>();
						params.put("dataDate", dataDate);
						params.put("taskId", objs[i]);
						params.put("taskType", taskType);
						params.put("sts", "2");//就绪
						
						String jql = " update RptEngineAutoTaskInfo tsk set tsk.sts = :sts "
								+ " where tsk.id.dataDate = :dataDate and tsk.id.taskId = :taskId "
								+ " and tsk.type = :taskType";
						
						this.baseDAO.batchExecuteWithNameParam(jql, params);
					}else{
						RptEngineAutoTaskInfoPK id = new RptEngineAutoTaskInfoPK();
						RptEngineAutoTaskInfo taskInfo = new RptEngineAutoTaskInfo();
						id.setDataDate(dataDate);
						id.setTaskId(objs[i]);
						taskInfo.setId(id);
						taskInfo.setType(taskType);
						taskInfo.setSts("2");
						taskInfo.setStartTime(new Timestamp(new Date().getTime()));
						taskInfo.setRetryTimes(new BigDecimal(0));
						this.saveEntity(taskInfo);
					}
				}
			}
		}
		rstMap.put("msg", "ok");
		return rstMap;
	}
	
	/**
	 * 获取数据模型信息树形结构
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getRowModuleTree(String contextPath,String searchNm,String dsId,boolean colFlag) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		// 构造根节点
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		rootNode.setText("全部");
		Map<String, String> rootParam = new HashMap<String, String>();
		rootParam.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
		rootParam.put("type", catalogType);
		rootNode.setParams(rootParam);
		rootNode.setIcon(contextPath + treeRootIcon);
		nodes.add(rootNode);
		
		// 获取数据模型信息
		Map<String,Object> params = new HashMap<String, Object>();
		
		List<String> catalogIds = new ArrayList<String>();;
		if(StringUtils.isNotBlank(searchNm)){
			params.put("setNm", "%"+searchNm+"%");
		}
		if(StringUtils.isNotBlank(dsId)){
			params.put("dsId", dsId);
		}
		List<String> list = Lists.newArrayList();
		list.add("RPT_IDX_RESULT");
		list.add("RPT_REPORT_RESULT");
		params.put("setIds", list);
		
		params.put("setType", GlobalConstants4plugin.SET_TYPE_DETAIL);
		params.put("catalogId", GlobalConstants4plugin.STS_ON);//过滤掉指标事实表和报表事实表
		List<RptSysModuleInfo> modules = this.rptTmpDAO.getModuleInfoNoDetailNodes(params);
		List<String> setIds = new ArrayList<String>();
		for (RptSysModuleInfo mTmp : modules) {
			// 加工数据模型节点
			if (!setIds.contains(mTmp.getSetId())) {
				setIds.add(mTmp.getSetId());
			}
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(mTmp.getSetId());
			treeNode.setText(mTmp.getSetNm());
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("realId", mTmp.getSetId());
			paramMap.put("id", mTmp.getSetId());
			paramMap.put("type", moduleType);
			paramMap.put(
					"resDefNo",
					GlobalConstants4plugin.MODEL_RES_NO);
			treeNode.setParams(paramMap);
			treeNode.setUpId("catalog" + mTmp.getCatalogId());
			treeNode.setIcon(contextPath + moduleNodeIcon);
			catalogIds.add(mTmp.getCatalogId());
			nodes.add(treeNode);
		}
		// 获取目录信息
		List<String> caIds = new ArrayList<String>();
		getFolderInfo(contextPath,catalogIds, caIds, nodes);
		
		return nodes;
	}
	
	private void getFolderInfo(String contextPath,List<String> upIds,List<String> catalogIds,List<CommonTreeNode> nodes){
		Map<String,Object> cp = new HashMap<String, Object>();
		List<RptSysModuleCatalog> catalogs = new ArrayList<RptSysModuleCatalog>();
		if(upIds.size()>0){
			cp.put("catalogIds", upIds);
			catalogs = this.rptTmpDAO.getModuleCatalogNodes(cp);
		}
		upIds.clear();
		List<String> upCaIds = new ArrayList<String>();
		if(catalogs != null && catalogs.size() > 0){
			for (RptSysModuleCatalog cTmp : catalogs) {
				catalogIds.add(cTmp.getCatalogId());
				upCaIds.add(cTmp.getUpId());
				// 加工目录节点
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId("catalog" + cTmp.getCatalogId());
				treeNode.setText(cTmp.getCatalogNm());
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("realId", cTmp.getCatalogId());
				paramMap.put("type", catalogType);
				treeNode.setParams(paramMap);
				if (GlobalConstants4frame.TREE_ROOT_NO.equals(cTmp.getUpId())
						|| StringUtils.isEmpty(cTmp.getUpId())) {
					treeNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				} else {
					treeNode.setUpId("catalog" + cTmp.getUpId());
				}
				treeNode.setIcon(contextPath + treeFolderIcon);
				nodes.add(treeNode);
			}
			for(String upCaId : upCaIds){
				if(!upCaId.equals("0") && !catalogIds.contains(upCaId))
					upIds.add(upCaId);
			}
			if(upIds.size()>0){
				getFolderInfo(contextPath,upIds, catalogIds, nodes);
			}
		}
	}
}
	