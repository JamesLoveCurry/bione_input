package com.yusys.bione.plugin.valid.repository;

import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicRptRel;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface ValidLogicRelMybatisDao {
	public void updateRel(RptValidLogicRptRel info);
	public void saveRel(RptValidLogicRptRel info);
	public void deleteRel(Map<String, Object> map);
}
