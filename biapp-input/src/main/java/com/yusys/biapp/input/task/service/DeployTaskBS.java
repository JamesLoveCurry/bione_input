package com.yusys.biapp.input.task.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;
import com.yusys.biapp.input.inputTable.entity.RptListDataloadSqlInfo;
import com.yusys.biapp.input.inputTable.service.InputTableBS;
import com.yusys.biapp.input.task.entity.*;
import com.yusys.biapp.input.task.repository.TaskDao;
import com.yusys.biapp.input.task.repository.TaskOperDao;
import com.yusys.biapp.input.task.vo.*;
import com.yusys.biapp.input.task.web.vo.DeployTaskVO;
import com.yusys.biapp.input.task.web.vo.TaskDeptUserList;
import com.yusys.biapp.input.task.web.vo.TaskUnifyNodeVO;
import com.yusys.biapp.input.utils.DateUtils;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SqlValidateUtils;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfoExt;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.authobj.service.RoleBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.schedule.entity.BioneTriggerInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.frame.util.SplitStringBy1000;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
@Transactional(readOnly = true)
public class DeployTaskBS extends BaseBS<RptTskInfo> {
    
    protected static Logger log = LoggerFactory.getLogger(DeployTaskBS.class);
    
    @Autowired
    private DataSourceBS dataSourceBS;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private AuthBS authBS;
	@Autowired
	private SchedulerFactoryBean scheduler;
	@Autowired
	private InputTableBS tableBS;
	@Autowired
	private UserBS userBS;
	@Autowired
	private OrgBS orgBs;
	@Autowired
	private TaskOperDao taskOperDao;

	@Autowired
	private RoleBS roleBS;

	@Value("${database.type}")
	private String databaseType;
	
	public static final String DEFAULT_ROOT = "0";
	public static final String CATALOG_TYPE = "catalog";
	public static final String TASK_TYPE = "taskinfo";
	public static final String PARAM_TYPE_NO = "reportorgtype";
	
