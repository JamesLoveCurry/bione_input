package com.yusys.bione.plugin.regulation.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo;
import com.yusys.bione.plugin.design.entity.*;
import com.yusys.bione.plugin.design.web.vo.ExportReportInfoVO;
import com.yusys.bione.plugin.regulation.enums.*;
import com.yusys.bione.plugin.regulation.repository.RptModelImportDao;
import com.yusys.bione.plugin.regulation.util.VersionDataHandling;
import com.yusys.bione.plugin.regulation.vo.FrsSystemCfgVO;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrFrsExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.valid.repository.FunAndSymbolMybatisDao;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ReflectionUtils;
import com.yusys.bione.plugin.regulation.util.LoadData;
import com.yusys.bione.plugin.regulation.util.VerifyBase;

@Service
@Transactional(readOnly = true)
public class RptModelImportBS extends BaseBS<Object> {

	private static Logger logger = LoggerFactory.getLogger(RptModelImportBS.class);

	@Autowired
	RptModelImportDao rptModelImportDao;

	@Autowired
	RptModeImportInfoBS rptModeImportInfoBS;
	
	@Autowired
	private FunAndSymbolMybatisDao funAndSymbolMybatisDao;
	
	private Map<String, String> static_idxInfo = new HashMap<String, String>(); //?????????????????????????????????

	@Transactional(readOnly = false)
	public void writeToDB(Connection conn, LoadData loadData)
			throws IOException, SQLException {
		ImportRptTool rptTool = ReflectionUtils.newInstance(this.getClass().getClassLoader(),
				"com.yusys.biapp.frs.design.service.ImportRptFrsTool", loadData);
		rptTool.saveRpt();
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param excelFileDir ?????????Excel??????????????????
	 * @param optionMap ????????????
	 * @throws SQLException
	 * @throws IOException
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> exec(String excelFileDir, Map<String, String> optionMap) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
		LoadData loadData = new LoadData(excelFileDir, optionMap);
		try {
			Connection conn = this.jdbcBaseDAO.getCon();
			loadData.initDictionary(conn);
			loadData.load(conn);
			logger.debug("Finish load template info from imported excel file");
			VerifyBase verify = ReflectionUtils.newInstance(this.getClass().getClassLoader(),
					"com.yusys.bione.plugin.regulation.util.Verify", loadData);
			verify.verifyData();
			logger.debug("Finish verify template info from imported excel file");
			if (loadData.getErrorCount() == 0) {
				writeToDB(conn, loadData);
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(loadData);
		}
		returnMap.put("error", loadData.getErrorMessage());
		return returnMap;
	}

	/**
	 * ??????????????????
	 *
	 * @param absolutePath ????????????
	 * @param optionMap    ????????????
	 **/
	@Transactional(rollbackFor = SQLException.class)
	public Map<String, Object> importNewVersion(String absolutePath, Map<String, String> optionMap, String fileName) throws IOException, SQLException {
		Map<String, Object> returnMap = new HashMap<>();
		//????????????
		VersionDataHandling versionDataHandling = new VersionDataHandling(absolutePath, optionMap);
		int sort = 2;
		try {
			rptModeImportInfoBS.insertImportDesignInfoLog(sort, optionMap.get("uuid"), "????????????????????????????????????????????????...");
			sort++;
			Connection connection = this.jdbcBaseDAO.getCon();
			versionDataHandling.loadData();
			rptModeImportInfoBS.insertImportDesignInfoLog(sort, optionMap.get("uuid"), "?????????????????????????????????????????????...");
			sort++;
			//????????????
			versionDataHandling.verifyData(connection);
			if (versionDataHandling.getErrorCount() == 0) {
				//??????????????????
				rptModeImportInfoBS.insertImportDesignInfoLog(sort, optionMap.get("uuid"), "???????????????????????????????????????????????????...");
				sort++;
				List<Map<String, Object>> rptInfos = prepareData(versionDataHandling.getParams(), optionMap.get("fromSystemRptCfg"), fileName);
				//????????????
				rptModeImportInfoBS.insertImportDesignInfoLog(sort, optionMap.get("uuid"), "???????????????????????????????????????????????????...");
				sort++;
				clearData(versionDataHandling.getParams());
				//????????????
				rptModeImportInfoBS.insertImportDesignInfoLog(sort, optionMap.get("uuid"), "?????????????????????????????????????????????...");
				sort++;
				insertData(rptInfos);
			}
		} catch (Exception e) {
			rptModeImportInfoBS.insertImportDesignInfoLog(sort, optionMap.get("uuid"), "???????????????????????????????????????");
			throw e;
		} finally {
			IOUtils.closeQuietly(versionDataHandling);
		}
		returnMap.put("error", versionDataHandling.getErrorMessage());
		//????????????
		this.static_idxInfo = new HashMap<String, String>();
		
		return returnMap;
	}

	/**
	 * ????????????
	 *
	 * @param params           ????????????
	 * @param fromSystemRptCfg ????????????????????????????????????
	 **/
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = SQLException.class)
	public List<Map<String, Object>> prepareData(Map<String, Object> params, String fromSystemRptCfg, String fileName) {
		logger.info("??????????????????");
		boolean isFromSystemRptCfg = StringUtils.isNotEmpty(fromSystemRptCfg);
		List<ExportReportInfoVO> exportReportInfoVOList = (List<ExportReportInfoVO>) params.get("exportReportInfoVOList");
		List<Map<String, Map<String, String>>> rptList = (List<Map<String, Map<String, String>>>) params.get("rptList");
		Map<Integer, Map<String, List<String>>> rptCellSide = (Map<Integer, Map<String, List<String>>>) params.get("rptCellSide");
		String excelFileDir = (String) params.get("excelFileDir");
		File[] excelFiles = (File[]) params.get("excelFiles");
		List<Map<String, Object>> rptInfos = new ArrayList<>();
		for (int i = 0; i < rptList.size(); i++) {
			Map<String, Object> rptConfigs = new HashMap<>();
			//?????????????????? rpt_mgr_report_info
			RptMgrReportInfo rptMgrReportInfo = rptModelImportDao.getSysReportInfo(exportReportInfoVOList.get(i).getRptNum());
			if (rptMgrReportInfo == null) {
				rptMgrReportInfo = new RptMgrReportInfo();
				rptMgrReportInfo.setRptId(RandomUtils.uuid2());
				rptMgrReportInfo.setCfgId(RandomUtils.uuid2());
				List<RptMgrReportCatalog> rptMgrReportCatalog = null;
				Map<String, Object> map = new HashMap<String, Object>();
				String[] catalogNms = exportReportInfoVOList.get(i).getCatalogNm().split("/");
				if(null != catalogNms && catalogNms.length > 1){
					String catalogNm = catalogNms[catalogNms.length - 1];
					String upCatalogNm = catalogNms[catalogNms.length - 2];
					map.put("catalogNm", catalogNm);
					map.put("upCatalogNm", upCatalogNm);
					rptMgrReportCatalog = rptModelImportDao.getSysRptMgrReportCatalog(map);
				}else if(catalogNms.length == 1){
					map.put("catalogNm", catalogNms[0]);
					map.put("upCatalogId", "0");
					rptMgrReportCatalog = rptModelImportDao.getSysRptMgrReportCatalog(map);
				}
				rptMgrReportInfo.setCatalogId(rptMgrReportCatalog != null && rptMgrReportCatalog.size() > 0 ? rptMgrReportCatalog.get(0).getCatalogId() : "0");
				rptMgrReportInfo.setExtType(GlobalConstants4plugin.RPT_EXT_TYPE_FRS);
				rptMgrReportInfo.setRptType(GlobalConstants4plugin.RPT_TYPE_DESIGN);
				rptMgrReportInfo.setDefSrc("01");
			}
			if(StringUtils.isNotBlank(exportReportInfoVOList.get(i).getBusiType())){
				BioneParamInfo busiInfo = rptModelImportDao.getBusiInfo(exportReportInfoVOList.get(i).getBusiType());
				rptMgrReportInfo.setBusiType(busiInfo.getParamValue());
			}
			Map<String, Object> sqlParams = new HashMap<>();
			sqlParams.put("verId", exportReportInfoVOList.get(i).getVerId());
			sqlParams.put("busiType", rptMgrReportInfo.getBusiType());
			FrsSystemCfgVO frsSystemCfgVO = rptModelImportDao.getFrsSystemCfg(sqlParams);
			rptMgrReportInfo.setStartDate(frsSystemCfgVO.getVerStartDate());
			rptMgrReportInfo.setRptNum(exportReportInfoVOList.get(i).getRptNum());
			rptMgrReportInfo.setRptNm(exportReportInfoVOList.get(i).getRptNm());
			rptMgrReportInfo.setRptCycle(exportReportInfoVOList.get(i).getRptCycle());
			rptMgrReportInfo.setRptSts(exportReportInfoVOList.get(i).getRptSts());
			rptMgrReportInfo.setRptBusiNm(exportReportInfoVOList.get(i).getRptBusiNm());
			rptMgrReportInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
			if(StringUtils.isNotBlank(exportReportInfoVOList.get(i).getUserName())){
				BioneUserInfo userInfo = rptModelImportDao.getUserInfo(exportReportInfoVOList.get(i).getUserName());
				rptMgrReportInfo.setDefUser(userInfo.getUserId());
			}
			rptMgrReportInfo.setRptDesc(exportReportInfoVOList.get(i).getRptDesc());
			if(StringUtils.isNotBlank(exportReportInfoVOList.get(i).getBusiLibId())){
				RptMgrBusiLibInfo libInfo = rptModelImportDao.getRptMgrBusiLibInfo(exportReportInfoVOList.get(i).getBusiLibId());
				rptMgrReportInfo.setBusiLibId(libInfo.getBusiLibId());
			}
			//?????????????????? rpt_mgr_frs_ext
			RptMgrFrsExt rptMgrFrsExt = rptModelImportDao.getSysRptMgrFrsExt(rptMgrReportInfo.getRptId());
			if (rptMgrFrsExt == null) {
				rptMgrFrsExt = new RptMgrFrsExt();
			}
			rptMgrFrsExt.setRptId(rptMgrReportInfo.getRptId());
			rptMgrFrsExt.setBusiType(rptMgrReportInfo.getBusiType());
			rptMgrFrsExt.setTmpVersionId(exportReportInfoVOList.get(i).getTmpVersionId());
			rptMgrFrsExt.setRepId(exportReportInfoVOList.get(i).getRepId());
			rptMgrFrsExt.setReportCode(exportReportInfoVOList.get(i).getReportCode());
			rptMgrFrsExt.setFillDesc(exportReportInfoVOList.get(i).getFillDesc());
			rptMgrFrsExt.setIsReleaseSubmit("N");
			rptConfigs.put("rpt_mgr_frs_ext", rptMgrFrsExt);

			//?????????????????? rpt_design_tmp_info
			sqlParams.clear();
			sqlParams.put("templateId", rptMgrReportInfo.getCfgId());
			//??????????????????
			List<String> verIds = rptModelImportDao.getSysRptVerIds(sqlParams);
			String endDate = frsSystemCfgVO.getVerEndDate();
			if (verIds == null || verIds.size() == 0 || exportReportInfoVOList.get(i).getVerId().compareTo(new BigDecimal(verIds.get(0))) >= 0) {
				endDate = "29991231";
			}

			sqlParams.put("verId", exportReportInfoVOList.get(i).getVerId());
			RptDesignTmpInfo rptDesignTmpInfo = rptModelImportDao.getSysRptDesignTmpInfo(sqlParams);
			if (rptDesignTmpInfo == null) {
				rptDesignTmpInfo = new RptDesignTmpInfo();
				rptDesignTmpInfo.setId(new RptDesignTmpInfoPK());
			}
			rptDesignTmpInfo.getId().setTemplateId(rptMgrReportInfo.getCfgId());
			rptDesignTmpInfo.getId().setVerId(exportReportInfoVOList.get(i).getVerId());
			rptDesignTmpInfo.setTemplateContentjson(rptList.get(i).get("rptInfo").get("sheetJson"));
			rptDesignTmpInfo.setTemplateType(exportReportInfoVOList.get(i).getTemplateType());
			rptDesignTmpInfo.setTemplateNm(exportReportInfoVOList.get(i).getTemplateNm());
			rptDesignTmpInfo.setVerEndDate(endDate);
			rptDesignTmpInfo.setVerStartDate(frsSystemCfgVO.getVerStartDate());
			rptDesignTmpInfo.setFixedLength(exportReportInfoVOList.get(i).getFixedLength());
			rptDesignTmpInfo.setIsPaging(exportReportInfoVOList.get(i).getIsPaging());
			rptDesignTmpInfo.setImportConfig(exportReportInfoVOList.get(i).getImportConfig());
			rptDesignTmpInfo.setSortSql(exportReportInfoVOList.get(i).getSortSql());
			rptDesignTmpInfo.setTemplateUnit(exportReportInfoVOList.get(i).getTemplateUnit());
			rptDesignTmpInfo.setIsUpt(exportReportInfoVOList.get(i).getIsUpt());
			rptDesignTmpInfo.setParentTemplateId("0");
			rptConfigs.put("rpt_design_tmp_info", rptDesignTmpInfo);

			rptMgrReportInfo.setEndDate(endDate);
			rptConfigs.put("rpt_mgr_report_info", rptMgrReportInfo);

			cacheIdxInfoMap();
			
			prepareIdxData(rptList.get(i), rptCellSide.get(i), exportReportInfoVOList.get(i), rptMgrReportInfo, rptConfigs, rptList, exportReportInfoVOList, excelFileDir, excelFiles[i]);
			//????????????????????????????????????
			if (isFromSystemRptCfg) {
				prepareSrcIdxData(exportReportInfoVOList.get(i), rptMgrReportInfo, rptConfigs);
			}

			rptInfos.add(rptConfigs);
		}
		return rptInfos;
	}

