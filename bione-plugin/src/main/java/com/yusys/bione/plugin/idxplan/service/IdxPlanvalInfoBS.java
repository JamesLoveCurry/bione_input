
package com.yusys.bione.plugin.idxplan.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalInfo;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResult;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResultPK;
import com.yusys.bione.plugin.idxplan.web.vo.RptIdxPlanResultVO;
import com.yusys.bione.plugin.idxplan.web.vo.RptIdxPlanvalInfoVO;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRelPK;
import com.yusys.bione.plugin.rptidx.repository.IdxCatalogMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.IdxInfoMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.IdxMeasureMybatisDao;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxDimTypeRelVO;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxMeasureInfoVO;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptvalid.entity.RptValidDimRel;
import com.yusys.bione.plugin.rptvalid.entity.RptValidDimRelPK;
import com.yusys.bione.plugin.valid.service.RptValidGroupBS;
import com.yusys.bione.plugin.wizard.web.vo.IdxPlanvalImportVO;

@Service("idxPlanvalInfoBS")
@Transactional(readOnly = true)
public class IdxPlanvalInfoBS extends BaseBS<RptIdxPlanvalResult> {
	protected static Logger log = LoggerFactory.getLogger(IdxPlanvalInfoBS.class);

	@Autowired
	private IdxInfoMybatisDao idxDao;

	@Autowired
	private IdxCatalogMybatisDao catalogDao;

	@Autowired
	private IdxDimRelMybatisDao relDao;

	@Autowired
	private IdxMeasureMybatisDao measureDao;
	
	@Autowired
	private RptValidGroupBS groupBS;
	

	private String nodeTypeFolder = "idxCatalog";
	private String nodeTypeIdx = "idxInfo";

