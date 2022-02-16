package com.yusys.bione.frame.user.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRelPK;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.logicsys.entity.BioneAuthObjSysRel;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.entity.BioneUserAttr;
import com.yusys.bione.frame.user.entity.BioneUserAttrGrp;
import com.yusys.bione.frame.user.entity.BioneUserAttrVal;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.web.vo.BioneUserAttrGrpVO;
import com.yusys.bione.frame.user.web.vo.BioneUserAttrValVO;
import com.yusys.bione.frame.util.SplitStringBy1000;

/**
 * <pre>
 * Title:用户管理业务逻辑类
 * Description: 提供用户管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class UserBS extends BaseBS<Object> {

	/**
	 * 获取列表数据, 支持查询
	 * 超级管理员支持所有功能
	 * 普通管理员可根据授权机构编辑查询辖内用户
	 * 普通用户无任何权限
	 * 
	 * @param firstResult
	 *            分页的开始索引
	 * @param pageSize
	 *            页面大小
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            搜索条件
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<Object[]> getUserList(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap,String isSts, boolean isQuery) {
		SearchResult<Object[]> result = new SearchResult<Object[]>();
		StringBuilder jql = new StringBuilder("");
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		jql.append("select usr.USER_ID, usr.USER_NO, usr.USER_PWD, usr.USER_NAME, usr.SEX, usr.BIRTHDAY, usr.EMAIL, usr.ADDRESS, usr.POSTCODE, usr.MOBILE, usr.TEL, usr.ORG_NO, usr.DEPT_NO, "
				+ " usr.USER_ICON, usr.USER_STS, usr.IS_BUILTIN, usr.REMARK, usr.LAST_PWD_UPDATE_TIME, usr.LAST_UPDATE_USER, usr.LAST_UPDATE_TIME, usr.LOGIC_SYS_NO,dept.DEPT_NAME,org.ORG_NAME,"
				+ "dept.dept_ID, usr.USER_AGNAME, usr.IS_MANAGER, usr.ID_CARD"
				+ " from Bione_User_Info usr left join bione_org_info org  on usr.org_no = org.org_no left join bione_dept_info dept on usr.dept_no = dept.dept_no and usr.org_no = dept.org_no "
				+ " where 1=1 and usr.logic_Sys_No='"
				+ logicSysNo+"' and usr.IS_BUILTIN='0'");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		if(StringUtils.isNotBlank(isSts)){
			jql.append(" and usr.user_sts = :isSts");
			values.put("isSts",isSts);
		}
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			if("Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){ //管理者
				if(!isQuery){
					jql.append(" and ((usr.is_Manager = 'N' or usr.is_Manager is null) or (usr.is_Manager = 'Y' and usr.org_no !='"+BioneSecurityUtils.getCurrentUserInfo().getOrgNo()+"'))");// 不能看到同级管理者的信息
				}
				List<String> curOrgNo = Lists.newArrayList();
				List<String> orgNos = Lists.newArrayList();
				// 获取用户授权机构
				List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
				curOrgNo.add(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
				orgNos.addAll(authOrgNos);
				// 获取辖内机构
				this.getAllChildBioneOrgNo(curOrgNo, orgNos);
				orgNos = orgNos.stream().distinct().collect(Collectors.toList());
				List<List<String>> allOrgs = SplitStringBy1000.change(orgNos);
				jql.append(" and ( ");
				for(int i=0; i<allOrgs.size(); i++){
					if(i == allOrgs.size() -1){
						jql.append(" usr.org_No in (:orgNos"+i+"))");
					}else{
						jql.append(" usr.org_No in (:orgNos"+i+") or ");
					}
					values.put("orgNos"+i, allOrgs.get(i));
				}
				if (!StringUtils.isEmpty(orderBy)) {
					jql.append(" order by " + orderBy + " " + orderType);
				}
				result = this.baseDAO.findPageWithNameParamByNativeSQL(firstResult,
						pageSize, jql.toString(), values);
			} else { //普通用户没有查询以及编辑权限
				
			}
		}else{ //超级管理员查询所有
			if (!StringUtils.isEmpty(orderBy)) {
				jql.append(" order by " + orderBy + " " + orderType);
			}
			result = this.baseDAO.findPageWithNameParamByNativeSQL(firstResult,
					pageSize, jql.toString(), values);
		}
		
		return result;
	}
	
	public void getAllChildBioneOrgNo(List<String> orgs,List<String> allowOrgs) {
		if (orgs != null && orgs.size() > 0) {
			List<List<String>> orgLists = SplitStringBy1000.change(orgs);
			List<String> upOrgs = new ArrayList<String>();
			for(List<String> orgList : orgLists){
				String jql = "select org from BioneOrgInfo org where org.upNo in ?0 and org.logicSysNo = ?1";
				List<BioneOrgInfo> neworgs = this.baseDAO.findWithIndexParam(jql,
						orgList, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
				if (neworgs != null && neworgs.size() > 0) {
					for (BioneOrgInfo org : neworgs) {
						if(!allowOrgs.contains(org.getOrgNo())){
							allowOrgs.add(org.getOrgNo());
							upOrgs.add(org.getOrgNo());
						}
					}
				}
			}
			if (upOrgs.size() > 0) {
				getAllChildBioneOrgNo(upOrgs, allowOrgs);
			}
		}
	}
	
	/**
	 * 获取机构下拉框数据
	 */
	public List<Map<String, String>> getOrgComboxData(String orgNo) {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		StringBuilder jql = new StringBuilder(
				"select org.orgNo, org.orgName from BioneOrgInfo org where 1=1 and org.logicSysNo='" + logicSysNo
						+ "' ");
		if (orgNo != null) {
			jql.append(" and org.orgNo = '" + orgNo + "'");
		}
		List<Object[]> objList = this.baseDAO.findWithNameParm(jql.toString(), null);
		List<Map<String, String>> comboList = new ArrayList<Map<String, String>>();
		Map<String, String> orgMap;
		for (Object[] obj : objList) {
			orgMap = new HashMap<String, String>();
			orgMap.put("id", obj[0] != null ? obj[0].toString() : "");
			orgMap.put("text", obj[1] != null ? obj[1].toString() : "");
			comboList.add(orgMap);
		}
		return comboList;
	}

	/**
	 * 获取部门下拉框数据
	 */
	public List<Map<String, String>> getDeptComboxData(String orgNo, String deptNo) {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		StringBuilder jql = new StringBuilder(
				"select dept.deptNo, dept.deptName from BioneDeptInfo dept where 1=1 and org.logicSysNo='" + logicSysNo
						+ "' ");
		if (orgNo != null) {
			jql.append(" and dept.orgNo = '" + orgNo + "'");
		}
		if (deptNo != null) {
			jql.append(" and dept.deptNo = '" + deptNo + "'");
		}
		List<Object[]> objList = this.baseDAO.findWithNameParm(jql.toString(), null);
		List<Map<String, String>> comboxList = new ArrayList<Map<String, String>>();
		Map<String, String> deptMap = new HashMap<String, String>();
		for (Object[] obj : objList) {
			deptMap.put("id", obj[0] != null ? obj[0].toString() : "");
			deptMap.put("text", obj[1] != null ? obj[1].toString() : "");
			comboxList.add(deptMap);
		}
		return comboxList;
	}

	/**
	 * @return 用于ligerui生成表单的json格式数据
	 */
	public String getUserEditEvents() {
		StringBuilder jql = new StringBuilder(
				"select attr from BioneUserAttr attr where attr.attrSts = :parm0 order by attr.grpId, attr.orderNo asc");

		Map<String, String> values = Maps.newHashMap();
		values.put("parm0", GlobalConstants4frame.COMMON_STATUS_VALID);
		List<BioneUserAttr> attrList = this.baseDAO.findWithNameParm(jql.toString(), values);

		StringBuilder lml = new StringBuilder("");
		lml.append("[");
		lml.append("{name : 'userId',type : 'hidden'}");
		int i = 1;
		for (BioneUserAttr attr : attrList) {
			lml.append(", {");
			if (attr.getFieldName() != null) {
				lml.append("name : '" + attr.getFieldName() + "'");
				if (i == 1) {
					lml.append(", group : '用户信息'");
					lml.append(", groupicon : groupicon");
				}
			}
			if (attr.getLabelName() != null) {
				lml.append(", display : '" + attr.getLabelName() + "'");
			}
			if (attr.getIsNewline() != null) {
				lml.append(", newline : " + attr.getIsNewline());
			}
			if (attr.getLabelAlign() != null) {
				lml.append(", labelAlign : '" + attr.getLabelAlign() + "'");
			}
			if (attr.getLabelWidth() != null) {
				lml.append(", labelWidth : " + attr.getLabelWidth());
			}
			if (attr.getElementAlign() != null) {
				lml.append(", align : '" + attr.getElementAlign() + "'");
			}
			if (attr.getElementWidth() != null) {
				lml.append(", width : " + attr.getElementWidth());
			}
			if (attr.getElementType() != null && !"02".equals(attr.getElementType())) {
				if ("01".equals(attr.getElementType())) {
					lml.append(", type : 'text'");
				} else if ("03".equals(attr.getElementType())) {
					lml.append(", type : 'date'");
				}
			} else if ("02".equals(attr.getElementType())) {
				lml.append(", type : 'select'");
				lml.append(", comboboxName : '" + attr.getFieldName() + "Combo'");
				/*
				 * if(attr.getIfExt() != null) { lml.append(
				 * ", options : { ifExt : " + attr.getIfExt()); //是否扩展的处理 }
				 */
				lml.append(", options : { ");
				// lml.append(", url : " + attr.getUrl());
				lml.append(", ajaxType : 'get' }");
			}

			if (attr.getFieldLength() != null || attr.getIsAllowNull() != null || attr.getCheckRuleType() != null
			// || attr.getUrl() != null
			) {
				lml.append(", validate : {");
				int j = 0;
				if (attr.getFieldLength() != null) {
					lml.append("maxlength : " + attr.getFieldLength());
					++j;
				}
				if (attr.getIsAllowNull() != null) {
					if (j != 0) {
						lml.append(", ");
					}
					lml.append("required : " + ("0".equals(attr.getIsAllowNull()) ? true : false));
					++j;
				}
				if (attr.getCheckRuleType() != null) {
					if (j != 0) {
						lml.append(", ");
					}
					lml.append(attr.getCheckRuleType() + " : true");
					++j;
				}
				/*
				 * if(attr.getUrl() != null) { if(j != 0) { lml.append(", "); }
				 * lml.append("remote : " + attr.getUrl()); //
				 * if(attr.getMessages() != null) { // lml.append(
				 * ", messages : { remote : " + attr.getMessages + "}"); //此处将
				 * URL 当做了远程验证来处理, 实际是用于下拉框的 // } }
				 */
				lml.append("}");
			}
			lml.append("}");
			++i;
		}
		lml.append("]");
		return StringUtils.deleteWhitespace(lml.toString());
	}

	/**
	 * 获取用户头像
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserIcon(String userId) {
		String jql = "select user.userIcon from BioneUserInfo user where user.userId = :userId";
		Map<String, String> values = Maps.newHashMap();
		values.put("userId", userId);
		List<Object> objList = this.baseDAO.findWithNameParm(jql, values);
		if (!CollectionUtils.isEmpty(objList)) {
			return (String) objList.get(0);
		}
		return "";
	}

	/**
	 * 获取用户编码
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserNo(String userId) {
		String jql = "select user.userNo from BioneUserInfo user where user.userId = :userId";
		Map<String, String> values = Maps.newHashMap();
		values.put("userId", userId);
		List<Object> objList = this.baseDAO.findWithNameParm(jql, values);
		if (!CollectionUtils.isEmpty(objList)) {
			return (String) objList.get(0);
		}
		return "";
	}

	/**
	 * 验证用户密码
	 * 
	 * @param userId
	 * @param userPwd
	 * @return
	 */
	public BioneUserInfo validUserPwd(String userId, String userPwd) {
		String jql = "select user from BioneUserInfo user where user.userId = :userId and user.userPwd = :userPwd";
		Map<String, String> values = Maps.newHashMap();
		values.put("userId", userId);
		values.put("userPwd", userPwd);
		List<BioneUserInfo> userList = this.baseDAO.findWithNameParm(jql, values);
		if (!CollectionUtils.isEmpty(userList)) {
			return userList.get(0);
		}
		return null;
	}

	/**
	 * 保存用户
	 * 
	 * @return
	 */
	@Transactional(readOnly = false)
	public String saveUserInfo(BioneUserInfo user) {
		String id = null;
		//String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		BioneUserInfo userInfo = this.getEntityById(BioneUserInfo.class, user.getUserId());
		String logicSysNo = "FRS";
		user.setLogicSysNo(logicSysNo);
		if (user != null) {
			if (user.getUserId() != null && !"".equals(user.getUserId()) && userInfo != null) {
				// 修改操作
				id = user.getUserId();
				// 修改时，若当前逻辑系统存在机构授权对象，删除用户与旧机构关系，添加用户与新机构关系
				if (!GlobalConstants4frame.SUPER_LOGIC_SYSTEM.equals(logicSysNo) && StringUtils.isNotEmpty(user.getOrgNo())) {
					List<BioneAuthObjSysRel> rels = this.getEntityListByProperty(BioneAuthObjSysRel.class,
							"id.objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
					if (rels != null && rels.size() > 0) {
						BioneUserInfo oldUser = this.getEntityById(BioneUserInfo.class, id);
						BioneOrgInfo oldOrg = this.getEntityByProperty(BioneOrgInfo.class, "orgNo", oldUser.getOrgNo());
						if(oldOrg != null){
							// 删除旧关系数据
							String delJql = "delete from BioneAuthObjUserRel rel where rel.id.logicSysNo = ?0 and rel.id.objDefNo = ?1 and rel.id.objId = ?2 and rel.id.userId = ?3";
							this.baseDAO.batchExecuteWithIndexParam(delJql, logicSysNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG,
									oldOrg.getOrgId(), id);
						}
					}
					List<BioneAuthObjSysRel> deps = this.getEntityListByProperty(BioneAuthObjSysRel.class,
							"id.objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT);
					if (deps != null && deps.size() > 0) {
						BioneUserInfo oldUser = this.getEntityById(BioneUserInfo.class, id);
						String jql = "select obj from " + BioneDeptInfo.class.getSimpleName() + " obj where obj.deptNo=?0 and obj.orgNo=?1";
						BioneDeptInfo oldDept = this.baseDAO.findUniqueWithIndexParam(jql, oldUser.getDeptNo(), oldUser.getOrgNo());
						if(oldDept != null){
							// 删除旧关系数据
							String delJql = "delete from BioneAuthObjUserRel rel where rel.id.logicSysNo = ?0 and rel.id.objDefNo = ?1 and rel.id.objId = ?2 and rel.id.userId = ?3";
							this.baseDAO.batchExecuteWithIndexParam(delJql, logicSysNo, GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT,
									oldDept.getDeptId(), id);
						}
					}
				}
			} else {
				//user.setUserId(RandomUtils.uuid2());
			}
			// 若当前逻辑系统存在机构授权对象，建立用户与新部门的关系
			if (!GlobalConstants4frame.SUPER_LOGIC_SYSTEM.equals(logicSysNo) && StringUtils.isNotEmpty(user.getDeptNo()) && userInfo != null) {
				List<BioneAuthObjSysRel> rels = this.getEntityListByProperty(BioneAuthObjSysRel.class, "id.objDefNo",
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT);
				if (rels != null && rels.size() > 0) {
					List<BioneDeptInfo> newDept = this.findByPropertys(BioneDeptInfo.class, new String[]{"deptNo", "orgNo"}, new String[]{user.getDeptNo(),user.getOrgNo()});
					BioneAuthObjUserRel userRel = new BioneAuthObjUserRel();
					BioneAuthObjUserRelPK userRelPK = new BioneAuthObjUserRelPK();
					userRelPK.setLogicSysNo(logicSysNo);
					userRelPK.setObjDefNo(GlobalConstants4frame.AUTH_OBJ_DEF_ID_DEPT);
					if (CollectionUtils.isNotEmpty(newDept)) {
						userRelPK.setObjId(newDept.get(0).getDeptId());
					}
					userRelPK.setUserId(user.getUserId());
					userRel.setId(userRelPK);
					this.baseDAO.merge(userRel);
				}
			}

			// 若当前逻辑系统存在机构授权对象，建立用户与新机构的关系
			if (!GlobalConstants4frame.SUPER_LOGIC_SYSTEM.equals(logicSysNo) && StringUtils.isNotEmpty(user.getOrgNo()) && userInfo != null) {
				List<BioneAuthObjSysRel> rels = this.getEntityListByProperty(BioneAuthObjSysRel.class, "id.objDefNo",
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
				if (rels != null && rels.size() > 0) {
					BioneOrgInfo newOrg = this.getEntityByProperty(BioneOrgInfo.class, "orgNo", user.getOrgNo());
					BioneAuthObjUserRel userRel = new BioneAuthObjUserRel();
					BioneAuthObjUserRelPK userRelPK = new BioneAuthObjUserRelPK();
					userRelPK.setLogicSysNo(logicSysNo);
					userRelPK.setObjDefNo(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
					userRelPK.setObjId(newOrg.getOrgId());
					userRelPK.setUserId(user.getUserId());
					userRel.setId(userRelPK);
					this.baseDAO.merge(userRel);
				}
			}
			BioneUserInfo userTmp = (BioneUserInfo) this.baseDAO.merge(user);
			if (userTmp != null) {
				id = userTmp.getUserId();
			}
		}
		return id;
	}

	/**
	 * 获取有效的用户属性
	 * 
	 * @return
	 */
	public List<BioneUserAttrGrpVO> getAttrsAndGrps() {
		List<BioneUserAttrGrpVO> grps = new ArrayList<BioneUserAttrGrpVO>();
		Map<String, List<BioneUserAttr>> grpAttrMap = new HashMap<String, List<BioneUserAttr>>();
		// 获取全部分组
		Map<String, Object> params = new HashMap<String, Object>();
		String jql1 = "select grp from BioneUserAttrGrp grp order by grp.orderNo asc";
		List<BioneUserAttrGrp> grpList = this.baseDAO.findWithNameParm(jql1, params);
		if (grpList != null && grpList.size() > 0) {
			List<String> grpIds = new ArrayList<String>();
			for (int i = 0; i < grpList.size(); i++) {
				if (!grpIds.contains(grpList.get(i).getGrpId())) {
					grpIds.add(grpList.get(i).getGrpId());
				}
			}
			if (grpIds.size() > 0) {
				// 获取用户信息属性
				String jql2 = "select attr from BioneUserAttr attr where attr.grpId in (?0) and attr.attrSts = ?1 order by attr.orderNo asc";
				List<BioneUserAttr> attrList = this.baseDAO.findWithIndexParam(jql2, grpIds,
						GlobalConstants4frame.COMMON_STATUS_VALID);
				if (attrList != null) {
					// 组装返回对象
					for (int j = 0; j < attrList.size(); j++) {
						BioneUserAttr attrTmp = attrList.get(j);
						List<BioneUserAttr> attrsTmp = grpAttrMap.get(attrTmp.getGrpId());
						if (attrsTmp == null) {
							if (attrTmp.getGrpId() != null && !"".equals(attrTmp.getGrpId())) {

								attrsTmp = new ArrayList<BioneUserAttr>();
								attrsTmp.add(attrTmp);
								grpAttrMap.put(attrTmp.getGrpId(), attrsTmp);
							}
						} else {
							attrsTmp.add(attrTmp);
						}
					}
					for (int k = 0; k < grpList.size(); k++) {
						BioneUserAttrGrp grpTmp = grpList.get(k);
						BioneUserAttrGrpVO grpVO = new BioneUserAttrGrpVO();
						BeanUtils.copyProperties(grpTmp, grpVO);
						List<BioneUserAttr> attrListTmp = grpAttrMap.get(grpTmp.getGrpId());
						grpVO.setAttrs(attrListTmp);
						if (attrListTmp != null && attrListTmp.size() > 0) {
							grps.add(grpVO);
						}
					}
				}
			}
		}
		return grps;
	}

	/**
	 * 获取指定用户扩展属性集合
	 * 
	 * @param userId
	 * @return
	 */
	public List<BioneUserAttrValVO> getAttrValsByUser(String userId) {
		List<BioneUserAttrValVO> vals = new ArrayList<BioneUserAttrValVO>();
		if (userId != null && !"".equals(userId)) {
			String jql = "select val.id.userId,val.id.attrId,val.attrValue,attr.fieldName from BioneUserAttrVal val , BioneUserAttr attr where val.id.attrId = attr.attrId and val.id.userId = ?0 ";
			List<Object[]> objs = this.baseDAO.findWithIndexParam(jql, userId);
			if (objs != null) {
				for (int i = 0; i < objs.size(); i++) {
					Object[] oTmp = objs.get(i);
					if (oTmp.length >= 4) {
						BioneUserAttrValVO valTmp = new BioneUserAttrValVO();
						valTmp.setUserId(oTmp[0] == null || "".equals(oTmp[0]) ? "" : (String) oTmp[0]);
						valTmp.setAttrId(oTmp[1] == null || "".equals(oTmp[1]) ? "" : (String) oTmp[1]);
						valTmp.setAttrValue(oTmp[2] == null || "".equals(oTmp[2]) ? "" : (String) oTmp[2]);
						valTmp.setFieldName(oTmp[3] == null || "".equals(oTmp[3]) ? "" : (String) oTmp[3]);

						vals.add(valTmp);
					}
				}
			}
		}
		return vals;
	}

	/**
	 * 获取指定用户指定扩展属性
	 * 
	 * @param userId
	 * @param attrId
	 * 
	 * @return
	 */
	public BioneUserAttrVal getValByUserAndAttr(String userId, String attrId) {
		BioneUserAttrVal val = null;
		if (userId != null && !"".equals(userId) && attrId != null && !"".equals(attrId)) {
			String jql = "select val from BioneUserAttrVal val where val.id.userId = ?0 and val.id.attrId = ?1 ";
			List<BioneUserAttrVal> vals = this.baseDAO.findWithIndexParam(jql, userId, attrId);
			if (vals != null && vals.size() > 0) {
				val = vals.get(0);
			}
		}
		return val;
	}

	/**
	 * 移除指定用户扩展属性
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = false)
	public void deleteUserAttrsByUId(String userId) {
		if (userId != null && !"".equals(userId)) {
			String jql = "delete from BioneUserAttrVal val where val.id.userId = ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, userId);
		}
	}

	/**
	 * 删除用户方法,动态表单时调用
	 * 
	 * @param userIds
	 * @return
	 */
	@Transactional(readOnly = false)
	public void deleteUserByUId(String[] userIds) {
		if (userIds != null && userIds.length > 0) {
			List<String> userIdsList = new ArrayList<String>();
			for (int i = 0; i < userIds.length; i++) {
				userIdsList.add(userIds[i]);
			}
			String jql1 = "delete from BioneUserAttrVal val where val.id.userId in (?0)";
			String jql2 = "delete from BioneUserInfo use where use.userId in (?0)";
			// 删除用户对应的授权对象关系
			String jql3 = "delete from BioneAuthObjUserRel rel where rel.id.userId in (?0)";
			this.baseDAO.batchExecuteWithIndexParam(jql1, userIdsList);
			this.baseDAO.batchExecuteWithIndexParam(jql2, userIdsList);
			this.baseDAO.batchExecuteWithIndexParam(jql3, userIdsList);
		}
	}

	/**
	 * 根据用户标识获取用户信息
	 * 
	 * @param userNo
	 * @return
	 */
	public BioneUserInfo getUserByUserNo(String userNo) {
		BioneUserInfo user = null;
		if (userNo != null && !"".equals(userNo)) {
			String jql = "select user from BioneUserInfo user where user.userNo = ?0 ";
			List<BioneUserInfo> users = this.baseDAO.findWithIndexParam(jql, userNo);
			if (users != null && users.size() > 0) {
				user = users.get(0);
			}
		}
		return user;
	}

	/**
	 * 检查某一用户属性在用户信息表中是否有值
	 * 
	 * @param userId
	 * @parma fieldName
	 * @return
	 */
	public String getFieldValueByUserId(String userId, String fieldName) {
		String value = null;
		if (userId != null && !"".equals(userId) && fieldName != null && !"".equals(fieldName)) {
			// 判断该field是否是BioneUserInfo中的field
			try {
				Field f = BioneUserInfo.class.getDeclaredField(fieldName);
				if (f != null) {
					String jql = "select info." + fieldName + " from BioneUserInfo info where info.userId = ?0";
					List<String> fieldList = this.baseDAO.findWithIndexParam(jql, userId);
					if (fieldList != null && fieldList.size() > 0) {
						value = fieldList.get(0) == null ? "" : fieldList.get(0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return value;
	}

	public String getOrgName(String logicSysNo, String orgNo) {
		String jql = "select org.orgName from BioneOrgInfo org where org.logicSysNo=?0 and org.orgNo=?1";
		String result = this.baseDAO.findUniqueWithIndexParam(jql, logicSysNo, orgNo);
		return result;
	}

	public String getDeptName(String logicSysNo, String deptNo) {
		String jql = "select dept.deptName from BioneDeptInfo dept where dept.logicSysNo=?0 and dept.deptNo=?1";
		String result = this.baseDAO.findUniqueWithIndexParam(jql, logicSysNo, deptNo);
		return result;
	}

	/**
	 * --author huangye 批量删除用户
	 * 
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void removeEntityBatch(String ids) throws Exception {
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		String[] idArray = StringUtils.split(ids, ',');
		// 删除用户信息、用户扩展信息、授权对象用户关系
		deleteUserByUId(idArray);
	}

	/**
	 * ---author huangye 20130703 08:29 判断某机构是否包含用户
	 * 
	 * @param orgId
	 *            机构ID
	 * @return 包含用户返回true,否则false
	 */
	public boolean checkIsOrgBeUsedByUser(String orgId) {
		boolean flag = false;
		BioneOrgInfo orgInfo = this.getEntityById(BioneOrgInfo.class, orgId);
		String jql = "SELECT user FROM BioneUserInfo user WHERE user.orgNo=?0";
		List<BioneUserInfo> userList = this.baseDAO.findWithIndexParam(jql, orgInfo.getOrgNo());
		if (userList != null && userList.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 通过逻辑系统标识和用户标识，获得用户信息
	 * 
	 * @param logicSysNo
	 *            逻辑系统标识
	 * @param userNo
	 *            用户标识
	 */
	public BioneUserInfo getUserInfo(String logicSysNo, String userNo) {
		String jql = "SELECT user FROM BioneUserInfo user WHERE user.logicSysNo=?0 and user.userNo=?1";
		List<BioneUserInfo> userList = this.baseDAO.findWithIndexParam(jql, logicSysNo, userNo);
		if (userList != null && userList.size() == 1) {
			return userList.get(0);
		} else {
			return null;
		}

	}

	public List<BioneUserInfo> getUserInofByIds(List<String> ids) {
		String jql = "select user from BioneUserInfo user where user.userId in(?0)";
		return this.baseDAO.findWithIndexParam(jql, ids);
	}


	/**
	 * 更改用户状态
	 * 
	 * @param userId
	 * @param sts
	 */
	@Transactional(readOnly = false)
	public void changeUserSts(String userId, String sts) {
		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(sts)) {
			String jql = "update BioneUserInfo user set userSts = ?0 where userId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, sts, userId);
		}
	}
	/**
	 * 获取当前用户信息
	 * @param
	 */
	public Map<String,Object> getCurrentUserInfo() {
		Map<String,Object> params = new HashMap<String, Object>();
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
	
	// 根据当前机构及部门编号获取唯一的部门ID
	private String getDeptIdByInfo(String orgNo, String deptNo) {
		String jql = "select dept.deptId from BioneDeptInfo dept where dept.logicSysNo = ?0 and dept.orgNo = ?1 and dept.deptNo =?2";
		String deptId = this.baseDAO.findUniqueWithIndexParam(jql,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
				orgNo, deptNo);
		return deptId;
	}

	// 获取当前登录人的信息
	private BioneUserInfo getLinkInfoList(String userId) {
		BioneUserInfo linkInfo = this
				.getEntityById(BioneUserInfo.class, userId);
		return linkInfo;
	}

	/**
	 * 查询用户与角色关系表
	 * @param ids 用户ids
	 * @return
	 */
	public List<BioneAuthObjUserRel> getUserRolesByIds(List<String> ids) {
		String jql = "select rel from BioneAuthObjUserRel rel where rel.id.userId in(?0) ";
		return this.baseDAO.findWithIndexParam(jql, ids);
	}

	/**
	 * 更改用户头像
	 *
	 * @param curImageUrl 用户头像URL
	 */
	@Transactional(readOnly = false)
	public void changeUserHeadImage(String userId, String curImageUrl) {
		if (StringUtils.isNotEmpty(curImageUrl) && StringUtils.isNotEmpty(userId)) {
			String jql = "update BioneUserInfo user set userIcon = ?0 where userId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, curImageUrl, userId);
		}
	}
	
	/**
	 * 
	 * 根据机构获取该机构下的所有成员
	 * @param orgNo
	 * @return
	 */
	public List<BioneUserInfo> getUserByOrgNo(String orgNo){
		List<BioneUserInfo> list = new ArrayList<BioneUserInfo>();
		if (orgNo != null && !"".equals(orgNo)) {
			String jql = "select user from BioneUserInfo user where user.orgNo = ?0 ";
			list = this.baseDAO.findWithIndexParam(jql,
					orgNo);
		}
		return list;
	}

	/**
	 * 更改是否管理者
	 * @param userId
	 * @param isManager
	 */
	public void changeIsManager(String userId, String isManager) {
		if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(isManager)) {
			String jql = "update BioneUserInfo user set isManager = ?0 where userId = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, isManager, userId);
		}
	}

	/**
	 * 获取用户，isManager等于Y
	 * @param isManager
	 */
	public List<BioneUserInfo> getUserList(String isManager) {
		List<BioneUserInfo> list = new ArrayList<BioneUserInfo>();
		String jql = "select user from BioneUserInfo user where user.isManager = ?0 ";
		list = this.baseDAO.findWithIndexParam(jql, isManager);
		
		return list;
	}
}
