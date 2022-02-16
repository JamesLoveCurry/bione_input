package com.yusys.biapp.input.task.repository;


import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;

@MyBatisRepository
public interface TaskAuthDao {
	
	public List<BioneRoleInfo>getTaskAuthRole(List<String> list);
	
	public List<String>getChildOrgs(String orgNo);
	
	
	public List<Map<String,Object>>getUserInfoByOrgs(Map<String,Object>map);
	
}
