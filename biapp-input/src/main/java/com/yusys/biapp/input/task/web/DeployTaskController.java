package com.yusys.biapp.input.task.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.task.entity.RptTskCatalog;
import com.yusys.biapp.input.task.entity.RptTskExeobjRel;
import com.yusys.biapp.input.task.entity.RptTskInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.task.service.DeployTaskBS;
import com.yusys.biapp.input.task.vo.TaskFlowNodeVO;
import com.yusys.biapp.input.task.web.vo.DeployTaskVO;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.service.TempleConstraintBS;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.service.RoleBS;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rpt/input/task")
public class DeployTaskController extends BaseController {


	@Autowired
	private DeployTaskBS deployTaskBS;
	@Autowired
	private AuthBS authBS;
	@Autowired
	private RoleBS roleBS;
	@Autowired
	private TempleConstraintBS templeConstraintBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {

		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		Map<String, List<String>>map = userObj.getAuthObjMap();
		List<String>roleList=map.get("AUTH_OBJ_ROLE");
//		List<String>roleList=map.get("AUTH_OBJ_ORG");
		String canOper = "0";
		if(userObj.getLoginName().equalsIgnoreCase("admin")){
			canOper="1";
		}else{
			if(roleList != null && roleList.size() > 0){
				for (String role : roleList) {
		        	if(StringUtils.isNotEmpty(role)&&roleBS.getEntityById(role)!=null)
//		        		if(roleBS.getEntityById(role).getRoleName().equals("业务管理员")&&userObj.getOrgNo().equals("817000000"))
	        			if("业务管理员".equals(roleBS.getEntityById(role).getRoleName())&&"817000000".equals(userObj.getOrgNo()))
		        		{
		        			canOper = "1";
		        			break;
		        		}
		        }
			}
		}
		return new ModelAndView("/input/task/task-index", "canOper", canOper);
	}
	
	@RequestMapping(value = "/getTaskTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTaskTree(String searchNm, String nodeId, String taskName){
		if(searchNm==null&&nodeId==null)
			return deployTaskBS.getRootRptTskInfoNode(this.getContextPath());
		else
			return deployTaskBS.getRptTskInfoNode(this.getContextPath(),searchNm, nodeId);
	}
	@RequestMapping(value = "/selectTrigger", method = RequestMethod.GET)
    public ModelAndView selectTrigger() {
        return new ModelAndView("/frame/task/task-trigger");
    }
    
	/**
	 * 得到任务类型
	 * @return
	 */
	@RequestMapping(value = "/getTaskType.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getTaskType() {
		return  this.deployTaskBS.getTaskType();
	}
	
