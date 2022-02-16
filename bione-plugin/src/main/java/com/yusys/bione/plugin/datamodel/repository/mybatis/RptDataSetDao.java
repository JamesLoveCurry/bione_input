package com.yusys.bione.plugin.datamodel.repository.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.vo.RptSysColVO;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;
import com.yusys.bione.plugin.wizard.web.vo.DataSetImportVO;

@MyBatisRepository
public interface RptDataSetDao {
	List<RptSysModuleCol>     getSearchStruct(Map<String,Object>   params);
	
	List<RptSysModuleCol>     getGridStruct(Map<String,Object>   params);
	
	List<RptSysModuleCatalog>     getCatalogListByUpId(String   upId);
	
	List<RptSysModuleCatalog> catalogNameCanUse(Map<String,Object>   params);
	
	List<RptSysModuleCatalog> getAllCatalogs();
	
	List<RptSysModuleInfo> datasetNameCanUse(Map<String,Object>   params);
	
	public RptSysModuleCatalog getCatalogInfo(String catalogId);
	
	public void saveCatalog(RptSysModuleCatalog catalog);
	
	public void updateCatalog(RptSysModuleCatalog catalog);
	
	List<RptSysModuleInfo>   getModuleInfoListByCatalogIds(List<String>  list);
	
	List<RptSysModuleInfo>   getModuleInfoListWithPager(List<String>  list);
	
	
	List<RptSysModuleInfo>   getModulesInfosWithCondition(Map<String,Object>   condition);
	
	List<RptSysModuleInfo>   getModulesInfosByParams();
	
	List<RptSysModuleInfo>   getModulesInfosByParams(Map<String,Object>   condition);
	
	public  void  batchDelCatalog(List<String>  list);
	
	public BioneDsInfo findDataSourceById(String  id);
	
	public RptSysModuleInfo findModuleInfoById(String  id);
	
	public List<RptSysModuleCol>   getDatacolsOfDataset(String  id);
	
	public List<BioneDsInfo> getDataSources(String  id);	
	
	public  void saveModuleInfo(RptSysModuleInfo  info);
	
	public  void updateModuleInfo(RptSysModuleInfo  info);
	
	public  void batchDelModuleColBySetId(String   id);
	
	public  void saveModuleCol(RptSysModuleCol  col);
	
	public  void updateModuleCol(RptSysModuleCol  col);
	
	public  void batchDelModuleCols(List<?>  list);
	
	public  void batchDelModuleInfos(List<?>  list);
	
	public List<RptSysModuleCol>  findModuleColsByParams(Map<String,Object> params);
	
	public List<RptSysModuleCol>  findModuleColsByColTypeAndSetId(Map<String,Object> params);
	
	public List<RptSysColVO>  findModuleColsByColTypeAndSetIds(Map<String,Object> params);
	
	
	public  List<RptIdxMeasureInfo>   getAllMeasures(Map<String,Object>  params);
	
	public  BioneDriverInfo   findDriverInfoById(String   id);
	
	public RptSysModuleInfo getInfoByName(HashMap<String, String> params);
	
	public void batchDelModuleColByColIds(Map<String,Object>  params);

	public List<String> getDataSetNamesByParams(Map<String,Object> params) ;
	
	public List<RptSysModuleInfo> getDataSetsByParams(Map<String,Object> params);
	
	public List<RptSysModuleCol> getDatacolsByParams(Map<String,Object> params);
	
	public Integer sameTableEnNameCheck(Map<String,Object>  map);
	
	//导出查询目录 孙玉明
	List<RptSysModuleCatalog> getCatalogById(Map<String, Object> params);

	//导出查询 孙玉明
	List<DataSetImportVO> getExportModelInfo(Map<String, Object> params);
	
	//删除字段信息表
	void deleteModuleCol(Map<String,Object> params);
			
	//删除模型信息表
	void deleteModuleInfo(Map<String,Object> params);
	
	//检测数据集是否被指标关联
	public   Integer  getRelatedCountByDatasetId(Map<String,Object> params);
	
	public List<RptSysModuleInfo> getModuleInfo(Map<String, Object> map);
	
}
