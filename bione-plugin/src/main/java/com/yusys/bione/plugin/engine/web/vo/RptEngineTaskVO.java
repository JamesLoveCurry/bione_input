package com.yusys.bione.plugin.engine.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.engine.entity.RptEngineAutoTaskInfo;


@SuppressWarnings("serial")
public class RptEngineTaskVO extends RptEngineAutoTaskInfo{
	
	private String taskNm;

	public RptEngineTaskVO() {
		
	}

	public RptEngineTaskVO(RptEngineAutoTaskInfo info, String taskNm) {
		BeanUtils.copy(info, this);
		this.taskNm = taskNm;
	}

	public String getTaskNm() {
		return taskNm;
	}

	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}

}