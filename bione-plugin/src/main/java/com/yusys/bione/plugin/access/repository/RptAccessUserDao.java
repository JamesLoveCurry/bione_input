package com.yusys.bione.plugin.access.repository;

import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.plugin.access.web.vo.RptByUserAccessVO;
import com.yusys.bione.plugin.access.web.vo.RptUserAccessVO;

@MyBatisRepository
public interface RptAccessUserDao {

	PageMyBatis<RptByUserAccessVO> search(Map<String, Object> map);

	PageMyBatis<RptUserAccessVO> findInfo(Map<String, Object> map);

}
