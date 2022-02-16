package com.yusys.bione.plugin.paramtmp.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.mtool.entity.BioneDatasetInfo;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpAttr;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpCatalog;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpInfo;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface ParamtmpMybatisDao {
	public List<RptParamtmpInfo> findParamtmpInfo(Map<String, Object> map);
	public List<RptParamtmpAttr> findParamtmpAttr(Map<String, Object> map);
	public List<RptParamtmpCatalog> findParamtmpCatalog(Map<String, Object> map);
	public void updateParamtmpInfo(RptParamtmpInfo info);
	public void updateParamtmpAttr(RptParamtmpAttr attr);
	public void updateParamtmpCatalog(RptParamtmpCatalog catalog);
	public void saveParamtmpInfo(RptParamtmpInfo info);
	public void saveParamtmpCatalog(RptParamtmpCatalog catalog);
	public void saveParamtmpAttr(RptParamtmpAttr attr);
	public void deleteParamtmpInfo(Map<String, String> map);
	public void deleteParamtmpCatalog(Map<String, String> map);
	public void deleteParamtmpAttr(Map<String, String> map);
//	public List<BioneParamTypeInfo> findParamTypeInfoPage();
	public List<RptParamtmpCatalog> findCatalogNotId(Map<String, String> map);
	public List<RptParamtmpInfo> findParamtmpInfoNotId(Map<String, String> map);
	public List<RptParamtmpInfo> findParamtmpInfoInCatalog(Map<String, Object> map);
	public void deleteCatalogInID(List<String> list);
	public List<BioneDatasetInfo> findDatasetInfo(Map<String, String> map);
	
}
