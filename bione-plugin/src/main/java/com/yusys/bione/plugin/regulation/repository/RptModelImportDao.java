package com.yusys.bione.plugin.regulation.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo;
import com.yusys.bione.plugin.design.entity.*;
import com.yusys.bione.plugin.regulation.vo.FrsSystemCfgVO;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrFrsExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface RptModelImportDao {

    void deleteTest();

    String getTemplateIdByRptNum(String rptNum);

    void deleteRptIdxMeasureRel(Map<String, Object> sqlParams);

    void deleteRptIdxDimRel(Map<String, Object> sqlParams);

    void deleteRptIdxSrcRelInfo(Map<String, Object> sqlParams);

    void deleteRptIdxFilterInfo(Map<String, Object> sqlParams);

    void deleteRptIdxFormulaInfo(Map<String, Object> sqlParams);

    void deleteRptIdxInfo(Map<String, Object> sqlParams);
    
    void deleteRptIdxCfg(Map<String, Object> sqlParams);

    void deleteRptDesignTmpInfo(Map<String, Object> sqlParams);

    void deleteRptDesignCellInfo(Map<String, Object> sqlParams);

    void deleteRptDesignBatchCfg(Map<String, Object> sqlParams);

    void deleteRptDesignSourceDs(Map<String, Object> sqlParams);

    void deleteRptDesignSourceFormula(Map<String, Object> sqlParams);

    void deleteRptDesignSourceText(Map<String, Object> sqlParams);

    void deleteRptDesignSourceIdx(Map<String, Object> sqlParams);

    void deleteRptDesignSourceTabidx(Map<String, Object> sqlParams);

    void deleteRptDesignSourceTabdim(Map<String, Object> sqlParams);

    void deleteRptParamtmpInfo(Map<String, Object> sqlParams);

    void deleteRptParamtmpAttrs(Map<String, Object> sqlParams);

    void deleteRptDesignQueryDim(Map<String, Object> sqlParams);

    void deleteRptDesignQueryDetail(Map<String, Object> sqlParams);

    void deleteRptDesignComCellInfo(Map<String, Object> sqlParams);

    void deleteRptDesignFavInfo(Map<String, Object> sqlParams);

    void deleteRptMgrReportInfo(Map<String, Object> sqlParams);

    void insertTest();

    RptMgrReportInfo getSysReportInfo(String rptNum);

    FrsSystemCfgVO getFrsSystemCfg(Map<String, Object> sqlParams);

    void insertRptMgrReportInfo(RptMgrReportInfo rptMgrReportInfo);

    void deleteRptMgrFrsExt(Map<String, Object> sqlParams);

    RptMgrFrsExt getSysRptMgrFrsExt(String rptId);

    void insertRptMgrFrsExt(RptMgrFrsExt rptMgrFrsExt);

    RptDesignTmpInfo getSysRptDesignTmpInfo(Map<String, Object> sqlParams);

    List<RptMgrReportCatalog> getSysRptMgrReportCatalog(Map<String, Object> map);

    void insertRptDesignTmpInfo(RptDesignTmpInfo rptDesignTmpInfo);

    void insertRptDesignCellInfo(List<RptDesignCellInfo> list);

    void insertRptDesignSourceIdx(List<RptDesignSourceIdx> subList);

    List<String> getSysIdxNo(Map<String, String> cellString);

    void insertRptIdxInfo(List<RptIdxInfo> subList);

    void insertRptIdxMeasureRel(List<RptIdxMeasureRel> subList);

    void insertRptIdxDimRel(List<RptIdxDimRel> subList);

    void insertRptIdxSrcRelInfo(List<RptIdxSrcRelInfo> subList);

    void insertRptIdxFilterInfo(List<RptIdxFilterInfo> subList);

    void insertRptIdxFormulaInfo(List<RptIdxFormulaInfo> subList);
    
    void insertRptIdxCfg(List<RptIdxCfg> subList);

    String getSetIdByModelNm(Map<String, String> dsParams);

    String getColumnIdByColNm(Map<String, String> dsParams);

    void insertRptDesignSourceDs(List<RptDesignSourceDs> subList);

    void insertRptDesignSourceFormula(List<RptDesignSourceFormula> subList);

    void insertRptDesignSourceText(List<RptDesignSourceText> subList);

    void insertRptDesignComCellInfo(List<RptDesignComcellInfo> subList);

    void insertImportDesignInfoLog(Map<String, Object> params);

    List<String> getImportDesignInfoLog(String uuid);

    void cleanImportDesignInfoLog();

    int getTotalImportDesignInfoLogCount();

    List<String> getSysRptVerIds(Map<String, Object> sqlParams);

    List<RptIdxFormulaInfo> getSysRptIdxFormulaInfo(Map<String, Object> sqlParams);

    List<RptIdxFilterInfo> getSysRptIdxFilterInfo(Map<String, Object> sqlParams);

    List<RptIdxSrcRelInfo> getSysRptIdxSrcRelInfo(Map<String, Object> sqlParams);

    List<RptIdxDimRel> getSysRptIdxDimRel(Map<String, Object> sqlParams);

    List<RptIdxMeasureRel> getSysRptIdxMeasureRel(Map<String, Object> sqlParams);

    List<RptIdxInfo> getSysRptIdxInfo(Map<String, Object> sqlParams);

    List<RptDesignSourceIdx> getSysRptDesignSourceIdx(Map<String, Object> sqlParams);

    void updateRptDesignTmpInfo(Map<String, Object> sqlParams);

    void updateRptIdxInfo(Map<String, Object> sqlParams);

    List<RptDesignCellInfo> getSysRptDesignCellInfo(Map<String, Object> sqlParams);

    List<String> getSysMeasureNo(Map<String, String> params);
    
    List<RptIdxInfo> getIdxNoAndNm(Map<String, String> params);

	BioneUserInfo getUserInfo(String userName);

	RptMgrBusiLibInfo getRptMgrBusiLibInfo(String busiLibId);

	BioneParamInfo getBusiInfo(String busiType);
}
