package com.yusys.bione.frame.auth.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRel;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.auth.repository.AuthMybatisDao;
import com.yusys.bione.frame.authobj.entity.*;
import com.yusys.bione.frame.authres.entity.BioneDataRuleInfo;
import com.yusys.bione.frame.authres.entity.BioneFuncInfo;
import com.yusys.bione.frame.authres.entity.BioneMenuInfo;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.authres.service.MenuBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.logicsys.entity.BioneAdminUserInfo;
import com.yusys.bione.frame.logicsys.entity.BioneAuthObjSysRel;
import com.yusys.bione.frame.logicsys.entity.BioneAuthResSysRel;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IAuthObject;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.util.SplitStringBy1000;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * Title:授权管理业务逻辑类
 * Description: 提供授权管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:1.01   修改人：songxf  修改日期: 2012-07-05    修改内容:增加逻辑系统支持
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class AuthBS extends BaseBS<Object> {

	protected static Logger log = LoggerFactory.getLogger(AuthBS.class);

	@Autowired
	private MenuBS menuBS;

	@Autowired
	private AuthMybatisDao authMybatisDao;

	/**
	 * 根据授对象类型，查找用户被授权的授权对象的Id列表
	 * 
	 * @param logicSysNo
	 * @param authObjType
	 * @param userId
	 * @return
	 */
	public List<String> findAuthObjectIdByType(String logicSysNo,
			String authObjType, String userId) {

		String jql = "select rel.id.objId from  BioneAuthObjUserRel rel where rel.id.logicSysNo=?0 and rel.id.objDefNo=?1 and rel.id.userId=?2 ";

		return this.baseDAO.findWithIndexParam(jql, logicSysNo, authObjType,
				userId);

	}

	/**
	 * 获取除了用户以外的所有授权对象
	 * 
	 * @return
	 */
	public List<BioneAuthObjDef> getAuthObjDefWithNoUsr() {
		String jql = "select obj from BioneAuthObjDef obj where obj.objDefNo not in (?0)";
		return this.baseDAO.findWithIndexParam(jql,
				GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER);
	}

	/**
	 * 获取当前逻辑系统可配置的授权对象信息 不包括用户
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneAuthObjDef> getAuthObjDefBySys(String logicSysNo) {
		String jql = "select obj from BioneAuthObjDef obj, BioneAuthObjSysRel rel where rel.id.logicSysNo = ?0"
				+ " and obj.objDefNo = rel.id.objDefNo and obj.objDefNo not in (?1) order by obj.objDefOrder";
		// Set<String> values =
		// Sets.newHashSet(GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER,
		// GlobalConstants4frame.AUTH_OBJ_DEF_ID_POSI);
		return this.baseDAO.findWithIndexParam(jql, logicSysNo,
				GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER);
	}
	
	/**
	 * 获取当前逻辑系统可配置的授权对象信息 包括用户
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneAuthObjDef> getAllAuthObjDefBySys(String logicSysNo) {
		String jql = "select obj from BioneAuthObjDef obj, BioneAuthObjSysRel rel where rel.id.logicSysNo = ?0"
				+ " and obj.objDefNo = rel.id.objDefNo order by obj.objDefOrder";
		return this.baseDAO.findWithIndexParam(jql, logicSysNo);
	}

	/**
	 * 查找当前用户关联的授权对象集合
	 * 
	 * @param logicSysNo
	 * @param userId
	 * @return
	 */
	public List<BioneAuthObjUserRel> findAuthObjUserRelList(String logicSysNo,
			String userId) {

		String jql = "select rel from  BioneAuthObjUserRel rel where rel.id.logicSysNo=?0 and rel.id.userId=?1 ";

		return this.baseDAO.findWithIndexParam(jql, logicSysNo, userId);

	}

	/**
	 * 查找当前用户关联的授权对象分类Map
	 * 
	 * @param logicSysNo
	 * @param userId
	 * @return
	 */
	public Map<String, List<String>> findAuthObjUserRelMap(String logicSysNo,
			String userId) {

		Map<String, List<String>> authObjUserRelMap = Maps.newHashMap();
		String jql = "select obj from BioneAuthObjDef obj,BioneAuthObjSysRel rel where obj.objDefNo=rel.id.objDefNo and rel.id.logicSysNo=?0";

		// 所有的授权对象定义
		List<BioneAuthObjDef> authObjList = this.baseDAO.findWithIndexParam(
				jql, logicSysNo);

		if (authObjList != null) {

			for (BioneAuthObjDef authObjDef : authObjList) {
				if (!authObjDef.getObjDefNo().equals(
						GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER)) {
					IAuthObject authObj = null;
					List<String> authObjIdList = null;
					try {
						authObj = SpringContextHolder.getBean(authObjDef
								.getBeanName());
						authObjIdList = authObj.doGetAuthObjectIdListOfUser();
					} catch (Exception e) {
						log.error("授权对象[" + authObjDef.getBeanName() + "]定义错误!");
						continue;
					}
					if (!CollectionUtils.isEmpty(authObjIdList))
						authObjUserRelMap.put(authObjDef.getObjDefNo(),
								authObjIdList);
				}
			}
		}

		return authObjUserRelMap;

	}

	/**
	 * 查找某类授权对象所可以访问的某类授权资源的ID集合
	 * 
	 * @param logicSysNo
	 * @param resDefNo
	 *            授权资源标识
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param authObjId
	 *            授权对象Id
	 * @return
	 */
	public List<String> findAuthResIdListByType(String logicSysNo,
			String resDefNo, String authObjDefNo, List<String> authObjIds) {

		if (CollectionUtils.isEmpty(authObjIds))
			return null;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("logicSysNo", logicSysNo);
		param.put("resDefNo", resDefNo);
		param.put("objDefNo", authObjDefNo);
		// 对查询到的资源ID去重 edit by zhongqh
		String jql = "select rel.id.resId from  BioneAuthObjResRel rel where rel.id.logicSysNo=:logicSysNo and rel.id.resDefNo= :resDefNo and  "
				+ "rel.id.objDefNo= :objDefNo ";

		if(authObjIds.size() > 1000){
			List<List<String>> objIdsList = SplitStringBy1000.change(authObjIds);
			jql += "and (";
			for(int i=0; i<objIdsList.size(); i++){
				if(i == objIdsList.size()-1){
					jql += "rel.id.objId in (:objIds"+i+"))";
				}else{
					jql += "rel.id.objId in (:objIds"+i+") or ";
				}
				param.put("objIds"+i, objIdsList.get(i));
			}
		}else{
			param.put("objIds", authObjIds);
			jql += "and rel.id.objId in (:objIds)";
		}
		return this.baseDAO.findWithNameParm(jql, param);

	}

	/**
	 * 查找某类授权对象所可以访问的公告资源的ID集合
	 * 
	 * @param logicSysNo
	 *            逻辑系统号
	 * @param authObjDefNo
	 *            授权对象标识
	 * @return
	 */
	public List<String> findAuthMsgIdListByType(String logicSysNo,
			String authObjDefNo , List<String> authObjIds) {
		if(authObjIds == null
				|| authObjIds.size() <= 0){
			return new ArrayList<String>();
		}
		String jql = "select distinct t.id.announcementId from BioneMsgAuthObjRel t where t.id.logicSysNo=?0 and t.objDefNo=?1 and t.id.objId in ?2";
		return this.baseDAO.findWithIndexParam(jql, logicSysNo, authObjDefNo , authObjIds);
	}

	/**
	 * 查找当前用户的授权关系
	 * 
	 * @param logicSysNo
	 * @param objDefNo
	 *            授权对象标识
	 * @param authObjIds
	 *            授权对象id
	 * @return
	 */
	public List<BioneAuthObjResRel> findCurrentUserAuthObjResRels(
			String logicSysNo, String objDefNo, List<String> objIds) {

		if (CollectionUtils.isEmpty(objIds))
			return null;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("logicSysNo", logicSysNo);
		param.put("objDefNo", objDefNo);
		String jql = "select rel from  BioneAuthObjResRel rel where rel.id.logicSysNo= :logicSysNo and rel.id.objDefNo= :objDefNo ";
		if(objIds.size() > 1000){
			List<List<String>> objIdsList = SplitStringBy1000.change(objIds);
			jql += "and (";
			for(int i=0; i<objIdsList.size(); i++){
				if(i == objIdsList.size()-1){
					jql += "rel.id.objId in (:objIds"+i+"))";
				}else{
					jql += "rel.id.objId in (:objIds"+i+") or ";
				}
				param.put("objIds"+i, objIdsList.get(i));
			}
		}else{
			param.put("objIds", objIds);
			jql += "and rel.id.objId in (:objIds)";
		}

		return this.baseDAO.findWithNameParm(jql, param);
	}

	/**
	 * 查找当前用户所属的授权组Id
	 * 
	 * @return
	 */
	public List<String> findAuthGroupIdOfCurrentUser(String logicSysNo) {

		List<String> authGorupIdList = Lists.newArrayList();

		List<BioneAuthObjgrpObjRel> authObjGrpRelList = this.baseDAO
				.findWithIndexParam(
						"select rel from BioneAuthObjgrpObjRel rel where rel.id.logicSysNo=?0 order by rel.id.objgrpId,rel.id.objDefNo",
						logicSysNo);

		// 将授权组和授权对象的关系按照 授权组Id-授权对象标识-授权对象Id分组
		Map<String, Map<String, List<String>>> authGroupMap = Maps.newHashMap();

		if (authObjGrpRelList != null) {

			String objGroupId = null;
			String objDefNo = null;
			Map<String, List<String>> objDefMap = null;
			List<String> objIdList = null;

			for (BioneAuthObjgrpObjRel rel : authObjGrpRelList) {

				objGroupId = rel.getId().getObjgrpId();
				objDefNo = rel.getId().getObjDefNo();
				objDefMap = authGroupMap.get(objGroupId);

				// 授权组Id-所有授权对象分类集合
				if (objDefMap == null) {
					objDefMap = Maps.newHashMap();
					authGroupMap.put(objGroupId, objDefMap);
				}

				objIdList = objDefMap.get(objDefNo);

				// 授权对象分类标识-授权对象集合
				if (objIdList == null) {
					objIdList = Lists.newArrayList();
					objDefMap.put(objDefNo, objIdList);
				}

				objIdList.add(rel.getId().getObjId());
			}

			BioneUser currentUser = BioneSecurityUtils.getCurrentUserInfo();
			// 当前用户与授权对象的关系
			Map<String, List<String>> userAuthObjMap = currentUser
					.getAuthObjMap();

			Iterator<String> it = authGroupMap.keySet().iterator();

			boolean bool = false;

			while (it.hasNext()) {

				bool = true;
				objGroupId = it.next();
				objDefMap = authGroupMap.get(objGroupId);

				Iterator<String> innerIt = objDefMap.keySet().iterator();

				List<String> userAuthObjIdList = null;
				while (bool && innerIt.hasNext()) {

					bool = false;

					objDefNo = innerIt.next();
					objIdList = objDefMap.get(objDefNo);

					userAuthObjIdList = userAuthObjMap.get(objDefNo);

					if (userAuthObjIdList != null) {

						for (String authObjId : userAuthObjIdList) {

							if (objIdList.contains(authObjId)) {
								bool = true;
								break;
							}

						}
					}

				}

				// 当前用户满足此授权组的条件
				if (bool)
					authGorupIdList.add(objGroupId);

			}

		}

		return authGorupIdList;

	}

	/**
	 * 查找所有的资源操作信息，以Map形式返回
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public Map<String, BioneResOperInfo> findAllResOperInfoMap() {

		Map<String, BioneResOperInfo> retMap = Maps.newHashMap();
		List<BioneResOperInfo> resOperInfos = this
				.getEntityList(BioneResOperInfo.class);

		if (resOperInfos != null) {

			for (BioneResOperInfo resOperInfo : resOperInfos)
				retMap.put(resOperInfo.getOperId(), resOperInfo);
		}

		return retMap;
	}

	/**
	 * 查找所有数据规则信息，以Map形式返回
	 * 
	 * @return
	 */
	public Map<String, BioneDataRuleInfo> findAllDataRuleInfoMap() {

		Map<String, BioneDataRuleInfo> retMap = Maps.newHashMap();
		List<BioneDataRuleInfo> dataRuleInfos = this
				.getEntityList(BioneDataRuleInfo.class);

		if (dataRuleInfos != null) {

			for (BioneDataRuleInfo dataRuleInfo : dataRuleInfos)
				retMap.put(dataRuleInfo.getDataRuleId(), dataRuleInfo);
		}

		return retMap;
	}

	/**
	 * 查找所有菜单信息，以Map形式返回
	 * 
	 * @return key:menuId , value:功能点对象
	 */
	public Map<String, BioneFuncInfo> findAllMenuInfoMap() {

		Map<String, BioneFuncInfo> retMap = Maps.newHashMap();
		List<BioneMenuInfo> menuInfos = this.getEntityList(BioneMenuInfo.class);
		if (menuInfos != null) {
			List<BioneFuncInfo> funcInfos = this
					.getEntityList(BioneFuncInfo.class);
			Map<String, BioneFuncInfo> funcMap = Maps.newHashMap();
			if (funcInfos != null) {
				for (BioneFuncInfo funcTmp : funcInfos) {
					funcMap.put(funcTmp.getFuncId(), funcTmp);
				}
			}
			for (BioneMenuInfo menuInfo : menuInfos) {
				BioneFuncInfo func = funcMap.get(menuInfo.getFuncId());
				if (func == null) {
					continue;
				}
				retMap.put(menuInfo.getMenuId(), func);
			}
		}

		return retMap;
	}

	/**
	 * 查找所有菜单访问路径列表，路径不包含参数部分
	 * 
	 * @return
	 */
	public List<String> findAllMenuNavPathList() {

		List<String> menuNavPathList = Lists.newArrayList();
		List<BioneFuncInfo> menuInfos = this.getEntityList(BioneFuncInfo.class);

		if (menuInfos != null) {

			String navPath = null;
			for (BioneFuncInfo menuInfo : menuInfos) {

				navPath = menuInfo.getNavPath();

				// 去掉URL后面的参数
				navPath = StringUtils.substringBefore(navPath, "?");

				// 对路径进行格式化处理
				navPath = WebUtils.normalize(navPath);

				menuNavPathList.add(navPath);
			}

		}

		return menuNavPathList;
	}

	/**
	 * 查找当前用户有效的授权机构id
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<String> findValidAuthOrgIdOfUser(String loigcSysNo,
			String authObjDefNo, String userId) {

		// 查找机构状态为生效的机构
		String jql = "select org.orgId from BioneAuthObjUserRel rel,BioneOrgInfo org where rel.id.userId=?0 and rel.id.objDefNo=?1 and rel.id.objId=org.orgId and org.orgSts=?2 and org.logicSysNo=?3  and rel.id.logicSysNo=?3";

		return this.baseDAO.findWithIndexParam(jql, userId, authObjDefNo,
				GlobalConstants4frame.COMMON_STATUS_VALID, loigcSysNo);
	}

	/**
	 * 查找当前用户有效的授权部门id
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<String> findValidAuthDeptIdOfUser(String loigcSysNo,
			String authObjDefNo, String userId) {

		String jql = "select dept.deptId from BioneAuthObjUserRel rel,BioneDeptInfo dept where rel.id.userId=?0 and rel.id.objDefNo=?1 and rel.id.objId=dept.deptId and dept.deptSts=?2 and dept.logicSysNo=?3  and rel.id.logicSysNo=?3";

		return this.baseDAO.findWithIndexParam(jql, userId, authObjDefNo,
				GlobalConstants4frame.COMMON_STATUS_VALID, loigcSysNo);
	}

	/**
	 * 查找当前用户有效的角色id
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<String> findValidAuthRoleIdOfUser(String loigcSysNo,
			String authObjDefNo, String userId) {

		// 查找机构状态为生效的机构
		String jql = "select role.roleId from BioneAuthObjUserRel rel,BioneRoleInfo role where rel.id.userId=?0 and rel.id.objDefNo=?1 and rel.id.objId=role.roleId and role.roleSts=?2 and role.logicSysNo=?3 and rel.id.logicSysNo=?3";

		return this.baseDAO.findWithIndexParam(jql, userId, authObjDefNo,
				GlobalConstants4frame.COMMON_STATUS_VALID, loigcSysNo);
	}

	/**
	 * 查找当前用户有效的授权组id
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<String> findValidAuthObjGrpIdOfUser(String loigcSysNo,
			String authObjDefNo, String userId) {

		// 查找机构状态为生效的机构
		String jql = "select grp.objgrpId from BioneAuthObjUserRel rel,BioneAuthObjgrpInfo grp where rel.id.userId=?0 and rel.id.objDefNo=?1 and rel.id.objId=grp.objgrpId and grp.objgrpSts=?2 and grp.logicSysNo=?3 and rel.id.logicSysNo=?3";

		return this.baseDAO.findWithIndexParam(jql, userId, authObjDefNo,
				GlobalConstants4frame.COMMON_STATUS_VALID, loigcSysNo);
	}

	/**
	 * 查找当前用户有效的授权机构对象（含分级授权）
	 * 
	 * @param logicSysNo
	 *            逻辑系统
	 * @param isHierarchicalAuth
	 *            是否使用分级授权
	 * @return
	 */
	public List<BioneOrgInfo> findValidAuthOrgOfUser(String logicSysNo,
			boolean isHierarchicalAuth) {
		// 查找机构状态为生效的机构
		StringBuilder jql = new StringBuilder(
				"select org from BioneOrgInfo org where org.logicSysNo=:logicSysNo and org.orgSts=:orgSts");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		List<BioneOrgInfo> orgs = this.baseDAO.findWithNameParm(jql.toString(),
				paramMap);
		if (isHierarchicalAuth) {
			// 使用分级授权，过滤出用户平级和下级的机构信息
			List<String> orgNos = BioneSecurityUtils
					.getOrgsByHierarchicalAuth();
			if (orgs != null && orgNos != null && orgNos.size() > 0) {
				List<BioneOrgInfo> orgsTmp = new ArrayList<BioneOrgInfo>();
				for (BioneOrgInfo orgTmp : orgs) {
					if (!orgNos.contains(orgTmp.getOrgNo())) {
						continue;
					}
					orgsTmp.add(orgTmp);
				}
				orgs = orgsTmp;
			}
		}
		return orgs;
	}

	/**
	 * 查找当前用户有效的授权机构对象（含分级授权）异步树
	 * 
	 * @param logicSysNo
	 *            逻辑系统
	 * @param isHierarchicalAuth
	 *            是否使用分级授权
	 * @return
	 */
	public List<BioneOrgInfo> findValidAuthOrgOfUser(String logicSysNo, boolean isHierarchicalAuth, String upOrgNo, String... searchText) {
		String searchTextStr = "";
		if (searchText.length > 0 && StringUtils.isNotEmpty(searchText[0])) {
			searchTextStr = searchText[0];
		}
		List<BioneOrgInfo> orgs;
		if (GlobalConstants4frame.TREE_ROOT_NO.equals(upOrgNo) && StringUtils.isEmpty(searchTextStr)) {
			if (isHierarchicalAuth) {
				upOrgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
				String upOrgJql = "select org.upNo from  BioneOrgInfo org where org.logicSysNo=:logicSysNo and org.orgSts=:orgSts and org.orgNo=:orgNo";
				Map<String, Object> upOrgparamMap = new HashMap<String, Object>();
				upOrgparamMap.put("logicSysNo", logicSysNo);
				upOrgparamMap.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
				upOrgparamMap.put("orgNo", upOrgNo);
				List<String> upOrg = this.baseDAO.findWithNameParm(upOrgJql.toString(), upOrgparamMap);
				upOrgNo = upOrg.get(0);
			}
		}
		// 查找机构状态为生效的机构
		StringBuilder jql = new StringBuilder("select org from BioneOrgInfo org where org.logicSysNo=:logicSysNo and org.orgSts=:orgSts ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		if (StringUtils.isEmpty(searchTextStr)) {
			jql.append(" and org.upNo=:upNo ");
			paramMap.put("upNo", upOrgNo);
		} else {
			jql.append(" and org.orgName like :searchNm");
			paramMap.put("searchNm", "%" + searchTextStr + "%");
		}
		orgs = this.baseDAO.findWithNameParm(jql.toString(), paramMap);
		if (isHierarchicalAuth) {
			// 使用分级授权，过滤出用户平级和下级的机构信息
			List<String> orgNos = BioneSecurityUtils.getOrgsByHierarchicalAuth();
			if (orgs != null && orgNos != null && orgNos.size() > 0) {
				List<BioneOrgInfo> orgsTmp = new ArrayList<BioneOrgInfo>();
				for (BioneOrgInfo orgTmp : orgs) {
					if (!orgNos.contains(orgTmp.getOrgNo())) {
						continue;
					}
					orgsTmp.add(orgTmp);
				}
				orgs = orgsTmp;
			}
		}
		return orgs;
	}

	/**
	 * 查找当前用户有效的授权机构对象
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @return
	 */
	public List<BioneOrgInfo> findValidAuthOrgOfUser(String loigcSysNo) {

		// 查找机构状态为生效的机构
		String jql = "select org from BioneOrgInfo org where org.logicSysNo=?0 and org.orgSts=?1";

		return this.baseDAO.findWithIndexParam(jql, loigcSysNo,
				GlobalConstants4frame.COMMON_STATUS_VALID);
	}

	/**
	 * 获取指定机构的下级机构编号
	 * 
	 * @param orgNos
	 * @param logicSysNo
	 * @return
	 */
	public List<String> getOrgNosByParent(List<String> orgNos, String logicSysNo) {
		List<String> returnNos = new ArrayList<String>();
		if (orgNos != null && orgNos.size() > 0
				&& !StringUtils.isEmpty(logicSysNo)) {
			String jql = "select org.orgNo from BioneOrgInfo org where org.upNo in (?0) and org.orgSts = ?1 and org.logicSysNo = ?2";
			returnNos = this.baseDAO.findWithIndexParam(jql, orgNos,
					GlobalConstants4frame.COMMON_STATUS_VALID, logicSysNo);
			if (returnNos == null) {
				returnNos = new ArrayList<String>();
			}
		}
		return returnNos;
	}

	/**
	 * 查找当前用户有效的授权角色对象
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @return
	 */
	public List<BioneAuthObjgrpInfo> findValidAuthObjGrpOfUser(String loigcSysNo) {

		// 查找机构状态为生效的角色
		String jql = "select obj from BioneAuthObjgrpInfo obj where obj.logicSysNo=?0";

		return this.baseDAO.findWithIndexParam(jql, loigcSysNo);
	}

	public List<BioneAuthObjgrpInfo> findValidAuthObjGrpOfUser(
			String loigcSysNo, String searchText) {

		// 查找机构状态为生效的角色
		String jql = "select obj from BioneAuthObjgrpInfo obj where obj.logicSysNo=?0 and obj.objgrpName like ?1";

		return this.baseDAO.findWithIndexParam(jql, loigcSysNo, "%"
				+ searchText + "%");
	}

	/**
	 * 查找当前用户有效的授权部门对象
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @return
	 */
	public List<BioneDeptInfo> findValidAuthDeptOfUser(String loigcSysNo) {

		// 查找机构状态为生效的角色
		String jql = "select dept from BioneDeptInfo dept where dept.logicSysNo=?0 and dept.deptSts=?1";

		return this.baseDAO.findWithIndexParam(jql, loigcSysNo,
				GlobalConstants4frame.COMMON_STATUS_VALID);
	}

	/**
	 * 查找当前用户有效的授权部门对象（含分级授权逻辑）
	 * 
	 * @param logicSysNo
	 *            逻辑系统
	 * @param isHierarchicalAuth
	 *            是否使用分级授权
	 * @return
	 */
	public List<BioneDeptInfo> findValidAuthDeptOfUser(String logicSysNo,
			boolean isHierarchicalAuth , String... searchText) {
		// 查找部门状态为生效的部门
		StringBuilder jql = new StringBuilder(
				"select dept from BioneDeptInfo dept where dept.logicSysNo=:logicSysNo and dept.deptSts=:deptSts ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("deptSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		String searchTextStr = "";
		if (searchText.length > 0 && StringUtils.isNotEmpty(searchText[0])) {
			searchTextStr = searchText[0];
		}
		if(StringUtils.isNotEmpty(searchTextStr)){
			jql.append(" and dept.deptName like :searchText ");
			paramMap.put("searchText", "%"+searchTextStr+"%");
		}
		List<BioneDeptInfo> depts = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);
		if (isHierarchicalAuth) {
			// 使用分级授权，过滤出用户平级和下级的机构信息
			List<String> orgNos = BioneSecurityUtils
					.getOrgsByHierarchicalAuth();
			if (depts != null && orgNos != null && orgNos.size() > 0) {
				List<BioneDeptInfo> deptsTmp = new ArrayList<BioneDeptInfo>();
				for (BioneDeptInfo deptTmp : depts) {
					if (!orgNos.contains(deptTmp.getOrgNo())) {
						continue;
					}
					deptsTmp.add(deptTmp);
				}
				depts = deptsTmp;
			}
		}
		return depts;
	}

	/**
	 * 查找当前用户有效的授权部门对象（含分级授权逻辑）
	 * 
	 * @param logicSysNo
	 *            逻辑系统
	 * @param isHierarchicalAuth
	 *            是否使用分级授权
	 * @return
	 */
	public List<BioneDeptInfo> findValidAuthDeptOfUser(String logicSysNo,
			String orgNo , String... searchText) {
		String searchTextStr = "";
		if (searchText.length > 0 && StringUtils.isNotEmpty(searchText[0])) {
			searchTextStr = searchText[0];
		}
		// 查找部门状态为生效的角色
		StringBuilder jql = new StringBuilder(
				"select dept from BioneDeptInfo dept where dept.logicSysNo=:logicSysNo and dept.deptSts=:deptSts and dept.orgNo=:orgNo");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("deptSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		paramMap.put("orgNo", orgNo);
		if(StringUtils.isNotEmpty(searchTextStr)){
			jql.append(" and dept.deptName like :searchText ");
			paramMap.put("searchText", "%"+searchTextStr+"%");
		}
		List<BioneDeptInfo> depts = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);
		return depts;
	}

	/**
	 * 查找当前用户有效的授权组对象
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<BioneRoleInfo> findValidAuthRoleOfUser(String loigcSysNo) {

		// 查找机构状态为生效的角色
		String jql = "select role from BioneRoleInfo role where role.logicSysNo=?0 and role.roleSts=?1 ";
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){//普通管理员
			jql += " and role.lastUpdateUser='" + BioneSecurityUtils.getCurrentUserId() + "' ";
		}else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& !"Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){//普通用户
			return null;
		}
		return this.baseDAO.findWithIndexParam(jql, loigcSysNo,
				GlobalConstants4frame.COMMON_STATUS_VALID);
	}

	public List<BioneRoleInfo> findValidAuthRoleOfUser(String loigcSysNo,
			String searchText) {

		// 查找机构状态为生效的角色
		String jql = "select role from BioneRoleInfo role where role.logicSysNo=?0 and role.roleSts=?1 and role.roleName like ?2";
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){//普通管理员
			jql += " and role.lastUpdateUser='" + BioneSecurityUtils.getCurrentUserId() + "' ";
		}else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() 
				&& !"Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){//普通用户
			return null;
		}
		return this.baseDAO.findWithIndexParam(jql, loigcSysNo,
				GlobalConstants4frame.COMMON_STATUS_VALID, "%" + searchText + "%");
	}

	/**
	 * 查找当前用户有效的授权用户id
	 * 
	 * @return
	 */
	public List<String> findValidAuthUserIdOfUser(String loigcSysNo) {

		// 查找状态为启用并且不包含内置的用户
		String jql = "select usr.userId from BioneUserInfo usr where usr.userSts=?0 and usr.logicSysNo=?1 and usr.isBuiltin=?2 or usr.isBuiltin is null  order by usr.userName";

		return this.baseDAO.findWithIndexParam(jql,
				GlobalConstants4frame.COMMON_STATUS_VALID, loigcSysNo,
				GlobalConstants4frame.COMMON_STATUS_INVALID);
	}

	/**
	 * 查找当前用户有效的授权用户对象集合
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneUserInfo> findValidAuthUserObjOfUser(String logicSysNo) {
		// 查找机构状态为启用并且不包含内置的用户
		StringBuilder jql = new StringBuilder(
				"select usr from BioneUserInfo usr where usr.userSts=:userSts and usr.logicSysNo=:logicSysNo  and (usr.isBuiltin=:isBuiltin or usr.isBuiltin is null) ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("isBuiltin", GlobalConstants4frame.COMMON_STATUS_INVALID);
		jql.append("  order by usr.userName ");
		List<BioneUserInfo> users = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);
		if (users == null) {
			users = new ArrayList<BioneUserInfo>();
		}
		return users;
	}
	
	/**
	 * 查找指定机构下有效的授权用户对象集合
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneUserInfo> findValidAuthUserObjOfUser(String logicSysNo , String orgNo) {
		// 查找机构状态为启用并且不包含内置的用户
		StringBuilder jql = new StringBuilder(
				"select usr from BioneUserInfo usr where usr.userSts=:userSts and usr.logicSysNo=:logicSysNo  and (usr.isBuiltin=:isBuiltin or usr.isBuiltin is null) ");
		if(StringUtils.isNotEmpty(orgNo)){
			jql.append(" and usr.orgNo=:orgNo ");
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("isBuiltin", GlobalConstants4frame.COMMON_STATUS_INVALID);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){//普通管理员不能修改自己的授权信息
			jql.append(" and (usr.isManager = 'N' or usr.isManager is null)");// 不能看到同级管理者的信息
		}
		if(StringUtils.isNotEmpty(orgNo)){			
			paramMap.put("orgNo", orgNo);
		}
		jql.append("  order by usr.userName ");
		List<BioneUserInfo> users = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);
		if (users == null) {
			users = new ArrayList<BioneUserInfo>();
		}
		return users;
	}

	/**
	 * 查找当前用户有效的授权用户对象集合（含分级授权）
	 * 
	 * @param logicSysNo
	 * @param isHierarchicalAuth
	 *            是否采用分级授权
	 * @return
	 */
	public List<BioneUserInfo> findValidAuthUserObjOfUser(String logicSysNo,
			boolean isHierarchicalAuth) {
		// 查找机构状态为启用并且不包含内置的用户
		StringBuilder jql = new StringBuilder(
				"select usr from BioneUserInfo usr where usr.userSts=:userSts and usr.logicSysNo=:logicSysNo  and (usr.isBuiltin=:isBuiltin or usr.isBuiltin is null) ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("isBuiltin", GlobalConstants4frame.COMMON_STATUS_INVALID);
		jql.append("  order by usr.userName ");
		List<BioneUserInfo> users = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);
		if (users == null) {
			users = new ArrayList<BioneUserInfo>();
		}
		if (isHierarchicalAuth) {
			// 使用分级授权，过滤出用户平级和下级的机构信息
			List<String> orgNos = BioneSecurityUtils
					.getOrgsByHierarchicalAuth();
			if (users != null && orgNos != null && orgNos.size() > 0) {
				List<BioneUserInfo> usersTmp = new ArrayList<BioneUserInfo>();
				for (BioneUserInfo userTmp : users) {
					if (!orgNos.contains(userTmp.getOrgNo())) {
						continue;
					}
					usersTmp.add(userTmp);
				}
				users = usersTmp;
			}
		}
		return users;
	}

	/**
	 * 查找当前用户有效的特定授权用户对象集合（含分级授权）
	 * 
	 * @param logicSysNo
	 * @param isHierarchicalAuth
	 * @param searchText
	 *            搜索项
	 * @return
	 */
	public List<BioneUserInfo> findValidAuthUserObjOfUser(String logicSysNo,
			boolean isHierarchicalAuth, String searchText) {
		// 查找机构状态为启用并且不包含内置的用户
		StringBuilder jql = new StringBuilder(
				"select usr from BioneUserInfo usr where usr.userSts=:userSts and usr.logicSysNo=:logicSysNo  and (usr.isBuiltin=:isBuiltin or usr.isBuiltin is null) ");
		jql.append(" and usr.userName like :userName ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("isBuiltin", GlobalConstants4frame.COMMON_STATUS_INVALID);
		paramMap.put("userName", "%" + searchText + "%");
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		if (!userObj.isSuperUser() && "Y".equals(userObj.getIsManager())) {
			// 获取用户授权机构,将授权机构挂在到
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			jql.append(" and usr.orgNo in :orgNo");
			paramMap.put("orgNo", authOrgNos);
		}
		jql.append("  order by usr.userName ");
		List<BioneUserInfo> users = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);
		if (users == null) {
			users = new ArrayList<BioneUserInfo>();
		}
		if (isHierarchicalAuth) {
			// 使用分级授权，过滤出用户平级和下级的机构信息
			List<String> orgNos = BioneSecurityUtils
					.getOrgsByHierarchicalAuth();
			if (users != null && orgNos != null && orgNos.size() > 0) {
				List<BioneUserInfo> usersTmp = new ArrayList<BioneUserInfo>();
				for (BioneUserInfo userTmp : users) {
					if (!orgNos.contains(userTmp.getOrgNo())) {
						continue;
					}
					usersTmp.add(userTmp);
				}
				users = usersTmp;
			}
		}
		return users;
	}

	/**
	 * 查找具体某个授权对象的实现类
	 * 
	 * @param authObjDefNo
	 *            授权对象标识
	 * @return
	 */
	public List<String> findAuthObjBeanNameByType(String authObjDefNo) {

		// 查找授权定义对象实现类
		String jql = "select beanName from BioneAuthObjDef def where def.objDefNo=?0";

		return this.baseDAO.findWithIndexParam(jql, authObjDefNo);
	}

	/**
	 * 查找具体某个授权资源的实现类
	 * 
	 * @param resObjDefNo
	 *            授权资源标识
	 * @return
	 */
	public List<String> findResObjBeanNameByType(String resObjDefNo) {

		// 查找授权资源实现类
		String jql = "select beanName from BioneAuthResDef def where def.resDefNo=?0";

		return this.baseDAO.findWithIndexParam(jql, resObjDefNo);
	}

	/**
	 * 查找有效的授权资源对象集合
	 * 
	 * @param logicSysNo
	 *            逻辑系统标志
	 * @return
	 */
	public List<Object[]> findValidMenu(String logicSysNo) {

		// 获取有效的菜单
		String jql = "select menu.menuId,menu.funcId,func.funcName,menu.upId,func.navIcon from BioneMenuInfo menu,BioneFuncInfo func where menu.funcId = func.funcId and menu.logicSysNo=?0 and func.funcSts=?1 order by func.orderNo,func.upId asc";

		return this.baseDAO.findWithIndexParam(jql, logicSysNo,
				GlobalConstants4frame.COMMON_STATUS_VALID);
	}

	/**
	 * 查询某个授权对象所可以访问的授权资源的关系对象集合
	 * 
	 * @param logicSysNo
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param authObjId
	 *            授权对象Id
	 * @return
	 */
	public List<BioneAuthObjResRel> findAuthResListByType(String logicSysNo,
			String authObjDefNo, String authObjId, String resDefNo) {
		String jql = "select rel from BioneAuthObjResRel rel where rel.id.logicSysNo=?0 and rel.id.objDefNo=?1 and rel.id.objId=?2 and rel.id.resDefNo=?3";
		return this.baseDAO.findWithIndexParam(jql, logicSysNo, authObjDefNo,
				authObjId, resDefNo);

	}

	/**
	 * 查询某个授权资源的所有操作许可
	 * 
	 * @param resDefNo
	 * 
	 * @param resNos
	 *            授权资源标识
	 * @return
	 */
	public List<BioneResOperInfo> findResOperList(String resDefNo,
			List<String> resNos) {

		String jql = "select oper from BioneResOperInfo oper where oper.resNo in (?0) and oper.resDefNo = ?1";

		return this.baseDAO.findWithIndexParam(jql, resNos, resDefNo);

	}

	/**
	 * 查询用户是否是逻辑系统管理员
	 * 
	 * @param userNo
	 * @param logicSysNo
	 * @return
	 */
	public boolean findAdminUserInfo(String userNo, String logicSysNo) {

		String jql = "select user from BioneAdminUserInfo admin,BioneLogicSysInfo sys,BioneUserInfo user where admin.id.logicSysId=sys.logicSysId and admin.id.userId=user.userId and user.userNo=?0 and sys.logicSysNo=?1 and user.logicSysNo=?2 and admin.userSts=?3";

		List<BioneAdminUserInfo> result = this.baseDAO.findWithIndexParam(jql,
				userNo, logicSysNo, GlobalConstants4frame.SUPER_LOGIC_SYSTEM,
				GlobalConstants4frame.COMMON_STATUS_VALID);
		if (result != null && result.size() == 1) {
			return true;
		}
		return false;

	}

	/**
	 * 获取当前逻辑系统有效的授权对象标识
	 * 
	 * @param userid
	 * @param logicsysno
	 * @return
	 */
	public List<String> getObjDefNoBySys(String logicSysNo, boolean isInput) {
		List<String> returnLst = new ArrayList<String>();
		if (logicSysNo != null && !"".equals(logicSysNo)) {
			// 若不是超级逻辑系统
			String jql = "";
			// 如果当前用户是超级管理员，查看全部
			// 如果是机构管理员，查看 角色和用户
			// 其他普通管理员，不能查看
			boolean isSuperUser = BioneSecurityUtils.getCurrentUserInfo().isSuperUser();
			String isManager = BioneSecurityUtils.getCurrentUserInfo().getIsManager();
			if (isSuperUser) {
				jql = "select rel from BioneAuthObjSysRel rel where rel.id.logicSysNo=?0";
			} else if (isManager.equals("Y")) {
				jql = "select rel from BioneAuthObjSysRel rel where rel.id.logicSysNo=?0 and rel.id.objDefNo in ('"+GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER+"','"+GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE+"')";
			} else {
				if(isInput){
					jql = "select rel from BioneAuthObjSysRel rel where rel.id.logicSysNo=?0 and rel.id.objDefNo in ('"+GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER+"','"+GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE+"')";
				} else {
					return returnLst;
				}
			}
			List<BioneAuthObjSysRel> sysRels = this.baseDAO.findWithIndexParam(
					jql, logicSysNo);
			if (sysRels != null) {
				for (int i = 0; i < sysRels.size(); i++) {
					if (!returnLst.contains(sysRels.get(i).getId()
							.getObjDefNo())) {
						returnLst.add(sysRels.get(i).getId().getObjDefNo());
					}
				}
			}
		}
		return returnLst;
	}

	/**
	 * 获取当前逻辑系统有效的资源对象标识
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<String> getResDefNoBySys(String logicSysNo) {
		List<String> returnLst = new ArrayList<String>();

		if (logicSysNo != null && !"".equals(logicSysNo)) {
			// 若不是超级逻辑系统
			String jql = "select rel from BioneAuthResSysRel rel where rel.id.logicSysNo=?0";
			List<BioneAuthResSysRel> rels = this.baseDAO.findWithIndexParam(
					jql, logicSysNo);
			if (rels != null) {
				for (int i = 0; i < rels.size(); i++) {
					if (!returnLst.contains(rels.get(i).getId().getResDefNo())) {
						returnLst.add(rels.get(i).getId().getResDefNo());
					}
				}
			}

		}
		return returnLst;
	}

	/**
	 * 根据menuId 获取 功能
	 * 
	 * @param menuId
	 * @return
	 */
	public BioneFuncInfo findFuncByMenuId(String menuId) {
		String jql = "select func from BioneFuncInfo func where func.funcId in ("
				+ "	select menuInfo.funcId from BioneMenuInfo menuInfo where menuInfo.menuId =?0 )";
		List<BioneFuncInfo> funcInfos = this.baseDAO.findWithIndexParam(jql,
				menuId);
		if (funcInfos.size() == 1) {
			return funcInfos.get(0);
		} else {
			return null;
		}

	}

	/**
	 * --author huangye 20130703 09:16 判断 授权对象是否被用户引用
	 * 
	 * @param objId
	 *            授权对象ID
	 * @param objDefNo
	 *            授权对象代码
	 * @return 被引用返回true,否则false
	 */
	public boolean checkIsObjBeUsedByUser(String objId, String objDefNo) {
		boolean flag = false;
		String jql = "SELECT userRel FROM BioneAuthObjUserRel userRel WHERE userRel.id.objId=?0 AND userRel.id.objDefNo=?1";
		List<BioneAuthObjUserRel> userRelList = this.baseDAO
				.findWithIndexParam(jql, objId, objDefNo);
		if (userRelList != null && userRelList.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 判断用户是否与对象进行绑定 --author huangye
	 * 
	 * @param userId
	 *            用户编号
	 * @return 用户与对象绑定了则返回true,否则 false
	 */
	public boolean checkIsUserUsedObj(String userId) {
		boolean flag = false;
		String jql = "SELECT rel FROM BioneAuthObjUserRel rel WHERE rel.id.userId=?0";
		List<BioneAuthObjUserRel> relList = this.baseDAO.findWithIndexParam(
				jql, userId);
		if (relList != null && relList.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 查找当前用户有效的授权岗位
	 * 
	 * @return
	 */
	public List<BionePosiInfo> findValidAuthPosiOfUser() {
		String jql = "SELECT posi FROM BionePosiInfo posi WHERE posi.logicSysNo=?0 AND posi.posiSts =?1";
		return this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo(),
				GlobalConstants4frame.COMMON_STATUS_VALID);
	}

	/**
	 * 查找当前用户有效的授权岗位（含分级授权逻辑）
	 * 
	 * @parma isHierarchicalAuth 是否使用分级授权
	 * @return
	 */
	public List<BionePosiInfo> findValidAuthPosiOfUser(
			boolean isHierarchicalAuth) {
		StringBuilder jql = new StringBuilder(
				"SELECT posi FROM BionePosiInfo posi WHERE posi.logicSysNo=:logicSysNo AND posi.posiSts =:posiSts");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		paramMap.put("posiSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		List<BionePosiInfo> posis = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);
		if (isHierarchicalAuth) {
			// 使用分级授权，过滤出用户平级和下级的机构信息
			List<String> orgNos = BioneSecurityUtils
					.getOrgsByHierarchicalAuth();
			if (posis != null && orgNos != null && orgNos.size() > 0) {
				List<BionePosiInfo> posisTmp = new ArrayList<BionePosiInfo>();
				for (BionePosiInfo posiTmp : posis) {
					if (!orgNos.contains(posiTmp.getOrgNo())) {
						continue;
					}
					posisTmp.add(posiTmp);
				}
				posis = posisTmp;
			}
		}
		return posis;
	}

	/**
	 * 查找当前用户有效的授权岗位（含分级授权逻辑）
	 * 
	 * @parma isHierarchicalAuth 是否使用分级授权
	 * @return
	 */
	public List<BionePosiInfo> findValidAuthPosiOfUser(
			boolean isHierarchicalAuth, String orgNo) {
		StringBuilder jql = new StringBuilder(
				"SELECT posi FROM BionePosiInfo posi WHERE posi.logicSysNo=:logicSysNo AND posi.posiSts =:posiSts and posi.orgNo=:orgNo");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		paramMap.put("posiSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		paramMap.put("orgNo", orgNo);
		List<BionePosiInfo> posis = this.baseDAO.findWithNameParm(
				jql.toString(), paramMap);

		return posis;
	}

	/**
	 * 查找当前用户有效的授权岗位
	 * 
	 * @param orgNo
	 *            机构编号
	 * @return
	 */
	public List<BionePosiInfo> findValidAuthPosiOfUser(String orgNo) {
		String jql = "SELECT posi FROM BionePosiInfo posi WHERE posi.logicSysNo=?0 AND posi.posiSts =?1 AND posi.orgNo=?2";
		return this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils
				.getCurrentUserInfo().getCurrentLogicSysNo(),
				GlobalConstants4frame.COMMON_STATUS_VALID, orgNo);
	}

	/**
	 * 查找当前用户有效的授权部门id
	 * 
	 * @param loigcSysNo
	 *            逻辑系统
	 * @param authObjDefNo
	 *            授权对象标识
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<String> findValidAuthPosiIdOfUser(String loigcSysNo,
			String authObjDefNo, String userId) {

		String jql = "select posi.posiId from BioneAuthObjUserRel rel,BionePosiInfo posi where rel.id.userId=?0 and rel.id.objDefNo=?1 and rel.id.objId=posi.posiId and posi.posiSts=?2 and posi.logicSysNo=?3  and rel.id.logicSysNo=?3";

		return this.baseDAO.findWithIndexParam(jql, userId, authObjDefNo,
				GlobalConstants4frame.COMMON_STATUS_VALID, loigcSysNo);
	}

	/**
	 * 获取当前用户与制定授权对象的从属关系
	 * 
	 * @param objDefNo
	 * @return
	 */
	public List<String> getCurUserAuthObjRel(String objDefNo) {
		List<String> objIds = new ArrayList<String>();
		if (!StringUtils.isEmpty(objDefNo)) {
			String jql = "select rel.id.objId from BioneAuthObjUserRel rel where rel.id.objDefNo = ?0 and rel.id.userId = ?1 and rel.id.logicSysNo = ?2";
			objIds = this.baseDAO.findWithIndexParam(jql, objDefNo,
					BioneSecurityUtils.getCurrentUserId(), BioneSecurityUtils
							.getCurrentUserInfo().getCurrentLogicSysNo());
			if (objIds == null) {
				objIds = new ArrayList<String>();
			}
		}
		return objIds;
	}

	/**
	 * 获取目标机构下的子机构编号
	 * 
	 * @param orgNo
	 *            机构编号
	 * @param levelType
	 *            分级授权类型
	 * @return
	 */
	public List<String> getChildOrgNos(String orgNo, String levelType) {
		List<String> childOrgs = new ArrayList<String>();
		if (!StringUtils.isEmpty(orgNo)) {
			StringBuilder paramJql = new StringBuilder("");
			if (GlobalConstants4frame.AUTH_LEVEL_TYPE_NEXT.equals(levelType)) {
				paramJql.append("'%/").append(orgNo)
						.append("/'||org.orgNo||'/'");
			} else {
				paramJql.append("'%/").append(orgNo).append("/%'");
			}
			StringBuilder jql = new StringBuilder(
					"select org.orgNo from BioneOrgInfo org where org.namespace like ")
					.append(paramJql);
			childOrgs = this.baseDAO.findWithNameParm(jql.toString(),
					new HashMap<String, Object>());
			if (!childOrgs.contains(orgNo)) {
				childOrgs.add(orgNo);
			}
		}
		return childOrgs;
	}

	public List<String> checkIsParent(String upOrgNo,
			boolean isHierarchicalAuth, String logicSysNo) {
		if (GlobalConstants4frame.TREE_ROOT_NO.equals(upOrgNo)) {
			if (isHierarchicalAuth) {
				upOrgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
				String upOrgJql = "select org.upNo from  BioneOrgInfo org where org.logicSysNo=:logicSysNo and org.orgSts=:orgSts and org.orgNo=:orgNo";
				Map<String, Object> upOrgparamMap = new HashMap<String, Object>();
				upOrgparamMap.put("logicSysNo", logicSysNo);
				upOrgparamMap
						.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
				upOrgparamMap.put("orgNo", upOrgNo);
				List<String> upOrg = this.baseDAO.findWithNameParm(
						upOrgJql.toString(), upOrgparamMap);
				upOrgNo = upOrg.get(0);
			} else {
				upOrgNo = "0";
			}
		}
		StringBuilder jql = new StringBuilder(
				"select distinct(org.orgNo) from BioneOrgInfo org,BioneOrgInfo test where org.logicSysNo=:logicSysNo and org.orgSts=:orgSts and org.upNo=:upNo and org.orgNo=test.upNo");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("orgSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		paramMap.put("upNo", upOrgNo);
		List<String> orgs = this.baseDAO.findWithNameParm(jql.toString(),
				paramMap);
		return orgs;
	}
	
	public List<String> checkIsParent4Dept(String logicSysNo , String... searchText) {
		StringBuilder jql = new StringBuilder("select distinct(dept.orgNo) from BioneDeptInfo dept where dept.logicSysNo=:logicSysNo and dept.deptSts=:deptSts");
		String searchTextStr = "";
		if (searchText.length > 0 && StringUtils.isNotEmpty(searchText[0])) {
			searchTextStr = searchText[0];
		}
		Map<String , Object> paramMap = new HashMap<String , Object>();
		paramMap.put("logicSysNo", logicSysNo);
		paramMap.put("deptSts", GlobalConstants4frame.COMMON_STATUS_VALID);
		if(StringUtils.isNotEmpty(searchTextStr)){
			jql.append(" and dept.deptName like :searchText");
			paramMap.put("searchText", "%"+searchTextStr+"%");
		}
		List<String> orgs = this.baseDAO.findWithNameParm(jql.toString(), paramMap);
		return orgs;
	}
	
	public List<String> getNoticesByUserRel() {
		BioneUser currUser = BioneSecurityUtils.getCurrentUserInfo();
		List<String> result = new ArrayList<>();
		String jql = "select usrRel.id.objDefNo from BioneAuthObjUserRel usrRel where usrRel.id.userId = ?0 and usrRel.id.logicSysNo = ?1";
		List<String> objDefNos = this.baseDAO.findWithIndexParam(jql,currUser.getUserId(),currUser.getCurrentLogicSysNo());
		List<String> objDefNoList = objDefNos.stream().distinct().collect(Collectors.toList());
		for (String objDefNo : objDefNoList) {
			if(GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER.equals(objDefNo)){
				String jqlUsr = "select rel.id.announcementId from BioneMsgAuthObjRel rel where rel.id.logicSysNo = ?0 and rel.objDefNo = ?1 and rel.id.objId = ?2 ";
				List<String> list = this.baseDAO.findWithIndexParam(jqlUsr, currUser.getCurrentLogicSysNo(),GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER,currUser.getUserId());
				result.addAll(list);
			} else if (GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE.equals(objDefNo)) {
				List<String> objRole = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);

				String jqlUsr = "select rel.id.announcementId from BioneMsgAuthObjRel rel where rel.id.logicSysNo = ?0 and rel.objDefNo = ?1 and rel.id.objId in ?2 ";
				List<String> list = this.baseDAO.findWithIndexParam(jqlUsr, currUser.getCurrentLogicSysNo(),GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE,objRole);
				result.addAll(list);
			} else if (GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG.equals(objDefNo)) {
				List<String> orgList = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);				
				String jqlUsr = "select rel.id.announcementId from BioneMsgAuthObjRel rel where rel.id.logicSysNo = :logicSysNo and rel.objDefNo = :objDefNo ";
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("logicSysNo", currUser.getCurrentLogicSysNo());
				param.put("objDefNo", GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
				if(orgList.size() > 1000){
					List<List<String>> objIdsList = SplitStringBy1000.change(orgList);
					jqlUsr += "and (";
					for(int i=0; i<objIdsList.size(); i++){
						if(i == objIdsList.size()-1){
							jqlUsr += "rel.id.objId in (:objIds"+i+"))";
						}else{
							jqlUsr += "rel.id.objId in (:objIds"+i+") or ";
						}
						param.put("objIds"+i, objIdsList.get(i));
					}
				}else{
					param.put("objIds", orgList);
					jqlUsr += "and rel.id.objId in (:objIds)";
				}

				List<String> list = this.baseDAO.findWithNameParm(jqlUsr, param);
				result.addAll(list);
			}
		}
		return result;
	}

	public List<BioneAuthObjResRel> getPremissionByUrl(String url) {
		List<BioneAuthObjResRel> result = new ArrayList<>();

		//根据url查询菜单
		List<BioneMenuInfo> menuInfoByUrl = menuBS.getMenuInfoByUrl(url);
		if(menuInfoByUrl == null || menuInfoByUrl.size() == 0){
			return result;
		}
		String userId = BioneSecurityUtils.getCurrentUserInfo().getUserId();
		List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
		List<String> authRoles = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
		String[] objDefNoArr = {GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER, GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG,GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE};
		for (String objDefNo : objDefNoArr) {
			Map<String, Object> param = new HashMap<>();
			param.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
			param.put("objDefNo", objDefNo);
			param.put("resDefNo", GlobalConstants4frame.AUTH_RES_DEF_ID_MENU);
			param.put("resId", menuInfoByUrl.get(0).getMenuId());
			if (objDefNo.equals(GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER)) {
				param.put("objId", userId);
			} else if (objDefNo.equals(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG)) {
				param.put("objIds", SplitStringBy1000.change(authOrgNos));
			} else if (objDefNo.equals(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE)) {
				param.put("objIds", SplitStringBy1000.change(authRoles));
			}
			List<BioneAuthObjResRel> list = authMybatisDao.getBioneAuthObjResRel(param);
			result.addAll(list);
		}
		return result;
	}
}
