package com.yusys.bione.frame.schedule.web;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.schedule.entity.BioneTaskInfo;
import com.yusys.bione.frame.schedule.entity.BioneTriggerInfo;
import com.yusys.bione.frame.schedule.service.TaskBS;
import com.yusys.bione.frame.schedule.service.TaskListBS;
import com.yusys.bione.frame.schedule.service.TriggerBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Title:任务信息处理Action类
 * Description: 系统任务信息表的增删改查
 * </pre>
 * 
 * @author yangyuhui yangyh4@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/schedule/task")
public class BioneTaskController extends BaseController {
	@Autowired
	private TaskListBS taskListBS;
	@Autowired
	private TaskBS taskBS;
	@Autowired
	private TriggerBS triggerBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/task/task-index";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/task/task-editNew";
	}

	@RequestMapping("selectTrigger")
	public String selectTrigger() {
		return "/frame/task/task-trigger";
	}

	@RequestMapping("/checkIsRunning")
	public String checkIsRunning(String taskId, String triggerId,
			String beanName) {
		if (taskId != null && !"".equals(taskId) && triggerId != null
				&& !"".equals(triggerId) && beanName != null
				&& !"".equals(beanName)) {
			BioneTaskInfo oldTask = this.taskListBS.getEntityById(
					BioneTaskInfo.class, taskId);
			if (oldTask != null) {
				// 若实现类和触发器没发生变化，只是修改任务名称，只进行维护bione任务表动作
				if (oldTask.getBeanName() != null
						&& !"".equals(oldTask.getBeanName())
						&& oldTask.getBeanName().equals(beanName)
						&& oldTask.getTriggerId() != null
						&& !"".equals(oldTask.getTriggerId())
						&& oldTask.getTriggerId().equals(triggerId)) {
					return "";
				} else {
					// 判断该任务在qrtz中是否正在运行
					String triggerState = this.taskBS.getTriggerState(taskId);
					if ("running".equals(triggerState)) {
						// 若是正在运行任务，不允许修改
						return "running";

					}
				}
			}
		}
		return "";
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> create(BioneTaskInfo task, String testTriggerId) {
		Map<String, Object> result = new HashMap<>();
		if (task.getTaskType() != null && !"".equals(task.getTaskType())) {
			// 修改
			task.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			task.setCreateTime(new Timestamp(new Date().getTime()));
			BioneTaskInfo oldTask = this.taskListBS.getEntityById(
					BioneTaskInfo.class, task.getTaskId());
			if (oldTask != null) {
				// 若实现类和触发器没发生变化，只是修改任务名称，只进行维护bione任务表动作
				if (oldTask.getBeanName() != null
						&& !"".equals(oldTask.getBeanName())
						&& oldTask.getBeanName().equals(task.getBeanName())
						&& oldTask.getTriggerId() != null
						&& !"".equals(oldTask.getTriggerId())
						&& oldTask.getTriggerId().equals(task.getTriggerId())) {
					task = this.taskListBS.mergeTask(task);
				} else {
					// 判断该任务在qrtz中是否正在运行
					String triggerState = this.taskBS.getTriggerState(task
							.getTaskId());
					if ("running".equals(triggerState)) {
						// 若是正在运行任务，不允许修改
						result.put("status","fail");
						result.put("msg","running");
						return result;
					} else {
						task = this.taskListBS.mergeTask(task);
						try {
							taskBS.updateJob(task);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			// 新增
			task.setTaskType(GlobalConstants4frame.TASK_TYPE_SYSTASK);
			task.setTaskId(RandomUtils.uuid2());
			if (testTriggerId != null && "".equals(task.getTriggerId())) {
				task.setTriggerId(testTriggerId);
			}
			task.setCreateTime(new Timestamp(new Date().getTime()));
			task.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			// test
			this.taskListBS.updateTaskInfo(task);
			if (task != null
					&& task.getTaskId() != null
					&& !"".equals(task.getTaskId())
					&& GlobalConstants4frame.TASK_STS_NORMAL
							.equals(task.getTaskSts())) {
				// 若是正常状态作业，立即调启
				try {
					this.taskBS.startJob(task.getTaskId(),
							Class.forName(task.getBeanName()),
							task.getTriggerId(), task.getTaskName(),
							task.getTaskType(), task.getLogicSysNo());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (task != null && task.getTaskId() != null
					&& !"".equals(task.getTaskId())
					&& GlobalConstants4frame.TASK_STS_STOP.equals(task.getTaskSts())) {
				try {
					this.taskBS.addDurableJob(task.getTaskId(),
							Class.forName(task.getBeanName()),
							task.getTriggerId(), task.getTaskName(),
							task.getTaskType(), task.getLogicSysNo());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("status","success");
		result.put("msg","操作成功");
		return result;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void destroy(@PathVariable("id") String id) {
		this.taskListBS.removeEntityById(id);
	}

	@RequestMapping("/testIsExists")
	@ResponseBody
	public boolean testIsExists(String beanName) {
		if (beanName != null && !"".equals(beanName)) {
			try {
				Class.forName(beanName);
			} catch (ClassNotFoundException e) {
				// logger.warn("在任务定制的实现类验证中，任务的实现类不存在");
				return false;
			}
		}
		return true;
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		String triggerId = "";
		String triggerName = "";
		try {
			BioneTaskInfo task = this.taskListBS.getEntityById(id);
			triggerId = task.getTriggerId();
			BioneTriggerInfo triggerTmp = triggerBS.getEntityById(task
					.getTriggerId());
			if (triggerTmp != null) {
				triggerName = triggerTmp.getTriggerName();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ModelMap mm = new ModelMap();
		mm.put("id", id);
		mm.put("triggerId", triggerId);
		mm.put("triggerName", triggerName);
		return new ModelAndView("/frame/task/task-edit", mm);
	}

	@ResponseBody
	@RequestMapping("/start")
	public void start(String id) {
		this.taskBS.startNowJob(id);
	}

	@ResponseBody
	@RequestMapping("/resume")
	public void resume(String id) {
		this.taskBS.resumeJob(id);
	}

	@ResponseBody
	@RequestMapping("/pause")
	public void pause(String id) {
		this.taskBS.pauseJob(id);
	}

	/**
	 * 初始化Grid
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<BioneTaskInfo> searchResult = taskListBS.getTaskList(
				pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition());
		Map<String, Object> taskMap = Maps.newHashMap();
		taskMap.put("Rows", searchResult.getResult());
		taskMap.put("Total", searchResult.getTotalCount());
		return taskMap;
	}

	/**
	 * 验证Id是否重复
	 */
	@RequestMapping("/checkIdIsExist")
	public boolean checkIdIsExist(String taskId) {
		return this.taskListBS.checkIdIsExist(taskId);
	}

	/**
	 * 通过id查询并转化为页面显示数据
	 */
	@RequestMapping("/showInfo")
	@ResponseBody
	public BioneTaskInfo showInfo(String id) {
		return this.taskBS.getEntityById(id);
	}

	// 批量删除
	@RequestMapping("/batchDelete")
	public void batchDelete(String ids) {
		if (ids != null && !"".equals(ids)) {
			this.taskListBS.batchDelete(ids);
		}
	}
}
