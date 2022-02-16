package com.yusys.biapp.input.task.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.task.entity.RptInputTaskTempleLock;
import com.yusys.bione.comp.service.BaseBS;

@Service
@Transactional(readOnly = true)
public class TaskLockBS extends BaseBS<RptInputTaskTempleLock> {

	/**
	 * 获取已锁定任务模板ID
	 * @param caseId
	 * @return
	 */
	public List<String> getTaskTempLock(String caseId) {
		List<RptInputTaskTempleLock> lockList = this.getEntityListByProperty(RptInputTaskTempleLock.class, "taskCaseId", caseId);
		List<String> list = Lists.newArrayList();
		for (RptInputTaskTempleLock role : lockList) {
			list.add(role.getTemplateId());
		}
		return list;
	}
}
