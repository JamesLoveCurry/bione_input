package com.yusys.bione.plugin.valid.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicDsRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicOrg;
import com.yusys.bione.plugin.valid.web.vo.CfgextLogicVO;
import com.yusys.bione.plugin.valid.web.vo.IdxInfoVO;
import com.yusys.bione.plugin.valid.web.vo.NameAndIdMap;

import java.util.List;
import java.util.Map;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author
 */
@MyBatisRepository
public interface ValidLogicMybatisDao {
	public String[] getValidLogicRptRelCheckIdsByTempLateId(Map<String, Object> condition);

	public List<CfgextLogicVO> listLogic(Map<String, Object> condition);

	public void updateLogic(CfgextLogicVO logic);

	public void insertLogic(RptValidCfgextLogic info);

	public void deleteLogic(Map<String, Object> map);

	public void deleteLogicRptRel(Map<String, Object> map);

	public List<CfgextLogicVO> list(Map<String, Object> map);

	public List<NameAndIdMap> selectMapping(Map<String, Object> map);

	public List<RptValidCfgextLogic> indexList(Map<String, String> map);

	public String getLineNm(Map<String, String> map);

	public String getRptNm(Map<String, String> map);

	public void insertLogicRel(RptValidLogicIdxRel rel);

	public void insertLogicDsRel(RptValidLogicDsRel rel);

	public void updateLogicRel(RptValidLogicIdxRel rel);

	public void deleteLogicRel(Map<String, Object> map);

	public void deleteLogicDsRel(Map<String, Object> map);

	public List<IdxInfoVO> getAllIdx(Map<String, Object> map);

	public List<NameAndIdMap> selectCellNoMapping(Map<String, Object> map);

	public List<NameAndIdMap> selectBusiNoMapping(Map<String, Object> map);

	public List<RptSysModuleCol> selectDatasetInfo(Map<String, Object> map);

	public void deleteLogicByTmpId(Map<String, Object> map);

	public void deleteLogicIdxByTmpId(Map<String, Object> map);

	public void deleteLogicRptByTmpId(Map<String, Object> map);
	
	public List<String> findCheckIds(Map<String, Object> map);
	
	public List<NameAndIdMap> getNameAndNo(Map<String, Object> map);
	
	public int findLogicByIdxNo(Map<String,Object> params);

	/**
	 * @param params
	 */
	public void upDateRptValidLogicOrg(Map<String, Object> params);

	/**
	 * @param params
	 */
	public void deleteRptValidLogicOrg(Map<String, Object> params);

	/**
	 * @param rptValidLogicOrg
	 */
	public void insertRptValidLogicOrg(RptValidLogicOrg rptValidLogicOrg);

	void batchInsertRptValidLogicOrg(List<Map<String, Object>> list);

    List<NameAndIdMap> getNameAndCellNo(Map<String, Object> map);
}
