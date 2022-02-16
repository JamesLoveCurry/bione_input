package com.yusys.biapp.input.task.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.biapp.input.task.service.InputTskMonitorBS;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.frame.base.web.BaseController;

@Controller
@RequestMapping("/input/task/monitor")
public class InputTskMonitorController extends BaseController{
	
	@Autowired
	private InputTskMonitorBS monitorBS;
	
	@RequestMapping( method = RequestMethod.GET)
	public ModelAndView index() {
		ModelMap mm = new ModelMap();
		return new ModelAndView("/input/task/monitor/task-monitor-index", mm);
	}
	
	/*
	 * @param pager
	 * @return
	 */
	@RequestMapping(value = "/getTaskList", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getTaskList(Pager pager) {
		return this.monitorBS.getTaskList(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
	}
	
	@RequestMapping(value = "/operLog", method = RequestMethod.GET)
	public ModelAndView operLog(String  taskInstanceId) {
		ModelMap mm = new ModelMap();
		mm.put("taskInstanceId", taskInstanceId);
		return new ModelAndView("/input/task/monitor/task-monitor-log", mm);
	}
	
	@RequestMapping(value = "/getTaskOperList", method = RequestMethod.POST)
	public Map<String,Object> getTaskOperList(String taskInstanceId,String taskNodeInstanceId) {
		return this.monitorBS.getTaskOperList(taskInstanceId,taskNodeInstanceId);
	}
	
	@RequestMapping(value = "/getTaskLogList", method = RequestMethod.POST)
	public Map<String,Object> getTaskLogList(String taskInstanceId) {
		return this.monitorBS.getTaskLogList(taskInstanceId);
	}
	
	@RequestMapping(value = "/operDetail", method = RequestMethod.GET)
	public ModelAndView operDetail(String  taskInstanceId,String taskNodeInstanceId ,String taskIndexType,String taskTypeList,String showType) {
		ModelMap mm = new ModelMap();
		mm.put("taskInstanceId", taskInstanceId);
		mm.put("taskNodeInstanceId", taskNodeInstanceId);
		mm.put("taskIndexType", taskIndexType);
		mm.put("taskIndexType", taskIndexType);
		mm.put("taskTypeList", taskTypeList);
		return new ModelAndView("/input/task/monitor/task-monitor-detail", mm);
	}
	
	@RequestMapping(value = "/deleteTask/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String,Object> deleteTask(@PathVariable("id") String taskInstanceId) {
		return this.monitorBS.deleteTask(taskInstanceId);
	}

}
