package com.yusys.bione.plugin.engine.web;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.engine.service.RptEngineBS;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.service.RptMgrBS;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;

/**
 * 
 * <pre>
 * Title:
 * Description:
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
@Controller
@RequestMapping("/report/frame/enginelog/rpt")
public class RptEngineLogController extends BaseController {
	@Autowired
	private RptEngineBS rptEngineBS;

	@Autowired
	public RptMgrBS rptMgrBS;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/engine/engine-log-rpt-index");
	}

	/**
	 * 构造报表引擎信息列表
	 */
	@RequestMapping(value = "/getEngineRptStsList.*")
	@ResponseBody
	public Map<String, Object> getEngineRptStsList(Pager pager) {
		Map<String, Object> map = Maps.newHashMap();
		List<String> taskTypes = new ArrayList<String>();
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTID);
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTTMP);
		map.put("taskTypes", taskTypes);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
			if(authRptIds != null && authRptIds.size() > 0){
				map.put("authRptIds", SplitStringBy1000.change(authRptIds));
			}else{
				map.put("authRptIds", "-999");
			}
		}
		SearchResult<RptTaskInstanceInfo> searchResult = this.rptEngineBS
				.getEngineRptStsList(pager, map);
		Map<String, Object> rowData = Maps.newHashMap();
		rowData.put("Rows", searchResult.getResult());
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}

	/**
	 * 自动刷新获取引擎状态
	 */
	@RequestMapping(value = "/getEngineRptPendingSts")
	public Map<String, Object> getEngineRptPendingSts() {
		Map<String, Object> returnMap = Maps.newHashMap();
		List<RptTaskInstanceInfo> pendingCount = this.rptEngineBS.getEngineRptPendingSts();
		returnMap.put("msg", "unExsi");
		if(null != pendingCount && pendingCount.size() > 0){
			returnMap.put("msg", "exsi");
		}
		return returnMap;
	}
	
	/**
	 * 构造报表引擎信息列表
	 */
	@RequestMapping(value = "/getEngineChildRptStsList.*")
	@ResponseBody
	public Map<String, Object> getEngineChildRptStsList(Pager pager,String taskNo, String instanceId) {
		SearchResult<RptTaskInstanceInfo> searchResult = this.rptEngineBS
				.getEngineChildRptStsList(pager,taskNo, instanceId);
		Map<String, Object> rowData = Maps.newHashMap();
		List<RptTaskInstanceInfo> list = searchResult.getResult();
		rowData.put("Rows", list);
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
	/**
	 * 报表引擎计算状态新增页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/newRptEngineLog")
	public ModelAndView newEngineBatch() {
		return new ModelAndView("/plugin/engine/engine-log-rpt-new");
	}
	
	/**
	 * 报表引擎详细信息页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/detailLog")
	public ModelAndView detailLog(String taskNo, String dataDate, String instanceId) {
		ModelMap map = new ModelMap();
		map.put("taskNo", StringUtils2.javaScriptEncode(taskNo));
		map.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		map.put("instanceId", StringUtils2.javaScriptEncode(instanceId));
		return new ModelAndView("/plugin/engine/engine-log-rpt-view", map);
	}
	
	/**
	 * 报表引擎详细信息子页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/detailChildLog")
	public ModelAndView detailChildLog(String taskNo, String dataDate, String instanceId) {
		ModelMap map = new ModelMap();
		map.put("taskNo", StringUtils2.javaScriptEncode(taskNo));
		map.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		map.put("instanceId", StringUtils2.javaScriptEncode(instanceId));
		return new ModelAndView("/plugin/engine/engine-log-rpt-child-view", map);
	}
	
	/**
	 * 获取引擎详细信息页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getDetail")
	@ResponseBody
	public Map<String,Object> getDetail(String taskNo, String dataDate, String instanceId) {
		Map<String,Object> result= new HashMap<String, Object>();
		result.put("baseData", this.rptEngineBS.getDetailTsk(taskNo, dataDate, instanceId));
		return result;
	}

	/***
	 * 检查所选的报表是否在执行任务
	 * 
	 * @param checkedRptIds
	 * @return
	 */
	@RequestMapping(value = "/isSelectedRptRunning.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> isSelectedRptRunning(String checkedRptIds,
			String dataDate) {
		Map<String, Object> params = Maps.newHashMap();
		List<String> checkedRptIdList = Lists.newArrayList();
		List<String> engineIdxStsList = Lists.newArrayList();
		engineIdxStsList.add(GlobalConstants4plugin.RPT_ENGINE_REPORT_STS_RUN);
		engineIdxStsList.add(GlobalConstants4plugin.RPT_ENGINE_REPORT_STS_WAIT);
		String[] checkedRptIdArr = StringUtils.split(checkedRptIds, ',');
		for (String checkedRptId : checkedRptIdArr) {
			checkedRptIdList.add(checkedRptId);
		}
		if (checkedRptIdList.size() == 0) {
			checkedRptIdList.add(" ");
		}
		params.put("rptIds", checkedRptIdList);
		params.put("sts", engineIdxStsList);
		params.put("dataDate", dataDate);
		List<RptMgrReportInfo> rptInfos = this.rptEngineBS
				.getRptMgrReportInfosByParams(params);
		if (rptInfos.size() == 0) {
			params.put("msg", "1");
		} else {
			String rptNames = "";
			for (int i = 0; i < rptInfos.size(); i++) {
				RptMgrReportInfo info = rptInfos.get(i);
				rptNames += info.getRptNm() + ",";
			}
			if (rptNames.length() > 0) {
				rptNames = rptNames.substring(0, rptNames.length() - 1);
			}
			params.put("msg", "0");
			params.put("rptNames", rptNames);
		}
		return params;
	}

	/**
	 * 获取批次任务执行类型列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/exeStsList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> exeStsList() {
		return this.rptEngineBS.exeStsList();
	}

	@SuppressWarnings("unused")
	private String getFormatDate(String date) {
		if (date == null || date.equals("")) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (date.length() == 8) {
			format = new SimpleDateFormat("yyyyMMdd");
		}
		Date newDate = null;
		try {
			newDate = format.parse(date);
			format = new SimpleDateFormat("yyyyMMdd");
			if (date.length() == 8) {
				format = new SimpleDateFormat("yyyy-MM-dd");
			}
			return format.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 新增或者修改报表引擎计算信息
	 */
	@RequestMapping(value = "/saveOrUpdateEngineRptSts.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveOrUpdateEngineRptSts(String dataDate,
			String isReRunsal, String checkedRptIds) {
		String step = "step0";
		Map<String, Object> params = Maps.newHashMap();
		Map<String, Object> map = Maps.newHashMap();
		map.put("dataDate", dataDate);
		map.put("isReRunsal", isReRunsal);
		map.put("checkedRptIds", checkedRptIds);
		boolean pingFlag = true;
		try {
			pingFlag = CommandRemote.testConnection(CommandRemoteType.INDEX);
		} catch (Throwable e1) {
			pingFlag = false;
			e1.printStackTrace();
		}
		if (!pingFlag) {
			logger.info("引擎连接失败！");
			params.put("msg", "引擎连接失败！");
			return params;
		}
		try {
			// logger.info("报表引擎跑数开始！");
			this.rptEngineBS.runData(map);
			// logger.info("报表引擎跑数成功！");
			params.put("msg", "启动成功！");
		} catch (Exception e) {
			if (step == "step2") {
				logger.info("报表引擎跑数失败，状态置为[执行失败]！");
				map.put("fail", "fail");
				try {
					logger.info("状态置为[执行失败]操作开始！");
					logger.info("状态置为[执行失败]操作成功！");
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.info("状态置为[执行失败]操作失败！");
				}
			} else {
				logger.info("状态置为[等待执行]操作失败！");
			}
			params.put("msg", "启动失败！");
			e.printStackTrace();
		}
		return params;
	}
	
	/**
	 * 手工重跑任务
	 */
	@RequestMapping(value = "/redoTask", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> redoTask(String instanceId,String dataDate,String taskType, String indexType) {
		Map<String, Object> params = Maps.newHashMap();
		boolean pingFlag = true;
		try {
			pingFlag = CommandRemote.testConnection(CommandRemoteType.INDEX);
		} catch (Throwable e1) {
			pingFlag = false;
			e1.printStackTrace();
		}
		if (!pingFlag) {
			logger.info("引擎连接失败！");
			params.put("msg", "引擎连接失败！");
			return params;
		}
		String info = this.rptEngineBS.redoTask(instanceId,dataDate,taskType, indexType);
		params.put("msg", info);
		return params;
	}
	/**
	 * 手工运行任务
	 */
	@RequestMapping(value = "/runTask", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> runTask(String instanceId,String dataDate,String taskType) {
		Map<String, Object> params = Maps.newHashMap();
		boolean pingFlag = true;
		try {
			pingFlag = CommandRemote.testConnection(CommandRemoteType.INDEX);
		} catch (Throwable e1) {
			pingFlag = false;
			e1.printStackTrace();
		}
		if (!pingFlag) {
			logger.info("引擎连接失败！");
			params.put("msg", "引擎连接失败！");
			return params;
		}
		String info = this.rptEngineBS.runTask(instanceId,dataDate,taskType);
		params.put("msg", info);
		return params;
	}
	/**
	 * 手工停止任务
	 */
	@RequestMapping(value = "/stopTask", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> stopTask(String taskNo) {
		Map<String, Object> params = Maps.newHashMap();
		boolean pingFlag = true;
		try {
			pingFlag = CommandRemote.testConnection(CommandRemoteType.INDEX);
		} catch (Throwable e1) {
			pingFlag = false;
			e1.printStackTrace();
		}
		if (!pingFlag) {
			logger.info("引擎连接失败！");
			params.put("msg", "引擎连接失败！");
			return params;
		}
		String info = this.rptEngineBS.stopTask(taskNo);
		params.put("msg", info);
		return params;
	}

	@RequestMapping(value = "/getRptTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getRptTree(String searchNm, String rptNum) {
		// 展示行内报表树，展示invalid报表，展示树根节点
		PropertiesUtils pUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String rptType = pUtils.getProperty("rptType");
		List<String> templateId = new ArrayList<String>();
		templateId.add(GlobalConstants4plugin.RPT_TMP_TYPE_CELL);
		templateId.add(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V);
		templateId.add(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H);
		templateId.add(GlobalConstants4plugin.RPT_TMP_TYPE_CROSS);
		templateId.add(GlobalConstants4plugin.RPT_TMP_TYPE_COM);
		return this.rptMgrBS
				.getRptTreeSync(
						StringUtils.isEmpty(rptType) ? GlobalConstants4plugin.RPT_EXT_TYPE_BANK
								: rptType, GlobalConstants4plugin.RPT_TYPE_DESIGN,
						searchNm, true, true, templateId, rptNum, true, null,null);
	}

	public RptEngineBS getRptEngineBS() {
		return rptEngineBS;
	}

	public void setRptEngineBS(RptEngineBS rptEngineBS) {
		this.rptEngineBS = rptEngineBS;
	}
	
	/**
	 * 删除报表跑数信息
	 * @param params
	 */
	@RequestMapping(value = "/deleteInstance", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteInstance(String taskNos) {
		Map<String, Object> params = Maps.newHashMap();
		this.rptEngineBS.deleteInstance(taskNos);
		params.put("msg", "ok");
		return params;
	}

	/**
	 * 校验报表是否已提交
	 * @param dataDate
	 * @param checkedRptIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getRptSts", method = RequestMethod.POST)
	public Map<String, Object> getRptSts(String dataDate,String checkedRptIds){
		return this.rptEngineBS.getRptSts(dataDate, checkedRptIds);
	}
}