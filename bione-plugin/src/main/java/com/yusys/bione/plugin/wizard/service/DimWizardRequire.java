package com.yusys.bione.plugin.wizard.service;

import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.*;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptdim.entity.RptDimCatalog;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfoPK;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.repository.mybatis.RptDimDao;
import com.yusys.bione.plugin.wizard.web.vo.DimImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DimWizardRequire extends BaseController implements IWizardRequire {
	@Autowired
	private ExcelBS excelBS;
	@Autowired
	private RptDimDao rptDimDao;

	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(DimImportVO.class, file);

		UploadResult result = new UploadResult();
		try {
			if(xlsImport != null){
				List<DimImportVO> vos = (List<DimImportVO>) xlsImport.ReadExcel();
				String ehcacheId = RandomUtils.uuid2();
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
						vos);

				result.setEhcacheId(ehcacheId);
				result.setFileName(file.getName());
				if (vos != null && vos.size() > 0) {
					for (DimImportVO vo : vos) {
						if (vo.getDimTypeNo() != null
								&& !vo.getDimTypeNo().equals(""))
							result.setInfo(vo.getDimTypeNo(), vo.getDimTypeNm());
					}
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
	public void saveData(String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptDimTypeInfo> types = (List<RptDimTypeInfo>) lists.get(0);
		List<RptDimItemInfo> items = (List<RptDimItemInfo>) lists.get(1);
		List<RptDimCatalog> catalogs = (List<RptDimCatalog>) lists.get(2);
		this.excelBS.deleteEntityJdbcBatch(types, null);
		List<String> fields = new ArrayList<String>();
		fields.add("id.dimTypeNo");
		this.excelBS.deleteEntityJdbcBatch(items, fields);
		this.excelBS.saveEntityJdbcBatch(types);
		this.excelBS.saveEntityJdbcBatch(items);
		this.excelBS.saveEntityJdbcBatch(catalogs);
	}

	private RptDimItemInfo createItem(DimImportVO vo,
			List<RptDimTypeInfo> types, String dimNo, String dimNm, int index,
			String[] dimNos, List<ValidErrorInfoObj> errors,Map<String,String> dimItemNos,Map<String,String> dimItemNms) {
		dimNos[index] = dimNo;
		RptDimItemInfo item = new RptDimItemInfo();
		RptDimItemInfoPK id = new RptDimItemInfoPK();
		BeanUtils.copy(vo, item);
		String dimNoInfo = dimNos[index-1];
		boolean flag=true;
		if(!StringUtils.isNotBlank(dimNos[index-1])){
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(6+2*index+1);
			obj.setValidTypeNm("上级维度项存在校验");
			obj.setErrorMsg("上级维度项不存在");
			errors.add(obj);
			flag=false;
		}
		
		if(!StringUtils.isNotBlank(dimNo)){
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(6+2*index+1);
			obj.setValidTypeNm("非空校验");
			obj.setErrorMsg("维度项代码不允许为空");
			errors.add(obj);
			flag=false;
		}
		if(!StringUtils.isNotBlank(dimNm)){
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(6+2*index+2);
			obj.setValidTypeNm("非空校验");
			obj.setErrorMsg("维度项名称不允许为空");
			errors.add(obj);
			flag=false;
		}
//		for (int i = 1; i < index; i++) {
//			dimNoInfo += dimNos[i];
//		}
		if(dimItemNos.get(dimNo)!=null){
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(6+2*index+1);
			obj.setValidTypeNm("唯一校验");
			obj.setErrorMsg("维度项代码不能重复");
			errors.add(obj);
			flag=false;
		}
		/*if(dimItemNms.get(dimNm)!=null){
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(6+2*index+2);
			obj.setValidTypeNm("唯一校验");
			obj.setErrorMsg("维度项名称不能重复");
			errors.add(obj);
			flag=false;
		}*/
		if(!flag){
			return null;
		}
		id.setDimItemNo(dimNo);
		id.setDimTypeNo(types.get(types.size() - 1).getDimTypeNo());
		item.setId(id);
		item.setDimItemNm(dimNm);
		item.setStartDate(types.get(types.size() - 1).getStartDate());
		item.setEndDate("29991231");
		item.setUpNo(dimNoInfo.equals("") ? GlobalConstants4frame.DEFAULT_TREE_ROOT_NO : dimNoInfo);
		item.setItemSts("Y");
		item.setRemark("");
		dimItemNos.put(dimNo, dimNo);
		dimItemNms.put(dimNm, dimNm);
		return item;
	}

	private RptDimCatalog getCatalogInfo(List<RptDimCatalog> catalogs,
			String name, RptDimCatalog catalog) {
		RptDimCatalog tempCatalog = new RptDimCatalog();
		List<String> temp = new ArrayList<String>();
		temp.add("catalogNm");
		temp.add("upCatalogId");
		String upCatalogId = GlobalConstants4frame.DEFAULT_TREE_ROOT_NO;
		if (catalog != null)
			upCatalogId = catalog.getCatalogId();
		tempCatalog.setUpCatalogId(upCatalogId);
		tempCatalog.setCatalogNm(name);
		RptDimCatalog catalogInfo = null;
		List<RptDimCatalog> catalogList = this.excelBS.validateExist(
				tempCatalog, RptDimCatalog.class, temp);
		boolean flag = true;
		if (catalogs.size() > 0) {
			for (RptDimCatalog ca : catalogs) {
				if (ca.getCatalogNm().equals(name)
						&& ca.getUpCatalogId().equals(upCatalogId)) {
					catalogInfo = ca;
					flag = false;
					break;
				}
			}
		}
		if (flag) {
			if (catalogList != null && catalogList.size() > 0) {
				catalogInfo = catalogList.get(0);
			}
			if (catalogInfo == null) {
				catalogInfo = new RptDimCatalog();
				catalogInfo.setUpCatalogId(upCatalogId);
				catalogInfo.setCatalogNm(name);
				catalogInfo.setCatalogId(RandomUtils.uuid2());
				catalogs.add(catalogInfo);
			}
		}
		return catalogInfo;

	}

	@Override
	public String export(String ids) {
		// TODO Auto-generated method stub
		String fileName = "";
		Map<String, RptDimCatalog> catalogMap = new HashMap<String, RptDimCatalog>();
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> dimTypeNos = ArrayUtils.asList(ids, ",");

		params.put("dimTypeNos", dimTypeNos);
		List<DimImportVO> vos = new ArrayList<DimImportVO>();
		List<RptDimTypeInfo> types = rptDimDao.getTypeInfosByParams(params);

		List<RptDimCatalog> catalogs = this.rptDimDao.getAllRptDimCatalogs();
		if (catalogs != null && catalogs.size() > 0) {
			for (RptDimCatalog catalog : catalogs) {
				catalogMap.put(catalog.getCatalogId(), catalog);
			}
		}
		String dimNos[] = new String[8];
		dimNos[0] = "";
		if (types != null && types.size() > 0) {
			for (RptDimTypeInfo type : types) {
				boolean flag = false;
				DimImportVO vo = new DimImportVO();
				List<String> catalogNm = new ArrayList<String>();
				setCatalogNm(catalogNm, type.getCatalogId(), catalogMap);
				if (catalogNm.size() > 0) {
					vo.setFirstCatalog(catalogNm.get(catalogNm.size() - 1));
				}
				if (catalogNm.size() > 1) {
					vo.setSecondCatalog(catalogNm.get(catalogNm.size() - 2));
				}
				if (catalogNm.size() > 2) {
					vo.setThirdCatalog(catalogNm.get(catalogNm.size() - 3));
				}
				vo.setDimTypeNo(type.getDimTypeNo());
				vo.setDimTypeNm(type.getDimTypeNm());
				vo.setDimSts(type.getDimSts());
				vo.setStartDate(type.getStartDate());
				vo.setDimTypeDesc(type.getDimTypeDesc());
				vo.setBusiDef(type.getBusiDef());
				vo.setBusiRule(type.getBusiRule());
				vo.setDefDept(type.getDefDept());
				vo.setUseDept(type.getUseDept());
				vos.add(vo);
				params.clear();
				params.put("dimTypeNo", type.getDimTypeNo());
				List<RptDimItemInfo> items = rptDimDao
						.getItemInfosByDimType(params);
				Map<String, List<RptDimItemInfo>> itemMap = setDimInfo(items);
				setItemInfos(itemMap, vos, 1, GlobalConstants4frame.DEFAULT_TREE_ROOT_NO, dimNos, flag);
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
					+ GlobalConstants4plugin.EXPORT_DIM_TEMPLATE_PATH, list);
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

	private void setItemInfos(Map<String, List<RptDimItemInfo>> itemMap,
			List<DimImportVO> vos, int index, String dimItemNo,
			String[] dimNos, boolean flag) {
		List<RptDimItemInfo> itemInfos = itemMap.get(dimItemNo);
		if (itemInfos != null && itemInfos.size() > 0) {
			for (RptDimItemInfo item : itemInfos) {
				vos.add(setRowInfo(dimNos, index, item));
				setItemInfos(itemMap, vos, index + 1, item.getId()
						.getDimItemNo(), dimNos, flag);
			}
		}
	}

	private DimImportVO setRowInfo(String[] dimNos, int index,
			RptDimItemInfo item) {
		DimImportVO vo = new DimImportVO();
		dimNos[index] = item.getId().getDimItemNo();
		BeanUtils.copy(item, vo);
		vo.setStartDate("");
		ReflectionUtils.invokeSetter(vo, "dimNo" + index, StringUtils
				.substring(item.getId().getDimItemNo(),
						dimNos[index - 1].length(), item.getId().getDimItemNo()
								.length()));
		ReflectionUtils.invokeSetter(vo, "dimNm" + index, item.getDimItemNm());
		return vo;
	}

	private void setCatalogNm(List<String> catalogNm, String catalogId,
			Map<String, RptDimCatalog> catalogMap) {
		RptDimCatalog catalog = catalogMap.get(catalogId);
		if(catalog!=null){
			catalogNm.add(catalog.getCatalogNm());
			if (!GlobalConstants4frame.DEFAULT_TREE_ROOT_NO.equals(catalog.getUpCatalogId())) {
				setCatalogNm(catalogNm, catalog.getUpCatalogId(), catalogMap);
			}
		}
			
	}

	private Map<String, List<RptDimItemInfo>> setDimInfo(
			List<RptDimItemInfo> items) {
		Map<String, List<RptDimItemInfo>> dimMap = new HashMap<String, List<RptDimItemInfo>>();
		if (items != null && items.size() > 0) {
			for (RptDimItemInfo item : items) {
				if (dimMap.get(item.getUpNo()) == null) {
					List<RptDimItemInfo> infos = new ArrayList<RptDimItemInfo>();
					infos.add(item);
					dimMap.put(item.getUpNo(), infos);
				} else {
					dimMap.get(item.getUpNo()).add(item);
				}
			}
		}
		return dimMap;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<DimImportVO> vos = (List<DimImportVO>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptDimTypeInfo> types = new ArrayList<RptDimTypeInfo>();
		List<RptDimItemInfo> items = new ArrayList<RptDimItemInfo>();
		List<RptDimCatalog> catalogs = new ArrayList<RptDimCatalog>();
		Map<String,String> dimTypeNos=new HashMap<String, String>();
		Map<String,String> dimTypeNms=new HashMap<String, String>();
		Map<String,String> dimItemNos=new HashMap<String, String>();
		Map<String,String> dimItemNms=new HashMap<String, String>();
		boolean dimTypeFlag=false;
		if (vos != null && vos.size() > 0) {
			String dimNos[] = new String[8];
			dimNos[0] = GlobalConstants4frame.DEFAULT_TREE_ROOT_NO;
			for (DimImportVO vo : vos) {
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				if (vo.getDimTypeNo() != null) {
					dimTypeFlag=false;
					boolean flag=true;
					if(dimTypeNos.get(vo.getDimTypeNo())!=null || vo.getDimTypeNo().equals("tempUnit")){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(1);
						obj.setValidTypeNm("唯一校验");
						obj.setErrorMsg("维度代码不能重复");
						errors.add(obj);
						flag=false;
					}
					if (!StringUtils.isNotBlank(vo.getDimTypeNm())) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(2);
						obj.setValidTypeNm("非空校验");
						obj.setErrorMsg("维度名称不允许为空");
						errors.add(obj);
						flag=false;
					}
					if(dimTypeNms.get(vo.getDimTypeNm())!=null){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(5);
						obj.setValidTypeNm("唯一校验");
						obj.setErrorMsg("维度名称不能重复");
						errors.add(obj);
						flag=false;
					}
					if(!flag)
						continue;
					RptDimCatalog catalog = null;
					RptDimTypeInfo info = new RptDimTypeInfo();
					BeanUtils.copy(vo, info);
					info.setDimType(GlobalConstants4plugin.DIM_TYPE_BUSI);
					if (StringUtils.isNotBlank(vo.getFirstCatalog())) {
						catalog = getCatalogInfo(catalogs,
								vo.getFirstCatalog(), null);
					}
					if (StringUtils.isNotBlank(vo.getSecondCatalog())) {
						catalog = getCatalogInfo(catalogs,
								vo.getSecondCatalog(), catalog);
					}
					if (StringUtils.isNotBlank(vo.getThirdCatalog())) {
						if (!StringUtils.isNotBlank(vo.getSecondCatalog())) {
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(5);
							obj.setValidTypeNm("目录层级空值校验");
							obj.setErrorMsg("二级目录为空值时,三级目录不能有值");
							errors.add(obj);
						} else
							catalog = getCatalogInfo(catalogs,
									vo.getThirdCatalog(), catalog);
					}
					if (catalog != null) {
						info.setCatalogId(catalog.getCatalogId());
					} else {
						info.setCatalogId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
					}
					info.setDimTypeEnNm("");
					info.setDimTypeDesc("");
					info.setEndDate("29991231");
					info.setDimTypeStruct(GlobalConstants4plugin.DIM_TYPE_STRUCT_LIST);
					info.setBusiDef(vo.getBusiDef());
					info.setBusiRule(vo.getBusiRule());
					info.setDefDept(vo.getDefDept());
					info.setUseDept(vo.getUseDept());
					info.setDimTypeDesc(vo.getDimTypeDesc());
					types.add(info);
					for (int i = 1; i < 8; i++) {
						dimNos[i] = "";
					}
					dimTypeNos.put(vo.getDimTypeNo(), vo.getDimTypeNo());
					dimTypeNms.put(vo.getDimTypeNm(), vo.getDimTypeNm());
					dimItemNos.clear();
					dimItemNms.clear();
					dimTypeFlag=true;
				}
				if (types == null || types.size() == 0) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("非空校验");
					obj.setErrorMsg("维度类型不能为空");
					errors.add(obj);
					continue;
				}
				
				if(dimTypeFlag){
					boolean dimItemFlag=false;
					if (vo.getDimNo1() != null && !vo.getDimNo1().equals("")) {
						RptDimItemInfo item=createItem(vo, types, vo.getDimNo1(),
								vo.getDimNm1(), 1, dimNos,errors,dimItemNos,dimItemNms);
						if(item!=null){
							items.add(item);
							dimItemFlag=true;
						}
					}
					if (vo.getDimNo2() != null && !vo.getDimNo2().equals("")) {
						RptDimItemInfo item=createItem(vo, types, vo.getDimNo2(),
								vo.getDimNm2(), 2, dimNos,errors,dimItemNos,dimItemNms);
						if(item!=null){
							items.add(item);
							types.get(types.size() - 1).setDimTypeStruct(
									GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE);
							dimItemFlag=true;
						}
					}
					if (vo.getDimNo3() != null && !vo.getDimNo3().equals("")) {
						RptDimItemInfo item=createItem(vo, types, vo.getDimNo3(),
								vo.getDimNm3(), 3, dimNos,errors,dimItemNos,dimItemNms);
						if(item!=null){
								items.add(item);
								types.get(types.size() - 1).setDimTypeStruct(
											GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE);
								dimItemFlag=true;
						}
					}
					if (vo.getDimNo4() != null && !vo.getDimNo4().equals("")) {
						RptDimItemInfo item=createItem(vo, types, vo.getDimNo4(),
								vo.getDimNm4(), 4, dimNos,errors,dimItemNos,dimItemNms);
						if(item!=null){
							items.add(item);
							types.get(types.size() - 1).setDimTypeStruct(
										GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE);
							dimItemFlag=true;
						}
					}
					if (vo.getDimNo5() != null && !vo.getDimNo5().equals("")) {
						RptDimItemInfo item=createItem(vo, types, vo.getDimNo5(),
								vo.getDimNm5(), 5, dimNos,errors,dimItemNos,dimItemNms);
						if(item!=null){
							items.add(item);
							types.get(types.size() - 1).setDimTypeStruct(
										GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE);
							dimItemFlag=true;
						}
					}
					if (vo.getDimNo6() != null && !vo.getDimNo6().equals("")) {
						RptDimItemInfo item=createItem(vo, types, vo.getDimNo6(),
								vo.getDimNm6(), 6, dimNos,errors,dimItemNos,dimItemNms);
						if(item!=null){
							items.add(item);
							types.get(types.size() - 1).setDimTypeStruct(
										GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE);
							dimItemFlag=true;
						}
					}
					if (vo.getDimNo7() != null && !vo.getDimNo7().equals("")) {
						RptDimItemInfo item=createItem(vo, types, vo.getDimNo7(),
								vo.getDimNm7(), 7, dimNos,errors,dimItemNos,dimItemNms);
						if(item!=null){
							items.add(item);
							types.get(types.size() - 1).setDimTypeStruct(
										GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE);
							dimItemFlag=true;
						}
					}
					if(!dimItemFlag){
						if(!dimTypeFlag){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(9);
							obj.setValidTypeNm("非空校验");
							obj.setErrorMsg("维度项类型不能为空");
							errors.add(obj);
						}
						else{
							
						}
					}
					
				}
				
				
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(types);
		lists.add(items);
		lists.add(catalogs);
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
