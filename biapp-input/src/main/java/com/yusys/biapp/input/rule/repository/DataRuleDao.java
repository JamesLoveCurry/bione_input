package com.yusys.biapp.input.rule.repository;


import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface DataRuleDao {
	
	public RptInputListDataRuleInfo getRptInputListDataRuleInfoByRuleId(String ruleId);
	
}
