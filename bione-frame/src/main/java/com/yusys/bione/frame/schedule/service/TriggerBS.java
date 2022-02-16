package com.yusys.bione.frame.schedule.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.schedule.entity.BioneTaskInfo;
import com.yusys.bione.frame.schedule.entity.BioneTriggerInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:触发器管理类
 * Description: 
 * </pre>
 * 
 * @author yangyf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class TriggerBS  extends BaseBS<BioneTriggerInfo>{

	protected static Logger log = LoggerFactory.getLogger(TriggerBS.class);
	
	@Autowired
	private TaskBS taskBS;
	
	/**
	 * 分布查询角发器信息
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneTriggerInfo> getTriggerList(int firstResult,
			int pageSize, String orderBy, String orderType,Map<String,Object> conditionMap) {
		String jql = "select trigger from BioneTriggerInfo trigger WHERE trigger.logicSysNo=:logicSysNo ";

		if ((null != conditionMap) && (null != conditionMap.get("field"))) {
			jql+="and trigger." + conditionMap.get("field")+" "+conditionMap.get("op")+" '"+ conditionMap.get("value")+"' ";
		}
		
		if (StringUtils.isNotBlank((CharSequence) conditionMap.get("jql"))) {
			jql+=" and " + conditionMap.get("jql")+" ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by trigger." + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		if(values==null){
			values=new HashMap<String, Object>();
		}
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		
		SearchResult<BioneTriggerInfo> triggerList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql, values);
		return triggerList;
	}
	
	public List<BioneTriggerInfo> findTriggerIdAndName() {
		StringBuilder jql = new StringBuilder("");
		jql.append("select trigger from BioneTriggerInfo trigger where trigger.logicSysNo = '" 
				+ BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo() + "'");
		return this.baseDAO.findWithNameParm(jql.toString(), null);
	}
	
	/**
	 * 判断该触发器下是否有正在运行作业
	 * @param triggerId
	 * @return
	 */
	public String checkHasRunningJobOrNot(String triggerId){
		StringBuilder returnStr = new StringBuilder("");
		if(triggerId == null || "".equals(triggerId)){
			return returnStr.toString();
		}
		String jql = "select task from BioneTaskInfo task where task.logicSysNo=?0 and task.taskSts=?1 and task.triggerId=?2";
		List<BioneTaskInfo> tasks = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
				GlobalConstants4frame.TASK_STS_NORMAL,triggerId);
		if(tasks != null){
			for(int i = 0 ; i < tasks.size() ; i++){
				//若所关联作业中有任何作业处于运行状态，返回
				BioneTaskInfo taskTmp = tasks.get(i);
				String sts = this.taskBS.getTriggerState(taskTmp.getTaskId());
				if("running".equals(sts)){
					if(!"".equals(returnStr.toString())){
						returnStr.append(",");
					}
					returnStr.append(taskTmp.getTaskName());
				}
			}
		}
		return returnStr.toString();
	}
	
	/**
	 * 查看关联了作业的触发器信息
	 * @param ids
	 * @return
	 */
	public String checkHasJobTriggers(String ids){
		StringBuilder names = new StringBuilder("");
		if(ids != null && !"".equals(ids)){
			String[] strs = StringUtils.split(ids, ',');
			for(int i = 0 ; i < strs.length ; i++){
				String idTmp = strs[i];
				//系统任务表
				List<BioneTaskInfo> infoTmp = this.getEntityListByProperty(BioneTaskInfo.class, "triggerId", idTmp);
				//RptTskInfo RPT_TSK_INFO 补录任务
				String rptTskSql = "select 1 from RPT_TSK_INFO where trigger_id=?0";
				List<Object[]>  rptTsklist = this.baseDAO.findByNativeSQLWithIndexParam(rptTskSql, idTmp);
				//RptFltskInfo RPT_FLTSK_INFO 监管报表任务
				String flTskSql = "select 1 from RPT_FLTSK_INFO where trigger_id=?0";
				List<Object[]>  flTsklist = this.baseDAO.findByNativeSQLWithIndexParam(flTskSql, idTmp);
				
				if((infoTmp != null && infoTmp.size() > 0) || (CollectionUtils.isNotEmpty(rptTsklist)) || (CollectionUtils.isNotEmpty(flTsklist))){
					BioneTriggerInfo triggerTmp = this.getEntityById(BioneTriggerInfo.class, idTmp);
					if(!"".equals(names.toString())){
						names.append(",");
					}
					names.append(triggerTmp.getTriggerName());
				}
			}
		}
		return names.toString();
	}
	
	/**
	 * 批量删除
	 * @param ids数组
	 * @return
	 */
	@Transactional(readOnly = false)
	public void deleteBatch(String[] ids){
		if(ids != null){
			List<String> idList = Arrays.asList(ids);
			String jql = "delete from BioneTriggerInfo info where info.triggerId in ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql,idList);
		}
	}
	
	/**
	 * 保存触发器信息
	 * @param 触发器
	 * @return
	 */
	@Transactional(readOnly = false)
	public void saveOrUpdateTrigger(BioneTriggerInfo triggerInfo){
		if(triggerInfo != null){
			this.saveOrUpdateEntity(triggerInfo);
			this.baseDAO.flush();
		}
	}
	
	/**
	 * 重名校验
	 * @param triggerId
	 * @param triggerName
	 * @return
	 */
	public boolean triggerNameValid(String triggerId, String triggerName) {
		Map<String, String> params = Maps.newHashMap();
		String jql = "select trigger from BioneTriggerInfo trigger where trigger.triggerName = :triggerName";
		if(StringUtils.isNotBlank(triggerId)) {
			jql += " and trigger.triggerId != :triggerId";
			params.put("triggerId", triggerId);
		}
		params.put("triggerName", triggerName);
		List<BioneTriggerInfo> triggers = this.baseDAO.findWithNameParm(jql, params);
		if(triggers.size() > 0) {
			return false;
		}
		return true;
	}
}
