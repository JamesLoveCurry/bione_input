/**
 * 
 */
package com.yusys.bione.frame.auth.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.CollectionsUtils;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRelPK;
import com.yusys.bione.frame.auth.repository.AuthMybatisDao;
import com.yusys.bione.frame.authobj.entity.BioneDeptInfo;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRel;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;

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
public class AuthUsrRelBS extends BaseBS<BioneAuthObjUserRel> {

	@Autowired
	private AuthMybatisDao authMybatisDao;

	/**
	 * 根据用户和逻辑系统，查询有关系的授权对象
	 * 
	 * @param logicSysNo
	 * @param userId
	 * @return
	 */
	public List<BioneAuthObjUserRel> getObjUserRelByUserId(String logicSysNo,
			String userId) {
		String jql = "select rel from BioneAuthObjUserRel rel where rel.id.logicSysNo=?0 and rel.id.userId=?1";

		return this.baseDAO.findWithIndexParam(jql, logicSysNo, userId);
	}
	
	/**
	 * 根据授权对象和逻辑系统，查询有关系的授权对象
	 * 
	 * @param logicSysNo
	 * @param objId
	 * @return
	 */
	public List<BioneAuthObjUserRel> getObjUserRelByObjId(String logicSysNo,
			String objId) {
		String jql = "select rel from BioneAuthObjUserRel rel where rel.id.logicSysNo=?0 and rel.id.objId=?1";
		
		return this.baseDAO.findWithIndexParam(jql, logicSysNo, objId);
	}

	/**
	 * 更新用户与授权对象关系
	 * 
	 * @param oldRels
	 *            旧关系
	 * @param newRels
	 *            新关系
	 * @param userOrgNo 
	 * @return
	 */
	@Transactional(readOnly = false)
	public void updateRelBatch(List<BioneAuthObjUserRel> oldRels,
			List<BioneAuthObjUserRel> newRels, String userOrgNo) {
		if (oldRels == null || newRels == null) {
			return;
		}
		// 先删除旧关系
		for (int i = 0; i < oldRels.size(); i++) {
			if(!oldRels.get(i).getId().getObjId().equals(userOrgNo)){
				this.removeEntity(oldRels.get(i));
			}
			/*//目前前台只能配置用户和角色的关系，机构关系不能配置，所以删除的时候只删除和角色的关系，机构关系保留
			if(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE.equals(oldRels.get(i).getId().getObjDefNo())) {
				this.removeEntity(oldRels.get(i));
			}*/
			if (i % 20 == 0) {
				this.baseDAO.flush();
			}
		}
		
		// 维护新关系
		for (int j = 0; j < newRels.size(); j++) {
			this.updateEntity(newRels.get(j));
			if (j % 20 == 0) {
				this.baseDAO.flush();
			}
		}
	}

	/**
	 * 获取所有授权对象定义简单信息封装JSON(用于页面动态渲染)
	 */
	public JSONArray getAllAuthObjDef() {
		List<Map<String, String>> defInfos = Lists.newArrayList();
		String jql = "select authObjDef from BioneAuthObjDef authObjDef where authObjDef.objDefNo!=?0";
		List<BioneAuthObjDef> authObjDefs = this.baseDAO.findWithIndexParam(
				jql, GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER);
		if (authObjDefs != null && authObjDefs.size() > 0) {
			for (BioneAuthObjDef def : authObjDefs) {
				Map<String, String> defInfo = Maps.newHashMap();
				defInfo.put("objDefNo", def.getObjDefNo());
				defInfo.put("objDefName", def.getObjDefName());
				defInfos.add(defInfo);
			}
		}
		return (JSONArray)JSON.toJSON(defInfos);
	}
	
	public List<BioneAuthObjUserRel> getObjUserRelByIds(
			String objId,List<String> userIds) {
		String jql = "select rel from BioneAuthObjUserRel rel where   rel.id.userId in ?0  and rel.id.objId = ?1   ";
		
		return this.baseDAO.findWithIndexParam(jql, userIds,objId);
	}

	public List<BioneAuthObjUserRelPK> getBioneAuthObjUserRelList(Pager pager, List<BioneAuthObjUserRel> list) {
		List<BioneAuthObjUserRelPK> bioneAuthObjUserRelPKList = new ArrayList<>();
		// 获取查询类型
		String type = getType(pager);
		for (BioneAuthObjUserRel objUserRel : list) {
			if (type.equalsIgnoreCase(objUserRel.getId().getObjDefNo())) {
				BioneAuthObjUserRelPK bioneAuthObjUserRelPK = new BioneAuthObjUserRelPK();
				bioneAuthObjUserRelPK.setLogicSysNo(objUserRel.getId().getLogicSysNo());
				bioneAuthObjUserRelPK.setObjDefNo(GlobalConstants4frame.map.get(objUserRel.getId().getObjDefNo()));
				bioneAuthObjUserRelPK.setUserId(objUserRel.getId().getUserId());
				bioneAuthObjUserRelPK.setObjId(objUserRel.getId().getObjId());
				if ("AUTH_OBJ_ROLE".equalsIgnoreCase(type)) {
					List<BioneRoleInfo> bioneRoleInfoList = authMybatisDao.getBioneRoleList(objUserRel.getId().getObjId());
					if (CollectionsUtils.isNotEmpty(bioneRoleInfoList)) {
						bioneAuthObjUserRelPK.setObjId(bioneRoleInfoList.get(0).getRoleName());
					}
				}
				if ("AUTH_OBJ_ORG".equalsIgnoreCase(type)) {
					List<BioneOrgInfo> bioneOrgInfoList = authMybatisDao.getBioneOrgInfo(objUserRel.getId().getObjId());
					if (CollectionsUtils.isNotEmpty(bioneOrgInfoList)) {
						bioneAuthObjUserRelPK.setObjId(bioneOrgInfoList.get(0).getOrgName());
					}
				}
				if ("AUTH_OBJ_DEPT".equalsIgnoreCase(type)) {
					List<BioneDeptInfo> bioneDeptInfoList = authMybatisDao.getBioneDept(objUserRel.getId().getObjId());
					if (CollectionsUtils.isNotEmpty(bioneDeptInfoList)) {
						bioneAuthObjUserRelPK.setObjId(bioneDeptInfoList.get(0).getDeptName());
					}
				}
				bioneAuthObjUserRelPKList.add(bioneAuthObjUserRelPK);
			}
		}
		return bioneAuthObjUserRelPKList;
	}

	/**
	 *  获取查询类型参数
	 * @param pager 分页参数
	 * @return
	 */
	private String getType(Pager pager) {
		Map maps = (Map) JSON.parse(pager.getCondition());
		JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(maps.get("rules")));
		Map rules = (Map) JSON.parse(JSON.toJSONString(jsonArray.get(0)));
		// 然后根据名称获取 名称下的数据
		return (String) rules.get("value");
	}

	public Map<String, Object> getRoleRelationMap(String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
		// 获取结果列表
		Map<String, Object> params = Maps.newHashMap();
		params.put("objId", id);
		params.put("objDefNo", "AUTH_OBJ_ROLE");
		params.put("logicSysNo", userInfo.getCurrentLogicSysNo());
		List<BioneUserInfo> bioneRoleInfoList = authMybatisDao.getBioneRoleUserList(params);
		result.put("Total", bioneRoleInfoList.size());
		result.put("Rows", bioneRoleInfoList);
		return result;
	}
}
