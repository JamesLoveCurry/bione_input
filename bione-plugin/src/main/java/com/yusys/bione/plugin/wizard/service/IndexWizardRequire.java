package com.yusys.bione.plugin.wizard.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.*;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo;
import com.yusys.bione.plugin.businessline.service.RptBusinessLineBS;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.service.RptDatasetBS;
import com.yusys.bione.plugin.design.util.IdxFormulaUtils;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.service.RptDimBS;
import com.yusys.bione.plugin.rptdim.web.vo.RptDimItemInfoVO;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.wizard.web.vo.IndexImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.*;

/**
 * 
 * <pre>
 * Title: 指标导入导出
 * Description: 
 * </pre>
 * @author yusys
 * @version 1.00.00
 */
@Service
public class IndexWizardRequire extends BaseController implements
		IWizardRequire {

	private RptBusinessLineBS lineBS = SpringContextHolder.getBean("rptBusinessLineBS");
	private ExcelBS excelBS = SpringContextHolder.getBean("excelBS");
	private IdxInfoBS idxInfoBS = SpringContextHolder.getBean("idxInfoBS");
	private RptDatasetBS rptDatasetBS = SpringContextHolder
			.getBean("rptDatasetBS");
	private RptDimBS rptDimBS = SpringContextHolder.getBean("rptDimBS");

	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(IndexImportVO.class, file);
		UploadResult result = new UploadResult();
		try {
			List<IndexImportVO> vos = (List<IndexImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					vos);

			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				for (IndexImportVO vo : vos) {
					result.setInfo(vo.getIndexNo(), vo.getIndexNm());
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {// 后台写入逻辑，前台已经做过校验逻辑
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptIdxCatalog> catalogSaveList = (List<RptIdxCatalog>) lists
				.get(0);
		List<RptIdxInfo> idxSaveList = (List<RptIdxInfo>) lists.get(1);
		List<RptIdxBusiExt> idxBusiSaveList = (List<RptIdxBusiExt>) lists
				.get(2);
		List<RptIdxMeasureRel> idxMeasureRelSaveList = (List<RptIdxMeasureRel>) lists
				.get(3);
		List<RptIdxDimRel> idxDimRelSaveList = (List<RptIdxDimRel>) lists
				.get(4);
		List<RptIdxFormulaInfo> idxFormulaSaveList = (List<RptIdxFormulaInfo>) lists
				.get(5);
		List<RptIdxFilterInfo> idxFilterSaveList = (List<RptIdxFilterInfo>) lists
				.get(6);
		List<RptIdxInfo> idxDelList = (List<RptIdxInfo>) lists.get(7);
		List<RptIdxSrcRelInfo> idxSrcSaveList = (List<RptIdxSrcRelInfo>) lists
				.get(8);
		List<RptIdxInfo> idxUptList = new ArrayList<RptIdxInfo>();
		if(lists.size()>9){
			idxUptList = (List<RptIdxInfo>) lists
			.get(9);	
		}
		List<String>   myPkStr =  Lists.newArrayList();
		myPkStr.add("id.indexNo");
//		myPkStr.add("id.indexVerId");//不考虑版本，将删除该指标对应版本的所有关联信息
		if (idxDelList.size() > 0) {//由于上面的【自定义删除主键】，下面的sql执行将有许多空语句
			this.excelBS.deleteEntityJdbcBatch(idxSaveList, myPkStr);
			this.excelBS.deleteEntityJdbcBatch(idxDelList, myPkStr);
			this.excelBS.deleteEntityJdbcBatch(idxBusiSaveList, myPkStr);
			this.excelBS.deleteEntityJdbcBatch(idxMeasureRelSaveList, myPkStr);
			this.excelBS.deleteEntityJdbcBatch(idxDimRelSaveList, myPkStr);
			this.excelBS.deleteEntityJdbcBatch(idxFormulaSaveList, myPkStr);
			this.excelBS.deleteEntityJdbcBatch(idxFilterSaveList, myPkStr);
			this.excelBS.deleteEntityJdbcBatch(idxSrcSaveList, myPkStr);
		}
		if (idxUptList.size() > 0) {//由于上面的【自定义删除主键】，下面的sql执行将有许多空语句
			myPkStr.add("id.indexVerId");
			List<String> uptFields = new ArrayList<String>();
			uptFields.add("endDate");
			this.excelBS.uptEntityJdbcBatch(idxUptList, myPkStr,uptFields);
		}
		this.excelBS.saveEntityJdbcBatch(catalogSaveList);
		this.excelBS.saveEntityJdbcBatch(idxSaveList);
		this.excelBS.saveEntityJdbcBatch(idxBusiSaveList);
		this.excelBS.saveEntityJdbcBatch(idxMeasureRelSaveList);
		this.excelBS.saveEntityJdbcBatch(idxDimRelSaveList);
		this.excelBS.saveEntityJdbcBatch(idxFormulaSaveList);
		this.excelBS.saveEntityJdbcBatch(idxFilterSaveList);
		this.excelBS.saveEntityJdbcBatch(idxSrcSaveList);
	}

	@Override
	public String export(String ids) {
		String fileName = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("list", ReBuildParam.splitLists(ArrayUtils.asList(ids, ",")));
		List<IndexImportVO> vos = this.idxInfoBS.getExportIdxInfo(params);
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe = null;
		try {
			fileName = this.getRealPath() + DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";
			fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ DESIGN_EXPORT_PATH + File.separator
					+ EXPORT_INDEX_TEMPLATE_PATH, list);
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

	@Override
	public String exportAll(String ids) {
		String zipfileName = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idxNos", ReBuildParam.splitLists(ArrayUtils.asList(ids, ",")));
		Long indexVerid = this.idxInfoBS.getIdxMaxVerId(params);
		List<String> fileNames = new ArrayList<String>();
		String folder = this.getRealPath() + DESIGN_EXPORT_PATH
				+ File.separator + RandomUtils.uuid2();
		File folderf = new File(folder);
		if(!folderf.exists()){
			folderf.mkdirs();
		}
		zipfileName = folder +"/" +RandomUtils.uuid2()+".zip";
		params.put("list", params.remove("idxNos"));
		for(int i = 1;i<=indexVerid;i++){
			
			List<IndexImportVO> vos = this.idxInfoBS.getExportIdxInfo(params,i);
			List<List<?>> list = new ArrayList<List<?>>();
			list.add(vos);
			XlsExcelTemplateExporter fe = null;
			try {
				String fileName = folder+"/"+"指标版本" + i + ".xls";
				fileNames.add(fileName);
				fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
						+ DESIGN_EXPORT_PATH + File.separator
						+ EXPORT_INDEX_TEMPLATE_PATH, list);
				fe.run();
			} catch (Exception e) {
				zipfileName = "";
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
		}
		try {
			createZipFile(zipfileName, fileNames);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			zipfileName = "";
			e.printStackTrace();
		}
		return zipfileName;
	}
	
	/**
	 * 压缩文件
	 * 
	 * @param zipFilePath
	 *            压缩文件路径
	 * @param filePathList
	 *            被压缩文件路径LIST
	 * @return
	 * @throws IOException
	 */
	private File createZipFile(String zipFilePath, List<String> filePathList)
			throws IOException {
		File zipFile = new File(zipFilePath);
		ZipOutputStream zout = null;
		try {
			zout = new ZipOutputStream(new FileOutputStream(zipFilePath));
			for (String filePath : filePathList) {
				File inputFile = new File(filePath);
				FilesUtils.zip(zout, inputFile, inputFile.getName(), zipFile);
				inputFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(zout);
		}
		return zipFile;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateVerInfo(String dsId, String ehcacheId) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();

		List<IndexImportVO> vos = (List<IndexImportVO>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);

		if (vos != null && vos.size() > 0) {// method - if - start
			// 全局，避免开销
			// 1.用来装dims(维度1,维度2,维度3)对应的维度数据结构
			List<RptDimTypeInfo> dimTypes = Lists.newArrayList();
			Map<String, RptDimTypeInfo> dimTypeFilterMap = Maps.newHashMap();
			// 2.用来处理组合指标的过滤维度关系
			Map<String, List<String>> filterInfoRelMap = Maps
					.newLinkedHashMap();
			Map<String, List<String>> filterInfoRelTempMap = Maps
					.newLinkedHashMap();
			Map<String, String> filterModeMap = Maps.newLinkedHashMap();
			// 3.用来处理组合指标的某个维度项中文名称和该维度项关系map
			Map<String, String> cnItemRel = Maps.newHashMap();
			// 4.参数通用map
			// Map<String, Object> param_search = Maps.newHashMap();
			// Map<String, Object> idxparams = Maps.newHashMap();
            //5.文件中map<指标编号,set<【关联维度】中文名对应的维度标识去重集合>>
			Map<String,Set<String>>   idxNoAndDimnosInFileRelMap  =  new HashMap<String, Set<String>>();
			//6。记载指标编号和对应的维度关系（在数据库中已经存在或者未存在）map
			//弥补了需求变更（基础指标的【关联维度】可以为空）带来的麻烦
			Map<String,Set<String>>   idxNoAndDimnosRelMap  =  new HashMap<String, Set<String>>();  
			//部门Map验证部门存在性
			Map<String,String> deptMap = new HashMap<String, String>();
			//用户Map验证部门存在性
			Map<String,String> userMap = new HashMap<String, String>();
			
			List<BioneDeptInfo> depts = this.idxInfoBS.getEntityListByProperty(BioneDeptInfo.class, "logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			if(depts != null && depts.size() > 0){
				for(BioneDeptInfo dept : depts){
					deptMap.put(dept.getDeptId(), dept.getDeptName());
				}
			}
			
			List<BioneUserInfo> infos = this.idxInfoBS.getAllEntityList(BioneUserInfo.class,"userId", false);
			if(infos != null && infos.size() > 0){
				for(BioneUserInfo info : infos){
					userMap.put(info.getUserId(), info.getUserName());
				}
			}
			
			//业务分库信息
			Map<String,String> busiLibMap = Maps.newHashMap();
			List<RptMgrBusiLibInfo> busiLibs = this.idxInfoBS.getAllEntityList(RptMgrBusiLibInfo.class);
			if(busiLibs != null && !busiLibs.isEmpty()) {
				for(RptMgrBusiLibInfo busiLib : busiLibs) {
					busiLibMap.put(busiLib.getBusiLibId(), busiLib.getBusiLibNm());
				}
			}
			
			// 指标目录内存信息
			List<RptIdxCatalog> catalogs = this.idxInfoBS.getAllCatalog();
			Map<String, RptIdxCatalog> catalogMap = Maps.newHashMap();
			if (catalogs != null) {
				for (RptIdxCatalog catalog : catalogs) {
					catalogMap.put(
							catalog.getUpNo() + "_"
									+ catalog.getIndexCatalogNm(), catalog);
				}
			}
			// 维度相关内存信息
			List<RptDimTypeInfo> allDimTypesFromDB = this.rptDimBS
					.getAllRptDimTypeInfos();
			List<RptDimItemInfoVO> allDimItemsFromDB = this.rptDimBS
					.getAllRptDimItemInfos();
			Map<String, RptDimTypeInfo> allDimTypesIdenByDimNoFromDBMap = Maps
					.newHashMap();
			Map<String, String> allDimTypesIdenByDimNmFromDBMap = Maps
					.newHashMap();
			Map<String, List<RptDimItemInfoVO>> allDimTypeAndItemsFromDBRelMap = Maps
					.newHashMap();
			if (allDimTypesFromDB != null && allDimTypesFromDB.size() > 0) {
				for (RptDimTypeInfo type : allDimTypesFromDB) {
					allDimTypesIdenByDimNmFromDBMap.put(type.getDimTypeNm(),
							type.getDimTypeNo());
					allDimTypesIdenByDimNoFromDBMap.put(type.getDimTypeNo(),
							type);
					allDimTypeAndItemsFromDBRelMap.put(type.getDimTypeNo(),
							new ArrayList<RptDimItemInfoVO>());
				}

			}
			if (allDimItemsFromDB != null && allDimItemsFromDB.size() > 0) {
				for (RptDimItemInfoVO vo : allDimItemsFromDB) {
					// 下面的get方法执行后返回值不应该为空
					if(allDimTypeAndItemsFromDBRelMap.get(vo.getDimTypeNo()) != null) allDimTypeAndItemsFromDBRelMap.get(vo.getDimTypeNo()).add(vo);
				}
			}

			// 指标内存信息
			Set<String> voIndexNoSet = new HashSet<String>();
			Set<String> voSrcIndexNoSet = new HashSet<String>();
			Set<String> voSrcMeaIndexNoSet = new HashSet<String>();
			Set<String> voIndexNmSet = new HashSet<String>();
			// Set<String> voIndexUsualNmSet = new HashSet<String>();
			Map<String, Set<String>> voIdxNoAndSrcInFileRelMap = Maps.newHashMap();
			Map<String, Set<String>> voIdxNoAndSrcInDBRelMap = Maps.newHashMap();
			Map<String, RptIdxInfo> allSrcInfoFromDBMap = Maps.newHashMap();
			Map<String, RptIdxMeasureRel> allSrcMeaInfoFromDBMap = Maps.newHashMap();
			Map<String, Set<String>> allSrcIdxDimRelFromDBMap = Maps.newHashMap();
			Map<String, RptIdxInfo> allInfoFromDBMap = Maps.newHashMap();
			
			// 数据集相关内存信息
			Map<String, Object> param2 = Maps.newHashMap();
			param2.put("sourceId", dsId);
			param2.put("notsetType", SET_TYPE_DETAIL);
			
			List<RptSysModuleInfo> allSysModuleInfoFromDBByDsId = this.rptDatasetBS
					.getDataSetsByParams(param2);// 通过数据源获取数据集
			List<RptSysModuleCol> allSysModuleColFromDB = this.rptDatasetBS
					.getDatacolsByParams(param2);
			Map<String, String> tableEnNmAndSetRelMap = Maps.newHashMap();
			Map<String, List<RptSysModuleCol>> setIdAndColListRelMap = Maps
					.newHashMap();
			Map<String, String> setIdAndSetNmRelMap = Maps
					.newHashMap();
			Map<String, RptSysModuleInfo> setIdAndModelMap = Maps
					.newHashMap();
			if (allSysModuleInfoFromDBByDsId != null
					&& allSysModuleInfoFromDBByDsId.size() > 0) {
				for (RptSysModuleInfo module : allSysModuleInfoFromDBByDsId) {
					tableEnNmAndSetRelMap.put(module.getTableEnNm(),
							module.getSetId());
					setIdAndColListRelMap.put(module.getSetId(),
							new ArrayList<RptSysModuleCol>());
					setIdAndModelMap.put(module.getTableEnNm(), module);
					setIdAndSetNmRelMap.put(module.getSetId(), module.getSetNm());
				}
			}
			if (allSysModuleColFromDB != null
					&& allSysModuleColFromDB.size() > 0) {
				for (RptSysModuleCol col : allSysModuleColFromDB) {
					if (setIdAndColListRelMap.get(col.getSetId()) != null) {
						setIdAndColListRelMap.get(col.getSetId()).add(col);
					}
				}
			}
			
			for (IndexImportVO vo : vos) {
				String temp_idxno = vo.getIndexNo();
				String index_type =  vo.getIndexType();
				String ds_id  =  vo.getDatasetId();
				idxNoAndDimnosRelMap.put(temp_idxno, new HashSet<String>());
                if(index_type.equals(DERIVE_INDEX)|| index_type.equals(COMPOSITE_INDEX)){
					String   dims  =  vo.getDims();
					String   indexFormula  =  vo.getIndexFormula();
					String[] dimTypeItemRelArr = null;
					if (StringUtils.isNotEmpty(indexFormula)) {
						dimTypeItemRelArr = StringUtils.split(indexFormula, '\n');
						if (dimTypeItemRelArr != null) {
							for (String dimTypeItemRel : dimTypeItemRelArr) {// 分析完公式中的维度和维度项之间的关系
								String[] objArr0_ = StringUtils.split(dimTypeItemRel, ':');
								String dimTypeNm = objArr0_[0];
								String dimTypeNo = allDimTypesIdenByDimNmFromDBMap
										.get(dimTypeNm);
								if(StringUtils.isNotEmpty(dimTypeNo)){
									idxNoAndDimnosRelMap.get(temp_idxno).add(dimTypeNo);
								}
							}
						}	
					}
					if(StringUtils.isNotEmpty(dims)){
						String[]   dimCnNmArr  = StringUtils.split(dims, ',');
						for(String  dimCnNm:dimCnNmArr){
							String  dimno  =  allDimTypesIdenByDimNmFromDBMap.get(dimCnNm);
							if(StringUtils.isNotEmpty(dimno)){
								idxNoAndDimnosRelMap.get(temp_idxno).add(dimno);
							}
						}
					}
				}else{
					if(StringUtils.isNotBlank(ds_id)){
						String[]  dsEnNmArr = StringUtils.split(ds_id, ':');
						String  setId = dsEnNmArr[0];
						List<RptSysModuleCol>  cols  =  setIdAndColListRelMap.get(setId);
						if(cols != null && cols.size() > 0){
							for(RptSysModuleCol col:cols){
								if(col.getColType()!=null&&col.getColType().equals(COL_TYPE_DIM)&&StringUtils.isNotEmpty(col.getDimTypeNo())){
									idxNoAndDimnosRelMap.get(temp_idxno).add(col.getDimTypeNo());
								}
							}
						}
					}
				}
				String temp_srcidxno = vo.getSrcIndexNo();
				String temp_idxnm = vo.getIndexNm();
				// String temp_idxusualnm = vo.getIndexUsualNm();
				String dims  =  vo.getDims()==null?"":vo.getDims();
				// 文件中可能出现相同的指标编号,所以只保存第一次出现的指标编号的相关信息
				if (StringUtils.isNotEmpty(temp_idxno)&& !voIndexNoSet.contains(temp_idxno)) {
					voIndexNoSet.add(temp_idxno);
					
					String[] dimCnNmArr = StringUtils.split(dims, ',');//基础指标的关联维度已经是可空的，前面已经用代码手动做过非空校验了
					Set<String>  allDimNosFromFile =  new HashSet<String>();
					for(String  dimCnNm:dimCnNmArr){
						String  dimno  =  allDimTypesIdenByDimNmFromDBMap.get(dimCnNm);
						if(StringUtils.isNotEmpty(dimno)){
							allDimNosFromFile.add(dimno);
						}
					}
					idxNoAndDimnosInFileRelMap.put(temp_idxno, allDimNosFromFile);
					
					
					voIdxNoAndSrcInFileRelMap.put(temp_idxno,new HashSet<String>());
					voIdxNoAndSrcInDBRelMap.put(temp_idxno,new HashSet<String>());
					if (StringUtils.isNotEmpty(temp_srcidxno)) {
						String[] temp_srcidxno_arr = StringUtils.split(temp_srcidxno, ',');
						for (String srcidxno : temp_srcidxno_arr) {
							if(srcidxno.indexOf(".")>=0){
								String srcIdxNos[] = StringUtils.split(srcidxno,".");
								voSrcIndexNoSet.add(srcIdxNos[0]);
								voSrcMeaIndexNoSet.add(srcIdxNos[0]);
							}
							else{
								voSrcIndexNoSet.add(srcidxno);
							}
							// value是HashSet,可以去重
							voIdxNoAndSrcInFileRelMap.get(temp_idxno).add(srcidxno);
						}
					}
				}
				if (StringUtils.isNotEmpty(temp_idxnm)) {
					voIndexNmSet.add(temp_idxnm);
				}
			}
			// 指标内存信息--指标名称和别名
			Map<String, Object> param1_ = Maps.newHashMap();
			param1_.put("indexNmList", ReBuildParam.splitLists(new ArrayList<String>(voIndexNmSet)));
			Map<String, Object> param6_ = Maps.newHashMap();
			param6_.put("indexNoList", ReBuildParam.splitLists(new ArrayList<String>(voIndexNoSet)));
			
			List<RptIdxInfo> idxListByNmList = Lists.newArrayList();
			if(voIndexNmSet.size()>0){
				idxListByNmList  = this.idxInfoBS
					.getIdxListByNmList(param1_);
			}
			
			List<RptIdxInfo> idxListByIdList = Lists.newArrayList();
			if(voIndexNoSet.size()>0){
				idxListByIdList  = this.idxInfoBS
					.getIdxListByIdList(param6_);
			}
			Map<String, RptIdxInfo> idxNoInfoRelMap = Maps
					.newHashMap();
			if(idxListByIdList != null && idxListByIdList.size()>0){
				for(RptIdxInfo info : idxListByIdList){
					idxNoInfoRelMap.put(info.getId().getIndexNo(), info);
				}
			}
			Map<String, List<String>> idxNmIndexNoListRelMap = Maps
					.newHashMap();
			if (idxListByNmList != null && idxListByNmList.size()>0) {
				for (RptIdxInfo info : idxListByNmList) {
					String indexNm = info.getIndexNm();
					String indexNo = info.getId().getIndexNo();
					if (!idxNmIndexNoListRelMap.containsKey(indexNm)) {
						idxNmIndexNoListRelMap.put(indexNm,
								new ArrayList<String>());
					}
					idxNmIndexNoListRelMap.get(indexNm).add(indexNo);
				}
			}
			//指标内存信息
			Map<String, Object> param4_ = Maps.newHashMap();
			param4_.put("list", ReBuildParam.splitLists(new ArrayList<String>(voIndexNoSet)));
			List<RptIdxInfo> allInfoFromDB = this.idxInfoBS.listIdxInfo(param4_);
			if (allInfoFromDB != null) {
				for (RptIdxInfo info : allInfoFromDB) {
					allInfoFromDBMap.put(info.getId().getIndexNo(), info);
				}
			}
			Map<String, Object> param0_ = Maps.newHashMap();
			List<RptIdxInfo> allSrcInfoFromDB = new ArrayList<RptIdxInfo>();
			List<RptIdxDimRel>  allSrcIdxDimRelList = new ArrayList<RptIdxDimRel>();
			List<RptIdxMeasureRel>  allSrcIdxMeasureRelList = new ArrayList<RptIdxMeasureRel>();
			if(voSrcIndexNoSet.size()>0){
			  param0_.put("list", ReBuildParam.splitLists(new ArrayList<String>(voSrcIndexNoSet)));
			  param0_.put("idxNoList", ReBuildParam.splitLists(new ArrayList<String>(voSrcIndexNoSet)));
			  allSrcInfoFromDB = this.idxInfoBS.listIdxInfo(param0_);
			  allSrcIdxDimRelList = this.idxInfoBS.getIdxDimRelByParams(param0_);
			}
			if(voSrcMeaIndexNoSet.size()>0){
				 param0_.put("idxNoList", ReBuildParam.splitLists(new ArrayList<String>(voSrcMeaIndexNoSet)));
				 allSrcIdxMeasureRelList = this.idxInfoBS.getIdxMeasureRelMaxByParams(param0_);
			}
			
			
			if(allSrcIdxDimRelList != null) {
				for(RptIdxDimRel dimrel:allSrcIdxDimRelList){
					String key  =   dimrel.getId().getIndexNo();
					if(allSrcIdxDimRelFromDBMap.get(key)==null){
						allSrcIdxDimRelFromDBMap.put(key, new HashSet<String>());
					}
					String  dimno  =  dimrel.getId().getDimNo();
					if(StringUtils.isNotEmpty(dimno)){
					   allSrcIdxDimRelFromDBMap.get(key).add(dimno);
					}
				}
			}
			
			if(allSrcIdxMeasureRelList != null) {
				for(RptIdxMeasureRel mearel:allSrcIdxMeasureRelList){
					allSrcMeaInfoFromDBMap.put(mearel.getId().getIndexNo()+"."+mearel.getId().getMeasureNo(), mearel);
				}
			}
			if (allSrcInfoFromDB != null) {
				for (RptIdxInfo info : allSrcInfoFromDB) {
					allSrcInfoFromDBMap.put(info.getId().getIndexNo(), info);
				}
			}
			Set<String> voIdxNoAndSrcInFileRelMapKeys = voIdxNoAndSrcInFileRelMap
					.keySet();
			for (String key : voIdxNoAndSrcInFileRelMapKeys) {
				Set<String> values = voIdxNoAndSrcInFileRelMap.get(key);
				for (String val : values) {
					if (allSrcInfoFromDBMap.containsKey(val)) {// 这儿得考虑指标编号的唯一性，所以不存在哪个指标编号对应的指标是属于哪一个指标的来源指标
						voIdxNoAndSrcInDBRelMap.get(key).add(val);
					}
					if(allSrcMeaInfoFromDBMap.containsKey(val)){
						voIdxNoAndSrcInDBRelMap.get(key).add(val);
					}
				} 
			}
			
			
			// del
			List<RptIdxInfo> idxDelList = Lists.newArrayList();
			// List<RptIdxBusiExt> idxBusiDelList = Lists.newArrayList();
			// saveList
			List<RptIdxCatalog> catalogSaveList = Lists.newArrayList();
			Map<String, RptIdxCatalog> catalogSaveMap = Maps.newHashMap();
			List<RptIdxInfo> idxSaveList = Lists.newArrayList();
			List<RptIdxSrcRelInfo> srcRelSaveList = Lists.newArrayList();
			List<RptIdxBusiExt> idxBusiSaveList = Lists.newArrayList();
			List<RptIdxMeasureRel> idxMeasureRelSaveList = Lists.newArrayList();
			List<RptIdxDimRel> idxDimRelSaveList = Lists.newArrayList();
			List<RptIdxFormulaInfo> idxFormulaSaveList = Lists.newArrayList();
			List<RptIdxFilterInfo> idxFilterSaveList = Lists.newArrayList();
			List<RptIdxInfo> idxOldInfoUptList = Lists.newArrayList();

			String finalIndexCatalogNo = "";
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			// String deptParamTypeNo = propertiesUtils
			// 		.getProperty("defDeptParamTypeNo");
			String steadyMeasureNo = propertiesUtils
					.getProperty("steadyMeasureNo");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			String indexDimTypeNo = propertiesUtils
					.getProperty("indexDimTypeNo");
			String defaultMeasure = propertiesUtils
					.getProperty("defaultMeasure");
			String defaultDim = propertiesUtils.getProperty("defaultDim");
			String dataDate = propertiesUtils.getProperty("dataDate");
			String orgNo = propertiesUtils.getProperty("orgNo");
			String currType = propertiesUtils.getProperty("currType");
			String indexNo_ = propertiesUtils.getProperty("indexNo");
			

			String[] tableColRelArr = null;
			Map<String, String> tableColRelMap = Maps.newLinkedHashMap();
			Map<String, List<String>> idxNoAndNmRelMap = Maps.newHashMap();
			// Map<String, List<String>> idxNoAndUsualNmRelMap = Maps.newHashMap();
			Set<String> indexNoSet = new HashSet<String>();
			for (IndexImportVO vo : vos) {// method - if - for - start
				//根据 业务分库 获取实体类 ID
				String datasetId = lineBS.getSetIdByBusiLibId(vo.getBusiLibId());
				//String datasetId = lineBS.getSetId(vo.getLineId(), "rpt");
				if(StringUtils.isBlank(datasetId)) {
					datasetId = propertiesUtils.getProperty("dsId");
				}
				// 一级分类名称
				String firstCatalogNm = vo.getFirstCatalog();
				// 二级分类名称
				String secondCatalogNm = vo.getSecondCatalog();
				// 三级分类名称
				String thirdCatalogNm = vo.getThirdCatalog();

				// 指标编号
				String indexNo = vo.getIndexNo();
				// 指标名称
				String indexNm = vo.getIndexNm();
				// 指标别名
				String indexUsualNm = vo.getIndexUsualNm();
				// 默认指标版本
				long indexVerId = 1;
				// 主管部门
				String deptId = vo.getDeptId();
				//联系人
				String userId = vo.getUserId();
				// 数据集中文名称
				String datasetCnNm = vo.getDatasetCnNm();
				// 数据集ID(ID:字段名,多个以回车分割)
				String datasetEnNm = vo.getDatasetId();
				// 维度集合
				String dims = vo.getDims()==null?"":vo.getDims();
				// 指标公式
				String indexFormula = vo.getIndexFormula();
				// 指标类型
				String indexType = vo.getIndexType();
				// 来源指标
				String srcIndexNos = vo.getSrcIndexNo();
				//业务分库
				String busiLibId= vo.getBusiLibId();
				//业务类型
				String busiType = vo.getBusiType();
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
					continue;
				}
				if (StringUtils.isNotEmpty(thirdCatalogNm)
						&& StringUtils.isEmpty(secondCatalogNm)) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(4);
					obj.setValidTypeNm("目录层级空值校验");
					obj.setErrorMsg("二级目录为空值时,三级目录不能有值");
					errors.add(obj);
					continue;
				}
				if (indexNoSet.contains(indexNo)) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("模板文件中指标编号重名校验");
					obj.setErrorMsg("模板文件中指标编号\"" + indexNo + "\"已经存在！");
					errors.add(obj);
					continue;
				} else {
					indexNoSet.add(indexNo);
				}
				if (idxNoInfoRelMap.keySet().contains(indexNo)) {
					RptIdxInfo info = idxNoInfoRelMap.get(indexNo);
					String startDate = info.getStartDate();
					if(vo.getStartDate().compareTo(startDate) <= 0){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(8);
						obj.setValidTypeNm("指标新版本启用时间校验");
						obj.setErrorMsg("指标新版本启用日期\"" + vo.getStartDate() + "\"必须大于旧版本启用日期\""+startDate);
						errors.add(obj);
						continue;
					}
					else{
						info.setEndDate(vo.getStartDate());
						idxOldInfoUptList.add(info);
						indexVerId = info.getId().getIndexVerId()+1;
					}
					
				}
				String reg = "[\\u4e00-\\u9fa5\\da-zA-Z－_、（）\\[\\]]+";
				Matcher matcher = Pattern.compile(reg).matcher(indexNm);
				boolean isMatcher = false;
				while (matcher.find()) {
					String temp = matcher.group(0);
					if(temp.equals(indexNm)){
						isMatcher = true;
					}
					
				}
				if(!isMatcher){
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("模板文件中指标名称规则校验");
					obj.setErrorMsg("模板文件中指标名称\"" + indexNm + "\"不能存在特殊字符");
					errors.add(obj);
					continue;
				}
				if (idxNoAndNmRelMap.get(indexNm)!=null&&idxNoAndNmRelMap.get(indexNm).size() > 0) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("模板文件中指标名称重名校验");
					obj.setErrorMsg("模板文件中指标名称\"" + indexNm + "\"已经存在");
					errors.add(obj);
					continue;
				} else {
					boolean idxNmExists = false;
					List<String> idxNos1 = idxNmIndexNoListRelMap.get(indexNm);
					if (idxNos1 != null) {
						for (String no : idxNos1) {
							if (!no.equals(indexNo)) {
								idxNmExists = true;
								break;
							}
						}
					}
					
					if (idxNmExists) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(5);
						obj.setValidTypeNm("数据库指标重名校验");
						obj.setErrorMsg("数据库中不同指标编号的指标名称\"" + indexNm + "\"已经存在");
						errors.add(obj);
						continue;
					} else {
						if (idxNoAndNmRelMap.get(indexNm) == null) {
							idxNoAndNmRelMap.put(indexNm,
									new ArrayList<String>());
						}
						idxNoAndNmRelMap.get(indexNm).add(indexNo);
					}
				}
//				if (StringUtils.isNotEmpty(indexUsualNm)) {
//					if (idxNoAndUsualNmRelMap.get(indexUsualNm)!=null&&idxNoAndUsualNmRelMap.get(indexUsualNm).size()>0) {
//						ValidErrorInfoObj obj = new ValidErrorInfoObj();
//						obj.setSheetName(vo.getSheetName());
//						obj.setExcelRowNo(vo.getExcelRowNo());
//						obj.setExcelColNo(6);
//						obj.setValidTypeNm("模板文件中指标别名重名校验");
//						obj.setErrorMsg("模板文件中指标别名\"" + indexUsualNm + "\"已经存在");
//						errors.add(obj);
//						continue;
//					} else if(idxNoAndNmRelMap.get(indexUsualNm)!=null&&idxNoAndNmRelMap.get(indexUsualNm).size() > 0){
//						boolean   isSame = false;
//						List<String>  tempUsuaList =  idxNoAndNmRelMap.get(indexUsualNm);
//						for(String  usua:tempUsuaList){
//							if(!usua.equals(indexNo)){
//								ValidErrorInfoObj obj = new ValidErrorInfoObj();
//								obj.setSheetName(vo.getSheetName());
//								obj.setExcelRowNo(vo.getExcelRowNo());
//								obj.setExcelColNo(6);
//								obj.setValidTypeNm("模板文件中指标别名重名校验");
//								obj.setErrorMsg("模板文件中指标名称\"" + indexUsualNm + "\"已经存在");
//								errors.add(obj);
//								isSame  = true;
//								break;
//							}
//						}
//						if(isSame){
//							continue;
//						}
//					}else{
//						boolean idxNmExists = false;
//						List<String> idxNos1 = idxNmIndexNoListRelMap
//								.get(indexUsualNm);
//						List<String> idxNos2 = idxUsualNmIndexNoListRelMap
//								.get(indexUsualNm);
//						if (idxNos1 != null) {
//							for (String no : idxNos1) {
//								if (!no.equals(indexNo)) {
//									idxNmExists = true;
//									break;
//								}
//							}
//						}
//						if (!idxNmExists && idxNos2 != null) {
//							for (String no : idxNos2) {
//								if (!no.equals(indexNo)) {
//									idxNmExists = true;
//									break;
//								}
//							}
//						}
//						if (idxNmExists) {
//							ValidErrorInfoObj obj = new ValidErrorInfoObj();
//							obj.setSheetName(vo.getSheetName());
//							obj.setExcelRowNo(vo.getExcelRowNo());
//							obj.setExcelColNo(6);
//							obj.setValidTypeNm("数据库中指标别名重名校验");
//							obj.setErrorMsg("数据库中指标别名或指标名称\"" + indexUsualNm
//									+ "\"已经存在");
//							errors.add(obj);
//							continue;
//						} else {
//							if (idxNoAndUsualNmRelMap.get(indexUsualNm) == null) {
//								idxNoAndUsualNmRelMap.put(indexUsualNm,new ArrayList<String>());
//							}
//							idxNoAndUsualNmRelMap.get(indexUsualNm).add(indexNo);
//						}
//					}
//				}

				String[] dimCnNmArr = StringUtils.split(dims, ',');//前面已经校验非空了
				Set<String>  allDimNosFromFile =  new HashSet<String>();
				for(String  dimCnNm:dimCnNmArr){
					String  dimno  =  allDimTypesIdenByDimNmFromDBMap.get(dimCnNm);
					if(StringUtils.isNotEmpty(dimno)){
						allDimNosFromFile.add(dimno);
					}
				}
				
				boolean dimFlag = false;
				for (String dimCnNm : dimCnNmArr) {
					if (!allDimTypesIdenByDimNmFromDBMap.containsKey(dimCnNm)&&!dimCnNm.equals("")) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(7);
						obj.setValidTypeNm("关联维度存在性校验");
						obj.setErrorMsg("关联维度\"" + dimCnNm + "\"不存在");
						errors.add(obj);
						dimFlag = true;
						break;
					}
				}
				if (dimFlag) {
					continue;
				}

				if(StringUtils.isNotBlank(deptId)){
					if(deptMap.get(deptId)== null){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(20);
						obj.setValidTypeNm("主管部门存在性校验");
						obj.setErrorMsg("主管部门\"" + deptId + "\"在数据库中不存在");
						errors.add(obj);
						continue;
					}
				}
				
				if(StringUtils.isNotBlank(userId)){
					if(userMap.get(userId)== null){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(27);
						obj.setValidTypeNm("联系人存在性校验");
						obj.setErrorMsg("联系人\"" + userId + "\"在数据库中不存在");
						errors.add(obj);
						continue;
					}
				}
				//业务分库
				if(StringUtils.isNotBlank(busiLibId)) {
					if(!busiLibMap.containsKey(busiLibId)) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(24);
						obj.setValidTypeNm("业务分库存在性校验");
						obj.setErrorMsg("业务分库\"" + busiLibId + "\"在数据库中不存在");
						errors.add(obj);
						continue;
					}
				}
				//业务类型校验
				if(StringUtils.isBlank(busiType)) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(19);
					obj.setValidTypeNm("业务类型非空校验");
					obj.setErrorMsg("业务类型不能为空");
					errors.add(obj);
					continue;
				}
				// RptIdxInfo rptIdxInfo = allInfoFromDBMap.get(indexNo);