	/**
	 * ?????????????????????????????????
	 */
	private void cacheIdxInfoMap() {
		Map<String, String> params = new HashMap<>();
		params.put("endDate", "29991231");
		params.put("isRptIndex", "N");
		List<RptIdxInfo> idxList = rptModelImportDao.getIdxNoAndNm(params);
		if(idxList != null) {
			for(RptIdxInfo idx : idxList) {
				this.static_idxInfo.put(idx.getIndexNm(), idx.getId().getIndexNo());
			}
		}
	}
	
	/**
	 * ?????????????????????????????????
	 *
	 * @param rptMgrReportInfo   ????????????
	 * @param rptConfigs         ????????????????????????
	 * @param exportReportInfoVO Excel???????????????
	 **/
	private void prepareSrcIdxData(ExportReportInfoVO exportReportInfoVO, RptMgrReportInfo rptMgrReportInfo, Map<String, Object> rptConfigs) {
		Map<String, Object> sqlParams = new HashMap<>();
		//?????????????????????
		sqlParams.put("templateId", rptMgrReportInfo.getCfgId());
		//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		List<String> verIds = rptModelImportDao.getSysRptVerIds(sqlParams);
		if (verIds.contains(exportReportInfoVO.getVerId().toString())) {
			sqlParams.put("verId", exportReportInfoVO.getVerId());
		} else {
			sqlParams.put("verId", verIds.get(0));
		}
		prepareSrcRptDesignSourceIdx(rptConfigs, sqlParams, exportReportInfoVO.getVerId());
		prepareSrcRptIdxInfo(rptConfigs, sqlParams, exportReportInfoVO.getVerId());
		//prepareSrcRptIdxMeasureRel(rptConfigs, sqlParams, exportReportInfoVO.getVerId());
		//prepareSrcRptIdxDimRel(rptConfigs, sqlParams, exportReportInfoVO.getVerId());
		prepareSrcRptIdxSrcRelInfo(rptConfigs, sqlParams, exportReportInfoVO.getVerId());
		prepareSrcRptIdxFilterInfo(rptConfigs, sqlParams, exportReportInfoVO.getVerId());
		prepareSrcRptIdxFormulaInfo(rptConfigs, sqlParams, exportReportInfoVO.getVerId());

	}

	/**
	 * RptIdxFormulaInfo
	 *
	 * @param sqlParams  ????????????
	 * @param rptConfigs ????????????????????????
	 * @param verId      ????????????
	 **/
	@SuppressWarnings("unchecked")
	private void prepareSrcRptIdxFormulaInfo(Map<String, Object> rptConfigs, Map<String, Object> sqlParams, BigDecimal verId) {
		logger.info("??????RptIdxFormulaInfo");
		//rpt_idx_formula_info
		List<RptIdxFormulaInfo> rptIdxFormulaInfoList = (List<RptIdxFormulaInfo>) rptConfigs.get("rpt_idx_formula_info");
		List<RptIdxFormulaInfo> sysRptIdxFormulaInfos = rptModelImportDao.getSysRptIdxFormulaInfo(sqlParams);
		List<RptIdxFormulaInfo> outRptIdxFormulaInfos = new ArrayList<>();
		//??????????????????
		if (rptIdxFormulaInfoList == null || rptIdxFormulaInfoList.size() == 0) {
			rptIdxFormulaInfoList = sysRptIdxFormulaInfos;
		}
		if (sysRptIdxFormulaInfos == null || sysRptIdxFormulaInfos.size() == 0) {
			rptConfigs.put("rpt_idx_formula_info", rptIdxFormulaInfoList);
			return;
		}
		for (RptIdxFormulaInfo rptIdxFormulaInfo : rptIdxFormulaInfoList) {
			boolean notIn = true;
			for (RptIdxFormulaInfo sysRptIdxFormulaInfo : sysRptIdxFormulaInfos) {
				if (rptIdxFormulaInfo.getId().getIndexNo().equals(sysRptIdxFormulaInfo.getId().getIndexNo())) {
					notIn = false;
					sysRptIdxFormulaInfo.getId().setIndexVerId(verId.longValue());
					outRptIdxFormulaInfos.add(sysRptIdxFormulaInfo);
				}
			}
			if (notIn) {
				outRptIdxFormulaInfos.add(rptIdxFormulaInfo);
			}
		}
		rptConfigs.put("rpt_idx_formula_info", outRptIdxFormulaInfos);
	}

	/**
	 * RptIdxFilterInfo
	 *
	 * @param sqlParams  ????????????
	 * @param rptConfigs ????????????????????????
	 * @param verId      ????????????
	 **/
	@SuppressWarnings("unchecked")
	private void prepareSrcRptIdxFilterInfo(Map<String, Object> rptConfigs, Map<String, Object> sqlParams, BigDecimal verId) {
		logger.info("??????RptIdxFilterInfo");
		List<RptIdxFilterInfo> rptIdxFilterInfoList = (List<RptIdxFilterInfo>) rptConfigs.get("rpt_idx_filter_info");
		List<RptIdxFilterInfo> sysRptIdxFilterInfos = rptModelImportDao.getSysRptIdxFilterInfo(sqlParams);
		List<RptIdxFilterInfo> outInRptIdxFilterInfos = new ArrayList<>();
		//??????????????????
		if (rptIdxFilterInfoList == null || rptIdxFilterInfoList.size() == 0) {
			rptIdxFilterInfoList = sysRptIdxFilterInfos;
		}
		if (sysRptIdxFilterInfos == null || sysRptIdxFilterInfos.size() == 0) {
			rptConfigs.put("rpt_idx_filter_info", rptIdxFilterInfoList);
			return;
		}
		List<RptIdxFilterInfoPK> exitList = new ArrayList<>();
		for (RptIdxFilterInfo rptIdxFilterInfo : rptIdxFilterInfoList) {
			boolean notIn = true;
			for (RptIdxFilterInfo sysRptIdxFilterInfo : sysRptIdxFilterInfos) {
				if (rptIdxFilterInfo.getId().getIndexNo().equals(sysRptIdxFilterInfo.getId().getIndexNo())) {
					notIn = false;
					sysRptIdxFilterInfo.getId().setIndexVerId(verId.longValue());
					if (!exitList.contains(sysRptIdxFilterInfo.getId())){
						outInRptIdxFilterInfos.add(sysRptIdxFilterInfo);
						exitList.add(sysRptIdxFilterInfo.getId());
					}
				}
			}
			if (notIn) {
				outInRptIdxFilterInfos.add(rptIdxFilterInfo);
			}
		}
		rptConfigs.put("rpt_idx_filter_info", outInRptIdxFilterInfos);
	}

	/**
	 * RptIdxSrcRelInfo
	 *
	 * @param sqlParams  ????????????
	 * @param rptConfigs ????????????????????????
	 * @param verId      ????????????
	 **/
	@SuppressWarnings("unchecked")
	private void prepareSrcRptIdxSrcRelInfo(Map<String, Object> rptConfigs, Map<String, Object> sqlParams, BigDecimal verId) {
		logger.info("??????RptIdxSrcRelInfo");
		List<RptIdxSrcRelInfo> rptIdxSrcRelInfoList = (List<RptIdxSrcRelInfo>) rptConfigs.get("rpt_idx_src_rel_info");
		List<RptIdxSrcRelInfo> sysRptIdxSrcRelInfos = rptModelImportDao.getSysRptIdxSrcRelInfo(sqlParams);
		List<RptIdxSrcRelInfo> outRptIdxSrcRelInfos = new ArrayList<>();
		//??????????????????
		if (rptIdxSrcRelInfoList == null || rptIdxSrcRelInfoList.size() == 0) {
			rptIdxSrcRelInfoList = sysRptIdxSrcRelInfos;
		}
		if (sysRptIdxSrcRelInfos == null || sysRptIdxSrcRelInfos.size() == 0) {
			rptConfigs.put("rpt_idx_src_rel_info", rptIdxSrcRelInfoList);
			return;
		}
		List<RptIdxSrcRelInfoPK> exitList = new ArrayList<>();
		for (RptIdxSrcRelInfo rptIdxSrcRelInfo : rptIdxSrcRelInfoList) {
			boolean notIn = true;
			for (RptIdxSrcRelInfo sysRptIdxSrcRelInfo : sysRptIdxSrcRelInfos) {
				if (rptIdxSrcRelInfo.getId().getIndexNo().equals(sysRptIdxSrcRelInfo.getId().getIndexNo())) {
					notIn = false;
					sysRptIdxSrcRelInfo.getId().setIndexVerId(verId.longValue());
					if (!exitList.contains(sysRptIdxSrcRelInfo.getId())){
						outRptIdxSrcRelInfos.add(sysRptIdxSrcRelInfo);
						exitList.add(sysRptIdxSrcRelInfo.getId());
					}
				}
			}
			if (notIn) {
				outRptIdxSrcRelInfos.add(rptIdxSrcRelInfo);
			}
		}
		rptConfigs.put("rpt_idx_src_rel_info", outRptIdxSrcRelInfos);
	}

