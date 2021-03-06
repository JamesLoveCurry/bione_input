package com.yusys.biapp.input.task.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.greenpineyu.fel.common.StringUtils;
import com.yusys.biapp.input.dict.utils.DataUtils;
import com.yusys.biapp.input.index.service.IdxDataInputBS;
import com.yusys.biapp.input.input.entity.RptListOperLog;
import com.yusys.biapp.input.input.entity.RptListOperLogPK;
import com.yusys.biapp.input.inputTable.service.InputTableBS;
import com.yusys.biapp.input.task.entity.RptTskExeobjRel;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.task.entity.RptTskInsLog;
import com.yusys.biapp.input.task.entity.RptTskNodeIns;
import com.yusys.biapp.input.task.repository.TaskDao;
import com.yusys.biapp.input.task.repository.TaskOperDao;
import com.yusys.biapp.input.task.web.vo.DeployTaskVO;
import com.yusys.biapp.input.task.web.vo.TaskOperListVO;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.biapp.input.utils.DateUtils;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
@Service
@Transactional(readOnly = true)
public class DeployTaskOperBS extends BaseBS<Object> {

	// private final Log log = LogFactory.getLog(DeployTaskOperBS.class);
	@Autowired
	private TaskOperDao taskOperDao;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private IdxDataInputBS idxDataInputBS;
	@Autowired
	private InputTableBS inputTableBS;
	@Autowired
	private  OrgBS orgBS;
	@Autowired
	private TempleBS templeBS;
	@Autowired
	private DataSourceBS dataSourceBS;
	@Autowired
	private DataUtils dataUtils ;
	@Autowired
	private DeployTaskBS  deployTaskBS;
	@Autowired
	private RptOrgInfoBS rptOrgInfoBS;
	
	
	public static final String SAVE="save";//??????
	public static final String SUBMIT="submit";//??????
	public static final String REJECT="reject";//??????
	public static final String SUCCESS="success";//??????
	public static final String REFUSE="refuse";//??????
	
	
	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	public Map<String, Object> getCurrenTaskByUserInfo(Pager pager,
			String searchType, String taskIndexType) {

		List<TaskOperListVO> voList = Lists.newArrayList();
		/*****?????????????????? *****/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("??????[").append(user.getLoginName()).append("]").append("???????????????????????????????????????????????????");
//		Map<String, Object> cd = pager.getSearchCondition();
//		Map<String,String> filedInfo =  (Map<String, String>) cd.get("fieldValues");
		
		PageMyBatis<Map<String, Object>> taskMaps = getTaskBySearchType(pager,
				searchType,taskIndexType);

//		Map<String,String>nmMap=Maps.newHashMap();
//		nmMap.put("queryType", "????????????");
//		nmMap.put("orgNoID", "????????????");
//		nmMap.put("deployUserNm", "???????????????");
//		nmMap.put("dataDate", "????????????");
//		nmMap.put("sts", "????????????");
//		nmMap.put("taskType", "????????????");
//		nmMap.put("taskNm", "????????????");
//		Map<String,Map<String,String>>exMap=Maps.newHashMap();
//		Map<String,String>tmpMap=Maps.newHashMap();
//		tmpMap.put("0", "?????????");
//		tmpMap.put("1", "????????????");
//		exMap.put("queryType", tmpMap);
//		if(filedInfo.get("orgNoID")!=null){
//			Map<String,String>tmpMap1=Maps.newHashMap();
//			BioneOrgInfo orgInfo = orgBS.getEntityByProperty(BioneOrgInfo.class, "orgNo", filedInfo.get("orgNoID"));
//			tmpMap1.put(orgInfo.getOrgNo(), orgInfo.getOrgName());
//			exMap.put("orgNoID", tmpMap1);
//		}
//
//		Map<String,String>tmpMap2=Maps.newHashMap();
//		tmpMap2.put("1", "????????????");
//		tmpMap2.put("2", "????????????");
//		exMap.put("sts", tmpMap2);
//
//		Map<String,String>tmpMap3=Maps.newHashMap();
//		List<Map<String,Object>>nodes= this.deployTaskBS.getTaskTypeList();
//		for(Map<String,Object> type:nodes){
//			tmpMap3.put( (String)type.get("PARAM_VALUE"),(String)type.get("PARAM_NAME") );
//		}
//		exMap.put("taskType", tmpMap3);
		
		if (taskMaps != null && !taskMaps.isEmpty()) {
			BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
			for (Map<String, Object> taskMap : taskMaps) {
				TaskOperListVO vo = new TaskOperListVO();
				//???mybatis??????Timestamp???String??????java???
				vo.setStartTime(taskMap.get("START_TIME").toString());
				vo.setTaskNm((String) taskMap.get("TASK_NM"));
				vo.setTaskTypeNm((String) taskMap.get("TASK_TYPE_NM"));
				vo.setCreator((String) taskMap.get("CREATOR"));
				vo.setTaskExeObjNm((String) taskMap.get("TASK_EXEOBJ_NM"));
				vo.setTaskExeObjTypeNm((String) taskMap.get("TASK_EXE_OBJ_TYPE_NM"));
				vo.setTaskInstanceId((String) taskMap.get("TASK_INSTANCE_ID"));
				vo.setTaskNodeInstanceId((String) taskMap.get("TASK_NODE_INSTANCE_ID"));
				vo.setSts((String) taskMap.get("STS"));
				Object taskTitle = taskMap.get("TASK_TITLE");
				vo.setTaskTitle(taskTitle==null?"":(String)taskTitle);
				vo.setDataDate((String) taskMap.get("DATA_DATE"));
				if(taskMap.containsKey("ORG_NAME"))
					vo.setOrgName((String) taskMap.get("ORG_NAME"));
				if(taskMap.containsKey("ORG_NO"))
					vo.setOrgNo((String) taskMap.get("ORG_NO"));
				if(taskMap.containsKey("DEPLOY_ORG"))
					vo.setDeployOrg((String) taskMap.get("DEPLOY_ORG"));
				if(taskMap.containsKey("DEPLOY_ORGNO"))
					vo.setDeployOrgNo((String) taskMap.get("DEPLOY_ORGNO"));
				String canDelete = "0";
				if(bioneUser.getLoginName().equalsIgnoreCase("admin"))
					canDelete = "1";
				else if(taskMap.containsKey("CREATE_USER"))
					canDelete = taskMap.get("CREATE_USER").equals(bioneUser.getUserId())?"1":"0";
				vo.setOrgNm((String) taskMap.get("ORG_NM"));
				vo.setCatalogNm((String) taskMap.get("CATALOG_NM"));
				vo.setCanDelete(canDelete);
				vo.setTaskId(taskMap.containsKey("TASK_ID") ? (String) taskMap.get("TASK_ID") : "0");
				vo.setTemplateId(taskMap.containsKey("TEMPLATE_ID") ? (String) taskMap.get("TEMPLATE_ID") : "0000");
				vo.setTaskNodeNm((String) taskMap.get("TASK_NODE_NM"));
				voList.add(vo);
			}
		}
		Map<String, Object> rsMap = new HashMap<String, Object>();
		rsMap.put("Rows", voList);
		rsMap.put("Total", taskMaps.getTotalCount());
		return rsMap;
	}

