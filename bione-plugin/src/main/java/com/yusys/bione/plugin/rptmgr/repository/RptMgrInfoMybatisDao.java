package com.yusys.bione.plugin.rptmgr.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrBankExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrIdxFilter;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleIdxRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrOuterCfg;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportDataItem;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrRptdimRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrRptitemIdxRel;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrIdxFilterVO;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrInfoVO;
import com.yusys.bione.plugin.rptmgr.web.vo.RptTmpDataInfoVO;
import com.yusys.bione.plugin.wizard.web.vo.ReportImportVO;

/**
 * 
 * <pre>
 * Title: 报表管理Dao层
 * Description:
 * </pre>
 * 
 * @author weijiaxiang weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
public interface RptMgrInfoMybatisDao {
	// 根据参数获取报表信息
	public List<RptMgrReportInfo> getRptsByParams(Map<String, Object> params);
	
	public List<RptMgrReportInfo> getRptsByLabel(Map<String, Object> params);
	
	// 根据参数获取报表信息
	public List<RptMgrReportInfo> getAllRptsByParams(Map<String, Object> params);
	
	// 获取指定类型的报表目录
	public List<RptMgrReportCatalog> getRptCatalogs(Map<String, Object> params);

	public List<RptMgrReportCatalog> catalogNameValid(Map<String, Object> params);
	
	//由于迁移AUTH包位置，加入此方法
	public List<BioneRoleInfo> getRptRoleInfoByParam(Map<String, Object> paramMap);

	public void saveCatalog(RptMgrReportCatalog catalog);

	public void updateCatalog(RptMgrReportCatalog catalog);

	public List<RptMgrReportCatalog> getCatalogById(Map<String, Object> params);

	public void deleteCatalog(String catalogId);

	public List<RptDimTypeInfo> findDimTypeInfoByReport(Map<String, Object> params);
	
	public List<RptTmpDataInfoVO> getRptTmpParams(Map<String,Object> params);
	
	public List<RptMgrInfoVO> getReportInfo(Map<String, Object> params);
	
	public List<RptMgrInfoVO> getReportInfoByCatalogId(Map<String, Object> params);
	
	public RptMgrReportInfo getRptInfoByParams(Map<String,Object> params);
	
	
	
	public String getReportInfoCfgId(String rptId);
	
	public List<RptMgrReportDataItem> getDataItemByParams(Map<String, Object> params);
	
	public List<RptMgrIdxFilterVO> getIdxFilterByParams(Map<String, Object> params);
	
	
	public void saveReportInfo(RptMgrReportInfo info);
	
	public void saveRptBankExt(RptMgrBankExt bank);
	
	public void saveRptOuterCfg(RptMgrOuterCfg cfg);
	
	public void saveRptDimRel(RptMgrRptdimRel dimrel);
	
	public void saveRptDataItem(RptMgrReportDataItem dataitem);
	
	public void saveRptIdxRel(RptMgrRptitemIdxRel idx);
	
	public void saveRptMgrModule(RptMgrModuleRel moduleRel);
	
	public void saveRptMgrModuleIdx(RptMgrModuleIdxRel moduleidxRel);
	
	public void saveRptMgrIdxFilter(RptMgrIdxFilter idxFilter);
	
	public void updateReportInfo(RptMgrReportInfo info);
	
	public void updateRptBankExt(RptMgrBankExt info);
	
	public void updateRptOuterCfg(RptMgrOuterCfg cfg);
	
	public void updateRptDimRel(RptMgrRptdimRel dimrel);
	
	public void updateRptDataItem(RptMgrReportDataItem dataitem);
	
	public void updateRptIdxRel(RptMgrRptitemIdxRel idx);
	
	public void updateRptMgrModule(RptMgrModuleRel moduleRel);
	
	public void updateRptMgrModuleIdx(RptMgrModuleIdxRel moduleidxRel);
	
	public void updateRptMgrIdxFilter(RptMgrIdxFilter idxFilter);
	
	public void deleteReportInfo(String rptId);
	
	public void deleteRptBankExt(String rptId);
	
	public void deleteRptOuterCfg(String cfgId);
	
	public void deleteRptDimRel(String rptId);
	
	public void deleteRptDataItem(String rptId);
	
	public void deleteRptIdxRel(String rptId);
	
	public void deleteRptMgrModule(String rptId);
	
	public void deleteRptMgrModuleIdx(String rptId);
	
	public void deleteRptMgrIdxFilter(String rptId);
	
	public List<RptMgrReportInfo> validateRpt(Map<String, Object> params);
	
	public List<RptMgrInfoVO> getReportFrsInfo(Map<String, Object> params);
	
	public List<ReportImportVO> getExportReportInfo(Map<String, Object> params);
	
	public String getTemplateIdByRptInfo(Map<String, Object> params);
	
	public String getTemplateTypeByRptInfo(Map<String, Object> params);

	public List<RptMgrInfoVO> getRptInfoByCatalogId(Map<String, Object> params);
	
	public List<Map<String, Object>> queryRptNamesByRptIds(Map<String, Object> param);
	
	public List<RptMgrReportCatalog> getEastRptTreeCatalogs(Map<String, Object> params);
	
	public List<RptMgrReportInfo> getEastRptTreeNodes2(Map<String, Object> paramMap);
	
	public List<Map<String, String>> getTemplatepath(Map<String, Object> paramMap);
	
	/**
	 * 根据报表id查询未归档的报表的填报人员
	 */
	public List<Map<String, String>> getFillUserByRpt(Map<String, Object> paramMap);
	
	//根据参数获取报表信息 202002 lcy 增加明细类报表逻辑校验配置
	public List<RptMgrInfoVO> getRptVOsByParams(Map<String, Object> params);

	/**
	 * @param param
	 * @return
	 */
	public List<RptTmpDataInfoVO> getRptsParams(Map<String, Object> param);

	/**
	 * @param param
	 * @return
	 */
	public List<RptTmpDataInfoVO> getRptsParams2(Map<String, Object> param);
}
