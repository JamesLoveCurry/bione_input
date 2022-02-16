package com.yusys.bione.plugin.valid.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.web.vo.CfgextWarnVO;
import com.yusys.bione.plugin.valid.web.vo.ValidCfgextWarnVO;

import java.util.List;
import java.util.Map;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface ValidWarnMybatisDao {
	public List<ValidCfgextWarnVO> listWarn(Map<String, String> map);
	public void saveWarn(RptValidCfgextWarn info);
	public void updateWarn(RptValidCfgextWarn info);
	public void deleteWarn(Map<String, Object> map);
	public List<String> getTmpInfo(Map<String, Object> map);
	public List<String> getCellInfo(Map<String, String> map);
	public List<String> findCheckIds(Map<String, Object> map);
	public int findWarnByIdxNo(Map<String,Object> params);

	List<CfgextWarnVO> getCfgextWarnList(Map<String, Object> condition);
}
