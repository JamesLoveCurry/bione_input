/**
 * 
 */
package com.yusys.bione.plugin.design.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchFilter;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRelPK;
import com.yusys.bione.frame.auth.service.AuthObjBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.label.entity.BioneLabelObjInfo;
import com.yusys.bione.frame.label.entity.BioneLabelObjRel;
import com.yusys.bione.frame.label.entity.BioneLabelObjRelPK;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.EntityUtils;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine;
import com.yusys.bione.plugin.businessline.service.RptBusinessLineBS;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.design.entity.*;
import com.yusys.bione.plugin.design.repository.RptTmpDAO;
import com.yusys.bione.plugin.design.repository.RptTmpDataDao;
import com.yusys.bione.plugin.design.util.IdxFormulaUtils;
import com.yusys.bione.plugin.design.web.vo.*;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpAttr;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpInfo;
import com.yusys.bione.plugin.paramtmp.service.ParamTempBS;
import com.yusys.bione.plugin.paramtmp.web.vo.RptParamtmpVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptidx.repository.IdxInfoMybatisDao;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrFrsExt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.valid.entitiy.*;
import com.yusys.bione.plugin.valid.service.ValidLogicBS;
import com.yusys.bione.plugin.valid.service.ValidWarnBS;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.PERMISSION_ALL;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.RES_PERMISSION_TYPE_OPER;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.*;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述...
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录!!
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptTmpBS extends BaseBS<Object> {

	private static Logger logger = LoggerFactory.getLogger(RptTmpBS.class);

	@Autowired
	private EntityUtils entityUtils;
	
	@Autowired
	private RptBusinessLineBS lineBS;
	
	@Autowired
	public ParamTempBS paramTempBS;

	@Autowired
	public RptTmpDataDao rptTmpDataDAO;

	@Autowired
	public RptTmpDAO rptTmpDAO;

	@Autowired
	public IdxInfoMybatisDao idxInfoDAO;
	
	@Autowired
	public ValidLogicBS validLogicBS;

	@Autowired
	public ValidWarnBS validWarnBS;
	
	@Autowired
	public IdxInfoBS idxInfoBS;
	
	@Autowired
	private RptMgrInfoMybatisDao reportInfoDAO;

	@Autowired
	private AuthObjBS authObjBS;

	private static final String MAXDATE = "29991231";

//	// 节点类型
//	private String rootTreeType = "01";
//	private String folderTreeType = "02";
	

	// 数据模型树
	private String catalogType = "01";
	private String moduleType = "02";
	// private String colCatalogType = "03";
	private String colType = "04";

	// 树根节点图标
	private String treeRootIcon = "/images/classics/icons/bricks.png";
	// 树文件夹节点图标
	private String treeFolderIcon = "/images/classics/icons/f1.gif";
	// 数据模型节点图标
	private String moduleNodeIcon = "/images/classics/icons/database_table.png";
	// 数据模型字段分类图标
	// private String fieldTypeIcon = "/images/classics/icons/Catalog.gif";
	// 数据模型字段图标
	private String fieldIcon = "/images/classics/icons/column.png";

	// 报表设计器模型相关数据结构
	private static Class<?>[] designEntities = { RptDesignTmpInfo.class,
			RptDesignCellInfo.class, RptDesignBatchCfg.class,
			RptDesignSourceDs.class, RptDesignSourceTabdim.class,
			RptDesignSourceFormula.class, RptDesignSourceText.class,
			RptDesignQueryDetail.class, RptDesignComcellInfo.class};
	private static Class<?>[] designFavEntities = { RptDesignFavInfo.class };
	// 设计器指标关系数据结构
	private static Class<?>[] idxRelEntities = { RptDesignSourceTabidx.class,
			RptDesignSourceIdx.class };
	// 报表指标相关数据结构
	private static Class<?>[] idxEntities = { RptIdxInfo.class,
			RptIdxMeasureRel.class, RptIdxDimRel.class,
			RptIdxFormulaInfo.class, RptIdxFilterInfo.class };
	// 参数模板关系
	private static Class<?>[] paramRelEntities = { RptDesignQueryDim.class };
	// 参数模板
	private static Class<?>[] paramEntities = { RptParamtmpInfo.class,
			RptParamtmpAttr.class };
	// 报表校验相关配置数据结构
	private static Class<?>[] validCfgEntities = {RptValidLogicRptRel.class,
			RptValidLogicIdxRel.class, RptValidWarnLevel.class};
	// 报表校验信息数据结构
	private static Class<?>[] validEntities = { RptValidCfgextLogic.class,
			RptValidCfgextWarn.class};
	
	// 报表指标配置信息
	private static Class<?>[] rptIdxCfgEntities = { RptIdxCfg.class};
	
	/**
	 * 校验报表编号是否存在
	 * @param rptNum
	 * @param rptId
	 * @return
	 */
	public Boolean checkRptNum(String rptNum, String rptId){
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "select rpt from RptMgrReportInfo rpt where rpt.rptNum = :rptNum ";
		params.put("rptNum", rptNum);
		if(StringUtils.isNotBlank(rptId)){
			jql += " and rpt.rptId != :rptId";
			params.put("rptId", rptId);
		}
		List<RptMgrReportInfo> rpts = this.baseDAO.findWithNameParm(jql, params);
		if(rpts != null && rpts.size() > 0){
			return false;
		}
		return true;
	}

	/**
	 * 校验报表名称是否存在
	 * @param rptNm
	 * @param rptId
	 * @param defSrc
	 * @return
	 */
	public Boolean checkRptNm(String rptNm, String rptId,String defSrc){
		Map<String, Object> params = new HashMap<String, Object>();
		String jql = "select rpt from RptMgrReportInfo rpt where rpt.rptNm = :rptNm ";
		params.put("rptNm", rptNm);
		if(StringUtils.isNotBlank(defSrc)){
			jql += " and rpt.defSrc = :defSrc";
			params.put("defSrc", defSrc);
			if(GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)){
				jql += " and rpt.defUser = :defUser";
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			}
			if(StringUtils.isNotBlank(rptId)){
				jql += " and rpt.rptId != :rptId";
				params.put("rptId", rptId);
			}
		}
		List<RptMgrReportInfo> rpts = this.baseDAO.findWithNameParm(jql, params);
		if(rpts != null && rpts.size() > 0){
			return false;
		}
		return true;
	}

	/**
	 * 获取报表基本信息
	 * @param rptId
	 * @return
	 */
	public ReportInfoVO getRptBaseInfo(String rptId, String verId, Map<String, RptMgrReportCatalog> catalogMap, Map<String, String> catalogUrlMap){
		Map<String,Object> params = new HashMap<String, Object>();
		String sql = "SELECT  rpt.rpt_id,rpt.rpt_Num, rpt.rpt_nm, cat.catalog_nm,rpt.rpt_cycle,tmp.template_type,rpt.create_time,usr.user_id,usr.user_name, "
				+ "rpt.is_cabin,rpt.rpt_sts,tmp.ver_id,usr.tel,usr.email, dept.dept_id,dept.dept_name,rpt.def_Src,rpt.def_User,rpt.Rank_Order,rpt.catalog_id,"
				+ "tmp.template_Unit,tmp.is_Auto_ADj,tmp.ver_start_date,tmp.template_id,rpt.line_id,tmp.is_upt,"
				+ "ext.is_release_submit,ext.report_code,ext.submit_main,ext.submit_range,rpt.busi_type,tmp.template_Nm,tmp.ver_end_date,"
				+ "ext.tmp_version_id,rpt.rpt_busi_nm,tmp.fixed_length,tmp.is_paging,tmp.sort_sql,tmp.import_config,rpt.rpt_desc,ext.rep_id,ext.fill_desc, lib.busi_lib_nm, cfg.system_name FROM RPT_DESIGN_TMP_INFO tmp left join RPT_MGR_REPORT_INFO rpt on rpt.cfg_id = tmp.template_id LEFT JOIN RPT_MGR_REPORT_CATALOG cat "
				+ "ON cat.catalog_id = rpt.catalog_id LEFT JOIN RPT_MGR_FRS_EXT ext ON ext.rpt_Id = rpt.rpt_id LEFT JOIN BIONE_USER_INFO usr "
				+ "ON usr.user_Id = rpt.def_user LEFT JOIN BIONE_DEPT_INFO dept ON dept.dept_id = ext.def_Dept left join rpt_mgr_busi_lib_info lib on rpt.busi_lib_id = lib.busi_lib_id "
				+ "left join frs_system_cfg cfg on tmp.ver_id = cfg.system_ver_id and rpt.busi_type = cfg.busi_type WHERE rpt.rpt_id = :rptId";
		params.put("rptId", rptId);
		//没有传版本，默认取最新版本
		if(StringUtils.isBlank(verId)) {
			sql += " and tmp.ver_end_DATE = :endDate";
			params.put("endDate", "29991231");
		}else {
			sql += " and tmp.ver_id = :verId";
			params.put("verId", verId);
		}
		List<Object[]> vos = this.baseDAO.findByNativeSQLWithNameParam(sql, params);
		if(vos != null && vos.size() == 1){
			Object[] obj = vos.get(0);
			ReportInfoVO vo = new ReportInfoVO();
			vo.setRptId(obj[0]!=null?obj[0].toString():"");
			vo.setRptNum(obj[1]!=null?obj[1].toString():"");
			vo.setRptNm(obj[2]!=null?obj[2].toString():"");
			if((null != catalogMap) && (catalogMap.size() > 0)) {
				String catalogId = obj[19].toString();
				String catalogUrl =  catalogUrlMap.get(catalogId);
				if(StringUtils.isBlank(catalogUrl)) {
					catalogUrl = this.getCatalogUrl(catalogMap, catalogId, "");
					if(StringUtils.isNotBlank(catalogUrl)) {
						catalogUrlMap.put(catalogId, catalogUrl);
					}
				}
				vo.setCatalogNm(catalogUrl);
			}else {
				vo.setCatalogNm(obj[3]!=null?obj[3].toString():"");
			}
			vo.setRptCycle(obj[4]!=null?obj[4].toString():"");
			vo.setTemplateType(obj[5]!=null?obj[5].toString():"");
			vo.setCreateTime(obj[6]!=null?(Timestamp)obj[6]:null);
			vo.setUserId(obj[7]!=null?obj[7].toString():"");
			vo.setUserName(obj[8]!=null?obj[8].toString():"");
			vo.setIsCabin(obj[9]!=null?obj[9].toString():"");
			vo.setRptSts(obj[10]!=null?obj[10].toString():"");
			vo.setVerId(obj[11]!=null?new BigDecimal(obj[11].toString()):new BigDecimal(1));
			vo.setTel(obj[12]!=null?obj[12].toString():"");
			vo.setEmail(obj[13]!=null?obj[13].toString():"");
			vo.setDefDept(obj[14]!=null?obj[14].toString():"");
			vo.setDeptName(obj[15]!=null?obj[15].toString():"");
			vo.setDefSrc(obj[16]!=null?obj[16].toString():"");
			vo.setDefUser(obj[17]!=null?obj[17].toString():"");
			vo.setRankOrder(obj[18]!=null?new BigDecimal(obj[18].toString()):null);
			vo.setCatalogId(obj[19]!=null?obj[19].toString():"");
			vo.setTemplateUnit(obj[20]!=null?obj[20].toString():"");
			vo.setIsAutoAdj(obj[21]!=null?obj[21].toString():"");
			vo.setVerStartDate(obj[22]!=null?obj[22].toString():"");
			vo.setTemplateId(obj[23]!=null?obj[23].toString():"");
			vo.setLineId(obj[24]!=null?obj[24].toString():"");
			vo.setMaxVerId(obj[11]!=null?new BigDecimal(obj[11].toString()):new BigDecimal(1));
			vo.setIsUpt(obj[25]!=null?obj[25].toString():"");
			vo.setIsReleaseSubmit(obj[26]!=null?obj[26].toString():"");
			vo.setReportCode(obj[27]!=null?obj[27].toString():"");
			vo.setSubmitMain(obj[28]!=null?obj[28].toString():"");
			vo.setSubmitRange(obj[29]!=null?obj[29].toString():"");
			vo.setBusiType(obj[30]!=null?obj[30].toString():"");
			vo.setTemplateNm(obj[31]!=null?obj[31].toString():"");
			vo.setVerEndDate(obj[32]!=null?obj[32].toString():"");
			vo.setTmpVersionId(obj[33]!=null?obj[33].toString():"");
			vo.setRptBusiNm(obj[34]!=null?obj[34].toString():"");
			vo.setFixedLength(obj[35]!=null?obj[35].toString():"");
			vo.setIsPaging(obj[36]!=null?obj[36].toString():"");
			vo.setSortSql(obj[37]!=null?obj[37].toString():"");
			vo.setImportConfig(obj[38]!=null?obj[38].toString():"");
			vo.setRptDesc(obj[39]!=null?obj[39].toString():"");
			vo.setRepId(obj[40]!=null?obj[40].toString():"");
			vo.setFillDesc(obj[41]!=null?obj[41].toString():"");
			vo.setBusiLibNm(obj[42]!=null?obj[42].toString():"");
			vo.setSystemName(obj[43]!=null?obj[43].toString():"");
			if(vo.getCatalogId().equals(GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID)){
				vo.setCatalogNm("撤回目录");
			}
			return vo;
		}
		return null;
	}
	
	/**
	 * 构造报表全路径（报表excel导出用）
	 * @param catalogMap
	 * @param catalogId
	 * @param catalogUrl
	 * @return
	 */
	private String getCatalogUrl(Map<String, RptMgrReportCatalog> catalogMap, String catalogId, String catalogUrl) {
		RptMgrReportCatalog catalog = catalogMap.get(catalogId);
		if(null != catalog) {
			if(StringUtils.isNotBlank(catalog.getUpCatalogId()) && !("0".equals(catalog.getUpCatalogId()))) {
				catalogUrl = this.getCatalogUrl(catalogMap, catalog.getUpCatalogId(), catalogUrl) + "/";
			}
			catalogUrl += catalog.getCatalogNm();
			return catalogUrl;
		}
		return "";
	}
	
	
	@SuppressWarnings("unchecked")
	public List<RptIdxFilterVO> getFilterInfo(List<RptDesignIdxCellVO> idxCells,List<RptDesignTabIdxVO> colIdxCells){
		List<RptIdxFilterVO> filters = new ArrayList<RptIdxFilterVO>();
		Map<String,Map<String,String>> dimMap = new HashMap<String, Map<String,String>>();
		if(idxCells != null && idxCells.size() >0){
			for(RptDesignIdxCellVO idxCell : idxCells){
				if(idxCell.getFiltInfos()!= null && idxCell.getFiltInfos().size() > 0){
					String json = idxCell.getFiltInfos().toString();
					List<Map<String,Object>> rels = (List<Map<String,Object>>)(List<?>)JSON.parseArray(json, Map.class);
					if(rels != null && rels.size() > 0){
						for(Map<String,Object> rel : rels){
							RptIdxFilterVO filter = new RptIdxFilterVO();
							final String dimNo = rel.get("dimNo").toString();
							String dimTypeNo = rel.get("dimNo").toString();
							String filterMode = rel.get("filterMode").toString();
							String filterVal = rel.get("filterVal").toString();
							boolean orgflag = false;
							String busiType = idxCell.getBusiType();
							if(!StringUtils.isNotBlank(busiType)){
								busiType = "01";
							}
							if(dimTypeNo.equals("ORG")){//机构过滤不支持导入导出，各行的机构不同需手动配置
								dimTypeNo = dimTypeNo+busiType;
								orgflag = true;
							}
							Map<String,String> dimitemMap = new HashMap<String, String>();
							if(dimMap.get(dimTypeNo)==null){
								if(orgflag){
									List<RptOrgInfo> orgs = this.getEntityListByProperty(RptOrgInfo.class, "id.orgType", busiType);
									if(orgs != null && orgs.size() > 0){
										for(RptOrgInfo org : orgs){
											dimitemMap.put(org.getId().getOrgNo(), org.getOrgNm());
										}
									}
									dimMap.put(dimTypeNo, dimitemMap);
								}
								else{
									List<RptDimItemInfo> items = this.getEntityListByProperty(RptDimItemInfo.class, "id.dimTypeNo", dimTypeNo);
									if(items != null && items.size() > 0){
										for(RptDimItemInfo item : items){
											dimitemMap.put(item.getId().getDimItemNo(), item.getDimItemNm());
										}
									}
									dimMap.put(dimTypeNo, dimitemMap);
								}
							}
							else{
								dimitemMap = dimMap.get(dimTypeNo);
							}
							filter.setDimNo(dimNo);
							filter.setFilterMode(filterMode);
							filter.setCellNo(idxCell.getCellNo());
							List<RptDimTypeInfo> rptDimTypeInfoList = this.getDimTypesByDims(new ArrayList<String>() {
								private static final long serialVersionUID = -3048969257015167878L;
								{
									add(dimNo);
								}
							});
							String dimTypeNm = rptDimTypeInfoList.size() > 0 ? rptDimTypeInfoList.get(0).getDimTypeNm() : "";
							filter.setDimTypeNm(dimTypeNm);
							String filterVals[] = StringUtils.split(filterVal, ",");
							String filterText = "";
							for(int i = 0 ; i < filterVals.length; i++){
								String value = filterVals[i];
								String text = dimitemMap.get(value);
								if(StringUtils.isNotBlank(text)){
									filterText += text + "("+value+")";
								}
								else{
									filterText += value;
								}
								if(i < filterVals.length - 1){
									filterText += ",";
								}
							}
							filter.setFilterVal(filterText);
							filters.add(filter);
						}
					}
				}
			}
		}
		if(colIdxCells != null && colIdxCells.size() >0){
			for(RptDesignTabIdxVO colIdxCell : colIdxCells){
				if(colIdxCell.getFiltInfos()!= null && colIdxCell.getFiltInfos().size() > 0){
					String json = colIdxCell.getFiltInfos().toString();
					List<Map<String,Object>> rels = (List<Map<String,Object>>)(List<?>)JSON.parseArray(json, Map.class);
					if(rels != null && rels.size() > 0){
						for(Map<String,Object> rel : rels){
							RptIdxFilterVO filter = new RptIdxFilterVO();
							String dimTypeNo = rel.get("dimNo").toString();
							String filterMode = rel.get("filterMode").toString();
							String filterVal = rel.get("filterVal").toString();
							boolean orgflag = false;
							if(dimTypeNo.equals("ORG")){
								dimTypeNo = dimTypeNo+colIdxCell.getBusiType();
								orgflag = true;
								continue;
							}
							Map<String,String> dimitemMap = new HashMap<String, String>();
							if(dimMap.get(dimTypeNo)==null){
								if(orgflag){
									List<RptOrgInfo> orgs = this.getEntityListByProperty(RptOrgInfo.class, "id.orgType", colIdxCell.getBusiType());
									if(orgs != null && orgs.size() > 0){
										for(RptOrgInfo org : orgs){
											dimitemMap.put(org.getId().getOrgNo(), org.getOrgNm());
										}
									}
									dimMap.put(dimTypeNo, dimitemMap);
								}
								else{
									List<RptDimItemInfo> items = this.getEntityListByProperty(RptDimItemInfo.class, "id.dimTypeNo", dimTypeNo);
									if(items != null && items.size() > 0){
										for(RptDimItemInfo item : items){
											dimitemMap.put(item.getId().getDimItemNo(), item.getDimItemNm());
										}
									}
									dimMap.put(dimTypeNo, dimitemMap);
								}
								
							}
							else{
								dimitemMap = dimMap.get(dimTypeNo);
							}
							filter.setDimNo(dimTypeNo);
							filter.setFilterMode(filterMode);
							filter.setCellNo(colIdxCell.getCellNo());
							String filterVals[] = StringUtils.split(filterVal,",");
							String filterText = "";
							for(int i = 0 ; i < filterVals.length; i++){
								String value = filterVals[i];
								String text = dimitemMap.get(value);
								if(StringUtils.isNotBlank(text)){
									filterText += text + "("+value+")";
								}
								else{
									filterText += value;
								}
								if(i < filterVals.length - 1){
									filterText += ",";
								}
							}
							filter.setFilterVal(filterText);
							filters.add(filter);
						}
					}
				}
			}
		}
		return filters;
	}
	
	@SuppressWarnings("unchecked")
	public List<RptDesignDimQueryVO> getDimQuery(String paramJson){
		List<RptDesignDimQueryVO> vos = new ArrayList<RptDesignDimQueryVO>();
		List<Map<String,Object>> params = (List<Map<String,Object>>)(List<?>)JSON.parseArray(paramJson, Map.class);
		if(params != null && params.size() > 0){
			for(Map<String,Object> param : params){
				RptDesignDimQueryVO vo = new RptDesignDimQueryVO();
				vo.setDisplay(param.get("display").toString());
				vo.setKey(param.get("key").toString());
				vo.setRequied(param.get("required").toString());
				vo.setChecked(param.get("checkbox")!=null?param.get("checkbox").toString():"");
				vo.setValue(param.get("value").toString()!=null?param.get("value").toString():"");
				vos.add(vo);
			}
		}
		return vos;
	}

	/**
	 * 获取目录信息
	 * 
	 * @return
	 */
	public List<RptMgrReportCatalog> getCatalogById(String catalogId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("catalogId", catalogId);
		return rptTmpDAO.getCatalogById(params);
	}

	/**
	 * 保存报表目录
	 * 
	 * @param catalog
	 */
	@Transactional(readOnly = false)
	public void saveFrsCatalog(RptMgrReportCatalog catalog, String extType) {
		if (catalog != null) {
			// 监管报表目录
			if (StringUtils.isNotEmpty(extType)) {
				catalog.setExtType(extType);
			} else {
				PropertiesUtils pUtils = PropertiesUtils.get(
						"bione-plugin/extension/report-common.properties");
				String rptType = pUtils.getProperty("rptType");
				if (StringUtils.isNotEmpty(rptType)) {
					catalog.setExtType(rptType);
				} else {
					catalog.setExtType(GlobalConstants4plugin.RPT_EXT_TYPE_FRS);
				}
			}
			if (StringUtils.isEmpty(catalog.getCatalogId())) {
				// 新增
				catalog.setCatalogId(RandomUtils.uuid2());
				this.rptTmpDAO.saveCatalog(catalog);
			} else {
				// 修改
				this.rptTmpDAO.updateCatalog(catalog);
			}
		}
	}

	/**
	 * 迭代获取指定目录及其所有子目录
	 * 
	 * @param catalogIds
	 * @return
	 */
	public List<String> getSubCatalogIds(List<String> catalogIds) {
		List<String> ids = new ArrayList<String>();
		if (catalogIds != null && catalogIds.size() > 0) {
			ids.addAll(catalogIds);
			List<RptMgrReportCatalog> catalogs = this.rptTmpDAO
					.getSubCatalogs(catalogIds);
			if (catalogs != null) {
				List<String> newIds = new ArrayList<String>();
				for (RptMgrReportCatalog catalogTmp : catalogs) {
					if (!newIds.contains(catalogTmp.getCatalogId())) {
						newIds.add(catalogTmp.getCatalogId());
					}
				}
				if (newIds.size() > 0) {
					// 迭代获取子目录id
					ids.addAll(this.getSubCatalogIds(newIds));
				}
			}
		}
		return ids;
	}

	/**
	 * 判断目录下是否含有报表
	 * 
	 * @param catalogId
	 * @return 是/否
	 */
	public boolean catalogHasRptOrNot(List<String> catalogIds) {
		boolean hasFlag = false;
		if (catalogIds == null || catalogIds.size() <= 0) {
			return hasFlag;
		}
		List<String> totalCatalogs = this.getSubCatalogIds(catalogIds);
		// 判断是否含有报表
		List<RptMgrReportInfo> rpts = this.rptTmpDAO
				.getRptsByCatalogId(totalCatalogs);
		if (rpts != null && rpts.size() > 0) {
			hasFlag = true;
		}
		return hasFlag;
	}

	/**
	 * 删除目录
	 * 
	 * @param catalogIds
	 */
	@Transactional(readOnly = false)
	public void deleteCatalogs(List<String> catalogIds) {
		if (catalogIds == null || catalogIds.size() <= 0) {
			return;
		}
		this.rptTmpDAO.deleteCatalogs(catalogIds);
	}

	/**
	 * 获取最新版本的指定报表信息
	 * 
	 * @param rptId
	 * @return
	 */
	public ReportInfoVO getFrsRptById(String rptId, String verId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		params.put("verId", verId);
		params.put("maxDate", MAXDATE);
		ReportInfoVO rpt = this.getRptBaseInfo(rptId, null, null, null);
		if (!StringUtils.isEmpty(rpt.getRptNm())) {
			rpt.setRptNm(rpt.getRptNm().trim());
		}
		if (!StringUtils.isEmpty(rpt.getRptNum())) {
			rpt.setRptNum(rpt.getRptNum().trim());
		}
		if (!StringUtils.isEmpty(rpt.getRptDesc())) {
			rpt.setRptDesc(rpt.getRptDesc().trim());
		}
		return rpt;
	}

	
	/**
	 * 获取数据模型信息树形结构
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getModuleTree(String contextPath,String searchNm,String dsId,boolean colFlag) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		// 构造根节点
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		rootNode.setText("全部");
		Map<String, String> rootParam = new HashMap<String, String>();
		rootParam.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
		rootParam.put("type", catalogType);
		rootNode.setParams(rootParam);
		rootNode.setIcon(contextPath + treeRootIcon);
		nodes.add(rootNode);
		
		// 获取数据模型信息
		Map<String,Object> params = new HashMap<String, Object>();
		if(BioneSecurityUtils.getCurrentUserInfo() != null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> setIds = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4plugin.MODEL_RES_NO);
			if(setIds != null && setIds.size() > 0){
				params.put("setIds", setIds);
			}
			else{
				return nodes;
			}
		}
		List<String> catalogIds = new ArrayList<String>();;
		if(StringUtils.isNotBlank(searchNm)){
			params.put("setNm", "%"+searchNm+"%");
		}
		if(StringUtils.isNotBlank(dsId)){
			params.put("dsId", dsId);
		}
		params.put("setType", GlobalConstants4plugin.SET_TYPE_DETAIL);
		List<RptSysModuleInfo> modules = this.rptTmpDAO.getModuleInfoNodes(params);
		List<String> setIds = new ArrayList<String>();
		for (RptSysModuleInfo mTmp : modules) {
			// 加工数据模型节点
			if (!setIds.contains(mTmp.getSetId())) {
				setIds.add(mTmp.getSetId());
			}
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(mTmp.getSetId());
			treeNode.setText(mTmp.getSetNm());
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("realId", mTmp.getSetId());
			paramMap.put("id", mTmp.getSetId());
			paramMap.put("type", moduleType);
			paramMap.put(
					"resDefNo",
					com.yusys.bione.plugin.base.common.GlobalConstants4plugin.MODEL_RES_NO);
			treeNode.setParams(paramMap);
			treeNode.setUpId("catalog" + mTmp.getCatalogId());
			treeNode.setIcon(contextPath + moduleNodeIcon);
			catalogIds.add(mTmp.getCatalogId());
			nodes.add(treeNode);
		}
		// 获取目录信息
		List<String> caIds = new ArrayList<String>();
		getFolderInfo(contextPath,catalogIds, caIds, nodes);
		if (setIds.size() > 0 && colFlag) {
			// 获取字段信息
			List<RptSysModuleColVO> cols = this.rptTmpDAO
					.getModuleColNodes(setIds);
			for (RptSysModuleColVO cTmp : cols) {
				String upId = cTmp.getSetId();
				CommonTreeNode colNode = new CommonTreeNode();
				colNode.setId("col" + cTmp.getColId());
				colNode.setText(StringUtils.isEmpty(cTmp.getCnNm()) ? cTmp
						.getEnNm() : cTmp.getCnNm());
				Map<String, String> colParams = new HashMap<String, String>();
				colParams.put("realId", cTmp.getColId());
				colParams.put("type", colType);
				colParams.put("setId", cTmp.getSetId());
				colParams.put("dimTypeStruct", cTmp.getDimTypeStruct());
				colNode.setParams(colParams);
				colNode.setUpId(upId);
				colNode.setIcon(contextPath + fieldIcon);
				colNode.setData(cTmp);
				nodes.add(colNode);
			}
		}
		return nodes;
	}
	
	/**
	 * 获取数据模型信息树形结构
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getRowModuleTree(String contextPath,String searchNm,String dsId,boolean colFlag) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		// 构造根节点
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		rootNode.setText("全部");
		Map<String, String> rootParam = new HashMap<String, String>();
		rootParam.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
		rootParam.put("type", catalogType);
		rootNode.setParams(rootParam);
		rootNode.setIcon(contextPath + treeRootIcon);
		nodes.add(rootNode);
		
		// 获取数据模型信息
		Map<String,Object> params = new HashMap<String, Object>();
		if(BioneSecurityUtils.getCurrentUserInfo() != null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> setIds = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4plugin.MODEL_RES_NO);
			if(setIds != null && setIds.size() > 0){
				params.put("setIds", setIds);
			}
			else{
				return nodes;
			}
		}
		List<String> catalogIds = new ArrayList<String>();;
		if(StringUtils.isNotBlank(searchNm)){
			params.put("setNm", "%"+searchNm+"%");
		}
		if(StringUtils.isNotBlank(dsId)){
			params.put("dsId", dsId);
		}
		params.put("setType", GlobalConstants4plugin.SET_TYPE_DETAIL);
		List<RptSysModuleInfo> modules = this.rptTmpDAO.getModuleInfoNodes(params);
		List<String> setIds = new ArrayList<String>();
		Map<String,String> srcMap = new HashMap<String, String>();
		for (RptSysModuleInfo mTmp : modules) {
			// 加工数据模型节点
			if (!setIds.contains(mTmp.getSetId())) {
				setIds.add(mTmp.getSetId());
			}
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(mTmp.getSetId());
			treeNode.setText(mTmp.getSetNm());
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("realId", mTmp.getSetId());
			paramMap.put("id", mTmp.getSetId());
			paramMap.put("type", moduleType);
			paramMap.put(
					"resDefNo",
					com.yusys.bione.plugin.base.common.GlobalConstants4plugin.MODEL_RES_NO);
			treeNode.setParams(paramMap);
			treeNode.setUpId("catalog" + mTmp.getCatalogId());
			treeNode.setIcon(contextPath + moduleNodeIcon);
			catalogIds.add(mTmp.getCatalogId());
			nodes.add(treeNode);
		}
		// 获取目录信息
		List<String> caIds = new ArrayList<String>();
		getFolderInfo(contextPath,catalogIds, caIds, nodes);
		
		if (setIds.size() > 0 && colFlag) {
			for(String setId : setIds){
				RptSysModuleColVO vo = new RptSysModuleColVO();
				vo.setSetId(setId);
				vo.setColId("@rowNum");
				vo.setCnNm("排序");
				CommonTreeNode colNode = new CommonTreeNode();
				colNode.setId("col" + "@rowNum");
				colNode.setText("排序");
				Map<String, String> colParams = new HashMap<String, String>();
				colParams.put("realId", "@rowNum");
				colParams.put("type", colType);
				colParams.put("setId", setId);
				colParams.put("dimTypeStruct", "01");
				colParams.put("srcCode", srcMap.get(setId));
				colNode.setParams(colParams);
				colNode.setUpId(setId);
				colNode.setIcon(contextPath + fieldIcon);
				colNode.setData(vo);
				nodes.add(colNode);
			}
			// 获取字段信息
			List<RptSysModuleColVO> cols = this.rptTmpDAO
					.getModuleColNodes(setIds);
			for (RptSysModuleColVO cTmp : cols) {
				String upId = cTmp.getSetId();
				CommonTreeNode colNode = new CommonTreeNode();
				colNode.setId("col" + cTmp.getColId());
				colNode.setText(StringUtils.isEmpty(cTmp.getCnNm()) ? cTmp
						.getEnNm() : cTmp.getCnNm());
				Map<String, String> colParams = new HashMap<String, String>();
				colParams.put("realId", cTmp.getColId());
				colParams.put("type", colType);
				colParams.put("setId", cTmp.getSetId());
				colParams.put("dimTypeStruct", cTmp.getDimTypeStruct());
				colParams.put("srcCode", srcMap.get(cTmp.getSetId()));
				colParams.put("busiType", cTmp.getBusiType());
				colNode.setParams(colParams);
				colNode.setUpId(upId);
				colNode.setIcon(contextPath + fieldIcon);
				colNode.setData(cTmp);
				nodes.add(colNode);
			}
		}
		return nodes;
	}
	
	/**
	 * 
	 * @param prefix 前缀
	 * @param length 长度
	 * @return 报表编号
	 */
	public String getAutoRptNum(String prefix,int length){
		String jql = "select info from RptMgrReportInfo info where info.rptNum like ?0 order by info.rptNum desc";
		List<RptMgrReportInfo> rptinfos = this.baseDAO.findWithIndexParam(jql, prefix+"%");
		if(rptinfos !=null && rptinfos.size()>0){
			String rptNum = rptinfos.get(0).getRptNum();
			rptNum = rptNum.substring(prefix.length(), rptNum.length());
			int num = NumberUtils.toInt(rptNum)+1;
			String nums = String.valueOf(num);
			rptNum = prefix;
			for(int i = 0;i < length-nums.length();i++){
				rptNum +="0";
			}
			rptNum += nums;
			return rptNum;
		}
		else{
			String rptNum = prefix;
			for(int i = 0;i < length;i++){
				rptNum +="0";
			}
			return rptNum;
		}
	}
	
	/**
	 * 获取数据模型信息树形结构
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CommonTreeNode> getModuleTree(String contextPath,String searchNm,String dsId,boolean colFlag,List<String> ids) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		// 构造根节点
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		rootNode.setText("全部");
		Map<String, String> rootParam = new HashMap<String, String>();
		rootParam.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
		rootParam.put("type", catalogType);
		rootNode.setParams(rootParam);
		rootNode.setIcon(contextPath + treeRootIcon);
		nodes.add(rootNode);
		
		// 获取数据模型信息
		Map<String,Object> params = new HashMap<String, Object>();
		if(BioneSecurityUtils.getCurrentUserInfo() != null && !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> setIds = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4plugin.MODEL_RES_NO);
			ids = ListUtils.intersection(ids, setIds);
		}
		if(ids != null && ids.size() > 0){
		}
		else{
			return nodes;
		}
		params.put("setIds", ids);
		List<String> catalogIds = new ArrayList<String>();;
		if(StringUtils.isNotBlank(searchNm)){
			params.put("setNm", "%"+searchNm+"%");
		}
		if(StringUtils.isNotBlank(dsId)){
			params.put("dsId", dsId);
		}
		params.put("setType", GlobalConstants4plugin.SET_TYPE_DETAIL);
		List<RptSysModuleInfo> modules = this.rptTmpDAO.getModuleInfoNodes(params);
		List<String> setIds = new ArrayList<String>();
		for (RptSysModuleInfo mTmp : modules) {
			// 加工数据模型节点
			if (!setIds.contains(mTmp.getSetId())) {
				setIds.add(mTmp.getSetId());
			}
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(mTmp.getSetId());
			treeNode.setText(mTmp.getSetNm());
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("realId", mTmp.getSetId());
			paramMap.put("id", mTmp.getSetId());
			paramMap.put("type", moduleType);
			paramMap.put(
					"resDefNo",
					com.yusys.bione.plugin.base.common.GlobalConstants4plugin.MODEL_RES_NO);
			treeNode.setParams(paramMap);
			treeNode.setUpId("catalog" + mTmp.getCatalogId());
			treeNode.setIcon(contextPath + moduleNodeIcon);
			catalogIds.add(mTmp.getCatalogId());
			nodes.add(treeNode);
		}
		// 获取目录信息
		List<String> caIds = new ArrayList<String>();
		getFolderInfo(contextPath,catalogIds, caIds, nodes);
		if (setIds.size() > 0 && colFlag) {
			// 获取字段信息
			List<RptSysModuleColVO> cols = this.rptTmpDAO
					.getModuleColNodes(setIds);
			for (RptSysModuleColVO cTmp : cols) {
				String upId = cTmp.getSetId();
				CommonTreeNode colNode = new CommonTreeNode();
				colNode.setId("col" + cTmp.getColId());
				colNode.setText(StringUtils.isEmpty(cTmp.getCnNm()) ? cTmp
						.getEnNm() : cTmp.getCnNm());
				Map<String, String> colParams = new HashMap<String, String>();
				colParams.put("realId", cTmp.getColId());
				colParams.put("type", colType);
				colParams.put("setId", cTmp.getSetId());
				colParams.put("dimTypeStruct", cTmp.getDimTypeStruct());
				colNode.setParams(colParams);
				colNode.setUpId(upId);
				colNode.setIcon(contextPath + fieldIcon);
				colNode.setData(cTmp);
				nodes.add(colNode);
			}
		}
		return nodes;
	}
	
	private void getFolderInfo(String contextPath,List<String> upIds,List<String> catalogIds,List<CommonTreeNode> nodes){
		Map<String,Object> cp = new HashMap<String, Object>();
		List<RptSysModuleCatalog> catalogs = new ArrayList<RptSysModuleCatalog>();
		if(upIds.size()>0){
			cp.put("catalogIds", upIds);
			catalogs = this.rptTmpDAO.getModuleCatalogNodes(cp);
		}
		upIds.clear();
		List<String> upCaIds = new ArrayList<String>();
		if(catalogs != null && catalogs.size() > 0){
			for (RptSysModuleCatalog cTmp : catalogs) {
				catalogIds.add(cTmp.getCatalogId());
				upCaIds.add(cTmp.getUpId());
				// 加工目录节点
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId("catalog" + cTmp.getCatalogId());
				treeNode.setText(cTmp.getCatalogNm());
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("realId", cTmp.getCatalogId());
				paramMap.put("type", catalogType);
				treeNode.setParams(paramMap);
				if (GlobalConstants4frame.TREE_ROOT_NO.equals(cTmp.getUpId())
						|| StringUtils.isEmpty(cTmp.getUpId())) {
					treeNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				} else {
					treeNode.setUpId("catalog" + cTmp.getUpId());
				}
				treeNode.setIcon(contextPath + treeFolderIcon);
				nodes.add(treeNode);
			}
			for(String upCaId : upCaIds){
				if(!upCaId.equals("0") && !catalogIds.contains(upCaId))
					upIds.add(upCaId);
			}
			if(upIds.size()>0){
				getFolderInfo(contextPath,upIds, catalogIds, nodes);
			}
		}
	}

	public List<CommonTreeNode> getRelModuleTree(String searchNm) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		// 构造根节点
		// 获取数据模型信息
		Map<String,Object> params = new HashMap<String, Object>();
		List<String> catalogIds = new ArrayList<String>();;
		if(StringUtils.isNotBlank(searchNm)){
			params.put("setNm", "%"+searchNm+"%");
		}
		List<String> setTypes = new ArrayList<String>();
		setTypes.add(GlobalConstants4plugin.SET_TYPE_MUTI_DIM);
		setTypes.add(GlobalConstants4plugin.SET_TYPE_GENERIC);
		setTypes.add(GlobalConstants4plugin.SET_TYPE_SUM);
		
		params.put("setTypes", setTypes);
		List<RptSysModuleInfo> modules = this.rptTmpDAO.getModuleInfoNodes(params);
		List<String> setIds = new ArrayList<String>();
		for (RptSysModuleInfo mTmp : modules) {
			// 加工数据模型节点
			if (!setIds.contains(mTmp.getSetId())) {
				setIds.add(mTmp.getSetId());
			}
			CommonTreeNode treeNode = new CommonTreeNode();
			if(!mTmp.getSetId().equals(GlobalConstants4plugin.RPT_REPORT_RESULT)&&!mTmp.getSetId().equals(GlobalConstants4plugin.RPT_IDX_RESULT)){
				treeNode.setId(mTmp.getSetId());
				treeNode.setText(mTmp.getSetNm());
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("realId", mTmp.getSetId());
				paramMap.put("type", moduleType);
				treeNode.setParams(paramMap);
				treeNode.setUpId("catalog" + mTmp.getCatalogId());
				treeNode.setIcon(this.getContextPath() + moduleNodeIcon);
				catalogIds.add(mTmp.getCatalogId());
				nodes.add(treeNode);
			}
		}
		// 获取目录信息
		List<String> caIds = new ArrayList<String>();
		getFolderInfo(catalogIds, caIds, nodes);
		return nodes;
	}
	private void getFolderInfo(List<String> upIds,List<String> catalogIds,List<CommonTreeNode> nodes){
		Map<String,Object> cp = new HashMap<String, Object>();
		List<RptSysModuleCatalog> catalogs = new ArrayList<RptSysModuleCatalog>();
		if(upIds.size()>0){
			cp.put("catalogIds", upIds);
			catalogs = this.rptTmpDAO.getModuleCatalogNodes(cp);
		}
		upIds.clear();
		List<String> upCaIds = new ArrayList<String>();
		if(catalogs != null && catalogs.size() > 0){
			for (RptSysModuleCatalog cTmp : catalogs) {
				catalogIds.add(cTmp.getCatalogId());
				upCaIds.add(cTmp.getUpId());
				// 加工目录节点
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId("catalog" + cTmp.getCatalogId());
				treeNode.setText(cTmp.getCatalogNm());
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("realId", cTmp.getCatalogId());
				paramMap.put("type", catalogType);
				treeNode.setParams(paramMap);
				if (GlobalConstants4frame.TREE_ROOT_NO.equals(cTmp.getUpId())
						|| StringUtils.isEmpty(cTmp.getUpId())) {
					treeNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				} else {
					treeNode.setUpId("catalog" + cTmp.getUpId());
				}
				treeNode.setIcon(getContextPath() + treeFolderIcon);
				nodes.add(treeNode);
			}
			for(String upCaId : upCaIds){
				if(!upCaId.equals("0") && !catalogIds.contains(upCaId))
					upIds.add(upCaId);
			}
			if(upIds.size()>0){
				getFolderInfo(upIds, catalogIds, nodes);
			}
		}
	}
	
	private String getRptNum(ReportInfoVO rptInfo){
		PropertiesUtils pUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String autoFlag = pUtils.getProperty("autoRptNum");
		if(autoFlag.equals("Y")){
			String platForm = pUtils.getProperty("platform");
			String custom = pUtils.getProperty("user");
			String numlength = pUtils.getProperty("numLength");
			int length = NumberUtils.toInt(numlength);
			if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(rptInfo.getDefSrc())) {
				return getAutoRptNum(custom, length);
			} else {
				return getAutoRptNum(platForm, length);
			}
		}
		else{
			if(StringUtils.isNotBlank(rptInfo.getRptNum())){
				return rptInfo.getRptNum();
			}else{
				return RandomUtils.uuid2();
			}
		}
	}
	
	
	/**
	 * 保存报表方法入口
	 * 
	 * @param baseObj
	 * @param tmpArr
	 * @param mainTmp
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> saveFrsRptEntrance(String baseObj,
			String tmpArr, String mainTmp) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(baseObj)) {
			return returnMap;
		}
		// 报表基本信息
		ReportInfoVO rptInfo = JSON.parseObject(baseObj, ReportInfoVO.class);
		String createTime = rptInfo.getCreateTimeStr();
		if (!StringUtils.isEmpty(createTime)) {
			rptInfo.setCreateTime(new Timestamp(Long.valueOf(createTime)));
		}
		if (StringUtils.isEmpty(rptInfo.getRptNum())) {
			// 报表编号为空，置为默认报表编号（uuid）
			rptInfo.setRptNum(getRptNum(rptInfo));
		}
		if (rptInfo.getRankOrder()== null) {
			// 排列顺序为空，自定义报表置为默认库中最大序号
			rptInfo.setRankOrder(new BigDecimal(this.searchRankOrder(rptInfo.getCatalogId()).get("rankOrder")));
		}
		// 设置定义来源相关
		if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(rptInfo.getDefSrc())) {
			rptInfo.setDefOrg(BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
		} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(rptInfo.getDefSrc())) {
			rptInfo.setDefUser(BioneSecurityUtils.getCurrentUserId());
		} else {
			rptInfo.setDefSrc(GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		}
		returnMap.put("rptNm", rptInfo.getRptNm());
		// 保存基本信息
		// 获取主模板ID
		String mainTmpId = rptInfo.getTemplateId();
		if (StringUtils.isEmpty(mainTmpId)) {
			mainTmpId = RandomUtils.uuid2();
			rptInfo.setTemplateId(mainTmpId);
		}
		String rptIdTmp = this.saveFrsRptBaseInfo(rptInfo);
		returnMap.put("rptId", rptIdTmp);
		rptInfo.setRptId(rptIdTmp);
		Map<String, String> lineTmpIds = new HashMap<String, String>();
		if (!StringUtils.isEmpty(mainTmp)) {
			// 保存模板信息
			JSONObject mainTmpObj = JSON.parseObject(mainTmp);
			if (mainTmpObj != null) {
				// 保存主模板
				String mainLine = "_mainTab";
				Map<String, String> mMap = saveDesignInfo(mainTmpObj, rptInfo,
						mainTmpId, null);
				returnMap.put("verId", mMap.get("verId"));
				returnMap.put("paramTemplateId", mMap.get("paramTemplateId"));
				lineTmpIds.put(mainLine, mMap.get("tmpId"));
				EhcacheUtils.put(mMap.get("tmpId")+"-"+ mMap.get("verId"), "tmpInfo",null);
				if (tmpArr != null) {
					JSONArray tmpsTmp = JSON.parseArray(tmpArr);
					// 保存子模板
					for (int m = 0; m < tmpsTmp.size(); m++) {
						JSONObject tmpObj = tmpsTmp.getJSONObject(m);
						String tmpLineId = tmpObj.getString("lineId");
						if (StringUtils.isEmpty(tmpLineId)) {
							continue;
						}
						Map<String, String> subMap = saveDesignInfo(tmpObj,
								rptInfo, mainTmpId, null);
						lineTmpIds.put(tmpLineId, subMap.get("tmpId"));
						EhcacheUtils.put(mMap.get("tmpId")+"-"+ mMap.get("verId") + "-" + tmpLineId, "tmpInfo",null);
					}
				}
			}
		}
		returnMap.put("lineTmpIds", lineTmpIds);
		return returnMap;
	}

	/**
	 * 保存监管报表基本信息
	 * 
	 * @param rptInfo
	 */
	@SuppressWarnings("unused")
	@Transactional(readOnly = false)
	public String saveFrsRptBaseInfo(ReportInfoVO frsRpt) {
		if (frsRpt == null) {
			return null;
		}
		String rptId = frsRpt.getRptId();
		Boolean isUpt = false;
		if (StringUtils.isEmpty(rptId)) {
			rptId = RandomUtils.uuid2();
		} else {
			isUpt = true;
		}
		// 获取平台系统类型
		PropertiesUtils pUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String rptType = pUtils.getProperty("rptType");
		if (!StringUtils.isEmpty(rptType)) {
			frsRpt.setExtType(rptType);
		}
		if (StringUtils.isEmpty(frsRpt.getBusiType())) {
			// 若没有维护【业务类型】，设置成【行内报表】
			if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(rptType)) {
				// 监管，缺省业务类型为1104
				frsRpt.setBusiType(GlobalConstants4plugin.RPT_FRS_BUSI_1104);
			} else {
				// 非监管（平台）
				frsRpt.setBusiType(GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
			}
		}
		BigDecimal versionId = frsRpt.getVerId() == null ? BigDecimal.ONE
				: frsRpt.getVerId();
		// 若是修改操作，获取旧模板信息
		if (isUpt) {
			RptMgrReportInfo oldRpt = this.getEntityById(
					RptMgrReportInfo.class, frsRpt.getRptId());
			if (oldRpt != null && !StringUtils.isEmpty(oldRpt.getRptCycle())
					&& !oldRpt.getRptCycle().equals(frsRpt.getRptCycle())) {
				// 若报表频度发生变化，更新指标报表频度
				Map<String, Object> uptParams = new HashMap<String, Object>();
				uptParams.put("templateId", frsRpt.getTemplateId());
				uptParams.put("indexVerId", versionId);
				uptParams.put("calcCycle", frsRpt.getRptCycle());
				this.rptTmpDAO.updateIdxCycle(uptParams);
			}
		}
		// 维护报表基本信息
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currDate = sdf.format(new Date());
		Timestamp currTime = new Timestamp(new Date().getTime());
		RptMgrReportInfo rptInfo = new RptMgrReportInfo();
		BeanUtils.copy(frsRpt, rptInfo);
		rptInfo.setRptId(rptId);
		rptInfo.setLineId(frsRpt.getRptlineId());
		rptInfo.setRptType(GlobalConstants4plugin.RPT_TYPE_DESIGN);
		if (rptInfo.getCreateTime() == null) {
			rptInfo.setCreateTime(currTime);
		}
		rptInfo.setCfgId(frsRpt.getTemplateId());
		this.baseDAO.merge(rptInfo);
		// 维护监管信息表
		RptMgrFrsExt frsInfo = new RptMgrFrsExt();
		BeanUtils.copy(frsRpt, frsInfo);
		frsInfo.setRptId(rptId);
		this.baseDAO.merge(frsInfo);
		if (isUpt) {
			// 更新报表对应模板的单位、更新报表对应的模板是否修改信息
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("templateUnit", frsRpt.getTemplateUnit());
			paramMap.put("isUpt", frsRpt.getIsUpt());
			paramMap.put("templateId", frsRpt.getTemplateId());
			paramMap.put("verId", versionId);
			this.rptTmpDAO.updateTemplate(paramMap);
		}

		return rptId;
	}

	/**
	 * 保存报表模板
	 * 
	 * @param tmpJson
	 * @param frsRpt
	 * @param modules
	 * @param formulas
	 * @param idxs
	 * @param staticCells
	 * @param idxCalcCells
	 * @param tabIdxCells
	 * @param tabDimCells
	 *            ...
	 * @param tmpRemark
	 * @param mainTmpId
	 * @param oldTmpId
	 * @param tmpLineId
	 * @param newVerStartDate
	 *            新版本开始时间，若不存在该参数，做普通保存；若存在该参数，发布新版本逻辑
	 */
	@Transactional(readOnly = false)
	public Map<String, String> saveFrsRptTmp(String tmpJson,
			ReportInfoVO frsRpt, List<String> storeIdxNos,
			List<RptDesignModuleCellVO> modules,
			List<RptDesignFormulaCellVO> formulas,
			List<RptDesignIdxCellVO> idxs,
			List<RptDesignStaticCellVO> staticCells,
			List<RptDesignIdxCalcCellVO> idxCalcCells,
			List<RptDesignTabIdxVO> tabIdxCells,
			List<RptDesignTabDimVO> tabDimCells,
			List<RptDesignComcellInfoVO> comCells,
			List<RptDesignBatchCfgVO> batchs,
			List<RptDesignQueryDetailVO> details,String tmpRemark,
			String mainTmpId, String oldTmpId, String tmpLineId,
			String newVerStartDate) {
		Map<String, String> returnMap = new HashMap<String, String>();

		// key:row , value:batch
		Map<String, List<RptDesignBatchCfg>> rowCfgMap = new HashMap<String, List<RptDesignBatchCfg>>();
		// key:col , value:batch
		Map<String, List<RptDesignBatchCfg>> colCfgMap = new HashMap<String, List<RptDesignBatchCfg>>();

		if (frsRpt == null || StringUtils.isEmpty(mainTmpId)) {
			return returnMap;
		}
		PropertiesUtils pUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		/** 待保存的数据 **/
		// 单元格信息
		List<RptDesignCellInfo> cells4Save = new ArrayList<RptDesignCellInfo>();
		// 指标来源
		List<RptDesignSourceIdx> sourceIdxs4Save = new ArrayList<RptDesignSourceIdx>();
		// 数据集来源
		List<RptDesignSourceDs> sourceDs4Save = new ArrayList<RptDesignSourceDs>();
		// 单元格公式来源
		List<RptDesignSourceFormula> formulas4Save = new ArrayList<RptDesignSourceFormula>();
		// 文本常量来源
		List<RptDesignSourceText> texts4Save = new ArrayList<RptDesignSourceText>();
		// 指标定义信息
		List<RptIdxInfo> idxs4Save = new ArrayList<RptIdxInfo>();
		// 指标血缘关系信息
		List<RptIdxSrcRelInfo> srcRels4Save = new ArrayList<RptIdxSrcRelInfo>();
		// 指标与度量关系
		List<RptIdxMeasureRel> mesureRels4Save = new ArrayList<RptIdxMeasureRel>();
		// 指标与维度关系
		List<RptIdxDimRel> dimRels4Save = new ArrayList<RptIdxDimRel>();
		// 指标过滤信息
		List<RptIdxFilterInfo> filters4Save = new ArrayList<RptIdxFilterInfo>();
		// 指标公式信息
		List<RptIdxFormulaInfo> idxFormulas4Save = new ArrayList<RptIdxFormulaInfo>();
		// 指标批量过滤配置
		List<RptDesignBatchCfg> batchCfg4Save = new ArrayList<RptDesignBatchCfg>();
		// 列表维度来源
		List<RptDesignSourceTabdim> tabDims4Save = new ArrayList<RptDesignSourceTabdim>();
		// 列表指标来源
		List<RptDesignSourceTabidx> tabIdxs4Save = new ArrayList<RptDesignSourceTabidx>();
		// 一般单元格详情
		List<RptDesignComcellInfo> coms4Save = new ArrayList<RptDesignComcellInfo>();
		// 来源指标列表
		Set<String> srcIndexNos = new HashSet<String>();
		//指标收藏表
		List<RptDesignFavInfo> favs = new ArrayList<RptDesignFavInfo>();
		// 指标标签集合
		//根据指标配置的报表反向配置指标的标签
		List<BioneLabelObjRel> bioneLabelObjRelSave = new ArrayList<BioneLabelObjRel>();
		//查找指标标签
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", "idx");
		// 查询条件明细
		List<RptDesignQueryDetail> detail4Save = new ArrayList<RptDesignQueryDetail>();
		// 获取平台系统类型
		String rptType = pUtils.getProperty("rptType");
		if (!StringUtils.isEmpty(rptType)) {
			frsRpt.setExtType(rptType);
		}
		String rptId = frsRpt.getRptId();
		String tmpId = RandomUtils.uuid2();
		if (StringUtils.isEmpty(tmpLineId)) {
			// 主模板
			tmpId = mainTmpId;
		} else if (!StringUtils.isEmpty(oldTmpId)) {
			tmpId = oldTmpId;
		}
		BigDecimal versionId = frsRpt.getVerId() == null ? BigDecimal.ONE
				: frsRpt.getVerId();
		returnMap.put("verId", versionId.toString());
		returnMap.put("tmpId", tmpId);
		frsRpt.setTemplateId(tmpId);
		frsRpt.setVerId(versionId);
		Boolean isUpt = false;
		if (!StringUtils.isEmpty(rptId)) {
			isUpt = true;
		} else {
			rptId = RandomUtils.uuid2();
			frsRpt.setRptId(rptId);
		}
		if (!StringUtils.isEmpty(newVerStartDate)) {
			frsRpt.setVerStartDate(newVerStartDate);
		} else if (StringUtils.isEmpty(frsRpt.getVerStartDate())) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String currDate = StringUtils.isEmpty(frsRpt.getVerStartDate()) ? sdf
					.format(new Date()) : frsRpt.getVerStartDate();
			frsRpt.setVerStartDate(currDate);
		}
		if(StringUtils.isBlank(frsRpt.getVerEndDate())) {
			frsRpt.setVerEndDate(MAXDATE);
		}
		if(storeIdxNos != null && storeIdxNos.size()>0){
			for(String indexNo : storeIdxNos){
				RptDesignFavInfo fav = new RptDesignFavInfo();
				fav.setFavourId(RandomUtils.uuid2());
				fav.setIndexNo(indexNo);
				fav.setTemplateId(tmpId);
				fav.setVerId(versionId);
				favs.add(fav);
			}
		}
		if (isUpt && !StringUtils.isEmpty(oldTmpId)
				&& StringUtils.isEmpty(newVerStartDate)) {
			// 删除报表模板信息(非发布版本动作)
			this.removeTmpInfos(oldTmpId, frsRpt.getVerId());
		}
		if (isUpt && StringUtils.isNotEmpty(newVerStartDate)
				&& StringUtils.isNotEmpty(frsRpt.getParamId())) {
			// 若是发布版本动作，创建新的参数模板
			frsRpt.setParamId(RandomUtils.uuid2());
		}
		frsRpt.setIsCabin(GlobalConstants4plugin.STS_OFF);
		// 分析批量过滤配置
		if (batchs != null) {
			for (RptDesignBatchCfgVO cfgTmp : batchs) {
				String posNum = "";
				if (GlobalConstants4plugin.RPT_FILTER_MODEL_ROW.equals(cfgTmp
						.getObjType()) && cfgTmp.getColId() == 0) {
					// 若是行过滤
					posNum = cfgTmp.getRowId().toString();
				} else if (GlobalConstants4plugin.RPT_FILTER_MODEL_COL.equals(cfgTmp
						.getObjType()) && cfgTmp.getRowId() == 0) {
					// 列过滤
					posNum = cfgTmp.getColId().toString();
				} else {
					continue;
				}
				JSONArray filtArr = cfgTmp.getFiltInfos();
				for (int i = 0; i < filtArr.size(); i++) {
					JSONObject fTmp = (JSONObject) filtArr.get(i);
					RptDesignBatchCfg objTmp = new RptDesignBatchCfg();
					RptDesignBatchCfgPK objTmpPK = new RptDesignBatchCfgPK();
					objTmpPK.setTemplateId(tmpId);
					objTmpPK.setVerId(versionId);
					objTmpPK.setCfgId(StringUtils.isEmpty(fTmp
							.getString("cfgId")) ? RandomUtils.uuid2() : fTmp
							.getString("cfgId"));
					objTmp.setId(objTmpPK);
					objTmp.setPosType(cfgTmp.getObjType());
					objTmp.setPosNum(posNum);
					objTmp.setDimType(fTmp.getString("dimTypeNo"));
					objTmp.setFilterMode(fTmp.getString("filterModelVal"));
					objTmp.setFilterVal(fTmp.getString("checkedVal"));
					batchCfg4Save.add(objTmp);
					if (GlobalConstants4plugin.RPT_FILTER_MODEL_ROW.equals(cfgTmp
							.getObjType())) {
						// 放入行map
						if (rowCfgMap.get(posNum) == null) {
							List<RptDesignBatchCfg> cTmp = new ArrayList<RptDesignBatchCfg>();
							cTmp.add(objTmp);
							rowCfgMap.put(posNum, cTmp);
						} else {
							rowCfgMap.get(posNum).add(objTmp);
						}
					} else {
						if (colCfgMap.get(posNum) == null) {
							List<RptDesignBatchCfg> cTmp = new ArrayList<RptDesignBatchCfg>();
							cTmp.add(objTmp);
							colCfgMap.put(posNum, cTmp);
						} else {
							colCfgMap.get(posNum).add(objTmp);
						}
					}
				}
			}
		}
		
		Map<String, String> posiIdxNoRels = new HashMap<String, String>();
		// 数据集指标处理
		for (RptDesignModuleCellVO mCellTmp : modules) {
			if (StringUtils.isEmpty(mCellTmp.getRowId())
					|| StringUtils.isEmpty(mCellTmp.getColId())
					|| StringUtils.isEmpty(mCellTmp.getCellNo())) {
				continue;
			}
			RptDesignCellInfo cellTmp = new RptDesignCellInfo();
			BeanUtils.copy(mCellTmp, cellTmp);
			cellTmp.setRowId(new BigDecimal(mCellTmp.getRowId()));
			cellTmp.setColId(new BigDecimal(mCellTmp.getColId()));
			if (!StringUtils.isEmpty(mCellTmp.getDataLen())) {
				cellTmp.setDataLen(new BigDecimal(mCellTmp.getDataLen()));
			}
			if (!StringUtils.isEmpty(mCellTmp.getDataPrecision())) {
				cellTmp.setDataPrecision(new BigDecimal(mCellTmp
						.getDataPrecision()));
			}
			cellTmp.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE);
			RptDesignCellInfoPK pk = new RptDesignCellInfoPK();
			pk.setCellNo(mCellTmp.getCellNo());
			pk.setTemplateId(tmpId);
			pk.setVerId(versionId);
			cellTmp.setId(pk);
			cells4Save.add(cellTmp);
			// 保存数据集来源
			RptDesignSourceDs dsInfo = new RptDesignSourceDs();
			BeanUtils.copy(mCellTmp, dsInfo);
			RptDesignSourceDsPK dsPK = new RptDesignSourceDsPK();
			dsPK.setCellNo(mCellTmp.getCellNo());
			dsPK.setTemplateId(tmpId);
			dsPK.setVerId(versionId);
			dsInfo.setId(dsPK);
			if (!StringUtils.isEmpty(mCellTmp.getSortOrder())) {
				dsInfo.setSortOrder(new BigDecimal(mCellTmp.getSortOrder()));
			}
			sourceDs4Save.add(dsInfo);
		}
		// 指标单元格处理
		// -- 分析指标公共维度
		// -- 公共维度根据指标单元格和表间取数公式单元格的维度分析生成
		List<String> colIdxCommonDims = this.getColIdxCommonDims(frsRpt, null,
				pUtils);
		StringBuilder rptDimStr = new StringBuilder("");
		for (String dimTmp : colIdxCommonDims) {
			if (!StringUtils.isEmpty(rptDimStr.toString())) {
				rptDimStr.append(",");
			}
			rptDimStr.append(dimTmp);
		}
		// 获取公共维度类型具体信息(目前是获取全部的维度类型)
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap.put("dimNos", commonDims);
		List<RptDimTypeInfo> dimTypes = this.idxInfoDAO
				.getDimTypeInfos(paramMap);
		Map<String, RptDimTypeInfo> dimTypeMap = new HashMap<String, RptDimTypeInfo>();
		if (dimTypes != null) {
			for (RptDimTypeInfo typeTmp : dimTypes) {
				dimTypeMap.put(typeTmp.getDimTypeNo(), typeTmp);
			}
		}
		
		if(comCells != null && comCells.size()>0){
			for(RptDesignComcellInfoVO comCell : comCells){
				RptDesignComcellInfo info = new RptDesignComcellInfo();
				RptDesignComcellInfoPK id = new RptDesignComcellInfoPK();
				id.setCellNo(comCell.getCellNo());
				id.setTemplateId(tmpId);
				id.setVerId(versionId);
				info.setId(id);
				info.setColId(comCell.getColId());
				info.setRowId(comCell.getRowId());
				info.setTypeId(comCell.getTypeId());
				info.setContent(comCell.getContent());
				coms4Save.add(info);
			}
		}
		
		if (idxs != null) {
			// 获取指标 计算规则&时间度量&取值方式 信息
			List<RptIdxCalcRule> rules = this
					.getEntityList(RptIdxCalcRule.class);
			List<RptIdxTimeMeasure> timeMeasures = this
					.getEntityList(RptIdxTimeMeasure.class);
			List<RptIdxValType> types = this.getEntityList(RptIdxValType.class);
			Map<String, RptIdxCalcRule> ruleMap = new HashMap<String, RptIdxCalcRule>();
			Map<String, RptIdxTimeMeasure> timeMeasureMap = new HashMap<String, RptIdxTimeMeasure>();
			Map<String, RptIdxValType> typeMap = new HashMap<String, RptIdxValType>();
			for (RptIdxCalcRule tmp : rules) {
				ruleMap.put(tmp.getRuleId(), tmp);
			}
			for (RptIdxTimeMeasure tmp : timeMeasures) {
				timeMeasureMap.put(tmp.getTimeMeasureId(), tmp);
			}
			for (RptIdxValType tmp : types) {
				typeMap.put(tmp.getModeId(), tmp);
			}
			for (RptDesignIdxCellVO iCellTmp : idxs) {
				if (StringUtils.isEmpty(iCellTmp.getRowId())
						|| StringUtils.isEmpty(iCellTmp.getColId())
						|| StringUtils.isEmpty(iCellTmp.getCellNo())) {
					continue;
				}
				if (!StringUtils.isEmpty(iCellTmp.getRealIndexNo())) {
					String posiStr = ExcelAnalyseUtils.toABC(
							Integer.parseInt(iCellTmp.getRowId()),
							Integer.parseInt(iCellTmp.getColId()));
					posiIdxNoRels.put(posiStr, iCellTmp.getRealIndexNo());
				}
				RptDesignCellInfo cellTmp = new RptDesignCellInfo();
				BeanUtils.copy(iCellTmp, cellTmp);
				cellTmp.setRowId(new BigDecimal(iCellTmp.getRowId()));
				cellTmp.setColId(new BigDecimal(iCellTmp.getColId()));
				if (!StringUtils.isEmpty(iCellTmp.getDataLen())) {
					cellTmp.setDataLen(new BigDecimal(iCellTmp.getDataLen()));
				}
				if (!StringUtils.isEmpty(iCellTmp.getDataPrecision())) {
					cellTmp.setDataPrecision(new BigDecimal(iCellTmp
							.getDataPrecision()));
				}
				cellTmp.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_IDX);
				RptDesignCellInfoPK pk = new RptDesignCellInfoPK();
				pk.setCellNo(iCellTmp.getCellNo());
				pk.setTemplateId(tmpId);
				pk.setVerId(versionId);
				cellTmp.setId(pk);
				cells4Save.add(cellTmp);
				String idxNoTmp = StringUtils
						.isEmpty(iCellTmp.getRealIndexNo()) ? RandomUtils
						.uuid2() : iCellTmp.getRealIndexNo();
				RptDesignSourceIdx sourceIdx = new RptDesignSourceIdx();
				sourceIdx.setIndexNo(idxNoTmp);
				RptDesignSourceIdxPK sourcePK = new RptDesignSourceIdxPK();
				sourcePK.setCellNo(iCellTmp.getCellNo());
				sourcePK.setTemplateId(tmpId);
				sourcePK.setVerId(versionId);
				sourceIdx.setId(sourcePK);
				sourceIdx.setModeId(iCellTmp.getModeId());
				sourceIdx.setRuleId(iCellTmp.getRuleId());
				sourceIdx.setTimeMeasureId(iCellTmp.getTimeMeasureId());
				sourceIdxs4Save.add(sourceIdx);
				
				if(StringUtils.isNotBlank(iCellTmp.getIndexNo())) {
					if(null != obj) {
						//根据指标配置的报表反向配置指标的标签
						BioneLabelObjRelPK labelPK = new BioneLabelObjRelPK();
						BioneLabelObjRel label = new BioneLabelObjRel();
						labelPK.setLabelId(frsRpt.getBusiType());
						labelPK.setLabelObjId(obj.getLabelObjId());
						labelPK.setObjId(iCellTmp.getIndexNo());
						labelPK.setRptId(frsRpt.getRptId());
						label.setId(labelPK);
						if(!srcIndexNos.contains(iCellTmp.getIndexNo())) {
							bioneLabelObjRelSave.add(label);
						}
					}
					srcIndexNos.add(iCellTmp.getIndexNo());
				}
				
				// -- 指标模块信息维护
				this.mergeRptIdxs(idxNoTmp, versionId, iCellTmp, frsRpt,
						rptDimStr.toString(), dimTypeMap, tmpId, idxs4Save,
						mesureRels4Save, dimRels4Save, filters4Save,
						idxFormulas4Save, srcRels4Save,pUtils, rowCfgMap, colCfgMap,
						ruleMap, timeMeasureMap, typeMap);
			}
		}

		// 文本常量表达式
		for (RptDesignStaticCellVO sCellTmp : staticCells) {
			if (StringUtils.isEmpty(sCellTmp.getRowId())
					|| StringUtils.isEmpty(sCellTmp.getColId())
					|| StringUtils.isEmpty(sCellTmp.getCellNo())) {
				continue;
			}
			RptDesignCellInfo cellTmp = new RptDesignCellInfo();
			BeanUtils.copy(sCellTmp, cellTmp);
			cellTmp.setRowId(new BigDecimal(sCellTmp.getRowId()));
			cellTmp.setColId(new BigDecimal(sCellTmp.getColId()));
			if (!StringUtils.isEmpty(sCellTmp.getDataLen())) {
				cellTmp.setDataLen(new BigDecimal(sCellTmp.getDataLen()));
			}
			if (!StringUtils.isEmpty(sCellTmp.getDataPrecision())) {
				cellTmp.setDataPrecision(new BigDecimal(sCellTmp
						.getDataPrecision()));
			}
			cellTmp.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT);
			RptDesignCellInfoPK pk = new RptDesignCellInfoPK();
			pk.setCellNo(sCellTmp.getCellNo());
			pk.setTemplateId(tmpId);
			pk.setVerId(versionId);
			cellTmp.setId(pk);
			cells4Save.add(cellTmp);
			// 保存文本表达式
			RptDesignSourceText sourceText = new RptDesignSourceText();
			BeanUtils.copy(sCellTmp, sourceText);
			RptDesignSourceTextPK sPK = new RptDesignSourceTextPK();
			sPK.setCellNo(sCellTmp.getCellNo());
			sPK.setTemplateId(tmpId);
			sPK.setVerId(versionId);
			sourceText.setId(sPK);
			texts4Save.add(sourceText);
		}
		// 表间取数表达式
		for (RptDesignIdxCalcCellVO cCellTmp : idxCalcCells) {
			if (StringUtils.isEmpty(cCellTmp.getRowId())
					|| StringUtils.isEmpty(cCellTmp.getColId())
					|| StringUtils.isEmpty(cCellTmp.getCellNo())) {
				continue;
			}
			if (!StringUtils.isEmpty(cCellTmp.getRealIndexNo())) {
				String posiStr = ExcelAnalyseUtils.toABC(
						Integer.parseInt(cCellTmp.getRowId()),
						Integer.parseInt(cCellTmp.getColId()));
				posiIdxNoRels.put(posiStr, cCellTmp.getRealIndexNo());
			}
			RptDesignCellInfo cellTmp = new RptDesignCellInfo();
			BeanUtils.copy(cCellTmp, cellTmp);
			cellTmp.setRowId(new BigDecimal(cCellTmp.getRowId()));
			cellTmp.setColId(new BigDecimal(cCellTmp.getColId()));
			if (!StringUtils.isEmpty(cCellTmp.getDataLen())) {
				cellTmp.setDataLen(new BigDecimal(cCellTmp.getDataLen()));
			}
			if (!StringUtils.isEmpty(cCellTmp.getDataPrecision())) {
				cellTmp.setDataPrecision(new BigDecimal(cCellTmp
						.getDataPrecision()));
			}
			cellTmp.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_RPTCALC);
			RptDesignCellInfoPK pk = new RptDesignCellInfoPK();
			pk.setCellNo(cCellTmp.getCellNo());
			pk.setTemplateId(tmpId);
			pk.setVerId(versionId);
			cellTmp.setId(pk);
			cells4Save.add(cellTmp);
			String idxNoTmp = StringUtils.isEmpty(cCellTmp.getRealIndexNo()) ? RandomUtils
					.uuid2() : cCellTmp.getRealIndexNo();
			RptDesignSourceIdx sourceIdx = new RptDesignSourceIdx();
			sourceIdx.setIndexNo(idxNoTmp);
			RptDesignSourceIdxPK sourcePK = new RptDesignSourceIdxPK();
			sourcePK.setCellNo(cCellTmp.getCellNo());
			sourcePK.setTemplateId(tmpId);
			sourcePK.setVerId(versionId);
			sourceIdx.setId(sourcePK);
			sourceIdxs4Save.add(sourceIdx);
		}
		// 指标列表 - 维度信息
		List<String> colDims = new ArrayList<String>(); // 列维度
		if (tabDimCells != null) {
			for (RptDesignTabDimVO tabDimVO : tabDimCells) {
				if (StringUtils.isEmpty(tabDimVO.getDimTypeNo())
						|| StringUtils.isEmpty(tabDimVO.getRowId())
						|| StringUtils.isEmpty(tabDimVO.getColId())) {
					continue;
				}
				// 保存单元格信息
				RptDesignCellInfo cellTmp = new RptDesignCellInfo();
				BeanUtils.copy(tabDimVO, cellTmp);
				cellTmp.setRowId(new BigDecimal(tabDimVO.getRowId()));
				cellTmp.setColId(new BigDecimal(tabDimVO.getColId()));
				if (!StringUtils.isEmpty(tabDimVO.getDataLen())) {
					cellTmp.setDataLen(new BigDecimal(tabDimVO.getDataLen()));
				}
				if (!StringUtils.isEmpty(tabDimVO.getDataPrecision())) {
					cellTmp.setDataPrecision(new BigDecimal(tabDimVO
							.getDataPrecision()));
				}
				cellTmp.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB);
				RptDesignCellInfoPK pk = new RptDesignCellInfoPK();
				pk.setCellNo(tabDimVO.getCellNo());
				pk.setTemplateId(tmpId);
				pk.setVerId(versionId);
				cellTmp.setId(pk);
				cells4Save.add(cellTmp);
				// 保存列表维度信息
				RptDesignSourceTabdim tabDimTmp = new RptDesignSourceTabdim();
				RptDesignSourceTabdimPK tabDimPk = new RptDesignSourceTabdimPK();
				tabDimPk.setCellNo(tabDimVO.getCellNo());
				tabDimPk.setTemplateId(tmpId);
				tabDimPk.setVerId(versionId);
				tabDimTmp.setId(tabDimPk);
				if (!colDims.contains(tabDimVO.getDimTypeNo())) {
					colDims.add(tabDimVO.getDimTypeNo());
				}
				BeanUtils.copy(tabDimVO, tabDimTmp);
				if (!GlobalConstants4plugin.DIM_TYPE_DATE
						.equals(tabDimVO.getDimType())) {
					// 若不是日期格式，日期格式字段置为空
					tabDimTmp.setDateFormat("");
				}
				tabDims4Save.add(tabDimTmp);
			}
		}
		// 指标列表 - 指标信息
		if (tabIdxCells != null) {
			// 获取指标列表中，指标维度信息
			colIdxCommonDims = this
					.getColIdxCommonDims(frsRpt, colDims, pUtils);
			StringBuilder colDimStr = new StringBuilder("");
			for (String dimTmp : colIdxCommonDims) {
				if (!StringUtils.isEmpty(rptDimStr.toString())) {
					colDimStr.append(",");
				}
				colDimStr.append(dimTmp);
			}
			// 获取指标 计算规则&时间度量&取值方式 信息
			List<RptIdxCalcRule> rules = this
					.getEntityList(RptIdxCalcRule.class);
			List<RptIdxTimeMeasure> timeMeasures = this
					.getEntityList(RptIdxTimeMeasure.class);
			List<RptIdxValType> types = this.getEntityList(RptIdxValType.class);
			Map<String, RptIdxCalcRule> ruleMap = new HashMap<String, RptIdxCalcRule>();
			Map<String, RptIdxTimeMeasure> timeMeasureMap = new HashMap<String, RptIdxTimeMeasure>();
			Map<String, RptIdxValType> typeMap = new HashMap<String, RptIdxValType>();
			for (RptIdxCalcRule tmp : rules) {
				ruleMap.put(tmp.getRuleId(), tmp);
			}
			for (RptIdxTimeMeasure tmp : timeMeasures) {
				timeMeasureMap.put(tmp.getTimeMeasureId(), tmp);
			}
			for (RptIdxValType tmp : types) {
				typeMap.put(tmp.getModeId(), tmp);
			}
			for (RptDesignTabIdxVO tiTmp : tabIdxCells) {
				if (StringUtils.isEmpty(tiTmp.getRowId())
						|| StringUtils.isEmpty(tiTmp.getColId())
						|| StringUtils.isEmpty(tiTmp.getCellNo())) {
					continue;
				}
				if (!StringUtils.isEmpty(tiTmp.getRealIndexNo())) {
					String posiStr = ExcelAnalyseUtils.toABC(
							Integer.parseInt(tiTmp.getRowId()),
							Integer.parseInt(tiTmp.getColId()));
					posiIdxNoRels.put(posiStr, tiTmp.getRealIndexNo());
				}
				// 保存单元格信息
				RptDesignCellInfo cellTmp = new RptDesignCellInfo();
				BeanUtils.copy(tiTmp, cellTmp);
				cellTmp.setRowId(new BigDecimal(tiTmp.getRowId()));
				cellTmp.setColId(new BigDecimal(tiTmp.getColId()));
				if (!StringUtils.isEmpty(tiTmp.getDataLen())) {
					cellTmp.setDataLen(new BigDecimal(tiTmp.getDataLen()));
				}
				if (!StringUtils.isEmpty(tiTmp.getDataPrecision())) {
					cellTmp.setDataPrecision(new BigDecimal(tiTmp
							.getDataPrecision()));
				}
				cellTmp.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB);
				RptDesignCellInfoPK pk = new RptDesignCellInfoPK();
				pk.setCellNo(tiTmp.getCellNo());
				pk.setTemplateId(tmpId);
				pk.setVerId(versionId);
				cellTmp.setId(pk);
				cells4Save.add(cellTmp);
				// 保存列表指标信息
				String idxNoTmp = StringUtils.isEmpty(tiTmp.getRealIndexNo()) ? RandomUtils
						.uuid2() : tiTmp.getRealIndexNo();
				RptDesignSourceTabidx tabIdx = new RptDesignSourceTabidx();
				tabIdx.setIndexNo(idxNoTmp);
				RptDesignSourceTabidxPK tabIdxPK = new RptDesignSourceTabidxPK();
				tabIdxPK.setCellNo(tiTmp.getCellNo());
				tabIdxPK.setTemplateId(tmpId);
				tabIdxPK.setVerId(versionId);
				tabIdx.setId(tabIdxPK);
				tabIdx.setModeId(tiTmp.getModeId());
				tabIdx.setRuleId(tiTmp.getRuleId());
				tabIdx.setTimeMeasureId(tiTmp.getTimeMeasureId());
				tabIdx.setIsPassyear(tiTmp.getIsPassyear());
				tabIdxs4Save.add(tabIdx);
				if(StringUtils.isNotBlank(tabIdx.getIndexNo())) {
					if(null != obj) {
						//根据指标配置的报表反向配置指标的标签
						BioneLabelObjRelPK labelPK = new BioneLabelObjRelPK();
						BioneLabelObjRel label = new BioneLabelObjRel();
						labelPK.setLabelId(frsRpt.getBusiType());
						labelPK.setLabelObjId(obj.getLabelObjId());
						labelPK.setObjId(tabIdx.getIndexNo());
						labelPK.setRptId(frsRpt.getRptId());
						label.setId(labelPK);
						if(!srcIndexNos.contains(tabIdx.getIndexNo())) {
							bioneLabelObjRelSave.add(label);
						}
					}
					srcIndexNos.add(tabIdx.getIndexNo());
				}
				// -- 指标模块信息维护
				this.mergeColIdxs(idxNoTmp, versionId, tiTmp, frsRpt,
						colDimStr.toString(), dimTypeMap, tmpId, idxs4Save,
						mesureRels4Save, dimRels4Save, filters4Save,
						idxFormulas4Save, srcRels4Save, pUtils, rowCfgMap, colCfgMap,
						ruleMap, timeMeasureMap, typeMap);
			}
		}
		// 最后解析excel公式，因为excel公式与指标公式转换时，需要用到之前分析的报表指标信息
		// excel公式报表指标处理
		for (RptDesignFormulaCellVO fCellTmp : formulas) {
			if (StringUtils.isEmpty(fCellTmp.getRowId())
					|| StringUtils.isEmpty(fCellTmp.getColId())
					|| StringUtils.isEmpty(fCellTmp.getCellNo())
					|| !GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(fCellTmp
							.getIsRptIndex())) {
				continue;
			}
			String posiStr = ExcelAnalyseUtils.toABC(
					Integer.parseInt(fCellTmp.getRowId()),
					Integer.parseInt(fCellTmp.getColId()));
			posiIdxNoRels.put(posiStr, fCellTmp.getRealIndexNo());
		}
		for (RptDesignFormulaCellVO fCellTmp : formulas) {
			if (StringUtils.isEmpty(fCellTmp.getRowId())
					|| StringUtils.isEmpty(fCellTmp.getColId())
					|| StringUtils.isEmpty(fCellTmp.getCellNo())) {
				continue;
			}
			RptDesignCellInfo cellTmp = new RptDesignCellInfo();
			BeanUtils.copy(fCellTmp, cellTmp);
			cellTmp.setRowId(new BigDecimal(fCellTmp.getRowId()));
			cellTmp.setColId(new BigDecimal(fCellTmp.getColId()));
			if (!StringUtils.isEmpty(fCellTmp.getDataLen())) {
				cellTmp.setDataLen(new BigDecimal(fCellTmp.getDataLen()));
			}
			if (!StringUtils.isEmpty(fCellTmp.getDataPrecision())) {
				cellTmp.setDataPrecision(new BigDecimal(fCellTmp
						.getDataPrecision()));
			}
			cellTmp.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA);
			RptDesignCellInfoPK pk = new RptDesignCellInfoPK();
			pk.setCellNo(fCellTmp.getCellNo());
			pk.setTemplateId(tmpId);
			pk.setVerId(versionId);
			cellTmp.setId(pk);
			cells4Save.add(cellTmp);
			// 保存excel公式来源
			RptDesignSourceFormula fInfo = new RptDesignSourceFormula();
			BeanUtils.copy(fCellTmp, fInfo);
			RptDesignSourceFormulaPK fPK = new RptDesignSourceFormulaPK();
			fPK.setCellNo(fCellTmp.getCellNo());
			fPK.setTemplateId(tmpId);
			fPK.setVerId(versionId);
			fInfo.setId(fPK);
			if (GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL.equals(frsRpt
					.getTemplateType())) {
				// 明细类，不支持excel公式指标化
				fInfo.setIsRptIndex(GlobalConstants4plugin.COMMON_BOOLEAN_NO);
			} else if (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(fInfo
					.getIsRptIndex())) {
				// 构建指标信息
				RptDesignIdxCalcCellVO formulaIdx = new RptDesignIdxCalcCellVO();
				BeanUtils.copy(fCellTmp, formulaIdx);
				// -- 将excel公式转换成平台公式(平台所有excel公式落成报表指标全走空指标逻辑，不进行转换派生操作)
/*				Map<String, String> formulaMap = new HashMap<String, String>();
				try {
					formulaMap = excelFormula.parse(fCellTmp.getExcelFormula(),
							posiIdxNoRels);
				} catch (FormulaException e) {
					logger.error("[" + formulaIdx.getCellNo() + "]:"
							+ e.getMessage());
				}
				String formulaContentTmp = formulaMap.get("formulaContent");
				String srcIndexNo = formulaMap.get("srcIndexNo");*/
				formulaIdx.setIndexNo("");
				formulaIdx.setFormulaContent("");
				if (idxCalcCells != null) {
					idxCalcCells.add(formulaIdx);
				}
				// 保存指标来源表
				RptDesignSourceIdx sourceIdx = new RptDesignSourceIdx();
				sourceIdx.setIndexNo(formulaIdx.getRealIndexNo());
				RptDesignSourceIdxPK sourcePK = new RptDesignSourceIdxPK();
				sourcePK.setCellNo(formulaIdx.getCellNo());
				sourcePK.setTemplateId(tmpId);
				sourcePK.setVerId(versionId);
				sourceIdx.setId(sourcePK);
				sourceIdxs4Save.add(sourceIdx);
			}
			formulas4Save.add(fInfo);
		}
		// 最后再保存表间取数 指标体系部分
		for (RptDesignIdxCalcCellVO cCellSaveTmp : idxCalcCells) {
			String idxNoTmp = StringUtils
					.isEmpty(cCellSaveTmp.getRealIndexNo()) ? RandomUtils
					.uuid2() : cCellSaveTmp.getRealIndexNo();
			// -- 指标模块信息维护
			this.mergeRptCalcIdxs(idxNoTmp, versionId, cCellSaveTmp, frsRpt,
					rptDimStr.toString(), tmpId, idxs4Save, mesureRels4Save,
					dimRels4Save, filters4Save, idxFormulas4Save, pUtils);
		}
		// 分析查询条件明细
		for (RptDesignQueryDetailVO detailSaveTmp : details) {
			RptDesignQueryDetail detail = new RptDesignQueryDetail();
			RptDesignQueryDetailPK detailPK = new RptDesignQueryDetailPK();
			BeanUtils.copy(detailSaveTmp, detail);
			detailPK.setOrderNum(detailSaveTmp.getOrderNum());
			detailPK.setTemplateId(tmpId);
			detailPK.setVerId(versionId);
			detail.setId(detailPK);
			detail4Save.add(detail);
		}
		
		// 报表模板信息
		RptDesignTmpInfo designTmp = new RptDesignTmpInfo();
		BeanUtils.copy(frsRpt, designTmp);
		RptDesignTmpInfoPK designTmpPK = new RptDesignTmpInfoPK();
		designTmpPK.setTemplateId(tmpId);
		designTmpPK.setVerId(versionId);
		designTmp.setId(designTmpPK);
		designTmp.setVerStartDate(frsRpt.getVerStartDate());
		designTmp.setVerEndDate(frsRpt.getVerEndDate());
		
		if (tmpRemark == null) {
			tmpRemark = "";
		}
		if (tmpJson == null) {
			tmpJson = "";
		}
		designTmp.setTemplateContentjson(tmpJson);
		designTmp.setRemark(tmpRemark);
		if (StringUtils.isEmpty(tmpLineId)) {
			// 主模板
			designTmp.setParentTemplateId(GlobalConstants4frame.TREE_ROOT_NO);
		} else {
			// 业务条线子模板
			designTmp.setLineId(tmpLineId);
			designTmp.setParentTemplateId(mainTmpId);
		}
		this.baseDAO.merge(designTmp);
		
		// 维护报表基本信息
		Timestamp currTime = new Timestamp(new Date().getTime());
		RptMgrReportInfo rptInfo = new RptMgrReportInfo();
		BeanUtils.copy(frsRpt, rptInfo);
		rptInfo.setLineId(frsRpt.getRptlineId());
		rptInfo.setCreateTime(currTime);//每次保存报表都保存最新修改时间和修改人,应对一些追责问题
		rptInfo.setDefUser(BioneSecurityUtils.getCurrentUserInfo().getUserNo());
		rptInfo.setCfgId(frsRpt.getTemplateId());
		this.baseDAO.save(rptInfo);
		
		//删除部分指标和标签的关系
		String jql = "delete from BioneLabelObjRel label where label.id.rptId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, frsRpt.getRptId());
		// 保存单元格信息
		entityUtils.saveEntityJdbcBatch(cells4Save);
		// 保存指标来源
		entityUtils.saveEntityJdbcBatch(sourceIdxs4Save);
		// 保存指标收藏信息
		entityUtils.saveEntityJdbcBatch(favs);
		// 保存数据集来源
		entityUtils.saveEntityJdbcBatch(sourceDs4Save);
		// 保存单元格公式来源
		entityUtils.saveEntityJdbcBatch(formulas4Save);
		// 保存文本常量来源
		entityUtils.saveEntityJdbcBatch(texts4Save);
		// 保存指标列表维度来源
		entityUtils.saveEntityJdbcBatch(tabDims4Save);
		// 保存指标列表指标来源
		entityUtils.saveEntityJdbcBatch(tabIdxs4Save);
		// 保存指标定义信息
		entityUtils.saveEntityJdbcBatch(idxs4Save);
		// 保存指标与度量关系
		entityUtils.saveEntityJdbcBatch(mesureRels4Save);
		// 保存指标与维度关系
		entityUtils.saveEntityJdbcBatch(dimRels4Save);
		// 保存指标过滤信息
		entityUtils.saveEntityJdbcBatch(filters4Save);
		// 保存指标公式信息
		entityUtils.saveEntityJdbcBatch(idxFormulas4Save);
		// 保存批量过滤配置
		entityUtils.saveEntityJdbcBatch(batchCfg4Save);
		// 保存查询条件配置
		entityUtils.saveEntityJdbcBatch(detail4Save);
		// 保存单元格属性
		entityUtils.saveEntityJdbcBatch(coms4Save);
		// 保存单元格属性
		entityUtils.saveEntityJdbcBatch(srcRels4Save);
		//保存指标标签关系
		entityUtils.saveEntityJdbcBatch(bioneLabelObjRelSave);
		saveQueryDim(frsRpt);
		returnMap.put("paramTemplateId", frsRpt.getParamId());
		return returnMap;
	}

	/**
	 * 获取设计器信息
	 * 
	 * @param templateId
	 * @param verId
	 * @param useParentJson
	 *            是否使用父模板样式
	 * @return
	 */
	public Map<String, Object> getDesignInfo(String templateId,
			BigDecimal verId, Boolean useParentJson, String isEmptyIdx, String type) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(templateId)) {
			// 模板信息
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("templateId", templateId);
			String jql = "select tmp from RptDesignTmpInfo tmp where tmp.id.templateId = :templateId ";
			if (verId != null) {
				jql += " and tmp.id.verId = :verId";
				map1.put("verId", verId);
			}
			else{
				jql += " and tmp.verEndDate = :endDate";
				map1.put("endDate", "29991231");
			}
			List<RptDesignTmpInfo> tmps = this.baseDAO.findWithNameParm(jql, map1);
			if (tmps != null && tmps.size() > 0) {
				// 取最新版本
				RptDesignTmpInfo currTmp = tmps.get(0);
				returnMap.put("tmpInfo", currTmp);
				// 获取模板对应的单元格信息
				verId = currTmp.getId().getVerId();
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("templateId", templateId);
				map2.put("verId", verId);
				jql = "select cell from RptDesignCellInfo cell where cell.id.templateId = :templateId and cell.id.verId =:verId";
				List<RptDesignCellInfo> cells = this.baseDAO.findWithNameParm(jql, map2);
				// 获取报表查询条件配置
				jql = "select query from RptDesignQueryDim query where query.id.templateId = :templateId and query.id.verId =:verId";
				List<RptDesignQueryDim> queryDims = this.rptTmpDAO
						.getQueryDim(map2);
				if (queryDims != null && queryDims.size() > 0) {
					returnMap.put("publicDim", queryDims.get(0).getPublicDim());
					returnMap.put("queryDim", queryDims.get(0).getQueryDim());
					returnMap.put("paramTemplateId", queryDims.get(0)
							.getParamTemplateId());
					// 获取param json
					RptParamtmpVO paramVO = this.paramTempBS.show(queryDims
							.get(0).getParamTemplateId(), "edit");
					returnMap.put("paramJson", paramVO.getParamJson());
				}
				if(currTmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)){
					// 获取查询条件明细
					Map<String , Object> detailParam = new HashMap<String , Object>();
					detailParam.put("templateId", templateId);
					detailParam.put("verId", verId);
					List<RptDesignQueryDetailVO> detailObjs = this.rptTmpDAO.getQueryDetails(detailParam);
					if (detailObjs != null) {
						returnMap.put("detailObjs", detailObjs);
					}
				}
				// 批量过滤
				List<RptBatchViewVO> batchCfgs = this.rptTmpDAO
						.getBatchCfgs(map2);
				// 数据模型字段cell
				List<RptDesignModuleCellVO> moduleCells = new ArrayList<RptDesignModuleCellVO>();
				// excel公式cell
				List<RptDesignFormulaCellVO> formulaCells = new ArrayList<RptDesignFormulaCellVO>();
				// 指标cell
				List<RptDesignIdxCellVO> idxCells = new ArrayList<RptDesignIdxCellVO>();
				// 静态表达式cell
				List<RptDesignStaticCellVO> staticCells = new ArrayList<RptDesignStaticCellVO>();
				// 表间取数表达式cell
				List<RptDesignIdxCalcCellVO> idxCalcCells = new ArrayList<RptDesignIdxCalcCellVO>();
				// 列表维度cell
				List<RptDesignTabDimVO> colDimCells = new ArrayList<RptDesignTabDimVO>();
				// 列表指标cell
				List<RptDesignTabIdxVO> colIdxCells = new ArrayList<RptDesignTabIdxVO>();
				// 一般单元格字段cell
				List<RptDesignComcellInfoVO> comCells = new ArrayList<RptDesignComcellInfoVO>();
				
				jql ="select com from RptDesignComcellInfo com where com.id.templateId = ?0 and com.id.verId = ?1";
				List<RptDesignComcellInfo> comCellInfos = this.baseDAO.findWithIndexParam(jql, templateId,verId);
				if(comCellInfos!=null && comCellInfos.size()>0){
					for(RptDesignComcellInfo comCellInfo : comCellInfos){
						RptDesignComcellInfoVO vo = new RptDesignComcellInfoVO();
						vo.setCellNo(comCellInfo.getId().getCellNo());
						vo.setRowId(comCellInfo.getRowId());
						vo.setColId(comCellInfo.getColId());
						vo.setTypeId(comCellInfo.getTypeId());
						vo.setContent(comCellInfo.getContent());
						comCells.add(vo);
					}
				}
				if (cells != null) {
					List<String> indexNos = new ArrayList<String>();
					List<String> moduleCellNos = new ArrayList<String>();
					List<String> formulaCellNos = new ArrayList<String>();
					List<String> idxCellNos = new ArrayList<String>();
					List<String> staticCellNos = new ArrayList<String>();
					List<String> idxCalcCellNos = new ArrayList<String>();
					List<String> colDimCellNos = new ArrayList<String>();
					List<String> colIdxCellNos = new ArrayList<String>();
					for (RptDesignCellInfo cellTmp : cells) {
						if(cellTmp.getDataUnit() == null)
							cellTmp.setDataUnit("");
						if (GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE
								.equals(cellTmp.getCellDataSrc())
								&& !moduleCellNos.contains(cellTmp.getId()
										.getCellNo())) {
							moduleCellNos.add(cellTmp.getId().getCellNo());
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA
								.equals(cellTmp.getCellDataSrc())
								&& !formulaCellNos.contains(cellTmp.getId()
										.getCellNo())) {
							formulaCellNos.add(cellTmp.getId().getCellNo());
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_IDX
								.equals(cellTmp.getCellDataSrc())
								&& !idxCellNos.contains(cellTmp.getId()
										.getCellNo())) {
							idxCellNos.add(cellTmp.getId().getCellNo());
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT
								.equals(cellTmp.getCellDataSrc())
								&& !staticCellNos.contains(cellTmp.getId()
										.getCellNo())) {
							staticCellNos.add(cellTmp.getId().getCellNo());
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_RPTCALC
								.equals(cellTmp.getCellDataSrc())
								&& !idxCalcCellNos.contains(cellTmp.getId()
										.getCellNo())) {
							idxCalcCellNos.add(cellTmp.getId().getCellNo());
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB
								.equals(cellTmp.getCellDataSrc())
								&& !colDimCellNos.contains(cellTmp.getId()
										.getCellNo())) {
							colDimCellNos.add(cellTmp.getId().getCellNo());
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB
								.equals(cellTmp.getCellDataSrc())
								&& !colIdxCellNos.contains(cellTmp.getId()
										.getCellNo())) {
							colIdxCellNos.add(cellTmp.getId().getCellNo());
						}
					}
					Map<String, RptDesignSourceDsVO> dsMap = this
							.getModuleCells(moduleCellNos, templateId, verId);
					Map<String, RptDesignFormulaCellVO> fMap = this
							.getFormulaCells(formulaCellNos, templateId, verId);
					Map<String, RptDesignIdxCellVO> iMap = this.getIdxCells(
							idxCellNos, templateId, verId, batchCfgs, type);
					Map<String, RptDesignSourceText> sMap = this
							.getStaticCells(staticCellNos, templateId, verId);
					Map<String, RptDesignTabIdxVO> ciMap = this.getColIdxCells(
							colIdxCellNos, templateId, verId, batchCfgs);
					Map<String, RptDesignTabDimVO> diMap = this.getColDimCells(
							colDimCellNos, templateId, verId);
					Map<String, RptDesignIdxCalcCellVO> caMap = this.getCalcCells(
							idxCalcCellNos, templateId, verId);
					for (RptDesignCellInfo cellTmp : cells) {
						if (GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE
								.equals(cellTmp.getCellDataSrc())
								&& dsMap.containsKey(cellTmp.getId()
										.getCellNo())) {
							RptDesignModuleCellVO ct = new RptDesignModuleCellVO();
							BeanUtils.copy(cellTmp, ct);
							BeanUtils.copy(
									dsMap.get(cellTmp.getId().getCellNo()), ct);
							ct.setCellNo(cellTmp.getId().getCellNo());
							moduleCells.add(ct);
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA
								.equals(cellTmp.getCellDataSrc())
								&& fMap.containsKey(cellTmp.getId().getCellNo())) {
							RptDesignFormulaCellVO ft = new RptDesignFormulaCellVO();
							BeanUtils.copy(cellTmp, ft);
							BeanUtils.copy(
									fMap.get(cellTmp.getId().getCellNo()), ft);
							formulaCells.add(ft);
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_IDX
								.equals(cellTmp.getCellDataSrc())
								&& iMap.containsKey(cellTmp.getId().getCellNo())) {
							RptDesignIdxCellVO it = new RptDesignIdxCellVO();
							it = iMap.get(cellTmp.getId().getCellNo());
							BeanUtils.copy(cellTmp, it);
							if(!StringUtils.isBlank(it.getIndexNo())) {
								indexNos.add(it.getIndexNo());
							}else {
								it.setIndexNm("空指标");
							}
							//如果是总账指标，指标号为指标编号.度量编号的方式
							if(StringUtils.isNotBlank(it.getMeasureNo()) && !("INDEX_VAL").equals(it.getMeasureNo())) {
								it.setIndexNo(it.getIndexNo() + "." + it.getMeasureNo());
							}
							//如果是导出模式为全空指标,所有指标导出为空指标
							if(StringUtils.isNotBlank(isEmptyIdx) && GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(isEmptyIdx)) {
								it.setIndexNo("");
								it.setIndexNm("空指标");
							}
							idxCells.add(it);
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT
								.equals(cellTmp.getCellDataSrc())
								&& sMap.containsKey(cellTmp.getId().getCellNo())) {
							RptDesignStaticCellVO st = new RptDesignStaticCellVO();
							BeanUtils.copy(cellTmp, st);
							BeanUtils.copy(
									sMap.get(cellTmp.getId().getCellNo()), st);
							st.setCellNo(cellTmp.getId().getCellNo());
							staticCells.add(st);
						}else if (GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB
								.equals(cellTmp.getCellDataSrc())
								&& ciMap.containsKey(cellTmp.getId()
										.getCellNo())) {
							RptDesignTabIdxVO it = new RptDesignTabIdxVO();
							it = ciMap.get(cellTmp.getId().getCellNo());
							BeanUtils.copy(cellTmp, it);
							if(!StringUtils.isBlank(it.getIndexNo()))
							indexNos.add(it.getIndexNo());
							colIdxCells.add(it);
						} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB
								.equals(cellTmp.getCellDataSrc())
								&& diMap.containsKey(cellTmp.getId()
										.getCellNo())) {
							RptDesignTabDimVO st = new RptDesignTabDimVO();
							BeanUtils.copy(
									diMap.get(cellTmp.getId().getCellNo()), st);
							BeanUtils.copy(cellTmp, st);
							st.setCellNo(cellTmp.getId().getCellNo());
							colDimCells.add(st);
						}else if (GlobalConstants4plugin.RPT_CELL_SOURCE_RPTCALC
								.equals(cellTmp.getCellDataSrc())) {
							RptDesignIdxCalcCellVO it = new RptDesignIdxCalcCellVO();
							it = caMap.get(cellTmp.getId().getCellNo());
							BeanUtils.copy(cellTmp, it);
							it.setCellNo(cellTmp.getId().getCellNo());
							idxCalcCells.add(it);
						} 
					}
					returnMap.put("moduleCells", moduleCells);
					returnMap.put("formulaCells", formulaCells);
					returnMap.put("idxCells", idxCells);
					returnMap.put("staticCells", staticCells);
					returnMap.put("idxCalcCells", idxCalcCells);
					returnMap.put("colIdxCells", colIdxCells);
					returnMap.put("colDimCells", colDimCells);
					returnMap.put("batchCfgs", batchCfgs);
					returnMap.put("comCells", comCells);
					// 获取公共维度信息
					if(!currTmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)&& indexNos.size()>0) {
						Map<String, Object> params = new HashMap<String, Object>();
						List<List<String>> indexNosList = SplitStringBy1000.change(indexNos);
						List<RptDimTypeInfo> types = new ArrayList<RptDimTypeInfo>();
						List<Object[]> objs = new ArrayList<Object[]>();
						for (List<String> strings : indexNosList) {
							String sql = "select inf.dim_Type_No ,inf.CATALOG_ID,inf.DIM_TYPE_DESC,inf.DIM_TYPE_EN_NM,inf.DIM_TYPE_NM,inf.DIM_TYPE,inf.DIM_TYPE_STRUCT,inf.DIM_STS"
									+ " from   RPT_DIM_TYPE_INFO inf where 1 = 1 "
									+ " and exists (select 1 from   ( "
									+ " select  rel.dim_no , count(rel.index_no) as idxc"
									+ " from    rpt_idx_dim_rel rel,rpt_idx_info idx"
									+ " where idx.index_no =rel.index_no "
									+ " and idx.index_ver_id = rel.index_ver_id and idx.end_date=:endDate "
									+ " and rel.index_no in :indexNos group by rel.dim_no ) tmp"
									+ " where inf.dim_type_no = tmp.dim_no and tmp.idxc = :idxSize) ";
							params.put("endDate", "29991231");
							params.put("indexNos", strings);
							params.put("idxSize", indexNos.size());
							objs.addAll(this.baseDAO.findByNativeSQLWithNameParam(sql, params));
						}
						if (objs.size() > 0) {
							for (Object[] obj : objs) {
								RptDimTypeInfo rptDimTypeInfo = new RptDimTypeInfo();
								rptDimTypeInfo.setDimTypeNo(obj[0] != null ? obj[0].toString() : "");
								rptDimTypeInfo.setCatalogId(obj[1] != null ? obj[1].toString() : "");
								rptDimTypeInfo.setDimTypeDesc(obj[2] != null ? obj[2].toString() : "");
								rptDimTypeInfo.setDimTypeEnNm(obj[3] != null ? obj[3].toString() : "");
								rptDimTypeInfo.setDimTypeNm(obj[4] != null ? obj[4].toString() : "");
								rptDimTypeInfo.setDimType(obj[5] != null ? obj[5].toString() : "");
								rptDimTypeInfo.setDimTypeStruct(obj[6] != null ? obj[6].toString() : "");
								rptDimTypeInfo.setDimSts(obj[7] != null ? obj[7].toString() : "");
								types.add(rptDimTypeInfo);
							}
						}
						if (types.size() > 0) {
							returnMap.put("allDimArray", types);
						}
					}
				}
				
			}
			
		}
		return returnMap;
	}

	/**
	 * 删除报表
	 * 
	 * @param rptIds
	 * @param verIds
	 * @return
	 */
	@Transactional(readOnly = false)
	public String delRpts(List<String> rptIds, List<String> verIds) {
		String errorMsg = "";
		if (rptIds != null && rptIds.size() > 0 && verIds != null
				&& rptIds.size() == verIds.size()) {
			// 获取报表基本信息
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("rptIds", rptIds);
			List<RptMgrReportInfo> rpts = this.rptTmpDAO.getRptInfos(params);
			if (rpts != null) {
				for (int i = 0; i < rpts.size(); i++) {
					RptMgrReportInfo rpt = rpts.get(i);
					removeTmpInfos(rpt.getCfgId(), null);
				}
			}
			// 删除报表信息表
			this.rptTmpDAO.deleteRptInfos(rptIds);
			// 删除监管报表扩展
			this.rptTmpDAO.deleteRptFrsExts(rptIds);
			//根据报表id删除指标标签
			this.deleteIdxLabelByRptIds(rptIds);
			//删除报表授权关系表
			this.rptTmpDAO.delAuthObjRels(rptIds);
		}
		return errorMsg;
	}

	/**
	 * 根据报表id删除指标标签
	 * @param rptIds
	 */
	private void deleteIdxLabelByRptIds(List<String> rptIds) {
		if(rptIds != null && rptIds.size() > 0) {
			String jql = "delete from BioneLabelObjRel label where label.id.rptId in ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, rptIds);
		}
	}
	
	
	/**
	 * 根据参数获取报表集合
	 * 
	 * @param params
	 * @return
	 */
	public List<RptMgrReportInfo> getRptsByParams(Map<String, Object> params) {
		List<RptMgrReportInfo> rpts = new ArrayList<RptMgrReportInfo>();
		if (params != null) {
			rpts = this.rptTmpDAO.getRptInfos(params);
		}
		return rpts;
	}

	/**
	 * 验证排列顺序信息
	 * 
	 * @return
	 */
	public String checkRankOrder(String rankOrder, String rptId) {
		String sql = " select t.rpt_id from RPT_MGR_REPORT_INFO t where t.rank_order =?0";
		List<Object[]> lists = this.baseDAO.findByNativeSQLWithIndexParam(sql, rankOrder);
		if(lists.size()>0){
			if(!StringUtils.isEmpty(rptId)) {
				for(Object list :lists){
					if(rptId.equals(list.toString())){
						return "true"; 
					}else{
						return "false"; 
					}
				}
			}else{
				return "false"; 
			}
		}
		return "true";
	}

	/**
	 * 获取排列顺序信息
	 * 
	 * @return
	 */
	public Map<String, String> searchRankOrder(String catalogId) {
		Map<String, String> param = Maps.newHashMap(); 
		String jql = " select max(t.rankOrder) from RptMgrReportInfo t where t.catalogId = ?0";
		BigDecimal rankOrder = this.baseDAO.findUniqueWithIndexParam(jql,catalogId);
		if(rankOrder != null){
			Integer data = rankOrder.intValue()+1;
			param.put("rankOrder", String.valueOf(data));
		}else{
			param.put("rankOrder", "0");
		}
		return param;
	}

	
	/**
	 * 获取业务条线信息
	 * 
	 * @return
	 */
	public List<RptMgrBusiLine> getBusiLines() {
		Map<String, Object> params = new HashMap<String, Object>();
		return this.rptTmpDAO.getBusiLine(params);
	}

	public List<RptMgrReportCatalog> getCatalogByParam(String notCatalogId,
			String catalogNm, String upCatalogId, String extType,String defSrc) {
		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isNotBlank(defSrc)) {
			params.put("defSrc", defSrc);
			if(defSrc.equals(GlobalConstants4plugin.RPT_DEF_SRC_USER)){
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			}
		}
		if (StringUtils.isNotBlank(upCatalogId)) {
			params.put("upCatalogId", upCatalogId);
		}
		if (StringUtils.isNotBlank(notCatalogId)) {
			params.put("notCatalogId", notCatalogId);
		}
		if (StringUtils.isNotBlank(catalogNm)) {
			params.put("catalogNm", catalogNm);
		}
		if (StringUtils.isNotBlank(upCatalogId)) {
			params.put("upCatalogId", upCatalogId);
		}
		if (StringUtils.isNotBlank(extType)) {
			params.put("extType", extType);
		} else {
			PropertiesUtils pUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String rptType = pUtils.getProperty("rptType");
			params.put("extType", rptType);
		}
		return this.rptTmpDAO.getCatalogByParams(params);
	}

	/**
	 * 获取业务条线报表模板
	 * 
	 * @param parentTmpId
	 * @return
	 */
	public List<RptDesignTmpInfo> getBusiLineTmp(String parentTmpId) {
		List<RptDesignTmpInfo> tmps = new ArrayList<RptDesignTmpInfo>();
		if (!StringUtils.isEmpty(parentTmpId)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("parentTemplateId", parentTmpId);
			tmps = this.rptTmpDAO.getTmpByParams(params);
		}
		return tmps;
	}

	/**
	 * 同步主模板报表指标
	 * 
	 * @param mainTmpId
	 *            主模板ID
	 * @param verId
	 *            版本号
	 */
	@Transactional(readOnly = false)
	public void syncMainTmp(String mainTmpId, String verId) {
		if (StringUtils.isEmpty(mainTmpId) || StringUtils.isEmpty(verId)) {
			return;
		}
		// 获取当前主模板对应的子模板id集合
		RptDesignTmpInfoPK pk = new RptDesignTmpInfoPK();
		pk.setTemplateId(mainTmpId);
		pk.setVerId(new BigDecimal(verId));
		RptDesignTmpInfo mainTmp = this.getEntityByProperty(
				RptDesignTmpInfo.class, "id", pk);
		if (mainTmp == null) {
			return;
		}
		RptMgrReportInfo rpt = this.getEntityByProperty(RptMgrReportInfo.class,
				"cfgId", mainTmpId);
		if (rpt == null) {
			return;
		}
		// 主模板中已定义的报表指标集合
		Map<String, Object> p1 = new HashMap<String, Object>();
		p1.put("templateId", mainTmpId);
		p1.put("verId", verId);
		List<RptDesignSourceIdx> mainCells = this.rptTmpDAO.getMainTmpIdxs(p1);
		Map<String, String> mainIdxs = new HashMap<String, String>();
		for (RptDesignSourceIdx si : mainCells) {
			if (StringUtils.isEmpty(si.getIndexNo())) {
				continue;
			}
			if (!mainIdxs.containsKey(si.getId().getCellNo())) {
				mainIdxs.put(si.getId().getCellNo(), si.getIndexNo());
			}
		}
		// 分析子模板
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentTemplateId", mainTmpId);
		params.put("verId", verId);
		// 不同步子模板excel公式
		params.put("notCellType", GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA);
		List<RptDesignSourceIdx> subIdxs = this.rptTmpDAO.getSubTmpIdxs(params);
		if (subIdxs == null || subIdxs.size() <= 0) {
			return;
		}
		Map<String, RptDesignIdxCalcCellVO> cellCalcMap = new HashMap<String, RptDesignIdxCalcCellVO>();
		for (RptDesignSourceIdx subTmp : subIdxs) {
			if (cellCalcMap.containsKey(subTmp.getId().getCellNo())) {
				RptDesignIdxCalcCellVO cellCalc = cellCalcMap.get(subTmp
						.getId().getCellNo());
				cellCalc.setIndexNo(cellCalc.getIndexNo() + ","
						+ subTmp.getIndexNo());
				cellCalc.setFormulaContent(cellCalc.getFormulaContent()
						+ "+I('" + subTmp.getIndexNo() + "')");
			} else {
				RptDesignIdxCalcCellVO cellCalc = new RptDesignIdxCalcCellVO();
				cellCalc.setCellNo(subTmp.getId().getCellNo());
				cellCalc.setIndexNo(subTmp.getIndexNo());
				cellCalc.setFormulaContent("I('" + subTmp.getIndexNo() + "')");
				String realIndexNo = "";
				if (mainIdxs.containsKey(subTmp.getId().getCellNo())) {
					realIndexNo = mainIdxs.get(subTmp.getId().getCellNo());
				}
				cellCalc.setRealIndexNo(realIndexNo);
				cellCalcMap.put(subTmp.getId().getCellNo(), cellCalc);
			}
		}
		// 删除主模板旧数据
		this.delMainCells4Sync(mainTmp, verId);
		// 新增主模板表间计算数据
		this.insertMainCells4Sync(mainTmp, rpt, verId, cellCalcMap);
	}

	/**
	 * 获取维度类型信息
	 * 
	 * @param idxNos
	 * @return
	 */
	public List<RptDimTypeInfo> getDimTypesByNos(List<String> idxNos) {
		if (idxNos == null || idxNos.size() <= 0) {
			return new ArrayList<RptDimTypeInfo>();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idxNos", idxNos);
		params.put("idxSize", idxNos.size());
		params.put("dimSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		List<RptDimTypeInfo> dims = this.rptTmpDAO.getDimTypes(params);
		return dims;
	}

	/**
	 * 获取维度类型信息
	 * 
	 * @param dimNos
	 * @return
	 */
	public List<RptDimTypeInfo> getDimTypesByDims(List<String> dimNos) {
		if (dimNos == null || dimNos.size() <= 0) {
			return new ArrayList<RptDimTypeInfo>();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dimNos", dimNos);
		params.put("dimSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		List<RptDimTypeInfo> dims = this.rptTmpDAO.getDimTypes(params);
		return dims;
	}

	/**
	 * 获取指定报表对应的版本信息
	 * 
	 * @param rptId
	 * @return
	 */
	public List<? extends ReportInfoVO> rptVersionView(String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		return this.rptTmpDAO.getRptVersion(params);
	}

	/** Private Methods Begin **/

	// 获取上下文路径
	private String getContextPath() {
		return GlobalConstants4frame.APP_CONTEXT_PATH;
	}

	// 同步操作中，删除主模板旧数据
	private void delMainCells4Sync(RptDesignTmpInfo mainTmp, String verId) {
		// 1. 删除【报表模板】旧数据
		Map<String, Object> designParams = new HashMap<String, Object>();
		designParams.put("templateId", mainTmp.getId().getTemplateId());
		designParams.put("verId", verId);
		// 不同步子模板excel公式
		designParams
				.put("notCellType", GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA);
		this.rptTmpDAO.deleteCell4Sync(designParams);
		this.rptTmpDAO.deleteSourceDs4Sync(designParams);
		this.rptTmpDAO.deleteSourceFormula4Sync(designParams);
		this.rptTmpDAO.deleteSourceText4Sync(designParams);
		// 2. 删除【指标管理】旧数据
		Map<String, Object> idxParams = new HashMap<String, Object>();
		idxParams.put("templateId", mainTmp.getId().getTemplateId());
		idxParams.put("verId", verId);
		// 不同步子模板excel公式
		idxParams.put("notCellType", GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA);
		this.rptTmpDAO.deleteIdx4Sync(idxParams);
		this.rptTmpDAO.deleteMeasureRel4Sync(idxParams);
		this.rptTmpDAO.deleteDimRel4Sync(idxParams);
		this.rptTmpDAO.deleteFilter4Sync(idxParams);
		this.rptTmpDAO.deleteFormula4Sync(idxParams);
	}

	// 同步操作中，为主模板新增表间计算数据
	private void insertMainCells4Sync(RptDesignTmpInfo mainTmp,
			RptMgrReportInfo rpt, String verId,
			Map<String, RptDesignIdxCalcCellVO> cellCalcMap) {
		if (cellCalcMap == null || cellCalcMap.size() <= 0) {
			return;
		}
		PropertiesUtils pUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		// 单元格信息
		List<RptDesignCellInfo> cells4Save = new ArrayList<RptDesignCellInfo>();
		// 指标来源信息
		List<RptDesignSourceIdx> sourceIdxs4Save = new ArrayList<RptDesignSourceIdx>();
		// 指标定义信息
		List<RptIdxInfo> idxs4Save = new ArrayList<RptIdxInfo>();
		// 指标与度量关系
		List<RptIdxMeasureRel> measureRels4Save = new ArrayList<RptIdxMeasureRel>();
		// 指标与维度关系
		List<RptIdxDimRel> dimRels4Save = new ArrayList<RptIdxDimRel>();
		// 指标公式信息
		List<RptIdxFormulaInfo> idxFormulas4Save = new ArrayList<RptIdxFormulaInfo>();
		Iterator<String> it = cellCalcMap.keySet().iterator();
		while (it.hasNext()) {
			String cellNoTmp = it.next();
			RptDesignIdxCalcCellVO calcTmp = cellCalcMap.get(cellNoTmp);
			cells4Save.add(generateDefaultCalcCell(calcTmp.getCellNo(), mainTmp
					.getId().getTemplateId(), verId));
			String realIndexNo = calcTmp.getRealIndexNo();
			if (StringUtils.isEmpty(realIndexNo)) {
				// 主模板中该单元格并不是报表指标
				realIndexNo = RandomUtils.uuid2();
				// 模板指标来源
				RptDesignSourceIdx sourceIdxTmp = new RptDesignSourceIdx();
				RptDesignSourceIdxPK sourceIdxTmpPK = new RptDesignSourceIdxPK();
				sourceIdxTmpPK.setCellNo(calcTmp.getCellNo());
				sourceIdxTmpPK.setTemplateId(mainTmp.getId().getTemplateId());
				sourceIdxTmpPK.setVerId(new BigDecimal(verId));
				sourceIdxTmp.setId(sourceIdxTmpPK);
				sourceIdxTmp.setIndexNo(realIndexNo);
				sourceIdxs4Save.add(sourceIdxTmp);
				calcTmp.setRealIndexNo(realIndexNo);
			}
			// 指标基本信息
			idxs4Save
					.add(generateDefaultIdxInfo(mainTmp, rpt, calcTmp, pUtils));
			// 指标度量关系
			measureRels4Save.add(generateDefaultMeasureRel(
					calcTmp.getRealIndexNo(), verId, pUtils,pUtils.getProperty("rptDsId")));
			// 指标维度关系
			dimRels4Save.addAll(generateDefaultDimRel(calcTmp.getRealIndexNo(),
					verId, pUtils,pUtils.getProperty("rptDsId")));
			// 指标公式信息
			idxFormulas4Save.add(generateDefaultFormula(calcTmp, verId));
		}
		// 执行保存
		entityUtils.saveEntityJdbcBatch(cells4Save);
		entityUtils.saveEntityJdbcBatch(sourceIdxs4Save);
		entityUtils.saveEntityJdbcBatch(idxs4Save);
		entityUtils.saveEntityJdbcBatch(measureRels4Save);
		entityUtils.saveEntityJdbcBatch(dimRels4Save);
		entityUtils.saveEntityJdbcBatch(idxFormulas4Save);
	}

	// 生成默认的表间计算单元格对象
	private RptDesignCellInfo generateDefaultCalcCell(String cellNo,
			String templateId, String verId) {
		RptDesignCellInfo cell = new RptDesignCellInfo();
		// pk begin
		RptDesignCellInfoPK cellId = new RptDesignCellInfoPK();
		cellId.setCellNo(cellNo);
		cellId.setTemplateId(templateId);
		cellId.setVerId(new BigDecimal(verId));
		cell.setId(cellId);
		// pk end
		Map<String, Object> posiMap = ExcelAnalyseUtils
				.getRowColByCellPosi(cellNo);
		cell.setRowId(new BigDecimal((Integer) (posiMap.get("row"))));
		cell.setColId(new BigDecimal((Integer) (posiMap.get("col"))));
		cell.setIsUpt(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		cell.setIsNull(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		cell.setDisplayFormat(GlobalConstants4plugin.CELL_DISPLAY_FORMAT_ORG);
		cell.setCellDataSrc(GlobalConstants4plugin.RPT_CELL_SOURCE_RPTCALC);
		return cell;
	}

	// 生成默认的指标基本信息对象
	private RptIdxInfo generateDefaultIdxInfo(RptDesignTmpInfo mainTmp,
			RptMgrReportInfo rpt, RptDesignIdxCalcCellVO calc,
			PropertiesUtils pUtils) {
		RptIdxInfo idx = new RptIdxInfo();
		// pk begin
		RptIdxInfoPK idxPK = new RptIdxInfoPK();
		idxPK.setIndexNo(calc.getRealIndexNo());
		idxPK.setIndexVerId(mainTmp.getId().getVerId().longValue());
		idx.setId(idxPK);
		// pk end
		idx.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
		idx.setIndexSts(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		idx.setIndexNm(calc.getCellNo());
		idx.setIndexType(GlobalConstants4plugin.DERIVE_INDEX); // 派生指标
		idx.setStartDate(mainTmp.getVerStartDate());
		idx.setEndDate(mainTmp.getVerEndDate());
		idx.setCalcCycle(rpt.getRptCycle());
		idx.setBusiType(rpt.getBusiType());
		idx.setIsSum(GlobalConstants4plugin.COMMON_BOOLEAN_NO);
		idx.setIsRptIndex(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		idx.setTemplateId(mainTmp.getId().getTemplateId());
		// 来源指标
		idx.setSrcIndexNo(calc.getIndexNo());
		idx.setSrcIndexMeasure(pUtils.getProperty("steadyMeasureNo"));
		return idx;
	}

	// 生成默认的指标度量关系对象
	public RptIdxMeasureRel generateDefaultMeasureRel(String indexNo,
			String verId, PropertiesUtils pUtils,String dsId) {
		RptIdxMeasureRel rel = new RptIdxMeasureRel();
		// pk begin
		RptIdxMeasureRelPK relPK = new RptIdxMeasureRelPK();
		relPK.setIndexNo(indexNo);
		relPK.setIndexVerId(Long.valueOf(verId));
		relPK.setDsId(dsId);
		relPK.setMeasureNo(pUtils.getProperty("steadyMeasureNo"));
		rel.setId(relPK);
		// pk end
		rel.setStoreCol(pUtils.getProperty("defaultMeasure"));
		return rel;
	}

	// 生成默认的指标维度关系对象集合
	public List<RptIdxDimRel> generateDefaultDimRel(String indexNo,
			String verId, PropertiesUtils pUtils,String dsId) {
		List<RptIdxDimRel> rels = new ArrayList<RptIdxDimRel>();
		// 机构
		RptIdxDimRel orgRel = new RptIdxDimRel();
		// pk begin
		RptIdxDimRelPK orgRelPK = new RptIdxDimRelPK();
		orgRelPK.setIndexNo(indexNo);
		orgRelPK.setIndexVerId(Long.valueOf(verId));
		orgRelPK.setDimNo(pUtils.getProperty("orgDimTypeNo"));
		orgRelPK.setDsId(dsId);
		orgRel.setId(orgRelPK);
		// pk end
		orgRel.setDimType(GlobalConstants4plugin.DIM_TYPE_ORG);
		orgRel.setStoreCol(pUtils.getProperty("orgNo"));
		rels.add(orgRel);
		// 日期
		RptIdxDimRel dateRel = new RptIdxDimRel();
		// pk begin
		RptIdxDimRelPK dateRelPK = new RptIdxDimRelPK();
		dateRelPK.setIndexNo(indexNo);
		dateRelPK.setIndexVerId(Long.valueOf(verId));
		dateRelPK.setDimNo(pUtils.getProperty("dateDimTypeNo"));
		dateRelPK.setDsId(dsId);
		dateRel.setId(dateRelPK);
		// pk end
		dateRel.setDimType(GlobalConstants4plugin.DIM_TYPE_DATE);
		dateRel.setStoreCol(pUtils.getProperty("dataDate"));
		rels.add(dateRel);
		// 指标维度
		RptIdxDimRel idxRel = new RptIdxDimRel();
		// pk begin
		RptIdxDimRelPK idxRelPK = new RptIdxDimRelPK();
		idxRelPK.setIndexNo(indexNo);
		idxRelPK.setIndexVerId(Long.valueOf(verId));
		idxRelPK.setDimNo(pUtils.getProperty("indexDimTypeNo"));
		idxRelPK.setDsId(dsId);
		idxRel.setId(idxRelPK);
		// pk end
		idxRel.setDimType(GlobalConstants4plugin.DIM_TYPE_INDEXNO);
		idxRel.setStoreCol(pUtils.getProperty("indexNo"));
		rels.add(idxRel);
		return rels;
	}

	/**
	 * 发布新版本
	 * 
	 * @param baseObj
	 * @param tmpArr
	 * @param mainTmp
	 * @param templateId
	 * @param versionId
	 * @param newVerStartDate
	 */
	@Transactional(readOnly = false)
	public void publishTmp(String baseObj, String tmpArr, String mainTmp,
			String templateId, String versionId, String newVerStartDate) {
		if (StringUtils.isEmpty(templateId) || StringUtils.isEmpty(versionId)
				|| StringUtils.isEmpty(newVerStartDate)) {
			return;
		}
		// 1.获取该模板对应的所有模板id集合
		Map<String, Object> param1 = new HashMap<String, Object>();
		param1.put("templateId", templateId);
		List<String> templateIds = this.rptTmpDAO.getAllTmpIdsByParent(param1);
		if (templateIds != null && templateIds.size() > 0) {
			// 2.将旧版本结束日期闭链
			publishCloseZip(templateId, versionId, MAXDATE, newVerStartDate, templateIds);
			// 3.保存新版本
			publishNewZip(baseObj, tmpArr, mainTmp, newVerStartDate, templateIds);
		}
	}

	/**
	 * 自定义报表 - 修改基本信息
	 * 
	 * @param rptId
	 * @param verId
	 * @param templateId
	 * @param rptNm
	 * @param templateUnit
	 */
	@Transactional(readOnly = false)
	public void updateBaseRpt(ReportInfoVO frs) {
		if (frs == null) {
			return;
		}
		String rptId = frs.getRptId();
		String templateId = frs.getTemplateId();
		BigDecimal verId = frs.getVerId();
		String rptNm = frs.getRptNm();
		String templateUnit = frs.getTemplateUnit();
		String calcCycle = frs.getRptCycle();
		String lineId = frs.getRptlineId();
		if (!StringUtils.isEmpty(rptId) && verId != null
				&& !StringUtils.isEmpty(templateId)) {
			RptMgrReportInfo oldRpt = this.getEntityById(
					RptMgrReportInfo.class, rptId);
			RptMgrReportInfo rpt = new RptMgrReportInfo();
			BeanUtils.copy(oldRpt, rpt);
			String oldLineId ="";
			String oldRptCycle = "";
			if(oldRpt != null){
				oldLineId = oldRpt.getLineId();
				oldRptCycle = oldRpt.getRptCycle();
			}
			Map<String, Object> params = new HashMap<String, Object>();
			
			params.put("rptId", rptId);
			params.put("rptNm", rptNm);
			if (StringUtils.isNotEmpty(frs.getCatalogId())) {
				rpt.setCatalogId(frs.getCatalogId());
			}
			if (StringUtils.isNotEmpty(frs.getRptCycle())) {
				rpt.setRptCycle(frs.getRptCycle());
			}
			if (StringUtils.isNotEmpty(frs.getRptDesc())) {
				rpt.setRptDesc(frs.getRptDesc());
			}
			if (StringUtils.isNotEmpty(frs.getRptSts())) {
				rpt.setRptSts(frs.getRptSts());
			}
			if (null!=frs.getRankOrder()) {
				rpt.setRankOrder(frs.getRankOrder());
			}
			if (null!=frs.getRptlineId()||!"".equals(frs.getRptlineId())) {
				rpt.setLineId( frs.getRptlineId());
			}
			// 修改报表信息
			this.updateEntity(rpt);
			Map<String, Object> params4 = new HashMap<String, Object>();
			params4.put("rptId", rptId);
			RptMgrFrsExt ext = this.getEntityById(RptMgrFrsExt.class, rptId);
			
			if (StringUtils.isNotEmpty(frs.getDefDept())) {
				ext.setDefDept(frs.getDefDept());
			}
			if (StringUtils.isNotEmpty(frs.getUserId())) {
				ext.setUserId(frs.getUserId());
			}
			this.updateEntity(ext);
			// 修改模板单位，启用时间
			Map<String, Object> params2 = new HashMap<String, Object>();
			params2.put("templateUnit", templateUnit);
			params2.put("templateId", templateId);
			params2.put("verId", verId);
			if (StringUtils.isNotEmpty(frs.getIsAutoAdj())) {
				params2.put("isAutoAdj", frs.getIsAutoAdj());
			}
			if (StringUtils.isNotEmpty(frs.getVerStartDate())) {
				params2.put("verStartDate", frs.getVerStartDate());
				// 修改指标信息表版本时间
				Map<String, Object> params3 = new HashMap<String, Object>();
				params3.put("verStartDate", frs.getVerStartDate());
				params3.put("templateId", templateId);
				params3.put("verId", verId);
				this.rptTmpDAO.updateIndexInfo(params3);
			}
			this.rptTmpDAO.updateTemplateInfo(params2);
			// 若报表频度发生变化，更新指标报表频度
			if (!StringUtils.isEmpty(oldRptCycle)
					&& !oldRptCycle.equals(calcCycle)) {
				Map<String, Object> uptParams = new HashMap<String, Object>();
				uptParams.put("templateId", frs.getTemplateId());
				uptParams.put("indexVerId", verId);
				uptParams.put("calcCycle", calcCycle);
				this.rptTmpDAO.updateIdxCycle(uptParams);
			}
			if (!StringUtils.isEmpty(oldLineId)
					&& !oldLineId.equals(lineId)) {
				PropertiesUtils pUtils = PropertiesUtils.get(
						"bione-plugin/extension/report-common.properties");
				String setId = this.lineBS.getSetId(lineId, "rpt");
				if(!StringUtils.isNotBlank(setId)){
					setId = pUtils.getProperty("rptDsId");
				}
				String jql = "update RptIdxInfo idx set idx.setId = ?0 where idx.templateId = ?1 and idx.id.indexVerId = ?2";
				this.baseDAO.batchExecuteWithIndexParam(jql, setId,frs.getTemplateId(),verId.longValue());
				jql = "update RptIdxDimRel rel set rel.id.dsId = ?0 where rel.id.indexVerId = ?2 and rel.id.indexNo in (select idx.id.indexNo from RptIdxInfo idx where idx.templateId = ?1 and idx.id.indexVerId = ?2)";
				this.baseDAO.batchExecuteWithIndexParam(jql, setId,frs.getTemplateId(),verId.longValue());
				jql = "update RptIdxMeasureRel rel set rel.id.dsId = ?0 where rel.id.indexVerId = ?2 and rel.id.indexNo in (select idx.id.indexNo from RptIdxInfo idx where idx.templateId = ?1 and idx.id.indexVerId = ?2)";
				this.baseDAO.batchExecuteWithIndexParam(jql, setId,frs.getTemplateId(),verId.longValue());
			}
		}
	}


	/**
	 * 复制指定报表为新报表
	 * @param frs
	 * @param isNewVer 区分是版本新增还是复制报表
	 * @param lastVer 
	 */
	@Transactional(readOnly = false)
	public void copyRpt(ReportInfoVO frs, Boolean isNewVer, BigDecimal lastVer) {
		if (frs == null || StringUtils.isEmpty(frs.getCopyRptId())) {
			return;
		}
		// 获取待拷贝报表的最新模板信息
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", frs.getCopyRptId());
		if(isNewVer) {
			params.put("maxDate", frs.getVerEndDate());
			params.put("verId", frs.getVerId());
		}else {
			params.put("maxDate", MAXDATE);
		}
		@SuppressWarnings("unchecked")
		List<ReportInfoVO> copyRpts = (List<ReportInfoVO>)this.rptTmpDAO.getFrsRptsById(params);
		if (copyRpts == null || copyRpts.size() <= 0) {
			return;
		}
		ReportInfoVO copyRpt = copyRpts.get(0);
		String copyTemplateId = copyRpt.getTemplateId();
		BigDecimal copyVersion = copyRpt.getMaxVerId();
		BigDecimal newVersion = BigDecimal.ONE;
		if(isNewVer) {
			copyVersion = frs.getVerId();
			newVersion = frs.getMaxVerId();
		}else {
			copyVersion = newVersion = frs.getVerId();
		}
		
		// 新的templateId
		String newTemplateId = frs.getTemplateId();
		String newUnit = frs.getTemplateUnit();
		String newVerStartDate = frs.getVerStartDate();
		String newIsAutoAdj = frs.getIsAutoAdj();
		// 报表基本信息
		if(!isNewVer) {
			newTemplateId = RandomUtils.uuid2();
			String rptId = RandomUtils.uuid2();
			frs.setRankOrder(new BigDecimal(this.searchRankOrder(frs.getCatalogId()).get("rankOrder")));
			RptMgrReportInfo rptInfo = new RptMgrReportInfo();
			BeanUtils.copy(frs, rptInfo);
			RptMgrFrsExt extInfo = new RptMgrFrsExt();
			BeanUtils.copy(frs, extInfo);
			extInfo.setRptId(rptId);
			Timestamp currTime = new Timestamp(new Date().getTime());
			rptInfo.setCreateTime(currTime);
			rptInfo.setRptId(rptId);
			rptInfo.setCfgId(newTemplateId);
			rptInfo.setRptNum(getRptNum(frs));
			if(GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(rptInfo.getDefSrc())){
				rptInfo.setDefUser(BioneSecurityUtils.getCurrentUserId());
			}
			this.baseDAO.merge(rptInfo);
			this.baseDAO.merge(extInfo);
			//普通用户复制的报表赋值默认权限
			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				saveAuthObjRel(rptId);
			}
		}
		// 获取待拷贝报表最新的相关数据结构
		// 1. 设计器配置相关
		Map<String, Object> designMap = new HashMap<String, Object>();
		designMap.put("id.templateId", copyTemplateId);
		designMap.put("id.verId", copyVersion);
		for (Class<?> designEntityTmp : designEntities) {
			List<Object> entities = entityUtils.getEntitiesJpa(designEntityTmp,
					designMap);
			if (entities != null && entities.size() > 0) {
				// 插入拷贝数据
				Map<String, Object> design4CopyMap = new HashMap<String, Object>();
				design4CopyMap.put("id.templateId", newTemplateId);
				design4CopyMap.put("id.verId", newVersion);
				design4CopyMap.put("templateUnit", newUnit);
				design4CopyMap.put("verStartDate", newVerStartDate);
				design4CopyMap.put("isAutoAdj", newIsAutoAdj);
				design4CopyMap.put("isUpt", frs.getIsUpt());
				if(isNewVer) {
					design4CopyMap.put("verEndDate", frs.getVerEndDate());
				}
				design4CopyMap.put("fixedLength", frs.getFixedLength());
				design4CopyMap.put("isPaging", frs.getIsPaging());
				design4CopyMap.put("importConfig", frs.getImportConfig());
				entityUtils.copyEntityJdbcBatch(entities, design4CopyMap, null, null);
			}
		}
		Map<String, Object> designFavMap = new HashMap<String, Object>();
		designFavMap.put("templateId", copyTemplateId);
		for (Class<?> designEntityTmp : designFavEntities) {
			List<Object> entities = entityUtils.getEntitiesJpa(designEntityTmp,
					designFavMap);
			if (entities != null && entities.size() > 0) {
				// 插入拷贝数据
				Map<String, Object> design4CopyMap = new HashMap<String, Object>();
				design4CopyMap.put("templateId", newTemplateId);
				List<String> randomList = new ArrayList<String>();
				randomList.add("favourId");
				entityUtils.copyEntityJdbcBatch(entities, design4CopyMap, null, randomList);
			}
		}
		// 2. 设计器指标关系相关
		Map<String, Object> idxRelMap = new HashMap<String, Object>();
		idxRelMap.put("id.templateId", copyTemplateId);
		idxRelMap.put("id.verId", copyVersion);
		// -- 旧指标号与新指标号对应关系
		Map<String, Object> idxNoMap = new HashMap<String, Object>();
		List<String> oldIdxs = new ArrayList<String>();
		for (Class<?> relEntityTmp : idxRelEntities) {
			List<Object> entities = entityUtils
					.getEntitiesJpa(relEntityTmp, idxRelMap);
			if (entities != null && entities.size() > 0) {
				// 分析获取旧指标与新指标关系
				for (Object tmp : entities) {
					String oldIdxNo = (String) entityUtils.getValueByFieldExpr(
							"indexNo", tmp);
					if (StringUtils.isNotEmpty(oldIdxNo)) {
						String newIdxNo = RandomUtils.uuid2();
						if(isNewVer) {//如果是发布新版本，则指标编号不变
							newIdxNo = oldIdxNo;
						}
						idxNoMap.put(oldIdxNo, newIdxNo);
						oldIdxs.add(oldIdxNo);
					}
				}
				// 插入拷贝数据
				Map<String, Object> rel4CopyMap = new HashMap<String, Object>();
				rel4CopyMap.put("id.templateId", newTemplateId);
				rel4CopyMap.put("id.verId", newVersion);
				entityUtils.copyEntityJdbcBatch(entities, rel4CopyMap, idxNoMap, null);
			}
		}
		// 3. 指标配置相关
		Map<String, Object> idxMap = new HashMap<String, Object>();
		if (oldIdxs.size() > 0) {
			List<List<String>> oldIdxLists = SplitStringBy1000.changeByNum(oldIdxs, 10000);//已10000做分割
			for(List<String> oldIdxList : oldIdxLists) {
				List<String> arrayIdxs = new ArrayList<String>();
				arrayIdxs.addAll(oldIdxList);
				idxMap.put("id.indexNo", arrayIdxs);
				idxMap.put("id.indexVerId", copyVersion.longValue());
				for (Class<?> idxEntityTmp : idxEntities) {
					List<Object> entities = entityUtils.getEntitiesJpa(idxEntityTmp, idxMap);
					if (entities != null && entities.size() > 0) {
						// 插入拷贝数据
						Map<String, Object> idx4CopyMap = new HashMap<String, Object>();
						idx4CopyMap.put("templateId", newTemplateId);
						idx4CopyMap.put("id.indexVerId", Long.valueOf(newVersion.toString()));
						
						if(isNewVer) {
							idx4CopyMap.put("startDate", newVerStartDate);
							idx4CopyMap.put("endDate", frs.getVerEndDate());
						}
						entityUtils.copyEntityJdbcBatch(entities, idx4CopyMap, idxNoMap, null);
					}
				}
			}
		}
		
		//报表指标配置信息
		Map<String, Object> idxCfgMap = new HashMap<String, Object>();
		idxCfgMap.put("id.templateId", copyTemplateId);
		idxCfgMap.put("id.verId", copyVersion);
		for (Class<?> paramEntityTmp : rptIdxCfgEntities) {
			List<Object> entities = entityUtils.getEntitiesJpa(paramEntityTmp, idxCfgMap);
			if (entities != null && entities.size() > 0) {
				// 插入拷贝数据
				Map<String, Object> idxCfgCopyMap = new HashMap<String, Object>();
				idxCfgCopyMap.put("id.templateId", newTemplateId);
				idxCfgCopyMap.put("id.verId", newVersion);
				idxCfgCopyMap.put("rptNum", frs.getRptNum());
				entityUtils.copyEntityJdbcBatch(entities, idxCfgCopyMap, null, null);
			}
		}
		
		//4.检验公式，发布新版本的时候才复制
		if(isNewVer) {
			//获取一张报表的上个版本的校验公式，并创建新版校验公式
			//this.createValidExt(frs, newVerStartDate, lastVer);
			
		}
		
		RptDesignQueryDimPK pk = new RptDesignQueryDimPK();
		pk.setTemplateId(copyTemplateId);
		pk.setVerId(copyRpt.getMaxVerId());
		RptDesignQueryDim oldDim = this.getEntityById(RptDesignQueryDim.class,
				pk);
		if (oldDim == null || StringUtils.isEmpty(oldDim.getParamTemplateId())) {
			return;
		}
		String copyParamId = oldDim.getParamTemplateId();
		String newParamId = RandomUtils.uuid2();
		// 5. 参数模板关系相关
		// paramEntities
		Map<String, Object> paramRelMap = new HashMap<String, Object>();
		paramRelMap.put("id.templateId", copyTemplateId);
		paramRelMap.put("id.verId", copyVersion);
		for (Class<?> paramRelEntityTmp : paramRelEntities) {
			List<Object> entities = entityUtils.getEntitiesJpa(paramRelEntityTmp,
					paramRelMap);
			if (entities != null && entities.size() > 0) {
				// 插入拷贝数据
				Map<String, Object> paramRel4CopyMap = new HashMap<String, Object>();
				paramRel4CopyMap.put("id.templateId", newTemplateId);
				paramRel4CopyMap.put("id.verId", newVersion);
				paramRel4CopyMap.put("paramTemplateId", newParamId);
				entityUtils.copyEntityJdbcBatch(entities, paramRel4CopyMap, null, null);
			}
		}
		// 6. 参数模板信息
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("paramtmpId", copyParamId);
		for (Class<?> paramEntityTmp : paramEntities) {
			List<Object> entities = entityUtils.getEntitiesJpa(paramEntityTmp,
					paramMap);
			if (entities != null && entities.size() > 0) {
				// 插入拷贝数据
				Map<String, Object> param4CopyMap = new HashMap<String, Object>();
				param4CopyMap.put("paramtmpId", newParamId);
				List<String> randomFields = new ArrayList<String>();
				randomFields.add("paramId");
				entityUtils.copyEntityJdbcBatch(entities, param4CopyMap, null, randomFields);
			}
		}
	}

	/**
	 * 创建新版校验公式
	 * @param frs
	 * @param newVerStartDate
	 * @param lastVer
	 */
	public void createValidExt(ReportInfoVO frs, String newVerStartDate, BigDecimal lastVer) {
		String jql = " select t.id.checkId from RptDesignTmpInfo r,RptValidLogicRptRel t,RptValidCfgextLogic l where r.id.templateId = t.id.rptTemplateId"
				+ " and t.id.checkId = l.checkId and r.verStartDate = l.startDate and r.verEndDate = l.endDate and r.id.templateId = ?0 and r.id.verId = ?1";
		List<String> checkList = this.baseDAO.findWithIndexParam(jql, frs.getTemplateId(), lastVer);
		jql = " select t.checkId from RptDesignTmpInfo r,RptValidCfgextWarn t where r.id.templateId = t.rptTemplateId and r.verStartDate = t.startDate "
				+ "and r.verEndDate = t.endDate and r.id.templateId = ?0 and r.id.verId = ?1";
		List<String> warnCheckList = this.baseDAO.findWithIndexParam(jql, frs.getTemplateId(), lastVer);
		checkList.addAll(warnCheckList);
		if(checkList.size() > 0) {
			Map<String, Object> checkIdMap = new HashMap<String, Object>();
			for(String checkId : checkList) {
				String newCheckId = RandomUtils.uuid2();
				checkIdMap.put(checkId, newCheckId);
			}
			//查找检验相关配置信息数据
			Map<String, Object> checkMap = new HashMap<String, Object>();
			checkMap.put("checkId", checkList);
			for (Class<?> validEntityTmp : validEntities) {
				List<Object> entities = entityUtils.getEntitiesJpa(validEntityTmp,
						checkMap);
				if (entities != null && entities.size() > 0) {
					// 插入拷贝数据
					Map<String, Object> checkCopyMap = new HashMap<String, Object>();
					checkCopyMap.put("startDate", newVerStartDate);
					checkCopyMap.put("endDate", frs.getVerEndDate());
					entityUtils.copyEntityJdbcBatch(entities, checkCopyMap, checkIdMap, null);
				}
			}
			
			//查找检验信息数据
			checkMap = new HashMap<String, Object>();
			checkMap.put("id.checkId", checkList);
			for (Class<?> validEntityTmp : validCfgEntities) {
				List<Object> entities = entityUtils.getEntitiesJpa(validEntityTmp,
						checkMap);
				if (entities != null && entities.size() > 0) {
					// 插入拷贝数据
					entityUtils.copyEntityJdbcBatch(entities, null, checkIdMap, null);
				}
			}
		}
	}

	/**
	 * 将平台全部报表进行超级复制，分置成 月、季、年三个报表 （这是一个debug版方法，规则太死，仅限内部人员使用，这不是演习 = 。 =）
	 */
	@Transactional(readOnly = false)
	public void superCopyRpt() {
		// 报表设计器模型相关数据结构
		Class<?>[] designEntitiesHehe = { RptDesignTmpInfo.class,
				RptDesignCellInfo.class, RptDesignBatchCfg.class,
				RptDesignSourceDs.class, RptDesignSourceTabdim.class,
				RptDesignSourceFormula.class, RptDesignSourceText.class };
		// 设计器指标关系数据结构
		Class<?>[] idxRelEntitiesHehe = { RptDesignSourceTabidx.class };
		// 报表指标相关数据结构
		Class<?>[] idxEntitiesHehe = { RptIdxInfo.class,
				RptIdxMeasureRel.class, RptIdxDimRel.class,
				RptIdxFilterInfo.class };
		// 参数模板关系
		Class<?>[] paramRelEntitiesHehe = { RptDesignQueryDim.class };
		// 参数模板
		Class<?>[] paramEntitiesHehe = { RptParamtmpInfo.class,
				RptParamtmpAttr.class };
		// 频度集合
		String[] copyCycles1 = { GlobalConstants4plugin.CALC_CYCLE_MONTH,
				GlobalConstants4plugin.CALC_CYCLE_SEASON,
				GlobalConstants4plugin.CALC_CYCLE_YEAR };
		String[] copyCycles2 = { GlobalConstants4plugin.CALC_CYCLE_SEASON,
				GlobalConstants4plugin.CALC_CYCLE_YEAR };
		String[] copyCycles3 = { GlobalConstants4plugin.CALC_CYCLE_YEAR };
		// 维度map
		Map<String, Object> dimParamMap = new HashMap<String, Object>();
		List<RptDimTypeInfo> dimTypes = this.idxInfoDAO
				.getDimTypeInfos(dimParamMap);
		Map<String, RptDimTypeInfo> dimTypeMap = new HashMap<String, RptDimTypeInfo>();
		if (dimTypes != null) {
			for (RptDimTypeInfo typeTmp : dimTypes) {
				dimTypeMap.put(typeTmp.getDimTypeNo(), typeTmp);
			}
		}
		// 计算规则
		List<RptIdxCalcRule> rules = this.getEntityList(RptIdxCalcRule.class);
		Map<String, RptIdxCalcRule> ruleMap = new HashMap<String, RptIdxCalcRule>();
		for (RptIdxCalcRule rule : rules) {
			ruleMap.put(rule.getRuleId(), rule);
		}
		// 时间度量
		List<RptIdxTimeMeasure> times = this
				.getEntityList(RptIdxTimeMeasure.class);
		Map<String, RptIdxTimeMeasure> timeMap = new HashMap<String, RptIdxTimeMeasure>();
		for (RptIdxTimeMeasure time : times) {
			timeMap.put(time.getTimeMeasureId(), time);
		}
		// 取值方式
		List<RptIdxValType> types = this.getEntityList(RptIdxValType.class);
		Map<String, RptIdxValType> typeMap = new HashMap<String, RptIdxValType>();
		for (RptIdxValType type : types) {
			typeMap.put(type.getModeId(), type);
		}
		// 获取待拷贝报表的最新全部报表
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("maxDate", MAXDATE);
		params.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		params.put("defSrc", "01");
		PropertiesUtils pUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String tntRptIds = pUtils.getProperty("tntRptIds");
		if (StringUtils.isNotEmpty(tntRptIds)) {
			List<String> paramList = new ArrayList<String>();
			String[] strs = StringUtils.split(tntRptIds, ',');
			for (String str : strs) {
				paramList.add(str);
			}
			params.put("rptIds", paramList);
		}
		@SuppressWarnings("unchecked")
		List<ReportInfoVO> copyRpts = (List<ReportInfoVO>)this.rptTmpDAO.getFrsRptsById(params);
		if (copyRpts == null || copyRpts.size() <= 0) {
			return;
		}
		for (ReportInfoVO copyRpt : copyRpts) {
			String keyWord = "日报";
			String[] copyCycles = copyCycles1;
			if (GlobalConstants4plugin.CALC_CYCLE_DAY.equals(copyRpt.getRptCycle())) {
				copyCycles = copyCycles1;
			} else if (GlobalConstants4plugin.CALC_CYCLE_MONTH.equals(copyRpt
					.getRptCycle())) {
				copyCycles = copyCycles2;
				keyWord = "月报";
			} else if (GlobalConstants4plugin.CALC_CYCLE_SEASON.equals(copyRpt
					.getRptCycle())) {
				copyCycles = copyCycles3;
				keyWord = "季报";
			}
			String copyTemplateId = copyRpt.getTemplateId();
			BigDecimal copyVersion = copyRpt.getMaxVerId();
			for (String rptCycle : copyCycles) {
				// 新的templateId
				String newTemplateId = RandomUtils.uuid2();
				// 报表基本信息
				RptMgrReportInfo rptInfo = new RptMgrReportInfo();
				BeanUtils.copy(copyRpt, rptInfo);
				Timestamp currTime = new Timestamp(new Date().getTime());
				rptInfo.setCreateTime(currTime);
				rptInfo.setRptId(RandomUtils.uuid2());
				rptInfo.setCfgId(newTemplateId);
				rptInfo.setRptCycle(rptCycle);
				String oldRptNm = copyRpt.getRptNm();
				String replaceWord = "";
				if (GlobalConstants4plugin.CALC_CYCLE_MONTH.equals(rptCycle)) {
					replaceWord = "月报";
				} else if (GlobalConstants4plugin.CALC_CYCLE_SEASON.equals(rptCycle)) {
					replaceWord = "季报";
				} else if (GlobalConstants4plugin.CALC_CYCLE_YEAR.equals(rptCycle)) {
					replaceWord = "年报";
				}
				String newNm = StringUtils.replace(oldRptNm, keyWord, replaceWord);
				rptInfo.setRptNm(newNm);
				this.baseDAO.merge(rptInfo);
				// 获取待拷贝报表最新的相关数据结构
				// 1. 设计器配置相关
				Map<String, Object> designMap = new HashMap<String, Object>();
				designMap.put("id.templateId", copyTemplateId);
				designMap.put("id.verId", copyVersion);
				for (Class<?> designEntityTmp : designEntitiesHehe) {
					List<Object> entities = entityUtils.getEntitiesJpa(
							designEntityTmp, designMap);
					if (entities != null && entities.size() > 0) {
						// 插入拷贝数据
						Map<String, Object> design4CopyMap = new HashMap<String, Object>();
						design4CopyMap.put("id.templateId", newTemplateId);
						design4CopyMap.put("id.verId", BigDecimal.ONE);
						entityUtils.copyEntityJdbcBatch(entities, design4CopyMap, null,
								null);
					}
				}
				// 2. 设计器指标关系相关
				Map<String, Object> idxRelMap = new HashMap<String, Object>();
				idxRelMap.put("id.templateId", copyTemplateId);
				idxRelMap.put("id.verId", copyVersion);
				// -- 旧指标号与新指标号对应关系
				Map<String, Object> idxNoMap = new HashMap<String, Object>();
				List<String> oldIdxs = new ArrayList<String>();
				for (Class<?> relEntityTmp : idxRelEntitiesHehe) {
					List<Object> entities = entityUtils.getEntitiesJpa(relEntityTmp,
							idxRelMap);
					if (entities != null && entities.size() > 0) {
						// 分析获取旧指标与新指标关系
						for (Object tmp : entities) {
							String oldIdxNo = (String) entityUtils.getValueByFieldExpr("indexNo", tmp);
							if (StringUtils.isNotEmpty(oldIdxNo)) {
								idxNoMap.put(oldIdxNo, RandomUtils.uuid2());
								oldIdxs.add(oldIdxNo);
							}
						}
						if (!"RptDesignSourceTabidx".equals(relEntityTmp
								.getSimpleName())) {
							// 插入拷贝数据
							Map<String, Object> rel4CopyMap = new HashMap<String, Object>();
							rel4CopyMap.put("id.templateId", newTemplateId);
							rel4CopyMap.put("id.verId", BigDecimal.ONE);
							entityUtils.copyEntityJdbcBatch(entities, rel4CopyMap,
									idxNoMap, null);
						}
					}
				}
				// 3. 指标配置相关
				Map<String, Object> idxMap = new HashMap<String, Object>();
				if (oldIdxs.size() > 0) {
					// 获取旧指标对应计算规则信息
					String jql = "select idx from RptDesignSourceTabidx idx where idx.indexNo in ?0 and idx.id.templateId = ?1 and idx.id.verId = ?2";
					List<RptDesignSourceTabidx> tabidxs = this.baseDAO
							.findWithIndexParam(jql, oldIdxs, copyTemplateId,
									copyVersion);
					Map<String, RptDesignSourceTabidx> tabidxMap = new HashMap<String, RptDesignSourceTabidx>();
					for (RptDesignSourceTabidx tabidx : tabidxs) {
						tabidxMap.put(tabidx.getIndexNo(), tabidx);
					}
					// 获取旧指标过滤公式
					String jql2 = "select filt from RptIdxFilterInfo filt where filt.id.indexNo in ?0 and filt.id.indexVerId = ?1";
					List<RptIdxFilterInfo> filters = this.baseDAO
							.findWithIndexParam(jql2, oldIdxs,
									copyVersion.longValue());
					// key-旧指标号；value-（key：维度类型标识；value：过滤信息集合）
					Map<String, Map<String, List<String>>> idxDimFiltMap = new HashMap<String, Map<String, List<String>>>();
					for (RptIdxFilterInfo filter : filters) {
						if (!idxDimFiltMap.containsKey(filter.getId()
								.getIndexNo())) {
							Map<String, List<String>> mapTmp = new HashMap<String, List<String>>();
							List<String> listTmp = new ArrayList<String>();
							listTmp.add(filter.getFilterVal());
							mapTmp.put(filter.getId().getDimNo(), listTmp);
							idxDimFiltMap.put(filter.getId().getIndexNo(),
									mapTmp);
						} else {
							Map<String, List<String>> dimMapTmp = idxDimFiltMap
									.get(filter.getId().getIndexNo());
							if (dimMapTmp
									.containsKey(filter.getId().getDimNo())) {
								dimMapTmp.get(filter.getId().getDimNo()).add(
										filter.getFilterVal());
							} else {
								List<String> listTmp = new ArrayList<String>();
								listTmp.add(filter.getFilterVal());
								dimMapTmp.put(filter.getId().getDimNo(),
										listTmp);
							}
							idxDimFiltMap.put(filter.getId().getIndexNo(),
									dimMapTmp);
						}
					}
					// 获取旧指标对应的来源指标
					String jql3 = "select idx from RptIdxInfo idx where idx.id.indexNo in ?0 and idx.id.indexVerId = ?1";
					List<RptIdxInfo> idxs = this.baseDAO.findWithIndexParam(
							jql3, oldIdxs, copyVersion.longValue());
					Map<String, String> srcIdxMap = new HashMap<String, String>();
					for (RptIdxInfo idxTmp : idxs) {
						srcIdxMap
								.put(idxTmp.getId().getIndexNo(),
										StringUtils.split(
												idxTmp.getSrcIndexNo(), ",")[0]);
					}
					idxMap.put("id.indexNo", oldIdxs);
					idxMap.put("id.indexVerId", copyVersion.longValue());
					for (Class<?> idxEntityTmp : idxEntitiesHehe) {
						List<Object> entities = entityUtils.getEntitiesJpa(
								idxEntityTmp, idxMap);
						if (entities != null && entities.size() > 0) {
							// 插入拷贝数据
							Map<String, Object> idx4CopyMap = new HashMap<String, Object>();
							idx4CopyMap.put("templateId", newTemplateId);
							idx4CopyMap.put("id.indexVerId", Long.valueOf("1"));
							idx4CopyMap.put("calcCycle", rptCycle);
							// -- 月，季，年报表均是派生指标
							idx4CopyMap.put("indexType",
									GlobalConstants4plugin.DERIVE_INDEX);
							entityUtils.copyEntityJdbcBatch(entities, idx4CopyMap,
									idxNoMap, null);
						}
					}
					// 保存公式信息
					// 这个地方，暂时不应该取非指标列表的公式
					String j = "select f from RptIdxFormulaInfo f where f.id.indexNo in ?0 and f.id.indexVerId = ?1";
					List<RptIdxFormulaInfo> fTmp = this.baseDAO
							.findWithIndexParam(j, oldIdxs,
									copyVersion.longValue());
					this.copyFormulaBeta(fTmp, rptCycle, idxNoMap, srcIdxMap,
							tabidxMap, idxDimFiltMap, dimTypeMap, ruleMap,
							timeMap, typeMap, newTemplateId);
				}
				RptDesignQueryDimPK pk = new RptDesignQueryDimPK();
				pk.setTemplateId(copyTemplateId);
				pk.setVerId(copyRpt.getMaxVerId());
				RptDesignQueryDim oldDim = this.getEntityById(
						RptDesignQueryDim.class, pk);
				if (oldDim == null
						|| StringUtils.isEmpty(oldDim.getParamTemplateId())) {
					return;
				}
				String copyParamId = oldDim.getParamTemplateId();
				String newParamId = RandomUtils.uuid2();
				// 4. 参数模板关系相关
				// paramEntities
				Map<String, Object> paramRelMap = new HashMap<String, Object>();
				paramRelMap.put("id.templateId", copyTemplateId);
				paramRelMap.put("id.verId", BigDecimal.ONE);
				for (Class<?> paramRelEntityTmp : paramRelEntitiesHehe) {
					List<Object> entities = entityUtils.getEntitiesJpa(
							paramRelEntityTmp, paramRelMap);
					if (entities != null && entities.size() > 0) {
						// 插入拷贝数据
						Map<String, Object> paramRel4CopyMap = new HashMap<String, Object>();
						paramRel4CopyMap.put("id.templateId", newTemplateId);
						paramRel4CopyMap.put("id.verId", BigDecimal.ONE);
						paramRel4CopyMap.put("paramTemplateId", newParamId);
						entityUtils.copyEntityJdbcBatch(entities, paramRel4CopyMap, null,
								null);
					}
				}
				// 5. 参数模板信息
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("paramtmpId", copyParamId);
				for (Class<?> paramEntityTmp : paramEntitiesHehe) {
					List<Object> entities = entityUtils.getEntitiesJpa(paramEntityTmp,
							paramMap);
					if (entities != null && entities.size() > 0) {
						// 插入拷贝数据
						Map<String, Object> param4CopyMap = new HashMap<String, Object>();
						param4CopyMap.put("paramtmpId", newParamId);
						List<String> randomFields = new ArrayList<String>();
						randomFields.add("paramId");
						entityUtils.copyEntityJdbcBatch(entities, param4CopyMap, null,
								randomFields);
					}
				}
			}
		}
	}

	/**
	 * 获取指标对应的维度信息
	 * 
	 * @param indexNo
	 * @param containDims
	 * @return
	 */
	public List<RptIdxDimRel> getIdxDimRels(String indexNo,
			List<String> containDims) {
		List<RptIdxDimRel> rels = new ArrayList<RptIdxDimRel>();
		if (StringUtils.isNotEmpty(indexNo)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("indexNo", indexNo);
			if (containDims != null && containDims.size() > 0) {
				params.put("containDims", containDims);
			}
			rels = this.rptTmpDAO.getIdxDimRels(params);
		}
		return rels;
	}

	/**
	 * 获取模型对应的维度信息
	 * 
	 * @param setId
	 * @param containDims
	 * @return
	 */
	public List<RptSysModuleCol> getModuleDimRels(String setId,
			List<String> containDims) {
		List<RptSysModuleCol> rels = new ArrayList<RptSysModuleCol>();
		if (StringUtils.isNotEmpty(setId)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("setId", setId);
			if (containDims != null && containDims.size() > 0) {
				params.put("containDims", containDims);
			}
			rels = this.rptTmpDAO.getModuleDimRels(params);
		}
		return rels;
	}

	
	@Transactional(readOnly = false)
	public void changeRptCatalog(String rptId, String catalogId,
			String currentCatalogId) {
		if (StringUtils.isEmpty(catalogId)) {
			return;
		}
		if (StringUtils.isNotEmpty(rptId)) {
			String jql = "update RptMgrReportInfo rpt set catalogId = ?0 where rptId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, catalogId, rptId);
		} else if (StringUtils.isNotEmpty(currentCatalogId)) {
			String jql = "update RptMgrReportInfo rpt set catalogId = ?0 where catalogId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, catalogId,
					currentCatalogId);
		}
	}
	/**
	 * 移动报表
	 * @param moveFlag
	 * @param selIds
	 * @param rankOrders
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, String> rptMove(String moveFlag,String selIds,String rankOrders) {
		Map<String, String> param = Maps.newHashMap();
		if(StringUtils.isNotEmpty(selIds) && StringUtils.isNotEmpty(rankOrders) && StringUtils.isNotEmpty(moveFlag)){
			String[] selId = StringUtils.split(selIds, ',');
			String[] rankOrder = StringUtils.split(rankOrders, ',');
			if("rptNode".equals(moveFlag)){
				for(int i=0;i<selId.length;i++){
					String jql =" update RptMgrReportInfo t set t.rankOrder =?0 where t.rptId =?1 ";
					this.baseDAO.batchExecuteWithIndexParam(jql, new BigDecimal(rankOrder[i]),selId[i]);
				}
			}
			if("catalogNode".equals(moveFlag)){
				for(int i=0;i<selId.length;i++){
					String jql =" update RptMgrReportCatalog t set t.catalogOrder =?0 where t.catalogId =?1 ";
					this.baseDAO.batchExecuteWithIndexParam(jql, new BigDecimal(rankOrder[i]),selId[i]);
				}
			}
			param.put("msg", "ok");
			return param;
		}
		param.put("msg", "error");
		return param;
	}
	
	/**
	 * 获取维度具体的层级信息
	 * 
	 * @param dimTypeNo
	 * @return
	 */
	public Map<String, Object> getDimLvlInfos(String dimTypeNo) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(dimTypeNo)) {
			RptDimTypeInfo dimInfo = this.getEntityById(RptDimTypeInfo.class,
					dimTypeNo);
			if (dimInfo != null) {
				Map<String, String> lvlMap = new HashMap<String, String>();
				returnMap.put("dimInfo", dimInfo);
				if (GlobalConstants4plugin.DIM_TYPE_ORG.equals(dimInfo.getDimType())) {
					// 机构维
					PropertiesUtils pro = PropertiesUtils.get(
							"bione-plugin/extension/report-common.properties");
					String orgflag = pro.getProperty("orglevelflag");
					if(orgflag.equals("true")){
						lvlMap.put("1", "总行");
						lvlMap.put("2", "分行");
						lvlMap.put("3", "二级分行");
						lvlMap.put("4", "支行");
						lvlMap.put("5", "网点");
					}
					else{
						String jql1 = "select org from RptOrgInfo org where org.id.orgType = ?0 order by length(org.namespace) desc";
						List<RptOrgInfo> orgInfos = this.baseDAO
								.findWithIndexParam(jql1,
										GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
						if (orgInfos != null && orgInfos.size() > 0) {
							String rootNo = "0";
							// 转换成map
							Map<String, RptOrgInfo> itemMap = new HashMap<String, RptOrgInfo>();
							for (RptOrgInfo itemTmp : orgInfos) {
								itemMap.put(itemTmp.getId().getOrgNo(), itemTmp);
							}
							Map<String, String> itemLvlMap = new HashMap<String, String>();
							Integer maxDeep = 1;
							String maxItemNo = "";
							for (RptOrgInfo itemTmp : orgInfos) {
								String namespaceTmp = getItemLvlDeep(itemTmp,
										itemMap, rootNo);
								Integer deepTmp = StringUtils.split(namespaceTmp, '/').length;
								if (deepTmp > maxDeep) {
									maxDeep = deepTmp;
									maxItemNo = itemTmp.getId().getOrgNo();
								}
								itemLvlMap.put(itemTmp.getId().getOrgNo(),
										namespaceTmp);
							}
							// 通过解析最深的item的namespace，分析每一层对应的样例名称
							String[] maxTmps = StringUtils.split(itemLvlMap.get(maxItemNo), '/');
							Integer lvl = 1;
							for (String maxTmp : maxTmps) {
								lvlMap.put(lvl + "", itemMap.get(maxTmp)
										.getOrgNm());
								lvl++;
							}
						}
					}
					
				} else if (GlobalConstants4plugin.DIM_TYPE_DATE.equals(dimInfo
						.getDimType())) {
					// 日期维
					lvlMap.put("1", "年");
					lvlMap.put("2", "半年");
					lvlMap.put("3", "季度");
					lvlMap.put("4", "月");
					lvlMap.put("5", "旬");
					lvlMap.put("6", "周");
					lvlMap.put("7", "日期");
				} else {
					// 其他维
					if (GlobalConstants4plugin.DIM_TYPE_STRUCT_LIST.equals(dimInfo
							.getDimTypeStruct())) {
						// 列表型
						lvlMap.put("1", "全部维度");
					} else {
						// 树形结构
						List<RptDimItemInfo> items = this
								.getEntityListByProperty(RptDimItemInfo.class,
										"id.dimTypeNo", dimInfo.getDimTypeNo());
						String rootNo = GlobalConstants4frame.DEFAULT_TREE_ROOT_NO;
						// 转换成map
						Map<String, RptDimItemInfo> itemMap = new HashMap<String, RptDimItemInfo>();
						for (RptDimItemInfo itemTmp : items) {
							itemMap.put(itemTmp.getId().getDimItemNo(), itemTmp);
						}
						// 获取item - namespace深度的关系
						Map<String, String> itemLvlMap = new HashMap<String, String>();
						Integer maxDeep = 1;
						String maxItemNo = "";
						for (RptDimItemInfo itemTmp : items) {
							String namespaceTmp = getItemLvlDeep(itemTmp,
									itemMap, rootNo);
							Integer deepTmp = StringUtils.split(namespaceTmp, '/').length;
							if (deepTmp > maxDeep) {
								maxDeep = deepTmp;
								maxItemNo = itemTmp.getId().getDimItemNo();
							}
							itemLvlMap.put(itemTmp.getId().getDimItemNo(),
									namespaceTmp);
						}
						// 通过解析最深的item的namespace，分析每一层对应的样例名称
						String[] maxTmps = StringUtils.split(itemLvlMap.get(maxItemNo), '/');
						Integer lvl = 1;
						for (String maxTmp : maxTmps) {
							lvlMap.put(lvl + "", itemMap.get(maxTmp)
									.getDimItemNm());
							lvl++;
						}
					}
				}
				returnMap.put("lvlMap", lvlMap);
			}
		}
		return returnMap;
	}

	public String getDeptIdByInfo(String orgNo,String deptNo){
		String jql = "select dept.deptId from BioneDeptInfo dept where dept.logicSysNo = ?0 and dept.orgNo = ?1 and dept.deptNo =?2";
		String deptId = this.baseDAO.findUniqueWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),orgNo,deptNo);
		return deptId;
	}
	
	
	/** Private Method Begin **/
	
	private String getItemLvlDeep(RptDimItemInfo item,
			Map<String, RptDimItemInfo> itemMap, String rootNo) {
		if(item != null){
			String deepNamespace = item.getId()
					.getDimItemNo();
			if(item.getUpNo().equals(rootNo)){
				return deepNamespace.toString();
			}
			else{
				return getItemLvlDeep(itemMap.get(item.getUpNo()),itemMap,rootNo)+"/"+deepNamespace;
			}
		}
		return "";
	}
	
	private String getItemLvlDeep(RptOrgInfo item,
			Map<String, RptOrgInfo> itemMap, String rootNo) {
		if(item !=null){
			String deepNamespace = item.getId().getOrgNo();
			if(item.getUpOrgNo().equals(rootNo)){
				return deepNamespace.toString();
			}
			else{
				return getItemLvlDeep(itemMap.get(item.getUpOrgNo()),itemMap,rootNo)+"/"+deepNamespace;
			}
		}
		return "";
		
	}

	private Map<String, String> saveDesignInfo(JSONObject tmpObj,
			ReportInfoVO rptInfo, String mainTmpId, String newVerStartDate) {
		long beginTime = System.currentTimeMillis();
		Map<String, String> returnMap = new HashMap<String, String>();
		JSONArray idxsTmp = tmpObj.getJSONArray("idxsArray");
		JSONArray rowColsTmp = tmpObj.getJSONArray("rowCols");
		String tmpJson = tmpObj.getString("tmpJson");
		String tmpRemark = tmpObj.getString("tmpRemark");
		JSONArray storeIdxNosTmp = null;
		if(tmpObj.containsKey("storeIdxNos")){
			storeIdxNosTmp = tmpObj.getJSONArray("storeIdxNos");
		}
		JSONArray cellsTmp = null;
		if(tmpObj.containsKey("cellsArray")){
			cellsTmp = tmpObj.getJSONArray("cellsArray");
		}
		// 查询条件相关
		JSONArray detailsTmp = null;
		if(tmpObj.containsKey("detailArr")){
			detailsTmp = tmpObj.getJSONArray("detailArr");
		}
		String publicDim = tmpObj.containsKey("publicDim") ? tmpObj
				.getString("publicDim") : null;
		String queryDim = tmpObj.containsKey("queryDim") ? tmpObj
				.getString("queryDim") : null;
		String paramJson = tmpObj.containsKey("paramJson") ? tmpObj
				.getString("paramJson") : null;
		String paramId = tmpObj.containsKey("paramId") ? tmpObj
				.getString("paramId") : null;
		rptInfo.setPublicDim(publicDim == null ? "" : publicDim);
		rptInfo.setQueryDim(queryDim == null ? "" : queryDim);
		rptInfo.setParamJson(paramJson == null ? "" : paramJson);
		rptInfo.setParamId(paramId == null ? "" : paramId);
		// 条线信息
		String tmpLineId = tmpObj.getString("lineId");
		String oldTmpId = tmpObj.getString("templateId");
		
		
		// 数据集单元格
		List<RptDesignModuleCellVO> moduleCells = new ArrayList<RptDesignModuleCellVO>();
		// excel公式单元格
		List<RptDesignFormulaCellVO> formulaCells = new ArrayList<RptDesignFormulaCellVO>();
		// 指标单元格
		List<RptDesignIdxCellVO> idxCells = new ArrayList<RptDesignIdxCellVO>();
		// 静态表达式单元格
		List<RptDesignStaticCellVO> staticCells = new ArrayList<RptDesignStaticCellVO>();
		// 表间取数单元格
		List<RptDesignIdxCalcCellVO> idxCalcCells = new ArrayList<RptDesignIdxCalcCellVO>();
		// 列表指标单元格
		List<RptDesignTabIdxVO> tabIdxCells = new ArrayList<RptDesignTabIdxVO>();
		// 列表度量单元格
		List<RptDesignTabDimVO> tabDimCells = new ArrayList<RptDesignTabDimVO>();
		// 一般单元格详情
		List<RptDesignComcellInfoVO> comCells = new ArrayList<RptDesignComcellInfoVO>();
			
		List<String> storeIdxNos = new ArrayList<String>();
		
		if(storeIdxNosTmp != null){
			for (int i = 0; i < storeIdxNosTmp.size(); i++) {
				String oTmp = storeIdxNosTmp.get(i).toString();
				storeIdxNos.add(oTmp);
			}
		}
		
		if(cellsTmp != null){
			for (int i = 0; i < cellsTmp.size(); i++) {
				RptDesignComcellInfoVO comCellTmp = cellsTmp.getObject(i, RptDesignComcellInfoVO.class);
				comCells.add(comCellTmp);
			}
		}
		
		for (int i = 0; i < idxsTmp.size(); i++) {
			JSONObject oTmp = idxsTmp.getJSONObject(i);
			String cellType = oTmp.getString("cellType");
			if (GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE.equals(cellType)) {
				RptDesignModuleCellVO moduleCellTmp = oTmp.toJavaObject(RptDesignModuleCellVO.class);
				if(!GlobalConstants4plugin.DATA_TYPE_MONEY.equals(moduleCellTmp.getDisplayFormat()) && !GlobalConstants4plugin.DATA_TYPE_VALUE.equals(moduleCellTmp.getDisplayFormat())){
					moduleCellTmp.setSumMode(GlobalConstants4plugin.DATA_SUM_NO);
				}
				moduleCells.add(moduleCellTmp);
			} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_IDX.equals(cellType)) {// 指标单元格
				RptDesignIdxCellVO idxCellTmp = oTmp.toJavaObject(RptDesignIdxCellVO.class);
				
				//直接！
				// 报表指标的业务类型，使用的就是所属报表的业务类型..
				idxCells.add(idxCellTmp);
			} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA.equals(cellType)) {// excel公式单元格
				RptDesignFormulaCellVO formulaCellTmp = oTmp.toJavaObject(RptDesignFormulaCellVO.class);
				formulaCells.add(formulaCellTmp);
			} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT
					.equals(cellType)) {
				RptDesignStaticCellVO staticCellTmp = oTmp.toJavaObject(RptDesignStaticCellVO.class);
				staticCells.add(staticCellTmp);
			} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_RPTCALC.equals(cellType)) {//表间计算单元格
				RptDesignIdxCalcCellVO idxCalcCellTmp = oTmp.toJavaObject(RptDesignIdxCalcCellVO.class);
				idxCalcCells.add(idxCalcCellTmp);
			} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB.equals(cellType)) {// 列表指标单元格
				// 指标列表 - 指标
				RptDesignTabIdxVO tabIdxCellTmp = oTmp.toJavaObject(RptDesignTabIdxVO.class);
				tabIdxCells.add(tabIdxCellTmp);
			} else if (GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB.equals(cellType)) {
				// 指标列表 - 维度
				RptDesignTabDimVO tabDimCellTmp = oTmp.toJavaObject(RptDesignTabDimVO.class);
				tabDimCells.add(tabDimCellTmp);
			}
		}
		// 行列过滤信息
		List<RptDesignBatchCfgVO> batchs = new ArrayList<RptDesignBatchCfgVO>();
		for (int i = 0; i < rowColsTmp.size(); i++) {
			RptDesignBatchCfgVO batchTmp = rowColsTmp.getObject(i, RptDesignBatchCfgVO.class);
			batchs.add(batchTmp);
		}
		// 查询明细
		List<RptDesignQueryDetailVO> details = new ArrayList<RptDesignQueryDetailVO>();
		if(detailsTmp != null){
			for (int i = 0; i < detailsTmp.size(); i++) {
				RptDesignQueryDetailVO detailTmp = detailsTmp.getObject(i, RptDesignQueryDetailVO.class);
				details.add(detailTmp);
			}
		}
		// 保存全部信息
		returnMap = this.saveFrsRptTmp(tmpJson, rptInfo,storeIdxNos, moduleCells,
				formulaCells, idxCells, staticCells, idxCalcCells, tabIdxCells,
				tabDimCells,comCells, batchs, details, tmpRemark, mainTmpId, oldTmpId,
				tmpLineId, newVerStartDate);
		logger.info("自定义报表保存,花费：" + (System.currentTimeMillis() - beginTime)
				/ 1000 + "s");
		return returnMap;
	}

	// 发布新版本 - 保存模板信息
	private void saveTmpWhenPublish(String baseObj, String tmpArr,
			String mainTmp, String newVerStartDate) {
		if (StringUtils.isEmpty(baseObj)) {
			return;
		}
		// 报表基本信息
		ReportInfoVO rptInfo = JSON.parseObject(baseObj, ReportInfoVO.class);
		if (StringUtils.isEmpty(rptInfo.getRptId())
				|| StringUtils.isEmpty(rptInfo.getTemplateId())) {
			return;
		}
		// 主模板ID
		String mainTmpId = rptInfo.getTemplateId();
		String createTime = rptInfo.getCreateTimeStr();
		if (!StringUtils.isEmpty(createTime)) {
			rptInfo.setCreateTime(new Timestamp(Long.valueOf(createTime)));
		}
		if (!StringUtils.isEmpty(mainTmp)) {
			BigDecimal newVerId = rptInfo.getVerId();
			newVerId = newVerId.add(BigDecimal.ONE);
			rptInfo.setVerId(newVerId);
			// 保存模板信息
			JSONObject mainTmpObj = JSON.parseObject(mainTmp);
			if (mainTmpObj != null) {
				// 保存主模板
				saveDesignInfo(mainTmpObj, rptInfo, mainTmpId, newVerStartDate);
				JSONArray tmpsTmp = JSON.parseArray(tmpArr);
				// 保存子模板
				for (int m = 0; m < tmpsTmp.size(); m++) {
					JSONObject tmpObj = tmpsTmp.getJSONObject(m);
					String tmpLineId = tmpObj.getString("lineId");
					if (StringUtils.isEmpty(tmpLineId)) {
						continue;
					}
					saveDesignInfo(tmpObj, rptInfo, mainTmpId, newVerStartDate);
				}
			}
		}
	}

	/**
	  * 发布新版本 - 将旧版本结束日期闭链
	 * @param templateId
	 * @param versionId 更新版本号
	 * @param lastVerEndDate 旧版本结束日期
	 * @param newVerEndDate 新版本结束日期
	 * @param templateIds
	 */
	public void publishCloseZip(String templateId, String versionId, String lastVerEndDate,
			String newVerEndDate, List<String> templateIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("templateIds", templateIds);
		params.put("verId", versionId);
		params.put("maxEndDate", lastVerEndDate);
		params.put("verEndDate", newVerEndDate);
		// 报表模板信息
		this.rptTmpDAO.updateTmpVerEndDate(params);
		// 指标信息
		this.rptTmpDAO.updateIdxVerEndDate(params);
		// 逻辑校验设置
		this.rptTmpDAO.updateLogicVerEndDate(params);
		// 警戒校验设置
		this.rptTmpDAO.updateWarnVerEndDate(params);
	}

	// 发布新版本 - 新增开链数据
	private void publishNewZip(String baseObj, String tmpArr, String mainTmp,
			String newVerStartDate, List<String> templateIds) {
		// 保存模板信息
		saveTmpWhenPublish(baseObj, tmpArr, mainTmp, newVerStartDate);
		// 保存校验信息
		saveValidWhenPublish(templateIds, newVerStartDate);
	}

	// 发布新版本 - 保存校验信息
	private void saveValidWhenPublish(List<String> templateIds,
			String newVerStartDate) {
		// 获取上一个版本的校验信息
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("verEndDate", newVerStartDate);
		params.put("templateIds", templateIds);
		List<RptValidCfgextLogic> logics = this.rptTmpDAO
				.getLogicByPublish(params);
		List<RptValidLogicRptRel> rels = this.rptTmpDAO.getRelByPublish(params);
		List<RptValidCfgextWarn> warns = this.rptTmpDAO
				.getWarnByPublish(params);
		List<RptValidWarnLevel> levels = this.rptTmpDAO
				.getLevelByPublish(params);
		// 1.处理逻辑校验信息
		// 分析出 check_id -> rel对象 关系
		Map<String, RptValidLogicRptRel> relMap = new HashMap<String, RptValidLogicRptRel>();
		for (RptValidLogicRptRel rel : rels) {
			relMap.put(rel.getId().getCheckId(), rel);
		}
		List<RptValidLogicRptRel> rel4Save = new ArrayList<RptValidLogicRptRel>();
		for (RptValidCfgextLogic logic : logics) {
			String oldId = logic.getCheckId();
			String uuidTmp = RandomUtils.uuid2();
			logic.setCheckId(uuidTmp);
			logic.setStartDate(newVerStartDate);
			logic.setEndDate(MAXDATE);
			RptValidLogicRptRel rel = relMap.get(oldId);
			rel.getId().setCheckId(uuidTmp);
			rel4Save.add(rel);
		}
		// 保存
		entityUtils.saveEntityJdbcBatch(logics);
		entityUtils.saveEntityJdbcBatch(rel4Save);
		// 2.处理警戒校验信息
		// 分析出 check_id -> 级别信息集合 关系
		Map<String, List<RptValidWarnLevel>> levelMap = new HashMap<String, List<RptValidWarnLevel>>();
		for (RptValidWarnLevel level : levels) {
			if (levelMap.containsKey(level.getId().getCheckId())) {
				levelMap.get(level.getId().getCheckId()).add(level);
			} else {
				List<RptValidWarnLevel> levelLst = new ArrayList<RptValidWarnLevel>();
				levelLst.add(level);
				levelMap.put(level.getId().getCheckId(), levelLst);
			}
		}
		List<RptValidWarnLevel> level4Save = new ArrayList<RptValidWarnLevel>();
		for (RptValidCfgextWarn warn : warns) {
			String oldId = warn.getCheckId();
			String uuidTmp = RandomUtils.uuid2();
			warn.setCheckId(uuidTmp);
			warn.setStartDate(newVerStartDate);
			warn.setEndDate(MAXDATE);
			List<RptValidWarnLevel> levelsTmp = levelMap.get(oldId);
			if (levelsTmp != null) {
				for (RptValidWarnLevel lTmp : levelsTmp) {
					lTmp.getId().setCheckId(uuidTmp);
				}
				level4Save.addAll(levelsTmp);
			}
		}
		// 保存
		entityUtils.saveEntityJdbcBatch(warns);
		entityUtils.saveEntityJdbcBatch(level4Save);
	}

	// 生成默认的指标公式信息
	private RptIdxFormulaInfo generateDefaultFormula(
			RptDesignIdxCalcCellVO calc, String verId) {
		RptIdxFormulaInfo fi = new RptIdxFormulaInfo();
		// pk begin
		RptIdxFormulaInfoPK fiPK = new RptIdxFormulaInfoPK();
		fiPK.setIndexNo(calc.getRealIndexNo());
		fiPK.setIndexVerId(Long.valueOf(verId));
		fi.setId(fiPK);
		// pk end
		fi.setFormulaContent(calc.getFormulaContent());
		fi.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_CALC);
		return fi;
	}

	private Map<String, RptDesignSourceDsVO> getModuleCells(
			List<String> moduleCellNos, String templateId, BigDecimal verId) {
		Map<String, RptDesignSourceDsVO> map = new HashMap<String, RptDesignSourceDsVO>();
		if (moduleCellNos != null && moduleCellNos.size() > 0
				&& !StringUtils.isEmpty(templateId) && verId != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			List<List<String>> cellNosParam = new ArrayList<List<String>>();
			if (moduleCellNos.size() > 1000) {
				int index = 0;
				int remain = moduleCellNos.size();
				while (remain > 1000) {
					cellNosParam
							.add(moduleCellNos.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < moduleCellNos.size()) {
					cellNosParam.add(moduleCellNos.subList(index,
							moduleCellNos.size()));
				}
			} else {
				cellNosParam.add(moduleCellNos);
			}
			params.put("cellNos", cellNosParam);
			params.put("templateId", templateId);
			params.put("verId", verId);
			List<RptDesignSourceDsVO> moduleCells = this.rptTmpDAO
					.getModuleCells(params);
			if (moduleCells != null) {
				for (RptDesignSourceDsVO tmp : moduleCells) {
					map.put(tmp.getId().getCellNo(), tmp);
				}
			}
		}
		return map;
	}

	private Map<String, RptDesignFormulaCellVO> getFormulaCells(
			List<String> formulaCellNos, String templateId, BigDecimal verId) {
		Map<String, RptDesignFormulaCellVO> map = new HashMap<String, RptDesignFormulaCellVO>();
		if (formulaCellNos != null && formulaCellNos.size() > 0
				&& !StringUtils.isEmpty(templateId) && verId != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			List<List<String>> cellNosParam = new ArrayList<List<String>>();
			if (formulaCellNos.size() > 1000) {
				int index = 0;
				int remain = formulaCellNos.size();
				while (remain > 1000) {
					cellNosParam.add(formulaCellNos
							.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < formulaCellNos.size()) {
					cellNosParam.add(formulaCellNos.subList(index,
							formulaCellNos.size()));
				}
			} else {
				cellNosParam.add(formulaCellNos);
			}
			params.put("cellNos", cellNosParam);
			params.put("templateId", templateId);
			params.put("verId", verId);
			List<RptDesignFormulaCellVO> formulaCells = this.rptTmpDAO
					.getFormulaCells(params);
			if (formulaCells != null) {
				for (RptDesignFormulaCellVO tmp : formulaCells) {
					if (tmp.getDataUnit() == null) tmp.setDataUnit("");
					map.put(tmp.getCellNo(), tmp);
				}
			}
		}
		return map;
	}

	private Map<String, RptDesignIdxCellVO> getIdxCells(
			List<String> idxCellNos, String templateId, BigDecimal verId,
			List<RptBatchViewVO> batchCfgs, String type) {
		Map<String, RptDesignIdxCellVO> map = new HashMap<String, RptDesignIdxCellVO>();
		if (idxCellNos != null && idxCellNos.size() > 0
				&& !StringUtils.isEmpty(templateId) && verId != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("isRptIdx", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			params.put("templateId", templateId);
			params.put("verId", verId);
			List<List<String>> cellNosParam = new ArrayList<List<String>>();
			if (idxCellNos.size() > 1000) {
				int index = 0;
				int remain = idxCellNos.size();
				while (remain > 1000) {
					cellNosParam.add(idxCellNos.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < idxCellNos.size()) {
					cellNosParam.add(idxCellNos.subList(index,
							idxCellNos.size()));
				}
			} else {
				cellNosParam.add(idxCellNos);
			}
			params.put("cellNos", cellNosParam);
			List<RptDesignIdxCellVO> idxs = this.rptTmpDAO
					.getIdxsByCell(params);
			if (idxs != null) {
				PropertiesUtils pUtils = PropertiesUtils.get(
						"bione-plugin/extension/report-common.properties");
				String commonMeasure = pUtils.getProperty("defaultMeasure");
				// 获取度量信息
				List<RptIdxMeasureInfo> measures = this
						.getEntityList(RptIdxMeasureInfo.class);
				Map<String, RptIdxMeasureInfo> measureMap = new HashMap<String, RptIdxMeasureInfo>();
				if (measures != null) {
					for (RptIdxMeasureInfo mTmp : measures) {
						measureMap.put(mTmp.getMeasureNo(), mTmp);
					}
				}
				// 报表指标标识集合
				List<String> rptIdxNos = new ArrayList<String>();
				// 来源指标标识集合
				List<String> srcIdxNos = new ArrayList<String>();
				// 报表指标标识+来源指标标识集合
				List<String> allIdxNos = new ArrayList<String>();
				for (RptDesignIdxCellVO cellTmp : idxs) {
					if (!rptIdxNos.contains(cellTmp.getRealIndexNo())) {
						rptIdxNos.add(cellTmp.getRealIndexNo());
					}
					if (!srcIdxNos.contains(cellTmp.getIndexNo())) {
						srcIdxNos.add(cellTmp.getIndexNo());
					}
					if (!allIdxNos.contains(cellTmp.getRealIndexNo())) {
						allIdxNos.add(cellTmp.getRealIndexNo());
					}
					if (!allIdxNos.contains(cellTmp.getIndexNo())) {
						allIdxNos.add(cellTmp.getIndexNo());
					}
				}
				// 获取指标维度关系 耗时过长 没发现引用暂时注掉
				Map<String, List<String>> dimMap = new HashMap<String, List<String>>();
				if (!"export".equals(type)) {
					if (allIdxNos.size() > 0) {
						Map<String, Object> p1 = new HashMap<String, Object>();
						List<List<String>> idxNosParam = new ArrayList<List<String>>();
						if (allIdxNos.size() > 1000) {
							int index = 0;
							int remain = allIdxNos.size();
							while (remain > 1000) {
								idxNosParam.add(allIdxNos.subList(index,
										index + 1000));
								index += 1000;
								remain -= 1000;
							}
							if (index < allIdxNos.size()) {
								idxNosParam.add(allIdxNos.subList(index,
										allIdxNos.size()));
							}
						} else {
							idxNosParam.add(allIdxNos);
						}
						p1.put("idxNos", idxNosParam);
						p1.put("verId", verId);
						List<RptIdxDimRel> dimRels = this.rptTmpDAO
								.getDimRelByVer(p1);
						if (dimRels != null) {
							for (RptIdxDimRel relTmp : dimRels) {
								if (dimMap.containsKey(relTmp.getId().getIndexNo())) {
									if (!dimMap.get(relTmp.getId().getIndexNo())
											.contains(relTmp.getId().getDimNo())) {
										dimMap.get(relTmp.getId().getIndexNo())
												.add(relTmp.getId().getDimNo());
									}
								} else {
									List<String> idList = new ArrayList<String>();
									idList.add(relTmp.getId().getDimNo());
									dimMap.put(relTmp.getId().getIndexNo(), idList);
								}
							}
						}
					}
				}
				// 获取指标过滤信息
				Map<String, List<RptDesignFilterVO>> filterMap = new HashMap<String, List<RptDesignFilterVO>>();
				if (rptIdxNos.size() > 0) {
					Map<String, Object> p2 = new HashMap<String, Object>();
					List<List<String>> idxNosParam = new ArrayList<List<String>>();
					if (rptIdxNos.size() > 1000) {
						int index = 0;
						int remain = rptIdxNos.size();
						while (remain > 1000) {
							idxNosParam.add(rptIdxNos.subList(index,
									index + 1000));
							index += 1000;
							remain -= 1000;
						}
						if (index < rptIdxNos.size()) {
							idxNosParam.add(rptIdxNos.subList(index,
									rptIdxNos.size()));
						}
					} else {
						idxNosParam.add(rptIdxNos);
					}
					p2.put("idxNos", idxNosParam);
					p2.put("verId", verId);
					List<RptDesignFilterVO> filters = this.rptTmpDAO
							.getFilterByVer(p2);
					if (filters != null) {
						for (RptDesignFilterVO fTmp : filters) {
							if (filterMap.containsKey(fTmp.getIndexNo())) {
								filterMap.get(fTmp.getIndexNo()).add(fTmp);
							} else {
								List<RptDesignFilterVO> fList = new ArrayList<RptDesignFilterVO>();
								fList.add(fTmp);
								filterMap.put(fTmp.getIndexNo(), fList);
							}
						}
					}
				}
				// 获取来源指标名称
				Map<String, RptIdxInfo> srcIdxInfos = new HashMap<String, RptIdxInfo>();
				if (srcIdxNos.size() > 0) {
					Map<String, Object> p3 = new HashMap<String, Object>();
					List<List<String>> idxNosParam = new ArrayList<List<String>>();
					if (srcIdxNos.size() > 1000) {
						int index = 0;
						int remain = srcIdxNos.size();
						while (remain > 1000) {
							idxNosParam.add(srcIdxNos.subList(index,
									index + 1000));
							index += 1000;
							remain -= 1000;
						}
						if (index < srcIdxNos.size()) {
							idxNosParam.add(srcIdxNos.subList(index,
									srcIdxNos.size()));
						}
					} else {
						idxNosParam.add(srcIdxNos);
					}
					p3.put("idxNos", idxNosParam);
					List<RptIdxInfo> idxsTmp = this.rptTmpDAO.getRptIdxs(p3);
					if (idxsTmp != null) {
						for (RptIdxInfo iTmp : idxsTmp) {
							if (!srcIdxInfos.containsKey(iTmp.getId()
									.getIndexNo())) {
								srcIdxInfos
										.put(iTmp.getId().getIndexNo(), iTmp);
							}
						}
					}
				}
				for (RptDesignIdxCellVO cTmp : idxs) {
					if (map.containsKey(cTmp.getCellNo())) {
						continue;
					}
					if (dimMap.containsKey(cTmp.getIndexNo()) && !"export".equals(type)) {
						cTmp.setAllDims(list2String(dimMap.get(cTmp
								.getIndexNo())));
					}
					if (dimMap.containsKey(cTmp.getRealIndexNo()) && !"export".equals(type)) {
						cTmp.setFactDims(list2String(dimMap.get(cTmp
								.getRealIndexNo())));
					}
					List<RptDesignFilterVO> filters = filterMap.get(cTmp
							.getRealIndexNo());
					if (filterMap.containsKey(cTmp.getRealIndexNo())
							&& filters.size() > 0) {
/*						if (batchCfgs != null && batchCfgs.size() > 0) {
							String cellNo = cTmp.getCellNo();
							Map<String, Object> mapTmp = ExcelAnalyseUtils
									.getRowColByCellPosi(cellNo);
							String rowNo = String.valueOf(mapTmp.get("row"));
							String colNo = String.valueOf(mapTmp.get("col")
									.toString());
							if (!StringUtils.isEmpty(rowNo)
									&& !StringUtils.isEmpty(colNo)) {
								// 判断需要从fiterMap中移除的dimTypeNo
								List<String> dimTypeList = new ArrayList<String>();
								Map<String, String> rowModel = new HashMap<String, String>();
								Map<String, String> colModel = new HashMap<String, String>();
								for (RptBatchViewVO tmp : batchCfgs) {
									Integer posNum = Integer.valueOf(tmp
											.getPosNum()) - 1;
									if (GlobalConstants4plugin.RPT_FILTER_MODEL_ROW
											.equals(tmp.getPosType())) {
										if (!rowNo.equals(posNum.toString())) {
											continue;
										}
										rowModel.put(tmp.getDimType(),
												tmp.getFilterMode());
									} else if (GlobalConstants4plugin.RPT_FILTER_MODEL_COL
											.equals(tmp.getPosType())) {
										if (!colNo.equals(posNum.toString())) {
											continue;
										}
										colModel.put(tmp.getDimType(),
												tmp.getFilterMode());
									} else {
										continue;
									}
								}
								Iterator<String> it1 = rowModel.keySet()
										.iterator();
								while (it1.hasNext()) {
									String key = it1.next();
									if (!colModel.containsKey(key)) {
										dimTypeList.add(key);
									} else if (rowModel.get(key).equals(
											colModel.get(key))) {
										dimTypeList.add(key);
									}
								}
								Iterator<String> it2 = colModel.keySet()
										.iterator();
								while (it2.hasNext()) {
									String key = it2.next();
									if (!dimTypeList.contains(key)) {
										dimTypeList.add(key);
									}
								}
								if (dimTypeList.size() > 0) {
									List<RptDesignFilterVO> finalLst = new ArrayList<RptDesignFilterVO>();
									for (RptDesignFilterVO fTmp : filters) {
										if (!dimTypeList.contains(fTmp
												.getDimNo())) {
											finalLst.add(fTmp);
										}
									}
									filters = finalLst;
								}
							}
						}*/
						cTmp.setFiltInfos((JSONArray)JSON.toJSON(filters));
					} else {
						cTmp.setFiltInfos(new JSONArray());
					}
					if (srcIdxInfos.containsKey(cTmp.getIndexNo())) {
						cTmp.setIndexNm(srcIdxInfos.get(cTmp.getIndexNo())
								.getIndexNm());
						/*cTmp.setIsSum(srcIdxInfos.get(cTmp.getIndexNo())
								.getIsSum());*/
						cTmp.setStatType(srcIdxInfos.get(cTmp.getIndexNo())
								.getStatType());
					}
					if (!commonMeasure.equals(cTmp.getMeasureNo())
							&& measureMap.containsKey(cTmp.getMeasureNo())) {
						// 若不是通用度量
						String nmTmp = cTmp.getIndexNm();
						cTmp.setIndexNm(nmTmp
								+ "."
								+ measureMap.get(cTmp.getMeasureNo())
										.getMeasureNm());
					}
					map.put(cTmp.getCellNo(), cTmp);
				}
			}
		}
		return map;
	}

	// 获取列表指标
	private Map<String, RptDesignTabIdxVO> getColIdxCells(
			List<String> idxCellNos, String templateId, BigDecimal verId,
			List<RptBatchViewVO> batchCfgs) {
		Map<String, RptDesignTabIdxVO> map = new HashMap<String, RptDesignTabIdxVO>();
		if (idxCellNos != null && idxCellNos.size() > 0
				&& !StringUtils.isEmpty(templateId) && verId != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("isRptIdx", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			params.put("templateId", templateId);
			params.put("verId", verId);
			List<List<String>> cellNosParam = new ArrayList<List<String>>();
			if (idxCellNos.size() > 1000) {
				int index = 0;
				int remain = idxCellNos.size();
				while (remain > 1000) {
					cellNosParam.add(idxCellNos.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < idxCellNos.size()) {
					cellNosParam.add(idxCellNos.subList(index,
							idxCellNos.size()));
				}
			} else {
				cellNosParam.add(idxCellNos);
			}
			params.put("cellNos", cellNosParam);
			List<RptDesignTabIdxVO> idxs = this.rptTmpDAO
					.getColIdxsByCell(params);
			if (idxs != null) {
				PropertiesUtils pUtils = PropertiesUtils.get(
						"bione-plugin/extension/report-common.properties");
				String commonMeasure = pUtils.getProperty("defaultMeasure");
				// 获取度量信息
				List<RptIdxMeasureInfo> measures = this
						.getEntityList(RptIdxMeasureInfo.class);
				Map<String, RptIdxMeasureInfo> measureMap = new HashMap<String, RptIdxMeasureInfo>();
				if (measures != null) {
					for (RptIdxMeasureInfo mTmp : measures) {
						measureMap.put(mTmp.getMeasureNo(), mTmp);
					}
				}
				// 报表指标标识集合
				List<String> rptIdxNos = new ArrayList<String>();
				// 来源指标标识集合
				List<String> srcIdxNos = new ArrayList<String>();
				// 报表指标标识+来源指标标识集合
				List<String> allIdxNos = new ArrayList<String>();
				for (RptDesignTabIdxVO cellTmp : idxs) {
					if (!rptIdxNos.contains(cellTmp.getRealIndexNo())) {
						rptIdxNos.add(cellTmp.getRealIndexNo());
					}
					if (!srcIdxNos.contains(cellTmp.getIndexNo())) {
						srcIdxNos.add(cellTmp.getIndexNo());
					}
					if (!allIdxNos.contains(cellTmp.getRealIndexNo())) {
						allIdxNos.add(cellTmp.getRealIndexNo());
					}
					if (!allIdxNos.contains(cellTmp.getIndexNo())) {
						allIdxNos.add(cellTmp.getIndexNo());
					}
				}
				// 获取指标维度关系
				Map<String, List<String>> dimMap = new HashMap<String, List<String>>();
				if (allIdxNos.size() > 0) {
					Map<String, Object> p1 = new HashMap<String, Object>();
					List<List<String>> idxNosParam = new ArrayList<List<String>>();
					if (allIdxNos.size() > 1000) {
						int index = 0;
						int remain = allIdxNos.size();
						while (remain > 1000) {
							idxNosParam.add(allIdxNos.subList(index,
									index + 1000));
							index += 1000;
							remain -= 1000;
						}
						if (index < allIdxNos.size()) {
							idxNosParam.add(allIdxNos.subList(index,
									allIdxNos.size()));
						}
					} else {
						idxNosParam.add(allIdxNos);
					}
					p1.put("idxNos", idxNosParam);
					p1.put("verId", verId);
					List<RptIdxDimRel> dimRels = this.rptTmpDAO
							.getDimRelByVer(p1);
					if (dimRels != null) {
						for (RptIdxDimRel relTmp : dimRels) {
							if (dimMap.containsKey(relTmp.getId().getIndexNo())) {
								if (!dimMap.get(relTmp.getId().getIndexNo())
										.contains(relTmp.getId().getDimNo())) {
									dimMap.get(relTmp.getId().getIndexNo())
											.add(relTmp.getId().getDimNo());
								}
							} else {
								List<String> idList = new ArrayList<String>();
								idList.add(relTmp.getId().getDimNo());
								dimMap.put(relTmp.getId().getIndexNo(), idList);
							}
						}
					}
				}
				// 获取指标过滤信息
				Map<String, List<RptDesignFilterVO>> filterMap = new HashMap<String, List<RptDesignFilterVO>>();
				if (rptIdxNos.size() > 0) {
					Map<String, Object> p2 = new HashMap<String, Object>();
					List<List<String>> idxNosParam = new ArrayList<List<String>>();
					if (rptIdxNos.size() > 1000) {
						int index = 0;
						int remain = rptIdxNos.size();
						while (remain > 1000) {
							idxNosParam.add(rptIdxNos.subList(index,
									index + 1000));
							index += 1000;
							remain -= 1000;
						}
						if (index < rptIdxNos.size()) {
							idxNosParam.add(rptIdxNos.subList(index,
									rptIdxNos.size()));
						}
					} else {
						idxNosParam.add(rptIdxNos);
					}
					p2.put("idxNos", idxNosParam);
					p2.put("verId", verId);
					List<RptDesignFilterVO> filters = this.rptTmpDAO
							.getFilterByVer(p2);
					if (filters != null) {
						for (RptDesignFilterVO fTmp : filters) {
							if (filterMap.containsKey(fTmp.getIndexNo())) {
								filterMap.get(fTmp.getIndexNo()).add(fTmp);
							} else {
								List<RptDesignFilterVO> fList = new ArrayList<RptDesignFilterVO>();
								fList.add(fTmp);
								filterMap.put(fTmp.getIndexNo(), fList);
							}
						}
					}
				}
				// 获取来源指标名称
				Map<String, RptIdxInfo> srcIdxInfos = new HashMap<String, RptIdxInfo>();
				if (srcIdxNos.size() > 0) {
					Map<String, Object> p3 = new HashMap<String, Object>();
					List<List<String>> idxNosParam = new ArrayList<List<String>>();
					if (srcIdxNos.size() > 1000) {
						int index = 0;
						int remain = srcIdxNos.size();
						while (remain > 1000) {
							idxNosParam.add(srcIdxNos.subList(index,
									index + 1000));
							index += 1000;
							remain -= 1000;
						}
						if (index < srcIdxNos.size()) {
							idxNosParam.add(srcIdxNos.subList(index,
									srcIdxNos.size()));
						}
					} else {
						idxNosParam.add(srcIdxNos);
					}
					p3.put("idxNos", idxNosParam);
					List<RptIdxInfo> idxsTmp = this.rptTmpDAO.getRptIdxs(p3);
					if (idxsTmp != null) {
						for (RptIdxInfo iTmp : idxsTmp) {
							if (!srcIdxInfos.containsKey(iTmp.getId()
									.getIndexNo())) {
								srcIdxInfos
										.put(iTmp.getId().getIndexNo(), iTmp);
							}
						}
					}
				}
				for (RptDesignTabIdxVO cTmp : idxs) {
					if (map.containsKey(cTmp.getCellNo())) {
						continue;
					}
					if (dimMap.containsKey(cTmp.getIndexNo())) {
						cTmp.setAllDims(list2String(dimMap.get(cTmp
								.getIndexNo())));
					}
					if (dimMap.containsKey(cTmp.getRealIndexNo())) {
						cTmp.setFactDims(list2String(dimMap.get(cTmp
								.getRealIndexNo())));
					}
					List<RptDesignFilterVO> filters = filterMap.get(cTmp
							.getRealIndexNo());
					if (filterMap.containsKey(cTmp.getRealIndexNo())
							&& filters.size() > 0) {
						if (batchCfgs != null && batchCfgs.size() > 0) {
							String cellNo = cTmp.getCellNo();
							Map<String, Object> mapTmp = ExcelAnalyseUtils
									.getRowColByCellPosi(cellNo);
							String rowNo = String.valueOf(mapTmp.get("row"));
							String colNo = String.valueOf(mapTmp.get("col")
									.toString());
							if (!StringUtils.isEmpty(rowNo)
									&& !StringUtils.isEmpty(colNo)) {
								// 判断需要从fiterMap中移除的dimTypeNo
								List<String> dimTypeList = new ArrayList<String>();
								Map<String, String> rowModel = new HashMap<String, String>();
								Map<String, String> colModel = new HashMap<String, String>();
								for (RptBatchViewVO tmp : batchCfgs) {
									Integer posNum = Integer.valueOf(tmp
											.getPosNum()) - 1;
									if (GlobalConstants4plugin.RPT_FILTER_MODEL_ROW
											.equals(tmp.getPosType())) {
										if (!rowNo.equals(posNum.toString())) {
											continue;
										}
										rowModel.put(tmp.getDimType(),
												tmp.getFilterMode());
									} else if (GlobalConstants4plugin.RPT_FILTER_MODEL_COL
											.equals(tmp.getPosType())) {
										if (!colNo.equals(posNum.toString())) {
											continue;
										}
										colModel.put(tmp.getDimType(),
												tmp.getFilterMode());
									} else {
										continue;
									}
								}
								Iterator<String> it1 = rowModel.keySet()
										.iterator();
								while (it1.hasNext()) {
									String key = it1.next();
									if (!colModel.containsKey(key)) {
										dimTypeList.add(key);
									} else if (rowModel.get(key).equals(
											colModel.get(key))) {
										dimTypeList.add(key);
									}
								}
								Iterator<String> it2 = colModel.keySet()
										.iterator();
								while (it2.hasNext()) {
									String key = it2.next();
									if (!dimTypeList.contains(key)) {
										dimTypeList.add(key);
									}
								}
								if (dimTypeList.size() > 0) {
									List<RptDesignFilterVO> finalLst = new ArrayList<RptDesignFilterVO>();
									for (RptDesignFilterVO fTmp : filters) {
										if (!dimTypeList.contains(fTmp
												.getDimNo())) {
											finalLst.add(fTmp);
										}
									}
									filters = finalLst;
								}
							}
						}
						cTmp.setFiltInfos((JSONArray)JSON.toJSON(filters));
					} else {
						cTmp.setFiltInfos(new JSONArray());
					}
					if (srcIdxInfos.containsKey(cTmp.getIndexNo())) {
						cTmp.setIndexNm(srcIdxInfos.get(cTmp.getIndexNo())
								.getIndexNm());
						cTmp.setStatType(srcIdxInfos.get(cTmp.getIndexNo())
								.getStatType());
					}
					if (!commonMeasure.equals(cTmp.getMeasureNo())
							&& measureMap.containsKey(cTmp.getMeasureNo())) {
						// 若不是通用度量
						String nmTmp = cTmp.getIndexNm();
						cTmp.setIndexNm(nmTmp
								+ "."
								+ measureMap.get(cTmp.getMeasureNo())
										.getMeasureNm());
					}
					map.put(cTmp.getCellNo(), cTmp);
				}
			}
		}
		return map;
	}

	private Map<String, RptDesignSourceText> getStaticCells(
			List<String> staticCellNos, String templateId, BigDecimal verId) {
		Map<String, RptDesignSourceText> map = new HashMap<String, RptDesignSourceText>();
		if (staticCellNos != null && staticCellNos.size() > 0
				&& !StringUtils.isEmpty(templateId) && verId != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			List<List<String>> cellNosParam = new ArrayList<List<String>>();
			if (staticCellNos.size() > 1000) {
				int index = 0;
				int remain = staticCellNos.size();
				while (remain > 1000) {
					cellNosParam
							.add(staticCellNos.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < staticCellNos.size()) {
					cellNosParam.add(staticCellNos.subList(index,
							staticCellNos.size()));
				}
			} else {
				cellNosParam.add(staticCellNos);
			}
			params.put("cellNos", cellNosParam);
			params.put("templateId", templateId);
			params.put("verId", verId);
			List<RptDesignSourceText> textCells = this.rptTmpDAO
					.getTextCells(params);
			if (textCells != null) {
				for (RptDesignSourceText tmp : textCells) {
					map.put(tmp.getId().getCellNo(), tmp);
				}
			}
		}
		return map;
	}

	// 列表维度
	private Map<String, RptDesignTabDimVO> getColDimCells(List<String> cellNos,
			String templateId, BigDecimal verId) {
		Map<String, RptDesignTabDimVO> map = new HashMap<String, RptDesignTabDimVO>();
		if (cellNos != null && cellNos.size() > 0
				&& !StringUtils.isEmpty(templateId) && verId != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			List<List<String>> cellNosParam = new ArrayList<List<String>>();
			if (cellNos.size() > 1000) {
				int index = 0;
				int remain = cellNos.size();
				while (remain > 1000) {
					cellNosParam.add(cellNos.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < cellNos.size()) {
					cellNosParam.add(cellNos.subList(index, cellNos.size()));
				}
			} else {
				cellNosParam.add(cellNos);
			}
			params.put("cellNos", cellNosParam);
			params.put("templateId", templateId);
			params.put("verId", verId);
			List<RptDesignTabDimVO> dimCells = this.rptTmpDAO
					.getColDimsByCell(params);
			if (dimCells != null) {
				for (RptDesignTabDimVO tmp : dimCells) {
					map.put(tmp.getCellNo(), tmp);
				}
			}
		}
		return map;
	}

	/**
	 * 根据报表模板id和版本id删除报表信息
	 * @param tmpId
	 * @param verId
	 */
	private void removeTmpInfos(String tmpId, BigDecimal verId) {
		if (StringUtils.isEmpty(tmpId)) {
			return;
		}
		// 报表模板信息
		Map<String, Object> param1 = new HashMap<String, Object>();
		param1.put("tmpId", tmpId);
		if (verId != null) {
			param1.put("verId", verId);
		}else {
			//逻辑检验公式删除,有版本号的时候不能删除全部校验公式
			validLogicBS.delete(null, tmpId);
			//逻辑检验公式删除,有版本号的时候不能删除全部校验公式
			validWarnBS.delete(null, tmpId);
		}
		param1.put("isRptIndex", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		// 删除度量关系
		this.rptTmpDAO.deleteIdxMeasureRel(param1);
		// 删除维度关系
		this.rptTmpDAO.deleteIdxDimRel(param1);
		// 删除血缘关系
		this.rptTmpDAO.deleteIdxSrcRel(param1);
		// 删除维度过滤
		this.rptTmpDAO.deleteIdxFilter(param1);
		// 删除公式信息
		this.rptTmpDAO.deleteIdxFormula(param1);
		// 删除指标信息
		this.rptTmpDAO.deleteIdxInfo(param1);
		// 模板信息
		this.rptTmpDAO.deleteDesignTmp(param1);
		// 单元格信息
		this.rptTmpDAO.deleteDesignCell(param1);
		// 指标批量过滤配置
		this.rptTmpDAO.deleteBatchCfg(param1);
		// 数据集来源
		this.rptTmpDAO.deleteSourceDs(param1);
		// 单元格公式来源
		this.rptTmpDAO.deleteSourceFormula(param1);
		// 文本常量来源
		this.rptTmpDAO.deleteSourceText(param1);
		// 指标来源
		this.rptTmpDAO.deleteSourceIdx(param1);
		// 列表指标来源
		this.rptTmpDAO.deleteSourceColIdx(param1);
		// 列表维度来源
		this.rptTmpDAO.deleteSourceColDim(param1);
		// 参数模板信息
		this.rptTmpDAO.deleteParamInfo(param1);
		// 参数模板属性
		this.rptTmpDAO.deleteParamAttrs(param1);
		// 报表模板查询维度
		this.rptTmpDAO.deleteQueryDim(param1);
		// 查询明细
		this.rptTmpDAO.deleteQueryDetail(param1);
		// 一般单元格属性
		this.rptTmpDAO.deleteComDetail(param1);
		//指标收藏
		this.rptTmpDAO.deleteFavInfo(param1);
		//删除指标配置（rpt_idx_cfg）
		this.rptTmpDAO.deleteRptIdxCfg(param1);
		// 判断该报表是否有下级报表 , 若有下级模板，级联删除
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentTemplateId", tmpId);
		params.put("verId", verId);
		List<RptDesignTmpInfo> childTmps = this.rptTmpDAO
				.getTmpByParams(params);
		if (childTmps != null) {
			for (RptDesignTmpInfo childTmp : childTmps) {
				this.removeTmpInfos(childTmp.getId().getTemplateId(), verId);
			}
		}
	}

	/*
	 * private List<String> getCommonDims(List<RptDesignIdxCellVO> cells,
	 * List<RptDesignIdxCalcCellVO> idxCalcCells, PropertiesUtils pUtils) {
	 * List<String> dimNos = new ArrayList<String>(); // if (cells != null) { //
	 * for (RptDesignIdxCellVO cellTmp : cells) { // String factDimStr =
	 * cellTmp.getFactDims(); // if (StringUtils.isEmpty(factDimStr) // ||
	 * StringUtils.isEmpty(cellTmp.getRowId()) // ||
	 * StringUtils.isEmpty(cellTmp.getColId()) // ||
	 * StringUtils.isEmpty(cellTmp.getCellNo()) // ||
	 * StringUtils.isEmpty(cellTmp.getIndexNo())) { // continue; // } // dimNos
	 * = this.commonDimsAnalyse(dimNos, factDimStr); // } // } // if
	 * (idxCalcCells != null) { // for (RptDesignIdxCalcCellVO calcTmp :
	 * idxCalcCells) { // String formulaDimStr = calcTmp.getFormulaDims(); // if
	 * (StringUtils.isEmpty(formulaDimStr) // ||
	 * StringUtils.isEmpty(calcTmp.getRowId()) // ||
	 * StringUtils.isEmpty(calcTmp.getColId()) // ||
	 * StringUtils.isEmpty(calcTmp.getCellNo()) // ||
	 * StringUtils.isEmpty(calcTmp.getIndexNo())) { // continue; // } // dimNos
	 * = this.commonDimsAnalyse(dimNos, formulaDimStr); // } // } //
	 * 目前直接使用固定的【机构】、【日期】、【币种】来作为报表指标公共维度 if (pUtils == null) { pUtils = new
	 * PropertiesUtils( "bione-plugin/extension/report-common.properties"); }
	 * String orgTypeNo = pUtils.getProperty("orgDimTypeNo"); String dateTypeNo
	 * = pUtils.getProperty("dateDimTypeNo"); // String currencyTypeNo =
	 * pUtils.getProperty("currencyDimTypeNo"); String indexTypeNo =
	 * pUtils.getProperty("indexDimTypeNo"); dimNos.add(orgTypeNo);
	 * dimNos.add(dateTypeNo); // dimNos.add(currencyTypeNo);
	 * dimNos.add(indexTypeNo); return dimNos; }
	 */

	/**
	 * 获取指标列表指标维度 (目前的算法是: 取查询维度和列表维度的并集)
	 * 
	 * @param frsRpt
	 *            报表对象
	 * @param dims
	 *            列表维度信息
	 * @return
	 */
	private List<String> getColIdxCommonDims(ReportInfoVO frsRpt,
			List<String> dims, PropertiesUtils pUtils) {
		List<String> returnDims = new ArrayList<String>();
		if (dims == null) {
			dims = new ArrayList<String>();
		}
		// 获取缺省指标号维度
		String indexDimNo = pUtils.getProperty("indexDimTypeNo");
		String orgDimNo = pUtils.getProperty("orgDimTypeNo");
		String dateDimNo = pUtils.getProperty("dateDimTypeNo");
		//币种
		String currencyDimTypeNo = pUtils.getProperty("currencyDimTypeNo");
		returnDims.addAll(dims);
		String queryDims = frsRpt.getQueryDim();
		if (StringUtils.isNotEmpty(queryDims)) {
			String[] queryDimArr = StringUtils.split(queryDims, ',');
			for (String queryDimTmp : queryDimArr) {
				if (!returnDims.contains(queryDimTmp)) {
					if(!queryDimTmp.equals("tempUnit"))
						returnDims.add(queryDimTmp);
				}
			}
		}
		if (!returnDims.contains(indexDimNo)) {
			returnDims.add(indexDimNo);
		}
		if (!returnDims.contains(orgDimNo)) {
			returnDims.add(orgDimNo);
		}
		if (!returnDims.contains(dateDimNo)) {
			returnDims.add(dateDimNo);
		}
		if (!returnDims.contains(currencyDimTypeNo)) {
			returnDims.add(currencyDimTypeNo);
		}
		return returnDims;
	}

//	private List<String> commonDimsAnalyse(List<String> dimNos,
//			String analyseDims) {
//		List<String> tmp = new ArrayList<String>();
//		String[] dims = analyseDims.split(",");
//		for (String factDim : dims) {
//			tmp.add(factDim);
//		}
//		if (dimNos.size() <= 0) {
//			dimNos.addAll(tmp);
//		} else {
//			List<String> dTmps = new ArrayList<String>();
//			dTmps.addAll(dimNos);
//			for (String dimTmp : dimNos) {
//				if (!tmp.contains(dimTmp)) {
//					dTmps.remove(dimTmp);
//				}
//			}
//			dimNos = dTmps;
//		}
//		return dimNos;
//	}

	private RptDesignBatchCfg getRowColFiltCfg(
			Map<String, List<RptDesignBatchCfg>> rowColMap, String dimTypeNo,
			String posNum) {
		RptDesignBatchCfg cfg = null;
		if (rowColMap != null && !StringUtils.isEmpty(dimTypeNo)
				&& !StringUtils.isEmpty(posNum)) {
			String posTmp = String.valueOf((Integer.valueOf(posNum) + 1));
			List<RptDesignBatchCfg> cfgs = rowColMap.get(posTmp);
			if (cfgs != null && cfgs.size() > 0) {
				for (RptDesignBatchCfg tmp : cfgs) {
					if (dimTypeNo.equals(tmp.getDimType())) {
						cfg = tmp;
						return cfg;
					}
				}
			}
		}
		return cfg;
	}

	private void mergeRptIdxs(String idxNo, BigDecimal verId,
			RptDesignIdxCellVO iCell, ReportInfoVO frsInfo, String factDims,
			Map<String, RptDimTypeInfo> dimTypeMap, String tmpId,
			List<RptIdxInfo> idxs4Save, List<RptIdxMeasureRel> mesureRels4Save,
			List<RptIdxDimRel> dimRels4Save,
			List<RptIdxFilterInfo> filters4Save,
			List<RptIdxFormulaInfo> idxFormulas4Save,
			List<RptIdxSrcRelInfo> srcRel4Save,
			PropertiesUtils pUtils,
			Map<String, List<RptDesignBatchCfg>> rowCfgMap,
			Map<String, List<RptDesignBatchCfg>> colCfgMap,
			Map<String, RptIdxCalcRule> calcRules,
			Map<String, RptIdxTimeMeasure> timeMeasures,
			Map<String, RptIdxValType> valTypes) {
		if (StringUtils.isEmpty(idxNo) || verId == null || iCell == null
				|| StringUtils.isEmpty(factDims)) {
			return;
		}
		if (pUtils == null) {
			pUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
		}
		String dsId = lineBS.getSetId(frsInfo.getRptlineId(), "rpt");
		if(!StringUtils.isNotBlank(dsId))
			dsId = pUtils.getProperty("rptDsId");
		// 获取指标对应的 计算规则&时间度量&取值方式 信息
		RptIdxCalcRule calcRule = calcRules.get(iCell.getRuleId());
		RptIdxTimeMeasure timeMeasure = timeMeasures.get(iCell
				.getTimeMeasureId());
		RptIdxValType valType = valTypes.get(iCell.getModeId());
		String idxType = GlobalConstants4plugin.DERIVE_INDEX;
		if ((calcRule == null || !StringUtils.isNotEmpty(calcRule
				.getRuleTemplate()))
				&& (timeMeasure == null || !StringUtils.isNotEmpty(timeMeasure
						.getMeasureTemplate()))
				&& (valType == null || !StringUtils.isNotEmpty(valType
						.getModeTemplate()))) {
			// 【计算规则】为【当日】并且【时间度量】为【无】的时候，为组合指标；else为派生指标
			idxType = GlobalConstants4plugin.COMPOSITE_INDEX;
		}
		// 指标信息表
		RptIdxInfo idxInfo = new RptIdxInfo();
		idxInfo.setIndexSts(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		// -- 指标名称：若单元格名称不为空，使用单元格名称，其次，使用单元格标识
		idxInfo.setIndexNm(StringUtils.isEmpty(iCell.getCellNm()) ? iCell
				.getCellNo() : iCell.getCellNm());
		idxInfo.setBusiNo(iCell.getBusiNo());
		if (StringUtils.isEmpty(iCell.getIndexNo())) {
			// 空指标暂被当做空指标类型保存
			idxInfo.setIndexType(GlobalConstants4plugin.EMPTY_INDEX);
		} else {
			// 其他报表指标，视具体情况
			idxInfo.setIndexType(idxType);
		}
		idxInfo.setStartDate(frsInfo.getVerStartDate());
		idxInfo.setEndDate(frsInfo.getVerEndDate());
		idxInfo.setCalcCycle(frsInfo.getRptCycle());
		if (iCell.getIndexNo() != null) {
			idxInfo.setSrcIndexNo(iCell.getIndexNo());
			idxInfo.setSrcIndexMeasure(StringUtils.isEmpty(iCell.getMeasureNo()) ? pUtils
					.getProperty("steadyMeasureNo") : iCell.getMeasureNo());
		}
		idxInfo.setTemplateId(tmpId);
		// 报表指标目前全部是不汇总
		idxInfo.setIsSum(iCell.getIsSum());
		idxInfo.setIsSave(iCell.getIsSave());
		idxInfo.setIsRptIndex(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		RptIdxInfoPK idxInfoPK = new RptIdxInfoPK();
		idxInfoPK.setIndexNo(idxNo);
		idxInfoPK.setIndexVerId(verId.longValue());
		idxInfo.setId(idxInfoPK);
		idxInfo.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
		idxInfo.setLastUptUser("");
		idxInfo.setBusiType(frsInfo.getBusiType());
		idxs4Save.add(idxInfo);
		// 指标与指标的关系
		String srcIndexNo = idxInfo.getSrcIndexNo();
		String srcMeasurexNo = idxInfo.getSrcIndexMeasure();
		if (StringUtils.isNotBlank(srcIndexNo)) {
			String[] srcIndexNos = StringUtils.split(srcIndexNo, ",");
			String[] srcMeasureNos = StringUtils.split(srcMeasurexNo, ",");
			
			for (int i = 0; i < srcIndexNos.length; i++) {
				RptIdxSrcRelInfo relinfo = new RptIdxSrcRelInfo();
				RptIdxSrcRelInfoPK id = new RptIdxSrcRelInfoPK();
				id.setIndexNo(idxInfo.getId().getIndexNo());
				id.setIndexVerId(idxInfo.getId().getIndexVerId());
				id.setSrcIndexNo(srcIndexNos[i]);
				id.setSrcMeasureNo(srcMeasureNos[i]);
				relinfo.setId(id);
				srcRel4Save.add(relinfo);
			}
		}
		// 指标与度量关系
		RptIdxMeasureRel msRel = new RptIdxMeasureRel();
		RptIdxMeasureRelPK msRelPK = new RptIdxMeasureRelPK();
		msRelPK.setMeasureNo(pUtils.getProperty("steadyMeasureNo"));
		msRelPK.setIndexNo(idxNo);
		msRelPK.setIndexVerId(verId.longValue());
		msRelPK.setDsId(dsId);
		msRel.setId(msRelPK);
		msRel.setStoreCol(pUtils.getProperty("defaultMeasure"));
		mesureRels4Save.add(msRel);
		// 指标维度关系
		String[] dims = StringUtils.split(factDims, ',');
		String orgDimNo = pUtils.getProperty("orgDimTypeNo");
		String dateDimTypeNo = pUtils.getProperty("dateDimTypeNo");
		String indexNoTypeNo = pUtils.getProperty("indexDimTypeNo");
		String currencyDimTypeNo = pUtils.getProperty("currencyDimTypeNo");
		Integer busiDimCount = 1;
		
		for (int i = 0; i < dims.length; i++) {
			if (StringUtils.isEmpty(dims[i])) {
				continue;
			}
			String storeCol = pUtils.getProperty("defaultDim");
			String dimType = GlobalConstants4plugin.DIM_TYPE_BUSI;
			if (orgDimNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("orgNo");
				dimType = GlobalConstants4plugin.DIM_TYPE_ORG;
			} else if (dateDimTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("dataDate");
				dimType = GlobalConstants4plugin.DIM_TYPE_DATE;
			} else if (indexNoTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("indexNo");
				dimType = GlobalConstants4plugin.DIM_TYPE_INDEXNO;
			}else if (currencyDimTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("currType");
				dimType = GlobalConstants4plugin.DIM_TYPE_CURRENCY;
			} else if (GlobalConstants4plugin.DIM_TYPE_BUSI.equals(dimType)) {
				storeCol += busiDimCount;
				busiDimCount++;
			}
			RptIdxDimRel dimRelTmp = new RptIdxDimRel();
			RptIdxDimRelPK dimRelPK = new RptIdxDimRelPK();
			dimRelPK.setIndexNo(idxNo);
			dimRelPK.setIndexVerId(verId.longValue());
			dimRelPK.setDimNo(dims[i]);
			dimRelPK.setDsId(dsId);
			dimRelTmp.setId(dimRelPK);
			dimRelTmp.setDimType(dimType);
			dimRelTmp.setStoreCol(storeCol);
			dimRelTmp.setOrderNum(new BigDecimal(i));
			dimRels4Save.add(dimRelTmp);
		}
		// 指标过滤信息
		JSONArray filterJson = iCell.getFiltInfos();
		Map<String, String> filterModels = new HashMap<String, String>();
		Map<String, List<String>> filterVals = new HashMap<String, List<String>>();
		String formulaContent = "";
		String isSum = iCell.getIsSum();
		if (filterJson != null) {
			// 分析单元格过滤
			for (int m = 0; m < filterJson.size(); m++) {
				RptDesignFilterVO filterTmp = filterJson.getObject(m, RptDesignFilterVO.class);
				String filterVal = filterTmp.getFilterVal();
				if (StringUtils.isEmpty(filterVal)
						|| StringUtils.isEmpty(filterTmp.getDimNo())) {
					continue;
				}
				RptDesignBatchCfg rowCfg = getRowColFiltCfg(rowCfgMap,
						filterTmp.getDimNo(), iCell.getRowId());
				RptDesignBatchCfg colCfg = getRowColFiltCfg(colCfgMap,
						filterTmp.getDimNo(), iCell.getColId());
				if (rowCfg != null
						&& colCfg != null
						&& rowCfg.getFilterMode()
								.equals(colCfg.getFilterMode())) {
					// 若行列都不为空，若过滤类型一致，有效；不一致，过滤不生效
					continue;
				} else if (rowCfg != null || colCfg != null) {
					// 存在行过滤或列过滤
					continue;
				}
				String filterModel = filterTmp.getFilterMode();
				if (!filterModels.containsKey(filterTmp.getDimNo())) {
					filterModels.put(filterTmp.getDimNo(), filterModel);
				}
				String filterValTmp = filterTmp.getFilterVal();
				List<String> filterList = new ArrayList<String>();
				String[] filterArray = StringUtils.split(filterValTmp, ',');
				for (String fTmp : filterArray) {
					if (!filterList.contains(fTmp)) {
						filterList.add(fTmp);
					}
				}
				// 构造过滤信息
				RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
				RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
				fInfoPK.setIndexNo(idxNo);
				fInfoPK.setIndexVerId(verId.longValue());
				fInfoPK.setDimNo(filterTmp.getDimNo());
				fInfoTmp.setId(fInfoPK);
				fInfoTmp.setFilterMode(filterModel);
				fInfoTmp.setFilterVal(filterValTmp);
				filters4Save.add(fInfoTmp);
				filterVals.put(filterTmp.getDimNo(), filterList);
			}
			// 分析行列过滤信息
			String row = StringUtils.isEmpty(iCell.getRowId()) ? "0" : String
					.valueOf((Integer.valueOf(iCell.getRowId()) + 1));
			String col = StringUtils.isEmpty(iCell.getColId()) ? "0" : String
					.valueOf((Integer.valueOf(iCell.getColId()) + 1));
			List<RptDesignBatchCfg> rows = rowCfgMap.get(row) == null ? new ArrayList<RptDesignBatchCfg>()
					: rowCfgMap.get(row);
			List<RptDesignBatchCfg> cols = colCfgMap.get(col) == null ? new ArrayList<RptDesignBatchCfg>()
					: colCfgMap.get(col);
			Map<String, RptDesignBatchCfg> colMap = batchList2Map(cols);

			List<String> containDimNo = new ArrayList<String>();
			List<String> notValidDimNo = new ArrayList<String>();
			// -- 分析行过滤
			for (RptDesignBatchCfg rowTmp : rows) {
				String valTmp = rowTmp.getFilterVal();
				if (StringUtils.isEmpty(valTmp)) {
					continue;
				}
				String[] vals = StringUtils.split(valTmp, ',');
				List<String> filterValsTmp = new ArrayList<String>();
				for (String vTmp : vals) {
					if (!filterValsTmp.contains(vTmp)) {
						filterValsTmp.add(vTmp);
					}
				}
				if (!colMap.containsKey(rowTmp.getDimType())) {
					// 1.若不包含列过滤
					String filterModel = rowTmp.getFilterMode();
					if (!filterModels.containsKey(rowTmp.getDimType())) {
						filterModels.put(rowTmp.getDimType(), filterModel);
					}
					containDimNo.add(rowTmp.getDimType());

					RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
					RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
					fInfoPK.setIndexNo(idxNo);
					fInfoPK.setIndexVerId(verId.longValue());
					fInfoPK.setDimNo(rowTmp.getDimType());
					fInfoTmp.setId(fInfoPK);
					fInfoTmp.setFilterMode(rowTmp.getFilterMode());
					fInfoTmp.setFilterVal(rowTmp.getFilterVal());
					filters4Save.add(fInfoTmp);
					filterVals.put(rowTmp.getDimType(), filterValsTmp);
				} else if (rowTmp.getFilterMode().equals(
						colMap.get(rowTmp.getDimType()).getFilterMode())) {
					// 2. 包含列过滤且过滤方式一致，获取过滤码值交集
					containDimNo.add(rowTmp.getDimType());
					String filterModel = rowTmp.getFilterMode();
					if (!filterModels.containsKey(rowTmp.getDimType())) {
						filterModels.put(rowTmp.getDimType(), filterModel);
					}

					String colFiltVals = colMap.get(rowTmp.getDimType())
							.getFilterMode();
					if (StringUtils.isEmpty(colFiltVals)) {
						continue;
					}
					String[] colVs = StringUtils.split(colFiltVals, ',');
					for (String cvTmp : colVs) {
						if (!filterValsTmp.contains(cvTmp)) {
							filterValsTmp.add(cvTmp);
						}
					}
					StringBuilder sb = new StringBuilder("");
					for (String tmp : filterValsTmp) {
						if (!"".equals(sb.toString())) {
							sb.append(",");
						}
						sb.append(tmp);
					}
					RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
					RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
					fInfoPK.setIndexNo(idxNo);
					fInfoPK.setIndexVerId(verId.longValue());
					fInfoPK.setDimNo(rowTmp.getDimType());
					fInfoTmp.setId(fInfoPK);
					fInfoTmp.setFilterMode(rowTmp.getFilterMode());
					fInfoTmp.setFilterVal(sb.toString());
					filters4Save.add(fInfoTmp);
					filterVals.put(rowTmp.getDimType(), filterValsTmp);
				} else {
					notValidDimNo.add(rowTmp.getDimType());
					continue;
				}
			}
			// -- 分析列过滤
			for (RptDesignBatchCfg colTmp : cols) {
				if (containDimNo.contains(colTmp.getDimType())) {
					// 行过滤中已分析的
					continue;
				}
				if (notValidDimNo.contains(colTmp.getDimType())) {
					// 行列过滤不匹配筛选掉的
					continue;
				}
				String valTmp = colTmp.getFilterVal();
				if (StringUtils.isEmpty(valTmp)) {
					continue;
				}
				String filterModel = colTmp.getFilterMode();
				if (!filterModels.containsKey(colTmp.getDimType())) {
					filterModels.put(colTmp.getDimType(), filterModel);
				}
				String[] vals = StringUtils.split(valTmp, ',');
				List<String> filterValsTmp = new ArrayList<String>();
				for (String vTmp : vals) {
					if (!filterValsTmp.contains(vTmp)) {
						filterValsTmp.add(vTmp);
					}
				}
				RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
				RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
				fInfoPK.setIndexNo(idxNo);
				fInfoPK.setIndexVerId(verId.longValue());
				fInfoPK.setDimNo(colTmp.getDimType());
				fInfoTmp.setId(fInfoPK);
				fInfoTmp.setFilterMode(colTmp.getFilterMode());
				fInfoTmp.setFilterVal(colTmp.getFilterVal());
				filters4Save.add(fInfoTmp);
				filterVals.put(colTmp.getDimType(), filterValsTmp);
			}

			formulaContent = IdxFormulaUtils.generateByDimFilter(filterModels,
					isSum, filterVals, dimTypeMap);
		}
		// 指标公式信息
		String indexNo = "";
		try {
			indexNo = StringUtils.split(idxInfo.getSrcIndexNo(), ",")[0];
			if(!idxInfo.getSrcIndexMeasure().equals("INDEX_VAL")){
				indexNo += "." + idxInfo.getSrcIndexMeasure();
			}
		} catch (Exception e) {

		}
		RptIdxFormulaInfo formulaTmp = new RptIdxFormulaInfo();
		RptIdxFormulaInfoPK formulaPK = new RptIdxFormulaInfoPK();
		formulaPK.setIndexNo(idxNo);
		formulaPK.setIndexVerId(verId.longValue());
		formulaTmp.setId(formulaPK);
		if (GlobalConstants4plugin.COMPOSITE_INDEX.equals(idxType)) {
			// 组合指标
			formulaTmp.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_FILTER);
			formulaTmp.setFormulaContent(formulaContent.toString());
		} else {
			// 基于【计算规则】&【时间度量】&【取值方式】的派生指标
			formulaTmp.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_CALC);
			formulaTmp.setFormulaContent(IdxFormulaUtils
					.generateColCalcFormula(indexNo, calcRule, timeMeasure,
							valType, formulaContent.toString()));
		}
		idxFormulas4Save.add(formulaTmp);
		// if (!StringUtils.isEmpty(formulaContent.toString())) {
		// RptIdxFormulaInfo formulaTmp = new RptIdxFormulaInfo();
		// RptIdxFormulaInfoPK formulaPK = new RptIdxFormulaInfoPK();
		// formulaPK.setIndexNo(idxNo);
		// formulaPK.setIndexVerId(verId.longValue());
		// formulaTmp.setId(formulaPK);
		// formulaTmp.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_FILTER);
		// formulaTmp.setFormulaContent(formulaContent.toString());
		// idxFormulas4Save.add(formulaTmp);
		// }
	}

	private void mergeColIdxs(String idxNo, BigDecimal verId,
			RptDesignTabIdxVO iCell, ReportInfoVO frsInfo, String factDims,
			Map<String, RptDimTypeInfo> dimTypeMap, String tmpId,
			List<RptIdxInfo> idxs4Save, List<RptIdxMeasureRel> mesureRels4Save,
			List<RptIdxDimRel> dimRels4Save,
			List<RptIdxFilterInfo> filters4Save,
			List<RptIdxFormulaInfo> idxFormulas4Save, 
			List<RptIdxSrcRelInfo> srcRel4Save,
			PropertiesUtils pUtils,
			Map<String, List<RptDesignBatchCfg>> rowCfgMap,
			Map<String, List<RptDesignBatchCfg>> colCfgMap,
			Map<String, RptIdxCalcRule> calcRules,
			Map<String, RptIdxTimeMeasure> timeMeasures,
			Map<String, RptIdxValType> valTypes) {
		if (StringUtils.isEmpty(idxNo) || verId == null || iCell == null
				|| StringUtils.isEmpty(factDims)) {
			return;
		}
		if (pUtils == null) {
			pUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
		}
		String dsId = lineBS.getSetId(frsInfo.getLineId(), "rpt");
		if(!StringUtils.isNotBlank(dsId))
			dsId = pUtils.getProperty("rptDsId");
		// 获取指标对应的 计算规则&时间度量&取值方式 信息
		RptIdxCalcRule calcRule = calcRules.get(iCell.getRuleId());
		RptIdxTimeMeasure timeMeasure = timeMeasures.get(iCell
				.getTimeMeasureId());
		RptIdxValType valType = valTypes.get(iCell.getModeId());
		String idxType = GlobalConstants4plugin.DERIVE_INDEX;
		if ((calcRule == null || !StringUtils.isNotEmpty(calcRule
				.getRuleTemplate()))
				&& (timeMeasure == null || !StringUtils.isNotEmpty(timeMeasure
						.getMeasureTemplate()))
				&& (valType == null || !StringUtils.isNotEmpty(valType
						.getModeTemplate()))) {
			// 【计算规则】为【当日】并且【时间度量】为【无】的时候，为组合指标；else为派生指标
			idxType = GlobalConstants4plugin.COMPOSITE_INDEX;
		}
		// 指标信息表
		RptIdxInfo idxInfo = new RptIdxInfo();
		idxInfo.setIndexSts(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		// -- 指标名称：若单元格名称不为空，使用单元格名称，其次，使用单元格标识
		idxInfo.setIndexNm(StringUtils.isEmpty(iCell.getCellNm()) ? iCell
				.getCellNo() : iCell.getCellNm());
		idxInfo.setBusiNo(iCell.getBusiNo());
		if (StringUtils.isEmpty(iCell.getIndexNo())) {
			// 空指标暂被当做空指标类型保存
			idxInfo.setIndexType(GlobalConstants4plugin.EMPTY_INDEX);
		} else {
			// 其他报表指标，视具体情况
			idxInfo.setIndexType(idxType);
		}
		idxInfo.setStartDate(frsInfo.getVerStartDate());
		idxInfo.setEndDate(frsInfo.getVerEndDate());
		idxInfo.setCalcCycle(frsInfo.getRptCycle());
		if (iCell.getIndexNo() != null) {
			idxInfo.setSrcIndexNo(iCell.getIndexNo());
			idxInfo.setSrcIndexMeasure(StringUtils.isEmpty(iCell.getMeasureNo()) ? pUtils
					.getProperty("steadyMeasureNo") : iCell.getMeasureNo());
		}
		idxInfo.setTemplateId(tmpId);
		// 报表指标目前全部是不汇总
		idxInfo.setIsSum(iCell.getIsSum());
		idxInfo.setIsSave(iCell.getIsSave());
		idxInfo.setIsRptIndex(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		RptIdxInfoPK idxInfoPK = new RptIdxInfoPK();
		idxInfoPK.setIndexNo(idxNo);
		idxInfoPK.setIndexVerId(verId.longValue());
		idxInfo.setId(idxInfoPK);
		idxInfo.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
		idxInfo.setLastUptUser("");
		idxInfo.setBusiType(frsInfo.getBusiType());
		idxs4Save.add(idxInfo);
		//指标与指标的关系
		String srcIndexNo = idxInfo.getSrcIndexNo();
		String srcMeasurexNo = idxInfo.getSrcIndexMeasure();
		if (StringUtils.isNotBlank(srcIndexNo)) {
			String[] srcIndexNos = StringUtils.split(srcIndexNo, ",");
			String[] srcMeasureNos = StringUtils.split(srcMeasurexNo, ",");
			for (int i = 0; i < srcIndexNos.length; i++) {
				RptIdxSrcRelInfo relinfo = new RptIdxSrcRelInfo();
				RptIdxSrcRelInfoPK id = new RptIdxSrcRelInfoPK();
				id.setIndexNo(idxInfo.getId().getIndexNo());
				id.setIndexVerId(idxInfo.getId().getIndexVerId());
				id.setSrcIndexNo(srcIndexNos[i]);
				id.setSrcMeasureNo(srcMeasureNos[i]);
				relinfo.setId(id);
				srcRel4Save.add(relinfo);
			}
		}
		// 指标与度量关系
		RptIdxMeasureRel msRel = new RptIdxMeasureRel();
		RptIdxMeasureRelPK msRelPK = new RptIdxMeasureRelPK();
		msRelPK.setMeasureNo(pUtils.getProperty("steadyMeasureNo"));
		msRelPK.setIndexNo(idxNo);
		msRelPK.setIndexVerId(verId.longValue());
		// 列表指标目前存储在 指标事实表 中
		msRelPK.setDsId(dsId);
		msRel.setId(msRelPK);
		msRel.setStoreCol(pUtils.getProperty("defaultMeasure"));
		mesureRels4Save.add(msRel);
		// 指标维度关系
		String[] dims = StringUtils.split(factDims, ',');
		String orgDimNo = pUtils.getProperty("orgDimTypeNo");
		String dateDimTypeNo = pUtils.getProperty("dateDimTypeNo");
		String indexNoTypeNo = pUtils.getProperty("indexDimTypeNo");
		Integer busiDimCount = 1;
		for (int i = 0; i < dims.length; i++) {
			if (StringUtils.isEmpty(dims[i])) {
				continue;
			}
			String storeCol = pUtils.getProperty("defaultDim");
			String dimType = GlobalConstants4plugin.DIM_TYPE_BUSI;
			if (orgDimNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("orgNo");
				dimType = GlobalConstants4plugin.DIM_TYPE_ORG;
			} else if (dateDimTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("dataDate");
				dimType = GlobalConstants4plugin.DIM_TYPE_DATE;
			} else if (indexNoTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("indexNo");
				dimType = GlobalConstants4plugin.DIM_TYPE_INDEXNO;
			} else if (GlobalConstants4plugin.DIM_TYPE_BUSI.equals(dimType)) {
				storeCol = storeCol + busiDimCount + "";
				busiDimCount++;
			}
			RptIdxDimRel dimRelTmp = new RptIdxDimRel();
			RptIdxDimRelPK dimRelPK = new RptIdxDimRelPK();
			dimRelPK.setIndexNo(idxNo);
			dimRelPK.setIndexVerId(verId.longValue());
			dimRelPK.setDimNo(dims[i]);
			// 列表指标目前存储在 指标事实表 中
			dimRelPK.setDsId(dsId);
			dimRelTmp.setId(dimRelPK);
			dimRelTmp.setDimType(dimType);
			dimRelTmp.setStoreCol(storeCol);
			dimRelTmp.setOrderNum(new BigDecimal(i));
			dimRels4Save.add(dimRelTmp);
		}
		// 指标过滤信息
		JSONArray filterJson = iCell.getFiltInfos();
		Map<String, String> filterModels = new HashMap<String, String>();
		Map<String, List<String>> filterVals = new HashMap<String, List<String>>();
		String formulaContent = "";
		String isSum = iCell.getIsSum();
		if (filterJson != null) {
			// 分析单元格过滤
			for (int m = 0; m < filterJson.size(); m++) {
				RptDesignFilterVO filterTmp = filterJson.getObject(m, RptDesignFilterVO.class);
				String filterVal = filterTmp.getFilterVal();
				if (StringUtils.isEmpty(filterVal)
						|| StringUtils.isEmpty(filterTmp.getDimNo())) {
					continue;
				}
				RptDesignBatchCfg rowCfg = getRowColFiltCfg(rowCfgMap,
						filterTmp.getDimNo(), iCell.getRowId());
				RptDesignBatchCfg colCfg = getRowColFiltCfg(colCfgMap,
						filterTmp.getDimNo(), iCell.getColId());
				if (rowCfg != null
						&& colCfg != null
						&& rowCfg.getFilterMode()
								.equals(colCfg.getFilterMode())) {
					// 若行列都不为空，若过滤类型一致，有效；不一致，过滤不生效
					continue;
				} else if (rowCfg != null || colCfg != null) {
					// 存在行过滤或列过滤
					continue;
				}
				String filterModel = filterTmp.getFilterMode();
				if (!filterModels.containsKey(filterTmp.getDimNo())) {
					filterModels.put(filterTmp.getDimNo(), filterModel);
				}
				String filterValTmp = filterTmp.getFilterVal();
				List<String> filterList = new ArrayList<String>();
				String[] filterArray = StringUtils.split(filterValTmp, ',');
				for (String fTmp : filterArray) {
					if (!filterList.contains(fTmp)) {
						filterList.add(fTmp);
					}
				}
				// 构造过滤信息
				RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
				RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
				fInfoPK.setIndexNo(idxNo);
				fInfoPK.setIndexVerId(verId.longValue());
				fInfoPK.setDimNo(filterTmp.getDimNo());
				fInfoTmp.setId(fInfoPK);
				fInfoTmp.setFilterMode(filterModel);
				fInfoTmp.setFilterVal(filterValTmp);
				filters4Save.add(fInfoTmp);
				filterVals.put(filterTmp.getDimNo(), filterList);
			}
			// 分析行列过滤信息
			String row = StringUtils.isEmpty(iCell.getRowId()) ? "0" : String
					.valueOf((Integer.valueOf(iCell.getRowId()) + 1));
			String col = StringUtils.isEmpty(iCell.getColId()) ? "0" : String
					.valueOf((Integer.valueOf(iCell.getColId()) + 1));
			List<RptDesignBatchCfg> rows = rowCfgMap.get(row) == null ? new ArrayList<RptDesignBatchCfg>()
					: rowCfgMap.get(row);
			List<RptDesignBatchCfg> cols = colCfgMap.get(col) == null ? new ArrayList<RptDesignBatchCfg>()
					: colCfgMap.get(col);
			Map<String, RptDesignBatchCfg> colMap = batchList2Map(cols);

			List<String> containDimNo = new ArrayList<String>();
			List<String> notValidDimNo = new ArrayList<String>();
			// -- 分析行过滤
			for (RptDesignBatchCfg rowTmp : rows) {
				String valTmp = rowTmp.getFilterVal();
				if (StringUtils.isEmpty(valTmp)) {
					continue;
				}
				String[] vals = StringUtils.split(valTmp, ',');
				List<String> filterValsTmp = new ArrayList<String>();
				for (String vTmp : vals) {
					if (!filterValsTmp.contains(vTmp)) {
						filterValsTmp.add(vTmp);
					}
				}
				if (!colMap.containsKey(rowTmp.getDimType())) {
					// 1.若不包含列过滤
					String filterModel = rowTmp.getFilterMode();
					if (!filterModels.containsKey(rowTmp.getDimType())) {
						filterModels.put(rowTmp.getDimType(), filterModel);
					}
					containDimNo.add(rowTmp.getDimType());

					RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
					RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
					fInfoPK.setIndexNo(idxNo);
					fInfoPK.setIndexVerId(verId.longValue());
					fInfoPK.setDimNo(rowTmp.getDimType());
					fInfoTmp.setId(fInfoPK);
					fInfoTmp.setFilterMode(rowTmp.getFilterMode());
					fInfoTmp.setFilterVal(rowTmp.getFilterVal());
					filters4Save.add(fInfoTmp);
					filterVals.put(rowTmp.getDimType(), filterValsTmp);
				} else if (rowTmp.getFilterMode().equals(
						colMap.get(rowTmp.getDimType()).getFilterMode())) {
					// 2. 包含列过滤且过滤方式一致，获取过滤码值交集
					containDimNo.add(rowTmp.getDimType());
					String filterModel = rowTmp.getFilterMode();
					if (!filterModels.containsKey(rowTmp.getDimType())) {
						filterModels.put(rowTmp.getDimType(), filterModel);
					}

					String colFiltVals = colMap.get(rowTmp.getDimType())
							.getFilterMode();
					if (StringUtils.isEmpty(colFiltVals)) {
						continue;
					}
					String[] colVs = StringUtils.split(colFiltVals, ',');
					for (String cvTmp : colVs) {
						if (!filterValsTmp.contains(cvTmp)) {
							filterValsTmp.add(cvTmp);
						}
					}
					StringBuilder sb = new StringBuilder("");
					for (String tmp : filterValsTmp) {
						if (!"".equals(sb.toString())) {
							sb.append(",");
						}
						sb.append(tmp);
					}
					RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
					RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
					fInfoPK.setIndexNo(idxNo);
					fInfoPK.setIndexVerId(verId.longValue());
					fInfoPK.setDimNo(rowTmp.getDimType());
					fInfoTmp.setId(fInfoPK);
					fInfoTmp.setFilterMode(rowTmp.getFilterMode());
					fInfoTmp.setFilterVal(sb.toString());
					filters4Save.add(fInfoTmp);
					filterVals.put(rowTmp.getDimType(), filterValsTmp);
				} else {
					notValidDimNo.add(rowTmp.getDimType());
					continue;
				}
			}
			// -- 分析列过滤
			for (RptDesignBatchCfg colTmp : cols) {
				if (containDimNo.contains(colTmp.getDimType())) {
					// 行过滤中已分析的
					continue;
				}
				if (notValidDimNo.contains(colTmp.getDimType())) {
					// 行列过滤不匹配筛选掉的
					continue;
				}
				String valTmp = colTmp.getFilterVal();
				if (StringUtils.isEmpty(valTmp)) {
					continue;
				}
				String filterModel = colTmp.getFilterMode();
				if (!filterModels.containsKey(colTmp.getDimType())) {
					filterModels.put(colTmp.getDimType(), filterModel);
				}
				String[] vals = StringUtils.split(valTmp, ',');
				List<String> filterValsTmp = new ArrayList<String>();
				for (String vTmp : vals) {
					if (!filterValsTmp.contains(vTmp)) {
						filterValsTmp.add(vTmp);
					}
				}
				RptIdxFilterInfo fInfoTmp = new RptIdxFilterInfo();
				RptIdxFilterInfoPK fInfoPK = new RptIdxFilterInfoPK();
				fInfoPK.setIndexNo(idxNo);
				fInfoPK.setIndexVerId(verId.longValue());
				fInfoPK.setDimNo(colTmp.getDimType());
				fInfoTmp.setId(fInfoPK);
				fInfoTmp.setFilterMode(colTmp.getFilterMode());
				fInfoTmp.setFilterVal(colTmp.getFilterVal());
				filters4Save.add(fInfoTmp);
				filterVals.put(colTmp.getDimType(), filterValsTmp);
			}

			formulaContent = IdxFormulaUtils.generateByDimFilter(filterModels,
					isSum, filterVals, dimTypeMap);
		}
		String indexNo = "";
		try {
			indexNo = StringUtils.split(idxInfo.getSrcIndexNo(), ",")[0];
			if(!idxInfo.getSrcIndexMeasure().equals("INDEX_VAL")){
				indexNo += "." + idxInfo.getSrcIndexMeasure();
			}
		} catch (Exception e) {

		}
		// 指标公式信息
		RptIdxFormulaInfo formulaTmp = new RptIdxFormulaInfo();
		RptIdxFormulaInfoPK formulaPK = new RptIdxFormulaInfoPK();
		formulaPK.setIndexNo(idxNo);
		formulaPK.setIndexVerId(verId.longValue());
		formulaTmp.setId(formulaPK);
		if (GlobalConstants4plugin.COMPOSITE_INDEX.equals(idxType)) {
			// 组合指标
			formulaTmp.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_FILTER);
			formulaTmp.setFormulaContent(formulaContent.toString());
		} else {
			// 基于【计算规则】&【时间度量】&【取值方式】的派生指标
			formulaTmp.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_CALC);
			formulaTmp.setFormulaContent(IdxFormulaUtils
					.generateColCalcFormula(indexNo, calcRule, timeMeasure,
							valType, formulaContent.toString()));
		}
		idxFormulas4Save.add(formulaTmp);
	}

	private void mergeRptCalcIdxs(String idxNo, BigDecimal verId,
			RptDesignIdxCalcCellVO cCell, ReportInfoVO frsInfo,
			String factDims, String tmpId, List<RptIdxInfo> idxs4Save,
			List<RptIdxMeasureRel> mesureRels4Save,
			List<RptIdxDimRel> dimRels4Save,
			List<RptIdxFilterInfo> filters4Save,
			List<RptIdxFormulaInfo> idxFormulas4Save, PropertiesUtils pUtils) {
		String dsId = lineBS.getSetId(frsInfo.getRptlineId(), "rpt");
		if(!StringUtils.isNotBlank(dsId))
			dsId = pUtils.getProperty("rptDsId");
		if (StringUtils.isEmpty(idxNo) || verId == null || cCell == null
				|| StringUtils.isEmpty(factDims)) {
			return;
		}
		if (pUtils == null) {
			pUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
		}
		String idxType = GlobalConstants4plugin.DERIVE_INDEX;
		if (StringUtils.isEmpty(cCell.getFormulaContent())
				|| StringUtils.isEmpty(cCell.getIndexNo())) {
			// 是空指标
			idxType = GlobalConstants4plugin.EMPTY_INDEX;
		}
		// 指标信息表
		RptIdxInfo idxInfo = new RptIdxInfo();
		idxInfo.setIndexSts(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		idxInfo.setIndexNm(cCell.getCellNo());
		idxInfo.setIndexType(idxType);
		idxInfo.setStartDate(frsInfo.getVerStartDate());
		idxInfo.setEndDate(frsInfo.getVerEndDate());
		idxInfo.setCalcCycle(frsInfo.getRptCycle());
		idxInfo.setSrcIndexNo(cCell.getIndexNo());
		idxInfo.setSrcIndexMeasure(pUtils.getProperty("steadyMeasureNo"));
		idxInfo.setTemplateId(tmpId);
		// 报表指标目前全部是不汇总
		idxInfo.setIsSum(StringUtils.isNotBlank(cCell.getIsSum()) ? cCell.getIsSum() : GlobalConstants4plugin.COMMON_BOOLEAN_NO);
		idxInfo.setIsRptIndex(GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		RptIdxInfoPK idxInfoPK = new RptIdxInfoPK();
		idxInfoPK.setIndexNo(idxNo);
		idxInfoPK.setIndexVerId(verId.longValue());
		idxInfo.setId(idxInfoPK);
		idxInfo.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
		idxInfo.setLastUptUser("");
		idxInfo.setBusiType(frsInfo.getBusiType());
		idxs4Save.add(idxInfo);
		// 指标与度量关系
		RptIdxMeasureRel msRel = new RptIdxMeasureRel();
		RptIdxMeasureRelPK msRelPK = new RptIdxMeasureRelPK();
		msRelPK.setMeasureNo(pUtils.getProperty("steadyMeasureNo"));
		msRelPK.setIndexNo(idxNo);
		msRelPK.setIndexVerId(verId.longValue());
		msRelPK.setDsId(dsId);
		msRel.setId(msRelPK);
		msRel.setStoreCol(pUtils.getProperty("defaultMeasure"));
		mesureRels4Save.add(msRel);
		// 指标维度关系
		String[] dims = StringUtils.split(factDims, ',');
		String orgDimNo = pUtils.getProperty("orgDimTypeNo");
		String dateDimTypeNo = pUtils.getProperty("dateDimTypeNo");
		String currencyDimTypeNo = pUtils.getProperty("currencyDimTypeNo");
		String indexNoTypeNo = pUtils.getProperty("indexDimTypeNo");
		Integer busiDimCount = 1;
		for (int i = 0; i < dims.length; i++) {
			if (StringUtils.isEmpty(dims[i])) {
				continue;
			}
			String storeCol = pUtils.getProperty("defaultDim");
			String dimType = GlobalConstants4plugin.DIM_TYPE_BUSI;
			if (orgDimNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("orgNo");
				dimType = GlobalConstants4plugin.DIM_TYPE_ORG;
			} else if (dateDimTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("dataDate");
				dimType = GlobalConstants4plugin.DIM_TYPE_DATE;
			} else if (currencyDimTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("currType");
				dimType = GlobalConstants4plugin.DIM_TYPE_CURRENCY;
			} else if (indexNoTypeNo.equals(dims[i])) {
				storeCol = pUtils.getProperty("indexNo");
				dimType = GlobalConstants4plugin.DIM_TYPE_INDEXNO;
			} else if (GlobalConstants4plugin.DIM_TYPE_BUSI.equals(dimType)) {
				storeCol += busiDimCount;
				busiDimCount++;
			}
			RptIdxDimRel dimRelTmp = new RptIdxDimRel();
			RptIdxDimRelPK dimRelPK = new RptIdxDimRelPK();
			dimRelPK.setIndexNo(idxNo);
			dimRelPK.setIndexVerId(verId.longValue());
			dimRelPK.setDimNo(dims[i]);
			dimRelPK.setDsId(dsId);
			dimRelTmp.setId(dimRelPK);
			dimRelTmp.setDimType(dimType);
			dimRelTmp.setStoreCol(storeCol);
			dimRelTmp.setOrderNum(new BigDecimal(i));
			dimRels4Save.add(dimRelTmp);
		}
		// 指标过滤信息，表间取数不维护
		// 指标公式信息
		if (!GlobalConstants4plugin.EMPTY_INDEX.equals(idxType)) {
			RptIdxFormulaInfo formula = new RptIdxFormulaInfo();
			RptIdxFormulaInfoPK formulaPK = new RptIdxFormulaInfoPK();
			formulaPK.setIndexNo(idxNo);
			formulaPK.setIndexVerId(verId.longValue());
			formula.setId(formulaPK);
			formula.setFormulaDesc(cCell.getFormulaDesc());
			formula.setFormulaContent(cCell.getFormulaContent());
			formula.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_CALC);
			idxFormulas4Save.add(formula);
		}
	}

	private String list2String(List<String> list) {
		StringBuilder returnStr = new StringBuilder();
		if (list != null) {
			for (String strTmp : list) {
				if (StringUtils.isEmpty(strTmp)) {
					continue;
				}
				if (!"".equals(returnStr.toString())) {
					returnStr.append(",");
				}
				returnStr.append(strTmp);
			}
		}
		return returnStr.toString();
	}



	private void copyFormulaBeta(List<RptIdxFormulaInfo> uptObjs,
			String rptCycle, Map<String, Object> idxNoMap,
			Map<String, String> srcIdxMap,
			Map<String, RptDesignSourceTabidx> tabidxMap,
			Map<String, Map<String, List<String>>> idxDimFiltMap,
			Map<String, RptDimTypeInfo> dimTypeMap,
			Map<String, RptIdxCalcRule> ruleMap,
			Map<String, RptIdxTimeMeasure> timeMap,
			Map<String, RptIdxValType> typeMap, String newTemplateId) {
		String uptSql = "insert into rpt_idx_formula_info(index_no , index_ver_id , formula_type , formula_content) values(?,?,?,?) ";
		String uptSql2 = "insert into rpt_design_source_tabidx(cell_no , template_id , ver_id , index_no , time_measure_id , rule_id , mode_id,is_passyear)values(?,?,?,?,?,?,?,?)";
		List<Object[]> updateParams = new ArrayList<Object[]>();
		List<Object[]> updateParams2 = new ArrayList<Object[]>();
		for (RptIdxFormulaInfo tmp : uptObjs) {
			String newIdxNo = (String) idxNoMap.get(tmp.getId().getIndexNo());
			Object[] o = new Object[4];
			o[0] = newIdxNo;
			o[1] = Long.valueOf("1");
			o[2] = "02";
			String filterContent = IdxFormulaUtils.generateByDimFilter(null,
					null, idxDimFiltMap.get(tmp.getId().getIndexNo()),
					dimTypeMap);
			RptDesignSourceTabidx oldIdxInfo = tabidxMap.get(tmp.getId()
					.getIndexNo());

			RptIdxCalcRule rule = null;
			RptIdxTimeMeasure time = null;
			RptIdxValType type = typeMap.get(oldIdxInfo.getModeId());

			if (GlobalConstants4plugin.CALC_CYCLE_MONTH.equals(rptCycle)) {
				rule = ruleMap.get("2");
				if ("2".equals(oldIdxInfo.getTimeMeasureId())) {
					time = timeMap.get("3");
				} else {
					time = timeMap.get(oldIdxInfo.getTimeMeasureId());
				}
			} else if (GlobalConstants4plugin.CALC_CYCLE_SEASON.equals(rptCycle)) {
				if ("2".equals(oldIdxInfo.getRuleId())) {
					rule = ruleMap.get("3");
				} else if ("5".equals(oldIdxInfo.getRuleId())) {
					rule = ruleMap.get("6");
				} else {
					rule = ruleMap.get("3");
				}
				if ("1".equals(oldIdxInfo.getTimeMeasureId())
						|| "6".equals(oldIdxInfo.getTimeMeasureId())) {
					time = timeMap.get(oldIdxInfo.getTimeMeasureId());
				} else {
					time = timeMap.get("4");
				}
			} else if (GlobalConstants4plugin.CALC_CYCLE_YEAR.equals(rptCycle)) {
				if ("2".equals(oldIdxInfo.getRuleId())
						|| "3".equals(oldIdxInfo.getRuleId())) {
					rule = ruleMap.get("4");
				} else if ("5".equals(oldIdxInfo.getRuleId())
						|| "6".equals(oldIdxInfo.getRuleId())) {
					rule = ruleMap.get("7");
				} else {
					rule = ruleMap.get("4");
				}
				if ("1".equals(oldIdxInfo.getTimeMeasureId())
						|| "6".equals(oldIdxInfo.getTimeMeasureId())) {
					time = timeMap.get(oldIdxInfo.getTimeMeasureId());
				} else {
					time = timeMap.get("5");
				}
			}

			// 保存列表指标
			RptDesignSourceTabidx tmp2 = tabidxMap
					.get(tmp.getId().getIndexNo());
			Object[] o2 = new Object[7];
			o2[0] = tmp2.getId().getCellNo();
			o2[1] = newTemplateId;
			o2[2] = BigDecimal.ONE;
			o2[3] = newIdxNo;
			o2[4] = time.getTimeMeasureId();
			o2[5] = rule.getRuleId();
			o2[6] = type.getModeId();
			o2[7] = tmp2.getIsPassyear();
			updateParams2.add(o2);

			String formulaContent = IdxFormulaUtils.generateColCalcFormula(
					srcIdxMap.get(tmp.getId().getIndexNo()), rule, time, type,
					filterContent);
			o[3] = formulaContent;
			updateParams.add(o);
		}
		this.jdbcBaseDAO.batchUpdate(uptSql.toString(), updateParams, 1000);
		this.jdbcBaseDAO.batchUpdate(uptSql2.toString(), updateParams2, 1000);
	}

	

	// 保存报表查询条件相关
	private void saveQueryDim(ReportInfoVO frsRpt) {
		if (StringUtils.isEmpty(frsRpt.getParamJson())) {
			return;
		}
		RptParamtmpInfo info = new RptParamtmpInfo();
		if (StringUtils.isEmpty(frsRpt.getParamId())) {
			frsRpt.setParamId(RandomUtils.uuid2());
		}
		info.setParamtmpId(frsRpt.getParamId());
		info.setCatalogId("1");
		info.setParamtmpNm("test");
		info.setTemplateType("custom");
		this.paramTempBS.save(info, frsRpt.getParamJson());
		// 保存模板查询条件相关
		RptDesignQueryDim queryDim = new RptDesignQueryDim();
		RptDesignQueryDimPK queryDimPK = new RptDesignQueryDimPK();
		queryDimPK.setTemplateId(frsRpt.getTemplateId());
		queryDimPK.setVerId(frsRpt.getVerId());
		queryDim.setId(queryDimPK);
		queryDim.setPublicDim(frsRpt.getPublicDim());
		queryDim.setQueryDim(frsRpt.getQueryDim());
		queryDim.setParamTemplateId(frsRpt.getParamId());
		this.baseDAO.merge(queryDim);
	}
	
	private Map<String, RptDesignBatchCfg> batchList2Map(
			List<RptDesignBatchCfg> list) {
		Map<String, RptDesignBatchCfg> returnMap = new HashMap<String, RptDesignBatchCfg>();
		for (RptDesignBatchCfg tmp : list) {
			returnMap.put(tmp.getDimType(), tmp);
		}
		return returnMap;
	}
	
	/**
	 * 根据模板id和版本号，删除对应版本的报表模板。并返回当前版本的启用日期
	 * @param templateId
	 * @param verId
	 * @return
	 */
	@Transactional(readOnly = false)
	public void cascadeRptInfoVelDel(String templateId, String verId) {
		Map<String, Object> param = Maps.newHashMap();
		if(StringUtils.isNotBlank(templateId) && StringUtils.isNotBlank(verId)) {
			BigDecimal verNum = new BigDecimal(verId);
			param.put("templateId", templateId);
			param.put("verId", verNum);
			String jql = "select tmp from RptDesignTmpInfo tmp where tmp.id.templateId = :templateId and tmp.id.verId = :verId";
			RptDesignTmpInfo tmpInfo = this.baseDAO.findUniqueWithNameParam(jql, param); 
			if(null != tmpInfo) {
				//删除模板相关信息
				this.removeTmpInfos(templateId, verNum);
				//删除对应的逻辑检验公式
				validLogicBS.deleteByVer(templateId, tmpInfo.getVerEndDate());
				//删除对应的警戒值检验公式
				validWarnBS.deleteByVer(templateId, tmpInfo.getVerEndDate());
				this.cascadeUptVer(templateId, verId, tmpInfo.getVerStartDate(), tmpInfo.getVerEndDate());
			}
		}
	}
	
	/**
	 * 根据模板id和版本号，和启用、停用日期，更新对应版本的报表模板和指标和检验公式
	 * @param templateId
	 * @param verId 删除版本ID
	 * @param startDate 删除版本开始日期
	 * @param endDate 删除版本结束日期
	 */
	@Transactional(readOnly = false)
	public void cascadeUptVer(String templateId, String verId, String startDate, String endDate) {
		//报表需要修改的相关表
		BigDecimal verNum = new BigDecimal(verId);//删除版本号
		String jql = "select info from RptDesignTmpInfo info where info.id.templateId = ?0 order by info.id.verId desc";
		List<RptDesignTmpInfo> infos = this.baseDAO.findWithIndexParam(jql, templateId);
		if (infos != null && infos.size() > 0) {
			//更新报表相关表结构
/*			String[] tmpEns= {"RptDesignBatchCfg","RptDesignCellInfo", "RptDesignComcellInfo", "RptDesignFavInfo", "RptDesignQueryDetail","RptDesignQueryDim","RptDesignSourceDs","RptDesignSourceFormula","RptDesignSourceIdx","RptDesignSourceTabdim","RptDesignSourceTabidx","RptDesignSourceText","RptDesignTmpInfo"};
  			for (RptDesignTmpInfo info : infos) {
				BigDecimal verNew = info.getId().getVerId();
				if (verNew.compareTo(verNum) > 0) {
					for (int i = 0; i < tmpEns.length; i++) {
						this.updateTmpVer(templateId, tmpEns[i], verNum, info, startDate);
					}
				}
			}*/
			
			RptDesignTmpInfo info = infos.get(0);//取当前最大的版本
			BigDecimal verNew = info.getId().getVerId();
			if((verNew.compareTo(verNum) < 0)) {//校验删除版本是否比目前库里最大版本要大
				Map<String,Object> params = new HashMap<String, Object>();
				jql = "update RptDesignTmpInfo info set info.verEndDate = :endDate where info.id.templateId = :templateId and info.id.verId = :verId";
				params.put("endDate", endDate);
				params.put("templateId", templateId);
				params.put("verId", verNew);
				this.baseDAO.batchExecuteWithNameParam(jql, params);//更新上个版本的结束日期
			}
			//更新指标相关表结构
			this.updateIdxVer(templateId, verNew, endDate);
			//更新检验公式相关表结构
			this.updateValidVer(templateId, startDate, endDate);
		}
	}
	
	/**
	 * 更新报表相关表结构(因为不涉及到中间版本的删除，所以暂时无用)
	 * @param templateId
	 * @param tbName
	 * @param verId
	 * @param info
	 * @param startDate
	 */
	@SuppressWarnings("unused")
	private void updateTmpVer(String templateId, String tbName, BigDecimal verId, RptDesignTmpInfo info, String startDate){
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "update "+tbName+" info set info.id.verId = :newindexVerId";
		BigDecimal verNew = info.getId().getVerId();
		BigDecimal verDifference = verNew.subtract(verId);
		BigDecimal one = new BigDecimal(1);
		if((verDifference.compareTo(one) == 0) && tbName.equals("RptDesignTmpInfo")){
			jql +=",info.verStartDate = :startDate";
			params.put("startDate", startDate);
		}
		jql += " where info.id.templateId = :templateId and info.id.verId = :verId";
		params.put("templateId", templateId);
		params.put("verId", info.getId().getVerId());
		params.put("newindexVerId", verNew.subtract(one));
		this.baseDAO.batchExecuteWithNameParam(jql, params);
	}
	
	/**
	 * 更新指标相关表结构
	 * @param templateId
	 * @param verId
	 * @param endDate
	 */
	private void updateIdxVer(String templateId, BigDecimal verId, String endDate){
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "update RptIdxInfo info set info.endDate = :endDate where info.templateId = :templateId and info.id.indexVerId = :indexVerId";
		params.put("endDate", endDate);
		params.put("templateId", templateId);
		long lastVid = verId.longValue();
		params.put("indexVerId", lastVid);
		this.baseDAO.batchExecuteWithNameParam(jql, params);
	}
	
	/**
	 * 更新检验公式相关表结构
	 * @param templateId
	 * @param startDate 要删除版本的开始日期
	 * @param endDate 要删除版本的结束日期
	 */
	private void updateValidVer(String templateId, String startDate, String endDate) {
		//更新逻辑检验相关表
		validLogicBS.updateValidVer(templateId, startDate, endDate);
		//更新警戒值校验相关表
		validWarnBS.updateValidVer(templateId, startDate, endDate);
	}
	
	/**
	 * 根据来源指标编号查找影响的报表
	 * @param pager
	 * @param param
	 * @return
	 */
	public SearchResult<RptMgrReportInfo> getRptsBySrcIdxNos(Pager pager,
			Map<String, Object> param) {
		PageHelper.startPage(pager);
		PageMyBatis<RptMgrReportInfo> page = (PageMyBatis<RptMgrReportInfo>) this.rptTmpDataDAO.getRptsBySrcIdxNos(param);
		SearchResult<RptMgrReportInfo> results = new SearchResult<RptMgrReportInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}

	/**
	 * 查找表间引用相关信息
	 * @param idxCellNos
	 * @param templateId
	 * @param verId
	 * @return
	 */
	private Map<String, RptDesignIdxCalcCellVO> getCalcCells(
			List<String> idxCellNos, String templateId, BigDecimal verId){
		Map<String, RptDesignIdxCalcCellVO> caMap = new HashMap<String, RptDesignIdxCalcCellVO>();
		if (idxCellNos != null && idxCellNos.size() > 0
				&& !StringUtils.isEmpty(templateId) && verId != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("isRptIdx", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			params.put("templateId", templateId);
			params.put("verId", verId);
			List<List<String>> cellNosParam = new ArrayList<List<String>>();
			if (idxCellNos.size() > 1000) {
				int index = 0;
				int remain = idxCellNos.size();
				while (remain > 1000) {
					cellNosParam.add(idxCellNos.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < idxCellNos.size()) {
					cellNosParam.add(idxCellNos.subList(index,
							idxCellNos.size()));
				}
			} else {
				cellNosParam.add(idxCellNos);
			}
			params.put("cellNos", cellNosParam);
			List<RptDesignIdxCalcCellVO> idxs = this.rptTmpDAO.getCalcIdxsByCell(params);
			if (idxs != null) {
				for (RptDesignIdxCalcCellVO cTmp : idxs) {
					caMap.put(cTmp.getCellNo(), cTmp);
				}
			}
		}
		return caMap;
	}
	
	@Transactional(readOnly = false)
	public void saveRptidTempRel(Map<String,String> params){
		String currentTime = DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss");
		params.put("modifyTime", currentTime);
		this.rptTmpDAO.saveRptidTempRel(params);
	}
	
	@Transactional(readOnly = false)
	public String saveRptZip(File inputFile, String outputPath) throws IOException{
		ZipFile zipFile = null;
		StringBuilder sbi = new StringBuilder();
		//制度版本、查询 制度名、业务类型(监管机构)
		List<Map<String, Object>> verlist = this.rptTmpDAO.getVeridByVernmAndBusitype();
		
		try {
			zipFile = new ZipFile(inputFile, "GB18030");
			for (Enumeration<?> entries = zipFile.getEntries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				
				String[] entryName = entry.getName().split("_");
				String busiNm = entryName[0];
				String verNm = entryName[1];
				String rptNm = entryName[2];
				String path = outputPath + File.separatorChar+busiNm+File.separatorChar+verNm;
				
				//查询rptId
				String rptId = null;
				rptId = this.rptTmpDAO.getRptIdByName(rptNm.substring(0, entryName[2].indexOf(".")));
				//获取制度版本
				String verId = null;
				for(Map<String, Object> map : verlist) {
					if(map.get("VERNM").equals(verNm) && map.get("BUSINM").equals(busiNm)) {
						verId = map.get("VERID").toString();
						break;
					}
				}
				//判断报表名称是否存在
				if(rptId == null) {
					sbi.append(rptNm).append(" 未匹配该报表名称  ").append("\n");
				}
				//判断制度版本是否存在
				if(verId == null) {
					sbi.append(rptNm).append(" 未匹配到其制度版本  ").append(verNm).append("\n");
				}
				if(sbi.length()>0) {
					continue;
				}
				//保存文件
				if (FilepathValidateUtils.validateFilepath(path)) {
					File outputPathDir = new File(path);
					if (! outputPathDir.exists()) {
						outputPathDir.mkdirs();
					}
					File file = new File(path,rptNm);
					if (entry.isDirectory()) {
						file.mkdirs();
					} else {
						InputStream in = null;
						OutputStream out = null;
						try {
							in = zipFile.getInputStream(entry);
							out = new FileOutputStream(file);
							IOUtils.copy(in, out);
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							IOUtils.closeQuietly(in);
							IOUtils.closeQuietly(out);
						}
					}
					//保存文件路径
					Map<String,String> params = new HashMap<String, String>();
					params.put("id", RandomUtils.uuid2());
					params.put("rptId", rptId);
					params.put("versionId", verId);
					params.put("templatePath",file.getAbsolutePath());
					String currentTime = DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss");
					params.put("modifyTime", currentTime);
					this.delRptTemplateByIdVer(params);
					this.saveRptidTempRel(params);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
			if (inputFile != null && inputFile.exists()) {
				inputFile.delete();
			}
		}
		return sbi.toString();
	}
	
	public SearchResult<Map<String,String>> getRptTemplateList(Pager pager, String rptId, String versionId) {
		Map<String, Object> param = new HashMap<String,Object>();
		pager.setOrderBy(false);
		pager.setFilter(false);
		Map<String, SearchFilter> searchFilters = pager.getSearchFilters();
		Iterator<String> it = searchFilters.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			SearchFilter sf = searchFilters.get(key);
			if (sf == null || sf.value == null) {
				continue;
			}
			if (sf.operator == SearchFilter.Operator.LIKE) {
				// 若是like操作
				String likeStr = "%" + searchFilters.get(key).value + "%";
				param.put(key, likeStr);
			} else {
				param.put(key, searchFilters.get(key).value + "");
			}
		}
		if(StringUtils.isNotBlank(rptId) && StringUtils.isNotBlank(versionId)){
			param.put("rptId", rptId);
			param.put("versionId", versionId);
		}
		PageHelper.startPage(pager);
		PageMyBatis<Map<String,String>> page = (PageMyBatis<Map<String,String>>) this.rptTmpDAO.getRptTemplateList(param);
		SearchResult<Map<String,String>> results = new SearchResult<Map<String,String>>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}
	
	@Transactional(readOnly = false)
	public void delRptTemplateById(String ids,String paths){
		String[] idsArr = ids.split(",");
		String[] pathArr = paths.split(",");
		for(int i=0; i<idsArr.length; i++) {
			this.rptTmpDAO.delRptTemplateById(idsArr[i]);
			String path = pathArr[i];
			if (FilepathValidateUtils.validateFilepath(path)) {
				File rptFile = new File(path);
				if(rptFile!=null && rptFile.exists()) {
					rptFile.delete();
				}
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void delRptTemplateByIdVer(Map<String,String> params){
		this.rptTmpDAO.delRptTemplateByIdVer(params);
	}
	
	public List<Map<String, String>> getFillUserByRpt(Map<String, Object> paramMap){
		return this.reportInfoDAO.getFillUserByRpt(paramMap);
	}
	
	/**
	 * 根据模板id和版本和结束日期获取报表模板
	 * @param templateId
	 * @param verId
	 * @param endDate
	 */
	public RptDesignTmpInfo getRptTmp(String templateId, String verId, String endDate) {
		Map<String, Object> param = Maps.newHashMap();
		if(StringUtils.isNotBlank(templateId) && (StringUtils.isNotBlank(verId) || StringUtils.isNotBlank(endDate))) {
			param.put("templateId", templateId);
			String jql = "select tmp from RptDesignTmpInfo tmp where tmp.id.templateId = :templateId";
			if(StringUtils.isNotBlank(verId)) {
				BigDecimal verNum = new BigDecimal(verId);
				param.put("verId", verNum);
				jql += " and tmp.id.verId = :verId";
			}
			if(StringUtils.isNotBlank(endDate)) {
				param.put("endDate", endDate);
				jql += " and tmp.verEndDate = :endDate";
			}
			List<RptDesignTmpInfo> tmpInfo = this.baseDAO.findWithNameParm(jql, param); 
			if(null != tmpInfo && tmpInfo.size() > 0) {
				return tmpInfo.get(0);
			}
		}
		return null;
	}

	/**
	 * @方法描述: 保存用户-报表授权信息
	 * @创建人: huzq1 
	 * @创建时间: 2021/10/14 10:07
	  * @param rptId
	 * @return
	 **/
	public void saveAuthObjRel(String rptId){
		List<BioneAuthObjResRel> newRels = new ArrayList<>();

		String[] authMenus = {RPTVIEW_RES_NO,RPTFILL_RES_NO,RPTAUDT_RES_NO,RPTCHECK_RES_NO};
		for (String authMenu : authMenus) {
			BioneAuthObjResRel rel = new BioneAuthObjResRel();
			BioneAuthObjResRelPK relPK = new BioneAuthObjResRelPK();
			relPK.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			relPK.setObjDefNo(GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER);
			relPK.setObjId(BioneSecurityUtils.getCurrentUserInfo().getUserId());
			relPK.setPermissionId(PERMISSION_ALL);
			relPK.setPermissionType(RES_PERMISSION_TYPE_OPER);
			relPK.setResDefNo(authMenu);
			relPK.setResId(rptId);
			rel.setId(relPK);
			List<BioneAuthObjResRel> authObjRelByObj = authObjBS.findAuthObjRelByObjAndRes(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
					GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER, BioneSecurityUtils.getCurrentUserInfo().getUserId(), authMenu, rptId);
			if(authObjRelByObj == null || authObjRelByObj.size() == 0){
				newRels.add(rel);
			}
		}
		List<BioneAuthObjResRel> oldRel = new ArrayList<>();
		this.authObjBS.updateRelBatch(oldRel, newRels);
	}

}
