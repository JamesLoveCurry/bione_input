package com.yusys.bione.plugin.rptshare.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.message.service.MsgNoticeLogBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.service.RptMgrBS;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrInfoVO;
import com.yusys.bione.plugin.rptshare.entity.RptMgrShareInfo;
import com.yusys.bione.plugin.rptshare.web.vo.RptMgrRptShareVO;
import com.yusys.bione.plugin.rptshare.web.vo.RptShareMineVO;

@Service
@Transactional(readOnly = true)
public class RptShareBS extends BaseBS<Object> {
	
	@Autowired
	private ExcelBS excelBS;
	
	@Autowired
	private RptMgrBS rptMgrBS;
	
	@Autowired
	private RptMgrInfoMybatisDao rptMgrInfoDao;
	
	@Autowired
	private RptMgrInfoMybatisDao reportInfoDAO;
	
	@Autowired
	private MsgNoticeLogBS msgNoticeLogBS;
	
	private String rptTreeNodePre = "r";
	
	private String folderTreeType = "02";
	private String rptTreeType = "03";
	private String userTreeType = "04";
	private String nullfolderTreeType = "05";
	
	// 树报表叶子节点图标
	private String treeNodeIcon1 = "/images/classics/icons/application_view_icons.png";
	private String treeNodeIcon2 = "/images/classics/icons/application_view_list.png";
	private String userRootIcon = "/images/classics/icons/user.png";
	
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getRptUserList(Pager pager, String userId,String rptNm,String auth) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(rptNm!=null&&!rptNm.equals("")){
			params.put("rptNm", "%"+rptNm+"%");
		}
		List<String> authRptIds = new ArrayList<String>();
		authRptIds.addAll(this.getShareRptId(GlobalConstants4plugin.RPT_EXT_TYPE_BANK, GlobalConstants4plugin.RPT_TYPE_DESIGN,userId));
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && StringUtils.isNotBlank(auth)){
			authRptIds = ListUtils.intersection(authRptIds, BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW"));
		}
		params.put("rptIds", ReBuildParam.splitLists(authRptIds));
		params.put("defUser", userId);
		params.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_USER);
		PageHelper.startPage(pager);
		PageMyBatis<RptMgrInfoVO> page = (PageMyBatis<RptMgrInfoVO>) this.rptMgrInfoDao
				.getReportInfoByCatalogId(params);
		Map<String, Object> shareMap = Maps.newHashMap();
		shareMap.put("Rows", page.getResult());
		shareMap.put("Total", page.getTotalCount());
		return shareMap;

	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getRptUserList(Pager pager, String userId, String catalogId,String rptNm,String auth) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(rptNm!=null&&!rptNm.equals("")){
			params.put("rptNm", "%"+rptNm+"%");
		}
		if (catalogId != null && !catalogId.equals("")) {
			List<String> catalogIds = new ArrayList<String>();
			catalogIds.add(catalogId);
			getAllCatalogId(catalogIds, catalogId);
			params.put("catalogIds", catalogIds);
		}
		List<String> authRptIds = new ArrayList<String>();
		authRptIds.addAll(this.getShareRptId(GlobalConstants4plugin.RPT_EXT_TYPE_BANK, GlobalConstants4plugin.RPT_TYPE_DESIGN,userId));
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && StringUtils.isNotBlank(auth)){
			authRptIds = ListUtils.intersection(authRptIds, BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW"));
		}
		params.put("rptIds", ReBuildParam.splitLists(authRptIds));
		params.put("defUser", userId);
		params.put("defSrc", GlobalConstants4plugin.RPT_DEF_SRC_USER);
		PageHelper.startPage(pager);
		PageMyBatis<RptMgrInfoVO> page = (PageMyBatis<RptMgrInfoVO>) this.rptMgrInfoDao
				.getReportInfoByCatalogId(params);
		Map<String, Object> shareMap = Maps.newHashMap();
		shareMap.put("Rows", page.getResult());
		shareMap.put("Total", page.getTotalCount());
		return shareMap;

	}
	
	public List<CommonTreeNode> userlist(String searchNm, String path) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		List<BioneUserInfo> infos = new ArrayList<BioneUserInfo>();
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		BioneLogicSysInfo sys = this.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", logicSysNo);
		if(sys!=null){
			String jql = "select user from BioneUserInfo user where user.userId in (select adm.id.userId from BioneAdminUserInfo adm where adm.id.logicSysId = ?0)";
			infos = this.baseDAO.findWithIndexParam(jql, sys.getLogicSysId()); 
		}
		String jql = "select user from BioneUserInfo user where user.logicSysNo = ?0 and user.userSts = ?1 ";
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and user.userName like ?2 ";
			List<BioneUserInfo> users  = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
					.getCurrentUserInfo().getCurrentLogicSysNo(),
					GlobalConstants4plugin.STS_ON, "%" + searchNm + "%");
			infos.addAll(users);
		} else {
			List<BioneUserInfo> users = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
					.getCurrentUserInfo().getCurrentLogicSysNo(),
					GlobalConstants4plugin.STS_ON);
			infos.addAll(users);
		}
		if (infos != null && infos.size() > 0) {
			for (BioneUserInfo info : infos) {
				if(info.getUserId().equals(BioneSecurityUtils.getCurrentUserId())){
					continue;
				}
				CommonTreeNode node = new CommonTreeNode();
				node.setId(info.getUserId());
				node.setText(info.getUserName());
				node.setIsParent(false);
				node.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
						+ "/user.png");
				nodes.add(node);
			}
		}
		return nodes;
	}

	public List<CommonTreeNode> userllist(String searchNm, String path) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		List<Object[]> infos = new ArrayList<Object[]>();
		String jql = "select user.userId,user.userName,max(share.createTime) from BioneUserInfo user,RptMgrShareInfo share where share.srcUserId = ?0 and  user.userSts = ?1 ";
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and user.userName like ?2 and share.objUserId = user.userId group by user.userId,user.userName order by max(share.createTime) desc ";
			infos = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
					.getCurrentUserId(),
					GlobalConstants4plugin.STS_ON, "%" + searchNm + "%");
		} else {
			jql += "  and share.objUserId = user.userId group by user.userId,user.userName order by max(share.createTime) desc ";
			infos = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
					.getCurrentUserId(), BioneSecurityUtils
					.getCurrentUserInfo().getCurrentLogicSysNo(),
					GlobalConstants4plugin.STS_ON);
		}
		if (infos != null && infos.size() > 0) {
			for (Object[] info : infos) {
				if(info[0].toString().equals(BioneSecurityUtils.getCurrentUserId())){
					continue;
				}
				CommonTreeNode node = new CommonTreeNode();
				node.setId(info[0].toString());
				node.setText(info[1].toString());
				node.setIsParent(false);
				node.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
						+ "/user.png");
				nodes.add(node);
			}
		}
		return nodes;
	}

	public List<CommonTreeNode> usermlist(String searchNm, String path) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		List<Object[]> infos = new ArrayList<Object[]>();
		String jql = "select user.userId,user.userName,count(share.objId) from BioneUserInfo user,RptMgrShareInfo share where share.srcUserId = ?0  and user.userSts = ?1 ";
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and user.userName like ?2 and share.objUserId = user.userId group by user.userId,user.userName order by count(share.objId) desc ";
			infos = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
					.getCurrentUserId(), 
					GlobalConstants4plugin.STS_ON, "%" + searchNm + "%");
		} else {
			jql += "  and share.objUserId = user.userId group by user.userId,user.userName order by count(share.objId) desc ";
			infos = this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
					.getCurrentUserId(), BioneSecurityUtils
					.getCurrentUserInfo().getCurrentLogicSysNo(),
					GlobalConstants4plugin.STS_ON);
		}
		if (infos != null && infos.size() > 0) {
			for (Object[] info : infos) {
				if(info[0].toString().equals(BioneSecurityUtils.getCurrentUserId())){
					continue;
				}
				CommonTreeNode node = new CommonTreeNode();
				node.setId(info[0].toString());
				node.setText(info[1].toString());
				node.setIsParent(false);
				node.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
						+ "/user.png");
				nodes.add(node);
			}
		}
		return nodes;
	}

	@Transactional(readOnly = false)
	public void saveshare(String ids, String remark, String id, String name,String objType) {
		String idArray[] = StringUtils.split(ids, ",");
		List<RptMgrShareInfo> shares = Lists.newArrayList();
		for (int i = 0; i < idArray.length; i++) {
			RptMgrShareInfo share = new RptMgrShareInfo();
			share.setShareId(RandomUtils.uuid2());
			share.setObjId(id);
			share.setObjName(name);
			share.setObjType(objType);
			share.setRemark(remark);
			share.setCreateTime(new Timestamp(System.currentTimeMillis()));
			share.setObjUserId(idArray[i]);
			share.setSrcUserId(BioneSecurityUtils.getCurrentUserId());
			share.setShareSts(GlobalConstants4plugin.STS_ON);
			shares.add(share);
		}
		if(idArray.length > 0){
			String msgDetail = "\""+name +"\"报表分享<br>" + remark;
			msgNoticeLogBS.savePlatMsgs(idArray, "报表分享", msgDetail, GlobalConstants4plugin.MSG_TYPE_SHARE);
		}
		excelBS.saveEntityJdbcBatch(shares);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> minelist(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql =  "        SELECT                                                        "
				+ "            sare.share_id,                                            "
				+ "            sare.obj_id,                                            "
				+ "            sare.obj_name,                                   		"
				+ "            sare.obj_type,                                            "
				+ "            usr.user_id,                                              "
				+ "            usr.user_name,                                            "
				+ "            sare.share_sts,                                           "
				+ "            sare.create_time                                          "
				+ "        FROM                                                          "
				+ "            RPT_MGR_SHARE_INFO sare ,                                 "
				+ "            Bione_User_Info usr                                      "
				+ "        WHERE                                                         "
				+ "            1=1                            "
				+ "        AND usr.user_sts = :userSts                                       "
				+ "        AND sare.src_user_id = :userId     							 "
				+ "        AND sare.obj_user_id = usr.user_id                            ";
		if (!conditionMap.get("jql").equals("")) {
			sql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			sql += "order by sare." + orderBy + " " + orderType;
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
//		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		values.put("userId", BioneSecurityUtils.getCurrentUserId());
		values.put("userSts", GlobalConstants4plugin.STS_ON);
		SearchResult<Object[]> results = this.baseDAO
				.findPageWithNameParamByNativeSQL(firstResult, pageSize, sql,
						values);

		List<RptShareMineVO> mines = new ArrayList<RptShareMineVO>();
		if (results.getResult() != null && results.getResult().size() > 0) {
			for (Object[] obj : results.getResult()) {
				RptShareMineVO mine = new RptShareMineVO(
						obj[0] != null ? obj[0].toString() : "",
						obj[1] != null ? obj[1].toString() : "",
						obj[2] != null ? obj[2].toString() : "",
						obj[3] != null ? obj[3].toString() : "",
						obj[4] != null ? obj[4].toString() : "",
						obj[5] != null ? obj[5].toString() : "",
						obj[7] != null ? (Timestamp)obj[7] : null,
						obj[6] != null ? obj[6].toString() : "");
				mines.add(mine);
			}
		}
		map.put("Rows", mines);
		map.put("Total", results.getTotalCount());
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> otherlist(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "        SELECT                                                        "
				+ "            sare.share_id,                                            "
				+ "            sare.obj_id,                                            "
				+ "            sare.obj_name ,                                   "
				+ "            sare.obj_type,                                            "
				+ "            usr.user_name,                                            "
				+ "            sare.share_sts,                                           "
				+ "            sare.create_time                                          "
				+ "        FROM                                                          "
				+ "            RPT_MGR_SHARE_INFO sare ,                                 "
				+ "            Bione_User_Info usr                                      "
				+ "        WHERE  1=1                                                       "
				+ "        AND usr.user_sts = :userSts                                       "
				+ "        AND sare.obj_user_id = :userId     							 "
				+ "        AND sare.src_user_id = usr.user_id                            ";
		if (!conditionMap.get("jql").equals("")) {
			sql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			sql += "order by sare." + orderBy + " " + orderType;
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		//values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		values.put("userId", BioneSecurityUtils.getCurrentUserId());
		values.put("userSts", GlobalConstants4plugin.STS_ON);
		SearchResult<Object[]> results = this.baseDAO
				.findPageWithNameParamByNativeSQL(firstResult, pageSize, sql,
						values);

		List<RptShareMineVO> mines = new ArrayList<RptShareMineVO>();
		if (results.getResult() != null && results.getResult().size() > 0) {
			for (Object[] obj : results.getResult()) {
				RptShareMineVO mine = new RptShareMineVO(
						obj[0] != null ? obj[0].toString() : "",
						obj[1] != null ? obj[1].toString() : "",
						obj[2] != null ? obj[2].toString() : "",
						obj[3] != null ? obj[3].toString() : "",
						obj[4] != null ? obj[4].toString() : "",
						obj[6] != null ? (Timestamp)obj[6] : null,
						obj[5] != null ? obj[5].toString() : "");
				mines.add(mine);
			}
		}
		map.put("Rows", mines);
		map.put("Total", results.getTotalCount());
		return map;
	}
	
	@Transactional(readOnly = false)
	public void changeShareStsAndSaveMsg(String shareId, String sts, String userId ,String rptName) {
		if (StringUtils.isNotEmpty(shareId) && StringUtils.isNotEmpty(sts)) {
			String jql = "update RptMgrShareInfo user set shareSts = ?0 where shareId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, sts, shareId);
		}
		//记录分享提醒消息
		if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(sts)){
			String msgDetail = "";
			String msgTitle = "";
			if("1".equals(sts)){
				msgTitle = "报表分享";
				msgDetail = "\""+rptName +"\"报表分享<br>" + "";
			}else{
				msgTitle = "取消报表分享";
				msgDetail = "\""+rptName +"\"取消报表分享<br>" + "";
			}
			msgNoticeLogBS.savePlatMsgs(StringUtils.split(userId, ","), msgTitle, msgDetail, GlobalConstants4plugin.MSG_TYPE_SHARE);
		}
	}
	
	public List<BioneUserInfo> getUserInfo(){
		List<BioneUserInfo> infos = new ArrayList<BioneUserInfo>();
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		BioneLogicSysInfo sys = this.getEntityByProperty(BioneLogicSysInfo.class, "logicSysNo", logicSysNo);
		if(sys!=null){
			String jql = "select user from BioneUserInfo user where user.userId in (select adm.id.userId from BioneAdminUserInfo adm where adm.id.logicSysId = ?0) and user.userId != ?1";
			infos = this.baseDAO.findWithIndexParam(jql, sys.getLogicSysId(),BioneSecurityUtils.getCurrentUserId()); 
		}
		String jql = "select user from BioneUserInfo user where user.userSts = ?0 and user.logicSysNo=?1 and user.userId != ?2";
		List<BioneUserInfo> users = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.STS_ON,BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),BioneSecurityUtils.getCurrentUserId());
		infos.addAll(users);
		return infos;
	}
	
	@Transactional(readOnly = false)
	public void changeShareSts(String objId,String objType,String sts){
		String jql = "update RptMgrShareInfo sare set sare.shareSts=?0 where sare.objId = ?1 and sare.objType = ?2";
		this.baseDAO.batchExecuteWithIndexParam(jql, sts,objId,objType);
	}
	
	public List<CommonTreeNode> getRptShareTreeSync(String extType,
			String rptType, String searchNm) {
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		// 如果用户为空 或者 权限类型为空，直接返回
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 加上权限过滤
		if (!StringUtils.isEmpty(searchNm)) {
			paramMap.put("rptNm", "%" + searchNm + "%");
		}
		if (rptType != null && !StringUtils.isEmpty(rptType)) {
			paramMap.put("rptType", rptType);
		}
		paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		if (extType != null && !extType.equals("")) {
			if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
					|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
				paramMap.put("extType", extType);
			}
		}
		paramMap.put("defSrc", "03");
		List<RptMgrReportInfo> rpts = new ArrayList<RptMgrReportInfo>();
		List<String> srptIds = getShareRptId(extType, rptType);
		paramMap.put("rptIds", srptIds);
		List<RptMgrReportInfo> nullrpts = Lists.newArrayList();
		if(null != srptIds && srptIds.size() > 0){
			rpts = this.reportInfoDAO.getRptsByParams(paramMap);
			String hql = "select rpt from RptMgrReportInfo rpt where rpt.extType = :extType  and rptType = :rptType and defSrc = :defSrc and rptSts = :rptSts and catalogId = :catalogId and rptId in :rptIds";
			paramMap.put("catalogId", GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
			if (!StringUtils.isEmpty(searchNm)) {
				hql += " and rptNm like :rptNm ";
			}
			nullrpts = this.baseDAO.findWithNameParm(hql,paramMap);
		}

		if (nullrpts != null && nullrpts.size() > 0) {
			rpts.addAll(nullrpts);
			List<String> userIds = new ArrayList<String>();
			for(RptMgrReportInfo nullrpt : nullrpts){
				if(!userIds.contains(nullrpt.getDefUser())){
					userIds.add(nullrpt.getDefUser());
				}
			}
			if(userIds != null && userIds.size()>0){
				for(String userId : userIds){
					CommonTreeNode backCtlNode = new CommonTreeNode();
					backCtlNode.setId(userId+"-"+GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
					backCtlNode.setText("撤回目录");
					backCtlNode.setIcon(this.getContextPath() + GlobalConstants4frame.LOGIC_MODULE_ICON);
					backCtlNode.setUpId(userId);
					Map<String, String> backMap = new HashMap<String, String>();
					backMap.put("realId", userId+"-"+GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
					backMap.put("nodeType", nullfolderTreeType);
					backCtlNode.setParams(backMap);
					returnNodes.add(backCtlNode);
				}
			}
			
		}
		
		if (rpts != null && rpts.size() > 0) {
			List<String> catalogIds = new ArrayList<String>();
			List<String> userIds = new ArrayList<String>();
			for (int i = 0; i < rpts.size(); i++) {
				RptMgrReportInfo rptTmp = rpts.get(i);
				if (StringUtils.isEmpty(rptTmp.getCatalogId())) {
					continue;
				}
				if (!catalogIds.contains(rptTmp.getCatalogId())) {
					catalogIds.add(rptTmp.getCatalogId());
				}
				if (!userIds.contains(rptTmp.getDefUser())) {
					userIds.add(rptTmp.getDefUser());
				}
				// 构造树节点
				CommonTreeNode nodeTmp = this.rptMgrBS.generateRptNode(rptTmp, extType, true,
						null);
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			String jql = "select user from BioneUserInfo user where user.userId in ?0 and user.logicSysNo in ?1 and user.userSts = ?2";
			List<String> logicSysNos = new ArrayList<String>();
			logicSysNos.add(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			logicSysNos.add("BIONE");
			
			if(userIds != null && userIds.size() >0){
				List<BioneUserInfo> users = this.baseDAO.findWithIndexParam(jql, userIds,logicSysNos,GlobalConstants4plugin.STS_ON);
				returnNodes.addAll(this.generateRootShareNode(users));
			}
			
			// 迭代查询需要展现的目录结构
			List<CommonTreeNode> ctlNodes = this.rptMgrBS.getFolderNodes(catalogIds, extType, "03",null, true);
			if(null != ctlNodes && ctlNodes.size() > 0){
				for(CommonTreeNode ctl : ctlNodes){
					if("0".equals(ctl.getUpId())){
						ctl.setUpId(ctl.getParams().get("defUser"));
						returnNodes.add(ctl);
					}else{
						returnNodes.add(ctl);
					}
				}
			}
		}
		Map<String, Object> values = new HashMap<String, Object>();
		String jql = "select distinct new com.yusys.bione.plugin.rptshare.web.vo.RptMgrRptShareVO(rpt,share.srcUserId) from RptMgrReportInfo rpt ,RptMgrShareInfo share where share.objUserId = :userId and share.shareSts = :shareSts and share.objType =:objType and rpt.rptId = share.objId ";
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and rpt.rptNm like :searchNm";
			values.put("searchNm", "%" + searchNm + "%");
		}
		values.put("userId", BioneSecurityUtils.getCurrentUserId());
		values.put("shareSts", GlobalConstants4plugin.STS_ON);
		values.put("objType", "1");
		List<RptMgrRptShareVO> vos = this.baseDAO.findWithNameParm(jql, values);
		if (vos != null && vos.size() > 0) {
			for (RptMgrRptShareVO vo : vos) {
				CommonTreeNode nodeTmp = generateShareRptNode(vo, extType, null);
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
		}
		return returnNodes;
	}

	public List<CommonTreeNode> getAuthRptShareTreeSync(String extType,
			String rptType, String searchNm) {
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		// 如果用户为空 或者 权限类型为空，直接返回
		returnNodes.addAll(generateRootShareNode());
		// 获取全部报表
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 加上权限过滤
		String hql = "select rpt from RptMgrReportInfo rpt where rpt.extType = :extType and defUser = :defUser  and defSrc = :defSrc and rptSts = :rptSts and catalogId = :catalogId ";
		
		if (!StringUtils.isEmpty(searchNm)) {
			hql += " and rptNm like :rptNm";
			paramMap.put("rptNm", "%" + searchNm + "%");
		}
		if (rptType != null && !StringUtils.isEmpty(rptType)) {
			hql += " and rptType = :rptType";
			paramMap.put("rptType", rptType);
		}
		paramMap.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		if (extType != null && !extType.equals("")) {
			if (GlobalConstants4plugin.RPT_EXT_TYPE_FRS.equals(extType)
					|| GlobalConstants4plugin.RPT_EXT_TYPE_BANK.equals(extType)) {
				paramMap.put("extType", extType);
			}
		}
		paramMap.put("defSrc", "03");
		paramMap.put("defUser", BioneSecurityUtils.getCurrentUserId());
		List<RptMgrReportInfo> rpts = new ArrayList<RptMgrReportInfo>();
		rpts = this.reportInfoDAO.getRptsByParams(paramMap);
		paramMap.put("catalogId", GlobalConstants4plugin.BACK_RPT_CTL_NODE_ID);
		List<RptMgrReportInfo> nullrpts = this.baseDAO.findWithNameParm(hql,
				paramMap);
		if (nullrpts != null && nullrpts.size() > 0) {
			rpts.addAll(nullrpts);
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
				CommonTreeNode nodeTmp = this.rptMgrBS.generateRptNode(rptTmp, extType, true,
						null);
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
			// if (!StringUtils.isEmpty(searchNm) ||
			// !StringUtils.isEmpty(rptNum)) {
			// 迭代查询需要展现的目录结构
			returnNodes.addAll(this.rptMgrBS.getFolderNodes(catalogIds, extType, "03",
					null, true));
			// }
		}
		Map<String, Object> values = new HashMap<String, Object>();
		String jql = "select distinct new com.yusys.bione.plugin.rptshare.web.vo.RptMgrRptShareVO(rpt,share.srcUserId) from RptMgrReportInfo rpt ,RptMgrShareInfo share where share.objUserId = :userId and share.shareSts = :shareSts and share.objType =:objType and rpt.rptId = share.objId ";
		if (StringUtils.isNotBlank(searchNm)) {
			jql += " and rpt.rptNm like :searchNm";
			values.put("searchNm", "%" + searchNm + "%");
		}
		values.put("userId", BioneSecurityUtils.getCurrentUserId());
		values.put("shareSts", GlobalConstants4plugin.STS_ON);
		values.put("objType", "1");
		List<RptMgrRptShareVO> vos = this.baseDAO.findWithNameParm(jql, values);
		if (vos != null && vos.size() > 0) {
			for (RptMgrRptShareVO vo : vos) {
				CommonTreeNode nodeTmp = generateShareRptNode(vo, extType, null);
				if (nodeTmp != null) {
					returnNodes.add(nodeTmp);
				}
			}
		}
		return returnNodes;
	}
	
	// 构造树节点
	private CommonTreeNode generateShareRptNode(RptMgrRptShareVO rptInfo,
			String extType, String resNo) {
		if (rptInfo == null) {
			return null;
		}
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId(rptTreeNodePre + rptInfo.getRptId());
		treeNode.setText(rptInfo.getRptNm());
		if (rptInfo.getRptType().equals("01"))
			treeNode.setIcon(this.getContextPath() + treeNodeIcon1);
		else
			treeNode.setIcon(this.getContextPath() + treeNodeIcon2);
		treeNode.setUpId(rptInfo.getUserId());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("extType", rptInfo.getExtType());
		paramMap.put("rptType", rptInfo.getRptType());
		paramMap.put("realId", rptInfo.getRptId());
		if (!StringUtils.isEmpty(resNo)) {
			paramMap.put("id", rptInfo.getRptId());
			paramMap.put("resDefNo", resNo);
		}
		paramMap.put("nodeType", rptTreeType);
		paramMap.put("cfgId", rptInfo.getCfgId());
		treeNode.setParams(paramMap);
		return treeNode;
	}
	
	// 构造根节点
	private List<CommonTreeNode> generateRootShareNode() {
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId("0");
		treeNode.setText(BioneSecurityUtils.getCurrentUserInfo().getUserName());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("nodeType", folderTreeType);
		treeNode.setIcon(this.getContextPath() + userRootIcon);
		paramMap.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
		treeNode.setParams(paramMap);
		treeNodes.add(treeNode);
		String jql = "select distinct user from BioneUserInfo user,RptMgrShareInfo share where share.objUserId = ?0 and share.shareSts = ?1 and user.userSts = ?2 and share.srcUserId = user.userId ";
		List<BioneUserInfo> users = this.baseDAO.findWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserId(), GlobalConstants4plugin.STS_ON,
				GlobalConstants4plugin.STS_ON);
		if (users != null && users.size() > 0) {
			for (BioneUserInfo user : users) {
				CommonTreeNode utreeNode = new CommonTreeNode();
				utreeNode.setId(user.getUserId());
				utreeNode.setText(user.getUserName());
				utreeNode.setUpId("-1");
				Map<String, String> uparamMap = new HashMap<String, String>();
				paramMap.put("nodeType", folderTreeType);
				utreeNode.setIcon(this.getContextPath() + userRootIcon);
				uparamMap.put("realId", user.getUserId());
				utreeNode.setParams(paramMap);
				treeNodes.add(utreeNode);
			}
		}
		return treeNodes;
	}
		
	private List<CommonTreeNode> generateRootShareNode(List<BioneUserInfo> infos) {
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		if(infos != null && infos.size() > 0){
			for(BioneUserInfo info : infos){
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(info.getUserId());
				treeNode.setText(info.getUserName());
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("nodeType", userTreeType);
				treeNode.setIcon(this.getContextPath() + userRootIcon);
				paramMap.put("realId", info.getUserId());
				treeNode.setParams(paramMap);
				treeNodes.add(treeNode);
			}
		}
		return treeNodes;
	}
	
	// 获取上下文路径
	private String getContextPath() {
		return GlobalConstants4frame.APP_CONTEXT_PATH;
	}
		
	public List<String> getShareRptId(String extType, String rptType) {
		String sql = "SELECT" + " info.rpt_id,info.rpt_nm " + "FROM"
				+ " RPT_MGR_REPORT_INFO info "
				+ " WHERE info.def_Src = ?0 and ext_type = ?1 and rpt_Type = ?2 "
				+ " and EXISTS" + "(" + "SELECT" + " 1 " + "FROM"
				+ "    RPT_MGR_SHARE_INFO sh " + " WHERE "
				+ "    info.rpt_id = sh.obj_id " + " AND sh.obj_type = ?3 "
				+ "AND sh.share_sts =?4)";

		List<Object[]> objs = this.baseDAO.findByNativeSQLWithIndexParam(sql,
				GlobalConstants4plugin.RPT_DEF_SRC_USER, extType, rptType,"1","1");
		List<String> rptIds = new ArrayList<String>();
		if(objs != null && objs.size() > 0){
			for(Object[] obj : objs){
				if(obj.length>0){
					rptIds.add(obj[0] != null ? obj[0].toString():"");
				}
			}
		}
		return rptIds;
	}
	
	public List<String> getShareRptId(String extType, String rptType,String userId) {
		String sql = "SELECT" + " info.rpt_id,info.rpt_nm " + "FROM"
				+ " RPT_MGR_REPORT_INFO info "
				+ " WHERE info.def_Src = ?0 and ext_type = ?1 and rpt_Type = ?2 "
				+ " and info.def_User = ?5 and EXISTS" + "(" + "SELECT" + " 1 " + "FROM"
				+ "    RPT_MGR_SHARE_INFO sh " + " WHERE "
				+ "    info.rpt_id = sh.obj_id " + " AND sh.obj_type = ?3 "
				+ "AND sh.share_sts =?4)";

		List<Object[]> objs = this.baseDAO.findByNativeSQLWithIndexParam(sql,
				GlobalConstants4plugin.RPT_DEF_SRC_USER, extType, rptType,"1","1",userId);
		List<String> rptIds = new ArrayList<String>();
		if(objs != null && objs.size() > 0){
			for(Object[] obj : objs){
				if(obj.length>0){
					rptIds.add(obj[0] != null ? obj[0].toString():"");
				}
			}
		}
		return rptIds;
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
}


