/**
 * 
 */
package com.yusys.biapp.input.input.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.dict.service.InputDataDictBS;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.security.authres.AuthObjectUtils;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.entity.RptInputLstTempleRule;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.biapp.input.template.service.TempleConstraintBS;
import com.yusys.biapp.input.template.service.TempleFieldBS;
import com.yusys.biapp.input.template.service.TempleRuleInfoBS;
import com.yusys.biapp.input.utils.TreeUtils;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

/**
 * <pre>
 * Title: 程序名称
 * Description: 功能描述
 * </pre>
 * @author xuguangyuan  xugy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
public class TaskCaseBS extends BaseBS<RptTskIns>{
//	private final Log log = LogFactory.getLog(TaskCaseBS.class);
	@Autowired
	private TempleBS templeBS;
	@Autowired
	private TempleFieldBS templeFieldBS;
	@Autowired
	private AuthObjectUtils authObjectUtils;
	@Autowired
	private TempleConstraintBS templeConstraintBS;
	@Autowired
	private InputDataDictBS inputDataDictBS;
	@Autowired
	private TempleRuleInfoBS templeRuleBS;
	@Autowired
	private DataSourceBS dataSourceBS;

	/**
	 * 根据任务ID获取任务实例
	 * @param taskIds
	 * @return
	 */
	public List<RptTskIns> getTaskCaseByTask(List<String> taskIds, String orgCode) {
		if ((taskIds == null || taskIds.isEmpty())) {
			return Lists.newArrayList();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String jql1 = "";
		if (StringUtils.isNotBlank(orgCode) && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			paramMap.put("taskIds", taskIds);
			paramMap.put("orgCode", orgCode);
			jql1 = "select distinct t from RptTskIns t , UdipCaseOrg uco where t.taskId in :taskIds and uco.orgCode=:orgCode and t.caseId = uco.caseId order by t.etlDate desc,t.taskSequnce desc";
		} else {
			paramMap.put("taskIds", taskIds);
			jql1 = "select distinct t from RptTskIns t where t.taskId in :taskIds order by t.etlDate desc,t.taskSequnce desc";
		}
		return baseDAO.findWithNameParm(jql1, paramMap);
	}
	/////////////////////////////////////////////////从UdipDataStateBS移过来的//////////////////////////////////////////////////////////
	/**
	 * 修改数据状态
	 * @param propName
	 * @param propVal
	 * @param taskStateSave
	 */
	@Transactional(readOnly=false)
	public void changerDataState(String templeId, String caseId,List<String> orgNo, String logicSysNo,String dataType){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dataType", dataType);
		paramMap.put("operator", BioneSecurityUtils.getCurrentUserInfo().getLoginName());
		paramMap.put("operateDate", DateUtils.getYYYY_MM_DD_HH_mm_ss());
		paramMap.put("caseId", caseId);
		paramMap.put("templeId", templeId);
		paramMap.put("orgCode", orgNo);
		String jql1 = "update RptTskIns t  set t.dataState=:dataType,t.operator=:operator ,t.operateDate=:operateDate  where caseId=:caseId and templeId=:templeId and orgCode in :orgCode";
		this.baseDAO.batchExecuteWithNameParam(jql1, paramMap);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//	/**
//	 * 根据任务ID获取任务实例
//	 * @param taskIds
//	 * @return
//	 */
//	public int getMaxSequence(String taskId,String etlDate) {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("taskId", taskId);
//		paramMap.put("etlDate", etlDate);
//		String jql1 = "select max(taskSequnce)  from UdipTaskCaseInfo t where t.taskId = :taskId and t.etlDate =:etlDate";
//		List<Object> list = baseDAO.findWithNameParm(jql1, paramMap);
//		if(list.get(0)==null)
//			return 0;
//		else
//			return Integer.valueOf(list.get(0).toString());
//	}
//	
//	/**
//	 * 根据任务ID和状态获取任务实例
//	 * @param taskIds
//	 * @return
//	 */
//	public List<UdipTaskCaseInfo> getTaskCaseByCase(List<String> taskIds,List<String> caseIds,String orgCode,String caseState) {
//		if ((caseIds == null || caseIds.isEmpty())) {
//			return Lists.newArrayList();
//		}
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		String jql1 = "",jql2="";
//		
//		if(StringUtils.isNotBlank(caseState)){
//			jql2="and t.caseState='"+caseState+"'";
//		}
//		if (StringUtils.isNotBlank(orgCode)) {
//			paramMap.put("orgCode", orgCode);
//			jql1 = "select t from UdipTaskCaseInfo t , UdipCaseOrg uco where  uco.orgCode=:orgCode and t.caseId = uco.caseId "+jql2+" order by t.dispatchDate desc,t.taskSequnce desc";
//		} else {
//			jql1 = "select t from UdipTaskCaseInfo t where 1=1 "+jql2+" order by t.dispatchDate desc,t.taskSequnce desc";
//		}
//		List<UdipTaskCaseInfo> UdipTaskCaseInfoList = baseDAO.findWithNameParm(jql1, paramMap);
//		List<UdipTaskCaseInfo> list = Lists.newArrayList();
//		for (UdipTaskCaseInfo info : UdipTaskCaseInfoList) {
//			if (caseIds.contains(info.getCaseId()) && taskIds.contains(info.getTaskId())) {
//				list.add(info);
//			}
//		}
//		return list;
//	}
//	
//	/**
//	 * 根据任务状态和角色获取任务实例
//	 * @param logicSysNo 逻辑系统ID
//	 * @param userRoles 用户角色
//	 * @param roleType 角色类型
//	 * @param userName 用户名称
//	 * @param orgCode 用户机构
//	 * @param dataState 数据状态
//	 * @param size 返回条数
//	 * @return
//	 */
//	public List<UdipTaskCaseVO> getTaskCaseByUser(String logicSysNo, List<String> userRoles, String roleType, String userName,String orgCode, String[] dataState , String taskName,String caseState) {
//		
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		
//		//1.首先获取查询的任务
//		String jql1 = "select task from UdipInputTaskInfo task where task.logicSysNo=:logicSysNo " ;
//		paramMap.put("logicSysNo", logicSysNo);
//		
//		if (StringUtils.isNotBlank(taskName)) {//支持多关键字的模糊搜索
//			taskName = taskName.trim();
//			String keys[] = taskName.split("\\s+");
//			
//			jql1 += " and (1=0 ";
//			for (int i = 0; i < keys.length; i++) {
//				String params = "taskName_" + i;
//				jql1 += " or task.taskName like :" + params;
//				paramMap.put(params, "%" + keys[i] + "%");
//			}
//			jql1 += ")";
//		}
//		
//		List<UdipInputTaskInfo> taskList = baseDAO.findWithNameParm(jql1,paramMap);
//		
//		//2.接着获取该用户拥有权限的任务
//		paramMap = new HashMap<String, Object>();
//		jql1 = "select role from UdipTaskRole role where role.roleCode in :roleCode " ;
//		paramMap.put("roleCode", userRoles);
//		if (StringUtils.isNotBlank(roleType)) {
//			jql1 += " and role.roleType= :roleType";
//			paramMap.put("roleType", roleType);
//		}
//		List<UdipTaskRole> roleList = baseDAO.findWithNameParm(jql1,paramMap);
//		Map<String ,UdipInputTaskInfo> taskMap = Maps.newHashMap();
//		for (UdipInputTaskInfo task : taskList) {
//			for (UdipTaskRole role : roleList) {
//				if(task.getTaskId().equals(role.getTaskId())){
//					if (role!=null && StringUtils.isNotBlank(role.getRoleUser())) {// 如果角色设置了人,那么就要看该用户在不在设置人的里面
//						if (role.getRoleUser().indexOf(userName) != -1){
//							taskMap.put(task.getTaskId(), task);
//						}
//					} else {
//						taskMap.put(task.getTaskId(), task);
//					}
//				}
//			}
//		}
//		//3.获取指定状态的任务实例ID
//		paramMap = new HashMap<String, Object>();
//		String jql = "select distinct s.caseId from UdipDataStateInfo s where s.logicSysNo=:logicSysNo ";
//		if (dataState != null && dataState.length > 0) {
//			jql += "and s.dataState in ("+ArrayUtils.toDBString(dataState)+")";
//		}
//		
//		paramMap.put("logicSysNo", logicSysNo);
//		List<String> caseIdList = baseDAO.findWithNameParm(jql,paramMap);
//		
//		//4.根据实例和任务，获取任务名称
//		List<UdipTaskCaseInfo> caseList= this.getTaskCaseByCase(new ArrayList<String>(taskMap.keySet()), caseIdList, orgCode,caseState);
//		
//		List<UdipTaskCaseVO> voList = Lists.newArrayList();
//		for (UdipTaskCaseInfo info : caseList) {
//			UdipTaskCaseVO vo = new UdipTaskCaseVO();
//			vo.to(info);
//			vo.setTaskName(taskMap.get(info.getTaskId()).getTaskName());
//			vo.setEtlDate("第"+info.getTaskSequnce()+"批");
//			voList.add(vo);
//		}
//		return voList;
//	}
//
//	/**
//	 * 根据任务ID获取任务实例
//	 * @param taskIds
//	 * @return
//	 */
//	public SearchResult<UdipTaskCaseInfo> getTaskCaseByTask(int firstResult, int pageSize,String taskId) {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		String jql1 = "select t from UdipTaskCaseInfo t where t.taskId=:taskId order by t.dispatchDate desc";
//		paramMap.put("taskId", taskId);
//		SearchResult<UdipTaskCaseInfo> authObjDefList = baseDAO.findPageWithNameParam(firstResult, pageSize, jql1, paramMap);
//		return authObjDefList;
//	}
//
//	public SearchResult<UdipTaskCaseInfo> getTaskCaselist(int firstResult, int pageSize, String orderBy, String orderType, Map<String, Object> conditionMap, String taskId) {
//		StringBuilder jql = new StringBuilder("");
//		@SuppressWarnings("unchecked")
//		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
//		if (StringUtils.isNotBlank(taskId)) {
//			jql.append("select task from UdipTaskCaseInfo task where task.taskId=:taskId");
//			values.put("taskId", taskId);
//		} else {
//			jql.append("select task from UdipTaskCaseInfo task where 1=1");
//		}
//
//		if (!conditionMap.get("jql").equals("")) {
//			jql.append(" and " + conditionMap.get("jql"));
//		}
//		if (!StringUtils.isEmpty(orderBy)) {
//			jql.append(" order by task." + orderBy + " " + orderType);
//		}
//		return this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
//	}
//
//	public List<String> getTempleTableList(String id) {
//		List<String> list = new ArrayList<String>();
//		try {
//			if (StringUtils.isNotBlank(id)) {
//				String tableName = templeBS.getEntityById(id).getTableName();
//				String dsId = templeBS.getEntityById(id).getDsId();
//				List<UdipTempleColumns> templeColums = templeColumnsBS.findEntityListByProperty("templeId", id);
//				Connection conn = null;
//				Statement state = null;
//				ResultSet rs = null;
//				try {
//					conn = rdbConnectionManagerBS.getConnection(dsId);
//					state = conn.createStatement();
//					StringBuilder sql = new StringBuilder(1000);
//					sql.append("select ");
//					for (int i = 0; i < templeColums.size() - 1; i++) {
//						sql.append(templeColums.get(i).getColumnName()).append(",");
//					}
//					sql.append(templeColums.get(templeColums.size() - 1).getColumnName()).append("  from ").append(tableName);
//					rs = state.executeQuery(sql.toString());
//					System.out.println(rs);
//					while (rs.next()) {
//						StringBuilder strbuff = new StringBuilder();
//						for (int i = 0; i < templeColums.size(); i++) {
//							strbuff.append(rs.getString(i + 1)).append(";");
//						}
//						list.add(strbuff.toString());
//					}
//
//				} catch (Exception e) {
//				} finally {
//					RdbConnectionManagerBS.closeResultSet(rs);
//					RdbConnectionManagerBS.closeStatement(state);
//					RdbConnectionManagerBS.closeConnection(conn);
//				}
//
//			}
//
//		} catch (Exception e) {
//			log.error(e);
//		}
//		return list;
//	}
//
	/**
	 * 根据任务ID获取模板Id
	 * @param taskIds
	 * @return
	 */
	public List<Object[]> getTempleByTask(List<String> taskIds) {
		if ((taskIds == null || taskIds.isEmpty())) {
			return new ArrayList<Object[]>();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("taskIds", taskIds);
		String jql1 = "select a,v from RptInputListTemplateInfo a ,RptTskExeobjRel t ,RptTskIns v where t.taskId=v.taskId and a.templeId =t.templeId and t.taskId in :taskIds order by a.templeName asc";
		return baseDAO.findWithNameParm(jql1, paramMap);
	}
//
//	public List<String> getTempleByCaseId(List<String> taskIds, String caseId) {
//		if (taskIds == null || taskIds.isEmpty() || StringUtils.isBlank(caseId)) {
//			return Lists.newArrayList();
//		}
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("taskIds", taskIds);
//		paramMap.put("caseId", caseId);
//		String jql1 = "select t.templeId as templeId from UdipTaskTempleInfo t ,UdipTaskCaseInfo v where t.taskId=v.taskId and t.taskId in :taskIds and v.caseId=:caseId";
//		return baseDAO.findWithNameParm(jql1, paramMap);
//	}
//	
//	/**
//	 * 
//	 * @param taskIds
//	 * @return
//	 */
//	public List<Object[]> getTempleByTaskId(List<String> taskIds) {
//		if (taskIds == null || taskIds.isEmpty() ) {
//			return Lists.newArrayList();
//		}
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("taskIds", taskIds);
//		String jql1 = "select v,t.taskId as templeId from UdipTaskTempleInfo t ,UdipTemple v where t.templeId=v.templeId and t.taskId in :taskIds order by v.templeName asc";
//		return baseDAO.findWithNameParm(jql1, paramMap);
//	}
	
	/**
	 * 统一获取数据
	 * @param templeId
	 * @param caseId
	 * @return
	 */
	public Map<String, Object> getMap(String templeId, String caseId,boolean isExport) {
		Map<String, Object> map = Maps.newHashMap();
		// 获取任务实例日期
		RptTskIns taskCase = this.getEntityById(caseId);
		// 获取补录模版
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templeId);
		// 获取操作人信息
		String oper = BioneSecurityUtils.getCurrentUserInfo().getLoginName();
		// 获取当天日期
		String data = DateUtils.getYYYY_MM_DD_HH_mm_ss();
		// 获取主键信息
		List<RptInputLstTempleConst> keyList = this.templeConstraintBS.findEntityListByProperty("templeId", templeId);
		// 获取规则信息
		List<RptInputLstTempleRule> ruleList = this.templeRuleBS.findEntityListByProperty("templeId", templeId);
		// 获取模版的所有字段实例
		List<RptInputLstTempleField> colList = this.templeFieldBS.findEntityListByPropertyAndOrder("templeId", templeId, "orderNo", false);
		
		// 排序
		Collections.sort(colList, new Comparator<RptInputLstTempleField>() {
			@Override
			public int compare(RptInputLstTempleField o1,
					RptInputLstTempleField o2) {
//				String no1 = StringUtils.isNotEmpty(o1.getOrderNo()) ? o1.getOrderNo() : "-1";
//				String no2 = StringUtils.isNotEmpty(o2.getOrderNo()) ? o2.getOrderNo() : "-1";
//				int n1 = Integer.parseInt(no1);
//				int n2 = Integer.parseInt(no2);
//				return n1 - n2;
                return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		// 对字段排序
		
		List<String> colName = Lists.newArrayList();
		Map<String,String> colNameType = Maps.newHashMap();
		List<String> colNameCN = Lists.newArrayList();
		List<String> colNameDetail = Lists.newArrayList();
		Map<String, RptInputLstTempleField> colMap = Maps.newHashMap();
		Map<String, Map<String, Object>> libList = Maps.newHashMap();
		List<String> keyCols = Lists.newArrayList();
		for (RptInputLstTempleConst tmp : keyList) {
			if (tmp.getKeyType().equals(UdipConstants.TAB_PRIMARY)) {
				// 把字段放入keyColes集合中
				String[] keysplit = tmp.getKeyColumn().split(";");
				for (String key : keysplit) {
					if (!UdipConstants.TAB_DATA_CASE.equals(key)) {
						keyCols.add(key);
					}
				}
			}
		}
//		keyCols.add(UdipConstants.TAB_ORDER_NO);
		for (RptInputLstTempleField field : colList) {
			// 把字段名称放入map中
			colMap.put(field.getFieldEnName(), field);
			colName.add(field.getFieldEnName());
			colNameType.put(field.getFieldEnName(), field.getFieldType());
			colNameCN.add(field.getFieldCnName());
			colNameDetail.add(field.getFieldDetail());
			if (StringUtils.isNotBlank(field.getDictId())) {
				// 获取字段信息
				List<CommonTreeNode> dictList = this.inputDataDictBS.buildLibTreeByLibId(field.getDictId(), taskCase.getDataDate());
				Map<String, Object> libDataMap = Maps.newHashMap();
				if (CollectionUtils.isNotEmpty(dictList)) {
					for (CommonTreeNode node : dictList) {
					    if(isExport){
                            //增加码值校验
                            libDataMap.put(node.getText(), node.getText());
                            libDataMap.put(node.getId(), node.getText());
                        }else {
                            libDataMap.put(node.getText(), node.getId());
                            libDataMap.put(node.getId(), node.getId());
                        }
					}
				}
				libList.put(field.getFieldEnName(), libDataMap);
			}
		}
		colName.add(UdipConstants.TAB_ORDER_NO);
		RptInputLstTempleField field = new RptInputLstTempleField();
		field.setAllowInput("1");
		colMap.put(UdipConstants.TAB_ORDER_NO, field);
		map.put("taskCase", taskCase);
		map.put("casedata", taskCase.getDataDate());
		map.put("temp", temp);
		map.put("oper", oper);
		map.put("data", data);
		map.put("keyList", keyList);
		map.put("ruleList", ruleList);
		map.put("colMap", colMap);
		map.put("colList", colList);
		map.put("libList", libList);
		map.put("keyCols", keyCols);
		map.put("colName", colName);
		map.put("colNameType", colNameType);
		map.put("colNameCN", colNameCN);
		map.put("colNameDetail", colNameDetail);
		return map;
	}
	
	/**
	 * 根据模版是否补录下级获取机构集合
	 * @param temp
	 * @param orgId
	 * @return
	 */
	public List<String> getOrgList(RptInputLstTempleInfo temp, String orgId, String caseId) {
		List<String> org = Lists.newArrayList();
		if (StringUtils.isBlank(orgId)) {
			if (StringUtils.isNotBlank(temp.getAllowInputLower()) && temp.getAllowInputLower().equals(UdipConstants.STATE_YES)) {
				List<String> children = new ArrayList<String>();
				/* 所有下发的机构 */
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("caseId", caseId);
				List<String> caseOrg = baseDAO.findWithNameParm("select t.orgCode from UdipCaseOrg t where t.caseId=:caseId", paramMap);

				/* 所有该用户有权限补录的机构 */
				List<CommonTreeNode> orgList = authObjectUtils.getOrgList(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());// 所有用户机构
				TreeUtils.childrensCode(orgList, BioneSecurityUtils.getCurrentUserInfo().getOrgNo(), children);
				children.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
				return TreeUtils.containS(caseOrg, children);// 返回存在下发机构中补录机构
			} else {
				org.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			}
		} else {
			String[] organ = orgId.split(",");
			for (String org1 : organ) {
				org.add(org1);
			}
		}
		return org;
	}
	
	public String getTaskCommentByUniqueKey(String templateId,String uniqueKey){
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templateId);
		String condition = uniqueKey.replaceAll(",", "' AND ").replace("=", "='")+"'";
		String sql =" SELECT COMMENTS FROM  "+temp.getTableEnName()+"  WHERE 1=1 AND "+condition;
		String comment = null;
		Connection conn = null;
		Statement stmt = null ;
		try{
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				comment = rs.getString("COMMENTS");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				if(stmt!=null)
				stmt.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return comment;
	}
	
	public void saveComment(String templateId,String comment,String uniqueKey ){
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templateId);
		Connection conn = null;
		Statement stmt = null ;
		try{
			conn = this.dataSourceBS.getConnection(temp.getDsId());
//			String condition = uniqueKey.replaceAll(",", "' AND ").replace("=", "='")+"'";
			StringBuilder sqlBuffer = new StringBuilder();
			sqlBuffer.append(" UPDATE  ").append(temp.getTableEnName()).append("   SET COMMENTS = '").append(comment)
			.append("'  WHERE 1=1 AND ").append("DATAINPUT_ID='").append(uniqueKey).append("'");
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlBuffer.toString());
			conn.commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				if(stmt!=null)
				stmt.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void updateAllErrorMark(String templateId,String flowNodeId,String mark){
		
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templateId);
		Connection conn = null;
		Statement stmt = null ;
		try{
			String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			StringBuilder sqlBuffer = new StringBuilder();
			sqlBuffer.append(" UPDATE  ").append(schema).append(".").append(temp.getTableEnName()).append("   SET ERROR_MARK = '").append(mark)
			.append("' WHERE FLOW_NODE_ID='").append(flowNodeId).append("'");
			stmt.executeUpdate(sqlBuffer.toString());
			conn.commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				if(stmt!=null)
				stmt.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateAllErrorMark(String templateId,String mark){
		
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templateId);
		Connection conn = null;
		Statement stmt = null ;
		try{
			String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			stmt = conn.createStatement();
			StringBuilder sqlBuffer = new StringBuilder();
			sqlBuffer.append(" UPDATE  ").append(schema).append(".").append(temp.getTableEnName()).append("   SET ERROR_MARK = '").append(mark)
			.append("' ");
			stmt.executeUpdate(sqlBuffer.toString());
			conn.commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				if(stmt!=null)
				stmt.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updateErrorMark(String saveObj,String templateId,String mark){
		if(StringUtils.isEmpty(saveObj))
			return ;
		JSONObject jsonObj = JSON.parseObject(saveObj);
		String errorIds = jsonObj.getString("errorIds");
		String comments = jsonObj.getString("comments");
		try {
			comments = URLDecoder.decode(URLDecoder.decode(comments,"UTF-8"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		//更新错误条目
		String[]uniqueList= errorIds.split(";");
		String[]commArray = comments.split(";");
		
		RptInputLstTempleInfo temp = this.templeBS.getEntityById(templateId);
		Connection conn = null;
		Statement stmt = null ;
		StringBuilder sqlBuffer;
		try{
			String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
			conn = this.dataSourceBS.getConnection(temp.getDsId());
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			for(String uniqueKey:uniqueList){
				sqlBuffer = new StringBuilder();
				sqlBuffer.append(" UPDATE  ").append(schema).append(".").append(temp.getTableEnName()).append("   SET ERROR_MARK = '").append(mark)
				.append("'  WHERE DATAINPUT_ID ='").append(uniqueKey).append("'");
				stmt.executeUpdate(sqlBuffer.toString());
			}
			
			for (String comm : commArray) {
				if (StringUtils.isNotBlank(comm)) {
					String[] updComm = comm.split(",");
					String saveCtx = updComm[1];
					if(StringUtils.isEmpty(saveCtx)||saveCtx.equalsIgnoreCase("null"))
						saveCtx = "";
					sqlBuffer = new StringBuilder();
					sqlBuffer.append(" UPDATE  ").append(schema).append(".").append(temp.getTableEnName()).append("   SET COMMENTS = '")
							.append(saveCtx).append("'  WHERE DATAINPUT_ID ='").append(updComm[0]).append("'");
					stmt.executeUpdate(sqlBuffer.toString());
				}
			}
			
			conn.commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				if(stmt!=null)
				stmt.close();
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
//	/**
//	 * 获取当前用户可以提交和验证的机构树
//	 * @param caseId
//	 * @return
//	 */
//	public List<CommonTreeNode> getOrgList(UdipTemple temp,String caseId){
//		List<String> org = Lists.newArrayList();
//		/*所有下发的机构*/
//		List<String> dataState = Lists.newArrayList();
//		dataState.add(UdipConstants.TASK_STATE_SAVE);
//		dataState.add(UdipConstants.TASK_STATE_VALIDATE);
//		dataState.add(UdipConstants.TASK_STATE_REFUSE);
//		dataState.add(UdipConstants.TASK_STATE_DISPATCH);
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("caseId", caseId);
//		paramMap.put("templeId", temp.getTempleId());
//		paramMap.put("dataState", dataState);
//		List<String> caseOrg = baseDAO.findWithNameParm( "select t.orgCode from UdipDataStateInfo t where t.caseId=:caseId and templeId=:templeId and dataState in :dataState", paramMap);
//		
//		/*所有该用户有权限补录的机构*/
//		List<CommonTreeNode> orgList = authObjectUtils.getOrgList(BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());// 所有用户机构
//		
//		if(UdipConstants.STATE_YES.equals(temp.getConnLower())){//可补录下级机构，获取所有下级机构
//			TreeUtils.childrensCode(orgList, BiOneSecurityUtils.getCurrentUserInfo().getOrgNo(), org);
//		}
//		org.add(BiOneSecurityUtils.getCurrentUserInfo().getOrgNo());//加上本用户所在机构
//		return TreeUtils.containT(orgList, TreeUtils.containS(caseOrg, org));
//	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	public Map<String,Object> saveAutoLoad(Map<String, Object> getMap,List<Map<String, Object>> rows,String flowNodeId,String taskInstanceId, String dataDate){
	    Map<String,Object> resMap = Maps.newHashMap();
//        String[] sql = new String[rows.size()];
        
        StringBuilder sqlCol = new StringBuilder();
        StringBuilder sqlVal = new StringBuilder();
        
        Connection conn = null;
        Statement stat = null;
        
        try {
            RptInputLstTempleInfo temp = (RptInputLstTempleInfo)getMap.get("temp");
            
            String schema = this.dataSourceBS.getSchemaByDsId(temp.getDsId());
            
            conn = dataSourceBS.getConnection(temp.getDsId());
            stat = conn.createStatement();
            conn.setAutoCommit(false);
            sqlCol.append("insert into ");
            sqlCol.append(schema);
            sqlCol.append(".");
            sqlCol.append(temp.getTableEnName());
            sqlCol.append(" ( ");
            
            List<String> colName =(List<String>) getMap.get("colName");
            
            BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
            
            String sysOperDate = DateUtils.getFormatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
            boolean first = true;
            for(int i = 0; i < rows.size(); i++){
                sqlVal.delete(0, sqlVal.length());
                Map<String,Object> tm = rows.get(i);
                for(String col : colName){
                    if(!"SYS_ORDER_NO".equals(col)){
                        sqlVal.append(" '");
                        sqlVal.append(tm.get(col) == null ? "" : tm.get(col));
                        sqlVal.append("',");
                    }
                }
                
                if(first){
                    first = false;
                    
                    for(String col : colName){
                        if(!"SYS_ORDER_NO".equals(col)){
                            sqlCol.append(" ");
                            sqlCol.append(col);
                            sqlCol.append(",");
                        }
                    }
                    
                    sqlCol.append("SYS_ORDER_NO,");
                    sqlCol.append("DATAINPUT_ID,");
                    sqlCol.append("SYS_DATA_DATE,");
                    sqlCol.append("SYS_DATA_CASE,");
                    sqlCol.append("SYS_OPERATOR,");
                    sqlCol.append("SYS_OPER_ORG,");
                    sqlCol.append("SYS_OPER_DATE,");
                    sqlCol.append("SYS_DATA_STATE,");
                    sqlCol.append("ERROR_MARK,");
                    sqlCol.append("FLOW_NODE_ID,");
                    sqlCol.append("COMMENTS");
                    sqlCol.append(" ) ");
                    sqlCol.append("values (");
                }
                
                sqlVal.append(" '");
                sqlVal.append(i);
                sqlVal.append("','");
                sqlVal.append(RandomUtils.uuid2());
                sqlVal.append("','");
                sqlVal.append(dataDate);
                sqlVal.append("','");
                sqlVal.append(taskInstanceId);
                sqlVal.append("','");
                sqlVal.append(user.getUserName());
                sqlVal.append("','");
                sqlVal.append(user.getOrgNo());
                sqlVal.append("','");
                sqlVal.append(sysOperDate);
                sqlVal.append("','");
                sqlVal.append("save");
                sqlVal.append("','");
                sqlVal.append("02");
                sqlVal.append("','");
                sqlVal.append(flowNodeId);
                sqlVal.append("','");
                sqlVal.append("");
                sqlVal.append("' ");
                
                sqlVal.append(")");
                
                System.out.println(sqlCol.toString() + sqlVal.toString());
                stat.addBatch(sqlCol.toString() + sqlVal.toString());
            }
            stat.executeBatch();
            
            conn.commit();
            
            String updateJql = "update RptTskIns rti set rti.loadDataMark = '3' where rti.taskInstanceId = ?0 ";
            
            this.baseDAO.batchExecuteWithIndexParam(updateJql, taskInstanceId);
            
            resMap.put("flag", true);
            
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            resMap.put("flag", false);
            resMap.put("msg", "系统错误,请联系管理员.");
            return resMap;
        }finally{
           
            dataSourceBS.releaseConnection(null, stat, conn);
        }
        
        return resMap;
    }
	
	
	
}
