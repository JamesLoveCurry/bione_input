/**
 * 
 */
package com.yusys.bione.frame.auth.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.CollectionsUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRelPK;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.auth.repository.AuthMybatisDao;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.PERMISSION_ALL;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.RES_PERMISSION_TYPE_OPER;

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
public class AuthObjBS extends BaseBS<BioneAuthObjResRel> {
	@Autowired ExcelBS excelBS;

	@Autowired
	private AuthMybatisDao authMybatisDao;

	@Autowired
	private AuthBS authBS;
	/**
	 * 查询某个授权对象所有授权资源许可关系
	 * 
	 * @param logicSysNo
	 * @param objDefNo
	 *            授权对象标识
	 * @param objId
	 *            授权对象Id
	 * @return
	 */
	public List<BioneAuthObjResRel> findAuthObjRelByObj(String logicSysNo,
			String objDefNo, String objId, String resDefNo) {
		String jql = "select rel from BioneAuthObjResRel rel where rel.id.logicSysNo=?0 and rel.id.objDefNo.id.objDefNo=?1 and rel.id.objId=?2 and rel.id.resDefNo=?3";

		return this.baseDAO
				.findWithIndexParam(jql, logicSysNo, objDefNo, objId, resDefNo);
	}

	/**
	 * 获取传入集合中已配置了菜单权限
	 * 
	 * @param rels
	 * @return list
	 */
	public List<BioneResOperInfo> findHasOperRess(List<BioneAuthObjResRel> rels) {
		if (rels != null) {
			List<String> defNos = new ArrayList<String>();
			List<String> nos = new ArrayList<String>();
			for (int i = 0; i < rels.size(); i++) {
				BioneAuthObjResRel rel = rels.get(i);
				if (RES_PERMISSION_TYPE_OPER.equals(rel.getId()
						.getPermissionType())
						&& PERMISSION_ALL.equals(rel.getId().getPermissionId())) {
					// 若是操作许可类型,且从页面获取的授权许可为空
					if (!defNos.contains(rel.getId().getResDefNo())) {
						defNos.add(rel.getId().getResDefNo());
					}
					if (!nos.contains(rel.getId().getResId())) {
						nos.add(rel.getId().getResId());
					}
				}
			}
			if (defNos.size() == 0 || nos.size() == 0) {
				return null;
			}
			Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
			paramMap.put("defNo", defNos);
			String jql = "select rel from BioneResOperInfo rel where rel.resDefNo in :defNo";
			List<BioneResOperInfo> opersTmp = this.baseDAO.findWithNameParm(jql, paramMap);
			List<BioneResOperInfo> returnLst = Lists.newArrayList();
			for(BioneResOperInfo operTmp : opersTmp){
				if(nos.contains(operTmp.getResNo())){
					returnLst.add(operTmp);
				}
			}
			return returnLst;
		}
		return null;
	}

	/**
	 * 更新授权对象与资源关系
	 * 
	 * @param oldRels
	 *            旧关系
	 * @param newRels
	 *            新关系
	 * @return
	 */
	@Transactional(readOnly = false)
	public void updateRelBatch(List<BioneAuthObjResRel> oldRels,
			List<BioneAuthObjResRel> newRels) {
		
		if (oldRels == null || newRels == null) {
			return;
		}
		// 20200604 更新新增量数据 避免全量与数据库交互 影响性能
		// 删除内容 
//		List<BioneAuthObjResRel> deleteRels = Lists.newArrayList();
//		//旧授权
//		for(BioneAuthObjResRel oldRel : oldRels) {
//			// 如果新授权中未匹配到旧授权 说明已经被删除
//			if(!newRels.contains(oldRel)) {
//				deleteRels.add(oldRel);
//			}
//		}
		// 新增内容
//		List<BioneAuthObjResRel> saveRels = Lists.newArrayList();
//		// 新授权
//		for(BioneAuthObjResRel newRel : newRels) {
//			if(!oldRels.contains(newRel)) {
//				saveRels.add(newRel);
//			}
//		}
		
		// 增量 删除内容 执行 deleteEntityJdbcBatch
		if(oldRels.size() > 0) {
			// 删除增加过滤条件 
			List<String> fields = Lists.newArrayList();
			fields.add("id.logicSysNo");
			fields.add("id.objDefNo");
			fields.add("id.resDefNo");
			fields.add("id.permissionType");
			fields.add("id.objId");
			fields.add("id.resId");
			fields.add("id.permissionId");
			this.excelBS.deleteEntityJdbcBatch(oldRels, fields);
		}
		// 增量 新增内容 执行 saveEntityJdbcBatch
		if(newRels.size() > 0) {
			this.excelBS.saveEntityJdbcBatch(newRels);
		}
	}

