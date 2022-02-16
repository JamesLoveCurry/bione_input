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
			level1.setLevelNm("预警");
			level1.setLevelType("01");
			level1.setMinusRangeVal(new BigDecimal(0));
			level1.setPostiveRangeVal(new BigDecimal(0));
			level1.setRemindColor("660000");
			list.add(level1);
			
			RptValidWarnLevel level = new RptValidWarnLevel();
			level.setIsPassCond("0");
			level.setLevelNm("报警");
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
	 * 保存-异动校验
	 * @param warn
	 * @param params 
	 */
	@Transactional(readOnly = false)
	public void saveWarn(ValidCfgextWarnVO warn, Map<String, Object> params) {
		//保存异动校验配置
		RptValidCfgextWarn rptValidCfgextWarn = new RptValidCfgextWarn();
		if(warn.getCheckId() == null || warn.getCheckId().equals("")){//新增
			rptValidCfgextWarn.setCheckId(RandomUtils.uuid2());
		}else{//修改	
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
				case "01"://日
					rptValidCfgextWarn.setCompareValType("01");//环比（上日）
					break;
				case "02"://月
					rptValidCfgextWarn.setCompareValType("03");//环比（上月月末）
					break;
				case "03"://季
					rptValidCfgextWarn.setCompareValType("06");//环比（上季季末）
					break;
				case "04"://年
					rptValidCfgextWarn.setCompareValType("08");//环比（上年年末）
					break;
				case "12"://半年
					rptValidCfgextWarn.setCompareValType("10");//环比（上半年末）
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

		if(params == null){//params==null用于前台保存
			rptValidCfgextWarn.setStartDate(warn.getStartDate());
			rptValidCfgextWarn.setEndDate(warn.getEndDate());
		}else{//params！=null用于导入，校验公式的开始结束日期来源于模板的开始结束日期
			//根据模板id和版本号获取开始结束日期
			BigDecimal verId = new BigDecimal(params.get("verId").toString());//导入的版本
			String templateId = warn.getRptTemplateId();
			String busiType = params.get("busiType").toString();
			//查询模板表，查看导入版本的模板是否存在
			String selectJql = "select t from RptDesignTmpInfo t where t.id.templateId = ?0 and t.id.verId = ?1";
			RptDesignTmpInfo tmp = this.baseDAO.findUniqueWithIndexParam(selectJql, templateId, verId);
			if(tmp != null){//如果模板存在
				rptValidCfgextWarn.setStartDate(tmp.getVerStartDate());
				rptValidCfgextWarn.setEndDate(tmp.getVerEndDate());
			}else{//新版本导入
				//导入新版本，开始日期为版本开始日期
				String jql = " select t.ver_start_date,t.ver_end_date from frs_system_cfg t where t.system_ver_id = ?0 and t.busi_type = ?1";
				Object[] obj = this.baseDAO.findByNativeSQLWithIndexParam(jql, verId.longValue(), busiType).get(0);
				rptValidCfgextWarn.setStartDate(obj[0].toString());
				//若存在下一个版本，将新版本的结束日期改为下一个版本的开始日期
				String nextjql = " select min(t.id.verId) from RptDesignTmpInfo t where t.id.templateId = ?0 and t.id.verId > ?1";
				BigDecimal nextVer = this.baseDAO.findUniqueWithIndexParam(nextjql, templateId, verId);//找到小于要更新版本的最大版本
				if(nextVer != null){//存在下一个版本，查询开始日期
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
		
		//保存警报级别
		RptValidWarnLevel level = new RptValidWarnLevel();
		RptValidWarnLevelPK pk = new RptValidWarnLevelPK();
		if(warn.getLevelNum() == null || warn.getCheckId().equals("")){//新增
			pk.setLevelNum(RandomUtils.uuid2());
		}else{//修改
			pk.setLevelNum(warn.getLevelNum());
		}
		pk.setCheckId(rptValidCfgextWarn.getCheckId());

		level.setId(pk);
		level.setIndexNo(warn.getIndexNo());
		level.setLevelNm(warn.getLevelNm() == null ? "预警" : warn.getLevelNm());
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
	 * 根据检验公式id和报表模板id1删除警戒值校验公式
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
	 * 根据模板id和结束日期，删除对应的校验公式
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
	 * 根据模板编号开始结束日期更新检验公式
	 * @param templateId
	 * @param startDate 要删除版本的开始日期
	 * @param endDate 要删除版本的结束日期
	 */
	@Transactional(readOnly = false)
	public void updateValidVer(String templateId, String startDate, String endDate) {
		if (StringUtils.isNotBlank(templateId) && StringUtils.isNotBlank(endDate) && StringUtils.isNotBlank(startDate)) {
			String jql = "update RptValidCfgextWarn info set info.endDate = :endDate where info.rptTemplateId = :templateId and info.endDate = :startDate";//更新上一个版本的结束日期
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("endDate", endDate);
			params.put("startDate", startDate);
			params.put("templateId", templateId);
			this.baseDAO.batchExecuteWithNameParam(jql, params);
		}
	}
	
	/**
	 * 根据指标编号删除检验公式
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
				//进行1000为一个单元的分割
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
	 * @方法描述: 生成下载文件
	 * @创建人: huzq1 
	 * @创建时间: 2021/7/10 10:47
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
				condition.put("endDate", "29991231");//默认导出最新版本的校验公式
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
		// 查询所有报表所属的业务类型
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
					obj.setValidTypeNm("非空校验");
					obj.setErrorMsg("报表名称不允许为空");
					errors.add(obj);
				}
				RptMgrReportInfo info = getRptMgrReportInfo(vo);
				if (info == null) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("报表校验");
					obj.setErrorMsg("报表“" + vo.getRptNm() + "”不存在，请先导入对应报表");
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
					obj.setValidTypeNm("日期校验");
					obj.setErrorMsg("校验公式的开始结束日期与报表["+vo.getRptNm()+"]的不一致，请在通用报表定制功能查看报表版本，填写正确的日期");
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
		//保存预警校验公式
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
			case "01"://元
				val = value;
				break;
			case "02"://百
				val = value.multiply(new BigDecimal(100));
				break;
			case "03"://千
				val = value.multiply(new BigDecimal(1000));
				break;
			case "04"://万
				val = value.multiply(new BigDecimal(10000));
				break;
			case "05"://亿
				val = value.multiply(new BigDecimal(100000000));
				break;
		}
		return val;
	}

	public BigDecimal divideUnit(String unit, BigDecimal value){
		BigDecimal val = new BigDecimal(0);
		switch (unit) {
			case "01"://元
				val = value;
				break;
			case "02"://百
				val = value.divide(new BigDecimal(100));
				break;
			case "03"://千
				val = value.divide(new BigDecimal(1000));
				break;
			case "04"://万
				val = value.divide(new BigDecimal(10000));
				break;
			case "05"://亿
				val = value.divide(new BigDecimal(100000000));
				break;
		}
		return val;
	}
}