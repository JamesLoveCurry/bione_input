package com.yusys.bione.plugin.rptdim.repository.mybatis;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptdim.entity.RptDimCatalog;
import com.yusys.bione.plugin.rptdim.entity.RptDimHistoryInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.web.vo.RptDimItemInfoVO;

@MyBatisRepository
public interface RptDimDao {
	List<RptDimCatalog> getCatalogsByUpId(String   nodeId);
	
	public  void  rptDimCatalogDelById(String  id);
	
	public  void  rptDimItemDelByPkId(Map<String,String>  params);
	
	public  void  rptDimTypeDelByIds(List<String>  idList);
	
	public  RptDimCatalog  findDimCatalogById(String  catalogId);
	
	public  void  createRptDimCatalog(RptDimCatalog  rptDimCatalog);
	
	public  void  createRptDimTypeInfo(RptDimTypeInfo  rptDimTypeInfo);
	
	public  void  updateRptDimCatalog(RptDimCatalog  rptDimCatalog);
	
	public   void  updateRptDimTypeInfo(RptDimTypeInfo  rptDimTypeInfo);
	
	public   void  updateRptDimItemInfo(RptDimItemInfo  rptDimItemInfo);
	
	public  List<RptDimTypeInfo>   getTypeInfosByParams(Map<String,Object>  params);
	
	public  List<RptDimItemInfo>   getItemInfosByParams();
	
	public  List<RptDimItemInfo>   getItemInfosByDimType(Map<String,Object>  params);
	
	public  List<RptDimTypeInfo>   getTypeInfosByCatalogId(String  catalogId);
	
	public  RptDimTypeInfo  findDimTypeInfoById(String  dimTypeNo);
	
	public  List<RptDimItemInfo>    findDimItemListByTypeNo(Map<String,Object>  params);
	
	public  List<RptDimItemInfo>    findDimItemListByUpNo(Map<String,String>  params);
	
	public Integer  getItemsCount();
	
	public Integer  getCatalogChildrenCountByUpId(String  catalogId);
	
	public   RptDimItemInfo  findDimItemInfoByPkId(Map<String,String>  params);
	
	public   void  createRptDimItemInfo(RptDimItemInfo  rptDimItemInfo);
	
	public   void  createRptDimHistory(RptDimHistoryInfo  history);
	
	public   void  deleteRptDimHistoryById(String  hisId);
	
	public   void  updateRptDimHistory(RptDimHistoryInfo   history);
	
	public List<RptDimHistoryInfo> getAllHistoryByDimTypeNo(String  dimTypeNo);
	
	public  RptDimHistoryInfo   getMaxVerHistory();
	
	public  RptDimHistoryInfo   getRptDimHistoryByHisId(String  hisId);
	
	public List<RptDimCatalog>   getAllRptDimCatalogs();
	
	public List<RptDimTypeInfo>   getAllTypeInfoByParams(Map<String,Object>  params);
	
	public Integer testSameDimItemNm(Map<String,Object>   params);
	
	public Integer testSameDimTypeNo(Map<String,Object>   params);
	
	public Integer testSameDimTypeNm(Map<String,Object>   params);
	
	public Integer testSameDimCatalogNm(Map<String,Object>   params);
	
	public List<RptDimTypeInfo> findDimTypeInfoByIds(List<String> dimNos);
	
	public  List<RptDimTypeInfo>  getAllRptDimTypeInfos();
	
	public List<RptDimItemInfoVO> getAllRptDimItemInfos();
	
	public  String  getMaxDimNo(String  type);
	
	//检测维度是否被指标或者报表关联
	public   Integer  getRelatedCountByDimno(Map<String,Object> params);

}
