package com.yusys.bione.plugin.access.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.plugin.access.web.vo.DeptUserAccessVO;
import com.yusys.bione.plugin.access.web.vo.RptUserAccessVO;

@MyBatisRepository
public interface RptAccessReportDao {

	public List<DeptUserAccessVO> search(Map<String, Object> map) ;

	public PageMyBatis<RptUserAccessVO> findInfo(Map<String, Object> map);
		

}
