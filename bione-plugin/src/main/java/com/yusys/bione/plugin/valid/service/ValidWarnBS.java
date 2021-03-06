package com.yusys.bione.plugin.valid.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.excel.AbstractExcelImport;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.ExcelImporter;
import com.yusys.bione.frame.excel.XlsExcelTemplateExporter;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevel;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevelPK;
import com.yusys.bione.plugin.valid.repository.ValidWarnLevelMybatisDao;
import com.yusys.bione.plugin.valid.repository.ValidWarnMybatisDao;
import com.yusys.bione.plugin.valid.web.vo.CfgextWarnVO;
import com.yusys.bione.plugin.valid.web.vo.ValidCfgextWarnVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ValidWarnBS extends BaseBS<Object>{

	@Autowired
	private ValidWarnMybatisDao warnDao;
	
	@Autowired
	private ValidWarnLevelMybatisDao levelDao;

	@Autowired
	private RptMgrInfoMybatisDao reportInfoDAO;

	public Map<String, Object> list(Pager pager, String templateId, String srcIdxNo, String dataDate) {
		if(StringUtils.isBlank(templateId) && StringUtils.isBlank(srcIdxNo)) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		if(StringUtils.isNotBlank(templateId)){
			map.put("rptTemplateId", templateId);
		}
		if(StringUtils.isNotBlank(srcIdxNo)) {
			map.put("indexNo", srcIdxNo);
		}
		if(StringUtils.isNotBlank(dataDate)) {
			map.put("dataDate", dataDate);
		}else{
			String condition = pager.getCondition();
			if(StringUtils.isBlank(condition) || condition.indexOf("endDate") < 0) {
				map.put("endDate", "29991231");
			}else{
				JSONObject obj = JSONObject.parseObject(condition);
				JSONArray array = JSONArray.parseArray(obj.getString("rules"));
				for(int i = 0; i < array.size(); i++){
					JSONObject job = array.getJSONObject(i);
					if(job.getString("field").equalsIgnoreCase("warn.endDate")){
						map.put("startDate", job.getString("value"));
					}
				}
			}
			if(pager.getCondition() != null ){
				pager.setCondition(StringUtils.replace(pager.getCondition(), "'", "''"));
			}
		}
		PageHelper.startPage(pager);
		PageMyBatis<ValidCfgextWarnVO> page = (PageMyBatis<ValidCfgextWarnVO>) this.warnDao.listWarn(map);
		List<ValidCfgextWarnVO> vos = new ArrayList<>();
		for (ValidCfgextWarnVO vo : page.getResult()) {
			if("01".equals(vo.getRangeType())){
				vo.setMinusRangeVal(divideUnit(vo.getUnit(), vo.getMinusRangeVal()));
				vo.setPostiveRangeVal(divideUnit(vo.getUnit(), vo.getPostiveRangeVal()));
			}
			vos.add(vo);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Rows", page.getResult());
		result.put("Total", page.getTotalCount());
		return result;
	}


	public Map<String, Object> getLevelInfo(String rptId, String checkId) {
		List<RptValidWarnLevel> list = new ArrayList<RptValidWarnLevel>();
		if (checkId != null && !checkId.equals("")) {
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("rptId", rptId);
			condition.put("checkId", checkId);

			list = this.levelDao
					.listWarnLevel(condition);
		}else{
			RptValidWarnLevel level1 = new RptValidWarnLevel();
			level1.setIsPassCond("1");
			level1.setLevelNm("??????");
			level1.setLevelType("01");
			level1.setMinusRangeVal(new BigDecimal(0));
			level1.setPostiveRangeVal(new BigDecimal(0));
			level1.setRemindColor("660000");
			list.add(level1);
			
			RptValidWarnLevel level = new RptValidWarnLevel();
			level.setIsPassCond("0");
			level.setLevelNm("??????");
			level.setLevelType("01");
			level.setMinusRangeVal(new BigDecimal(0));
			level.setPostiveRangeVal(new BigDecimal(0));
			level.setRemindColor("ff0000");
			list.add(level);
			
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Rows", list);
		result.put("Total", list.size());
		return result;
	}


	/**
	 * ??????-????????????
	 * @param warn
	 * @param params 
	 */
	@Transactional(readOnly = false)
	public void saveWarn(ValidCfgextWarnVO warn, Map<String, Object> params) {
		//????????????????????????
		RptValidCfgextWarn rptValidCfgextWarn = new RptValidCfgextWarn();
		if(warn.getCheckId() == null || warn.getCheckId().equals("")){//??????
			rptValidCfgextWarn.setCheckId(RandomUtils.uuid2());
		}else{//??????	
			rptValidCfgextWarn.setCheckId(warn.getCheckId());
		}
		
		rptValidCfgextWarn.setIndexNo(warn.getIndexNo());
		rptValidCfgextWarn.setRptTemplateId(warn.getRptTemplateId());
		rptValidCfgextWarn.setCompareType(warn.getCompareType());
		rptValidCfgextWarn.setIsFrs(warn.getIsFrs());
		if(StringUtils.isNotBlank(warn.getCompareValType())){
			rptValidCfgextWarn.setCompareValType(warn.getCompareValType());
		}else{
			switch (warn.getRptCycle()) {
				case "01"://???
					rptValidCfgextWarn.setCompareValType("01");//??????????????????
					break;
				case "02"://???
					rptValidCfgextWarn.setCompareValType("03");//????????????????????????
					break;
				case "03"://???
					rptValidCfgextWarn.setCompareValType("06");//????????????????????????
					break;
				case "04"://???
					rptValidCfgextWarn.setCompareValType("08");//????????????????????????
					break;
				case "12"://??????
					rptValidCfgextWarn.setCompareValType("10");//????????????????????????
					break;
				default:
					rptValidCfgextWarn.setCompareValType("");
					break;
			}
		}
		rptValidCfgextWarn.setRangeType(warn.getRangeType());
		rptValidCfgextWarn.setRemark(warn.getRemark());
		if("01".equals(warn.getRangeType())){
			rptValidCfgextWarn.setUnit(warn.getUnit());
		}

		if(params == null){//params==null??????????????????
			rptValidCfgextWarn.setStartDate(warn.getStartDate());
			rptValidCfgextWarn.setEndDate(warn.getEndDate());
		}else{//params???=null????????????????????????????????????????????????????????????????????????????????????
			//????????????id????????????????????????????????????
			BigDecimal verId = new BigDecimal(params.get("verId").toString());//???????????????
			String templateId = warn.getRptTemplateId();
			String busiType = params.get("busiType").toString();
			//?????????????????????????????????????????????????????????
			String selectJql = "select t from RptDesignTmpInfo t where t.id.templateId = ?0 and t.id.verId = ?1";
			RptDesignTmpInfo tmp = this.baseDAO.findUniqueWithIndexParam(selectJql, templateId, verId);
			if(tmp != null){//??????????????????
				rptValidCfgextWarn.setStartDate(tmp.getVerStartDate());
				rptValidCfgextWarn.setEndDate(tmp.getVerEndDate());
			}else{//???????????????
				//???????????????????????????????????????????????????
				String jql = " select t.ver_start_date,t.ver_end_date from frs_system_cfg t where t.system_ver_id = ?0 and t.busi_type = ?1";
				Object[] obj = this.baseDAO.findByNativeSQLWithIndexParam(jql, verId.longValue(), busiType).get(0);
				rptValidCfgextWarn.setStartDate(obj[0].toString());
				//??????????????????????????????????????????????????????????????????????????????????????????
				String nextjql = " select min(t.id.verId) from RptDesignTmpInfo t where t.id.templateId = ?0 and t.id.verId > ?1";
				BigDecimal nextVer = this.baseDAO.findUniqueWithIndexParam(nextjql, templateId, verId);//??????????????????????????????????????????
				if(nextVer != null){//??????????????????????????????????????????
					jql = " select t.verStartDate from RptDesignTmpInfo t where t.id.templateId = ?0 and t.id.verId = ?1";
					String nextVerStartDate = this.baseDAO.findUniqueWithIndexParam(jql, templateId, nextVer);
					rptValidCfgextWarn.setEndDate(nextVerStartDate);
				}else{
					rptValidCfgextWarn.setEndDate("29991231");
				}
			}
		}
//		this.warnDao.saveWarn(rptValidCfgextWarn);
		this.baseDAO.save(rptValidCfgextWarn);
		
		//??????????????????
		RptValidWarnLevel level = new RptValidWarnLevel();
		RptValidWarnLevelPK pk = new RptValidWarnLevelPK();
		if(warn.getLevelNum() == null || warn.getCheckId().equals("")){//??????
			pk.setLevelNum(RandomUtils.uuid2());
		}else{//??????
			pk.setLevelNum(warn.getLevelNum());
		}
		pk.setCheckId(rptValidCfgextWarn.getCheckId());

		level.setId(pk);
		level.setIndexNo(warn.getIndexNo());
		level.setLevelNm(warn.getLevelNm() == null ? "??????" : warn.getLevelNm());
		level.setLevelType(warn.getLevelType() == null ? "01" : warn.getLevelType());
		if("01".equals(warn.getRangeType())){
			level.setMinusRangeVal(multiplyUnit(warn.getUnit(), warn.getMinusRangeVal()));
			level.setPostiveRangeVal(multiplyUnit(warn.getUnit(), warn.getPostiveRangeVal()));
		} else {
			level.setMinusRangeVal(warn.getMinusRangeVal());
			level.setPostiveRangeVal(warn.getPostiveRangeVal());
		}
		level.setRemindColor(warn.getRemindColor() == null ? "FF0000" : warn.getRemindColor());
		level.setIsPassCond(warn.getIsPassCond() == null ? "1" : warn.getIsPassCond());
		
//		this.levelDao.saveWarnLevel(level);
		this.baseDAO.save(level);
	}

	public ValidCfgextWarnVO getInfo(String checkId, String cfgId) {
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("checkId", checkId);
		List<ValidCfgextWarnVO> list = this.warnDao.listWarn(condition);
		if(list != null && list.size() > 0){
			for (ValidCfgextWarnVO vo : list) {
				if("01".equals(vo.getRangeType())){
					vo.setMinusRangeVal(divideUnit(vo.getUnit(), vo.getMinusRangeVal()));
					vo.setPostiveRangeVal(divideUnit(vo.getUnit(), vo.getPostiveRangeVal()));
				}
			}
			return list.get(0);
		}
		return null;
	}

	/**
	 * ??????????????????id???????????????id1???????????????????????????
	 * @param checkIds
	 * @param templateId
	 */
	@Transactional(readOnly = false)
	public void delete(String checkIds, String templateId) {
		List<String> checkIdList = new ArrayList<String>();
		Map<String, Object> condition = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(checkIds)) {
			if (checkIds.endsWith(",")) {
				checkIds = checkIds.substring(0, checkIds.length() - 1);
			}
			String id[] = StringUtils.split(checkIds, ',');
			checkIdList = Arrays.asList(id);
		}else if(StringUtils.isNotBlank(templateId)) {
			condition.put("templateId", templateId);
			checkIdList = this.warnDao.findCheckIds(condition);
		}
		if(null != checkIdList && checkIdList.size() > 0) {
			condition.clear();
			condition.put("ids", checkIdList);
			this.warnDao.deleteWarn(condition);
			
			condition.clear();
			condition.put("checkIds", checkIdList);
			this.levelDao.deleteWarnLevel(condition);
		}
	}

	/**
	 * ????????????id?????????????????????????????????????????????
	 * @param templateId
	 * @param endDate
	 */
	@Transactional(readOnly = false)
	public void deleteByVer(String templateId, String endDate) {
		if (StringUtils.isNotBlank(templateId) && StringUtils.isNotBlank(endDate)) {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("templateId", templateId);
			condition.put("endDate", endDate);
			List<String> checkIds = warnDao.findCheckIds(condition);
			if(null != checkIds && checkIds.size() > 0) {
				condition.clear();
				condition.put("ids", checkIds);
				this.warnDao.deleteWarn(condition);
				
				condition.clear();
				condition.put("checkIds", checkIds);
				this.levelDao.deleteWarnLevel(condition);
			}
		}
	}
	
	public String validStartDate(String cfgId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("templateId", cfgId);
		map.put("endDate", "29991231");
		List<String> list = this.warnDao.getTmpInfo(map);
		if(list != null && list.size() == 1){
			return list.get(0);
		}
		return "29991231";
	}

	/**
	 * ??????????????????????????????????????????????????????
	 * @param templateId
	 * @param startDate ??????????????????????????????
	 * @param endDate ??????????????????????????????
	 */
	@Transactional(readOnly = false)
	public void updateValidVer(String templateId, String startDate, String endDate) {
		if (StringUtils.isNotBlank(templateId) && StringUtils.isNotBlank(endDate) && StringUtils.isNotBlank(startDate)) {
			String jql = "update RptValidCfgextWarn info set info.endDate = :endDate where info.rptTemplateId = :templateId and info.endDate = :startDate";//????????????????????????????????????
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("endDate", endDate);
			params.put("startDate", startDate);
			params.put("templateId", templateId);
			this.baseDAO.batchExecuteWithNameParam(jql, params);
		}
	}
	
	/**
	 * ????????????????????????????????????
	 * @param idxNo
	 */
	@Transactional(readOnly = false)
	public void deleteByIdxNo(String idxNo) {
		if (StringUtils.isNotBlank(idxNo)) {
			List<String> checkIdList = new ArrayList<String>();
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("idxNo", idxNo);
			condition.put("endDate", "29991231");
			checkIdList = warnDao.findCheckIds(condition);
			if(null != checkIdList && checkIdList.size() > 0) {
				//??????1000????????????????????????
				List<List<String>> idLists = SplitStringBy1000.change(checkIdList);
				for(List<String> idListBy1000 : idLists) {
					condition.clear();
					condition.put("ids", idListBy1000);
					this.warnDao.deleteWarn(condition);
					
					condition.clear();
					condition.put("checkIds", idListBy1000);
					this.levelDao.deleteWarnLevel(condition);
				}
			}
		}
	}

	/**
	 * @????????????: ??????????????????
	 * @?????????: huzq1 
	 * @????????????: 2021/7/10 10:47
	 * @param ids
	 * @param templateId
	 * @param rptNm
	 * @param realPath
	 * @param endDate
	 * @param busiType
	 * @return
	 **/
	public File expValidWarnRel(String ids, String templateId, String rptNm, String realPath, String endDate, String busiType) {
		Map<String, Object> params = new HashMap<>();
		params.put("cfgId", templateId);
		RptMgrReportInfo info = reportInfoDAO.getRptInfoByParams(params);

		List<CfgextWarnVO> vos = new ArrayList<>();
		Map<String, Object> condition = new HashMap<String, Object>();
		if("all".equals(ids)){
			if(StringUtils.isNotBlank(endDate)){
				condition.put("endDate", endDate);
			}else{
				condition.put("endDate", "29991231");//???????????????????????????????????????
			}
			condition.put("templateId", templateId);
			vos = warnDao.getCfgextWarnList(condition);
		} else {
			List<String> checkIds = Arrays.asList(ids.split(","));
			condition.put("ids", checkIds);
			vos = warnDao.getCfgextWarnList(condition);
		}

		for (CfgextWarnVO cfgextWarnVO : vos) {
			cfgextWarnVO.setCfgId(templateId);
			cfgextWarnVO.setRptCycle(info.getRptCycle());
			cfgextWarnVO.setRptNm(info.getRptNm());
			cfgextWarnVO.setBusiType(info.getBusiType());
			if ("01".equals(cfgextWarnVO.getRangeType())) {
				cfgextWarnVO.setMinusRangeVal(divideUnit(cfgextWarnVO.getUnit(), cfgextWarnVO.getMinusRangeVal()));
				cfgextWarnVO.setPostiveRangeVal(divideUnit(cfgextWarnVO.getUnit(), cfgextWarnVO.getPostiveRangeVal()));
			}
		}

		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe = null;
		if(StringUtils.isBlank(rptNm)) {
			rptNm = RandomUtils.uuid2();
		}

		File resFile = null;
		try {
			if (FilepathValidateUtils.validateFilepath(realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + rptNm + ".xls")) {
				resFile = new File(realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH
						+ File.separator + rptNm + ".xls");
				fe = new XlsExcelTemplateExporter(resFile, realPath
						+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
						+ GlobalConstants4plugin.EXPORT_VALIDWARN_TEMPLATE_PATH, list);
				fe.run();
			}
		} catch (Exception e) {
			resFile = null;
			e.printStackTrace();
		} finally {
			try {
				if (fe != null) {
					fe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		return resFile;
	}

	public UploadResult impValidWarnRel(File file, String ehcacheId) {
		AbstractExcelImport xlsImport = null;
		xlsImport = new ExcelImporter(CfgextWarnVO.class, file);
		UploadResult result = new UploadResult();
		try {
			if(xlsImport!=null){
				List<CfgextWarnVO> vos = (List<CfgextWarnVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
				if (null == vos) {
					vos = new ArrayList<>();
				}
				List<CfgextWarnVO> newVos = (List<CfgextWarnVO>) xlsImport.ReadExcel();
				vos.addAll(newVos);
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, vos);
				result.setEhcacheId(ehcacheId);
				result.setFileName(file.getName());
				if (newVos != null && newVos.size() > 0) {
					RptMgrReportInfo info = getRptMgrReportInfo(newVos.get(0));
					if (null != info)
						result.setInfo(info.getRptId(), info.getRptNm());
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	public  RptMgrReportInfo getRptMgrReportInfo(CfgextWarnVO vo) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(vo.getRptNm())) {
			params.put("rptNm", vo.getRptNm());
		}
		return reportInfoDAO.getRptInfoByParams(params);
	}

	public List<ValidErrorInfoObj> easyImpValidWarn(String dsId, String ehcacheId) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<CfgextWarnVO> vos = (List<CfgextWarnVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<String> chackIds = new ArrayList<String>();
		// ???????????????????????????????????????
		List<String> rptNms = new ArrayList<String>();
		List<String> orgTypeList = new ArrayList<String>();
		for(CfgextWarnVO warnVO : vos){
			if(!rptNms.contains(warnVO.getRptNm())){
				rptNms.add(warnVO.getRptNm());
				RptMgrReportInfo info = getRptMgrReportInfo(warnVO);
				if (info != null) {
					if(!orgTypeList.contains(info.getBusiType())){
						orgTypeList.add(info.getBusiType());
					}
				}
			}
		}

		if (vos != null && vos.size() > 0) {
			for (CfgextWarnVO vo : vos) {
				if (StringUtils.isBlank(vo.getRptNm())) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("????????????");
					obj.setErrorMsg("???????????????????????????");
					errors.add(obj);
				}
				RptMgrReportInfo info = getRptMgrReportInfo(vo);
				if (info == null) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("????????????");
					obj.setErrorMsg("?????????" + vo.getRptNm() + "???????????????????????????????????????");
					errors.add(obj);
				}else {
					vo.setCfgId(info.getCfgId());
					vo.setBusiType(info.getBusiType());
					vo.setRptCycle(info.getRptCycle());
				}
				String sql = "select * from rpt_design_tmp_info t where t.template_id = ?0 and t.ver_start_date = ?1 and t.ver_end_date = ?2";
				List<Object[]> tmp = this.baseDAO.findByNativeSQLWithIndexParam(sql, vo.getCfgId(), vo.getStartDate(), vo.getEndDate());
				if(tmp.size() == 0){
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(9);
					obj.setValidTypeNm("????????????");
					obj.setErrorMsg("??????????????????????????????????????????["+vo.getRptNm()+"]???????????????????????????????????????????????????????????????????????????????????????");
					errors.add(obj);
				}
				chackIds.add(vo.getCheckId());
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(chackIds);
		lists.add(vos);
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, lists);
		return errors;
	}

	@Transactional(readOnly = false)
	public void saveValidWarnRel(List<CfgextWarnVO> cfgextLogic) {
		//????????????????????????
		for (CfgextWarnVO cfgextWarnVO : cfgextLogic) {
			ValidCfgextWarnVO validCfgextWarnVO = new ValidCfgextWarnVO();
			validCfgextWarnVO.setCheckId(cfgextWarnVO.getCheckId());
			validCfgextWarnVO.setCheckNm(cfgextWarnVO.getCheckNm());
			validCfgextWarnVO.setRptTemplateId(cfgextWarnVO.getCfgId());
			validCfgextWarnVO.setIndexNo(cfgextWarnVO.getIndexNo());
			validCfgextWarnVO.setCompareType(cfgextWarnVO.getCompareType());
			validCfgextWarnVO.setRptCycle(cfgextWarnVO.getRptCycle());
			validCfgextWarnVO.setRangeType(cfgextWarnVO.getRangeType());
			validCfgextWarnVO.setUnit(cfgextWarnVO.getUnit());
			validCfgextWarnVO.setMinusRangeVal(cfgextWarnVO.getMinusRangeVal());
			validCfgextWarnVO.setPostiveRangeVal(cfgextWarnVO.getPostiveRangeVal());
			validCfgextWarnVO.setIsFrs(cfgextWarnVO.getIsFrs());
			validCfgextWarnVO.setRemark(cfgextWarnVO.getRemark());
			validCfgextWarnVO.setStartDate(cfgextWarnVO.getStartDate());
			validCfgextWarnVO.setEndDate(cfgextWarnVO.getEndDate());

			saveWarn(validCfgextWarnVO, null);
		}
	}


	public BigDecimal multiplyUnit(String unit, BigDecimal value){
		BigDecimal val = new BigDecimal(0);
		switch (unit) {
			case "01"://???
				val = value;
				break;
			case "02"://???
				val = value.multiply(new BigDecimal(100));
				break;
			case "03"://???
				val = value.multiply(new BigDecimal(1000));
				break;
			case "04"://???
				val = value.multiply(new BigDecimal(10000));
				break;
			case "05"://???
				val = value.multiply(new BigDecimal(100000000));
				break;
		}
		return val;
	}

	public BigDecimal divideUnit(String unit, BigDecimal value){
		BigDecimal val = new BigDecimal(0);
		switch (unit) {
			case "01"://???
				val = value;
				break;
			case "02"://???
				val = value.divide(new BigDecimal(100));
				break;
			case "03"://???
				val = value.divide(new BigDecimal(1000));
				break;
			case "04"://???
				val = value.divide(new BigDecimal(10000));
				break;
			case "05"://???
				val = value.divide(new BigDecimal(100000000));
				break;
		}
		return val;
	}
}