//				if (rptIdxInfo != null) {
//					indexVerId = rptIdxInfo.getId().getIndexVerId();
//				}
				String catalogMapKey = "0_" + firstCatalogNm;
				RptIdxCatalog firstCatalog = catalogMap.get(catalogMapKey);
				if (firstCatalog == null) {
					firstCatalog = new RptIdxCatalog();
					firstCatalog.setUpNo("0");
					firstCatalog.setIndexCatalogNm(firstCatalogNm);
					firstCatalog.setIndexCatalogNo(RandomUtils.uuid2());
					String key = firstCatalog.getUpNo() + "_"
							+ firstCatalog.getIndexCatalogNm();
					catalogMap.put(
							key, firstCatalog);
					if (!catalogSaveMap.containsKey(key)) {
						catalogSaveList.add(firstCatalog);
						catalogSaveMap.put(key, firstCatalog);
					}
				}
				finalIndexCatalogNo = firstCatalog.getIndexCatalogNo();
				RptIdxCatalog secondCatalog = null;
				if (StringUtils.isNotBlank(secondCatalogNm)) {
					catalogMapKey = firstCatalog.getIndexCatalogNo() + "_"
							+ secondCatalogNm;
					secondCatalog = catalogMap.get(catalogMapKey);
					if (secondCatalog == null) {
						secondCatalog = new RptIdxCatalog();
						secondCatalog.setUpNo(firstCatalog.getIndexCatalogNo());
						secondCatalog.setIndexCatalogNm(secondCatalogNm);
						secondCatalog.setIndexCatalogNo(RandomUtils.uuid2());
						String key = secondCatalog.getUpNo() + "_"
								+ secondCatalog.getIndexCatalogNm();
						catalogMap.put(
								key, secondCatalog);
						if (!catalogSaveMap.containsKey(key)) {
							catalogSaveList.add(secondCatalog);
							catalogSaveMap.put(key, secondCatalog);
						}
					}
					finalIndexCatalogNo = secondCatalog.getIndexCatalogNo();
				}

				RptIdxCatalog thirdCatalog = null;
				if (StringUtils.isNotBlank(thirdCatalogNm)) {
					catalogMapKey = secondCatalog.getIndexCatalogNo() + "_"
							+ thirdCatalogNm;
					thirdCatalog = catalogMap.get(catalogMapKey);
					if (thirdCatalog == null) {
						thirdCatalog = new RptIdxCatalog();
						thirdCatalog.setUpNo(secondCatalog.getIndexCatalogNo());
						thirdCatalog.setIndexCatalogNm(thirdCatalogNm);
						thirdCatalog.setIndexCatalogNo(RandomUtils.uuid2());
						String key = thirdCatalog.getUpNo() + "_"
								+ thirdCatalog.getIndexCatalogNm();
						catalogMap.put(
								key, thirdCatalog);
						if (!catalogSaveMap.containsKey(key)) {
							catalogSaveList.add(thirdCatalog);
							catalogSaveMap.put(key, thirdCatalog);
						}
					}
					finalIndexCatalogNo = thirdCatalog.getIndexCatalogNo();
				}
				String newSrcIndexNos = "";
				String newSrcMeasureNos = "";
				if(StringUtils.isNotBlank(srcIndexNos)){
					String srcIndexArr[] = StringUtils.split(srcIndexNos, ",");
					for(int i = 0; i < srcIndexArr.length;i++){
						if(srcIndexArr[i].indexOf(".")>=0){
							String indexNos[] = StringUtils.split(srcIndexArr[i],".");
							newSrcIndexNos+=indexNos[0];
							newSrcMeasureNos+=indexNos[1];
						}
						else{
							newSrcIndexNos+=srcIndexArr[i];
							newSrcMeasureNos+=steadyMeasureNo;
						}
						if(i!=srcIndexArr.length-1){
							newSrcIndexNos+=",";
							newSrcMeasureNos+=",";
						}
					}
				}
				RptIdxInfoPK idxInfoPK = new RptIdxInfoPK();
				idxInfoPK.setIndexNo(indexNo);
				idxInfoPK.setIndexVerId(indexVerId);
				RptIdxInfo idxInfo = new RptIdxInfo();
				idxInfo.setId(idxInfoPK);
				idxInfo.setIndexCatalogNo(finalIndexCatalogNo);
				idxInfo.setIndexNm(indexNm);
				idxInfo.setStartDate(vo.getStartDate());
				idxInfo.setCalcCycle(vo.getCalcCycle());
				idxInfo.setIndexType(indexType);
				idxInfo.setSrcIndexNo(newSrcIndexNos);
				idxInfo.setSrcIndexMeasure(newSrcMeasureNos);
				idxInfo.setIsSum(vo.getIsSum());
				idxInfo.setIndexSts(vo.getIndexSts());
				idxInfo.setDataType(vo.getDataType());
				idxInfo.setDataUnit(vo.getDataUnit());
				idxInfo.setRemark(vo.getRemark());
				idxInfo.setIsSave(vo.getIsSave());
				idxInfo.setStatType(vo.getStatType());
				idxInfo.setEndDate("29991231");
				idxInfo.setLineId(vo.getLineId());
				idxInfo.setBusiLibId(busiLibId);//业务分库
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				String dateStr = sdf.format(new Date());
				idxInfo.setCreateDate(dateStr);
				idxInfo.setIsRptIndex(COMMON_BOOLEAN_NO);
				idxInfo.setIsCabin(COMMON_BOOLEAN_NO);
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				idxInfo.setLastUptUser(user.getUserId());
				idxInfo.setLastUptTime(new Timestamp(new Date().getTime()));
				idxInfo.setSrcSourceId(dsId);
				idxInfo.setDeptId(deptId);
				idxInfo.setUserId(userId);
				RptIdxBusiExtPK idxBusiExtPK = new RptIdxBusiExtPK();
				idxBusiExtPK.setIndexNo(indexNo);
				idxBusiExtPK.setIndexVerId(indexVerId);
				RptIdxBusiExt busiExt = new RptIdxBusiExt();
				busiExt.setId(idxBusiExtPK);
				busiExt.setIndexUsualNm(indexUsualNm);
				HashSet<String> srcIndexSets = new HashSet<String>();
				if(StringUtils.isNotBlank(newSrcIndexNos)){
					String[] srcIndexArr = StringUtils.split(srcIndexNos, ",");
					String[] srcIndexNoArr = StringUtils.split(newSrcIndexNos, ",");
					String[] srcMeasureNoArr = StringUtils.split(newSrcMeasureNos, ",");
					List<String> srcIndexNoList = new ArrayList<String>();
					for(int i = 0;i < srcIndexNoArr.length; i++){
						if(srcIndexNoList.contains(srcIndexArr[i])){
							continue;
						}
						RptIdxSrcRelInfo relinfo = new RptIdxSrcRelInfo();
						RptIdxSrcRelInfoPK id = new RptIdxSrcRelInfoPK();
						id.setIndexNo(idxInfo.getId().getIndexNo());
						id.setIndexVerId(idxInfo.getId().getIndexVerId());
						id.setSrcIndexNo(srcIndexNoArr[i]);
						id.setSrcMeasureNo(srcMeasureNoArr[i]);
						srcIndexNoList.add(srcIndexArr[i]);
						srcIndexSets.add(srcIndexArr[i]);
						relinfo.setId(id);
						srcRelSaveList.add(relinfo);
					}
				}
				// String dataSetNm = vo.getDatasetEnNm();
				// if(null != vo.getDatasetEnNm() && vo.getDatasetEnNm().indexOf(":")>=0){
				//	dataSetNm =StringUtils.split(vo.getDatasetEnNm(),":")[0];
				// }
