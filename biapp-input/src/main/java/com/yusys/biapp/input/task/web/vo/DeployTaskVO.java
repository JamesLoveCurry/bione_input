package com.yusys.biapp.input.task.web.vo;

import java.util.List;

import com.yusys.biapp.input.task.entity.RptTskInfo;
import com.yusys.biapp.input.task.vo.TaskNodeOrgVO;
import com.yusys.biapp.input.task.vo.TskExeobjRelVO;


public class DeployTaskVO {

	private String taskTitle;
	// 任务基本信息
	private RptTskInfo rptTskInfo;

	private List<TaskNodeOrgVO> taskNodeOrgList;

	// 监管任务与任务对象关系
	private TskExeobjRelVO tskExeobjRelVO;

	private String dataDate;

	private List<String> orgs;
	
	private List<String>openOrgs;
	
	private List<TaskUnifyNodeVO> taskUnifyNodeList;
	
	private String loadDataMark;
	
	private List<String> upOrg;
	
	//后置任务
	private DeployTaskVO afterTask;

	public List<String> getUpOrg() {
		return upOrg;
	}

	public void setUpOrg(List<String> upOrg) {
		this.upOrg = upOrg;
	}

	public DeployTaskVO getAfterTask() {
        return afterTask;
    }

    public void setAfterTask(DeployTaskVO afterTask) {
        this.afterTask = afterTask;
    }

    public DeployTaskVO() {
	}

	public RptTskInfo getRptTskInfo() {
		return rptTskInfo;
	}

	public void setRptTskInfo(RptTskInfo rptTskInfo) {
		this.rptTskInfo = rptTskInfo;
	}

	public List<TaskNodeOrgVO> getTaskNodeOrgList() {
		return taskNodeOrgList;
	}

	public void setTaskNodeOrgList(List<TaskNodeOrgVO> taskNodeOrgList) {
		this.taskNodeOrgList = taskNodeOrgList;
	}

	public TskExeobjRelVO getTskExeobjRelVO() {
		return tskExeobjRelVO;
	}

	public void setTskExeobjRelVO(TskExeobjRelVO tskExeobjRelVO) {
		this.tskExeobjRelVO = tskExeobjRelVO;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public List<String> getOrgs() {
		return orgs;
	}

	public void setOrgs(List<String> orgs) {
		this.orgs = orgs;
	}

	public List<TaskUnifyNodeVO> getTaskUnifyNodeList() {
		return taskUnifyNodeList;
	}

	public void setTaskUnifyNodeList(List<TaskUnifyNodeVO> taskUnifyNodeList) {
		this.taskUnifyNodeList = taskUnifyNodeList;
	}

	public List<String> getOpenOrgs() {
		return openOrgs;
	}

	public void setOpenOrgs(List<String> openOrgs) {
		this.openOrgs = openOrgs;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getLoadDataMark() {
		return loadDataMark;
	}

	public void setLoadDataMark(String loadDataMark) {
		this.loadDataMark = loadDataMark;
	}

}