	/**
	 * 得到任务类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getTaskNodeTypeBox.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getTaskNodeType() {
	    return  (List<CommonComboBoxNode>)this.deployTaskBS.getTaskNodeTypeList("TASK_NODE_TYPE").get("combox");
	}
	
	/**
	 * 新增指标目录
	 * @param indexCatalogNo
	 * @return
	 */
	@RequestMapping(value = "/newCatalog", method = RequestMethod.GET)
	public ModelAndView newCatalog(String upNo,String upName){
		try {
			upName = URLDecoder.decode(URLDecoder.decode(upName,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		ModelMap mm = new ModelMap();
		mm.put("upNo", StringUtils2.javaScriptEncode(upNo));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		return new ModelAndView("/input/task/task-catalog-edit", mm);
	}
	
	/**
	 * 新增指标目录
	 * @param indexCatalogNo
	 * @return
	 */
	@RequestMapping(value = "/initUpdateCatalog", method = RequestMethod.GET)
	public ModelAndView initUpdateCatalog(String catalogId,String upName){
		RptTskCatalog catalog = deployTaskBS.getRptTskCatalogByCatalogId(catalogId);
		try {
			upName = URLDecoder.decode(URLDecoder.decode(upName,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalog.getCatalogId()));
		mm.put("catalogName", StringUtils2.javaScriptEncode(catalog.getCatalogNm()));
		mm.put("remark", StringUtils2.javaScriptEncode(catalog.getRemark()));
		mm.put("upNo", StringUtils2.javaScriptEncode(catalog.getUpNo()));
		mm.put("upName", StringUtils2.javaScriptEncode(upName));
		return new ModelAndView("/input/task/task-catalog-edit", mm);
	}
	
	/**
	 * 保存指标目录
	 * @param model
	 */
	@RequestMapping(value = "/editIdxInputCatalog.*",method = RequestMethod.POST)
	public void createIdxInputCatalog(String catalogName,String remark,String upNo,String  catalogId) {
		
		this.deployTaskBS.createTskCatalog(catalogId,catalogName, remark, upNo);
	}
	
	@RequestMapping(value = "/testSameInputCatalogNm", method = RequestMethod.POST)
	@ResponseBody
	public boolean testSameInputCatalogNm(String upNo,String catalogName,String catalogId){
		return  this.deployTaskBS.testSameIndexCatalogNm( upNo, catalogName, catalogId);
	}

	@RequestMapping(value = "/testSameFlowNm", method = RequestMethod.POST)
	@ResponseBody
	public boolean testSameFlowNm(String taskDefId,String flowNm){
		return  this.deployTaskBS.testSameFlowNm( taskDefId, flowNm);
	}
	@RequestMapping(value = "/testRptIdxInputNm", method = RequestMethod.POST)
	@ResponseBody
	public boolean testRptIdxInputNm(String taskNm,String  catalogId,String taskId,String taskDefId){
		return  this.deployTaskBS.testRptDimItemNm(taskNm, catalogId,taskId,taskDefId);
	}
	
	/***
	 *  判断该指标目录下面是否具有指标
	 * @param  indexCatalogNo
	 * @return result
	 */
	@RequestMapping(value = "/canDeleteCatalog.*", method = RequestMethod.POST)
	@ResponseBody
	public String canDeleteCatalog(String catalogId) {
		return this.deployTaskBS.canDeleteCatalog(catalogId);
	}
	
	/**
	 * 删除指标目录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delCatalog/{id}", method = RequestMethod.POST)
	public Map<String,String> delCatalog(@PathVariable("id") String id) {
		Map<String,String> resultMap=Maps.newHashMap();
		this.deployTaskBS.cascadeDel(id);
		resultMap.put("msg", "0");
		return resultMap;
	}
	

	@RequestMapping(value = "/taskInfos", method = RequestMethod.GET)
	public ModelAndView taskInfos(String  nodeId,String nodeType,String taskId) {
		ModelMap mm = new ModelMap();
		//初始化为根节点
		if(StringUtils.isEmpty(nodeId))
		{
			nodeType = DeployTaskBS.CATALOG_TYPE;
			nodeId = DeployTaskBS.DEFAULT_ROOT;
		}
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		Map<String, List<String>>map = userObj.getAuthObjMap();
		List<String>roleList=map.get("AUTH_OBJ_ROLE");
//		List<String>roleList=map.get("AUTH_OBJ_ORG");
		String canOper = "0";
		if(userObj.getLoginName().equalsIgnoreCase("admin")){
			canOper="1";
		}else{
			if(roleList != null && roleList.size() > 0){
		        for (String role : roleList) {
		        	if(StringUtils.isNotEmpty(role)&&roleBS.getEntityById(role)!=null)
		        		if("业务管理员".equals(roleBS.getEntityById(role).getRoleName())&&"817000000".equals(userObj.getOrgNo()))
		        		{
		        			canOper = "1";
		        			break;
		        		}
		        }
			}
		}
        mm.put("canOper", canOper);
		//userObj
		if(nodeType.equals(DeployTaskBS.CATALOG_TYPE))
		{
			//查询目录下的任务信息
			return new ModelAndView("/input/task/task-list", mm);//deployTaskBS.getRptTskInfoList(pager, nodeId,null));
		}else{
			//如果查询的是单个任务
			mm.put("tskinfo", StringUtils2.javaScriptEncode(taskId));
			return new ModelAndView("/input/task/idx-input-task", mm);
		}
		
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
	@RequestMapping(value = "/getList", method = RequestMethod.POST)
	public Map<String, Object> getList(Pager pager,	String catalogId, String type, String taskName) {
		// Map<String, Object> cd = pager.getSearchCondition();
		// Map<String,String> filedInfo =  (Map<String, String>) cd.get("fieldValues");
		Map<String, Object> m=  deployTaskBS.getRptTskInfoList(pager, catalogId, type, taskName);

		/*****添加日志记录 *****/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append("对角色管理进行了一次查询，查询条件为：");
//
//		Map<String,String>nmMap=Maps.newHashMap();
//		nmMap.put("taskNm", "任务名称");
//		nmMap.put("taskType", "任务类型");
//		nmMap.put("exeObjType", "补录类型");
//		Map<String,Map<String,String>>exMap=Maps.newHashMap();
//
//		List<Map<String,Object>>nodes= this.deployTaskBS.getTaskTypeList();
//		
//		Map<String,String>tmpMap1=Maps.newHashMap();
//		for(Map<String,Object> type:nodes){
//			tmpMap1.put( (String)type.get("PARAM_VALUE"),(String)type.get("PARAM_NAME") );
//		}
//		exMap.put("taskType", tmpMap1);
//		
//		Map<String,String>tmpMap=Maps.newHashMap();
//		tmpMap.put("01", "指标补录");
//		tmpMap.put("02", "明细补录");
//		exMap.put("exeObjType", tmpMap);
//		roleBS.saveQueryLog("补录任务管理",  user.getUserId(), user.getLoginName(),nmMap,filedInfo,exMap);
		return m;
	}
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/taskInfoFrame" , method = RequestMethod.GET)
	public  ModelAndView taskInfoFrame(String taskId,String catalogId,String catalogName,String canOper){
		
		ModelMap mm = new ModelMap();
		try {
			if(taskId!=null)
			{
				taskId = URLDecoder.decode(URLDecoder.decode(taskId,"UTF-8"),"UTF-8");
				mm.put("taskId", StringUtils2.javaScriptEncode(taskId));
				mm.put("deployTaskVO", StringUtils2.javaScriptEncode(JSON.toJSONString(deployTaskBS.getTaskById(taskId,null))));
			}
			if(catalogId!=null)
			{
				catalogId = URLDecoder.decode(URLDecoder.decode(catalogId,"UTF-8"),"UTF-8");
				mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
			}
			if(catalogName!=null)
			{
				catalogName = URLDecoder.decode(URLDecoder.decode(catalogName,"UTF-8"),"UTF-8");
				mm.put("catalogName", StringUtils2.javaScriptEncode(catalogName));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mm.put("canOper", StringUtils2.javaScriptEncode(canOper));
		return new ModelAndView("/input/task/task-info-frame-weihai", mm);
		
	}
	
	@RequestMapping(value = "/getTaskList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getTaskList() {
		return this.deployTaskBS.getTaskList();
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/getTaskById" , method = RequestMethod.POST)
	@ResponseBody
	public 	DeployTaskVO getTaskById(String taskId){
		return deployTaskBS.getTaskById(taskId,null);
	}

	
	@RequestMapping(value = "/getBaseTaskById" , method = RequestMethod.POST)
	@ResponseBody
	public 	DeployTaskVO getBaseTaskById(String taskId){
		return deployTaskBS.getBaseTaskById(taskId);
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/base" , method = RequestMethod.GET)
	public  String toBase(){
		return "/input/task/task-info-base";
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/type" , method = RequestMethod.GET)
	public  String toType(){
		return "/input/task/task-info-type";
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/node" , method = RequestMethod.GET)
	public  ModelAndView toNode(){
		return new ModelAndView("/input/task/task-info-node", "defaultTaskDefId", "1");
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/chooseExeObjs" , method = RequestMethod.GET)
	public ModelAndView chooseExeObjs(String selectedObjs,String parentRowIndex){
		
		if(selectedObjs!=null){
			try {
				selectedObjs = URLDecoder.decode(URLDecoder.decode(selectedObjs,"UTF-8"),"UTF-8");
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		}
		ModelMap mm = new ModelMap();
		mm.put("selectedObjs", StringUtils2.javaScriptEncode(selectedObjs));
		mm.put("parentRowIndex", StringUtils2.javaScriptEncode(parentRowIndex));
		
		return new ModelAndView("/input/task/task-idxdimref", mm);
	}
	
	
	/***
	 *  判断该指标目录下面是否具有指标
	 * @param  indexCatalogNo
	 * @return result
	 */
	@RequestMapping(value = "/getTaskNodeList.*", method = RequestMethod.POST)
	@ResponseBody
	public  Map<String, Object>getTaskNodeList(String taskDefId) {
		if(taskDefId==null) return null;
		 return  deployTaskBS.getTaskNodeList(taskDefId);
	}
	/**
     * 流程管理节点类型
     * 
     * @return
     */
    @RequestMapping(value = "/getTaskNodeType.*", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getTaskNodeTypeList(String paramTypeNo) {
        return this.deployTaskBS.getTaskNodeTypeList(paramTypeNo);

    }

	/**
	 * 维度机构树 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getOrgTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getOrgTree(String orgs) {
		return this.deployTaskBS.getOrgTree(orgs);

	}
	
	// 获取授权对象-下拉菜单
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getAuthObjCombo.*")
	@ResponseBody
	public List<Map> getAuthObjCombo() {
		List<Map> authObjComboList = new ArrayList<Map>();
		// 获取授权对象
		List<BioneAuthObjDef> objs = this.authBS
				.getEntityList(BioneAuthObjDef.class);
		// 获取授权对象id列表
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authStrs = this.authBS.getObjDefNoBySys(userObj
				.getCurrentLogicSysNo(), true);
		if (objs != null) {
			for (BioneAuthObjDef authObj : objs) {
				if (authStrs.contains(authObj.getObjDefNo())
						&&!authObj.getObjDefNo().equals("AUTH_OBJ_GROUP")
						&&!authObj.getObjDefNo().equals("AUTH_OBJ_ORG")
						&&!authObj.getObjDefNo().equals("AUTH_OBJ_DEPT")
						&&!authObj.getObjDefNo().equals("AUTH_OBJ_POSI")) {
					//AUTH_OBJ_DEPT, text=部门}, {id=AUTH_OBJ_POSI, text=岗位
					Map<String, String> objMap = new HashMap<String, String>();
					objMap.put("id", authObj.getObjDefNo());
					objMap.put("text", authObj.getObjDefName());
					authObjComboList.add(objMap);
				}
			}
		}
		return authObjComboList;
	}

	@RequestMapping(value = "/getAuthObjName", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String getAuthObjName(String paramValue) {
		if(StringUtils.isEmpty(paramValue)) {
			return "";
		}
		// 获取授权对象
		List<BioneAuthObjDef> objs = this.authBS.getEntityList(BioneAuthObjDef.class);
		for(BioneAuthObjDef obj: objs) {
			if(paramValue.trim().equals(obj.getObjDefNo())) {
				return obj.getObjDefName();
			}
		}
		return "";
	}
	
	/**
	 * 保存维度类型
	 * @param model
	 */
	/*
	@RequestMapping(value = "/saveTask"  ,method = RequestMethod.POST)
	public void saveTask(String deployTask){

		deployTaskBS.localSaveTask( deployTask);
		
	}
	*/
	
	@RequestMapping(value = "/saveTask"  ,method = RequestMethod.POST)
	public void saveTask(@RequestBody DeployTaskVO deployTask){
		// String saveMark = StringUtils.isEmpty(deployTask.getRptTskInfo().getTaskId())?"01":"03";
		deployTaskBS.localSaveTask( deployTask);
		/******添加日志记录******/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append(saveMark.equals("01") ? "新增" : "修改")
//			.append("了一个任务,任务的名称为:").append(deployTask.getRptTskInfo().getTaskNm());
//		roleBS.saveLog(saveMark, "补录任务管理 ", buff.toString(), user.getUserId(), user.getLoginName());
	}

	/**
	 * 保存任务
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/saveFlow", method = RequestMethod.POST)
	@ResponseBody
	public String saveFlow(String taskDefId,String flowNm) {
		return deployTaskBS.saveFlow(taskDefId,flowNm);
	}
	

	/**
	 * 保存任务
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/saveTaskFlowNode", method = RequestMethod.POST)
	@ResponseBody
	public String saveTaskFlowNode(@RequestBody TaskFlowNodeVO taskFlowNodeVO){
		return deployTaskBS.saveTaskFlowNode(taskFlowNodeVO);
	}
	

	/**
	 * 保存维度类型
	 * @param model
	 */
	/*
	@RequestMapping(value = "/deployTask"  ,method = RequestMethod.POST)
	public void deployTask(String deployTask,String orgNos){

		deployTaskBS.startTask( deployTask,orgNos);
		
	}
	*/
	@RequestMapping(value = "/deployTask"  ,method = RequestMethod.POST)
	public void deployTask(@RequestBody DeployTaskVO DeployTaskVO){

		deployTaskBS.startWeihaiTask(DeployTaskVO);
		
	}
	
	@RequestMapping(value = "/getDeployAuthObjInfo"  ,method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> getDeployAuthObjInfo(@RequestBody DeployTaskVO deployTaskVO){
		return this.deployTaskBS.getDeployAuthObjInfo(deployTaskVO);
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/selectDeployDept" , method = RequestMethod.GET)
	public ModelAndView  selectDeployDept(String taskId,String operType,String insId, String exeObjNm){

		ModelMap mm = new ModelMap();
		mm.put("taskId", StringUtils2.javaScriptEncode(taskId));
		mm.put("defaultTaskDefId", "1");
		RptTskInfo  tinfo =deployTaskBS.getSingleRptTskInfo(taskId,null);
		mm.put("triggerType", StringUtils2.javaScriptEncode(tinfo.getTriggerType()));
		mm.put("orgType", StringUtils2.javaScriptEncode(tinfo.getTaskType()));
		mm.put("operType", StringUtils2.javaScriptEncode(operType));
		mm.put("insId", StringUtils2.javaScriptEncode(insId));
		mm.put("taskTitle", StringUtils2.javaScriptEncode(exeObjNm));
		
		if("del".equals(operType)){
			Map<String,List<String>> insNodes = deployTaskBS.getTskInsNode(insId);
			mm.put("insNodes", StringUtils2.javaScriptEncode(JSON.toJSONString(insNodes)));
		}
		
		try {
			DeployTaskVO task = deployTaskBS.getDeployTaskById(taskId,null);
			if(task != null){
				mm.put("deployTaskVO", StringUtils2.javaScriptEncode(JSON.toJSONString(task)));
			}
		} catch (Exception e) {
		}
		return new ModelAndView("/input/task/task-deploy", mm);
	}
	
	@RequestMapping(value = "/tskInsNode", method = RequestMethod.GET)
	@ResponseBody
	public void delTskInsNode(String insId,String nodes) {
		deployTaskBS.delTskInsNode(insId,nodes);
	}
	
	/*
	private List<String>getOrgNms(List<String>orgNos){
		List<String>orgNmsList=Lists.newArrayList();
		for(String orgNo:orgNos){
			orgNmsList.add(orgBS.findOrgInfoByOrgNo(orgNo).getOrgName());
		}
		return orgNmsList;
	}
	*/

	@RequestMapping(value = "/getDeployDeptTreeNode", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode>  getDeployDeptTreeNode(CommonTreeNode parentNode){
		List<CommonTreeNode>list =  deployTaskBS.getDeployDeptTreeNode(parentNode);
		return list;
	}
	/**
	 * 删除任务
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delTask/{ids}", method = RequestMethod.POST)
	@ResponseBody
	public String deleteTasks(@PathVariable("ids") String ids) {
		
		/**********添加日志记录*************/
		
//		List<RptTskInfo> userList = Lists.newArrayList();
//		String[] idArray = ids.split(",");
//		for(String id : idArray){
//			if(id.contains("@"))
//				id = id.substring(0, id.indexOf("@"));
//			userList.add(deployTaskBS.getSingleRptTskInfo(id,null));
//		}
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]删除了").append(userList.size()).append("个补录,任务的名称为:");
//		boolean isFirst=true;
//		for(RptTskInfo tsk:userList)
//		{
//			if(isFirst)
//				isFirst=false;
//			else
//				buff.append(",");
//			buff.append("[").append(tsk.getTaskNm()).append("]");
//		}
//		deployTaskBS.saveLog("02", "补录任务管理 ", buff.toString(), user.getUserId(), user.getLoginName());
		
		//return deployTaskBS.deleteTasksCascade(ids);
		//删除前校验是否已经下发过任务 20190605
		//删除的所有任务都是停用状态不检查是否下发任务 20190814
		boolean flag = deployTaskBS.checkTaskinsByTaskid(ids);
		return flag == false ? "0" : deployTaskBS.batchDeleteTasks(ids);
	}
	/**
	 * 发布
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deployTaskWithoutCheck", method = RequestMethod.POST)
	@ResponseBody
	public boolean deployTaskWithoutCheck( String taskId,String orgNos,String dataDate) {
		return deployTaskBS.deployTask(taskId, orgNos, dataDate);
	}

	/**
	 * 发布
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/changeTaskSts", method = RequestMethod.POST)
	@ResponseBody
	public boolean changeTaskSts( String tskIds) {
		return deployTaskBS.changeTaskSts(tskIds);
	}
	
	
	/**
	 * 停止触发器
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/stopTrigger", method = RequestMethod.POST)
	@ResponseBody
	public boolean stopTrigger(String taskId,String triggerId) {
		return deployTaskBS.stopTrigger(taskId,triggerId);
	}
	

	
	/**
	 * 判断是否可以停止触发器
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/canStopTrigger", method = RequestMethod.POST)
	@ResponseBody
	public boolean canStopTrigger(String taskId,String triggerId) {
		return deployTaskBS.hasTrigger(taskId,triggerId);
	}
	
	/**
	 * 指标信息框架页
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/selectObjBox" , method = RequestMethod.GET)
	public String  selectObjBox(String selectedType){
		if(selectedType.equals("01"))
			return  "/input/index/idx-datainput-templaterel" ;
		else
			return  "/input/temple/input-list-temple-select";
	}
	
	
	

	/**
	 * 指标信息框架页
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/dealTask" , method = RequestMethod.GET)
	public ModelAndView dealTask(String taskInstanceId,boolean canEdit){
		
		RptTskExeobjRel exeobj = deployTaskBS.getRptTskExeobjRelByInstanceId(taskInstanceId);
		RptTskIns rptTskIns =  deployTaskBS.getRptTskInsById(taskInstanceId);
		
		
		//01 指标补录  02 明细补录
		ModelMap mm = new ModelMap();
		if(exeobj.getId().getExeObjType().equals("01")){
			mm.put("templateId", StringUtils2.javaScriptEncode(exeobj.getId().getExeObjId()));
			mm.put("taskInstanceId", StringUtils2.javaScriptEncode(taskInstanceId));
			mm.put("canEdit", canEdit);
			mm.put("taskId", StringUtils2.javaScriptEncode(rptTskIns.getTaskId()));
			mm.put("dataDate", StringUtils2.javaScriptEncode(rptTskIns.getDataDate()));
			return new ModelAndView("/input/index/idx-datainput-template-deal", mm);
		}else if(exeobj.getId().getExeObjType().equals("02")){
			mm.put("templateId", StringUtils2.javaScriptEncode(exeobj.getId().getExeObjId()));
			mm.put("taskInstanceId", StringUtils2.javaScriptEncode(taskInstanceId));
			mm.put("taskNodeInstanceId", StringUtils2.javaScriptEncode(rptTskIns.getTaskNodeInstanceId()));
			mm.put("canEdit", canEdit);
			mm.put("autoLoad","1".equals(rptTskIns.getLoadDataMark()));
			mm.put("taskId", StringUtils2.javaScriptEncode(rptTskIns.getTaskId()));
			mm.put("dataDate", StringUtils2.javaScriptEncode(rptTskIns.getDataDate()));
			mm.addAttribute("orgNo", StringUtils2.javaScriptEncode(BioneSecurityUtils.getCurrentUserInfo().getOrgNo()));
			List<RptInputLstTempleConst> keyList = this.templeConstraintBS.findEntityListByProperty("templeId", exeobj.getId().getExeObjId());
			if(keyList!=null&&!keyList.isEmpty())
			{
				mm.addAttribute("keyColumn", StringUtils2.javaScriptEncode(keyList.get(0).getKeyColumn()));
			}
			return new ModelAndView("/input/input/task-case-info", mm);
		}
		return null;
	}
	

	/**
	 * 跳转到统一任务节点配置
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/toUnifyNode" , method = RequestMethod.GET)
	public ModelAndView toUnifyNode(String taskId,String taskDefId,String orgType){
		
		//01 指标补录  02 明细补录
		ModelMap mm = new ModelMap();
		mm.put("taskDefId", StringUtils2.javaScriptEncode(taskDefId));
		mm.put("orgType", StringUtils2.javaScriptEncode(orgType));
		return new ModelAndView("/input/task/task-unify-node", mm);
	}

	/**
	 * 跳转到统一任务节点配置
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/toUnifyNodeFrame" , method = RequestMethod.GET)
	public ModelAndView  toUnifyNodeFrame(String nodeType,String taskNodeDefId,String taskOrderno,String taskNodeNm){

		
		if(taskNodeNm!=null){
			try {
				taskNodeNm = URLDecoder.decode(URLDecoder.decode(taskNodeNm,"UTF-8"),"UTF-8");
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		}
		//01 指标补录  02 明细补录
		ModelMap mm = new ModelMap();
		mm.put("nodeType", StringUtils2.javaScriptEncode(nodeType));
		mm.put("taskNodeDefId", StringUtils2.javaScriptEncode(taskNodeDefId));
		mm.put("taskOrderno", StringUtils2.javaScriptEncode(taskOrderno));
		mm.put("taskNodeNm", StringUtils2.javaScriptEncode(taskNodeNm));
		return new ModelAndView("/input/task/task-unify-node-frame", mm);
	}
	/**
	 * 跳转到统一任务节点配置
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/toSeparateNode" , method = RequestMethod.GET)
	public ModelAndView toSeparateNode(String taskId){
		//01 指标补录  02 明细补录
		taskId = StringUtils2.javaScriptEncode(taskId);
		return new ModelAndView("/input/task/task-separate-node", "taskId", taskId);
	}

	/**
	 * 跳转到统一任务节点配置
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/toCustom" , method = RequestMethod.GET)
	public ModelAndView toCustom(String needInit){
		if(StringUtils.isEmpty(needInit))
			needInit = "1";

		ModelMap mm = new ModelMap();
		mm.put("needInit", needInit);
		return new ModelAndView("/input/task/task-unify-node-select-custom");
	}

	/**
	 * 跳转到统一任务节点配置
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/toCommon" , method = RequestMethod.GET)
	public ModelAndView  toCommon(String needInit){
		if(StringUtils.isEmpty(needInit))
			needInit = "1";

		ModelMap mm = new ModelMap();
		mm.put("needInit", needInit);
		
		return new ModelAndView("/input/task/task-unify-node-select-common");
	}
	

	// 获取授权对象树(暂包含:用户，角色，机构，授权组等)
	@RequestMapping("/getTypeTree.*")
	@ResponseBody
	public List<CommonTreeNode> getTypeTree(String objDefNo, String  handleType,String orgNo,String searchNm,String nodeType) {
		return deployTaskBS.getTypeTree(this.getContextPath(), objDefNo,   handleType,orgNo,searchNm,nodeType);
	}

	@RequestMapping(value="/getParentOrgIds.*",method=RequestMethod.POST)
	@ResponseBody
	public List<String>getParentOrgIds(String orgNo){
		List<String>orgList = Lists.newArrayList();
		for(String org:orgNo.split(",")){
			if(StringUtils.isNotEmpty(org))
				orgList.add(org);
		}
		List<String>returnList =  deployTaskBS.getParentOrgIds(orgList);
		return returnList;
	}

	@RequestMapping(value = "/testDulpDeployTask", method = RequestMethod.POST)
	@ResponseBody
	public List<String> testDulpDeployTask(String selectedOrgs,String dataDate,String taskId){
		return this.deployTaskBS.getDulpDeployTask(selectedOrgs,dataDate,taskId);
	}


	@RequestMapping(value = "/getListWorkFlow", method = RequestMethod.POST)
	@ResponseBody
	public  List<CommonTreeNode>getListWorkFlow(){
		return this.deployTaskBS.getListWorkFlow();
	}
	/**
	 * 跳转到统一任务节点配置
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/flowManager" , method = RequestMethod.GET)
	public String  flowManager(){
		return "/input/task/task-flowmanager";
	}


	/**
	 * 跳转到统一任务节点配置
	 * @param taskInstanceId
	 * @return
	 */
	@RequestMapping(value = "/editFlow" , method = RequestMethod.GET)
	public ModelAndView  editFlow(String taskDefId,String flowNm){
		ModelMap mm = new ModelMap();
		mm.put("taskDefId", StringUtils2.javaScriptEncode(taskDefId));
		if(StringUtils.isNotEmpty(flowNm)){
			try {
				flowNm = URLDecoder.decode(URLDecoder.decode(flowNm,"UTF-8"),"UTF-8");
				mm.put("flowNm", StringUtils2.javaScriptEncode(flowNm));
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		}
		return new ModelAndView("/input/task/task-editflow",mm);
	}
	

	/***
	 *  判断该指标目录下面是否具有指标
	 * @param  indexCatalogNo
	 * @return result
	 */
	@RequestMapping(value = "/delTaskFlow", method = RequestMethod.POST)
	@ResponseBody
	public String delTaskFlow(String taskDefId) {
		//删除流程前校验是否有任务关联
		boolean flag = this.deployTaskBS.checkTaskByFlowid(taskDefId);
		return flag == false ? "0": this.deployTaskBS.delTaskFlow(taskDefId);
	}
	
	@RequestMapping(value = "/getPubOrgTree", method = RequestMethod.POST)
    @ResponseBody
    public List<CommonTreeNode> getOrgTree(String id, String searchName,
            String srcCode,String orgType) {
        return this.deployTaskBS.getAuthOrgTree(id, searchName,orgType);

    }
	
    /**
     * chenhx 20171121
     * 新增后置依赖任务
     * 后置依赖任务面板
     */
    @RequestMapping(value = "/afterTaskOption", method = RequestMethod.GET)
	public ModelAndView afterTaskOptionWin(String triggerType,String taskId) {
	    Map<String, Object> resMap = Maps.newHashMap();
	    resMap.put("triggerType", StringUtils2.javaScriptEncode(triggerType));
	    resMap.put("taskId", StringUtils2.javaScriptEncode(taskId));
	    return new ModelAndView("/input/task/task-afterTaskOption",resMap);
    }
	
    /**
     * chenhx 20171121
     * 新增后置依赖任务
     * 后置依赖任务tab页
     */
	@RequestMapping(value = "/afterTaskOptionTabItems", method = RequestMethod.GET)
	public ModelAndView afterTaskOptionTabItemsWin(String taskNodeDefId,String taskOrderno,String taskNodeNm,String nodeType) {
	    Map<String, Object> resMap = Maps.newHashMap();
	    resMap.put("taskNodeDefId", StringUtils2.javaScriptEncode(taskNodeDefId));
	    resMap.put("taskOrderno", StringUtils2.javaScriptEncode(taskOrderno));
	    resMap.put("taskNodeNm", StringUtils2.javaScriptEncode(taskNodeNm));
	    resMap.put("nodeType", StringUtils2.javaScriptEncode(nodeType));
	    return new ModelAndView("/input/task/task-afterTaskOption-tabItems",resMap);
	}

	/**
	 * @description: 任务下发历史页面
	 * @author 黄正鑫
	 * @date 2021/7/7 14:24
	 */
	@RequestMapping(value = "/deptView" , method = RequestMethod.GET)
	public ModelAndView  deptView(String taskId){
		ModelMap mm = new ModelMap();
		mm.put("taskId", StringUtils2.javaScriptEncode(taskId));
		return new ModelAndView("/input/task/task-dept-view", mm);
	}

	/**
	 * @description: 任务下发历史
	 * @author 黄正鑫
	 * @date 2021/7/7 14:14
	 */
	@RequestMapping(value = "/getTaskDeptList.*" , method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getTaskDeptList(String taskId){
		if(StringUtils.isNotEmpty(taskId)) {
			return deployTaskBS.getTaskDeptList(taskId);
		}
		return null;
	}

	/**
	 * @方法描述: 获取同步行内机构树（辖内机构+授权机构）
	 * @创建人: huzq1 
	 * @创建时间: 2021/11/23 11:15
	  * @param searchName
	 * @param orgType
	 * @return
	 **/
	@ResponseBody
	@RequestMapping(value = "/getSyncPubOrgTree", method = RequestMethod.POST)
	public List<CommonTreeNode> getSyncOrgTree(String searchName) {
		return this.deployTaskBS.getSyncOrgTree(searchName);
	}
}
