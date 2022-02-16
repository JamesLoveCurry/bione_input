package com.yusys.bione.frame.activiticustom;

import java.lang.reflect.Method;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateTask;

import com.yusys.bione.comp.repository.jdbc.JDBCBaseDAO;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.activiticustom.annotation.Notify;
import com.yusys.bione.frame.activiticustom.annotation.TaskListener;
import com.yusys.bione.frame.activiticustom.utils.ClassUtil;

public class TaskListenerDispatcher implements org.activiti.engine.delegate.TaskListener {
	
	private JDBCBaseDAO jdbcBaseDAO;
	
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		
		/*if(jdbcBaseDAO == null) {
			jdbcBaseDAO = SpringContextHolder.getBean("jdbcBaseDAO");
		}
		//assignment,create,complete,delete
		String eventName = delegateTask.getEventName();
		String destName = (String)delegateTask.getVariableLocal("destName");
		
		//只允许complete事件，调用指定接口的指定的方法
		if("complete".equals(eventName)) {
			try {
				Set<Class<?>> classSet = ClassUtil.getAllClassByPackname("com.yusys.bione.frame.activiticustom");
				for(Class cls : classSet) {
					//获取使用指定注解的类
					if(cls.isAnnotationPresent(TaskListener.class)) {
						TaskListener anno = (TaskListener) cls.getAnnotation(TaskListener.class);
						String Key = anno.key();
						String ProcessDefinitionId = delegateTask.getProcessDefinitionId();
						String processDefKey = ProcessDefinitionId.substring(0, ProcessDefinitionId.indexOf(":"));
						if(processDefKey.equals(Key)) {
							Method[] methods = cls.getDeclaredMethods();
							for(Method method : methods) {
								//method.getAnnotation(annotationClass)
								if(method.isAnnotationPresent(Notify.class)) {
									Object taskListener = cls.newInstance();
									Object obj = method.invoke(taskListener, null);
								}
							}
						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new ActivitiException("Exception occurred while trying to invoke the business listener which impl the TaskListener Annotation");
			}
		}*/
	}
	
}

