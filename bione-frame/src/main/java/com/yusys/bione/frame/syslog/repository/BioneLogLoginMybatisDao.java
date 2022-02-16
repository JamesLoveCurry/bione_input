package com.yusys.bione.frame.syslog.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.syslog.entity.BioneLogLogin;
import com.yusys.bione.frame.syslog.web.vo.BioneLogLoginVO;
import com.yusys.bione.frame.syslog.web.vo.BioneLogUserVO;

@MyBatisRepository
public interface BioneLogLoginMybatisDao {

	public List<BioneLogLoginVO> list(Map<String, Object> params);
	
	public void save(BioneLogLogin login);
	
	public void update(BioneLogLogin login);
	
	public List<BioneLogUserVO> userDetail(Map<String, Object> map);
}
