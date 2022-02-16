package com.yusys.bione.plugin.valid.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaFunc;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaSymbol;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface FunAndSymbolMybatisDao {
	public List<RptIdxFormulaFunc> listFunc(Map<String, Object> map);
	public List<RptIdxFormulaSymbol> listSymbol(Map<String, Object> map);
}
