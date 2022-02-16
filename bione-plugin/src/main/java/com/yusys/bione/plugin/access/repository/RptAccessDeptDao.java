package com.yusys.bione.plugin.access.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;

@MyBatisRepository
public interface RptAccessDeptDao {

	List<BioneDeptInfo> getTree(Map<String, Object> map);
	
}
