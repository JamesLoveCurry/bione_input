package com.yusys.bione.plugin.rptidx.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxBusiExt;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDsRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFilterInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;
import com.yusys.bione.plugin.rptidx.web.vo.IdxEditorInfoVO;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxInfoVO;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxMeasureRelInfoVO;

/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
public interface IdxInfoMybatisDao {
	public List<Map<String,String>> getAllIdxInfo(Map<String, Object> map);
	
	public List<RptIdxInfo> getAllIdxInfoBean(Map<String, Object> map);
	
	public List<RptIdxInfoVO> listIdxInfoShow(Map<String, Object> map);
	
	public List<IdxEditorInfoVO> listIdxEditorInfoShow(Map<String, Object> map);

	public List<RptIdxInfo> listIdxInfo(Map<String, Object> map);

	public List<RptIdxInfo> getIdxInfoByParams(Map<String, Object> map);

	public void saveIdxInfo(RptIdxInfo info);

	public void saveBusiExt(RptIdxBusiExt busiExt);

	public void saveIdxMeasureRel(RptIdxMeasureRel idxMeasureRel);

	public void saveIdxFilterInfo(RptIdxFilterInfo idxFilterInfo);

	public void updateIdxInfo(RptIdxInfo info);

	public void updateBusiExt(RptIdxBusiExt busiExt);

	public void deleteIdxInfo(Map<String, Object> map);

	public Integer testSameIndexNo(Map<String, Object> params);

	public Integer testSameIndexNm(Map<String, Object> params);

	public List<RptIdxBusiExt> findLIkeInfoList(Map<String, Object> params);

	public List<RptIdxMeasureInfo> getMeasureColListByParams(
			Map<String, Object> params);

	public String getSourceIdBySetId(String setId);

	public RptIdxDsRel getRptIdxDsRel(Map<String, Object> params);

	public List<RptSysModuleCol> getRptSysColByParams(Map<String, Object> params);

	public void deleteIdxBusiExt(Map<String, Object> params);

	public void deleteIdxMeasureRel(Map<String, Object> params);

	public void deleteIdxFormula(Map<String, Object> params);

	public void deleteIdxFilter(Map<String, Object> params);

	public void deleteIdxDim(Map<String, Object> params);
	
	public void deleteIdxSrc(Map<String, Object> params);
	

	public RptIdxBusiExt getIdxBusiExtByParams(Map<String, Object> params);

	public List<RptIdxMeasureRel> getMeasureRelByParams(
			Map<String, Object> params);

	public List<RptIdxMeasureRelInfoVO> getMeasureRelInfoList(
			Map<String, Object> params);

	public void saveIdxDimRel(RptIdxDimRel rptIdxDimRel);
	
	public void deleteIdxDimRel(Map<String, Object> params);

	public void saveIdxFormulaInfo(RptIdxFormulaInfo idxFormulaInfo);

	public List<RptIdxDimRel> getDimListBySrcIndex(Map<String, Object> params);

	public List<RptIdxDimRel> getDimListByOldVerIndex(Map<String, Object> params);

	public RptDimTypeInfo getDimTypeInfoById(String dimTypeNo);

	public List<RptDimTypeInfo> getDimTypeInfos(Map<String, Object> params);

	public RptIdxFilterInfo getIdxFilterInfoByParams(Map<String, Object> params);

	public List<RptIdxFilterInfo> getIdxFilterInfoListByParams(
			Map<String, Object> params);

	public RptIdxFormulaInfo getFormulaInfoByParams(Map<String, Object> params);

	public RptIdxMeasureInfo getMeasureById(String measureNo);

	public List<RptIdxInfo> getAllIdxVersionByParams(Map<String, Object> params);

	public String getMaxIndexNo(String type);

	public Integer getIdxCountByCataNos(Map<String, Object> map);

	public List<BioneParamInfo> getParamDeptListByParams(Map<String, Object> map);
	
	public List<RptIdxCatalog> getCatalogHaveIdx(Map<String, Object> map);
	
	public List<RptIdxFormulaInfo>  getIdxFormulaByParams(Map<String, Object> map);
	
	public List<RptIdxDimRel>  getIdxDimRelByParams(Map<String, Object> map);
	
	public List<RptIdxMeasureRel>  getIdxMeasureRelByParams(Map<String, Object> map);
	public List<RptIdxMeasureRel>  getIdxMeasureRelMaxByParams(Map<String, Object> map);
	
	public List<RptIdxInfo> getIdxListByNmList(Map<String, Object> map);
	
	public List<RptIdxInfo> getIdxListByIdList(Map<String, Object> map);
	
	public List<RptIdxBusiExt>    getIdxListByUsualNmList(Map<String, Object> map);
	
	public   Integer  getRelatedCountByIndexNo(Map<String,Object> params);
	
	public List<RptIdxInfo> getOrgIdxs(Map<String , Object> params);
	
	public List<RptIdxInfo> getUserIdxs(Map<String , Object> params);
	
	public List<RptIdxInfo> listIdxDsInfo(Map<String, Object> map);
	
	public Long getIdxMaxVerId(Map<String , Object> params);
	
	public List<Map<String , Object> > queryIdxInfo(Map<String , Object> params);

	public RptIdxInfo getIdxDsInfo(Map<String, Object> map);
	
	public List<RptIdxInfoVO> influenceIdxList(Map<String, Object> map);
	
	public List<String> getSrcIdxNoListByParams(Map<String, Object> map);

	public List<RptIdxInfo> getRptIdxInfo(
			Map<String, Object> params);
}