//				idxInfo.setIdxBelongType(getSrcSys(idxInfo, srcIndexSets, setIdAndModelMap.get(dataSetNm), allSrcInfoFromDBMap));
				busiExt.setBusiDef(vo.getBusiDef());
				busiExt.setBusiRule(vo.getBusiRule());
				/* 复合指标（派生指标、组合指标）*/
				if (indexType.equals(DERIVE_INDEX)|| indexType.equals(COMPOSITE_INDEX)) {
					idxInfo.setSetId(datasetId);
					/*来源指标校验*/
					if (StringUtils.isNotEmpty(srcIndexNos)) {
						String[] srcIdxIds = StringUtils
								.split(srcIndexNos, ",");
						boolean flag = false;
						for (String srcIdxId : srcIdxIds) {
							if ((voIdxNoAndSrcInDBRelMap.get(indexNo) == null || !voIdxNoAndSrcInDBRelMap.get(indexNo).contains(srcIdxId))&& !voIndexNoSet.contains(srcIdxId)) {
								ValidErrorInfoObj obj = new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(13);
								obj.setValidTypeNm("来源指标存在性校验");
								obj.setErrorMsg("来源指标\"" + srcIdxId + "\"不存在");
								errors.add(obj);
								flag = true;
								break;
							}else{//来源指标业务类型是否和当前指标的业务类型一致，或者是基础类型
								String idxBusiType = this.idxInfoBS.getBusiTypeByIndexNo(srcIdxId);
								if(busiType.equals(idxBusiType) || "00".equals(idxBusiType)){
									idxInfo.setBusiType(busiType);
								}else{
									ValidErrorInfoObj obj = new ValidErrorInfoObj();
									obj.setSheetName(vo.getSheetName());
									obj.setExcelRowNo(vo.getExcelRowNo());
									obj.setExcelColNo(13);
									obj.setValidTypeNm("来源指标的业务类型校验");
									obj.setErrorMsg("来源指标\"" + srcIdxId + "\"的业务类型应与 当前指标的业务类型一致或为基础类型");
									errors.add(obj);
									flag = true;
									break;
								}
							}
						}
						if (flag) {
							continue;
						}
					} else {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(13);
						obj.setValidTypeNm("复杂(组合、派生)指标来源指标非空性校验");
						obj.setErrorMsg("复杂(组合、派生)指标来源指标不能为空");
						errors.add(obj);
						continue;
					}
                    Set<String>  allSrcDimSet =  new  HashSet<String>();
                    String[]   srcIndexNoArr  =  null;
                    if(StringUtils.isNotEmpty(srcIndexNos)){
                    	srcIndexNoArr  = StringUtils.split(srcIndexNos, ',');
                    	for(String  srcIndexNo:srcIndexNoArr){
                    		if(allSrcIdxDimRelFromDBMap.get(srcIndexNo)!=null){
                    		   allSrcDimSet.addAll(allSrcIdxDimRelFromDBMap.get(srcIndexNo));
                    		}
                    	}
                    }
                    boolean  srcDimInDBFlag  = false;
                    boolean  srcDimInFileFlag  = false;
                    for(String  dim:allDimNosFromFile){
                    	for(String  srcIndexNo:srcIndexNoArr){
                    		if(idxNoAndDimnosInFileRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosInFileRelMap.get(srcIndexNo).contains(dim)){
                    			srcDimInFileFlag  = true;
                    			break;
                    		}
                    	}
                    	if(!allSrcDimSet.contains(dim)&&!srcDimInFileFlag){
                    		//做进一步校验，根据来源指标对应的合法维度集合（来源指标通不过是不会到这一步的）
                    		boolean   exists  =  false;
                    		for(String  srcIndexNo:srcIndexNoArr){
                        		if((idxNoAndDimnosRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosRelMap.get(srcIndexNo).contains(dim))
                        				|| dim.equals(DIM_TYPE_INDEXNO_NAME)
                        				|| dim.equals(DIM_TYPE_DATE_NAME)
                        				|| dim.equals(DIM_TYPE_CURRENCY_NAME)
                        				|| dim.equals(DIM_TYPE_ORG_NAME)){
                        			exists = true;
                        			break;
                        		}
                        	}
                    		if(!exists){
	                    		RptDimTypeInfo  dimtype  =  allDimTypesIdenByDimNoFromDBMap.get(dim);
								String  dimnm  = dimtype.getDimTypeNm();
	                    		ValidErrorInfoObj obj = new ValidErrorInfoObj();
	    						obj.setSheetName(vo.getSheetName());
	    						obj.setExcelRowNo(vo.getExcelRowNo());
	    						obj.setExcelColNo(7);
	    						obj.setValidTypeNm("【关联维度】校验");
	    						obj.setErrorMsg("【关联维度】中维度\""+dimnm+"\"在来源指标对应的维度集合中不存在");
	    						errors.add(obj);
	    						srcDimInDBFlag =  true;
	    						break;
                    		}
                    	}
                    }
                    if(srcDimInDBFlag){
                    	continue;
                    }
					dimTypes.clear();
					if (dims.length() > 0) {
						String[] dimNmArr = StringUtils.split(dims, ',');
						Set<String>  dimNmSet  =  new  HashSet<String>();
						for(String  dimNm:dimNmArr){
							dimNmSet.add(dimNm);
						}
						for (String dimNms : dimNmSet) {
							dimTypes.add(allDimTypesIdenByDimNoFromDBMap.get(allDimTypesIdenByDimNmFromDBMap.get(dimNms)));
						}

					}
					RptIdxMeasureRelPK rimrpk = new RptIdxMeasureRelPK();
					rimrpk.setDsId(datasetId);
					rimrpk.setIndexNo(indexNo);
					rimrpk.setIndexVerId(indexVerId);
					rimrpk.setMeasureNo(steadyMeasureNo);
					RptIdxMeasureRel rimrel = new RptIdxMeasureRel();
					rimrel.setId(rimrpk);
					rimrel.setStoreCol(defaultMeasure);
					rimrel.setOrderNum(new BigDecimal(1));
					idxMeasureRelSaveList.add(rimrel);
					// 如果组合指标情况下，选择的来源指标是总账指标，则需要指定来源指标度量
					// idxInfo.setSrcIndexMeasure("measureNoFromSumAcc");//模板导入的方式，来源指标为总账指标没有意义，可以不不用考虑
					RptIdxFormulaInfoPK rifipk = new RptIdxFormulaInfoPK();
					rifipk.setIndexNo(indexNo);
					rifipk.setIndexVerId(indexVerId);
					RptIdxFormulaInfo rifinfo = new RptIdxFormulaInfo();
					rifinfo.setId(rifipk);
					int orderVal = 1;
					int complexIndexOrderVal = 1;// 复合指标（派生、组合）
					String filterMode = RPT_IDX_FILTER_MODE_IN;// RPT_IDX_FILTER_MODE_IN-"01"表示包含，RPT_IDX_FILTER_MODE_NOT_IN-"02"表示(模板导入默认使用"01")
					Map<String,RptIdxDimRel> dimRelMap = new HashMap<String, RptIdxDimRel>();
					Map<String,RptIdxFilterInfo> dimFiterMap = new HashMap<String, RptIdxFilterInfo>();
					
					//复合指标（组合指标、派生指标）默认加入维度（指标号、日期、机构、币种）到指标维度关系表
					//dimrel_org
					RptIdxDimRelPK ridrpk_org = new RptIdxDimRelPK();
					ridrpk_org.setDimNo(orgDimTypeNo);
					ridrpk_org.setDsId(datasetId);
					ridrpk_org.setIndexNo(indexNo);
					ridrpk_org.setIndexVerId(indexVerId);
					RptIdxDimRel ridrel_org = new RptIdxDimRel();
					ridrel_org.setId(ridrpk_org);
					ridrel_org.setDimType(DIM_TYPE_ORG);
					ridrel_org.setStoreCol(orgNo);
					ridrel_org.setOrderNum(new BigDecimal(orderVal++));
					dimRelMap.put(orgDimTypeNo, ridrel_org);
					
					//dimrel_date
					RptIdxDimRelPK ridrpk_date = new RptIdxDimRelPK();
					ridrpk_date.setDimNo(dateDimTypeNo);
					ridrpk_date.setDsId(datasetId);
					ridrpk_date.setIndexNo(indexNo);
					ridrpk_date.setIndexVerId(indexVerId);
					RptIdxDimRel ridrel_date = new RptIdxDimRel();
					ridrel_date.setId(ridrpk_date);
					ridrel_date.setDimType(DIM_TYPE_DATE);
					ridrel_date.setStoreCol(dataDate);
					ridrel_date.setOrderNum(new BigDecimal(orderVal++));
					dimRelMap.put(dateDimTypeNo, ridrel_date);
					//dimrel_index
					RptIdxDimRelPK ridrpk_index = new RptIdxDimRelPK();
					ridrpk_index.setDimNo(indexDimTypeNo);
					ridrpk_index.setDsId(datasetId);
					ridrpk_index.setIndexNo(indexNo);
					ridrpk_index.setIndexVerId(indexVerId);
					RptIdxDimRel ridrel_index = new RptIdxDimRel();
					ridrel_index.setId(ridrpk_index);
					ridrel_index.setDimType(DIM_TYPE_INDEXNO);
					ridrel_index.setStoreCol(indexNo_);
					ridrel_index.setOrderNum(new BigDecimal(orderVal++));
					dimRelMap.put(indexDimTypeNo, ridrel_index);
					
					if (indexType.equals(COMPOSITE_INDEX)) {// 组合指标
						filterInfoRelMap.clear();
						dimTypeFilterMap.clear();
						filterModeMap.clear();
						filterInfoRelTempMap.clear();
						String[] dimTypeItemRelArr = null;
						if (StringUtils.isNotEmpty(indexFormula)) {
							dimTypeItemRelArr = StringUtils.split(indexFormula, '\n');
						}
						if (dimTypeItemRelArr != null) {
							boolean  filterDimNmExists = false;
							boolean  filterDimItemNmExists = false;
							boolean  filterDimExists = false;
							Set<String>   filterDimNoSet  =  new HashSet<String>();
							for (String dimTypeItemRel : dimTypeItemRelArr) {// 分析完公式中的维度和维度项之间的关系
								String[] objArr0_ = StringUtils.split(dimTypeItemRel, ':');
								String dimTypeNm = objArr0_[0];
								String dimItemNms = objArr0_.length > 1 ? objArr0_[1]
										: "";
								String[] objArr1_ = StringUtils.split(dimItemNms, ',');
								String dimTypeNo = allDimTypesIdenByDimNmFromDBMap
										.get(dimTypeNm);
								if(StringUtils.isEmpty(dimTypeNo)){
									ValidErrorInfoObj obj = new ValidErrorInfoObj();
		    						obj.setSheetName(vo.getSheetName());
		    						obj.setExcelRowNo(vo.getExcelRowNo());
		    						obj.setExcelColNo(14);
		    						obj.setValidTypeNm("组合指标【指标公式】校验");
		    						obj.setErrorMsg("指标公式中维度\""+dimTypeNm+"\"在数据库中不存在");
		    						errors.add(obj);
									filterDimNmExists= true;
									break;
								}
								filterDimNoSet.add(dimTypeNo);
								List<RptDimItemInfoVO> items = allDimTypeAndItemsFromDBRelMap
										.get(dimTypeNo);
								cnItemRel.clear();
								for (RptDimItemInfoVO item : items) {
									cnItemRel.put(item.getDimItemNm(),
											item.getDimItemNo());
								}
								if(StringUtils.isNotEmpty(dimItemNms)){
									filterInfoRelTempMap.put(dimTypeNo,new ArrayList<String>());
								}
								filterModeMap.put(dimTypeNo, filterMode);
								for (String itemNm : objArr1_) {
									if(StringUtils.isNotEmpty(itemNm)&&(cnItemRel.size()==0||StringUtils.isEmpty(cnItemRel.get(itemNm)))){
										ValidErrorInfoObj obj = new ValidErrorInfoObj();
			    						obj.setSheetName(vo.getSheetName());
			    						obj.setExcelRowNo(vo.getExcelRowNo());
			    						obj.setExcelColNo(14);
			    						obj.setValidTypeNm("组合指标【指标公式】校验");
			    						obj.setErrorMsg("公式项\""+dimTypeItemRel+"\"中的维度值\""+itemNm+"\"在数据库中不存在");
			    						errors.add(obj);
										filterDimItemNmExists  =  true;
										break;
									}
									if(filterInfoRelTempMap.get(dimTypeNo)!=null)  filterInfoRelTempMap.get(dimTypeNo).add(cnItemRel.get(itemNm));
								}
								if(filterDimItemNmExists){
									break;
								}
							}
							if(filterDimNmExists){
								continue;
							}
							if(filterDimItemNmExists){
								continue;
							}
							srcDimInFileFlag  =  false;
							for(String  dimno:filterDimNoSet){
								for(String  srcIndexNo:srcIndexNoArr){
		                    		if(idxNoAndDimnosInFileRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosInFileRelMap.get(srcIndexNo).contains(dimno)){
		                    			srcDimInFileFlag  = true;
		                    			break;
		                    		}
		                    	}
		                    	if(!allSrcDimSet.contains(dimno)&&!srcDimInFileFlag){
		                    		//做进一步校验，根据来源指标对应的合法维度集合（来源指标通不过是不会到这一步的）
		                    		boolean   exists  =  false;
		                    		for(String  srcIndexNo:srcIndexNoArr){
		                        		if(idxNoAndDimnosRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosRelMap.get(srcIndexNo).contains(dimno)){
		                        			exists = true;
		                        			break;
		                        		}
		                        	}
		                    		if(!exists){
			                    		RptDimTypeInfo  dimtype  =  allDimTypesIdenByDimNoFromDBMap.get(dimno);
										String  dimnm  = dimtype.getDimTypeNm();
			                    		ValidErrorInfoObj obj = new ValidErrorInfoObj();
			    						obj.setSheetName(vo.getSheetName());
			    						obj.setExcelRowNo(vo.getExcelRowNo());
			    						obj.setExcelColNo(14);
			    						obj.setValidTypeNm("组合指标【指标公式】校验");
			    						obj.setErrorMsg("【指标公式】中维度\""+dimnm+"\"在来源指标对应的维度集合中不存在");
			    						errors.add(obj);
			    						filterDimExists =  true;
			                    		break;
		                    		}
		                    	}
		                    }
							if(filterDimExists){
								continue;
							}
						}
						for (RptDimTypeInfo info : dimTypes) {// 将剩余的维度加入filterInfoRelMap,其余的应该是特殊维度或者没有减维的普通维度
							if (filterInfoRelTempMap.get(info.getDimTypeNo()) != null) {
								dimTypeFilterMap.put(info.getDimTypeNo(), info);
								filterInfoRelMap.put(info.getDimTypeNo(),
										filterInfoRelTempMap.get(info
												.getDimTypeNo()));
							} else {
								filterInfoRelMap.put(info.getDimTypeNo(), null);
							}
						}
						for(String  key: filterInfoRelTempMap.keySet()){
							if(filterInfoRelMap.get(key)==null){
								filterInfoRelMap.put(key, filterInfoRelTempMap.get(key));
							}
						}
						String isSum = IS_SUM_FALSE;
						RptIdxInfo srcIdxInfo = allSrcInfoFromDBMap
								.get(srcIndexNos);
						if (srcIdxInfo != null) {
							String thisIsSum = srcIdxInfo.getIsSum();
							if (StringUtils.isNotEmpty(thisIsSum)) {
								isSum = thisIsSum;
							}
						}
						// 拼凑公式
						String formulaContent = IdxFormulaUtils
								.generateByDimFilter(filterModeMap, isSum,
										filterInfoRelTempMap, dimTypeFilterMap);

						Set<String> keys = filterInfoRelMap.keySet();
						for (String key : keys) {
							RptDimTypeInfo   rptDimTypeInfo_  =  allDimTypesIdenByDimNoFromDBMap.get(key);
							// 过滤信息
							if(rptDimTypeInfo_==null){
								continue;
							}
							
							RptIdxFilterInfoPK rifilteripk = new RptIdxFilterInfoPK();
							rifilteripk.setDimNo(key);
							rifilteripk.setIndexNo(indexNo);
							rifilteripk.setIndexVerId(indexVerId);
							List<String> itemNoList = filterInfoRelMap.get(key);
							String itemNos = "";
							if (itemNoList != null) {
								for (String itemNo : itemNoList) {
									if(StringUtils.isNotEmpty(itemNo)){
										itemNos += itemNo + ",";
									}
								}
							}
							if (itemNos.length() > 0) {
								itemNos = itemNos.substring(0,
										itemNos.length() - 1);
							}
							RptIdxFilterInfo rifilterinfo = new RptIdxFilterInfo();
							rifilterinfo.setId(rifilteripk);
							rifilterinfo.setFilterMode(filterMode);
							rifilterinfo.setFilterVal(itemNos);
							if(StringUtils.isNotBlank(itemNos))
								dimFiterMap.put(rifilteripk.getDimNo(), rifilterinfo);

							// 维度信息
							RptIdxDimRelPK ridrpk = new RptIdxDimRelPK();
							ridrpk.setDimNo(key);
							ridrpk.setDsId(datasetId);
							ridrpk.setIndexNo(indexNo);
							ridrpk.setIndexVerId(indexVerId);
							RptIdxDimRel ridrel = new RptIdxDimRel();
							ridrel.setId(ridrpk);
							if (key.equals(orgDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_ORG);
								ridrel.setStoreCol(orgNo);
							} else if (key.equals(dateDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_DATE);
								ridrel.setStoreCol(dataDate);
							} else if (key.equals(currencyDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_CURRENCY);
								ridrel.setStoreCol(currType);
							} else if (key.equals(indexDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_INDEXNO);
								ridrel.setStoreCol(indexNo_);
							} else {
								ridrel.setStoreCol(defaultDim+ (complexIndexOrderVal++));
								ridrel.setDimType(DIM_TYPE_BUSI);
							}
							ridrel.setOrderNum(new BigDecimal(orderVal++));
							dimRelMap.put(ridrel.getId().getDimNo(), ridrel);
						}
						// 公式信息
						rifinfo.setFormulaContent(formulaContent);
						rifinfo.setFormulaDesc("");
						rifinfo.setFormulaType(FORMULA_TYPE_FILTER);
						idxFormulaSaveList.add(rifinfo);
					} else {// 派生指标
						//校验公式信息
						if(StringUtils.isEmpty(indexFormula)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(11);
							obj.setValidTypeNm("派生指标指标公式非空校验");
							obj.setErrorMsg("派生指标的指标公式不能为空！");
							errors.add(obj);
							continue;
						}
						rifinfo.setFormulaContent(indexFormula);
						// rifinfo.setFormulaDesc(formulaDesc);
						rifinfo.setFormulaType(FORMULA_TYPE_CALC);
						idxFormulaSaveList.add(rifinfo);
						if (dimTypes != null) {
							// 维度信息
							for (RptDimTypeInfo info : dimTypes) {
								if(info==null){
									continue;
								}
								String dimTypeNo = info.getDimTypeNo();
								RptIdxDimRelPK ridrpk = new RptIdxDimRelPK();
								ridrpk.setDimNo(dimTypeNo);
								ridrpk.setDsId(datasetId);
								ridrpk.setIndexNo(indexNo);
								ridrpk.setIndexVerId(indexVerId);
								RptIdxDimRel ridrel = new RptIdxDimRel();
								ridrel.setId(ridrpk);
								if (dimTypeNo.equals(orgDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_ORG);
									ridrel.setStoreCol(orgNo);
								} else if (dimTypeNo.equals(dateDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_DATE);
									ridrel.setStoreCol(dataDate);
								} else if (dimTypeNo.equals(currencyDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_CURRENCY);
									ridrel.setStoreCol(currType);
								} else if (dimTypeNo.equals(indexDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_INDEXNO);
									ridrel.setStoreCol(indexNo_);
								} else {
									ridrel.setStoreCol(defaultDim+ (complexIndexOrderVal++));
									ridrel.setDimType(DIM_TYPE_BUSI);
								}
								ridrel.setOrderNum(new BigDecimal(orderVal++));
								dimRelMap.put(ridrel.getId().getDimNo(), ridrel);
							}
						}
					}
					for(String key : dimRelMap.keySet()){
						idxDimRelSaveList.add(dimRelMap.get(key));
					}

					for(String key : dimFiterMap.keySet()){
						idxFilterSaveList.add(dimFiterMap.get(key));
					}
				} else {// 基础指标（根指标、总账指标、泛化指标）
					
					if(StringUtils.isEmpty(datasetCnNm)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(11);
						obj.setValidTypeNm("数据集中文名基础指标非空校验");
						obj.setErrorMsg("基础指标(根指标、总账指标、泛化指标)的数据集中文名不能为空");
						errors.add(obj);
						continue;
					}
					String[]  datasetCnNmArr  = StringUtils.split(datasetCnNm, '\n');
					boolean   dsCnNmExists  = false;
					for(String dsCnNm:datasetCnNmArr){
						if(!setIdAndSetNmRelMap.containsValue(dsCnNm)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(11);
							obj.setValidTypeNm("数据集中文名基础指标存在校验");
							obj.setErrorMsg("【数据集中文名】\""+dsCnNm+"\"在数据库中不存在");
							errors.add(obj);
							dsCnNmExists  = true;
							break;
						}
					}
					if(dsCnNmExists){
						continue;
					}
					tableColRelMap.clear();
					tableColRelArr = StringUtils.split(datasetEnNm, '\n');
					for (int i = 0; i < tableColRelArr.length; i++) {
						String tableColRel = tableColRelArr[i];
						String[] objArr = StringUtils.split(tableColRel, ':');
						String setId = objArr[0];
						String columnNm = objArr[1];
						tableColRelMap.put(setId, columnNm);
					}

					Set<Entry<String, String>> tableColRelEntries = tableColRelMap
							.entrySet();// 是有序的
				    Map<String,String>   allUniqueDimNoAndTableEntryRelMap  =  Maps.newHashMap();
				    boolean  dsRelatedF = false;
					for (Entry<String,String> entry : tableColRelEntries) {// 对每一个数据集进行遍历
						String measureColEnNm = entry.getValue();
						String setId = entry.getKey();
						if(StringUtils.isEmpty(setId)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(12);
							obj.setValidTypeNm("数据集ID存在性校验");
							obj.setErrorMsg("【数据集ID】其中一项\""+(entry.getKey()+":"+measureColEnNm)+"\"对应的数据集不存在");
							errors.add(obj);
							dsRelatedF = true;
							break;
						}else{
							if(!StringUtils.isNotBlank(idxInfo.getSetId())){
								idxInfo.setSetId(setId);
							}
							String moduleBusiType = this.idxInfoBS.getBusiTypeBySetId(setId);
							if(busiType.equals(moduleBusiType) || "00".equals(moduleBusiType)){//数据集业务类型校验
								idxInfo.setBusiType(busiType);
							}else{
								ValidErrorInfoObj obj = new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(12);
								obj.setValidTypeNm("数据集业务类型校验");
								obj.setErrorMsg("【数据集ID】其中一项\""+(entry.getKey()+":"+measureColEnNm)+"\"的业务类型应与 当前指标的业务类型一致或为基础类型");
								errors.add(obj);
								dsRelatedF = true;
								break;
							}
						}
						List<RptSysModuleCol> dbCols = setIdAndColListRelMap.get(setId);
						boolean  measureExists =  false;
						for (RptSysModuleCol col : dbCols) {
							String  enNm  = col.getEnNm();
							if(StringUtils.isNotEmpty(enNm)&&enNm.toUpperCase().equals(measureColEnNm.toUpperCase())&&COL_TYPE_MEASURE.equals(col.getColType())){
								measureExists  =  true;
								break;
							}
						}
						if(!measureExists && !indexType.equals(SUM_ACCOUNT_INDEX)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(12);
							obj.setValidTypeNm("数据集英文名存在性校验");
							obj.setErrorMsg("【数据集英文名】其中一项\""+(entry.getKey()+":"+measureColEnNm)+"\"中的字段\""+measureColEnNm+"\"不存在或该字段类型不是度量");
							errors.add(obj);
							dsRelatedF=true;
							break;
						}
						int measureOrderNum = 1;
						int dimOrderNum = 1;
						List<String> meList = new ArrayList<String>();
						if (indexType.equals(SUM_ACCOUNT_INDEX)){
							if(StringUtils.isNotBlank(vo.getDatasetId())){
								String attrs[] = StringUtils.split(vo.getDatasetId(),":");
								if(attrs.length  == 2){
									meList = ArrayUtils.asList(attrs[1], ",");
								}
							}
						}
						for (RptSysModuleCol  col:dbCols) {
								String  colType  = col.getColType();
								String colEnNm  = col.getEnNm();
								if (colType.equals(COL_TYPE_MEASURE)) {// 该字段为度量字段
									RptIdxMeasureRelPK rimrpk = new RptIdxMeasureRelPK();
									rimrpk.setDsId(setId);
									rimrpk.setIndexNo(indexNo);
									rimrpk.setIndexVerId(indexVerId);
									  /* 1.根指标可以选择多个数据集，每一个数据集只能对应一个度量，且该度量和模板中给出的一致,这和前台页面做的一致
									   2.泛化指标只可以选择一个数据集，该数据集只能对应一个度量，且该度量和模板中给出的一致
									   3.总账指标只可以选择一个数据集，该数据集的所有度量都该写入指标度量关系表中*/
									if (!indexType.equals(SUM_ACCOUNT_INDEX)&& (measureOrderNum == 2||!colEnNm.toUpperCase().equals(measureColEnNm.toUpperCase()))) {
										continue;
									}
									if(indexType.equals(SUM_ACCOUNT_INDEX) && !meList.contains(col.getMeasureNo())){
										continue;
									}
									if (indexType.equals(ROOT_INDEX)||indexType.equals(GENERIC_INDEX)) {// 根指标或者泛化指标
										rimrpk.setMeasureNo(steadyMeasureNo);
									} else {
										rimrpk.setMeasureNo(col.getMeasureNo());// 总账指标
									}
									RptIdxMeasureRel rimrel = new RptIdxMeasureRel();
									rimrel.setId(rimrpk);
									if(indexType.equals(SUM_ACCOUNT_INDEX)){
										  if(measureOrderNum==1&&!colEnNm.equals(measureColEnNm)){
											  measureOrderNum  = 2;
											  rimrel.setOrderNum(new BigDecimal(measureOrderNum++));
										  }else{
											  if(colEnNm.equals(measureColEnNm)){//总账指标和模板中【数据集英文字段】对应的度量字段一致，则排序置为1
												  rimrel.setOrderNum(new BigDecimal(1));
											  }else{
												  rimrel.setOrderNum(new BigDecimal(measureOrderNum++));
											  }
										  }
									}else{
									      rimrel.setOrderNum(new BigDecimal(measureOrderNum++));
									}
									rimrel.setStoreCol(colEnNm);
									idxMeasureRelSaveList.add(rimrel);
								}else if (col.getColType().equals(COL_TYPE_DIM)) {// 该字段为维度字段
									RptIdxDimRelPK ridrpk = new RptIdxDimRelPK();
									String  dimno  =  col.getDimTypeNo();
									RptDimTypeInfo   dim   =  allDimTypesIdenByDimNoFromDBMap.get(dimno);
									allUniqueDimNoAndTableEntryRelMap.put(dimno,setIdAndSetNmRelMap.get(setId)+"##"+dim.getDimTypeNm());
									ridrpk.setDimNo(col.getDimTypeNo());
									ridrpk.setDsId(setId);
									ridrpk.setIndexNo(indexNo);
									ridrpk.setIndexVerId(indexVerId);
									RptIdxDimRel ridrel = new RptIdxDimRel();
									ridrel.setId(ridrpk);
									if (col.getDimTypeNo().equals(orgDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_ORG);
									} else if (col.getDimTypeNo().equals(dateDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_DATE);
									} else if (col.getDimTypeNo().equals(currencyDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_CURRENCY);
									} else if (col.getDimTypeNo().equals(indexDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_INDEXNO);
									} else {
										ridrel.setDimType(DIM_TYPE_BUSI);
									}
									ridrel.setOrderNum(new BigDecimal(dimOrderNum++));
									ridrel.setStoreCol(colEnNm);
									idxDimRelSaveList.add(ridrel);
								}
						}
					}
					if(dsRelatedF){
						continue;
					}
					boolean  flag2 = false;
					for(String key :allDimNosFromFile){
						if(!allUniqueDimNoAndTableEntryRelMap.containsKey(key)){
							RptDimTypeInfo  dim  =  allDimTypesIdenByDimNoFromDBMap.get(key);
							String  dimnm  = dim.getDimTypeNm();
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(7);
							obj.setValidTypeNm("【关联维度】和【数据集英文名】维度信息匹配校验");
							obj.setErrorMsg("【关联维度】中的维度\""+dimnm+"\"在【数据集英文名】对应的数据集中不存在");
							errors.add(obj);
							flag2  = true;
						    break;
						}
					}
					if(flag2){
						continue;
					}
				}

				idxInfo.setRemark(busiExt.getBusiDef());
				idxSaveList.add(idxInfo);
				idxBusiSaveList.add(busiExt);
			}// method - if - for - end
			List<List<?>> lists = new ArrayList<List<?>>();
			lists.add(catalogSaveList);
			lists.add(idxSaveList);
			lists.add(idxBusiSaveList);
			lists.add(idxMeasureRelSaveList);
			lists.add(idxDimRelSaveList);
			lists.add(idxFormulaSaveList);
			lists.add(idxFilterSaveList);
			lists.add(idxDelList);
			lists.add(srcRelSaveList);
			lists.add(idxOldInfoUptList);
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					lists);
		}// method - if - end
		return errors;
	}

	/*private String getSrcSys(RptIdxInfo idx,HashSet<String> srcIndex,RptSysModuleInfo info,Map<String,RptIdxInfo> infoMaps){
		String indexType = idx.getIndexType();
		String sysCode = GlobalConstants4plugin.ORG_TYPE_PUBLIC;
		if(indexType.equals(SUM_ACCOUNT_INDEX)){
			return GlobalConstants4plugin.ORG_TYPE_GJ;
		}
		else if(indexType.equals(ROOT_INDEX)||indexType.equals(GENERIC_INDEX)){
			if(info != null && StringUtils.isNotBlank(info.getSrcSysCode())){
				return info.getSrcSysCode();
			}
			else{
				return GlobalConstants4plugin.ORG_TYPE_PUBLIC;
			}
			
		}
		else if(indexType.equals(COMPOSITE_INDEX)||indexType.equals(DERIVE_INDEX)){
			if(srcIndex != null && srcIndex.size() > 0){
				for(String indexNo : srcIndex){
					String sCode = GlobalConstants4plugin.ORG_TYPE_PUBLIC;
					if(infoMaps.get(indexNo)!=null){
						sCode = infoMaps.get(indexNo).getIdxBelongType();
					}
					if(sCode.equals(GlobalConstants4plugin.ORG_TYPE_PUBLIC)){
						return GlobalConstants4plugin.ORG_TYPE_PUBLIC;
					}
					else{
						if(sysCode.equals("")){
							sysCode = sCode;
						}
						else{
							if(!sysCode.equals(sCode)){
								return GlobalConstants4plugin.ORG_TYPE_PUBLIC;
							}
						}
					}
				}
			}
		}
		return sysCode;
	}*/
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();

		List<IndexImportVO> vos = (List<IndexImportVO>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);

		if (vos != null && vos.size() > 0) {// method - if - start
			// 全局，避免开销
			// 1.用来装dims(维度1,维度2,维度3)对应的维度数据结构
			List<RptDimTypeInfo> dimTypes = Lists.newArrayList();
			Map<String, RptDimTypeInfo> dimTypeFilterMap = Maps.newHashMap();
			// 2.用来处理组合指标的过滤维度关系
			Map<String, List<String>> filterInfoRelMap = Maps
					.newLinkedHashMap();
			Map<String, List<String>> filterInfoRelTempMap = Maps
					.newLinkedHashMap();
			Map<String, String> filterModeMap = Maps.newLinkedHashMap();
			// 3.用来处理组合指标的某个维度项中文名称和该维度项关系map
			Map<String, String> cnItemRel = Maps.newHashMap();
			// 4.参数通用map
            //5.文件中map<指标编号,set<【关联维度】中文名对应的维度标识去重集合>>
			Map<String,Set<String>>   idxNoAndDimnosInFileRelMap  =  new HashMap<String, Set<String>>();
			//6。记载指标编号和对应的维度关系（在数据库中已经存在或者未存在）map
			//弥补了需求变更（基础指标的【关联维度】可以为空）带来的麻烦
			Map<String,Set<String>>   idxNoAndDimnosRelMap  =  new HashMap<String, Set<String>>();  
			//部门Map验证部门存在性
			Map<String,String> deptMap = new HashMap<String, String>();
			//用户Map验证部门存在性
			Map<String,String> userMap = new HashMap<String, String>();
			
			List<BioneDeptInfo> depts = this.idxInfoBS.getEntityListByProperty(BioneDeptInfo.class, "logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			if(depts != null && depts.size() > 0){
				for(BioneDeptInfo dept : depts){
					deptMap.put(dept.getDeptId(), dept.getDeptName());
				}
			}
			
			List<BioneUserInfo> infos = this.idxInfoBS.getAllEntityList(BioneUserInfo.class, "userId", false);
			if(infos != null && infos.size() > 0){
				for(BioneUserInfo info : infos){
					userMap.put(info.getUserId(), info.getUserName());
				}
			}
			
			//业务分库信息
			Map<String,String> busiLibMap = Maps.newHashMap();
			List<RptMgrBusiLibInfo> busiLibs = this.idxInfoBS.getAllEntityList(RptMgrBusiLibInfo.class);
			if(busiLibs != null && !busiLibs.isEmpty()) {
				for(RptMgrBusiLibInfo busiLib : busiLibs) {
					busiLibMap.put(busiLib.getBusiLibId(), busiLib.getBusiLibNm());
				}
			}
			
			// 指标目录内存信息
			List<RptIdxCatalog> catalogs = this.idxInfoBS.getAllCatalog();
			Map<String, RptIdxCatalog> catalogMap = Maps.newHashMap();
			if (catalogs != null) {
				for (RptIdxCatalog catalog : catalogs) {
					catalogMap.put(
							catalog.getUpNo() + "_"
									+ catalog.getIndexCatalogNm(), catalog);
				}
			}
			// 维度相关内存信息
			List<RptDimTypeInfo> allDimTypesFromDB = this.rptDimBS
					.getAllRptDimTypeInfos();
			List<RptDimItemInfoVO> allDimItemsFromDB = this.rptDimBS
					.getAllRptDimItemInfos();
			Map<String, RptDimTypeInfo> allDimTypesIdenByDimNoFromDBMap = Maps
					.newHashMap();
			Map<String, String> allDimTypesIdenByDimNmFromDBMap = Maps
					.newHashMap();
			Map<String, List<RptDimItemInfoVO>> allDimTypeAndItemsFromDBRelMap = Maps
					.newHashMap();
			if (allDimTypesFromDB != null && allDimTypesFromDB.size() > 0) {
				for (RptDimTypeInfo type : allDimTypesFromDB) {
					allDimTypesIdenByDimNmFromDBMap.put(type.getDimTypeNm(),
							type.getDimTypeNo());
					allDimTypesIdenByDimNoFromDBMap.put(type.getDimTypeNo(),
							type);
					allDimTypeAndItemsFromDBRelMap.put(type.getDimTypeNo(),
							new ArrayList<RptDimItemInfoVO>());
				}

			}
			if (allDimItemsFromDB != null && allDimItemsFromDB.size() > 0) {
				for (RptDimItemInfoVO vo : allDimItemsFromDB) {
					// 下面的get方法执行后返回值不应该为空
					if(allDimTypeAndItemsFromDBRelMap.get(vo.getDimTypeNo()) != null) allDimTypeAndItemsFromDBRelMap.get(vo.getDimTypeNo()).add(vo);
				}
			}

			// 指标内存信息
			Set<String> voIndexNoSet = new HashSet<String>();
			Set<String> voSrcIndexNoSet = new HashSet<String>();
			Set<String> voSrcMeaIndexNoSet = new HashSet<String>();
			Set<String> voIndexNmSet = new HashSet<String>();
			Map<String, Set<String>> voIdxNoAndSrcInFileRelMap = Maps.newHashMap();
			Map<String, Set<String>> voIdxNoAndSrcInDBRelMap = Maps.newHashMap();
			Map<String, RptIdxInfo> allSrcInfoFromDBMap = Maps.newHashMap();
			Map<String, RptIdxMeasureRel> allSrcMeaInfoFromDBMap = Maps.newHashMap();
			Map<String, Set<String>> allSrcIdxDimRelFromDBMap = Maps.newHashMap();
			Map<String, RptIdxInfo> allInfoFromDBMap = Maps.newHashMap();
			
			// 数据集相关内存信息
			Map<String, Object> param2 = Maps.newHashMap();
			param2.put("sourceId", dsId);
			param2.put("notsetType", SET_TYPE_DETAIL);
			List<RptSysModuleInfo> allSysModuleInfoFromDBByDsId = this.rptDatasetBS
					.getDataSetsByParams(param2);// 通过数据源获取数据集
			List<RptSysModuleCol> allSysModuleColFromDB = this.rptDatasetBS
					.getDatacolsByParams(param2);
			Map<String, String> tableEnNmAndSetRelMap = Maps.newHashMap();
			Map<String, List<RptSysModuleCol>> setIdAndColListRelMap = Maps
					.newHashMap();
			Map<String, String> setIdAndSetNmRelMap = Maps
					.newHashMap();
			Map<String, RptSysModuleInfo> setIdAndModelMap = Maps
					.newHashMap();
			if (allSysModuleInfoFromDBByDsId != null
					&& allSysModuleInfoFromDBByDsId.size() > 0) {
				for (RptSysModuleInfo module : allSysModuleInfoFromDBByDsId) {
					tableEnNmAndSetRelMap.put(module.getTableEnNm(),
							module.getSetId());
					setIdAndColListRelMap.put(module.getSetId(),
							new ArrayList<RptSysModuleCol>());
					setIdAndSetNmRelMap.put(module.getSetId(), module.getSetNm());
					setIdAndModelMap.put(module.getTableEnNm(), module);
				}
			}
			if (allSysModuleColFromDB != null
					&& allSysModuleColFromDB.size() > 0) {
				for (RptSysModuleCol col : allSysModuleColFromDB) {
					if (setIdAndColListRelMap.get(col.getSetId()) != null) {
						setIdAndColListRelMap.get(col.getSetId()).add(col);
					}
				}
			}
			
			
			for (IndexImportVO vo : vos) {
				String temp_idxno = vo.getIndexNo();
				String index_type =  vo.getIndexType();
				String ds_id  =  vo.getDatasetId();
				idxNoAndDimnosRelMap.put(temp_idxno, new HashSet<String>());
                if(DERIVE_INDEX.equals(index_type)|| COMPOSITE_INDEX.equals(index_type)){
					String   dims  =  vo.getDims();
					String   indexFormula  =  vo.getIndexFormula();
					String[] dimTypeItemRelArr = null;
					if (StringUtils.isNotEmpty(indexFormula)) {
						dimTypeItemRelArr = StringUtils.split(indexFormula, '\n');
						if (dimTypeItemRelArr != null) {
							for (String dimTypeItemRel : dimTypeItemRelArr) {// 分析完公式中的维度和维度项之间的关系
								String[] objArr0_ = StringUtils.split(dimTypeItemRel, ':');
								String dimTypeNm = objArr0_[0];
								String dimTypeNo = allDimTypesIdenByDimNmFromDBMap
										.get(dimTypeNm);
								if(StringUtils.isNotEmpty(dimTypeNo)){
									idxNoAndDimnosRelMap.get(temp_idxno).add(dimTypeNo);
								}
							}
						}	
					}
					if(StringUtils.isNotEmpty(dims)){
						String[]   dimCnNmArr  = StringUtils.split(dims, ',');
						for(String  dimCnNm:dimCnNmArr){
							String  dimno  =  allDimTypesIdenByDimNmFromDBMap.get(dimCnNm);
							if(StringUtils.isNotEmpty(dimno)){
								idxNoAndDimnosRelMap.get(temp_idxno).add(dimno);
							}
						}
					}
				}else{
					if(StringUtils.isNotBlank(ds_id)){
						String[]  dsEnNmArr = StringUtils.split(ds_id, ':');
						String  setId = dsEnNmArr[0];
						List<RptSysModuleCol>  cols  =  setIdAndColListRelMap.get(setId);
						if(cols != null && cols.size() > 0){
							for(RptSysModuleCol col:cols){
								if(col.getColType()!=null&&col.getColType().equals(COL_TYPE_DIM)&&StringUtils.isNotEmpty(col.getDimTypeNo())){
									idxNoAndDimnosRelMap.get(temp_idxno).add(col.getDimTypeNo());
								}
							}
						}
					}
				}
				String temp_srcidxno = vo.getSrcIndexNo();
				String temp_idxnm = vo.getIndexNm();
				String dims  =  vo.getDims()==null?"":vo.getDims();
				// 文件中可能出现相同的指标编号,所以只保存第一次出现的指标编号的相关信息
				if (StringUtils.isNotEmpty(temp_idxno)&& !voIndexNoSet.contains(temp_idxno)) {
					voIndexNoSet.add(temp_idxno);
					
					String[] dimCnNmArr = StringUtils.split(dims, ',');//基础指标的关联维度已经是可空的，前面已经用代码手动做过非空校验了
					Set<String>  allDimNosFromFile =  new HashSet<String>();
					for(String  dimCnNm:dimCnNmArr){
						String  dimno  =  allDimTypesIdenByDimNmFromDBMap.get(dimCnNm);
						if(StringUtils.isNotEmpty(dimno)){
							allDimNosFromFile.add(dimno);
						}
					}
					idxNoAndDimnosInFileRelMap.put(temp_idxno, allDimNosFromFile);
					
					
					voIdxNoAndSrcInFileRelMap.put(temp_idxno,new HashSet<String>());
					voIdxNoAndSrcInDBRelMap.put(temp_idxno,new HashSet<String>());
					if (StringUtils.isNotEmpty(temp_srcidxno)) {
						String[] temp_srcidxno_arr = StringUtils.split(temp_srcidxno, ',');
						for (String srcidxno : temp_srcidxno_arr) {
							if(srcidxno.indexOf(".")>=0){
								String srcIdxNos[] = StringUtils.split(srcidxno,".");
								voSrcIndexNoSet.add(srcIdxNos[0]);
								voSrcMeaIndexNoSet.add(srcIdxNos[0]);
							}
							else{
								voSrcIndexNoSet.add(srcidxno);
							}
							// value是HashSet,可以去重
							voIdxNoAndSrcInFileRelMap.get(temp_idxno).add(srcidxno);
						}
					}
				}
				if (StringUtils.isNotEmpty(temp_idxnm)) {
					voIndexNmSet.add(temp_idxnm);
				}
			}
			// 指标内存信息--指标名称和别名
			Map<String, Object> param1_ = Maps.newHashMap();
			param1_.put("indexNmList", ReBuildParam.splitLists(new ArrayList<String>(voIndexNmSet)));
			List<RptIdxInfo> idxListByNmList = Lists.newArrayList();
			if(voIndexNmSet.size()>0){
				idxListByNmList  = this.idxInfoBS
					.getIdxListByNmList(param1_);
			}
			Map<String, List<String>> idxNmIndexNoListRelMap = Maps
					.newHashMap();
			if (idxListByNmList != null) {
				for (RptIdxInfo info : idxListByNmList) {
					String indexNm = info.getIndexNm();
					String indexNo = info.getId().getIndexNo();
					if (!idxNmIndexNoListRelMap.containsKey(indexNm)) {
						idxNmIndexNoListRelMap.put(indexNm,
								new ArrayList<String>());
					}
					idxNmIndexNoListRelMap.get(indexNm).add(indexNo);
				}
			}
			//指标内存信息
			Map<String, Object> param4_ = Maps.newHashMap();
			param4_.put("list", ReBuildParam.splitLists(new ArrayList<String>(voIndexNoSet)));
			List<RptIdxInfo> allInfoFromDB = this.idxInfoBS.listIdxInfo(param4_);
			if (allInfoFromDB != null) {
				for (RptIdxInfo info : allInfoFromDB) {
					allInfoFromDBMap.put(info.getId().getIndexNo(), info);
				}
			}
			Map<String, Object> param0_ = Maps.newHashMap();
			List<RptIdxInfo> allSrcInfoFromDB = new ArrayList<RptIdxInfo>();
			List<RptIdxDimRel>  allSrcIdxDimRelList = new ArrayList<RptIdxDimRel>();
			List<RptIdxMeasureRel>  allSrcIdxMeasureRelList = new ArrayList<RptIdxMeasureRel>();
			
			if(voSrcIndexNoSet.size()>0){
			  param0_.put("list", ReBuildParam.splitLists(new ArrayList<String>(voSrcIndexNoSet)));
			  param0_.put("idxNoList", ReBuildParam.splitLists(new ArrayList<String>(voSrcIndexNoSet)));
			  allSrcInfoFromDB = this.idxInfoBS.listIdxInfo(param0_);
			  allSrcIdxDimRelList = this.idxInfoBS.getIdxDimRelByParams(param0_);
			}
			if(voSrcMeaIndexNoSet.size()>0){
				 param0_.put("idxNoList", ReBuildParam.splitLists(new ArrayList<String>(voSrcMeaIndexNoSet)));
				 allSrcIdxMeasureRelList = this.idxInfoBS.getIdxMeasureRelMaxByParams(param0_);
			}
			
			if(allSrcIdxDimRelList != null) {
				for(RptIdxDimRel dimrel:allSrcIdxDimRelList){
					String key  =   dimrel.getId().getIndexNo();
					if(allSrcIdxDimRelFromDBMap.get(key)==null){
						allSrcIdxDimRelFromDBMap.put(key, new HashSet<String>());
					}
					String  dimno  =  dimrel.getId().getDimNo();
					if(StringUtils.isNotEmpty(dimno)){
					   allSrcIdxDimRelFromDBMap.get(key).add(dimno);
					}
				}
			}
			if(allSrcIdxMeasureRelList != null) {
				for(RptIdxMeasureRel mearel:allSrcIdxMeasureRelList){
					allSrcMeaInfoFromDBMap.put(mearel.getId().getIndexNo()+"."+mearel.getId().getMeasureNo(), mearel);
				}
			}
			if (allSrcInfoFromDB != null) {
				for (RptIdxInfo info : allSrcInfoFromDB) {
					allSrcInfoFromDBMap.put(info.getId().getIndexNo(), info);
				}
			}
			
			Set<String> voIdxNoAndSrcInFileRelMapKeys = voIdxNoAndSrcInFileRelMap
					.keySet();
			for (String key : voIdxNoAndSrcInFileRelMapKeys) {
				Set<String> values = voIdxNoAndSrcInFileRelMap.get(key);
				for (String val : values) {
					if (allSrcInfoFromDBMap.containsKey(val)) {// 这儿得考虑指标编号的唯一性，所以不存在哪个指标编号对应的指标是属于哪一个指标的来源指标
						voIdxNoAndSrcInDBRelMap.get(key).add(val);
					}
					if(allSrcMeaInfoFromDBMap.containsKey(val)){
						voIdxNoAndSrcInDBRelMap.get(key).add(val);
					}
				}
			}
			
			
			// del
			List<RptIdxInfo> idxDelList = Lists.newArrayList();
			List<RptIdxBusiExt> idxBusiDelList = Lists.newArrayList();
			// saveList
			List<RptIdxCatalog> catalogSaveList = Lists.newArrayList();
			Map<String, RptIdxCatalog> catalogSaveMap = Maps.newHashMap();
			List<RptIdxInfo> idxSaveList = Lists.newArrayList();
			List<RptIdxSrcRelInfo> srcRelSaveList = Lists.newArrayList();
			List<RptIdxBusiExt> idxBusiSaveList = Lists.newArrayList();
			List<RptIdxMeasureRel> idxMeasureRelSaveList = Lists.newArrayList();
			List<RptIdxDimRel> idxDimRelSaveList = Lists.newArrayList();
			List<RptIdxFormulaInfo> idxFormulaSaveList = Lists.newArrayList();
			List<RptIdxFilterInfo> idxFilterSaveList = Lists.newArrayList();

			String finalIndexCatalogNo = "";
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String steadyMeasureNo = propertiesUtils
					.getProperty("steadyMeasureNo");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			String indexDimTypeNo = propertiesUtils
					.getProperty("indexDimTypeNo");
			String defaultMeasure = propertiesUtils
					.getProperty("defaultMeasure");
			String defaultDim = propertiesUtils.getProperty("defaultDim");
			String dataDate = propertiesUtils.getProperty("dataDate");
			String orgNo = propertiesUtils.getProperty("orgNo");
			String currType = propertiesUtils.getProperty("currType");
			String indexNo_ = propertiesUtils.getProperty("indexNo");
			

			String[] tableColRelArr = null;
			Map<String, String> tableColRelMap = Maps.newLinkedHashMap();
			Map<String, List<String>> idxNoAndNmRelMap = Maps.newHashMap();
			Set<String> indexNoSet = new HashSet<String>();
			for (IndexImportVO vo : vos) {// method - if - for - start
				String datasetId = lineBS.getSetId(vo.getLineId(), "rpt");
				if("".equals(datasetId) && StringUtils.isNotBlank(dsId))
					datasetId = propertiesUtils.getProperty("dsId");
				// 一级分类名称
				String firstCatalogNm = vo.getFirstCatalog();
				// 二级分类名称
				String secondCatalogNm = vo.getSecondCatalog();
				// 三级分类名称
				String thirdCatalogNm = vo.getThirdCatalog();

				// 指标编号
				String indexNo = vo.getIndexNo();
				// 指标名称
				String indexNm = vo.getIndexNm();
				// 指标别名
				String indexUsualNm = vo.getIndexUsualNm();
				// 默认指标版本
				long indexVerId = 1;
				// 主管部门
				String deptId = vo.getDeptId();
				//联系人
				String userId = vo.getUserId();
				// 数据集中文名称
				String datasetCnNm = vo.getDatasetCnNm();
				// 数据集ID(ID:字段名,多个以回车分割)
				String datasetEnNm = vo.getDatasetId();
				// 维度集合
				String dims = vo.getDims()==null?"":vo.getDims();
				// 指标公式
				String indexFormula = vo.getIndexFormula();
				// 指标类型
				String indexType = vo.getIndexType();
				// 来源指标
				String srcIndexNos = vo.getSrcIndexNo();
				// 业务分库
				String busiLibId = vo.getBusiLibId();
				//业务类型
				String busiType = vo.getBusiType();
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
					continue;
				}
				if (StringUtils.isNotEmpty(thirdCatalogNm)
						&& StringUtils.isEmpty(secondCatalogNm)) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(4);
					obj.setValidTypeNm("目录层级空值校验");
					obj.setErrorMsg("二级目录为空值时,三级目录不能有值");
					errors.add(obj);
					continue;
				}
				if (indexNoSet.contains(indexNo)) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("模板文件中指标编号重名校验");
					obj.setErrorMsg("模板文件中指标编号\"" + indexNo + "\"已经存在！");
					errors.add(obj);
					continue;
				} else {
					indexNoSet.add(indexNo);
				}
				String reg = "[\\u4e00-\\u9fa5\\da-zA-Z－_（）、\\[\\]]+";
				Matcher matcher = Pattern.compile(reg).matcher(indexNm);
				boolean isMatcher = false;
				while (matcher.find()) {
					String temp = matcher.group(0);
					if(temp.equals(indexNm)){
						isMatcher = true;
					}
					
				}
				if(!isMatcher){
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("模板文件中指标名称规则校验");
					obj.setErrorMsg("模板文件中指标名称\"" + indexNm + "\"不能存在特殊字符");
					errors.add(obj);
					continue;
				}
				if (idxNoAndNmRelMap.get(indexNm)!=null&&idxNoAndNmRelMap.get(indexNm).size() > 0) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("模板文件中指标名称重名校验");
					obj.setErrorMsg("模板文件中指标名称\"" + indexNm + "\"已经存在");
					errors.add(obj);
					continue;
				} else {
					boolean idxNmExists = false;
					List<String> idxNos1 = idxNmIndexNoListRelMap.get(indexNm);
					if (idxNos1 != null) {
						for (String no : idxNos1) {
							if (!no.equals(indexNo)) {
								idxNmExists = true;
								break;
							}
						}
					}
					
					if (idxNmExists) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(5);
						obj.setValidTypeNm("数据库指标重名校验");
						obj.setErrorMsg("数据库中不同指标编号的指标名称\"" + indexNm + "\"已经存在");
						errors.add(obj);
						continue;
					} else {
						if (idxNoAndNmRelMap.get(indexNm) == null) {
							idxNoAndNmRelMap.put(indexNm,
									new ArrayList<String>());
						}
						idxNoAndNmRelMap.get(indexNm).add(indexNo);
					}
				}

				String[] dimCnNmArr = StringUtils.split(dims, ',');//前面已经校验非空了
				Set<String>  allDimNosFromFile =  new HashSet<String>();
				for(String  dimCnNm:dimCnNmArr){
					String  dimno  =  allDimTypesIdenByDimNmFromDBMap.get(dimCnNm);
					if(StringUtils.isNotEmpty(dimno)){
						allDimNosFromFile.add(dimno);
					}
				}
				
				boolean dimFlag = false;
				for (String dimCnNm : dimCnNmArr) {
					if (!allDimTypesIdenByDimNmFromDBMap.containsKey(dimCnNm)&&!dimCnNm.equals("")) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(7);
						obj.setValidTypeNm("关联维度存在性校验");
						obj.setErrorMsg("关联维度\"" + dimCnNm + "\"不存在");
						errors.add(obj);
						dimFlag = true;
						break;
					}
				}
				if (dimFlag) {
					continue;
				}

				if(StringUtils.isNotBlank(deptId)){
					if(deptMap.get(deptId)== null){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(20);
						obj.setValidTypeNm("主管部门存在性校验");
						obj.setErrorMsg("主管部门\"" + deptId + "\"在数据库中不存在");
						errors.add(obj);
						continue;
					}
				}
				
				if(StringUtils.isNotBlank(userId)){
					if(userMap.get(userId)== null){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(27);
						obj.setValidTypeNm("联系人存在性校验");
						obj.setErrorMsg("联系人\"" + userId + "\"在数据库中不存在");
						errors.add(obj);
						continue;
					}
				}
				
				//业务分库
				if(StringUtils.isNotBlank(busiLibId)) {
					if(!busiLibMap.containsKey(busiLibId)) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(24);
						obj.setValidTypeNm("业务分库存在性校验");
						obj.setErrorMsg("业务分库\"" + busiLibId + "\"在数据库中不存在");
						errors.add(obj);
						continue;
					}
				}
				
				//业务类型校验
				if(StringUtils.isBlank(busiType)) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(19);
					obj.setValidTypeNm("业务类型非空校验");
					obj.setErrorMsg("业务类型不能为空");
					errors.add(obj);
					continue;
				}
				
				
				RptIdxInfo rptIdxInfo = allInfoFromDBMap.get(indexNo);
