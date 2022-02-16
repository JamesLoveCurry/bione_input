package com.yusys.biapp.input.task.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.task.entity.RptTskNodeIns;
import com.yusys.biapp.input.task.web.vo.InputTskLogVO;
import com.yusys.biapp.input.task.web.vo.TaskOperListVO;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;

@Service
@Transactional(readOnly = true)
public class InputTskMonitorBS extends BaseBS<Object>{

	@Autowired
	private DataSourceBS dsBS;
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTaskList(int firstResult,int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		String sql = "SELECT TSKINS.TASK_INSTANCE_ID,TSKINS.TASK_NODE_INSTANCE_ID, TSKINS.TASK_NAME,TINFO.EXE_OBJ_TYPE, "
				+ " TEMPLE.TEMPLE_NAME, TSKINS.START_TIME,USR.USER_NAME,TSKINS.STS,TSKINS.DATA_DATE,ORGINFO.ORG_NAME, "
				+ " PARAM.PARAM_NAME,TINFO.TASK_STS "
				+ " FROM "
				+ " RPT_INPUT_LIST_TABLE_INFO TEMPLE,RPT_INPUT_TSK_INS TSKINS,RPT_INPUT_TSK_INFO TINFO,BIONE_USER_INFO USR,BIONE_ORG_INFO ORGINFO,BIONE_PARAM_INFO PARAM"
				+ " WHERE TSKINS.TASK_TYPE=PARAM.PARAM_VALUE "
				+ " AND TINFO.TASK_ID = TSKINS.TASK_ID "
				+ " AND PARAM.PARAM_TYPE_NO=:paramTypeNo "
				+ " AND TEMPLE.TEMPLE_ID = TSKINS.EXE_OBJ_ID "
				+ " AND ORGINFO.LOGIC_SYS_NO=:logicSysNo "
				+ " AND TSKINS.ORG_NO = ORGINFO.ORG_NO "
				+ " AND USR.USER_ID = TSKINS.CREATE_USER ";
		if (!conditionMap.get("jql").equals("")) {
			sql += " and " + conditionMap.get("sql") + " ";
		}
		if (!StringUtils.isEmpty(orderBy)) {
			sql += "order by TSKINS." + orderBy + " " + orderType;
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		values.put("paramTypeNo", GlobalConstants4frame.TASK_TYPE_NO);
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		SearchResult<Object[]> taskList = this.baseDAO.findPageWithNameParamByNativeSQL(firstResult, pageSize, sql, values);
		List<Object[]> tasks = taskList.getResult();
		List<TaskOperListVO> operVos = new ArrayList<TaskOperListVO>();
		if(tasks != null && tasks.size() > 0){
			for(Object[] task : tasks){
				TaskOperListVO operVo = new TaskOperListVO();
				operVo.setTaskInstanceId(getDefVal(task[0], ""));
				operVo.setTaskNodeInstanceId(getDefVal(task[1], ""));
				operVo.setTaskNm(getDefVal(task[2], ""));
				operVo.setTaskExeObjTypeNm(getDefVal(task[3], ""));
				operVo.setTaskExeObjNm(getDefVal(task[4], ""));
				operVo.setStartTime((String) (task[5] != null ? task[5]:null));
				operVo.setCreator(getDefVal(task[6], ""));
				operVo.setSts(getDefVal(task[7], ""));
				operVo.setDataDate(getDefVal(task[8], ""));
				operVo.setOrgName(getDefVal(task[9], ""));
				operVo.setTaskTypeNm(getDefVal(task[10], ""));
				operVo.setSts(getDefVal(task[11], "0"));
				operVos.add(operVo);
			}
		}
		rsMap.put("Rows", operVos);
		rsMap.put("Total", taskList.getTotalCount());
		return rsMap;
	}
	
	
	public Map<String,Object> getTaskOperList(String taskInstanceId,String taskNodeInstanceId){
		Map<String,Object> map = Maps.newHashMap();
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "select ins from RptTskNodeIns ins where ins.taskInstanceId = :taskInstanceId";
		params.put("taskInstanceId", taskInstanceId);
		if(StringUtils.isNotBlank(taskNodeInstanceId)){
			RptTskNodeIns nins = this.getEntityById(RptTskNodeIns.class, taskNodeInstanceId);
			jql += " and ins.orderNo < :orderNo";
			params.put("orderNo", nins.getTaskOrderno());
		}
		params.put("taskInstanceId", taskInstanceId);
		jql += " order by orderNo";
		List<RptTskNodeIns> mapList =  this.baseDAO.findWithNameParm(jql, params);
		map.put("Rows",mapList);
		return map;
	}
	
	public Map<String,Object> getTaskLogList(String taskInstanceId){
		Map<String,Object>map = Maps.newHashMap();
		String jql = "select new com.yuchengtech.report.input.task.web.vo.InputTskLogVO(ins,log,usr.userName) from RptTskNodeIns ins,InputTskInsLog log,BioneUserInfo usr "
				+ "where ins.taskInstanceId = ?0  and ins.taskNodeInstanceId = log.taskNodeInstanceId and log.operUser = usr.userId order by log.operTime";
		List<InputTskLogVO> mapList =  this.baseDAO.findWithIndexParam(jql, taskInstanceId);
		map.put("Rows",mapList);
		return map;
	}
	
	@Transactional(readOnly = false)
	public Map<String,Object> deleteTask(String taskInstanceId){
		Map<String,Object> res = new HashMap<String, Object>();
		try{
			RptTskIns oins = this.getEntityById(RptTskIns.class,taskInstanceId);
			RptInputLstTempleInfo temple = this.getEntityById(RptInputLstTempleInfo.class, oins.getExeObjId());
			String jql = "select tsk from RptTskNodeIns tsk where tsk.taskInstanceId = ?0";
			List<RptTskNodeIns> inss = this.baseDAO.findWithIndexParam(jql, taskInstanceId);
			List<String> taskNodeInsIds = new ArrayList<String>();
			if(inss != null && inss.size() > 0){
				for(RptTskNodeIns ins : inss){
					taskNodeInsIds.add(ins.getTaskNodeInstanceId());
				}
			}
			jql = "delete from InputTskNodeInsRel rel where rel.id.taskNodeInstanceId in (select node.taskNodeInstanceId from RptTskNodeIns node where node.taskInstanceId = ?0)";
			this.baseDAO.batchExecuteWithIndexParam(jql, taskInstanceId);
			jql = "delete from RptTskNodeIns node where node.taskInstanceId = ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, taskInstanceId);
			jql = "delete from RptTskIns tsk where tsk.taskInstanceId = ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, taskInstanceId);
			jql = "delete from InputTskInsLog log where log.taskInstanceId = ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, taskInstanceId);
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("taskInstanceId", taskInstanceId);
			String sql = "delete "+temple.getTableEnName()+ " where "+GlobalConstants4frame.TAB_DATA_CASE+"= :taskInstanceId";
			excuteByNativeSQL(temple.getDsId(), sql, params);
		}catch(Exception e){
			e.printStackTrace();
			res.put("error", e.getMessage());
		}
		return res;
	}
	
	@SuppressWarnings("rawtypes")
	public Boolean excuteByNativeSQL(String dsId, String sql,
			Map<String, Object> condition) {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean flag = true;
		try {
			conn = this.dsBS.getConnection(dsId);
			Pattern pattern = Pattern.compile(":[^ ]+");
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(sql);
			List<Object> params = new ArrayList<Object>();
			while (matcher.find()) {
				String param = matcher.group(0).substring(1,
						matcher.group(0).length());
				Object parVal = condition.get(param);
				String reStr = "?";
				if (parVal instanceof Collection
						&& ((Collection) parVal).size() > 0) {
					reStr = "(";
					for (int h = 0; h < ((Collection) parVal).size(); h++) {
						if (h != ((Collection) parVal).size() - 1)
							reStr += "?,";
						else
							reStr += "?) ";
						params.add(((Collection) parVal).toArray()[h]);
					}
				} else {
					params.add(parVal);
				}
				matcher.appendReplacement(sb, reStr);
			}
			matcher.appendTail(sb);
			sql = sb.toString();
			pst = conn.prepareStatement(sql);
			for (int l = 0; l < params.size(); l++) {
				try{
					pst.setObject(l + 1, params.get(l));
				}
				catch(Exception e){
					pst.setString(l + 1, params.get(l).toString());
				}
			}
			flag = pst.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeConnection(conn);
			JdbcUtils.closeStatement(pst);
		}
		return flag;
	}
	
	private String getDefVal(Object obj,String defVal){
		if(obj != null){
			return obj.toString();
		}
		else{
			return defVal;
		}
	}
}
