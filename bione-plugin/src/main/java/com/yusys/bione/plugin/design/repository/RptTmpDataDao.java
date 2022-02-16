package com.yusys.bione.plugin.design.repository;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Primary;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.design.entity.RptDesignBatchCfg;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignComcellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignFavInfo;
import com.yusys.bione.plugin.design.entity.RptDesignQueryDetail;
import com.yusys.bione.plugin.design.entity.RptDesignQueryDim;
import com.yusys.bione.plugin.design.entity.RptDesignSourceDs;
import com.yusys.bione.plugin.design.entity.RptDesignSourceFormula;
import com.yusys.bione.plugin.design.entity.RptDesignSourceIdx;
import com.yusys.bione.plugin.design.entity.RptDesignSourceTabdim;
import com.yusys.bione.plugin.design.entity.RptDesignSourceTabidx;
import com.yusys.bione.plugin.design.entity.RptDesignSourceText;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimCatalog;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxBusiExt;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCfg;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFilterInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxSrcRelInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrBankExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrFrsExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrIdxFilter;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleIdxRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrOuterCfg;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrRptdimRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicRptRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevel;

@MyBatisRepository
@Primary
public interface RptTmpDataDao {
	public List<RptMgrReportInfo> getRptsByRptIds(Map<String,Object> params);
	
	public List<RptMgrReportCatalog> getCatalogsByRptIds(Map<String,Object> params);
	
	public List<RptMgrFrsExt> getRptsFrsByRptIds(Map<String,Object> params);
	
	public List<RptDesignTmpInfo> getDesignTmpByIds(Map<String,Object> params);
	
	public List<RptValidLogicRptRel> getLogicCheckInfoByIds(Map<String,Object> params);
	
	public List<RptDesignCellInfo> getDesignCellByIds(Map<String,Object> params);
	
	public List<RptDesignSourceIdx> getDesignIdxByIds(Map<String,Object> params);
	
	public List<RptDesignSourceTabidx> getDesignIdxTabByIds(Map<String,Object> params);
	
	public List<RptDesignSourceTabdim> getDesignDimTabByIds(Map<String,Object> params);
	
	public List<RptDesignSourceDs> getDesignDsByIds(Map<String,Object> params);
	
	public List<RptDesignSourceFormula> getDesignFormulaByIds(Map<String,Object> params);
	
	public List<RptDesignSourceText> getDesignTextByIds(Map<String,Object> params);
	
	public List<RptDesignQueryDim> getDesignQueryDimByIds(Map<String,Object> params);
	
	public List<RptDesignBatchCfg> getDesignBatchCfgByIds(Map<String,Object> params);
	
	public List<RptDesignFavInfo> getDesignFavInfoByIds(Map<String,Object> params);
	
	public List<RptDesignQueryDetail> getDesignQueryDetailsByIds(Map<String,Object> params);
	
	public List<RptSysModuleInfo> getSysModuleByIds(Map<String,Object> params);
	
	public List<RptSysModuleCol> getSysColByIds(Map<String,Object> params);
	
	public List<RptSysModuleCatalog> getSysCatalogByIds(Map<String,Object> params);
	
	public List<RptValidLogicRptRel> getValidRelByIds(Map<String,Object> params);
	
	public List<RptValidCfgextLogic> getValidLogicByIds(Map<String,Object> params);
	
	public List<RptValidCfgextWarn> getValidWarnByIds(Map<String,Object> params);
	
	public List<RptValidWarnLevel> getValidWarnLevelByIds(Map<String,Object> params);
	
	public List<BioneDsInfo> getBioneDsByIds(Map<String,Object> params);
	
	public List<RptIdxInfo> getRptIdxByIds(Map<String,Object> params);
	
	public List<RptIdxMeasureRel> getRptIdxMeasureByIds(Map<String,Object> params);
	
	public List<RptIdxDimRel> getRptIdxDimByIds(Map<String,Object> params);
	
	public List<RptIdxFormulaInfo> getRptIdxFormulaByIds(Map<String,Object> params);
	
	public List<RptIdxFilterInfo> getRptIdxFilterByIds(Map<String,Object> params);
	
	public List<RptIdxSrcRelInfo> getRptIdxSrcByIds(Map<String,Object> params);
	//中间断一下 太多了
	public List<RptIdxBusiExt> getRptIdxBusiExtByIds(Map<String,Object> params);
	
	public List<RptIdxCatalog> getCatalogIdx(Map<String, Object> map);
	
	public List<RptDimItemInfo> getItemInfo(Map<String, Object> map);
	
	public List<RptDimCatalog> getDimCatalog(Map<String,Object> map);
	
	public List<RptDimTypeInfo> getTypeInfoList(Map<String,Object> map);
	
	public List<RptMgrBankExt> getRptsBankByRptIds(Map<String,Object> params);
	
	public List<RptMgrRptdimRel> getRptsDimByRptIds(Map<String,Object> params);
	
	public List<RptMgrOuterCfg> getRptsOuterByRptIds(Map<String,Object> params);
	
	public List<RptMgrModuleRel> getRptsModuleRelByRptIds(Map<String,Object> params);
	
	public List<RptMgrModuleIdxRel> getRptsModuleIdxRelByRptIds(Map<String,Object> params);
	
	public List<RptMgrIdxFilter> getRptsIdxFilterRelByRptIds(Map<String,Object> params);
	
	public List<RptValidLogicIdxRel> getValidLogicIdxRelByIds(Map<String,Object> params);
	
	public List<RptMgrReportInfo> getRptsBySrcIdxNos(Map<String,Object> params);
	
	public List<Map<String, Object>> queryDrillSql(String drillSql);
	
	public List<RptDesignComcellInfo> getDesignComcellByIds(Map<String,Object> params);

	public List<RptIdxCfg> getRptIdxCfgByIds(Map<String, Object> params);
}
