package com.yusys.bione.frame.syslog.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.syslog.entity.BioneLogFunc;
import com.yusys.bione.frame.syslog.web.vo.BioneLogFuncVO;
import com.yusys.bione.frame.syslog.web.vo.BioneLogMenuVO;

@MyBatisRepository
public interface BioneLogFuncMybatisDao {

	public void saveLogFunc(BioneLogFunc bioneLogFunc);

	public List<BioneLogFuncVO> list(Map<String, Object> condition);
	
	public List<BioneLogMenuVO> menuList(Map<String, Object> map);
}