	/**
	 * ???????????????????????? 1 ????????? 2 ????????? 3 ?????????
	 * 
	 * @param pager
	 * @param searchType
	 * @return
	 */
	private PageMyBatis<Map<String, Object>> getTaskBySearchType(Pager pager,
			String searchType,String taskIndexType) {

		Map<String, Object> map = Maps.newHashMap();

		if (pager != null && StringUtils.isNotEmpty(pager.getCondition())) {
			JSONObject json = JSON.parseObject(pager.getCondition());
			JSONArray array = json.getJSONArray("rules");
			for (int i = 0; i < array.size(); i++) {
				JSONObject temp = array.getJSONObject(i);
				String filedName = (String) temp.get("field");
				if (filedName.equals("catalogNm")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
					map.put("catalogNm", "%" + temp.get("value") + "%");
					}
					continue;
				}
				if (filedName.equals("taskNm")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
					map.put("taskNm", "%" + temp.get("value") + "%");
					}
					continue;
				}
				if (filedName.equals("taskType")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						map.put("taskType", temp.get("value"));
					}
					continue;
				}
				if (filedName.equals("sts")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						map.put("sts", temp.get("value"));
					}
					continue;
				}
				if (filedName.equals("dataDate")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						map.put("dataDate", ((String)temp.get("value")).replaceAll("-", ""));
					}
					continue;
				}
				if(filedName.equals("deployUserNm")){
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						map.put("deployUserNm", "%" + temp.get("value") + "%");
					}
					continue;
				}
				if(filedName.equals("orgNoID")){
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						map.put("executeOrg", temp.get("value"));
					}
					continue;
				}
				if(filedName.equals("queryType")){
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						map.put("queryType", temp.get("value"));
					}
					continue;
				}
			}
			json.put("rules", "[]");
			pager.setCondition(json.toString());
		}
		map.put("nodeType", taskIndexType);
		map.put("searchType", searchType);
		map.put("paramTypeNo", DeployTaskBS.PARAM_TYPE_NO);

		if (searchType.equals("1") || searchType.equals("2"))
			return getNeedOperTask(pager, map);
