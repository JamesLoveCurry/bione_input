package com.yusys.bione.plugin.rptmgr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.label.service.LabelBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.design.service.RptTmpBS;
import com.yusys.bione.plugin.design.web.vo.ReportInfoVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleIdxRelPK;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportDataItem;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrIdxFilterVO;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrInfoVO;

@Service
@Transactional(readOnly = true)
public class RptMgrInfoBS extends BaseBS<Object> {
	@Autowired
	private RptMgrBS rptBS;
	@Autowired
	private LabelBS labelBS;
	@Autowired
	private RptMgrInfoMybatisDao rptMgrInfoDao;
	
	@Autowired
	private RptTmpBS rptTmpBS;
	/**
	 * 目录名字 唯一性验证 判断同路径下是否已存在同名目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param upId
	 *            上级目录Id
	 * @param catalogName
	 *            目录名称
	 * @return
	 */
	public boolean catalogNameValid(String catalogId, String upId,
			String catalogNm) {
		String jql = "select catalog from RptMgrReportCatalog catalog where 1=1 "
				+ "and catalog.catalogNm = :catalogNm and catalog.extType = :extType and catalog.defSrc = :defSrc";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("catalogNm", catalogNm);
		if (catalogId != null && !catalogId.equals("")) {
			jql += " and catalog.catalogId = :catalogId";
			map.put("catalogId", catalogId);
		}
		if (upId != null && !upId.equals("")) {
			jql += " and catalog.upCatalogId = :upCatalogId";
			map.put("upCatalogId", upId);
		}

		map.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		map.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		
		List<RptMgrReportCatalog> list = this.baseDAO.findWithNameParm(jql, map);
		return (notEmpty(list)) ? false : true;
	}

	/**
	 * 非空判断
	 * 
	 * @param temp
	 *            String
	 * @return
	 */
	private boolean notEmpty(String temp) {
		if (temp == null || temp.trim().length() == 0)
			return false;
		else
			return true;
	}

	/**
	 * 非空判断
	 * 
	 * @param temp
	 *            List
	 * @return
	 */
	private boolean notEmpty(List<?> temp) {
		if (temp == null || temp.size() == 0)
			return false;
		else
			return true;
	}

	@Transactional(readOnly = false)
	public void saveorupdateEntity(RptMgrReportCatalog catalog) {
		if (catalog.getCatalogId() == null || "".equals(catalog.getCatalogId())) {
			catalog.setCatalogId(RandomUtils.uuid2());
			this.rptMgrInfoDao.saveCatalog(catalog);
		} else {
			this.rptMgrInfoDao.updateCatalog(catalog);
		}
	}

	public RptMgrReportCatalog getCatalogById(String catalogId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("catalogId", catalogId);
		params.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		List<RptMgrReportCatalog> catalogs = this.rptMgrInfoDao
				.getCatalogById(params);
		if (catalogs != null && catalogs.size() > 0) {
			return catalogs.get(0);
		} else {
			return new RptMgrReportCatalog();
		}
	}

	@Transactional(readOnly = false)
	public boolean deleteCatalog(String catalogId) {
		if (!notEmpty(catalogId))
			return false;

		List<String> sonIds = new ArrayList<String>();
		sonIds.add(catalogId);
		// 获取所有子目录
		this.getSonsByType(catalogId, sonIds);

		// 删除前检查该目录下是否有报表节点
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", sonIds);
		List<RptMgrReportInfo> list = this.rptMgrInfoDao
				.getRptsByParams(params);
		if (notEmpty(list))
			return false;
		this.rptMgrInfoDao.deleteCatalog(catalogId);
		return true;
	}

	@Transactional(readOnly = false)
	public boolean deleteReport(String rptId) {
		if (!notEmpty(rptId))
			return false;
		String cfgId = this.rptMgrInfoDao.getReportInfoCfgId(rptId);
		// 删除前检查该目录下是否有报表节点
		this.rptMgrInfoDao.deleteReportInfo(rptId);
		this.rptMgrInfoDao.deleteRptBankExt(rptId);
		this.rptMgrInfoDao.deleteRptDimRel(rptId);
		this.rptMgrInfoDao.deleteRptDataItem(rptId);
		this.rptMgrInfoDao.deleteRptIdxRel(rptId);
		this.rptMgrInfoDao.deleteRptOuterCfg(cfgId);
		this.rptMgrInfoDao.deleteRptMgrModule(rptId);
		this.rptMgrInfoDao.deleteRptMgrModuleIdx(rptId);
		this.rptMgrInfoDao.deleteRptMgrIdxFilter(rptId);
		return true;
	}

	/**
	 * 递归查找目录下的所有子目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param sonIds
	 *            子目录Id集合
	 */
	private void getSonsByType(String catalogId, List<String> sonIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("upCatalogId", catalogId);
		params.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		List<RptMgrReportCatalog> children = this.rptMgrInfoDao
				.getRptCatalogs(params);
		if (notEmpty(children)) {
			for (RptMgrReportCatalog cl : children) {
				sonIds.add(cl.getCatalogId());
				this.getSonsByType(cl.getCatalogId(), sonIds);
			}
		}

	}

	public List<RptDimTypeInfo> findDimTypeInfoByReport(String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		return this.rptMgrInfoDao.findDimTypeInfoByReport(params);
	}

