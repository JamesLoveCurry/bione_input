package com.yusys.bione.plugin.valid.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.valid.web.vo.CfgextOrgMergeVO;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface ValidOrgMergeMybatisDao {
	List<CfgextOrgMergeVO> listOrgMerge(Map<String, Object> condition);
	void delete(Map<String, Object> param);
}
