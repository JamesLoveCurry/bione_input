package com.yusys.bione.plugin.wizard.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
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
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrBankExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.wizard.web.vo.ReportImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
@Service
public class ReportWizardRequire extends BaseController implements
		IWizardRequire {
	@Autowired
	private ExcelBS excelBS;
	
	@Autowired
	private RptMgrInfoMybatisDao rptDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(ReportImportVO.class, file);
		UploadResult result = new UploadResult();
		try {
			List<ReportImportVO> vos = (List<ReportImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, vos);
			
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				for (ReportImportVO vo : vos) {
					result.setInfo(vo.getRptNum(), vo.getRptNm());
				}
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId,String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists= (List<List<?>>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptMgrReportInfo> infos=(List<RptMgrReportInfo>) lists.get(0);
		List<RptMgrBankExt> exts=(List<RptMgrBankExt>) lists.get(1);
		List<RptMgrReportCatalog> catalogs=(List<RptMgrReportCatalog>) lists.get(2);
		List<String> field=new ArrayList<String>();
		field.add("rptNum");
		this.excelBS.deleteEntityJdbcBatch(infos,field);
		this.excelBS.deleteEntityJdbcBatch(exts, null);
		this.excelBS.saveEntityJdbcBatch(infos);
		this.excelBS.saveEntityJdbcBatch(exts);
		this.excelBS.saveEntityJdbcBatch(catalogs);
		EhcacheUtils.remove(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
	}
	
	private RptMgrReportCatalog getCatalogInfo(List<RptMgrReportCatalog> catalogs,String name,RptMgrReportCatalog catalog){
		RptMgrReportCatalog tempCatalog=new RptMgrReportCatalog();
		List<String> temp=new ArrayList<String>();
		temp.add("catalogNm");
		temp.add("upCatalogId");
		temp.add("extType");
		String upCatalogId="0";
		if(catalog!=null)
			upCatalogId=catalog.getCatalogId();
		tempCatalog.setUpCatalogId(upCatalogId);
		tempCatalog.setCatalogNm(name);
		tempCatalog.setExtType(GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		RptMgrReportCatalog catalogInfo  = null;
		List<RptMgrReportCatalog> catalogList = this.excelBS.validateExist(tempCatalog,RptMgrReportCatalog.class,temp);
		boolean flag=true;
		if(catalogs.size()>0){
			for(RptMgrReportCatalog ca:catalogs){
				if(ca.getCatalogNm().equals(name)&&ca.getUpCatalogId().equals(upCatalogId)){
					catalogInfo=ca;
					flag=false;
					break;
				}
			}
		}
		if(flag){
			if(catalogList!=null&&catalogList.size()>0){
				catalogInfo =catalogList.get(0);
			}
			if(catalogList==null||catalogList.size()==0){
				catalogInfo  =  new RptMgrReportCatalog();
				if(catalog==null)
					catalogInfo.setUpCatalogId("0");
				else
					catalogInfo.setUpCatalogId(catalog.getCatalogId());
				catalogInfo.setCatalogNm(name);
				catalogInfo.setCatalogId(RandomUtils.uuid2());
				catalogInfo.setExtType(GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
				catalogs.add(catalogInfo);
			}
			
		}
		return catalogInfo;
		
	}

	@Override
	public String export(String ids) {
		// TODO Auto-generated method stub
		String fileName="";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rptIds", ReBuildParam.splitLists(ArrayUtils.asList(ids, ",")));
		List<ReportImportVO> vos=this.rptDao.getExportReportInfo(params);
		if(vos!=null&&vos.size()>0){
			for(ReportImportVO vo:vos){
				List<String> catalogNm=new ArrayList<String>();
				setCatalogNm(catalogNm, vo.getCatalogId());
				if(catalogNm.size()>0){
					vo.setFirstCatalog(catalogNm.get(catalogNm.size()-1));
				}  
				if(catalogNm.size()>1){
					vo.setSecondCatalog(catalogNm.get(catalogNm.size()-2));
				}
				if(catalogNm.size()>2){
					vo.setThirdCatalog(catalogNm.get(catalogNm.size()-3));
				}
			}
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe=null;
		try {
			fileName=this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator+ RandomUtils.uuid2()+".xls";
			fe=new XlsExcelTemplateExporter(fileName,this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH +File.separator+ GlobalConstants4plugin.EXPORT_RPT_TEMPLATE_PATH,list);
			fe.run();
		} catch (Exception e) {
			fileName="";
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

	private void setCatalogNm(List<String> catalogNm,String catalogId){	
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("catalogId", catalogId);
		params.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		List<RptMgrReportCatalog> catalogs=this.rptDao.getCatalogById(params);
		if(catalogs!=null&&catalogs.size()>0){
			catalogNm.add(catalogs.get(0).getCatalogNm());
			if(!"0".equals(catalogs.get(0).getUpCatalogId())){
				setCatalogNm(catalogNm, catalogs.get(0).getUpCatalogId());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId,String ehcacheId) {
		// TODO Auto-generated method stub
		Map<String,String> rptNum=new HashMap<String, String>();
		Map<String,String> rptNm=new HashMap<String, String>();
		List<ValidErrorInfoObj> errors =new ArrayList<ValidErrorInfoObj>();
		List<ReportImportVO> vos=(List<ReportImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptMgrReportInfo> infos=new ArrayList<RptMgrReportInfo>();
		List<RptMgrBankExt> exts=new ArrayList<RptMgrBankExt>();
		List<RptMgrReportCatalog> catalogs=new ArrayList<RptMgrReportCatalog>();
		List<String> fields=new ArrayList<String>();
		fields.add("rptNum");
		if(vos!=null&&vos.size()>0){
			for(ReportImportVO vo:vos){
				try{
					Validator.validate(vo);
				}
				catch(ValidateException e){
					errors.addAll(e.getErrorInfoObjs());
				}
				if(rptNum.get(vo.getRptNum())!=null){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("唯一性校验");
					obj.setErrorMsg("报表编号不能重复");
					errors.add(obj);
				}
				if(rptNm.get(vo.getRptNm())!=null){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("唯一性校验");
					obj.setErrorMsg("报表名称不能重复");
					errors.add(obj);
				}
				rptNum.put(vo.getRptNum(), vo.getRptNum());
				rptNm.put(vo.getRptNm(), vo.getRptNm());
				RptMgrReportCatalog catalog=null;
				RptMgrReportInfo info=new RptMgrReportInfo();
				RptMgrBankExt ext=new RptMgrBankExt();
				String rptId=RandomUtils.uuid2();
				BeanUtils.copy(vo, info);
				BeanUtils.copy(vo, ext);
				if(StringUtils.isNotBlank(vo.getFirstCatalog())){
					catalog=getCatalogInfo(catalogs, vo.getFirstCatalog(), null);
				}
				if(StringUtils.isNotBlank(vo.getSecondCatalog())){
					catalog=getCatalogInfo(catalogs, vo.getSecondCatalog(), catalog);
				}
				if(StringUtils.isNotBlank(vo.getThirdCatalog())){
					if(!StringUtils.isNotBlank(vo.getSecondCatalog())){
						ValidErrorInfoObj obj=new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(4);
						obj.setValidTypeNm("目录层级空值校验");
						obj.setErrorMsg("二级目录为空值时,三级目录不能有值");
						errors.add(obj);
					}
					else{
						catalog=getCatalogInfo(catalogs, vo.getThirdCatalog(), catalog);
					}
					
				}
				if(catalog!=null){
					info.setCatalogId(catalog.getCatalogId());
				}
				else{
					info.setCatalogId("0");
				}
				fields.clear();
				fields.add("rptNum");
				List<RptMgrReportInfo> rptInfos=this.excelBS.validateExist(info, RptMgrReportInfo.class, fields);
				if(rptInfos!=null&&rptInfos.size()>0){
					rptId=rptInfos.get(0).getRptId();
					info.setCfgId(rptInfos.get(0).getCfgId());
				}
				else{
					String cfgId=RandomUtils.uuid2();
					info.setCfgId(cfgId);
				}
				info.setRptId(rptId);
				ext.setRptId(rptId);
				
				fields.clear();
				fields.add("rptId");
				List<RptMgrBankExt> bankInfos=this.excelBS.validateExist(ext, RptMgrBankExt.class, fields);
				if(bankInfos!=null&&bankInfos.size()>0){
					ext.setRequireExplain(bankInfos.get(0).getRequireExplain());
				}
				info.setExtType(GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
				info.setBusiType(GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
				info.setRptType(GlobalConstants4plugin.RPT_TYPE_OUTER);
				infos.add(info);
				exts.add(ext);
				
			}
			
		}
		List<List<?>> lists=new ArrayList<List<?>>();
		lists.add(infos);
		lists.add(exts);
		lists.add(catalogs);
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, lists);
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
