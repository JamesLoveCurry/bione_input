package com.yusys.biapp.input.supplement;

import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.task.entity.RptTskCatalog;
import com.yusys.biapp.input.task.entity.RptTskInfo;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

@MyBatisRepository
public interface SupDao {
	
	/**
	 * 得到所有的目录信息
	 * @return
	 */
	public List<RptTskCatalog>getRptTskCatalog(Map<String, Object> map);
	
	/**
	 * 根据条件查询目录下的任务信息
	 * @param catalogId
	 * @return
	 */
	public List<RptTskInfo>getTaskInfos(Map<String,Object>map);

}