	/**
	 * RptIdxDimRel
	 *
	 * @param sqlParams  ????????????
	 * @param rptConfigs ????????????????????????
	 * @param verId      ????????????
	 **/
	@SuppressWarnings({ "unchecked", "unused" })
	private void prepareSrcRptIdxDimRel(Map<String, Object> rptConfigs, Map<String, Object> sqlParams, BigDecimal verId) {
		logger.info("??????RptIdxDimRel");
		List<RptIdxDimRel> rptIdxDimRelList = (List<RptIdxDimRel>) rptConfigs.get("rpt_idx_dim_rel");
		List<RptIdxDimRel> sysRptIdxDimRels = rptModelImportDao.getSysRptIdxDimRel(sqlParams);
		List<RptIdxDimRel> outRptIdxDimRels = new ArrayList<>();
		//??????????????????
		if (rptIdxDimRelList == null || rptIdxDimRelList.size() == 0) {
			rptIdxDimRelList = sysRptIdxDimRels;
		}
		if (sysRptIdxDimRels == null || sysRptIdxDimRels.size() == 0) {
			rptConfigs.put("rpt_idx_dim_rel", rptIdxDimRelList);
			return;
		}
		List<RptIdxDimRelPK> exitList = new ArrayList<>();
		for (RptIdxDimRel rptIdxDimRel : rptIdxDimRelList) {
			boolean notIn = true;
			for (RptIdxDimRel sysRptIdxDimRel : sysRptIdxDimRels) {
				if (rptIdxDimRel.getId().getIndexNo().equals(sysRptIdxDimRel.getId().getIndexNo())) {
					notIn = false;
					sysRptIdxDimRel.getId().setIndexVerId(verId.longValue());
					//??????????????????????????????
					if (!exitList.contains(sysRptIdxDimRel.getId())){
						outRptIdxDimRels.add(sysRptIdxDimRel);
						exitList.add(sysRptIdxDimRel.getId());
					}
				}
			}
			if (notIn) {
				outRptIdxDimRels.add(rptIdxDimRel);
			}
		}
		rptConfigs.put("rpt_idx_dim_rel", outRptIdxDimRels);
	}

	/**
	 * RptIdxMeasureRel
	 *
	 * @param sqlParams  ????????????
	 * @param rptConfigs ????????????????????????
	 * @param verId      ????????????
	 **/
	@SuppressWarnings({ "unused", "unchecked" })
	private void prepareSrcRptIdxMeasureRel(Map<String, Object> rptConfigs, Map<String, Object> sqlParams, BigDecimal verId) {
		logger.info("??????RptIdxMeasureRel");
		List<RptIdxMeasureRel> rptIdxMeasureRelList = (List<RptIdxMeasureRel>) rptConfigs.get("rpt_idx_measure_rel");
		List<RptIdxMeasureRel> sysRptIdxMeasureRels = rptModelImportDao.getSysRptIdxMeasureRel(sqlParams);
		List<RptIdxMeasureRel> outRptIdxMeasureRels = new ArrayList<>();
		//??????????????????
		if (rptIdxMeasureRelList == null || rptIdxMeasureRelList.size() == 0) {
			rptIdxMeasureRelList = sysRptIdxMeasureRels;
		}
		if (sysRptIdxMeasureRels == null || sysRptIdxMeasureRels.size() == 0) {
			rptConfigs.put("rpt_idx_measure_rel", rptIdxMeasureRelList);
			return;
		}
		List<RptIdxMeasureRelPK> exitList = new ArrayList<>();
		for (RptIdxMeasureRel rptIdxMeasureRel : rptIdxMeasureRelList) {
			boolean notIn = true;
			for (RptIdxMeasureRel sysRptIdxMeasureRel : sysRptIdxMeasureRels) {
				if (rptIdxMeasureRel.getId().getIndexNo().equals(sysRptIdxMeasureRel.getId().getIndexNo())) {
					notIn = false;
					sysRptIdxMeasureRel.getId().setIndexVerId(verId.longValue());
					if (!exitList.contains(sysRptIdxMeasureRel.getId())){
						outRptIdxMeasureRels.add(sysRptIdxMeasureRel);
						exitList.add(sysRptIdxMeasureRel.getId());
					}
				}
			}
			if (notIn) {
				outRptIdxMeasureRels.add(rptIdxMeasureRel);
			}
		}
		rptConfigs.put("rpt_idx_measure_rel", outRptIdxMeasureRels);
	}

	/**
	 * RptIdxInfo
	 *
	 * @param sqlParams  ????????????
	 * @param rptConfigs ????????????????????????
	 * @param verId      ????????????
	 **/
	@SuppressWarnings("unchecked")
	private void prepareSrcRptIdxInfo(Map<String, Object> rptConfigs, Map<String, Object> sqlParams, BigDecimal verId) {
		logger.info("??????RptIdxInfo");
		List<RptIdxInfo> rptIdxInfoList = (List<RptIdxInfo>) rptConfigs.get("rpt_idx_info");
		List<RptIdxInfo> sysRptIdxInfoList = rptModelImportDao.getSysRptIdxInfo(sqlParams);
		List<RptIdxInfo> outRptIdxInfos = new ArrayList<>();
		//??????????????????
		if (rptIdxInfoList == null || rptIdxInfoList.size() == 0) {
			rptIdxInfoList = sysRptIdxInfoList;
		}
		if (sysRptIdxInfoList == null || sysRptIdxInfoList.size() == 0) {
			rptConfigs.put("rpt_idx_info", rptIdxInfoList);
			return;
		}
		for (RptIdxInfo rptIdxInfo : rptIdxInfoList) {
			boolean notIn = true;
			for (RptIdxInfo sysRptIdxInfo : sysRptIdxInfoList) {
				if (rptIdxInfo.getId().getIndexNo().equals(sysRptIdxInfo.getId().getIndexNo())) {
					notIn = false;
					sysRptIdxInfo.getId().setIndexVerId(verId.longValue());
					sysRptIdxInfo.setIndexNm(rptIdxInfo.getIndexNm());
					outRptIdxInfos.add(sysRptIdxInfo);
				}
			}
			if (notIn) {
				outRptIdxInfos.add(rptIdxInfo);
			}
		}
		rptConfigs.put("rpt_idx_info", outRptIdxInfos);
	}

