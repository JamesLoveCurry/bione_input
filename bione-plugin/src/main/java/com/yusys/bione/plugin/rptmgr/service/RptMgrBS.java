package com.yusys.bione.plugin.rptmgr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.SqlValidateUtils;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.label.entity.BioneLabelInfo;
import com.yusys.bione.frame.label.entity.BioneLabelObjInfo;
import com.yusys.bione.frame.label.service.LabelBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrInfoVO;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptMgrBS extends BaseBS<Object> {

	@Autowired
	private RptMgrInfoMybatisDao reportInfoDAO;

	@Autowired
	private UserBS userBS;

	@Autowired
	private LabelBS labelBS;

	// 树根节点图标
	private String treeRootIcon = "/images/classics/icons/bricks.png";

	private String userRootIcon = "/images/classics/icons/user.png";
	// 树文件夹节点图标
	// 树报表叶子节点图标
	private String treeNodeIcon1 = "/images/classics/icons/application_view_icons.png";

	private String treeNodeIcon2 = "/images/classics/icons/application_view_list.png";

	private String roleIcon = "/images/classics/icons/user.png";

	// 文件夹树节前缀
	private String folderTreeNodePre = "f";
	// 报表树节前缀
	private String rptTreeNodePre = "r";

	// 节点类型
	private String rootTreeType = "01";
	private String folderTreeType = "02";
	private String rptTreeType = "03";
	
	private String nullfolderTreeType = "05";

	/**
	 * 获取报表目录树
	 * 
	 * @param folderNo
	 * 
	 * @param extType
	 *            报表扩展类型：GlobalConstants4plugin.RPT_EXT_TYPE_BANK -> 行内报表
	 *            GlobalConstants4plugin.RPT_EXT_TYPE_FRS -> 监管报表
	 * @param showRoot
	 *            是否显示根节点
	 * @return
	 */
	public List<CommonTreeNode> getRptCatalogTreeSync(String extType,
			boolean showRoot,String defSrc) {
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, true, null));
		}
		// 获取全部报表

		// 获取全部的目录结构
		Map<String, Object> params = new HashMap<String, Object>();
		String jql = "select catalog from RptMgrReportCatalog catalog where 1=1 and extType = :extType and defSrc = :defSrc";
		params.put("extType", extType);
		if(StringUtils.isNotBlank(defSrc)){
			if(GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)){
				jql += " and defUser = :defUser";
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			}
			params.put("defSrc", defSrc);
		}
		else{
			params.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		}
		List<RptMgrReportCatalog> catalogs = this.baseDAO.findWithNameParm(jql, params);
		returnNodes.addAll(this.generateFolderNodes(catalogs, extType, true));
		return returnNodes;
	}

	/**
	 * 获取报表目录树
	 * 
	 * @param folderNo
	 * 
	 * @param extType
	 *            报表扩展类型：GlobalConstants4plugin.RPT_EXT_TYPE_BANK -> 行内报表
	 *            GlobalConstants4plugin.RPT_EXT_TYPE_FRS -> 监管报表
	 * @param showRoot
	 *            是否显示根节点
	 * @return
	 */
	public List<CommonTreeNode> getUserCatalogTreeSync(String extType,
			boolean showRoot) {
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, true, null));
		}
		// 获取全部报表

		// 获取全部的目录结构
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("extType", extType);
		params.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_USER);
		params.put("defUser", BioneSecurityUtils.getCurrentUserId());
		List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
				.getRptCatalogs(params);
		returnNodes.addAll(this.generateFolderNodes(catalogs, extType, true));
		CommonTreeNode backCtlNode = new CommonTreeNode();
		backCtlNode.setId(BioneSecurityUtils.getCurrentUserId()+"-"+GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
		backCtlNode.setText("撤回目录");
		backCtlNode.setIcon(this.getContextPath() + GlobalConstants4frame.LOGIC_MODULE_ICON);
		backCtlNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
		Map<String, String>  backMap= new HashMap<String, String>();
		backMap.put("realId", BioneSecurityUtils.getCurrentUserId()+"-"+GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
		backMap.put("nodeType", nullfolderTreeType);
		backCtlNode.setParams(backMap);
		returnNodes.add(backCtlNode);
		return returnNodes;
	}
	
	/**
	 * 获取报表树
	 * 
	 * @param folderNo
	 * 
	 * @param extType
	 *            报表扩展类型：GlobalConstants4plugin.RPT_EXT_TYPE_BANK -> 行内报表
	 *            GlobalConstants4plugin.RPT_EXT_TYPE_FRS -> 监管报表
	 * @param rptType
	 *            报表定义方式
	 * @param searchNm
	 *            过滤条件
	 * @param 是否显示禁用报表
	 * @param showRoot
	 *            是否显示根节点
	 * @param tempalteType
	 *            是明细类报表还是指标类报表 GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL 明细类
	 *            GlobalConstants4plugin.RPT_TMP_TYPE_CELL 单元格类
	 * @param defSrc
	 *            定义来源 GlobalConstants4plugin.RPT_DEF_SRC_***，为null表示全行报表
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getRptTreeSync(String extType, String rptType,
			String searchNm, boolean showInvalid, boolean showRoot,
			List<String> templateType, String rptNum, boolean showCatalog,
			String defSrc,String isCabin) {
		List<String> authRptIds = new ArrayList<String>();
		/*
		 * authRptIds = this.roleRptBS.getReadRptsByUserId(BioneSecurityUtils
		 * .getCurrentUserId());
		 */
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, true, defSrc));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)) {
			// 带有查询条件
			if (!StringUtils.isEmpty(searchNm)) {
				paramMap.put("rptNm", "%" + searchNm + "%");
			}
			if (!StringUtils.isEmpty(rptNum)) {
				paramMap.put("rptNum", rptNum);
			}
		} else if (showCatalog) {
			// 获取全部的目录结构
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("extType", extType);
			if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
				params.put("defSrc", defSrc);
				params.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
						.getOrgNo());
			} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
				params.put("defSrc", defSrc);
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			} else {
				params.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
			}
			List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
					.getRptCatalogs(params);
			returnNodes.addAll(this
					.generateFolderNodes(catalogs, extType, true));
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (!StringUtils.isEmpty(isCabin)) {
			paramMap.put("isCabin", isCabin);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
				|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
			paramMap.put("extType", extType);
		}
		if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
		} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defUser", BioneSecurityUtils.getCurrentUserId());
		} else {
			paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		}
		
		//不是系统管理员，加权限过滤,不然跑数会出现误操作的情况2020年6月15日15:45:57
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			paramMap.put("rptIds", authRptIds);
		}
		
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getRptsByParams(paramMap);
		paramMap.put("catalogId", GlobalConstants4frame.TREE_ROOT_NO);
		rpts.addAll(this.reportInfoDAO.getRptsByParams(paramMap));
		paramMap.put("catalogId", GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
		rpts.addAll(this.reportInfoDAO.getRptsByParams(paramMap));
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType, true,
						null);
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						&& (rptTmp.getInfoRights() == null || rptTmp
								.getInfoRights().equals("Y"))
						&& !authRptIds.contains(rptTmp.getRptId()))
					nodeTmp.getParams().put("isPri", "N");
				else
					nodeTmp.getParams().put("isPri", "Y");
				
				nodeTmp.setTitle(rptTmp.getRptDesc() == null ? rptTmp.getRptNm() : rptTmp.getRptDesc());
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)
					|| !showCatalog) {
				// 迭代查询需要展现的目录结构
				returnNodes.addAll(this.getFolderNodes(catalogIds, extType,
						defSrc, null, true));
			}
			if (catalogIds.contains(GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID)) {
				CommonTreeNode backCtlNode = new CommonTreeNode();
				backCtlNode.setId(BioneSecurityUtils.getCurrentUserId()+"-"+GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
				backCtlNode.setText("撤回目录");
				backCtlNode.setIcon(this.getContextPath() + GlobalConstants4frame.LOGIC_MODULE_ICON);
				backCtlNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				Map<String, String> backMap = new HashMap<String, String>();
				backMap.put("realId", GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
				backMap.put("nodeType", nullfolderTreeType);
				backCtlNode.setParams(backMap);
				returnNodes.add(backCtlNode);
			}
		}
		return returnNodes;
	}

	public List<CommonTreeNode> getRptTreeSyncPro(String extType, String rptType,
			String searchObj, boolean showInvalid, boolean showRoot,
			List<String> templateType, String rptNum, boolean showCatalog,
			String defSrc,String isCabin) {
		List<String> authRptIds = new ArrayList<String>();
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, true, defSrc));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(searchObj)) {
			JSONObject obj = JSON.parseObject(searchObj);
			if(!"".equals(obj.get("rptNum"))){
				paramMap.put("rptNum", obj.get("rptNum"));
			}
			if(!"".equals(obj.get("rptNm"))){
				paramMap.put("rptNm", "%" + obj.get("rptNm") + "%");			
			}
			if(!"".equals(obj.get("isCabin"))){
				paramMap.put("isCabin", obj.get("isCabin"));			
			}
		} else if (showCatalog) {
			// 获取全部的目录结构
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("extType", extType);
			if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
				params.put("defSrc", defSrc);
				params.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
						.getOrgNo());
			} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
				params.put("defSrc", defSrc);
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			} else {
				params.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
			}
			List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
					.getRptCatalogs(params);
			returnNodes.addAll(this
					.generateFolderNodes(catalogs, extType, true));
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (!StringUtils.isEmpty(isCabin)) {
			paramMap.put("isCabin", isCabin);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
				|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
			paramMap.put("extType", extType);
		}
		if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
		} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defUser", BioneSecurityUtils.getCurrentUserId());
		} else {
			paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		}
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getRptsByParams(paramMap);
		paramMap.put("catalogId", GlobalConstants4frame.TREE_ROOT_NO);
		rpts.addAll(this.reportInfoDAO.getRptsByParams(paramMap));
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType, true,
						null);
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						&& (rptTmp.getInfoRights() == null || rptTmp
								.getInfoRights().equals("Y"))
						&& !authRptIds.contains(rptTmp.getRptId()))
					nodeTmp.getParams().put("isPri", "N");
				else
					nodeTmp.getParams().put("isPri", "Y");
				
				nodeTmp.setTitle(rptTmp.getRptDesc() == null ? rptTmp.getRptNm() : rptTmp.getRptDesc());
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			if (StringUtils.isNotBlank(searchObj) || !showCatalog) {
				// 迭代查询需要展现的目录结构
				if (catalogIds.contains(GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID)) {
					CommonTreeNode backCtlNode = new CommonTreeNode();
					backCtlNode.setId(BioneSecurityUtils.getCurrentUserId()+"-"+GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
					backCtlNode.setText("撤回目录");
					backCtlNode.setIcon(this.getContextPath() + GlobalConstants4frame.LOGIC_MODULE_ICON);
					backCtlNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
					Map<String, String> backMap = new HashMap<String, String>();
					backMap.put("realId", GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
					backMap.put("nodeType", nullfolderTreeType);
					backCtlNode.setParams(backMap);
					returnNodes.add(backCtlNode);
				}
				returnNodes.addAll(this.getFolderNodes(catalogIds, extType,
						defSrc, null, true));
			}
		}
		return returnNodes;
	}

	/**
	 * 获取报表树
	 * 
	 * @param folderNo
	 * 
	 * @param extType
	 *            报表扩展类型：GlobalConstants4plugin.RPT_EXT_TYPE_BANK -> 行内报表
	 *            GlobalConstants4plugin.RPT_EXT_TYPE_FRS -> 监管报表
	 * @param rptType
	 *            报表定义方式
	 * @param searchNm
	 *            过滤条件
	 * @param showInvalid
	 *            是否显示禁用报表
	 * @param showRoot
	 *            是否显示根节点
	 * @param tempalteType
	 *            是明细类报表还是指标类报表 GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL 明细类
	 *            GlobalConstants4plugin.RPT_TMP_TYPE_CELL 单元格类
	 * @param defSrc
	 *            定义来源 GlobalConstants4plugin.RPT_DEF_SRC_***，为null表示全行报表
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getAllRptTreeSync(String extType,
			String rptType, String searchNm, boolean showInvalid,
			boolean showRoot, List<String> templateType, String rptNum,
			boolean showCatalog, String defSrc) {
		List<String> authRptIds = new ArrayList<String>();
		/*
		 * authRptIds = this.roleRptBS.getReadRptsByUserId(BioneSecurityUtils
		 * .getCurrentUserId());
		 */
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, true, defSrc));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)) {
			// 带有查询条件
			if (!StringUtils.isEmpty(searchNm)) {
				paramMap.put("rptNm", "%" + searchNm + "%");
			}
			if (!StringUtils.isEmpty(rptNum)) {
				paramMap.put("rptNum", rptNum);
			}
		} else if (showCatalog) {
			// 获取全部的目录结构
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("extType", extType);
			if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
				params.put("defSrc", defSrc);
				params.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
						.getOrgNo());
			} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
				params.put("defSrc", defSrc);
				params.put("defUser", BioneSecurityUtils.getCurrentUserId());
			} else {
				params.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
			}
			List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
					.getRptCatalogs(params);
			RptMgrReportCatalog catalog = new RptMgrReportCatalog();
			catalog.setCatalogId("f");
			catalog.setCatalogNm("报表模型配置");
			catalog.setUpCatalogId("0");
			catalogs.add(catalog);
			returnNodes.addAll(this
					.generateFolderNodes(catalogs, extType, true));
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
				|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
			paramMap.put("extType", extType);
		}
		if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
		} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defUser", BioneSecurityUtils.getCurrentUserId());
		} else {
			paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		}
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getAllRptsByParams(paramMap);
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				// if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
				// continue;
				// }
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType, true,
						null);
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						&& (rptTmp.getInfoRights() == null || rptTmp
								.getInfoRights().equals("Y"))
						&& !authRptIds.contains(rptTmp.getRptId()))
					nodeTmp.getParams().put("isPri", "N");
				else
					nodeTmp.getParams().put("isPri", "Y");
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)
					|| !showCatalog) {
				// 迭代查询需要展现的目录结构

				returnNodes.addAll(this.getFolderNodes(catalogIds, extType,
						defSrc, null, true));
			}
		}
		List<RptMgrReportCatalog> catalogs = new ArrayList<RptMgrReportCatalog>();
		RptMgrReportCatalog catalog = new RptMgrReportCatalog();
		catalog.setCatalogId("");
		catalog.setCatalogNm("报表模型配置");
		catalog.setUpCatalogId("0");
		catalogs.add(catalog);
		returnNodes.addAll(this.generateFolderNodes(catalogs, extType, true));
		return returnNodes;
	}

	public List<CommonTreeNode> getRptTreeByIds(String extType, String rptType,
			String searchNm, boolean showInvalid, boolean showRoot,
			List<String> templateType, String rptNum, boolean showCatalog,
			boolean iconHasContext, String resNo,List<String> rptIds) {
		List<String> authRptIds = new ArrayList<String>();
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, iconHasContext, null));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if(rptIds != null && rptIds.size() > 0 ){
			paramMap.put("rptIds", rptIds);
		}
		else{
			return returnNodes;
		}
		if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)) {
			// 带有查询条件
			if (!StringUtils.isEmpty(searchNm)) {
				paramMap.put("rptNm", "%" + searchNm + "%");
			}
			if (!StringUtils.isEmpty(rptNum)) {
				paramMap.put("rptNum", rptNum);
			}
		} else if (showCatalog) {
			// 获取全部的目录结构
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("extType", extType);
			params.put("defaultDefSrc", GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
			List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
					.getRptCatalogs(params);
			returnNodes.addAll(this.generateFolderNodes(catalogs, extType,
					iconHasContext));
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
				|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
			paramMap.put("extType", extType);
		}
		paramMap.put("defaultDefSrc", GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getRptsByParams(paramMap);
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType,
						iconHasContext, resNo);
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						&& (rptTmp.getInfoRights() == null || rptTmp
								.getInfoRights().equals("Y"))
						&& !authRptIds.contains(rptTmp.getRptId()))
					nodeTmp.getParams().put("isPri", "N");
				else
					nodeTmp.getParams().put("isPri", "Y");
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)
					|| !showCatalog) {
				// 迭代查询需要展现的目录结构
				returnNodes.addAll(this.getFolderNodes(catalogIds, extType,
						null, null, iconHasContext));
			}
		}
		return returnNodes;
	}
	
	public List<CommonTreeNode> getRptTreeAuth(String extType, String rptType,
			String searchNm, boolean showInvalid, boolean showRoot,
			List<String> templateType, String rptNum, boolean showCatalog,
			boolean iconHasContext, String resNo) {
		List<String> authRptIds = new ArrayList<String>();
		authRptIds = BioneSecurityUtils.getResIdListOfUser(resNo);
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, iconHasContext, null));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)) {
			// 带有查询条件
			if (!StringUtils.isEmpty(searchNm)) {
				paramMap.put("rptNm", "%" + searchNm + "%");
			}
			if (!StringUtils.isEmpty(rptNum)) {
				paramMap.put("rptNum", rptNum);
			}
		} else if (showCatalog) {
			// 获取全部的目录结构
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("extType", extType);
			params.put("defaultDefSrc", GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
			List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
					.getRptCatalogs(params);
			returnNodes.addAll(this.generateFolderNodes(catalogs, extType,
					iconHasContext));
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
				|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
			paramMap.put("extType", extType);
		}
		paramMap.put("defaultDefSrc", GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
		//权限控制，管理者只能看到授权给自己的报表，普通用户无权限
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			if (authRptIds.size()>0) {
				paramMap.put("authRptIds", SplitStringBy1000.change(authRptIds));				
			} else {
				return returnNodes;
			}
		} else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& !"Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			return returnNodes;
		}
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getRptsByParams(paramMap);
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType,
						iconHasContext, resNo);
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						&& (rptTmp.getInfoRights() == null || rptTmp
								.getInfoRights().equals("Y"))
						&& !authRptIds.contains(rptTmp.getRptId())){
					nodeTmp.getParams().put("isPri", "N");
				} else {
					nodeTmp.getParams().put("isPri", "Y");
				}
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)
					|| !showCatalog) {
				// 迭代查询需要展现的目录结构
				returnNodes.addAll(this.getFolderNodes(catalogIds, extType,
						null, null, iconHasContext));
			}
		}
		return returnNodes;
	}
	
	public List<CommonTreeNode> getRptTreeAuthByObj(String extType, String rptType,
			String searchObj, boolean showInvalid, boolean showRoot,
			List<String> templateType, String rptNum, boolean showCatalog,
			boolean iconHasContext, String resNo) {
		List<String> authRptIds = new ArrayList<String>();
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, iconHasContext, null));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(searchObj)) {
			JSONObject obj = JSON.parseObject(searchObj);
			if(!"".equals(obj.get("rptNum"))){
				paramMap.put("rptNum", obj.get("rptNum"));
			}
			if(!"".equals(obj.get("rptNm"))){
				paramMap.put("rptNm", "%" + obj.get("rptNm") + "%");			
			}
			if(!"".equals(obj.get("belongDeptNm"))){
				paramMap.put("belongDeptId", obj.get("belongDeptNm"));
			}
			if(!"".equals(obj.get("isCabin"))){
				paramMap.put("isCabin", obj.get("isCabin"));			
			}
		} else if (showCatalog) {
			// 获取全部的目录结构
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("extType", extType);
			params.put("defaultDefSrc", GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
			List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
					.getRptCatalogs(params);
			returnNodes.addAll(this.generateFolderNodes(catalogs, extType,
					iconHasContext));
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
				|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
			paramMap.put("extType", extType);
		}
		paramMap.put("defaultDefSrc", GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getRptsByParams(paramMap);
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType,
						iconHasContext, resNo);
				if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						&& (rptTmp.getInfoRights() == null || rptTmp
								.getInfoRights().equals("Y"))
						&& !authRptIds.contains(rptTmp.getRptId()))
					nodeTmp.getParams().put("isPri", "N");
				else
					nodeTmp.getParams().put("isPri", "Y");
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			if (StringUtils.isNotBlank(searchObj) || !showCatalog) {
				// 迭代查询需要展现的目录结构
				returnNodes.addAll(this.getFolderNodes(catalogIds, extType,
						null, null, iconHasContext));
			}
		}
		return returnNodes;
	}

	public List<CommonTreeNode> getRptAndTemplateTree(String extType,
			String rptType, boolean showInvalid, List<String> templateType,
			String searchName) {
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 获取全部的目录结构
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("extType", extType);
		params.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
				.getRptCatalogs(params);
		returnNodes.addAll(this.generateFolderNodes(catalogs, extType, true));
		if (!StringUtils.isEmpty(searchName)) {
			// 带有查询条件
			if (!StringUtils.isEmpty(searchName)) {
				//2020 lcy 【后台管理】sql注入 代码优化
				if(SqlValidateUtils.validateStr(searchName)) {
					searchName = SqlValidateUtils.replaceValue(searchName);
				}
				paramMap.put("rptNm", "%" + searchName + "%");
			}
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
				|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
			paramMap.put("extType", extType);
		}
		paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		List<RptMgrInfoVO> rpts = this.reportInfoDAO.getRptVOsByParams(paramMap);
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		if (rpts != null && rpts.size() > 0) {
			paramMap.clear();
			paramMap.put("tempalteType", templateType);
			if (!StringUtils.isEmpty(searchName)) {
				for (RptMgrInfoVO rptVO : rpts) {
					CommonTreeNode reportNode = new CommonTreeNode();
					reportNode.setId(rptVO.getRptId());
					reportNode.setText(rptVO.getRptNm());
					//reportNode.setUpId(catalog.getCatalogId());
					//202002 lcy 增加明细类报表逻辑校验配置
					reportNode.getParams().put("nodeType", this.getReportNodeTypeByTemplateType(rptVO.getTemplateType()));
					reportNode.getParams().put("templateId",rptVO.getCfgId());
					reportNode.getParams().put("rptId", rptVO.getRptId());
					reportNode.getParams().put("busiType", rptVO.getBusiType());
					reportNode.getParams().put("rptCycle", rptVO.getRptCycle());
					reportNode.getParams().put("verStartDate", rptVO.getVerStartDate());
					reportNode.setIcon(this.getContextPath()
									+ (rptVO.getRptType().equals("01") ? treeNodeIcon1
											: treeNodeIcon2));
					resultList.add(reportNode);
				}
			}else{
				for (RptMgrReportCatalog catalog : catalogs) {
					CommonTreeNode catalogNode = new CommonTreeNode();
					catalogNode.setId(catalog.getCatalogId());
					catalogNode.setText(catalog.getCatalogNm());
					catalogNode.setUpId(catalog.getUpCatalogId());
					catalogNode.getParams().put("nodeType", "catalog");
					catalogNode.setIcon(this.getContextPath() + GlobalConstants4frame.LOGIC_MODULE_ICON);
					List<CommonTreeNode> catalogChild = new ArrayList<CommonTreeNode>();
					catalogNode.setChildren(catalogChild);

					for (RptMgrInfoVO rptVO : rpts) {
						if (rptVO.getCatalogId().equals(catalog.getCatalogId())) {
							CommonTreeNode reportNode = new CommonTreeNode();
							reportNode.setId(rptVO.getRptId());
							reportNode.setText(rptVO.getRptNm());
							reportNode.setUpId(catalog.getCatalogId());
							//202002 lcy 增加明细类报表逻辑校验配置
							reportNode.getParams().put("nodeType", this.getReportNodeTypeByTemplateType(rptVO.getTemplateType()));
							reportNode.getParams().put("templateId",
									rptVO.getCfgId());
							reportNode.getParams().put("rptId", rptVO.getRptId());
							reportNode.getParams().put("busiType", rptVO.getBusiType());
							reportNode.getParams().put("rptCycle", rptVO.getRptCycle());
							reportNode.getParams().put("verStartDate", rptVO.getVerStartDate());
							reportNode.setIcon(this.getContextPath()
											+ (rptVO.getRptType().equals("01") ? treeNodeIcon1
													: treeNodeIcon2));
							catalogChild.add(reportNode);
						}
					}

					resultList.add(catalogNode);
				}
			}
			
		}
		return resultList;
	}

	/**
	 * 根据模版类型 获取节点类型
	 * 明细报表 和 综合报表
	 * 202002 lcy 增加明细类报表逻辑校验配置
	 * @param templateType
	 * @return
	 */
	private String getReportNodeTypeByTemplateType(String templateType) {
		String nodeType = "report";
		if(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL.equals(templateType)
				|| GlobalConstants4plugin.RPT_TMP_TYPE_COM.equals(templateType)){
			//202002 lcy 明细类报表 和 综合类报表 逻辑校验配置展示 挡板
			nodeType = "detail_report";
		}
		return nodeType;
	}

	/**
	 * 根据用户ID获取授权报表树 add by zhongqh
	 * 
	 * @param folderNo
	 * 
	 * @param extType
	 *            报表扩展类型：GlobalConstants4plugin.RPT_EXT_TYPE_BANK -> 行内报表
	 *            GlobalConstants4plugin.RPT_EXT_TYPE_FRS -> 监管报表
	 * @param rptType
	 *            报表定义方式
	 * @param searchNm
	 *            过滤条件
	 * @param showInvalid
	 *            是否显示禁用报表
	 * @param showRoot
	 *            是否显示根节点
	 * @param tempalteType
	 *            是明细类报表还是指标类报表 GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL 明细类
	 *            GlobalConstants4plugin.RPT_TMP_TYPE_CELL 单元格类
	 * @param userId
	 *            用户ID
	 * @param defSrc
	 * @param authType
	 *            权限类型：分为查看权限和填报权限 标识分别为 ： AUTH_RES_RPT_VIEW 和 AUTH_RES_RPT_FILL
	 * 
	 * @return
	 */
	public List<CommonTreeNode> getAuthRptTreeSync(String extType,
			String rptType, String searchNm, boolean showInvalid,
			boolean showRoot, List<String> templateType, String rptNum,
			String userId, String defSrc, String authType) {
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		// 如果用户为空 或者 权限类型为空，直接返回
		if (StringUtils.isEmpty(authType)) {
			return returnNodes;
		}
		List<String> authRptIds = Lists.newArrayList();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			if ("AUTH_RES_RPT_VIEW".equals(authType)) {
				// authRptIds = this.roleRptBS.getReadRptsByUserId(userId);
				authRptIds = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_RPT_VIEW");
			}
			if ("AUTH_RES_RPT_FILL".equals(authType)) {
				// authRptIds = this.roleRptBS.getWriteRptsByUserId(userId);
				authRptIds = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_RPT_FILL");
			}
		}
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, true, defSrc));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 加上权限过滤

		if (!StringUtils.isEmpty(searchNm) || !StringUtils.isEmpty(rptNum)) {
			// 带有查询条件
			if (!StringUtils.isEmpty(searchNm)) {
				paramMap.put("rptNm", "%" + searchNm + "%");
			}
			if (!StringUtils.isEmpty(rptNum)) {
				paramMap.put("rptNum", rptNum);
			}
		} /*
		 * else { // 获取全部的目录结构 Map<String, Object> params = new HashMap<String,
		 * Object>(); if (extType != null && !extType.equals("")) {
		 * params.put("extType", extType); } List<RptMgrReportCatalog> catalogs
		 * = this.reportInfoDAO .getRptCatalogs(params);
		 * returnNodes.addAll(this.generateFolderNodes(catalogs, extType)); }
		 */
		if (rptType != null && !StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (extType != null && !extType.equals("")) {
			if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
					|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
				paramMap.put("extType", extType);
			}
		}
		if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
		} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defUser", BioneSecurityUtils.getCurrentUserId());
		} else {
			paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		}
		List<RptMgrReportInfo> rpts = new ArrayList<RptMgrReportInfo>();
		if (authType != null && !authType.equals("")) {
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				if (authRptIds != null && authRptIds.size() > 0) {
					paramMap.put("authRptIds",
							ReBuildParam.toDbList(authRptIds));
					rpts = this.reportInfoDAO.getRptsByParams(paramMap);
				} else {
					authRptIds.add("");
					paramMap.put("authRptIds",
							ReBuildParam.toDbList(authRptIds));
				}
			}
		}
		rpts = this.reportInfoDAO.getRptsByParams(paramMap);
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType, true,
						null);
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			// if (!StringUtils.isEmpty(searchNm) ||
			// !StringUtils.isEmpty(rptNum)) {
			// 迭代查询需要展现的目录结构
			returnNodes.addAll(this.getFolderNodes(catalogIds, extType, defSrc,
					null, true));
			// }
		}
		return returnNodes;
	}
	
	public List<CommonTreeNode> getAuthRptTreeSyncPro(String extType,
			String rptType, String searchObj, boolean showInvalid,
			boolean showRoot, List<String> templateType, String rptNum,
			String userId, String defSrc, String authType) {
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		// 如果用户为空 或者 权限类型为空，直接返回
		if (StringUtils.isEmpty(authType)) {
			return returnNodes;
		}
		List<String> authRptIds = Lists.newArrayList();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			if ("AUTH_RES_RPT_VIEW".equals(authType)) {
				// authRptIds = this.roleRptBS.getReadRptsByUserId(userId);
				authRptIds = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_RPT_VIEW");
			}
			if ("AUTH_RES_RPT_FILL".equals(authType)) {
				// authRptIds = this.roleRptBS.getWriteRptsByUserId(userId);
				authRptIds = BioneSecurityUtils
						.getResIdListOfUser("AUTH_RES_RPT_FILL");
			}
		}
		if (showRoot) {
			// 构建根节点
			returnNodes.addAll(generateRootNode(extType, true, defSrc));
		}
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 带有查询条件
		if (StringUtils.isNotBlank(searchObj)) {
			JSONObject obj = JSON.parseObject(searchObj);
			if(!"".equals(obj.get("rptNum"))){
				paramMap.put("rptNum", obj.get("rptNum"));
			}
			if(!"".equals(obj.get("rptNm"))){
				paramMap.put("rptNm", "%" + obj.get("rptNm") + "%");			
			}
			if(!"".equals(obj.get("belongDeptNm"))){
				paramMap.put("belongDeptId", obj.get("belongDeptNm"));
			}
			if(!"".equals(obj.get("isCabin"))){
				paramMap.put("isCabin", obj.get("isCabin"));			
			}
		}
		if (rptType != null && !StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		if (extType != null && !extType.equals("")) {
			if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
					|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
				paramMap.put("extType", extType);
			}
		}
		if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
		} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
			paramMap.put("defSrc", defSrc);
			paramMap.put("defUser", BioneSecurityUtils.getCurrentUserId());
		} else {
			paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		}
		List<RptMgrReportInfo> rpts = new ArrayList<RptMgrReportInfo>();
		if (authType != null && !authType.equals("")) {
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
				if (authRptIds != null && authRptIds.size() > 0) {
					paramMap.put("authRptIds",
							ReBuildParam.toDbList(authRptIds));
					rpts = this.reportInfoDAO.getRptsByParams(paramMap);
				} else {
					authRptIds.add("");
					paramMap.put("authRptIds",
							ReBuildParam.toDbList(authRptIds));
				}
			}
		}
		rpts = this.reportInfoDAO.getRptsByParams(paramMap);
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = generateRptNode(rptTmp, extType, true,
						null);
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			// 迭代查询需要展现的目录结构
			returnNodes.addAll(this.getFolderNodes(catalogIds, extType, defSrc,
					null, true));
			// }
		}
		return returnNodes;
	}

	

	public List<CommonTreeNode> getAuthRoleTree(String rptId, String roleNm,
			String authType) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("resId", rptId);
		// paramMap.put("logicSysNo",
		// BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		paramMap.put("objDefNo", GlobalConstants4plugin.AUTH_ROLE);
		paramMap.put("resDefNo", authType);
		paramMap.put("resId", rptId);
		if (roleNm != null && !roleNm.equals(""))
			paramMap.put("roleNm", "%" + roleNm + "%");
		List<BioneRoleInfo> relVos = reportInfoDAO
				.getRptRoleInfoByParam(paramMap);
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId("0");
		rootNode.setUpId("");
		rootNode.setText("角色");
		rootNode.setIsParent(true);
		rootNode.setIcon(this.getContextPath() + treeRootIcon);
		nodes.add(rootNode);
		if (relVos != null && relVos.size() > 0) {
			for (BioneRoleInfo vo : relVos) {
				CommonTreeNode node = new CommonTreeNode();
				node.setId(vo.getRoleId());
				node.setText(vo.getRoleName());
				node.setUpId("0");
				node.setIcon(this.getContextPath() + roleIcon);
				nodes.add(node);
			}
		}
		return nodes;
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
	
	@SuppressWarnings("unchecked")
	public List<CommonTreeNode> getLabelFilter(String basePath, String searchNm,
			String rptType, List<String> templateType, boolean showInvalid,String ids,String type) {
		List<String> authRptIds = new ArrayList<String>();
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		if(type.equals("filter")){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Set<String> idsList = ArrayUtils.asSet(ids, ",");
 			BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", "rpt");
			String labelObjId = "";
			if(obj!=null){
				labelObjId = obj.getLabelObjId();
			}
			Set<String> allLabelId = new HashSet<String>();
			this.getAllLabelInfo(idsList, allLabelId, labelObjId);
			paramMap.put("labelObjId", labelObjId);
			paramMap.put("labelIds", allLabelId);
			// 获取全部报表
			if (!StringUtils.isEmpty(searchNm)) {
				// 带有查询条件
				if (!StringUtils.isEmpty(searchNm)) {
					paramMap.put("rptNm", "%" + searchNm + "%");
				}
			}
			if (!StringUtils.isEmpty(rptType)) {
				paramMap.put("rptType", rptType);
			}
			if (templateType != null && templateType.size() > 0) {
				paramMap.put("templateType", templateType);
			}
			if (!showInvalid) {
				paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			}
			paramMap.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
			paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
			List<RptMgrReportInfo> rpts = this.reportInfoDAO
					.getRptsByLabel(paramMap);
			if(rpts != null && rpts.size()>0){
				List<String> rptIds = new ArrayList<String>();
				for(RptMgrReportInfo rpt : rpts){
					rptIds.add(rpt.getRptId());
				}
				Map<String,List<String>> rptlabelMap = this.labelBS.getIdxLabelMap("rpt", rptIds);
				for(RptMgrReportInfo rptTmp : rpts){
					if (BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
							|| !authRptIds.contains(rptTmp.getRptId())) {
						List<String> labels = rptlabelMap.get(rptTmp.getRptId());
						if(labels != null && labels.size()>0){
							for(String label : labels){
								if(allLabelId.contains(label)){
									CommonTreeNode newNode =  generateRptNode(rptTmp,
											GlobalConstants4plugin.RPT_EXT_TYPE_BANK, true, null);;
									newNode.setUpId(label);
									returnNodes.add(newNode);
								}
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
							returnNodes.add(generateLabelNode(basePath,info));
						}
					}
				}
			}
		}
		if(type.equals("search")){
			String AllArray[] = StringUtils.split(ids,",");
			List<String> rptLIds = null;
			for(int i=0;i<AllArray.length;i++){
				List<String> labelIdsList = ArrayUtils.asList(AllArray[i], ",");
				if(rptLIds == null){
					rptLIds = this.labelBS.getObjIdByObjNo("rpt", labelIdsList);
				}
				else{
					List<String> colIds = this.labelBS.getObjIdByObjNo("rpt", labelIdsList);
					if(colIds.size()<=0){
						return returnNodes;
					}
					rptLIds.retainAll(colIds);
				}
			}
			if(rptLIds == null ){
				return returnNodes;
			}
			if(rptLIds != null && rptLIds.size()<=0){
				return returnNodes;
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 获取全部报表
			if (!StringUtils.isEmpty(searchNm)) {
				// 带有查询条件
				if (!StringUtils.isEmpty(searchNm)) {
					paramMap.put("rptNm", "%" + searchNm + "%");
				}
			}
			if (!StringUtils.isEmpty(rptType)) {
				paramMap.put("rptType", rptType);
			}
			if (templateType != null && templateType.size() > 0) {
				paramMap.put("templateType", templateType);
			}
			if (!showInvalid) {
				paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			}
			paramMap.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
			paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
			List<RptMgrReportInfo> rpts = this.reportInfoDAO
					.getRptsByParams(paramMap);
			if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				rptLIds = ListUtils.intersection(authRptIds, rptLIds);
			}
			returnNodes.addAll(generateRootNode(GlobalConstants4plugin.RPT_EXT_TYPE_BANK, true, null));
			for(RptMgrReportInfo rptTmp : rpts){
				if (rptLIds.contains(rptTmp.getRptId())) {
					CommonTreeNode newNode =  generateRptNode(rptTmp,
							GlobalConstants4plugin.RPT_EXT_TYPE_BANK, true, null);;
					newNode.setUpId("0");
					returnNodes.add(newNode);
				}
				
			}
		}
		return returnNodes;
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
	 * 获取标签树
	 * 
	 * @param searchNm
	 * @return
	 */
	public List<CommonTreeNode> getLabeltree(String basePath, String searchNm,
			String rptType, List<String> templateType, boolean showInvalid ,boolean isAuth) {
		List<String> authRptIds = new ArrayList<String>();
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(searchNm)) {
			// 带有查询条件
			if (!StringUtils.isEmpty(searchNm)) {
				paramMap.put("rptNm", "%" + searchNm + "%");
			}
		} else {

		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		paramMap.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getRptsByParams(paramMap);

		if (rpts != null && rpts.size() > 0) {
			for (RptMgrReportInfo rptTmp : rpts) {
				CommonTreeNode nodeTmp = generateRptNode(rptTmp,
						GlobalConstants4plugin.RPT_EXT_TYPE_BANK, true, null);
				if (BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						|| authRptIds.contains(rptTmp.getRptId()) || !isAuth) {
					returnNodes.add(nodeTmp);
				}
			}
			returnNodes = this.labelBS.getLabelObjTree(basePath, returnNodes,
					"rpt");
		}
		return returnNodes;
	}
	
	public List<CommonTreeNode> getLabeltreePro(String basePath, String searchObj,
			String rptType, List<String> templateType, boolean showInvalid) {
		List<String> authRptIds = new ArrayList<String>();
		authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(searchObj)) {
			JSONObject obj = JSON.parseObject(searchObj);
			if(!"".equals(obj.get("rptNum"))){
				paramMap.put("rptNum", obj.get("rptNum"));
			}
			if(!"".equals(obj.get("rptNm"))){
				paramMap.put("rptNm", "%" + obj.get("rptNm") + "%");			
			}
			if(!"".equals(obj.get("belongDeptNm"))){
				paramMap.put("belongDeptId", obj.get("belongDeptNm"));
			}
			if(!"".equals(obj.get("isCabin"))){
				paramMap.put("isCabin", obj.get("isCabin"));			
			}
		}
		if (!StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		if (templateType != null && templateType.size() > 0) {
			paramMap.put("templateType", templateType);
		}
		if (!showInvalid) {
			paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		}
		paramMap.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_BANK);
		paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
		List<RptMgrReportInfo> rpts = this.reportInfoDAO
				.getRptsByParams(paramMap);

		if (rpts != null && rpts.size() > 0) {
			for (RptMgrReportInfo rptTmp : rpts) {
				CommonTreeNode nodeTmp = generateRptNode(rptTmp,
						GlobalConstants4plugin.RPT_EXT_TYPE_BANK, true, null);
				if (BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
						|| !authRptIds.contains(rptTmp.getRptId())) {
					returnNodes.add(nodeTmp);
				}
			}
			returnNodes = this.labelBS.getLabelObjTree(basePath, returnNodes,
					"rpt");
		}
		return returnNodes;
	}

	public boolean validateRpt(String rptId,String rptNm){
		Map<String,Object> params = new HashMap<String,Object>();
		String jql = "select rpt from RptMgrReportInfo rpt where rpt.rptType = :rptType and rpt.rptNm = :rptNm";
		params.put("rptNm", rptNm);
		params.put("rptType", "01");
		if(StringUtils.isNotBlank(rptId)){
			jql += " and rpt.rptId != :rptId";
			params.put("rptId", rptId);
		}
		List<RptMgrReportInfo> rpt = this.baseDAO.findWithNameParm(jql, params);
		if(rpt.size()>0){
			return false;
		}
		return true;
	}
	/***** Private Method begin *****/

	// 获取上下文路径
	private String getContextPath() {
		return GlobalConstants4frame.APP_CONTEXT_PATH;
	}

	// 获取相应的报表目录
	public List<CommonTreeNode> getFolderNodes(List<String> folderIds,
			String extType, String defSrc, List<CommonTreeNode> nodesTmp,
			boolean iconHasContext) {
		if (nodesTmp == null) {
			nodesTmp = new ArrayList<CommonTreeNode>();
		}
		if (folderIds != null && folderIds.size() > 0) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("extType", extType);
			paramMap.put("ids", folderIds);
			if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
				paramMap.put("defSrc", defSrc);
				paramMap.put("defOrg", BioneSecurityUtils.getCurrentUserInfo()
						.getOrgNo());
			} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
				paramMap.put("defSrc", defSrc);
				paramMap.put("defUser", BioneSecurityUtils.getCurrentUserId());
			} else {
				paramMap.put("defaultDefSrc", GlobalConstants4plugin.RPT_DEF_SRC_LIB);
			}
			List<RptMgrReportCatalog> catalogs = this.reportInfoDAO
					.getRptCatalogs(paramMap);
			if (catalogs != null) {
				nodesTmp.addAll(this.generateFolderNodes(catalogs, extType,
						nodesTmp, iconHasContext));
				// 获取上级编目号集合
				List<String> upIds = new ArrayList<String>();
				for (RptMgrReportCatalog cTmp : catalogs) {
					if (StringUtils.isEmpty(cTmp.getUpCatalogId())
							|| GlobalConstants4frame.TREE_ROOT_NO.equals(cTmp
									.getUpCatalogId())) {
						continue;
					}
					if (!upIds.contains(cTmp.getUpCatalogId())) {
						upIds.add(cTmp.getUpCatalogId());
					}
				}
				// 迭代查询
				this.getFolderNodes(upIds, extType, defSrc, nodesTmp,
						iconHasContext);
			}
		}
		return nodesTmp;
	}

	// 构造根节点
	private List<CommonTreeNode> generateRootNode(String extType,
			boolean iconHasContext, String defSrc) {
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();

		// treeNode.setText("全部报表");
		if (GlobalConstants4plugin.RPT_DEF_SRC_ORG.equals(defSrc)) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setText(StringUtils.isEmpty(BioneSecurityUtils
					.getCurrentUserInfo().getOrgName()) ? "当前机构"
					: BioneSecurityUtils.getCurrentUserInfo().getOrgName());
			treeNode.setTitle(StringUtils.isEmpty(BioneSecurityUtils
					.getCurrentUserInfo().getOrgName()) ? "当前机构"
					: BioneSecurityUtils.getCurrentUserInfo().getOrgName());
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("nodeType", folderTreeType);
			treeNode.setIcon((iconHasContext ? this.getContextPath() : "")
					+ treeRootIcon);
			paramMap.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
			treeNode.setParams(paramMap);
			treeNodes.add(treeNode);
		} else if (GlobalConstants4plugin.RPT_DEF_SRC_USER.equals(defSrc)) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			treeNode.setText(userBS.getUserNo(BioneSecurityUtils
					.getCurrentUserId()));
			treeNode.setTitle(userBS.getUserNo(BioneSecurityUtils
					.getCurrentUserId()));
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("nodeType", folderTreeType);

			treeNode.setIcon((iconHasContext ? this.getContextPath() : "")
					+ userRootIcon);
			paramMap.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
			treeNode.setParams(paramMap);
			treeNodes.add(treeNode);
		} else {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			Map<String, String> paramMap = new HashMap<String, String>();
			treeNode.setText("全部报表");
			treeNode.setTitle("全部报表");
			paramMap.put("nodeType", rootTreeType);
			treeNode.setIcon((iconHasContext ? this.getContextPath() : "")
					+ treeRootIcon);
			paramMap.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
			treeNode.setParams(paramMap);
			treeNodes.add(treeNode);
		}
		return treeNodes;
	}

	
	
	// 构造树节点
	public CommonTreeNode generateRptNode(RptMgrReportInfo rptInfo,
			String extType, boolean iconHasContext, String resNo) {
		if (rptInfo == null) {
			return null;
		}
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId(rptTreeNodePre + rptInfo.getRptId());
		treeNode.setText(rptInfo.getRptNm());
		treeNode.setTitle(rptInfo.getRptDesc() == null ? rptInfo.getRptNm() : rptInfo.getRptDesc());
		if (rptInfo.getRptType().equals("01"))
			treeNode.setIcon((iconHasContext ? this.getContextPath() : "")
					+ treeNodeIcon1);
		else
			treeNode.setIcon((iconHasContext ? this.getContextPath() : "")
					+ treeNodeIcon2);
		if (GlobalConstants4frame.TREE_ROOT_NO.equals(rptInfo.getCatalogId())) {
			treeNode.setUpId(rptInfo.getCatalogId());
		} else if (GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID.equals(rptInfo
				.getCatalogId())) {
			treeNode.setUpId(rptInfo.getDefUser()+"-"+GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
		} else {
			treeNode.setUpId(folderTreeNodePre
					+ (rptInfo.getCatalogId() != null ? rptInfo.getCatalogId()
							: ""));
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("extType", rptInfo.getExtType());
		paramMap.put("rptType", rptInfo.getRptType());
		paramMap.put("isCabin", rptInfo.getIsCabin());
		paramMap.put("realId", rptInfo.getRptId());
		if (!StringUtils.isEmpty(resNo)) {
			paramMap.put("id", rptInfo.getRptId());
			paramMap.put("resDefNo", resNo);
		}
		paramMap.put("nodeType", rptTreeType);
		paramMap.put("cfgId", rptInfo.getCfgId());
		paramMap.put("isLeaf", "true");
		treeNode.setParams(paramMap);
		return treeNode;
	}

	
	private List<CommonTreeNode> generateFolderNodes(
			List<RptMgrReportCatalog> catalogs, String extType,
			boolean iconHasContext) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if (catalogs != null) {
			for (int i = 0; i < catalogs.size(); i++) {
				RptMgrReportCatalog cTmp = catalogs.get(i);
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(folderTreeNodePre + cTmp.getCatalogId());
				treeNode.setText(cTmp.getCatalogNm());
				treeNode.setTitle(cTmp.getCatalogNm());
				treeNode.setIcon((iconHasContext ? this.getContextPath() : "")
						+ GlobalConstants4frame.LOGIC_MODULE_ICON);
				if (GlobalConstants4frame.TREE_ROOT_NO.equals(cTmp.getUpCatalogId())) {
					treeNode.setUpId(cTmp.getUpCatalogId());
				} else {
					treeNode.setUpId(folderTreeNodePre + cTmp.getUpCatalogId());
				}
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("extType", cTmp.getExtType());
				paramMap.put("realId", cTmp.getCatalogId());
				paramMap.put("nodeType", folderTreeType);
				treeNode.setParams(paramMap);
				nodes.add(treeNode);
			}
		}
		return nodes;
	}

	// 构造目录节点
	private List<CommonTreeNode> generateFolderNodes(
			List<RptMgrReportCatalog> catalogs, String extType,
			List<CommonTreeNode> nodesTmp, boolean iconHasContext) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if (catalogs != null) {
			for (int i = 0; i < catalogs.size(); i++) {
				RptMgrReportCatalog cTmp = catalogs.get(i);
				if (checkFolder(cTmp, nodesTmp)) {
					CommonTreeNode treeNode = new CommonTreeNode();
					treeNode.setId(folderTreeNodePre + cTmp.getCatalogId());
					treeNode.setText(cTmp.getCatalogNm());
					treeNode.setTitle(cTmp.getCatalogNm());
					treeNode.setIcon((iconHasContext ? this.getContextPath()
							: "") + GlobalConstants4frame.LOGIC_MODULE_ICON);
					if (GlobalConstants4frame.TREE_ROOT_NO.equals(cTmp
							.getUpCatalogId())) {
						treeNode.setUpId(cTmp.getUpCatalogId());
					} else {
						treeNode.setUpId(folderTreeNodePre
								+ cTmp.getUpCatalogId());
					}
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("extType", cTmp.getExtType());
					paramMap.put("realId", cTmp.getCatalogId());
					paramMap.put("nodeType", folderTreeType);
					paramMap.put("defUser", cTmp.getDefUser());
					treeNode.setParams(paramMap);
					nodes.add(treeNode);
				}

			}
		}
		return nodes;
	}

	private boolean checkFolder(RptMgrReportCatalog catalog,
			List<CommonTreeNode> nodesTmp) {
		if (nodesTmp != null & nodesTmp.size() > 0) {
			for (CommonTreeNode tree : nodesTmp) {
				if (tree.getId().equals(
						folderTreeNodePre + catalog.getCatalogId())) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 平台报表 - 业务条线
	 * 
	 * @return
	 */	
	public List<CommonTreeNode> getLineNoTree(String basePath) {
		Map<String, Object> conditions = Maps.newHashMap();
		conditions.put("itemSts", "Y");
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		
		String jql = "select line from RptMgrBusiLine line ";
		List<RptMgrBusiLine> infos = this.baseDAO.findWithIndexParam(jql);
		
		CommonTreeNode defaultnode = new CommonTreeNode();
		defaultnode.setId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
	    defaultnode.setText("");
	    defaultnode.getParams().put("type", "root");
	    defaultnode.setIsParent(true);
	    defaultnode.setIcon(basePath + GlobalConstants4plugin.TREE_ICON_ROOT);
	    nodes.add(defaultnode);
	    if (infos != null && infos.size() > 0) {
	    		for (RptMgrBusiLine info : infos) {
	    			CommonTreeNode node = new CommonTreeNode();
	    			node.setId(info.getLineId());
	    			node.setText(info.getLineNm());
	    			node.setUpId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
	    			node.setIcon(basePath + GlobalConstants4plugin.REPORT_LABELOBJ_ICON);
	    			node.getParams().put("type", "common");
	    			nodes.add(node);
	    		}
	    	}
	    return nodes;
	}
	
	/**
	 * 平台报表 - 开发厂商
	 * 
	 * @return
	 */	
	public List<CommonTreeNode> getDeveloperTree(String basePath) {
		Map<String, Object> conditions = Maps.newHashMap();
		conditions.put("itemSts", "Y");
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		
		String jql = "select param from BioneParamInfo param where param.paramTypeNo = ?0";
		List<BioneParamInfo> infos = this.baseDAO.findWithIndexParam(jql,"Developer");
		
		CommonTreeNode defaultnode = new CommonTreeNode();
		defaultnode.setId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
	    defaultnode.setText("");
	    defaultnode.getParams().put("type", "root");
	    defaultnode.setIsParent(true);
	    defaultnode.setIcon(basePath + GlobalConstants4plugin.TREE_ICON_ROOT);
	    nodes.add(defaultnode);
	    if (infos != null && infos.size() > 0) {
	    		for (BioneParamInfo info : infos) {
	    			CommonTreeNode node = new CommonTreeNode();
	    			node.setId(info.getParamValue());
	    			node.setText(info.getParamName());
	    			node.setUpId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
	    			node.setIcon(basePath + GlobalConstants4plugin.REPORT_LABELOBJ_ICON);
	    			node.getParams().put("type", "common");
	    			nodes.add(node);
	    		}
	    	}
	    return nodes;
	}
	/***** Private Method end *****/
	
	public List<String> initAuthRptIds(List<BioneAuthObjResRel> rels){
        Set<String> authRptIds = new HashSet<String>();
        Set<String> catalogIds = new HashSet<String>();
        if(rels!=null&& rels.size()>0){
            for(BioneAuthObjResRel rel :rels){
                if(StringUtils.isNotBlank(rel.getResType())){
                    if(rel.getResType().equals(GlobalConstants4frame.RES_TYPE_ROOT)){
                        String jql ="select rpt.rptId from RptMgrReportInfo rpt where 1=1";
                        List<String> rptIds = this.baseDAO.findWithIndexParam(jql);
                        return rptIds;
                    }
                    else if(rel.getResType().equals(GlobalConstants4frame.RES_TYPE_CATA)){
                        catalogIds.add(rel.getId().getResId());
                    }
                    else if(rel.getResType().equals(GlobalConstants4frame.RES_TYPE_NODE)){
                        authRptIds.add(rel.getId().getResId());
                    }
                }
                else{
                    authRptIds.add(rel.getId().getResId());
                }
            }
        }
        
        if(catalogIds != null && catalogIds.size()>0){
            List<String> allcatalogIds = new ArrayList<String>();
            getAllCatalog(new ArrayList<String>(catalogIds), allcatalogIds);
            if(allcatalogIds != null && allcatalogIds.size()>0){
                String jql ="select rpt.rptId from RptMgrReportInfo rpt where 1=1 and rpt.catalogId in ?0";
                List<String> rptIds = this.baseDAO.findWithIndexParam(jql,allcatalogIds);
                if(rptIds != null && rptIds.size()>0){
                    authRptIds.addAll(rptIds);
                }
            }
        }
        return new ArrayList<String>(authRptIds);
    }
	private void getAllCatalog(List<String> catalogIds,List<String> allcatalogIds){
        allcatalogIds.addAll(catalogIds);
        String jql = "select cata.catalogId from RptMgrReportCatalog cata where cata.upCatalogId in ?0";
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
}