//		if (searchType.equals("2"))
//			return getOperedTask(pager, map);
		if (searchType.equals("3"))
			return getQueryTask(pager, map);
		if (searchType.equals("4"))
			return getNeedBackTask(pager, map);
		if (searchType.equals("5"))
			return getQueryMyTask(pager, map);
		return null;
	}

	private PageMyBatis<Map<String, Object>> getNeedOperTask(
			Pager pager, Map<String, Object> map) {
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			
			List<String> orgOpers = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);			
			
			StringBuilder sb = new StringBuilder();

			for(String operNo : orgOpers){
				sb.append(" or OBJREL.ORG_NO = '");
				sb.append(operNo);
				sb.append("' ");
			}
			map.put("orLike", sb.toString());
			
			String orgNo = user.getOrgNo();
			map.put("userid", user.getUserId());// ??????ID
			map.put("deptno", user.getDeptNo());// ??????
			map.put("orgNo", orgNo);// ??????
			map.put("orgLikeNo", orgNo);// ??????
			
			map.put("roles", user.getAuthObjMap().get("AUTH_OBJ_ROLE"));// ??????
			map.put("posis", user.getAuthObjMap().get("AUTH_OBJ_POSI"));// ??????
		}else {
			map.put("userno", "admin");
		} 
		PageHelper.startPage(pager);
		PageMyBatis<Map<String, Object>> result = (PageMyBatis<Map<String, Object>>) taskOperDao.getNeedOperTask(map);
		return result;
	}

	@SuppressWarnings("unused")
	private PageMyBatis<Map<String, Object>> getOperedTask(
			Pager pager, Map<String, Object> map) {
		PageHelper.startPage(pager);
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		map.put("userid", user.getUserId());// ??????ID
		return (PageMyBatis<Map<String, Object>>) taskOperDao
				.getOperedTask(map);
	}

	private PageMyBatis<Map<String, Object>> getNeedBackTask(
			Pager pager, Map<String, Object> map) {
		PageHelper.startPage(pager);
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		map.put("userid", user.getUserId());// ??????ID
		map.put("orgNo", user.getOrgNo());
		return (PageMyBatis<Map<String, Object>>) taskOperDao
				.getNeedBackTask(map);
	}
	
	private PageMyBatis<Map<String, Object>> getQueryTask(
			Pager pager, Map<String, Object> map) {

		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		map.put("userid", user.getUserId());// ??????ID
		if(!user.isSuperUser()){
			/****??????????????????????????????        start***************/
			List<String> allorgNos = new ArrayList<String>();
			//??????????????????
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			allorgNos.addAll(authOrgNos);
			orgBS.getAllChildOrgNos(BioneSecurityUtils.getCurrentUserInfo().getOrgNo(), allorgNos, true);
			map.put("orgNos", SplitStringBy1000.change(allorgNos));// ??????ID
		}
		
		/****??????????????????????????????         end***************/
		PageHelper.startPage(pager);
		return (PageMyBatis<Map<String, Object>>) taskOperDao.getQueryTask(map);
	}
	
	private PageMyBatis<Map<String, Object>> getQueryMyTask(
			Pager pager, Map<String, Object> map) {

		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		map.put("queryType", "5");
		map.put("userid", user.getUserId());// ??????ID

		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<BioneOrgInfo>downOrgList=Lists.newArrayList();
		orgBS.findLowerOrgInfosByOrgId(userObj.getOrgNo(), downOrgList);
		List<String>orgList=Lists.newArrayList();
		for(BioneOrgInfo orgInfo:downOrgList){
			if(!orgInfo.getOrgNo().equals(userObj.getOrgNo()))
				orgList.add(orgInfo.getOrgNo());
		}
		map.put("orgList", orgList);// ??????ID
		PageHelper.startPage(pager);
		return (PageMyBatis<Map<String, Object>>) taskOperDao
				.getQueryTask(map);
	}
	
	private void updateTableStatus(String templeId,String status,String taskInstanceId,String taskNodeInstanceId,boolean isFlowChange){
		RptInputLstTempleInfo info = templeBS.getEntityById(templeId);
		String dsId = info.getDsId();
		String tableNm = info.getTableEnName();
		String schema = this.dataSourceBS.getSchemaByDsId(dsId);
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append(" UPDATE ").append(schema).append(".").append(tableNm).append(" SET SYS_DATA_STATE ='").append(status).append("' WHERE FLOW_NODE_ID ");
		if(!isFlowChange)
			sqlBuff.append(" ='").append(taskNodeInstanceId).append("'");
		else
		{
			List<String>nodeList = getAllTaskNodeInsIds(taskInstanceId);
			sqlBuff.append("  IN (");
			boolean isFirst = true;
			for(String node:nodeList){
				if(isFirst) isFirst= false;
				else sqlBuff.append(",");
				sqlBuff.append("'").append(node).append("'");
			}
			sqlBuff.append(")  ");
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = this.dataSourceBS.getConnection(dsId);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			stmt.execute(sqlBuff.toString());
			conn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(conn!=null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			e.printStackTrace();
		} finally {
			this.dataSourceBS.releaseConnection(null, stmt, conn);
		}
		
	}
	
	
	private void completeRptTskNodeIns(String taskNodeInstanceId) {

		RptTskNodeIns nodeIns = new RptTskNodeIns();
		nodeIns.setTaskNodeInstanceId(taskNodeInstanceId);
		nodeIns.setSts("2");
		nodeIns.setEndTime(DateUtils.getFormatTime(new Date().getTime(), "yyyyMMdd"));
		
		taskOperDao.updateRptTskNodeIns(nodeIns);

	}
	
	private void reButRptTskNodeIns(String taskNodeInstanceId,String taskInsId){
//		RptTskNodeIns nodeIns = new RptTskNodeIns();
//		nodeIns.setTaskNodeInstanceId(taskNodeInstanceId);
//		nodeIns.setSts("0");
//		nodeIns.setStartTime("toNull");
		
//		taskOperDao.updateRptTskNodeIns(nodeIns);
		StringBuilder sb = new StringBuilder();
		sb.append("");
		sb.append(" update  rpt_tsk_node_ins a ");
		sb.append(" set a.start_time = null , ");
		sb.append(" a.end_time = null , ");
		sb.append(" a.sts = '0' ");
		sb.append(" where a.task_instance_id = '" + taskInsId + "'  ");
		sb.append(" and a.task_orderno > ( ");
		sb.append(" select task_orderno from( select task_orderno from rpt_tsk_node_ins o where 1=1  ");
		sb.append("and  o.task_node_instance_id = '"+ taskNodeInstanceId +"'  )b) ");
		
		this.baseDAO.createNativeQueryWithIndexParam(sb.toString()).executeUpdate();
		
	}

	private void initOtherRptTskNodeIns(String taskNodeInstanceId) {
		RptTskNodeIns nodeIns = new RptTskNodeIns();
		nodeIns.setTaskNodeInstanceId(taskNodeInstanceId);
		nodeIns.setSts("1");
		nodeIns.setStartTime(DateUtils.getFormatTime(new Date().getTime(), "yyyyMMdd"));
		nodeIns.setEndTime("toNull");
		taskOperDao.updateRptTskNodeIns(nodeIns);
	}

	public void completeRptTskIns(String taskInstanceId,Map<String,Object> insMap) {

		RptTskIns ins = new RptTskIns();
		ins.setTaskInstanceId(taskInstanceId);
		ins.setEndTime(new Timestamp(System.currentTimeMillis()));
		ins.setSts("2");
		taskOperDao.updateRptTskIns(ins);
		/**
         * chenhx 20171121
         * ????????????????????????
         * ??????????????????????????????,????????????????????????
         */
		
		DeployTaskVO taskNodeVO = deployTaskBS.getTaskById((String)insMap.get("TASK_ID"),null);
		
		if("1".equals(taskNodeVO.getRptTskInfo().getAfterTaskSts())){
		    
		    DeployTaskVO afterTaskVO = deployTaskBS.getTaskById(taskNodeVO.getRptTskInfo().getAfterTaskObjId(),null);
	        afterTaskVO.setLoadDataMark("2".equals((String)insMap.get("LOAD_DATA_MARK")) ? "2" : "1");
	        afterTaskVO.setTaskTitle((String)insMap.get("TASK_TITLE")+ "-????????????");
	        
	        /**
	         * ????????????????????????
	         */
	        if("1".equals(afterTaskVO.getLoadDataMark())){
	        	String dataDate = (String)insMap.get("DATA_DATE");
	            Set<String> paramOrgs = deployTaskBS.getLoadData(afterTaskVO.getRptTskInfo().getTaskId(),true,afterTaskVO.getOrgs(),dataDate);
	            List<String> treList = Lists.newArrayList();
	            treList.addAll(paramOrgs);
	            afterTaskVO.setOrgs(treList);
	        }
	        
	        deployTaskBS.deploySingleTaskWithOrgs(afterTaskVO.getOrgs(), afterTaskVO, (String)insMap.get("DATA_DATE"), BioneSecurityUtils.getCurrentUserInfo().getUserId(),afterTaskVO.getLoadDataMark());
		}
	}

	public Map<String,Object>getTaskInsLog(String taskInstanceId){
		Map<String,Object>map = Maps.newHashMap();
		List<Map<String,Object>> mapList =  taskOperDao.getTaskInsLog(taskInstanceId);
		for(Map<String,Object> tmpMap :mapList){
			Object obj =  tmpMap.get("OPER_TIME");
			if(obj!=null && obj instanceof  Timestamp){
				Timestamp stamp = (Timestamp)obj;
				tmpMap.put("OPER_TIME", DateUtils.formatDate(new Date(stamp.getTime()), "yyyy-MM-dd HH:mm:ss"));
			}
			else if(obj!=null && obj instanceof  oracle.sql.TIMESTAMP){
				try {
					tmpMap.put("OPER_TIME", DateUtils.formatDate(new Date(new Timestamp(((oracle.sql.TIMESTAMP) obj).dateValue().getTime()).getTime()), "yyyy-MM-dd HH:mm:ss"));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		map.put("Rows",mapList);
		
		return map;
	}

	public Map<String,Object>getListLogDetail(String logId){
		
		Map<String,Object>queryMap = Maps.newHashMap();
		queryMap.put("logId", logId);
		List<RptListOperLog> mapList =  taskOperDao.getListLogDetail(queryMap);
		
		Map<String,Object>map = Maps.newHashMap();
		for(RptListOperLog rptListOperLog :mapList){
			rptListOperLog.setShowDate(DateUtils.formatDate(rptListOperLog.getOperTime(), "yyyy-MM-dd HH:mm:ss"));
		}
		map.put("Rows",mapList);
		
		return map;
	}
	
	public Map<String,Object>getTaskOperInfo(String taskInstanceId){
		Map<String,Object>map = Maps.newHashMap();
		List<RptTskNodeIns> mapList =  taskOperDao.getRptTskNodeIns(taskInstanceId);
		for(RptTskNodeIns ins :mapList){
			if(StringUtils.isNotEmpty(ins.getStartTime()) ){
				ins.setStartTime(formatDateStyle(ins.getStartTime()));
			}
			if(StringUtils.isNotEmpty(ins.getEndTime()) ){
				ins.setEndTime(formatDateStyle(ins.getEndTime()));
			}
		}
		map.put("Rows",mapList);
		
		return map;
	}
	
	private String formatDateStyle(String value){
		StringBuilder buff = new StringBuilder();
		buff.append(value.substring(0, 4)).append("-").append(value.substring(4, 6))
		.append("-").append(value.substring(6, 8));
		return buff.toString();
	}
	
	/**
	 * ????????????
	 * 
	 * @param taskNodeInstanceId
	 * @param taskInstanceId
	 * @param remark
	 */
	public String submitTask(String taskInstanceId,String taskNodeInstanceId,String templateId) {
		return dealTask(taskInstanceId,"1",taskNodeInstanceId,templateId,null);
	}
	
	/**
	 * ????????????
	 * 
	 * @param taskNodeInstanceId
	 * @param taskInstanceId
	 * @param remark
	 */
	public String openLockTask(String taskInstanceId,String taskNodeInstanceId,String templateId) {
		return dealTask(taskInstanceId,"3",taskNodeInstanceId,templateId,null);
	}
	
	public String submitMultiTask(String taskInstanceIds,String taskNodeInstanceIds,String templateId){
		String[] taskInstanceIdArray = taskInstanceIds.split(",");
		String[] taskNodeInstanceIdArray = taskNodeInstanceIds.split(",");
		boolean isOk = true;
		String msg = null;
		for(int i = 0 ;i <taskInstanceIdArray.length;i++){
			String taskInstanceId = taskInstanceIdArray[i];
			String taskNodeInstanceId = taskNodeInstanceIdArray[i];
			msg = dealTask(taskInstanceId,"1",taskNodeInstanceId,templateId,null);
			isOk = msg.equals("1");
			if(!isOk) break;
		}
		return msg;
		
	}
	
	public String rebutMultiTask(String taskInstanceIds,String taskNodeInstanceIds,String templateId){
		String[] taskInstanceIdArray = taskInstanceIds.split(",");
		String[] taskNodeInstanceIdArray = taskNodeInstanceIds.split(",");
		boolean isOk = true;
		String msg = null;
		for(int i =0 ;i <taskInstanceIds.length();i++){
			String taskInstanceId = taskInstanceIdArray[i];
			msg = dealTask(taskInstanceId,"2",taskNodeInstanceIdArray[i],templateId,null);
			isOk = msg.equals("1");
			if(!isOk) break;
		}
		return msg;
		
	}
	
	public String backMultiTask(String taskInstanceIds,String templeId){
		String[] taskInstanceIdArray = taskInstanceIds.split(",");
		boolean isOk = true;
		for(String taskInstanceId:taskInstanceIdArray){
			isOk = reOpenTask(templeId,taskInstanceId);
			if(!isOk) break;
		}
		if(!isOk)
			return "0";
		else
			return "1";
		
	}
	
	/**
	 * ????????????
	 * @param taskInstanceId
	 * @return
	 */
	public String rebutTask(String taskInstanceId,String taskNodeInstanceId,String templateId,String rebutNode){
		return dealTask(taskInstanceId,"2",taskNodeInstanceId,templateId,rebutNode);
	}
	
	public String reWrite(String taskInstanceId){

		RptTskExeobjRel rptTskExeobjRel = taskDao.getRptTskExeobjRelByTaskInstanceId(taskInstanceId);
		String rs = inputTableBS.reWriteData(rptTskExeobjRel.getId().getExeObjId(),taskInstanceId);

		RptTskIns rptTskIns = taskDao.getRptTskInsById(taskInstanceId);
		// ??????????????????
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		RptTskInsLog log = new RptTskInsLog();
		log.setOperTime(new Timestamp(System.currentTimeMillis()));
		log.setTaskInstanceId(taskInstanceId);
		log.setTaskNodeInstanceId(rptTskIns.getTaskNodeInstanceId());
		log.setLogId(RandomUtils.uuid2());
		log.setOperUser(user.getUserId());
		log.setRemark( "??????:"+rptTskIns.getTaskNm()+" ??????"+(rs.equals("1")?"??????":rs)+",?????????:"+user.getUserName());
		log.setOperType("5");//???????????? ??????
			
		taskOperDao.saveTskInsLog(log);
		
		return rs;
	}
	
	
	public boolean reOpenTask(String templeId,String taskInstanceId){
		try{
			//?????????????????????????????????
			RptTskIns ins =  taskDao.getRptTskInsById(taskInstanceId);
			String taskNodeInstanceId = ins.getTaskNodeInstanceId();
			ins.setSts("1");
			//ins.setTaskInstanceId(taskInstanceId);
			taskOperDao.updateRptTskIns(ins);
			
			//???????????????????????????????????????????????????
			RptTskNodeIns nodeIns = new RptTskNodeIns();
			nodeIns.setEndTime("toNull");
			nodeIns.setSts("1");
			nodeIns.setTaskNodeInstanceId(taskNodeInstanceId);
			taskOperDao.updateRptTskNodeIns(nodeIns);
			
			// ??????????????????
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			RptTskInsLog log = new RptTskInsLog();
			log.setOperTime(new Timestamp(System.currentTimeMillis()));
			log.setTaskInstanceId(taskInstanceId);
			log.setTaskNodeInstanceId(ins.getTaskNodeInstanceId());
			log.setLogId(RandomUtils.uuid2());
			log.setOperUser(user.getUserId());
			log.setRemark( "??????:"+ins.getTaskNm()+" ????????????,?????????:"+user.getUserName());
			log.setOperType("4");
			taskOperDao.saveTskInsLog(log);
			if(StringUtils.isNotEmpty(templeId))
				updateTableStatus(templeId,REFUSE,taskInstanceId,null,true);
			
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
	}

	/**
	 * ??????????????????  1   
	 *       ????????????  2
	 *       ???????????? 3
	 * @param taskInstanceId
	 * @param type
	 * @return
	 */
	public synchronized String dealTask(String taskInstanceId, String type,
			String currentTaskNodeInstanceId, String templateId,
			String rebutNode) {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("taskInstanceId", taskInstanceId);
			params.put("type", type);
			Map<String, Object> insMap = taskOperDao.getRptTskInsInfoWithNode(params);
			if (insMap == null || insMap.isEmpty())
				return "???????????????,?????????????????????";
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			String otherNodeId = (String) insMap
					.get("OTHERINS_NODE_INSTANCE_ID");
			String taskNodeInstanceId = (String) insMap
					.get("TASK_NODE_INSTANCE_ID");
			String taskNm = (String) insMap.get("TASK_NM");
			String remark = "";
			if (!currentTaskNodeInstanceId.equals(taskNodeInstanceId))
				return "?????????????????????????????????????????????????????????";
			if(type.equals("3")){//??????????????????????????????????????????
				List<RptTskNodeIns> nodeList = this.getTaskNodes(taskInstanceId);
				for(RptTskNodeIns node : nodeList){
					if("01".equals(node.getNodeType())){
						rebutNode = node.getTaskNodeInstanceId();
					}
				}
			}
			// ??????????????????
			if (StringUtils.isEmpty(otherNodeId)) {
				if (type.equals("1")) {
					// ???????????????????????????,?????????????????????
					afterCompleteTask(taskInstanceId);
					completeRptTskIns(taskInstanceId, insMap);
					remark = "??????:" + taskNm + " ??????,?????????:" + user.getUserName();
					if (StringUtils.isNotEmpty(templateId))
						updateTableStatus(templateId, SUCCESS, taskInstanceId, null, true);
				} else if(type.equals("2")){
					// ?????????????????????????????????1????????????????????????????????????????????????,???????????????????????????????????????
					RptTskIns ins = new RptTskIns();
					ins.setTaskNodeInstanceId(rebutNode);
					ins.setTaskInstanceId(taskInstanceId);
					ins.setLoadDataMark("3");
					taskOperDao.updateRptTskIns(ins);
					// ???????????????????????????
					initOtherRptTskNodeIns(rebutNode);
					remark = "??????:" + taskNm + " ????????????,?????????:" + user.getUserName();
					if (StringUtils.isNotEmpty(templateId))
						updateTableStatus(templateId, REJECT, taskInstanceId, null, true);
				}else if(type.equals("3")){//??????????????????????????????????????????
					RptTskIns ins = new RptTskIns();
					ins.setTaskNodeInstanceId(rebutNode);
					ins.setTaskInstanceId(taskInstanceId);
					ins.setLoadDataMark("3");
					ins.setSts("1");
					ins.setEndTime(null);
					taskOperDao.updateRptTskIns(ins);
					// ???????????????????????????
					initOtherRptTskNodeIns(rebutNode);
					remark = "??????:" + taskNm + " ????????????,?????????:" + user.getUserName();
					if (StringUtils.isNotEmpty(templateId))
						updateTableStatus(templateId, REJECT, taskInstanceId, null, true);
				}
			} else {
				// ?????????????????????????????????1????????????????????????????????????????????????,???????????????????????????????????????
				RptTskIns ins = new RptTskIns();
				if (type.equals("2")) {
					ins.setTaskNodeInstanceId(rebutNode);
				} else if(type.equals("1")){
					ins.setTaskNodeInstanceId(otherNodeId);
				}else if(type.equals("3")){
					ins.setTaskNodeInstanceId(rebutNode);
					ins.setSts("1");
					ins.setEndTime(null);
				}
				ins.setTaskInstanceId(taskInstanceId);
				ins.setLoadDataMark("3");
				taskOperDao.updateRptTskIns(ins);
				if (type.equals("2")) {
					// ???????????????????????????
					initOtherRptTskNodeIns(rebutNode);
				} else if (type.equals("1")) {
					// ???????????????????????????
					initOtherRptTskNodeIns(otherNodeId);
				}else if (type.equals("3")) {
					// ???????????????????????????
					initOtherRptTskNodeIns(rebutNode);
				}
				// sendHintMsg(otherNodeId,orgNo,taskNm);
				if (type.equals("1")) {
					remark = "??????:" + taskNm + " ????????????,?????????:" + user.getUserName();
					if (StringUtils.isNotEmpty(templateId))
						updateTableStatus(templateId, SUBMIT, null,
								currentTaskNodeInstanceId, false);
				} else {
					remark = "??????:" + taskNm + " ????????????,?????????:" + user.getUserName();
					if (StringUtils.isNotEmpty(templateId))
						updateTableStatus(templateId, REJECT, taskInstanceId,
								null, true);
				}

			}
			// ?????????????????????
			if (type.equals("1")) {
				// ????????????
				completeRptTskNodeIns(taskNodeInstanceId);
			} else {
				// ????????????
				// reButRptTskNodeIns(taskNodeInstanceId);

				reButRptTskNodeIns(rebutNode, taskInstanceId);
			}
			// ??????????????????
			RptTskInsLog log = new RptTskInsLog();
			log.setOperTime(new Timestamp(System.currentTimeMillis()));
			log.setTaskInstanceId(taskInstanceId);
			log.setTaskNodeInstanceId(taskNodeInstanceId);
			log.setLogId(RandomUtils.uuid2());
			log.setOperUser(user.getUserId());
			log.setRemark(remark);
			if (type.equals("1"))
				log.setOperType("2");
			else if (type.equals("2"))
				log.setOperType("3");
			else if (type.equals("3"))
				log.setOperType("3");
			taskOperDao.saveTskInsLog(log);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "??????????????????,?????????" + ex.getMessage();
		}
		return "1";
	}
	
	/*private void sendHintMsg(String taskNodeInstanceId,String orgNo,String taskNm){
		List<Map<String,Object>>list = taskOperDao.getRptTskNodeTskinsByTaskNode(taskNodeInstanceId);
		for(Map<String,Object> map : list){
			String taskObjType = (String) map.get("TASK_OBJ_TYPE");
			String taskObjId = (String) map.get("TASK_OBJ_ID");
			
			String isSendNotify=taskOperDao.getIsSendNotifyByTaskNodeInstanceId(taskNodeInstanceId);
			try {
				if(StringUtils.isNotEmpty(isSendNotify)&&isSendNotify.equals("1"))
					smsUtils.sendDealMsg(orgNo, taskObjType, taskObjId, taskNm);
			} catch (BioneMessageException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
	}*/
	
	
	private void afterCompleteTask(String taskInstanceId){
		RptTskExeobjRel rptTskExeobjRel = taskDao.getRptTskExeobjRelByTaskInstanceId(taskInstanceId);
		String exeObjType = rptTskExeobjRel.getId().getExeObjType();
		if(exeObjType.equals("01")){
			idxDataInputBS.tempDataToResult(rptTskExeobjRel.getId().getExeObjId(),taskInstanceId);
		}else if(exeObjType.equals("02")){
			//???????????????,??????????????????
		    String sql = "SELECT AUTO_REWRITE FROM RPT_INPUT_REWRITE_TEMPLE_INFO WHERE TEMPLE_ID = ?0 ";
	        List<?> rs = this.baseDAO.createNativeQueryWithIndexParam(sql, rptTskExeobjRel.getId().getExeObjId()).getResultList();
	        if(null != rs && rs.size() > 0){
	        	if("true".equals(String.valueOf(rs.get(0)))){
	        		inputTableBS.reWriteData(rptTskExeobjRel.getId().getExeObjId(),taskInstanceId);
	        	}
	        }
		}
	}

	
	public boolean canReOpen(String taskInstanceId){

		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		Map<String,Object>params = Maps.newHashMap();
		params.put("taskInstanceId", taskInstanceId);
		params.put("currentUser", user.getUserId());
		params.put("orgNo", user.getOrgNo());
		
		List<Integer> rs =  taskOperDao.canReOpen(params);
		if(rs == null) return false;
		return !rs.isEmpty();
		
	}
	
	public List<String>getAllTaskNodeInsIds(String taskInstanceId){
		return taskOperDao.getAllTaskNodeInsIds(taskInstanceId);
	}

	@Transactional(readOnly = false)
	public void saveOperLog(String taskInstanceId, String logList, String templateId, String taskId) {
		RptTskIns ins = taskDao.getRptTskInsById(taskInstanceId);
		// ?????????????????????

		// ??????????????????
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		RptTskInsLog log = new RptTskInsLog();
		String logId = RandomUtils.uuid2();
		log.setOperTime(new Timestamp(System.currentTimeMillis()));
		log.setTaskInstanceId(taskInstanceId);
		log.setTaskNodeInstanceId(ins.getTaskNodeInstanceId());
		log.setLogId(logId);
		log.setOperUser(user.getUserId());
		log.setRemark("??????:" + ins.getTaskNm() + " ????????????,?????????:" + user.getUserName());
		log.setOperType("1");
		taskOperDao.saveTskInsLog(log);

		JSONArray logListJson = JSON.parseArray(logList);
		for (int i = 0; i < logListJson.size(); i++) {
			JSONObject logObject = (JSONObject) logListJson.get(i);
			String type = logObject.getString("type");
			RptListOperLog operLog = new RptListOperLog();
			RptListOperLogPK pk = new RptListOperLogPK();
			String fieldId = null;
			if (logObject.containsKey("id"))
				fieldId = logObject.getString("id");
			if (type.equals("add"))
				fieldId = RandomUtils.uuid2();
			if(StringUtils.isEmpty(fieldId))
				fieldId = RandomUtils.uuid2();
			pk.setFieldId(fieldId);
			pk.setLogId(logId);
			operLog.setId(pk);
			operLog.setNodeId(ins.getTaskNodeInstanceId());
			String content = logObject.getString("content");
			operLog.setOperContent(content);
			operLog.setTaskId(taskId);
			operLog.setOperTime(new Timestamp(System.currentTimeMillis()));
			operLog.setOperType(type);
			operLog.setOperUser(user.getUserId());
			operLog.setTempleId(templateId);
			this.baseDAO.merge(operLog);
		}
		
		//?????????????????????
		/*
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append(" INSERT INTO RPT_LIST_OPER_LOG (LOG_ID,NODE_ID,FIELD_ID,TEMPLE_ID,TASK_ID,OPER_TYPE,OPER_CONTENT,OPER_USER,OPER_DATE) ")
			   .append(" VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? ) ");
		
		BioneUser usr = BioneSecurityUtils.getCurrentUserInfo();
		JSONArray logListJson = JSONArray.fromObject(logList);
		List<Object[]>paramList = Lists.newArrayList();
		for(int i = 0 ;i < logListJson.size(); i++){
			JSONObject logObject = (JSONObject) logListJson.get(i);
			String type = logObject.getString("type");
			String id = logObject.getString("id");
			if(type.equals("add"))
				id = RandomUtils.uuid2();
			String content = logObject.getString("content");
			Object[]param2 = new Object[]{logId,ins.getTaskNodeInstanceId(),id,templateId,taskId,type,content,usr.getUserId(),new Date()};
			paramList.add(param2);
		}
		//this.jdbcBaseDAO.batchUpdate(sqlBuff.toString(),paramList);
		
		Connection conn = null;
		try {
			conn = jdbcBaseDAO.getDataSource().getConnection();
			if(null != conn){
				//??????
				PreparedStatement psd = conn.prepareStatement(sqlBuff.toString());	
				for(int i = 0; i < paramList.size(); i++){
					Object[] arr = paramList.get(i);
					for(int arrd = 0; arrd < arr.length; arrd++){
						if(  arr[arrd] instanceof java.util.Date )
							psd.setDate(arrd + 1, new java.sql.Date(((Date)arr[arrd]).getTime()));
						else
							psd.setObject(arrd + 1, arr[arrd]);
					}
					psd.addBatch();
				}
				psd.executeBatch();
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
	public Map<String, Object> deleteTask(String taskInstanceIds){
		Map<String, Object> result = new HashMap<>();
		result.put("status", "success");

		String[] taskInstanceIdss = taskInstanceIds.split(",");
		try{
			for(int i=0; i<taskInstanceIdss.length; i++){
				List<RptTskNodeIns> nodeIns =taskOperDao.getRptTskNodeIns(taskInstanceIdss[i]);
				List<String> taskNodeInsIds =Lists.newArrayList();
				for(RptTskNodeIns ins:nodeIns){
					taskNodeInsIds.add(ins.getTaskNodeInstanceId());
				}
				Map<String,Object> exeMap = taskOperDao.getTskDealInfoByNodeInfo(taskInstanceIdss[i]);
				if(null != exeMap) {
					String templeId = (String) exeMap.get("EXE_OBJ_ID");
					RptInputLstTempleInfo info = templeBS.getEntityById(templeId);
					boolean isInfoEx=dataUtils.isInfoExist(info);//????????????????????????????????????
					if(isInfoEx==true){
						//???????????????????????????
						dataUtils.copyAndDeleteRecord(info,taskNodeInsIds);
					}
				}
				
				taskOperDao.deleteRptTskNodeTskinsRelByInsId(taskInstanceIdss[i]);
				taskOperDao.deleteRptTskNodeInsByInsId(taskInstanceIdss[i]);
				taskOperDao.deleteRptTskInsByInsId(taskInstanceIdss[i]);
				
			}
		} catch(Exception ex){
			ex.printStackTrace();
			result.put("status", "fail");
			result.put("msg", "??????????????????????????????"+ex.getMessage());
		}
		return result;

	}
	
	@SuppressWarnings("unchecked")
	public List<RptTskNodeIns> getTaskNodes(String taskInstanceId) {
		String jql = "SELECT tni.taskNodeInstanceId, tni.taskNodeNm, tni.nodeType FROM RptTskNodeIns tni where tni.taskInstanceId = ?0 and tni.sts = '2' order by tni.taskOrderno";
		List<Object[]> l = this.baseDAO.createQueryWithIndexParam(jql, taskInstanceId).getResultList();
		List<RptTskNodeIns> res = Lists.newArrayList();
		for(Object[] objarr : l ){
			RptTskNodeIns a = new RptTskNodeIns();
			a.setTaskNodeInstanceId(String.valueOf(objarr[0]));
			a.setTaskNodeNm(String.valueOf(objarr[1]));
			a.setNodeType(String.valueOf(objarr[2]));
			res.add(a);
		}
		return res;
	}
	
}