	/**
	 * 显示树状列表
	 * @param basePath
	 *            基本路径
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
	@SuppressWarnings("unchecked")
	public List<CommonTreeNode> getAsyncTree(String contextPath,
			String searchNm, String isShowIdx, /*String isShowMeasure,*/
			String idxNos, String isShowDim, String indexNo,
			/*String exSumAccoutIndex, */String isEngine, String isAuth,
			String nodeType, String upId, String indexVerId, String isPublish,
			String showEmptyFolder, String isPreview, String defSrc) {
		if (StringUtils.isEmpty(upId)) {
			upId = "0";
		}
		if (StringUtils.isEmpty(nodeType)) {
			nodeType = nodeTypeFolder;
		}
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		Map<String, Object> condition = new HashMap<String, Object>();
		if (nodeType.equals(nodeTypeFolder)) {  //节点类型为目录
			if (StringUtils.isNotEmpty(idxNos)
					|| StringUtils.isNotEmpty(indexNo)
					|| StringUtils.isNotEmpty(isEngine) && isEngine.equals("1")
					|| StringUtils.isNotEmpty(searchNm)
					|| StringUtils.isNotEmpty(isPreview)) {
				List<CommonTreeNode> singleTree = this.getAsyncTree(
						contextPath, searchNm, isShowIdx, /*isShowMeasure,*/
						idxNos, isShowDim, indexNo, isEngine, isPublish,
						/*exSumAccoutIndex,*/ isAuth, isPreview, defSrc);
				for (CommonTreeNode node : singleTree) {
					if (node.getId().equals(upId)) {  //目录为父目录
						for (CommonTreeNode child : node.getChildren()) {
							if (child.getParams().get("nodeType")
									.equals(nodeTypeIdx)) {   //子节点为指标类型
								child.setUpId(child.getId());
								resultList.add(child);
							}
						}
					}
					if (node.getUpId().equals(upId)) {
						node.setChildren(null);
						node.setUpId(node.getId());
						resultList.add(node);
					}
				}
			} else {
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				condition.put("upNo", upId);
				condition.put("needOrderByNm", true);
				if (StringUtils.isNotEmpty(defSrc)) {
					condition.put("defSrc", defSrc);
					if (GlobalConstants4plugin.INDEX_DEF_SRC_ORG.equals(defSrc)) {
						condition.put("defOrg", user.getOrgNo());
					} else if (GlobalConstants4plugin.INDEX_DEF_SRC_USER.equals(defSrc)) {
						condition.put("defUser", user.getUserId());
					}
				}
				List<RptIdxCatalog> catalogList = this.catalogDao
						.listIdxCatalog(condition);
				//显示内容为空的目录
				if (StringUtils.isNotEmpty(showEmptyFolder) && showEmptyFolder.equals("1")) {
					Map<String, String> catalogNeedToShow = (Map<String, String>) EhcacheUtils
							.get(BioneSecurityUtils.getCurrentUserId(),
									"catalogNeedToShow");
					for (RptIdxCatalog tmp : catalogList) {
						if (catalogNeedToShow != null
								&& catalogNeedToShow.get(tmp.getIndexCatalogNo()) != null) {
							CommonTreeNode catalog = new CommonTreeNode();
							catalog.setId(tmp.getIndexCatalogNo());
							catalog.setText(tmp.getIndexCatalogNm());
							catalog.setData(tmp);
							catalog.setIsParent(true);
							catalog.setUpId(tmp.getIndexCatalogNo());
							catalog.setIcon(contextPath
									+ GlobalConstants4frame.LOGIC_MODULE_ICON);
							catalog.getParams().put("nodeType", "idxCatalog");
							resultList.add(catalog);
						}
					}

				} else {  //不显示内容为空的目录
					for (RptIdxCatalog tmp : catalogList) {
						CommonTreeNode catalog = new CommonTreeNode();
						catalog.setId(tmp.getIndexCatalogNo());
						catalog.setText(tmp.getIndexCatalogNm());
						catalog.setData(tmp);
						catalog.setIsParent(true);
						catalog.setUpId(tmp.getIndexCatalogNo());
						catalog.setIcon(contextPath
								+ GlobalConstants4frame.LOGIC_MODULE_ICON);
						catalog.getParams().put("nodeType", "idxCatalog");
						resultList.add(catalog);
					}
				}
				
				condition.clear();   //准备指标搜索的条件
				condition.put("indexCatalogNo", upId);
				condition.put("needOrderByNo", true);
				if (StringUtils.isNotEmpty(isPublish) && isPublish.equals("1")) {
					condition.put("isPublish", GlobalConstants4plugin.IS_PUBLISH_TRUE);
				}
//				if (!StringUtils.isEmpty(exSumAccoutIndex)
//						&& exSumAccoutIndex.equals("1")) {
//					condition.put("exSumAccoutIndex",
//									com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
//				}
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						&& isAuth != null && !isAuth.equals("")
						&& StringUtils.isEmpty(defSrc)) {
					List<String> list = BioneSecurityUtils
							.getResIdListOfUser("AUTH_RES_IDX");
					if (list != null && list.size() > 0) {
						condition.put("list", ReBuildParam.splitLists(list));
					}
				}
				if (!(!StringUtils.isEmpty(isShowIdx) && isShowIdx.equals("0"))) {
					if (StringUtils.isNotEmpty(defSrc)) {
						condition.put("defSrc", defSrc);
						if (GlobalConstants4plugin.INDEX_DEF_SRC_ORG.equals(defSrc)) {
							condition.put("defOrg", user.getOrgNo());
						} else if (GlobalConstants4plugin.INDEX_DEF_SRC_USER.equals(defSrc)) {
							condition.put("defUser", user.getUserId());
						}
					}
					List<RptIdxInfo> idxList = this.idxDao
							.listIdxInfo(condition);
					List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
					if (!StringUtils.isEmpty(isShowDim)  //显示维度
							&& isShowDim.equals("1")) {
						condition.clear();
						if(idxList != null && idxList.size() > 0){
							condition.put("idxList", idxList);
							dimRelList = this.getDimNosOfIndex(condition);
						}
					}
					for (RptIdxInfo tmp : idxList) {
						CommonTreeNode idxNode = new CommonTreeNode();
						idxNode.setId(tmp.getId().getIndexNo());
						idxNode.setText(tmp.getIndexNm());
						idxNode.setData(tmp);
						idxNode.setUpId(tmp.getId().getIndexNo());
						idxNode.setIcon(contextPath
								+ "/images/classics/menuicons/grid.png");
						idxNode.getParams().put("nodeType", "idxInfo");
						idxNode.getParams().put("indexNo",
								tmp.getId().getIndexNo());
						idxNode.getParams().put("indexVerId",
								String.valueOf((tmp.getId().getIndexVerId())));
						idxNode.getParams().put("isSum", tmp.getIsSum());
						if (StringUtils.isNotEmpty(isShowDim)   //显示维度
								&& isShowDim.equals("1")) {
							for (RptIdxDimRel rel : dimRelList) {
								if (rel.getId().getIndexNo()
										.equals(tmp.getId().getIndexNo())
										&& rel.getId().getIndexVerId() == tmp
												.getId().getIndexVerId()) {
									idxNode.getParams().put("dimNos",
											rel.getId().getDimNo());
								}
							}
						}
						if (tmp.getIndexType()
								.equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)) {
//							if (!(isShowMeasure != null && isShowMeasure.equals("0"))) {
//								idxNode.getParams().put("haveMeasure", "true");
//								idxNode.setIsParent(true);
//							}
						} else {
							idxNode.setIsParent(false);
						}
						resultList.add(idxNode);
					}
				}
			}
		}else if (nodeType.equals(nodeTypeIdx)   //节点类型为指标
				&& StringUtils.isNotEmpty(indexVerId)) {
			condition.clear();
			RptIdxMeasureRelPK pk = new RptIdxMeasureRelPK();
			pk.setIndexNo(upId);
			pk.setIndexVerId(new Long(indexVerId));
			condition.put("id", pk);
			List<RptIdxMeasureInfoVO> measureList = measureDao.list(condition);
			for (RptIdxMeasureInfoVO measure : measureList) {  //度量信息
				CommonTreeNode node = new CommonTreeNode();
				node.setUpId(measure.getMeasureNo());
				node.setId(measure.getMeasureNo());
				node.setText(measure.getMeasureNm());
				node.setIcon(contextPath + "/images/classics/menuicons/computer.png");
				node.getParams().put("nodeType", "measureInfo");
				node.getParams().put("indexNo", upId);
				node.getParams().put("indexVerId", indexVerId);
				resultList.add(node);
			}
		}
		return resultList;
	}

	// 重载方法getAsyncTree()
	public List<CommonTreeNode> getAsyncTree(String basePath, String searchNm,
			String isShowIdx, /*String isShowMeasureInfo,*/ String idxNos,
			String isShowDim, String indexNo, String isEngine,
			String isPublish, /*String exSumAccoutIndex,*/ String isAuth,
			String isPreview, String defSrc) {
		Map<String, Object> condition = new HashMap<String, Object>();

		Map<String, Object> catalogMap = new HashMap<String, Object>();
		condition.put("needOrderByNm", "needOrderByNm");
		if (StringUtils.isNotEmpty(defSrc)) {
			BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
			condition.put("defSrc", defSrc);
			if (GlobalConstants4plugin.INDEX_DEF_SRC_ORG.equals(defSrc)) {
				condition.put("defOrg", user.getOrgNo());
			} else if (GlobalConstants4plugin.INDEX_DEF_SRC_USER.equals(defSrc)) {
				condition.put("defUser", user.getUserId());
			}
		}
		List<RptIdxCatalog> catalogList = this.catalogDao.listIdxCatalog(condition);
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();

		List<RptIdxInfo> idxList = new ArrayList<RptIdxInfo>();

		if (StringUtils.isNotEmpty(isShowIdx) && isShowIdx.equals("1")) {
			if (StringUtils.isNotEmpty(isPublish) && isPublish.equals("1")) {
				condition.put("isPublish", GlobalConstants4plugin.IS_PUBLISH_TRUE);
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
				isEngineList.add(GlobalConstants4plugin.COMPOSITE_INDEX);
				isEngineList.add(GlobalConstants4plugin.DERIVE_INDEX);
				isEngineList.add(GlobalConstants4plugin.ADD_RECORD_INDEX);
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
//			if (!StringUtils.isEmpty(exSumAccoutIndex)
//					&& exSumAccoutIndex.equals("1")) {
//				condition
//						.put("exSumAccoutIndex",
//								com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
//			}
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
					&& isAuth != null && !isAuth.equals("")
					&& StringUtils.isEmpty(defSrc)) {
				List<String> list = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_IDX");
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
			catalog.getParams().put("nodeType", nodeTypeFolder);
			
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
					tmp.getParams().put("indexVerId",
							String.valueOf((idxList.get(j).getId()
									.getIndexVerId())));
					tmp.getParams().put("isSum", idxList.get(j).getIsSum());
					// 给指标增加其维度信息
					if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
						for (RptIdxDimRel rel : dimRelList) {
							if (rel.getId().getIndexNo()
									.equals(idxList.get(j).getId().getIndexNo())
									&& rel.getId().getIndexVerId() == idxList
											.get(j).getId().getIndexVerId()) {
								tmp.getParams().put("dimNos",
										rel.getId().getDimNo());
							}
						}
					}
					// 给总账指标增加度量信息作为子节点
//					if (!StringUtils.isEmpty(isShowMeasureInfo)
//							&& isShowMeasureInfo.equals("1")
//							&& idxList.get(j).getIndexType() != null
//							&& idxList.get(j).getIndexType()
//									.equals(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX)) {
//						tmp.getParams().put("haveMeasure", "true");
//						tmp.setIsParent(true);
//					}
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
						|| StringUtils.isNotEmpty(isEngine) && isEngine.equals("1") 
						|| StringUtils.isNotEmpty(isPreview))) {
			List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
			for (CommonTreeNode tmp : resultList) {
				if (tmp.getChildren() != null && tmp.getChildren().size() > 0
						&& !list.contains(tmp)) {
					list.add(tmp);
					searchParent(list, catalogMap, tmp);
				}
			}
			resultList = list;
		}
		return resultList;
	}
	
	public List<RptIdxDimRel> getDimNosOfIndex(Map<String, Object> map) {
		List<RptIdxDimRel> list = this.relDao.getAllDimRel(map);
		List<RptIdxDimRel> resultList = new ArrayList<RptIdxDimRel>();
		for (RptIdxDimRel x : list) {
			boolean flag = false;
			for (RptIdxDimRel y : resultList) {
				if (x.getId().getIndexNo().equals(y.getId().getIndexNo())
						&& x.getId().getIndexVerId() == y.getId().getIndexVerId()) {
					//对维度进行去重
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
	
	private void searchParent(List<CommonTreeNode> list,
			Map<String, Object> catalogMap, CommonTreeNode node) {
		CommonTreeNode treeNode = (CommonTreeNode) catalogMap.get(node.getUpId());
		boolean flag = list.contains(treeNode);
		if (treeNode != null && !flag) {
			treeNode.getParams().put("hasInfos", "true");  // contains会忽略params属性
			list.add(treeNode);
			searchParent(list, catalogMap, treeNode);
		}
	}

	//不涉及权限进行展示
	public List<CommonTreeNode> getTree(String basePath, String nodeType, String upId) {
		
		if (StringUtils.isEmpty(nodeType)) {
			nodeType = nodeTypeFolder;
		}
		
		if(StringUtils.isEmpty(upId)){
			upId = "0";
		}
		
		String jql = "select catalog from RptIdxCatalog catalog where catalog.upNo = ?0 order by catalog.indexCatalogNm";
		List<RptIdxCatalog> catalogList = this.baseDAO.findWithIndexParam(jql, upId);

		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		for(RptIdxCatalog catalog : catalogList){
			CommonTreeNode cataNode = new CommonTreeNode();
			cataNode.setId(catalog.getIndexCatalogNo());
			cataNode.setUpId(catalog.getUpNo());
			cataNode.setText(catalog.getIndexCatalogNm());
			cataNode.setData(catalog);
			cataNode.setIsParent(true);
			cataNode.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
			cataNode.getParams().put("nodeType", nodeTypeFolder);
			resultList.add(cataNode);
		}

		if(catalogList.size() == 0){
			jql = "select info from RptIdxInfo info where info.indexCatalogNo = ?0 order by info.indexNm";
			List<RptIdxInfo> infoList = this.baseDAO.findWithIndexParam(jql, upId);
			
			for(RptIdxInfo info : infoList){
				CommonTreeNode infoNode = new CommonTreeNode();
				infoNode.setId(info.getId().getIndexNo());
				infoNode.setUpId(info.getIndexCatalogNo());
				infoNode.setText(info.getIndexNm());
				infoNode.setData(info);
				infoNode.setIcon(basePath + "/images/classics/menuicons/grid.png");
				infoNode.getParams().put("nodeType", nodeTypeIdx);
				infoNode.getParams().put("indexNo",info.getId().getIndexNo());
				infoNode.getParams().put("indexVerId",
								String.valueOf(info.getId().getIndexVerId()));
				resultList.add(infoNode);
			}
		}
		
		return resultList;	
	}

	/**
	 * 获取指标类型下拉框列表
	 * 
	 * @param situation
	 * @param defSrc
	 * @return
	 */
	public List<CommonComboBoxNode> getIndexTypeList(String situation, String defSrc) {
		List<CommonComboBoxNode> listNodes = new ArrayList<CommonComboBoxNode>();
		CommonComboBoxNode node = null;
		if(situation == null){
			node = new CommonComboBoxNode();
			node.setId("");
			node.setText("全部");
			listNodes.add(node);
		}
		
		if (StringUtils.isEmpty(defSrc)) {
			node = new CommonComboBoxNode();
			node.setId(GlobalConstants4plugin.ROOT_INDEX);
			node.setText("根指标");
			listNodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(GlobalConstants4plugin.GENERIC_INDEX);
			node.setText("泛化指标");
			listNodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			node.setText("总账指标");
			listNodes.add(node);
			node = new CommonComboBoxNode();
			node.setId(GlobalConstants4plugin.ADD_RECORD_INDEX);
			node.setText("补录指标");
			listNodes.add(node);
		}
		
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.COMPOSITE_INDEX);
		node.setText("组合指标");
		listNodes.add(node);
		node = new CommonComboBoxNode();
		node.setId(GlobalConstants4plugin.DERIVE_INDEX);
		node.setText("派生指标");
		listNodes.add(node);
		
		return listNodes;
	}

	/**
	 * 查找具体指标的界限
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @param orgNo
	 * @return 
	 */
	public RptIdxPlanResultVO getIdxPlanvalInfoByParams(String indexNo, String dataDate,String orgNo,String currency,String indexVal) {
//		long tmpIndexVerId = Integer.parseInt(indexVerId);
		String sql;
		String jql;
		RptIdxPlanResultVO planvalInfoVO = new RptIdxPlanResultVO();//构造一个新的VO  
		if(indexNo.contains(".")){
			if(currency.equals("-")){
				sql = " select distinct t.index_no,t.data_date,t.org_no,t1.org_nm,t.currency,t.index_val "
						+ " from RPT_IDX_PLAN_RESULT t "
						+ " left join Rpt_Org_Info t1 on t.org_no = t1.org_no "
						+ " where t.index_no = ?0 "
						+ " and t.data_Date = ?1 "
						+ " and t.org_No = ?2 "
						+ " and t.currency = ?3 ";
			}else{
				sql = " select distinct t.index_no,t.data_date,t.org_no,t1.org_nm,t2.dim_item_nm,t.index_val "
						+ " from RPT_IDX_PLAN_RESULT t "
						+ " left join Rpt_Org_Info t1 on t.org_no = t1.org_no "
						+ " left join Rpt_Dim_Item_Info t2 on t.currency = t2.dim_item_no"
						+ " where t.index_no = ?0 "
						+ " and t.data_Date = ?1 "
						+ " and t.org_No = ?2 "
						+ " and t.currency = ?3 ";
			}
			
			 List<Object[]> planvalInfos = this.baseDAO.findByNativeSQLWithIndexParam(sql, indexNo, 
					dataDate,orgNo,currency);
			 Map<String, String> planvalMap = new HashMap<String,String>();
			 if(planvalInfos.size()>0){
				 for(Object[] planval : planvalInfos){
					 planvalMap.put("indexNo", planval[0].toString());
					 planvalMap.put("dataDate", planval[1].toString());
					 planvalMap.put("orgNo", planval[2].toString());
					 planvalMap.put("orgNm", planval[3].toString());
					 planvalMap.put("currency", planval[4].toString());
					 planvalMap.put("indexVal", planval[5] != null ? planval[5].toString() : null);
				 }
			 }
			 indexNo = indexNo + ".";
			 String compNo[] = StringUtils.split(indexNo, ".");
			 String sql1 = " select t1.index_nm,t2.measure_nm from RPT_IDX_INFO t1,RPT_IDX_MEASURE_INFO t2 "
			 		+ " where t1.index_no = ?0 and t2.measure_no = ?1 ";
			 List<Object[]> nameList = this.baseDAO.findByNativeSQLWithIndexParam(sql1, compNo[0],compNo[1]);
			 if(nameList.size()>0){
				 for(Object[] planval : nameList){
					 planvalMap.put("indexNm", planval[0].toString()+"."+planval[1].toString());
				 }
			 }
			 planvalInfoVO.setIndexNo(planvalMap.get("indexNo"));
			 planvalInfoVO.setDataDate(planvalMap.get("dataDate"));
	         //planvalInfoVO.setIndexCatalogNo(planvalInfos.get(0).getIndexCatalogNo());
	         planvalInfoVO.setIndexNm(planvalMap.get("indexNm"));
	         planvalInfoVO.setIndexType("Y");
	         planvalInfoVO.setOrgNo(planvalMap.get("orgNo"));
	         planvalInfoVO.setOrgNm(planvalMap.get("orgNm"));
	         planvalInfoVO.setCurrency(planvalMap.get("currency"));
	         planvalInfoVO.setIndexVal(planvalMap.get("indexVal") != null ? new BigDecimal(planvalMap.get("indexVal")) : null);
			
		}else{
			if(currency.equals("-")){
				jql = "select new com.yusys.bione.plugin.idxplan.web.vo.RptIdxPlanResultVO(info, planval,org) "
						+ "from  RptIdxInfo info, RptIdxPlanvalResult planval,RptOrgInfo org "
						+ "where planval.id.indexNo = info.id.indexNo "
						+ "and planval.id.orgNo = org.id.orgNo "
						+ "and planval.id.indexNo = ?0 "
						+ "and planval.id.dataDate = ?1 "
						+ "and planval.id.orgNo = ?2 "
						+ "and planval.id.currency = ?3 ";
			}else{
				jql = "select new com.yusys.bione.plugin.idxplan.web.vo.RptIdxPlanResultVO(info, planval,dim, org) "
					+ "from  RptIdxInfo info, RptIdxPlanvalResult planval,RptDimItemInfo dim,RptOrgInfo org "
					+ "where planval.id.indexNo = info.id.indexNo "
					+ "and planval.id.orgNo = org.id.orgNo "
					+ "and planval.id.currency = dim.id.dimItemNo "
					+ "and planval.id.indexNo = ?0 "
					+ "and planval.id.dataDate = ?1 "
					+ "and planval.id.orgNo = ?2 "
					+ "and planval.id.currency = ?3 ";
				}
			List<RptIdxPlanResultVO>  planvalInfos =  this.baseDAO.findWithIndexParam(jql, indexNo, 
					dataDate,orgNo,currency);
			if(planvalInfos.size()>0){
				planvalInfoVO.setIndexNo(planvalInfos.get(0).getIndexNo());
				planvalInfoVO.setDataDate(planvalInfos.get(0).getDataDate());
			  //planvalInfoVO.setIndexCatalogNo(planvalInfos.get(0).getIndexCatalogNo());
				planvalInfoVO.setIndexNm(planvalInfos.get(0).getIndexNm());
				planvalInfoVO.setIndexType(planvalInfos.get(0).getIndexType());
				planvalInfoVO.setOrgNo(planvalInfos.get(0).getOrgNo());
				planvalInfoVO.setOrgNm(planvalInfos.get(0).getOrgNm());
				planvalInfoVO.setCurrency(planvalInfos.get(0).getCurrency());
				planvalInfoVO.setIndexVal(planvalInfos.get(0).getIndexVal() != null ? planvalInfos.get(0).getIndexVal() : null );
			}
		}
		/*String tmpOrgNm = "";
		String tmpOrgNo = "";
		for(int i=0; i<planvalInfos.size(); i++){
			RptIdxPlanResultVO planvalInfo = planvalInfos.get(i);
			if(i == planvalInfos.size()-1){
				tmpOrgNo += planvalInfo.getOrgNo();
				tmpOrgNm += planvalInfo.getOrgNm();
			}else{
				tmpOrgNo += planvalInfo.getOrgNo() + ",";
				tmpOrgNm += planvalInfo.getOrgNm() + ",";
			}
		}*/
		
		return planvalInfoVO;
	}

	/**
	 * 根据条件查找指标界限
	 * 
	 * @param pageFirstIndex
	 * @param pageSize
	 * @param sortName
	 * @param sortOrder
	 * @param indexCatalogNo
	 * @param indexNo
	 * @param indexVerId
	 * @param indexNm
	 * @param indexType
	 * @param defSrc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<RptIdxPlanResultVO> getPlanvalInfos(int pageFirstIndex,
			int pageSize, String sortName, String sortOrder, 
			Map<String, Object> conditionMap, String indexCatalogNo, 
			String indexNo, String defSrc) {
		SearchResult<Object[]> tmpResultList = null;
		SearchResult<RptIdxPlanResultVO> resultList = new SearchResult<RptIdxPlanResultVO>();
		String jql = "";
		/*long tmpIndexVerId = 0;
		if(StringUtils.isNotEmpty(indexVerId)){
			tmpIndexVerId = Integer.parseInt(indexVerId);
		}*/
		
		if(StringUtils.isEmpty(indexCatalogNo)){
			jql = "select * from (select planval.index_no,"
					+ " (case when instr(planval.index_no,'.')>0 then(info.index_nm||'.'||measure.measure_nm) else info.index_nm end) as index_nm,"
					+ " planval.org_no,org.org_Nm,planval.data_date,planval.currency as currencyId,dim.dim_item_nm as currency,planval.index_val " 
					+ " from Rpt_Idx_Plan_Result planval "
					+ " left join Rpt_Idx_Info info on (case when instr(planval.index_no,'.')>0 then substr(planval.index_no,0,instr(planval.index_no,'.')-1) else planval.index_no end)=info.index_no "
					+ " left join Rpt_Org_Info org on org.org_no=planval.org_no "
					+ " left join RPT_DIM_ITEM_INFO dim on dim.dim_item_no = planval.currency and dim.dim_type_no = 'CURRENCY' "
					+ " left join RPT_IDX_MEASURE_INFO measure on (case when instr(planval.index_no,'.')>0 then substr(planval.index_no,instr(planval.index_no,'.')+1,length(planval.index_no)) else planval.index_no end)= measure.measure_no"
					+ " where 1=1 "; 
		}else{
			if(indexNo.contains(".")){
				jql = "select * from (select planval.index_no,"
						+ " (case when instr(planval.index_no,'.')>0 then(info.index_nm||'.'||measure.measure_nm) else info.index_nm end) as index_nm,"
						+ " planval.org_no,org.org_Nm,planval.data_date,planval.currency as currencyId,dim.dim_item_nm as currency,planval.index_val " 
						+ " from Rpt_Idx_Plan_Result planval "
						+ " left join Rpt_Idx_Info info on (case when instr(planval.index_no,'.')>0 then substr(planval.index_no,0,instr(planval.index_no,'.')-1) else planval.index_no end)=info.index_no "
						+ " left join Rpt_Org_Info org on org.org_no=planval.org_no "
						+ " left join RPT_DIM_ITEM_INFO dim on dim.dim_item_no = planval.currency and dim.dim_type_no = 'CURRENCY'"
						+ " left join RPT_IDX_MEASURE_INFO measure on (case when instr(planval.index_no,'.')>0 then substr(planval.index_no,instr(planval.index_no,'.')+1,length(planval.index_no)) else planval.index_no end)= measure.measure_no"
						+ " where planval.index_No = '" + indexNo + "' ";
			}else{
				jql = "select * from (select planval.index_no,"
						+ " (case when instr(planval.index_no,'.')>0 then(info.index_nm||'.'||measure.measure_nm) else info.index_nm end) as index_nm,"
						+ " planval.org_no,org.org_Nm,planval.data_date,planval.currency as currencyId,dim.dim_item_nm as currency,planval.index_val " 
						+ " from Rpt_Idx_Plan_Result planval "
						+ " left join Rpt_Idx_Info info on (case when instr(planval.index_no,'.')>0 then substr(planval.index_no,0,instr(planval.index_no,'.')-1) else planval.index_no end)=info.index_no "
						+ " left join Rpt_Org_Info org on org.org_no=planval.org_no "
						+ " left join RPT_DIM_ITEM_INFO dim on dim.dim_item_no = planval.currency and dim.dim_type_no = 'CURRENCY'"
						+ " left join RPT_IDX_MEASURE_INFO measure on (case when instr(planval.index_no,'.')>0 then substr(planval.index_no,instr(planval.index_no,'.')+1,length(planval.index_no)) else planval.index_no end)= measure.measure_no"
						+ " where planval.index_No = '" + indexNo + "' or planval.index_No like '" + indexNo + ".%' ";
			}
			
		}
		
		//搜索查询
		if (!conditionMap.get("jql").equals("")) {
			String tmpjql = (String)conditionMap.get("jql");
			Map<String, Object> tmpParam = (Map<String, Object>) conditionMap.get("params");
			
			String sj[] = StringUtils.split(tmpjql, "and");  //通过and标识截断语句
			if(sj.length == 3){   //指标名称及类型均有参数
				String sp0 = (String)tmpParam.get("param0");
				String sp1 = (String)tmpParam.get("param1");
				String sp2 = (String)tmpParam.get("param2");
				jql += "and (info.index_Nm like '" + sp0 + "') or (measure.measure_nm like '" + sp0 + "') ";
				jql += "and org.org_Nm like '" + sp1 + "' ";
				jql += "and planval.data_date = '" + sp2 + "' ";
			}else if(sj.length == 2){ 
				String sp0 = (String)tmpParam.get("param0");
				String sp1 = (String)tmpParam.get("param1");
				if(sj[0].trim().equals("indexNm like:param0")){
					jql += "and (info.index_Nm like '" + sp0 + "') or (measure.measure_nm like '" + sp0 + "') ";
				}
				if(sj[0].trim().equals("orgNm like:param0")){
					jql += "and org.org_Nm like '" + sp0 + "' ";
				}
				if(sj[0].trim().equals("dataDate =:param0")){
					jql += "and planval.data_date = '" + sp0 + "' ";
				}
				if(sj[1].trim().equals("indexNm like:param1")){
					jql += "and (info.index_Nm like '" + sp1 + "') or (measure.measure_nm like '" + sp1 + "') ";
				}
				if(sj[1].trim().equals("orgNm like:param1")){
					jql += "and org.org_Nm like '" + sp1 + "' ";
				}
				if(sj[1].trim().equals("dataDate =:param1")){
					jql += "and planval.data_date = '" + sp1 + "' ";
				}	
			}else{   //指标名称与类型只有一个有参数
				String sp0 = (String)tmpParam.get("param0");
				if(sj[0].trim().equals("indexNm like:param0")){
					jql += "and (info.index_Nm like '" + sp0 + "') or (measure.measure_nm like '" + sp0 + "') ";
				}else if(sj[0].trim().equals("orgNm like:param0")){
					jql += "and org.org_Nm like '" + sp0 + "' ";
				}else if(sj[0].trim().equals("dataDate =:param0")){
					jql += "and planval.data_date = '" + sp0 + "' ";
				}
			}
		}
		jql +=" )group by  index_no,index_nm,org_no,org_Nm,data_date,currencyId,currency,index_val ";
		//排序
		if(StringUtils.isNotEmpty(sortName)){
			//jql += "order by info." + sortName + " " + sortOrder;
			if(sortName.equals("indexNm")){
				jql += "order by index_Nm " + sortOrder;
			}
		}
	
//		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");

		tmpResultList = this.baseDAO.findPageWithNameParamByNativeSQL(pageFirstIndex, pageSize, 
				jql, null);
//		resultList = this.baseDAO.findPageWithNameParam(pageFirstIndex, pageSize, 
//				jql, values);
		
		//由于采用native语言查询，因而需要将取得的值传到VO中
		List<RptIdxPlanResultVO> planvalInfoList = new ArrayList<RptIdxPlanResultVO>();
		for(int i=0; i<tmpResultList.getResult().size(); i++){
			RptIdxPlanResultVO planvalInfoVO = new RptIdxPlanResultVO();
			Object[] obj = tmpResultList.getResult().get(i);
			planvalInfoVO.setIndexNo(String.valueOf(obj[0]));
			planvalInfoVO.setIndexNm(String.valueOf(obj[1]));
			planvalInfoVO.setOrgNo(String.valueOf(obj[2]));
			planvalInfoVO.setOrgNm(String.valueOf(obj[3]));
			planvalInfoVO.setDataDate(String.valueOf(obj[4]).substring(0, 4));
			planvalInfoVO.setCurrencyId(String.valueOf(obj[5]));
			planvalInfoVO.setCurrency(String.valueOf(obj[6]));
			planvalInfoVO.setIndexVal((obj[7] != null) ? new BigDecimal(String.valueOf(obj[7])) : null);
			
			planvalInfoList.add(planvalInfoVO);
		}
		
		resultList.setResult(planvalInfoList);
		resultList.setTotalCount(tmpResultList.getTotalCount());
				
		return resultList;
	}

	/**
	 * 构造机构树
	 * 
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> listOrgTree(String indexNo) {
	/*	Map<String, Object> srcSysMap = new HashMap<String, Object>();
		List<String> srcList = new ArrayList<String>();*/
	/*	String srcType = "";
		String sql = " select t.IDX_BELONG_TYPE from RPT_IDX_INFO t where t.INDEX_NO =?0";
		List<Object[]> dataType = this.baseDAO.findByNativeSQLWithIndexParam(sql, indexNo);
		if(dataType.size()>0){
			for(Object src : dataType){
				srcType = src.toString();
			}
		}
		if("01".equals(srcType)){
			srcList.add("01");
		}
		if("02".equals(srcType)){  //"01"-公共，"02"-管驾，"03"-CRM
			srcList.add("01");
			srcList.add("02");
		}
		if("03".equals(srcType)){  
			srcList.add("01");
			srcList.add("03");
		}
		srcSysMap.put("srcSysCode", srcList);*/
		
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();

		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId("0");
		baseNode.setText("全部");
		baseNode.setUpId("0");
		RptOrgInfo orgBase = new RptOrgInfo();
		orgBase.setMgrOrgNo("0");;
		orgBase.setUpOrgNo("0");
		baseNode.setData(orgBase);
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		

		Map<String, Object> orgNmMap = new HashMap<String, Object>();
		List<Object[]> orgList = getOrgNoList();//查询所有机构，用于机构编号转机构名称，返回到页面
		if(null != orgList && orgList.size() > 0){//把机构放入到map里，便于判断
			for(Object[] org : orgList){
				orgNmMap.put(org[0].toString(), org[1].toString());
			}
		}
		
		//获取所有的机构
		String jql = " select org from RptOrgInfo org ";
		List<RptOrgInfo> orgs = this.baseDAO.findWithNameParm(jql, null);
		for(RptOrgInfo org : orgs){
			if((orgNmMap.get(org.getUpOrgNo())!=null && !"".equals(orgNmMap.get(org.getUpOrgNo())))|| ("0".equals(org.getUpOrgNo()))){
				CommonTreeNode treeChildNode = new CommonTreeNode();
				treeChildNode.setId(org.getId().getOrgNo());
				treeChildNode.setText(org.getOrgNm());
				treeChildNode.setUpId(org.getUpOrgNo());
				treeChildNode.setData(org);
				//treeChildNode.getParams().put("type", "idxCatalog");
				treeChildNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ORG_ICON);
				treeNodes.add(treeChildNode);
			}
		}
		return treeNodes;
	}

	/**
	 * 构造维度树
	 * 
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> listDimTree(String indexNo) {
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		
		String idxNo = indexNo;
		
		if(idxNo.contains(".")){
			idxNo = idxNo + ".";
			String compNo[] = StringUtils.split(idxNo, ".");
			idxNo = compNo[0];
		}

		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId("0");
		baseNode.setText("全部");
		baseNode.setUpId("0");
		RptIdxDimTypeRelVO dimBase = new RptIdxDimTypeRelVO();
		baseNode.setData(dimBase);
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		
		Map<String, Object> dimMap = new HashMap<String, Object>();
		dimMap.put("indexNo", idxNo);
		dimMap.put("dimType", "04");
		
		//获取指标的所有维度
		String sql = " select distinct rel.dim_No,dim.dim_Type_Nm,rel.dim_Type from Rpt_Idx_Dim_Rel rel"
				+ " left join Rpt_Dim_Type_Info dim on dim.dim_Type_No = rel.dim_No"
				+ " where rel.index_No = :indexNo and rel.dim_Type <> :dimType order by rel.dim_type desc ";
		List<Object[]> dimList = this.baseDAO.findByNativeSQLWithNameParam(sql, dimMap);
		
		if(null != dimList && dimList.size() > 0){//把机构放入到map里，便于判断
			for(Object[] dim : dimList){
				RptIdxDimTypeRelVO dimTypeRel= new RptIdxDimTypeRelVO();
				dimTypeRel.setDimNo(dim[0] != null?dim[0].toString():""); 
				dimTypeRel.setDimTypeNm(dim[1] != null?dim[1].toString():""); 
				dimTypeRel.setDimType(dim[2] != null?dim[2].toString():"");
				if("01".equals(dim[2].toString())||"02".equals(dim[2].toString())){
					dimTypeRel.setJudge("1");
				}else{
					dimTypeRel.setJudge(isChecked(indexNo,dim[0].toString()));
				}
	
				CommonTreeNode treeChildNode = new CommonTreeNode();
				treeChildNode.setId(dim[0].toString());
				treeChildNode.setText(dim[1].toString());
				treeChildNode.setUpId("0");
				treeChildNode.setData(dimTypeRel);
				treeChildNode.getParams().put("type", "dimInfo");
				treeChildNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ORG_ICON);
				treeNodes.add(treeChildNode);
			}
		}
		return treeNodes;
	}

	public String  isChecked(String indexNo,String dimNo){
		String isCheck = "0";
		String sql = " select t.CHECK_ID from RPT_VALID_DIM_REL t where t.CHECK_ID =?0 and t.DIM_NO = ?1";
		List<Object[]> checkList = this.baseDAO.findByNativeSQLWithIndexParam(sql, indexNo,dimNo);
		if(checkList.size()>0){
			isCheck = "1";
		}
		return isCheck;
	}  
	
	/**
	 * 更改计划值
	 * 
	 * @param indexNo
	 * @param orgNo
	 */
	@Transactional(readOnly = false)
	public void changePlanval(String indexNo, String dataDate,String orgNo,String currency,BigDecimal indexVal) {
//		long tmpIndexVerId = Integer.parseInt(indexVerId);
		if (StringUtils.isNotEmpty(indexNo) && StringUtils.isNotEmpty(orgNo)) {
			String jql = "update RptIdxPlanvalResult planval set planval.indexVal = ?0 "
					+ " where planval.id.indexNo = ?1 "
					+ " and planval.id.dataDate = ?2"
					+ " and planval.id.orgNo = ?3"
					+ " and planval.id.currency = ?4";
			this.baseDAO.batchExecuteWithIndexParam(jql, indexVal, indexNo, dataDate, orgNo, currency);
		}
	}
	

	/**
	 * 关联维度保存
	 * 
	 * @param ids
	 * 
	 */
	@Transactional(readOnly = false)
	public void saveDimInfo(String indexNo,String ids,String dimTypes) {
		if(StringUtils.isNotEmpty(indexNo) && StringUtils.isNotEmpty(ids) && StringUtils.isNotEmpty(dimTypes)){
			String jql = " delete from RptValidDimRel rel where rel.id.checkId = ?0 ";
			this.baseDAO.batchExecuteWithIndexParam(jql,indexNo);
			String[] id = StringUtils.split(ids, ',');
		    String[] dimType = StringUtils.split(dimTypes, ',');
		    for(int i = 0,j = 1;i<id.length;i++){
		    	RptValidDimRel dimRel = new RptValidDimRel();
		    	RptValidDimRelPK pk = new RptValidDimRelPK();
		    	dimRel.setId(pk);
		    	pk.setCheckId(indexNo);
		    	pk.setValidType("05");
		    	pk.setDimNo(id[i]);
		    	dimRel.setDimType(dimType[i]);
		    	if("01".equals(dimType[i])){
		    		dimRel.setStoreCol("DATA_DATE");
		    	}else if("02".equals(dimType[i])){
		    		dimRel.setStoreCol("ORG_NO");
		    	}else if("03".equals(dimType[i])){
		    		dimRel.setStoreCol("CURRENCY");
		    	}else{
		    		dimRel.setStoreCol("DIM"+j);
		    		j++;
		    	}
		        this.groupBS.saveEntity(dimRel);
		       } 
		}
	}
	
	/**
	 * 删除指定对象
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @param orgNo
	 */
	@Transactional(readOnly = false)
	public void idxPlanvalInfoDelete(String indexNo, String dataDate, String orgNo,String currency,String indexVal) {
//		long tmpIndexVerId = Integer.parseInt(indexVerId);
		String jql = "delete from RptIdxPlanvalResult planval where planval.id.indexNo = '" + indexNo + "' "
				+ " and planval.id.dataDate = '" + dataDate+"' "
				+ " and planval.id.orgNo =  '"+ orgNo+"' "
				+ " and planval.id.currency = '" + currency+"' "
				+ " and planval.indexVal = '" + indexVal+ "'";
		this.baseDAO.batchExecuteWithNameParam(jql, null);
	}

	/**
	 *  删除一组对象
	 *  
	 * @param indexNoArray
	 * @param indexVerIdArray
	 * @param orgNosArray
	 */
	@Transactional(readOnly = false)
	public void deletePlanvalssByIds(String[] indexNos, String[] dataDates,String[] orgNos, String[] currencys, String[] indexVals) {
		if (indexNos != null) {
			String indexNo = "";
			String dataDate = "";
			String orgNo="";
			String currency = "";
			String indexVal = "";
			for (int i = 0; i < indexNos.length; i++) {
				indexNo = indexNos[i];
				dataDate = dataDates[i];
				orgNo=orgNos[i];
				currency = currencys[i];
				indexVal = indexVals[i];
				this.idxPlanvalInfoDelete(indexNo, dataDate, orgNo,currency,indexVal);
				String sql = " select t.index_No from RPT_IDX_PLAN_RESULT t where t.index_no = ?0 ";
				List<Object[]> idxList = this.baseDAO.findByNativeSQLWithIndexParam(sql, indexNo);
				if(idxList.size() <= 0){
					String jql = " delete from RptValidDimRel rel where rel.id.checkId = ?0 ";
					this.baseDAO.batchExecuteWithIndexParam(jql,indexNo); 
				}
			}
		}
	}

	/**
	 * 检测机构是否存在
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @param orgNo
	 * @return
	 */
	public boolean chechIsOrgNo(String indexNo, String indexVerId, String orgNos) {
		boolean flag = true;
		long tmpIndexVerId = 0;
		if(StringUtils.isNotEmpty(indexVerId)){
			tmpIndexVerId = Integer.parseInt(indexVerId);
		}
		if(StringUtils.isNotBlank(orgNos)){
			List<String> tmpOrgNosList = ArrayUtils.asList(orgNos, ",");
			String jql = "select planval from RptIdxPlanvalInfo planval where planval.id.indexNo = ?0 "
					+ "and planval.id.indexVerId = ?1 and planval.id.orgNo in (?2)";
			List<RptIdxPlanvalInfo> planvalInfos = this.baseDAO.findWithIndexParam(jql, indexNo, tmpIndexVerId,tmpOrgNosList);
			if(planvalInfos != null && planvalInfos.size()>0 ){
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 检测指标对应币种维度是否存在
	 * 
	 * @param indexNo
	 * @return
	 */
	public boolean checkIsDimData(String indexNo) {
		boolean flag = true;
		if(indexNo.contains(".")){
			indexNo = indexNo + ".";
			String compNo[] = StringUtils.split(indexNo, ".");
			indexNo = compNo[0];
		}
		if(StringUtils.isNotBlank(indexNo)){
			String jql = "select dim from RptIdxDimRel dim where dim.id.indexNo = ?0 "
					+ "and dim.id.dimNo = ?1 ";
			List<RptIdxDimRel> dimInfos = this.baseDAO.findWithIndexParam(jql, indexNo,"CURRENCY");
			if(dimInfos != null && dimInfos.size()>0 ){
				flag = false;
			}
		}
		return flag;
	}
	
	/**
	 * 删除原有记录
	 * 
	 * @param oldPlanVal
	 * 
	 */
	public void delEntity(String oldPlanVal, RptIdxPlanvalInfoVO model) {
		String jql = "delete RptIdxPlanvalInfo planval where planval.id.indexNo = ?0 " +
				"and planval.id.indexVerId = ?1 and planval.planVal = ?2 ";

		this.baseDAO.batchExecuteWithIndexParam(jql, model.getIndexNo(), 
				new Long(model.getIndexVerId()), new BigDecimal(oldPlanVal));
	}
	
	/**
	 * 
	 * 添加新纪录
	 * 
	 * @param model
	 */
	public void addEntity(RptIdxPlanResultVO model){
		RptIdxPlanvalResultPK id = new RptIdxPlanvalResultPK();
		RptIdxPlanvalResult planvalInfo = new RptIdxPlanvalResult();
		String[] orgNos = StringUtils.split(model.getOrgNo(), ',');
		String orgNo;
		
		for(int i=0; i<orgNos.length; i++){
			orgNo = orgNos[i];
			id.setIndexNo(model.getIndexNo());	
			id.setDataDate(model.getDataDate());
			id.setCurrency(model.getCurrency());
			id.setPlanType("01");
			id.setDim1("-");
			id.setDim2("-");
			id.setDim3("-");
			id.setDim4("-");
			id.setDim5("-");
			id.setDim6("-");
			id.setDim7("-");
			id.setDim8("-");
			id.setDim9("-");
			id.setDim10("-");
			id.setOrgNo(orgNo);
			planvalInfo.setId(id);
			planvalInfo.setIndexVal(model.getIndexVal());	
			
			this.saveEntity(planvalInfo);
		}
	}

	/**
	 * 查询所选择指标对应的计划值数据
	 *  @return 指标号实体 
	 */
	public List<IdxPlanvalImportVO> getAllPlanvalList(String idx) {
		String index =idx+".%";
		String sql =" select t.index_No,t.org_No,t.data_Date,t.currency,t.index_Val "
				+ " from Rpt_Idx_Plan_Result t "
				+ " where t.index_No=?0 or t.index_No like ?1 ";
		List<Object[]> planInfo = this.baseDAO.findByNativeSQLWithIndexParam(sql, idx ,index);
		List<IdxPlanvalImportVO> vos = new ArrayList<IdxPlanvalImportVO>();
		for(Object[] planval: planInfo ){
			String relateDim = "";
			if(planval[0]!= null){
				String sql1 = " select t1.DIM_TYPE_NM from RPT_VALID_DIM_REL t"
						+ " left join RPT_DIM_TYPE_INFO t1 on t1.DIM_TYPE_NO = t.DIM_NO "
						+ " where t.check_id = ?0 and t.valid_type = ?1"; 
				List<Object[]> dimList = this.baseDAO.findByNativeSQLWithIndexParam(sql1, planval[0],"05");
				if(dimList.size()>0){
					for(Object dim : dimList){
						relateDim += dim+","; 
					}
					relateDim = relateDim.substring(0, relateDim.length()-1);
				}
			}
			IdxPlanvalImportVO vo = new IdxPlanvalImportVO();
			
			vo.setIndexNo(planval[0].toString());
			vo.setOrgNo(planval[1].toString());
			vo.setDataDate(planval[2].toString());
			vo.setCurrency(planval[3].toString());
			vo.setIndexVal(planval[4]!= null ? new BigDecimal(planval[4].toString()):new BigDecimal(""));
			vo.setRelateDim(relateDim);
			vos.add(vo);
		}
		return  vos;
	}
	/**
	 * 查询指标信息表所有存在的指标，用于指标存在性检验
	 * @return 指标号实体
	 */
	public List<Object[]> getIndexNoList() {
		String sql = "select info.INDEX_NO,info.INDEX_NM,info.INDEX_VER_ID from RPT_IDX_INFO info where info.END_DATE = ?0 and info.IS_RPT_INDEX = ?1 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql, "29991231","N");
	}
	/**
	 * 查询机构信息表所有存在的机构，用于机构存在性检验
	 * @return 机构编号实体
	 */
	public List<Object[]> getOrgNoList() {
		String sql = "select info.ORG_NO,info.ORG_NM from RPT_ORG_INFO info where info.IS_VIRTUAL_ORG = ?0 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql,"N");
	}
	/**
	 * 查询币种表中所有存在的币种，用于币种存在性检验
	 * @return 币种编号实体
	 */
	public List<Object[]> getDimItemNoList() {
		String sql = "select dim.DIM_ITEM_NO,dim.DIM_ITEM_NM from RPT_DIM_ITEM_INFO dim where dim.DIM_TYPE_NO = ?0 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql,"CURRENCY");
	}
	
	/**
	 * 获取异步树的目录节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getValidIdxCtls(CommonTreeNode upNode, String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<String> allCtlNos = new ArrayList<String>();
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getPlanValidIdx());//规避oracle in 超过1000
		Map<String,Object> value = new HashMap<String, Object>();
		String jql = "select idx.indexCatalogNo from RptIdxInfo idx where idx.endDate =:endDate and (";
		for(int i=0;i<multiList.size();i++){
			String indexNos = "index" + i;
			if(i == 0){
				jql += " idx.id.indexNo in (:"+indexNos+") ";
			}else{
				jql += " or idx.id.indexNo in (:"+indexNos+") ";
			}
			value.put(indexNos, multiList.get(i));
		}
		jql += ")";
		value.put("endDate", "29991231");
		List<String> oneLvlctlNos = this.baseDAO.findWithNameParm(jql, value);
		if(null != oneLvlctlNos && oneLvlctlNos.size() > 0){
			allCtlNos.addAll(oneLvlctlNos);
			this.getAllUpCtlNos(oneLvlctlNos,allCtlNos);
			jql = "select ctl from RptIdxCatalog ctl where ctl.upNo = ?0 and ctl.indexCatalogNo in (?1)";
			List<RptIdxCatalog> ctls = this.baseDAO.findWithIndexParam(jql, upNode.getId(), allCtlNos);
			if(null != ctls && ctls.size() > 0){
				for(RptIdxCatalog ctl : ctls){
					CommonTreeNode node = new CommonTreeNode();
					Map<String, String> params = new HashMap<String, String>();
					params.put("nodeType", "idxCatalog");
					params.put("validType", validType);
					node.setId(ctl.getIndexCatalogNo());
					node.setUpId(upNode.getId());
					node.setText(ctl.getIndexCatalogNm());
					node.setTitle(ctl.getIndexCatalogNm());
					node.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
					node.setIsParent(true);
					node.setParams(params);
					rstNode.add(node);
				}
			}
		}
		return rstNode;
	}
	
	/**
	 * 获取异步树的指标节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getValidIdxs(CommonTreeNode upNode, String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getPlanValidIdx());//规避oracle in 超过1000
		Map<String,Object> value = new HashMap<String, Object>();
		String jql = "select idx from RptIdxInfo idx where idx.indexCatalogNo = :indexCatalogNo and idx.endDate = :endDate and (";
		for(int i=0;i<multiList.size();i++){
			String indexNos = "index" + i;
			if(i == 0){
				jql += " idx.id.indexNo in (:"+indexNos+") ";
			}else{
				jql += " or idx.id.indexNo in (:"+indexNos+") ";
			}
			value.put(indexNos, multiList.get(i));
		}
		jql += ")";
		value.put("indexCatalogNo", upNode.getId());
		value.put("endDate", "29991231");
		List<RptIdxInfo> idxs = this.baseDAO.findWithNameParm(jql, value);
		if(null != idxs && idxs.size() > 0){
			for(RptIdxInfo idx : idxs){
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "idxInfo");
				params.put("validType", validType);
				params.put("idxType", idx.getIndexType());
				node.setId(idx.getId().getIndexNo());
				node.setUpId(upNode.getId());
				node.setText(idx.getIndexNm());
				node.setTitle(idx.getIndexNm());
				node.setIcon(basePath + "/images/classics/menuicons/grid.png");
				node.setIsParent("05".equals(idx.getIndexType()) ? true : false);
				node.setParams(params);
				rstNode.add(node);
			}
		}
		return rstNode;
	}
	
	/**
	 * 获取异步树的度量节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getIdxMeasure(CommonTreeNode upNode, String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<String> indexNos = new ArrayList<String>();
		indexNos.add(upNode.getId());
		Map<String, String> glMap = this.groupBS.getMeasureRelMapping(indexNos);
		String jql = "select plan from RptIdxPlanvalResult plan where plan.id.indexNo in (?0)";
		List<RptIdxPlanvalResult> plans = this.baseDAO.findWithIndexParam(jql, glMap.keySet());
		if(null != plans && plans.size() > 0){
			for(RptIdxPlanvalResult plan : plans){
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "measureInfo");
				params.put("validType", validType);
				node.setId(plan.getId().getIndexNo());
				node.setUpId(upNode.getId());
				node.setText(glMap.get(plan.getId().getIndexNo()));
				node.setTitle(glMap.get(plan.getId().getIndexNo()));
				node.setIcon(basePath + "/images/classics/icons/icon_link.gif");
				node.setParams(params);
				rstNode.add(node);
			}
		}
		return rstNode;
	}
	
	@SuppressWarnings("serial")
	private List<String> getPlanValidIdx() {
		List<String> idxs = new ArrayList<String>();
		String jql = "select distinct plan.id.indexNo from RptIdxPlanvalResult plan";
		List<String> vIdxs = this.baseDAO.findWithIndexParam(jql);
		if(null != vIdxs && vIdxs.size() > 0){
			for(String indexNo : vIdxs){
				if(StringUtils.isNotBlank(indexNo)){
					idxs.add(StringUtils.split(indexNo, ".")[0]);
				}
			}
		}
		return idxs.size() > 0 ? idxs : new ArrayList<String>(){{add("null");}};
	}
	
	private void getAllUpCtlNos(List<String> ctlNos,List<String> allCtlNos) {
		String jql = "select ctl.upNo from RptIdxCatalog ctl where ctl.indexCatalogNo in (?0)";
		ctlNos = this.baseDAO.findWithIndexParam(jql, ctlNos);
		if(null != ctlNos && ctlNos.size() > 0){
			allCtlNos.addAll(ctlNos);
			getAllUpCtlNos(ctlNos,allCtlNos);
		}
	}
	
	public List<RptIdxInfo> getPlanIdxInfo(List<String> ids){
		List<RptIdxInfo> rstList = new ArrayList<RptIdxInfo>();
		if(null != ids && ids.size() > 0){
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo in (?0) and idx.endDate = ?1";
			rstList = this.baseDAO.findWithIndexParam(jql, ids, "29991231");
		}
		return rstList;
	}
	
	public List<CommonTreeNode> findSyncTreeByKeyWord(String basePath, String searchNm, String validType) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getPlanValidIdx());//规避oracle in 超过1000
		Map<String,Object> values = new HashMap<String, Object>();
		String jql = "select idx from RptIdxInfo idx where (idx.id.indexNo like :indexNo or idx.indexNm like :indexNm) and idx.endDate = :endDate and (";
		for(int i=0;i<multiList.size();i++){
			String indexNos = "index" + i;
			if(i == 0){
				jql += " idx.id.indexNo in (:"+indexNos+") ";
			}else{
				jql += " or idx.id.indexNo in (:"+indexNos+") ";
			}
			values.put(indexNos, multiList.get(i));
		}
		jql += ")";
		values.put("indexNo", "%"+searchNm+"%");
		values.put("indexNm", "%"+searchNm+"%");
		values.put("endDate", "29991231");
		List<RptIdxInfo> idxs = this.baseDAO.findWithNameParm(jql, values);
		if(null != idxs && idxs.size() > 0) {
			List<String> glIndexNo = new ArrayList<String>();//总账
			List<String> oneLvlCtlNos = new ArrayList<String>();
			List<RptIdxCatalog> allCtlobj = new ArrayList<RptIdxCatalog>();
			for(RptIdxInfo idx : idxs) {
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "idxInfo");
				params.put("validType", validType);
				params.put("idxType", idx.getIndexType());
				node.setId(idx.getId().getIndexNo());
				node.setUpId(idx.getIndexCatalogNo());
				node.setText(idx.getIndexNm());
				node.setTitle(idx.getIndexNm());
				node.setIcon(basePath + "/images/classics/menuicons/grid.png");
				node.setParams(params);
				rstNodes.add(node);
				oneLvlCtlNos.add(idx.getIndexCatalogNo());
				if("05".equals(idx.getIndexType())){
					glIndexNo.add(idx.getId().getIndexNo());
				}
			}
			this.groupBS.getAllUpCtlNos(oneLvlCtlNos, allCtlobj);
			if(null != allCtlobj && allCtlobj.size() > 0) {
				for(RptIdxCatalog ctl : allCtlobj) {
					CommonTreeNode node = new CommonTreeNode();
					Map<String, String> params = new HashMap<String, String>();
					params.put("nodeType", "idxCatalog");
					params.put("validType", validType);
					node.setId(ctl.getIndexCatalogNo());
					node.setUpId(ctl.getUpNo());
					node.setText(ctl.getIndexCatalogNm());
					node.setTitle(ctl.getIndexCatalogNm());
					node.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
					node.setParams(params);
					rstNodes.add(node);
				}
			}
			if(null != glIndexNo && glIndexNo.size() > 0) {//总账
				Map<String, String> glMap = this.groupBS.getMeasureRelMapping(glIndexNo);
				jql = "select distinct plan.id.indexNo from RptIdxPlanvalResult plan where plan.id.indexNo in (?0)";
				List<String> measures = this.baseDAO.findWithIndexParam(jql, glMap.keySet());
				if(null != measures && measures.size() > 0){
					for(String measureNo : measures){
						CommonTreeNode node = new CommonTreeNode();
						Map<String, String> params = new HashMap<String, String>();
						params.put("nodeType", "measureInfo");
						params.put("validType", validType);
						node.setId(measureNo);
						node.setUpId(StringUtils.split(measureNo, ".")[0]);
						node.setText(glMap.get(measureNo));
						node.setTitle(glMap.get(measureNo));
						node.setIcon(basePath + "/images/classics/icons/icon_link.gif");
						node.setParams(params);
						rstNodes.add(node);
					}
				}
			}
		}
		return rstNodes;
	}
}
