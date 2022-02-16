package com.yusys.bione.plugin.engine.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.engine.entity.RptEngineTsk;
import com.yusys.bione.plugin.engine.repository.mybatis.EngineDao;

@Service
@Transactional(readOnly = true)
public class RptEngineTaskObjRelBS extends BaseBS<Object> {
	@Autowired
	private EngineDao edao;
	
	/**
	 * 获取用于加载grid的数据（公告显示时查看所有）
	 */
	public Map<String,Object> getTaskObjRel(Pager pager) {
		 Map<String,Object> result=new HashMap<String, Object>();
		PageHelper.startPage(pager);
		PageMyBatis<RptEngineTsk> lists=(PageMyBatis<RptEngineTsk>) this.edao.getEngineInfo(null);
		result.put("Rows", lists.getResult());
		result.put("Total", lists.getTotalCount());
		return result;
	}
	
	@Transactional(readOnly = false)
	public void removeTsk(String tskNo){
		String jql = "delete from RptEngineTsk tsk where tsk.taskNo = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, tskNo);
	}
	
	@Transactional(readOnly = false)
	public void removeTskRel(String tskNo){
		String jql = "delete from RptEngineTskobjRel tsk where tsk.id.taskNo = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, tskNo);
	}
}
