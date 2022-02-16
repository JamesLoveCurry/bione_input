package com.yusys.bione.frame.schedule.web;

import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.TriggerInfoHolder;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.schedule.entity.BioneTriggerInfo;
import com.yusys.bione.frame.schedule.service.TaskBS;
import com.yusys.bione.frame.schedule.service.TriggerBS;
import com.yusys.bione.frame.schedule.web.vo.BioneTriggerInfoVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:触发器管理类
 * Description:
 * </pre>
 * 
 * @author huangye huangye@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/schedule/trigger")
public class TriggerController extends BaseController {
	protected static Logger log = LoggerFactory
			.getLogger(TriggerController.class);
	@Autowired
	private TriggerBS triggerBS;
	@Autowired
	private TaskBS taskBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/trigger/trigger-index";
	}

	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<BioneTriggerInfo> triggerResult = this.triggerBS
				.getTriggerList(pager.getPageFirstIndex(), pager.getPagesize(),
						pager.getSortname(), pager.getSortorder(),
						pager.getSearchCondition());
		Map<String, Object> triggerMap = Maps.newHashMap();
		triggerMap.put("Rows", triggerResult.getResult());
		triggerMap.put("Total", triggerResult.getTotalCount());
		return triggerMap;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/trigger/trigger-editNew";
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/trigger/trigger-edit", "id", id);
	}

	@RequestMapping("/showTriggerInfo.*")
	@ResponseBody
	public BioneTriggerInfo showTriggerInfo(String id) {
		return this.triggerBS.getEntityById(id);
	}

	@RequestMapping("/checkHasJobOrNot")
	@ResponseBody
	public boolean checkHasJobOrNot(String ids) {
		if (ids != null && !"".equals(ids)) {
			String names = this.triggerBS.checkHasJobTriggers(ids);
			if (names == null || names.equals(""))
				return true;
		}
		return false;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void destroy(String id) {
		if (id != null && !"".equals(id)) {
			String[] ids = StringUtils.split(id, ',');
			this.triggerBS.deleteBatch(ids);
		}
	}

	@RequestMapping("/destroyOwn")
	public void destroyOwn(String idStr) {
		if (idStr != null && !"".equals(idStr)) {
			String[] ids = StringUtils.split(idStr, ',');
			this.triggerBS.deleteBatch(ids);
		}
	}

	@RequestMapping("/checkRunningJob")
	@ResponseBody
	public boolean checkRunningJob(String triggerId, String cron) {
		if (triggerId != null && !"".equals(triggerId) && cron != null
				&& !"".equals(cron)) {
			BioneTriggerInfo oldTrigger = this.triggerBS.getEntityById(
					BioneTriggerInfo.class, triggerId);
			if (oldTrigger != null) {
				if (!cron.equals(oldTrigger.getCron())) {
					// 若触发器发生更改,判断该触发器的关联作业是否有正在运行作业，若有，给出提示
					String str = this.triggerBS
							.checkHasRunningJobOrNot(triggerId);
					if (!"".equals(str) && str != null) {
						// 若有运行作业
						return true;
					}
				}
			}
		}
		return false;
	}

	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneTriggerInfoVO triggervo, String endTime,
			String startTime) {
		BioneTriggerInfo trigger = new BioneTriggerInfo();
		// 新增的触发器尚未被其它作业引用，则无须对作业进行调度
		triggervo.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		// 旧触发器数据
		BioneTriggerInfo ti = null;
		if (triggervo.getTriggerId() != null
				&& !"".equals(triggervo.getTriggerId())) {
			ti = this.triggerBS.getEntityById(triggervo.getTriggerId());
		}
		BeanUtils.copy(triggervo, trigger);
		if (startTime != null && !"".equals(startTime)) {
			trigger.setStartTime(new Timestamp(DateUtils
					.getDateStartLong(startTime)));
		}
		if (endTime != null && !"".equals(endTime)) {
			trigger.setEndTime(new Timestamp(DateUtils
					.getDateStartLong(endTime)));
		}
		if (trigger.getTriggerId() == null || trigger.getTriggerId().equals("")) {
			trigger.setTriggerId(RandomUtils.uuid2());
		}
		this.triggerBS.saveOrUpdateTrigger(trigger);
		// 刷新trigger的缓存
		TriggerInfoHolder.refreshTriggerInfo();
		if (trigger.getTriggerId() != null
				&& !"".equals(trigger.getTriggerId())) {
			// 修改操作时，对触发器信息修改了的进行重跑
			if (ti != null && ti.getCron().equals(trigger.getCron())) {
				// 说明触发器信息都没改变，则可以不采取操作
			} else {
				this.taskBS.updateJobByTriggerId(trigger.getTriggerId());
			}
		}
	}
	
	/**
	 * 重名校验
	 * @param triggerId
	 * @param triggerName
	 * @return
	 */
	@RequestMapping("/triggerNameValid")
	@ResponseBody
	public boolean triggerNameValid(String triggerId, String triggerName) {
		boolean valid = true;
		valid = triggerBS.triggerNameValid(triggerId, triggerName);
		return valid;
	}
}
