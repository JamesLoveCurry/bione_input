/**
 * 
 */
package com.yusys.bione.plugin.design.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.design.entity.*;
import com.yusys.bione.plugin.design.web.vo.*;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCfg;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFilterInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxSrcRelInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicRptRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevel;

import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
@Primary
public interface RptTmpDAO {

	public List<RptMgrReportCatalog> getCatalogByIds(List<String> catalogIds);
	
	public List<RptMgrReportCatalog> getCatalogByUpId(Map<String, String> params);

	public List<RptMgrReportCatalog> getCatalogById(Map<String, String> params);

	public List<RptMgrReportCatalog> getCatalogByParams(
			Map<String, String> params);

	public List<RptMgrReportCatalog> getSubCatalogs(List<String> catalogIds);

	public List<RptBatchViewVO> getBatchCfgs(Map<String, Object> params);
	
	public List<RptDesignQueryDim> getQueryDim(Map<String , Object> params);

	public List<RptMgrReportInfo> getRptsByCatalogId(List<String> catalogIds);

	public void saveCatalog(RptMgrReportCatalog catalog);

	public void updateCatalog(RptMgrReportCatalog catalog);

	public void deleteCatalogs(List<String> catalogIds);

	public List<? extends ReportInfoVO> getFrsRptsByPage(Map<String, Object> params);

	public List<? extends ReportInfoVO> getFrsRptsById(Map<String, Object> params);
	
	public List<? extends ReportInfoVO> getFrsRptsByIdNew(Map<String, Object> params);

	public List<RptSysModuleCatalog> getModuleCatalogNodes(Map<String,Object> params);

	public List<RptSysModuleInfo> getModuleInfoNodes(Map<String,Object> params);
	
	public List<RptSysModuleInfo> getModuleInfoNoDetailNodes(Map<String,Object> params);

	public List<RptSysModuleColVO> getModuleColNodes(List<String> setIds);
	
	public List<RptDesignQueryDetailVO> getQueryDetails(Map<String , Object> params);

	public List<RptDesignTmpInfo> getTmpById(Map<String, Object> params);

	public List<RptDesignTmpInfo> getTmpByParams(Map<String, Object> params);

	public List<RptDesignCellInfo> getTmpCells(Map<String, Object> params);
	
	public List<RptDesignComcellInfo> getTmpComCells(Map<String, Object> params);
	
	public List<Map<String, Object>> getTmpAllCells(Map<String, Object> params);

	public List<RptDesignSourceDsVO> getModuleCells(Map<String, Object> params);

	public List<RptDesignFormulaCellVO> getFormulaCells(
			Map<String, Object> params);

	public List<RptDesignSourceText> getTextCells(Map<String, Object> params);

	public List<RptDesignIdxCellVO> getIdxCells(Map<String, Object> params);

	public List<RptDesignSrcIdxVO> getIdxCellsVO(Map<String, Object> params);
	
	public List<RptDesignSrcIdxTabVO> getIdxTabCellsVO(Map<String, Object> params);
	
	public List<RptDesignSrcDimTabVO> getDimTabCellsVO(Map<String, Object> params);

	public List<RptDesignSrcDsVO> getDsCellsVO(Map<String, Object> params);

	public List<RptDesignSrcFormulaVO> getFormulaCellsVO(
			Map<String, Object> params);

	public List<RptDesignSrcTextVO> getTextCellsVO(Map<String, Object> params);

	public List<RptDesignIdxCellVO> getIdxsByCell(Map<String, Object> params);
	
	public List<RptDesignIdxCalcCellVO> getCalcIdxsByCell(Map<String, Object> params);
	
	public List<RptDesignTabIdxVO> getColIdxsByCell(Map<String, Object> params);
	
	public List<RptDesignTabDimVO> getColDimsByCell(Map<String , Object> params);

	public List<RptIdxDimRel> getDimRelByNos(Map<String, Object> params);

	public List<RptIdxDimRel> getDimRelByVer(Map<String, Object> params);

	public List<RptIdxFormulaInfo> getFormulaByVer(Map<String, Object> params);

	public List<RptDesignFilterVO> getFilterByNos(Map<String, Object> params);

	public List<RptDesignFilterVO> getFilterByVer(Map<String, Object> params);

	public List<RptIdxInfo> getRptIdxs(Map<String, Object> params);

	public List<RptMgrReportInfo> getRptInfos(Map<String, Object> params);
	
	public List<RptMgrReportInfo> getRptById(Map<String, Object> params);

	public List<RptDesignSourceIdx> getSubTmpIdxs(Map<String, Object> params);

	public List<RptDesignSourceIdx> getMainTmpIdxs(Map<String, Object> params);

	public void deleteRptInfos(List<String> rptIds);

	public void deleteRptFrsExts(List<String> rptIds);

	public void deleteDesignTmp(Map<String, Object> params);

	public void deleteDesignCell(Map<String, Object> params);

	public void deleteSourceIdx(Map<String, Object> params);

	public void deleteSourceDs(Map<String, Object> params);

	public void deleteSourceFormula(Map<String, Object> params);

	public void deleteSourceText(Map<String, Object> params);

	public void deleteIdxInfo(Map<String, Object> params);

	public void deleteIdxMeasureRel(Map<String, Object> params);

	public void deleteIdxDimRel(Map<String, Object> params);
	
