package com.yusys.biapp.input.task.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean.MethodInvokingJob;

import com.yusys.biapp.input.task.service.DeployTaskBS;
import com.yusys.bione.comp.utils.SpringContextHolder;



/**
 * <pre>
 * Title:定时执行任务下发的接口类
 * Description:
 * </pre>
 * 
 * @author liangyaohan
 * @version 1.00.00
 * 
 * <pre>
 * 修改记录
 *    修改后版本:	修改人：		修改日期:	修改内容:
 * </pre>
 */
public class DeployTaskJob extends MethodInvokingJob {
	protected static Logger log = LoggerFactory.getLogger(DeployTaskJob.class);
			

	@Override	
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		Long beginTime = System.currentTimeMillis();
		log.info("----------------- 触发器下发-补录任务，开始执行 ----------------");
		try{
			
			DeployTaskBS deployTaskOperBS = SpringContextHolder.getBean("deployTaskBS");
			String taskId = context.getJobDetail().getKey().getName();
			String userId = (String) context.getJobDetail().getJobDataMap().get("userId");
			String loadDataMark = (String) context.getJobDetail().getJobDataMap().get("loadDataMark");
			Integer dateOffsetAmount = (Integer) context.getJobDetail().getJobDataMap().get("dateOffsetAmount");
			// 增加月末判断标记‘9999’，当传入9999时，自动设置本月最后一天下发，2021/11/3update
			String dataDate = "";
			if (dateOffsetAmount == 9999) {
				dataDate = deployTaskOperBS.getLastDate();
			} else {
				dataDate = deployTaskOperBS.getCurDataDateStrByStemp(dateOffsetAmount);
			}

			deployTaskOperBS.beginTriggerTask(taskId,userId,loadDataMark,dataDate);
			log.info("----------------- 触发器下发-补录任务，执行结束，共花费"+ (System.currentTimeMillis() - beginTime)/1000+"秒 ----------------");
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
}