//				if (rptIdxInfo != null) {
//					indexVerId = rptIdxInfo.getId().getIndexVerId();
//				}
				String catalogMapKey = "0_" + firstCatalogNm;
				RptIdxCatalog firstCatalog = catalogMap.get(catalogMapKey);
				if (firstCatalog == null) {
					firstCatalog = new RptIdxCatalog();
					firstCatalog.setUpNo("0");
					firstCatalog.setIndexCatalogNm(firstCatalogNm);
					firstCatalog.setIndexCatalogNo(RandomUtils.uuid2());
					String key = firstCatalog.getUpNo() + "_"
							+ firstCatalog.getIndexCatalogNm();
					catalogMap.put(
							key, firstCatalog);
					if (!catalogSaveMap.containsKey(key)) {
						catalogSaveList.add(firstCatalog);
						catalogSaveMap.put(key, firstCatalog);
					}
				}
				finalIndexCatalogNo = firstCatalog.getIndexCatalogNo();
				RptIdxCatalog secondCatalog = null;
				if (StringUtils.isNotBlank(secondCatalogNm)) {
					catalogMapKey = firstCatalog.getIndexCatalogNo() + "_"
							+ secondCatalogNm;
					secondCatalog = catalogMap.get(catalogMapKey);
					if (secondCatalog == null) {
						secondCatalog = new RptIdxCatalog();
						secondCatalog.setUpNo(firstCatalog.getIndexCatalogNo());
						secondCatalog.setIndexCatalogNm(secondCatalogNm);
						secondCatalog.setIndexCatalogNo(RandomUtils.uuid2());
						String key = secondCatalog.getUpNo() + "_"
								+ secondCatalog.getIndexCatalogNm();
						catalogMap.put(
								key, secondCatalog);
						if (!catalogSaveMap.containsKey(key)) {
							catalogSaveList.add(secondCatalog);
							catalogSaveMap.put(key, secondCatalog);
						}
					}
					finalIndexCatalogNo = secondCatalog.getIndexCatalogNo();
				}

				RptIdxCatalog thirdCatalog = null;
				if (StringUtils.isNotBlank(thirdCatalogNm)) {
					catalogMapKey = secondCatalog.getIndexCatalogNo() + "_"
							+ thirdCatalogNm;
					thirdCatalog = catalogMap.get(catalogMapKey);
					if (thirdCatalog == null) {
						thirdCatalog = new RptIdxCatalog();
						thirdCatalog.setUpNo(secondCatalog.getIndexCatalogNo());
						thirdCatalog.setIndexCatalogNm(thirdCatalogNm);
						thirdCatalog.setIndexCatalogNo(RandomUtils.uuid2());
						String key = thirdCatalog.getUpNo() + "_"
								+ thirdCatalog.getIndexCatalogNm();
						catalogMap.put(
								key, thirdCatalog);
						if (!catalogSaveMap.containsKey(key)) {
							catalogSaveList.add(thirdCatalog);
							catalogSaveMap.put(key, thirdCatalog);
						}
					}
					finalIndexCatalogNo = thirdCatalog.getIndexCatalogNo();
				}
				String newSrcIndexNos = "";
				String newSrcMeasureNos = "";
				
				if(StringUtils.isNotBlank(srcIndexNos)){
					String srcIndexArr[] = StringUtils.split(srcIndexNos, ",");
					for(int i = 0; i < srcIndexArr.length;i++){
						if(srcIndexArr[i].indexOf(".")>=0){
							String indexNos[] = StringUtils.split(srcIndexArr[i],".");
							newSrcIndexNos+=indexNos[0];
							newSrcMeasureNos+=indexNos[1];
						}
						else{
							newSrcIndexNos+=srcIndexArr[i];
							newSrcMeasureNos+=steadyMeasureNo;
						}
						if(i!=srcIndexArr.length-1){
							newSrcIndexNos+=",";
							newSrcMeasureNos+=",";
						}
					}
				}
				RptIdxInfoPK idxInfoPK = new RptIdxInfoPK();
				idxInfoPK.setIndexNo(indexNo);
				idxInfoPK.setIndexVerId(indexVerId);
				RptIdxInfo idxInfo = new RptIdxInfo();
				idxInfo.setId(idxInfoPK);
				idxInfo.setIndexCatalogNo(finalIndexCatalogNo);
				idxInfo.setIndexNm(indexNm);
				idxInfo.setStartDate(vo.getStartDate());
				idxInfo.setCalcCycle(vo.getCalcCycle());
				idxInfo.setIndexType(indexType);
				idxInfo.setSrcIndexNo(newSrcIndexNos);
				idxInfo.setSrcIndexMeasure(newSrcMeasureNos);
				idxInfo.setIsSum(vo.getIsSum());
				idxInfo.setIndexSts(vo.getIndexSts());
				idxInfo.setDataType(vo.getDataType());
				idxInfo.setDataUnit(vo.getDataUnit());
				idxInfo.setRemark(vo.getRemark());
				idxInfo.setIsSave(vo.getIsSave());
				idxInfo.setStatType(vo.getStatType());
				idxInfo.setEndDate("29991231");
				idxInfo.setLineId(vo.getLineId());
				idxInfo.setBusiLibId(vo.getBusiLibId());//业务分库
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				String dateStr = sdf.format(new Date());
				idxInfo.setCreateDate(dateStr);
				idxInfo.setIsRptIndex(COMMON_BOOLEAN_NO);
				idxInfo.setIsCabin("0");
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				idxInfo.setLastUptUser(user.getUserId());
				idxInfo.setLastUptTime(new Timestamp(new Date().getTime()));
				idxInfo.setSrcSourceId(dsId);
				idxInfo.setDeptId(deptId);
				idxInfo.setUserId(userId);
				
				RptIdxBusiExtPK idxBusiExtPK = new RptIdxBusiExtPK();
				idxBusiExtPK.setIndexNo(indexNo);
				idxBusiExtPK.setIndexVerId(indexVerId);
				RptIdxBusiExt busiExt = new RptIdxBusiExt();
				busiExt.setId(idxBusiExtPK);
				busiExt.setIndexUsualNm(indexUsualNm);
				HashSet<String> srcIndexSets = new HashSet<String>();
				if(StringUtils.isNotBlank(newSrcIndexNos)){
					String[] srcIndexArr = StringUtils.split(srcIndexNos, ",");
					String[] srcIndexNoArr = StringUtils.split(newSrcIndexNos, ",");
					String[] srcMeasureNoArr = StringUtils.split(newSrcMeasureNos, ",");
					List<String> srcIndexNoList = new ArrayList<String>();
					for(int i = 0;i < srcIndexNoArr.length; i++){
						if(srcIndexNoList.contains(srcIndexArr[i])){
							continue;
						}
						RptIdxSrcRelInfo relinfo = new RptIdxSrcRelInfo();
						RptIdxSrcRelInfoPK id = new RptIdxSrcRelInfoPK();
						id.setIndexNo(idxInfo.getId().getIndexNo());
						id.setIndexVerId(idxInfo.getId().getIndexVerId());
						id.setSrcIndexNo(srcIndexNoArr[i]);
						id.setSrcMeasureNo(srcMeasureNoArr[i]);
						srcIndexSets.add(srcIndexArr[i]);
						srcIndexNoList.add(srcIndexArr[i]);
						relinfo.setId(id);
						srcRelSaveList.add(relinfo);
					}
				}
				/*String dataSetNm = vo.getDatasetEnNm();
				if(null != vo.getDatasetEnNm() && vo.getDatasetEnNm().indexOf(":")>=0){
					dataSetNm =StringUtils.split(vo.getDatasetEnNm(),":")[0];
				}
				idxInfo.setIdxBelongType(getSrcSys(idxInfo, srcIndexSets, setIdAndModelMap.get(dataSetNm), allSrcInfoFromDBMap));*/
				busiExt.setBusiDef(vo.getBusiDef());
				busiExt.setBusiRule(vo.getBusiRule());
				/* 复合指标（派生指标、组合指标）*/
				if (indexType.equals(DERIVE_INDEX)|| indexType.equals(COMPOSITE_INDEX)) {
					idxInfo.setSetId(datasetId);
					/*来源指标校验*/
					if (StringUtils.isNotEmpty(srcIndexNos)) {
						String[] srcIdxIds = StringUtils
								.split(srcIndexNos, ",");
						boolean flag = false;
						for (String srcIdxId : srcIdxIds) {
							if ((voIdxNoAndSrcInDBRelMap.get(indexNo) == null || !voIdxNoAndSrcInDBRelMap.get(indexNo).contains(srcIdxId))&& !voIndexNoSet.contains(srcIdxId)) {
								ValidErrorInfoObj obj = new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(13);
								obj.setValidTypeNm("来源指标存在性校验");
								obj.setErrorMsg("来源指标\"" + srcIdxId + "\"不存在");
								errors.add(obj);
								flag = true;
								break;
							}else{//来源指标业务类型是否和当前指标的业务类型一致，或者是基础类型
								String idxBusiType = this.idxInfoBS.getBusiTypeByIndexNo(srcIdxId);
								if(busiType.equals(idxBusiType) || "00".equals(idxBusiType)){
									idxInfo.setBusiType(busiType);
								}else{
									ValidErrorInfoObj obj = new ValidErrorInfoObj();
									obj.setSheetName(vo.getSheetName());
									obj.setExcelRowNo(vo.getExcelRowNo());
									obj.setExcelColNo(13);
									obj.setValidTypeNm("来源指标的业务类型校验");
									obj.setErrorMsg("来源指标\"" + srcIdxId + "\"的业务类型应与 当前指标的业务类型一致或为基础类型");
									errors.add(obj);
									flag = true;
									break;
								}
							}
						}
						if (flag) {
							continue;
						}
					} else {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(13);
						obj.setValidTypeNm("复杂(组合、派生)指标来源指标非空性校验");
						obj.setErrorMsg("复杂(组合、派生)指标来源指标不能为空");
						errors.add(obj);
						continue;
					}
                    Set<String>  allSrcDimSet =  new  HashSet<String>();
                    String[]   srcIndexNoArr  =  null;
                    if(StringUtils.isNotEmpty(srcIndexNos)){
                    	srcIndexNoArr  = StringUtils.split(srcIndexNos, ',');
                    	for(String  srcIndexNo:srcIndexNoArr){
                    		if(allSrcIdxDimRelFromDBMap.get(srcIndexNo)!=null){
                    		   allSrcDimSet.addAll(allSrcIdxDimRelFromDBMap.get(srcIndexNo));
                    		}
                    	}
                    }
                    boolean  srcDimInDBFlag  = false;
                    boolean  srcDimInFileFlag  = false;
                    for(String  dim:allDimNosFromFile){
                    	for(String  srcIndexNo:srcIndexNoArr){
                    		if(idxNoAndDimnosInFileRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosInFileRelMap.get(srcIndexNo).contains(dim)){
                    			srcDimInFileFlag  = true;
                    			break;
                    		}
                    	}
                    	if(!allSrcDimSet.contains(dim)&&!srcDimInFileFlag){
                    		//做进一步校验，根据来源指标对应的合法维度集合（来源指标通不过是不会到这一步的）
                    		boolean   exists  =  false;
                    		for(String  srcIndexNo:srcIndexNoArr){
                        		if((idxNoAndDimnosRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosRelMap.get(srcIndexNo).contains(dim))
                        				|| dim.equals(DIM_TYPE_INDEXNO_NAME)
                        				|| dim.equals(DIM_TYPE_DATE_NAME)
                        				|| dim.equals(DIM_TYPE_CURRENCY_NAME)
                        				|| dim.equals(DIM_TYPE_ORG_NAME)){
                        			exists = true;
                        			break;
                        		}
                        	}
                    		if(!exists){
	                    		RptDimTypeInfo  dimtype  =  allDimTypesIdenByDimNoFromDBMap.get(dim);
								String  dimnm  = dimtype.getDimTypeNm();
	                    		ValidErrorInfoObj obj = new ValidErrorInfoObj();
	    						obj.setSheetName(vo.getSheetName());
	    						obj.setExcelRowNo(vo.getExcelRowNo());
	    						obj.setExcelColNo(7);
	    						obj.setValidTypeNm("【关联维度】校验");
	    						obj.setErrorMsg("【关联维度】中维度\""+dimnm+"\"在来源指标对应的维度集合中不存在");
	    						errors.add(obj);
	    						srcDimInDBFlag =  true;
	    						break;
                    		}
                    	}
                    }
                    if(srcDimInDBFlag){
                    	continue;
                    }
					dimTypes.clear();
					if (dims.length() > 0) {
						String[] dimNmArr = StringUtils.split(dims, ',');
						Set<String>  dimNmSet  =  new  HashSet<String>();
						for(String  dimNm:dimNmArr){
							dimNmSet.add(dimNm);
						}
						for (String dimNms : dimNmSet) {
							dimTypes.add(allDimTypesIdenByDimNoFromDBMap.get(allDimTypesIdenByDimNmFromDBMap.get(dimNms)));
						}

					}
					RptIdxMeasureRelPK rimrpk = new RptIdxMeasureRelPK();
					rimrpk.setDsId(datasetId);
					rimrpk.setIndexNo(indexNo);
					rimrpk.setIndexVerId(indexVerId);
					rimrpk.setMeasureNo(steadyMeasureNo);
					RptIdxMeasureRel rimrel = new RptIdxMeasureRel();
					rimrel.setId(rimrpk);
					rimrel.setStoreCol(defaultMeasure);
					rimrel.setOrderNum(new BigDecimal(1));
					idxMeasureRelSaveList.add(rimrel);
					// 如果组合指标情况下，选择的来源指标是总账指标，则需要指定来源指标度量
					// idxInfo.setSrcIndexMeasure("measureNoFromSumAcc");//模板导入的方式，来源指标为总账指标没有意义，可以不不用考虑
					RptIdxFormulaInfoPK rifipk = new RptIdxFormulaInfoPK();
					rifipk.setIndexNo(indexNo);
					rifipk.setIndexVerId(indexVerId);
					RptIdxFormulaInfo rifinfo = new RptIdxFormulaInfo();
					rifinfo.setId(rifipk);
					int orderVal = 1;
					int complexIndexOrderVal = 1;// 复合指标（派生、组合）
					String filterMode = RPT_IDX_FILTER_MODE_IN;// RPT_IDX_FILTER_MODE_IN-"01"表示包含，RPT_IDX_FILTER_MODE_NOT_IN-"02"表示(模板导入默认使用"01")
					Map<String,RptIdxDimRel> dimRelMap = new HashMap<String, RptIdxDimRel>();
					Map<String,RptIdxFilterInfo> dimFiterMap = new HashMap<String, RptIdxFilterInfo>();
					
					//复合指标（组合指标、派生指标）默认加入维度（指标号、日期、机构、币种）到指标维度关系表
					//dimrel_org
					RptIdxDimRelPK ridrpk_org = new RptIdxDimRelPK();
					ridrpk_org.setDimNo(orgDimTypeNo);
					ridrpk_org.setDsId(datasetId);
					ridrpk_org.setIndexNo(indexNo);
					ridrpk_org.setIndexVerId(indexVerId);
					RptIdxDimRel ridrel_org = new RptIdxDimRel();
					ridrel_org.setId(ridrpk_org);
					ridrel_org.setDimType(DIM_TYPE_ORG);
					ridrel_org.setStoreCol(orgNo);
					ridrel_org.setOrderNum(new BigDecimal(orderVal++));
					dimRelMap.put(orgDimTypeNo, ridrel_org);
					/*//filter_org
					RptIdxFilterInfoPK rifilteripk_org = new RptIdxFilterInfoPK();
					rifilteripk_org.setDimNo(orgDimTypeNo);
					rifilteripk_org.setIndexNo(indexNo);
					rifilteripk_org.setIndexVerId(indexVerId);
                    RptIdxFilterInfo rifilterinfo_org = new RptIdxFilterInfo();
                    rifilterinfo_org.setId(rifilteripk_org);
                    rifilterinfo_org.setFilterMode(filterMode);
					idxFilterSaveList.add(rifilterinfo_org);*/
					
					//dimrel_date
					RptIdxDimRelPK ridrpk_date = new RptIdxDimRelPK();
					ridrpk_date.setDimNo(dateDimTypeNo);
					ridrpk_date.setDsId(datasetId);
					ridrpk_date.setIndexNo(indexNo);
					ridrpk_date.setIndexVerId(indexVerId);
					RptIdxDimRel ridrel_date = new RptIdxDimRel();
					ridrel_date.setId(ridrpk_date);
					ridrel_date.setDimType(DIM_TYPE_DATE);
					ridrel_date.setStoreCol(dataDate);
					ridrel_date.setOrderNum(new BigDecimal(orderVal++));
					dimRelMap.put(dateDimTypeNo, ridrel_date);
					//filter_date
					/*RptIdxFilterInfoPK rifilteripk_date = new RptIdxFilterInfoPK();
					rifilteripk_date.setDimNo(dateDimTypeNo);
					rifilteripk_date.setIndexNo(indexNo);
					rifilteripk_date.setIndexVerId(indexVerId);
                    RptIdxFilterInfo rifilterinfo_date = new RptIdxFilterInfo();
                    rifilterinfo_date.setId(rifilteripk_date);
                    rifilterinfo_date.setFilterMode(filterMode);
					idxFilterSaveList.add(rifilterinfo_date);*/
					
					//dimrel_currency
					/*RptIdxDimRelPK ridrpk_currency = new RptIdxDimRelPK();
					ridrpk_currency.setDimNo(currencyDimTypeNo);
					ridrpk_currency.setDsId(datasetId);
					ridrpk_currency.setIndexNo(indexNo);
					ridrpk_currency.setIndexVerId(indexVerId);
					RptIdxDimRel ridrel_currency = new RptIdxDimRel();
					ridrel_currency.setId(ridrpk_currency);
					ridrel_currency.setDimType(DIM_TYPE_CURRENCY);
					ridrel_currency.setStoreCol(currType);
					ridrel_currency.setOrderNum(new BigDecimal(orderVal++));
					dimRelMap.put(currencyDimTypeNo, ridrel_currency);*/
					//filter_currency
					/*RptIdxFilterInfoPK rifilteripk_currency = new RptIdxFilterInfoPK();
					rifilteripk_currency.setDimNo(currencyDimTypeNo);
					rifilteripk_currency.setIndexNo(indexNo);
					rifilteripk_currency.setIndexVerId(indexVerId);
                    RptIdxFilterInfo rifilterinfo_currency = new RptIdxFilterInfo();
                    rifilterinfo_currency.setId(rifilteripk_currency);
                    rifilterinfo_currency.setFilterMode(filterMode);
					idxFilterSaveList.add(rifilterinfo_currency);*/
					
					//dimrel_index
					RptIdxDimRelPK ridrpk_index = new RptIdxDimRelPK();
					ridrpk_index.setDimNo(indexDimTypeNo);
					ridrpk_index.setDsId(datasetId);
					ridrpk_index.setIndexNo(indexNo);
					ridrpk_index.setIndexVerId(indexVerId);
					RptIdxDimRel ridrel_index = new RptIdxDimRel();
					ridrel_index.setId(ridrpk_index);
					ridrel_index.setDimType(DIM_TYPE_INDEXNO);
					ridrel_index.setStoreCol(indexNo_);
					ridrel_index.setOrderNum(new BigDecimal(orderVal++));
					dimRelMap.put(indexDimTypeNo, ridrel_index);
					//filter_index
					/*RptIdxFilterInfoPK rifilteripk_index = new RptIdxFilterInfoPK();
					rifilteripk_index.setDimNo(indexDimTypeNo);
					rifilteripk_index.setIndexNo(indexNo);
					rifilteripk_index.setIndexVerId(indexVerId);
                    RptIdxFilterInfo rifilterinfo_index = new RptIdxFilterInfo();
                    rifilterinfo_index.setId(rifilteripk_index);
                    rifilterinfo_index.setFilterMode(filterMode);
					idxFilterSaveList.add(rifilterinfo_index);*/
					
					if (indexType.equals(COMPOSITE_INDEX)) {// 组合指标
						filterInfoRelMap.clear();
						dimTypeFilterMap.clear();
						filterModeMap.clear();
						filterInfoRelTempMap.clear();
						String[] dimTypeItemRelArr = null;
						if (StringUtils.isNotEmpty(indexFormula)) {
							dimTypeItemRelArr = StringUtils.split(indexFormula, '\n');
						}
						if (dimTypeItemRelArr != null) {
							boolean  filterDimNmExists = false;
							boolean  filterDimItemNmExists = false;
							boolean  filterDimExists = false;
							Set<String>   filterDimNoSet  =  new HashSet<String>();
							for (String dimTypeItemRel : dimTypeItemRelArr) {// 分析完公式中的维度和维度项之间的关系
								String[] objArr0_ = StringUtils.split(dimTypeItemRel, ':');
								String dimTypeNm = objArr0_[0];
								String dimItemNms = objArr0_.length > 1 ? objArr0_[1]
										: "";
								String[] objArr1_ = StringUtils.split(dimItemNms, ',');
								String dimTypeNo = allDimTypesIdenByDimNmFromDBMap
										.get(dimTypeNm);
								if(StringUtils.isEmpty(dimTypeNo)){
									ValidErrorInfoObj obj = new ValidErrorInfoObj();
		    						obj.setSheetName(vo.getSheetName());
		    						obj.setExcelRowNo(vo.getExcelRowNo());
		    						obj.setExcelColNo(14);
		    						obj.setValidTypeNm("组合指标【指标公式】校验");
		    						obj.setErrorMsg("指标公式中维度\""+dimTypeNm+"\"在数据库中不存在");
		    						errors.add(obj);
									filterDimNmExists= true;
									break;
								}
								filterDimNoSet.add(dimTypeNo);
								List<RptDimItemInfoVO> items = allDimTypeAndItemsFromDBRelMap
										.get(dimTypeNo);
								cnItemRel.clear();
								for (RptDimItemInfoVO item : items) {
									cnItemRel.put(item.getDimItemNm(),
											item.getDimItemNo());
								}
								if(StringUtils.isNotEmpty(dimItemNms)){
									filterInfoRelTempMap.put(dimTypeNo,new ArrayList<String>());
								}
								filterModeMap.put(dimTypeNo, filterMode);
								for (String itemNm : objArr1_) {
									if(StringUtils.isNotEmpty(itemNm)&&(cnItemRel.size()==0||StringUtils.isEmpty(cnItemRel.get(itemNm)))){
										ValidErrorInfoObj obj = new ValidErrorInfoObj();
			    						obj.setSheetName(vo.getSheetName());
			    						obj.setExcelRowNo(vo.getExcelRowNo());
			    						obj.setExcelColNo(14);
			    						obj.setValidTypeNm("组合指标【指标公式】校验");
			    						obj.setErrorMsg("公式项\""+dimTypeItemRel+"\"中的维度值\""+itemNm+"\"在数据库中不存在");
			    						errors.add(obj);
										filterDimItemNmExists  =  true;
										break;
									}
									if(filterInfoRelTempMap.get(dimTypeNo)!=null)  filterInfoRelTempMap.get(dimTypeNo).add(cnItemRel.get(itemNm));
								}
								if(filterDimItemNmExists){
									break;
								}
							}
							if(filterDimNmExists){
								continue;
							}
							if(filterDimItemNmExists){
								continue;
							}
							srcDimInFileFlag  =  false;
							for(String  dimno:filterDimNoSet){
								for(String  srcIndexNo:srcIndexNoArr){
		                    		if(idxNoAndDimnosInFileRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosInFileRelMap.get(srcIndexNo).contains(dimno)){
		                    			srcDimInFileFlag  = true;
		                    			break;
		                    		}
		                    	}
		                    	if(!allSrcDimSet.contains(dimno)&&!srcDimInFileFlag){
		                    		//做进一步校验，根据来源指标对应的合法维度集合（来源指标通不过是不会到这一步的）
		                    		boolean   exists  =  false;
		                    		for(String  srcIndexNo:srcIndexNoArr){
		                        		if(idxNoAndDimnosRelMap.get(srcIndexNo)!=null&&idxNoAndDimnosRelMap.get(srcIndexNo).contains(dimno)){
		                        			exists = true;
		                        			break;
		                        		}
		                        	}
		                    		if(!exists){
			                    		RptDimTypeInfo  dimtype  =  allDimTypesIdenByDimNoFromDBMap.get(dimno);
										String  dimnm  = dimtype.getDimTypeNm();
			                    		ValidErrorInfoObj obj = new ValidErrorInfoObj();
			    						obj.setSheetName(vo.getSheetName());
			    						obj.setExcelRowNo(vo.getExcelRowNo());
			    						obj.setExcelColNo(14);
			    						obj.setValidTypeNm("组合指标【指标公式】校验");
			    						obj.setErrorMsg("【指标公式】中维度\""+dimnm+"\"在来源指标对应的维度集合中不存在");
			    						errors.add(obj);
			    						filterDimExists =  true;
			                    		break;
		                    		}
		                    	}
		                    }
							if(filterDimExists){
								continue;
							}
						}
						for (RptDimTypeInfo info : dimTypes) {// 将剩余的维度加入filterInfoRelMap,其余的应该是特殊维度或者没有减维的普通维度
							if (filterInfoRelTempMap.get(info.getDimTypeNo()) != null) {
								dimTypeFilterMap.put(info.getDimTypeNo(), info);
								filterInfoRelMap.put(info.getDimTypeNo(),
										filterInfoRelTempMap.get(info
												.getDimTypeNo()));
							} else {
								filterInfoRelMap.put(info.getDimTypeNo(), null);
							}
						}
						for(String  key: filterInfoRelTempMap.keySet()){
							if(filterInfoRelMap.get(key)==null){
								filterInfoRelMap.put(key, filterInfoRelTempMap.get(key));
							}
						}
						String isSum = IS_SUM_FALSE;
						RptIdxInfo srcIdxInfo = allSrcInfoFromDBMap
								.get(srcIndexNos);
						if (srcIdxInfo != null) {
							String thisIsSum = srcIdxInfo.getIsSum();
							if (StringUtils.isNotEmpty(thisIsSum)) {
								isSum = thisIsSum;
							}
						}
						// 拼凑公式
						String formulaContent = IdxFormulaUtils
								.generateByDimFilter(filterModeMap, isSum,
										filterInfoRelTempMap, dimTypeFilterMap);

						Set<String> keys = filterInfoRelMap.keySet();
						for (String key : keys) {
							RptDimTypeInfo   rptDimTypeInfo_  =  allDimTypesIdenByDimNoFromDBMap.get(key);
							// 过滤信息
							if(rptDimTypeInfo_==null){
								continue;
							}
							
							RptIdxFilterInfoPK rifilteripk = new RptIdxFilterInfoPK();
							rifilteripk.setDimNo(key);
							rifilteripk.setIndexNo(indexNo);
							rifilteripk.setIndexVerId(indexVerId);
							List<String> itemNoList = filterInfoRelMap.get(key);
							String itemNos = "";
							if (itemNoList != null) {
								for (String itemNo : itemNoList) {
									if(StringUtils.isNotEmpty(itemNo)){
										itemNos += itemNo + ",";
									}
								}
							}
							if (itemNos.length() > 0) {
								itemNos = itemNos.substring(0,
										itemNos.length() - 1);
							}
							RptIdxFilterInfo rifilterinfo = new RptIdxFilterInfo();
							rifilterinfo.setId(rifilteripk);
							rifilterinfo.setFilterMode(filterMode);
							rifilterinfo.setFilterVal(itemNos);
							if(StringUtils.isNotBlank(itemNos))
								dimFiterMap.put(rifilteripk.getDimNo(), rifilterinfo);

							// 维度信息
							RptIdxDimRelPK ridrpk = new RptIdxDimRelPK();
							ridrpk.setDimNo(key);
							ridrpk.setDsId(datasetId);
							ridrpk.setIndexNo(indexNo);
							ridrpk.setIndexVerId(indexVerId);
							RptIdxDimRel ridrel = new RptIdxDimRel();
							ridrel.setId(ridrpk);
							if (key.equals(orgDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_ORG);
								ridrel.setStoreCol(orgNo);
							} else if (key.equals(dateDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_DATE);
								ridrel.setStoreCol(dataDate);
							} else if (key.equals(currencyDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_CURRENCY);
								ridrel.setStoreCol(currType);
							} else if (key.equals(indexDimTypeNo)) {
								ridrel.setDimType(DIM_TYPE_INDEXNO);
								ridrel.setStoreCol(indexNo_);
							} else {
								ridrel.setStoreCol(defaultDim+ (complexIndexOrderVal++));
								ridrel.setDimType(DIM_TYPE_BUSI);
							}
							ridrel.setOrderNum(new BigDecimal(orderVal++));
							dimRelMap.put(ridrel.getId().getDimNo(), ridrel);
						}
						// 公式信息
						rifinfo.setFormulaContent(formulaContent);
						rifinfo.setFormulaDesc("");
						rifinfo.setFormulaType(FORMULA_TYPE_FILTER);
						idxFormulaSaveList.add(rifinfo);
					} else {// 派生指标
						//校验公式信息
						if(StringUtils.isEmpty(indexFormula)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(14);
							obj.setValidTypeNm("派生指标指标公式非空校验");
							obj.setErrorMsg("派生指标的指标公式不能为空！");
							errors.add(obj);
							continue;
						}
						rifinfo.setFormulaContent(indexFormula);
						// rifinfo.setFormulaDesc(formulaDesc);
						rifinfo.setFormulaType(FORMULA_TYPE_CALC);
						idxFormulaSaveList.add(rifinfo);
						if (dimTypes != null) {
							// 维度信息
							for (RptDimTypeInfo info : dimTypes) {
								if(info==null){
									continue;
								}
								String dimTypeNo = info.getDimTypeNo();
								RptIdxDimRelPK ridrpk = new RptIdxDimRelPK();
								ridrpk.setDimNo(dimTypeNo);
								ridrpk.setDsId(datasetId);
								ridrpk.setIndexNo(indexNo);
								ridrpk.setIndexVerId(indexVerId);
								RptIdxDimRel ridrel = new RptIdxDimRel();
								ridrel.setId(ridrpk);
								if (dimTypeNo.equals(orgDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_ORG);
									ridrel.setStoreCol(orgNo);
								} else if (dimTypeNo.equals(dateDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_DATE);
									ridrel.setStoreCol(dataDate);
								} else if (dimTypeNo.equals(currencyDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_CURRENCY);
									ridrel.setStoreCol(currType);
								} else if (dimTypeNo.equals(indexDimTypeNo)) {
									ridrel.setDimType(DIM_TYPE_INDEXNO);
									ridrel.setStoreCol(indexNo_);
								} else {
									ridrel.setStoreCol(defaultDim+ (complexIndexOrderVal++));
									ridrel.setDimType(DIM_TYPE_BUSI);
								}
								ridrel.setOrderNum(new BigDecimal(orderVal++));
								dimRelMap.put(ridrel.getId().getDimNo(), ridrel);
							}
						}
					}
					for(String key : dimRelMap.keySet()){
						idxDimRelSaveList.add(dimRelMap.get(key));
					}

					for(String key : dimFiterMap.keySet()){
						idxFilterSaveList.add(dimFiterMap.get(key));
					}
				} else {// 基础指标（根指标、总账指标、泛化指标）
					if(StringUtils.isEmpty(datasetCnNm)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(11);
						obj.setValidTypeNm("数据集中文名基础指标非空校验");
						obj.setErrorMsg("基础指标(根指标、总账指标、泛化指标)的数据集中文名不能为空");
						errors.add(obj);
						continue;
					}
					String[]  datasetCnNmArr  = StringUtils.split(datasetCnNm, '\n');
					boolean   dsCnNmExists  = false;
					for(String dsCnNm:datasetCnNmArr){
						if(!setIdAndSetNmRelMap.containsValue(dsCnNm)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(11);
							obj.setValidTypeNm("数据集中文名基础指标存在校验");
							obj.setErrorMsg("【数据集中文名】\""+dsCnNm+"\"在数据库中不存在");
							errors.add(obj);
							dsCnNmExists  = true;
							break;
						}
					}
					if(dsCnNmExists){
						continue;
					}
					tableColRelMap.clear();
					tableColRelArr = StringUtils.split(datasetEnNm, '\n');
					for (int i = 0; i < tableColRelArr.length; i++) {
						String tableColRel = tableColRelArr[i];
						String[] objArr = StringUtils.split(tableColRel, ':');
						String setId = objArr[0];
						String columnNm ="";
						if(objArr.length == 2)
							columnNm = objArr[1];
						tableColRelMap.put(setId, columnNm);
					}

					Set<Entry<String, String>> tableColRelEntries = tableColRelMap
							.entrySet();// 是有序的
				    Map<String,String>   allUniqueDimNoAndTableEntryRelMap  =  Maps.newHashMap();
				    boolean  dsRelatedF = false;
					for (Entry<String,String> entry : tableColRelEntries) {// 对每一个数据集进行遍历
						String measureColEnNm = entry.getValue();
						String setId = entry.getKey();
						if(StringUtils.isEmpty(setId)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(12);
							obj.setValidTypeNm("数据集ID存在性校验");
							obj.setErrorMsg("【数据集ID】其中一项\""+(entry.getKey()+":"+measureColEnNm)+"\"对应的数据集不存在");
							errors.add(obj);
							dsRelatedF = true;
							break;
						}else{
							if(!StringUtils.isNotBlank(idxInfo.getSetId())){
								idxInfo.setSetId(setId);
							}
							String moduleBusiType = this.idxInfoBS.getBusiTypeBySetId(setId);
							if(busiType.equals(moduleBusiType) || "00".equals(moduleBusiType)){//数据集业务类型校验
								idxInfo.setBusiType(busiType);
							}else{
								ValidErrorInfoObj obj = new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(12);
								obj.setValidTypeNm("数据集业务类型校验");
								obj.setErrorMsg("【数据集ID】其中一项\""+(entry.getKey()+":"+measureColEnNm)+"\"的业务类型应与 当前指标的业务类型一致或为基础类型");
								errors.add(obj);
								dsRelatedF = true;
								break;
							}
						}
						List<RptSysModuleCol> dbCols = setIdAndColListRelMap.get(setId);
						boolean  measureExists =  false;
						for (RptSysModuleCol col : dbCols) {
							String  enNm  = col.getEnNm();
							if(StringUtils.isNotEmpty(enNm)&&enNm.toUpperCase().equals(measureColEnNm.toUpperCase())&&COL_TYPE_MEASURE.equals(col.getColType())){
								measureExists  =  true;
								break;
							}
						}
						if(!measureExists && !indexType.equals(SUM_ACCOUNT_INDEX)){
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(12);
							obj.setValidTypeNm("数据集英文名存在性校验");
							obj.setErrorMsg("【数据集英文名】其中一项\""+(entry.getKey()+":"+measureColEnNm)+"\"中的字段\""+measureColEnNm+"\"不存在或该字段类型不是度量");
							errors.add(obj);
							dsRelatedF=true;
							break;
						}
						int measureOrderNum = 1;
						int dimOrderNum = 1;
						List<String> meList = new ArrayList<String>();
						if (indexType.equals(SUM_ACCOUNT_INDEX)){
							if(StringUtils.isNotBlank(vo.getDatasetId())){
								String attrs[] = StringUtils.split(vo.getDatasetId(),":");
								if(attrs.length  == 2){
									meList = ArrayUtils.asList(attrs[1], ",");
								}
							}
						}
						for (RptSysModuleCol  col:dbCols) {
								String  colType  = col.getColType();
								String colEnNm  = col.getEnNm();
								if (colType.equals(COL_TYPE_MEASURE)) {// 该字段为度量字段
									RptIdxMeasureRelPK rimrpk = new RptIdxMeasureRelPK();
									rimrpk.setDsId(setId);
									rimrpk.setIndexNo(indexNo);
									rimrpk.setIndexVerId(indexVerId);
									  /* 1.根指标可以选择多个数据集，每一个数据集只能对应一个度量，且该度量和模板中给出的一致,这和前台页面做的一致
									   2.泛化指标只可以选择一个数据集，该数据集只能对应一个度量，且该度量和模板中给出的一致
									   3.总账指标只可以选择一个数据集，该数据集的所有度量都该写入指标度量关系表中*/
									if (!indexType.equals(SUM_ACCOUNT_INDEX)&& (measureOrderNum == 2||!colEnNm.toUpperCase().equals(measureColEnNm.toUpperCase()))) {
										continue;
									}
									if(indexType.equals(SUM_ACCOUNT_INDEX) && !meList.contains(col.getMeasureNo())){
										continue;
									}
									if (indexType.equals(ROOT_INDEX)||indexType.equals(GENERIC_INDEX)) {// 根指标或者泛化指标
										rimrpk.setMeasureNo(steadyMeasureNo);
									} else {
										rimrpk.setMeasureNo(col.getMeasureNo());// 总账指标
									}
									RptIdxMeasureRel rimrel = new RptIdxMeasureRel();
									rimrel.setId(rimrpk);
									if(indexType.equals(SUM_ACCOUNT_INDEX)){
										  if(measureOrderNum==1&&!colEnNm.equals(measureColEnNm)){
											  measureOrderNum  = 2;
											  rimrel.setOrderNum(new BigDecimal(measureOrderNum++));
										  }else{
											  if(colEnNm.equals(measureColEnNm)){//总账指标和模板中【数据集英文字段】对应的度量字段一致，则排序置为1
												  rimrel.setOrderNum(new BigDecimal(1));
											  }else{
												  rimrel.setOrderNum(new BigDecimal(measureOrderNum++));
											  }
										  }
									}else{
									      rimrel.setOrderNum(new BigDecimal(measureOrderNum++));
									}
									rimrel.setStoreCol(colEnNm);
									idxMeasureRelSaveList.add(rimrel);
								}else if (col.getColType().equals(COL_TYPE_DIM)) {// 该字段为维度字段
									RptIdxDimRelPK ridrpk = new RptIdxDimRelPK();
									String  dimno  =  col.getDimTypeNo();
									RptDimTypeInfo   dim   =  allDimTypesIdenByDimNoFromDBMap.get(dimno);
									allUniqueDimNoAndTableEntryRelMap.put(dimno,setIdAndSetNmRelMap.get(setId)+"##"+dim.getDimTypeNm());
									ridrpk.setDimNo(col.getDimTypeNo());
									ridrpk.setDsId(setId);
									ridrpk.setIndexNo(indexNo);
									ridrpk.setIndexVerId(indexVerId);
									RptIdxDimRel ridrel = new RptIdxDimRel();
									ridrel.setId(ridrpk);
									if (col.getDimTypeNo().equals(orgDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_ORG);
									} else if (col.getDimTypeNo().equals(dateDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_DATE);
									} else if (col.getDimTypeNo().equals(currencyDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_CURRENCY);
									} else if (col.getDimTypeNo().equals(indexDimTypeNo)) {
										ridrel.setDimType(DIM_TYPE_INDEXNO);
									} else {
										ridrel.setDimType(DIM_TYPE_BUSI);
									}
									ridrel.setOrderNum(new BigDecimal(dimOrderNum++));
									ridrel.setStoreCol(colEnNm);
									idxDimRelSaveList.add(ridrel);
								}
						}
					}
					if(dsRelatedF){
						continue;
					}
					boolean  flag2 = false;
					for(String key :allDimNosFromFile){
						if(!allUniqueDimNoAndTableEntryRelMap.containsKey(key)){
							RptDimTypeInfo  dim  =  allDimTypesIdenByDimNoFromDBMap.get(key);
							String  dimnm  = dim.getDimTypeNm();
							ValidErrorInfoObj obj = new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(7);
							obj.setValidTypeNm("【关联维度】和【数据集英文名】维度信息匹配校验");
							obj.setErrorMsg("【关联维度】中的维度\""+dimnm+"\"在【数据集英文名】对应的数据集中不存在");
							errors.add(obj);
							flag2  = true;
						    break;
						}
					}
					if(flag2){
						continue;
					}
				}

				if (rptIdxInfo != null) {// 同标识指标
					idxDelList.add(idxInfo);
					idxBusiDelList.add(busiExt);
				}
				idxInfo.setRemark(busiExt.getBusiDef());
				idxSaveList.add(idxInfo);
				idxBusiSaveList.add(busiExt);
			}// method - if - for - end
			List<List<?>> lists = new ArrayList<List<?>>();
			lists.add(catalogSaveList);
			lists.add(idxSaveList);
			lists.add(idxBusiSaveList);
			lists.add(idxMeasureRelSaveList);
			lists.add(idxDimRelSaveList);
			lists.add(idxFormulaSaveList);
			lists.add(idxFilterSaveList);
			lists.add(idxDelList);
			lists.add(srcRelSaveList);
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					lists);
		}// method - if - end
		return errors;
	}

	@Override
	public void saveData(HttpServletRequest request, String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		
	}
}