	/**
	 * 判断对象是否与资源关联
	 * --author huangye
	 * @param objId 对象ID
	 * @param objDefNo 对象定义标识
	 * @return 若有关联则返回true,否则false
	 */
	public boolean checkIsObjBeUsedByRes(String objId,String objDefNo){
		boolean flag=false;
		String jql="SELECT rel FROM BioneAuthObjResRel rel WHERE rel.id.objDefNo=?0 AND rel.id.objId=?1";
		List<BioneAuthObjResRel> relList=this.baseDAO.findWithIndexParam(jql, objDefNo,objId);
		if(relList!=null&&relList.size()>0){
			flag=true;
		}
		return flag;
	}

	/**
	 * 获取对象对应资源
	 *
	 * @param pager    分页参数
	 * @param id       objectid
	 * @return map
	 */
	public Map<String, Object> getObjectDefNoResourceMap(Pager pager, String id, String objDefNo) {
		Map<String, String> pagerMap = getType(pager);
		pager.setCondition("");
		Map<String, Object> userObjResRelMap = Maps.newHashMap();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		PageHelper.startPage(pager);
		Map<String, Object> params = Maps.newHashMap();
		params.put("objId", id);
		params.put("objDefNo", objDefNo);
		params.put("resDefNo", pagerMap.get("resDefNo"));
		params.put("logicSysNo", userObj.getCurrentLogicSysNo());
		if (StringUtils.isNotBlank(pagerMap.get("resName")) && "AUTH_OBJ_USER".equals(objDefNo)) {
			params.put("resName", pagerMap.get("resName"));
		}
		PageMyBatis<BioneAuthObjResRelPK> pageList = getBioneAuthObjResRelPageList(objDefNo, params);
		if (CollectionsUtils.isNotEmpty(pageList)) {
			List<CommonTreeNode> list = new ArrayList<>();
			String name = "";
			Map<String, Object> param = Maps.newHashMap();
			param.put("resDefNo", pagerMap.get("resDefNo"));
			List<BioneAuthResDef> bioneAuthResDefList = authMybatisDao.getBioneAuthResDefInfo(param);
			if (CollectionsUtils.isNotEmpty(bioneAuthResDefList)) {
				name = bioneAuthResDefList.get(0).getResName();
			}
			for (BioneAuthObjResRelPK rel : pageList) {
				if (CollectionsUtils.isEmpty(list)) {
					list = getAuthResDefTree(pagerMap.get("resDefNo"));
				}
				rel.setResDefNo(name);
				rel.setObjDefNo(GlobalConstants4frame.map.get(rel.getObjDefNo()));
				String resId = rel.getResId();
				for (CommonTreeNode commonTreeNode : list) {
					if (commonTreeNode.getId().contains(resId)) {
						rel.setResId(commonTreeNode.getText());
					}
				}
			}
		}
		userObjResRelMap.put("Total", pageList.getTotalCount());
		if (StringUtils.isNotBlank(pagerMap.get("resName")) && "AUTH_OBJ_ROLE".equals(objDefNo)) {
			for (int i = 0; i < pageList.size(); i++) {
				if(!pageList.get(i).getResId().contains(pagerMap.get("resName"))){
					pageList.remove(i);
					i--;
				}
			}
			userObjResRelMap.put("Total", pageList.size());
		}
		getPageList(pagerMap, userObjResRelMap, pageList);
		userObjResRelMap.put("Rows", pageList.getResult());
		return userObjResRelMap;
	}

