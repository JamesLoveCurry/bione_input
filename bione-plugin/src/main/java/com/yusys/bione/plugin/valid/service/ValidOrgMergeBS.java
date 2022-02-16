package com.yusys.bione.plugin.valid.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.yusys.bione.plugin.base.utils.EntityUtils;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextOrgmerge;
import com.yusys.bione.plugin.valid.repository.ValidOrgMergeMybatisDao;
import com.yusys.bione.plugin.valid.web.vo.CfgextOrgMergeVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;

@Service
@Transactional(readOnly = true)
public class ValidOrgMergeBS extends BaseBS<Object>{

	@Autowired
	private ValidOrgMergeMybatisDao orgMergeDao;

	@Autowired
	private RptMgrInfoMybatisDao reportInfoDAO;
	
	@Autowired
	private EntityUtils entityUtils;
	
	/**
	 * 查询列表
	 * @param pager
	 * @param templateId
	 * @return
	 */
	public Map<String, Object> list(Pager pager, String templateId) {
		Map<String, Object> param = new HashMap<String, Object>();
		if(StringUtils.isBlank(templateId)) {
			return null;
		}else{
			param.put("sumTemplateId", templateId);
		}
		String condition = pager.getCondition();
		//默认查询最新版本
		if(StringUtils.isBlank(condition) || condition.indexOf("t.endDate") < 0) {
			param.put("endDate", "29991231");
		}else{
			JSONObject obj = JSONObject.parseObject(condition);
			JSONArray array = JSONArray.parseArray(obj.getString("rules"));
			for(int i = 0; i < array.size(); i++){
				JSONObject job = array.getJSONObject(i);
				if(job.getString("field").equalsIgnoreCase("t.endDate")){
					param.put("startDate", job.getString("value"));
					//param.put("endDate", job.getString("value"));
				}
			}
		}
		if(pager.getCondition() != null ){
			pager.setCondition(StringUtils.replace(pager.getCondition(), "'", "''"));
		}
		PageHelper.startPage(pager);
		PageMyBatis<CfgextOrgMergeVO> page = (PageMyBatis<CfgextOrgMergeVO>) this.orgMergeDao.listOrgMerge(param);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Rows", page.getResult());
		result.put("Total", page.getTotalCount());
		return result;
	}
	/**
	 * 修改时获取反显数据
	 * @param checkId
	 * @return
	 */
	public CfgextOrgMergeVO getFormData(String checkId) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("checkId", checkId);
		List<CfgextOrgMergeVO> list = this.orgMergeDao.listOrgMerge(condition);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 保存
	 * @param rptValidCfgextOrgmerge
	 */
	@Transactional(readOnly = false)
	public void saveData(RptValidCfgextOrgmerge rptValidCfgextOrgmerge) {
		if(StringUtils.isBlank(rptValidCfgextOrgmerge.getCheckId())){//新增
			rptValidCfgextOrgmerge.setCheckId(RandomUtils.uuid2());
		}
		this.baseDAO.save(rptValidCfgextOrgmerge);
	}
	
