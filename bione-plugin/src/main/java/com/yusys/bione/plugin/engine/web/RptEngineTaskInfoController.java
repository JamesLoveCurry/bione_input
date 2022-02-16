package com.yusys.bione.plugin.engine.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.engine.service.RptEngineTaskInfoBS;

@Controller
@RequestMapping("rpt/frame/rptenginetsk")
public class RptEngineTaskInfoController extends BaseController {
	
	@Autowired
	RptEngineTaskInfoBS engineTaskInfoBS;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/autotask/rpt-engine-index");
	}
	
	@RequestMapping(value = "module", method = RequestMethod.GET)
	public ModelAndView modulePage() {
		return new ModelAndView("/plugin/autotask/rpt-engine-task-module");
	}
	
	@RequestMapping(value = "report", method = RequestMethod.GET)
	public ModelAndView reportPage() {
		return new ModelAndView("/plugin/autotask/rpt-engine-task-report");
	}
	
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public ModelAndView indexPage(String tskType) {
		tskType = StringUtils2.javaScriptEncode(tskType);
		return new ModelAndView("/plugin/autotask/rpt-engine-task-index", "tskType", tskType);
	}
	
	@RequestMapping(value="moduleList.*",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> moduleList(Pager pager){
		return this.engineTaskInfoBS.module(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
	}
	
	@RequestMapping(value="reportList",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> reportList(Pager pager){
		return this.engineTaskInfoBS.report(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
	}
	
	@RequestMapping(value="indexList",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> indexList(Pager pager, String tskType){
		return this.engineTaskInfoBS.index(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition(), tskType);
	}
	
	@RequestMapping(value="update",method = RequestMethod.POST)
	public Map<String, String> update(String selectedObj){
		return this.engineTaskInfoBS.update(selectedObj);
	}
	
	/**
	 * 报表跑数任务新增页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/newRptTask")
	public ModelAndView newRptTaskBatch(String taskType) {
		taskType = StringUtils2.javaScriptEncode(taskType);
		return new ModelAndView("/plugin/autotask/rpt-engine-report-new", "taskType", taskType);
	}
	/**
	 * 模型跑数任务新增页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/newModuleTask")
	public ModelAndView newModuleTaskBatch(String taskType) {
		taskType = StringUtils2.javaScriptEncode(taskType);
		return new ModelAndView("/plugin/autotask/rpt-engine-module-new", "taskType", taskType);
	}
	/**
	 * 指标跑数任务新增页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/newIndexTask")
	public ModelAndView newIndexTaskBatch(String taskType) {
		taskType = StringUtils2.javaScriptEncode(taskType);
		return new ModelAndView("/plugin/autotask/rpt-engine-index-new", "taskType", taskType);
	}
	
	/**
	 * 新增或者修改任务信息
	 */
	@RequestMapping(value = "/saveOrUpdateAutoTask.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveOrUpdateAutoTask(String dataDate, String checkedTaskIds,String taskType) {
		Map<String, Object> params = this.engineTaskInfoBS.saveOrUpdateAutoTask(dataDate,checkedTaskIds,taskType);
		return params;
	}
	
	/**
	 * 获取数据模型树
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getRowModuleTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getRowModuleTree(String searchNm,String dsId) {
		List<CommonTreeNode> nodes = this.engineTaskInfoBS.getRowModuleTree(this.getContextPath(),searchNm,dsId,true);
		if (nodes == null) {
			nodes = new ArrayList<CommonTreeNode>();
		}
		return nodes;
	}
}