	public RptMgrInfoVO getReportInfo(String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		params.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		List<RptMgrInfoVO> vos = this.rptMgrInfoDao.getReportInfo(params);
		if (vos != null && vos.size() > 0) {
			return vos.get(0);
		} else {
			return new RptMgrInfoVO();
		}
	}
	
	public ReportInfoVO getReportFrsInfo(String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		ReportInfoVO vo = this.rptTmpBS.getRptBaseInfo(rptId, null, null, null);
		return vo;
 	}

	public List<RptMgrReportDataItem> getDataItemByRptId(String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		return this.rptMgrInfoDao.getDataItemByParams(params);
	}

	public List<RptMgrIdxFilterVO> getIdxFilterByParams(RptMgrModuleIdxRelPK id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", id.getRptId());
		params.put("setId", id.getSetId());
		params.put("colId", id.getColId());
		params.put("indexNo", id.getIndexNo());
		return this.rptMgrInfoDao.getIdxFilterByParams(params);
	}

	public Map<String, Object> getRptList(Pager pager, String catalogId,String rptNm,String userId,String auth) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (catalogId != null && !catalogId.equals("")) {
			List<String> catalogIds = new ArrayList<String>();
			catalogIds.add(catalogId);
			getAllCatalogId(catalogIds, catalogId);
			params.put("catalogIds", catalogIds);
		}
		if(rptNm!=null&&!rptNm.equals("")){
			params.put("rptNm", "%"+rptNm+"%");
		}
		
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && StringUtils.isNotBlank(auth)){
			List<String> authRptIds = new ArrayList<String>();
			authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
			params.put("rptIds", ReBuildParam.splitLists(authRptIds));
		}
		params.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		PageHelper.startPage(pager);
		PageMyBatis<RptMgrInfoVO> page = (PageMyBatis<RptMgrInfoVO>) this.rptMgrInfoDao
				.getReportInfoByCatalogId(params);
		Map<String, Object> shareMap = Maps.newHashMap();
		shareMap.put("Rows", page.getResult());
		shareMap.put("Total", page.getTotalCount());
		return shareMap;

	}
	
	public Map<String,Object> nullRow(){
		Map<String,Object> result =new HashMap<String, Object>();
		result.put("Rows", new ArrayList<Object>());
		result.put("Total", 0);
		return result;
		
	}
	
	public Map<String, Object> getRptLabelList(Pager pager, String catalogId,String rptNm,String userId,String labelIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (catalogId != null && !catalogId.equals("")) {
			List<String> catalogIds = new ArrayList<String>();
			catalogIds.add(catalogId);
			getAllCatalogId(catalogIds, catalogId);
			params.put("catalogIds", catalogIds);
		}
		if(rptNm!=null&&!rptNm.equals("")){
			params.put("rptNm", "%"+rptNm+"%");
		}
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> authRptIds = new ArrayList<String>();
			authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
			params.put("rptIds", ReBuildParam.splitLists(authRptIds));
			if(authRptIds.size()<=0){
				return nullRow();
			}
		}
		if (StringUtils.isNotBlank(labelIds)){
			String AllArray[] = StringUtils.split(labelIds,";");
			List<String> rptLIds = null;
			for(int i=0;i<AllArray.length;i++){
				List<String> labelIdsList = ArrayUtils.asList(AllArray[i], ",");
				if(rptLIds == null){
					rptLIds = this.labelBS.getObjIdByObjNo("rpt", labelIdsList);
				}
				else{
					List<String> colIds = this.labelBS.getObjIdByObjNo("rpt", labelIdsList);
					if(colIds.size()<=0){
						return nullRow();
					}
					rptLIds.retainAll(colIds);
				}
			}
			if(rptLIds.size()<=0){
				return nullRow();
			}
			params.put("labelIds", ReBuildParam.splitLists(rptLIds));
		}
		
		PageHelper.startPage(pager);
		PageMyBatis<RptMgrInfoVO> page = (PageMyBatis<RptMgrInfoVO>) this.rptMgrInfoDao
				.getReportInfoByCatalogId(params);
		Map<String, Object> shareMap = Maps.newHashMap();
		shareMap.put("Rows", page.getResult());
		shareMap.put("Total", page.getTotalCount());
		return shareMap;

	}

	private void getAllCatalogId(List<String> catalogId, String upCatalogId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("upCatalogId", upCatalogId);
		params.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		List<RptMgrReportCatalog> catalogs = this.rptMgrInfoDao
				.getCatalogById(params);
		if (catalogs != null && catalogs.size() > 0) {
			for (RptMgrReportCatalog catalog : catalogs) {
				catalogId.add(catalog.getCatalogId());
				getAllCatalogId(catalogId, catalog.getCatalogId());
			}
		}
	}


	
	public boolean validateRptNm(String rptNm, String rptId) {
		return this.rptBS.validateRpt(rptId,rptNm);
	}
	
	public boolean validateRptNum(String rptNum, String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptNum", rptNum);
		params.put("rptId", rptId);
		return this.rptMgrInfoDao.validateRpt(params).size()>0? false:true;
	}
	
	public String getTemplateIdByRptInfo(String rptId,String busiLineId,String dataDate){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("rptId", rptId);
		if(busiLineId!=null&&!busiLineId.equals("")&&!busiLineId.equals("*"))
			params.put("busiLineId", busiLineId);
		params.put("dataDate", dataDate);
		return this.rptMgrInfoDao.getTemplateIdByRptInfo(params);
	}

}
