package com.yusys.biapp.input.logs.repository;


import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.logs.entity.RptInputLstValidateLog;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface ValidateLogDao {
	
	public List<RptInputLstValidateLog>getRptInputLstValidateLog(Map<String,Object>params);
	
}