	/**
	 * RptDesignSourceIdx && RptDesignCellinfo
	 *
	 * @param sqlParams  ????????????
	 * @param rptConfigs ????????????????????????
	 * @param verId      ????????????
	 **/
	@SuppressWarnings("unchecked")
	private void prepareSrcRptDesignSourceIdx(Map<String, Object> rptConfigs, Map<String, Object> sqlParams, BigDecimal verId) {
		logger.info("??????RptDesignSourceIdx???RptDesignCellinfo");
		List<RptDesignSourceIdx> rptDesignSourceIdxList = (List<RptDesignSourceIdx>) rptConfigs.get("rpt_design_source_idx");
		List<RptDesignSourceIdx> sysRptDesignSourceIdxList = rptModelImportDao.getSysRptDesignSourceIdx(sqlParams);
		List<RptDesignSourceIdx> outRptDesignSourceIdxs = new ArrayList<>();
		List<RptDesignCellInfo> rptDesignCellInfoList = (List<RptDesignCellInfo>) rptConfigs.get("rpt_design_cell_info");
		List<RptDesignCellInfo> sysRptDesignCellInfoList = rptModelImportDao.getSysRptDesignCellInfo(sqlParams);
		List<RptDesignCellInfo> outRptDesignCellInfo = new ArrayList<>();
		//??????????????????
		if (rptDesignSourceIdxList == null || rptDesignSourceIdxList.size() == 0) {
			rptDesignSourceIdxList = sysRptDesignSourceIdxList;
		}
		if (sysRptDesignSourceIdxList == null || sysRptDesignSourceIdxList.size() == 0) {
			rptConfigs.put("rpt_design_source_idx", rptDesignSourceIdxList);
			return;
		}
		if (rptDesignCellInfoList == null || rptDesignCellInfoList.size() == 0) {
			rptDesignCellInfoList = sysRptDesignCellInfoList;
		}
		for (RptDesignSourceIdx rptDesignSourceIdx : rptDesignSourceIdxList) {
			boolean notIn = true;
			for (RptDesignSourceIdx sysRptDesignSourceIdx : sysRptDesignSourceIdxList) {
				if (rptDesignSourceIdx.getIndexNo().equals(sysRptDesignSourceIdx.getIndexNo())) {
					notIn = false;
					for (RptDesignCellInfo sysRptDesignCellInfo : sysRptDesignCellInfoList) {
						if (sysRptDesignCellInfo.getId().getCellNo().equals(sysRptDesignSourceIdx.getId().getCellNo())) {
							RptDesignCellInfo outCell = new RptDesignCellInfo();
							BeanUtils.copy(sysRptDesignCellInfo, outCell);
							outCell.getId().setCellNo(rptDesignSourceIdx.getId().getCellNo());
							outCell.getId().setVerId(verId);
							int[] rowAndCol = ExcelAnalyseUtils.getRowNoColumnIdx(outCell.getId().getCellNo());
							if (rowAndCol != null && rowAndCol.length == 2) {
								outCell.setRowId(BigDecimal.valueOf(rowAndCol[0]));
								outCell.setColId(BigDecimal.valueOf(rowAndCol[1]));
							}
							outRptDesignCellInfo.add(outCell);
						}
					}
					//??????????????????
					RptDesignSourceIdx outIdx = new RptDesignSourceIdx();
					BeanUtils.copy(sysRptDesignSourceIdx, outIdx);
					outIdx.getId().setCellNo(rptDesignSourceIdx.getId().getCellNo());
					outIdx.getId().setVerId(verId);
					outRptDesignSourceIdxs.add(outIdx);
				}
			}
			if (notIn) {
				outRptDesignSourceIdxs.add(rptDesignSourceIdx);
				for (RptDesignCellInfo rptDesignCellInfo : rptDesignCellInfoList) {
					if (rptDesignCellInfo.getId().getCellNo().equals(rptDesignSourceIdx.getId().getCellNo())) {
						outRptDesignCellInfo.add(rptDesignCellInfo);
					}
				}
			}
		}
		List<RptDesignCellInfo> notInList = new ArrayList<>();
		for (RptDesignCellInfo rptDesignCellInfo : rptDesignCellInfoList) {
			boolean flag = true;
			for (RptDesignCellInfo outCell : outRptDesignCellInfo) {
				if (outCell.getId().getCellNo().equals(rptDesignCellInfo.getId().getCellNo())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				notInList.add(rptDesignCellInfo);
			}
		}

		outRptDesignCellInfo.addAll(notInList);
		rptConfigs.put("rpt_design_source_idx", outRptDesignSourceIdxs);
		rptConfigs.put("rpt_design_cell_info", outRptDesignCellInfo);
	}

	/**
	 * ??????????????????
	 *
	 * @param i                      ??????
	 * @param exportReportInfoVOList ????????????????????????
	 * @param frsSystemCfgVO         ??????????????????
	 * @param rptCellSide            ??????Excel?????????????????????
	 * @param rptConfigs             ??????????????????
	 * @param rptList
	 * @param rptMgrReportInfo
	 **/
	@SuppressWarnings("serial")
	private void prepareIdxData(Map<String, Map<String, String>> rptInfo, Map<String, List<String>> cellSide,
								ExportReportInfoVO exportReportInfoVO, RptMgrReportInfo rptMgrReportInfo, Map<String, Object> rptConfigs,
								List<Map<String, Map<String, String>>> rptList, List<ExportReportInfoVO> exportReportInfoVOList, String excelFileDir, File excelFiles) {
		FormulaParsingWorkbook workbookWrapper = null;
		Sheet sheet = null;
		try {
			String file = excelFileDir + "/" + excelFiles.getName();
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			//SXSSFWorkbook swb = new SXSSFWorkbook(workbook,100);
			//XSSFWorkbook xssfWorkbook = swb.getXSSFWorkbook();
			workbookWrapper = XSSFEvaluationWorkbook.create(workbook);
			sheet = workbook.getSheet("2-????????????");
			workbook.close();
		} catch (IOException e) {
			logger.error(e.toString());
		}

		Map<String, String> rptCellNm = rptInfo.get("rptCellNm");
		Map<String, String> idxSumUpt = rptInfo.get("idxSumUpt");
		Map<String, String> cellDisplayUnit = rptInfo.get("cellDisplayUnit");
		Map<String, String> rptIdx = rptInfo.get("rptIdx");
		Map<String, String> rptSource = rptInfo.get("rptSource");
		Map<String, String> rptFilter = rptInfo.get("rptFilter");
		Map<String, String> detailed = rptInfo.get("detailed");
		Map<String, String> excelFormula = rptInfo.get("excelFormula");
		Map<String, String> cellCaliberExplain = rptInfo.get("cellCaliberExplain");
		Map<String, String> cellCaliberTechnology = rptInfo.get("cellCaliberTechnology");
		Map<String, String> rptBusiNo = rptInfo.get("rptBusiNo");
		List<String> allCells = cellSide.get("allCells");

		//rpt_design_cell_info
		List<RptDesignCellInfo> rptDesignCellInfoList = new ArrayList<>();
		//rpt_design_source_idx
		List<RptDesignSourceIdx> rptDesignSourceIdxList = new ArrayList<>();
		//rpt_idx_info
		List<RptIdxInfo> rptIdxInfoList = new ArrayList<>();
		//rpt_idx_measure_rel
		List<RptIdxMeasureRel> rptIdxMeasureRelList = new ArrayList<>();
		//rpt_idx_dim_rel
		List<RptIdxDimRel> rptIdxDimRelList = new ArrayList<>();
		//rpt_idx_src_rel_info
		List<RptIdxSrcRelInfo> rptIdxSrcRelInfoList = new ArrayList<>();
		//rpt_idx_filter_info
		List<RptIdxFilterInfo> rptIdxFilterInfoList = new ArrayList<>();
		//rpt_idx_formula_info
		List<RptIdxFormulaInfo> rptIdxFormulaInfoList = new ArrayList<>();
		//rpt_idx_cfg
		List<RptIdxCfg> rptIdxCfgList = new ArrayList<>();
		//rpt_design_source_ds
		List<RptDesignSourceDs> rptDesignSourceDsList = new ArrayList<>();
		//rpt_design_source_formula
		List<RptDesignSourceFormula> rptDesignSourceFormulaList = new ArrayList<>();
		//rpt_design_source_text
		List<RptDesignSourceText> rptDesignSourceTextList = new ArrayList<>();
		//rpt_design_comcell_info
		List<RptDesignComcellInfo> rptDesignComcellInfoList = new ArrayList<>();
		for (String cellKey : allCells) {
			String cellNm = null;//???????????????
			BigDecimal verId = exportReportInfoVO.getVerId();//????????????
			//rpt_design_cell_info
			if (!cellSide.get("comCells").contains(cellKey)) {
				RptDesignCellInfo rptDesignCellInfo = new RptDesignCellInfo();
				RptDesignCellInfoPK rptDesignCellInfoPK = new RptDesignCellInfoPK();
				rptDesignCellInfoPK.setCellNo(cellKey);
				rptDesignCellInfoPK.setTemplateId(rptMgrReportInfo.getCfgId());
				rptDesignCellInfoPK.setVerId(verId);
				rptDesignCellInfo.setId(rptDesignCellInfoPK);
				if (StringUtils.isNotEmpty(rptCellNm.get(cellKey))) {
					rptDesignCellInfo.setCellNm(rptCellNm.get(cellKey));
					cellNm = rptCellNm.get(cellKey);
				} else {
					rptDesignCellInfo.setCellNm(cellKey);
					cellNm = cellKey;
				}
				rptDesignCellInfo.setIsNull("Y");
				rptDesignCellInfo.setIsUpt("Y");
				int[] rowAndColNo = ExcelAnalyseUtils.getRowNoColumnIdx(cellKey);
				if (rowAndColNo != null && rowAndColNo.length == 2) {
					rptDesignCellInfo.setRowId(BigDecimal.valueOf(rowAndColNo[0]));
					rptDesignCellInfo.setColId(BigDecimal.valueOf(rowAndColNo[1]));
				}
				if (cellSide.get("idxCells").contains(cellKey)) {
					if (StringUtils.isNotEmpty(idxSumUpt.get(cellKey))) {
						rptDesignCellInfo.setIsUpt(VersionDataHandling.transCode(idxSumUpt.get(cellKey).split("&&")[2]));
					}
					if (StringUtils.isNotEmpty(cellDisplayUnit.get(cellKey))) {
						rptDesignCellInfo.setDisplayFormat(DisplayFormat.get(cellDisplayUnit.get(cellKey).split("&&")[0]).toString());
						rptDesignCellInfo.setDataUnit(DataUnit.get(cellDisplayUnit.get(cellKey).split("&&")[1]).toString());
						if(StringUtils.isNotBlank(cellDisplayUnit.get(cellKey).split("&&")[2]) && StringUtils.isNumeric(cellDisplayUnit.get(cellKey).split("&&")[2])){
							rptDesignCellInfo.setDataPrecision(new BigDecimal(cellDisplayUnit.get(cellKey).split("&&")[2]));
						}
					}
					rptDesignCellInfo.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_IDX);
					rptDesignCellInfo.setCaliberExplain(cellCaliberExplain.get(cellKey));
					rptDesignCellInfo.setCaliberTechnology(cellCaliberTechnology.get(cellKey));
					if (rptBusiNo.get(cellKey) != null) {
						rptDesignCellInfo.setBusiNo(rptBusiNo.get(cellKey));
					}
				}
				if (cellSide.get("modelCells").contains(cellKey)) {
					if (StringUtils.isNotEmpty(idxSumUpt.get(cellKey))) {
						rptDesignCellInfo.setIsUpt(VersionDataHandling.transCode(idxSumUpt.get(cellKey).split("&&")[2]));
					}
					if (StringUtils.isNotEmpty(cellDisplayUnit.get(cellKey))) {
						rptDesignCellInfo.setDisplayFormat(DisplayFormat.get(cellDisplayUnit.get(cellKey).split("&&")[0]).toString());
						rptDesignCellInfo.setDataUnit(DataUnit.get(cellDisplayUnit.get(cellKey).split("&&")[1]).toString());
						if(StringUtils.isNotBlank(cellDisplayUnit.get(cellKey).split("&&")[2]) && StringUtils.isNumeric(cellDisplayUnit.get(cellKey).split("&&")[2])){
							rptDesignCellInfo.setDataPrecision(new BigDecimal(cellDisplayUnit.get(cellKey).split("&&")[2]));
						}
					}
					rptDesignCellInfo.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE);
					rptDesignCellInfo.setCaliberExplain(cellCaliberExplain.get(cellKey));
					rptDesignCellInfo.setCaliberTechnology(cellCaliberTechnology.get(cellKey));
					//??????
					if ("??????".equals(detailed.get(cellKey).split("&&")[7])) {
						rptDesignCellInfo.setIsMerge("Y");
					} else {
						rptDesignCellInfo.setIsMerge("N");
					}
					//???????????????
					if ("???????????????".equals(detailed.get(cellKey).split("&&")[8])) {
						rptDesignCellInfo.setIsMergeCol("Y");
					} else {
						rptDesignCellInfo.setIsMergeCol("N");
					}
				}
				if (cellSide.get("formulaCells").contains(cellKey)) {
					if (StringUtils.isNotEmpty(idxSumUpt.get(cellKey))) {
						rptDesignCellInfo.setIsUpt(VersionDataHandling.transCode(idxSumUpt.get(cellKey).split("&&")[2]));
					}
					if (StringUtils.isNotEmpty(cellDisplayUnit.get(cellKey))) {
						rptDesignCellInfo.setDisplayFormat(DisplayFormat.get(cellDisplayUnit.get(cellKey).split("&&")[0]).toString());
						rptDesignCellInfo.setDataUnit(DataUnit.get(cellDisplayUnit.get(cellKey).split("&&")[1]).toString());
						if(StringUtils.isNotBlank(cellDisplayUnit.get(cellKey).split("&&")[2]) && StringUtils.isNumeric(cellDisplayUnit.get(cellKey).split("&&")[2])){
							rptDesignCellInfo.setDataPrecision(new BigDecimal(cellDisplayUnit.get(cellKey).split("&&")[2]));
						}
					}
					rptDesignCellInfo.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_RPTCALC);
					rptDesignCellInfo.setCaliberExplain(cellCaliberExplain.get(cellKey));
					rptDesignCellInfo.setCaliberTechnology(cellCaliberTechnology.get(cellKey));
					if (rptBusiNo.get(cellKey) != null) {
						rptDesignCellInfo.setBusiNo(rptBusiNo.get(cellKey));
					}
				}
				if (cellSide.get("excelCells").contains(cellKey)) {
					if (StringUtils.isNotEmpty(idxSumUpt.get(cellKey))) {
						rptDesignCellInfo.setIsUpt(VersionDataHandling.transCode(idxSumUpt.get(cellKey).split("&&")[2]));
					}
					if (StringUtils.isNotEmpty(cellDisplayUnit.get(cellKey))) {
						rptDesignCellInfo.setDisplayFormat(DisplayFormat.get(cellDisplayUnit.get(cellKey).split("&&")[0]).toString());
						rptDesignCellInfo.setDataUnit(DataUnit.get(cellDisplayUnit.get(cellKey).split("&&")[1]).toString());
						if(StringUtils.isNotBlank(cellDisplayUnit.get(cellKey).split("&&")[2]) && StringUtils.isNumeric(cellDisplayUnit.get(cellKey).split("&&")[2])){
							rptDesignCellInfo.setDataPrecision(new BigDecimal(cellDisplayUnit.get(cellKey).split("&&")[2]));
						}
					}
					rptDesignCellInfo.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA);
					rptDesignCellInfo.setCaliberExplain(cellCaliberExplain.get(cellKey));
					rptDesignCellInfo.setCaliberTechnology(cellCaliberTechnology.get(cellKey));
					if (rptBusiNo.get(cellKey) != null) {
						rptDesignCellInfo.setBusiNo(rptBusiNo.get(cellKey));
					}
				}
				if (cellSide.get("staticeCells").contains(cellKey)) {
					rptDesignCellInfo.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT);
				}
				rptDesignCellInfoList.add(rptDesignCellInfo);
			}

			if (cellSide.get("comCells").contains(cellKey)) {
				//rpt_design_comcell_info
				RptDesignComcellInfo rptDesignComcellInfo = new RptDesignComcellInfo();
				RptDesignComcellInfoPK rptDesignComcellInfoPK = new RptDesignComcellInfoPK();
				rptDesignComcellInfoPK.setCellNo(cellKey);
				rptDesignComcellInfoPK.setTemplateId(rptMgrReportInfo.getCfgId());
				rptDesignComcellInfoPK.setVerId(verId);
				rptDesignComcellInfo.setId(rptDesignComcellInfoPK);
				rptDesignComcellInfo.setContent(rptSource.get(cellKey));
				rptDesignComcellInfo.setTypeId("05");
				rptDesignComcellInfo.setColId(BigDecimal.valueOf(ExcelAnalyseUtils.getRowNoColumnIdx(cellKey)[1]));
				rptDesignComcellInfo.setRowId(BigDecimal.valueOf(ExcelAnalyseUtils.getRowNoColumnIdx(cellKey)[0]));
				rptDesignComcellInfoList.add(rptDesignComcellInfo);
			}

			if (cellSide.get("staticeCells").contains(cellKey)) {
				if (StringUtils.isNotEmpty(rptSource.get(cellKey))) {
					//rpt_design_source_text
					RptDesignSourceText rptDesignSourceText = new RptDesignSourceText();
					RptDesignSourceTextPK rptDesignSourceTextPK = new RptDesignSourceTextPK();
					rptDesignSourceTextPK.setCellNo(cellKey);
					rptDesignSourceTextPK.setTemplateId(rptMgrReportInfo.getCfgId());
					rptDesignSourceTextPK.setVerId(verId);
					rptDesignSourceText.setId(rptDesignSourceTextPK);
					rptDesignSourceText.setExpression(rptSource.get(cellKey));
					rptDesignSourceTextList.add(rptDesignSourceText);
				}
			}

			if (cellSide.get("excelCells").contains(cellKey)) {
				//rpt_design_source_formula
				if (StringUtils.isNotEmpty(rptSource.get(cellKey))) {
					RptDesignSourceFormula rptDesignSourceFormula = new RptDesignSourceFormula();
					RptDesignSourceFormulaPK rptDesignSourceFormulaPK = new RptDesignSourceFormulaPK();
					rptDesignSourceFormulaPK.setCellNo(cellKey);
					rptDesignSourceFormulaPK.setTemplateId(rptMgrReportInfo.getCfgId());
					rptDesignSourceFormulaPK.setVerId(verId);
					rptDesignSourceFormula.setId(rptDesignSourceFormulaPK);
					rptDesignSourceFormula.setExcelFormula(rptSource.get(cellKey).substring(1));
					if (StringUtils.isNotEmpty(excelFormula.get(cellKey))) {
						if ("????????????".equals(excelFormula.get(cellKey).split("&&")[0])) {
							rptDesignSourceFormula.setIsRptIndex("Y");
						} else {
							rptDesignSourceFormula.setIsRptIndex("N");
						}
						if ("??????".equals(excelFormula.get(cellKey).split("&&")[1])) {
							rptDesignSourceFormula.setIsAnalyseExt("Y");
							rptDesignSourceFormula.setAnalyseExtType(ExtendType.get(excelFormula.get(cellKey).split("&&")[2]).toString());
						} else {
							rptDesignSourceFormula.setIsAnalyseExt("N");
						}
					}
					rptDesignSourceFormulaList.add(rptDesignSourceFormula);
				}
			}

			if (cellSide.get("modelCells").contains(cellKey)) {
				//rpt_design_source_ds
				if (StringUtils.isNotEmpty(rptSource.get(cellKey))) {
					Map<String, String> dsParams = new HashMap<>();
					String modelNm = rptSource.get(cellKey).split("\\.")[0];
					String colNm = rptSource.get(cellKey).split("\\.")[1];
					dsParams.put("modelNm", modelNm);
					String setId = rptModelImportDao.getSetIdByModelNm(dsParams);
					dsParams.put("setId", setId);
					dsParams.put("colNm", colNm);
					String columnId = rptModelImportDao.getColumnIdByColNm(dsParams);
					RptDesignSourceDs rptDesignSourceDs = new RptDesignSourceDs();
					RptDesignSourceDsPK rptDesignSourceDsPK = new RptDesignSourceDsPK();
					rptDesignSourceDsPK.setCellNo(cellKey);
					rptDesignSourceDsPK.setTemplateId(rptMgrReportInfo.getCfgId());
					rptDesignSourceDsPK.setVerId(verId);
					rptDesignSourceDs.setId(rptDesignSourceDsPK);
					rptDesignSourceDs.setColumnId(columnId);
					rptDesignSourceDs.setDsId(setId);
					if (StringUtils.isNotEmpty(detailed.get(cellKey))) {
						//??????
						if ("??????".equals(detailed.get(cellKey).split("&&")[0])) {
							rptDesignSourceDs.setIsExt("Y");
							rptDesignSourceDs.setExtDirection(ExtendDirection.get(detailed.get(cellKey).split("&&")[1]).toString());
							rptDesignSourceDs.setExtMode(ExtendMode.get(detailed.get(cellKey).split("&&")[2]).toString());
						} else {
							rptDesignSourceDs.setIsExt("N");
						}
						//??????
						if ("??????".equals(detailed.get(cellKey).split("&&")[3])) {
							rptDesignSourceDs.setIsSort("Y");
							rptDesignSourceDs.setSortMode(SortMode.get(detailed.get(cellKey).split("&&")[4]).toString());
							if(StringUtils.isNotBlank(detailed.get(cellKey).split("&&")[5].toString()) && StringUtils.isNumeric(detailed.get(cellKey).split("&&")[5])){
								rptDesignSourceDs.setSortOrder(new BigDecimal(detailed.get(cellKey).split("&&")[5].toString()));
							}
						} else {
							rptDesignSourceDs.setIsSort("N");
						}
						//??????
						if ("??????".equals(detailed.get(cellKey).split("&&")[6])) {
							rptDesignSourceDs.setIsConver("Y");
						} else {
							rptDesignSourceDs.setIsConver("N");
						}
					}
					rptDesignSourceDsList.add(rptDesignSourceDs);
				}
			}

			if (!cellSide.get("comCells").contains(cellKey) && !cellSide.get("staticeCells").contains(cellKey) && !cellSide.get("modelCells").contains(cellKey)) {
				if(cellSide.get("excelCells").contains(cellKey) && ("03".equals(exportReportInfoVO.getTemplateType()) || "01".equals(exportReportInfoVO.getTemplateType())) && "??????????????????".equals(excelFormula.get(cellKey).split("&&")[0])){//????????????/???????????????excel????????????????????????
				}else{
					//rpt_design_source_idx
					RptDesignSourceIdx rptDesignSourceIdx = new RptDesignSourceIdx();
					RptDesignSourceIdxPK rptDesignSourceIdxPK = new RptDesignSourceIdxPK();
					rptDesignSourceIdxPK.setCellNo(cellKey);
					rptDesignSourceIdxPK.setTemplateId(rptMgrReportInfo.getCfgId());
					rptDesignSourceIdxPK.setVerId(verId);
					rptDesignSourceIdx.setId(rptDesignSourceIdxPK);
					rptDesignSourceIdx.setIndexNo(rptIdx.get(cellKey));

					//rpt_idx_info
					RptIdxInfo rptIdxInfo = new RptIdxInfo();
					RptIdxInfoPK rptIdxInfoPK = new RptIdxInfoPK();
					rptIdxInfoPK.setIndexVerId(new Long(verId.toString()));
					rptIdxInfoPK.setIndexNo(rptIdx.get(cellKey));
					rptIdxInfo.setId(rptIdxInfoPK);
					rptIdxInfo.setEndDate(rptMgrReportInfo.getEndDate());
					rptIdxInfo.setStartDate(rptMgrReportInfo.getStartDate());
					rptIdxInfo.setIndexNm(cellNm);
					rptIdxInfo.setTemplateId(rptMgrReportInfo.getCfgId());
					rptIdxInfo.setIndexSts("Y");
					rptIdxInfo.setIsRptIndex("Y");
					rptIdxInfo.setIndexCatalogNo("0");
					rptIdxInfo.setIsSum(cellSide.get("excelCells").contains(cellKey) ? "N" : VersionDataHandling.transCode(idxSumUpt.get(cellKey).split("&&")[0]));
					rptIdxInfo.setIsFillSum(cellSide.get("excelCells").contains(cellKey) ? "N" : VersionDataHandling.transCode(idxSumUpt.get(cellKey).split("&&")[1]));//excel???????????????????????????????????????N
					rptIdxInfo.setBusiType(rptMgrReportInfo.getBusiType());
					rptIdxInfo.setCalcCycle(rptMgrReportInfo.getRptCycle());
					if (StringUtils.isNotEmpty(cellDisplayUnit.get(cellKey))) {
						rptIdxInfo.setDataUnit(DataUnit.get(cellDisplayUnit.get(cellKey).split("&&")[1]).toString());
						if(StringUtils.isNotBlank(cellDisplayUnit.get(cellKey).split("&&")[2]) && StringUtils.isNumeric(cellDisplayUnit.get(cellKey).split("&&")[2])){
							rptIdxInfo.setDataPrecision(new BigDecimal(cellDisplayUnit.get(cellKey).split("&&")[2]));
						}
					}
				
					//rpt_idx_formula_info
					RptIdxFormulaInfo rptIdxFormulaInfo = new RptIdxFormulaInfo();
					RptIdxFormulaInfoPK rptIdxFormulaInfoPK = new RptIdxFormulaInfoPK();
					rptIdxFormulaInfoPK.setIndexVerId(new Long(verId.toString()));
					rptIdxFormulaInfoPK.setIndexNo(rptIdx.get(cellKey));
					rptIdxFormulaInfo.setId(rptIdxFormulaInfoPK);
					
					//rpt_idx_cfg
					RptIdxCfg rptIdxCfg = new RptIdxCfg();
					RptIdxCfgPK rptIdxCfgPK = new RptIdxCfgPK();
					rptIdxCfgPK.setTemplateId(rptMgrReportInfo.getCfgId());
					rptIdxCfgPK.setIndexNo(rptIdx.get(cellKey));
					rptIdxCfgPK.setVerId(verId);
					rptIdxCfgPK.setCellNo(cellKey);
					rptIdxCfg.setId(rptIdxCfgPK);
					rptIdxCfg.setRptNum(rptMgrReportInfo.getRptNum());
					rptIdxCfgList.add(rptIdxCfg);
					
					if (cellSide.get("idxCells") != null && cellSide.get("idxCells").contains(cellKey)) {
						rptDesignSourceIdx.setModeId("1");
						rptDesignSourceIdx.setRuleId("1");
						rptDesignSourceIdx.setTimeMeasureId("1");
						if ("?????????".equals(rptSource.get(cellKey))) {
							rptIdxInfo.setIndexType("06");
						} else {
							rptIdxInfo.setIndexType("02");
							rptIdxInfo.setSrcIndexNo(getSrcIndexNo("idx", rptSource.get(cellKey), null, null));
							if (rptSource.get(cellKey).contains(".")){
								rptIdxInfo.setSrcIndexMeasure(getSrcIndexMeasure(rptSource.get(cellKey)));
							} else {
								rptIdxInfo.setSrcIndexMeasure("INDEX_VAL");
							}

							//rpt_idx_src_rel_info
							RptIdxSrcRelInfo rptIdxSrcRelInfo = new RptIdxSrcRelInfo();
							RptIdxSrcRelInfoPK rptIdxSrcRelInfoPK = new RptIdxSrcRelInfoPK();
							rptIdxSrcRelInfoPK.setIndexNo(rptIdx.get(cellKey));
							rptIdxSrcRelInfoPK.setIndexVerId(new Long(verId.toString()));
							rptIdxSrcRelInfoPK.setSrcIndexNo(getSrcIndexNo("idx", rptSource.get(cellKey), null, null));
							rptIdxSrcRelInfoPK.setSrcMeasureNo("INDEX_VAL");
							rptIdxSrcRelInfo.setId(rptIdxSrcRelInfoPK);
							rptIdxSrcRelInfoList.add(rptIdxSrcRelInfo);
						}
						
						//??????????????????????????????
						String filterString = rptFilter.get(cellKey);
						String[] filterStrings = filterString.split("&&");
						StringBuilder formulaContent = new StringBuilder();
						for (String rptfilter : filterStrings) {
							StringBuilder formula = new StringBuilder();
							if (StringUtils.isNotEmpty(rptfilter) && rptfilter.split("=").length > 1) {
								//??????????????????
								String filterType = rptfilter.split("=")[0];
								List<String> filterTypes = new ArrayList<>();
								//????????????
								String filterItem = rptfilter.split("=")[1];
								StringBuilder filterVal = new StringBuilder();
								String regex = "(?<=\\()(\\S+)(?=\\))";
								Pattern pattern = Pattern.compile(regex);
								if (StringUtils.isNotEmpty(filterType)) {
									Matcher matcher = pattern.matcher(filterType);
									while (matcher.find()) {
										filterTypes.add(matcher.group());
									}
								}
								if (StringUtils.isNotEmpty(filterItem)) {
									for (String item : filterItem.split(",")) {
										List<String> filterItems = new ArrayList<>();
										Matcher matcher = pattern.matcher(item);
										while (matcher.find()) {
											filterItems.add(matcher.group());
										}
										filterVal.append(filterItems.get(filterItems.size() - 1)).append(",");
										formula.append("$").append(filterTypes.get(filterTypes.size() - 1)).append("=='").append(filterItems.get(filterItems.size() - 1)).append("'||");
									}
									filterVal.deleteCharAt(filterVal.length() - 1);
									formula.delete(formula.length() - 2, formula.length());
								}
								//rpt_idx_filter_info
								RptIdxFilterInfo rptIdxFilterInfo = new RptIdxFilterInfo();
								RptIdxFilterInfoPK rptIdxFilterInfoPK = new RptIdxFilterInfoPK();
								rptIdxFilterInfoPK.setIndexVerId(new Long(verId.toString()));
								rptIdxFilterInfoPK.setIndexNo(rptIdx.get(cellKey));
								rptIdxFilterInfoPK.setDimNo(filterTypes.get(filterTypes.size() - 1));
								rptIdxFilterInfo.setId(rptIdxFilterInfoPK);
								rptIdxFilterInfo.setFilterMode("01");
								rptIdxFilterInfo.setFilterVal(filterVal.toString());
								rptIdxFilterInfoList.add(rptIdxFilterInfo);
							}
							if (StringUtils.isNotEmpty(formula.toString())){
								if (StringUtils.isNotEmpty(formulaContent.toString())) {
									formulaContent.append(" (").append(formula).append(") &&");
								}else{
									formulaContent.append("(").append(formula).append(") &&");
								}
							}
						}
						if (StringUtils.isNotEmpty(formulaContent.toString())) {
							formulaContent.delete(formulaContent.length() - 3, formulaContent.length());
							rptIdxFormulaInfo.setFormulaContent(formulaContent.toString());
						}
						rptIdxFormulaInfo.setFormulaType("01");
						
						rptIdxInfo.setSetId("RPT_REPORT_RESULT");
					}
					if (cellSide.get("formulaCells") != null && cellSide.get("formulaCells").contains(cellKey)) {
						rptIdxInfo.setIndexType("03");
						rptIdxInfo.setSrcIndexMeasure("INDEX_VAL");
						rptIdxInfo.setSrcIndexNo(getSrcIndexNo("formula", rptSource.get(cellKey), rptList, exportReportInfoVOList));
						for (String formulaString : getSrcIndexNo("formula", rptSource.get(cellKey), rptList, exportReportInfoVOList).split(",")) {
							if (StringUtils.isNotEmpty(formulaString)) {
								//rpt_idx_src_rel_info
								RptIdxSrcRelInfo rptIdxSrcRelInfo = new RptIdxSrcRelInfo();
								RptIdxSrcRelInfoPK rptIdxSrcRelInfoPK = new RptIdxSrcRelInfoPK();
								rptIdxSrcRelInfoPK.setIndexNo(rptIdx.get(cellKey));
								rptIdxSrcRelInfoPK.setIndexVerId(new Long(verId.toString()));
								rptIdxSrcRelInfoPK.setSrcIndexNo(formulaString);
								rptIdxSrcRelInfoPK.setSrcMeasureNo("INDEX_VAL");
								rptIdxSrcRelInfo.setId(rptIdxSrcRelInfoPK);
								rptIdxSrcRelInfoList.add(rptIdxSrcRelInfo);
							}
						}

						rptIdxFormulaInfo.setFormulaDesc(rptSource.get(cellKey));
						rptIdxFormulaInfo.setFormulaContent(changeFormulaIdx(rptSource.get(cellKey), rptList, exportReportInfoVOList));
						rptIdxFormulaInfo.setFormulaType("02");
					}
					if (cellSide.get("excelCells") != null && cellSide.get("excelCells").contains(cellKey)) {
						rptIdxInfo.setIndexType("09");
						rptIdxInfo.setSrcIndexMeasure("INDEX_VAL");
						rptIdxInfo.setSrcIndexNo(getExcelSrcIndexNo(workbookWrapper, sheet, rptSource.get(cellKey),rptIdx));
						rptIdxFormulaInfo.getId().setIndexNo(null);
					}
					rptIdxInfoList.add(rptIdxInfo);
					if (StringUtils.isNotEmpty(rptIdxFormulaInfo.getId().getIndexNo()))
						rptIdxFormulaInfoList.add(rptIdxFormulaInfo);

					//rpt_idx_measure_rel
					RptIdxMeasureRel rptIdxMeasureRel = new RptIdxMeasureRel();
					RptIdxMeasureRelPK rptIdxMeasureRelPK = new RptIdxMeasureRelPK();
					rptIdxMeasureRelPK.setIndexVerId(new Long(verId.toString()));
					rptIdxMeasureRelPK.setIndexNo(rptIdx.get(cellKey));
					rptIdxMeasureRelPK.setDsId("RPT_REPORT_RESULT");
					rptIdxMeasureRelPK.setMeasureNo("INDEX_VAL");
					rptIdxMeasureRel.setId(rptIdxMeasureRelPK);
					rptIdxMeasureRel.setStoreCol("INDEX_VAL");
					rptIdxMeasureRelList.add(rptIdxMeasureRel);

					//rpt_idx_dim_rel
					List<String[]> dims = new ArrayList<String[]>() {
						{
							add(new String[]{"DATE", "01", "DATA_DATE", "0"});
							add(new String[]{"INDEXNO", "04", "INDEX_NO", "1"});
							add(new String[]{"ORG", "02", "ORG_NO", "2"});
						}
					};
					for (String[] dimRel : dims) {
						RptIdxDimRel rptIdxDimRel = new RptIdxDimRel();
						RptIdxDimRelPK rptIdxDimRelPK = new RptIdxDimRelPK();
						rptIdxDimRelPK.setIndexVerId(new Long(verId.toString()));
						rptIdxDimRelPK.setDimNo(dimRel[0]);
						rptIdxDimRelPK.setDsId("RPT_REPORT_RESULT");
						rptIdxDimRelPK.setIndexNo(rptIdx.get(cellKey));
						rptIdxDimRel.setId(rptIdxDimRelPK);
						rptIdxDimRel.setDimType(dimRel[1]);
						rptIdxDimRel.setStoreCol(dimRel[2]);
						rptIdxDimRel.setOrderNum(new BigDecimal(dimRel[3]));
						rptIdxDimRelList.add(rptIdxDimRel);
					}
					rptDesignSourceIdxList.add(rptDesignSourceIdx);
				}
			}
		}
		if (rptDesignCellInfoList.size() > 0) {
			rptConfigs.put("rpt_design_cell_info", rptDesignCellInfoList);
		}
		if (rptDesignSourceIdxList.size() > 0) {
			rptConfigs.put("rpt_design_source_idx", rptDesignSourceIdxList);
		}
		if (rptIdxInfoList.size() > 0) {
			rptConfigs.put("rpt_idx_info", rptIdxInfoList);
		}
		if (rptIdxMeasureRelList.size() > 0) {
			rptConfigs.put("rpt_idx_measure_rel", rptIdxMeasureRelList);
		}
		if (rptIdxDimRelList.size() > 0) {
			rptConfigs.put("rpt_idx_dim_rel", rptIdxDimRelList);
		}
		if (rptIdxSrcRelInfoList.size() > 0) {
			rptConfigs.put("rpt_idx_src_rel_info", rptIdxSrcRelInfoList);
		}
		if (rptIdxFilterInfoList.size() > 0) {
			rptConfigs.put("rpt_idx_filter_info", rptIdxFilterInfoList);
		}
		if (rptIdxFormulaInfoList.size() > 0) {
			rptConfigs.put("rpt_idx_formula_info", rptIdxFormulaInfoList);
		}
		if (rptIdxCfgList.size() > 0) {
			rptConfigs.put("rpt_idx_cfg", rptIdxCfgList);
		}
		if (rptDesignSourceDsList.size() > 0) {
			rptConfigs.put("rpt_design_source_ds", rptDesignSourceDsList);
		}
		if (rptDesignSourceFormulaList.size() > 0) {
			rptConfigs.put("rpt_design_source_formula", rptDesignSourceFormulaList);
		}
		if (rptDesignSourceTextList.size() > 0) {
			rptConfigs.put("rpt_design_source_text", rptDesignSourceTextList);
		}
		if (rptDesignComcellInfoList.size() > 0) {
			rptConfigs.put("rpt_design_comcell_info", rptDesignComcellInfoList);
		}
		
		//????????????
		workbookWrapper = null;
		sheet = null;
	}

	/**
	 * @????????????: ??????excel????????????????????????
	 * @?????????: huzq1 
	 * @????????????: 2021/1/26 9:57
	 * @param fileName
	 * @param formula
	 * @return
	 **/
	private String getExcelSrcIndexNo(FormulaParsingWorkbook workbookWrapper, Sheet sheet, String formula, Map<String, String> rptIdx) {
		try {
			formula = formula.substring(1);//?????????????????????=
			List<String> list = new ArrayList<>();
			Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL, sheet.getWorkbook().getSheetIndex(sheet));
			for (int i = 0; i < ptgs.length; i++) {
				if (ptgs[i] instanceof RefPtgBase) { // base class for cell references
					RefPtgBase ref = (RefPtgBase) ptgs[i];
					String key = ref.toFormulaString().replaceAll("[${}]", "");
					String indexNo = rptIdx.get(key);
					if(!list.contains(indexNo)){
						list.add(indexNo);
					}
				} else if (ptgs[i] instanceof AreaPtgBase) { // base class for range references
					AreaPtgBase ref = (AreaPtgBase) ptgs[i];
					int firstRow = ref.getFirstRow();
					int lastRow = ref.getLastRow();
					int firstColumn = ref.getFirstColumn();
					int lastColumn = ref.getLastColumn();

					int rowSize = lastRow - firstRow + 1;
					int colSize = lastColumn - firstColumn + 1;
					for (int row = 0; row < rowSize; row++) {
						for (int col = 0; col < colSize; col++) {
							String s = ExcelAnalyseUtils.toABC(firstRow + row, firstColumn + col);
							String key = s.replaceAll("[${}]", "");
							String indexNo = rptIdx.get(key);
							if(!list.contains(indexNo)){
								list.add(indexNo);
							}
						}
					}
				}
			}
			return StringUtils.join(list, ",");
		} catch (Exception e){
			logger.error(e.toString());
		}
		return null;
	}

	/**
	 * ??????????????????
	 * @param indexNm ????????????
	 */
	private String getSrcIndexMeasure(String indexNm) {
		Map<String, String> params = new HashMap<>();
		params.put("measureNm", indexNm.split("\\.")[1]);
		List<String> srcIndexMeasure = rptModelImportDao.getSysMeasureNo(params);
		return srcIndexMeasure.get(0);
	}


	/**
	 * ?????????????????????
	 *
	 * @param cellString ???????????????
	 **/
	public String changeFormulaIdx(String cellString, List<Map<String, Map<String, String>>> rptList, List<ExportReportInfoVO> exportReportInfoVOList) {
		Map<String, String> params = new HashMap<>();
		//????????????????????????????????????????????????I?????????I?????????
		cellString = changeRptFunc(cellString);
		
		String regex = "\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[:]|[-])+'\\)";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(cellString);
		while (matcher.find()) {
			String regex1 = "(?<=\\(')(\\S+)(?='\\))";
			Pattern pattern1 = Pattern.compile(regex1);
			Matcher matcher1 = pattern1.matcher(matcher.group());
			while (matcher1.find()) {
				String rptNm = matcher1.group().split("\\.")[0];
			     //????????????????????????,?????????"????????????."?????????????????????????????? 
                String indexNm = matcher1.group().replace(rptNm + ".", "");
				params.put("indexNm", indexNm);
				params.put("rptNm", rptNm);
				List<String> srcIndexNos = rptModelImportDao.getSysIdxNo(params);
				if (srcIndexNos != null && srcIndexNos.size()>0) {
					cellString = cellString.replace(matcher1.group(), srcIndexNos.get(0));
				} else {
					//????????????????????????
					for (int i = 0; i < rptList.size(); i++){
						//??????????????????
						if (StringUtils.isNotEmpty(rptNm) && rptNm.equals(exportReportInfoVOList.get(i).getRptNm())){
							Map<String, String> rptCellNm = rptList.get(i).get("rptCellNm");
							if (StringUtils.isNotEmpty(indexNm)){
								for (String key : rptCellNm.keySet()) {
									//??????????????????
									if (indexNm.equals(rptCellNm.get(key))) {
										Map<String, String> rptIdx = rptList.get(i).get("rptIdx");
										cellString = cellString.replace(matcher1.group(), rptIdx.get(key));
									}
								}
							}
						}
					}
				}
			}
		}
		return cellString;
	}

	/**
	 * ????????????????????????
	 *
	 * @param indexType  ???????????? idx ?????? formula ????????????
	 * @param cellString ???????????????
	 **/
	public String getSrcIndexNo(String indexType, String cellString, List<Map<String, Map<String, String>>> rptList,List<ExportReportInfoVO> exportReportInfoVOList) {
		StringBuilder srcIndexNo = new StringBuilder();
		Map<String, String> params = new HashMap<>();
		if ("idx".equals(indexType)) {
			// ??????????????????
			if(cellString.contains(".")){
				cellString = cellString.split("\\.")[0];
			}
			String idxInfoNo = this.static_idxInfo.get(cellString);
			srcIndexNo.append(idxInfoNo);
		}
		if ("formula".equals(indexType)) {
			String regex = "I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[:]|[-])+'\\)";
			Pattern p = Pattern.compile(regex);
			Matcher matcher = p.matcher(cellString);
			List<String> exitIndexNo = new ArrayList<>();
			while (matcher.find()) {
				String regex1 = "(?<=I\\(')(\\S+)(?='\\))";
				Pattern pattern1 = Pattern.compile(regex1);
				Matcher matcher1 = pattern1.matcher(matcher.group());
				while (matcher1.find()) {
					//???????????????????????????.??????
					String rptNm = matcher1.group().split("\\.")[0];
					//????????????????????????,?????????"????????????."?????????????????????????????? 
                    String indexNm = matcher1.group().replace(rptNm + ".", "");
					params.put("indexNm", indexNm);
					params.put("rptNm", rptNm);
					List<String> srcIndexNos = rptModelImportDao.getSysIdxNo(params);
					if (srcIndexNos != null && srcIndexNos.size() > 0) {
						if(!exitIndexNo.contains(srcIndexNos.get(0))){
							srcIndexNo.append(srcIndexNos.get(0)).append(",");
							exitIndexNo.add(srcIndexNos.get(0));
						}
					} else {
						//????????????????????????
						for (int i = 0; i < rptList.size(); i++){
							//??????????????????
							if (StringUtils.isNotEmpty(rptNm) && rptNm.equals(exportReportInfoVOList.get(i).getRptNm())){
								Map<String, String> rptCellNm = rptList.get(i).get("rptCellNm");
								if (StringUtils.isNotEmpty(indexNm)){
									for (String key : rptCellNm.keySet()) {
										//??????????????????
										if (indexNm.equals(rptCellNm.get(key))) {
											if(rptList.get(i).get("rptCell").get(key).equals("?????????????????????") ||
													rptList.get(i).get("rptCell").get(key).equals("???????????????") ||
													rptList.get(i).get("rptCell").get(key).equals("Excel?????????")){
												Map<String, String> rptIdx = rptList.get(i).get("rptIdx");
												if((null != rptIdx) && (StringUtils.isNotBlank(rptIdx.get(key)))){
													if(!exitIndexNo.contains(rptIdx.get(key))){
														srcIndexNo.append(rptIdx.get(key)).append(",");
														exitIndexNo.add(rptIdx.get(key));
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if (srcIndexNo.length() > 0)
				srcIndexNo.deleteCharAt(srcIndexNo.length() - 1);
		}
		return srcIndexNo.toString();
	}

	/**
	 * ????????????
	 *
	 * @param rptInfos ??????????????????
	 **/
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = SQLException.class)
	public void insertData(List<Map<String, Object>> rptInfos) {
		logger.info("???????????????????????????????????????");
		//rptModelImportDao.insertTest();
		for (Map<String, Object> rptInfo : rptInfos) {
			//??????rpt_mgr_report_info
			RptMgrReportInfo rptMgrReportInfo = (RptMgrReportInfo) rptInfo.get("rpt_mgr_report_info");
			logger.info("?????????????????????" + rptMgrReportInfo.getRptNm());
			rptModelImportDao.insertRptMgrReportInfo(rptMgrReportInfo);
			//??????rpt_mgr_frs_ext
			RptMgrFrsExt rptMgrFrsExt = (RptMgrFrsExt) rptInfo.get("rpt_mgr_frs_ext");
			rptModelImportDao.insertRptMgrFrsExt(rptMgrFrsExt);
			//??????rpt_design_tmp_info
			RptDesignTmpInfo rptDesignTmpInfo = (RptDesignTmpInfo) rptInfo.get("rpt_design_tmp_info");
			rptModelImportDao.insertRptDesignTmpInfo(rptDesignTmpInfo);
			//??????rpt_design_cell_info
			List<RptDesignCellInfo> rptDesignCellInfoList = (List<RptDesignCellInfo>) rptInfo.get("rpt_design_cell_info");
			//??????????????????????????????
			int batchCount = 1000;
			if (rptDesignCellInfoList != null) {
				for (int rowCount = 0; rowCount < rptDesignCellInfoList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptDesignCellInfoList.size()) {
						rptModelImportDao.insertRptDesignCellInfo(rptDesignCellInfoList.subList(rowCount, rptDesignCellInfoList.size()));
					} else {
						rptModelImportDao.insertRptDesignCellInfo(rptDesignCellInfoList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//??????rpt_design_source_idx
			List<RptDesignSourceIdx> rptDesignSourceIdxList = (List<RptDesignSourceIdx>) rptInfo.get("rpt_design_source_idx");
			if (rptDesignSourceIdxList != null) {
				for (int rowCount = 0; rowCount < rptDesignSourceIdxList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptDesignSourceIdxList.size()) {
						rptModelImportDao.insertRptDesignSourceIdx(rptDesignSourceIdxList.subList(rowCount, rptDesignSourceIdxList.size()));
					} else {
						rptModelImportDao.insertRptDesignSourceIdx(rptDesignSourceIdxList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//??????rpt_idx_info
			List<RptIdxInfo> rptIdxInfoList = (List<RptIdxInfo>) rptInfo.get("rpt_idx_info");
			if (rptIdxInfoList != null) {
				for (int rowCount = 0; rowCount < rptIdxInfoList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptIdxInfoList.size()) {
						rptModelImportDao.insertRptIdxInfo(rptIdxInfoList.subList(rowCount, rptIdxInfoList.size()));
					} else {
						rptModelImportDao.insertRptIdxInfo(rptIdxInfoList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//??????rpt_idx_measure_rel
			List<RptIdxMeasureRel> rptIdxMeasureRelList = (List<RptIdxMeasureRel>) rptInfo.get("rpt_idx_measure_rel");
			if (rptIdxMeasureRelList != null) {
				for (int rowCount = 0; rowCount < rptIdxMeasureRelList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptIdxMeasureRelList.size()) {
						rptModelImportDao.insertRptIdxMeasureRel(rptIdxMeasureRelList.subList(rowCount, rptIdxMeasureRelList.size()));
					} else {
						rptModelImportDao.insertRptIdxMeasureRel(rptIdxMeasureRelList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//??????rpt_idx_dim_rel
			List<RptIdxDimRel> rptIdxDimRelList = (List<RptIdxDimRel>) rptInfo.get("rpt_idx_dim_rel");
			if (rptIdxDimRelList != null) {
				for (int rowCount = 0; rowCount < rptIdxDimRelList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptIdxDimRelList.size()) {
						rptModelImportDao.insertRptIdxDimRel(rptIdxDimRelList.subList(rowCount, rptIdxDimRelList.size()));
					} else {
						rptModelImportDao.insertRptIdxDimRel(rptIdxDimRelList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//??????rpt_idx_src_rel_info
			List<RptIdxSrcRelInfo> rptIdxSrcRelInfoList = (List<RptIdxSrcRelInfo>) rptInfo.get("rpt_idx_src_rel_info");
			if (rptIdxSrcRelInfoList != null) {
				for (int rowCount = 0; rowCount < rptIdxSrcRelInfoList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptIdxSrcRelInfoList.size()) {
						rptModelImportDao.insertRptIdxSrcRelInfo(rptIdxSrcRelInfoList.subList(rowCount, rptIdxSrcRelInfoList.size()));
					} else {
						rptModelImportDao.insertRptIdxSrcRelInfo(rptIdxSrcRelInfoList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//??????rpt_idx_filter_info
			List<RptIdxFilterInfo> rptIdxFilterInfoList = (List<RptIdxFilterInfo>) rptInfo.get("rpt_idx_filter_info");
			if (rptIdxFilterInfoList != null) {
				for (int rowCount = 0; rowCount < rptIdxFilterInfoList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptIdxFilterInfoList.size()) {
						rptModelImportDao.insertRptIdxFilterInfo(rptIdxFilterInfoList.subList(rowCount, rptIdxFilterInfoList.size()));
					} else {
						rptModelImportDao.insertRptIdxFilterInfo(rptIdxFilterInfoList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//rpt_idx_formula_info
			List<RptIdxFormulaInfo> rptIdxFormulaInfoList = (List<RptIdxFormulaInfo>) rptInfo.get("rpt_idx_formula_info");
			if (rptIdxFormulaInfoList != null) {
				for (int rowCount = 0; rowCount < rptIdxFormulaInfoList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptIdxFormulaInfoList.size()) {
						rptModelImportDao.insertRptIdxFormulaInfo(rptIdxFormulaInfoList.subList(rowCount, rptIdxFormulaInfoList.size()));
					} else {
						rptModelImportDao.insertRptIdxFormulaInfo(rptIdxFormulaInfoList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//rpt_idx_cfg
			List<RptIdxCfg> rptIdxCfgList = (List<RptIdxCfg>) rptInfo.get("rpt_idx_cfg");
			if (rptIdxCfgList != null) {
				for (int rowCount = 0; rowCount < rptIdxCfgList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptIdxCfgList.size()) {
						rptModelImportDao.insertRptIdxCfg(rptIdxCfgList.subList(rowCount, rptIdxCfgList.size()));
					} else {
						rptModelImportDao.insertRptIdxCfg(rptIdxCfgList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//rpt_design_source_ds
			List<RptDesignSourceDs> rptDesignSourceDsList = (List<RptDesignSourceDs>) rptInfo.get("rpt_design_source_ds");
			if (rptDesignSourceDsList != null) {
				for (int rowCount = 0; rowCount < rptDesignSourceDsList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptDesignSourceDsList.size()) {
						rptModelImportDao.insertRptDesignSourceDs(rptDesignSourceDsList.subList(rowCount, rptDesignSourceDsList.size()));
					} else {
						rptModelImportDao.insertRptDesignSourceDs(rptDesignSourceDsList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//rpt_design_source_formula
			List<RptDesignSourceFormula> rptDesignSourceFormulaList = (List<RptDesignSourceFormula>) rptInfo.get("rpt_design_source_formula");
			if (rptDesignSourceFormulaList != null) {
				for (int rowCount = 0; rowCount < rptDesignSourceFormulaList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptDesignSourceFormulaList.size()) {
						rptModelImportDao.insertRptDesignSourceFormula(rptDesignSourceFormulaList.subList(rowCount, rptDesignSourceFormulaList.size()));
					} else {
						rptModelImportDao.insertRptDesignSourceFormula(rptDesignSourceFormulaList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//rpt_design_source_text
			List<RptDesignSourceText> rptDesignSourceTextList = (List<RptDesignSourceText>) rptInfo.get("rpt_design_source_text");
			if (rptDesignSourceTextList != null) {
				for (int rowCount = 0; rowCount < rptDesignSourceTextList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptDesignSourceTextList.size()) {
						rptModelImportDao.insertRptDesignSourceText(rptDesignSourceTextList.subList(rowCount, rptDesignSourceTextList.size()));
					} else {
						rptModelImportDao.insertRptDesignSourceText(rptDesignSourceTextList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			//rpt_design_comcell_info
			List<RptDesignComcellInfo> rptDesignComcellInfoList = (List<RptDesignComcellInfo>) rptInfo.get("rpt_design_comcell_info");
			if (rptDesignComcellInfoList != null) {
				for (int rowCount = 0; rowCount < rptDesignComcellInfoList.size(); rowCount = rowCount + batchCount) {
					if (rowCount + batchCount > rptDesignComcellInfoList.size()) {
						rptModelImportDao.insertRptDesignComCellInfo(rptDesignComcellInfoList.subList(rowCount, rptDesignComcellInfoList.size()));
					} else {
						rptModelImportDao.insertRptDesignComCellInfo(rptDesignComcellInfoList.subList(rowCount, rowCount + batchCount));
					}
				}
			}
			EhcacheUtils.remove(rptDesignTmpInfo.getId().getTemplateId()+"-"+ rptDesignTmpInfo.getId().getVerId(), "tmpInfo");
		}
	}

	/**
	 * ??????????????????????????????????????????
	 *
	 * @param params ????????????
	 **/
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = SQLException.class)
	public void clearData(Map<String, Object> params) {
		logger.info("????????????,????????????????????????");
		List<ExportReportInfoVO> exportReportInfoVOList = (List<ExportReportInfoVO>) params.get("exportReportInfoVOList");
		for (ExportReportInfoVO exportReportInfoVO : exportReportInfoVOList) {
			String templateId = rptModelImportDao.getTemplateIdByRptNum(exportReportInfoVO.getRptNum());
			BioneParamInfo busiInfo = rptModelImportDao.getBusiInfo(exportReportInfoVO.getBusiType());
			String busiType = busiInfo.getParamValue();
			if (templateId != null) {
				//??????????????????
				Map<String, Object> sqlParams = new HashMap<>();
				sqlParams.put("templateId", templateId);
				List<String> verIds = rptModelImportDao.getSysRptVerIds(sqlParams);
				if (verIds != null && verIds.size() != 0 && exportReportInfoVO.getVerId().compareTo(new BigDecimal(verIds.get(0))) >= 0) {
					//?????????????????????????????????
					sqlParams.put("busiType", busiType);
					sqlParams.put("verId", exportReportInfoVO.getVerId());
					FrsSystemCfgVO frsSystemCfgVO = rptModelImportDao.getFrsSystemCfg(sqlParams);
					sqlParams.put("endDate", frsSystemCfgVO.getVerStartDate());
					sqlParams.put("verId", verIds.get(0));
					rptModelImportDao.updateRptIdxInfo(sqlParams);
					rptModelImportDao.updateRptDesignTmpInfo(sqlParams);
				}
				sqlParams.put("verId", exportReportInfoVO.getVerId());
				rptModelImportDao.deleteRptIdxMeasureRel(sqlParams);
				rptModelImportDao.deleteRptIdxDimRel(sqlParams);
				rptModelImportDao.deleteRptIdxSrcRelInfo(sqlParams);
				rptModelImportDao.deleteRptIdxFilterInfo(sqlParams);
				rptModelImportDao.deleteRptIdxFormulaInfo(sqlParams);
				rptModelImportDao.deleteRptIdxInfo(sqlParams);
				rptModelImportDao.deleteRptIdxCfg(sqlParams);
				rptModelImportDao.deleteRptDesignTmpInfo(sqlParams);
				rptModelImportDao.deleteRptDesignCellInfo(sqlParams);
				rptModelImportDao.deleteRptDesignBatchCfg(sqlParams);//--
				rptModelImportDao.deleteRptDesignSourceDs(sqlParams);
				rptModelImportDao.deleteRptDesignSourceFormula(sqlParams);
				rptModelImportDao.deleteRptDesignSourceText(sqlParams);
				rptModelImportDao.deleteRptDesignSourceIdx(sqlParams);
				rptModelImportDao.deleteRptDesignSourceTabidx(sqlParams);//--
				rptModelImportDao.deleteRptDesignSourceTabdim(sqlParams);//--
				rptModelImportDao.deleteRptParamtmpInfo(sqlParams);//--
				rptModelImportDao.deleteRptParamtmpAttrs(sqlParams);//--
				rptModelImportDao.deleteRptDesignQueryDim(sqlParams);//--
				rptModelImportDao.deleteRptDesignQueryDetail(sqlParams);//--
				rptModelImportDao.deleteRptDesignComCellInfo(sqlParams);
				rptModelImportDao.deleteRptDesignFavInfo(sqlParams);//--
				rptModelImportDao.deleteRptMgrFrsExt(sqlParams);
				rptModelImportDao.deleteRptMgrReportInfo(sqlParams);
			}
		}
	}

	/**
	 * ??????????????????
	 *
	 * @param uuid ??????????????????
	 **/
	public Map<String, String> getImportDesignInfosUploadLog(String uuid) {
		Map<String, String> result = new HashMap<>();
		StringBuilder out = new StringBuilder();
		if (StringUtils.isNotEmpty(uuid)) {
			List<String> contents = rptModelImportDao.getImportDesignInfoLog(uuid);
			for (String content : contents) {
				out.append(content);
			}
		}
		if (out.length() == 0) {
			out.append("<li>????????????????????????...</li>");
		}
		result.put("out", out.toString());
		return result;
	}
	
	
	/**
	 * ???"03"???????????????????????????????????????????????????ThisMonthFirst(I('bfa6ead060e711e98651cfd84b3ca728'))=???ThisMonthFirst('bfa6ead060e711e98651cfd84b3ca728')
	 * @param expr
	 * @return
	 */
	private String changeRptFunc(String expr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("funcType", GlobalConstants4plugin.VALID_FUNC_TYPE_RPT);
		List<RptIdxFormulaFunc> funcs = this.funAndSymbolMybatisDao.listFunc(map);
		if(funcs == null || funcs.size() == 0)
			return expr;
		
		StringBuffer sb = new StringBuffer();
		String patternString = "";
		//?????????????????????
		for(RptIdxFormulaFunc func : funcs){
			if("A".equals(func.getFormulaId())){//A()??????????????????????????????????????????????????? mapjin2 20200325
				patternString = patternString  + func.getFormulaId() + "\\(I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|\\.)+'\\)+,'[A-Z]','Y'+\\)" + "|";
				patternString = patternString  + func.getFormulaId() + "\\(I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|\\.)+'\\)+,'[A-Z]'+\\)" + "|";
			}else{
				patternString = patternString  + func.getFormulaId() + "\\(I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|\\.)+'\\)\\)" + "|";
			}
		}
		if(patternString.endsWith("|")){
			patternString = patternString.substring(0, patternString.length() - 1);
		}
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(expr);
		//???????????????????????????????????????
		while (matcher.find()) {
			matcher.appendReplacement(sb,matcher.group(0).substring(0, matcher.group(0).indexOf("("))+ "(" + //??????A+( =>A(
					matcher.group(0).substring(matcher.group(0).indexOf("'"), matcher.group(0).indexOf("'", matcher.group(0).indexOf("'") + 1)+1) + //????????????????????????????????????????????????????????? =>'bfa6ead060e711e98651cfd84b3ca728'
					matcher.group(0).substring(matcher.group(0).indexOf("'", matcher.group(0).indexOf("'") + 1)+2, matcher.group(0).lastIndexOf(")"))+")");//?????????????????????????????????????????????????????????+???)??? =>,'M') ??????=A('bfa6ead060e711e98651cfd84b3ca728','M')
		}
		matcher.appendTail(sb);
		return sb.toString().equals("")?expr:sb.toString();
	}
	
}