	public Map<String, Object> getTaskNodeTypeList(String paramTypeNo) {
	    Map<String, Object> resMap = Maps.newHashMap();
	    Map<String, Object> translate = Maps.newHashMap();
	    List<CommonComboBoxNode> nodes = Lists.newArrayList();
	    String jql = "select param from BioneParamInfo param where param.paramTypeNo = ?0 and param.logicSysNo = ?1";
	    List<BioneParamInfo> params = this.baseDAO.findWithIndexParam(jql, paramTypeNo,BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	    for(BioneParamInfo param : params){
	        CommonComboBoxNode node = new CommonComboBoxNode();
	        node.setId(param.getParamValue());
	        node.setText(param.getParamName());
	        nodes.add(node);
	        translate.put(param.getParamValue(), param.getParamName());
	    }
	    resMap.put("combox", nodes);
	    resMap.put("translate", translate);
	    return resMap;
    }

	public String localSaveTask(DeployTaskVO deployTaskVO) {
		
		String taskId = createTaskBaseInfo(deployTaskVO);

		/**
		 * chenhx 20171121
		 * 新增后置依赖任务
		 */
		if("1".equals(deployTaskVO.getRptTskInfo().getAfterTaskSts())){
	        startWeihaiTask(deployTaskVO.getAfterTask());
	    }
		return taskId;
	}

	@Transactional( readOnly = false)
	public String startWeihaiTask(DeployTaskVO deployTaskVO){
		DeployTaskVO oldDeployTaskVO = getTaskById(deployTaskVO.getRptTskInfo().getTaskId(),null);
		oldDeployTaskVO.getRptTskInfo().setTaskDefId(deployTaskVO.getRptTskInfo().getTaskDefId());
		oldDeployTaskVO.getRptTskInfo().setNodeType(deployTaskVO.getRptTskInfo().getNodeType());
		deployTaskVO.setRptTskInfo(oldDeployTaskVO.getRptTskInfo());
		deployTaskVO.setTskExeobjRelVO(oldDeployTaskVO.getTskExeobjRelVO());
		
		return startTask(deployTaskVO);
	}
	
	@Transactional( readOnly = false)
	public String startTask(DeployTaskVO deployTaskVO) {
		
		List<String>orgNos = deployTaskVO.getOrgs();
		
		String taskId = createTaskBaseInfo( deployTaskVO);
		createTaskInsInfo( deployTaskVO,orgNos,deployTaskVO.getLoadDataMark());
		return taskId;
	}
	

	/**
	 * 启动一个作业
	 * 
	 * @param tskInfo
	 * @param taskFlow
	 * @return
	 */
	@Transactional( readOnly = false)
	public boolean deployTask( String  taskId,String orgs,String dataDate) {
		try{
			DeployTaskVO deployTaskVO = getTaskById(taskId,null);
			deployTaskVO.setDataDate(dataDate);
			List<String>orgNos = null;
			if(StringUtils.isNotEmpty(orgs))
				orgNos = getOrgsByJson(orgs);
			createTaskInsInfo( deployTaskVO,orgNos,deployTaskVO.getLoadDataMark());
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean changeTaskSts(String taskIds){
		try{
			taskDao.changeTaskSts(Arrays.asList(taskIds.split(",")));
			/******添加日志记录******/
//			List<Map<String,String>>nms = taskDao.getTaskNmsByIds(Arrays.asList(taskIds.split(",")));
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("用户[").append(user.getLoginName()).append("]").append("修改了任务及其状态分别为:");
//			boolean isFirst=true;
//			for(Map<String,String> nm:nms){
//				if(isFirst)
//					isFirst=false;
//				else
//					buff.append(",");
//				buff.append("[").append(nm.get("TASK_NM")).append(",").append(nm.get("TASK_STS").equals("1")?"启用":"停用").append("]");
//			}
//			saveLog("03", "补录任务管理 ", buff.toString(), user.getUserId(), user.getLoginName());
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean hasTrigger(String taskId,String tirggerId){
		try {
			JobDetail dtl =  scheduler.getScheduler().getJobDetail(new JobKey(taskId, taskId));
			if(dtl==null){
				return false;
			}else{
				return true;
			}
		} catch (SchedulerException e) {
			return false;
		}
	}
	
	public boolean stopTrigger(String taskId,String tirggerId){
		try {
			scheduler.getScheduler().deleteJob(new JobKey(taskId, taskId));
			/******添加日志记录******/
//			List<String>l=Lists.newArrayList();
//			l.add(taskId);
//			List<Map<String,String>>nms = taskDao.getTaskNmsByIds(l);
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("用户[").append(user.getLoginName()).append("]").append("停止了自动任务下发,对应的任务为:");
//			boolean isFirst=true;
//			for(Map<String,String> nm:nms){
//				if(isFirst)
//					isFirst=false;
//				else
//					buff.append(",");
//				buff.append("[").append(nm.get("TASK_NM")).append("]");
//			}
//			saveLog("03", "补录任务管理 ", buff.toString(), user.getUserId(), user.getLoginName());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public String delTaskFlow(String taskDefId){
		try{
			Map<String,Object>m=Maps.newHashMap();
			m.put("taskDefId", taskDefId);
			// List<RptTskFlow>flowList = taskDao.getListWorkFlow(m);
			Map<String,Object>params=Maps.newHashMap();
			params.put("taskDefId", taskDefId);
			taskDao.delTaskFlowNode(params);
			taskDao.delTaskFlow(taskDefId);
			/*********添加日志记录***********/
//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			buff.append("用户[").append(user.getLoginName()).append("]删除了任务流程:").append(flowList.get(0).getFlowNm());
//			userBS.saveLog("02", "补录任务管理-任务流程管理", buff.toString(), user.getUserId(), user.getLoginName());
		}catch(Exception ex){
			return "删除出错,错误原因:"+ex;
		}
		return "1";
		
	}
	
	public String saveTaskFlowNode(TaskFlowNodeVO flowNodeVO){

		try{
			Map<String,Object>params=Maps.newHashMap();
			params.put("taskDefId", flowNodeVO.getTaskDefId());
			taskDao.delTaskFlowNode(params);
			List<RptTskFlowNode> nodeList=flowNodeVO.getFlowNodeList();
			
			//日志定义参数
//			List<String>saveNodes=Lists.newArrayList();
			
			for(RptTskFlowNode node:nodeList){
//              saveNodes.add(node.getFlowNodeNm()+"@@"+node.getNodeType());
                node.setTaskNodeDefId(RandomUtils.uuid2());
                taskDao.saveTaskFlowNode(node);
        }

//			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//			StringBuilder buff = new StringBuilder();
//			Map<String,Object>m=Maps.newHashMap();
//			m.put("taskDefId", flowNodeVO.getTaskDefId());
//			List<RptTskFlow>flowList = taskDao.getListWorkFlow(m);
//			buff.append("用户[").append(user.getLoginName()).append("]").append("").append("修改了任务流程:").append(flowList.get(0).getFlowNm()).append("变更后的节点信息为:");
//			boolean isFirst=true;
//			if(saveNodes!=null&&!saveNodes.isEmpty()){
//				for(String node:saveNodes)
//				{
//					if(isFirst)
//						isFirst=false;
//					else
//						buff.append(",");
//					String[] tg = node.split("@@");
//					buff.append("[").append(tg[0]).append(",").append(tg[1].equals("01")?"填报":"审批").append("]");
//				}
//			}
//			saveLog("03", "补录任务管理-任务流程管理", buff.toString(), user.getUserId(), user.getLoginName());
		}catch(Exception ex){
			return "删除出错,错误原因:"+ex;
		}
		return "1";
	}
	

	/**
	 * 立即执行一个job
	 * 
	 * @param jobId
	 *            任务名Id
	 * @throws RuntimeException
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	private void startNowJob(String taskId,String triggerId, int dateOffsetAmount,String loadDataMark,String dataDate) {

		//String taskKey = BioneSecurityUtils.getCurrentUserInfo().getUserId()+"_"+taskId;
		String taskKey = taskId;
		try {
			JobDetail jobDetail = scheduler.getScheduler().getJobDetail(new JobKey(taskKey, taskKey)); 
			if (jobDetail != null) {	// jobDetail == null 说明这个任务还没有添加到调度任务
				scheduler.getScheduler().triggerJob(new JobKey(taskKey, taskKey));
			} else {
				jobDetail = newJob((Class<? extends Job>) Class.forName("com.yusys.biapp.input.task.job.DeployTaskJob")).withIdentity(taskKey, taskKey).build();
				// 创建触发器
				jobDetail.getJobDataMap().put("userId", BioneSecurityUtils.getCurrentUserInfo().getUserId());
				jobDetail.getJobDataMap().put("loadDataMark", loadDataMark);
				jobDetail.getJobDataMap().put("dateOffsetAmount", dateOffsetAmount);
//				jobDetail.getJobDataMap().put("dataDate", this.getCurDataDateStrByStemp(dateOffsetAmount));
				final TimeZone zone = TimeZone.getTimeZone("GMT+8"); // 获取中国时区
				TimeZone.setDefault(zone); // 设置时区
				BioneTriggerInfo triggerInfo = this.getEntityById(BioneTriggerInfo.class, triggerId);
				String Cron = triggerInfo.getCron();
				Date starttime = triggerInfo.getStartTime();
				Date currtime = org.apache.commons.lang3.time.DateUtils.addMinutes(new Date(), 1);
				if (starttime == null) {
					starttime = currtime; 
				}else{
					starttime = appendDate(starttime,dateOffsetAmount);
					if(currtime.after(starttime)) 
						starttime = currtime;
				}
				Date endtime = triggerInfo.getEndTime();
				if(endtime == null) {
					endtime = DateUtils.getDateStart("2099-12-12");
				}else {
					endtime = DateUtils.getDateEnd(endtime.toString());
				}
				if(endtime.before(starttime))
					return ;
				TriggerBuilder<Trigger> builder = newTrigger().withIdentity(taskKey, taskKey).startAt(starttime);
				if(endtime !=null)
					builder.endAt(endtime);
				Trigger trigger = builder.withSchedule(cronSchedule(Cron)).build();
				
				scheduler.getScheduler().scheduleJob(jobDetail, trigger);
				scheduler.getScheduler().interrupt(new JobKey(taskKey,taskKey));
				// scheduleJob 只是添加了任务，并没有马上执行一次
				//scheduler.getScheduler().triggerJob(new JobKey(taskId,triggerId));
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException("立即运行一个任务时异常", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	private Date appendDate(Date date, int value) {
		Calendar calendar = new GregorianCalendar();
		if (value == 9999) {
			calendar.setTime(new Date());
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			calendar.add(Calendar.MONTH, 1);
		} else {
			calendar.setTime(date);
			calendar.add(calendar.DATE, value);//把日期往后增加一天.整数往后推,负数往前移动
		}
		return calendar.getTime();
	}
	
	
	public Set<String> findSqlAsCol(String sql) {
		Pattern pattern2 = Pattern.compile("(as|AS|aS|As)\\s+[a-zA-Z0-9_]*(\\s|,)");
		Matcher matcher2 = pattern2.matcher(sql);
		Set<String> values = Sets.newHashSet();
		while(matcher2.find()){
			String col = matcher2.group().toUpperCase();
			values.add(col.replace("AS ", "").replace(",", "").trim());
		}
		return values;
	}
	
	public Set<String> getLoadData(String taskId,boolean isAfterTask,List<String> orgNos,String dataDate){
		
		List<RptListDataloadSqlInfo> sqlList = tableBS.getSqlInfoByTaskId(taskId);
	    Set<String> sqlSet = Sets.newHashSet();
	   
	    //查询预加载sql
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();
        try {
            if(sqlList == null || sqlList.size() == 0){
                throw new RuntimeException("未查询到预加载SQL.");
            }
            if(dataDate.length() == 10){
				dataDate = StringUtils.replace(dataDate,"-","");
			}
            
            RptListDataloadSqlInfo rptListDataloadSqlInfo = sqlList.get(0);
            
            String sql = StringUtils.replace(rptListDataloadSqlInfo.getSql2(), "@{dataDate}", "'"+dataDate+"'");
            
            Set<String> cols = findSqlAsCol(sql);
            
            if(cols.contains("FILTER_NM")
                    || cols.contains("FILTER_NO")){
            	
            	boolean isTrans = cols.contains("FILTER_NM");
            	
            	//查询需要过滤的字段
            	String filgerCol = null;
            	sb.append("SELECT ");
            	if(!isTrans){
            		filgerCol = "FILTER_NO";
            	}else{
            		filgerCol = "FILTER_NM";
            	}
            	log.info("触发器下发任务:应用过滤字段:" + filgerCol);
            	sb.append(filgerCol);
            	sb.append(" FROM ( ");
            	sb.append(sql);
            	sb.append(" ) s ");
            	log.info("触发器下发任务:过滤sql为:" + sb.toString());
            	//JDBC查询需要过滤的字段
            	log.info("获取预加载sql数据源Connection -start");
                conn = dataSourceBS.getConnection(rptListDataloadSqlInfo.getDsId());
                log.info("获取预加载sql数据源Connection -end");
                log.info("获取预加载sql数据源Statment -satrt");
                stat = conn.createStatement();
                log.info("获取预加载sql数据源Statment -end");
                log.info("获取预加载sql数据源ResultSet -start");
                rs = stat.executeQuery(sb.toString());
                log.info("获取预加载sql数据源ResultSet -end");
                while(rs.next()){
                    sqlSet.add(rs.getString(filgerCol));
                }
                //去重的机构
                Set<String> orgNosSet = Sets.newHashSet();
                orgNosSet.addAll(orgNos);
                //最后参数list
                List<String> paramOrg = Lists.newArrayList();
                
                if(!isTrans){
                	for(String o : sqlSet){
                		if(!orgNosSet.add(o)){
                			paramOrg.add(o);
                		}
                	}
                	
                }else{
                	//数据字典查出的数据是     字典名A:值A;;字典名B:值B
                    Map<String, String> dictMap = getorgDict();
                    //循环查询需要过滤的机构
                    for(String o : sqlSet){
                        //如果去重的机构添加不了
                        	try {
                        		if(dictMap.containsKey(o)){
                        			if(!orgNosSet.add(dictMap.get(o))){
    	                    		//那就通过机构名 查询字典map将机构存进去
    	                    			paramOrg.add(dictMap.get(o));
    	                    		}
                        		}else{
                        			throw new RuntimeException("无法再FILTER数据字典里查询到机构["+o+"]信息。");
                        		}
    						} catch (Exception e) {
    							log.info("机构[" + o + "]触发器下发失败,失败原因："+e.getMessage());
//    							e.printStackTrace();
    						}
                    }
                    log.info("过滤后机构集合为:" + paramOrg.size());
                }
                sqlSet.clear();
                sqlSet.addAll(paramOrg);
                
            }else{
            	/*sb.append("SELECT ");
            	sb.append(" count(1) as cnt ");
            	sb.append(" FROM ( ");
            	sb.append(sql);
            	sb.append(" ) s ");
            	log.info("触发器下发任务:未发现过滤字段，过滤sql为:" + sb.toString());
            	
            	//JDBC查询需要过滤的字段
            	log.info("获取预加载sql数据源Connection -start");
                conn = dataSourceBS.getConnection(rptListDataloadSqlInfo.getDsId());
                log.info("获取预加载sql数据源Connection -end");
                log.info("获取预加载sql数据源tatment -satrt");
                stat = conn.createStatement();
                log.info("获取预加载sql数据源Statment -end");
                log.info("获取预加载sql数据源ResultSet -start");
                rs = stat.executeQuery(sb.toString());
                log.info("获取预加载sql数据源ResultSet -end");
                
                if(rs.next()){
                	String cnt = rs.getString("cnt");
                	if(!"0".equals(cnt)){
                		sqlSet.addAll(orgNos);
                	}
                }*/
            	sqlSet.addAll(orgNos);
                log.info("未发现过滤字段,本次下发根据实际选中机构下发。");
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
            log.info(taskId + "获取预加载过滤机构数据失败了,失败原因:" + e.getMessage());
            sqlSet.clear();
        }finally{
            dataSourceBS.releaseConnection(rs, stat, conn);
        }
        return sqlSet;
	}
	
	
	public List<String> filterDeployOrg(String taskId,List<String> orgNos,String loadDataMark,String dataDate){
		List<String> resList = Lists.newArrayList();
        /**
         * 触发器下发查询 FILTER_NM字段
         *    后置依赖任务触发查询FILTER_NO字段
         */
        //如果需要预加载
        if("true".equals(loadDataMark)){
            try {
                Set<String> sqlSet = getLoadData(taskId, false,orgNos,dataDate);
                resList.addAll(sqlSet);
                return resList;
            } catch (Exception e) {
            	e.printStackTrace();
                log.info("获取过滤机构失败.错误原因:" + e.getMessage());
            }
        }
	    return resList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getorgDict(){
	    StringBuilder jql = new StringBuilder();
	    PropertiesUtils pUtils = PropertiesUtils.get("input.properties");
	    
        jql.append("select dict from RptInputListDataDictInfo dict where dict.dictName = '"+ pUtils.getProperty("DEPLOY_DICT") +"' ");
        List<RptInputListDataDictInfo> dicts = this.baseDAO.createQueryWithIndexParam(jql.toString()).getResultList();
        
        if(dicts == null || dicts.size() == 0){
            throw new RuntimeException("根据FILTER_NM过滤时,未查询到FILTER_NM过滤字典.");
        }
        //数据字典查出的数据是     字典名A:值A;;字典名B:值B
        Map<String, String> dictMap = Maps.newHashMap();
        RptInputListDataDictInfo dict = dicts.get(0);
        String staticDictStr = dict.getStaticContent();
        //将字典名A:值A;;字典名B:值B 分割成  [字典名A:值A,字典名B:值B]
        String[] staticDictArr = staticDictStr.split(";;");
        String[] staticDictObjArr = null;
        for(String staticDictObj : staticDictArr){
            //将[字典名A:值A,字典名B:值B] 分割成 [字典名A,值A]
            staticDictObjArr = staticDictObj.split(":");
            if(staticDictObjArr != null && staticDictObjArr.length == 2){
                //组装map {字典名A:值A,字典名B,值B}
                dictMap.put(staticDictObjArr[0], staticDictObjArr[1]);
            }
        }
        return dictMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getorgDictRev(){
        StringBuilder jql = new StringBuilder();
        jql.append("select dict from RptInputListDataDictInfo dict where dict.dictName = 'FILTER_NM' ");
        List<RptInputListDataDictInfo> dicts = this.baseDAO.createQueryWithIndexParam(jql.toString()).getResultList();
        
        if(dicts == null || dicts.size() == 0){
            throw new RuntimeException("数据字典查询失败.");
        }
        //数据字典查出的数据是     字典名A:值A;;字典名B:值B
        Map<String, String> dictMap = Maps.newHashMap();
        RptInputListDataDictInfo dict = dicts.get(0);
        String staticDictStr = dict.getStaticContent();
        //将字典名A:值A;;字典名B:值B 分割成  [字典名A:值A,字典名B:值B]
        String[] staticDictArr = staticDictStr.split(";;");
        String[] staticDictObjArr = null;
        for(String staticDictObj : staticDictArr){
            //将[字典名A:值A,字典名B:值B] 分割成 [字典名A,值A]
            staticDictObjArr = staticDictObj.split(":");
            if(staticDictObjArr != null && staticDictObjArr.length == 2){
                //组装map {字典名A:值A,字典名B,值B}
                dictMap.put(staticDictObjArr[1], staticDictObjArr[0]);
            }
        }
        return dictMap;
    }
	

	public void beginTriggerTask(String taskId,String creator,String loadDataMark,String dataDate){
		
		DeployTaskVO taskNodeVO = getTaskById(taskId,creator);
		if(StringUtils.isEmpty(taskId)|| taskNodeVO.getRptTskInfo()==null)
			return ;
		List<String> orgNos = getTaskOrgs(taskId);
		
		if("true".equals(loadDataMark)){
		    List<String> paramOrg = filterDeployOrg(taskId,orgNos,loadDataMark,dataDate);
		    if(paramOrg != null && paramOrg.size() > 0){
		        orgNos = paramOrg;
		    }else{
	    		log.info("触发器下发：预加载任务没有有数据的机构本次取消下发。---taskId" + taskId);
		    	return;
		    }
		}
		log.info("taskId" + taskId + "触发器下发：下发成功总机构个数：" + orgNos.size() + "。机构详情："
        		+ orgNos.toString());
		deploySingleTaskWithOrgs(orgNos,taskNodeVO,dataDate,creator,loadDataMark);
		
		
	}
	/**
	 * 获取组织相关信息
	 * @param orgs
	 * @return
	 */
	private List<String> getOrgsByJson(String orgs){
		List<String>orgList = Lists.newArrayList();
		JSONArray taskValueObj = JSON.parseArray(orgs);
		for(int i = 0 ; i <taskValueObj.size();i++){
			orgList.add(taskValueObj.getString(i));
		}
		return orgList;
	}

	/**
	 * 创建基本流程信息
	 * 
	 * @param tskInfo
	 * @param taskNodeVO
	 * @param params
	 */
	private String createTaskBaseInfo(DeployTaskVO taskNodeVO) {

		// 1. 保存任务主表信息
		String taskId = taskNodeVO.getRptTskInfo().getTaskId();
		RptTskInfo tskInfo = taskNodeVO.getRptTskInfo();
		tskInfo.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		if(StringUtils.isEmpty(taskId)){
			taskId = RandomUtils.uuid2();
			BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
			tskInfo.setTaskId(taskId);
			tskInfo.setCreateUser(bioneUser.getUserId());
			tskInfo.setCreateOrg(bioneUser.getOrgNo());
			taskDao.saveRptTskInfo(tskInfo);
		}else{
			//更新任务信息
			taskDao.updateRptTskInfo(tskInfo);
			//清除任务相关的表数据,重新插入
			clearTaskOtherInfo(taskId);
			//删除调度任务
			if (this.checkExists(taskId)) {
				this.stopJob(taskId);
				this.deleteJob(taskId);
				/*if(StringUtils.isNotBlank(tskInfo.getTriggerId())){
					
				}*/
			}
		}
		
		// 2 生成执行对象相关信息
		if( taskNodeVO.getTskExeobjRelVO()!=null){
			TskExeobjRelVO tskExeobjRelVO = taskNodeVO.getTskExeobjRelVO();
			if(!StringUtils.isEmpty(tskExeobjRelVO.getExeObjId())&&!StringUtils.isEmpty(tskExeobjRelVO.getExeObjType()))
			{
				RptTskExeobjRel rel = new RptTskExeobjRel();
				RptTskExeobjRelPK pk = new RptTskExeobjRelPK();
				pk.setExeObjId(tskExeobjRelVO.getExeObjId());
				pk.setExeObjType(tskExeobjRelVO.getExeObjType());
				pk.setTaskId(taskId);
				
				rel.setExeObjNm(tskExeobjRelVO.getExeObjNm());
				
				rel.setId(pk);
				
				taskDao.saveRptTskExeobjRel(rel);
			}
		}
		saveTaskNode( taskNodeVO,taskId);
		
		return taskId;
	}
	
	private void saveTaskNode(DeployTaskVO taskNodeVO,String taskId){
		String nodeType = taskNodeVO.getRptTskInfo().getNodeType();
		if(nodeType == null||taskNodeVO.getTaskUnifyNodeList()==null || taskNodeVO.getTaskUnifyNodeList().isEmpty())
			return ;
		if(nodeType.equals("01"))//统一设置
			saveUnifyTaskNode(taskNodeVO.getTaskUnifyNodeList(),taskNodeVO.getOrgs(),taskId);
		else if(nodeType.equals("02"))//自定义设置
			saveSeparateTaskNode(taskNodeVO.getTaskNodeOrgList(),taskId);
	}
	
	private void saveUnifyTaskNode( List<TaskUnifyNodeVO> taskUnifyNodeList,List<String>orgs,String taskId){
		//保存统一设置的机构信息
		for(String org:orgs){
			RptTskOrgRel rel = new RptTskOrgRel();
			RptTskOrgRelPK pk = new RptTskOrgRelPK();
			pk.setOrgNo(org);
			pk.setTaskId(taskId);
			rel.setId(pk);
			
			taskDao.saveRptTskOrgRel(rel);
		}
		
		for(TaskUnifyNodeVO taskUnifyNodeVO:taskUnifyNodeList){

			//保存统一设置的节点信息
			String taskNodeId = RandomUtils.uuid2();
			RptTskNode tskNode = new RptTskNode();
			tskNode.setTaskNodeId(taskNodeId);
			tskNode.setTaskId(taskId);
			tskNode.setTaskNodeDefId(taskUnifyNodeVO.getTaskNodeDefId());
			tskNode.setTaskNodeNm(taskUnifyNodeVO.getTaskNodeNm());
			tskNode.setTaskOrderno(new BigDecimal(taskUnifyNodeVO.getTaskOrderno()));
			tskNode.setHandleType(taskUnifyNodeVO.getHandleType());
			tskNode.setNodeType(taskUnifyNodeVO.getNodeType());
			
			tskNode.setOrgNo(taskUnifyNodeVO.getOrgNo());
//					tskNode.setIsCanInterrupt(taskNodeObjVO.getIsCanInterrupt());
			taskDao.saveRptTskNode(tskNode);
			
			if(taskUnifyNodeVO.getTaskObjIdMap()!=null){
				for(Map<String,String>  taskObjMap:taskUnifyNodeVO.getTaskObjIdMap() ){
					RptTskNodeTskobjRel rel = new RptTskNodeTskobjRel();
					RptTskNodeTskobjRelPK pk = new RptTskNodeTskobjRelPK();
					pk.setTaskId(taskId);
					pk.setTaskObjId(taskObjMap.get("taskObjId"));
					pk.setTaskNodeId(taskNodeId);
					rel.setId(pk);
					
					rel.setTaskObjNm(taskObjMap.get("taskObjNm"));
					rel.setTaskObjType(taskObjMap.get("taskObjType"));
					
					taskDao.saveRptTskNodeTskobjRel(rel);
				}
			}
		}
	}
	
	/**
	 * 统一设置流程节点信息
	 * @param nodeList
	 * @param taskId
	 */
	private void saveSeparateTaskNode(List<TaskNodeOrgVO> nodeList,String taskId){

		//遍历每个流程节点,更新对应的信息
		if (nodeList != null&&!nodeList.isEmpty()) {
			for (TaskNodeOrgVO taskNodeOrgVO : nodeList) {
				if(taskNodeOrgVO.getTaskNodeObjList()==null || taskNodeOrgVO.getTaskNodeObjList().isEmpty())
					continue;
				String orgNo = taskNodeOrgVO.getOrgNo();
				for(TaskNodeObjVO  taskNodeObjVO: taskNodeOrgVO.getTaskNodeObjList()  ){
						if(taskNodeObjVO.getTaskObjVOList()==null || taskNodeObjVO.getTaskObjVOList().isEmpty())
							continue;
						
						String taskNodeId = RandomUtils.uuid2();
		
						RptTskNode tskNode = new RptTskNode();
						tskNode.setTaskNodeId(taskNodeId);
						tskNode.setTaskId(taskId);
						tskNode.setTaskNodeDefId(taskNodeObjVO.getTaskNodeDefId());
						tskNode.setTaskNodeNm(taskNodeObjVO.getTaskNodeNm());
						tskNode.setTaskOrderno(new BigDecimal(taskNodeObjVO.getTaskOrderno()));
						tskNode.setOrgNo(orgNo);
		//				tskNode.setIsCanInterrupt(taskNodeObjVO.getIsCanInterrupt());
						taskDao.saveRptTskNode(tskNode);
						
						if(taskNodeObjVO.getTaskObjVOList()!=null  ){
							for(TaskObjVO taskObjVO:taskNodeObjVO.getTaskObjVOList() ){
								RptTskNodeTskobjRel rel = new RptTskNodeTskobjRel();
								RptTskNodeTskobjRelPK pk = new RptTskNodeTskobjRelPK();
								pk.setTaskId(taskId);
								pk.setTaskObjId(taskObjVO.getTaskObjId());
								pk.setTaskNodeId(taskNodeId);
								rel.setId(pk);
								
								rel.setTaskObjNm(taskObjVO.getTaskObjNm());
								rel.setTaskObjType(taskObjVO.getTaskObjType());
								
								taskDao.saveRptTskNodeTskobjRel(rel);
							}
						}
				}
			}
		}
	}
	/**
	 * 2015-9-17 指标补录任务假删,明细补录任务真删
	 * @param ids
	 * @return
	 */
	public String batchDeleteTasks(String ids){
		String[] idsArray = ids.split(",");
		List<String>listTaskList = Lists.newArrayList();
		List<String>idsTaskList = Lists.newArrayList();
		for(String idsTemp : idsArray){
			if(StringUtils.isEmpty(idsTemp))
				continue;
			String[] idTemp =  idsTemp.split("@");
			String idType = idTemp[1];
			String id = idTemp[0];
			if(idType.equals("01"))
				idsTaskList.add(id);
			else if(idType.equals("02"))
				listTaskList.add(id);
			
			//删除调度任务
			if (this.checkExists(id)) {
				this.stopJob(id);
				this.deleteJob(id);
			}
		}
		//真删明细补录
		if(!listTaskList.isEmpty()){
			deleteTasks(listTaskList);
		}
		//假删指标补录
		if(!idsTaskList.isEmpty()){
			logicDeleteTasks(idsTaskList);
		}
		return "1";
	}
	
	
	/**
	 * 级联物理删除任务（包括已经补录的数据）
	 * 2015-9-17 删除关联模板及任务相关信息
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean deleteTasksCascade(String ids) {
		List<String> taskIds = Arrays.asList(ids.split(","));
		StringBuilder stb = new StringBuilder(1000);
		Connection conn = null;
		PreparedStatement stmt = null;
		List<Exception> error = Lists.newArrayList();
		stb.append("select i.taskInstanceId as templeId, t.dsId, t.tableEnName from RptTskIns i, RptTskExeobjRel r, RptInputLstTempleInfo t where i.taskId = r.id.taskId and r.id.exeObjId = t.templeId and r.id.taskId in ?0");
		List<Object[]> temples = this.baseDAO.findWithIndexParam(stb.toString(), taskIds);
		for (Object[] info : temples) {
			String taskInsId = (String) info[0];
			String dsId = (String) info[1];
			String tableEnName = (String) info[2];
			stb.setLength(0);
			String schema = this.dataSourceBS.getSchemaByDsId(dsId);
			stb.append("delete from ").append(schema).append(".").append(tableEnName).append(" where SYS_DATA_CASE = ?");
			try {
				conn = this.dataSourceBS.getConnection(dsId);
				stmt = conn.prepareStatement(stb.toString());
				stmt.setString(1, taskInsId);
				stmt.execute();
			} catch (Exception e) {
				this.dataSourceBS.releaseConnection(null, stmt, conn);
				error.add(e);
				e.printStackTrace();
			}
		}
		if (error.size() == 0) {
			this.deleteTasks(taskIds);
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * 假删
	 * @param ids
	 * @return
	 */
	public boolean logicDeleteTasks(List<String> idList) {
		try {
			taskDao.batchLogicDelRptTskInfo(idList);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * 真删
	 * @param ids
	 * @return
	 */
	public boolean deleteTasks(List<String> taskIdList) {
		taskDao.batchDeleteRptTskNodeTskobjRel(taskIdList);
		taskDao.batchDeleteRptTskExeobjRel(taskIdList);
		taskDao.batchDeleteRptTskNode(taskIdList);
		taskDao.batchDeleteRptTskInfo(taskIdList);
		taskDao.batchDeleteRptTskIns(taskIdList);
		return true;
	}
	
	/**
	 * 清除任务相关的表数据
	 * @param taskId
	 */
	private void clearTaskOtherInfo(String taskId){
		taskDao.deleteRptTskNodeTskobjRelByTask(taskId);
		taskDao.deleteRptTskExeobjRelByTask(taskId);
		taskDao.deleteRptTskNodeByTask(taskId);
		taskDao.deleteRptTskOrgRelByTask(taskId);
	}
	
	
	/**
	 * 创建实例信息
	 * 触发器下发则不生成实例
	 * 
	 * @param tskInfo
	 * @param taskNodeVO
	 */
	private void createTaskInsInfo(DeployTaskVO taskNodeVO, List<String> orgNos,String loadDataMark) {
	    
		String triggerType = taskNodeVO.getRptTskInfo().getTriggerType();
		if (triggerType.equals("1")) {
			//deployTaskWithOrgs(orgNos, taskNodeVO, taskNodeVO.getDataDate(), BioneSecurityUtils.getCurrentUserInfo().getUserId(),loadDataMark);
			
		    //下发过滤
		    if("1".equals(loadDataMark)){
		        List<String> paramOrgs = filterDeployOrg(taskNodeVO.getRptTskInfo().getTaskId(),orgNos,loadDataMark,taskNodeVO.getDataDate());
		        if(paramOrgs != null && paramOrgs.size() > 0){
		            orgNos = paramOrgs;
		        }
		    }
		    
		    deploySingleTaskWithOrgs(orgNos, taskNodeVO, taskNodeVO.getDataDate(), BioneSecurityUtils.getCurrentUserInfo().getUserId(),loadDataMark);
		} else if(triggerType.equals("2")){
			String dataDate = DateUtils.getFormatTime(System.currentTimeMillis(), "yyyyMMdd");
			Integer dateOffsetAmount =  taskNodeVO.getRptTskInfo().getDateOffsetAmount();
			startNowJob(taskNodeVO.getRptTskInfo().getTaskId(), taskNodeVO.getRptTskInfo().getTriggerId(),dateOffsetAmount==null?0:dateOffsetAmount.intValue(),loadDataMark,dataDate);
		}//后置任务
		else if(triggerType.equals("3")){
		    /**
	         * chenhx 20171121
	         * 后置任务不做处理
	         * 等任务流程结束后处理
	         */
		}
	}
	
	private List<TaskUnifyNodeVO>mergetSameUnifyNode(List<TaskUnifyNodeVO>nodeList,String orgNo){
		
		Map<String,TaskUnifyNodeVO>orderMap=Maps.newHashMap();
		for(TaskUnifyNodeVO node :nodeList){
			if("AUTH_OBJ_ROLE".equals(node.getTaskObjType())){
				if(node.getOrgNo().indexOf(orgNo) != -1){
//					node.setOrgNo(orgNo);
					if(orderMap.containsKey(node.getTaskOrderno())){
						TaskUnifyNodeVO vo = orderMap.get(node.getTaskOrderno());
						List<Map<String, String>>orignMap = vo.getTaskObjIdMap();
						orignMap.addAll(node.getTaskObjIdMap());
						node.setTaskObjIdMap(orignMap);
						
					}
					
					orderMap.put(node.getTaskOrderno(), node);
				}
			}else{
				if(orgNo.equals(node.getOrgNo())){
					if(orderMap.containsKey(node.getTaskOrderno())){
						TaskUnifyNodeVO vo = orderMap.get(node.getTaskOrderno());
						List<Map<String, String>>orignMap = vo.getTaskObjIdMap();
						orignMap.addAll(node.getTaskObjIdMap());
						node.setTaskObjIdMap(orignMap);
					}
					orderMap.put(node.getTaskOrderno(), node);
				}
			}
		}
		return Arrays.asList(orderMap.values().toArray(new TaskUnifyNodeVO[orderMap.size()])) ;
		
	}
	
	public void deploySingleTaskWithOrgs(List<String>orgNos,DeployTaskVO taskNodeVO,String dataDate,String creator,String loadDataMark){
		List<RptTskIns> rptTskInsList = new ArrayList<RptTskIns>();
		List<RptTskNodeIns> rptTskNodeInsList = new ArrayList<RptTskNodeIns>();
		List<RptTskNodeTskinsRel> rptTskNodeTskinsRelList = new ArrayList<RptTskNodeTskinsRel>();
		List<TaskUnifyNodeVO> taskUnifyNodeList = taskNodeVO.getTaskUnifyNodeList();
		if(orgNos == null){
			return;
		}
		for(String orgNo : orgNos){
			// 生成实例信息
			BioneUser userObj;
			if(StringUtils.isNotEmpty(creator)){
				List<String>ids=Lists.newArrayList();
				ids.add(creator);
				List<BioneUserInfo>infos= userBS.getUserInofByIds(ids);
				 BioneUserInfo tmpUserObj = infos.get(0);
				 userObj = new BioneUser();
				 userObj.setUserId(creator);
				 userObj.setOrgNo(tmpUserObj.getOrgNo());
				 userObj.setLoginName(tmpUserObj.getUserNo());
			}else{
				userObj = BioneSecurityUtils.getCurrentUserInfo();
			}
			String taskNodeId = RandomUtils.uuid2();// 子实例的第一个节点ID
			RptTskInfo tskInfo = taskNodeVO.getRptTskInfo();
			String insId = RandomUtils.uuid2();
		
			RptTskIns rptTskIns = new RptTskIns();
			rptTskIns.setTaskInstanceId(insId);
			rptTskIns.setTaskId(tskInfo.getTaskId());
			rptTskIns.setTaskNm(tskInfo.getTaskNm());
			rptTskIns.setSts("1");// 0-未提交 1-已提交 2-填报完成
			rptTskIns.setTaskNodeInstanceId(taskNodeId);
			rptTskIns.setIsUpdate("0");// 0-未修改 1-已修改 初始化0
			rptTskIns.setExeObjId(taskNodeVO.getTskExeobjRelVO().getExeObjId());// 执行对象
			rptTskIns.setIsCheck("1");// 0-未校验 1-已校验
			rptTskIns.setTaskTitle(StringUtils.isEmpty(taskNodeVO.getTaskTitle())?tskInfo.getTaskNm():taskNodeVO.getTaskTitle());
		
			rptTskIns.setTaskType(tskInfo.getTaskType());
			rptTskIns.setUpTaskInstanceId(DEFAULT_ROOT);
			rptTskIns.setLogicDelNo("N");
			rptTskIns.setExeObjId(tskInfo.getExeObjId());
			rptTskIns.setOrgNo(orgNo);
			rptTskIns.setStartTime(new Timestamp(System.currentTimeMillis()));
			rptTskIns.setCreateUser(creator);
			if(dataDate!= null && dataDate.length() == 10){
				dataDate = dataDate.replace("-", "");
			}
			rptTskIns.setDataDate(dataDate);
			rptTskIns.setCreateOrg(userObj.getOrgNo());
			String loaddm = "2";//2不加载  1 加载 3 已经加载过了
			if(StringUtils.isNotEmpty(loadDataMark)&&(loadDataMark.equalsIgnoreCase("true")|| loadDataMark.equalsIgnoreCase("1"))){
				loaddm = "1";
			}
			rptTskIns.setLoadDataMark(loaddm);
			rptTskInsList.add(rptTskIns);
		
		
			String firstTaskNodeId = taskNodeId;
			boolean isFirst = false;
			List<TaskUnifyNodeVO> mergedTaskUnifyNodeList = mergetSameUnifyNode(taskUnifyNodeList,orgNo);
			for (TaskUnifyNodeVO nodeVO : mergedTaskUnifyNodeList) {
				isFirst = nodeVO.getTaskOrderno().equals("0");
				RptTskNodeIns nodeIns = new RptTskNodeIns();
				if (!isFirst) {
					taskNodeId = RandomUtils.uuid2();
					nodeIns.setStartTime(null);
					nodeIns.setSts("0");// 0 未初始化 1 正常 2 结束 3 中止 4驳回
				} else {
					taskNodeId = firstTaskNodeId;
					nodeIns.setStartTime(DateUtils.getFormatTime(new Date().getTime(), "yyyyMMdd"));
					nodeIns.setSts("1");// 0 未初始化 1 正常 2 结束 3 中止 4驳回
					isFirst = false;
				}
				nodeIns.setTaskNodeInstanceId(taskNodeId);
				nodeIns.setTaskNodeNm(nodeVO.getTaskNodeNm());
				nodeIns.setIsCanInterrupt("");
				nodeIns.setTaskInstanceId(insId);
				nodeIns.setTaskOrderno(nodeVO.getTaskOrderno());
				nodeIns.setNodeType(nodeVO.getNodeType());
				nodeIns.setTaskNodeDefId(nodeVO.getTaskNodeDefId());
				rptTskNodeInsList.add(nodeIns);
		
				String taskObjType = nodeVO.getTaskObjType();
				List<Map<String, String>> taskObjIds = nodeVO.getTaskObjIdMap();
				for (Map<String, String> taskObjMap : taskObjIds) {
					String taskObjId = taskObjMap.get("taskObjId");
					if(taskObjType == null){
						taskObjType = taskObjMap.get("taskObjType");
					}
					RptTskNodeTskinsRel rel = new RptTskNodeTskinsRel();
					RptTskNodeTskinsRelPK pk = new RptTskNodeTskinsRelPK();
					
					pk.setTaskNodeInstanceId(taskNodeId);
					pk.setTaskObjType(taskObjType);
					pk.setTaskObjId(taskObjId);
					
					rel.setOrgNo(orgNo);
					rel.setId(pk);
					rel.setTaskObjNm(nodeVO.getTaskNodeNm());
					rptTskNodeTskinsRelList.add(rel);
				}
			}
			
		}
		taskDao.saveRptTskIns(rptTskInsList);
		taskDao.saveRptTskNodeIns(rptTskNodeInsList);
		if(!CollectionUtils.isEmpty(rptTskNodeTskinsRelList)){
			taskDao.saveRptTskNodeTskinsRel(rptTskNodeTskinsRelList);
		}
	}

	/************************************************** 创建作业相关 结束 **************************************************/


	/**
	 * 创建目录
	 * 
	 * @param catalogNm
	 * @param remark
	 * @param upNo
	 */
	public void createTskCatalog(String catalogId, String catalogNm,
			String remark, String upNo) {

		RptTskCatalog catalog = new RptTskCatalog();
		boolean isUpd = true;
		if (catalogId == null || catalogId.equals("")) {
			catalogId = RandomUtils.uuid2();
			isUpd = false;
		}
		catalog.setCatalogId(catalogId);
		catalog.setCatalogNm(catalogNm);
		catalog.setRemark(remark);
		catalog.setUpNo(upNo);
		if (!isUpd)
			taskDao.saveRptTskCatalog(catalog);
		else
			taskDao.updateRptTskCatalog(catalog);

		/******添加日志记录******/
//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append(isUpd?"修改":"新增").append("了一个目录,目录名称为:");
//		buff.append(catalogNm);
//		saveLog("03", "补录任务管理 ", buff.toString(), user.getUserId(), user.getLoginName());
	}

	/**
	 * 查询目录节点信息
	 * 
	 * @param basePath
	 * @return
	 */
	public List<CommonTreeNode> getCatalogTreeNode(String basePath,
			String parentCatalogId, String catalogName) {
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("catalogName", catalogName);
		map.put("parentCatalogId", parentCatalogId);
        
        map.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
        
      //lcy 20191010 增加分级授权判断 防止普通用户无法查看补录任务
      	boolean isHierarchicalAuth = BioneSecurityUtils.checkIsHierarchicalAuth(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
      	if(isHierarchicalAuth) {
      		map.put("objDefNo", "AUTH_OBJ_ROLE");
      		map.put("resDefNo", "AUTH_RES_SUPPLEMENT");
      		List<String> objRole = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get("AUTH_OBJ_ROLE");
      		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
      			map.put("objIds", objRole);
      		}
      	}
        
        List<RptTskCatalog> catalogList = taskDao.getRptTskCatalog(map);
		for (RptTskCatalog catalog : catalogList) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(catalog.getCatalogId());
			treeNode.setText(catalog.getCatalogNm());
			treeNode.setIsParent(true);
			treeNode.setUpId(catalog.getUpNo());
			treeNode.setIsexpand(false);
			treeNode.getParams().put("nodeType", "catalog");
			treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
			nodeList.add(treeNode);
		}
		return nodeList;
	}

	/**
	 * 查询目录下的任务节点信息
	 * 
	 * @param basePath
	 * @param catalogId
	 * @return
	 */
	private List<CommonTreeNode> getTaskNode(String basePath, String catalogId, String taskName) {
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		if (catalogId == null)
			appendRootTreeNode(basePath, nodeList);
		else {
			// 添加子目录节点
			nodeList.addAll(getCatalogTreeNode(basePath, catalogId, null));
			// 添加子任务节点
			Map<String, Object> map = Maps.newHashMap();
			map.put("taskName", taskName);
			map.put("catalogId", catalogId);
			map.put("paramTypeNo", PARAM_TYPE_NO);
            map.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
          //lcy 20191010 增加分级授权判断 防止普通用户无法查看补录任务
    		boolean isHierarchicalAuth = BioneSecurityUtils.checkIsHierarchicalAuth(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
    		if(isHierarchicalAuth) {
    			map.put("objDefNo", "AUTH_OBJ_ROLE");
    			map.put("resDefNo", "AUTH_RES_SUPPLEMENT");
    			List<String> objRole = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get("AUTH_OBJ_ROLE");
    			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
    				map.put("objIds", objRole);
    			}
    		}

            //增加权限,只能当前用户及当前用户上级可以看到该任务
            BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			/*
			List<BioneOrgInfo>downOrgList=Lists.newArrayList();
			orgBS.findLowerOrgInfosByOrgId(userObj.getOrgNo(), downOrgList);
			List<String>orgList=Lists.newArrayList();
			for(BioneOrgInfo orgInfo:downOrgList){
				if(!orgInfo.getOrgNo().equals(userObj.getOrgNo()))
					orgList.add(orgInfo.getOrgNo());
			}
			map.put("orgList", orgList);
			*/
			map.put("currentUserId", userObj.getUserId());
			map.put("orgNo", "%,"+userObj.getOrgNo()+",%");
			map.put("userId", "%,"+userObj.getUserId()+",%");
			map.put("loginName", userObj.getLoginName());
			map.put("orderField","UPDATE_DATE");
			
			List<RptTskInfo> rptTskInfoList = taskDao.getTaskInfos(map);
			for (RptTskInfo info : rptTskInfoList) {
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(info.getTaskId());
				treeNode.setText(info.getTaskNm());
				treeNode.setIsParent(false);
				treeNode.getParams().put("nodeType", TASK_TYPE);
				// treeNode.setUpId(info.getCatalogId());
				treeNode.setIcon(basePath + "/images/classics/menuicons/grid.png");
				nodeList.add(treeNode);
			}
		}
		return nodeList;
	}
	
	private void appendRootTreeNode(String basePath,List<CommonTreeNode> nodeList ){

		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId("ROOT");
		treeNode.setText("根目录");
		treeNode.setIsParent(true);
		treeNode.setUpId(null);
		treeNode.setIsexpand(true);
		treeNode.getParams().put("nodeType", "catalog");
		treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
		nodeList.add(treeNode);
	
	}

	/**
	 * 根据条件查询监管任务信息
	 * 
	 * @param maps
	 * @return
	 */
	public Map<String, Object> getRptTskInfoList(Pager pager,
			String catalogId, String type, String taskName) {

		Map<String, Object> params = Maps.newHashMap();

		if (catalogId == null) {
			catalogId="ROOT";
		}
		params.put("catalogId", catalogId);
		String condition = pager.getCondition();

		if (condition == null) {
			if (catalogId != null)
				condition = "{\"op\":\"and\",\"rules\":[{\"op\":\"=\",\"field\":\"catalogId\",\"value\":\""
						+ catalogId + "\",\"type\":\"string\"}]}";
			pager.setCondition(condition);
		}
		if (StringUtils.isNotEmpty(condition) ) {
			JSONObject json = JSON.parseObject(pager.getCondition());
			JSONArray array = json.getJSONArray("rules");
			for (int i = 0; i < array.size(); i++) {
				JSONObject temp = array.getJSONObject(i);
				String filedName = (String) temp.get("field");
				if (filedName.equals("taskNm")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						params.put("taskNm", "%" + temp.get("value") + "%");
						continue;
					}
				}
				if (filedName.equals("catalogId")) {
					if(StringUtils.isNotEmpty(catalogId)){
						params.put("catalogId", catalogId);
						continue;
					}
				}
				if (filedName.equals("exeObjType")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						params.put("exeObjType", temp.get("value") );
						continue;
					}
				}
				if (filedName.equals("taskType")) {
					if(StringUtils.isNotEmpty((String)temp.get("value"))){
						params.put("taskType", temp.get("value") );
						continue;
					}
				}
			}
			json.put("rules", "[]");
			pager.setCondition(json.toString());
		}
		PageHelper.startPage(pager);
		
		params.put("paramTypeNo", PARAM_TYPE_NO);
		params.put("taskName", taskName);
		params.put("catalogId", catalogId);
		
		if("ROOT".equals(catalogId)){
			params.put("type", type);
		}
		params.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		//lcy 20191010 增加分级授权判断 防止普通用户无法查看补录任务
		boolean isHierarchicalAuth = BioneSecurityUtils.checkIsHierarchicalAuth(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if(isHierarchicalAuth) {
			params.put("objDefNo", "AUTH_OBJ_ROLE");
			params.put("resDefNo", "AUTH_RES_SUPPLEMENT");
			List<String> objRole = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get("AUTH_OBJ_ROLE");
			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				params.put("objIds", objRole);
			}
		}

		//增加权限,只能当前用户及当前用户上级可以看到该任务
		/*BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<BioneOrgInfo>downOrgList=Lists.newArrayList();
		orgBS.findLowerOrgInfosByOrgId(userObj.getOrgNo(), downOrgList);
		
		List<String>orgList=Lists.newArrayList();
		for(BioneOrgInfo orgInfo:downOrgList){
			if(!orgInfo.getOrgNo().equals(userObj.getOrgNo()))
				orgList.add(orgInfo.getOrgNo());
		}
		*/
		//修改权限,只有模板选定的机构可以操作任务
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		params.put("currentUserId", userObj.getUserId());
		params.put("orgNo", "%,"+userObj.getOrgNo()+",%");
		params.put("userId", "%,"+userObj.getUserId()+",%");
		params.put("loginName", userObj.getLoginName());
		
		
		PageMyBatis<RptTskInfo> page = (PageMyBatis<RptTskInfo>) taskDao.getTaskInfos(params);
		/**
		 * 增加查询触发器状态字段
		 */
		List<RptTskInfo> resTmpList = page.getResult();
		List<JSONObject> resList = Lists.newArrayList();
		for(RptTskInfo tmpTask : resTmpList){
			JSONObject tmpTaskJson = (JSONObject)JSON.toJSON(tmpTask);
			tmpTaskJson.put("updateDate", tmpTask.getUpdateDate().getTime());
			if("1".equals(tmpTask.getTriggerType())){
				tmpTaskJson.put("triggerType", "未启用");
			}else{
				try {
					JobDetail dtl =  scheduler.getScheduler().getJobDetail(
							new JobKey(tmpTask.getTaskId(), tmpTask.getTaskId()));
					if(dtl==null){
						tmpTaskJson.put("triggerType", "停用");
					}else{
						tmpTaskJson.put("triggerType", "启用中");
					}
				} catch (SchedulerException e) {
					tmpTaskJson.put("triggerType", "停用");
				}
			}
			resList.add(tmpTaskJson);
		}
		
		Map<String, Object> rsMap = Maps.newHashMap();
		rsMap.put("Rows", resList);
		rsMap.put("Total", page.getTotalCount());

		return rsMap;
	}

	public boolean testSameIndexCatalogNm(String upNo, String catalogName,
			String catalogId) {

		Map<String, Object> params = Maps.newHashMap();
		params.put("upNo", upNo);
		params.put("catalogName", catalogName);
		params.put("catalogId", catalogId);
		return taskDao.testSameIndexCatalogNm(params) ==0;

	}
	
	public boolean testSameFlowNm(String taskDefId, String flowNm){

		Map<String, String> params = Maps.newHashMap();
		if(StringUtils.isNotEmpty(taskDefId))
			params.put("taskDefId", taskDefId);
		params.put("flowNm", flowNm);
		return taskDao.getFlowCnt(params) ==0;
	}
	
	
	
	public List<String> getDulpDeployTask(String selectedOrgs,String dataDate,String taskId){
		JSONArray array = JSON.parseArray(selectedOrgs);
		List<String>list = Lists.newArrayList();
		for(int i = 0 ;i <array.size();i++){
			list.add(array.getString(i));
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("orgList", list);
		params.put("dataDate", dataDate);
		params.put("taskId", taskId);

		List<String> orgNameList =  taskDao.getDulpDeployTask(params);
		if(orgNameList==null || orgNameList.isEmpty())
			return null;
		
		return orgNameList;
	}
	

	public RptTskCatalog getRptTskCatalogByCatalogId(String catalogId) {
		return taskDao.getRptTskCatalogByCatalogId(catalogId);
	}

	/**
	 * 遍历目录及目录下的子节点,将所有节点下的任务数量加和,判断是否可删除 0 可删除 1 不可删除
	 * 
	 * @param catalogId
	 * @return
	 */
	public String canDeleteCatalog(String catalogId) {
		boolean canDel = true;
		canDel = countCatalogTaskContainChild(catalogId, canDel);
		return canDel ? "0" : "1";
	}

	/**
	 * 将所有子节点下面的任务数量加和
	 * 
	 * @param catalogId
	 */
	private boolean countCatalogTaskContainChild(String catalogId, boolean canDel) {
		if (!canDel)
			return canDel;
		Map<String, Object> params = Maps.newHashMap();
		params.put("catalogId", catalogId);
		Integer cnt = this.taskDao.getTaskInfosCount(params);
		int catalogCnt = cnt == null ? 0 : cnt.intValue();
		if (catalogCnt != 0) {
			canDel = false;
			return canDel;
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("parentCatalogId", catalogId);
		List<RptTskCatalog> logList = taskDao.getRptTskCatalog(map);
		for (RptTskCatalog log : logList) {
			canDel = countCatalogTaskContainChild(log.getCatalogId(), canDel);
			if (!canDel)
				return canDel;
		}
		return canDel;
	}

	/**
	 * 遍历目录,将目录删除
	 * 
	 * @param id
	 */
	public void cascadeDel(String catalogId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("catalogId", catalogId);
		// List<RptTskCatalog> catalogList = taskDao.getRptTskCatalog(map);
		// String catalogNm = catalogList.get(0).getCatalogNm();
		
		List<String> catalogIdList = Lists.newArrayList();
		gatherCatalogDelList(catalogIdList, catalogId);
		taskDao.deleteTskCatalog(catalogIdList);

//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]").append("删除了一个目录,目录名称为:")
//		.append(catalogNm);
//		saveLog("02", "补录任务管理", buff.toString(), user.getUserId(), user.getLoginName());
	}

	private void gatherCatalogDelList(List<String> catalogIdList,
			String catalogId) {

		catalogIdList.add(catalogId);
		Map<String, Object> map = Maps.newHashMap();
		map.put("parentCatalogId", catalogId);
		List<RptTskCatalog> catalogList = taskDao.getRptTskCatalog(map);
		for (RptTskCatalog catalog : catalogList) {
			gatherCatalogDelList(catalogIdList, catalog.getCatalogId());
		}

	}

	public boolean testRptDimItemNm(String taskNm, String catalogId,String taskId,String taskDefId) {
		if (catalogId == null)
			catalogId = DEFAULT_ROOT;

		Map<String, Object> params = Maps.newHashMap();
		params.put("taskNma", taskNm);
		params.put("catalogIdb", catalogId);
		params.put("taskIdc", taskId);
		if(!StringUtils.isEmpty(taskDefId)){
			params.put("taskDefIdd", taskDefId);
		}
		return this.taskDao.getTaskInfosCount(params).compareTo(0) == 0;

	}

	
	public List<CommonTreeNode>getRootRptTskInfoNode(String basePath){
		
		return getTaskNode(basePath,null, null);	
		
	}
	
	public List<CommonTreeNode>getRptTskInfoNode(String basePath,String searchNm,String nodeId){
		return  getTaskNode(basePath,nodeId, searchNm);	
	}
	
	public List<Map<String,Object>> getTaskTypeList(){
		return taskDao.getTaskType(PARAM_TYPE_NO);
	}
	
	/**
	 * 得到任务类型
	 * @return
	 */
	public List<CommonComboBoxNode> getTaskType(){
		
		List<Map<String,Object>>typeMap = taskDao.getTaskType(PARAM_TYPE_NO);
		if(typeMap==null || typeMap.isEmpty())
			return null;
		List<CommonComboBoxNode>nodeList=Lists.newArrayList();
		for(Map<String,Object> type:typeMap){
			CommonComboBoxNode boxNode = new CommonComboBoxNode();
			boxNode.setId((String)type.get("PARAM_VALUE"));
			boxNode.setText((String)type.get("PARAM_NAME"));
			nodeList.add(boxNode);
		}
		return nodeList;
	}
	
	
	/**
	 * 获取任务列表
	 */
	public List<CommonComboBoxNode> getTaskList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();

		List<RptTskFlow> list = taskDao.getTaskList();
			for (RptTskFlow flow:list) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(flow.getTaskDefId());
				node.setText(flow.getFlowNm());
				nodes.add(node);
			}
		return nodes;
	}
	
	public DeployTaskVO getBaseTaskById(String taskId){
		DeployTaskVO vo = new DeployTaskVO();
		// 设置任务基本信息
		RptTskInfo rptTskInfo = getSingleRptTskInfo(taskId,null);
		vo.setRptTskInfo(rptTskInfo);
		vo.setTskExeobjRelVO(getTskExeobjRelVO(taskId));
		
		/**
         * chenhx 20171121
         * 新增后置依赖任务
         */
		if(rptTskInfo != null && "1".equals(rptTskInfo.getAfterTaskSts())){
            vo.setAfterTask(getAfterTaskById(rptTskInfo.getAfterTaskObjId(),null));
        }
		
		return vo;
	}
	
	/**
	 * 查询与任务信息相关的信息
	 * @param taskId
	 * @return
	 */
	public DeployTaskVO getTaskById(String taskId,String userId) {

		DeployTaskVO vo = new DeployTaskVO();
		// 设置任务基本信息
		RptTskInfo rptTskInfo = getSingleRptTskInfo(taskId,userId);
		vo.setRptTskInfo(rptTskInfo);
		
		/**
         * chenhx 20171121
         * 新增后置依赖任务
         * 此处查询后置任务
         */
		if(rptTskInfo != null){
			if("1".equals(rptTskInfo.getAfterTaskSts())){
				vo.setAfterTask(getAfterTaskById(rptTskInfo.getAfterTaskObjId(),userId));
			}
		}

		List<String> selectedList = getTaskOrgs(taskId);
		if(!selectedList.isEmpty())
		{
			vo.setOrgs(selectedList);
			List<String> resultList = Lists.newArrayList();
			vo.setOpenOrgs(getOpenOrgs(resultList, selectedList));
		}
		vo.setTaskUnifyNodeList(getTaskUnifyNodeList(taskId));
		// 设置执行对象相关信息
		vo.setTskExeobjRelVO(getTskExeobjRelVO(taskId));
		
		return vo;
	}
	
	public DeployTaskVO getAfterTaskById(String taskId,String userId) {
	    
	    DeployTaskVO vo = new DeployTaskVO();
	    // 设置任务基本信息
	    RptTskInfo rptTskInfo = getSingleRptTskInfo(taskId,userId);
	    
	    StringBuilder sb = new StringBuilder();
	    if("mysql".equals(databaseType) || "postgresql".equals(databaseType)){
			sb.append(" SELECT a.task_title, a.load_data_mark ,a.data_date FROM rpt_tsk_ins a  ");
			sb.append(" where a.task_id = ?0  ");
			sb.append(" order by start_time desc limit 1");
 		} else {
			sb.append(" SELECT a.task_title, a.load_data_mark ,a.data_date,rownum FROM rpt_tsk_ins a  ");
			sb.append(" where a.task_id = ?0  ");
			sb.append(" and  rownum = 1 order by start_time desc  ");
		}
	    
	    List<Object[]> l = this.baseDAO.findByNativeSQLWithIndexParam(sb.toString(), taskId);
	    
	    if(l != null && l.size() > 0){
	    	vo.setTaskTitle(String.valueOf(l.get(0)[0]));
	    	vo.setLoadDataMark("2".endsWith(String.valueOf(l.get(0)[1]))? "false" : "true");
	    	vo.setDataDate(String.valueOf(l.get(0)[2]));
	    }
	    
	    vo.setRptTskInfo(rptTskInfo);
	    
	    List<String> selectedList = getTaskOrgs(taskId);
	    if(!selectedList.isEmpty())
	    {
	        vo.setOrgs(selectedList);
	        List<String> resultList = Lists.newArrayList();
	        vo.setOpenOrgs(getOpenOrgs(resultList, selectedList));
	        List<String> upOrg = getUpOrgs(selectedList,new HashSet<String>());
	        vo.setUpOrg(upOrg);
	    }
	    
	    
	    vo.setTaskUnifyNodeList(getTaskUnifyNodeList(taskId));
	    // 设置执行对象相关信息
	    vo.setTskExeobjRelVO(getTskExeobjRelVO(taskId));
	    
	    return vo;
	}
	
	
public DeployTaskVO getDeployTaskById(String taskId,String userId) {
	    
	    DeployTaskVO vo = new DeployTaskVO();
	    // 设置任务基本信息
	    RptTskInfo rptTskInfo = getSingleRptTskInfo(taskId,userId);
	    
	    StringBuilder sb = new StringBuilder();
	    if("mysql".equals(databaseType) || "postgresql".equals(databaseType)){
			sb.append(" SELECT a.task_title, a.load_data_mark ,a.data_date FROM rpt_tsk_ins a  ");
			sb.append(" where a.task_id = ?0  ");
			sb.append(" order by start_time desc limit 1");
		} else {
			sb.append(" SELECT a.task_title, a.load_data_mark ,a.data_date,rownum FROM rpt_tsk_ins a  ");
			sb.append(" where a.task_id = ?0  ");
			sb.append(" and  rownum = 1 order by start_time desc  ");
		}
	    
	    List<Object[]> l = this.baseDAO.findByNativeSQLWithIndexParam(sb.toString(), taskId);
	    
	    if(l != null && l.size() > 0){
	    	vo.setTaskTitle(String.valueOf(l.get(0)[0]));
	    	vo.setLoadDataMark("2".endsWith(String.valueOf(l.get(0)[1]))? "false" : "true");
	    	vo.setDataDate(String.valueOf(l.get(0)[2]));
	    }
	    
	    vo.setRptTskInfo(rptTskInfo);
	    
	    List<String> selectedList = getTaskOrgs(taskId);
	    if(!selectedList.isEmpty())
	    {
	        vo.setOrgs(selectedList);
	        List<String> resultList = Lists.newArrayList();
	        vo.setOpenOrgs(getOpenOrgs(resultList, selectedList));
	    }
	    List<String> upOrg = getUpOrgs(selectedList,new HashSet<String>());
	    vo.setUpOrg(upOrg);
	    
	    vo.setTaskUnifyNodeList(getTaskUnifyNodeList(taskId));
	    // 设置执行对象相关信息
	    vo.setTskExeobjRelVO(getTskExeobjRelVO(taskId));
	    
	    return vo;
	}

	public List<String> getUpOrgs(List<String> selectedList ,Set<String> orgs){
		orgs.addAll(selectedList);
		List<String>upOrgList = taskDao.getUpOrgNos(SplitStringBy1000.change(selectedList));
		Set<String> res = Sets.newHashSet();
		for(String uo : upOrgList){
			if(null == uo || "".equals(uo) || "0".equals(uo) || "ROOT".equals(uo)){
				continue;
			}
			res.add(uo);
		}
		List<String> l = new ArrayList<String>();
		if(res.size() > 0){
			l.addAll(res);
			return getUpOrgs(l,orgs);
		}
		l.addAll(orgs);
		return l;
	}
	
	
	
	public List<String> getOrgInfos(String taskId){
		return  getTaskOrgs(taskId);
	}
	
	private List<String>getOpenOrgs(List<String>resultList,List<String>orgList){
		List<String>upOrgList = taskDao . getUpOrgNos(SplitStringBy1000.change(orgList));
		if(upOrgList==null || upOrgList.isEmpty())
			return resultList;
		resultList.addAll(upOrgList);
		
		return getOpenOrgs(resultList,upOrgList);
		
	}
	
	public List<String>getTaskOrgs(String taskId){
		
		return taskDao.getTaskOrgs(taskId);
		
	}
	
	public List<TaskUnifyNodeVO> getTaskUnifyNodeList(String taskId){
		
		List<TaskUnifyNodeVO> unifyNodeList = Lists.newArrayList();
		List<Map<String,Object>>nodeMapList =  taskDao.getTaskNodeInfos(taskId);
		if(nodeMapList!=null){
			for(Map<String,Object> nodeMap:nodeMapList){
				TaskUnifyNodeVO taskUnifyNodeVO = new TaskUnifyNodeVO();
				taskUnifyNodeVO.setHandleType((String)nodeMap.get("HANDLE_TYPE"));
				taskUnifyNodeVO.setTaskNodeDefId((String)nodeMap.get("TASK_NODE_DEF_ID"));
				taskUnifyNodeVO.setTaskNodeNm((String)nodeMap.get("TASK_NODE_NM"));
				taskUnifyNodeVO.setTaskOrderno(((BigDecimal)nodeMap.get("TASK_ORDERNO")).toString());
				taskUnifyNodeVO.setNodeType((String)nodeMap.get("NODE_TYPE"));
				String orgNo = nodeMap.get("ORG_NO").toString();
				if(StringUtils.isNotEmpty(orgNo))
					taskUnifyNodeVO.setOrgNo(orgNo);
				
				List<TaskObjVO> taskObjList = getTaskObjOfNode(taskId,(String)nodeMap.get("TASK_NODE_ID"));
				boolean isSetType = false;
				List<Map<String,String>> taskObjIdMap = Lists.newArrayList();
				for(TaskObjVO vo: taskObjList){
					Map<String,String>p = Maps.newHashMap();
					if(!isSetType)
					{
						taskUnifyNodeVO.setTaskObjType(vo.getTaskObjType());
						isSetType = true;
					}
					p.put("taskObjId", vo.getTaskObjId());
					p.put("taskObjNm", vo.getTaskObjNm());
					p.put("taskObjType", vo.getTaskObjType());
					taskObjIdMap.add(p);
				}
				taskUnifyNodeVO.setTaskObjIdMap(taskObjIdMap);
				unifyNodeList.add(taskUnifyNodeVO);
			}
		}
		return unifyNodeList;
	}
	
	/**
	 * 查询执行对象相关信息
	 * @param taskId
	 * @return
	 */
	private TskExeobjRelVO getTskExeobjRelVO(String taskId){
		
		TskExeobjRelVO vo = new TskExeobjRelVO();
		
		Map<String,Object>relMap =  taskDao.getTskExeobjRelInfo(taskId);
		if(relMap==null) return null;
		vo.setExeObjId((String)relMap.get("EXE_OBJ_ID") );
		vo.setExeObjNm((String)relMap.get("EXE_OBJ_NM") );
		vo.setExeObjType((String)relMap.get("EXE_OBJ_TYPE") );
		
		return vo;
		
	}
	/**
	 * 查询任务相关节点信息
	 * @param taskId
	 * @return
	 */
	/*
	private List<TaskNodeOrgVO>getTaskNodeInfos(String taskId){

		List<Map<String,Object>>nodeMapList =  taskDao.getTaskNodeInfos(taskId);
		if(nodeMapList!=null){
			Map<String , TaskNodeOrgVO> taskNodeObjMap = Maps.newHashMap();
			for(Map<String,Object> nodeMap:nodeMapList){
				String orgNo = (String)nodeMap.get("ORG_NO");
				TaskNodeOrgVO taskNodeOrgVO;
				if(taskNodeObjMap.containsKey(orgNo)){
					taskNodeOrgVO = taskNodeObjMap.get(orgNo);
				}else{
					taskNodeOrgVO= new TaskNodeOrgVO();
					List<TaskNodeObjVO> taskNodeObjList = Lists.newArrayList();
					taskNodeOrgVO.setTaskNodeObjList(taskNodeObjList);
					taskNodeOrgVO.setOrgNo(orgNo);
					taskNodeOrgVO.setOrgNo((String)nodeMap.get("ORG_Nm"));
					
				}
				TaskNodeObjVO taskNodeObjVO = new TaskNodeObjVO();
				taskNodeObjVO.setIsCanInterrupt((String)nodeMap.get("IS_CAN_INTERRUPT"));
				taskNodeObjVO.setTaskNodeDefId((String)nodeMap.get("TASK_NODE_DEF_ID"));
				taskNodeObjVO.setTaskNodeNm((String)nodeMap.get("TASK_NODE_NM"));
				taskNodeObjVO.setTaskOrderno(((BigDecimal)nodeMap.get("TASK_ORDERNO")).intValue());
				taskNodeObjVO.setNodeType((String)nodeMap.get("NODE_TYPE"));
				String taskNodeId = (String)nodeMap.get("TASK_NODE_ID");
				
				taskNodeObjVO.setTaskObjVOList(getTaskObjOfNode(taskId,taskNodeId));
				taskNodeOrgVO.getTaskNodeObjList().add(taskNodeObjVO);
				taskNodeObjMap.put(orgNo, taskNodeOrgVO);
			}
			List<TaskNodeOrgVO> returnList = Lists.newArrayList(taskNodeObjMap.values());
			return returnList;
			
		}
		return null;
		
	}
	*/
	
	private List<TaskObjVO> getTaskObjOfNode(String taskId,String taskNodeId){
		Map<String ,Object >param = Maps.newHashMap();
		param.put("taskId", taskId);
		param.put("taskNodeId", taskNodeId);
		
		List<TaskObjVO> voList = Lists.newArrayList();
		
		List<Map<String,Object>>rptTskNodeTskobjRelList =  taskDao.getTaskObjOfNode(param);
		if(rptTskNodeTskobjRelList!=null){
			
			for(Map<String,Object> map:rptTskNodeTskobjRelList)
			{
				TaskObjVO vo = new TaskObjVO();
				vo.setTaskObjId((String)map.get("TASK_OBJ_ID"));
				vo.setTaskObjNm((String)map.get("TASK_OBJ_NM"));
				vo.setTaskObjType((String)map.get("TASK_OBJ_TYPE"));
				
				voList.add(vo);
			}
		}
		
		return voList;
		
	}
	

	/**
	 * 查询任务基本信息
	 * @param taskId
	 * @return
	 */
	public RptTskInfo getSingleRptTskInfo(String taskId,String userId) {

		Map<String, Object> map = Maps.newHashMap();
		map.put("taskId", taskId);
		map.put("paramTypeNo", PARAM_TYPE_NO);
		String loginName,orgNo;
		try{
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			userId = user.getUserId();
			loginName=user.getLoginName();
			orgNo=user.getOrgNo();
		}catch(Exception ex){
			List<String>users=Lists.newArrayList();
			users.add(userId);
			List<BioneUserInfo> tmpUsers = userBS.getUserInofByIds(users);
			if(tmpUsers!=null&&tmpUsers.size()>0){
				BioneUserInfo user = tmpUsers.get(0);
				userId = user.getUserId();
				loginName=user.getUserNo();
				orgNo=user.getOrgNo();
			}else{
				users.clear();
				users.add("admin");
				BioneUserInfo tmpUser = userBS.getUserByUserNo("admin");
				userId=tmpUser.getUserId();
				loginName=tmpUser.getUserName();
				orgNo=tmpUser.getOrgNo();
			}
		}
		map.put("currentUserId", userId);
		map.put("loginName", loginName);
		map.put("orgNo", "%,"+orgNo+",%");
		map.put("userId", "%,"+userId+",%");
		List<RptTskInfo> infoList = taskDao.getTaskInfos(map);

		return CollectionUtils.isEmpty(infoList) ? null : infoList.get(0);
	}
	
	
	

	/**
	 * 根据条件查询监管任务信息
	 * 
	 * @param maps
	 * @return
	 */
	public Map<String, Object> getTaskNodeList(String taskDefId) {


		Map<String, Object> rsMap = Maps.newHashMap();
		List<TaskNodeObjVO> voList = Lists.newArrayList();
		List<Map<String,Object>>nodeList = taskDao.getTaskNodeList(taskDefId);
		if(nodeList!=null){
			for(Map<String,Object> node : nodeList)
			{
				TaskNodeObjVO vo = new TaskNodeObjVO();
				vo.setTaskNodeDefId((String)node.get("TASK_NODE_DEF_ID"));
				vo.setIsCanInterrupt((String)node.get("IS_CAN_INTERRUPT"));
				vo.setTaskNodeNm((String)node.get("TASK_NODE_NM"));
				vo.setTaskOrderno(((BigDecimal)node.get("TASK_ORDERNO")).intValue());
				vo.setNodeType((String)node.get("NODE_TYPE"));
				
				voList.add(vo);
			}
		}
		rsMap.put("Rows", voList);

		return rsMap;
	}

	/**
	 * 根据条件查询监管任务信息
	 * 
	 * @param maps
	 * @return
	 */
	public List<CommonTreeNode> getListWorkFlow() {
		List<CommonTreeNode> voList = Lists.newArrayList();
		Map<String,Object>m=Maps.newHashMap();
		List<RptTskFlow>flowList = taskDao.getListWorkFlow(m);
		if(flowList!=null){
			for(RptTskFlow flow : flowList)
			{
				CommonTreeNode node = new CommonTreeNode();
				node.setId(flow.getTaskDefId());
				node.setText(flow.getFlowNm());
				voList.add(node);
			}
		}
		return voList;
	}

	public List<CommonTreeNode> getDeployDeptTreeNode(
			CommonTreeNode parentNode) {
		if(!StringUtils.isEmpty(parentNode.getId())){
			String test=parentNode.getId().replaceAll("AUTH_OBJ_ORG_", "");
			parentNode.setId(test);
		}
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		String logicNoOrg = userObj.getCurrentLogicSysNo();
		// 是否使用分级授权
		boolean isHierarchicalAuth = BioneSecurityUtils
				.checkIsHierarchicalAuth(logicNoOrg);
		String pId = parentNode.getId();
		if(StringUtils.isEmpty(pId))
			pId = "0";
		// 获取机构信息
		List<BioneOrgInfo> orgInfoList = this.authBS.findValidAuthOrgOfUser(
				logicNoOrg, isHierarchicalAuth,null,pId);
		if (orgInfoList == null) {
			return nodes;
		}
		List<String>isParent=authBS.checkIsParent(pId, isHierarchicalAuth, logicNoOrg);

		for (int i = 0; i < orgInfoList.size(); i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			CommonTreeNode node = new CommonTreeNode();
			BioneOrgInfo obj = orgInfoList.get(i);
			node.setId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getOrgNo());
			node.setText(obj.getOrgName());
			if (CommonTreeNode.ROOT_ID.equals(obj.getUpNo())) {
				node.setUpId(obj.getUpNo());
			} else {
				node.setUpId(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG + "_" + obj.getUpNo());
			}
			node.setData(obj);
			paramMap.put("id", obj.getOrgId());
			paramMap.put("realId", obj.getOrgNo());
			// 有这个属性的节点不允许点击
			paramMap.put("cantClick", "1");
			node.setParams(paramMap);
			node.setIcon( GlobalConstants4frame.APP_CONTEXT_PATH
			+ GlobalConstants4frame.LOGIC_DEPT_ICON);
			//node.setIcon(icon_org);
			if(isParent.contains(obj.getOrgNo())){
				node.setIsParent(true);
			}
			else{
				node.setIsParent(false);
			}
			nodes.add(node);
		}
		return nodes;
	}
	
	public RptTskExeobjRel getRptTskExeobjRelByInstanceId(String taskInstanceId){
		
		return taskDao.getRptTskExeobjRelByTaskInstanceId(taskInstanceId);
	}
	
	public Map<String,Object>getRptTskInsOrgInfo(String taskInstanceId){
		return taskDao.getRptTskInsOrgInfo(taskInstanceId);
	}
	
	public RptTskIns getRptTskInsById(String taskInstanceId){
		return taskDao.getRptTskInsById(taskInstanceId);
	}
	public static final String RPT_FRS_BUSI_BANK = "01"; // 行内
	public List<CommonTreeNode> getOrgTree(String orgs) {
		List<String> orgList = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		if(StringUtils.isNotEmpty(orgs)){
			String[] ids = orgs.split(",");
			 for(String id: ids){
				 if(StringUtils.isNotEmpty(id)){
					 orgList.add(id);
				 }
			 }
			 params.put("orgList", orgList);
		}
		List<CommonTreeNode> list = taskDao.findOrgNode(params);
		//漏洞修复 ：MyBatis文件-代码注入 爆发点 3、5、13、26、28
		String iconpath ="/images/classics/icons/organ.gif";
		for(CommonTreeNode node:list){
				node.setIcon(iconpath);
				node.setIsParent(false);
		}
		return list;
	}

	/**
	 * 
	 * @param path
	 * @param objDefNo
	 * @param handleType 01 上级行 02 自定义
	 * @param orgs
	 * @return
	 */
	public List<CommonTreeNode>getTypeTree(String path,String objDefNo, String  handleType,String orgs,String searchNm,String nodeType){
		if(StringUtils.isEmpty(orgs))
			return null;
		if(objDefNo.equals("AUTH_OBJ_ROLE")){
			CommonTreeNode commonTreeNode;
			List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
			List<BioneRoleInfoExt> bean = this.roleBS.getBioneRoleInfoExt();
			for (BioneRoleInfoExt boine : bean) {
				commonTreeNode = new CommonTreeNode();
				commonTreeNode.setId(boine.getCode());
				commonTreeNode.setText(boine.getCodeDesc());
				commonTreeNode.setUpId("0");
				commonTreeNode.setIcon(path+"/images/classics/icons/role.gif");
				nodes.add(commonTreeNode);
			}
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			List<BioneRoleInfo> roleList = null;

			roleList = this.authBS.findValidAuthRoleOfUser(userObj.getCurrentLogicSysNo());
			if (roleList == null) {
				return nodes;
			}
			for (int i = 0; i < roleList.size(); i++) {
				CommonTreeNode node = new CommonTreeNode();
				BioneRoleInfo role = roleList.get(i);
				node.setId(role.getRoleId());
				node.setText(role.getRoleName());
				node.setIcon(path+"/images/classics/icons/role.gif");
				node.setUpId(role.getRoleTypeJg());
				nodes.add(node);
			}
			return nodes;

//			Map<String ,Object>params = Maps.newHashMap();
//			if(handleType.equals("01")){
//				List<String> l =  getParentOrgs(orgs);
//				if(l.isEmpty()) return null;
//				params.put("orgList",l);
//			}else {
//				params.put("orgList", getOrgs(orgs));
//			}
//			params.put("logicSysNo", GlobalConstants4frame.FRS_LOGIC_SYSTEM);
//			if(StringUtils.isNotEmpty(searchNm)) {
//				params.put("searchNm", "%"+searchNm+"%");
//			}
//			List<Map<String, Object>> roleOrgList = taskDao.getTaskRoleTreeOfOrgs(params);
//			return getObjectTree(roleOrgList,"02",path);
		}
		if(objDefNo.equals("AUTH_OBJ_USER")){
			Map<String ,Object>params = Maps.newHashMap();
			if(handleType.equals("01")){
				List<String>l =  getParentOrgs(orgs);
				if(l.isEmpty()) {
					return null;
				}
				params.put("orgList",l);
			} else {
				params.put("orgList", getOrgs(orgs));
			}
			
			params.put("nodeType", nodeType);
			if(StringUtils.isNotEmpty(searchNm)&&!searchNm.equals("undefined")) {
				params.put("searchNm",  "%"+searchNm+"%");
			}
			List<Map<String, Object>> userOrgList = taskDao.getTaskUserTreeOfOrgs(params);
			return getObjectTree(userOrgList,"01",path);
		}
		return null;
	}

	private List<CommonTreeNode> getObjectTree(List<Map<String,Object>> list,String type, String path){
		if(list!=null &&!list.isEmpty()){
			List<CommonTreeNode>treeNodeList = Lists.newArrayList();
			List<String>orgList=Lists.newArrayList();
			//漏洞修复 ：MyBatis文件-代码注入 爆发点 3、5、13、26、28
			String orgIcon = path+"/images/classics/icons/organ.gif";
			String roleIcon = path+"/images/classics/icons/role.gif";
			String userIcon = path+"/images/classics/icons/user.gif";
			for(Map<String,Object> map : list){
				if(type.equals("02")){
					CommonTreeNode roleNode = new CommonTreeNode();
					roleNode.setId((String)map.get("ROLE_ID"));
					roleNode.setIcon(roleIcon);
					roleNode.setText((String)map.get("ROLE_NAME"));
					roleNode.setUpId("0");
					treeNodeList.add(roleNode);
				}else{
					String orgNo = (String)map.get("ORG_NO");
					if(!orgList.contains(orgNo)){
						CommonTreeNode orgNode = new CommonTreeNode();
						orgNode.setId(orgNo);
						orgNode.setIsParent(true);
						orgNode.setIsexpand(true);
						orgNode.setText((String)map.get("ORG_NAME"));
						orgNode.setIcon(orgIcon);
						orgNode.setUpId("0");
						treeNodeList.add(orgNode);
						orgList.add(orgNo);
					}
					CommonTreeNode userNode = new CommonTreeNode();
					userNode.setId((String)map.get("USER_ID"));
					userNode.setIsParent(false);
					userNode.setIcon(userIcon);
					userNode.setText((String)map.get("USER_NAME")+"("+(String)map.get("USER_NO")+")");
					userNode.setUpId(orgNo);
					treeNodeList.add(userNode);
				}
			}
			return treeNodeList;
		}
		return null;
	
	}
	private List<String>getOrgs(String orgNo){
		String[] orgArray = orgNo.split(",");
		List<String>orgList = Lists.newArrayList();
		for(String org : orgArray)
		{
			if(StringUtils.isNotEmpty(org))
				orgList.add(org);
		}
		
		return orgList;
	}
	
	private List<String>getParentOrgs(String orgs){
		String[] orgArray = orgs.split(",");
		List<String>resultList = Lists.newArrayList();
		for(String org:orgArray){
			if(StringUtils.isNotEmpty(org))
				resultList.add(org);
		}
		if(resultList.isEmpty())
			return null;
		return getParentOrgIds(resultList,false);
	}
	
	public List<String>getParentOrgIds(List<String> orgList,boolean toRoot){

		List<String>resultList = Lists.newArrayList();
		return this.getOpenOrgs(resultList, orgList,toRoot);
	}
	
	private List<String>getOpenOrgs(List<String>resultList,List<String>orgList,boolean toRoot){
		List<String>upOrgList = taskDao.getUpOrgNos(SplitStringBy1000.change(orgList));
		if(upOrgList==null || upOrgList.isEmpty())
			return resultList;
		resultList.addAll(upOrgList);
		if(!toRoot)
			return resultList;
		return getOpenOrgs(resultList,upOrgList,toRoot);
	}
	public List<String>getParentOrgIds(List<String> orgList){

		List<String>resultList = Lists.newArrayList();
		return this.getOpenOrgs(resultList, orgList);
		// get(resultList,orgList);
	}
	
	public String saveFlow(String taskDefId,String flowNm){
		// String saveMark = "01";
		try{
			Map<String,String>params=Maps.newHashMap();
			params.put("flowNm", flowNm);
			if(StringUtils.isEmpty(taskDefId))
			{
				//校验名称不能重复
				Integer count = taskDao.getFlowCnt(params);
				if(count>0) {
					return "2";
				}
				taskDefId = RandomUtils.uuid2();
				params.put("taskDefId", taskDefId);
				taskDao.saveFlow(params);
			}else
			{
				// saveMark = "03";
				params.put("taskDefId", taskDefId);
				taskDao.updateFlow(params);
			}
		}catch(Exception ex){
			return "保存目录失败:"+ex;
		}
		return "1";
	}
	
	public Map<String,List<String>> getValidateOrgInfo(){
		Map<String,List<String>> validateMap = new HashMap<String, List<String>>();
		List<String> orgNos = new ArrayList<String>();
		List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		for(String org : authOrgNos){
			orgNos.add(org);
			orgBs.getAllChildOrgNos(org, orgNos, true);
		}
		validateMap.put("validate", orgNos);
        return validateMap;
   }
	
	public List<CommonTreeNode> getAuthOrgTree(String upNo,String searchName,String orgType) {
        Map<String, Object> params = Maps.newHashMap();
        if(!StringUtils.isEmpty(upNo)){
                params.put("upOrgNo", upNo);
        }else{
            if(StringUtils.isNotBlank(searchName)){
            	//2020 lcy 【后台管理】sql注入 代码优化
				if(SqlValidateUtils.validateStr(searchName)) {
					searchName = SqlValidateUtils.replaceValue(searchName);
				}
                params.put("orgNmLike",  "%"+searchName+"%");
                if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
                    Map<String,List<String>> validateMap = new HashMap<String, List<String>>();
                    validateMap = this.getValidateOrgInfo();
                    List<String> orgNos=validateMap.get("validate");
                    params.put("orgNos",  ReBuildParam.splitLists(orgNos));
                }
            }
            else{
                if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
                    List<String> vorgNos= new ArrayList<String>();
                    vorgNos.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
                    params.put("orgNos", ReBuildParam.splitLists(vorgNos));
                }
                else{
                    params.put("upOrgNo", "0");
                }
            }
        }
        if(StringUtils.isNotBlank(orgType)){
            params.put("orgType", orgType);
        }else{
            params.put("orgType", GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
        }
		params.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
        List<CommonTreeNode> list = taskDao.findPubOrgNode(params);
        //漏洞修复 ：MyBatis文件-代码注入 爆发点 3、5、13、26、28
        String iconpath = GlobalConstants4frame.APP_CONTEXT_PATH + "/" + GlobalConstants4frame.ICON_URL + "/mnuTree.gif";
        for(CommonTreeNode node:list){
        	node.setIcon(iconpath);
            if(StringUtils.isNotBlank(searchName)){
                node.setText(node.getText()+"("+node.getId()+")");
                node.setIsParent(false);
            }
            else{
                node.setText(node.getText()+"("+node.getId()+")");
                node.setIsParent(true);
                node.setChildren(new ArrayList<CommonTreeNode>());
            }
        }
        return list;
        
    }
	
	public Map<String,String> getMsgTmp() {
		Map<String,String> res = Maps.newHashMap();
		String mailTip = "select v.var_value from bione_Sys_Var_Info v where v.var_No = 'MAIL_TIP'";
		String msgTip = "select v.var_value from bione_Sys_Var_Info v where v.var_No = 'MSG_TIP'";
		
		List<Object[]> mail = this.baseDAO.findByNativeSQLWithIndexParam(mailTip);
		List<Object[]> msg = this.baseDAO.findByNativeSQLWithIndexParam(msgTip);
		if(mail != null && mail.size()>0){
			res.put("mailTip", String.valueOf(msg.get(0)));
		}
		if(msg != null && msg.size()>0){
			res.put("msgTip", String.valueOf(msg.get(0)));
		}
		
		return res;
	}
	
	public List<Object[]> getSendUsers(){

		StringBuilder sql = new StringBuilder();
		sql.append(" select distinct user_id, ");
		sql.append(" email, ");
		sql.append(" mobile, ");
		sql.append(" user_name, ");
		sql.append(" msgTip, ");
		sql.append(" emlTip, ");
		sql.append(" sum(cnt) as cnt, ");
		sql.append(" node_type ");
		sql.append(" from (select distinct ui.user_id, ");
		sql.append(" ui.email, ");
		sql.append(" ui.mobile, ");
		sql.append(" ui.user_name, ");
		sql.append(" msg.attr_value    as msgTip, ");
		sql.append(" eml.attr_value    as emlTip, ");
		sql.append(" tskinsr.cnt, ");
		sql.append(" tskinsr.node_type ");
		sql.append(" from bione_user_info ui ");
		sql.append(" LEFT JOIN (SELECT val.user_id, val.attr_value, attr.field_name ");
		sql.append(" FROM bione_user_attr_val val ");
		sql.append(" inner join bione_user_attr attr ");
		sql.append(" on val.attr_id = attr.attr_id ");
		sql.append(" and attr.field_name = 'msgTip') msg ");
		sql.append(" on ui.user_id = msg.user_id ");
		sql.append(" LEFT JOIN (SELECT val.user_id, val.attr_value, attr.field_name ");
		sql.append(" FROM bione_user_attr_val val ");
		sql.append(" inner join bione_user_attr attr ");
		sql.append(" on val.attr_id = attr.attr_id ");
		sql.append(" and attr.field_name = 'emailTip') eml ");
		sql.append(" on ui.user_id = eml.user_id ");
		sql.append(" inner join (SELECT distinct tr.task_obj_id, ");
		sql.append(" count(tr.task_node_instance_id) as cnt, ");
		sql.append(" node_type ");
		sql.append("  FROM rpt_tsk_node_tskins_rel tr ");
		sql.append("  inner join (SELECT ni.task_node_instance_id, ");
		sql.append(" decode(ni.node_type, '01', '01', '02') as node_type ");
		sql.append(" FROM rpt_tsk_node_ins ni ");
		sql.append("  where ni.sts = '1') ntnii2 ");
		sql.append(" on tr.task_node_instance_id = ");
		sql.append(" ntnii2.task_node_instance_id ");
		sql.append("  where 1 = 1 ");
		sql.append(" and tr.task_obj_type = 'AUTH_OBJ_USER' ");
		sql.append(" group by task_obj_id, node_type) tskinsr ");
		sql.append("  on ui.user_id = tskinsr.task_obj_id ");
		sql.append("  where 1 = 1 ");
		sql.append(" union all ");
		sql.append(" select distinct ru.user_id, ");
		sql.append(" ru.email, ");
		sql.append(" ru.mobile, ");
		sql.append(" ru.user_name, ");
		sql.append(" msg.attr_value      as msgTip, ");
		sql.append(" eml.attr_value      as emlTip, ");
		sql.append(" tskinsrel.cnt, ");
		sql.append(" tskinsrel.node_type ");
		sql.append("   from bione_auth_obj_user_rel rur ");
		sql.append("  inner join ( ");
		
		sql.append("  SELECT  usri.USER_ID, usri.EMAIL, usri.MOBILE, usri.USER_NAME, useorg.org_no ");
		sql.append("  FROM BIONE_AUTH_OBJ_USER_REL usrel ");
		sql.append("  LEFT JOIN BIONE_USER_INFO usri ");
		sql.append("  ON usri.user_id = usrel.user_id ");
		sql.append("  LEFT JOIN bione_org_info useorg ");
		sql.append("  ON usrel.obj_id = useorg.org_id ");
		sql.append("  WHERE usrel.obj_def_no = 'AUTH_OBJ_ORG' ");
		
		sql.append("  ) ru on ru.user_id = rur.user_id ");
		sql.append("   LEFT JOIN (SELECT val.user_id, val.attr_value, attr.field_name ");
		sql.append("   FROM bione_user_attr_val val ");
		sql.append("  inner join bione_user_attr attr ");
		sql.append("     on val.attr_id = attr.attr_id ");
		sql.append("    and attr.field_name = 'msgTip') msg ");
		sql.append("   on ru.user_id = msg.user_id ");
		sql.append(" LEFT JOIN (SELECT val.user_id, val.attr_value, attr.field_name ");
		sql.append("   FROM bione_user_attr_val val ");
		sql.append("  inner join bione_user_attr attr ");
		sql.append("     on val.attr_id = attr.attr_id ");
		sql.append("    and attr.field_name = 'emailTip') eml ");
		sql.append("     on ru.user_id = eml.user_id ");
		sql.append("  inner join (SELECT distinct tr.task_obj_id, ");
		sql.append("  count(tr.task_node_instance_id) as cnt, ");
		sql.append("  nitnii.node_type ,tr.org_no ");
		sql.append("   FROM rpt_tsk_node_tskins_rel tr ");
		sql.append("  inner join (SELECT ni.task_node_instance_id, ");
		sql.append(" decode(ni.node_type, '01', '01', '02') as node_type ");
		sql.append("  FROM rpt_tsk_node_ins ni ");
		sql.append(" where ni.sts = '1') nitnii ");
		sql.append(" on tr.task_node_instance_id = ");
		sql.append("    nitnii.task_node_instance_id ");
		sql.append(" where 1 = 1 ");
		sql.append("   and tr.task_obj_type = 'AUTH_OBJ_ROLE' ");
		sql.append(" group by tr.task_obj_id, node_type,tr.org_no ) tskinsrel ");
		sql.append("     on rur.obj_id = tskinsrel.task_obj_id ");
		sql.append("     AND TSKINSREL.org_no = ru.org_no ");
		sql.append("  where 1 = 1) a ");
		sql.append(" group by user_id, email, mobile, user_name, msgTip, emlTip,node_type ");
			
		return this.baseDAO.findByNativeSQLWithIndexParam(sql.toString());
	}
	
	public void banchIns(String[] sql){
		JdbcTemplate jdbc = this.jdbcBaseDAO.getJdbcTemplate();
		jdbc.batchUpdate(sql);
	}
	
	public Map<String,List<String>> getTskInsNode(String insId){
		Map<String,List<String>> resMap = Maps.newHashMap();
		List<Map<String,String>> tskInsRel = taskDao.getTskInsNode(insId);
		Set<String> insOrg = Sets.newHashSet();
		for(Map<String,String> tm : tskInsRel){
			insOrg.add(tm.get("ORG_NO"));
			List<String> tl = null;
			if(resMap.containsKey(tm.get("NODE_TYPE"))){
				tl = resMap.get(tm.get("NODE_TYPE"));
				tl.add(tm.get("TASK_OBJ_ID"));
			}else{
				tl = Lists.newArrayList();
				tl.add(tm.get("TASK_OBJ_ID"));
				resMap.put(tm.get("NODE_TYPE"), tl);
			}
		}
		List<String> l = Lists.newArrayList();
		l.addAll(insOrg);
		resMap.put("insOrg",l);
		return resMap;
	}
	
	
	@Transactional( readOnly = false)
	public void delTskInsNode(String insId,String nodes){
		StringBuilder sb = new StringBuilder();
		
		JSONObject jsonObj = JSON.parseObject(nodes);
		Set<String> keys = jsonObj.keySet();
		for(String key : keys){
			JSONArray insNodes = jsonObj.getJSONArray(key);
			for(int i = 0; i < insNodes.size(); i++){
				JSONObject defNode = insNodes.getJSONObject(i);
				sb.append(" DELETE FROM RPT_TSK_NODE_TSKINS_REL RTNTR ");
				sb.append(" WHERE RTNTR.TASK_OBJ_ID = '");
				sb.append(defNode.getString("taskObjId"));
				sb.append("' AND RTNTR.TASK_OBJ_TYPE = '");
				sb.append(defNode.getString("taskObjType"));
				sb.append("' AND RTNTR.TASK_NODE_INSTANCE_ID IN(  ");
				sb.append(" SELECT TASK_NODE_INSTANCE_ID FROM RPT_TSK_NODE_INS RTNI  ");
				sb.append(" WHERE RTNI.TASK_INSTANCE_ID = '");
				sb.append(insId);
				sb.append("' AND NODE_TYPE = '");
				sb.append(key);
				sb.append("' ) ");
				
				this.baseDAO.createNativeQueryWithIndexParam(sb.toString()).executeUpdate();
				sb.setLength(0);
			}
		}
	}
	/**
	 * 根据任务查询下发的任务
	 */
	public boolean checkTaskinsByTaskid(String ids) {
		
		String[] idsArray = ids.split(",");
		StringBuilder idsSql = new StringBuilder(200);
		for (int i = 0; i < idsArray.length; i++) {
			if(StringUtils.isEmpty(idsArray[i]))
				continue;
			String[] idTemp =  idsArray[i].split("@");
			String id = idTemp[0];
			if(i == 0) {
				idsSql.append("'").append(id).append("'");
			}else {
				idsSql.append(",").append("'").append(id).append("'");
			}
		}
		//删除的所有任务都是停用状态不检查是否下发任务 20190814
		StringBuilder tasksql = new StringBuilder(200);
		//停用
		tasksql.append("select task_id from RPT_TSK_INFO where task_sts='0' and task_id in (");
		tasksql.append(idsSql);
		tasksql.append(")");
		List<Object[]> taskResule = this.baseDAO.findByNativeSQLWithNameParam(tasksql.toString(),null);
		//所有任务都停用返回true，删除所有任务
		if(taskResule.size()==idsArray.length) {
			return true;
		}
		
		//删除前校验是否已经下发过任务 20190605
		StringBuilder sql = new StringBuilder(200);
		sql.append("select task_instance_id from RPT_TSK_INS where task_id in (");
		sql.append(idsSql);
		sql.append(")");
		List<Object[]> resule = this.baseDAO.findByNativeSQLWithNameParam(sql.toString(),null);
		return resule.size()==0;
	}
	
	/**
	 * 根据流程是否被引用
	 */
	public boolean checkTaskByFlowid(String flowId) {
		StringBuilder sql = new StringBuilder(200);
		sql.append("select task_id taskId from RPT_TSK_INFO where task_def_id = '")
			.append(flowId)
			.append("'");
		List<Object[]> resule = this.baseDAO.findByNativeSQLWithNameParam(sql.toString(),null);
		return resule.size()==0;
	}
	
	/**
	 * 检查一个job是否存在
	 * 
	 * @param jobId
	 *            任务名Id
	 * @return result 是否存在
	 * @throws RuntimeException
	 */
	public boolean checkExists(String jobId) {
		boolean result = false;
		try {
			result = scheduler.getScheduler().checkExists(
					new JobKey(jobId, jobId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}
	
	/**
	 * 停止一个正在执行的job
	 * 
	 * @param jobId
	 *            任务名Id
	 * @throws RuntimeException
	 */
	@Transactional(readOnly = false)
	public void stopJob(String jobId) {
		try {
			scheduler.getScheduler().interrupt(new JobKey(jobId, jobId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 删除job
	 * @param taskId
	 */
	public void deleteJob(String taskId) {
		try {
			scheduler.getScheduler().deleteJob(new JobKey(taskId, taskId));
		} catch (SchedulerException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * @param deployTaskVO
	 * @return
	 */
	public Map<String, String> getDeployAuthObjInfo(DeployTaskVO deployTaskVO) {
		Map<String, String> msg = new HashMap<String, String>();
		String taskId = deployTaskVO.getRptTskInfo().getTaskId();
		String dataDate = deployTaskVO.getDataDate();
		List<TaskUnifyNodeVO> taskUnifyNodeList = deployTaskVO.getTaskUnifyNodeList();
		List<Map<String, String>> taskObjIdMapList = new ArrayList<Map<String, String>>();
		for(TaskUnifyNodeVO vo : taskUnifyNodeList){
			if("01".equals(vo.getNodeType())){
				taskObjIdMapList.addAll(vo.getTaskObjIdMap());
			}
		}
		List<String> taskObjList = new ArrayList<String>();
		for(Map<String, String> taskObjIdMap : taskObjIdMapList){
			taskObjList.add(taskObjIdMap.get("taskObjId"));
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("taskId", taskId);
		if(StringUtils.isNotBlank(dataDate)){
			dataDate = dataDate.replaceAll("-", "");
			params.put("dataDate", dataDate);
		}
		
		params.put("taskObjList", taskObjList);
		params.put("orgNos", deployTaskVO.getOrgs());
		//查询当前任务是否已经已经下发给这些用户或角色
		List<Map<String, Object>> result = this.taskOperDao.getDeployAuthObjInfo(params);
		if((null != result) && result.size() > 0) {
			List<String> taskObjIds = new ArrayList<String>();
			for(Map<String, Object> map : result){
				taskObjIds.add(map.get("TASK_OBJ_ID").toString());
			}
			//查询以下发的用户或角色名称
			List<Map<String, String>> names = this.taskOperDao.getTaskObjNames(taskObjIds);
			StringBuilder deployMsg = new StringBuilder();
			if(names != null && names.size() > 0){
				for(Map<String, String> map : names){
					deployMsg.append("["+map.get("name")+"],");
				}
				deployMsg.append("在当前数据日期["+dataDate+"]下已是填报用户/角色,请重新选择！");
			}
			msg.put("msg", deployMsg.toString());
		}
		return msg;
	}
	
	/**
	 * 通过偏移量，获取数据日期，格式为：20140905
	 * 
	 * @return
	 */
	public String getCurDataDateStrByStemp(int offset) {
		long timeOfOneDay = 24 * 60 * 60 * 1000;
		long tiemOfCurDay = new Date().getTime();
		Timestamp DataDateTemp = new Timestamp(tiemOfCurDay
				+ (timeOfOneDay * offset));
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		return ft.format(DataDateTemp);
	}

	/**
	 *  获取本月最后一天的日期
	 * @return 输出yyyyMMdd
	 */
	public String getLastDate() {
		//设置时间格式
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		//获得实体类
		Calendar ca = Calendar.getInstance();
		//设置最后一天
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		//最后一天格式化
		return format.format(ca.getTime());
	}

	public Map<String,Object> getTaskDeptList(String taskId){
		List<TaskDeptUserList> list = taskDao.getTaskDeptList(taskId);
		Map<String,Object> res = new HashMap<String, Object>();
		res.put("Rows", list);
		res.put("Total", list.size());
		return res;
	}

	/**
	 * @方法描述: 获取同步行内机构树（辖内机构+授权机构）
	 * @创建人: huzq1 
	 * @创建时间: 2021/11/23 11:15
	  * @param searchName
	 * @return
	 **/
	public List<CommonTreeNode> getSyncOrgTree(String searchName) {
		Map<String, Object> params = Maps.newHashMap();
		if(StringUtils.isNotBlank(searchName)){
			//2020 lcy 【后台管理】sql注入 代码优化
			if(SqlValidateUtils.validateStr(searchName)) {
				searchName = SqlValidateUtils.replaceValue(searchName);
			}
			params.put("orgNmLike",  "%"+searchName+"%");
		}
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> curOrgNo = Lists.newArrayList();
			List<String> orgNos = Lists.newArrayList();
			// 获取用户授权机构
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			curOrgNo.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			orgNos.addAll(authOrgNos);
			// 获取辖内机构
			userBS.getAllChildBioneOrgNo(curOrgNo, orgNos);
			List<String> collect = orgNos.stream().distinct().collect(Collectors.toList());
			params.put("orgNos", ReBuildParam.splitLists(collect));
		}
		params.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		List<CommonTreeNode> list = taskDao.findPubOrgNode(params);
		//漏洞修复 ：MyBatis文件-代码注入 爆发点 3、5、13、26、28
		String iconpath = GlobalConstants4frame.APP_CONTEXT_PATH + "/" + GlobalConstants4frame.ICON_URL + "/mnuTree.gif";
		for(CommonTreeNode node:list){
			node.setIcon(iconpath);
			if(StringUtils.isNotBlank(searchName)){
				node.setText(node.getText()+"("+node.getId()+")");
				node.setIsParent(false);
			} else {
				node.setText(node.getText()+"("+node.getId()+")");
				node.setIsParent(true);
				node.setChildren(new ArrayList<CommonTreeNode>());
			}
		}
		return list;
	}

}
