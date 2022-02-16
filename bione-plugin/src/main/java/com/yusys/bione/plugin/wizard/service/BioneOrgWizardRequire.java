package com.yusys.bione.plugin.wizard.service;


import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.authobj.web.vo.BioneOrgInfoVO;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.AbstractExcelImport;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.excel.ExcelImporter;
import com.yusys.bione.frame.excel.XlsExcelTemplateExporter;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;

@Service
public class BioneOrgWizardRequire extends BaseController implements
		IWizardRequire {
	private OrgBS orgBS = SpringContextHolder.getBean("orgBS");
	@Autowired
	private ExcelBS excelBS;
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(BioneOrgInfoVO.class, file);
		UploadResult result = new UploadResult();
		try {
			if(xlsImport != null){
				List<BioneOrgInfoVO> vos = (List<BioneOrgInfoVO>) xlsImport
						.ReadExcel();
				String ehcacheId = RandomUtils.uuid2();
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
						vos);
				result.setEhcacheId(ehcacheId);
				result.setFileName(file.getName());
				if (vos != null && vos.size() > 0) {
					for (BioneOrgInfoVO vo : vos) {
						result.setInfo(vo.getOrgNo(), vo.getOrgName());
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String export(String orgNos) {
		String fileName = "";
		List<BioneOrgInfo> infos = this.orgBS.getEntityListByProperty(BioneOrgInfo.class, "logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		List<BioneOrgInfoVO> vos = new ArrayList<BioneOrgInfoVO>();
		if(infos != null && infos.size() >0 ){
			for(BioneOrgInfo info :infos){
				BioneOrgInfoVO vo = new BioneOrgInfoVO();
				BeanUtils.copy(info, vo);
				vos.add(vo);
			}
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe = null;
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";
			fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_BIONEORG_TEMPLATE_PATH, list);
			fe.run();
		} catch (Exception e) {
			fileName = "";
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
		return fileName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<BioneOrgInfo> orgs = (List<BioneOrgInfo>) lists.get(0);
		List<String> fields = new ArrayList<String>();
		fields.add("orgNo");
		this.excelBS.deleteEntityJdbcBatch(orgs, fields);
		this.excelBS.saveEntityJdbcBatch(orgs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<BioneOrgInfo> orgs = new ArrayList<BioneOrgInfo>();
		List<BioneOrgInfoVO> vos = (List<BioneOrgInfoVO>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		if (vos != null && vos.size() > 0) {
			List<String> orgNoList = Lists.newArrayList();
			List<String> orgNmList = Lists.newArrayList();
			for (BioneOrgInfoVO vo : vos) {
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				//机构管理-模板导入校验重复的机构编号、机构名称 20190730
				if(orgNoList.contains(vo.getOrgNo())) {
					ValidErrorInfoObj vali = new ValidErrorInfoObj();
					vali.setExcelColNo(1);
					vali.setExcelRowNo(vo.getExcelRowNo());
					vali.setSheetName(vo.getSheetName());
					vali.setValidTypeNm("机构编号重复校验");
					vali.setErrorMsg("机构编号有重复记录");
					errors.add(vali);
				}else {
					orgNoList.add(vo.getOrgNo());
				}
				
				if(orgNmList.contains(vo.getOrgName())) {
					ValidErrorInfoObj vali = new ValidErrorInfoObj();
					vali.setExcelColNo(1);
					vali.setExcelRowNo(vo.getExcelRowNo());
					vali.setSheetName(vo.getSheetName());
					vali.setValidTypeNm("机构名称重复校验");
					vali.setErrorMsg("机构名称有重复记录");
					errors.add(vali);
				}else {
					orgNmList.add(vo.getOrgName());
				}
				
				BioneOrgInfo org = new BioneOrgInfo();
				BeanUtils.copy(vo, org);
				org.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
				org.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());;
				org.setOrgSts("1");
				org.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				org.setOrgId(vo.getOrgNo());
				orgs.add(org);
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(orgs);
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
				lists);
		return errors;
	}

	@Override
	public String exportAll(String ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidErrorInfoObj> validateVerInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveData(HttpServletRequest request, String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		
	}
	
}
