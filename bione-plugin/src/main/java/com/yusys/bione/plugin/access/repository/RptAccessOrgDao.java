package com.yusys.bione.plugin.access.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;

@MyBatisRepository
public interface RptAccessOrgDao {

	public List<BioneOrgInfo> list(Map<String, Object> map);
}