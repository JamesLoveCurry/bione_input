package com.yusys.bione.plugin.engine.web;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.engine.entity.RptEngineRefreshInfo;
import com.yusys.bione.plugin.engine.service.EngineRefreshBS;
import com.yusys.bione.plugin.engine.service.FrsRptEngineBS;
import com.yusys.bione.plugin.engine.service.IdxEngineBS;
import com.yusys.bione.plugin.engine.util.EngineUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/frs/frame/engineRefresh")
public class FrsEngineRefreshController{

	@Autowired
	private FrsRptEngineBS rptEngineBS;

	@Autowired
	private IdxEngineBS idxEngineBS;

	@Autowired
	private EngineRefreshBS engineRefreshBS;

	/**
	 * 引擎辅助功能
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/engine/engine-refresh-index");
	}

	/*@RequestMapping("/refresh")
	public void engineRefresh(String cfgType,String nodeType,String dsType,String modelType,String indexType){
		if(cfgType!=null&&nodeType!=null&&dsType!=null&&modelType!=null&&indexType!=null){
			EngineUtils.refreshAllCache();
		}else{
			if(cfgType!=null){
				EngineUtils.refreshIdxCfgCache();
			}
			if(nodeType!=null){
				EngineUtils.refreshNodeCfgCache();
			}
			if(dsType!=null){
				EngineUtils.refreshDsCfgCache();
			}
			if(modelType!=null){
				EngineUtils.refreshModelCfgCache();
			}
			if(indexType!=null){
				EngineUtils.refreshIndexCfgCache();
			}
		}

	}*/

	@RequestMapping("/refresh")
	public void engineRefresh(String refrType){
			if(refrType.equals("noderef")){
				EngineUtils.refreshNodeCfgCache();//节点刷新
			}
			if(refrType.equals("dsref")){
				EngineUtils.refreshDsCfgCache();//数据源刷新
			}
			if(refrType.equals("confref")){
				EngineUtils.refreshIdxCfgCache();//配置刷新
			}
	}

	/**
	 * 刷新类型列表
	 *
	 * @return
	 */
	@RequestMapping(value = "/taskTypeList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> taskTypeList() {
		return this.engineRefreshBS.taskTypeList();
	}

	//展示主引擎刷新任务列表
	@RequestMapping("/getEngineRefrList.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager){
		SearchResult<RptEngineRefreshInfo> searchResult=engineRefreshBS.getEngineRefrList(pager.getPageFirstIndex(),pager.getPagesize(),  pager.getSortname(),pager.getSortorder(),pager.getSearchCondition());
		Map<String, Object> map = Maps.newHashMap();
		map.put("Rows",searchResult.getResult());
		map.put("Total", searchResult.getTotalCount());
		return map;
	}

	//跳转主引擎刷新任务页面
	@RequestMapping(value="detailLog")
	@ResponseBody
	public ModelAndView detailLog(String taskNo){
		ModelMap map=new ModelMap();
		map.put("taskNo", taskNo);
		return new ModelAndView("/plugin/engine/engine-refresh-detaillog",map);
	}



	//主引擎刷新任务页面详情
	@RequestMapping(value="getDetail")
	@ResponseBody
	public Map<String,Object> getDetail(String taskNo){
		Map<String,Object> result=Maps.newHashMap();
		result.put("baseData",this.engineRefreshBS.getDetailEngRefr(taskNo));
		return result;
	}

	//子引擎刷新任务展示列表
	@RequestMapping(value="getEngRefrChild.*")
	@ResponseBody
	public Map<String,Object> getEngRefrChild(Pager page,String parentTaskId){
		SearchResult<RptEngineRefreshInfo> searchResult=this.engineRefreshBS.getEngRefrChild(page,parentTaskId);
		Map<String,Object> map=Maps.newHashMap();
		map.put("Rows", searchResult.getResult());
		map.put("Total", searchResult.getTotalCount());
		return map;
	}

	//跳转子引擎刷新任务页面
	@RequestMapping(value = "detailChildLog")
	@ResponseBody
	public ModelAndView detailChildLog(String taskNo){
		Map<String,Object> map=Maps.newHashMap();
		map.put("taskNo", taskNo);
		return new ModelAndView("/plugin/engine/engine-refresh-detailChildLog",map);
	}

	//子引擎刷新任务详情
	@RequestMapping(value = "/getDetailChild")
	@ResponseBody
	public Map<String,Object> getDetailChild(String taskNo){
		Map<String,Object>map=Maps.newHashMap();
		RptEngineRefreshInfo info = this.engineRefreshBS.getDetailChild(taskNo);
		map.put("baseData", info);
		return map;
	}

	//删除引擎刷新任务
	@RequestMapping(value = "deleteEngineRefresh")
	@ResponseBody
	public Map<String,Object> deleteEngineRefresh(String taskNos){
		return this.engineRefreshBS.deleteEngineRefresh(taskNos);
	}


	@RequestMapping("/force")
	public void forchSuccess(String indexType,String reportType,String dataDate){
		Map<String,Object> params  =  Maps.newHashMap();
		params.put("sts", GlobalConstants4plugin.RPT_ENGINE_IDX_STS_FINISH);
		params.put("dataDate",dataDate);
		if(indexType!=null){
			idxEngineBS.updateRptEngineIdxSts(params);
		}
		if(reportType!=null){
			rptEngineBS.updateRptEngineReportSts(params);
		}
	}
	/**
	 * 校验状态强置
	 * @param dataDate
	 */
	@RequestMapping("/forceCheckeSts")
	public void forceCheckeSts(String dataDate){
		rptEngineBS.updateRptEngineReportCheckeSts(dataDate);
	}
	/**
	 * 汇总状态强置
	 * @param dataDate
	 */
	@RequestMapping("/forceSumSts")
	public void forceSumSts(String dataDate){
		rptEngineBS.updateRptEngineReportSumSts(dataDate);
	}
	
	/**
	 * 获取正在进行中的任务
	 * @param refrType
	 */
	@RequestMapping("/getRefreshSts")
	public Map<String, Object> getRefreshSts(String refrType){
		Map<String, Object> result = new HashMap<String, Object>();
		List<RptEngineRefreshInfo> list = rptEngineBS.getRefreshSts(refrType);
		if(list != null && list.size() > 0){
			result.put("flag", false);
			result.put("msg", "有正在执行中的任务，请等待刷新完成！");
		}else{
			result.put("flag", true);
		}
		return result;
	}
}
