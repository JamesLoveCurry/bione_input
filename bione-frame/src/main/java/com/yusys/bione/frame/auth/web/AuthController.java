package com.yusys.bione.frame.auth.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRelPK;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthObjBS;
import com.yusys.bione.frame.auth.web.vo.AuthTreeNode;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IAuthObject;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.frame.syslog.entity.BioneLogAuthDetail;
import com.yusys.bione.frame.syslog.entity.BioneLogAuthInfo;
import com.yusys.bione.frame.syslog.service.LogAuthInfoBS;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.*;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.PERMISSION_ALL;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.RES_PERMISSION_TYPE_OPER;

/**
 * <pre>
 * Title:?????????????????????
 * Description: ????????????????????????action
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * ????????????
 *    ???????????????:     ????????????  ????????????:     ????????????:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/auth")
public class AuthController extends BaseController {
	protected static Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthBS authBS;

	@Autowired
	private AuthObjBS authObjBS;
	
	@Autowired
	private LogAuthInfoBS logAuthBS;

	// ??????????????????????????????nodeType??????????????????????????????????????????????????????
	private String resOperNodeType = "RES_OPER";

	@RequestMapping("/manage")
	public ModelAndView manage() {
		ModelMap mm = new ModelMap();
		mm.put("isSuperUser", BioneSecurityUtils.getCurrentUserInfo().isSuperUser());
		mm.put("isManager", BioneSecurityUtils.getCurrentUserInfo().getIsManager());
		
		return new ModelAndView("/frame/auth/auth-manage", mm);
	}

	// ??????????????????-????????????
	@RequestMapping("/getAuthObjCombo.*")
	@ResponseBody
	public List<Map<String, String>> getAuthObjCombo() {
		List<Map<String, String>> authObjComboList = new ArrayList<Map<String, String>>();
		// ???objDefOrder?????? ?????????????????????
		List<BioneAuthObjDef> objs = this.authBS
				.getAllEntityList(BioneAuthObjDef.class, "objDefOrder", false);
		// ??????????????????id??????
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authStrs = this.authBS.getObjDefNoBySys(userObj.getCurrentLogicSysNo(), false);
		if (objs != null) {
			for (BioneAuthObjDef authObj : objs) {
				if (authStrs.contains(authObj.getObjDefNo())) {
					Map<String, String> objMap = new HashMap<String, String>();
					objMap.put("id", authObj.getObjDefNo());
					objMap.put("text", authObj.getObjDefName());
					authObjComboList.add(objMap);
				}
			}
		}
		return authObjComboList;
	}

	// ?????????????????????(?????????:???????????????????????????????????????)
	@RequestMapping("/getAuthObjDefTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthObjDefTree(String objDefNo, CommonTreeNode node, String searchText) {
		List<CommonTreeNode> pageShowTree = null;
		if (objDefNo != null && !"".equals(objDefNo)) {
			// ???????????????
			List<String> beanNames = this.authBS
					.findAuthObjBeanNameByType(objDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				// ?????????????????????????????????????????????
				String beanName = beanNames.get(0);
				IAuthObject authObj = SpringContextHolder.getBean(beanName);
				if (authObj != null) {
//					pageShowTree = authObj.doGetAuthObjectInfo();
					if (StringUtils.isEmpty(node.getId())) {
						node.setId("0");
					}
					if (StringUtils.isNotEmpty(searchText)) {
						pageShowTree = authObj.doGetAuthObjectInfoAsync(node, searchText);
					} else {
						pageShowTree = authObj.doGetAuthObjectInfoAsync(node);
					}
					if (pageShowTree != null) {
						for (int i = 0; i < pageShowTree.size(); i++) {
							(pageShowTree.get(i)).setIcon(this.getRequest()
									.getContextPath()
									+ "/"
									+ (pageShowTree.get(i)).getIcon());
						}
					}
				}
			}
		}
		return pageShowTree;
	}

	// ??????????????????tab

	@RequestMapping("/getAuthResDefTabs.*")
	@ResponseBody
	public Map<String, List<Map<String, String>>> getAuthResDefTabs() {
		Map<String, List<Map<String, String>>> ress = new HashMap<String, List<Map<String, String>>>();
		List<BioneAuthResDef> resDefs = this.authBS
				.getAllEntityList(BioneAuthResDef.class, "resDefOrder", false);
		// ?????????????????????????????????
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authRess = this.authBS.getResDefNoBySys(userObj
				.getCurrentLogicSysNo());
		if (resDefs != null && resDefs.size() > 0) {
			List<Map<String, String>> subList = new ArrayList<Map<String, String>>();
			for (BioneAuthResDef obj : resDefs) {
				if (authRess.contains(obj.getResDefNo())) {
					Map<String, String> resMap = new HashMap<String, String>();
					resMap.put("resDefNo", obj.getResDefNo());
					resMap.put("resName", obj.getResName());
					resMap.put("resBean", obj.getBeanName());
					/*if (AUTH_RES_DEF_ID_MENU.equals(obj.getResDefNo())) {
						// ????????????????????????????????????
						subList.add(0, resMap);
					} else {*/
						subList.add(resMap);
					//}
				}
			}
			ress.put("Data", subList);
		}
		return ress;
	}

	// ???????????????(????????? ?????? ???)
	@RequestMapping("/getAuthResDefTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthResDefTree(String resDefNo) {
		List<CommonTreeNode> pageShowTree = null;
		if (resDefNo != null && !"".equals(resDefNo)) {
			// ???????????????
			List<String> beanNames = this.authBS
					.findResObjBeanNameByType(resDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				// ?????????????????????????????????????????????
				String beanName = beanNames.get(0);
				try {
					IResObject resObj = SpringContextHolder.getBean(beanName);
					if (resObj != null) {
						pageShowTree = resObj.doGetResInfo();
					}
				} catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
					e.printStackTrace();
				}
			}
		}
		return pageShowTree;
	}

	// ?????????????????????
	@RequestMapping("/getMenuOperTree.*")
	@ResponseBody
	public List<CommonTreeNode> getMenuOperTree(String resArray) {
		List<CommonTreeNode> pageShowTree = new ArrayList<CommonTreeNode>();
		JSONArray arrayTmp = JSON.parseArray(resArray);
		if(arrayTmp == null){
			return pageShowTree;
		}
		// resNo , ??????????????????
		Map<String , AuthTreeNode> authResMap = new HashMap<String , AuthTreeNode>();
		List<String> resIdList = new ArrayList<String>();
		List<String> pList = new ArrayList<String>();
		String resDefNo = "";
		for (int i = 0; i < arrayTmp.size(); i ++) {
			AuthTreeNode nodeTmp = arrayTmp.getObject(i, AuthTreeNode.class);
			if(StringUtils.isEmpty(resDefNo)){
				resDefNo = nodeTmp.getNodeType();
			}
			if(!resIdList.contains(nodeTmp.getRealId())){
				resIdList.add(nodeTmp.getRealId());
			}
			if(!pList.contains(nodeTmp.getPermissionId())
					&& !GlobalConstants4frame.PERMISSION_NONE.equals(nodeTmp.getPermissionId())){
				pList.add(nodeTmp.getPermissionId());
			}
			authResMap.put(nodeTmp.getRealId() , nodeTmp);
		}
		
		if (resIdList.size() > 0 && resDefNo != null
				&& !"".equals(resDefNo)) {
			List<String> beanNames = this.authBS
					.findResObjBeanNameByType(resDefNo);

			List<BioneResOperInfo> operInfos = null;
			if (beanNames != null && beanNames.size() > 0) {
				// ?????????????????????????????????????????????
				String beanName = beanNames.get(0);
				IResObject resObj = SpringContextHolder.getBean(beanName);
				if (resObj != null) {
					operInfos = resObj.findResOperList(resDefNo, resIdList);
				}
			}

			if (operInfos != null) {
			    Set<String> set = new HashSet<String>();
			    
				for (int j = 0; j < operInfos.size(); j++) {
					BioneResOperInfo oper = operInfos.get(j);
					if(CommonTreeNode.ROOT_ID.equals(oper.getUpNo())
							&& authResMap.containsKey(oper.getResNo())){
					    set.add(oper.getResNo());
//						AuthTreeNode nodeTmp = authResMap.get(oper.getResNo());
//						pageShowTree.add(generateResNode(resDefNo , nodeTmp));
					}
				}
				
				for (String resNo : set) {
				    AuthTreeNode nodeTmp = authResMap.get(resNo);
				    pageShowTree.add(generateResNode(resDefNo , nodeTmp));
				}
				for (int j = 0; j < operInfos.size(); j++) {
				    BioneResOperInfo oper = operInfos.get(j);
				    CommonTreeNode node = new CommonTreeNode();
	                node.setId(resOperNodeType + "_" + oper.getOperId());
	                node.setText(oper.getOperName());
	                if (CommonTreeNode.ROOT_ID.equals(oper.getUpNo())) {
	                    node.setUpId(resDefNo + "_" + oper.getResNo());
	                } else {
	                    node.setUpId(resOperNodeType + "_" + oper.getUpNo());
	                }
	                Map<String, String> paramMap = new HashMap<String, String>();
	                paramMap.put("realId", oper.getOperId());
	                paramMap.put("nodeType", resOperNodeType);
	                node.setParams(paramMap);
	                // ??????????????????????????????????????????
	                if (pList.toString().indexOf(oper.getOperId()) != -1) {
	                    node.setIschecked(true);
	                } else {
	                    node.setIschecked(false);
	                }
	                pageShowTree.add(node);
				}
			}
		}

		return pageShowTree;
	}
	
	// ?????????????????????(????????????)
	public void getDataRuleTree() {

	}

	// ???????????????????????????????????????
	@RequestMapping("/getAuthObjResRel.*")
	@ResponseBody
	public Map<String, List<BioneAuthObjResRel>> getAuthObjResRel(String objDefNo, String objDefId, String resDefNo) {
		Map<String, List<BioneAuthObjResRel>> ress = new HashMap<String, List<BioneAuthObjResRel>>();
		if (objDefNo != null && !"".equals(objDefNo) && objDefId != null
				&& !"".equals(objDefId)) {
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			List<BioneAuthObjResRel> relList = this.authBS
					.findAuthResListByType(userObj.getCurrentLogicSysNo(),
							objDefNo, objDefId,resDefNo);
			ress.put("Data", relList);
		}
		return ress;
	}

	/**
	 * ?????????????????????????????????????????????
	 *
	 * @param pager ????????????
	 * @param id    ??????id
	 * @return map
	 */
	@RequestMapping("/getRoleObjResRel.*")
	@ResponseBody
	public Map<String, Object> getRoleObjResRelMap(Pager pager, String id) {
		String objDefNo = "AUTH_OBJ_ROLE";
		return this.authObjBS.getObjectDefNoResourceMap(pager, id, objDefNo);
	}

	/**
	 * ?????????????????????????????????????????????
	 *
	 * @param pager ????????????
	 * @param id    ??????id
	 * @return map
	 */
	@RequestMapping("/getUserObjResRel.*")
	@ResponseBody
	public Map<String, Object> getUserObjResRel(Pager pager, String id) {
		String objDefNo = "AUTH_OBJ_USER";
		return this.authObjBS.getObjectDefNoResourceMap(pager, id, objDefNo);
	}

	/**
	 * ??????????????????????????????
	 * @return
	 */
	@RequestMapping(value = "/getBioneAuthResDefInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getBioneAuthResDefInfo(){
		List<BioneAuthResDef> resDefs = this.authBS
				.getAllEntityList(BioneAuthResDef.class, "resDefOrder", false);
		return this.authObjBS.getCommonComboBoxNodes(resDefs);
	}

	/**
	 * ????????????????????????
	 * ????????????
	 * @param nodes
	 */
	@RequestMapping("/saveAuth")
	public void saveAuth(String nodes) {
		if (nodes != null && !"".equals(nodes)) {
			// ??????????????????
			String saveLogId = RandomUtils.uuid2();
			List<BioneLogAuthDetail> logDetailList = Lists.newArrayList();
			// 20200604 ?????????????????????????????????????????? ????????????
			String isSaveLog = this.getPropertiesProperty("bione-frame/security/security.properties", "auth.isRecordLog");
			
			// ??????????????????
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			JSONObject groupJson = JSON.parseObject(nodes);
			String objDefNo = (String) groupJson.get("objDefNo");
			String objId = (String) groupJson.get("objId");
			String resDefNo = (String) groupJson.get("resDefNo");
			// ????????????????????? ??????Json ????????????????????????
			List<BioneAuthObjResRel> relsNew = this.getBioneAuthObjResRelList(groupJson, isSaveLog, saveLogId, logDetailList);
			// ??????????????????????????????
			List<BioneAuthObjResRel> relsOld = this.authObjBS.findAuthObjRelByObj(userObj.getCurrentLogicSysNo(), objDefNo, objId, resDefNo);
			// ????????????
			this.authObjBS.updateRelBatch(relsOld, relsNew);
			// ????????????
			if(StringUtils.equals("true", isSaveLog)){
				BioneLogAuthInfo logInfo = new BioneLogAuthInfo(saveLogId, objId, objDefNo, userObj.getCurrentLogicSysNo(), new Timestamp(System.currentTimeMillis()), userObj.getUserId());
				this.logAuthBS.saveLogs(logInfo, logDetailList);
			}
		}
	}

	/**
	 * ??????Json ????????????????????????
	 * @param groupJson
	 * @param isSaveLog
	 * @param saveLogId
	 * @param logDetailList
	 * @return
	 */
	private List<BioneAuthObjResRel> getBioneAuthObjResRelList(JSONObject groupJson, 
			String isSaveLog, String saveLogId, List<BioneLogAuthDetail> logDetailList) {
		
		List<BioneAuthObjResRel> relsNew = Lists.newArrayList();
		// ??????????????????
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		String objDefNo = (String) groupJson.get("objDefNo");
		String objId = (String) groupJson.get("objId");
		String resDefNo = (String) groupJson.get("resDefNo");
		JSONArray resIds = groupJson.getJSONArray("resIds");
		for (Object resId : resIds) {
			BioneAuthObjResRel rel = new BioneAuthObjResRel();
			BioneAuthObjResRelPK relPK = new BioneAuthObjResRelPK();
			relPK.setLogicSysNo(userObj.getCurrentLogicSysNo());
			relPK.setObjDefNo(objDefNo);
			relPK.setObjId(objId);
			relPK.setPermissionId(PERMISSION_ALL);
			relPK.setPermissionType(RES_PERMISSION_TYPE_OPER);
			relPK.setResDefNo(resDefNo);
			relPK.setResId((String)resId);
			rel.setId(relPK);
			relsNew.add(rel);

			// ????????????
			if(StringUtils.equals("true", isSaveLog)){
				BioneLogAuthDetail logDetail = new BioneLogAuthDetail(saveLogId, resDefNo, (String)resId);
				logDetailList.add(logDetail);
			}
		}

		//--------------------------------------------------
//		List<BioneResOperInfo> hasOperRess = this.authObjBS.findHasOperRess(relsNew);
//		if (hasOperRess != null) {
//			for (int i = 0; i < relsNew.size(); i++) {
//				BioneAuthObjResRel rel = relsNew.get(i);
//				for (int j = 0; j < hasOperRess.size(); j++) {
//					BioneResOperInfo oper = hasOperRess.get(j);
//					if (rel.getId().getResDefNo().equals(oper.getResDefNo())
//							&& rel.getId().getResId().equals(oper.getResNo())) {
//						relsNew.get(i).getId().setPermissionId(PERMISSION_NONE);
//					}
//				}
//			}
//		}
		return relsNew;
	}

	/**
	 * ?????????????????????????????????
	 * @param propertiesPath
	 * @param propertyName
	 * @return
	 */
	private String getPropertiesProperty(String propertiesPath, String propertyName) {
		String propertyValue = "";
		try {
			PropertiesUtils pUtils = PropertiesUtils.get(propertiesPath);
			propertyValue = pUtils.getProperty(propertyName);
		} catch (Exception e) {
			logger.warn("??????????????????" + propertiesPath + "????????????" + propertyName+ "????????????");
		}
		return propertyValue;
	}

	private CommonTreeNode generateResNode(String resDefNo , AuthTreeNode res){
		CommonTreeNode node = new CommonTreeNode();
		node.setId(res.getId());
		node.setUpId("0");
		node.setText(res.getText());
		node.setIcon(res.getIcon());
		node.setIsParent(true);
		Map<String , String> params = new HashMap<String , String>();
		params.put("realId", res.getId());
		params.put("nodeType" , resDefNo);
		params.put("permissionId", res.getPermissionId());
		node.setParams(params);
		return node;
	}
	
}
