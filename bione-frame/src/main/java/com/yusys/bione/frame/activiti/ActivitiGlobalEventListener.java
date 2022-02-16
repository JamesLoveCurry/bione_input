package com.yusys.bione.frame.activiti;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

public class ActivitiGlobalEventListener implements ActivitiEventListener {

	protected Logger logger = LoggerFactory.getLogger(ActivitiGlobalEventListener.class);
	
	private ActivitiSupport activitiSupport;

	private synchronized void init() {
		if (activitiSupport != null) {
			return;
		}
		try {
			Class<?> c = Class.forName("com.yusys.bione.frame.activiti.ActivitiSupport");
			activitiSupport = (ActivitiSupport)ContextLoader.getCurrentWebApplicationContext().getBean(c);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对 TASK_CREATED 事件响应，设置当前任务的执行候选人
	 */
	private void taskCreate(ActivitiEvent event) {
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent)event;
		DelegateTask delegateTask = (DelegateTask)entityEvent.getEntity();

		Set<IdentityLink> identityLinkSet = delegateTask.getCandidates();
		// 检查是否已经有执行候选人了
		for (Iterator<IdentityLink> it = identityLinkSet.iterator(); it.hasNext(); ) {
			IdentityLink identityLink = it.next();
			if (! StringUtils.isEmpty(identityLink.getUserId())) {
				// 如果已经有执行候选人了，不再处理
				return;
			}
		}
		Set<String> userIdSet = new HashSet<String>();
		for (Iterator<IdentityLink> it = identityLinkSet.iterator(); it.hasNext(); ) {
			IdentityLink identityLink = it.next();
			// 找到执行候选角色
			if (StringUtils.isEmpty(identityLink.getGroupId())) {
				continue;
			}
			// 添加执行候选人
			userIdSet.addAll(activitiSupport.calCandidateUser(identityLink.getGroupId(), delegateTask.getVariables()));
		}
		if (userIdSet.size() > 0) {
			delegateTask.addCandidateUsers(userIdSet);
		}
	}
	
	@Override
	public void onEvent(ActivitiEvent event) {
		if (event.getType() == ActivitiEventType.ENGINE_CREATED) {
			return;
		}
		init();
		switch (event.getType()) {
		case TASK_CREATED:
			taskCreate(event);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean isFailOnException() {
		return true;
	}
}
