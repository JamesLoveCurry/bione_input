package com.yusys.biapp.input.task.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.biapp.input.task.entity.RptTskNodeIns;
import com.yusys.biapp.input.task.service.DeployTaskOperBS;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/rpt/input/taskoper")
public class DeployTaskOperController {

	@Autowired
	private DeployTaskOperBS deployTaskOperBS;
	

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/input/task/task-oper-index";
	}

	@RequestMapping(value = "/fillInTaskIndex", method = RequestMethod.GET)
	public ModelAndView fillInTaskIndex() {
		return new ModelAndView("/input/task/task-oper-index", "taskIndexType", "01");
	}
	
	/**
	 * 单列表补录任务列表页
	 * 
	 * @param taskExeObjType
	 * @param taskIndexType
	 * @return
	 */
	@RequestMapping(value = "/needDealTask", method = RequestMethod.GET)
	public ModelAndView needDealTask(String taskIndexType) {
		if(taskIndexType != null && taskIndexType.equals("03")){
			return new ModelAndView("/report/frs/rptsubmit/rpt-submit-child-index", "orgTypes", "02,03");
		} 
		taskIndexType = taskIndexType == null ? "01" : StringUtils2.javaScriptEncode(taskIndexType);
		return new ModelAndView("/input/task/task-needdeal-index", "taskIndexType", taskIndexType);
	}

	@RequestMapping(value = "/approveTaskIndex", method = RequestMethod.GET)
	public ModelAndView approveTaskIndex() {
		return new ModelAndView("/input/task/task-oper-index", "taskIndexType", "02");
	}

	@RequestMapping(value = "/needbackIndex", method = RequestMethod.GET)
	public ModelAndView needbackIndex() {
		return new ModelAndView("/input/task/task-back-index", "taskIndexType", "01");
	}
	@RequestMapping(value = "/monitorTaskIndex", method = RequestMethod.GET)
	public ModelAndView monitorTaskIndex() {
		return new ModelAndView("/input/task/task-monitor-index");
	}

	@RequestMapping(value = "/mytask", method = RequestMethod.GET)
	public ModelAndView mytask() {
		return new ModelAndView("/input/task/task-mytask");
	}
	/*
	 * /** 获取服务器用户
	 * 
	 * @param pager
	 * 
	 * @param serverId
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTaskList", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getTaskList(Pager pager,	String taskExeObjType ,String taskIndexType) {
		
		return deployTaskOperBS.getCurrenTaskByUserInfo( pager,	taskExeObjType,taskIndexType);
	}
	@RequestMapping(value = "/initOperTask", method = RequestMethod.GET)
	public ModelAndView initOperTask(String  taskInstanceId,String showType,String taskNodeInstanceId,String taskIndexType,String taskTypeList) {
		ModelMap mm = new ModelMap();
		try {
			taskInstanceId = URLDecoder.decode(URLDecoder.decode(taskInstanceId,"UTF-8"),"UTF-8");
			taskNodeInstanceId = URLDecoder.decode(URLDecoder.decode(taskNodeInstanceId,"UTF-8"),"UTF-8");
			showType = URLDecoder.decode(URLDecoder.decode(showType,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		mm.put("taskInstanceId", StringUtils2.javaScriptEncode(taskInstanceId));
		mm.put("taskNodeInstanceId", StringUtils2.javaScriptEncode(taskNodeInstanceId));
		mm.put("showType", StringUtils2.javaScriptEncode(showType));
		mm.put("taskIndexType", StringUtils2.javaScriptEncode(taskIndexType));
		mm.put("taskTypeList", StringUtils2.javaScriptEncode(taskTypeList));
		mm.put("canReOpen", deployTaskOperBS.canReOpen(taskInstanceId));
		return new ModelAndView("/input/task/task-oper-content", mm);
		
	}
	
	@RequestMapping(value = "/initOperLog", method = RequestMethod.GET)
	public ModelAndView initOperLog(String  taskInstanceId) {
		taskInstanceId = StringUtils2.javaScriptEncode(EncodeUtils.urlDecode(EncodeUtils.urlDecode(taskInstanceId)));
		return new ModelAndView("/input/task/task-oper-log", "taskInstanceId", taskInstanceId);
	}

	/*
	 * /** 获取服务器用户
	 * 
	 * @param pager
	 * 
	 * @param serverId
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getTaskLog", method = RequestMethod.POST)
	public Map<String,Object> getTaskLog(String taskInstanceId) {
		
		return deployTaskOperBS.getTaskInsLog(taskInstanceId);
	}

	@RequestMapping(value = "/getListLogDetail", method = RequestMethod.POST)
	public Map<String,Object>  getListLogDetail(String logId){
		return deployTaskOperBS.getListLogDetail(logId);
	}
	

	@RequestMapping(value = "/getTaskOperInfo", method = RequestMethod.POST)
	public Map<String,Object> getTaskOperInfo(String taskInstanceId) {
		
		return deployTaskOperBS.getTaskOperInfo(taskInstanceId);
	}
	@RequestMapping(value = "/submitTask", method = RequestMethod.POST)
	@ResponseBody
	public String  submitTask(String taskInstanceId,String taskNodeInstanceId,String templateId) {
		
		return deployTaskOperBS.submitTask(taskInstanceId,taskNodeInstanceId,templateId);
	}
	
	
	@RequestMapping(value = "/banchSubmitTask", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String>  submitTask(String submitTasks) {
		Map<String,String> resMap = Maps.newHashMap();
		resMap.put("flag", "true");
		JSONArray taskArray = JSON.parseArray(submitTasks);
		Set<String> idSet = Sets.newHashSet();
		
		int error = 0;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < taskArray.size(); i++){
			JSONObject task = taskArray.getJSONObject(i);
			sb.delete(0, sb.length());
			sb.append(task.getString("taskInstanceId"));
			sb.append(task.getString("taskInstanceId"));
			sb.append(task.getString("templateId"));
			
			if(idSet.add(sb.toString())){
				String msg = deployTaskOperBS.submitTask(task.getString("taskInstanceId"),
						task.getString("taskNodeInstanceId"),task.getString("templateId"));
				if(!"1".equals(msg)){
					error++;
				}
			}
		}
		if(error != 0){
			resMap.put("error", String.valueOf(error));
		}
		
		return resMap;
	}
	
	/**
	 * 批量解锁
	 * @param openLockTasks
	 * @return
	 */
	@RequestMapping(value = "/batchOpenLock", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String>  batchOpenLock(String openLockTasks) {
		Map<String,String> resMap = Maps.newHashMap();
		resMap.put("flag", "true");
		JSONArray taskArray = JSON.parseArray(openLockTasks);
		Set<String> idSet = Sets.newHashSet();
		int error = 0;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < taskArray.size(); i++){
			JSONObject task = taskArray.getJSONObject(i);
			sb.delete(0, sb.length());
			sb.append(task.getString("taskInstanceId"));
			sb.append(task.getString("taskInstanceId"));
			sb.append(task.getString("templateId"));
			
			if(idSet.add(sb.toString())){
				String msg = deployTaskOperBS.openLockTask(task.getString("taskInstanceId"),
						task.getString("taskNodeInstanceId"),task.getString("templateId"));
				if(!"1".equals(msg)){
					error++;
				}
			}
		}
		if(error != 0){
			resMap.put("error", String.valueOf(error));
		}
		
		return resMap;
	}
	
	

	@RequestMapping(value = "/rebutTask", method = RequestMethod.POST)
	@ResponseBody
	public String  rebutTask(String taskInstanceId,String taskNodeInstanceId,String templateId,String rebutNode) {
		
		return deployTaskOperBS.rebutTask(taskInstanceId, taskNodeInstanceId,templateId,rebutNode);
	}

	@RequestMapping(value = "/reOpenTask", method = RequestMethod.POST)
	@ResponseBody
	public boolean  reOpenTask(String templateId,String taskInstanceId) {
		
		return deployTaskOperBS.reOpenTask(templateId,taskInstanceId);
	}

	@RequestMapping(value = "/reWrite", method = RequestMethod.POST)
	@ResponseBody
	public String  reWrite(String taskInstanceId) {
		
		return deployTaskOperBS.reWrite(taskInstanceId);
	}

	@RequestMapping(value = "/submitMultiTask", method = RequestMethod.POST)
	@ResponseBody
	public String  submitMultiTask(String taskInstanceIds,String taskNodeInstanceIds,String templateId) {
		
		return deployTaskOperBS.submitMultiTask(taskInstanceIds,taskNodeInstanceIds,templateId);
	}
	

	@RequestMapping(value = "/rebutMultiTask", method = RequestMethod.POST)
	@ResponseBody
	public String  rebutMultiTask(String taskInstanceIds,String taskNodeInstanceIds,String templateId) {
		
		return deployTaskOperBS.rebutMultiTask(taskInstanceIds,taskNodeInstanceIds,templateId);
	}
	

	@RequestMapping(value = "/submitBackTask", method = RequestMethod.POST)
	@ResponseBody
	public String  submitBackTask(String taskInstanceIds,String templeId) {
		
		return deployTaskOperBS.backMultiTask(taskInstanceIds, templeId);
	}
	

	/**
	 * 删除任务
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteTask", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteTask(String taskInstanceIds) {
		return this.deployTaskOperBS.deleteTask(taskInstanceIds);
	}

	@RequestMapping(value = "/initIndexLog", method = RequestMethod.GET)
	public ModelAndView initIndexLog(String  logId) {
		logId = StringUtils2.javaScriptEncode(logId);
		return new ModelAndView("/input/task/task-listlog-detail", "logId", logId);
		
	}
	
	@RequestMapping(value = "/taskNodes/{tasInsId}", method = RequestMethod.GET)
	public ModelAndView getTaskNodesWin(@PathVariable("tasInsId")String  tasInsId, String flag) {
		ModelMap mm = new ModelMap();
		mm.put("taskInstanceId", StringUtils2.javaScriptEncode(tasInsId));
		mm.put("flag", StringUtils2.javaScriptEncode(flag));		
		return new ModelAndView("/input/task/task-oper-content-selTskNode", mm);
	}
	@RequestMapping(value = "/taskNodes/selectjson.*", method = RequestMethod.POST)
	@ResponseBody
	public List<RptTskNodeIns> getTaskNodes(String  tasInsId) {
		return deployTaskOperBS.getTaskNodes(tasInsId);
	}

	@ResponseBody
	@RequestMapping("/resetTask")
	public Map<String, Object> resetTask(String taskInstanceId, String templateId){
		Map<String, Object> result = new HashMap<>();
		result.put("status", "success");
		try {
			Map<String, Object> taskOperInfo = deployTaskOperBS.getTaskOperInfo(taskInstanceId);
			String rebutNode = "";
			String taskNodeInstanceId = "";
			List<RptTskNodeIns> list = (List<RptTskNodeIns>) taskOperInfo.get("Rows");
			for (RptTskNodeIns rptTskNodeIns : list) {
				if("01".equals(rptTskNodeIns.getNodeType())){
					rebutNode = rptTskNodeIns.getTaskNodeInstanceId();
				}
				if("1".equals(rptTskNodeIns.getSts())){
					taskNodeInstanceId = rptTskNodeIns.getTaskNodeInstanceId();
				}
			}

			deployTaskOperBS.rebutTask(taskInstanceId, taskNodeInstanceId, templateId, rebutNode);
		} catch (Exception e){
			e.printStackTrace();
			result.put("status", "fail");
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
}
