/**
 * 
 */
package com.yusys.biapp.input.input.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.common.InputConstants;
import com.yusys.biapp.input.data.service.AuthRecordBS;
import com.yusys.biapp.input.data.service.DataStateBS;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.input.service.RptTskExeobjRelBS;
import com.yusys.biapp.input.input.service.TaskCaseBS;
import com.yusys.biapp.input.inputTable.entity.RptListDataloadSqlInfo;
import com.yusys.biapp.input.inputTable.service.InputTableBS;
import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.biapp.input.logs.service.ValidateLogBS;
import com.yusys.biapp.input.rule.common.DataFormatValidate;
import com.yusys.biapp.input.rule.common.ValidateUtils;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.service.DataRuleBS;
import com.yusys.biapp.input.task.entity.RptTskExeobjRel;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.task.entity.RptTskNodeIns;
import com.yusys.biapp.input.task.repository.TaskOperDao;
import com.yusys.biapp.input.task.service.DeployTaskBS;
import com.yusys.biapp.input.task.service.DeployTaskOperBS;
import com.yusys.biapp.input.task.service.TaskLockBS;
import com.yusys.biapp.input.task.vo.DataLoadInfoVO;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.entity.RptInputLstTempleRule;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.biapp.input.template.service.TempleFieldBS;
import com.yusys.biapp.input.template.service.TempleRuleInfoBS;
import com.yusys.biapp.input.utils.ExcelConstants;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jdbc.entity.BioneTableMetaData;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.FormatUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

/**
 * <pre>
 * Title: 程序名称
 * Description: 功能描述
 * </pre>
 * 
 * @author xuguangyuan xugy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */
@Controller
@RequestMapping("/rpt/input/taskcase")
public class TaskCaseController extends BaseController {
	private final String PATH = "/input/input/";

	private final Log logger = LogFactory.getLog(TaskCaseController.class);
	
	@Autowired
	private DeployTaskBS deployTaskBS;
	@Autowired
	private DataSourceBS dataSourceBS;
	@Autowired
	private TempleBS templeBS;
	@Autowired
	private TaskCaseBS taskCaseBS;
	@Autowired
	private TaskLockBS taskLockBS;
	@Autowired
	private RptTskExeobjRelBS rptTskExeobjRelBS;
	@Autowired
	private ValidateLogBS validateLogBS;
//	@Autowired
//	private LogBS logBS;
	@Autowired
	private DataFormatValidate dataFormatValidate;
	@Autowired
	private DataUtils dataUtils;
	@Autowired
	private AuthRecordBS authRecordBS;
	@Autowired
	private DataStateBS dataStateBS;
	@Autowired
	private ExcelConstants excelConstants;
	@Autowired
	private TempleRuleInfoBS templeRuleInfoBS;
	@Autowired
	private DataRuleBS dataRuleBS;
	@Autowired
	private TempleFieldBS templeFieldBS;
	@Autowired
	DeployTaskOperBS deployTaskOperBS;
	@Autowired
	private TaskOperDao taskOperDao;
	@Autowired
	private InputTableBS tableBS;

	/**
	 * 数据补录首页
	 * 
	 * @param caseId
	 * @param click
	 * @param modelMap
	 * @return
	 */
	public ModelAndView index(String caseId, String click, ModelMap modelMap) {
		modelMap.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		modelMap.addAttribute("click", StringUtils2.javaScriptEncode(click));
		return new ModelAndView(PATH + "task-case-index", modelMap);
	}

	/**
	 * 任务目录树
	 * 
	 * @param searchNm
	 * @param nodeId
	 * @return
	 */
	@RequestMapping(value = "/taskCatalogTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTaskTree(String searchNm, String nodeId) {
		if (searchNm == null && nodeId == null) {
			return deployTaskBS.getRootRptTskInfoNode(InputConstants.APP_CONTEXT_PATH);
		} else {
			return deployTaskBS.getRptTskInfoNode(InputConstants.APP_CONTEXT_PATH, searchNm, nodeId);
		}
	}

	/**
	 * 手工补录界面跳转
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/inputTaskInfo")
	public ModelAndView inputTaskInfo(String templeId, String caseId, String searchTerms,String sqlStr) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		if (StringUtils.isNotBlank(searchTerms)) {
			mm.addAttribute("searchTerms", StringUtils2.javaScriptEncode(searchTerms));
		}
		if (StringUtils.isNotBlank(sqlStr)) {
			mm.addAttribute("sqlStr", StringUtils2.javaScriptEncode(sqlStr));
		}
		return new ModelAndView(PATH + "task-case-info", mm);
	}

	/**
	 * 预览界面跳转
	 * 
	 * @param templeId
	 * @param caseId
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/inputTaskInfoLook")
	public ModelAndView inputTaskInfoLook(String templeId, String caseId, String searchTerms, String orgId, String sqlStr) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		mm.addAttribute("orgId", StringUtils2.javaScriptEncode(orgId));
		if (StringUtils.isNotBlank(searchTerms)) {
			mm.addAttribute("searchTerms", StringUtils2.javaScriptEncode(searchTerms));
		}
		if (StringUtils.isNotBlank(sqlStr)) {
			mm.addAttribute("sqlStr", StringUtils2.javaScriptEncode(sqlStr));
		}
		return new ModelAndView(PATH + "task-case-detail", mm);
	}

	/**
	 * 数据补录excel 表格操作界面跳转
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/downloadPage")
	public ModelAndView downloadPage(String templeId, String caseId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		return new ModelAndView(PATH + "task-case-data", mm);
	}

	/**
	 * 跳转到实例机构设置
	 * 
	 * @return
	 */
	@RequestMapping(value = "/taskCaseOrg")
	public ModelAndView taskCaseOrg(String caseId) {
		caseId = StringUtils2.javaScriptEncode(caseId);
		return new ModelAndView(PATH + "task-case-org", "caseId", caseId);
	}

