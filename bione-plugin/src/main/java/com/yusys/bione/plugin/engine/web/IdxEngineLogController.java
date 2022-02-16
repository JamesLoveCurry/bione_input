package com.yusys.bione.plugin.engine.web;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.engine.service.IdxEngineBS;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
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
@RequestMapping("/report/frame/enginelog/idx")
public class IdxEngineLogController extends BaseController {
	@Autowired
	private IdxInfoBS idxInfoBs;

	@Autowired
	private IdxEngineBS idxEngineBS;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/engine/engine-log-idx-index");
	}

	/**
	 * 构造引擎指标状态展示列表
	 */
	@RequestMapping(value = "/getEngineIdxStsList.*")
	@ResponseBody
	public Map<String, Object> getEngineIdxStsList(Pager pager) {
		Map<String, Object> map = Maps.newHashMap();
		List<String> taskTypes = new ArrayList<String>();
		taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDX);
		map.put("taskTypes", taskTypes);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> authIdxIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
			if(authIdxIds != null && authIdxIds.size() > 0){
				map.put("authIdxIds", SplitStringBy1000.change(authIdxIds));
			}else{
				map.put("authIdxIds", "-999");
			}
		}
		SearchResult<RptTaskInstanceInfo> searchResult = this.idxEngineBS
				.getEngineIdxStsList(pager, map);
		Map<String, Object> rowData = Maps.newHashMap();
		rowData.put("Rows", searchResult.getResult());
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
	
	/**
	 * 自动刷新获取引擎状态
	 */
	@RequestMapping(value = "/getEngineIdxPendingSts")
	public Map<String, Object> getEngineIdxPendingSts() {
		Map<String, Object> returnMap = Maps.newHashMap();
		List<RptTaskInstanceInfo> pendingCount = this.idxEngineBS.getEngineIdxPendingSts();
		returnMap.put("msg", "unExsi");
		if(null != pendingCount && pendingCount.size() > 0){
			returnMap.put("msg", "exsi");
		}
		return returnMap;
	}


	/**
	 * 指标引擎计算状态信息新增页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/newIdxEngineLog")
	public ModelAndView newEngineBatch() {
		return new ModelAndView("/plugin/engine/engine-log-idx-new");
	}
	
	/**
	 * 影响指标引擎计算状态信息新增页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/addDependencyIdx")
	public ModelAndView addDependencyIdx() {
		return new ModelAndView("/plugin/engine/engine-log-dependencyidx-new");
	}

	/***
	 * 检查所选的指标是否在执行任务
	 * 
	 * @param checkedIndexIds
	 * @return
	 */
	@RequestMapping(value = "/isSelectedIndexRunning.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> isSelectedIndexRunning(String checkedIndexIds,
			String dataDate) {
		Map<String, Object> params = Maps.newHashMap();
		List<String> checkedIndexIdList = Lists.newArrayList();
		List<String> engineIdxStsList = Lists.newArrayList();
		engineIdxStsList.add(GlobalConstants4plugin.RPT_ENGINE_REPORT_STS_RUN);
		engineIdxStsList.add(GlobalConstants4plugin.RPT_ENGINE_REPORT_STS_WAIT);
		String[] checkedIndexIdArr = StringUtils.split(checkedIndexIds, ',');
		for (String checkedIndexId : checkedIndexIdArr) {
			checkedIndexIdList.add(checkedIndexId);
		}
		if (checkedIndexIdList.size() == 0) {
			checkedIndexIdList.add(" ");
		}
		List<List<String>> indexNosParam = new ArrayList<List<String>>();
		if(checkedIndexIdList.size() > 1000){
			int index = 0;
			int remain = checkedIndexIdList.size();
			while(remain > 1000){
				indexNosParam.add(checkedIndexIdList.subList(index, index+1000));
				index += 1000;
				remain -= 1000;
			}
			if(index < checkedIndexIdList.size()){
				indexNosParam.add(checkedIndexIdList.subList(index, checkedIndexIdList.size()));
			}
		}else{
			indexNosParam.add(checkedIndexIdList);
		}
		params.put("nos", indexNosParam);
		params.put("sts", engineIdxStsList);
		params.put("dataDate", dataDate);
		List<RptIdxInfo> idxInfos = this.idxEngineBS
				.getRptIdxInfosByParams(params);
		if (idxInfos.size() == 0) {
			params.put("msg", "1");
		} else {
			String indexNames = "";
			for (int i = 0; i < idxInfos.size(); i++) {
				RptIdxInfo info = idxInfos.get(i);
				indexNames += info.getIndexNm() + ",";
			}
			if (indexNames.length() > 0) {
				indexNames = indexNames.substring(0, indexNames.length() - 1);
			}
			params.put("msg", "0");
			params.put("indexNames", indexNames);
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
		return this.idxEngineBS.exeStsList();
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
	 * 新增或者修改引擎指标信息
	 */
	@RequestMapping(value = "/saveOrUpdateEngineIdxSts.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveOrUpdateEngineIdxSts(String dataDate,
			String isReRunsal, String checkedIndexIds) {
		String step = "step0";
		Map<String, Object> params = Maps.newHashMap();
		Map<String, Object> map = Maps.newHashMap();
		map.put("dataDate", dataDate);
		map.put("isReRunsal", isReRunsal);
		map.put("checkedIndexIds", checkedIndexIds);
		boolean  pingFlag = true;
		try {
			pingFlag  =  CommandRemote.testConnection( CommandRemoteType.INDEX);
		} catch (Throwable e1) {
			pingFlag  = false;
			e1.printStackTrace();
		}
		if (!pingFlag) {
			logger.info("引擎连接失败！");
			params.put("msg", "引擎连接失败！");
			return params;
		}
		try {
			step = "step1";
			// 发送socket命令进行数据重跑
			step = "step2";
			this.idxEngineBS.runData(map);
			params.put("msg", "启动成功！");
		} catch (Exception e) {
			if (step == "step2") {
				logger.info("指标引擎跑数失败，状态置为[执行失败]！");
				map.put("fail", "fail");
				try {
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.info("状态置为[执行失败]操作失败！");
				}
			}else{
				logger.info("状态置为[等待执行]操作失败！");
			}
			params.put("msg", "启动失败！");
			e.printStackTrace();
		}
		return params;
	}
	
	/**
	 * 指标引擎详细信息页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/detailLog")
	public ModelAndView detailLog(String taskNo, String dataDate, String instanceId, String indexType) {
		ModelMap map = new ModelMap();
		map.put("taskNo", StringUtils2.javaScriptEncode(taskNo));
		map.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		map.put("instanceId", StringUtils2.javaScriptEncode(instanceId));
		String url = "/plugin/engine/engine-log-idx-view";
		if(StringUtils.isNotBlank(indexType)){ //基础指标进行影响跑数
			if(GlobalConstants4plugin.ROOT_INDEX.equals(indexType) || GlobalConstants4plugin.GENERIC_INDEX.equals(indexType)
					|| GlobalConstants4plugin.GENERIC_INDEX.equals(indexType)){
				url = "/plugin/engine/engine-log-dependencyidx-view";
			}
		}
		return new ModelAndView(url, map);
	}
	
	/**
	 * 构造指标引擎信息列表
	 */
	@RequestMapping(value = "/getEngineChildIdxStsList.*")
	@ResponseBody
	public Map<String, Object> getEngineChildIdxStsList(Pager pager,String taskNo, String instanceId) {
		SearchResult<RptTaskInstanceInfo> searchResult = this.idxEngineBS
				.getEngineChildIdxStsList(pager,taskNo, instanceId);
		Map<String, Object> rowData = Maps.newHashMap();
		List<RptTaskInstanceInfo> list = searchResult.getResult();
		rowData.put("Rows", list);
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
}