	/**
	 *  获取查询类型参数
	 * @param pager 分页参数
	 * @return Map
	 */
	private Map<String, String> getType(Pager pager) {
		Map<String, String> params = Maps.newHashMap();
		Map map = (Map) JSON.parse(pager.getCondition());
		JSONArray rulesJson = JSONArray.parseArray(JSON.toJSONString(map.get("rules")));
		for (Iterator<?> conditioniter = rulesJson.iterator(); conditioniter.hasNext();) {
			JSONObject rule = (JSONObject) conditioniter.next();
			String field = (String) rule.get("field");
			String value = (String) rule.get("value");
			params.put(field,value);
		}
		return params;
	}

	private void getPageList(Map<String, String> pagerMap, Map<String, Object> userObjResRelMap, PageMyBatis<BioneAuthObjResRelPK> pageList) {
		if (StringUtils.isNotBlank(pagerMap.get("resourceName"))) {
			for (int i = 0; i < pageList.size(); i++) {
				if(!pageList.get(i).getResId().contains(pagerMap.get("resourceName"))){
					pageList.remove(i);
					i--;
				}
			}
			userObjResRelMap.put("Total", pageList.size());
		}
	}

	private PageMyBatis<BioneAuthObjResRelPK> getBioneAuthObjResRelPageList(String objDefNo, Map<String, Object> params) {
		PageMyBatis<BioneAuthObjResRelPK> pageList;
		if ("AUTH_OBJ_USER".equals(objDefNo)) {
			pageList = (PageMyBatis<BioneAuthObjResRelPK>) this.authMybatisDao.getUserAllBioneResDefInfo(params);
		} else {
			pageList = (PageMyBatis<BioneAuthObjResRelPK>) this.authMybatisDao.getUserBioneResDefInfo(params);
		}
		return pageList;
	}

	public List<CommonTreeNode> getAuthResDefTree(String resDefNo) {
		List<CommonTreeNode> pageShowTree = null;
		if (resDefNo != null && !"".equals(resDefNo)) {
			// 获取实现类
			List<String> beanNames = this.authBS
					.findResObjBeanNameByType(resDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				// 存在至少一个授权对象实现类申明
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

	public List<CommonComboBoxNode> getCommonComboBoxNodes(List<BioneAuthResDef> resDefs) {
		// 获取该逻辑系统授权资源
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authRess = this.authBS.getResDefNoBySys(userObj
				.getCurrentLogicSysNo());
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		if (resDefs != null && resDefs.size() > 0) {
			for (BioneAuthResDef obj : resDefs) {
				if (authRess.contains(obj.getResDefNo())) {
					CommonComboBoxNode node = new CommonComboBoxNode();
					node.setId(obj.getResDefNo());
					node.setText(obj.getResName());
					nodes.add(node);
				}
			}
		}
		return nodes;
	}

	/**
	 * 根据查询授权对象,授权资源查询
	 *
	 * @param logicSysNo
	 * @param objDefNo
	 *            授权对象标识
	 * @param objId
	 *            授权对象Id
	 * @return
	 */
	public List<BioneAuthObjResRel> findAuthObjRelByObjAndRes(String logicSysNo,
														String objDefNo, String objId, String resDefNo, String resId) {
		String jql = "select rel from BioneAuthObjResRel rel where rel.id.logicSysNo=?0 " +
				"and rel.id.objDefNo.id.objDefNo=?1 and rel.id.objId=?2 and rel.id.resDefNo = ?3 and rel.id.resId = ?4";

		return this.baseDAO
				.findWithIndexParam(jql, logicSysNo, objDefNo, objId, resDefNo, resId);
	}
}