	/**
	 * 跳转到任务提交或者校验时的机构选择页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/taskSubmitOrg")
	public ModelAndView taskSubmitOrg(String caseId, String templeId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		return new ModelAndView(PATH + "task-submit-org", mm);
	}

	/**
	 * 校验结果页面跳转
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/taskCaseValidateLog")
	public ModelAndView taskCaseValidateLog(String templeId, String caseId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		return new ModelAndView(PATH + "task-case-validate-his", mm);
	}

	/**
	 * 任务记录界面跳转
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/chackAuthRecordInfo")
	public ModelAndView chackAuthRecordInfo(String templeId, String caseId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		return new ModelAndView(PATH + "task-auth-record-detail", mm);
	}

	/**
	 * 
	 * @param templeId
	 * @param caseId
	 * @param look
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/getSearchTerms")
	public ModelAndView getSearchTerms(String templeId, String caseId, String look, String orgId) {
		ModelMap mm = new ModelMap();
//		UdipTemple temp = templeBS.getEntityById(templeId);
//		if(temp != null && StringUtils.isNotBlank(temp.getOrgColumn())&& StringUtils.isNotBlank(temp.getConnLower()) && temp.getConnLower().equals("no")){
//			mm.addAttribute("orgOwn", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
//			mm.addAttribute("orgColumn", temp.getOrgColumn());
//		}else if (temp != null && StringUtils.isNotBlank(temp.getOrgColumn())&& StringUtils.isNotBlank(temp.getConnLower())){
//			mm.addAttribute("orgOwn", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
//		}
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		mm.addAttribute("look", StringUtils2.javaScriptEncode(look));
		return new ModelAndView(PATH + "temple-search-terms", mm);
	}

	/**
	 * 补录自定义查询页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "queryData", method = RequestMethod.GET)
	public ModelAndView indexQueryDate() {
		return new ModelAndView(PATH + "query-data-index");
	}

	/**
	 * 首页界面跳转
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String caseId, String click) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		mm.addAttribute("click", StringUtils2.javaScriptEncode(click));
		return new ModelAndView(PATH + "task-case-index", mm);
	}

	/**
	 * 获取用于数据补录首页grid的数据
	 * 
	 * @param rf
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf, String caseId) {
		List<String> templeIdSet = new ArrayList<String>();
		RptTskIns caseInfo = this.taskCaseBS.getEntityById(caseId);
		if (caseInfo != null) {
			templeIdSet = new ArrayList<String>();
			List<RptTskExeobjRel> templeIdList = this.rptTskExeobjRelBS.findEntityListByProperty("taskId", caseInfo.getTaskId());
			for (RptTskExeobjRel templelist : templeIdList) {
				// 执行对象类型1：是指标，2：是明细
				if (templelist.getId().getExeObjType().equals("2")) {
					templeIdSet.add(templelist.getId().getExeObjId());
				}
			}
		}
		SearchResult<RptInputLstTempleInfo> searchResult = this.templeBS.getTaskCaseTempleList(0, 0, rf.getSortname(), rf.getSortorder(), rf.getSearchCondition(), templeIdSet);
		List<RptInputLstTempleInfo> tempList = searchResult.getResult();

		String[] propVal = { caseId };
		String[] propName = { "taskInstanceId" };
		List<RptTskIns> udipDataState = taskCaseBS.findByPropertys(RptTskIns.class, propName, propVal);
		Map<String, RptTskIns> udipDataStateMap = Maps.newHashMap();
		for (RptTskIns dataState : udipDataState) {
			udipDataStateMap.put(dataState.getExeObjId(), dataState);
		}

		// FIXME 测试任务是否锁定, 原码没读懂锁定原理, 尚未迁移
//		List<UdipTaskTempLock> templock = udipTaskTempLockBS.findEntityListByProperty("caseId", caseId);
//		List<String> templeId = Lists.newArrayList();
//		for(UdipTaskTempLock tasktemp : templock){
//			templeId.add(tasktemp.getTempId());
//		}
//					
//		for(int i =0;i< tempList.size();i++){
//			if(!templeId.isEmpty()){
//				if(templeId.contains(tempList.get(i).getTempleId())){
//					tempList.get(i).setState("0");
//				}
//			}
//			tempList.get(i).setCreator(udipDataStateMap.get(tempList.get(i).getTempleId())!=null?udipDataStateMap.get(tempList.get(i).getTempleId()).getDataState():"未下发");
//		}
		
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", tempList);
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}
	
	@RequestMapping(value = "/saveComment",method = RequestMethod.POST)
	@ResponseBody
	public void saveComment(String templateId,String comment,String uniqueKey ){

		try {
			uniqueKey = URLDecoder.decode(URLDecoder.decode(uniqueKey,"UTF-8"),"UTF-8");
			comment = URLDecoder.decode(URLDecoder.decode(comment,"UTF-8"),"UTF-8");
			taskCaseBS.saveComment(templateId,comment,uniqueKey );
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}

	/**
	 * 获取taskcase数据信息
	 * 
	 * @param templeId
	 * @param caseId
	 * @param orgId
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getTaskCaseInfoList")
	@ResponseBody
	public Map<String, Object> getTaskCaseInfoList(Pager pager,String taskNodeInstanceId,
			String taskInstanceId, String onlineTask, String templeId, String caseId, 
				String searchTerms, String orgId, String sqlStr,String listType) throws UnsupportedEncodingException {
		logger.debug("getTaskCaseInfoList-----0--start："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		Map<String, Map<String, String>> condition = Maps.newHashMap();
		// 统一获取信息
		if (StringUtils.isNotBlank(sqlStr)) {
			sqlStr = URLDecoder.decode(sqlStr, "utf-8");
		}
		Map<String, Object> getMap = this.taskCaseBS.getMap(templeId, caseId,true);
		logger.debug("getTaskCaseInfoList-------1---taskCaseBS.getMap.："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		Map<String, String> columnsType = (Map<String, String>) getMap.get("colNameType");
		if (StringUtils.isNotBlank(searchTerms)) {
			searchTerms = URLDecoder.decode(searchTerms, "utf-8");
			String[] searchStr = searchTerms.split("@@");
			for (int i = 0; i < searchStr.length; i++) {
				String[] paramValue = searchStr[i].split("#@#");
				Map<String, String> columnsValue = Maps.newHashMap();
				columnsValue.put("type", columnsType.get(paramValue[0]));
				columnsValue.put("value", paramValue[1]);
				condition.put(paramValue[0], columnsValue);
			}
		}
		List<String> dataState = Lists.newArrayList();
		if (StringUtils.isNotBlank(onlineTask)) {
			dataState.add(UdipConstants.TASK_STATE_SAVE);
			dataState.add(UdipConstants.TASK_STATE_VALIDATE);
			dataState.add(UdipConstants.TASK_STATE_REFUSE);
			dataState.add(UdipConstants.TASK_STATE_DISPATCH);
			dataState.add(UdipConstants.TASK_STATE_SUBMIT);
			dataState.add(UdipConstants.TASK_STATE_SUCESS);
			dataState.add(UdipConstants.TASK_STATE_REJECT);
		}
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
		List<String> org = this.taskCaseBS.getOrgList(temp, orgId, caseId);
		logger.debug("getTaskCaseInfoList-------2---taskCaseBS.getOrgList.："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		String caseOrNot = caseId;
		String caseData = (String) getMap.get("casedata");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		try {
			int start = pager.getPage() == 0 ? 0 : pager.getPage() - 1;
			if ("1".equals(temp.getAllowInputHist())) {
				caseOrNot = null;
				caseData = null;
			}
			Map<String,Object>queryMap=Maps.newHashMap();
			queryMap.put("taskInstanceId", taskInstanceId);
			Map<String,Object>map = taskOperDao.getRptTskInsInfoWithNode(queryMap);
			logger.debug("getTaskCaseInfoList-------3---taskOperDao.getRptTskInsInfoWithNode.："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
			String orgSql = "";//" and SYS_OPER_ORG = '" + BioneSecurityUtils.getCurrentUserInfo().getOrgNo() + "' ";
			String nodeSql ;
			if(!map.get("NODE_TYPE").equals("01")){
				/*****审批节点,能看到一个流程下所有的数据*****************/
				nodeSql = " and ";
				List<String>allTaskNodeInsIds = deployTaskOperBS.getAllTaskNodeInsIds(taskInstanceId);
				logger.debug("getTaskCaseInfoList-------4---deployTaskOperBS.getAllTaskNodeInsIds.："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				StringBuilder flowNodeBuff = new StringBuilder();
				if(allTaskNodeInsIds!=null&&allTaskNodeInsIds.size()!=0){
					flowNodeBuff.append("  FLOW_NODE_ID IN (");
					boolean isFirst=true;
					for(String id:allTaskNodeInsIds){
						if(!isFirst) flowNodeBuff.append(",");
						else isFirst=false;
						flowNodeBuff.append("'").append(id).append("'");
					}
					flowNodeBuff.append(")");
					nodeSql += flowNodeBuff.toString();
				}
				/*****添加任务过滤    一个流程只能看到自己流程下的数据*****************/
			}else{
				/*****填报节点,能看到一个流程节点下所有的数据*****************/
				nodeSql = "  AND FLOW_NODE_ID ='"+taskNodeInstanceId+"'";
			}
			//添加筛选条件
			StringBuilder searchSql = new StringBuilder();
			if (StringUtils.isNotEmpty(pager.getCondition())) {
				JSONObject json = JSON.parseObject(pager.getCondition());
				JSONArray array = json.getJSONArray("rules");
				for (int i = 0; i < array.size(); i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					String filedName = (String) jsonObject.get("field");
					if (StringUtils.isNotEmpty((String) jsonObject.get("value"))) {
						searchSql.append(" AND ").append(filedName).append(" ").append(jsonObject.get("op")).append(" '%").append(jsonObject.get("value")).append("%'");
					}
				}
			}
			//获取链接
			Connection conn = this.dataSourceBS.getConnection(temp.getDsId());
			int dataCount = dataUtils.getDataCount(conn,(RptInputLstTempleInfo) getMap.get("temp"),
			        condition, sqlStr + orgSql + nodeSql + searchSql.toString(), caseData, dataState.isEmpty() ? null : dataState,
			                org, getMap.get("oper"), caseOrNot);//添加查询条件searchSql
			logger.debug("getTaskCaseInfoList-------5---dataUtils.getDataCount.："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
			/*********需要得到可以更新的数据的主键  cl*******************/
			List<String>colList=(List<String>) getMap.get("colName");
			colList.add("ERROR_MARK");
			colList.add("COMMENTS");
			colList.add("DATAINPUT_ID");
			dataList = dataUtils.getData(conn,null,temp, (List<String>) getMap.get("keyCols"), condition, sqlStr + orgSql + nodeSql + searchSql.toString(), colList, columnsType,
					start * pager.getPagesize(), pager.getPagesize(), caseData, 
						dataState.isEmpty() ? null : dataState, org, getMap.get("oper"), 
								caseOrNot, (Map<String, Map<String, Object>>)getMap.get("libList"),listType,pager);//添加查询条件searchSql
			//关闭链接
			this.dataSourceBS.releaseConnection(null, null, conn);
			logger.debug("getTaskCaseInfoList-------6---dataUtils.getData.："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
			Map<String,Object> dataMap = getDataShow(dataList,colList,taskInstanceId,pager);//数据是否预加载
			List<Map<String, Object>> showList = (List<Map<String, Object>>)dataMap.get("Rows");
			
			//dataList = dataUtils.getData(temp, (List<String>) getMap.get("keyCols"), condition, sqlStr + orgSql, (List<String>) getMap.get("colName"), columnsType, start * pager.getPagesize(), pager.getPagesize(), caseData, dataState.isEmpty() ? null : dataState, org, getMap.get("oper"), caseOrNot);
			/*********需要得到可以更新的数据的主键*******************/
			Map<String, Object> objDefMap = Maps.newHashMap();
			objDefMap.put("Rows", showList);
			objDefMap.put("Total", dataCount + Integer.parseInt(String.valueOf(dataMap.get("Total"))));

			logger.debug("getTaskCaseInfoList-------end："+FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
			return objDefMap;
		} catch (Exception e) {
		    e.printStackTrace();
			return null;
		}
	}
	
	/**
     * 下载excel文件
     * 
     * @param response
     * @param id
     * @throws Exception
     */
    @RequestMapping(value = "/excel/{taskId}")
    public void downExcelByTaskId(HttpServletResponse response, @PathVariable("taskId") String taskId
            ,String templeId,String caseId,String taskInstanceId,String taskNodeInstanceId) {
        File file = null;
        File tmpFile = null;
        try {
            Object[] result = (Object[])templeBS.getTemplePathByTaskId(taskId);
            if (FilepathValidateUtils.validateFilepath(String.valueOf(result[1]))) {
	            file = new File(String.valueOf(result[1]));
	            tmpFile = preloadExcel(file,templeId,caseId,taskInstanceId,String.valueOf(result[2]),taskNodeInstanceId);
	            
	            
	            DownloadUtils.download(response, tmpFile, tmpFile.getName()); //
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(tmpFile != null){
                tmpFile.delete();
            }
        }
    }
	
    @SuppressWarnings("unchecked")
	private File preloadExcel(File excelFile, String templeId,String caseId,String taskInstanceId,String fileN,String taskNodeInstanceId){
        
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(fileN,
            		".xls",new File(getRealPath() + InputConstants.FILE_EXP_PATH));
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        
        Pager p = new Pager();
        p.setPage(1);
        p.setPagesize(999999999);
        
        Map<String, Object> getMap = this.taskCaseBS.getMap(templeId, caseId,true);
        List<String>colList=(List<String>) getMap.get("colName");
        Map<String,Integer> colToIndex = Maps.newHashMap();
        for(int i = 0; i < colList.size(); i++){
        	if(!"SYS_ORDER_NO".equals(colList.get(i))){
        		colToIndex.put(colList.get(i), i);
        	}
        }
        List<Map<String, Object>> rowDatas = getPreloadData(colList,taskInstanceId);
        InputStream is = null;
        FileOutputStream os = null;
        HSSFWorkbook wb = null;
        try {
        	Map<String, Object> rowsMap = getTaskCaseInfoList(p, taskNodeInstanceId, taskInstanceId, "online", templeId, caseId, "", "", "","export");
        	List<Map<String, Object>> rows = (List<Map<String, Object>>)rowsMap.get("Rows");
        	
            is = new FileInputStream(excelFile);  
            wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = null;
            HSSFFont font = wb.createFont();
            font.setColor(HSSFColorPredefined.BLACK.getIndex());
            
            HSSFCellStyle cellStyle = wb.createCellStyle();
//            cellStyle.setDataFormat(format.getFormat("@"));
//            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFont(font);
            cellStyle.setLocked(false);
            cellStyle.setHidden(false);
            /** 自动换行 (否则会出现混乱挤占问题) */
            cellStyle.setWrapText(true);
            
            
            if(null != rows){
            	for(int r = 2; r < rows.size() + 2; r++){
            		row = sheet.createRow(r);
            		HSSFCell cell = null;
            		Map<String, Object> cellVal = rows.get(r - 2);
            		Set<String> colNm = cellVal.keySet();
            		
            		for(String col : colNm){
            			if(colToIndex.containsKey(col)){
            				cell = row.createCell(colToIndex.get(col));
            				cell.setCellValue(cellVal.get(col) == null ? "": String.valueOf(cellVal.get(col)));
            				cell.setCellType(CellType.STRING);
            				cell.setCellStyle(cellStyle);
            			}
            		}
            	}
            }
            
            if(null != rowDatas){
            	for(int r = 2; r < rowDatas.size() + 2; r++){
            		row = sheet.createRow(r);
            		HSSFCell cell = null;
            		Map<String, Object> cellVal = rowDatas.get(r - 2);
            		Set<String> colNm = cellVal.keySet();
            		
            		for(String col : colNm){
            			if(colToIndex.containsKey(col)){
            				cell = row.createCell(colToIndex.get(col));
            				cell.setCellValue(cellVal.get(col) == null ? "": String.valueOf(cellVal.get(col)));
            				cell.setCellType(CellType.STRING);
            				cell.setCellStyle(cellStyle);
            			}
            		}
            	}
            	
            }
            os = new FileOutputStream(tmpFile);
            wb.write(os);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
        	IOUtils.closeQuietly(os);
        	IOUtils.closeQuietly(is);
        	IOUtils.closeQuietly(wb);
        }
        return tmpFile;
	}
    
    
    private  List<Map<String, Object>> getPreloadData(List<String>keyCols,String taskInstanceId){
        List<Map<String, Object>>resList=Lists.newArrayList();
        RptTskIns rptTskIns  = deployTaskBS.getRptTskInsById(taskInstanceId);
        //需要加载
        if(rptTskIns.getLoadDataMark().equals("1")){
            List<Map<String, Object>> dataLoadList =  
            	initLoadData(rptTskIns.getLoadDataMark(),rptTskIns.getExeObjId(),rptTskIns.getDataDate(),rptTskIns.getOrgNo());
            
            if(dataLoadList == null){
            	return resList;
            }
            for(Map<String, Object> oneCol:dataLoadList){
                Map<String,Object>oneMap=Maps.newHashMap();
                //展示的数据
                for(String col:keyCols){
                    oneMap.put(col, oneCol.get(col));
                }
                oneMap.put("data_type", "0");
                resList.add(oneMap);
            }
        }
        return resList;
    }
	
	
	/**
	 * 得到包括主键在内的所有需要查询的字段信息
	 * @param keyCols
	 * @param templeId
	 * @return
	 
	private List<String>getUniqueColList(String templeId){
		List<String> list =  this.templeFieldBS.findUniqueListBytempleId(templeId);
		list.add("__ID");
		return list;
	}
	
	private List<String>getAllQueryCols(List<String>uniqueList,List<String>keyCols){
		List<String>rsList = Lists.newArrayList(keyCols);
		for(String col:uniqueList){
			if(!rsList.contains(col))
				rsList.add(col);
		}
		return rsList;
	}
	*/

    private List<Map<String, Object>> initLoadData(String loadDataMark,String exeObjId,String dataDate, String orgNo){
        if(loadDataMark.equals("1")) {
            DataLoadInfoVO dataLoadInfoVO = this.tableBS.getRptListDataloadTypeByTempId(exeObjId);
            List<RptInputLstTempleField> colList = this.templeFieldBS.findEntityListByPropertyAndOrder("templeId", exeObjId, "orderNo", false);
            List<String> list = this.getColList(colList);
            List<Map<String,Object>> dataList = Lists.newArrayList();
            if(null == dataLoadInfoVO){
            	return dataList;
            }
            
            if(dataLoadInfoVO.getType().equals("1")) {
                dataList = this.initDataLoadDataType(dataLoadInfoVO.getCfgId(), dataLoadInfoVO.getDsId(), list);
            } else if(dataLoadInfoVO.getType().equals("2")) {
                dataList = this.initDataLoadSqlType(dataLoadInfoVO.getCfgId(), dataLoadInfoVO.getDsId(), list,dataDate, orgNo);
            }
            return dataList;
        }
        return null;

    }
	
    /*
	private Map<String,RptInputLstTempleField>getColMapInfo(List<RptInputLstTempleField>colList){
		
		Map<String,RptInputLstTempleField>map=Maps.newHashMap();
		for(RptInputLstTempleField col:colList){
			map.put(col.getFieldEnName(), col);
		}
		return map;
	}
	*/

	private List<Map<String,Object>> initDataLoadDataType(String cfgId,String dsId,List<String> colList){
		String dataInfo = tableBS.getDataInfo(cfgId);
		JSONArray jsonArray = JSON.parseArray(dataInfo);
		List<Map<String,Object>>list=Lists.newArrayList();
		for(int i =0 ;i<jsonArray.size();i++){
			Map<String,Object>map=Maps.newHashMap();
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			if(jsonObj!=null)
			{
				for (Iterator<Entry<String, Object>> it = jsonObj.entrySet().iterator(); it.hasNext(); ) {
					Entry<String, Object> entry = it.next();
					String key = entry.getKey();
					if (! key.startsWith("__")) {
						map.put(key, entry.getValue());
					}
				}
			}
			list.add(map);
		}
		return list;
	}
	private List<String>getColList(List<RptInputLstTempleField> colList){
		
		List<String>list=Lists.newArrayList();
		for(RptInputLstTempleField field:colList){
			list.add(field.getFieldEnName());
		}
		return list;
	}
	/**
	 * 特殊逻辑
	 */
	private List<Map<String,Object>> initDataLoadSqlType(String cfgId,String dsId,List<String> colList,String dataDate, String orgNo){
		RptListDataloadSqlInfo rptListDataloadSqlInfo = tableBS.getSqlInfo(cfgId);
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		try {
		    /**
		     * 查询数据加载过滤
		     */
			Set<String> cols = deployTaskBS.findSqlAsCol(rptListDataloadSqlInfo.getSql2().toUpperCase());
			
		    if(cols.contains("FILTER_NM")){
		        Map<String,String> orgDictRev = deployTaskBS.getorgDictRev();
		        String sql = "select * from ("+rptListDataloadSqlInfo.getSql2()+") t where 1=1 ";
		        if(!user.isSuperUser()){
		        	sql += " and t.FILTER_NM = '"+orgDictRev.get(user.getOrgNo())+"'";
		        }
		        
		        try {
		            return dataUtils.getData(sql,rptListDataloadSqlInfo.getDsId(),colList,dataDate,orgNo);
                } catch (Exception e) {
                	e.printStackTrace();
                    //过滤数据失败
                }
		    }
		    
		    if(cols.contains("FILTER_NO")){
		        String sql = "select * from ("+rptListDataloadSqlInfo.getSql2()+") t where 1=1 ";
		        
		        if(!user.isSuperUser()){
		        	sql += " and t.FILTER_NO = '"+user.getOrgNo()+"'";
		        }
		        
		        try {
		            return dataUtils.getData(sql,rptListDataloadSqlInfo.getDsId(),colList,dataDate,orgNo);
                } catch (Exception e) {
                	e.printStackTrace();
                    //过滤数据失败
                }
		    }
		    
			return dataUtils.getData(rptListDataloadSqlInfo.getSql2(),rptListDataloadSqlInfo.getDsId(),colList,dataDate, orgNo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 数据是否预加载
	 * @param dataList
	 * @param keyCols
	 * @param taskInstanceId
	 * @param pager
	 * @return
	 */
	private Map<String,Object> getDataShow(List<Map<String, Object>>dataList,List<String>keyCols,String taskInstanceId,Pager pager){
	    Map<String,Object> resMap = Maps.newHashMap();
	    resMap.put("Total", 0);
		List<Map<String, Object>>showList=Lists.newArrayList();
		RptTskIns rptTskIns  = deployTaskBS.getRptTskInsById(taskInstanceId);
		for(Map<String, Object> oneCol:dataList){
			Map<String,Object>oneMap=Maps.newHashMap();
			//展示的数据
			for(String col:keyCols){
				oneMap.put(col, oneCol.get(col));
			}
			showList.add(oneMap);
		}
		//需要加载
		if(pager.getPagesize() > showList.size() && rptTskIns.getLoadDataMark().equals("1"))
		{
			List<Map<String, Object>> dataLoadList =  initLoadData(rptTskIns.getLoadDataMark(),rptTskIns.getExeObjId(),rptTskIns.getDataDate(),rptTskIns.getOrgNo());
			logger.info("预加载数据总条数为：" + dataLoadList.size());
			int i = 0;
			if(dataLoadList != null){
				for(Map<String, Object> oneCol:dataLoadList){
				    if(i++ >= pager.getPageFirstIndex()){
				        if(showList.size() < pager.getPagesize()){
				            Map<String,Object>oneMap=Maps.newHashMap();
		                    //展示的数据
		                    for(String col:keyCols){
		                        oneMap.put(col, oneCol.get(col));
		                    }
		                    oneMap.put("data_type", "0");
		                    
		                    showList.add(oneMap);
				        }else{
		                    break;
		                }
					}
				}
			}
			resMap.put("Total", dataLoadList == null ? "0" : dataLoadList.size());
		}
		resMap.put("Rows", showList);
		
		return resMap;
	}
	

	/**
	 * 跳转到选择机构页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/comment", method = RequestMethod.GET)
	public ModelAndView comment(String templateId, String uniqueKey, String content,String taskIndexType, String view) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("taskIndexType", StringUtils2.javaScriptEncode(taskIndexType));
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("uniqueKey", StringUtils2.javaScriptEncode(uniqueKey));
		map.put("view", StringUtils2.javaScriptEncode(view));
		map.put("userName", BioneSecurityUtils.getCurrentUserInfo().getUserName());
		if(StringUtils.isNotEmpty(content)){
			try {
				content = URLDecoder.decode(URLDecoder.decode(content,"UTF-8"),"UTF-8");
				map.put("content", StringUtils2.javaScriptEncode(content));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ModelAndView(
				PATH +"task-case-comment", map);
	}

	/**
	 * 导入文件批次查看跳转
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/chackDataFile")
	public ModelAndView chackDataFile(String templeId, String caseId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		return new ModelAndView("/udip/task/task-case-data-records", mm);
	}

	/**
	 * 根据模版Id与任务批次Id获取数据时间批次
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping(value = "/getDataFileRecords")
	@ResponseBody
	public Map<String, Object> getDataFileRecords(String templeId, String caseId) {
		// 统一获取信息
		Map<String, Object> getMap = taskCaseBS.getMap(templeId, caseId,false);

		// org.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		List<String> colList = Lists.newArrayList();
		List<RptInputLstTempleField> col = this.templeFieldBS.findEntityListByProperty("templeId", templeId);
		for (RptInputLstTempleField columns : col) {
			colList.add(columns.getFieldEnName());
		}
		List<String> dataState = Lists.newArrayList();
		dataState.add(UdipConstants.TASK_STATE_SAVE);
		dataState.add(UdipConstants.TASK_STATE_VALIDATE);
		dataState.add(UdipConstants.TASK_STATE_REFUSE);
		List<Map<String, Object>> dataList;
		Map<String, Object> objDefMap = Maps.newHashMap();

		String caseOrNot = caseId;
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
		List<String> org = this.taskCaseBS.getOrgList(temp, null, caseId);
		try {
			if ("1".equals(temp.getAllowInputHist())) {
				caseOrNot = null;
				dataState = null;
			}
			dataList = dataUtils.findDataFileRecords(temp, null, (String) getMap.get("casedata"), dataState, org, getMap.get("oper"), caseOrNot);
			objDefMap.put("Rows", dataList);
			return objDefMap;
		} catch (Exception e) {
			logger.error(e);
			return objDefMap;
		}

	}

	/**
	 * 根据时间删除导入的数据
	 * 
	 * @param templeId
	 * @param caseId
	 * @param times
	 * @return
	 */
	@RequestMapping("/deleteDataFileRecords")
	@ResponseBody
	public List<String> deleteDataFileRecords(String templeId, String caseId,
			String times) {
		// 统一获取信息
		Map<String, Object> getMap = taskCaseBS.getMap(templeId, caseId,false);
		String org = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		List<String> colList = Lists.newArrayList();
		List<RptInputLstTempleField> col = this.templeFieldBS
				.findEntityListByProperty("templeId", templeId);
		for (RptInputLstTempleField columns : col) {
			colList.add(columns.getFieldEnName());
		}
		String caseOrNot = caseId;
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
		String caseData = (String) getMap.get("casedata");
		List<String> dataList = Lists.newArrayList();
		try {
			if ("1".equals(temp.getAllowInputHist())) {
				caseOrNot = null;
				caseData = null;
			}
			dataList = dataUtils.deleteDataFileRecords(temp, times, null,
					caseData, null, org, getMap.get("oper"), caseOrNot);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//					.getCurrentUserInfo().getCurrentLogicSysNo(),
//					BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//					"补录模板【" + udipTemp.getTempleName() + "】,数据入库时间【" + times
//							+ "】批次删除成功");
			return dataList;
		} catch (Exception e) {
			logger.error(e);
			dataList.add(e.getMessage());
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils
//					.getCurrentUserInfo().getCurrentLogicSysNo(),
//					BioneSecurityUtils.getCurrentUserInfo().getUserId(),
//					"补录模板【" + udipTemp.getTempleName() + "】,数据入库时间【" + times
//							+ "】批次删除失败");
			return dataList;
		}

	}
	
	
	/**
	 * chenhx1 20180205
	 * 在操作预加载数据前需要把预加载出来的数据插入到补录表里
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/saveAutoLoad",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveAutoLoad(String templeId,String taskInstanceId,String taskNodeInstanceId){
	    Map<String,Object> resMap = Maps.newHashMap();
	    RptTskIns caseInfo = taskCaseBS.getEntityById(taskInstanceId);
        if(caseInfo != null && !"1".equals(caseInfo.getLoadDataMark())){
            resMap.put("flag", true);
        }else {
        	 Map<String, Object> getMap = taskCaseBS.getMap(templeId, taskInstanceId,false);
     	    
     	    Pager p = new Pager();
             p.setPage(1);
             p.setPagesize(999999999);
             try {
                 Map<String, Object> rowsMap = getTaskCaseInfoList(p, taskNodeInstanceId, 
                         taskInstanceId, "online", templeId, taskInstanceId, "", "", "","save");
                 List<Map<String, Object>> rows = (List<Map<String, Object>>)rowsMap.get("Rows");
                 resMap = taskCaseBS.saveAutoLoad(getMap, rows, taskNodeInstanceId,taskInstanceId,caseInfo.getDataDate());
             } catch (Exception e) {
                 resMap.put("flag", false);
                 resMap.put("msg", "系统错误,请联系管理员.");
                 
             }
        }
        return resMap;
	}
	
	/**
	 * 手工补录的保存操作
	 * 
	 * @param templeId
	 * @param paramStrAdd
	 * @param paramStrDel
	 * @param param
	 * @param caseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/saveTaskcaseTempleInfo",method=RequestMethod.POST)
    @ResponseBody
	public List<String> saveTaskcaseTempleInfo(String taskInstanceId,String taskId,String templeId, 
	        String paramStrAdd, String paramStrDel, String paramStrUdp, String param, String caseId,String logList) {
		try {
			if(paramStrAdd == null) {
				paramStrAdd = "";
			}
			if(paramStrUdp == null) {
				paramStrUdp = "";
			}
		    paramStrAdd = URLDecoder.decode(paramStrAdd,"UTF-8");
            paramStrUdp = URLDecoder.decode(paramStrUdp,"UTF-8");		    
			// 统一获取信息
			Map<String, Object> getMap = taskCaseBS.getMap(templeId, caseId,false);
			RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
			List<String> org = this.taskCaseBS.getOrgList(temp, null, caseId);

			String[] paramCol = param.split(",");
			List<Map<String, Object>> dataAdd = Lists.newArrayList();
			List<Map<String, Object>> dataUdp = Lists.newArrayList();
			List<String> dataOld = Lists.newArrayList();
			
			if ("1".equals(temp.getAllowAdd())) {
				dataAdd = getDataList(paramStrAdd, paramCol);
			}
			if ("1".equals(temp.getAllowUpdate())) {
				dataUdp = getDataList(paramStrUdp, paramCol);
			}
			if ("1".equals(temp.getAllowDelete())) {
				String[] paramDel = paramStrDel.split("@;@");
				for(String p : paramDel){
					if(StringUtils.isNotEmpty(p))
						dataOld.add(p);
				}
			}
			List<String> list = Lists.newArrayList();
			List<RptInputLstValidateLog> listlog = Lists.newArrayList();
			List<RptInputLstValidateLog> listlog2 = Lists.newArrayList();
			// 日志实体
			RptInputLstValidateLog validateLog = validateLogBS.getUdipValidateLog(caseId, "", (String) getMap.get("oper"), "", templeId, (String) getMap.get("data"));
			try {
				validateLogBS.deleteUdipValidateLog(caseId, templeId, validateLog.getOrgNo());

				Boolean chack = false;
				
				this.dataFormatValidate.validate(listlog, listlog2, dataAdd, dataUdp, null, getMap);
				
				if (CollectionUtils.isEmpty(listlog)) {
					RptInputLstValidateLog log2 = new RptInputLstValidateLog();
					log2.setValidateResult("数据合法性校验成功");
					listlog.add(log2);
					Map<String,Object>queryMap=Maps.newHashMap();
					queryMap.put("taskInstanceId", taskInstanceId);
					Map<String,Object> map = taskOperDao.getRptTskInsInfoWithNode(queryMap);
					String taskNodeInstanceId = (String)map.get("TASK_NODE_INSTANCE_ID");
					chack = dataUtils.deleteAndAddDataWeihai(taskNodeInstanceId,1, dataAdd, 
					        dataOld, dataUdp, (RptInputLstTempleInfo) getMap.get("temp"), 
					        (List<String>) getMap.get("keyCols"), (Map<String, RptInputLstTempleField>) getMap.get("colMap"),
					        (Map<String, Map<String, Object>>) getMap.get("libList"), 
					        (String) getMap.get("casedata"),
					        null, getMap.get("data"),getMap.get("oper"), caseId, 
					        org);
					this.taskCaseBS.updateAllErrorMark(templeId,taskNodeInstanceId,"02");
					//更新预加载数据状态
					RptTskIns ins = new RptTskIns();
					ins.setTaskInstanceId(taskInstanceId);
					ins.setLoadDataMark("3");
					taskOperDao.updateRptTskIns(ins);
				} else {
					list.add("数据合法性校验失败");
//					logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + udipTemple.getTempleName() + "】关联的补录表【" + udipTemple.getTableEnName() + "】入库前数据校验失败");
				}
				if (chack) {
					// 更改dataState状态
					this.dataStateBS.changerDataState(templeId, caseId, org, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), UdipConstants.TASK_STATE_SAVE);
					// 新增一条数据操作记录
					this.authRecordBS.saveAuthRecord(templeId, caseId, UdipConstants.TASK_STATE_SAVE);
				} else {
					list.add("数据插入数据库失败");
				}
				validateLogBS.saveUdipValidateLog(listlog, validateLog);
				if(list.isEmpty())
				{
					this.deployTaskOperBS.saveOperLog(taskInstanceId,logList,templeId,taskId);
				}
				return list;
			} catch (Exception e) {
                e.printStackTrace();
				RptInputLstValidateLog log2 = new RptInputLstValidateLog();
				log2.setValidateResult("数据插入数据库失败：" + e.getMessage());
				listlog.add(log2);
				validateLogBS.saveUdipValidateLog(listlog, validateLog);
				list.add(e.getMessage());
				return list;
			}
		} catch (Exception e1) {
			List<String> list = Lists.newArrayList();
			list.add(e1.getMessage());
			return list;
		}
	}

	/**
	 * 导出补录数据成excel
	 * 
	 * @param response
	 * @param templeId
	 * @param caseId
	 * @param orgId
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/excelDownLoalTaskcaseInfo")
	public void excelDownLoalTaskcaseInfo(HttpServletResponse response,
			String templeId, String caseId, String orgId, String sqlStr,String taskInstanceId,String taskNodeInstanceId)
			throws UnsupportedEncodingException {
		if (StringUtils.isNotBlank(sqlStr)) {
			sqlStr = URLDecoder.decode(sqlStr, "utf-8");
		} else {
			sqlStr = "";
		}
		// 统一获取信息
		Map<String, Object> getMap = taskCaseBS.getMap(templeId, caseId,false);
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
		List<Map<String, Object>> dataList = Lists.newArrayList();

		List<String> org = taskCaseBS.getOrgList(temp, orgId, caseId);
		

		List<String> colName = (List<String>) getMap.get("colName");
		List<String> colNameCN = (List<String>) getMap.get("colNameCN");
		List<String> colNameDetail = (List<String>) getMap.get("colNameDetail");
		String caseOrNot = caseId;
		String caseData = (String) getMap.get("casedata");
		File file = null;
		try {
			if ("1".equals(/* temp.getHisable() */temp.getAllowInputHist())) {
				caseOrNot = null;
				caseData = null;
			}
			RptInputLstTempleInfo temple = templeBS.getEntityById(templeId);
			RptTskIns caseInfo = taskCaseBS.getEntityById(caseId);
			String title = temple.getTempleName();
			StringBuilder title2 = new StringBuilder(1000);
			String[] d = new String[]{"", "元", "万元"};
			colName.remove(UdipConstants.TAB_ORDER_NO);
			List<String>allTaskNodeInsIds = deployTaskOperBS.getAllTaskNodeInsIds(caseId);
			title2.append("机构：").append(BioneSecurityUtils.getCurrentUserInfo().getOrgName()).append(";").append("报告期：").append(caseInfo.getDataDate()).append(";").append("单位：").append(StringUtils.isNotEmpty(temple.getUnit()) ? d[Integer.parseInt(temple.getUnit())] : "");
			
			

			Map<String,Object>queryMap=Maps.newHashMap();
			queryMap.put("taskInstanceId", taskInstanceId);
			Map<String,Object>map = taskOperDao.getRptTskInsInfoWithNode(queryMap);
			String orgSql = "";//" and SYS_OPER_ORG = '" + BioneSecurityUtils.getCurrentUserInfo().getOrgNo() + "' ";
			String nodeSql ;
			if(map.get("NODE_TYPE").equals("02")){
				/*****审批节点,能看到一个流程下所有的数据*****************/
				nodeSql = " and ";
				List<String>allTempTaskNodeInsIds = deployTaskOperBS.getAllTaskNodeInsIds(taskInstanceId);
				StringBuilder flowNodeBuff = new StringBuilder();
				if(allTempTaskNodeInsIds!=null&&allTempTaskNodeInsIds.size()!=0){
					flowNodeBuff.append("  FLOW_NODE_ID IN (");
					boolean isFirst=true;
					for(String id:allTempTaskNodeInsIds){
						if(!isFirst) flowNodeBuff.append(",");
						else isFirst=false;
						flowNodeBuff.append("'").append(id).append("'");
					}
					flowNodeBuff.append(")");
					nodeSql += flowNodeBuff.toString();
				}
				/*****添加任务过滤    一个流程只能看到自己流程下的数据*****************/
			}else{
				/*****填报节点,能看到一个流程节点下所有的数据*****************/
				nodeSql = "  AND FLOW_NODE_ID ='"+taskNodeInstanceId+"'";
			}
			
			
			dataList = dataUtils.getData(allTaskNodeInsIds,temp, (List<String>) getMap.get("keyCols"), null, sqlStr + orgSql+nodeSql, colName, (Map<String, String>) getMap.get("colNameType"), 0, 0, caseData, null, org, getMap.get("oper"), caseOrNot);
			file = fileNetUpload(dataList, colName, colNameCN, colNameDetail, (List<String>) getMap.get("keyCols"), title, title2.toString());
			DownloadUtils.download(response, file, temp.getTempleName() + ".xls"); // 提供下载
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "导出补录模板【" + temp.getTempleName() + "】关联的补录表【" + temp.getTableEnName() + "】的excel数据文件");
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}
	
	private File fileNetUpload(List<Map<String, Object>> dataList, List<String> colName, List<String> colNameCN,
			List<String> colNameDetail,List<String> keyCols, String... title) {
		File localFile = null;
		String xlsFile = String.valueOf(System.currentTimeMillis()) + ".xls";
		localFile = new File(xlsFile);
		OutputStream out = null;
		HSSFWorkbook workBook = null;
		if (title != null && title.length == 2) {
			workBook = excelConstants.getWorkBookForListMap(dataList, colName, colNameCN, colNameDetail,keyCols, title[0], title[1]);
		} else {
			workBook = excelConstants.getWorkBookForListMap(dataList, colName, colNameCN, colNameDetail,keyCols);
		}
		try {
			FileOutputStream fOut = new FileOutputStream(localFile);
			out = new BufferedOutputStream(fOut);
			workBook.write(fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return localFile;
	}

	/**
	 * 校验结果导出
	 * 
	 * @param response
	 * @param templeId
	 * @param caseId
	 */
	@RequestMapping("/excelDownLoalValidateInfo")
	public void excelDownLoalValidateInfo(HttpServletResponse response, String templeId, String caseId) {
		SearchResult<RptInputLstValidateLog> searchResult = this.validateLogBS.getValidateLog(0, 10000, templeId, caseId);
		RptInputLstTempleInfo temp = templeBS.getEntityById(templeId);
		List<String> ruleName = Lists.newArrayList();
		List<String> validateResult = Lists.newArrayList();
		if (!searchResult.getResult().isEmpty()) {
			for (RptInputLstValidateLog udipValidateLog : searchResult.getResult()) {
				ruleName.add(udipValidateLog.getRuleName());
				validateResult.add(udipValidateLog.getValidateResult());
			}
		}
		OutputStream out = null;
		try {
			String fileName = temp.getTempleName() + "校验结果" + DateUtils.formatDate(new Date(), "yyyy-MM-dd hh_mm_ss") + ".xls";
			response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(StringUtils.replaceEachRepeatedly(fileName, new String[] {":", " ", "/", ";"},  new String[] {"_", "_", "_", "_"}), "UTF-8"));
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			out = new BufferedOutputStream(response.getOutputStream());
			HSSFWorkbook workBook = this.excelConstants.getWorkBookForListMap(ruleName, validateResult);
			workBook.write(out);
			out.flush();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@RequestMapping(value="/saveErrorMark.*",method=RequestMethod.POST)
	@ResponseBody
	public void saveErrorMark(String saveObj,String templateId){
	
		this.taskCaseBS.updateErrorMark(saveObj, templateId,"01");
	}

	/**
	 * 统一分割字符串
	 * 
	 * @param paramStr
	 * @param paramCol
	 * @return
	 */
	private List<Map<String, Object>> getDataList(String paramStr, String[] paramCol) {
		List<Map<String, Object>> data = Lists.newArrayList();
		if (StringUtils.length(paramStr) > 0) {
			String[] paramDel = paramStr.split("@;@");
			// 循环得到新增数据信息，放入list中
			if (paramDel != null && paramDel.length > 0) {
				for (int i = 0; i < paramDel.length; i++) {
					String[] paramValue = paramDel[i].split("#@@");
					Map<String, Object> map = Maps.newHashMap();
					for (int j = 0; j < paramCol.length; j++) {
						if (("DATAINPUT_ID".equals(paramCol[j])|| UdipConstants.TAB_ORDER_NO.equals(paramCol[j])) && paramValue.length <= j) {
							continue;
						}
						//if(paramCol[j].equalsIgnoreCase("COMMENTS"))
						//{
						//	map.put(paramCol[j], paramValue[j-1].trim());
						//}else
						if("COMMENTS".equals(paramCol[j])){
							String comments = EncodeUtils.urlDecode(EncodeUtils.urlDecode(paramValue[j].trim()));
							map.put(paramCol[j], comments);
						}else{
							map.put(paramCol[j], paramValue[j].trim());
						}
						
					}
					data.add(map);
				}
			}
		}
		return data;
	}
	

	/**
	 * 补录任务填报 数据校验按钮
	 * @param taskInstanceId
	 * @param taskId
	 * @param templeId
	 * @param paramStrAdd
	 * @param paramStrDel
	 * @param paramStrUdp
	 * @param param
	 * @param caseId
	 * @param logList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/validateWeiHaiTaskcaseTempleInfo.*",method=RequestMethod.POST)
	@ResponseBody
	public synchronized List<String>validateWeiHaiTaskcaseTempleInfo(String taskInstanceId,String taskId,String templeId, 
	        String paramStrAdd, String paramStrDel, String paramStrUdp, String param, String caseId,String logList){
		logger.debug("validateWeiHaiTaskcaseTempleInfo-----start:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		List<String> list = Lists.newArrayList();
		try {
		    paramStrAdd = URLDecoder.decode(URLDecoder.decode(paramStrAdd,"UTF-8"),"UTF-8");
		    paramStrUdp = URLDecoder.decode(URLDecoder.decode(paramStrUdp,"UTF-8"),"UTF-8");
			/*******************************保存用户需要验证的数据          开始*************************************************/
			Map<String,Object>queryMap=Maps.newHashMap();
			queryMap.put("taskInstanceId", taskInstanceId);
			Map<String,Object> map = taskOperDao.getRptTskInsInfoWithNode(queryMap);
			

			List<RptTskNodeIns> nodeIns=taskOperDao.getRptTskNodeIns(taskInstanceId);
			List<String>taskNodeInsIds=Lists.newArrayList();
			for(RptTskNodeIns ins:nodeIns){
				taskNodeInsIds.add(ins.getTaskNodeInstanceId());
			}
			
			String taskNodeInstanceId = (String)map.get("TASK_NODE_INSTANCE_ID");
			
			RptInputLstTempleInfo rptInputLstTempleInfo = this.templeBS.getEntityById(templeId);
			dataUtils.saveCurrentNodeData(rptInputLstTempleInfo,taskNodeInsIds);
			/********************************保存用户需要验证的数据          结束************************************************/

			// 统一获取信息
			Map<String, Object> getMap = taskCaseBS.getMap(templeId, caseId,false);
			RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");

			List<RptInputLstTempleField>colList = (List<RptInputLstTempleField>)getMap.get("colList");
			RptInputLstTempleField field=new RptInputLstTempleField();
			field.setFieldEnName("SYS_ORDER_NO");
			colList.add(field);
			RptInputLstTempleField datainputId=new RptInputLstTempleField();
			datainputId.setFieldEnName("DATAINPUT_ID");
			colList.add(datainputId);
			
			List<String> org = this.taskCaseBS.getOrgList(temp, null, caseId);

			String[] paramCol = param.split(",");
			List<Map<String, Object>> dataAdd = Lists.newArrayList();
			List<Map<String, Object>> dataUdp = Lists.newArrayList();
			List<String> dataOld = Lists.newArrayList();
			
			if ("1".equals(temp.getAllowAdd())) {
				dataAdd = getDataList(paramStrAdd, paramCol);
			}
			if ("1".equals(temp.getAllowUpdate())) {
				dataUdp = getDataList(paramStrUdp, paramCol);
			}
			if ("1".equals(temp.getAllowDelete())) {
				String[] paramDel = paramStrDel.split("@;@");
				for(String p : paramDel){
					if(StringUtils.isNotEmpty(p))
						dataOld.add(p);
				}
			}
			
			List<RptInputLstValidateLog> listlog = Lists.newArrayList();
			List<RptInputLstValidateLog> listlog2 = Lists.newArrayList();
			// 日志实体
			RptInputLstValidateLog validateLog = validateLogBS.getUdipValidateLog(caseId, "", (String) getMap.get("oper"), "", templeId, (String) getMap.get("data"));
			logger.debug("validateWeiHaiTaskcaseTempleInfo-----getUdipValidateLog:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
			try {
				validateLogBS.deleteUdipValidateLog(caseId, templeId, validateLog.getOrgNo());
				
				List<Map<String,Object>>dbDataList = dataUtils.getValidateData(temp,taskNodeInsIds,colList);
				
				this.dataFormatValidate.validate(listlog, listlog2, dataAdd, dataUdp, dbDataList, getMap);
				
				if(listlog==null||listlog.isEmpty()){
					logger.debug("validateWeiHaiTaskcaseTempleInfo-----listlog.addAll:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
					dataUtils.updateWeihaiValidateData(taskNodeInstanceId,1, dataAdd, dataOld, 
					        dataUdp, (RptInputLstTempleInfo) getMap.get("temp"), (List<String>) getMap.get("keyCols"),
				        (Map<String, RptInputLstTempleField>) getMap.get("colMap"),(Map<String, Map<String, Object>>) getMap.get("libList"),
				        (String) getMap.get("casedata"), null, getMap.get("data"),getMap.get("oper"), caseId, org);					
					List<RptInputLstValidateLog> log3 = validataWeiHaiTaskcaseInfo( templeId,  taskInstanceId, taskNodeInstanceId,(List<RptInputLstTempleField>)getMap.get("colList"));
					listlog.addAll(log3);
				}
				if (CollectionUtils.isEmpty(listlog)) {
					RptInputLstValidateLog log2 = new RptInputLstValidateLog();
					log2.setValidateResult("数据合法性校验成功");
					listlog.add(log2);
					
				} else {
					list.add("数据合法性校验失败");
				}
				logger.debug("validateWeiHaiTaskcaseTempleInfo-----list.add:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
				validateLogBS.saveUdipValidateLog(listlog, validateLog);
				
				dataUtils.deleteValidateTable((RptInputLstTempleInfo) getMap.get("temp"),taskNodeInsIds);
			} catch (Exception e) {
                e.printStackTrace();
				RptInputLstValidateLog log2 = new RptInputLstValidateLog();
				log2.setValidateResult("数据插入数据库失败：" + e.getMessage());
				listlog.add(log2);
				validateLogBS.saveUdipValidateLog(listlog, validateLog);
				list.add(e.getMessage());
			}
		} catch (Exception e1) {
			//捕获一场后打印错误信息 20190902
			e1.printStackTrace();
			list.add(e1.getMessage());
		}
		logger.debug("validateWeiHaiTaskcaseTempleInfo-----end:" + FormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
		return list;
	}

	/**
	 * 手工补录的数据逻辑校验
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<RptInputLstValidateLog> validataWeiHaiTaskcaseInfo(String templeId, String rptTskInsId,String rptTskNodeInsId,List<RptInputLstTempleField>colList) {

		// 统一获取信息
		Map<String, Object> getMap = this.taskCaseBS.getMap(templeId, rptTskInsId,false);
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		List<RptInputLstValidateLog> listlog = Lists.newArrayList();
		List<RptInputLstValidateLog> listlog2 = Lists.newArrayList();
		List<RptInputLstValidateLog> list = Lists.newArrayList();
		Map<String, List<RptInputListDataRuleInfo>> map = Maps.newHashMap();
		List<RptInputLstTempleRule> tempRule = this.templeRuleInfoBS.findEntityListByProperty("templeId", templeId);
		for (RptInputLstTempleRule temprule : tempRule) {
			RptInputListDataRuleInfo ruleInfo = this.dataRuleBS.getEntityById(temprule.getRuleId());

			if (!map.containsKey(ruleInfo.getRuleType())) {
				map.put(ruleInfo.getRuleType(), new ArrayList<RptInputListDataRuleInfo>());
			}
			map.get(ruleInfo.getRuleType()).add(ruleInfo);
		}
		try {
			temp.setTableEnName(temp.getTableEnName()+"_VALIDATE");
			RptInputLstTempleField field=new RptInputLstTempleField();
			field.setFieldEnName("SYS_ORDER_NO");
			colList.add(field);

			RptInputLstTempleField datainputId=new RptInputLstTempleField();
			datainputId.setFieldEnName("DATAINPUT_ID");
			colList.add(datainputId);
			
			List<RptTskNodeIns> nodeIns=taskOperDao.getRptTskNodeIns(rptTskInsId);
			List<String>taskNodeInsIds=Lists.newArrayList();
			for(RptTskNodeIns ins:nodeIns){
				taskNodeInsIds.add(ins.getTaskNodeInstanceId());
			}
			
			dataList = dataUtils.getValidateData(temp,taskNodeInsIds,colList);
			listlog = ValidateUtils.validate((RptTskIns) getMap.get("taskCase"), (RptInputLstTempleInfo) getMap.get("temp"), map, dataList, 0,(Map<String, RptInputLstTempleField>)getMap.get("colMap"));
		} catch (Exception e) {
		}
		listlog2 = ValidateUtils.validate((RptTskIns) getMap.get("taskCase"), (RptInputLstTempleInfo) getMap.get("temp"), map);
		listlog.addAll(listlog2);
		
		validateLogBS.deleteUdipValidateLog(rptTskInsId, templeId,null);
		// RptInputLstValidateLog log = validateLogBS.getUdipValidateLog(rptTskInsId, "", (String) getMap.get("oper"), "", templeId, (String) getMap.get("data"));
		if (listlog.isEmpty()) {
			RptInputLstValidateLog log2 = new RptInputLstValidateLog();
			log2.setValidateResult("数据规则校验成功");
			listlog.add(log2);
			//validateLogBS.saveUdipValidateLog(listlog, log);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + temp.getTempleName() + "】关联的补录表【" + temp.getTableEnName() + "】数据入库后校验成功");
		} else {
			list.addAll(listlog);
			//validateLogBS.saveUdipValidateLog(listlog, log);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + temp.getTempleName() + "】关联的补录表【" + temp.getTableEnName() + "】数据入库后校验失败");
		}
		return list;
	}
	/**
	 * 手工补录的数据逻辑校验
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/validataTaskcaseInfo")
	@ResponseBody
	public List<String> validataTaskcaseInfo(String templeId, String caseId, String orgList) {

		// 统一获取信息
		Map<String, Object> getMap = this.taskCaseBS.getMap(templeId, caseId,false);
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
		List<String> org = ArrayUtils.asList(orgList, ",");
		List<String> orgList1 = Lists.newArrayList(org);
		/** 获取校验规则 */
		List<RptInputLstTempleRule> tempRule = this.templeRuleInfoBS.findEntityListByProperty("templeId", templeId);
		List<String> list = Lists.newArrayList();
		List<String> colList = (List<String>) getMap.get("colName");
		colList.add("DATAINPUT_ID");
		Map<String, List<RptInputListDataRuleInfo>> map = Maps.newHashMap();
		for (RptInputLstTempleRule temprule : tempRule) {
			RptInputListDataRuleInfo ruleInfo = this.dataRuleBS.getEntityById(temprule.getRuleId());

			if (!map.containsKey(ruleInfo.getRuleType())) {
				map.put(ruleInfo.getRuleType(), new ArrayList<RptInputListDataRuleInfo>());
			}
			map.get(ruleInfo.getRuleType()).add(ruleInfo);
		}
		/** 所能操作的数据状态 */
		List<String> dataState = Lists.newArrayList();
		dataState.add(UdipConstants.TASK_STATE_SAVE);
		dataState.add(UdipConstants.TASK_STATE_VALIDATE);
		dataState.add(UdipConstants.TASK_STATE_REFUSE);
		dataState.add(UdipConstants.TASK_STATE_DISPATCH);
		List<Map<String, Object>> dataList = Lists.newArrayList();

		List<RptInputLstValidateLog> listlog = Lists.newArrayList();
		List<RptInputLstValidateLog> listlog2 = Lists.newArrayList();
		// 校验日志实体
		RptInputLstValidateLog log = validateLogBS.getUdipValidateLog(caseId, "", (String) getMap.get("oper"), "", templeId, (String) getMap.get("data"));
		String caseOrNot = caseId;
		String caseData = (String) getMap.get("casedata");
		try {
			if ("1".equals(temp.getAllowInputHist())) {
				caseOrNot = null;
				caseData = null;
			}
			dataList = dataUtils.getData(null,(RptInputLstTempleInfo) getMap.get("temp"), (List<String>) getMap.get("keyCols"), null, "", colList, (Map<String, String>) getMap.get("colNameType"), 0, 0, caseData, dataState, org, getMap.get("oper"), caseOrNot);
			listlog = ValidateUtils.validate((RptTskIns) getMap.get("taskCase"), (RptInputLstTempleInfo) getMap.get("temp"), map, dataList, 0,(Map<String, RptInputLstTempleField>)getMap.get("colMap"));
		} catch (Exception e) {
		}
		listlog2 = ValidateUtils.validate((RptTskIns) getMap.get("taskCase"), (RptInputLstTempleInfo) getMap.get("temp"), map, caseData, org, ((RptInputLstTempleInfo) getMap.get("temp")).getOrgColumn(), caseOrNot);
		listlog.addAll(listlog2);
		
		validateLogBS.deleteUdipValidateLog(caseId, templeId, log.getOrgNo());
		if (listlog.isEmpty()) {
			try {
				Boolean check = dataUtils.updateData((RptInputLstTempleInfo) getMap.get("temp"), (Map<String, RptInputLstTempleField>) getMap.get("colMap"), dataList, (List<String>) getMap.get("keyCols"), caseData, UdipConstants.TASK_STATE_VALIDATE, getMap.get("data"), getMap.get("oper"), caseOrNot);
				if (check) {
					// 更改dataState状态
					this.dataStateBS.changerDataState(templeId, caseId, orgList1, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), UdipConstants.TASK_STATE_VALIDATE);
					// 新增一条数据操作记录
					this.authRecordBS.saveAuthRecord(templeId, caseId, UdipConstants.TASK_STATE_VALIDATE);
				}
			} catch (Exception e) {
			}
		}
		if (listlog.isEmpty()) {
			RptInputLstValidateLog log2 = new RptInputLstValidateLog();
			log2.setValidateResult("数据规则校验成功");
			listlog.add(log2);
			validateLogBS.saveUdipValidateLog(listlog, log);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + temp.getTempleName() + "】关联的补录表【" + temp.getTableEnName() + "】数据入库后校验成功");
		} else {
			list.add("数据规则校验失败");
			validateLogBS.saveUdipValidateLog(listlog, log);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + temp.getTempleName() + "】关联的补录表【" + temp.getTableEnName() + "】数据入库后校验失败");
		}
		return list;
	}

	/**
	 * 检查下发记录
	 * 
	 * @param templeId
	 * @param caseId
	 * @return 1:补录表已不存在或已修改 0:该批次补录已结束 dataState:任务状态(六种状态)
	 */
	@RequestMapping(value = "/authRecordDataType")
	@ResponseBody
	public String authRecordDataType(@RequestParam(defaultValue = "") String templeId, String caseId) {
		templeId = "";
		String[] ids = StringUtils.split(templeId, ',');
		String dataTypes = UdipConstants.TASK_STATE_VALIDATE;
		List<String> templeList = this.taskLockBS.getTaskTempLock(caseId);
		for (int i = 0; i < ids.length; i++) {
			if (templeList.contains(ids[i])) {
				return "2";
			}
			// 判断补录表是否存在
			RptInputLstTempleInfo temple = templeBS.getEntityById(ids[i]);
			List<BioneTableMetaData> tabMetaList = this.dataSourceBS.getTableMetaData(temple.getDsId(), temple.getTableEnName());
			if (CollectionUtils.isNotEmpty(tabMetaList)) {
				// 判断任务是否已经结束
				RptTskIns caseInfo = this.taskCaseBS.getEntityById(caseId);
				if (caseInfo.equals(null) || UdipConstants.CASE_STATE_END.equals(caseInfo.getSts())) {
					return "0";
				}
				BioneUser biOneUser = BioneSecurityUtils.getCurrentUserInfo();
				List<RptTskIns> rptTskInsList = this.taskCaseBS.findByPropertys(RptTskIns.class, new String[] { "exeObjId", "taskInstanceId", "orgNo" }, new Object[] { ids[i], caseId, biOneUser.getOrgNo() });
				if (CollectionUtils.isEmpty(rptTskInsList)) {// 判断状态是否异常
					return null;
				} else {
					if (UdipConstants.CASE_STATE_SUBMIT.equals(rptTskInsList.get(0).getSts())) {
						// 判断任务是否已经提交
						return UdipConstants.TASK_STATE_SUBMIT;
					} else {
						dataTypes = rptTskInsList.get(0).getSts();
					}
				}
			} else {
				return "1";
			}
		}
		return dataTypes;
	}

	/**
	 * 任务实例模板查询跳转
	 */
	@RequestMapping("/taskTempleSearch")
	public ModelAndView taskTempleSearch(String paramStr, String templeId, String caseId, String rowindex) throws UnsupportedEncodingException {
		ModelMap mm = new ModelMap();
		RptInputLstTempleInfo temp = templeBS.getEntityById(templeId);
		if (temp != null && StringUtils.isNotBlank(temp.getOrgColumn()) && StringUtils.isNotBlank(temp.getAllowInputLower()) && temp.getAllowInputLower().equals("no")) {
			mm.addAttribute("orgOwn", StringUtils2.javaScriptEncode(BioneSecurityUtils.getCurrentUserInfo().getOrgNo()));
			mm.addAttribute("orgColumn", StringUtils2.javaScriptEncode(temp.getOrgColumn()));
		} else if (temp != null && StringUtils.isNotBlank(temp.getOrgColumn()) && StringUtils.isNotBlank(temp.getAllowInputLower())) {
			mm.addAttribute("orgOwn", StringUtils2.javaScriptEncode(BioneSecurityUtils.getCurrentUserInfo().getOrgNo()));
		}
		mm.addAttribute("paramStr", StringUtils2.javaScriptEncode(URLDecoder.decode(paramStr, "utf-8")));
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		mm.addAttribute("rowindex", StringUtils2.javaScriptEncode(rowindex));
		return new ModelAndView(PATH + "task-case-temple-search", mm);
	}

	/**
	 * 手工补录的提交操作
	 * 
	 * @param templeId
	 * @param authable
	 * @param caseId
	 * @return
	 */
	@RequestMapping(value = "/submitTaskcaseTempleInfo", method = RequestMethod.POST)
	@ResponseBody
	public String submitTaskcaseTempleInfo(String templeId, String authable, String caseId, @RequestParam(defaultValue = "") String orgCode) {
		// 统一获取信息
		Map<String, Object> getMap = this.taskCaseBS.getMap(templeId, caseId,false);
		RptInputLstTempleInfo temp = (RptInputLstTempleInfo) getMap.get("temp");
		List<String> org = ArrayUtils.asList(orgCode, ",");// 根据用户选择的机构来提交。
//		List<String> orgList = Lists.newArrayList(org);
		if (StringUtils.isBlank(authable)) {
			authable = temp.getIsCheck();
		}
		List<String> dataState = Lists.newArrayList();
		dataState.add(UdipConstants.TASK_STATE_SAVE);
		dataState.add(UdipConstants.TASK_STATE_VALIDATE);
		dataState.add(UdipConstants.TASK_STATE_REFUSE);
		dataState.add(UdipConstants.TASK_STATE_DISPATCH);

		Boolean chackExcel = false;
		RptInputLstTempleInfo udipTemple = this.templeBS.getEntityById(templeId);
		this.taskCaseBS.updateAllErrorMark(udipTemple.getTempleId(),"02");
		String caseOrNot = caseId;
		String etlDate = (String) getMap.get("casedata");
		try {
			/** 无数据也可以提交2014-05-22 **/
//			int dataCount = dataUtils.getDataCount((UdipTemple) getMap.get("temp") , null,null, caseData,dataState.isEmpty()?null:dataState,org,getMap.get("oper"),caseOrNot);
			/*
			 * if(dataCount == 0){//无数据也可以提交2014-05-22 return "无数据提交"; }
			 */
			if ("1".equals(temp.getAllowInputHist())) {
				caseOrNot = null;
				etlDate = null;
			}

			if (authable.equals("1")) {
				chackExcel = dataUtils.updateDataState((RptInputLstTempleInfo) getMap.get("temp"), etlDate, UdipConstants.TASK_STATE_SUBMIT, org, caseOrNot);
			} else if (authable.equals("0")) {
				chackExcel = dataUtils.updateDataState((RptInputLstTempleInfo) getMap.get("temp"), etlDate, UdipConstants.TASK_STATE_SUCESS, org, caseOrNot);
			}
			if (chackExcel) {
				// 新增一条数据操作记录
				this.authRecordBS.saveAuthRecord(templeId, caseId, UdipConstants.TASK_STATE_SUBMIT);
				// 更改dataState状态
				// XXX 任务实例的状态改为由 陈磊 的任务流程控制
				// this.dataStateBS.changerDataState(templeId, caseId, orgList, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), UdipConstants.TASK_STATE_SUBMIT);
//				logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + udipTemple.getTempleName() + "】关联的补录表【" + udipTemple.getTableEnName() + "】数据提交成功");
			}
			return "";
		} catch (Exception e) {
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + udipTemple.getTempleName() + "】关联的补录表【" + udipTemple.getTableEnName() + "】数据提交失败");
			return e.getMessage();
		}
	}
	
//	/**
//	 * 批量提交操作
//	 * @param templeId
//	 * @param authable
//	 * @param caseId
//	 * @return
//	 */
//	@RequestMapping("/submitTaskcaseTempleInfoList")
//	@ResponseBody
//	public String submitTaskcaseTempleInfoList( String templeId, String authable, String caseId,String orgList) {
//		String chackExcel = "";
//		String chacktemp = "";
//		StringBuilder templeName = new StringBuilder();
//		String[] ids = templeId.split(",");
//		for (int i = 0; i < ids.length; i++) {
//			chacktemp = this.submitTaskcaseTempleInfo(ids[i], authable, caseId,orgList);
//			if(StringUtils.isNotBlank(chacktemp)){
//				templeName.append("模板").append((templeBS.getEntityById(ids[i])).getTempleName()).append("：").append(chacktemp).append("；");
//			}
//			chackExcel = chackExcel + chacktemp;
//		}
//		return templeName.toString();
//	}

	
	
	
//	/**
//	 * Excel批量数据合法校验
//	 */
//	@RequestMapping(value = "/validateDataFile")
//	@ResponseBody
//	public List<String> getValidateList(String id) {
//		return dataFileBS.saveDataFileById(id,this.getRequest().getRemoteAddr());
//	}
	
	
//	/**
//	 * 根据taskId获取所下发的任务实例
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping("/getCaseList")
//	@ResponseBody
// 	public Map<String, Object> dataTableList(Pager rf,String id){
//		SearchResult<UdipTaskCaseInfo> result = this.udipTaskCaseBS.getTaskCaseByTask(rf.getPageFirstIndex(), rf.getPagesize(),id);
//		Map<String, Object> objDefMap = Maps.newHashMap();
//		objDefMap.put("Rows", result.getResult());
//		objDefMap.put("Total", result.getTotalCount());
// 		return objDefMap;
//	}
//	/**
//	 * 根据caseid删除下发记录
//	 * @param caseId
//	 * @return
//	 */
//
//	@RequestMapping(value = "/taskcaseDelete")
//	@ResponseBody
//	public String taskcaseDelete(String caseId) {
//		try {
//			if(StringUtils.isNotBlank(caseId)){
//				String[] ids = caseId.split(",");
//				for (int i = 0; i < ids.length; i++) {
//					udipTaskCaseBS.removeEntityById(ids[i]);
//					dataFileBS.removeEntityByProperty("caseId", ids[i]);
//					udipAuthRecordBS.removeEntityByProperty("caseId", ids[i]);
//					udipDataStateBS.removeEntityByProperty("caseId", ids[i]);
//					udipCaseOrgBS.removeEntityByProperty("caseId", ids[i]);
//					udipTaskTempLockBS.removeEntityByProperty("caseId", ids[i]);
//					templeBS.setTaskTempLock(caseId,"");
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//			return "删除任务失败！";
//		}
//		return "ok";
//	}
//	
//	/**
//	 * 根据caseId删除补录数据
//	 * @param caseId
//	 * @return
//	 */
//	@RequestMapping(value = "/caseDataDelete")
//	@ResponseBody
//	public String caseDataDelete(String caseId) {
//		try {
//			if(StringUtils.isNotBlank(caseId)){
//				String[] ids = caseId.split(",");
//				for (int i = 0; i < ids.length; i++) {
//					UdipTaskCaseInfo taskCase = this.udipTaskCaseBS.getEntityById(ids[i]);
//					
//					List<UdipTaskTempleInfo> taskTemple = this.udipTaskTempleBS.findEntityListByProperty("taskId", taskCase.getTaskId());
//					for(UdipTaskTempleInfo temple : taskTemple){
//						UdipTemple temp = this.templeBS.getEntityById(temple.getTempleId());
//						dataUtils.deleteByCaseId( temp, taskCase.getEtlDate(),null, null, BioneSecurityUtils.getCurrentUserInfo().getLoginName(),ids[i]);
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//			return "删除补录数据失败！";
//		}
//		return "ok";
//	}
//	
//	/**
//	 * 根据caseid结束任务
//	 * @param caseId
//	 * @return
//	 */
//	@RequestMapping(value = "/taskOver")
//	@ResponseBody
//	public String taskOver(String caseId) {
//		List<UdipTaskCaseInfo> caseList = Lists.newArrayList();
//		try {
//			if(StringUtils.isNotBlank(caseId)){
//				caseList= udipTaskCaseBS.getTaskCaseById(ArrayUtils.asCollection(caseId));
//				templeBS.setTaskTempLock(caseId,"");
//				udipTaskBS.taskOver(caseList);
//			}
//		} catch (Exception e) {
//			logger.error(e);
//			return "结束任务失败!";
//		}
//		for(UdipTaskCaseInfo info :caseList){
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "结束任务【" + info.getCaseId() + "】,批次【"+info.getTaskSequnce()+"】");
//		}
//		return "ok";
//	}
//	@RequestMapping(value = "/taskStart")
//	@ResponseBody
//	public String taskStart(String caseId) {
//		List<UdipTaskCaseInfo> caseList = Lists.newArrayList();
//		try {
//			if(StringUtils.isNotBlank(caseId)){
//				caseList= udipTaskCaseBS.getTaskCaseById(ArrayUtils.asCollection(caseId));
//				udipTaskBS.taskStart(caseList);
//			}
//		} catch (Exception e) {
//			logger.error(e);
//			return "结束重启失败!";
//		}
//		for(UdipTaskCaseInfo info :caseList){
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "重启任务【" + info.getCaseId() + "】,批次【"+info.getTaskSequnce()+"】");
//		}
//		return "ok";
//	}
//	
//	
//	
//	
//	/**
//	 * 获取用户能补录的机构树
//	 * @param caseId
//	 * @return
//	 */
//	@RequestMapping(value = "/inputOrgTree.*", method = RequestMethod.GET)
//	@ResponseBody
//	public Map<String, List<CommonTreeNode>> inputOrgTree(String caseId , String templeId) {
//		Map<String, List<CommonTreeNode>> map = Maps.newHashMap();
//		UdipTemple temp = templeBS.getEntityById(templeId);
//		List<CommonTreeNode> orgList = udipTaskCaseBS.getOrgList(temp,caseId);
//		for (CommonTreeNode node : orgList) {
//			node.setIcon(this.getRequest().getContextPath() + "/" + node.getIcon());
//		}
//		map.put("orgList", orgList);
//		return map;
//	}
	
	/**
	 * 根据templeID与caseId查找校验信息
	 * 
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	@RequestMapping("/getTaskCaseValidateLog")
	@ResponseBody
	public Map<String, Object> getTaskCaseValidateLog(Pager rf, String templeId, String caseId) {
		SearchResult<RptInputLstValidateLog> searchResult = this.validateLogBS.getValidateLog(rf.getPageFirstIndex(), rf.getPagesize(), templeId, caseId);
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", searchResult.getResult());
		objDefMap.put("Total", searchResult.getTotalCount());
		return objDefMap;
	}
	@RequestMapping("/getValidateLog")
	@ResponseBody
	public String getValidateLog(String templeId, String taskInstanceId) {
		List<Object[]> logList = this.validateLogBS.getValidateLog(templeId, taskInstanceId);
		if(logList== null || logList.size()==0) {
			return "0";
		}else if(logList.size() > 0 ) {
			for(Object[] objArr : logList) {
				String result = (String) objArr[0];
				if(result.contains("校验失败")) {
					return "0";
				}
			}
		}
		return "1";
	}
	
//	/**
//	 * 回退数据操作
//	 * @param templeId
//	 * @param caseId
//	 * @return
//	 */
//	@RequestMapping(value = "/rollbackTaskcaseInfo")
//	@ResponseBody
//	public String rollbackTaskcaseInfo( String templeId, String caseId,String orgCode) {
//		UdipTemple temp = this.templeBS.getEntityById(templeId);
//		UdipTaskCaseInfo taskCase = this.udipTaskCaseBS.getEntityById(caseId);
////		List<String> org = udipTaskCaseBS.getOrgList(temp,null,caseId);
//		List<String> org = ArrayUtils.asList(orgCode, ",");
//		List<String> orgList = Lists.newArrayList(org);
//		try {
//			dataUtils.deleteByCaseId( temp, taskCase.getEtlDate(),null, org, BioneSecurityUtils.getCurrentUserInfo().getLoginName(),caseId);
//			
//			List<UdipDataLoadInfo> dataLoadInfoList = this.dataLoadBS.findByPropertys(UdipDataLoadInfo.class, new String[] { "templeId" }, new Object[] { temp.getTempleId() });
//			if (!dataLoadInfoList.isEmpty()) {
//				try {
//					dataUtils.loadData(temp, null, dataLoadInfoList, taskCase.getEtlDate(), orgList, taskCase.getCaseId());// 预装载
//				} catch (Exception e) {
//					logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + temp.getTempleName() + "】关联的补录表【"+temp.getTableName()+"】回退失败");
//				}
//			}
//			
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + temp.getTempleName() + "】关联的补录表【"+temp.getTableName()+"】回退删除成功");
//		} catch (Exception e) {
//			logger.error(e);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BioneSecurityUtils.getCurrentUserInfo().getUserId(), "补录模板【" + temp.getTempleName() + "】关联的补录表【"+temp.getTableName()+"】回退删除失败");
//			return "数据回退失败!";
//		}
//		//更改dataState状态
//		udipDataStateBS.changerDataState(templeId,caseId,orgList,BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),UdipConstants.TASK_STATE_DISPATCH);
//		return "ok";
//	}
//	
//	
//	/**
//	 * 查找数据是否存在状态记录
//	 */
//	@RequestMapping(value = "/findAuthRecordInfo")
//	@ResponseBody
//	public UdipDataStateInfo findAuthRecordInfo(String templeId , String caseId) {
//		String[] propVal = {templeId,caseId,BioneSecurityUtils.getCurrentUserInfo().getOrgNo(),BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo()}; 
//		String[] propName = {"templeId","caseId","orgCode","logicSysNo"}; 
//		List<UdipDataStateInfo> udipDataState = udipDataStateBS.findByPropertys(UdipDataStateInfo.class, propName, propVal);
//		if(!udipDataState.isEmpty()){
//			return udipDataState.get(0);
//		}else{
//			return null;
//		}
//	}
}
