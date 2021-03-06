package com.yusys.bione.frame.activiticustom.web;

import com.yusys.bione.comp.repository.jdbc.JDBCBaseDAO;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.activiticustom.TaskListenerDispatcher;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.delegate.TaskListenerInvocation;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * @author Leo
 *
 */
@Controller
@RequestMapping("/activiti")
public class CustomActiviti extends BaseController {
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ProcessEngineFactoryBean processEngine;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private JDBCBaseDAO jdbcBaseDao;
	
	
	/**
	 * ????????????????????????
	 * @return
	 */
	@RequestMapping(value = "/getAllModels", method = RequestMethod.POST)
	@ResponseBody
	public List<Map<String, Object>> getAllModels() throws SQLException {
		List<Map<String, Object>> list = new ArrayList<>();
		boolean postgresql = jdbcBaseDao.getCon().getMetaData().getURL().contains("postgresql");
		if(postgresql){
			List<Map<String, Object>> maps = jdbcBaseDao.find("select id_,rev_,name_,key_ from act_re_model order by last_update_time_ desc", new Object[0]);
			for (Map<String, Object> map : maps) {
				Set<String> keySet = map.keySet();
				Map<String, Object> hashMap = new HashMap<>();
				for (String key : keySet) {
					if(map.get(key) instanceof PGobject){
						PGobject pGobject = (PGobject) map.get(key);
						hashMap.put(key.toUpperCase(), pGobject.getValue());
					} else if (map.get(key) instanceof Integer){
						Integer integer = (Integer) map.get(key);
						hashMap.put(key.toUpperCase(), integer.toString());
					}
				}
				list.add(hashMap);
			}
		} else {
			list = jdbcBaseDao.find("select id_,rev_,name_,key_ from act_re_model order by last_update_time_ desc", new Object[0]);
			for(int i =0; i < list.size(); i++) {
				Map<String, Object> modelMap =  list.get(i);
				//??????mysql?????????????????????
				if(null != modelMap.get("name_")) {
					modelMap.put("NAME_", modelMap.get("name_"));
				}
				if(null != modelMap.get("id_")) {
					modelMap.put("ID_", modelMap.get("id_"));
				}
				if(null != modelMap.get("key_")){
					modelMap.put("KEY_", modelMap.get("key_"));
				}
				if(null != modelMap.get("rev_")){
					modelMap.put("REV_", modelMap.get("rev_"));
				}
			}
		}
		return list;
	}
	/**
	 * ??????????????????
	 * @param processDefinitionKey ???????????????frs_process3
	 * @param businessKey = taskinsid
	 * @return
	 */
	@RequestMapping(value = "/createInstance", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> createInstance(String processDefinitionKey,String businessKey,Map<String, Object> parameter) {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).singleResult();
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinition.getId())
			.processInstanceBusinessKey(businessKey).singleResult();
		if(processInstance != null) {
			throw new RuntimeException("Have activiti task of same processDefinition running");
		}
		identityService.setAuthenticatedUserId(BioneSecurityUtils.getCurrentUserInfo().getUserId());
		processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), businessKey,parameter);
		if(processInstance==null) {
			throw new RuntimeException("Process instance startup fail");
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("processDefinitionId", processDefinition.getId());
		map.put("processInstanceId", processInstance.getId());
		return map;
	}
	
	/**
	 * ???????????????
	 * @param processDefinitionId
	 * @param processInstanceId
	 * @return
	 */
	@RequestMapping(value = "/processInfo", method = RequestMethod.GET)
	public ModelAndView processInfo(String processDefinitionId,String processInstanceId){
		ModelMap map = new ModelMap();
		map.put("processDefinitionId", StringUtils2.javaScriptEncode(processDefinitionId));
		map.put("processInstanceId", StringUtils2.javaScriptEncode(processInstanceId));
		return new ModelAndView( "/plugin/customactiviti/processInfo", map);
	}
	/**
	 * ????????????????????????????????????
	 * @param processInstanceId
	 * @return
	 */
	@RequestMapping(value = "/getHistoryInfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getHistoryInfo(String processInstanceId){
		Map<String,Object> hisMap = new HashMap<String,Object>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		//?????????????????????
		HistoricProcessInstance hisIns = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		Map<String,String> insInfoMap = new HashMap<String,String>();
		insInfoMap.put("name","??????");
		insInfoMap.put("startTime",formatLongDateToString(hisIns.getStartTime()));
		insInfoMap.put("endTime",formatLongDateToString(hisIns.getStartTime()));//???????????????????????????????????????
		insInfoMap.put("assignee",this.getUserNameById(hisIns.getStartUserId()));
		list.add(insInfoMap);
		//??????????????????
		List<HistoricTaskInstance> hisTasklist = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
		if(hisTasklist!=null && hisTasklist.size()>0) {
			for(HistoricTaskInstance task : hisTasklist) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("name",task.getName());
				map.put("startTime",formatLongDateToString(task.getStartTime()));
				map.put("endTime",formatLongDateToString(task.getEndTime()));
				map.put("assignee",this.getUserNameById(task.getAssignee()));
				list.add(map);
			}
		}
		hisMap.put("Rows", list);
		hisMap.put("Total", list.size());
		return hisMap;
	}

	/**
	 * ?????????????????????????????????????????????
	 * ????????????????????????????????????????????????????????????????????????????????????
	 * @param processDefinitionKey
	 * @param businessKey
	 * @return
	 */
	@RequestMapping(value = "/getStepAndFlows", method = RequestMethod.POST)
	@ResponseBody
	public List<Object> getStepAndFlows (String processDefinitionKey,String businessKey){
		ProcessDefinition processDefinition= repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey).singleResult();
		String processDefinitionId = processDefinition.getId();
		ProcessDefinitionEntity processDefinitionEntity= (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processDefinitionId);
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId)
				.processInstanceBusinessKey(businessKey).singleResult();
		ActivityImpl currActiviti = processDefinitionEntity.findActivity(pi.getActivityId());
		List<Object> list = new ArrayList<Object>();
		if(currActiviti!=null) {
			String activityName = (String)currActiviti.getProperty("name");
			list.add(activityName);
			List<PvmTransition> pvmList = currActiviti.getOutgoingTransitions();
			if(pvmList!=null && pvmList.size()>0) {
				for(PvmTransition pvm : pvmList) {
					String id = pvm.getId();
					String name = (String)pvm.getProperty("name");
					String conditionText = (String) pvm.getProperty("conditionText");
					Map<String,String> pvmMap = new HashMap<String,String>();
					ActivityImpl destActivity = (ActivityImpl)pvm.getDestination();
					String destType = (String)destActivity.getProperty("type");
					if("endEvent".equals(destType)) {
						name = "??????";
					}
					pvmMap.put("destinationType", destType);
					pvmMap.put("name", name);
					pvmMap.put("id", id);
					pvmMap.put("conditionText", conditionText);
					
					list.add(pvmMap);
				}
			}
		}
		return list;
	} 
	
	@RequestMapping(value = "/nextStepByFlowId", method = RequestMethod.POST)
	@ResponseBody
	public boolean nextStepByFlowId(String processDefinitionKey,String businessKey,
			String flowId,String flowName,Map<String,Object> paraMap) {
		//paraMap ????????????
		boolean flag = processNextAct(processDefinitionKey,businessKey,flowId,flowName,paraMap);
		return flag;
	}
	/**
	 * ??????processDefinitionKey??????businessKey???????????????
	 * @param processDefinitionKey
	 * @param businessKey
	 * @return
	 */
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	@ResponseBody
	public String cancel(String processDefinitionKey,String businessKey,String comment) {
		String info ="";
		try {
			// ??????????????????
			String processDefinitionId = getProcessDefinitionId(processDefinitionKey);
			List<ProcessInstance> list1 = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId)
				.processInstanceBusinessKey(businessKey).list();
			if(list1 !=null && list1.size()>0) {
				for(ProcessInstance processInstance : list1) {
					runtimeService.deleteProcessInstance(processInstance.getId(), StringUtils.isBlank(comment) ?"????????????":comment);	
				}
			}
			List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processDefinitionId).processInstanceBusinessKey(businessKey).list();
			if(list !=null && list.size()>0) {
				for(HistoricProcessInstance processInstance : list) {
					historyService.deleteHistoricProcessInstance(processInstance.getId());//deleteProcessInstance(processInstance.getId(), StringUtils.isBlank(comment) ?"????????????":comment);	
				}
			}
			info = "?????????";
		} catch (Exception e) {
			info = "????????????";
		}
		return info;
	}
	/**
	 * ??????processDefinitionKey?????????????????????
	 * @param processDefinitionKey
	 * @return
	 */
	@RequestMapping(value = "/cancelByKey", method = RequestMethod.POST)
	@ResponseBody
	public String cancelByKey(String processDefinitionKey) {
		String info ="";
		try {
			// ??????????????????
			String processDefinitionId = getProcessDefinitionId(processDefinitionKey);
			List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
					.processDefinitionId(processDefinitionId)
					.list();
			if(list !=null && list.size()>0) {
				for(ProcessInstance processInstance : list) {
					runtimeService.deleteProcessInstance(processInstance.getId(), "????????????");	
				}
			}
			info = "?????????";
		} catch (Exception e) {
			info = "????????????";
		}
		return info;
	}
	public boolean processNextAct (String processDefinitionKey,String businessKey,
			String flowId,String flowName,Map<String,Object> paraMap) {
		// ?????????????????? 
		String processDefinitionId = this.getProcessDefinitionId(processDefinitionKey);
		RepositoryServiceImpl repositoryServiceImpl = (RepositoryServiceImpl)repositoryService;
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryServiceImpl
				.getDeployedProcessDefinition(processDefinitionId);
		ProcessInstance pi = getProcessInstance(processDefinitionId,businessKey);
		Task currTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
		//??????????????????
		ActivityImpl currActiviti = processDefinitionEntity.findActivity(pi.getActivityId());
		ActivityImpl nextActivity = null;
		boolean flag = true;
		
        try {
        	nextActivity = this.getNextUserTask(currActiviti, flowId, paraMap);
			this.jump(currTask, nextActivity,flowName);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
        return flag;
	}
	/**
	 * @param activity	????????????
	 * @param flowId	????????????
	 * @param paraMap	???????????????
	 * @return
	 */
	public ActivityImpl getNextUserTask(ActivityImpl activity,String flowId,Map<String,Object> paraMap) {
        List<PvmTransition> outPvmlist = activity.getOutgoingTransitions();
        if (outPvmlist != null && outPvmlist.size() > 0) {
        	if(!"".equals(flowId)) {
        		for (PvmTransition pvm : outPvmlist) {
        			String pvmId = pvm.getId();
                    if(flowId.equals(pvmId)) {
                    	ActivityImpl nextActivity = (ActivityImpl) pvm.getDestination();
                        String type = (String)nextActivity.getProperty("type");
                        if("userTask".equals(type) || "endEvent".equals(type)) {
                        	return nextActivity;
                        }else {
                        	return this.getNextUserTask(nextActivity,"",paraMap);
                        }
                    }
                }
        	}else {
        		for (PvmTransition pvm : outPvmlist) {
                	String conditionText = (String) pvm.getProperty("conditionText");
                	boolean flowConditioon = isConditionMap(paraMap,conditionText);
                    if(flowConditioon== true) {
                    	ActivityImpl nextActivity = (ActivityImpl) pvm.getDestination();
                        String type = (String)nextActivity.getProperty("type");
                        if("userTask".equals(type)) {
                        	return nextActivity;
                        }else {
                        	return this.getNextUserTask(nextActivity,"",paraMap);
                        }
                    }
                }
        	}
        }
        return null;
	}
	
	/**
	 * @param paraMap	??????????????????????????????:?????????????????????
	 * @param el   	?????????????????? 	
	 * @return
	 */
	public boolean isConditionMap(Map<String,Object> paraMap,String el) {
		ExpressionFactory factory = new ExpressionFactoryImpl();
		SimpleContext context = new SimpleContext();
		for(String key : paraMap.keySet()) {
			context.setVariable(key, factory.createValueExpression(paraMap.get(key), paraMap.get(key).getClass()));
		}
		ValueExpression e = factory.createValueExpression(context, el,boolean.class);
		return(Boolean)  e.getValue(context);
	}
	
	/**
	 * ????????????
	 * @param currTask
	 * @param destActivity
	 * @param comment
	 * @throws Exception
	 */
	public void jump(final Task currTask,final ActivityImpl destActivity,final String comment) throws Exception {
		
		processEngine.getObject().getManagementService().executeCommand(new Command<ExecutionEntity>() {
			@Override
			public ExecutionEntity execute(CommandContext commandContext) {
				TaskEntity taskEntity = commandContext.getTaskEntityManager().findTaskById(currTask.getId());
				ExecutionEntity executionEntity = taskEntity.getExecution();
		        //taskEntity.fireEvent("complete");
				String taskEventName = "complete";
		        TaskDefinition taskDefinition = taskEntity.getTaskDefinition();
		        if (taskDefinition != null) {
		          List<TaskListener> taskEventListeners = taskEntity.getTaskDefinition().getTaskListener(taskEventName);
		          if(taskEventListeners == null) {
		        	  taskEventListeners = new ArrayList<TaskListener>();
		        	  TaskListenerDispatcher taskListenerDispatcher = SpringContextHolder.getBean("taskListenerDispatcher");
		        	  taskEventListeners.add(taskListenerDispatcher);
		          }
		          if (taskEventListeners != null) {
		            for (TaskListener taskListener : taskEventListeners) {
		              ExecutionEntity execution = taskEntity.getExecution();
		              if (execution != null) {
		            	  taskEntity.setAssignee(BioneSecurityUtils.getCurrentUserInfo().getUserId());
		            	  taskEntity.setEventName(taskEventName);
		            	  taskEntity.setVariableLocal("destName", destActivity.getProperties().get("name"));
		              }
		              try {
		                Context.getProcessEngineConfiguration()
		                  .getDelegateInterceptor()
		                  .handleInvocation(new TaskListenerInvocation(taskListener, (DelegateTask)taskEntity));
		              }catch (Exception e) {
		                throw new ActivitiException("Exception while invoking TaskListener: "+e.getMessage(), e);
		              }
		            }
		          }
		        }
		      
		        executionEntity.destroyScope(comment);
		        executionEntity.executeActivity(destActivity);
		        return executionEntity;
			}
		});
	} 
	
	/**
	 * ??????????????????
	 * @param processDefinitionId
	 * @param businessKey
	 * @return
	 */
	public ProcessInstance getProcessInstance(String processDefinitionId, String businessKey) {
		ProcessInstanceQuery piQuery = runtimeService.createProcessInstanceQuery()
				.processDefinitionId(processDefinitionId);
		piQuery.processInstanceBusinessKey(businessKey);
		ProcessInstance processInstance = piQuery.singleResult();
		return processInstance;
	}
	/**
	 * ??????historyService??????processInstanceId??????????????????????????????
	 * @param processDefinitionId
	 * @param businessKey
	 * @return
	 */
	public String getProcessInstanceId(String processDefinitionId, String businessKey) {
		String processInstanceId = historyService.createHistoricProcessInstanceQuery()
			.processDefinitionId(processDefinitionId)
			.processInstanceBusinessKey(businessKey)
			.notDeleted()
			.singleResult()
			.getId();
		return processInstanceId;
	}
	/**
	 * ??????????????????ID
	 * @param processDefinitionKey
	 * @return
	 */
	public String getProcessDefinitionId(String processDefinitionKey) {

		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition processDefinition = processDefinitionQuery
				.processDefinitionKey(processDefinitionKey).singleResult();
		if (processDefinition != null) {
			return processDefinition.getId();
		}
		throw new RuntimeException("Cannot find Process Definition of processDefinitionKey: " + processDefinitionKey);
	}
	
	/**
	 * ??????????????????
	 * @param processDefinitionId
	 * @param businessKey
	 * @return
	 */
	public Execution getExecution(String processDefinitionId,String businessKey) {
		ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
				.processDefinitionId(processDefinitionId);
        if (StringUtils.isNotEmpty(businessKey)) {
			executionQuery.processInstanceBusinessKey(businessKey);
		}
        return executionQuery.singleResult();
	}
	public Execution getExecution(String processInstanceId) {
		ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
        		.processInstanceId(processInstanceId);
        return executionQuery.singleResult();
	}
	public String formatLongDateToString(Date p_date) {
		String strtime = "";
		SimpleDateFormat format;
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		if(p_date!=null && p_date.getTime()>0) {
			strtime = format.format(p_date);
		}
		return strtime;
	}
	
	public String getUserNameById(String userId) {
		String user_name = null;
		if(userId != null) {
			String sql = "select t.user_name from bione_user_info t where t.user_id= '"+userId+"'";
			Map<String,Object> map = jdbcBaseDao.findObject(sql,new Object[0]);
			user_name = (String) map.get("USER_NAME");
		}
		return user_name;
	}

}
