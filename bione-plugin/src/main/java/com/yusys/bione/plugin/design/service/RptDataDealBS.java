package com.yusys.bione.plugin.design.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.JpaEntityUtils;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.design.entity.RptDesignBatchCfg;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignComcellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignFavInfo;
import com.yusys.bione.plugin.design.entity.RptDesignQueryDetail;
import com.yusys.bione.plugin.design.entity.RptDesignQueryDim;
import com.yusys.bione.plugin.design.entity.RptDesignSourceDs;
import com.yusys.bione.plugin.design.entity.RptDesignSourceFormula;
import com.yusys.bione.plugin.design.entity.RptDesignSourceIdx;
import com.yusys.bione.plugin.design.entity.RptDesignSourceTabdim;
import com.yusys.bione.plugin.design.entity.RptDesignSourceTabidx;
import com.yusys.bione.plugin.design.entity.RptDesignSourceText;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.repository.RptTmpDataDao;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpCatalog;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpConfig;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpFilter;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpInfo;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSearch;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSort;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSql;
import com.yusys.bione.plugin.detailtmp.entity.RptDetailTmpSum;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpAttr;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpCatalog;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpInfo;
import com.yusys.bione.plugin.paramtmp.repository.ParamtmpMybatisDao;
import com.yusys.bione.plugin.rptdim.entity.RptDimCatalog;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxBusiExt;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCfg;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFilterInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxSrcRelInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrBankExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrFrsExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrIdxFilter;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleIdxRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrOuterCfg;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrRptdimRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicRptRel;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author weijx weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptDataDealBS extends BaseBS<Object> {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RptTmpDataDao rptTmpDataDAO;

	@Autowired
	private ParamtmpMybatisDao paramDao;
	
	
	@SuppressWarnings("unchecked")
	public String saveImport(String jsonInfo,String dsId) throws ClassNotFoundException {
		JSONObject map = JSON.parseObject(jsonInfo);
		List<List<String>> fields = new ArrayList<List<String>>();
		for (String cla : map.keySet()) {
			fields = createField(cla);
			List<?> delists = createList(cla, map);
			String jsons = map.getString(cla);
			if(cla.equals(RptMgrReportInfo.class.getName()+" 1")){
				continue;
			}
			List<Object> lists = (List<Object>)JSON.parseArray(jsons, Class.forName(cla));
			if(delists==null){
				delists=lists;
			}
			if(cla.equals(RptSysModuleInfo.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptSysModuleInfo sys=(RptSysModuleInfo) info;
						sys.setSourceId(dsId);
					}
				}
			}
			if(cla.equals(RptMgrOuterCfg.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptMgrOuterCfg cfg=(RptMgrOuterCfg) info;
						cfg.setServerId(dsId);
					}
				}
			}
			if(cla.equals(RptDetailTmpInfo.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptDetailTmpInfo cfg=(RptDetailTmpInfo) info;
						cfg.setDsId(dsId);
					}
				}
			}
			if(cla.equals(RptDetailTmpSql.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptDetailTmpSql cfg=(RptDetailTmpSql) info;
						cfg.setDsId(dsId);
					}
				}
			}
			if(cla.equals(RptDesignTmpInfo.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptDesignTmpInfo tmp=(RptDesignTmpInfo) info;
						String key =tmp.getId().getTemplateId()+"-"+tmp.getId().getVerId();
						if(StringUtils.isNotBlank(tmp.getLineId())){
							key+="-"+tmp.getLineId();
						}
						EhcacheUtils.remove(key, "tmpInfo");
					}
				}
			}
			this.deleteEntityJdbcBatch(Class.forName(cla), delists,fields);
			this.saveEntityJdbcBatch(lists);
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public String saveImport(String jsonInfo,String dsId,String serverId) throws ClassNotFoundException {
		JSONObject map = JSON.parseObject(jsonInfo);
		List<List<String>> fields = new ArrayList<List<String>>();
		for (String cla : map.keySet()) {
			fields = createField(cla);
			List<?> delists = createList(cla, map);
			String jsons = map.getString(cla);
			if(cla.equals(RptMgrReportInfo.class.getName()+" 1")){
				continue;
			}
			List<Object> lists = (List<Object>)JSON.parseArray(jsons, Class.forName(cla));
			if(delists==null){
				delists=lists;
			}
			if(cla.equals(RptSysModuleInfo.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptSysModuleInfo sys=(RptSysModuleInfo) info;
						sys.setSourceId(dsId);
					}
				}
			}
			if(cla.equals(RptMgrOuterCfg.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptMgrOuterCfg cfg=(RptMgrOuterCfg) info;
						cfg.setServerId(serverId);
					}
				}
			}
			if(cla.equals(RptDesignTmpInfo.class.getName())){
				if(lists!=null&&lists.size()>0){
					for(Object info: lists){
						RptDesignTmpInfo tmp=(RptDesignTmpInfo) info;
						String key =tmp.getId().getTemplateId()+"-"+tmp.getId().getVerId();
						if(StringUtils.isNotBlank(tmp.getLineId())){
							key+="-"+tmp.getLineId();
						}
						EhcacheUtils.remove(key, "tmpInfo");
					}
				}
			}
			this.deleteEntityJdbcBatch(Class.forName(cla), delists,fields);
			this.saveEntityJdbcBatch(lists);
		}
		return "";
	}

	private List<?> createList(String cla, JSONObject map) throws ClassNotFoundException{
		if (cla.equals(RptDesignTmpInfo.class.getName())
				|| cla.equals(RptDesignCellInfo.class.getName())
				|| cla.equals(RptDesignComcellInfo.class.getName())
				|| cla.equals(RptDesignSourceIdx.class.getName())
				|| cla.equals(RptDesignSourceDs.class.getName())
				|| cla.equals(RptDesignSourceFormula.class.getName())
				|| cla.equals(RptDesignSourceText.class.getName())
				|| cla.equals(RptDesignSourceTabidx.class.getName())
				|| cla.equals(RptDesignSourceTabdim.class.getName())
				|| cla.equals(RptValidLogicRptRel.class.getName())
				|| cla.equals(RptValidCfgextWarn.class.getName())) {
			return JSON.parseArray(map.getString(RptDesignTmpInfo.class.getName()), RptDesignTmpInfo.class);
		}
		if (cla.equals(RptDetailTmpInfo.class.getName())
				|| cla.equals(RptDetailTmpConfig.class.getName())
				|| cla.equals(RptDetailTmpSearch.class.getName())
				|| cla.equals(RptDetailTmpSum.class.getName())
				|| cla.equals(RptDetailTmpSql.class.getName())
				|| cla.equals(RptDetailTmpFilter.class.getName())
				|| cla.equals(RptDetailTmpSort.class.getName())) {
			return JSON.parseArray(map.getString(RptDetailTmpInfo.class.getName()), RptDetailTmpInfo.class);
		}
		if (cla.equals(RptIdxInfo.class.getName())
				|| cla.equals(RptIdxBusiExt.class.getName())
				|| cla.equals(RptIdxMeasureRel.class.getName())
				|| cla.equals(RptIdxDimRel.class.getName())
				|| cla.equals(RptIdxFilterInfo.class.getName())
				|| cla.equals(RptIdxFormulaInfo.class.getName())) {
			return JSON.parseArray(map.getString(RptIdxInfo.class.getName()), RptIdxInfo.class);
		}
		if ( cla.equals(RptDimItemInfo.class.getName())) {
			return JSON.parseArray(map.getString(RptDimTypeInfo.class.getName()), RptDimTypeInfo.class);
		}
		if (cla.equals(RptSysModuleInfo.class.getName())
				|| cla.equals(RptSysModuleCol.class.getName())) {
			return JSON.parseArray(map.getString(RptSysModuleInfo.class.getName()), RptSysModuleInfo.class);
		}
		if (cla.equals(RptMgrRptdimRel.class.getName())
				|| cla.equals(RptMgrModuleRel.class.getName())
				|| cla.equals(RptMgrModuleIdxRel.class.getName())
				|| cla.equals(RptMgrIdxFilter.class.getName())) {
			List<RptMgrReportInfo> retList = JSON.parseArray(map.getString(RptMgrReportInfo.class.getName()+" 1"), RptMgrReportInfo.class);
			if (retList == null || retList.size() <= 0) {
				retList = JSON.parseArray(map.getString(RptMgrReportInfo.class.getName()), RptMgrReportInfo.class);
			}
			return retList;
		}
		if (cla.equals(RptParamtmpAttr.class.getName())){
			return JSON.parseArray(map.getString(RptParamtmpInfo.class.getName()), RptParamtmpInfo.class);
		}
		return null;
	}
	
	private List<List<String>> createField(String cla) {
		List<List<String>> fields = new ArrayList<List<String>>();
		if (cla.equals(RptDesignTmpInfo.class.getName())
				|| cla.equals(RptDesignCellInfo.class.getName())
				|| cla.equals(RptDesignSourceIdx.class.getName())
				|| cla.equals(RptDesignSourceTabidx.class.getName())
				|| cla.equals(RptDesignSourceTabdim.class.getName())
				|| cla.equals(RptDesignSourceDs.class.getName())
				|| cla.equals(RptDesignSourceFormula.class.getName())
				|| cla.equals(RptDesignSourceText.class.getName())
				|| cla.equals(RptDesignQueryDim.class.getName())
				|| cla.equals(RptDesignBatchCfg.class.getName())
				|| cla.equals(RptDesignQueryDetail.class.getName())
				|| cla.equals(RptDesignComcellInfo.class.getName())
				|| cla.equals(RptIdxCfg.class.getName())) {
			List<String> info=new ArrayList<String>();
			List<String> info2=new ArrayList<String>();
			info.add("id.templateId");
			info.add("id.templateId");
			fields.add(info);
			info2.add("id.verId");
			info2.add("id.verId");
			fields.add(info2);
			return fields;
		}
		if (cla.equals(RptDetailTmpInfo.class.getName())
				|| cla.equals(RptDetailTmpSql.class.getName())
				|| cla.equals(RptDetailTmpFilter.class.getName())) {
			List<String> info=new ArrayList<String>();
			info.add("templateId");
			info.add("templateId");
			fields.add(info);
			return fields;
		}
		if (cla.equals(RptDetailTmpSearch.class.getName())
				|| cla.equals(RptDetailTmpSort.class.getName())
				|| cla.equals(RptDetailTmpConfig.class.getName())
				|| cla.equals(RptDetailTmpSum.class.getName())) {
			List<String> info=new ArrayList<String>();
			info.add("id.templateId");
			info.add("templateId");
			fields.add(info);
			return fields;
		}
		if (cla.equals(RptIdxInfo.class.getName())
				|| cla.equals(RptIdxBusiExt.class.getName())
				|| cla.equals(RptIdxMeasureRel.class.getName())
				|| cla.equals(RptIdxDimRel.class.getName())
				|| cla.equals(RptIdxFilterInfo.class.getName())
				|| cla.equals(RptIdxFormulaInfo.class.getName())
				|| cla.equals(RptIdxSrcRelInfo.class.getName())) {
			List<String> info=new ArrayList<String>();
			List<String> info2=new ArrayList<String>();
			info.add("id.indexNo");
			info.add("id.indexNo");
			fields.add(info);
			info2.add("id.indexVerId");
			info2.add("id.indexVerId");
			fields.add(info2);
			return fields;
		}
		if ( cla.equals(RptDimItemInfo.class.getName())) {
			List<String> info=new ArrayList<String>();
			info.add("id.dimTypeNo");
			info.add("dimTypeNo");
			fields.add(info);
			return fields;
		}
		if (cla.equals(RptSysModuleInfo.class.getName())
				|| cla.equals(RptSysModuleCol.class.getName())) {
			List<String> info=new ArrayList<String>();
			info.add("setId");
			info.add("setId");
			fields.add(info);
			return fields;
		}
		if (cla.equals(RptMgrRptdimRel.class.getName())
				|| cla.equals(RptMgrModuleRel.class.getName())
				|| cla.equals(RptMgrModuleIdxRel.class.getName())
				|| cla.equals(RptMgrIdxFilter.class.getName())) {
			List<String> info=new ArrayList<String>();
			info.add("id.rptId");
			info.add("rptId");
			fields.add(info);
			return fields;
		}
		if (cla.equals(RptMgrRptdimRel.class.getName())
				|| cla.equals(RptMgrModuleRel.class.getName())
				|| cla.equals(RptMgrModuleIdxRel.class.getName())
				|| cla.equals(RptMgrIdxFilter.class.getName())) {
			List<String> info=new ArrayList<String>();
			info.add("id.rptId");
			info.add("rptId");
			fields.add(info);
			return fields;
		}
		if (cla.equals(RptParamtmpAttr.class.getName())){
			List<String> info=new ArrayList<String>();
			info.add("paramtmpId");
			info.add("paramtmpId");
			fields.add(info);
			return fields;
		}
		return null;
	}

	public Map<String,Object> exportModel(List<String> setIds){
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		String jql = "select module from RptSysModuleInfo module where module.setId in ?0";
		List<RptSysModuleInfo> modules = this.baseDAO.findWithIndexParam(jql, setIds);
		tmpMap.put(RptSysModuleInfo.class.getName(), JSON.toJSONString(modules));
		jql = "select col from RptSysModuleCol col where col.setId in ?0";
		List<RptSysModuleCol> cols = this.baseDAO.findWithIndexParam(jql, setIds);
		tmpMap.put(RptSysModuleCol.class.getName(), JSON.toJSONString(cols));
		if (modules != null && modules.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for(RptSysModuleInfo module :modules){
				catalogIds.add(module.getCatalogId());
			}
			jql = "select catalog from RptSysModuleCatalog catalog where catalog.catalogId in ?0";
			List<RptSysModuleCatalog> smcatalogs = this.baseDAO.findWithIndexParam(jql, catalogIds);
			getAllSysCatalogInfo(smcatalogs, smcatalogs);
			tmpMap.put(RptSysModuleCatalog.class.getName(), JSON.toJSONString(smcatalogs));
		}
		return tmpMap;
	}
	
	public Map<String,Object> exportDetail(List<String> templateIds){
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		//
		params.put("templateIds", templateIds);
		String jql = "select tmp from RptDetailTmpInfo tmp where tmp.templateId in ?0";
		List<RptDetailTmpInfo> detailList = this.baseDAO.findWithIndexParam(jql, templateIds);
		tmpMap.put(RptDetailTmpInfo.class.getName(), JSON.toJSONString(detailList));
		
		jql = "select tmp from RptDetailTmpConfig tmp where tmp.id.templateId in ?0";
		List<RptDetailTmpConfig> configList = this.baseDAO.findWithIndexParam(jql, templateIds);
		tmpMap.put(RptDetailTmpConfig.class.getName(), JSON.toJSONString(configList));
		
		if(configList != null && configList.size()>0){
			Set<String> setIds = new HashSet<String>();
			for(RptDetailTmpConfig config : configList){
				setIds.add(config.getSetId());
			}
			if(setIds != null && setIds.size() > 0){
				jql = "select module from RptSysModuleInfo module where module.setId in ?0";
				List<RptSysModuleInfo> modules = this.baseDAO.findWithIndexParam(jql, setIds);
				tmpMap.put(RptSysModuleInfo.class.getName(),JSON.toJSONString(modules));
				jql = "select col from RptSysModuleCol col where col.setId in ?0";
				List<RptSysModuleCol> cols = this.baseDAO.findWithIndexParam(jql, setIds);
				tmpMap.put(RptSysModuleCol.class.getName(), JSON.toJSONString(cols));
				if (modules != null && modules.size() > 0) {
					List<String> catalogIds = new ArrayList<String>();
					for(RptSysModuleInfo module :modules){
						catalogIds.add(module.getCatalogId());
					}
					jql = "select catalog from RptSysModuleCatalog catalog where catalog.catalogId in ?0";
					List<RptSysModuleCatalog> smcatalogs = this.baseDAO.findWithIndexParam(jql, catalogIds);
					getAllSysCatalogInfo(smcatalogs, smcatalogs);
					tmpMap.put(RptSysModuleCatalog.class.getName(), JSON.toJSONString(smcatalogs));
				}
			}
		}
		
		jql = "select tmp from RptDetailTmpFilter tmp where tmp.templateId in ?0";
		List<RptDetailTmpFilter> filterList = this.baseDAO.findWithIndexParam(jql, templateIds);
		tmpMap.put(RptDetailTmpFilter.class.getName(), JSON.toJSONString(filterList));
		
		jql = "select tmp from RptDetailTmpSearch tmp where tmp.id.templateId in ?0";
		List<RptDetailTmpSearch> searchList = this.baseDAO.findWithIndexParam(jql, templateIds);
		tmpMap.put(RptDetailTmpSearch.class.getName(), JSON.toJSONString(searchList));
		
		jql = "select tmp from RptDetailTmpSort tmp where tmp.id.templateId in ?0";
		List<RptDetailTmpSort> sortList = this.baseDAO.findWithIndexParam(jql, templateIds);
		tmpMap.put(RptDetailTmpSort.class.getName(), JSON.toJSONString(sortList));
		
		jql = "select tmp from RptDetailTmpSql tmp where tmp.templateId in ?0";
		List<RptDetailTmpSql> sqlList = this.baseDAO.findWithIndexParam(jql, templateIds);
		tmpMap.put(RptDetailTmpSql.class.getName(), JSON.toJSONString(sqlList));
		if(sqlList != null && sqlList.size()>0){
			List<String> paramsId = new ArrayList<String>();
			for(RptDetailTmpSql sql : sqlList){
				paramsId.add(sql.getParamtmpId());
			}
			Map<String, Object> paramsq = new HashMap<String, Object>();
			paramsq.put("paramtmpIds", paramsId);
			List<RptParamtmpInfo> paramList = this.paramDao
					.findParamtmpInfo(paramsq);
			tmpMap.put(RptParamtmpInfo.class.getName(), JSON.toJSONString(paramList));
			if (paramList != null && paramList.size() > 0) {
				List<RptParamtmpAttr> attrList = this.paramDao
						.findParamtmpAttr(paramsq);
				tmpMap.put(RptParamtmpAttr.class.getName(), JSON.toJSONString(attrList));
				List<RptParamtmpCatalog> tmpCatalogs = this.paramDao.findParamtmpCatalog(paramsq);
				getAllTmpCatalogInfo(tmpCatalogs, tmpCatalogs);
				tmpMap.put(RptParamtmpCatalog.class.getName(), JSON.toJSONString(tmpCatalogs));
			}
		}
		
		jql = "select tmp from RptDetailTmpSum tmp where tmp.id.templateId in ?0";
		List<RptDetailTmpSum> sumList = this.baseDAO.findWithIndexParam(jql, templateIds);
		tmpMap.put(RptDetailTmpSum.class.getName(), JSON.toJSONString(sumList));
		
		if(detailList != null && detailList.size()>0){
			List<String> catalogIds = new ArrayList<String>();
			for(RptDetailTmpInfo tmp : detailList){
				catalogIds.add(tmp.getCatalogId());
			}
			if(catalogIds != null && catalogIds.size()>0){
				List<RptDetailTmpCatalog> rptCatalogs = new ArrayList<RptDetailTmpCatalog>();
				getDetailCatalogInfo(rptCatalogs, catalogIds);
				tmpMap.put(RptDetailTmpCatalog.class.getName(), JSON.toJSONString(rptCatalogs));
			}
		}
		return tmpMap;
	}
	
	public Map<String,Object> exportParam(List<String> paramIds){
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		//
		params.put("paramtmpIds", paramIds);
		List<RptParamtmpInfo> paramList = this.paramDao
				.findParamtmpInfo(params);
		tmpMap.put(RptParamtmpInfo.class.getName(), JSON.toJSONString(paramList));
		
		//
		if (paramList != null && paramList.size() > 0) {
			List<RptParamtmpAttr> attrList = this.paramDao
					.findParamtmpAttr(params);
			tmpMap.put(RptParamtmpAttr.class.getName(), JSON.toJSONString(attrList));
			
			List<RptParamtmpCatalog> tmpCatalogs = this.paramDao.findParamtmpCatalog(params);
			getAllTmpCatalogInfo(tmpCatalogs, tmpCatalogs);
			tmpMap.put(RptParamtmpCatalog.class.getName(), JSON.toJSONString(tmpCatalogs));
		}
		return tmpMap;
	}
	/**
	 * 导出报表模板
	 * 
	 * @param rptIds
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> exportTmp(List<String> rptIds, String busiType, String verId)
			throws IOException {

		Map<String, Object> tmpMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		//
		params.put("rptIds", rptIds);
		List<RptMgrReportInfo> rptList = this.rptTmpDataDAO
				.getRptsByRptIds(params);
		tmpMap.put(RptMgrReportInfo.class.getName(), JSON.toJSONString(rptList));
		//
		List<RptMgrFrsExt> rptFrs = this.rptTmpDataDAO
				.getRptsFrsByRptIds(params);
		tmpMap.put(RptMgrFrsExt.class.getName(), JSON.toJSONString(rptFrs));
		//
		if (rptList != null && rptList.size() > 0) {
			params.clear();
			params.put("rpts", rptList);
			List<RptMgrReportCatalog> rptCatalogs = this.rptTmpDataDAO
					.getCatalogsByRptIds(params);
			getAllCatalogInfo(rptCatalogs, rptCatalogs);
			tmpMap.put(RptMgrReportCatalog.class.getName(), JSON.toJSONString(rptCatalogs));
			params.put("prpts", rptList);
			if(StringUtils.isNotBlank(verId)){
				params.put("verId", verId);
			}
			List<RptDesignTmpInfo> tmpInfos = this.rptTmpDataDAO
					.getDesignTmpByIds(params);
			tmpMap.put(RptDesignTmpInfo.class.getName(), JSON.toJSONString(tmpInfos));
			if (tmpInfos != null && tmpInfos.size() > 0) {
				params.clear();
				params.put("tmps", tmpInfos);
				if(StringUtils.isNotBlank(verId)){
					params.put("verId", verId);
				}
				List<RptIdxCfg> idxCfgs = this.rptTmpDataDAO
						.getRptIdxCfgByIds(params);
				tmpMap.put(RptIdxCfg.class.getName(), JSON.toJSONString(idxCfgs));
				List<RptDesignBatchCfg> bcfgs = this.rptTmpDataDAO
						.getDesignBatchCfgByIds(params);
				tmpMap.put(RptDesignBatchCfg.class.getName(), JSON.toJSONString(bcfgs));
				
				List<RptDesignQueryDim> qdims = this.rptTmpDataDAO
						.getDesignQueryDimByIds(params);
				tmpMap.put(RptDesignQueryDim.class.getName(), JSON.toJSONString(qdims));
				
				List<RptDesignFavInfo> favs = this.rptTmpDataDAO
						.getDesignFavInfoByIds(params);
				tmpMap.put(RptDesignFavInfo.class.getName(), JSON.toJSONString(favs));
				if(qdims != null && qdims.size()>0){
					List<String> paramsId = new ArrayList<String>();
					for(RptDesignQueryDim qdim : qdims){
						paramsId.add(qdim.getParamTemplateId());
					}
					Map<String, Object> paramsq = new HashMap<String, Object>();
					paramsq.put("paramtmpIds", paramsId);
					List<RptParamtmpInfo> paramList = this.paramDao
							.findParamtmpInfo(paramsq);
					tmpMap.put(RptParamtmpInfo.class.getName(), JSON.toJSONString(paramList));
					if (paramList != null && paramList.size() > 0) {
						List<RptParamtmpAttr> attrList = this.paramDao
								.findParamtmpAttr(paramsq);
						tmpMap.put(RptParamtmpAttr.class.getName(), JSON.toJSONString(attrList));
					}
				}
				List<RptDesignQueryDetail> qdetails = this.rptTmpDataDAO
						.getDesignQueryDetailsByIds(params);
				tmpMap.put(RptDesignQueryDetail.class.getName(), JSON.toJSONString(qdetails));
				
				
				List<RptDesignCellInfo> cells = this.rptTmpDataDAO
						.getDesignCellByIds(params);
				tmpMap.put(RptDesignCellInfo.class.getName(), JSON.toJSONString(cells));
				
				List<RptDesignComcellInfo> comcellIs = this.rptTmpDataDAO.getDesignComcellByIds(params);
				tmpMap.put(RptDesignComcellInfo.class.getName(), JSON.toJSONString(comcellIs));
				
				List<RptDesignSourceIdx> idxs = this.rptTmpDataDAO
						.getDesignIdxByIds(params);
				tmpMap.put(RptDesignSourceIdx.class.getName(), JSON.toJSONString(idxs));
				List<RptDesignSourceTabidx> idxTabs = this.rptTmpDataDAO
						.getDesignIdxTabByIds(params);
				tmpMap.put(RptDesignSourceTabidx.class.getName(), JSON.toJSONString(idxTabs));
				if ((idxs != null && idxs.size() > 0) || (idxTabs != null && idxTabs.size() > 0)) {
					List<String> indexNos = new ArrayList<String>();
					if(idxs != null && idxs.size() > 0){
						for(RptDesignSourceIdx idx : idxs){
							indexNos.add(idx.getIndexNo());
						}
					}
					if(idxTabs != null && idxTabs.size() > 0){
						for(RptDesignSourceTabidx idxTab : idxTabs){
							indexNos.add(idxTab.getIndexNo());
						}
					}
					Map<String, Object> params1 = new HashMap<String, Object>();
					List<List<String>> indexNosParam = new ArrayList<List<String>>();
					if(indexNos.size() > 1000){
						int index = 0;
						int remain = indexNos.size();
						while(remain > 1000){
							indexNosParam.add(indexNos.subList(index, index+1000));
							index += 1000;
							remain -= 1000;
						}
						if(index < indexNos.size()){
							indexNosParam.add(indexNos.subList(index, indexNos.size()));
						}
					}else{
						indexNosParam.add(indexNos);
					}
					params1.put("indexNos", indexNosParam);
					if(StringUtils.isNotBlank(verId)){
						params1.put("verId", verId);
					}
					List<RptIdxInfo> idxInfos = this.rptTmpDataDAO
							.getRptIdxByIds(params1);
					tmpMap.put(RptIdxInfo.class.getName(), JSON.toJSONString(idxInfos));
					List<RptIdxMeasureRel> idxmes = this.rptTmpDataDAO
							.getRptIdxMeasureByIds(params1);
					tmpMap.put(RptIdxMeasureRel.class.getName(), JSON.toJSONString(idxmes));
					List<RptIdxDimRel> dimRel = this.rptTmpDataDAO
							.getRptIdxDimByIds(params1);
					tmpMap.put(RptIdxDimRel.class.getName(), JSON.toJSONString(dimRel));
					List<RptIdxFormulaInfo> idxFormulas = this.rptTmpDataDAO
							.getRptIdxFormulaByIds(params1);
					tmpMap.put(RptIdxFormulaInfo.class.getName(), JSON.toJSONString(idxFormulas));
					List<RptIdxFilterInfo> idxFilters = this.rptTmpDataDAO
							.getRptIdxFilterByIds(params1);
					tmpMap.put(RptIdxFilterInfo.class.getName(), JSON.toJSONString(idxFilters));
					List<RptIdxSrcRelInfo> idxSrcs = this.rptTmpDataDAO
							.getRptIdxSrcByIds(params1);
					tmpMap.put(RptIdxSrcRelInfo.class.getName(), JSON.toJSONString(idxSrcs));
					
				}
				List<RptDesignSourceTabdim> dimTabs = this.rptTmpDataDAO
						.getDesignDimTabByIds(params);
				tmpMap.put(RptDesignSourceTabdim.class.getName(), JSON.toJSONString(dimTabs));
				List<RptDesignSourceDs> dss = this.rptTmpDataDAO
						.getDesignDsByIds(params);
				tmpMap.put(RptDesignSourceDs.class.getName(), JSON.toJSONString(dss));
				if (dss != null && dss.size() > 0) {
					Map<String, Object> params1 = new HashMap<String, Object>();
					params1.put("dss", dss);
					List<RptSysModuleInfo> modules = this.rptTmpDataDAO
							.getSysModuleByIds(params1);
					tmpMap.put(RptSysModuleInfo.class.getName(), JSON.toJSONString(modules));
					List<RptSysModuleCol> cols = this.rptTmpDataDAO
							.getSysColByIds(params1);
					tmpMap.put(RptSysModuleCol.class.getName(), JSON.toJSONString(cols));
					params1.clear();
					params1.put("modules", modules);
					if (modules != null && modules.size() > 0) {
						List<RptSysModuleCatalog> smcatalogs = this.rptTmpDataDAO
								.getSysCatalogByIds(params1);
						getAllSysCatalogInfo(smcatalogs, smcatalogs);
						tmpMap.put(RptSysModuleCatalog.class.getName(), JSON.toJSONString(smcatalogs));
					}
				}
				List<RptDesignSourceFormula> formulas = this.rptTmpDataDAO
						.getDesignFormulaByIds(params);
				tmpMap.put(RptDesignSourceFormula.class.getName(), JSON.toJSONString(formulas));
				List<RptDesignSourceText> texts = this.rptTmpDataDAO
						.getDesignTextByIds(params);
				tmpMap.put(RptDesignSourceText.class.getName(), JSON.toJSONString(texts));
			}
		}
		return tmpMap;
	}

	
	/**
	 * 导出报表模板
	 * 
	 * @param rptIds
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> exportRptTmp(List<String> rptIds)
			throws IOException {

		Map<String, Object> tmpMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		//
		params.put("rptIds", rptIds);
		List<RptMgrReportInfo> rptList = this.rptTmpDataDAO
				.getRptsByRptIds(params);
		tmpMap.put(RptMgrReportInfo.class.getName(), JSON.toJSONString(rptList));
		//
		List<RptMgrBankExt> rptBank = this.rptTmpDataDAO.getRptsBankByRptIds(params);
		tmpMap.put(RptMgrBankExt.class.getName(), JSON.toJSONString(rptBank));
		//
		if (rptList != null && rptList.size() > 0) {
			params.clear();
			params.put("rpts", rptList);
			List<RptMgrReportCatalog> rptCatalogs = this.rptTmpDataDAO
					.getCatalogsByRptIds(params);
			getAllCatalogInfo(rptCatalogs, rptCatalogs);
			tmpMap.put(RptMgrReportCatalog.class.getName(), JSON.toJSONString(rptCatalogs));
			params.clear();
			params.put("rptIds", rptIds);
			List<RptMgrOuterCfg> cfglist=this.rptTmpDataDAO.getRptsOuterByRptIds(params);
			if(cfglist != null && cfglist.size() > 0){
				params.clear();
				params.put("cfgs", cfglist);
				List<RptParamtmpInfo> paramList = this.paramDao
						.findParamtmpInfo(params);
				tmpMap.put(RptParamtmpInfo.class.getName(), JSON.toJSONString(paramList));
				//
				if (paramList != null && paramList.size() > 0) {
					List<RptParamtmpAttr> attrList = this.paramDao
							.findParamtmpAttr(params);
					tmpMap.put(RptParamtmpAttr.class.getName(), JSON.toJSONString(attrList));
					
					List<RptParamtmpCatalog> tmpCatalogs = this.paramDao.findParamtmpCatalog(params);
					getAllTmpCatalogInfo(tmpCatalogs, tmpCatalogs);
					tmpMap.put(RptParamtmpCatalog.class.getName(), JSON.toJSONString(tmpCatalogs));
				}
			}
			params.clear();
			params.put("rptIds", rptIds);
			tmpMap.put(RptMgrOuterCfg.class.getName(), JSON.toJSONString(cfglist));
			List<RptMgrModuleRel> moduleList = this.rptTmpDataDAO.getRptsModuleRelByRptIds(params);
			tmpMap.put(RptMgrModuleRel.class.getName(), JSON.toJSONString(moduleList));
			if (moduleList != null && moduleList.size() > 0) {
				Map<String, Object> params1 = new HashMap<String, Object>();
				params1.put("modules", moduleList);
				List<RptSysModuleInfo> modules = this.rptTmpDataDAO
						.getSysModuleByIds(params1);
				tmpMap.put(RptSysModuleInfo.class.getName(), JSON.toJSONString(modules));
				if(modules!=null&&modules.size()>0){
					params1.clear();
					params1.put("moduleList", modules);
					List<RptSysModuleCol> cols = this.rptTmpDataDAO
							.getSysColByIds(params1);
					tmpMap.put(RptSysModuleCol.class.getName(), JSON.toJSONString(cols));
					params1.clear();
					params1.put("modules", modules);
					List<RptSysModuleCatalog> smcatalogs = this.rptTmpDataDAO
							.getSysCatalogByIds(params1);
					getAllSysCatalogInfo(smcatalogs, smcatalogs);
					tmpMap.put(RptSysModuleCatalog.class.getName(), JSON.toJSONString(smcatalogs));
				}
				
			}
			List<RptMgrModuleIdxRel> idxList = this.rptTmpDataDAO.getRptsModuleIdxRelByRptIds(params);
			tmpMap.put(RptMgrModuleIdxRel.class.getName(), JSON.toJSONString(idxList));
			List<RptMgrIdxFilter> filterList = this.rptTmpDataDAO.getRptsIdxFilterRelByRptIds(params);
			tmpMap.put(RptMgrIdxFilter.class.getName(), JSON.toJSONString(filterList));
		}
		return tmpMap;
	}
	
	/**
	 * 导出报表数据集模板
	 * 
	 * @param rptIds
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> exportDsTmp(List<String> rptIds)
			throws IOException {

		Map<String, Object> tmpMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		//
		params.put("rptIds", rptIds);
		List<RptMgrReportInfo> rptList = this.rptTmpDataDAO
				.getRptsByRptIds(params);
		tmpMap.put(RptMgrReportInfo.class.getName()+" 1", JSON.toJSONString(rptList));
		List<RptMgrModuleRel> moduleList = this.rptTmpDataDAO.getRptsModuleRelByRptIds(params);
		tmpMap.put(RptMgrModuleRel.class.getName(), JSON.toJSONString(moduleList));
		if (moduleList != null && moduleList.size() > 0) {
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("modules", moduleList);
			List<RptSysModuleInfo> modules = this.rptTmpDataDAO
					.getSysModuleByIds(params1);
			tmpMap.put(RptSysModuleInfo.class.getName(), JSON.toJSONString(modules));
			if(modules!=null&&modules.size()>0){
				params1.clear();
				params1.put("moduleList", modules);
				List<RptSysModuleCol> cols = this.rptTmpDataDAO
						.getSysColByIds(params1);
				tmpMap.put(RptSysModuleCol.class.getName(), JSON.toJSONString(cols));
				params1.clear();
				params1.put("modules", modules);
				List<RptSysModuleCatalog> smcatalogs = this.rptTmpDataDAO
						.getSysCatalogByIds(params1);
				getAllSysCatalogInfo(smcatalogs, smcatalogs);
				tmpMap.put(RptSysModuleCatalog.class.getName(), JSON.toJSONString(smcatalogs));
			}
		}
		List<RptMgrModuleIdxRel> idxList = this.rptTmpDataDAO.getRptsModuleIdxRelByRptIds(params);
		tmpMap.put(RptMgrModuleIdxRel.class.getName(), JSON.toJSONString(idxList));
		List<RptMgrIdxFilter> filterList = this.rptTmpDataDAO.getRptsIdxFilterRelByRptIds(params);
		tmpMap.put(RptMgrIdxFilter.class.getName(), JSON.toJSONString(filterList));
		return tmpMap;
	}
	
	/**
	 * 导出指标模板
	 * 
	 * @param idxs
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> exportIdx(List<String> indexNos) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> tmpMap = new HashMap<String, String>();
		List<List<String>> indexNosParam = new ArrayList<List<String>>();
		if(indexNos.size() > 1000){
			int index = 0;
			int remain = indexNos.size();
			while(remain > 1000){
				indexNosParam.add(indexNos.subList(index, index+1000));
				index += 1000;
				remain -= 1000;
			}
			if(index < indexNos.size()){
				indexNosParam.add(indexNos.subList(index, indexNos.size()));
			}
		}else{
			indexNosParam.add(indexNos);
		}
		params.put("indexNos", indexNosParam);
		// 指标出数据
		List<RptIdxInfo> idxInfoList = this.rptTmpDataDAO
				.getRptIdxByIds(params);
		tmpMap.put(RptIdxInfo.class.getName(), JSON.toJSONString(idxInfoList));
		if (idxInfoList != null && idxInfoList.size() > 0) {
			List<RptIdxBusiExt> extList = this.rptTmpDataDAO
					.getRptIdxBusiExtByIds(params);
			tmpMap.put(RptIdxBusiExt.class.getName(), JSON.toJSONString(extList));
			List<RptIdxMeasureRel> measureRelList = this.rptTmpDataDAO
					.getRptIdxMeasureByIds(params);
			tmpMap.put(RptIdxMeasureRel.class.getName(), JSON.toJSONString(measureRelList));
			List<RptIdxFormulaInfo> measureInfoList = this.rptTmpDataDAO
					.getRptIdxFormulaByIds(params);
			tmpMap.put(RptIdxFormulaInfo.class.getName(), JSON.toJSONString(measureInfoList));
			List<RptIdxFilterInfo> filterList = this.rptTmpDataDAO
					.getRptIdxFilterByIds(params);
			tmpMap.put(RptIdxFilterInfo.class.getName(), JSON.toJSONString(filterList));
			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("idxInfoList", idxInfoList);
			List<String> catalogNos = new ArrayList<String>();
			for(RptIdxInfo idxTmp : idxInfoList){
				if(!catalogNos.contains(idxTmp.getIndexCatalogNo())){
					catalogNos.add(idxTmp.getIndexCatalogNo());
				}
			}
			if(catalogNos.size() > 0){		
				map.put("catalogNos", catalogNos);
			}
			List<RptIdxCatalog> idxCatalogList = this.rptTmpDataDAO
					.getCatalogIdx(map);
			getIdxCatalogInfo(idxCatalogList, idxCatalogList);
			tmpMap.put(RptIdxCatalog.class.getName(), JSON.toJSONString(idxCatalogList));
			List<RptIdxDimRel> dimRelList = this.rptTmpDataDAO
					.getRptIdxDimByIds(params);
			tmpMap.put(RptIdxDimRel.class.getName(), JSON.toJSONString(dimRelList));
			// 维度
			if (dimRelList != null && dimRelList.size() > 0) {
				Map<String, Object> paramss = new HashMap<String, Object>();
				paramss.put("dimRelList", dimRelList);
				List<RptDimTypeInfo> infoList = this.rptTmpDataDAO
						.getTypeInfoList(paramss);
				tmpMap.put(RptDimTypeInfo.class.getName(), JSON.toJSONString(infoList));
				if (infoList != null && infoList.size() > 0) {
					Map<String, Object> paramItem = new HashMap<String, Object>();
					paramItem.put("infoList", infoList);
					List<RptDimItemInfo> itemInfoList = this.rptTmpDataDAO
							.getItemInfo(paramItem);
					tmpMap.put(RptDimItemInfo.class.getName(), JSON.toJSONString(itemInfoList));
					List<RptDimCatalog> dimCatalogList = this.rptTmpDataDAO
							.getDimCatalog(paramItem);
					getDimCatalog(dimCatalogList, dimCatalogList);
					tmpMap.put(RptDimCatalog.class.getName(), JSON.toJSONString(dimCatalogList));
				}
			}
			// 数据集
			HashSet<String> list = new HashSet<String>();
			for (int i = 0; i < dimRelList.size(); i++) {
				list.add(dimRelList.get(i).getId().getDsId());
			}
			for (int i = 0; i < measureRelList.size(); i++) {
				list.add(measureRelList.get(i).getId().getDsId());
			}
			if (list != null && list.size() > 0) {
				Map<String, Object> paramss = new HashMap<String, Object>();
				paramss.put("list", list);
				List<RptSysModuleInfo> moduleList = this.rptTmpDataDAO
						.getSysModuleByIds(paramss);
				tmpMap.put(RptSysModuleInfo.class.getName(), JSON.toJSONString(moduleList));
				if (moduleList != null && moduleList.size() > 0) {
					paramss.clear();
					paramss.put("moduleList", moduleList);
					List<RptSysModuleCol> colList = this.rptTmpDataDAO
							.getSysColByIds(paramss);
					tmpMap.put(RptSysModuleCol.class.getName(), JSON.toJSONString(colList));
					List<RptSysModuleCatalog> modelCatalogList = this.rptTmpDataDAO
							.getSysCatalogByIds(paramss);
					getModuleCatalog(modelCatalogList, modelCatalogList);
					tmpMap.put(RptSysModuleCatalog.class.getName(), JSON.toJSONString(modelCatalogList));
				}
			}
		}
		return tmpMap;
	}
	
	
	// 维度
	public Map<String, Object> exportDim(List<String> dimTypeNos) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		params.put("dimTypeNos", dimTypeNos);
		List<RptDimTypeInfo> infoList = this.rptTmpDataDAO
				.getTypeInfoList(params);
		tmpMap.put(RptDimTypeInfo.class.getName(), JSON.toJSONString(infoList));
		if (infoList != null && infoList.size() > 0) {
			Map<String, Object> paramItem = new HashMap<String, Object>();
			paramItem.put("infoList", infoList);
			List<RptDimItemInfo> itemInfoList = this.rptTmpDataDAO
					.getItemInfo(paramItem);
			tmpMap.put(RptDimItemInfo.class.getName(), JSON.toJSONString(itemInfoList));
			List<RptDimCatalog> dimCatalogList = this.rptTmpDataDAO
					.getDimCatalog(paramItem);
			getDimCatalog(dimCatalogList, dimCatalogList);
			tmpMap.put(RptDimCatalog.class.getName(), JSON.toJSONString(dimCatalogList));
		}
		return tmpMap;
	}

	
	// 指标文件夹
	private void getIdxCatalogInfo(List<RptIdxCatalog> rptCatalogs,
			List<RptIdxCatalog> catalogs) {
		if (catalogs != null && catalogs.size() > 0) {
			List<RptIdxCatalog> newCatalogs = new ArrayList<RptIdxCatalog>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("catalogs", catalogs);
			catalogs = this.rptTmpDataDAO.getCatalogIdx(params);
			if (catalogs != null && catalogs.size() > 0) {
				for (RptIdxCatalog catalog : catalogs) {
					if (!rptCatalogs.contains(catalog)) {
						newCatalogs.add(catalog);
					}
				}
				rptCatalogs.addAll(newCatalogs);
				this.getIdxCatalogInfo(rptCatalogs, newCatalogs);
			}
		}
	}
	
	// 指标文件夹
	private void getDetailCatalogInfo(List<RptDetailTmpCatalog> rptCatalogs,
			List<String> catalogs) {
		if (catalogs != null && catalogs.size() > 0) {
			List<String> newCatalogs = new ArrayList<String>();
			List<RptDetailTmpCatalog> newCatalogLists = new ArrayList<RptDetailTmpCatalog>();
			String jql = "select catalog from RptDetailTmpCatalog catalog where catalog.catalogId in ?0";
			List<RptDetailTmpCatalog> catalogList = this.baseDAO.findWithIndexParam(jql, catalogs);
			if (catalogList != null && catalogList.size() > 0) {
				for (RptDetailTmpCatalog catalog : catalogList) {
					if (!rptCatalogs.contains(catalog)) {
						newCatalogs.add(catalog.getCatalogId());
						newCatalogLists.add(catalog);
					}
				}
				rptCatalogs.addAll(newCatalogLists);
				this.getDetailCatalogInfo(rptCatalogs, newCatalogs);
			}
		}
	}

	// 维度文件夹
	private void getModuleCatalog(List<RptSysModuleCatalog> rptCatalogs,
			List<RptSysModuleCatalog> catalogs) {
		if (catalogs != null && catalogs.size() > 0) {
			List<RptSysModuleCatalog> newCatalogs = new ArrayList<RptSysModuleCatalog>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("catalogs", catalogs);
			catalogs = this.rptTmpDataDAO.getSysCatalogByIds(params);
			if (catalogs != null && catalogs.size() > 0) {
				for (RptSysModuleCatalog catalog : catalogs) {
					if (!rptCatalogs.contains(catalog)) {
						newCatalogs.add(catalog);
					}
				}
				rptCatalogs.addAll(newCatalogs);
				this.getModuleCatalog(rptCatalogs, newCatalogs);
			}
		}
	}

	// 数据集文件夹
	private void getDimCatalog(List<RptDimCatalog> rptCatalogs,
			List<RptDimCatalog> catalogs) {
		if (catalogs != null && catalogs.size() > 0) {
			List<RptDimCatalog> newCatalogs = new ArrayList<RptDimCatalog>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("catalogs", catalogs);
			catalogs = this.rptTmpDataDAO.getDimCatalog(params);
			if (catalogs != null && catalogs.size() > 0) {
				for (RptDimCatalog catalog : catalogs) {
					if (!rptCatalogs.contains(catalog)) {
						newCatalogs.add(catalog);
					}
				}
				rptCatalogs.addAll(newCatalogs);
				this.getDimCatalog(rptCatalogs, newCatalogs);
			}
		}
	}

	public boolean validateExist(Object obj, Class<?> cla) {
		StringBuilder selectSql = new StringBuilder("");
		selectSql.append("select * from ");
		String tableNm = JpaEntityUtils.getTableName(cla);
		selectSql.append(tableNm);
		selectSql.append(" where 1=1");
		Map<String, String> fieldNms = JpaEntityUtils.getIdColumnsByEntity(cla);
		Iterator<String> it = fieldNms.keySet().iterator();
		while (it.hasNext()) {
			String fieldNm = it.next();
			selectSql.append(" and ");
			selectSql.append(fieldNms.get(fieldNm));
			selectSql.append(" = ");
			selectSql.append(" ? ");
		}
		int index = 0;
		Object[] objs = new Object[fieldNms.keySet().size()];
		for (String fieldNmTmp : fieldNms.keySet()) {
			objs[index] = this.getValueByFieldExpr(fieldNmTmp, obj);
			index++;
		}
		return this.jdbcBaseDAO.find(selectSql.toString(), objs).size() > 0 ? true
				: false;
	}

	public Object getValue(Class<?> cla, String value, String text,
			String condition, Object val) throws SecurityException,
			NoSuchFieldException {
		StringBuilder selectSql = new StringBuilder("");
		String vColumn = cla.getField(value).getAnnotation(Column.class).name();
		String tColumn = cla.getField(text).getAnnotation(Column.class).name();
		selectSql.append("select ").append(vColumn).append("from ");
		String tableNm = JpaEntityUtils.getTableName(cla);
		selectSql.append(tableNm);
		selectSql.append(" where 1=1");
		selectSql.append(" and ");
		selectSql.append(tColumn).append(" = ?");
		selectSql.append(" and ").append(condition);
		Object[] objs = new Object[1];
		objs[1] = val;
		return this.jdbcBaseDAO.find(selectSql.toString(), objs).get(0)
				.get("vColumn");
	}

	public <T> void deleteEntityJdbcBatch(Class<?> saveClass,Collection<T> saveObjs,
			List<List<String>> fields) {
		if (saveObjs != null && saveObjs.size() > 0) {
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder deleteSql = new StringBuilder("delete from ").append(
					tableNm).append(" where 1=1");
			Set<Object[]> params = new HashSet<Object[]>();
			if (fields == null || fields.size() <= 0) {
				fieldNms = JpaEntityUtils.getIdColumnsByEntity(saveClass);
				Iterator<String> it = fieldNms.keySet().iterator();
				while (it.hasNext()) {
					String fieldNm = it.next();
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(fieldNm));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fieldNms.keySet().size()];
					for (String fieldNmTmp : fieldNms.keySet()) {
						objs[index] = this.getValueByFieldExpr(fieldNmTmp,
								saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			} else {
				for (List<String> field : fields) {
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(field.get(0)));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fields.size()];
					for (List<String> field : fields) {
						objs[index] = this.getValueByFieldExpr(field.get(1), saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			this.jdbcBaseDAO.batchUpdate(deleteSql.toString(), new ArrayList<Object[]>(params), 1000);
		}
	}

	public <T> void saveEntityJdbcBatch(List<T> saveObjs) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.get(0).getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder uptSql = new StringBuilder("insert into ").append(
					tableNm).append("(");
			Iterator<String> it = fieldNms.keySet().iterator();
			StringBuilder valuesTmp = new StringBuilder("");
			boolean isFirst = true;
			while (it.hasNext()) {
				String fieldNm = it.next();
				if (!isFirst) {
					uptSql.append(" , ");
					valuesTmp.append(" , ");
				}
				uptSql.append(fieldNms.get(fieldNm));
				valuesTmp.append(" ? ");
				isFirst = false;
			}
			uptSql.append(") values (").append(valuesTmp).append(")");
			List<Object[]> params = new ArrayList<Object[]>();
			for (Object saveObjTmp : saveObjs) {
				int index = 0;
				Object[] objs = new Object[fieldNms.keySet().size()];
				for (String fieldNmTmp : fieldNms.keySet()) {
					objs[index] = this.getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				params.add(objs);
			}
			this.jdbcBaseDAO.batchUpdate(uptSql.toString(), params, 1000);
		}
	}

	private Object getValueByFieldExpr(String fieldExpr, Object obj) {
		Object val = null;
		if (!StringUtils.isEmpty(fieldExpr)) {
			String[] fieldDetails = StringUtils.split(fieldExpr, '.');
			val = obj;
			for (int i = 0; i < fieldDetails.length; i++) {
				if (fieldDetails[i] == null
						|| "".equals(fieldDetails[i].trim())) {
					continue;
				}
				try {
					val = getField(val, fieldDetails[i].trim());
				} catch (Exception e) {
					e.printStackTrace();
					val = null;
				}
			}
		}
		return val;
	}

	private Object getField(Object obj, String field) throws Exception {
		String firstLetter = field.substring(0, 1).toUpperCase();
		String getMethodName = "get" + firstLetter + field.substring(1);
		Method method = obj.getClass().getMethod(getMethodName);
		return method.invoke(obj);
	}

	private void getAllSysCatalogInfo(List<RptSysModuleCatalog> rptCatalogs,
			List<RptSysModuleCatalog> catalogs) {
		if (catalogs != null && catalogs.size() > 0) {
			List<RptSysModuleCatalog> newCatalogs = new ArrayList<RptSysModuleCatalog>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("catalogs", catalogs);
			catalogs = this.rptTmpDataDAO.getSysCatalogByIds(params);
			if (catalogs != null && catalogs.size() > 0) {
				for (RptSysModuleCatalog catalog : catalogs) {
					if (!rptCatalogs.contains(catalog)) {
						newCatalogs.add(catalog);
					}
				}
				rptCatalogs.addAll(newCatalogs);
				this.getAllSysCatalogInfo(rptCatalogs, newCatalogs);
			}
		}
	}

	private void getAllCatalogInfo(List<RptMgrReportCatalog> rptCatalogs,
			List<RptMgrReportCatalog> catalogs) {
		if (catalogs != null && catalogs.size() > 0) {
			List<RptMgrReportCatalog> newCatalogs = new ArrayList<RptMgrReportCatalog>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("catalogs", catalogs);
			catalogs = this.rptTmpDataDAO.getCatalogsByRptIds(params);
			if (catalogs != null && catalogs.size() > 0) {
				for (RptMgrReportCatalog catalog : catalogs) {
					if (!rptCatalogs.contains(catalog)) {
						newCatalogs.add(catalog);
					}
				}
				rptCatalogs.addAll(newCatalogs);
				this.getAllCatalogInfo(rptCatalogs, newCatalogs);
			}
		}
	}
	
	private void getAllTmpCatalogInfo(List<RptParamtmpCatalog> rptCatalogs,
			List<RptParamtmpCatalog> catalogs) {
		if (catalogs != null && catalogs.size() > 0) {
			List<RptParamtmpCatalog> newCatalogs = new ArrayList<RptParamtmpCatalog>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("catalogs", catalogs);
			catalogs = this.paramDao.findParamtmpCatalog(params);
			if (catalogs != null && catalogs.size() > 0) {
				for (RptParamtmpCatalog catalog : catalogs) {
					if (!rptCatalogs.contains(catalog)) {
						newCatalogs.add(catalog);
					}
				}
				rptCatalogs.addAll(newCatalogs);
				this.getAllTmpCatalogInfo(rptCatalogs, newCatalogs);
			}
		}
	}

}