	public void deleteIdxSrcRel(Map<String, Object> params);

	public void deleteIdxFilter(Map<String, Object> params);

	public void deleteIdxFormula(Map<String, Object> params);

	public void deleteBatchCfg(Map<String, Object> params);
	
	public void deleteParamInfo(Map<String , Object> params);
	
	public void deleteParamAttrs(Map<String , Object> params);
	
	
	public void deleteFavInfo(Map<String , Object> params);
	
	public void deleteSourceColIdx(Map<String , Object> params);
	
	public void deleteSourceColDim(Map<String , Object> params);

	public void deleteCell4Sync(Map<String, Object> params);

	public void deleteSourceDs4Sync(Map<String, Object> params);

	public void deleteSourceFormula4Sync(Map<String, Object> params);

	public void deleteSourceText4Sync(Map<String, Object> params);

	public void deleteIdx4Sync(Map<String, Object> params);

	public void deleteMeasureRel4Sync(Map<String, Object> params);

	public void deleteDimRel4Sync(Map<String, Object> params);

	public void deleteFilter4Sync(Map<String, Object> params);

	public void deleteFormula4Sync(Map<String, Object> params);

	public List<String> getPriCellNo(Map<String, Object> params);

	public List<RptMgrBusiLine> getBusiLine(Map<String, Object> params);

	public void updateTemplate(Map<String, Object> params);

	public void updateIdxCycle(Map<String, Object> params);

	public List<RptDimTypeInfo> getDimTypes(Map<String, Object> params);

	public List<RptIdxDimRel> getIdxDimRels(Map<String, Object> params);
	
	public List<RptSysModuleCol> getModuleDimRels(Map<String, Object> params);
	
	public void deleteQueryDim(Map<String , Object> params);
	
	public void deleteQueryDetail(Map<String , Object> params);
	
	public void deleteComDetail(Map<String , Object> params);

	/** 版本相关DAO begin **/

	public List<String> getAllTmpIdsByParent(Map<String, Object> params);

	public void updateTmpVerEndDate(Map<String, Object> params);

	public void updateIdxVerEndDate(Map<String, Object> params);

	public void updateLogicVerEndDate(Map<String, Object> params);

	public void updateWarnVerEndDate(Map<String, Object> params);

	public List<RptValidCfgextLogic> getLogicByPublish(
			Map<String, Object> params);

	public List<RptValidLogicRptRel> getRelByPublish(Map<String, Object> params);

	public List<RptValidCfgextWarn> getWarnByPublish(Map<String, Object> params);

	public List<RptValidWarnLevel> getLevelByPublish(Map<String, Object> params);

	public List<? extends ReportInfoVO> getRptVersion(Map<String, Object> params);
	
	public RptDesignTmpInfo getDesignTmpById(Map<String,Object> params);

	/** 版本相关DAO end **/

	public void updateRptBase(Map<String, Object> params);

	public void updateTemplateInfo(Map<String, Object> params);
	
	public void updateRptExt(Map<String, Object> params);
	
	public void updateIndexInfo(Map<String, Object> params);
	
	/**
	 * 任务与报表是否绑定 
	 */
	public List<String> getDeployedTaskparams(Map<String, Object> params);
	
	/**
	 * 保存rptid和表样路径定 
	 */
	public void saveRptidTempRel(Map<String,String> params);
	
	public String getRptIdByName(String rptNm);
	
	public List<Map<String,Object>> getVeridByVernmAndBusitype();
	
	public List<Map<String,String>> getRptTemplateList(Map<String,Object> params);
	
	public void delRptTemplateById(String id);
	
	public void delRptTemplateByIdVer(Map<String,String> params);
	
	public void saveTmpInfo(RptDesignTmpInfo tmpInfo);

	/**
	 * @param dsId
	 * @return
	 */
	public List<Map<String, String>> getPkColsByDsId(String dsId);

	/**
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getRptIdxCfgList(Map<String, Object> params);

	/**
	 * @param param1
	 */
	public void deleteRptIdxCfg(Map<String, Object> param1);

	/**
	 * 获取静态常量单元格
	 * @return
	 */
	List<Map<String, Object>> getStatictextCell(Map<String, Object> param);

	void delAuthObjRels(List<String> rptIds);

	public void saveRptDesignFavInfo(List<RptDesignFavInfo> subList);

	public void saveBatchCfg(List<RptDesignBatchCfg> subList);

	public void saveSourceDs(List<RptDesignSourceDs> subList);

	public void saveComDetail(List<RptDesignComcellInfo> subList);

	public void saveSourceText(List<RptDesignSourceText> subList);

	public void saveSourceFormula(List<RptDesignSourceFormula> subList);

	public void saveIdxMeasureRel(List<RptIdxMeasureRel> subList);

	public void saveIdxDimRel(List<RptIdxDimRel> subList);

	public void saveIdxFilter(List<RptIdxFilterInfo> subList);

	public void saveIdxInfo(List<RptIdxInfo> subList);

	public void saveIdxSrcRel(List<RptIdxSrcRelInfo> subList);

	public void saveIdxFormula(List<RptIdxFormulaInfo> subList);

	public void saveDesignCell(List<RptDesignCellInfo> subList);

	public void saveSourceIdx(List<RptDesignSourceIdx> subList);

	public void saveRptIdxCfg(List<RptIdxCfg> subList);
}
