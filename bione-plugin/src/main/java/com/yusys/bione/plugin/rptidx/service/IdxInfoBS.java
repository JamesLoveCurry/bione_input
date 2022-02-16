package com.yusys.bione.plugin.rptidx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.label.entity.BioneLabelInfo;
import com.yusys.bione.frame.label.entity.BioneLabelObjInfo;
import com.yusys.bione.frame.label.service.LabelBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.repository.mybatis.RptDataSetDao;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.repository.mybatis.RptDimDao;
import com.yusys.bione.plugin.rptdim.web.vo.RptDimItemInfoVO;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptidx.repository.*;
import com.yusys.bione.plugin.rptidx.web.vo.*;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.valid.repository.FunAndSymbolMybatisDao;
import com.yusys.bione.plugin.valid.service.ValidLogicBS;
import com.yusys.bione.plugin.wizard.web.vo.IndexImportVO;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.*;

/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class IdxInfoBS extends BaseBS<Object>{

	@Autowired
	private IdxInfoMybatisDao idxDao;

	@Autowired
	private IdxCatalogMybatisDao catalogDao;

	@Autowired
	private IdxDimRelMybatisDao relDao;

	@Autowired
	private IdxMeasureMybatisDao measureDao;

	@Autowired
	private IdxFormulaMybatisDao formulaDao;

	@Autowired
	private RptDimDao rptDimDao;

	@Autowired
	private RptDataSetDao rptDataSetDao;

	@Autowired
	private ValidLogicBS logicBs;// 公式校验
	
	@Autowired
	private LabelBS labelBS;

	@Autowired
	private FunAndSymbolMybatisDao funAndSymbolMybatisDao;
	
	@Autowired
	private ExcelBS excelBS;
	
	/**
	 * 
	 * @param basePath
	 * @param searchNm
	 *            搜索名称
	 * @param isShowIdx
	 *            是否显示指标
	 * @param isShowMeasureInfo
	 *            总账指标下是否显示度量
	 * @param idxNos
	 *            过滤指标
	 * @param isShowDim
	 *            是否显示指标下的维度 1 显示，其他不显示
	 * @param indexNo
	 *            指定指标编号进行过滤
	 * @param exSumAccoutIndex
	 *            过滤总账指标
	 * @param isEngine
	 *            是否是指标引擎配置（只获取派生和组合）
	 * @param isAuth
	 *            是否需要权限过滤 1过滤，其他不过滤
	 * @param isPublish
	 *            是否在指标列表中展示指标
	 * @return
	 */
	public List<CommonTreeNode> getTree(String basePath, String searchNm,
			String isShowIdx, String isShowMeasureInfo, String idxNos,
			String isShowDim, String indexNo, String exSumAccoutIndex,
			String isEngine, String isAuth, String isPublish, String defSrc) {
		Map<String, Object> condition = new HashMap<String, Object>();

		Map<String, Object> catalogMap = new HashMap<String, Object>();
		condition.put("needOrderByNm", "needOrderByNm");
		List<RptIdxCatalog> catalogList = this.catalogDao
				.listIdxCatalog(condition);
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();

		List<RptIdxInfo> idxList = new ArrayList<RptIdxInfo>();

		List<RptIdxMeasureInfoVO> measureList = new ArrayList<RptIdxMeasureInfoVO>();// 度量信息

		if (!StringUtils.isEmpty(isShowIdx) && isShowIdx.equals("1")) {
			if (!StringUtils.isEmpty(isPublish) && isPublish.equals("1")) {
				condition.put("isPublish", IS_PUBLISH_TRUE);
			}
			if (!StringUtils.isEmpty(searchNm)) {
				// 带有查询条件
				condition.put("indexNm", "%" + searchNm + "%");
			}

			if (!StringUtils.isEmpty(indexNo)) {
				// 带有查询条件
				condition.put("indexNo", indexNo);
			}

			if (!StringUtils.isEmpty(isEngine) && isEngine.equals("1")) {
				List<String> isEngineList = Lists.newArrayList();
				isEngineList.add(COMPOSITE_INDEX);
				isEngineList.add(DERIVE_INDEX);
				condition.put("isEngineList", isEngineList);
			}

			if (!StringUtils.isEmpty(idxNos)) {
				if (idxNos.endsWith(",")) {
					idxNos = idxNos.substring(0, idxNos.length() - 1);
				}
				String idxNo[] = StringUtils.split(idxNos, ',');
				List<String> idxNoList = new ArrayList<String>();
				for (String tmp : idxNo) {
					idxNoList.add(tmp);
				}
				List<List<?>> idxNoLists = ReBuildParam.splitLists(idxNoList);
				if (idxNoList != null && idxNoList.size() > 0) {
					condition.put("idxNos", idxNoLists);
				}
			}

			if (!StringUtils.isEmpty(exSumAccoutIndex)
					&& exSumAccoutIndex.equals("1")) {
				condition
						.put("exSumAccoutIndex",
								GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			}
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
					&& isAuth != null && !isAuth.equals("")) {
				List<String> list = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_IDX");
				// List<String>
				// list=this.idxBS.getIdxByUserId(BioneSecurityUtils.getCurrentUserId());
				if (list != null && list.size() > 0) {
					condition.put("list", ReBuildParam.splitLists(list));
					idxList = this.idxDao.listIdxInfo(condition);
				}
			} else
				idxList = this.idxDao.listIdxInfo(condition);
		}

		if (!StringUtils.isEmpty(isShowMeasureInfo)
				&& isShowMeasureInfo.equals("1")) {
			measureList = measureDao.list(new HashMap<String, Object>());
		}

		List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
		if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
			dimRelList = this.getDimNosOfIndex(new HashMap<String, Object>());
		}

		for (int i = 0; i < catalogList.size(); i++) {
			CommonTreeNode catalog = new CommonTreeNode();
			catalog.setId(catalogList.get(i).getIndexCatalogNo());
			catalog.setText(catalogList.get(i).getIndexCatalogNm());
			catalog.setData(catalogList.get(i));
			catalog.setUpId(catalogList.get(i).getUpNo());
			catalog.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
			catalog.getParams().put("type", "idxCatalog");
			List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();

			for (int j = 0; j < idxList.size(); j++) {

				if (catalogList.get(i).getIndexCatalogNo()
						.equals(idxList.get(j).getIndexCatalogNo())) {
					CommonTreeNode tmp = new CommonTreeNode();
					tmp.setId(idxList.get(j).getId().getIndexNo());
					tmp.setText(idxList.get(j).getIndexNm());
					tmp.setData(idxList.get(j));
					tmp.setUpId(catalog.getId());
					tmp.setIcon(basePath
							+ "/images/classics/menuicons/grid.png");
					tmp.getParams().put("id",
							idxList.get(j).getId().getIndexNo());
					tmp.getParams().put("type", "idxInfo");
					tmp.getParams().put("indexNo",
							idxList.get(j).getId().getIndexNo());
					tmp.getParams().put(
							"indexVerId",
							String.valueOf((idxList.get(j).getId()
									.getIndexVerId())));
					tmp.getParams().put("isSum", idxList.get(j).getIsSum());
					// 给指标增加其维度信息
					if (!StringUtils.isEmpty(isShowDim)
							&& isShowDim.equals("1")) {
						for (RptIdxDimRel rel : dimRelList) {
							if (rel.getId()
									.getIndexNo()
									.equals(idxList.get(j).getId().getIndexNo())
									&& rel.getId().getIndexVerId() == idxList
											.get(j).getId().getIndexVerId()) {
								tmp.getParams().put("dimNos",
										rel.getId().getDimNo());
							}
						}
					}
					// 给总账指标增加度量信息作为子节点
					if (!StringUtils.isEmpty(isShowMeasureInfo)
							&& isShowMeasureInfo.equals("1")
							&& idxList.get(j).getIndexType() != null
							&& idxList
									.get(j)
									.getIndexType()
									.equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)) {
						tmp.getParams().put("haveMeasure", "true");
						List<CommonTreeNode> measureNodes = new ArrayList<CommonTreeNode>();
						for (RptIdxMeasureInfoVO measure : measureList) {
							if (measure.getIndexNo().equals(
									idxList.get(j).getId().getIndexNo())
									&& measure.getIndexVerId() == idxList
											.get(j).getId().getIndexVerId()) {
								CommonTreeNode node = new CommonTreeNode();
								node.setUpId(idxList.get(j).getId()
										.getIndexNo());
								node.setId(measure.getMeasureNo());
								node.setText(measure.getMeasureNm());
								node.setIcon(basePath
										+ "/images/classics/menuicons/computer.png");
								node.getParams().put("type", "measureInfo");
								node.getParams().put("indexNo",
										idxList.get(j).getId().getIndexNo());
								node.getParams().put(
										"indexVerId",
										String.valueOf(idxList.get(j).getId()
												.getIndexVerId()));
								node.getParams().put(
										"display",
										idxList.get(j).getIndexNm() + "."
												+ measure.getMeasureNm());
								measureNodes.add(node);
							}
						}
						tmp.setChildren(measureNodes);
					}
					childList.add(tmp);
				}
			}
			if (childList.size() > 0) {
				catalog.getParams().put("hasInfos", "true");
			}
			catalog.setChildren(childList);
			resultList.add(catalog);
		}
		for (CommonTreeNode catalog : resultList) {
			catalogMap.put(catalog.getId(), catalog);
		}
		if (!StringUtils.isEmpty(isShowIdx)
				&& isShowIdx.equals("1")
				&& (StringUtils.isNotEmpty(searchNm)
						|| StringUtils.isNotEmpty(idxNos)
						|| StringUtils.isNotEmpty(indexNo) || StringUtils
							.isNotEmpty(isEngine))) {
			List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
			for (CommonTreeNode tmp : resultList) {
				if (tmp.getChildren() != null && tmp.getChildren().size() > 0
						&& !list.contains(tmp)) {
					list.add(tmp);
					searchParent(list, catalogMap, tmp);
				}
			}
			resultList = list;
			// return this.createTreeNode(list,false,null,basePath);
		}
		// return this.createTreeNode(resultList,true,catalogMap,basePath);
		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.setText("全部");
		baseNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.getParams().put("type", "root");
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		RptIdxCatalog catalogBase = new RptIdxCatalog();
		baseNode.setData(catalogBase);
		catalogBase.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
		resultList.add(baseNode);
		return resultList;
	}

	/**
	 * 授权用指标树
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getAuthTree() {
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		Map<String, Object> condition = new HashMap<String, Object>();
		Map<String, Object> catalogMap = new HashMap<String, Object>();
		condition.put("needOrderByNm", "needOrderByNm");
		List<String> authRptIds = new ArrayList<String>();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())) {
			authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
			condition.put("list", SplitStringBy1000.change(authRptIds));
		} else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && !"Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			return resultList;
		}
		List<RptIdxInfo> idxList = new ArrayList<RptIdxInfo>();
		idxList = this.idxDao.listIdxInfo(condition);
		Set<String> catalogNos = new HashSet<String>();
		//20191105，idxList为啥是空的业务逻辑没有深究，最简单的非空判断，保证程序正常运行
		if(idxList.size()>0){
			for (int j = 0; j < idxList.size(); j++) {
				CommonTreeNode tmp = new CommonTreeNode();
				tmp.setId(idxList.get(j).getId().getIndexNo());
				tmp.setText(idxList.get(j).getIndexNm());
				tmp.setData(idxList.get(j));
				tmp.setUpId(idxList.get(j).getIndexCatalogNo());
				catalogNos.add(idxList.get(j).getIndexCatalogNo());
				tmp.setIcon("/images/classics/menuicons/grid.png");
				tmp.getParams().put("id",
						idxList.get(j).getId().getIndexNo());
				tmp.getParams()
						.put("resDefNo",
								com.yusys.bione.plugin.base.common.GlobalConstants4plugin.IDX_RES_NO);
				tmp.getParams().put("type", "idxInfo");
				tmp.getParams().put("indexNo",
						idxList.get(j).getId().getIndexNo());
				tmp.getParams().put(
						"indexVerId",
						String.valueOf((idxList.get(j).getId()
								.getIndexVerId())));
				tmp.getParams().put("isSum", idxList.get(j).getIsSum());
				resultList.add(tmp);
			}
			Set<String> allCatalogNo = new HashSet<String>();
			this.getAllCatalogNo(catalogNos,  allCatalogNo);
			String jql = "select catalog from RptIdxCatalog catalog where catalog.indexCatalogNo in :catalogNos ";
			jql += " order by catalog.indexCatalogNm ";
			List<String> catalogNoList = new ArrayList<>(allCatalogNo);
			List<List<String>> catalogNoLists = SplitStringBy1000.change(catalogNoList);
			List<RptIdxCatalog> catalogList = new ArrayList<RptIdxCatalog>();
			for(int i = 0; i < catalogNoLists.size(); i++) {
				Map<String,Object> conditions = new HashMap<String, Object>();
				conditions.put("catalogNos", catalogNoLists.get(i));
				catalogList.addAll(this.baseDAO.findWithNameParm(jql, conditions));
			}
			
			for (int i = 0; i < catalogList.size(); i++) {
				CommonTreeNode catalog = new CommonTreeNode();
				catalog.setId(catalogList.get(i).getIndexCatalogNo());
				catalog.setText(catalogList.get(i).getIndexCatalogNm());
				catalog.setData(catalogList.get(i));
				catalog.setUpId(catalogList.get(i).getUpNo());
				catalog.setIcon(GlobalConstants4frame.LOGIC_MODULE_ICON);
				catalog.getParams().put("type", "idxCatalog");
				List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();
				if (childList.size() > 0) {
					catalog.getParams().put("hasInfos", "true");
				}
				catalog.setChildren(childList);
				resultList.add(catalog);
			}
			for (CommonTreeNode catalog : resultList) {
				catalogMap.put(catalog.getId(), catalog);
			}
			// return this.createTreeNode(resultList,true,catalogMap,basePath);
			// 构造根节点
			CommonTreeNode baseNode = new CommonTreeNode();
			baseNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			baseNode.setText("全部");
			baseNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
			baseNode.getParams().put("type", "root");
			baseNode.setIcon(GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
			RptIdxCatalog catalogBase = new RptIdxCatalog();
			baseNode.setData(catalogBase);
			catalogBase.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
			resultList.add(baseNode);
		}
		return resultList;
	}

	private void getAllCatalogNo(Set<String> catalogNo, Set<String> allCatalogNo){
		if(catalogNo != null && catalogNo.size() > 0){
			String jql = "select catalog from RptIdxCatalog catalog where 1=1 and catalog.indexCatalogNo in :catalogNo";
			List<String> catalogNoList = new ArrayList<>(catalogNo);
			List<List<String>> catalogNos = SplitStringBy1000.change(catalogNoList);
			List<RptIdxCatalog> catalogs = new ArrayList<RptIdxCatalog>();
			for(int i = 0; i < catalogNos.size(); i++) {
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("catalogNo", catalogNos.get(i));
				catalogs.addAll(this.baseDAO.findWithNameParm(jql, params));
			}
			catalogNo.clear();
			for(RptIdxCatalog catalog : catalogs){
				allCatalogNo.add(catalog.getIndexCatalogNo());
				if(!catalog.getUpNo().equals("0")){
					catalogNo.add(catalog.getUpNo());
				}
			}
			if(catalogNo != null && catalogNo.size() > 0){
				getAllCatalogNo(catalogNo, allCatalogNo);
			}
		}  
	}
	
	public String getIndexVerId(String indexNo){
		String jql = "select idx.id.indexVerId from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.endDate = ?1";
		List<Long> verId = this.baseDAO.findWithIndexParam(jql, indexNo,"29991231");
		if(verId != null && verId.size()>0){
			 return String.valueOf(verId.get(0));
		}
		return "1";
	}
	public List<CommonTreeNode> getAsyncTree(String basePath, String searchNm,
			String isShowIdx, String isShowMeasureInfo, String idxNos,
			String isShowDim, String indexNo, String isEngine,
			String isPublish, String exSumAccoutIndex, String isAuth,
			String isPreview, String defSrc) {
		Map<String, Object> condition = new HashMap<String, Object>();

		Map<String, Object> catalogMap = new HashMap<String, Object>();
		condition.put("needOrderByNm", "needOrderByNm");
		if (StringUtils.isNotEmpty(defSrc)) {
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			condition.put("defSrc", defSrc);
			if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
				condition.put("defOrg", user.getOrgNo());
			} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
				condition.put("defUser", user.getUserId());
			}
		}
		List<RptIdxCatalog> catalogList = this.catalogDao
				.listIdxCatalog(condition);
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();

		List<RptIdxInfo> idxList = new ArrayList<RptIdxInfo>();

		if (StringUtils.isNotEmpty(isShowIdx) && isShowIdx.equals("1")) {
			if (StringUtils.isNotEmpty(isPublish) && isPublish.equals("1")) {
				condition.put("indexSts", INDEX_STS_START);
			}
			if (StringUtils.isNotEmpty(searchNm)) {
				// 带有查询条件
				condition.put("indexNm", "%" + searchNm + "%");
			}

			if (StringUtils.isNotEmpty(indexNo)) {
				// 带有查询条件
				condition.put("indexNo", indexNo);
			}

			if (StringUtils.isNotEmpty(isEngine) && isEngine.equals("1")) {
				List<String> isEngineList = Lists.newArrayList();
				isEngineList.add(COMPOSITE_INDEX);
				isEngineList.add(DERIVE_INDEX);
				isEngineList.add(ADD_RECORD_INDEX);
				condition.put("isEngineList", isEngineList);
			}

			if (StringUtils.isNotEmpty(idxNos)) {
				if (idxNos.endsWith(",")) {
					idxNos = idxNos.substring(0, idxNos.length() - 1);
				}
				String idxNo[] = StringUtils.split(idxNos, ',');
				List<String> idxNoList = new ArrayList<String>();
				for (String tmp : idxNo) {
					idxNoList.add(tmp);
				}
				List<List<?>> idxNoLists = ReBuildParam.splitLists(idxNoList);
				if (idxNoList != null && idxNoList.size() > 0) {
					condition.put("idxNos", idxNoLists);
				}
			}
			if (!StringUtils.isEmpty(exSumAccoutIndex)
					&& exSumAccoutIndex.equals("1")) {
				condition
						.put("exSumAccoutIndex",
								com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			}
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
					&& isAuth != null && !isAuth.equals("")
					&& StringUtils.isEmpty(defSrc)) {
				List<String> list = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_IDX");
				// List<String>
				// list=this.idxBS.getIdxByUserId(BioneSecurityUtils.getCurrentUserId());
				if (list != null && list.size() > 0) {
					condition.put("list", ReBuildParam.splitLists(list));
					idxList = this.idxDao.listIdxInfo(condition);
				}
			} else
				idxList = this.idxDao.listIdxInfo(condition);
		}

		List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
		if (StringUtils.isNotEmpty(isShowDim) && isShowDim.equals("1")) {
			dimRelList = this.getDimNosOfIndex(new HashMap<String, Object>());
		}

		for (int i = 0; i < catalogList.size(); i++) {
			CommonTreeNode catalog = new CommonTreeNode();
			catalog.setIsParent(true);
			catalog.setId(catalogList.get(i).getIndexCatalogNo());
			catalog.setText(catalogList.get(i).getIndexCatalogNm());
			catalog.setData(catalogList.get(i));
			catalog.setUpId(catalogList.get(i).getUpNo());
			catalog.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
			catalog.getParams().put("nodeType", nodeTypeFoler);
			List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();

			for (int j = 0; j < idxList.size(); j++) {

				if (catalogList.get(i).getIndexCatalogNo()
						.equals(idxList.get(j).getIndexCatalogNo())) {
					CommonTreeNode tmp = new CommonTreeNode();
					tmp.setId(idxList.get(j).getId().getIndexNo());
					tmp.setText(idxList.get(j).getIndexNm());
					tmp.setData(idxList.get(j));
					tmp.setUpId(catalog.getId());
					tmp.setIcon(basePath
							+ "/images/classics/menuicons/grid.png");
					tmp.getParams().put("nodeType", nodeTypeIdx);
					tmp.getParams().put("indexNo",
							idxList.get(j).getId().getIndexNo());
					tmp.getParams().put(
							"indexVerId",
							String.valueOf((idxList.get(j).getId()
									.getIndexVerId())));
					tmp.getParams().put("isSum", idxList.get(j).getIsSum());
					// 给指标增加其维度信息
					if (!StringUtils.isEmpty(isShowDim)
							&& isShowDim.equals("1")) {
						for (RptIdxDimRel rel : dimRelList) {
							if (rel.getId()
									.getIndexNo()
									.equals(idxList.get(j).getId().getIndexNo())
									&& rel.getId().getIndexVerId() == idxList
											.get(j).getId().getIndexVerId()) {
								tmp.getParams().put("dimNos",
										rel.getId().getDimNo());
							}
						}
					}
					// 给总账指标增加度量信息作为子节点
					if (!StringUtils.isEmpty(isShowMeasureInfo)
							&& isShowMeasureInfo.equals("1")
							&& idxList.get(j).getIndexType() != null
							&& idxList
									.get(j)
									.getIndexType()
									.equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)) {
						tmp.getParams().put("haveMeasure", "true");
						tmp.setIsParent(true);
					}
					childList.add(tmp);
				}
			}
			if (childList.size() > 0) {
				catalog.getParams().put("hasInfos", "true");
			}
			catalog.setChildren(childList);
			resultList.add(catalog);
		}
		for (CommonTreeNode catalog : resultList) {
			catalogMap.put(catalog.getId(), catalog);
		}
		if (StringUtils.isNotEmpty(isShowIdx)
				&& isShowIdx.equals("1")
				&& (StringUtils.isNotEmpty(searchNm)
						|| StringUtils.isNotEmpty(idxNos)
						|| StringUtils.isNotEmpty(indexNo)
						|| StringUtils.isNotEmpty(isEngine)
						&& isEngine.equals("1") || StringUtils
							.isNotEmpty(isPreview))) {
			List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
			for (CommonTreeNode tmp : resultList) {
				if (tmp.getChildren() != null && tmp.getChildren().size() > 0
						&& !list.contains(tmp)) {
					list.add(tmp);
					searchParent(list, catalogMap, tmp);
				}
			}
			resultList = list;
			// return this.createTreeNode(list,false,null,basePath);
		}
		// return this.createTreeNode(resultList,true,catalogMap,basePath);
		return resultList;
	}
	@SuppressWarnings({ "unchecked" })
	private List<RptIdxInfo> getIdxByConfig(String searchNm, String isShowMeasureInfo, 
			String isShowDim, String indexNo,String indexNm, String isEngine,
			String isPublish, String exSumAccoutIndex, String isAuth, String defSrc, String upId, List<String> indexNos, List<String> labelIds, String labelObjId, String isCabin, String busiType){
		List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("isRptIndex", "N");
		params.put("endDate", "29991231");
		Map<String,Object> cparams = new HashMap<String, Object>();
		String hql = "select rel.id.objId from BioneLabelObjRel rel where rel.id.labelObjId = :labelObjId and rel.id.labelId in :labelIds";
		cparams.put("labelObjId", labelObjId);
		cparams.put("labelIds", labelIds);
		List<String> cindexNos = this.baseDAO.findWithNameParm(hql, cparams);
		String jql = "select  idx from RptIdxInfo idx where 1=1 ";
		jql += " and idx.isRptIndex = :isRptIndex and idx.endDate = :endDate ";
		
		params.put("isRptIndex", "N");
		List<String> indexType = new ArrayList<String>();
		if("1".equals(isPublish)){
			params.put("indexSts", "Y");
			jql += " and idx.indexSts = :indexSts";
		}
		if(StringUtils.isNotBlank(busiType)){
			params.put("busiType", busiType);
			jql += " and (idx.busiType = :busiType or idx.busiType = " + GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC + ")";//业务类型为传参加公共类型
		}
		if(!"1".equals(exSumAccoutIndex)){
		}else{
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			jql += " and idx.indexType not in :indexType ";
		}
		if("1".equals(isEngine)){
			params.put("indexSts", "Y");
			indexType.clear();
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX);
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX);
		}
		
		if("1".equals(isCabin)){
			params.put("isCabin", "1");
			jql += " and idx.isCabin = :isCabin";
		}
		if("1".equals(isAuth)){
			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				if(indexNos != null){
					indexNos = ListUtils.intersection(indexNos, BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX"));
				}
				else{
					indexNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
				}
				if(indexNos.size()<=0){
					return idxs;
				}	
			}
		}
		if(indexNos == null){
			indexNos = cindexNos;
		}
		else{
			indexNos = ListUtils.intersection(indexNos, cindexNos);
		}
		if(indexNos != null){
			if(indexNos.size() > 0){
				List<List<?>> indexNoLists = ReBuildParam.splitLists(indexNos);
				jql += " and ( ";
				int i = 0;
				for(List<?> indexNoList : indexNoLists){
					jql += " idx.id.indexNo in (:indexNoList"+i+") ";
					params.put("indexNoList"+i, indexNoList);
					if(i < indexNoLists.size() - 1){
						jql += " or ";
					}
					else{
						jql += " ) ";
					}
					i++;
				}
			}
			else{
				return idxs;
			}
			
		}
		
		if(StringUtils.isNotBlank(defSrc)){
			params.put("defSrc", defSrc);
			jql += " and idx.defSrc = :defSrc";
		}
		if(StringUtils.isNotBlank(upId)){
			params.put("upId", upId);
			jql += " and idx.indexCatalogNo = :upId";
		}
		if(StringUtils.isNotBlank(searchNm)){
			params.put("searchNm", "%"+searchNm+"%");
			jql += " and (idx.indexNm like :searchNm or idx.id.indexNo like :searchNm)";
		}
		if(StringUtils.isNotBlank(indexNo)){
			params.put("indexNo", indexNo);
			jql += " and idx.id.indexNo = :indexNo";
		}
		if(StringUtils.isNotBlank(indexNm)){
			params.put("indexNm", "%"+indexNm+"%");
			jql += " and idx.indexNm like :indexNm";
		}
		jql += " order by idx.id.indexNo";
		idxs =this.baseDAO.findWithNameParm(jql, params);
		return idxs;
	}
	
	@SuppressWarnings("unchecked")
	private List<RptIdxInfo> getIdxByConfig(String searchNm, String isShowMeasureInfo, 
			String isShowDim, String indexNo,String indexNm, String isEngine,
			String isPublish, String exSumAccoutIndex, String isAuth, String defSrc, String upId, List<String> indexNos, String isCabin, String busiType){
		List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
		List<Object[]> vos = new ArrayList<Object[]>();
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("isRptIndex", "N");
		params.put("endDate", "29991231");
		String jql = "select idx,ext from RptIdxInfo idx,RptIdxBusiExt ext ";
		jql += " where idx.id.indexNo = ext.id.indexNo and idx.id.indexVerId =ext.id.indexVerId and idx.isRptIndex = :isRptIndex and idx.endDate = :endDate";
		List<String> indexType = new ArrayList<String>();
		if("1".equals(isPublish)){
			params.put("indexSts", "Y");
			jql += " and idx.indexSts = :indexSts";
		}
		if(!"1".equals(exSumAccoutIndex)){
		}else{
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			jql += " and idx.indexType not in :indexType ";
		}
		
		// 指标跑数
		if("1".equals(isEngine)){
			params.put("indexSts", "Y");
			indexType.clear();
			jql += " and idx.indexType in :indexType ";
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX);
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX);
		}
		
		if("2".equals(isEngine)){
			params.put("indexSts", "Y");
			indexType.clear();
			jql += " and idx.indexType in :indexType ";
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.GENERIC_INDEX);//泛化指标
		}
		//影响指标跑数
		if("3".equals(isEngine)){
			params.put("indexSts", "Y");
			indexType.clear();
			jql += " and idx.indexType not in :indexType ";
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX);
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX);
		}
		if(indexType!= null && indexType.size() > 0){
			params.put("indexType", indexType);
		}
		
		if("1".equals(isCabin)){
			params.put("isCabin", "1");
			jql += " and idx.isCabin = :isCabin";
		}
		if(StringUtils.isNotBlank(busiType)){
			params.put("busiType",busiType);
			jql += " and (idx.busiType = :busiType or idx.busiType = " + GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC + ")";//业务类型为传参加公共类型
		}
		if(StringUtils.isNotBlank(defSrc)){
			params.put("defSrc", defSrc);
			jql += " and idx.defSrc = :defSrc";
		}
		if(StringUtils.isNotBlank(upId)){
			params.put("upId", upId);
			jql += " and idx.indexCatalogNo = :upId";
		}
		if(StringUtils.isNotBlank(searchNm)){
			params.put("searchNm", "%"+searchNm+"%");
			jql += " and (idx.indexNm like :searchNm)";
		}
		if(StringUtils.isNotBlank(indexNo)){
			params.put("indexNo", indexNo);
			jql += " and idx.id.indexNo = :indexNo";
		}
		if(StringUtils.isNotBlank(indexNm)){
			params.put("indexNm", "%"+indexNm+"%");
			jql += " and idx.indexNm like :indexNm";
		}
		jql += " order by idx.id.indexNo";
		vos =this.baseDAO.findWithNameParm(jql, params);
		if(vos != null && vos.size() > 0){
			for(Object[] vo : vos){
				RptIdxInfo idx = (RptIdxInfo) vo[0];
				RptIdxBusiExt ext = (RptIdxBusiExt) vo[1];
				idx.setRemark("业务定义："+(ext.getBusiDef()== null ? "无": ext.getBusiDef())+"\n"+"业务口径："+(ext.getBusiRule()== null ? "无": ext.getBusiRule()));
				idxs.add(idx);
			}
		}
		if(idxs != null && idxs.size() > 0){
			if("1".equals(isAuth)){
				if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
					indexNos = (List<String>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), "resIndexNo");
					if(indexNos != null && indexNos.size() > 0){
						Iterator<RptIdxInfo> it = idxs.iterator();
						while(it.hasNext()){
							if(!indexNos.contains(it.next().getId().getIndexNo())){
								it.remove();
							}
						}
					}else{
						return null;
					}
					
				}
			}
		}
		return idxs;
	}
	
	
	@SuppressWarnings({ "unchecked" })
	private List<RptIdxInfo> getIdxByConfig(JSONObject searchObj, String isShowMeasureInfo, 
			String isShowDim, String isEngine,String isPublish, String exSumAccoutIndex, String isAuth, String defSrc, List<String> indexNos, String isCabin, String busiType){
		List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("isRptIndex", "N");
		params.put("endDate", "29991231");
		String jql = "select idx from RptIdxInfo idx where idx.isRptIndex = :isRptIndex and idx.endDate = :endDate";
		List<String> indexType = new ArrayList<String>();
		if("1".equals(isPublish)){
			params.put("indexSts", "Y");
			jql += " and idx.indexSts = :indexSts";
		}
		if(StringUtils.isNotBlank(busiType)){
			params.put("busiType", busiType);
			jql += " and (idx.busiType = :busiType or idx.busiType = " + GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC + ")";//业务类型为传参加公共类型
		}
		if(!"1".equals(exSumAccoutIndex)){
		}else{
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			jql += " and idx.indexType not in :indexType ";
		}
		if("1".equals(isEngine)){
			params.put("indexSts", "Y");
			indexType.clear();
			jql += " and idx.indexType in :indexType ";
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX);
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX);
		}
		if(indexType!= null && indexType.size() > 0){
			params.put("indexType", indexType);
		}
		if("1".equals(isAuth)){
			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				if(indexNos != null){
					indexNos = ListUtils.intersection(indexNos, BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX"));
				}
				else{
					indexNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
				}
				if(indexNos.size()<=0){
					return idxs;
				}	
			}
		}
		if(indexNos != null){
			if(indexNos.size() > 0){
				List<List<?>> indexNoLists = ReBuildParam.splitLists(indexNos);
				jql += " and ( ";
				int i = 0;
				for(List<?> indexNoList : indexNoLists){
					jql += " idx.id.indexNo in (:indexNoList"+i+") ";
					params.put("indexNoList"+i, indexNoList);
					if(i < indexNoLists.size() - 1){
						jql += " or ";
					}
					else{
						jql += " ) ";
					}
					i++;
				}
			}
			else{
				return idxs;
			}
			
		}
		
		if(StringUtils.isNotBlank(defSrc)){
			params.put("defSrc", defSrc);
			jql += " and idx.defSrc = :defSrc";
		}
		if("1".equals(isCabin)){
			params.put("isCabin", "1");
			jql += " and idx.isCabin = :isCabin";
		}
		if(searchObj.size() > 1){//高级搜索条件集合
			if(!"".equals(searchObj.getString("indexNo"))){
				params.put("indexNo", searchObj.getString("indexNo"));
				jql += " and idx.id.indexNo = :indexNo ";
			}
			if(!"".equals(searchObj.getString("indexNm"))){
				params.put("indexNm", "%"+searchObj.getString("indexNm")+"%");
				jql += " and idx.indexNm like :indexNm ";
			}
			if(!"".equals(searchObj.getString("indexType"))){
				params.put("indexType", searchObj.getString("indexType"));
				jql += " and idx.indexType = :indexType ";
			}
			if(!"".equals(searchObj.getString("isCabin"))){
				params.put("isCabin", searchObj.getString("isCabin"));
				jql += " and idx.isCabin = :isCabin ";
			}
		}
		idxs =this.baseDAO.findWithNameParm(jql, params);
		return idxs;
	}
	
	private Set<String> getCatalogByConfig(String searchNm, String isShowMeasureInfo, 
			String isShowDim, String indexNo,String indexNm, String isEngine,
			String isPublish, String exSumAccoutIndex, String isAuth, String defSrc, String isCabin, String busiType){
		Set<String> catalogNos = new HashSet<String>();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("isRptIndex", "N");
		params.put("endDate", "29991231");
		String jql = "select distinct idx.indexCatalogNo from RptIdxInfo idx where idx.isRptIndex = :isRptIndex and idx.endDate = :endDate";
		List<String> indexType = new ArrayList<String>();
		if("1".equals(isPublish)){
			params.put("indexSts", "Y");
			jql += " and idx.indexSts = :indexSts";
		}
		if("1".equals(isCabin)){
			params.put("isCabin", "1");
			jql += " and idx.isCabin = :isCabin";
		}
		if(StringUtils.isNotBlank(busiType)){
			params.put("busiType", busiType);
			jql += " and (idx.busiType = :busiType or idx.busiType = " + GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC + ")";//业务类型为传参加公共类型
		}
		if(!"1".equals(exSumAccoutIndex)){
		}else{
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			jql += " and idx.indexType not in :indexType ";
		}
		if("1".equals(isEngine)){
			params.put("indexSts", "Y");
			indexType.clear();
			jql += " and idx.indexType in :indexType ";
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX);
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX);
		}
		if(indexType!= null && indexType.size() > 0){
			params.put("indexType", indexType);
		}
		if(StringUtils.isNotBlank(defSrc)){
			params.put("defSrc", defSrc);
			jql += " and idx.defSrc = :defSrc";
		}
		if(StringUtils.isNotBlank(searchNm)){
			params.put("searchNm", "%"+searchNm+"%");
			jql += " and (idx.indexNm like :searchNm or idx.id.indexNo like :searchNm)";
		}
		if(StringUtils.isNotBlank(indexNo)){
			params.put("indexNo", indexNo);
			jql += " and idx.id.indexNo = :indexNo";
		}
		if(StringUtils.isNotBlank(indexNm)){
			params.put("indexNm", "%"+indexNm+"%");
			jql += " and idx.id.indexNm like :indexNm";
		}
		List<String> catalogs =this.baseDAO.findWithNameParm(jql, params);
		if(catalogs != null && catalogs.size() > 0){
			for(String catalogNo: catalogs){
				catalogNos.add(catalogNo);
			}
		}
		return catalogNos;
	}
	
	private void getAllCatalogNo(Set<String> catalogNo,String defSrc,Set<String> allCatalogNo){
		if(catalogNo != null && catalogNo.size() > 0){
			String jql = "select catalog from RptIdxCatalog catalog where 1=1 and catalog.indexCatalogNo in :catalogNo";
			Map<String,Object> params = new HashMap<String, Object>();
			if (StringUtils.isNotEmpty(defSrc)) {
				jql += " and catalog.defSrc = :defSrc";
				params.put("defSrc", defSrc);
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
					jql += " and catalog.defOrg = :defOrg";
					params.put("defOrg", user.getOrgNo());
				} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
					jql += " and catalog.defUser = :defUser";
					params.put("defUser", user.getUserId());
				}
			}
			List<String> catalogNoss = new ArrayList<String>(catalogNo);
			List<List<String>> catalogNoList = SplitStringBy1000.change(catalogNoss);
			List<RptIdxCatalog> catalogss = new ArrayList<RptIdxCatalog>();
			for(List<String> catalogNos : catalogNoList){
				params.put("catalogNo", catalogNos);
				List<RptIdxCatalog> catalogs = this.baseDAO.findWithNameParm(jql, params);
				catalogss.addAll(catalogs);
			}
			catalogNo.clear();
			for(RptIdxCatalog catalog : catalogss){
				allCatalogNo.add(catalog.getIndexCatalogNo());
				if(!catalog.getUpNo().equals("0")){
					catalogNo.add(catalog.getUpNo());
				}
			}
			if(catalogNo != null && catalogNo.size() > 0){
				getAllCatalogNo(catalogNo, defSrc, allCatalogNo);
			}
		}  
	}
	private String rootCatalog = "rootCatalog";
	private String nodeTypeFoler = "idxCatalog";
	private String nodeTypeIdx = "idxInfo";

	/**
	 * 
	 * @param basePath
	 * @param searchNm
	 *            搜索名称
	 * @param isShowIdx
	 *            是否显示指标
	 * @param isShowMeasureInfo
	 *            总账指标下是否显示度量
	 * @param idxNos
	 *            过滤指标
	 * @param isShowDim
	 *            是否显示指标下的维度 1 显示，其他不显示
	 * @param indexNo
	 *            指定指标编号进行过滤
	 * @param exSumAccoutIndex
	 *            过滤总账指标
	 * @param isEngine
	 *            是否是指标引擎配置（只获取派生和组合）
	 * @param isAuth
	 *            是否需要权限过滤 1过滤，其他不过滤
	 * @param isPublish
	 *            是否在指标列表中展示指标
	 * @param showEmptyFolder
	 *            是否显示空文件夹
	 * @param isPreview
	 *            1.指标字典 other指标管理
	 * @param defSrc
	 *            1.指标定义来源
	 * @return
	 */
	public List<CommonTreeNode> getAsyncTree(String contextPath,
			String searchNm, String isShowIdx, String isShowMeasureInfo,
			String idxNos, String isShowDim, String indexNo,String indexNm,
			String exSumAccoutIndex, String isEngine, String isAuth,
			String nodeType, String upId, String indexVerId, String isPublish,
			String showEmptyFolder, String isPreview, String defSrc, List<String> indexNos, String isCabin, String busiType) {
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		if (StringUtils.isEmpty(upId)) {
			upId = "0";
			CommonTreeNode node = generateRootNode(contextPath);
			node.setIsParent(true);
			
			resultList.add(node);
			return resultList;
		}
		if (StringUtils.isEmpty(nodeType)) {
			nodeType = nodeTypeFoler;
		}
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if (nodeType.equals(nodeTypeFoler)) {
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			String jql = "select catalog from RptIdxCatalog catalog where upNo = :upNo ";
			condition.put("upNo", upId);
			if (StringUtils.isNotEmpty(defSrc)) {
				jql += " and catalog.defSrc = :defSrc";
				condition.put("defSrc", defSrc);
				if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
					jql += " and catalog.defOrg = :defOrg";
					condition.put("defOrg", user.getOrgNo());
				} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
					jql += " and catalog.defUser = :defUser";
					condition.put("defUser", user.getUserId());
				}
			}
			List<RptIdxInfo> idxList = getIdxByConfig(searchNm, isShowMeasureInfo, isShowDim, indexNo, indexNm ,isEngine, isPublish, exSumAccoutIndex, isAuth, defSrc, upId, indexNos, isCabin, busiType);
			Set<String> allCatalogNos = new HashSet<String>();
			if(!"1".equals(showEmptyFolder)){
				Set<String> catalogNos =  this.getCatalogByConfig(searchNm, isShowMeasureInfo, isShowDim, indexNo, indexNm ,isEngine, isPublish, exSumAccoutIndex, isAuth, defSrc, isCabin, busiType);
				if(idxList!= null && idxList.size() > 0 ){
					for(RptIdxInfo info : idxList){
						catalogNos.add(info.getIndexCatalogNo());
					}
				}
				this.getAllCatalogNo(catalogNos, defSrc, allCatalogNos);
			}
			
			jql += " order by catalogOrder asc";
			
			List<RptIdxCatalog> catalogList = this.baseDAO.findWithNameParm(jql, condition);
			for (RptIdxCatalog tmp : catalogList) {
				if(allCatalogNos.contains(tmp.getIndexCatalogNo()) || "1".equals(showEmptyFolder)){
					CommonTreeNode node = generateCatalogNode(contextPath, tmp);
					node.setIsParent(true);
					resultList.add(node);
				}
			}
			if(idxList != null && idxList.size() > 0){
				List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
				if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
						for(RptIdxInfo idx : idxList){
							if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
									|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
									||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
							{
								relidx.add(idx);
							}
						}
						if(relidx.size()>0){
							condition.put("idxList", relidx);
							dimRelList = this.getDimNosOfIndex(condition);
						}
					}
				}
				Map<String,String> dimRelMap = new HashMap<String, String>(); 
				if (dimRelList != null && dimRelList.size()>0){
					for(RptIdxDimRel rel : dimRelList){
						dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
					}
				}
				for (RptIdxInfo tmp : idxList) {
					CommonTreeNode idxNode = generateIndexNode(contextPath, tmp, isShowDim, isShowMeasureInfo, dimRelMap, null);
					resultList.add(idxNode);
				}
			}
		}
		else if (nodeType.equals(nodeTypeIdx)) {
			resultList.addAll(generateMeasureNode(contextPath, upId, indexVerId,isShowDim));
		}
		return resultList;
	}
	
	public List<CommonTreeNode> getSyncTree(String basePath, String searchNm,
            String isShowIdx, String isShowMeasureInfo, 
            String isShowDim, String indexNo, String indexNm ,String isEngine,
            String isPublish, String exSumAccoutIndex, String isAuth, String showEmptyFolder, String defSrc, List<String> indexNos, String isCabin, String isRoot, String busiType) {
        List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
        if (StringUtils.isEmpty(isRoot)) {
            resultList.add(generateRootNode(basePath));
        }
        if("1".equals(isShowIdx)){
            List<RptIdxInfo> idxList = getIdxByConfig(searchNm, isShowMeasureInfo, isShowDim, indexNo,indexNm, isEngine, isPublish, exSumAccoutIndex, isAuth, defSrc, "", indexNos, isCabin, busiType);
            if(idxList != null && idxList.size() > 0){
                Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
                List<RptIdxMeasureInfoVO> measureList = new ArrayList<RptIdxMeasureInfoVO>();// 度量信息
                if (!StringUtils.isEmpty(isShowMeasureInfo)
                        && isShowMeasureInfo.equals("1")) {
                    if(idxList != null && idxList.size() > 0){
                        List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
                        for(RptIdxMeasureInfo info : infos){
                            measureInfoMap.put(info.getMeasureNo(), info);
                        }
                        Map<String,Object> condition = new HashMap<String, Object>();
                        condition.put("idxList", idxList);
                        measureList = measureDao.getMeasure(condition);
                    }
                }
                Map<String,List<RptIdxMeasureInfoVO>> measureMap = new HashMap<String, List<RptIdxMeasureInfoVO>>(); 
                if (measureList != null && measureList.size()>0){
                    for(RptIdxMeasureInfoVO measure : measureList){
                        RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
                        measure.setMeasureNm(info.getMeasureNm());
                        measure.setMeasureType(info.getMeasureType());
                        measure.setCalcFormula(info.getCalcFormula());
                        measure.setRemark(info.getRemark());
                        List<RptIdxMeasureInfoVO> vos = measureMap.get(measure.getIndexNo()+"-"+measure.getIndexVerId());
                        if(vos == null){
                            vos = new ArrayList<RptIdxMeasureInfoVO>();
                            measureMap.put(measure.getIndexNo()+"-"+measure.getIndexVerId(),vos);
                        }
                        vos.add(measure);
                    }
                }
                List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
                if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
                    if(idxList != null && idxList.size() > 0){
                        Map<String,Object> condition = new HashMap<String, Object>();
                        List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
                        for(RptIdxInfo idx : idxList){
                            if(idx.getIndexType().equals(GlobalConstants4plugin.ROOT_INDEX)
                                    || idx.getIndexType().equals(GlobalConstants4plugin.COMPOSITE_INDEX)
                                    ||idx.getIndexType().equals(GlobalConstants4plugin.DERIVE_INDEX))
                            {
                                relidx.add(idx);
                            }
                        }
                        if(relidx.size()>0){
                            condition.put("idxList", relidx);
                            dimRelList = this.getDimNosOfIndex(condition);
                        }
                    }
                }
                Map<String,String> dimRelMap = new HashMap<String, String>(); 
                if (dimRelList != null && dimRelList.size()>0){
                    for(RptIdxDimRel rel : dimRelList){
                        dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
                    }
                }
                Set<String> catalogNos = new HashSet<String>();
                for(RptIdxInfo idx : idxList){
                    catalogNos.add(idx.getIndexCatalogNo());
                    resultList.add(generateIndexNode(basePath, idx, isShowDim, isShowMeasureInfo, dimRelMap, measureMap));
                }
                List<RptIdxCatalog> catalogs = new ArrayList<RptIdxCatalog>();
                List<RptIdxCatalog> catalogss = new ArrayList<RptIdxCatalog>();
                if(!"1".equals(showEmptyFolder)){
                    Set<String> allCatalogNo = new HashSet<String>();
                    this.getAllCatalogNo(catalogNos, defSrc, allCatalogNo);
                    String jql = "select catalog from RptIdxCatalog catalog where catalog.indexCatalogNo in :catalogNos ";
                    Map<String,Object> conditions = new HashMap<String, Object>();
                    if (StringUtils.isNotEmpty(defSrc)) {
                        jql += " and catalog.defSrc = :defSrc";
                        conditions.put("defSrc", defSrc);
                        BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
                        if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
                            jql += " and catalog.defOrg = :defOrg";
                            conditions.put("defOrg", user.getOrgNo());
                        } else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
                            jql += " and catalog.defUser = :defUser";
                            conditions.put("defUser", user.getUserId());
                        }
                    }
                    List<String> catalogNoss = new ArrayList<String>(allCatalogNo);
        			List<List<String>> catalogNoList = SplitStringBy1000.change(catalogNoss);
        			for(List<String> catalogNo : catalogNoList){
        				conditions.put("catalogNos", catalogNo);
                        catalogs = this.baseDAO.findWithNameParm(jql, conditions);
                        catalogss.addAll(catalogs);
        			}
                }
                else{
                    String jql = "select catalog from RptIdxCatalog catalog where 1=1 ";
                    Map<String,Object> condition = new HashMap<String, Object>();
                    if(StringUtils.isNotBlank(defSrc)){
                        condition.put("defSrc", defSrc);
                        jql += " and catalog.defSrc = :defSrc";
                    }
                    catalogs = this.baseDAO.findWithNameParm(jql, condition);
                    catalogss.addAll(catalogs);
                }
                if(catalogss != null && catalogss.size() > 0){
                    for(RptIdxCatalog catalog : catalogss){
                        resultList.add(generateCatalogNode(basePath, catalog));
                    }
                }
            }
        }
        else{
            Map<String,Object> params = new HashMap<String, Object>();
            String jql = "select catalog from RptIdxCatalog catalog where 1=1 ";
            if (StringUtils.isNotEmpty(defSrc)) {
                jql += " and catalog.defSrc = :defSrc";
                params.put("defSrc", defSrc);
                BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
                if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
                    jql += " and catalog.defOrg = :defOrg";
                    params.put("defOrg", user.getOrgNo());
                } else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
                    jql += " and catalog.defUser = :defUser";
                    params.put("defUser", user.getUserId());
                }
            }
            jql += " order by catalog.catalogOrder ";
            List<RptIdxCatalog> catalogs = this.baseDAO.findWithNameParm(jql, params);
            if(catalogs != null && catalogs.size() > 0){
                for(RptIdxCatalog catalog : catalogs){
                    resultList.add(generateCatalogNode(basePath, catalog));
                }
            }
        }
        return resultList;
    }
	
	private void getAllLabelInfo(Set<String> labelIds,Set<String> AlllabelIds,String labelObjId){
		if(labelIds != null && labelIds.size() > 0){
			String jql ="select info from BioneLabelInfo info where 1=1 and info.labelObjId = :labelObjId and info.labelId in :labelIds";
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("labelIds", labelIds);
			params.put("labelObjId", labelObjId);
			List<BioneLabelInfo> labels = this.baseDAO.findWithNameParm(jql, params);
			labelIds.clear();
			if(labels != null && labels.size()>0){
				for(BioneLabelInfo label : labels){
					AlllabelIds.add(label.getLabelId());
					if(!label.getUpId().equals("0")){
						labelIds.add(label.getUpId());
					}
				}
				if(labelIds != null && labelIds.size() > 0){
					getAllLabelInfo(labelIds, AlllabelIds, labelObjId);
				}
			}
			
		}  
	}
	
	public List<CommonTreeNode> getSyncTreePro(String basePath, String searchObj,
			String isShowIdx, String isShowMeasureInfo, 
			String isShowDim, String indexNo, String indexNm, String isEngine,
			String isPublish, String exSumAccoutIndex, String isAuth, String showEmptyFolder, String defSrc, List<String> indexNos, String isCabin, String busiType) {
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		resultList.add(generateRootNode(basePath));
		if("1".equals(isShowIdx)){
			List<RptIdxInfo> idxList = Lists.newArrayList();
			if(StringUtils.isNotBlank(searchObj)){
				JSONObject obj = JSON.parseObject(searchObj);
				idxList = getIdxByConfig(obj, isShowMeasureInfo, isShowDim, isEngine, isPublish, exSumAccoutIndex, isAuth,  defSrc, indexNos, isCabin, busiType);
			}
			if(idxList != null && idxList.size() > 0){
				Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
				List<RptIdxMeasureInfoVO> measureList = new ArrayList<RptIdxMeasureInfoVO>();// 度量信息
				if (!StringUtils.isEmpty(isShowMeasureInfo)
						&& isShowMeasureInfo.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
						for(RptIdxMeasureInfo info : infos){
							measureInfoMap.put(info.getMeasureNo(), info);
						}
						Map<String,Object> condition = new HashMap<String, Object>();
						condition.put("idxList", idxList);
						measureList = measureDao.getMeasure(condition);
					}
				}
				Map<String,List<RptIdxMeasureInfoVO>> measureMap = new HashMap<String, List<RptIdxMeasureInfoVO>>(); 
				if (measureList != null && measureList.size()>0){
					for(RptIdxMeasureInfoVO measure : measureList){
						RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
						measure.setMeasureNm(info.getMeasureNm());
						measure.setMeasureType(info.getMeasureType());
						measure.setCalcFormula(info.getCalcFormula());
						measure.setRemark(info.getRemark());
						List<RptIdxMeasureInfoVO> vos = measureMap.get(measure.getIndexNo()+"-"+measure.getIndexVerId());
						if(vos == null){
							vos = new ArrayList<RptIdxMeasureInfoVO>();
							measureMap.put(measure.getIndexNo()+"-"+measure.getIndexVerId(),vos);
						}
						vos.add(measure);
					}
				}
				List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
				if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						Map<String,Object> condition = new HashMap<String, Object>();
						List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
						for(RptIdxInfo idx : idxList){
							if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
									|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
									||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
							{
								relidx.add(idx);
							}
						}
						if(relidx.size()>0){
							condition.put("idxList", relidx);
							dimRelList = this.getDimNosOfIndex(condition);
						}
					}
				}
				Map<String,String> dimRelMap = new HashMap<String, String>(); 
				if (dimRelList != null && dimRelList.size()>0){
					for(RptIdxDimRel rel : dimRelList){
						dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
					}
				}
				Set<String> catalogNos = new HashSet<String>();
				for(RptIdxInfo idx : idxList){
					catalogNos.add(idx.getIndexCatalogNo());
					resultList.add(generateIndexNode(basePath, idx, isShowDim, isShowMeasureInfo, dimRelMap, measureMap));
				}
				List<RptIdxCatalog> catalogs = new ArrayList<RptIdxCatalog>();
				if(!"1".equals(showEmptyFolder)){
					Set<String> allCatalogNo = new HashSet<String>();
					this.getAllCatalogNo(catalogNos, defSrc, allCatalogNo);
					String jql = "select catalog from RptIdxCatalog catalog where catalog.indexCatalogNo in :catalogNos ";
					Map<String,Object> conditions = new HashMap<String, Object>();
					conditions.put("catalogNos", allCatalogNo);
					if (StringUtils.isNotEmpty(defSrc)) {
						jql += " and catalog.defSrc = :defSrc";
						conditions.put("defSrc", defSrc);
						BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
						if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
							jql += " and catalog.defOrg = :defOrg";
							conditions.put("defOrg", user.getOrgNo());
						} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
							jql += " and catalog.defUser = :defUser";
							conditions.put("defUser", user.getUserId());
						}
					}
					jql += " order by catalog.catalogOrder ";
					catalogs = this.baseDAO.findWithNameParm(jql, conditions);
				}
				else{
					String jql = "select catalog from RptIdxCatalog catalog where 1=1 ";
					Map<String,Object> condition = new HashMap<String, Object>();
					if(StringUtils.isNotBlank(defSrc)){
						condition.put("defSrc", defSrc);
						jql += " and catalog.defSrc = :defSrc";
					}
					catalogs = this.baseDAO.findWithNameParm(jql, condition);
				}
				if(catalogs != null && catalogs.size() > 0){
					for(RptIdxCatalog catalog : catalogs){
						resultList.add(generateCatalogNode(basePath, catalog));
					}
				}
			}
		}
		else{
			Map<String,Object> params = new HashMap<String, Object>();
			String jql = "select catalog from RptIdxCatalog catalog where 1=1 ";
			if (StringUtils.isNotEmpty(defSrc)) {
				jql += " and catalog.defSrc = :defSrc";
				params.put("defSrc", defSrc);
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
					jql += " and catalog.defOrg = :defOrg";
					params.put("defOrg", user.getOrgNo());
				} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
					jql += " and catalog.defUser = :defUser";
					params.put("defUser", user.getUserId());
				}
			}
			jql += " order by catalog.catalogOrder ";
			List<RptIdxCatalog> catalogs = this.baseDAO.findWithNameParm(jql, params);
			if(catalogs != null && catalogs.size() > 0){
				for(RptIdxCatalog catalog : catalogs){
					resultList.add(generateCatalogNode(basePath, catalog));
				}
			}
		}
		return resultList;
	}
	
	public List<CommonTreeNode> getSyncLabelFilter(String contextPath,
			String searchNm, String indexVerId, String isShowDim, String isShowMeasureInfo, String isAuth, String ids, String type, String isCabin, String busiType){
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		if(type.equals("filter")){
			if(StringUtils.isNotBlank(ids)){
				Set<String> idList = ArrayUtils.asSet(ids, ",");
				BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", "idx");
				String labelObjId = "";
				if(obj!=null){
					labelObjId = obj.getLabelObjId();
				}
				Set<String> allLabelId = new HashSet<String>();
				this.getAllLabelInfo(idList, allLabelId, labelObjId);
				List<RptIdxInfo> idxList = getIdxByConfig("", isShowDim, isShowMeasureInfo , "", "", "", "1", "", isAuth,  "", "", null, new ArrayList<String>(allLabelId), labelObjId, isCabin, busiType);
				Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
				List<RptIdxMeasureInfoVO> measureList = new ArrayList<RptIdxMeasureInfoVO>();// 度量信息
				if (!StringUtils.isEmpty("1")
						&& isShowMeasureInfo.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
						for(RptIdxMeasureInfo info : infos){
							measureInfoMap.put(info.getMeasureNo(), info);
						}
						Map<String,Object> condition = new HashMap<String, Object>();
						condition.put("idxList", idxList);
						measureList = measureDao.getMeasure(condition);
					}
				}
				Map<String,List<RptIdxMeasureInfoVO>> measureMap = new HashMap<String, List<RptIdxMeasureInfoVO>>(); 
				if (measureList != null && measureList.size()>0){
					for(RptIdxMeasureInfoVO measure : measureList){
						RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
						measure.setMeasureNm(info.getMeasureNm());
						measure.setMeasureType(info.getMeasureType());
						measure.setCalcFormula(info.getCalcFormula());
						measure.setRemark(info.getRemark());
						List<RptIdxMeasureInfoVO> vos = measureMap.get(measure.getIndexNo()+"-"+measure.getIndexVerId());
						if(vos == null){
							vos = new ArrayList<RptIdxMeasureInfoVO>();
							measureMap.put(measure.getIndexNo()+"-"+measure.getIndexVerId(),vos);
						}
						vos.add(measure);
					}
				}
				List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
				List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
				List<String> indexNos = new ArrayList<String>();
				for(RptIdxInfo idx : idxList){
					if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
							|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
							||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
					{
						relidx.add(idx);
					}
					indexNos.add(idx.getId().getIndexNo());
				}
				if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						Map<String,Object> condition = new HashMap<String, Object>();
						
						if(relidx.size()>0){
							condition.put("idxList", relidx);
							dimRelList = this.getDimNosOfIndex(condition);
						}
					}
				}
				Map<String,String> dimRelMap = new HashMap<String, String>(); 
				if (dimRelList != null && dimRelList.size()>0){
					for(RptIdxDimRel rel : dimRelList){
						dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
					}
				}
				Map<String,List<String>> idxlabelMap = this.labelBS.getIdxLabelMap("idx", indexNos);
				for(RptIdxInfo idx : idxList){
					List<String> labels = idxlabelMap.get(idx.getId().getIndexNo());
					if(labels != null && labels.size()>0){
						for(String label : labels){
							if(allLabelId.contains(label)){
								CommonTreeNode newNode = generateIndexNode(contextPath, idx, isShowDim, isShowMeasureInfo, dimRelMap, measureMap);
								newNode.setUpId(label);
								resultList.add(newNode);
							}
						}
					}
				}
				String jql ="select info from BioneLabelInfo info where 1=1 and info.labelObjId = :labelObjId and info.labelId in :labelIds";
				Map<String,Object> conditions = new HashMap<String, Object>();
				conditions.put("labelIds", allLabelId);
				conditions.put("labelObjId", labelObjId);
				jql += " order by info.labelName ";
				List<BioneLabelInfo> labels = new ArrayList<BioneLabelInfo>();
				if(allLabelId != null && allLabelId.size()>0){
					labels = this.baseDAO.findWithNameParm(jql, conditions);
					if(labels != null && labels.size() > 0){
						for(BioneLabelInfo info : labels){
							resultList.add(generateLabelNode(contextPath,info));
						}
					}
				}
			}
		}
		if(type.equals("search")){
			resultList.add(generateRootNode(contextPath));
			if (StringUtils.isNotBlank(ids)) {
				String AllArray[] = StringUtils.split(ids, ",");
				List<String> rptLIds = null;
				for (int i = 0; i < AllArray.length; i++) {
					List<String> labelIdsList = ArrayUtils.asList(AllArray[i], ",");
					if (rptLIds == null) {
						rptLIds = this.labelBS.getObjIdByObjNo("idx", labelIdsList);
					} else {
						List<String> colIds = this.labelBS.getObjIdByObjNo("idx",
								labelIdsList);
						if (colIds.size() <= 0) {
							return resultList;
						}
						rptLIds.retainAll(colIds);
					}
				}
				if (rptLIds.size() <= 0) {
					return resultList;
				}
				List<RptIdxInfo> idxList = getIdxByConfig("", isShowDim, isShowMeasureInfo , "", "", "", "1", "", "1", "", "", rptLIds, isCabin, busiType);
				Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
				List<RptIdxMeasureInfoVO> measureList = new ArrayList<RptIdxMeasureInfoVO>();// 度量信息
				if (!StringUtils.isEmpty("1")
						&& isShowMeasureInfo.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
						for(RptIdxMeasureInfo info : infos){
							measureInfoMap.put(info.getMeasureNo(), info);
						}
						Map<String,Object> condition = new HashMap<String, Object>();
						condition.put("idxList", idxList);
						measureList = measureDao.getMeasure(condition);
					}
				}
				Map<String,List<RptIdxMeasureInfoVO>> measureMap = new HashMap<String, List<RptIdxMeasureInfoVO>>(); 
				if (measureList != null && measureList.size()>0){
					for(RptIdxMeasureInfoVO measure : measureList){
						RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
						measure.setMeasureNm(info.getMeasureNm());
						measure.setMeasureType(info.getMeasureType());
						measure.setCalcFormula(info.getCalcFormula());
						measure.setRemark(info.getRemark());
						List<RptIdxMeasureInfoVO> vos = measureMap.get(measure.getIndexNo()+"-"+measure.getIndexVerId());
						if(vos == null){
							vos = new ArrayList<RptIdxMeasureInfoVO>();
							measureMap.put(measure.getIndexNo()+"-"+measure.getIndexVerId(),vos);
						}
						vos.add(measure);
					}
				}
				List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
				List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
				List<String> indexNos = new ArrayList<String>();
				for(RptIdxInfo idx : idxList){
					if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
							|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
							||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
					{
						relidx.add(idx);
					}
					indexNos.add(idx.getId().getIndexNo());
				}
				if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						Map<String,Object> condition = new HashMap<String, Object>();
						
						if(relidx.size()>0){
							condition.put("idxList", relidx);
							dimRelList = this.getDimNosOfIndex(condition);
						}
					}
				}
				Map<String,String> dimRelMap = new HashMap<String, String>(); 
				if (dimRelList != null && dimRelList.size()>0){
					for(RptIdxDimRel rel : dimRelList){
						dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
					}
				}
				for(RptIdxInfo idx : idxList){
					CommonTreeNode newNode = generateIndexNode(contextPath, idx, isShowDim, isShowMeasureInfo, dimRelMap, measureMap);
					newNode.setUpId("0");
					resultList.add(newNode);
				}
			}
		}
		return resultList;
	}
	
	public List<CommonTreeNode> getSyncLabelTree(String contextPath,
			String searchNm, String indexVerId, String isShowDim, String isShowMeasureInfo, String isCabin, String busiType) {
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		List<RptIdxInfo> idxList = getIdxByConfig(searchNm, isShowMeasureInfo, isShowDim, "", "", "", "1", "", "1",  "", "", null, isCabin, busiType);
		if(idxList != null && idxList.size() > 0){
			Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
			List<RptIdxMeasureInfoVO> measureList = new ArrayList<RptIdxMeasureInfoVO>();// 度量信息
			if (!StringUtils.isEmpty(isShowMeasureInfo)
					&& isShowMeasureInfo.equals("1")) {
				if(idxList != null && idxList.size() > 0){
					List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
					for(RptIdxMeasureInfo info : infos){
						measureInfoMap.put(info.getMeasureNo(), info);
					}
					Map<String,Object> condition = new HashMap<String, Object>();
					condition.put("idxList", idxList);
					measureList = measureDao.getMeasure(condition);
				}
			}
			Map<String,List<RptIdxMeasureInfoVO>> measureMap = new HashMap<String, List<RptIdxMeasureInfoVO>>(); 
			if (measureList != null && measureList.size()>0){
				for(RptIdxMeasureInfoVO measure : measureList){
					RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
					measure.setMeasureNm(info.getMeasureNm());
					measure.setMeasureType(info.getMeasureType());
					measure.setCalcFormula(info.getCalcFormula());
					measure.setRemark(info.getRemark());
					List<RptIdxMeasureInfoVO> vos = measureMap.get(measure.getIndexNo()+"-"+measure.getIndexVerId());
					if(vos == null){
						vos = new ArrayList<RptIdxMeasureInfoVO>();
						measureMap.put(measure.getIndexNo()+"-"+measure.getIndexVerId(),vos);
					}
					vos.add(measure);
				}
			}
			List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
			List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
			List<String> indexNos = new ArrayList<String>();
			for(RptIdxInfo idx : idxList){
				if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
						|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
						||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
				{
					relidx.add(idx);
				}
				indexNos.add(idx.getId().getIndexNo());
			}
			if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
				if(idxList != null && idxList.size() > 0){
					Map<String,Object> condition = new HashMap<String, Object>();
					
					if(relidx.size()>0){
						condition.put("idxList", relidx);
						dimRelList = this.getDimNosOfIndex(condition);
					}
				}
			}
			Map<String,String> dimRelMap = new HashMap<String, String>(); 
			if (dimRelList != null && dimRelList.size()>0){
				for(RptIdxDimRel rel : dimRelList){
					dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
				}
			}
			Set<String> labelIds = new HashSet<String>();
			Map<String,List<String>> idxlabelMap = this.labelBS.getIdxLabelMap("idx", indexNos);
			BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", "idx");
			String labelObjId = "";
			if(obj!=null){
				labelObjId = obj.getLabelObjId();
			}
			boolean nullFlag = false;
			for(RptIdxInfo idx : idxList){
				CommonTreeNode node = generateIndexNode(contextPath, idx, isShowDim, isShowMeasureInfo, dimRelMap, measureMap);
				List<String> labels = idxlabelMap.get(idx.getId().getIndexNo());
				if(labels != null && labels.size()>0){
					for(String label : labels){
						CommonTreeNode newNode = generateIndexNode(contextPath, idx, isShowDim, isShowMeasureInfo, dimRelMap, measureMap);
						newNode.setUpId(label);
						resultList.add(newNode);
						labelIds.add(label);
					}
				}
				else{
					node.setUpId("nullLabel");
					resultList.add(node);
					nullFlag = true;
				}
			}
			if(nullFlag){
				resultList.add(generateNullIdxNode(contextPath));
			}
			List<BioneLabelInfo> labels = new ArrayList<BioneLabelInfo>();
			Set<String> allLabelId = new HashSet<String>();
			this.getAllLabelInfo(labelIds, allLabelId, labelObjId);
			String jql ="select info from BioneLabelInfo info where 1=1 and info.labelObjId = :labelObjId and info.labelId in :labelIds";
			Map<String,Object> conditions = new HashMap<String, Object>();
			conditions.put("labelIds", allLabelId);
			conditions.put("labelObjId", labelObjId);
			jql += " order by info.labelName ";
			if(allLabelId != null && allLabelId.size()>0){
				labels = this.baseDAO.findWithNameParm(jql, conditions);
				if(labels != null && labels.size() > 0){
					for(BioneLabelInfo info : labels){
						resultList.add(generateLabelNode(contextPath,info));
					}
				}
			}
			else{
				return resultList;
			}
			
		}
		return resultList;
	}
	
	public List<CommonTreeNode> getAsyncLabelTree(String contextPath,
			String searchNm, String upId, String indexVerId, String isShowDim, String isShowMeasureInfo, String nodeType, String isCabin, String busiType) {
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		if(!StringUtils.isNotBlank(upId)){
			upId = "0";
			resultList.add(generateNullIdxNode(contextPath));
			List<BioneLabelInfo> infos =this.labelBS.getLabelByObjNo("idx", upId);
			if(infos != null && infos.size()>0){
				for(BioneLabelInfo info : infos){
					resultList.add(generateLabelNode(contextPath,info));
				}
			}
			return resultList;
		}
		else if(nodeType.equals("label")){
			boolean superFlag = true;
			List<String> authlist = new ArrayList<String>();
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				superFlag =false;
				authlist = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_IDX");
			}
			if(upId.equals("nullLabel")){
				List<Object[]> vos = new ArrayList<Object[]>();
				List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
				List<String> objIds = this.labelBS.getObjIdByObjNo("idx");
				Map<String,Object> params = new HashMap<String, Object>();
				String jql = "select idx,ext from RptIdxInfo idx,RptIdxBusiExt ext where idx.id.indexNo = ext.id.indexNo and idx.id.indexVerId =ext.id.indexVerId and idx.endDate = :endDate and idx.indexSts = :indexSts and idx.isRptIndex = :isRptIndex ";
				if(StringUtils.isNotBlank(searchNm)){
					jql += " and idx.indexNm = :indexNm";
					params.put("indexNm", searchNm);
				}
				params.put("endDate",  "29991231");
				params.put("indexSts",  "Y");
				params.put("isRptIndex",  "N");
				if(StringUtils.isNotBlank(busiType)){
					jql += " and (idx.busiType = :busiType or idx.busiType = " + GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC + ")";//业务类型为传参加公共类型
					params.put("busiType", busiType);
				}
				vos = this.baseDAO.findWithNameParm(jql, params);
				if(vos != null && vos.size() > 0){
					for(Object[] vo : vos){
						RptIdxInfo idx = (RptIdxInfo) vo[0];
						RptIdxBusiExt ext = (RptIdxBusiExt) vo[1];
						idx.setRemark("业务定义："+(ext.getBusiDef()== null ? "无": ext.getBusiDef())+"\n"+"业务口径："+(ext.getBusiRule()== null ? "无": ext.getBusiRule()));
						idxs.add(idx);
					}
				}
				Map<String,Object> condition = new HashMap<String, Object>();
				List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
				if(idxs != null && idxs.size() > 0){
					Map<String,String> dimRelMap = new HashMap<String, String>(); 
					if("1".equals(isShowDim)){
						List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
						for(RptIdxInfo idx : idxs){
							if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
									|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
									||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
							{
								relidx.add(idx);
							}
						}
						if(relidx.size()>0){
							condition.put("idxList", relidx);
							dimRelList = this.getDimNosOfIndex(condition);
							if (dimRelList != null && dimRelList.size()>0){
								for(RptIdxDimRel rel : dimRelList){
									dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
								}
							}
						}
					}
					for(RptIdxInfo idx : idxs){
						if(!superFlag){
							if(authlist.contains(idx.getId().getIndexNo())&&!objIds.contains(idx.getId().getIndexNo()))
								resultList.add(generateIndexNode(contextPath, idx, isShowDim, isShowMeasureInfo, dimRelMap, null));
						}
						else{
							if(!objIds.contains(idx.getId().getIndexNo()))
								resultList.add(generateIndexNode(contextPath,idx, isShowDim, isShowMeasureInfo, dimRelMap, null));
						}
					}
				}
			}
			else{
				List<Object[]> vos = new ArrayList<Object[]>();
				List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
				List<BioneLabelInfo> infos =this.labelBS.getLabelByObjNo("idx", upId);
				String jql = "select idx,ext from RptIdxInfo idx,RptIdxBusiExt ext,BioneLabelObjRel rel,BioneLabelObjInfo obj where idx.id.indexNo = ext.id.indexNo and idx.id.indexVerId =ext.id.indexVerId and obj.labelObjNo = :labelObjNo and obj.labelObjId = rel.id.labelObjId and rel.id.labelId =:labelId and rel.id.objId = idx.id.indexNo and idx.endDate = :endDate and idx.indexSts = :indexSts and idx.isRptIndex = :isRptIndex ";
				Map<String,Object> params = new HashMap<String, Object>();
				if(infos != null && infos.size()>0){
					for(BioneLabelInfo info : infos){
						resultList.add(generateLabelNode(contextPath,info));
					}
				}
				if(StringUtils.isNotBlank(searchNm)){
					jql += " and idx.indexNm = :indexNm";
					params.put("indexNm", searchNm);
				}
				if(StringUtils.isNotBlank(isCabin)){
					jql += " and idx.isCabin = :isCabin";
					params.put("isCabin",  "1");
				}
				params.put("labelObjNo",  "idx");
				params.put("labelId",  upId);
				params.put("endDate",  "29991231");
				params.put("indexSts",  "Y");
				params.put("isRptIndex",  "N");
				
//				List<RptIdxInfo> idxs = this.baseDAO.findWithNameParm(jql, params);
				vos = this.baseDAO.findWithNameParm(jql, params);
				if(vos != null && vos.size() > 0){
					Set<String> srcIndexNos = new HashSet<String>();
					for(Object[] vo : vos){
						RptIdxInfo idx = (RptIdxInfo) vo[0];
						RptIdxBusiExt ext = (RptIdxBusiExt) vo[1];
						
						idx.setRemark("业务定义："+(ext.getBusiDef()== null ? "无": ext.getBusiDef())+"\n"+"业务口径："+(ext.getBusiRule()== null ? "无": ext.getBusiRule()));
						if(!srcIndexNos.contains(idx.getId().getIndexNo())) {
							idxs.add(idx);
							srcIndexNos.add(idx.getId().getIndexNo());
						}
					}
				}
				List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
				Map<String,Object> condition = new HashMap<String, Object>();
				if(idxs != null && idxs.size() > 0){
					Map<String,String> dimRelMap = new HashMap<String, String>(); 
					if("1".equals(isShowDim)){
						List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
						for(RptIdxInfo idx : idxs){
							if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
									|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
									||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
							{
								relidx.add(idx);
							}
						}
						if(relidx.size()>0){
							condition.put("idxList", relidx);
							dimRelList = this.getDimNosOfIndex(condition);
							if (dimRelList != null && dimRelList.size()>0){
								for(RptIdxDimRel rel : dimRelList){
									dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
								}
							}
						}
					}
					for(RptIdxInfo idx : idxs){
						if(!superFlag){
							if(authlist.contains(idx.getId().getIndexNo()))
								resultList.add(generateIndexNode(contextPath, idx, isShowDim, isShowMeasureInfo, dimRelMap, null));
						}
						else{
							resultList.add(generateIndexNode(contextPath, idx, isShowDim, isShowMeasureInfo, dimRelMap, null));
						}
					}
				}
			}
			
		}
		else if (nodeType.equals(nodeTypeIdx)
				&& StringUtils.isNotEmpty(indexVerId)) {
			resultList.addAll(generateMeasureNode(contextPath, upId, indexVerId,isShowDim));
		}
		return resultList;
	}
	
	private CommonTreeNode generateNullIdxNode(String contextPath){
		CommonTreeNode nnode = new CommonTreeNode();
		nnode.setId("nullLabel");
		nnode.setText("（无标签）");
		nnode.setUpId("0");
		nnode.setIcon(contextPath
				+ "/images/classics/icons/tag_blue.png");
		nnode.setIsParent(true);
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("nodeType", "label");
		nnode.setParams(paramMap);
		return nnode;
	}
	
	private CommonTreeNode generateRootNode(String basePath){
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.setText("全部");
		baseNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.getParams().put("type", "root");
		baseNode.getParams().put("nodeType", "idxCatalog");
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		RptIdxCatalog catalogBase = new RptIdxCatalog();
		baseNode.setData(catalogBase);
		catalogBase.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
		return baseNode;
	}
	private CommonTreeNode generateCatalogNode(String basePath,RptIdxCatalog catalog){
		CommonTreeNode node = new CommonTreeNode();
		node.setId(catalog.getIndexCatalogNo());
		node.setText(catalog.getIndexCatalogNm());
		node.setData(catalog);
		node.setUpId(catalog.getUpNo());
		node.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
		node.getParams().put("type", "idxCatalog");
		node.getParams().put("nodeType", "idxCatalog");
		return node;
	}
	
	private CommonTreeNode generateIndexNode(String basePath,RptIdxInfo idx, 
			String isShowDim, String isShowMeasureInfo,
			Map<String, String> dimRelMap,
			Map<String,List<RptIdxMeasureInfoVO>> measureMap) {
		CommonTreeNode tmp = new CommonTreeNode();
		tmp.setId(idx.getId().getIndexNo());
//		tmp.setText("["+idx.getId().getIndexNo()+"]" + idx.getIndexNm());
		tmp.setText(idx.getIndexNm());
		tmp.setTitle(idx.getRemark());
		tmp.setData(idx);
		tmp.setUpId(idx.getIndexCatalogNo());
		tmp.setIcon(basePath
				+ "/images/classics/menuicons/grid.png");
		tmp.getParams().put("id",
				idx.getId().getIndexNo());
		tmp.getParams().put("type", "idxInfo");
		tmp.getParams().put("nodeType", "idxInfo");
		tmp.getParams().put("indexType", idx.getIndexType());
		tmp.getParams().put("indexNo",
				idx.getId().getIndexNo());
		tmp.getParams().put("indexNm",
				idx.getIndexNm());
		tmp.getParams().put(
				"indexVerId",
				String.valueOf((idx.getId()
						.getIndexVerId())));
		tmp.getParams().put("resDefNo",
				com.yusys.bione.plugin.base.common.GlobalConstants4plugin.IDX_RES_NO);
		tmp.getParams().put("isSum", idx.getIsSum());
		tmp.getParams().put("dataType", idx.getDataType());
		tmp.getParams().put("dataUnit", idx.getDataUnit());
		tmp.getParams().put("statType", idx.getStatType());
		tmp.getParams().put("isLeaf", "true");
		
		// 给指标增加其维度信息
		if (!StringUtils.isEmpty(isShowDim)
				&& isShowDim.equals("1")) {
			if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
					|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
					||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX)){
				tmp.getParams().put("dimNos",dimRelMap.get(idx.getId().getIndexNo()+"-"+idx.getId()
						.getIndexVerId()));		
			}
			else{
				tmp.getParams().put("dimNos",com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMMON_DIM_TYPE);		
			}
		}
		// 给总账指标增加度量信息作为子节点
		if (!StringUtils.isEmpty(isShowMeasureInfo)
				&& isShowMeasureInfo.equals("1")
				&& idx.getIndexType() != null
				&& idx
						.getIndexType()
						.equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)) {
			tmp.getParams().put("haveMeasure", "true");
			tmp.setIsParent(true);
			if(measureMap != null){
				List<CommonTreeNode> measureNodes = new ArrayList<CommonTreeNode>();
				List<RptIdxMeasureInfoVO> measureList = measureMap.get(idx.getId().getIndexNo()+"-"+idx.getId()
						.getIndexVerId());
				if(measureList != null && measureList.size()>0){
					for (RptIdxMeasureInfoVO measure : measureList) {
						CommonTreeNode node = new CommonTreeNode();
						node.setUpId(idx.getId()
								.getIndexNo());
						node.setId(measure.getMeasureNo());
						node.setText(measure.getMeasureNm());
						node.setData(idx);
						node.setIcon(basePath
								+ "/images/classics/menuicons/computer.png");
						node.getParams().put("nodeType", "measureInfo");
						node.getParams().put("indexNo",
								idx.getId().getIndexNo());
						node.getParams().put(
								"indexVerId",
								String.valueOf(idx.getId()
										.getIndexVerId()));
						node.getParams().put(
								"display",
								idx.getIndexNm() + "."
										+ measure.getMeasureNm());
						if (!StringUtils.isEmpty(isShowDim)
								&& isShowDim.equals("1")) {
							node.getParams().put("dimNos",com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMMON_DIM_TYPE);	
						}
						measureNodes.add(node);
					}
					tmp.setChildren(measureNodes);
				}
			}
			
			
		}
		return tmp;
	} 
	
	private List<CommonTreeNode> generateMeasureNode(String basePath,String upId,String indexVerId,String isShowDim){
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
		List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
		for(RptIdxMeasureInfo info : infos){
			measureInfoMap.put(info.getMeasureNo(), info);
		}
		Map<String,Object> condition = new HashMap<String, Object>();
		RptIdxMeasureRelPK pk = new RptIdxMeasureRelPK();
		pk.setIndexNo(upId);
		pk.setIndexVerId(new Long(indexVerId));
		condition.put("id", pk);
		List<RptIdxMeasureInfoVO> measureList = measureDao.getMeasure(condition);
		String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.isRptIndex = ?1 and idx.id.indexVerId = ?2";
		RptIdxInfo idx = this.baseDAO.findUniqueWithIndexParam(jql, upId,"N",new Long(indexVerId));
		for (RptIdxMeasureInfoVO measure : measureList) {
			RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
			if(info!=null){
				CommonTreeNode node = new CommonTreeNode();
				node.setUpId(upId);
				node.setData(idx);
				node.setId(measure.getMeasureNo());
				node.setText(info.getMeasureNm());
				node.setIcon(basePath
						+ "/images/classics/menuicons/computer.png");
				node.getParams().put("nodeType", "measureInfo");
				node.getParams().put("indexNo", upId);
				node.getParams().put("indexVerId", indexVerId);
				if (!StringUtils.isEmpty(isShowDim)
						&& isShowDim.equals("1")) {
					node.getParams().put("dimNos",com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMMON_DIM_TYPE);	
				}
				resultList.add(node);
			}
		}
		return resultList;
	}
	
	private CommonTreeNode generateLabelNode(String basePath,BioneLabelInfo info){
		CommonTreeNode node = new CommonTreeNode();
		node.setId(info.getLabelId());
		node.setText(info.getLabelName());
		node.setUpId(info.getUpId());
		node.setIcon(basePath
				+ "/images/classics/icons/tag_blue.png");
		node.setData(info);
		node.setIsParent(true);
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("nodeType", "label");
		node.setParams(paramMap);
		return node;
	}
	
	/**
	 * 报表设计中用到的指标信息
	 * 
	 * @param contextPath
	 * @param upId
	 * @param indexVerId
	 * @param nodeType
	 * @param defSrc
	 * @return
	 */
	public List<CommonTreeNode> getTreeByRptDesign(String contextPath,
			String upId, String indexVerId, String nodeType, String defSrc) {
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		String globalRoot = "#ytec_common_root#";
		String orgRoot = "#ytec_org_root#";
		String userRoot = "#ytec_user_root#";
		if (StringUtils.isEmpty(upId)) {
			// 初始化根节点，全部节点，分行节点，用户节点
			CommonTreeNode catalog = new CommonTreeNode();
			catalog.setId(globalRoot);
			catalog.setText("公共指标");
			catalog.setIsParent(true);
			catalog.setUpId("-1");
			catalog.setIcon(contextPath + GlobalConstants4frame.LOGIC_MODULE_ICON);
			catalog.getParams().put("nodeType", this.rootCatalog);
			catalog.getParams()
					.put("defSrc",
							GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
			resultList.add(catalog);
			// 判断该用户是否具备分行指标和用户指标
			Map<String, Object> orgIdxParams = new HashMap<String, Object>();
			orgIdxParams
					.put("defSrc",
							GlobalConstants4plugin.INDEX_DEF_SRC_ORG);
			orgIdxParams.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
			List<RptIdxInfo> orgInfos = this.idxDao.getOrgIdxs(orgIdxParams);
			Map<String, Object> userIdxParams = new HashMap<String, Object>();
			userIdxParams
					.put("defSrc",
							GlobalConstants4plugin.INDEX_DEF_SRC_USER);
			userIdxParams.put("defUser", BioneSecurityUtils.getCurrentUserId());
			userIdxParams.put("indexSts", "Y");
			List<RptIdxInfo> userInfos = this.idxDao.getUserIdxs(userIdxParams);
			if (orgInfos != null && orgInfos.size() > 0) {
				// 存在分行指标
				CommonTreeNode catalog2 = new CommonTreeNode();
				catalog2.setId(orgRoot);
				catalog2.setText("分行指标");
				catalog2.setIsParent(true);
				catalog2.setUpId("-1");
				catalog2.setIcon(contextPath + GlobalConstants4frame.LOGIC_ORG_ICON);
				catalog2.getParams().put("nodeType", this.rootCatalog);
				catalog2.getParams()
						.put("defSrc",
								GlobalConstants4plugin.INDEX_DEF_SRC_ORG);
				resultList.add(catalog2);
			}
			if (userInfos != null && userInfos.size() > 0) {
				// 存在用户指标
				CommonTreeNode catalog3 = new CommonTreeNode();
				catalog3.setId(userRoot);
				catalog3.setText("用户指标");
				catalog3.setIsParent(true);
				catalog3.setUpId("-1");
				catalog3.setIcon(contextPath + GlobalConstants4frame.LOGIC_USER_ICON);
				catalog3.getParams().put("nodeType", this.rootCatalog);
				catalog3.getParams()
						.put("defSrc",
								GlobalConstants4plugin.INDEX_DEF_SRC_USER);
				resultList.add(catalog3);
			}
			return resultList;
		}
		if (rootCatalog.equals(nodeType)) {
			// 点击根节点
			upId = "0";
			nodeType = nodeTypeFoler;
		}
		if (StringUtils.isEmpty(nodeType)) {
			nodeType = nodeTypeFoler;
		}
		Map<String, Object> condition = new HashMap<String, Object>();
		if (nodeType.equals(nodeTypeFoler)) {
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			condition.put("upNo", upId);
			condition.put("needOrderByNm", true);
			if (StringUtils.isNotEmpty(defSrc)) {
				if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
					condition.put("defOrg", user.getOrgNo());
					condition.put("defSrc", defSrc);
				} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
					condition.put("defUser", user.getUserId());
					condition.put("defSrc", defSrc);
				} else {
					condition
							.put("defaultDefSrc",
									com.yusys.bione.plugin.base.common.GlobalConstants4plugin.RPT_DEF_SRC_LIB);
				}
			}
			List<RptIdxCatalog> catalogList = this.catalogDao
					.listIdxCatalog(condition);
			for (RptIdxCatalog tmp : catalogList) {
				CommonTreeNode catalog = new CommonTreeNode();
				catalog.setId(tmp.getIndexCatalogNo());
				catalog.setText(tmp.getIndexCatalogNm());
				catalog.setData(tmp);
				catalog.setIsParent(true);
				String upIdTmp = tmp.getIndexCatalogNo();
				if (rootCatalog.equals(nodeType)) {
					if (com.yusys.bione.plugin.base.common.GlobalConstants4plugin.INDEX_DEF_SRC_LIB
							.equals(nodeType)) {
						upIdTmp = globalRoot;
					} else if (com.yusys.bione.plugin.base.common.GlobalConstants4plugin.INDEX_DEF_SRC_ORG
							.equals(nodeType)) {
						upIdTmp = orgRoot;
					} else if (com.yusys.bione.plugin.base.common.GlobalConstants4plugin.INDEX_DEF_SRC_USER
							.equals(nodeType)) {
						upIdTmp = userRoot;
					}
				}
				catalog.setUpId(upIdTmp);
				catalog.setIcon(contextPath + GlobalConstants4frame.LOGIC_MODULE_ICON);
				catalog.getParams().put("nodeType", "idxCatalog");
				if (StringUtils.isNotEmpty(defSrc)) {
					catalog.getParams().put("defSrc", defSrc);
				}
				resultList.add(catalog);
			}
			condition.clear();
			condition.put("indexCatalogNo", upId);
			condition.put("needOrderByNo", true);
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
					&& (StringUtils.isEmpty(defSrc) || com.yusys.bione.plugin.base.common.GlobalConstants4plugin.INDEX_DEF_SRC_LIB
							.equals(defSrc))) {
				List<String> list = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_IDX");
				// List<String>
				// list=this.idxBS.getIdxByUserId(BioneSecurityUtils.getCurrentUserId());
				if (list != null && list.size() > 0) {
					condition.put("list", ReBuildParam.splitLists(list));
				}
			}
			if (StringUtils.isNotEmpty(defSrc)) {
				if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
					condition.put("defSrc", defSrc);
					condition.put("defOrg", user.getOrgNo());
				} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
					condition.put("defUser", user.getUserId());
					condition.put("defSrc", defSrc);
				} else {
					condition
							.put("defaultDefSrc",
									com.yusys.bione.plugin.base.common.GlobalConstants4plugin.RPT_DEF_SRC_LIB);
				}
			}
			if ("0".equals(upId)) {
				// 不需要判断是否有目录
				condition.put("noNeedCatalog", 'Y');
			}
			condition.put("indexSts", "Y");
			List<RptIdxInfo> idxList = this.idxDao.listIdxInfo(condition);
			for (RptIdxInfo tmp : idxList) {
				CommonTreeNode idxNode = new CommonTreeNode();
				idxNode.setId(tmp.getId().getIndexNo());
				idxNode.setText(tmp.getIndexNm());
				idxNode.setData(tmp);
				idxNode.setUpId(tmp.getId().getIndexNo());
				idxNode.setIcon(contextPath
						+ "/images/classics/menuicons/grid.png");
				idxNode.getParams().put("nodeType", "idxInfo");
				idxNode.getParams().put("indexNo", tmp.getId().getIndexNo());
				idxNode.getParams().put("indexVerId",
						String.valueOf((tmp.getId().getIndexVerId())));
				idxNode.getParams().put("isSum", tmp.getIsSum());
				if (StringUtils.isNotEmpty(defSrc)) {
					idxNode.getParams().put("defSrc", defSrc);
				}
				if (tmp.getIndexType()
						.equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)) {
					idxNode.getParams().put("haveMeasure", "true");
					idxNode.setIsParent(true);
				} else {
					idxNode.setIsParent(false);
				}
				resultList.add(idxNode);
			}
		} else if (nodeType.equals(nodeTypeIdx)
				&& StringUtils.isNotEmpty(indexVerId)) {
			condition.clear();
			RptIdxMeasureRelPK pk = new RptIdxMeasureRelPK();
			pk.setIndexNo(upId);
			pk.setIndexVerId(new Long(indexVerId));
			condition.put("id", pk);
			List<RptIdxMeasureInfoVO> measureList = measureDao.list(condition);
			for (RptIdxMeasureInfoVO measure : measureList) {
				CommonTreeNode node = new CommonTreeNode();
				node.setUpId(measure.getMeasureNo());
				node.setId(measure.getMeasureNo());
				node.setText(measure.getMeasureNm());
				node.setIcon(contextPath
						+ "/images/classics/menuicons/computer.png");
				node.getParams().put("nodeType", "measureInfo");
				node.getParams().put("indexNo", upId);
				node.getParams().put("indexVerId", indexVerId);
				if (StringUtils.isNotEmpty(defSrc)) {
					node.getParams().put("defSrc", defSrc);
				}
				resultList.add(node);
			}
		}
		return resultList;
	}

	private void searchParent(List<CommonTreeNode> list,
			Map<String, Object> catalogMap, CommonTreeNode node) {
		CommonTreeNode treeNode = (CommonTreeNode) catalogMap.get(node
				.getUpId());
		boolean flag = list.contains(treeNode);
		if (treeNode != null && !flag) {
			treeNode.getParams().put("hasInfos", "true");// contains会忽略params属性
			list.add(treeNode);
			searchParent(list, catalogMap, treeNode);
		}
	}

	private void setHasInfosFlag(CommonTreeNode node,
			Map<String, Object> catalogMap, Map<String, Object> map) {// 该方式存在重复的赋值操作
		CommonTreeNode treeNode = (CommonTreeNode) catalogMap.get(node
				.getUpId());
		if (treeNode != null && !map.containsKey(treeNode.getId())) {
			treeNode.getParams().put("hasInfos", "true");
			map.put(treeNode.getId(), null);
			setHasInfosFlag(treeNode, catalogMap, map);
		}
	}

	public List<CommonTreeNode> createTreeNode(List<CommonTreeNode> nodes,
			boolean setHasInfosFlag, Map<String, Object> catalogMap,
			String basePath) {
		List<CommonTreeNode> result = Lists.newArrayList();
		Map<String, CommonTreeNode> cache = createMapCache(nodes);
		CommonTreeNode parent = null;
		for (CommonTreeNode node : nodes) {
			parent = cache.get(node.getUpId());
			if (parent == null) {
				result.add(node);
			} else {
				parent.addChildNode(node);
			}
			String hasInfos = node.getParams().get("hasInfos");
			if (setHasInfosFlag && hasInfos != null && hasInfos.equals("true")) {// 1.没有搜索条件&&没有排除的ids；2.node是info的直接上级catalog
				setHasInfosFlag(node, catalogMap, new HashMap<String, Object>());
			}
		}
		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.setText("全部");
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		RptIdxCatalog catalogBase = new RptIdxCatalog();
		catalogBase.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
		baseNode.setData(catalogBase);
		baseNode.setChildren(result);
		result = Lists.newArrayList();
		result.add(baseNode);
		return result;
	}

	private Map<String, CommonTreeNode> createMapCache(
			List<CommonTreeNode> nodes) {
		Map<String, CommonTreeNode> result = Maps.newHashMap();
		for (CommonTreeNode node : nodes) {
			result.put(node.getId(), node);
		}
		return result;
	}

	public RptIdxCatalog getRptIdxCatalogById(String indexCatalogNo,
			String defSrc) {
		Map<String, Object> params = Maps.newHashMap();
		if (StringUtils.isNotEmpty(defSrc)) {
			params.put("defSrc", defSrc);
		}
		params.put("indexCatalogNo", indexCatalogNo);
		List<RptIdxCatalog> list = this.catalogDao.listIdxCatalog(params);
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);

	}

	public void createRptIdxCatalog(RptIdxCatalog model) {
		this.catalogDao.saveIdxCatalog(model);
	}

	public void updateCatalog(RptIdxCatalog model) {
		this.catalogDao.updateIdxCatalog(model);
	}

	public void updateIndex(RptIdxInfo model) {
		this.idxDao.updateIdxInfo(model);
	}

	@Transactional(readOnly = false)
	public void cascadeDel(String id, String defSrc) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("upNo", id);
		params.put("defSrc", defSrc);
		List<RptIdxCatalog> catalogs = this.catalogDao.listIdxCatalog(params);
		for (int i = 0; i < catalogs.size(); i++) {
			RptIdxCatalog cata = catalogs.get(i);
			cascadeDel(cata.getIndexCatalogNo(), defSrc);
		}
		params.put("indexCatalogNo", id);
		this.catalogDao.deleteIdxCatalog(params);

	}

	public Integer testSameIndexCatalogNm(Map<String, Object> params) {
		return this.catalogDao.testSameIndexCatalogNm(params);
	}

	/**
	 * 派生指标取维度的方法，方娟
	 * 
	 * @param idxNos
	 * @param indexVerIds
	 * @return
	 */
	public Map<String, CommonComboBoxNode> getExtendsDimByIdx(String idxNos,
			String indexVerIds) {
		Map<String, CommonComboBoxNode> map = new HashMap<String, CommonComboBoxNode>();
		if (!StringUtils.isEmpty(idxNos) && !StringUtils.isEmpty(indexVerIds)) {
			if (idxNos.endsWith(",")) {
				idxNos = idxNos.substring(0, idxNos.length() - 1);
			}
			if (indexVerIds.endsWith(",")) {
				indexVerIds = indexVerIds
						.substring(0, indexVerIds.length() - 1);
			}
			String idxNo[] = StringUtils.split(idxNos, ',');
//			Map<String, Object> condition = new HashMap<String, Object>();
			List<String> idxNoList = new ArrayList<String>();
			for (String idxNoTmp : idxNo) {
				if (!idxNoList.contains(idxNoTmp)) {
					idxNoList.add(idxNoTmp);
				}
			}
			/*List<List<String>> indexNosParam = new ArrayList<List<String>>();
			if (idxNoList.size() > 1000) {
				int index = 0;
				int remain = idxNoList.size();
				while (remain > 1000) {
					indexNosParam.add(idxNoList.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < idxNoList.size()) {
					indexNosParam
							.add(idxNoList.subList(index, idxNoList.size()));
				}
			} else {
				indexNosParam.add(idxNoList);
			}*/
			Map<String, Object> sysDimMap = new HashMap<String, Object>();
//			condition.put("ids", indexNosParam);
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String necessaryDimTypeKeys = propertiesUtils
					.getProperty("extendsIdxNeedDim");// 派生指标的必选维度，无币种维度
			if (necessaryDimTypeKeys != null
					&& !necessaryDimTypeKeys.equals("")) {
				String[] arr = StringUtils.split(necessaryDimTypeKeys, ',');
				for (int i = 0; i < arr.length; i++) {
					String temp = arr[i];
					sysDimMap.put(propertiesUtils.getProperty(temp),
							propertiesUtils.getProperty(temp));
				}

			}
			//修改派生指标取来源指标的公共维度 方娟  2016-02-17
			//之前的方法取的是并集，有错误，后改为求交集
//			List<RptDimTypeInfo> infos = this.relDao.getDimByIdxInfo(condition);
			String jql = "select dim from RptDimTypeInfo dim where dim.dimTypeNo in " +
					"(select rel.id.dimNo from RptIdxDimRel rel, RptIdxInfo idx " +
					" where rel.id.indexNo in(?0) and idx.endDate = '29991231'" +
					" and rel.id.indexNo = idx.id.indexNo and rel.id.indexVerId = idx.id.indexVerId  " +
					"  group by rel.id.dimNo having count(rel.id.dimNo) = ?1)";
			List<RptDimTypeInfo> infos = this.baseDAO.findWithIndexParam(jql, idxNoList, new Long(idxNoList.size()));

			for (RptDimTypeInfo tmp : infos) {
				if (!sysDimMap.containsKey(tmp.getDimTypeNo())) {
					CommonComboBoxNode node = new CommonComboBoxNode();
					node.setId(tmp.getDimTypeNo());
					node.setText(tmp.getDimTypeNm());
					map.put(tmp.getDimTypeNo(), node);
				}
			}
		}
		return map;
	}

	public List<CommonComboBoxNode> getDimByIdx(String idxNos,
			String indexVerIds) {
		Map<String, CommonComboBoxNode> map = new HashMap<String, CommonComboBoxNode>();
		if (!StringUtils.isEmpty(idxNos) && !StringUtils.isEmpty(indexVerIds)) {
			if (idxNos.endsWith(",")) {
				idxNos = idxNos.substring(0, idxNos.length() - 1);
			}
			if (indexVerIds.endsWith(",")) {
				indexVerIds = indexVerIds
						.substring(0, indexVerIds.length() - 1);
			}
			String idxNo[] = StringUtils.split(idxNos, ',');
			Map<String, Object> condition = new HashMap<String, Object>();
			List<String> idxNoList = new ArrayList<String>();
			for (String idxNoTmp : idxNo) {
				if (!idxNoList.contains(idxNoTmp)) {
					idxNoList.add(idxNoTmp);
				}
			}
			List<List<String>> indexNosParam = new ArrayList<List<String>>();
			if (idxNoList.size() > 1000) {
				int index = 0;
				int remain = idxNoList.size();
				while (remain > 1000) {
					indexNosParam.add(idxNoList.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < idxNoList.size()) {
					indexNosParam
							.add(idxNoList.subList(index, idxNoList.size()));
				}
			} else {
				indexNosParam.add(idxNoList);
			}
			Map<String, Object> sysDimMap = new HashMap<String, Object>();
			condition.put("ids", indexNosParam);
			// condition.put("dimType", DIM_TYPE_BUSI);
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String necessaryDimTypeKeys = propertiesUtils
					.getProperty("necessaryDimTypeKeys");
			if (necessaryDimTypeKeys != null
					&& !necessaryDimTypeKeys.equals("")) {
				String[] arr = StringUtils.split(necessaryDimTypeKeys, ',');
				for (int i = 0; i < arr.length; i++) {
					String temp = arr[i];
					sysDimMap.put(propertiesUtils.getProperty(temp),
							propertiesUtils.getProperty(temp));
				}

			}
			List<RptDimTypeInfo> infos = this.relDao.getDimByIdxInfo(condition);
			for (RptDimTypeInfo tmp : infos) {
				if (!sysDimMap.containsKey(tmp.getDimTypeNo())) {
					CommonComboBoxNode node = new CommonComboBoxNode();
					node.setId(tmp.getDimTypeNo());
					node.setText(tmp.getDimTypeNm());
					map.put(tmp.getDimTypeNo(), node);
				}
			}
		}
		List<CommonComboBoxNode> result = new ArrayList<CommonComboBoxNode>();
		for (Object object : map.keySet()) {
			result.add((CommonComboBoxNode) map.get(object));
		}
		return result;

	}

	public List<CommonComboBoxNode> getSaticDim() {
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String neDimTypeKey = propertiesUtils.getProperty("extendsIdxNeedDim");
		if (neDimTypeKey != null) {
			List<CommonComboBoxNode> result = new ArrayList<CommonComboBoxNode>();
			String keys[] = StringUtils.split(neDimTypeKey, ',');
			for (String tmp : keys) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(propertiesUtils.getProperty(tmp));
				if (tmp.equals("orgDimTypeNo")) {
					node.setText("机构");
				} else if (tmp.equals("dateDimTypeNo")) {
					node.setText("日期");
				} else if (tmp.equals("currencyDimTypeNo")) {
					node.setText("币种");
				} else if (tmp.equals("indexDimTypeNo")) {
					node.setText("指标号");
				}
				result.add(node);
			}
			return result;
			/*
			 * List<CommonComboBoxNode> result = new
			 * ArrayList<CommonComboBoxNode>(); CommonComboBoxNode orgNode = new
			 * CommonComboBoxNode();
			 * orgNode.setId(propertiesUtils.getProperty("orgDimTypeNo"));
			 * orgNode.setText("机构"); for(String tmp:keys){
			 * if(tmp.equals(orgNode.getId())){ result.add(orgNode); } }
			 * 
			 * CommonComboBoxNode dateNode = new CommonComboBoxNode();
			 * dateNode.setId(propertiesUtils.getProperty("dateDimTypeNo"));
			 * dateNode.setText("时间"); for(String tmp:keys){
			 * if(tmp.equals(orgNode.getId())){ result.add(dateNode); } }
			 * 
			 * 
			 * CommonComboBoxNode currencyNode = new CommonComboBoxNode();
			 * currencyNode
			 * .setId(propertiesUtils.getProperty("currencyDimTypeNo"));
			 * currencyNode.setText("币种"); for(String tmp:keys){
			 * if(tmp.equals(orgNode.getId())){ result.add(currencyNode); } }
			 * 
			 * return result;
			 */
		}
		return null;
	}

	public SearchResult<RptIdxInfo> getIdxInfoListByParams(Pager pager,
			Map<String, Object> param) {
		Map<String, Object> params = Maps.newHashMap();
		if (param.get("indexNm") != null) {
			params.put("indexNm", "%" + (String) param.get("indexNm") + "%");
		}
		JSONObject json = null;
		json = JSON.parseObject(pager.getCondition());
		JSONArray array = json.getJSONArray("rules");
		for (int i = 0; i < array.size(); i++) {
			JSONObject temp = array.getJSONObject(i);
			String filedName = (String) temp.get("field");
			if (filedName.equals("indexNm")) {
				params.put("indexNm", "%" + temp.get("value") + "%");
				array.remove(i);
				break;
			}

		}
		json.put("rules", array.toString());
		pager.setCondition(json.toString());
		PageHelper.startPage(pager);
		PageMyBatis<RptIdxInfo> page = (PageMyBatis<RptIdxInfo>) this.idxDao
				.listIdxInfo(params);
		SearchResult<RptIdxInfo> results = new SearchResult<RptIdxInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}

	public SearchResult<RptIdxInfoVO> getIdxInfoListShowByParams(Pager pager,
			Map<String, Object> param) {
		if (param.get("defSrc") != null) {
			String defSrc = (String) param.get("defSrc");
			param.put("defSrc", defSrc);
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
				param.put("defOrg", user.getOrgNo());
			} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
				param.put("defUser", user.getUserId());
			}
		}
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			param.put("isNotSuper", BioneSecurityUtils.getCurrentUserInfo().isSuperUser());
			param.put("userId", BioneSecurityUtils.getCurrentUserId());
			// 当前用户授权对象的集合
			Map<String, List<String>> userAuthObjMap = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap();
			if(userAuthObjMap.containsKey(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG)){
				if(userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG).size() > 0){
					param.put("authOrg", SplitStringBy1000.change(userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG)));
				}
			}
			if(userAuthObjMap.containsKey(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE)){
				if(userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE).size() > 0){
					param.put("authRole", SplitStringBy1000.change(userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE)));
				}
			}
			if(userAuthObjMap.containsKey(GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT)){
				if(userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT).size() > 0){
					param.put("authDept", SplitStringBy1000.change(userAuthObjMap.get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT)));
				}
			}
		}
		PageHelper.startPage(pager);
		PageMyBatis<RptIdxInfoVO> page = (PageMyBatis<RptIdxInfoVO>) this.idxDao.listIdxInfoShow(param);
		SearchResult<RptIdxInfoVO> results = new SearchResult<RptIdxInfoVO>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}

	public List<RptIdxMeasureRelInfoVO> getMeasureRelInfoList(
			Map<String, Object> params) {
		List<RptIdxMeasureRelInfoVO> list = (List<RptIdxMeasureRelInfoVO>) this.idxDao
				.getMeasureRelInfoList(params);
		return list;
	}

	@Transactional(readOnly = false)
	public void cascadeIdxInfoDel(String id) {
		List<String> idList = Arrays.asList(id.split(","));

		Map<String, Object> param_ = Maps.newHashMap();
		param_.put("indexNos", idList);
		// 1.删除指标扩展信息
		this.idxDao.deleteIdxBusiExt(param_);
		// 2.删除指标访问历史
		// code here
		// 3.删除指标与度量关系
		this.idxDao.deleteIdxMeasureRel(param_) ;
		// 4.删除指标维度关系
		this.idxDao.deleteIdxDim(param_);
		// 5.删除指标公式信息
		this.idxDao.deleteIdxFormula(param_);
		// 6.删除指标过滤信息
		this.idxDao.deleteIdxFilter(param_);
		// 7.删除指标血缘关系
		this.idxDao.deleteIdxSrc(param_);
		// 7.删除指标数据集映射
		// code here
		// 8.删除指标数据集映射维度过滤
		// code here
		this.idxDao.deleteIdxInfo(param_);
		// 9.删除指标对比组
		String jql = "delete from RptIdxCompGrp grp where grp.id.mainIndexNo in ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, idList);
		// 10.删除指标同类组
		jql = "delete from RptIdxSimilarGrp grp where grp.id.indexNo in ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, idList);
		// 11.删除指标标签关系
		jql = "delete from BioneLabelObjRel rel where rel.id.objId in ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, idList);
	}
	
	@Transactional(readOnly = false)
	public String cascadeIdxInfoVerDel(String id, String vid) {
		Map<String, Object> param_ = Maps.newHashMap();
		id=id.substring(0, id.length()-1);
		param_.put("id", id);
		param_.put("vid", vid);
		RptIdxInfoPK pid = new RptIdxInfoPK();
		pid.setIndexNo(id);
		pid.setIndexVerId(Long.parseLong(vid));
		RptIdxInfo info = this.getEntityById(RptIdxInfo.class, pid);
		// 1.删除指标扩展信息
		this.idxDao.deleteIdxBusiExt(param_);
		// 2.删除指标访问历史
		// code here
		// 3.删除指标与度量关系
		this.idxDao.deleteIdxMeasureRel(param_);
		// 4.删除指标维度关系
		this.idxDao.deleteIdxDim(param_);
		// 5.删除指标公式信息
		this.idxDao.deleteIdxFormula(param_);
		// 6.删除指标过滤信息
		this.idxDao.deleteIdxFilter(param_);
		// 7.删除指标血缘关系
		this.idxDao.deleteIdxSrc(param_);
		// 7.删除指标数据集映射
		// code here
		// 8.删除指标数据集映射维度过滤
		// code here
		this.idxDao.deleteIdxInfo(param_);
		return info.getStartDate();
	}
	
	@Transactional(readOnly = false)
	public void cascadeUptVer(String id, String vid,String startDate) {
		//指标需要修改的相关表
		String[] idxEns= {"RptIdxInfo","RptIdxBusiExt","RptIdxMeasureRel","RptIdxDimRel","RptIdxFormulaInfo","RptIdxFilterInfo","RptIdxSrcRelInfo"};
		// 重新修改下个版本配置属性
		if (id.endsWith(",")) {
			id = id.substring(0, id.length() - 1);
		}
		long cvid = Long.parseLong(vid);
		String jql = "select info from RptIdxInfo info where info.id.indexNo = ?0 order by info.id.indexVerId";
		List<RptIdxInfo> infos = this.baseDAO.findWithIndexParam(jql, id);
		if (infos != null && infos.size() > 0) {
			for (RptIdxInfo info : infos) {
				if (info.getId().getIndexVerId() == cvid) {
					startDate = info.getStartDate();
				}
				if (info.getId().getIndexVerId() > cvid) {
					for (int i = 0; i < idxEns.length; i++) {
						this.excuteUptVer(id, idxEns[i], cvid, info, startDate);
					}
				}
				if((info.getId().getIndexVerId() < cvid)) {
					RptIdxInfo lastInfo = infos.get(infos.size() - 1);
					long lastVid = lastInfo.getId().getIndexVerId();
					if((lastVid == cvid - 1) && (lastVid == info.getId().getIndexVerId())) {
						Map<String,Object> params = new HashMap<String, Object>();
						jql = "update RptIdxInfo info set info.endDate = :endDate where info.id.indexNo = :indexNo and info.id.indexVerId = :indexVerId";
						params.put("endDate", "29991231");
						params.put("indexNo", id);
						params.put("indexVerId", info.getId().getIndexVerId());
						this.baseDAO.batchExecuteWithNameParam(jql, params);
					}
				}
			}
		}
	}
	private void excuteUptVer(String id,String tbName,long cvid,RptIdxInfo info,String startDate){
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "update "+tbName+" info set info.id.indexVerId = :newindexVerId";
		if(info.getId().getIndexVerId() == cvid+1 && tbName.equals("RptIdxInfo")){
			jql +=",info.startDate = :startDate";
			params.put("startDate", startDate);
		}
		jql += " where info.id.indexNo = :indexNo and info.id.indexVerId = :indexVerId";
		params.put("indexNo", id);
		params.put("indexVerId", info.getId().getIndexVerId());
		params.put("newindexVerId", info.getId().getIndexVerId()-1);
		this.baseDAO.batchExecuteWithNameParam(jql, params);
	}

	public List<CommonComboBoxNode> getIndexTypeList(String situation,
			String defSrc) {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		if (situation == null) {
			node = new CommonComboBoxNode();
			node.setId("");
			node.setText("全部");
			nodes.add(node);
		}
		if (StringUtils.isEmpty(defSrc)) {
			node = new CommonComboBoxNode();
			node.setId(ROOT_INDEX);
			node.setText("根指标");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(GENERIC_INDEX);
			node.setText("泛化指标");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(SUM_ACCOUNT_INDEX);
			node.setText("总账指标");
			nodes.add(node);
		}
		node = new CommonComboBoxNode();
		node.setId(COMPOSITE_INDEX);
		node.setText("组合指标");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(DERIVE_INDEX);
		node.setText("派生指标");
		nodes.add(node);
		return nodes;
	}

	public List<CommonComboBoxNode> dataTypeList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId(DATA_TYPE_MONEY);
		node.setText("金额");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(DATA_TYPE_VALUE);
		node.setText("数值");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(DATA_TYPE_PERCENT);
		node.setText("比例");
		nodes.add(node);
		return nodes;
	}

	public List<CommonComboBoxNode> getParamDeptListByParams(String type,
			String logicSysNo) {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("type", type);
		List<BioneParamInfo> paramList = this.idxDao
				.getParamDeptListByParams(map);
		for (BioneParamInfo param : paramList) {
			CommonComboBoxNode node = new CommonComboBoxNode();
			node.setId(param.getParamValue());
			node.setText(param.getParamName());
			nodes.add(node);
		}
		return nodes;
	}

	public List<CommonComboBoxNode> calcCycleList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId(CALC_CYCLE_DAY);
		node.setText("日");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(CALC_CYCLE_MONTH);
		node.setText("月");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(CALC_CYCLE_SEASON);
		node.setText("季");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(CALC_CYCLE_YEAR);
		node.setText("年");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(CALC_CYCLE_WEEK);
		node.setText("周");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(CALC_CYCLE_ONE_THIRD_MONTH);
		node.setText("旬");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(CALC_CYCLE_HALF_YEAR);
		node.setText("半年");
		nodes.add(node);
		return nodes;
	}

	public List<CommonComboBoxNode> dataUnitList(String dataType) {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		if (DATA_TYPE_VALUE.equals(dataType)) {
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_YUAN);
			node.setText("个");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_HUNDRED);
			node.setText("百");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_THOUSAND);
			node.setText("千");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_TEN_THOUSAND);
			node.setText("万");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_HUNDRED_MILLION);
			node.setText("亿");
			nodes.add(node);
		} else {
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_YUAN);
			node.setText("元");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_HUNDRED);
			node.setText("百元");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_THOUSAND);
			node.setText("千元");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_TEN_THOUSAND);
			node.setText("万元");
			nodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(DATA_UNIT_HUNDRED_MILLION);
			node.setText("亿元");
			nodes.add(node);
		}
		return nodes;
	}

	public List<CommonComboBoxNode> indexStsList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId(INDEX_STS_START);
		node.setText("启用");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(INDEX_STS_STOP);
		node.setText("停用");
		nodes.add(node);
		return nodes;
	}
	
	public List<CommonComboBoxNode> statTypeList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId(IDX_STAT_TYPE_TIMEPOINT);
		node.setText("时点");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(IDX_STAT_TYPE_STAGE);
		node.setText("时期");
		nodes.add(node);
		return nodes;
	}

	public List<CommonComboBoxNode> isPublishList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId(IS_PUBLISH_TRUE);
		node.setText("是");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(IS_PUBLISH_FALSE);
		node.setText("否");
		nodes.add(node);
		return nodes;
	}

	public List<CommonComboBoxNode> isSumList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId(IS_SUM_TRUE);
		node.setText("是");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(IS_SUM_FALSE);
		node.setText("否");
		nodes.add(node);
		return nodes;
	}

	public List<CommonComboBoxNode> hasInforightList() {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = null;
		node = new CommonComboBoxNode();
		node.setId(HAS_INFORIGHT_TRUE);
		node.setText("是");
		nodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(HAS_INFORIGHT_FALSE);
		node.setText("否");
		nodes.add(node);
		return nodes;
	}

	public List<RptIdxInfo> listIdxInfo(Map<String, Object> map) {
		return this.idxDao.listIdxInfo(map);
	}

	public List<RptIdxInfo> getIdxInfoByParams(Map<String, Object> map) {
		return this.idxDao.getIdxInfoByParams(map);
	}

	public RptIdxBusiExt getIdxBusiExtByParams(Map<String, Object> params) {
		return this.idxDao.getIdxBusiExtByParams(params);
	}

	public Integer testSameIndexNo(Map<String, Object> params) {
		return this.idxDao.testSameIndexNo(params);
	}

	public Integer testSameIndexNm(Map<String, Object> params) {
		Integer sameIndexNmNum = this.idxDao.testSameIndexNm(params);// 指标表内部校验
		if (sameIndexNmNum > 0) {
			return sameIndexNmNum;// 如果sameIndexNmNum>0 ，则程序停止
		}
		String indexNm = params.get("indexNm").toString();
		params.put("indexNm", "%" + indexNm + "%");
		List<RptIdxBusiExt> likeInfoList = this.idxDao.findLIkeInfoList(params);// 查询指标扩展信息中的别名字段
		for (RptIdxBusiExt ext : likeInfoList) {
			String usualNum = ext.getIndexUsualNm();
			if (usualNum != null) {
				String[] temps = StringUtils.split(usualNum, ',');
				for (int i = 0; i < temps.length; i++) {
					if (temps[i].equals(indexNm)) {
						return 1;
					}
				}
			}
		}
		return 0;
	}

	public List<RptIdxMeasureInfo> getMeasureColListByParams(
			Map<String, Object> params) {
		return this.idxDao.getMeasureColListByParams(params);
	}

	public String getSourceIdBySetId(String setId) {
		return this.idxDao.getSourceIdBySetId(setId);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void saveIdxInfoAndRelInfo(Map<String, Object> bs_params) {
		RptIdxInfo info = (RptIdxInfo) bs_params.get("info");
		String isNewVer = (String) bs_params.get("isNewVer");
		if (isNewVer.equals("Y")) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("indexNo", info.getId().getIndexNo());
			params.put("indexVerId", info.getId().getIndexVerId() - 1);
			List<RptIdxInfo> idxInfos = this.getIdxInfoByParams(params);
			if(idxInfos != null && idxInfos.size() > 0){
				RptIdxInfo oldVerIndex = idxInfos.get(0);
				oldVerIndex.setEndDate(info.getStartDate());
				this.idxDao.updateIdxInfo(oldVerIndex);// 没有考虑改变指标类型，可以在页面做校验
			}
		}
		
		RptIdxBusiExt busiExt = (RptIdxBusiExt) bs_params.get("busiExt");
		RptIdxMeasureRel idxMeasureRel = (RptIdxMeasureRel) bs_params
				.get("idxMeasureRel");
		List<RptIdxDimRel> idxDimRelList = (List<RptIdxDimRel>) bs_params
				.get("idxDimRelList");
		List<RptIdxMeasureRel> idxMeasureRelList = (List<RptIdxMeasureRel>) bs_params
				.get("idxMeasureRelList");
		List<RptIdxFilterInfo> idxFilterList = (List<RptIdxFilterInfo>) bs_params
				.get("idxFilterList");
		RptIdxFormulaInfo idxFormulaInfo = (RptIdxFormulaInfo) bs_params
				.get("idxFormulaInfo");
		String srcIndexNo = info.getSrcIndexNo();
		String jql = "delete from RptIdxSrcRelInfo info where info.id.indexNo = ?0 and info.id.indexVerId = ?1";
		this.baseDAO.batchExecuteWithIndexParam(jql, info.getId().getIndexNo(),info.getId().getIndexVerId());
		if(StringUtils.isNotBlank(srcIndexNo)){
			List<RptIdxSrcRelInfo> relInfos = new ArrayList<RptIdxSrcRelInfo>();
			String[] srcIndexNos = StringUtils.split(srcIndexNo, ",");
			for (int i = 0; i < srcIndexNos.length; i++) {
				RptIdxSrcRelInfo relinfo = new RptIdxSrcRelInfo();
				RptIdxSrcRelInfoPK id = new RptIdxSrcRelInfoPK();
				id.setIndexNo(info.getId().getIndexNo());
				id.setIndexVerId(info.getId().getIndexVerId());
				id.setSrcIndexNo(srcIndexNos[i]);
				id.setSrcMeasureNo("INDEX_VAL");//直接给表的src_measure_no字段添加了INDEX_VAL
				relinfo.setId(id);
				relInfos.add(relinfo);
			}
			this.excelBS.saveEntityJdbcBatch(relInfos);
		}
		String saveType = (String) bs_params.get("saveType");
		this.saveOrUpdateEntity(info);
		this.saveOrUpdateEntity(busiExt);
		if (saveType.equals(ROOT_INDEX) || saveType.equals(GENERIC_INDEX)
				|| saveType.equals(SUM_ACCOUNT_INDEX)) {
			for (int j = 0; j < idxMeasureRelList.size(); j++) {
				this.idxDao.saveIdxMeasureRel(idxMeasureRelList.get(j));
			}
		} else {
			this.idxDao.saveIdxMeasureRel(idxMeasureRel);
			if (!saveType.equals(ADD_RECORD_INDEX)) {
				this.idxDao.saveIdxFormulaInfo(idxFormulaInfo);
			}
			if (saveType.equals(COMPOSITE_INDEX)) {
				for (int i = 0; i < idxFilterList.size(); i++) {
					RptIdxFilterInfo filterInfo = idxFilterList.get(i);
					this.idxDao.saveIdxFilterInfo(filterInfo);
				}
			}
		}
		for (int i = 0; i < idxDimRelList.size(); i++) {
			RptIdxDimRel rptIdxDimRel = idxDimRelList.get(i);
			this.idxDao.saveIdxDimRel(rptIdxDimRel);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void updateIdxInfoAndRelInfo(Map<String, Object> bs_params) {
		RptIdxInfo info = (RptIdxInfo) bs_params.get("info");
		RptIdxBusiExt busiExt = (RptIdxBusiExt) bs_params.get("busiExt");
		RptIdxMeasureRel idxMeasureRel = (RptIdxMeasureRel) bs_params
				.get("idxMeasureRel");
		List<RptIdxDimRel> idxDimRelList = (List<RptIdxDimRel>) bs_params
				.get("idxDimRelList");
		List<RptIdxMeasureRel> idxMeasureRelList = (List<RptIdxMeasureRel>) bs_params
				.get("idxMeasureRelList");
		List<RptIdxFilterInfo> idxFilterList = (List<RptIdxFilterInfo>) bs_params
				.get("idxFilterList");
		RptIdxFormulaInfo idxFormulaInfo = (RptIdxFormulaInfo) bs_params
				.get("idxFormulaInfo");
		String saveType = (String) bs_params.get("saveType");
		if (saveType.equals("newVersion")) {// 对应的按钮为业务属性页面的"发布为新版本",现在已经屏蔽了可以忽略,新版本中的技术属性和老版本一致，（没有考虑指标类型发生变化的情况）
			Map<String, Object> params = Maps.newHashMap();
			params.put("indexNo", info.getId().getIndexNo());
			params.put("indexVerId", info.getId().getIndexVerId());
			List<RptIdxInfo> idxInfos = this.getIdxInfoByParams(params);
			RptIdxInfo oldVerIndex = idxInfos.get(0);
			oldVerIndex.setEndDate(info.getStartDate());
			this.idxDao.updateIdxInfo(oldVerIndex);// 没有考虑改变指标类型，可以在页面做校验
			info.setEndDate("29991231");
			info.getId().setIndexVerId(info.getId().getIndexVerId() + 1);
			info.setSrcIndexMeasure(oldVerIndex.getSrcIndexMeasure());
			info.setSrcIndexNo(oldVerIndex.getSrcIndexNo());
			busiExt.getId().setIndexVerId(busiExt.getId().getIndexVerId() + 1);
			this.saveOrUpdateEntity(info);
			this.saveOrUpdateEntity(busiExt);
			// 将旧指标的关联信息存入新指标
			idxMeasureRelList = this.getMeasureRelByParams(params);
			idxDimRelList = this.idxDao.getDimListByOldVerIndex(params);
			idxFilterList = this.idxDao.getIdxFilterInfoListByParams(params);
			idxFormulaInfo = this.idxDao.getFormulaInfoByParams(params);
			if (idxMeasureRelList != null && idxMeasureRelList.size() > 0) {
				for (int i = 0; i < idxMeasureRelList.size(); i++) {
					RptIdxMeasureRel measure = idxMeasureRelList.get(i);
					measure.getId().setIndexVerId(
							measure.getId().getIndexVerId() + 1);
					this.idxDao.saveIdxMeasureRel(measure);
				}
			}
			if (idxDimRelList != null && idxDimRelList.size() > 0) {
				for (int i = 0; i < idxDimRelList.size(); i++) {
					RptIdxDimRel dim = idxDimRelList.get(i);
					dim.getId().setIndexVerId(dim.getId().getIndexVerId() + 1);
					this.idxDao.saveIdxDimRel(dim);
				}
			}
			if (idxFilterList != null && idxFilterList.size() > 0) {
				for (int i = 0; i < idxFilterList.size(); i++) {
					RptIdxFilterInfo filter = idxFilterList.get(i);
					filter.getId().setIndexVerId(
							filter.getId().getIndexVerId() + 1);
					this.idxDao.saveIdxFilterInfo(filter);
				}
			}
			if (idxFormulaInfo != null) {
				idxFormulaInfo.getId().setIndexVerId(
						idxFormulaInfo.getId().getIndexVerId() + 1);
				this.idxDao.saveIdxFormulaInfo(idxFormulaInfo);
			}
		} else {
			this.saveOrUpdateEntity(info);
			//如果指标修改了开始日期，那么指标上个版本的结束日期也要一并修改
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.id.indexVerId = ?1";
			RptIdxInfo idx = this.baseDAO.findUniqueWithIndexParam(jql, info.getId().getIndexNo(), info.getId().getIndexVerId() - 1);
			if(null != idx) {
				idx.setEndDate(info.getStartDate());
				this.saveOrUpdateEntity(idx);
			}
			this.saveOrUpdateEntity(busiExt);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", info.getId().getIndexNo());
			params.put("vid", info.getId().getIndexVerId());
			params.put("indexNo", info.getId().getIndexNo());
			params.put("indexVerId", info.getId().getIndexVerId());
			this.idxDao.deleteIdxMeasureRel(params);
			this.idxDao.deleteIdxFormula(params);
			this.idxDao.deleteIdxFilter(params);
			if (saveType.equals(ROOT_INDEX) || saveType.equals(GENERIC_INDEX)
					|| saveType.equals(SUM_ACCOUNT_INDEX)) {
				for (int j = 0; j < idxMeasureRelList.size(); j++) {
					this.idxDao.saveIdxMeasureRel(idxMeasureRelList.get(j));
				}
				this.idxDao.deleteIdxDim(params);
				for (int i = 0; i < idxDimRelList.size(); i++) {
					RptIdxDimRel rptIdxDimRel = idxDimRelList.get(i);
					this.idxDao.saveIdxDimRel(rptIdxDimRel);
				}
			} else {
				this.idxDao.saveIdxMeasureRel(idxMeasureRel);
				List<RptIdxDimRel> finalIdxDimRelList = Lists.newArrayList();// 有序的最终存入数据库的数据
				List<RptIdxDimRel> oldFullIdxDimRelList = this.idxDao
						.getDimListByOldVerIndex(params);// 旧版本复合指标所有维度关系数据(orderNum
															// asc)
				Map<String, RptIdxDimRel> newFullIdxDimRelMap = Maps
						.newHashMap();
				for (int i = 0; i < idxDimRelList.size(); i++) {
					RptIdxDimRel new_ = idxDimRelList.get(i);
					newFullIdxDimRelMap.put(new_.getDimType() + "_"
							+ new_.getId().getDimNo(), new_);
				}
				for (int i = 0; i < oldFullIdxDimRelList.size(); i++) {
					RptIdxDimRel old = oldFullIdxDimRelList.get(i);
					String dimType = old.getDimType();
					String oldKey = dimType + "_" + old.getId().getDimNo();
					RptIdxDimRel new_sys = newFullIdxDimRelMap.get(oldKey);
					if (new_sys != null) {
						RptIdxDimRel dimRel = new RptIdxDimRel();
						BeanUtils.copy(new_sys, dimRel);
						if (dimType.equals(DIM_TYPE_BUSI)) {
							dimRel.setStoreCol(old.getStoreCol());
						}
						dimRel.setOrderNum(old.getOrderNum());
						finalIdxDimRelList.add(dimRel);//这个循环执行完，就是保留了所有新维度关系中原来就有的维度关系
					}
				}
				if((idxDimRelList.size() != finalIdxDimRelList.size()) || (idxDimRelList.size() != oldFullIdxDimRelList.size())) {//指标维度关系有变化
					this.idxDao.deleteIdxDim(params);//删除原有的指标维度关系
					finalIdxDimRelList = idxDimRelList;//保存新的指标维度关系
					for (int i = 0; i < finalIdxDimRelList.size(); i++) {
						RptIdxDimRel rptIdxDimRel = finalIdxDimRelList.get(i);
						this.idxDao.saveIdxDimRel(rptIdxDimRel);
					}
				}
				if (!saveType.equals(ADD_RECORD_INDEX)) {
					this.idxDao.saveIdxFormulaInfo(idxFormulaInfo);
				}
				if (saveType.equals(COMPOSITE_INDEX)) {
					for (int i = 0; i < idxFilterList.size(); i++) {
						RptIdxFilterInfo filterInfo = idxFilterList.get(i);
						this.idxDao.saveIdxFilterInfo(filterInfo);
					}
				}
			}
		}
	}

	public List<RptSysModuleCol> getRptSysColByParams(Map<String, Object> params) {
		return this.idxDao.getRptSysColByParams(params);
	}

	public List<RptIdxMeasureRel> getMeasureRelByParams(
			Map<String, Object> params) {
		return this.idxDao.getMeasureRelByParams(params);
	}

	// weijx添加
	public List<RptIdxInfo> findIdxInfoByIds(List<String> idxNos) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("list", ReBuildParam.splitLists(idxNos));
		return this.idxDao.listIdxInfo(params);
	}

	public List<RptIdxDimRel> getDimListBySrcIndex(Map<String, Object> params) {
		return this.idxDao.getDimListBySrcIndex(params);
	}

	public RptDimTypeInfo getDimTypeInfoById(String dimTypeNo) {
		return this.idxDao.getDimTypeInfoById(dimTypeNo);
	}

	public List<RptIdxDimRel> getDimNosOfIndex(Map<String, Object> map) {

		List<RptIdxDimRel> list = this.relDao.getAllDimRel(map);

		List<RptIdxDimRel> resultList = new ArrayList<RptIdxDimRel>();
		for (RptIdxDimRel x : list) {
			boolean flag = false;
			for (RptIdxDimRel y : resultList) {
				if (x.getId().getIndexNo().equals(y.getId().getIndexNo())
						&& x.getId().getIndexVerId() == y.getId()
								.getIndexVerId()) {
					//edit by fangjuan 20150721 对维度进行去重
					if(!y.getId().getDimNo().contains(x.getId().getDimNo())){
						String newDimNos = y.getId().getDimNo() + ","
								+ x.getId().getDimNo();
						y.getId().setDimNo(newDimNos);
					}
					flag = true;// 存在
				}
			}
			if (!flag) {
				resultList.add(x);
			}
		}
		return resultList;
	}

	public IdxFormulaAndSrcIdxVO getExtendIdxInfo(String indexNo,
			String indexVerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		RptIdxDimRelPK id = new RptIdxDimRelPK();
		id.setIndexNo(indexNo);
		id.setIndexVerId(new Long(indexVerId));
		map.put("id", id);
		List<IdxFormulaAndSrcIdxVO> list = this.formulaDao.list(map);
		IdxFormulaAndSrcIdxVO vo = new IdxFormulaAndSrcIdxVO();
		if (list != null && list.size() == 1) {
			vo = list.get(0);
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			id.setDsId(propertiesUtils.getProperty("dsId"));
			map.put("id", id);
			/*map.put("dimType",
					com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DIM_TYPE_BUSI);*/
			List<String> stringList = this.relDao.getDimNos(map);
			String dimNos = "";
			for (String tmp : stringList) {
				dimNos += tmp + ",";
			}
			if (dimNos.endsWith(",")) {
				dimNos = dimNos.substring(0, dimNos.length() - 1);
			}
			vo.setDimNos(dimNos);
			Map<String, String> map1 = this.replaceNameByIdx(
					vo.getFormulaContent());
			vo.setFormulaContent(vo.getFormulaContent());
			vo.setFormulaDesc(map1.get("expressionDesc"));
			vo.setMessage(map1.get("message") == null ? "" : map1
					.get("message"));
			return vo;
		}
		return null;
	}

	/**
	 * 获取所有子机构
	 *
	 * @param busiType 机构类型
	 * @param orgNos   机构编号集合
	 * @param orgs     子机构集合
	 **/
	private void getChildOrg(List<RptOrgInfo> orgs, List<String> orgNos, String busiType) {
		String jql = "select org from RptOrgInfo org where org.upOrgNo in :orgNos and org.id.orgType = :orgType";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgNos", orgNos);
		//params.put("orgType", com.yusys.bione.plugin.base.common.GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
		//为能够正确进行过滤，筛选对应机构类型
		if (StringUtils.isNotBlank(busiType)) {
			params.put("orgType", busiType);
		} else {
			params.put("orgType", com.yusys.bione.plugin.base.common.GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
		}
		List<RptOrgInfo> orgInfos = this.baseDAO.findWithNameParm(jql, params);
		orgNos.clear();
		if (orgInfos != null && orgInfos.size() > 0) {
			for (RptOrgInfo orgInfo : orgInfos) {
				orgs.add(orgInfo);
				orgNos.add(orgInfo.getId().getOrgNo());
			}
			getChildOrg(orgs, orgNos, busiType);
		}
	}

	public List<CommonTreeNode> getDimInfoTree(String dimTypeNo, String basePath, String busiType) {
		Map<String, Object> conditions = Maps.newHashMap();
		conditions.put("dimTypeNo", dimTypeNo);
		conditions.put("itemSts", "Y");
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if(dimTypeNo.equals("ORG")){
			 List<RptOrgInfo> orgInfos = new ArrayList<RptOrgInfo>();
			 List<String> orgNos = new ArrayList<String>();
			 if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				 orgNos.add("0");
			 }
			 else{
				 orgNos.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			 }
			getChildOrg(orgInfos, orgNos, busiType);
			if(orgInfos != null && orgInfos.size() > 0){
				for(RptOrgInfo org : orgInfos){
					CommonTreeNode node = new CommonTreeNode();
					node.setId(org.getId().getOrgNo());
					node.setText(org.getOrgNm());
					node.setUpId(org.getUpOrgNo());
					node.setIcon(basePath + REPORT_LABELOBJ_ICON);
					node.getParams().put("type", "common");
					nodes.add(node);
				}
			}
		}
		else{
			List<RptDimItemInfo> infos = this.rptDimDao
					.findDimItemListByTypeNo(conditions);

			conditions.clear();
			conditions.put("dimTypeNo", dimTypeNo);
			List<RptDimTypeInfo> dimType = this.rptDimDao
					.getTypeInfosByParams(conditions);

			CommonTreeNode defaultnode = new CommonTreeNode();
			defaultnode.setId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
			defaultnode.setText("维度项信息");
			defaultnode.getParams().put("type", "root");
			defaultnode.setIsParent(true);
			defaultnode.setIcon(basePath + GlobalConstants4plugin.TREE_ICON_ROOT);
			nodes.add(defaultnode);
			if (infos != null && infos.size() > 0) {
				if (dimType != null
						&& dimType.size() > 0
						&& dimType
								.get(0)
								.getDimTypeStruct()
								.equals(GlobalConstants4plugin.DIM_TYPE_STRUCT_TREE)) {
					for (RptDimItemInfo info : infos) {
						CommonTreeNode node = new CommonTreeNode();
						node.setId(info.getId().getDimItemNo());
						node.setText(info.getDimItemNm());
						node.setUpId(info.getUpNo());
						node.setIcon(basePath + REPORT_LABELOBJ_ICON);
						node.getParams().put("type", "common");
						nodes.add(node);
					}
				} else {
					for (RptDimItemInfo info : infos) {
						CommonTreeNode node = new CommonTreeNode();
						node.setId(info.getId().getDimItemNo());
						node.setText(info.getDimItemNm());
						node.setUpId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
						node.setIcon(basePath + REPORT_LABELOBJ_ICON);
						node.getParams().put("type", "common");
						nodes.add(node);
					}
				}
			}
		}
		
		return nodes;
	}

	public RptIdxFilterInfo getIdxFilterInfoByParams(Map<String, Object> params) {
		return this.idxDao.getIdxFilterInfoByParams(params);
	}

	public RptIdxFormulaInfo getFormulaInfoByParams(Map<String, Object> params) {
		return this.idxDao.getFormulaInfoByParams(params);
	}

	public RptIdxMeasureInfo getMeasureById(String measureNo) {
		return this.idxDao.getMeasureById(measureNo);
	}

	public List<RptIdxInfo> getAllIdxVersionByParams(Map<String, Object> params) {
		return this.idxDao.getAllIdxVersionByParams(params);
	}

	public List<CommonComboBoxNode> getDimByDimNos(String dimNos) {
		if (!StringUtils.isEmpty(dimNos)) {
			List<String> dimList = ArrayUtils.asList(dimNos, ",");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dimTypeNos", dimList);
			return this.relDao.getDimByDimNos(map);
		}
		return null;
	}

	public List<RptDimTypeInfo> getDimByIdxInfo(Map<String, Object> params) {
		return this.relDao.getDimByIdxInfo(params);
	}

	public String getMaxIndexNo(String type) {
		return this.idxDao.getMaxIndexNo(type);
	}

	public void searchChild(String indexCatalogNo, List<String> catas,
			List<RptIdxCatalog> cataList) {
		for (RptIdxCatalog idxcata : cataList) {
			if (idxcata.getUpNo().equals(indexCatalogNo)) {
				searchChild(idxcata.getIndexCatalogNo(), catas, cataList);
				catas.add(idxcata.getIndexCatalogNo());
			}
		}
	}

	public String isHasIdx(String indexCatalogNo) {
		Map<String, Object> condition = new HashMap<String, Object>();
		List<RptIdxCatalog> cataList = this.catalogDao
				.listIdxCatalog(condition);
		List<String> catas = new ArrayList<String>();
		searchChild(indexCatalogNo, catas, cataList);
		if (catas.size() == 0) {
			catas.add(indexCatalogNo);
		}
		condition.put("list", catas);
		Integer count = this.idxDao.getIdxCountByCataNos(condition);
		return count > 0 ? "1" : "0";
	}
	
	
	/**
	 * 
	 * 
	 * @param expression
	 * @param expressionDesc
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, String> replaceNoByIdx(String expressionDesc) {
		Map<String, Object> con = Maps.newHashMap();
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String steadyMeasureNo = propertiesUtils
				.getProperty("steadyMeasureNo");
		List<RptIdxMeasureInfo> minfos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
		Map<String, String> NoAndNameMap = new HashMap<String, String>();
		Map<String, String> resultMap = new HashMap<String, String>();
		String testExpression = expressionDesc;
		List<String> indexAllNos = new ArrayList<String>();
		
		List<String> indexSet = new ArrayList<String>();
		List<String> measureSet = new ArrayList<String>();
		
		Set<String> indexNms = new HashSet<String>();
		String indexNos = "";
		String measureNos = "";
		Pattern pattern = Pattern
				.compile("I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|[\\[]|[\\]]|[－]|\\.)+'\\)");
		StringBuffer sb = new StringBuffer();
		Matcher matcher = pattern.matcher(testExpression);
		while (matcher.find()) {
			String indexNm = matcher.group(0).substring(
					matcher.group(0).indexOf("'") + 1,
					matcher.group(0).indexOf("'",
							matcher.group(0).indexOf("'") + 1));
			if (indexNm.contains(".")) {
				indexNm = indexNm.substring(0, indexNm.indexOf("."));
			}
			indexNms.add(indexNm);
		}
		if(indexNms != null && indexNms.size() > 0){
			con.put("indexNms", ReBuildParam.splitLists(new ArrayList<String>(indexNms)));
			List<RptIdxInfo> list = this.idxDao.listIdxInfo(con);
			if (!StringUtils.isEmpty(expressionDesc)) {// 将表达式描述替换为表达式
				for (RptIdxInfo idx : list) {
					NoAndNameMap.put(idx.getIndexNm(), idx.getId().getIndexNo());
					if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)){
						for(RptIdxMeasureInfo info : minfos){
							NoAndNameMap.put(
									idx.getIndexNm() + "." + info.getMeasureNm(),
									idx.getId().getIndexNo() + "." + info.getMeasureNo());
						}
					}
				}
				
			}
			matcher.reset();
			while (matcher.find()) {
				String indexNm = matcher.group(0).substring(
						matcher.group(0).indexOf("'") + 1,
						matcher.group(0).indexOf("'",
								matcher.group(0).indexOf("'") + 1));
				String indexNo = NoAndNameMap.get(indexNm);
				if (NoAndNameMap.get(indexNm) == null) {
					resultMap.put("message", "表达式中第" + (matcher.start() + 1)
							+ "位的指标不存在，请修改！");
					return resultMap;
				}
				matcher.appendReplacement(sb, "I('" + NoAndNameMap.get(indexNm)
						+ "')");
				if(indexAllNos.contains(indexNo)){
					continue;
				}
				if (indexNo.contains(".")) {
					String srcindexNos[] = StringUtils.split(indexNo,".");
					indexSet.add(srcindexNos[0]);
					measureSet.add(srcindexNos[1]);
				}
				else{
					indexSet.add(indexNo);
					measureSet.add(steadyMeasureNo);
				}
				
				
			}
			matcher.appendTail(sb);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("funcType",
					com.yusys.bione.plugin.base.common.GlobalConstants4plugin.VALID_FUNC_TYPE_RPT);
			List<RptIdxFormulaFunc> funcs = this.funAndSymbolMybatisDao
					.listFunc(params);
			if (funcs != null && funcs.size() > 0) {
				StringBuffer s = new StringBuffer();
				String patternString = "";
				for (RptIdxFormulaFunc func : funcs) {
					patternString = patternString
							+ func.getFormulaId()
							+ "\\('([A-Z]|[a-z]|[^\\x00-\\xff]|[0-9]|[ ]|[_]|\\.)+'\\)"
							+ "|";
				}
				if (patternString.endsWith("|")) {
					patternString = patternString.substring(0,
							patternString.length() - 1);
				}
				Pattern pattern1 = Pattern.compile(patternString);
				Matcher matcher1 = pattern1.matcher(sb);
				while (matcher1.find()) {
					String indexNo = matcher1.group(0).substring(
							matcher1.group(0).indexOf("'") + 1,
							matcher1.group(0).indexOf("'",
									matcher1.group(0).indexOf("'") + 1));
					matcher1.appendReplacement(
							s,
							matcher1.group(0).substring(0,
									matcher1.group(0).indexOf("("))
									+ "(I('"
									+ NoAndNameMap.get(indexNo)
									+ "'))");
				}
				matcher1.appendTail(s);
				sb = s;
			}

			List<String> disidxSet = new ArrayList(new HashSet(indexSet));//去掉重复的sourceIndexId,若不去掉，添加不到rpt_idx_src_rel_info表里，
			for (String tmp : disidxSet) {
				indexNos += tmp + ",";
			}
			if (indexNos.endsWith(",")) {
				indexNos = indexNos.substring(0, indexNos.length() - 1);
			}
			
			for (String tmp : measureSet) {
				measureNos += tmp + ",";
			}
			if (measureNos.endsWith(",")) {
				measureNos = measureNos.substring(0, measureNos.length() - 1);
			}
			resultMap.put("indexNos", indexNos);
			resultMap.put("measureNos", measureNos);
			Map<String, String> map = this.logicBs.validExpression(
					sb.toString(), expressionDesc);
			resultMap.put("message", map.get("message"));
			resultMap.put("expression", map.get("expression"));
			resultMap.put("expressionDesc", map.get("expressionDesc"));
		}
		return resultMap;
	}

	/**
	 * 替换指标名称
	 * @param expression
	 * @return
	 */
	private Map<String,String> replaceNameByIdx(String expression){
		Map<String, Object> con = Maps.newHashMap();
		Map<String, String> NoAndNameMap = new HashMap<String, String>();
		Map<String, String> resultMap = new HashMap<String, String>();
		List<RptIdxMeasureInfo> minfos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
		String testExpression = "";
		resultMap.put("expression", expression);
		if (!StringUtils.isEmpty(expression)) {
			testExpression = expression;
			Set<String> indexSet = new HashSet<String>();
			Pattern pattern = Pattern.compile("I\\(I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[\\（]|[\\）]|[\\[]|[\\]]|[ ]|\\.)+'\\)\\)");
			Pattern patBrackets = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|[\\[]|[\\]]|[－]|\\.)+'");
			StringBuffer sb = new StringBuffer();
			Matcher matcher = patBrackets.matcher(testExpression);
			while (matcher.find()) {
				String indexNo = matcher.group(0).substring(
						matcher.group(0).indexOf("'") + 1,
						matcher.group(0).indexOf("'",
								matcher.group(0).indexOf("'") + 1));
				if (indexNo.contains(".")) {
					indexNo = indexNo.substring(0, indexNo.indexOf("."));
				}
				indexSet.add(indexNo);
			}
			if(indexSet != null && indexSet.size()>0){
				con.put("list", ReBuildParam.splitLists(new ArrayList<String>(indexSet)));
				List<RptIdxInfo> list = this.idxDao.listIdxInfo(con);
				if(list != null && list.size()>0){
					for (RptIdxInfo idx : list) {
						NoAndNameMap.put(idx.getId().getIndexNo(), idx.getIndexNm());
						if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)){
							for(RptIdxMeasureInfo info : minfos){
								NoAndNameMap.put(
										idx.getId().getIndexNo() + "." + info.getMeasureNo(),
										idx.getIndexNm() + "." + info.getMeasureNm());
							}
						}
					}
				}
				//先替换公式里不包含I('XXXX'),例如LastMonth('xxx')这种的公式
				matcher = patBrackets.matcher(testExpression);				
				while (matcher.find()) {
					String indexNo = matcher.group(0).substring(
							matcher.group(0).indexOf("'") + 1,
							matcher.group(0).indexOf("'",
									matcher.group(0).indexOf("'") + 1));
					if (NoAndNameMap.get(indexNo) != null) {
						matcher.appendReplacement(sb, "(I('" + NoAndNameMap.get(indexNo) + "')");
					}
				}
				matcher.appendTail(sb);			
				//再替换公式里包含I('XXXX'),这种的公式，因为上一个替换完会把I()替换为I(I()),需要把多余的I去掉
				matcher = pattern.matcher(sb.toString());
				sb = new StringBuffer();
				while (matcher.find()) {
					String indexNm = matcher.group(0).substring(
							matcher.group(0).indexOf("'") + 1,
							matcher.group(0).indexOf("'",
									matcher.group(0).indexOf("'") + 1));
					matcher.appendReplacement(sb, "I('" + indexNm + "')");
				}
				matcher.appendTail(sb);
				resultMap.put("expressionDesc", sb.toString());
			}
			else{
				resultMap.put("expressionDesc", expression);
			}
			
		}
		else{
			resultMap.put("expressionDesc", "");
		}
		return resultMap;
	}
	

	/**
	 * 获取所有指标目录树
	 * 
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> getCatalogTree(String defSrc) {
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(defSrc)) {
			map.put("defSrc", defSrc);
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
				map.put("defOrg", user.getOrgNo());
			} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
				map.put("defUser", user.getUserId());
			}
		}
		List<RptIdxCatalog> allCatalogs = this.catalogDao.listIdxCatalog(map);
		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId("0");
		baseNode.setText("全部");
		baseNode.setUpId("0");
		RptIdxCatalog catalogBase = new RptIdxCatalog();
		catalogBase.setIndexCatalogNo("0");
		catalogBase.setUpNo("0");
		baseNode.setData(catalogBase);
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		for (int i = 0; i < allCatalogs.size(); i++) {
			RptIdxCatalog catalog = allCatalogs.get(i);
			CommonTreeNode treeChildNode = new CommonTreeNode();
			treeChildNode.setData(catalog);
			treeChildNode.setId(catalog.getIndexCatalogNo());
			treeChildNode.setText(catalog.getIndexCatalogNm());
			treeChildNode.setUpId(catalog.getUpNo());
			treeChildNode.getParams().put("type", "idxCatalog");
			treeChildNode.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
			treeNodes.add(treeChildNode);
		}
		return treeNodes;
	}

	public List<RptIdxCatalog> getCatalogHaveIdx(Map<String, Object> map) {
		return this.idxDao.getCatalogHaveIdx(map);
	}

	public List<RptIdxCatalog> getAllCatalog() {

		return this.catalogDao.listIdxCatalog(new HashMap<String, Object>());
	}

	public List<RptIdxBusiExt> findIdxExtByParams(Map<String, Object> params) {
		return this.idxDao.findLIkeInfoList(params);
	}

	public List<RptIdxDimRel> getIdxDimRelByParams(Map<String, Object> params) {
		return this.idxDao.getIdxDimRelByParams(params);
	}

	public List<IndexImportVO> getExportIdxInfo(Map<String, Object> map,long indexVerId) {
		map.put("indexVerId", indexVerId);
		List<RptIdxInfo> infos = this.idxDao.listIdxInfo(map);// input parameter
		List<IndexImportVO> list = new ArrayList<IndexImportVO>();// output(return)
		if (infos != null && infos.size() > 0) {
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String defDeptParamTypeNo = propertiesUtils
					.getProperty("defDeptParamTypeNo");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			String indexDimTypeNo = propertiesUtils
					.getProperty("indexDimTypeNo");
			Set<String> sysDimSet = new HashSet<String>();
			sysDimSet.add(orgDimTypeNo);
			sysDimSet.add(dateDimTypeNo);
			sysDimSet.add(currencyDimTypeNo);
			sysDimSet.add(indexDimTypeNo);
			// temp params
			Map<String, Object> param0_ = new HashMap<String, Object>();
			param0_.put("idxNoList", map.get("list"));
			Map<BigDecimal, RptIdxDimRel> param1_ = new TreeMap<BigDecimal, RptIdxDimRel>();
			List<RptIdxCatalog> catalogs = this.getAllCatalog();
			List<RptIdxBusiExt> allExt = this.idxDao.findLIkeInfoList(param0_);
			List<RptIdxFilterInfo> allIdxFilterInfoList = this.idxDao
					.getIdxFilterInfoListByParams(param0_);
			List<RptIdxFormulaInfo> allIdxFormulaInfoList = this.idxDao
					.getIdxFormulaByParams(param0_);
			List<RptIdxDimRel> allIdxDimRel = this.idxDao
					.getIdxDimRelByParams(param0_);
			List<RptIdxMeasureRel> allIdxMeasureRel = this.idxDao
					.getIdxMeasureRelByParams(param0_);
			List<RptSysModuleInfo> allModules = this.rptDataSetDao
					.getDataSetsByParams(new HashMap<String, Object>());// 通过数据源获取数据集
			List<CommonComboBoxNode> defDeptBoxNodes = this
					.getParamDeptListByParams(defDeptParamTypeNo,
							BioneSecurityUtils.getCurrentUserInfo()
									.getCurrentLogicSysNo());

			// 维度相关内存信息
			List<RptDimTypeInfo> allDimTypeList = this.rptDimDao
					.getAllRptDimTypeInfos();
			List<RptDimItemInfoVO> allDimItemList = this.rptDimDao
					.getAllRptDimItemInfos();

			Map<String, List<RptIdxFilterInfo>> idxPKAndFilterRelMap = Maps
					.newHashMap();
			Map<String, RptIdxFormulaInfo> idxPkAndFormulaRelMap = Maps
					.newHashMap();
			Map<String, List<RptIdxDimRel>> idxPkAndDimRelMap = Maps
					.newHashMap();
			Map<String, List<RptIdxMeasureRel>> idxPkAndMeasureRelMap = Maps
					.newHashMap();
			Map<String, RptIdxBusiExt> allExtMap = Maps.newHashMap();
			Map<String, String> defDeptMap = Maps.newHashMap();
			Map<String, RptIdxCatalog> catalogMap = Maps.newHashMap();
			Map<String, RptDimTypeInfo> allDimTypeInfoMap = Maps.newHashMap();
			Map<String, RptDimItemInfoVO> allDimItemInfoMap = Maps.newHashMap();
			Map<String, RptSysModuleInfo> allSysModuleInfoMap = Maps
					.newHashMap();

			if (defDeptBoxNodes != null) {
				for (int i = 0; i < defDeptBoxNodes.size(); i++) {
					CommonComboBoxNode boxNode = defDeptBoxNodes.get(i);
					defDeptMap.put(boxNode.getId(), boxNode.getText());
				}
			}
			if (allExt != null) {
				for (RptIdxBusiExt ext : allExt) {
					allExtMap.put(ext.getId().getIndexNo(), ext);
				}
			}

			if (catalogs != null) {
				for (RptIdxCatalog catalog : catalogs) {
					catalogMap.put(catalog.getIndexCatalogNo(), catalog);
				}
			}

			if (allIdxFilterInfoList != null) {
				for (RptIdxFilterInfo filter : allIdxFilterInfoList) {
					String key = filter.getId().getIndexNo() + "_"
							+ filter.getId().getIndexVerId();
					if (idxPKAndFilterRelMap.get(key) == null) {
						idxPKAndFilterRelMap.put(key,
								new ArrayList<RptIdxFilterInfo>());
					}
					idxPKAndFilterRelMap.get(key).add(filter);
				}
			}

			if (allIdxFormulaInfoList != null) {
				for (RptIdxFormulaInfo formula : allIdxFormulaInfoList) {
					idxPkAndFormulaRelMap.put(formula.getId().getIndexNo()
							+ "_" + formula.getId().getIndexVerId(), formula);
				}
			}

			if (allIdxDimRel != null) {
				for (RptIdxDimRel dimrel : allIdxDimRel) {
					String key = dimrel.getId().getIndexNo() + "_"
							+ dimrel.getId().getIndexVerId();
					if (idxPkAndDimRelMap.get(key) == null) {
						idxPkAndDimRelMap.put(key,
								new ArrayList<RptIdxDimRel>());
					}
					idxPkAndDimRelMap.get(key).add(dimrel);
				}
			}

			if (allIdxMeasureRel != null) {
				for (RptIdxMeasureRel measurerel : allIdxMeasureRel) {
					String key = measurerel.getId().getIndexNo() + "_"
							+ measurerel.getId().getIndexVerId();
					if (idxPkAndMeasureRelMap.get(key) == null) {
						idxPkAndMeasureRelMap.put(key,
								new ArrayList<RptIdxMeasureRel>());
					}
					idxPkAndMeasureRelMap.get(key).add(measurerel);
				}
			}

			if (allDimTypeList != null) {
				for (RptDimTypeInfo dim : allDimTypeList) {
					allDimTypeInfoMap.put(dim.getDimTypeNo(), dim);
				}
			}

			if (allDimItemList != null) {
				for (RptDimItemInfoVO item : allDimItemList) {
					allDimItemInfoMap.put(
							item.getDimTypeNo() + "_" + item.getDimItemNo(),
							item);
				}
			}

			if (allModules != null) {
				for (RptSysModuleInfo module : allModules) {
					allSysModuleInfoMap.put(module.getSetId(), module);
				}
			}
			for (RptIdxInfo info : infos) {
				String indexNo = info.getId().getIndexNo();
				String catalogNo = info.getIndexCatalogNo();
				String indexType = info.getIndexType();
				RptIdxBusiExt ext = allExtMap.get(indexNo);
				String firstCatalogNm = "";
				String secondCatalogNm = "";
				String thirdCatalogNm = "";
				String indexUsualNm = "";
				RptIdxCatalog firstCatalog = catalogMap.get(catalogNo);
				RptIdxCatalog secondCatalog = null;
				RptIdxCatalog thirdCatalog = null;
				if (firstCatalog != null) {
					firstCatalogNm = firstCatalog.getIndexCatalogNm();
					secondCatalog = catalogMap.get(firstCatalog.getUpNo());
				}
				if (secondCatalog != null) {
					secondCatalogNm = secondCatalog.getIndexCatalogNm();
					thirdCatalog = catalogMap.get(secondCatalog.getUpNo());
				}
				if (thirdCatalog != null) {
					thirdCatalogNm = thirdCatalog.getIndexCatalogNm();
				}
				String oldFirstNm = firstCatalogNm;
				String oldSecondNm = secondCatalogNm;
				String oldThirdNm = thirdCatalogNm;
				if (thirdCatalog != null) {
					firstCatalogNm = oldThirdNm;
					thirdCatalogNm = oldFirstNm;
				} else if (secondCatalog != null) {
					firstCatalogNm = oldSecondNm;
					secondCatalogNm = oldFirstNm;
				}

				IndexImportVO vo = new IndexImportVO();

				if (ext != null) {
					indexUsualNm = ext.getIndexUsualNm();
					if (indexUsualNm == null) {
						indexUsualNm = "";
					}
					vo.setBusiDef(ext.getBusiDef());
					vo.setBusiRule(ext.getBusiRule());

				}
				vo.setIndexNo(indexNo);
				vo.setFirstCatalog(firstCatalogNm);
				vo.setSecondCatalog(secondCatalogNm);
				vo.setThirdCatalog(thirdCatalogNm);
				vo.setIndexNm(info.getIndexNm());
				vo.setIndexUsualNm(indexUsualNm);
				vo.setStartDate(info.getStartDate());
				vo.setCalcCycle(info.getCalcCycle());
				vo.setIndexType(indexType);
				vo.setSrcIndexNo(info.getSrcIndexNo());
				vo.setIsSum(info.getIsSum());
				vo.setIsSave(info.getIsSave());
				vo.setIndexSts(info.getIndexSts());
				vo.setDataType(info.getDataType());
				vo.setDataUnit(info.getDataUnit());
				vo.setDeptId(info.getDeptId());
				vo.setUserId(info.getUserId());
				vo.setStatType(info.getStatType());
				vo.setBusiLibId(info.getBusiLibId());//lcy 业务分库
				vo.setRemark(info.getRemark());
				vo.setBusiType(info.getBusiType());
				String key = indexNo + "_" + indexVerId;
				String dims = "";
				if (COMPOSITE_INDEX.equals(indexType)
						|| DERIVE_INDEX.equals(indexType)) {// 组合指标/派生指标
					String formulaStr = "";
					List<RptIdxDimRel> dimrels = idxPkAndDimRelMap.get(key);
					param1_.clear();
					if (dimrels != null) {
						for (RptIdxDimRel dimrel : dimrels) {
							param1_.put(dimrel.getOrderNum(), dimrel);
						}
					}
					Set<BigDecimal> dimrelkeys = param1_.keySet();
					if (dimrelkeys.size() > 0) {// 有序的拼凑维度
						for (BigDecimal dimrelkey : dimrelkeys) {
							RptIdxDimRel dimRel = param1_.get(dimrelkey);
							String dimno = dimRel.getId().getDimNo();
							RptDimTypeInfo dimtype = allDimTypeInfoMap
									.get(dimno);
							if (dimtype == null) {
								continue;
							}
							String dimnm = dimtype.getDimTypeNm();
							dims += dimnm + ",";
						}
					}
					if (dims.length() > 0) {
						dims = dims.substring(0, dims.length() - 1);
					}
					vo.setDims(dims);
					if (COMPOSITE_INDEX.equals(indexType)) {
						List<RptIdxFilterInfo> filters = idxPKAndFilterRelMap
								.get(key);
						if (filters != null) {
							int size = filters.size();
							for (int i = 0; i < size; i++) {
								RptIdxFilterInfo filter = filters.get(i);
								String dimno = filter.getId().getDimNo();
								String filterVal = filter.getFilterVal();
								RptDimTypeInfo dimtype = allDimTypeInfoMap
										.get(dimno);
								String dimnm = dimtype.getDimTypeNm();
								if (StringUtils.isNotEmpty(dimnm)) {
									dimnm += ":";
								}
								if (StringUtils.isNotEmpty(filterVal)) {
									String[] itemNoArr = StringUtils.split(filterVal, ',');
									for (String itemNo : itemNoArr) {
										RptDimItemInfoVO item = allDimItemInfoMap
												.get(dimtype.getDimTypeNo()
														+ "_" + itemNo);
										String itemNm = item.getDimItemNm();
										dimnm += itemNm + ",";
									}
									if (dimnm.length() > 0) {
										dimnm = dimnm.substring(0,
												dimnm.length() - 1);
									}
								} else {
									dimnm = "";
								}
								if (dimnm.length() > 0) {
									if (!formulaStr.equals("")) {
										formulaStr += "\n";
									}
									formulaStr += dimnm;
								}
							}
						}
					} else {
						RptIdxFormulaInfo formula = idxPkAndFormulaRelMap
								.get(key);
						formulaStr = formula == null ? "" : formula
								.getFormulaContent();
					}
					vo.setIndexFormula(formulaStr);
				} else {
					List<RptIdxMeasureRel> measurerels = idxPkAndMeasureRelMap
							.get(key);
					List<RptIdxDimRel> dimrels = idxPkAndDimRelMap.get(key);
					String datasetCnNm = "";
					String datasetId = "";
					
					if (measurerels != null && measurerels.size()>0) {
						
						String setId = measurerels.get(0).getId().getDsId();
						RptSysModuleInfo module = allSysModuleInfoMap
								.get(setId);
						datasetId = module.getSetId();
						datasetCnNm = module.getSetNm();
						
						if(indexType.equals(SUM_ACCOUNT_INDEX)){
							String merNos = "";
							for (RptIdxMeasureRel mrel : measurerels) {
								merNos += mrel.getId().getMeasureNo()+",";
							}
							if(StringUtils.isNotBlank(merNos)){
								merNos = StringUtils.substring(merNos, 0,merNos.length()-1);
							}
							datasetId = datasetId+":"+merNos;
						}
						else{
							datasetId = datasetId+":"+measurerels.get(0).getStoreCol();
						}
					}
					if (dimrels != null) {
						vo.setDims(dims);
					}
					vo.setDatasetCnNm(datasetCnNm);
					vo.setDatasetId(datasetId);;
				}
				list.add(vo);
			}
		}
		return list;
	}

	
	public List<IndexImportVO> getExportIdxInfo(Map<String, Object> map) {
		map.put("endDate", "29991231");
		List<RptIdxInfo> infos = this.idxDao.listIdxInfo(map);// input parameter
		List<IndexImportVO> list = new ArrayList<IndexImportVO>();// output(return)
																	// parameter
		if (infos != null && infos.size() > 0) {
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String defDeptParamTypeNo = propertiesUtils
					.getProperty("defDeptParamTypeNo");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			String indexDimTypeNo = propertiesUtils
					.getProperty("indexDimTypeNo");
			Set<String> sysDimSet = new HashSet<String>();
			sysDimSet.add(orgDimTypeNo);
			sysDimSet.add(dateDimTypeNo);
			sysDimSet.add(currencyDimTypeNo);
			sysDimSet.add(indexDimTypeNo);
			// temp params
			Map<String, Object> param0_ = new HashMap<String, Object>();
			param0_.put("idxNoList", map.get("list"));
			Map<BigDecimal, RptIdxDimRel> param1_ = new TreeMap<BigDecimal, RptIdxDimRel>();
			List<RptIdxCatalog> catalogs = this.getAllCatalog();
			List<RptIdxBusiExt> allExt = this.idxDao.findLIkeInfoList(param0_);
			List<RptIdxFilterInfo> allIdxFilterInfoList = this.idxDao
					.getIdxFilterInfoListByParams(param0_);
			List<RptIdxFormulaInfo> allIdxFormulaInfoList = this.idxDao
					.getIdxFormulaByParams(param0_);
			List<RptIdxDimRel> allIdxDimRel = this.idxDao
					.getIdxDimRelByParams(param0_);
			List<RptIdxMeasureRel> allIdxMeasureRel = this.idxDao
					.getIdxMeasureRelByParams(param0_);
			List<RptSysModuleInfo> allModules = this.rptDataSetDao
					.getDataSetsByParams(new HashMap<String, Object>());// 通过数据源获取数据集
			List<CommonComboBoxNode> defDeptBoxNodes = this
					.getParamDeptListByParams(defDeptParamTypeNo,
							BioneSecurityUtils.getCurrentUserInfo()
									.getCurrentLogicSysNo());

			// 维度相关内存信息
			List<RptDimTypeInfo> allDimTypeList = this.rptDimDao
					.getAllRptDimTypeInfos();
			List<RptDimItemInfoVO> allDimItemList = this.rptDimDao
					.getAllRptDimItemInfos();
			
			List<RptOrgInfo> orgNos = this.getAllEntityList(RptOrgInfo.class, "id.orgNo", false);			

			Map<String, List<RptIdxFilterInfo>> idxPKAndFilterRelMap = Maps
					.newHashMap();
			Map<String, RptIdxFormulaInfo> idxPkAndFormulaRelMap = Maps
					.newHashMap();
			Map<String, List<RptIdxDimRel>> idxPkAndDimRelMap = Maps
					.newHashMap();
			Map<String, List<RptIdxMeasureRel>> idxPkAndMeasureRelMap = Maps
					.newHashMap();
			Map<String, RptIdxBusiExt> allExtMap = Maps.newHashMap();
			Map<String, String> defDeptMap = Maps.newHashMap();
			Map<String, RptIdxCatalog> catalogMap = Maps.newHashMap();
			Map<String, RptDimTypeInfo> allDimTypeInfoMap = Maps.newHashMap();
			Map<String, RptDimItemInfoVO> allDimItemInfoMap = Maps.newHashMap();
			Map<String, RptSysModuleInfo> allSysModuleInfoMap = Maps
					.newHashMap();

			if (defDeptBoxNodes != null) {
				for (int i = 0; i < defDeptBoxNodes.size(); i++) {
					CommonComboBoxNode boxNode = defDeptBoxNodes.get(i);
					defDeptMap.put(boxNode.getId(), boxNode.getText());
				}
			}
			if (allExt != null) {
				for (RptIdxBusiExt ext : allExt) {
					allExtMap.put(ext.getId().getIndexNo(), ext);
				}
			}

			if (catalogs != null) {
				for (RptIdxCatalog catalog : catalogs) {
					catalogMap.put(catalog.getIndexCatalogNo(), catalog);
				}
			}

			if (allIdxFilterInfoList != null) {
				for (RptIdxFilterInfo filter : allIdxFilterInfoList) {
					String key = filter.getId().getIndexNo() + "_"
							+ filter.getId().getIndexVerId();
					if (idxPKAndFilterRelMap.get(key) == null) {
						idxPKAndFilterRelMap.put(key,
								new ArrayList<RptIdxFilterInfo>());
					}
					idxPKAndFilterRelMap.get(key).add(filter);
				}
			}

			if (allIdxFormulaInfoList != null) {
				for (RptIdxFormulaInfo formula : allIdxFormulaInfoList) {
					idxPkAndFormulaRelMap.put(formula.getId().getIndexNo()
							+ "_" + formula.getId().getIndexVerId(), formula);
				}
			}

			if (allIdxDimRel != null) {
				for (RptIdxDimRel dimrel : allIdxDimRel) {
					String key = dimrel.getId().getIndexNo() + "_"
							+ dimrel.getId().getIndexVerId();
					if (idxPkAndDimRelMap.get(key) == null) {
						idxPkAndDimRelMap.put(key,
								new ArrayList<RptIdxDimRel>());
					}
					idxPkAndDimRelMap.get(key).add(dimrel);
				}
			}

			if (allIdxMeasureRel != null) {
				for (RptIdxMeasureRel measurerel : allIdxMeasureRel) {
					String key = measurerel.getId().getIndexNo() + "_"
							+ measurerel.getId().getIndexVerId();
					if (idxPkAndMeasureRelMap.get(key) == null) {
						idxPkAndMeasureRelMap.put(key,
								new ArrayList<RptIdxMeasureRel>());
					}
					idxPkAndMeasureRelMap.get(key).add(measurerel);
				}
			}

			if (allDimTypeList != null) {
				for (RptDimTypeInfo dim : allDimTypeList) {
					allDimTypeInfoMap.put(dim.getDimTypeNo(), dim);
				}
			}

			if (allDimItemList != null && allDimItemList.size()>0) {
				for (RptDimItemInfoVO item : allDimItemList) {
					allDimItemInfoMap.put(
							item.getDimTypeNo() + "_" + item.getDimItemNo(),
							item);
				}
			}
			if(orgNos != null && orgNos.size() > 0){
				for (RptOrgInfo org : orgNos) {
					RptDimItemInfoVO info = new RptDimItemInfoVO();
					info.setDimTypeNo("ORG");
					info.setDimItemNo(org.getId().getOrgNo());
					info.setDimItemNm(org.getOrgNm());
					allDimItemInfoMap.put(
							"ORG" + "_" + org.getId().getOrgNo(),
							info);
				}
			}
			if (allModules != null && allModules.size() > 0) {
				for (RptSysModuleInfo module : allModules) {
					allSysModuleInfoMap.put(module.getSetId(), module);
				}
			}
			for (RptIdxInfo info : infos) {
				String indexNo = info.getId().getIndexNo();
				String catalogNo = info.getIndexCatalogNo();
				String indexType = info.getIndexType();
				long indexVerId = info.getId().getIndexVerId();
				RptIdxBusiExt ext = allExtMap.get(indexNo);
				String firstCatalogNm = "";
				String secondCatalogNm = "";
				String thirdCatalogNm = "";
				String indexUsualNm = "";
				RptIdxCatalog firstCatalog = catalogMap.get(catalogNo);
				RptIdxCatalog secondCatalog = null;
				RptIdxCatalog thirdCatalog = null;
				if (firstCatalog != null) {
					firstCatalogNm = firstCatalog.getIndexCatalogNm();
					secondCatalog = catalogMap.get(firstCatalog.getUpNo());
				}
				if (secondCatalog != null) {
					secondCatalogNm = secondCatalog.getIndexCatalogNm();
					thirdCatalog = catalogMap.get(secondCatalog.getUpNo());
				}
				if (thirdCatalog != null) {
					thirdCatalogNm = thirdCatalog.getIndexCatalogNm();
				}
				String oldFirstNm = firstCatalogNm;
				String oldSecondNm = secondCatalogNm;
				String oldThirdNm = thirdCatalogNm;
				if (thirdCatalog != null) {
					firstCatalogNm = oldThirdNm;
					thirdCatalogNm = oldFirstNm;
				} else if (secondCatalog != null) {
					firstCatalogNm = oldSecondNm;
					secondCatalogNm = oldFirstNm;
				}

				IndexImportVO vo = new IndexImportVO();

				if (ext != null) {
					indexUsualNm = ext.getIndexUsualNm();
					if (indexUsualNm == null) {
						indexUsualNm = "";
					}
					vo.setBusiDef(ext.getBusiDef());
					vo.setBusiRule(ext.getBusiRule());

				}
				vo.setIndexNo(indexNo);
				vo.setFirstCatalog(firstCatalogNm);
				vo.setSecondCatalog(secondCatalogNm);
				vo.setThirdCatalog(thirdCatalogNm);
				vo.setIndexNm(info.getIndexNm());
				vo.setIndexUsualNm(indexUsualNm);
				vo.setStartDate(info.getStartDate());
				vo.setCalcCycle(info.getCalcCycle());
				vo.setIndexType(indexType);
				vo.setSrcIndexNo(info.getSrcIndexNo());
				vo.setIsSum(info.getIsSum());
				vo.setIsSave(info.getIsSave());
				vo.setIndexSts(info.getIndexSts());
				vo.setDataType(info.getDataType());
				vo.setDataUnit(info.getDataUnit());
				vo.setDeptId(info.getDeptId());
				vo.setUserId(info.getUserId());
				vo.setStatType(info.getStatType());
				vo.setBusiLibId(info.getBusiLibId());//lcy 业务分库
				vo.setRemark(info.getRemark());
				vo.setLineId(info.getLineId());
				vo.setBusiType(info.getBusiType());
				String key = indexNo + "_" + indexVerId;
				String dims = "";
				if (COMPOSITE_INDEX.equals(indexType)
						|| DERIVE_INDEX.equals(indexType)) {// 组合指标/派生指标
					String formulaStr = "";
					List<RptIdxDimRel> dimrels = idxPkAndDimRelMap.get(key);
					param1_.clear();
					if (dimrels != null) {
						for (RptIdxDimRel dimrel : dimrels) {
							param1_.put(dimrel.getOrderNum(), dimrel);
						}
					}
					Set<BigDecimal> dimrelkeys = param1_.keySet();
					if (dimrelkeys.size() > 0) {// 有序的拼凑维度
						for (BigDecimal dimrelkey : dimrelkeys) {
							RptIdxDimRel dimRel = param1_.get(dimrelkey);
							String dimno = dimRel.getId().getDimNo();
							RptDimTypeInfo dimtype = allDimTypeInfoMap
									.get(dimno);
							if (dimtype == null) {
								continue;
							}
							String dimnm = dimtype.getDimTypeNm();
							dims += dimnm + ",";
						}
					}
					if (dims.length() > 0) {
						dims = dims.substring(0, dims.length() - 1);
					}
					vo.setDims(dims);
					if (COMPOSITE_INDEX.equals(indexType)) {
						List<RptIdxFilterInfo> filters = idxPKAndFilterRelMap
								.get(key);
						if (filters != null) {
							int size = filters.size();
							for (int i = 0; i < size; i++) {
								RptIdxFilterInfo filter = filters.get(i);
								String dimno = filter.getId().getDimNo();
								String filterVal = filter.getFilterVal();
								RptDimTypeInfo dimtype = allDimTypeInfoMap
										.get(dimno);
								String dimnm = dimtype.getDimTypeNm();
								if (StringUtils.isNotEmpty(dimnm)) {
									dimnm += ":";
								}
								if (StringUtils.isNotEmpty(filterVal)) {
									String[] itemNoArr = StringUtils.split(filterVal, ',');
									for (String itemNo : itemNoArr) {
										RptDimItemInfoVO item = allDimItemInfoMap
												.get(dimtype.getDimTypeNo()
														+ "_" + itemNo);
										String itemNm = item.getDimItemNm();
										dimnm += itemNm + ",";
									}
									if (dimnm.length() > 0) {
										dimnm = dimnm.substring(0,
												dimnm.length() - 1);
									}
								} else {
									dimnm = "";
								}
								if (dimnm.length() > 0) {
									if (!formulaStr.equals("")) {
										formulaStr += "\n";
									}
									formulaStr += dimnm;
								}
							}
						}
					} else {
						RptIdxFormulaInfo formula = idxPkAndFormulaRelMap
								.get(key);
						formulaStr = formula == null ? "" : formula
								.getFormulaContent();
					}
					vo.setIndexFormula(formulaStr);
				} else {
					List<RptIdxMeasureRel> measurerels = idxPkAndMeasureRelMap
							.get(key);
					List<RptIdxDimRel> dimrels = idxPkAndDimRelMap.get(key);
					String datasetCnNm = "";
					String datasetId = "";
					
					if (measurerels != null && measurerels.size()>0) {
						
						String setId = measurerels.get(0).getId().getDsId();
						RptSysModuleInfo module = allSysModuleInfoMap
								.get(setId);
						datasetId = module.getSetId();
						datasetCnNm = module.getSetNm();
						
						if(indexType.equals(SUM_ACCOUNT_INDEX)){
							String merNos = "";
							for (RptIdxMeasureRel mrel : measurerels) {
								merNos += mrel.getId().getMeasureNo()+",";
							}
							if(StringUtils.isNotBlank(merNos)){
								merNos = StringUtils.substring(merNos, 0,merNos.length()-1);
							}
							datasetId = datasetId+":"+merNos;
						}
						else{
							datasetId = datasetId+":"+measurerels.get(0).getStoreCol();
						}
					}
					if (dimrels != null) {
						vo.setDims(dims);
					}
					vo.setDatasetCnNm(datasetCnNm);
					vo.setDatasetId(datasetId);;
				}
				list.add(vo);
			}
		}
		return list;
	}
	

	public List<RptIdxInfo> getIdxListByNmList(Map<String, Object> params) {
		return this.idxDao.getIdxListByNmList(params);
	}

	public List<RptIdxBusiExt> getIdxListByUsualNmList(
			Map<String, Object> params) {
		return this.idxDao.getIdxListByUsualNmList(params);
	}

	public boolean isAsSrcIndexNos(List<String> indexNos) {
		List<RptIdxInfo> idxs = this.idxDao
				.getAllIdxInfoBean(new HashMap<String, Object>());
		Map<String, String> idxNoMap = Maps.newHashMap();
		if (indexNos != null && indexNos.size() > 0) {
			for (String idx : indexNos) {
				idxNoMap.put(idx, idx);
			}
		}
		if (idxs != null && idxs.size() > 0) {
			for (RptIdxInfo temp : idxs) {
				String srcIndexNo = temp.getSrcIndexNo();
				if (StringUtils.isNotEmpty(srcIndexNo)) {
					String[] srcIndexNoArr = StringUtils.split(srcIndexNo, ',');
					for (String srcindexno : srcIndexNoArr) {
						if (idxNoMap.containsKey(srcindexno)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// 检测指标是否被关联
	public boolean checkHasBeenCascaded(Map<String, Object> param) {
		Integer num = this.idxDao.getRelatedCountByIndexNo(param);
		if (num == null || num == 0)
			return false;
		return true;
	}
	
	/**
	 * 检测指标当前版本有没有被引用
	 * @param id
	 * @param vid
	 * @return
	 */
	public boolean idxInfoVerisSrcIdxNo(String indexNo, String indexVerId) {
		if(StringUtils.isNotBlank(indexNo) && StringUtils.isNotBlank(indexVerId)) {
			if (indexNo.endsWith(",")) {
				indexNo = indexNo.substring(0, indexNo.length() - 1);
			}
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.id.indexVerId = ?1";
			RptIdxInfo info = this.baseDAO.findUniqueWithIndexParam(jql, indexNo, Long.parseLong(indexVerId));
			if(null != info) {
				//找出来源指标是当前指标的全部指标
				jql = "select idx.id.indexNo from RptIdxInfo idx where idx.srcIndexNo like ?0 group by idx.id.indexNo";
				List<String> idxList = this.baseDAO.findWithIndexParam(jql, indexNo);
				if(idxList.size() > 0) {
					//判断是否引用当前版本,下游指标的开始日期小于该指标的结束日期，下游指标的结束日期大于该指标的开始日期
					jql = "select idx.id.indexNo from RptIdxInfo idx where idx.id.indexNo in ?0 and (idx.startDate < ?1 and idx.endDate > ?2)";
					List<String> chidxList = this.baseDAO.findWithIndexParam(jql, idxList, info.getEndDate(), info.getStartDate());
					if(chidxList.size() > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
		
	private CommonTreeNode generateStoreNode(String basePath){
		CommonTreeNode node = new CommonTreeNode();
		node.setId("store");
		node.setText("收藏目录");
		node.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		node.setIsParent(true);
		return node;
	}
	
	public List<CommonTreeNode> getTmpStoreTree(String basePath, String tmpId, String busiType) {
		List<CommonTreeNode> resultNodes = new ArrayList<CommonTreeNode>();
		resultNodes.add(generateStoreNode(basePath));
		String jql = "select idx from RptIdxInfo idx,RptDesignFavInfo fav where fav.templateId = ?0 and fav.indexNo = idx.id.indexNo and idx.endDate = ?1 and idx.isRptIndex = ?2 and idx.busiType = ?3";
		List<RptIdxInfo> idxList = this.baseDAO.findWithIndexParam(jql, tmpId, "29991231", "N", busiType);
		if(idxList != null && idxList.size() > 0){
			Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
			List<RptIdxMeasureInfoVO> measureList = new ArrayList<RptIdxMeasureInfoVO>();// 度量信息
			if(idxList != null && idxList.size() > 0){
				List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
				for(RptIdxMeasureInfo info : infos){
					measureInfoMap.put(info.getMeasureNo(), info);
				}
				Map<String,Object> condition = new HashMap<String, Object>();
				condition.put("idxList", idxList);
				measureList = measureDao.getMeasure(condition);
			}
			Map<String,List<RptIdxMeasureInfoVO>> measureMap = new HashMap<String, List<RptIdxMeasureInfoVO>>(); 
			if (measureList != null && measureList.size()>0){
				for(RptIdxMeasureInfoVO measure : measureList){
					RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
					measure.setMeasureNm(info.getMeasureNm());
					measure.setMeasureType(info.getMeasureType());
					measure.setCalcFormula(info.getCalcFormula());
					measure.setRemark(info.getRemark());
					List<RptIdxMeasureInfoVO> vos = measureMap.get(measure.getIndexNo()+"-"+measure.getIndexVerId());
					if(vos == null){
						vos = new ArrayList<RptIdxMeasureInfoVO>();
						measureMap.put(measure.getIndexNo()+"-"+measure.getIndexVerId(),vos);
					}
					vos.add(measure);
				}
			}
			List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
			if(idxList != null && idxList.size() > 0){
				Map<String,Object> condition = new HashMap<String, Object>();
				List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
				for(RptIdxInfo idx : idxList){
					if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
							|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
							||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX))
					{
						relidx.add(idx);
					}
				}
				if(relidx.size()>0){
					condition.put("idxList", relidx);
					dimRelList = this.getDimNosOfIndex(condition);
				}
			}
			Map<String,String> dimRelMap = new HashMap<String, String>(); 
			if (dimRelList != null && dimRelList.size()>0){
				for(RptIdxDimRel rel : dimRelList){
					dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
				}
			}
			for(RptIdxInfo idx : idxList){
				CommonTreeNode treeNode = generateIndexNode(basePath, idx, "1", "1", dimRelMap, measureMap);
				treeNode.setUpId("store");
				resultNodes.add(treeNode);
			}
		}
		return resultNodes;
	}

	/**
	 * 指标目录移动
	 * @param moveFlag
	 * @param selIds
	 * @param rankOrders
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, String> idxMove(String moveFlag,String selIds,String rankOrders) {
		Map<String, String> param = Maps.newHashMap();
		if(StringUtils.isNotEmpty(selIds) && StringUtils.isNotEmpty(rankOrders) && StringUtils.isNotEmpty(moveFlag)){
			String[] selId = StringUtils.split(selIds, ',');
			String[] rankOrder = StringUtils.split(rankOrders, ',');
			if("catalogNode".equals(moveFlag)){
				for(int i=0;i<selId.length;i++){
					String jql =" update RptIdxCatalog t set t.catalogOrder =?0 where t.indexCatalogNo =?1 ";
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
	 * 获取全部目录，
	 * @param upNos
	 * @param catalogs
	 */
	public void getAllIndexCatalogNos(List<String> upNos,List<String> catalogs){
		if(upNos != null && upNos.size() > 0){
			String jql = "select cat.indexCatalogNo from RptIdxCatalog cat where cat.upNo in ?0";
			List<String> indexCatalogNos = this.baseDAO.findWithIndexParam(jql, upNos);
			if(indexCatalogNos != null && indexCatalogNos.size() > 0){
				upNos = indexCatalogNos;
				catalogs.addAll(upNos);
				getAllIndexCatalogNos(upNos, catalogs);
			}
			
		}
	}
	
	/**
	 * 获取来源指标的维度信息
	 * @param indexNo
	 * @return
	 */
	public List<RptDimTypeInfo> getDimListBySrcIndex(String indexNo){
		String jql = "select dim from RptDimTypeInfo dim,RptIdxInfo idx,RptIdxDimRel rel where idx.id.indexNo = ?0 and idx.endDate = ?1 and idx.id.indexNo = rel.id.indexNo and idx.id.indexVerId = rel.id.indexVerId and rel.id.dimNo = dim.dimTypeNo";
		List<RptDimTypeInfo> dims = this.baseDAO.findWithIndexParam(jql, indexNo,"29991231");
		return dims;
	}
	
	public IdxEditorInfoVO getIdxEditorInfo(String indexNo, String indexVerId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("indexNo", indexNo);
		if (StringUtils.isNotEmpty(indexVerId)) {
			params.put("indexVerId", indexVerId);
		}
		List<IdxEditorInfoVO> list =  this.idxDao.listIdxEditorInfoShow(params);
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	public RptIdxFilterInfo getFilterInfo(String indexNo, String indexVerId,
			String dimNo) {
		if (StringUtils.isEmpty(indexVerId)) {
			return null;
		}
		String jql = "select t from RptIdxFilterInfo t where t.id.indexNo=?0 and t.id.indexVerId=?1 and t.id.dimNo=?2";
		List<RptIdxFilterInfo> list = this.baseDAO.findWithIndexParam(jql,
				indexNo, Long.parseLong(indexVerId), dimNo);
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	public RptIdxInfo getInfoById(String indexNo, String indexVerId) {
		String jql = "select t from RptIdxInfo t where t.id.indexNo=:indexNo ";
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexNo", indexNo);
		StringBuilder stb = new StringBuilder(jql);
		if (StringUtils.isNotEmpty(indexVerId)) {
			stb.append("and t.id.indexVerId=:indexVerId ");
			params.put("indexVerId", Long.parseLong(indexVerId));
		}
		stb.append(" order by t.id.indexVerId desc");
		List<RptIdxInfo> list = this.baseDAO.findWithNameParm(stb.toString(),
				params);
		return list.size() > 0 ? list.get(0) : null;
	}

	public Map<String, Object> getIdxRelInfo(String indexNo, String indexVerId) {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexNo", indexNo);
		params.put("indexVerId", Long.parseLong(indexVerId));
		@SuppressWarnings("serial")
		Map<String, String> jqls = new HashMap<String, String>() {
			{
				put("idxDimRel",
						"select t from RptIdxDimRel t where t.id.indexNo=:indexNo and t.id.indexVerId=:indexVerId");
				put("idxFormulaInfo",
						"select t from RptIdxFormulaInfo t where t.id.indexNo=:indexNo and t.id.indexVerId=:indexVerId");
				put("idxFilterInfo",
						"select t from RptIdxFilterInfo t where t.id.indexNo=:indexNo and t.id.indexVerId=:indexVerId");
			}
		};
		for (Iterator<String> iter = jqls.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			String val = jqls.get(key);
			result.put(key, this.baseDAO.findWithNameParm(val, params));
		}
		return result;
	}
	
	/**
	 * 获取指标自动编号
	 * @param prefix
	 * @return
	 */
	public String getAutoIndexNo(String prefix){
		Integer len = prefix.length();
		String idxNo = prefix;
		for(int a=0;a<(8-len);a++){
			idxNo += "_";
		}
		String idx = null;
		
		if(null!=idxNo && !"".equals(idxNo)){
			String jql = " select max(t.id.indexNo) from RptIdxInfo t where t.id.indexNo like ?0 ";
			idx = this.baseDAO.findUniqueWithIndexParam(jql, idxNo);
			if(StringUtils.isNotBlank(idx)){
				Integer data = Integer.parseInt(idx.substring(len))+1;
				String sign = ""; 
				for(int a=0;a<(8-len-String.valueOf(data).length());a++){
					sign += "0";
				}
				sign += String.valueOf(data);
				idxNo = prefix+sign;
			}else{
				String data ="";
				for(int a=0;a<(8-len-1);a++){
					data += "0";
				}
				data += "1";
				idxNo = prefix + data;
			}
		}
		return idxNo;
	}
	
	/**
	 * 指标编号前缀
	 * 
	 * @return
	 */	
	public List<CommonComboBoxNode> getIndexNoPrefixTree(String basePath,String indexType) {
		Map<String, String> idxNoPrefix = Maps.newHashMap();
		idxNoPrefix.put("01", "rootIndex");//根指标
		idxNoPrefix.put("02", "compIndex");//组合指标
		idxNoPrefix.put("03", "deriveIndex");//派生指标
		idxNoPrefix.put("04", "genericIndex");//泛化指标
		Map<String, Object> conditions = Maps.newHashMap();
		conditions.put("itemSts", "Y");
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		
		String jql = "select param from BioneParamInfo param where param.paramTypeNo = ?0";
		List<BioneParamInfo> infos = this.baseDAO.findWithIndexParam(jql,idxNoPrefix.get(indexType));
	    if (infos != null && infos.size() > 0) {
	    		for (BioneParamInfo info : infos) {
	    			CommonComboBoxNode node = new CommonComboBoxNode();
	    			node.setId(info.getParamValue());
	    			node.setText(info.getParamName());
	    			nodes.add(node);
	    		}
	    	}
	    return nodes;
	}

	// 根据当前机构及部门编号获取唯一的部门ID
	public String getDeptIdByInfo(String orgNo, String deptNo) {
		String jql = "select dept.deptId from BioneDeptInfo dept where dept.logicSysNo = ?0 and dept.orgNo = ?1 and dept.deptNo =?2";
		String deptId = this.baseDAO.findUniqueWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
				orgNo, deptNo);
		return deptId;
	}

	// 获取当前登录人的信息
	public BioneUserInfo getLinkInfoList(String userId) {
		BioneUserInfo linkInfo = this
				.getEntityById(BioneUserInfo.class, userId);
		return linkInfo;
	}
	//将前台获取的当前登录人的信息存放到Map中
	public Map<String, Object> inverse() {
		Map<String, Object> params = new HashMap<String, Object>();
		String userId = BioneSecurityUtils.getCurrentUserId();
		String userName = BioneSecurityUtils.getCurrentUserInfo().getUserName();
		String orgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		String deptNo = BioneSecurityUtils.getCurrentUserInfo().getDeptNo();
		String deptId = this.getDeptIdByInfo(orgNo, deptNo);
		String deptName = BioneSecurityUtils.getCurrentUserInfo().getDeptName();
		BioneUserInfo linkInfo = this.getLinkInfoList(userId);
		String tel = linkInfo.getTel();
		String email = linkInfo.getEmail();
		params.put("userId", userId);
		params.put("userName", userName);
		params.put("deptId", deptId);
		params.put("deptName", deptName);
		params.put("tel", tel);
		params.put("email", email);
		return params;
	}
	
	public Long getIdxMaxVerId(Map<String, Object> map) {
		return this.idxDao.getIdxMaxVerId(map);
	}
	
	public List<RptIdxInfo> getIdxListByIdList(Map<String, Object> params) {
		return this.idxDao.getIdxListByIdList(params);
	}
	
	public List<RptIdxMeasureRel> getIdxMeasureRelMaxByParams(
			Map<String, Object> params) {
		return this.idxDao.getIdxMeasureRelMaxByParams(params);
	}
	
	public List<String> initAuthIndexNos(List<BioneAuthObjResRel> rels){
        Set<String> authIndexNos = new HashSet<String>();
        Set<String> catalogIds = new HashSet<String>();
        if(rels!=null&& rels.size()>0){
            for(BioneAuthObjResRel rel :rels){
                if(StringUtils.isNotBlank(rel.getResType())){
                    if(rel.getResType().equals(GlobalConstants4frame.RES_TYPE_ROOT)){
                        String jql ="select  idx.id.indexNo from RptIdxInfo idx where 1=1 and idx.endData=?0 and idx.isRptIndex=?1";
                        List<String> indexNos = this.baseDAO.findWithIndexParam(jql,"29991231","N");
                        return indexNos;
                    }
                    else if(rel.getResType().equals(GlobalConstants4frame.RES_TYPE_CATA)){
                        catalogIds.add(rel.getId().getResId());
                    }
                    else if(rel.getResType().equals(GlobalConstants4frame.RES_TYPE_NODE)){
                        authIndexNos.add(rel.getId().getResId());
                    }
                }
                else{
                    authIndexNos.add(rel.getId().getResId());
                }
            }
        }
        
        if(catalogIds != null && catalogIds.size()>0){
            List<String> allcatalogIds = new ArrayList<String>();
            getAllCatalog(new ArrayList<String>(catalogIds), allcatalogIds);
            if(allcatalogIds != null && allcatalogIds.size()>0){
                String jql ="select distinct idx.id.indexNo from RptIdxInfo idx where 1=1 and idx.isRptIndex=?0 and idx.indexCatalogNo in ?1";
                List<String> indexNos = this.baseDAO.findWithIndexParam(jql,"N",allcatalogIds);
                if(indexNos != null && indexNos.size()>0){
                    authIndexNos.addAll(indexNos);
                }
            }
        }
        return new ArrayList<String>(authIndexNos);
    }
	
	private void getAllCatalog(List<String> catalogIds,List<String> allcatalogIds){
        allcatalogIds.addAll(catalogIds);
        String jql = "select cata.indexCatalogNo from RptIdxCatalog cata where cata.upNo in ?0";
        List<String> upcatalogIds = new ArrayList<String>();
        catalogIds = this.baseDAO.findWithIndexParam(jql, catalogIds);
        if(catalogIds != null && catalogIds.size()>0){
            for(String catalogId : catalogIds){
                if(!allcatalogIds.contains(catalogId)){
                    upcatalogIds.add(catalogId);
                }
            }
        }
        if(upcatalogIds !=null && upcatalogIds.size()>0){
            getAllCatalog(upcatalogIds, allcatalogIds);
        }
    }
	
	public String getBusiTypeByIndexNo(String indexNo) {
		String busiType = GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC;
		String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.endDate = ?1";
		List<RptIdxInfo> idxs = this.baseDAO.findWithIndexParam(jql, indexNo,"29991231");
		if(idxs != null && idxs.size() > 0){
			RptIdxInfo idx = idxs.get(0);
			if(StringUtils.isNotBlank(idx.getBusiType())){
				busiType = idx.getBusiType();
			}
		}
		return busiType;
	}
	
	public String getBusiTypeBySetId(String setId) {
		RptSysModuleInfo info = this.getEntityById(RptSysModuleInfo.class, setId);
		String busiType = GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC;
		if(info!=null){
			if(StringUtils.isNotBlank(info.getBusiType()))
				busiType = info.getBusiType();
		}
		return busiType;
	}
	
	public List<CommonComboBoxNode> getAllVer(String indexNo) {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		if(StringUtils.isNotBlank(indexNo)) {
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0";
			List<RptIdxInfo> idxs = this.baseDAO.findWithIndexParam(jql, indexNo);
			if(idxs.size() > 0) {
				for(RptIdxInfo idx : idxs){
					CommonComboBoxNode node = new CommonComboBoxNode();
					node.setId(idx.getId().getIndexVerId() + "");
					node.setText("V" + idx.getId().getIndexVerId());
					nodes.add(node);
				}
			}
		}
		return nodes;
	}
	
	public Map<String, Object> changeIdxVer(String indexNo, String indexVerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(indexNo) && StringUtils.isNotBlank(indexVerId)) {
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.id.indexVerId = ?1";
			RptIdxInfo idx = this.baseDAO.findUniqueWithIndexParam(jql, indexNo, Long.parseLong(indexVerId));
			if(null != idx) {
				CommonTreeNode tmp = new CommonTreeNode();
				tmp.setId(idx.getId().getIndexNo());
				tmp.setText(idx.getIndexNm());
				tmp.setTitle(idx.getRemark());
				tmp.setData(idx);
				tmp.getParams().put("id", idx.getId().getIndexNo());
				tmp.getParams().put("type", "idxInfo");
				tmp.getParams().put("nodeType", "idxInfo");
				tmp.getParams().put("indexType", idx.getIndexType());
				tmp.getParams().put("indexNo", idx.getId().getIndexNo());
				tmp.getParams().put("indexNm", idx.getIndexNm());
				tmp.getParams().put("indexVerId", String.valueOf((idx.getId().getIndexVerId())));
				tmp.getParams().put("dataType", idx.getDataType());
				tmp.getParams().put("dataUnit", idx.getDataUnit());
				// 给指标增加其维度信息
				if(idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.ROOT_INDEX)
						|| idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMPOSITE_INDEX)
						||idx.getIndexType().equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.DERIVE_INDEX)){
					jql = "select idx from RptIdxDimRel idx where idx.id.indexNo = ?0 and idx.id.indexVerId = ?1";
					List<RptIdxDimRel> idxs = this.baseDAO.findWithIndexParam(jql, indexNo, Long.parseLong(indexVerId));
					StringBuilder dimNos = new StringBuilder();
					for(int i = 0; i < idxs.size(); i++) {
						dimNos.append(idxs.get(i).getId().getDimNo() + ",");
					}
					tmp.getParams().put("dimNos",dimNos.substring(0, dimNos.length() - 1));		
				}
				else{
					tmp.getParams().put("dimNos",com.yusys.bione.plugin.base.common.GlobalConstants4plugin.COMMON_DIM_TYPE);		
				}
				map.put("idxInfo", tmp);
			}
		}
		return map;
	}
	
	/**
	 * 查询报表指标的来源指标信息
	 * @param queryDate
	 * @param srcIndexNo
	 * @param indexVerId
	 * @param rptIndexNo
	 * @return
	 */
	public Map<String, Object> querySrcIdx(String queryDate, String srcIndexNo, String indexVerId, String rptIndexNo){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(queryDate) && StringUtils.isNotBlank(srcIndexNo) && StringUtils.isNotBlank(indexVerId) && StringUtils.isNotBlank(rptIndexNo)) {
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.id.indexVerId = ?1 and (idx.startDate <= ?2 and idx.endDate > ?3)";
			RptIdxInfo idx = this.baseDAO.findUniqueWithIndexParam(jql, rptIndexNo, Long.parseLong(indexVerId), queryDate, queryDate);
			if(null == idx) {
				returnMap.put("error", "当前日期内无报表指标信息");
				return returnMap;
			}
			jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and (idx.startDate <= ?1 and idx.endDate > ?2)";
			RptIdxInfo srcIdx = this.baseDAO.findUniqueWithIndexParam(jql, srcIndexNo, queryDate, queryDate);
			if(null == srcIdx) {
				returnMap.put("error", "当前日期内无来源指标信息");
				return returnMap;
			}
			returnMap.put("srcIdx", srcIdx);
		}else {
			returnMap.put("error", "信息缺失");
		}
		return returnMap;
	}
	
	public Map<String,String> getDimItem(String dimTypeNo,String formerItemNos,String targetItemNos) {
		
		Map<String,String> itemMap = new HashMap<String,String>();
		List<String> formerItemNoList = ArrayUtils.asList(formerItemNos, ",");
		List<String> targetItemNoList = ArrayUtils.asList(targetItemNos, ",");
		
		if(dimTypeNo.equals("ORG")){
			StringBuilder orgBuilder = new StringBuilder();
			String jql = "select org from RptOrgInfo org where org.id.orgNo in ?0";
			//原过滤值
			List<RptOrgInfo> formerOrgs  = this.baseDAO.findWithIndexParam(jql, formerItemNoList);
			for(RptOrgInfo org : formerOrgs) {
				orgBuilder.append(org.getOrgNm()).append(",");
			}
			if(orgBuilder.length()>0) {
				itemMap.put("formerItem", orgBuilder.toString().substring(0,orgBuilder.length()-1));
			}
			
			orgBuilder.setLength(0);
			//当前要要保存的过滤值
			List<RptOrgInfo> targetOrgs  = this.baseDAO.findWithIndexParam(jql, targetItemNoList);
			for(RptOrgInfo org : targetOrgs) {
				orgBuilder.append(org.getOrgNm()).append(",");
			}
			
			if(orgBuilder.length()>0) {
				itemMap.put("targetItem", orgBuilder.toString().substring(0,orgBuilder.length()-1));
			}
		}
		else{
			Map<String, Object> conditions = Maps.newHashMap();
			conditions.put("dimTypeNo", dimTypeNo);
			List<RptDimItemInfo> dimItemList = this.rptDimDao.findDimItemListByTypeNo(conditions);
			
			StringBuilder formerItemBuilder = new StringBuilder();
			StringBuilder targetItemBuilder = new StringBuilder();
			for(RptDimItemInfo dimItem : dimItemList) {
				//原过滤值
				for(String formerItemNo : formerItemNoList) {
					if(formerItemNo.equals(dimItem.getId().getDimItemNo())) {
						formerItemBuilder.append(dimItem.getDimItemNm()).append(",");
						break;
					}
				}
				
				//当前要要保存的过滤值
				for(String targetItemNo : targetItemNoList) {
					if(targetItemNo.equals(dimItem.getId().getDimItemNo())) {
						targetItemBuilder.append(dimItem.getDimItemNm()).append(",");
						break;
					}
				}
			}
			//原过滤值
			if(formerItemBuilder.length()>0) {
				itemMap.put("formerItem", formerItemBuilder.toString().substring(0,formerItemBuilder.length()-1));
			}
			//当前要要保存的过滤值
			if(targetItemBuilder.length()>0) {
				itemMap.put("targetItem", targetItemBuilder.toString().substring(0,targetItemBuilder.length()-1));
			}
		}
		return itemMap;
	}
	
	/**
	 * 加载影响指标grid
	 * @param pager
	 * @param param
	 * @return
	 */
	public SearchResult<RptIdxInfoVO> getInfluenceIdxListByParams(Pager pager, Map<String, Object> param) {
		PageHelper.startPage(pager);
		PageMyBatis<RptIdxInfoVO> page = (PageMyBatis<RptIdxInfoVO>) this.idxDao.influenceIdxList(param);
		SearchResult<RptIdxInfoVO> results = new SearchResult<RptIdxInfoVO>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}
}