package com.yusys.bione.plugin.wizard.service;

import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.*;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.repository.mybatis.RptDataSetDao;
import com.yusys.bione.plugin.datamodel.service.RptDatasetBS;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.repository.mybatis.RptDimDao;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;
import com.yusys.bione.plugin.wizard.web.vo.DataSetImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ModelWizardRequire extends BaseController implements
		IWizardRequire {
	@Autowired
	private ExcelBS excelBS ;
	@Autowired
	private RptDatasetBS setBs ;
	@Autowired
	private RptDimDao rptDimDao ;
	@Autowired
	private RptDataSetDao rptDataSetDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(DataSetImportVO.class, file);
		
		UploadResult result = new UploadResult();
		try {
			List<DataSetImportVO> vos = (List<DataSetImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, vos);
			
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				for (DataSetImportVO vo : vos) {
					result.setInfo(vo.getTableEnNm() + "." + vo.getEnNm(), vo.getTableEnNm() + "." + vo.getEnNm());
					vo.setTableEnNm(vo.getTableEnNm().toUpperCase());
					vo.setEnNm(vo.getEnNm().toUpperCase());
				} 
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void saveFirstCatalog(Map<String, RptSysModuleCatalog> catalogToBuild, 
			Map<String, RptSysModuleCatalog> newCatalog,
			DataSetImportVO vo){
		boolean flag = false;
		for(String tmp : catalogToBuild.keySet()){
			if((vo.getFirstcatalogNm() + "," ).equals(tmp)){
				vo.setCatalogId(catalogToBuild.get(tmp).getCatalogId());
				flag = true;
			}
		}
		if(!flag){
			RptSysModuleCatalog catalog = new RptSysModuleCatalog();
			String catalogId=RandomUtils.uuid2();
			vo.setCatalogId(catalogId);
			catalog.setCatalogId(catalogId);
			catalog.setCatalogNm(vo.getFirstcatalogNm());
			catalog.setUpId("0");
			catalogToBuild.put(vo.getFirstcatalogNm()+ ",", catalog);
		}
	}
	public void saveSecondCatalog(Map<String, RptSysModuleCatalog> catalogToBuild, 
			Map<String, RptSysModuleCatalog> newCatalog,
			DataSetImportVO vo){
		boolean flag = false;
		for(String tmp : catalogToBuild.keySet()){
			if((vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",").equals(tmp)){
				vo.setCatalogId(catalogToBuild.get(tmp).getCatalogId());
				flag = true;
			}
		}
		if(!flag){
			RptSysModuleCatalog catalog = new RptSysModuleCatalog();
			String catalogId=RandomUtils.uuid2();
			vo.setCatalogId(catalogId);
			catalog.setCatalogId(catalogId);
			catalog.setCatalogNm(vo.getSecondCatalog());
			catalog.setUpId(newCatalog.get(vo.getFirstcatalogNm()+ ",") != null 
					? newCatalog.get(vo.getFirstcatalogNm()+ ",").getCatalogId() : catalogToBuild.get(vo.getFirstcatalogNm()+ ",").getCatalogId());
			catalogToBuild.put(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",", catalog);
		}
	}
	
	public void saveThirdCatalog(Map<String, RptSysModuleCatalog> catalogToBuild, 
			Map<String, RptSysModuleCatalog> newCatalog,
			DataSetImportVO vo){
		boolean flag = false;
		for(String tmp : catalogToBuild.keySet()){
			if((vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + "," + vo.getThirdCatalog() + ",").equals(tmp)){
				vo.setCatalogId(catalogToBuild.get(tmp).getCatalogId());
				flag = true;
			}
		}
		if(!flag){
			RptSysModuleCatalog catalog = new RptSysModuleCatalog();
			String catalogId=RandomUtils.uuid2();
			vo.setCatalogId(catalogId);
			catalog.setCatalogId(catalogId);
			catalog.setCatalogNm(vo.getThirdCatalog());
			catalog.setUpId(newCatalog.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",") != null ?
					newCatalog.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",").getCatalogId() : 
					catalogToBuild.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",").getCatalogId());
			catalogToBuild.put(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + "," + vo.getThirdCatalog() + ",", catalog);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		List<DataSetImportVO> vos = (List<DataSetImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<ValidErrorInfoObj> errorList = new ArrayList<ValidErrorInfoObj>();
		if(vos != null && vos.size() > 0){
			Map<String, String> tableMap = new HashMap<String, String>();
			Map<String,String> setIdMxMap = new HashMap<>();//???????????????????????????????????????Id??????????????????
			Map<String,String> isPkMap = new HashMap<>();//???????????????????????????????????????????????????Id??????????????????
			
			List<RptSysModuleCatalog> catalogList = this.rptDataSetDao.getCatalogById(new HashMap<String, Object>());
			
			Map<String, RptSysModuleCatalog> catalogMap = new HashMap<String, RptSysModuleCatalog>();
			Map<String, RptSysModuleCatalog> newCatalog = new HashMap<String, RptSysModuleCatalog>();
			
			for(RptSysModuleCatalog tmp : catalogList){
				catalogMap.put(tmp.getCatalogId(), tmp);//???????????????catalog
			}
			
			for(int i = 0;i<catalogList.size();i++){
				List<String> names = new ArrayList<String>(); //??????
				RptSysModuleCatalog catalog = catalogMap.get(catalogList.get(i).getCatalogId());
				names.add(catalog.getCatalogNm());
				while(catalogMap.get(catalog.getUpId()) != null){
					catalog = catalogMap.get(catalog.getUpId());
					names.add(catalog.getCatalogNm()) ;
				}
				String s = new String("");
				if(names != null && names.size() > 0){
					for(int j = names.size() - 1; j >= 0;j--){
						s += names.get(j) + ",";
					}
				}
				newCatalog.put(s, catalogList.get(i));
			}
			Map<String, String> srcSysCodeMap = new HashMap<String, String>();
			srcSysCodeMap.put("02", "??????");
			srcSysCodeMap.put("03", "CRM");
			
			Map<String, String> setTypeMap = new HashMap<String, String>();
			setTypeMap.put("00", "????????????");
			setTypeMap.put("01", "????????????");
			setTypeMap.put("02", "????????????");
			setTypeMap.put("03", "????????????");

			Map<String, RptSysModuleCatalog> catalogToBuild = new HashMap<String, RptSysModuleCatalog>();
			
			List<BioneParamInfo> params = this.excelBS.getEntityListByProperty(BioneParamInfo.class, "paramTypeNo", GlobalConstants4plugin.TREE_PARAM_TYPE);
			List<String> busiTypes = new ArrayList<String>();
			if(params != null && params.size() > 0 ){
				for(BioneParamInfo param : params){
					busiTypes.add(param.getParamValue());
				}
			}
			
			for(int i=0;i<vos.size();i++){
				DataSetImportVO vo = vos.get(i);
				vo.setTableEnNm(vo.getTableEnNm());
				vo.setEnNm(vo.getEnNm());
				tableMap.put(vo.getSetNm() + "," + vo.getTableEnNm(), vo.getTableEnNm());//???
				
				try{
					Validator.validate(vo);
				}
				catch(ValidateException e){
					errorList.addAll(e.getErrorInfoObjs());
					continue;
				}

				//?????????????????????(setType??????00??????????????????????????????????????????,????????????setIdMxList???isPkList????????????ID???????????????
				if("00".equals(vo.getSetType())==true){
					if (!setIdMxMap.containsKey(vo.getSetId())) {//?????????????????????ID??????setIdMxMap??????????????????ID??????????????????????????????setIdMxMap
						setIdMxMap.put(vo.getSetId(), vo.getSheetName());
					}
					if ("Y".equals(vo.getIsPk()) && !isPkMap.containsKey(vo.getSetId())) {//?????????????????????,??????isPkMap
						isPkMap.put(vo.getSetId(), vo.getSheetName());
					}
				}
				//?????????????????????????????????
				if(null != vo.getSetType() && null == setTypeMap.get(vo.getSetType())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(6);
					obj.setValidTypeNm("???????????????");
					obj.setErrorMsg("???????????????????????????");
					errorList.add(obj);
					}
				
				//????????????
				if(!StringUtils.isNotBlank(vo.getFirstcatalogNm()) ){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("????????????????????????");
					obj.setErrorMsg("?????????????????????");
					errorList.add(obj);
				}
				if(StringUtils.isNotBlank(vo.getThirdCatalog())){
					if(StringUtils.isBlank(vo.getSecondCatalog())){
						ValidErrorInfoObj obj=new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(5);
						obj.setValidTypeNm("????????????????????????");
						obj.setErrorMsg("????????????????????????,????????????????????????");
						errorList.add(obj);
					}
				}
				if(StringUtils.isNotBlank(vo.getBusiType())){
					if(StringUtils.isBlank(vo.getBusiType())||!busiTypes.contains(vo.getBusiType())){
						ValidErrorInfoObj obj=new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(12);
						obj.setValidTypeNm("???????????????");
						obj.setErrorMsg("?????????????????????");
						errorList.add(obj);
					}
				}
				//???????????? end
				//?????????????????????????????????????????????????????????????????????
				for(int j=i+1;j<vos.size();j++){
					DataSetImportVO y = vos.get(j);
					if(vo.getSetNm().equals(y.getSetNm()) && !vo.getTableEnNm().equals(y.getTableEnNm())){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(7);
						obj.setValidTypeNm("???????????????????????????");
						obj.setErrorMsg("???????????????????????????");
						errorList.add(obj);
						break;
					}
					if(vo.getSetNm().equals(y.getSetNm()) && vo.getTableEnNm().equals(y.getTableEnNm()) && vo.getEnNm().equals(y.getEnNm())){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(10);
						obj.setValidTypeNm("?????????????????????");
						obj.setErrorMsg("???????????????????????????");
						errorList.add(obj);
						break;
					}
					//?????????????????????????????????????????????
					if(vo.getSetType().equals("01")){
						if(vo.getSetNm().equals(y.getSetNm()) && vo.getTableEnNm().equals(y.getTableEnNm()) && !StringUtils.isEmpty(vo.getDimTypeNo()) && !StringUtils.isEmpty(y.getDimTypeNo()) && vo.getDimTypeNo().equals(y.getDimTypeNo())){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(11);
							obj.setValidTypeNm("????????????");
							obj.setErrorMsg("??????????????????");
							errorList.add(obj);
							break;
						}
					}
				}
				
				if(StringUtils.isNotBlank(vo.getFirstcatalogNm()) && !StringUtils.isNotBlank(vo.getSecondCatalog())){
					if(newCatalog.get(vo.getFirstcatalogNm() + ",") != null){//?????????????????????
						vo.setCatalogId(newCatalog.get(vo.getFirstcatalogNm() + ",").getCatalogId());
					}else{
						saveFirstCatalog(catalogToBuild, newCatalog, vo);
					}
				}else if(StringUtils.isNotBlank(vo.getFirstcatalogNm()) && StringUtils.isNotBlank(vo.getSecondCatalog()) && !StringUtils.isNotBlank(vo.getThirdCatalog())){
					if(newCatalog.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",") != null){//????????????????????????
						vo.setCatalogId(newCatalog.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",").getCatalogId());
					}else if(newCatalog.get(vo.getFirstcatalogNm() + ",") != null){//??????????????????
						//??????????????????
						saveSecondCatalog(catalogToBuild, newCatalog, vo);
					}else {//?????????????????????
						//??????????????????
						saveFirstCatalog(catalogToBuild, newCatalog, vo);
						saveSecondCatalog(catalogToBuild, newCatalog, vo);
					}
					
					
				}else if(StringUtils.isNotBlank(vo.getFirstcatalogNm()) && StringUtils.isNotBlank(vo.getSecondCatalog()) && StringUtils.isNotBlank(vo.getThirdCatalog())){
					if(newCatalog.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + "," + vo.getThirdCatalog() + ",") != null){//????????????????????????
						vo.setCatalogId(newCatalog.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + "," + vo.getThirdCatalog() + ",").getCatalogId());
					}else if(newCatalog.get(vo.getFirstcatalogNm() + "," + vo.getSecondCatalog() + ",") != null ){//?????????????????????
						
						saveThirdCatalog(catalogToBuild, newCatalog, vo);
						
					}else if(newCatalog.get(vo.getFirstcatalogNm() + ",") != null){//?????????????????????
						saveSecondCatalog(catalogToBuild, newCatalog, vo);
						saveThirdCatalog(catalogToBuild, newCatalog, vo);
						
					}else{//??????????????????????????????
						saveFirstCatalog(catalogToBuild, newCatalog, vo);
						saveSecondCatalog(catalogToBuild, newCatalog, vo);
						saveThirdCatalog(catalogToBuild, newCatalog, vo);
					}
				}
			}

			//???????????????????????????,??????setIdMxMap???isPkMap?????????????????????????????????
			Iterator it=setIdMxMap.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String,String> entry = (Map.Entry<String, String>) it.next();
				if(!isPkMap.containsKey(entry.getKey())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(entry.getValue());
					obj.setExcelColNo(14);
					obj.setValidTypeNm("????????????????????????");
					obj.setErrorMsg("??????????????????ID???"+entry.getKey()+"?????????????????????????????????????????????");
					errorList.add(obj);
				}
			}
			
			/*List<RptSysModuleCatalog> catalogList = new ArrayList<RptSysModuleCatalog>();
			for(String key : catalogMap.keySet()){
				RptSysModuleCatalog catalog = new RptSysModuleCatalog();
				String catalogId = RandomUtils.uuid2();
				catalog.setCatalogId(catalogId);
				catalog.setUpId("0");
				catalog.setCatalogNm(key);
				catalogMap.put(key, catalogId);
				catalogList.add(catalog);
			}*/
			List<String> tableList = new ArrayList<String>();
			for(String tmp : tableMap.keySet()){
				tableList.add(tableMap.get(tmp));
			}
			List<RptDimTypeInfo> dimList = this.rptDimDao.getAllRptDimTypeInfos();
			Map<String, String> dimMap = new HashMap<String, String>();
			for(RptDimTypeInfo tmp : dimList){
				dimMap.put(tmp.getDimTypeNm(), tmp.getDimTypeNo());
			}
			
			List<RptIdxMeasureInfo> allMeasures = rptDataSetDao.getAllMeasures(new HashMap<>());
			Map<String, String> measureMap = new HashMap<String, String>();
			for (RptIdxMeasureInfo measure : allMeasures) {
				measureMap.put(measure.getMeasureNm(), measure.getMeasureNo());
			}

			Map<String, List<RptSysModuleCol>> list = this.setBs.getFieldsOfTableList(dsId, tableList);
			//???????????????setId
			Map<String, String> oldModuleInfo = new HashMap<String, String>();
			List<RptSysModuleInfo> oldModuleList = this.rptDataSetDao.getModuleInfo(new HashMap<String, Object>());
			for(RptSysModuleInfo info : oldModuleList){
				oldModuleInfo.put(info.getSetId(), info.getSetId());//info.getCatalogId() + "," + info.getSetNm()
			}
			//???????????????setId ??????
			Map<String,Integer> sysOrderMap=new HashMap<String, Integer>();
			Set<RptSysModuleInfo> moduleList = new HashSet<RptSysModuleInfo>();
			List<RptSysModuleCol> colList = new ArrayList<RptSysModuleCol>();
			for(DataSetImportVO vo : vos){
				RptSysModuleInfo info = new RptSysModuleInfo();
				RptSysModuleCol col = new RptSysModuleCol();
				try{
					Validator.validate(vo);
				}
				catch(ValidateException e){
					continue;
				}
				
			//	info.setCatalogId(catalogMap.get(vo.getCatalogNm()));
				if(tableMap.keySet().contains(vo.getSetNm() + "," + vo.getTableEnNm()) && tableMap.get(vo.getSetNm() + "," + vo.getTableEnNm()).equals(vo.getTableEnNm())){//?????????
					if(oldModuleInfo.get(vo.getSetId()) != null){
						tableMap.put(vo.getSetNm() + "," + vo.getTableEnNm(), oldModuleInfo.get(vo.getSetId()));
					}else{
						tableMap.put(vo.getSetNm() + "," + vo.getTableEnNm(),vo.getSetId());//RandomUtils.uuid2()
					}
				}else if(!tableMap.keySet().contains(vo.getSetNm() + "," + vo.getTableEnNm())){
					//??????????????????
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(7);
					obj.setValidTypeNm("???????????????????????????");
					obj.setErrorMsg("????????????????????????????????????");
					errorList.add(obj);
				}
				info.setSetType(GlobalConstants4plugin.SET_TYPE_MUTI_DIM);
				info.setCatalogId(vo.getCatalogId());
				info.setSetId(tableMap.get(vo.getSetNm() + "," + vo.getTableEnNm()));
				info.setSetNm(vo.getSetNm());
				info.setTableEnNm(vo.getTableEnNm());
				info.setSourceId(dsId);
				info.setBusiType(vo.getBusiType());
				info.setSetType(vo.getSetType());
				info.setRemark(vo.getRemark());
				List<RptSysModuleCol> colDbList = list.get(vo.getTableEnNm().toUpperCase());
				if(colDbList==null||colDbList.size()<=0){
					colDbList = list.get(vo.getTableEnNm().toLowerCase());
				}
				if(colDbList != null && colDbList.size() > 0){
					Map<String, String> colMap = new HashMap<String, String>();
					List<RptSysModuleCol> cols = this.setBs.getEntityListByProperty(RptSysModuleCol.class, 
							"setId", tableMap.get(vo.getSetNm() + "," + vo.getTableEnNm()));
					if(null != cols && cols.size() > 0){
						for(RptSysModuleCol co : cols){
							colMap.put(co.getEnNm(), co.getColId());
						}
					}
					for(RptSysModuleCol tmp : colDbList){
						if(tmp.getEnNm().toUpperCase().equals(vo.getEnNm().toUpperCase())){
							BeanUtils.copy(tmp, col);
							col.setColId(null == colMap.get(col.getEnNm()) ? RandomUtils.uuid2() : colMap.get(col.getEnNm()));
							col.setCnNm(vo.getCnNm());
							col.setSetId(tableMap.get(vo.getSetNm() + "," + vo.getTableEnNm()));
							col.setColType(vo.getColType());
							if(col.getColType().equals(GlobalConstants4plugin.COL_TYPE_DIM)){
								if(StringUtils.isBlank(vo.getDimTypeNo() )){
									//????????????
									ValidErrorInfoObj obj=new ValidErrorInfoObj();
									obj.setSheetName(vo.getSheetName());
									obj.setExcelRowNo(vo.getExcelRowNo());
									obj.setExcelColNo(12);
									obj.setValidTypeNm("??????????????????");
									obj.setErrorMsg("???????????????????????????");
									errorList.add(obj);
								}else  if(dimMap.get(vo.getDimTypeNo()) != null){
									col.setDimTypeNo(dimMap.get(vo.getDimTypeNo()));
								}else{
									//????????????
									ValidErrorInfoObj obj=new ValidErrorInfoObj();
									obj.setSheetName(vo.getSheetName());
									obj.setExcelRowNo(vo.getExcelRowNo());
									obj.setExcelColNo(12);
									obj.setValidTypeNm("??????????????????");
									obj.setErrorMsg("????????????????????????");
									errorList.add(obj);
								}
							}else if(col.getColType().equals(GlobalConstants4plugin.COL_TYPE_MEASURE)){
								if(StringUtils.isBlank(vo.getMeasureNm() )){
									//????????????
									ValidErrorInfoObj obj=new ValidErrorInfoObj();
									obj.setSheetName(vo.getSheetName());
									obj.setExcelRowNo(vo.getExcelRowNo());
									obj.setExcelColNo(13);
									obj.setValidTypeNm("??????????????????");
									obj.setErrorMsg("???????????????????????????");
									errorList.add(obj);
								}else  if(measureMap.get(vo.getMeasureNm()) != null){
									col.setMeasureNo(measureMap.get(vo.getMeasureNm()));
								}else{
									//????????????
									ValidErrorInfoObj obj=new ValidErrorInfoObj();
									obj.setSheetName(vo.getSheetName());
									obj.setExcelRowNo(vo.getExcelRowNo());
									obj.setExcelColNo(13);
									obj.setValidTypeNm("??????????????????");
									obj.setErrorMsg("????????????????????????");
									errorList.add(obj);
								}
//								//?????????????????????????????? ????????????
//								if(!col.getDbType().equals(com.yusys.biome.frame.base.common.GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER)){
//									ValidErrorInfoObj obj=new ValidErrorInfoObj();
//									obj.setSheetName(vo.getSheetName());
//									obj.setExcelRowNo(vo.getExcelRowNo());
//									obj.setExcelColNo(8);
//									obj.setValidTypeNm("??????????????????");
//									obj.setErrorMsg("?????????????????????????????????????????????????????????");
//									errorList.add(obj);
//								}
							}
							if((col.getColType().equals(GlobalConstants4plugin.COL_TYPE_NORMAL) || col.getColType().equals(GlobalConstants4plugin.COL_TYPE_MEASURE)) && StringUtils.isNotBlank(vo.getDimTypeNo())){
								//????????????
								ValidErrorInfoObj obj=new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(12);
								obj.setValidTypeNm("??????????????????");
								obj.setErrorMsg("??????????????????????????????????????????????????????????????????????????????");
								errorList.add(obj);
							}
						}
					}
				}
				
				if(col.getSetId() == null || col.getSetId().equals("")){
					//????????????
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(10);
					obj.setValidTypeNm("???????????????????????????");
					obj.setErrorMsg("???????????????????????????");
					errorList.add(obj);
				}
				moduleList.add(info);
				if(sysOrderMap.get(col.getSetId())==null){
					col.setOrderNum(new BigDecimal(1));
					sysOrderMap.put(col.getSetId(), 1);
				}
				else{
					col.setOrderNum(new BigDecimal(sysOrderMap.get(col.getSetId())+1));
					sysOrderMap.put(col.getSetId(), sysOrderMap.get(col.getSetId())+1);
				}
				col.setIsPk(vo.getIsPk());//????????????????????????colList
				colList.add(col);
			}
			
			
			List<RptSysModuleCatalog> newCatalogList = new ArrayList<RptSysModuleCatalog>();
			for(String tmp : catalogToBuild.keySet()){
				newCatalogList.add(catalogToBuild.get(tmp));
			}
			
			List<RptSysModuleInfo> newModuleList = new ArrayList<RptSysModuleInfo>();
			for(RptSysModuleInfo tmp : moduleList){
				newModuleList.add(tmp);
			}
			Map<String, List<?>> resultMap = new HashMap<String, List<?>>();
			resultMap.put("catalog", newCatalogList);
			resultMap.put("module", newModuleList);
			resultMap.put("col", colList);
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, resultMap);
		}
		
		return errorList;
	}

	
	@Override
	@Transactional(readOnly = false)
	public void saveData(String ehcacheId, String dsId) {
		@SuppressWarnings("unchecked")
		Map<String, List<?>> map = (Map<String, List<?>>)EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		if(map != null){
			
			if(map.get("catalog") != null){
				this.excelBS.saveEntityJdbcBatch(map.get("catalog"));
			}
			
			if(map.get("module") != null){
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("list", map.get("module"));
				this.rptDataSetDao.deleteModuleCol(params);
				this.rptDataSetDao.deleteModuleInfo(params);
				for(int i=0;i<map.get("module").size();i++){
					this.rptDataSetDao.saveModuleInfo((RptSysModuleInfo) map.get("module").get(i));
				}
			}
			
			if(map.get("col") != null){
				//this.excelBS.saveEntityJdbcBatch(map.get("col"));//????????????????????????????????????mysql
				for(int i=0;i<map.get("col").size();i++){
					this.rptDataSetDao.saveModuleCol((RptSysModuleCol)map.get("col").get(i));
				}
			}
		}

	}
	
	
	@Override
	public String export(String ids) {
		String fileName="";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("setIds", ArrayUtils.asList(ids, ","));
		List<String> setTypes = new ArrayList<String>();
		setTypes.add(GlobalConstants4plugin.SET_TYPE_MUTI_DIM);
		setTypes.add(GlobalConstants4plugin.SET_TYPE_DETAIL);
		setTypes.add(GlobalConstants4plugin.SET_TYPE_GENERIC);
		setTypes.add(GlobalConstants4plugin.SET_TYPE_SUM);
		params.put("setTypes", setTypes);
		List<DataSetImportVO> vos=this.rptDataSetDao.getExportModelInfo(params);
		List<RptSysModuleCatalog> catalogs = this.rptDataSetDao.getCatalogById(params);
		Map<String, RptSysModuleCatalog> catalogMap = new HashMap<String, RptSysModuleCatalog>();
		Map<String, List<String>> newCatalog = new HashMap<String, List<String>>();
		for(RptSysModuleCatalog tmp : catalogs){
			catalogMap.put(tmp.getCatalogId(), tmp);//???????????????catalog
		}
		
		for(int i = 0;i<catalogs.size();i++){
			List<String> names = new ArrayList<String>(); //??????
			RptSysModuleCatalog catalog = catalogMap.get(catalogs.get(i).getCatalogId());
			names.add(catalog.getCatalogNm());
			while(catalogMap.get(catalog.getUpId()) != null){
				catalog = catalogMap.get(catalog.getUpId());
				names.add(catalog.getCatalogNm()) ;
			}
			
			List<String> unNames = new ArrayList<String>();
			for(int j=names.size() ; j>0;j--){
				unNames.add(names.get(j - 1));
			}
			newCatalog.put(catalogs.get(i).getCatalogId(), unNames);
		}
		if(vos!=null&&vos.size()>0){
			for(DataSetImportVO vo:vos){
				
				if(newCatalog.get(vo.getCatalogId()) != null){
					List<String> catalogList = newCatalog.get(vo.getCatalogId()) ;
					if(catalogList != null && catalogList.size() > 0){
						for(int i=0;i<catalogList.size();i++){
							if(i==0){
								vo.setFirstcatalogNm(catalogList.get(i));
							}else if(i==1){
								vo.setSecondCatalog(catalogList.get(i));
							}else if(i == 3){
								vo.setThirdCatalog(catalogList.get(i));
							}
						}
					}
				}
			}
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe=null;
		try {
			fileName=this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator+ RandomUtils.uuid2()+".xls";
			fe=new XlsExcelTemplateExporter(fileName,this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH +File.separator+ GlobalConstants4plugin.EXPORT_MODEL_TEMPLATE_PATH,list);
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