	/**
	 * 删除
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void delete(String ids) {
		List<String> checkIdList = Arrays.asList(ids.split(","));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("ids", checkIdList);
		this.orgMergeDao.delete(param);
	}
	
	/**
	 * 缓存要导入的数据
	 * @param file
	 * @param ehcacheId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public UploadResult impValidOrgMerge(File file, String ehcacheId) {
		AbstractExcelImport xlsImport = null;
		xlsImport = new ExcelImporter(CfgextOrgMergeVO.class, file);
		UploadResult result = new UploadResult();
		try {
			if(xlsImport!=null){
				List<CfgextOrgMergeVO> vos = (List<CfgextOrgMergeVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
				if (null == vos) {
					vos = new ArrayList<>();
				}
				List<CfgextOrgMergeVO> newVos = (List<CfgextOrgMergeVO>) xlsImport.ReadExcel();
				vos.addAll(newVos);
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, vos);
				result.setEhcacheId(ehcacheId);
				result.setFileName(file.getName());
				if (newVos != null && newVos.size() > 0) {
					RptMgrReportInfo info = getRptMgrReportInfo(newVos.get(0).getSumRptNm());
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
	
	/**
	 * 校验导入的数据
	 * @param ehcacheId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ValidErrorInfoObj> easyImpValidOrgMerge(String ehcacheId) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<CfgextOrgMergeVO> vos = (List<CfgextOrgMergeVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<String> chackIds = new ArrayList<String>();

		if (vos != null && vos.size() > 0) {
			for (CfgextOrgMergeVO vo : vos) {
				chackIds.add(vo.getCheckId());
				//校验总行报表名称
				if (StringUtils.isBlank(vo.getSumRptNm())) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getSumRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(2);
					obj.setValidTypeNm("非空校验");
					obj.setErrorMsg("总行报表名称不允许为空");
					errors.add(obj);
				} else {
					//报表存在性校验
					RptMgrReportInfo info = getRptMgrReportInfo(vo.getSumRptNm());
					if (info == null) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getSumRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(2);
						obj.setValidTypeNm("存在性校验");
						obj.setErrorMsg("总行报表“" + vo.getSumRptNm() + "”不存在，请检查总行报表名称是否输入正确！");
						errors.add(obj);
					}else {
						vo.setSumTemplateId(info.getCfgId());
					}
					
					//日期校验
					String sql = "select * from rpt_design_tmp_info t where t.template_id = ?0 and t.ver_start_date = ?1 and t.ver_end_date = ?2";
					List<Object[]> tmp = this.baseDAO.findByNativeSQLWithIndexParam(sql, vo.getSumTemplateId(), vo.getStartDate(), vo.getEndDate());
					if(tmp.size() == 0){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getSumRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(7);
						obj.setValidTypeNm("日期校验");
						obj.setErrorMsg("校验公式的开始结束日期与报表["+vo.getSumRptNm()+"]的不一致，请在通用报表定制功能查看报表版本，填写正确的日期");
						errors.add(obj);
					}
					
					//指标存在性校验
					sql = "select * from rpt_idx_info t where t.template_id = ?0 and t.start_date = ?1 and t.end_date = ?2";
					List<Object[]> idxInfo = this.baseDAO.findByNativeSQLWithIndexParam(sql, vo.getSumTemplateId(), vo.getStartDate(), vo.getEndDate());
					if(idxInfo.size() == 0){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getSumIndexNo());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(3);
						obj.setValidTypeNm("存在性校验");
						obj.setErrorMsg("总行指标“" + vo.getSumIndexNo() + "”不存在，请检查总行指标编号是否输入正确！");
						errors.add(obj);
					}
				}
				
				//校验分行报表名称
				if (StringUtils.isBlank(vo.getBranchRptNm())) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getBranchRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(4);
					obj.setValidTypeNm("非空校验");
					obj.setErrorMsg("分行报表名称不允许为空");
					errors.add(obj);
				} else {
					RptMgrReportInfo branchInfo = getRptMgrReportInfo(vo.getBranchRptNm());
					if (branchInfo == null) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getBranchRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(4);
						obj.setValidTypeNm("存在性校验");
						obj.setErrorMsg("分行报表“" + vo.getBranchRptNm() + "”不存在，请检查分行报表名称是否输入正确！");
						errors.add(obj);
					}else {
						vo.setBranchTemplateId(branchInfo.getCfgId());
					}
					
					//指标存在性校验
					String sql = "select * from rpt_idx_info t where t.template_id = ?0";
					List<Object[]> idxInfo = this.baseDAO.findByNativeSQLWithIndexParam(sql, vo.getBranchTemplateId());
					if(idxInfo.size() == 0){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getBranchIndexNo());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(5);
						obj.setValidTypeNm("存在性校验");
						obj.setErrorMsg("分行指标“" + vo.getBranchIndexNo() + "”不存在，请检查分行指标编号是否输入正确！");
						errors.add(obj);
					}
				}
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(chackIds);
		lists.add(vos);
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, lists);
		return errors;
	}

	/**
	 * 获取报表信息
	 * @param rptNm
	 * @return
	 */
	public  RptMgrReportInfo getRptMgrReportInfo(String rptNm) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(rptNm)) {
			params.put("rptNm", rptNm);
		}
		return reportInfoDAO.getRptInfoByParams(params);
	}
	
	/**
	 * 保存导入的数据
	 * @param cfgextOrgMergeVOs
	 */
	@Transactional(readOnly = false)
	public void saveValidOrgMerge(List<CfgextOrgMergeVO> cfgextOrgMergeVOs) {
		List<RptValidCfgextOrgmerge> orgmergeList = new ArrayList<RptValidCfgextOrgmerge>();
		//保存预警校验公式
		for (CfgextOrgMergeVO cfgextOrgMergeVO : cfgextOrgMergeVOs) {
			RptValidCfgextOrgmerge rptValidCfgextOrgmerge = new RptValidCfgextOrgmerge();
			if(StringUtils.isBlank(cfgextOrgMergeVO.getCheckId())){
				cfgextOrgMergeVO.setCheckId(RandomUtils.uuid());
			}
			rptValidCfgextOrgmerge.setCheckId(cfgextOrgMergeVO.getCheckId());
			rptValidCfgextOrgmerge.setSumTemplateId(cfgextOrgMergeVO.getSumTemplateId());
			rptValidCfgextOrgmerge.setSumIndexNo(cfgextOrgMergeVO.getSumIndexNo());
			rptValidCfgextOrgmerge.setBranchTemplateId(cfgextOrgMergeVO.getBranchTemplateId());
			rptValidCfgextOrgmerge.setBranchIndexNo(cfgextOrgMergeVO.getBranchIndexNo());
			rptValidCfgextOrgmerge.setCheckDesc(cfgextOrgMergeVO.getCheckDesc());
			rptValidCfgextOrgmerge.setStartDate(cfgextOrgMergeVO.getStartDate());
			rptValidCfgextOrgmerge.setEndDate(cfgextOrgMergeVO.getEndDate());
			orgmergeList.add(rptValidCfgextOrgmerge);
		}
		entityUtils.saveEntityJdbcBatch(orgmergeList);
	}
	
	/**
	 * 导出校验公式
	 * @param ids
	 * @param templateId
	 * @param realPath
	 * @param endDate
	 * @param sumRptNm
	 * @return
	 */
	public File expValidOrgMerge(String ids, String templateId, String realPath, String endDate, String sumRptNm) {
		File resFile = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> checkIds = new ArrayList<>();
		if("all".equals(ids)){
			if(StringUtils.isNotBlank(endDate)){
				condition.put("endDate", endDate);
			}else{
				condition.put("endDate", "29991231");//默认导出最新版本的校验公式
			}
			condition.put("sumTemplateId", templateId);
		} else {
			checkIds = Arrays.asList(ids.split(","));
			condition.put("checkIds", checkIds);
		}

		List<CfgextOrgMergeVO> vos = this.orgMergeDao.listOrgMerge(condition);
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe = null;
		if(StringUtils.isBlank(sumRptNm)) {
			sumRptNm = RandomUtils.uuid2();
		}
		try {
			if (FilepathValidateUtils.validateFilepath(realPath) && FilepathValidateUtils.validateFilepath(sumRptNm)) {
				resFile = new File(realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH
						+ File.separator + sumRptNm + ".xls");
				fe = new XlsExcelTemplateExporter(resFile, realPath
						+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
						+ GlobalConstants4plugin.EXPORT_VALIDORGMERGE_TEMPLATE_PATH, list);
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
}