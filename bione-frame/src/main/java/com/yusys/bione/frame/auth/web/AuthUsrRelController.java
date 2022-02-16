package com.yusys.bione.frame.auth.web;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER;

import java.util.*;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.CollectionsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRel;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRelPK;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthUsrRelBS;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IAuthObject;

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
@Controller
@RequestMapping("/bione/admin/authUsrRel")
public class AuthUsrRelController extends BaseController {

	@Autowired
	private AuthBS authBS;

	@Autowired
	private AuthUsrRelBS relBS;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		List<BioneAuthObjDef> authObjDefList = this.authBS.getAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		// 将授权对象定义传至页面
		return new ModelAndView("/frame/auth/auth-usr-rel-index", "authObjDefs", 
				StringUtils2.javaScriptEncode(JSON.toJSONString(authObjDefList)));
	}

	// 获取用户树
	@RequestMapping("/getUserTree.*")
	@ResponseBody
	public List<CommonTreeNode> getUserTree(CommonTreeNode node , String searchText) {
		List<CommonTreeNode> pageShowTree = new ArrayList<CommonTreeNode>();
		String objDefNo = AUTH_OBJ_DEF_ID_USER;
		if (objDefNo != null && !"".equals(objDefNo)) {
			// 获取实现类
			List<String> beanNames = this.authBS
					.findAuthObjBeanNameByType(objDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				// 存在至少一个授权对象实现类申明
				String beanName = beanNames.get(0);
				IAuthObject authObj = SpringContextHolder.getBean(beanName);
				if (authObj != null) {
					pageShowTree = authObj.doGetAuthObjectInfoAsync(node, searchText);
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

	// 获取指定授权对象树
	@RequestMapping("/getAuthObjTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthObjTree(String objDefNo, CommonTreeNode node) {
		List<CommonTreeNode> pageShowTree = new ArrayList<CommonTreeNode>();
		if (objDefNo == null || "".equals(objDefNo)) {
			return pageShowTree;
		}
		if (objDefNo != null && !"".equals(objDefNo)) {
			// 获取实现类
			List<String> beanNames = this.authBS.findAuthObjBeanNameByType(objDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				// 存在至少一个授权对象实现类申明
				String beanName = beanNames.get(0);
				IAuthObject authObj = SpringContextHolder.getBean(beanName);
				if (authObj != null) {
					// pageShowTree = authObj.doGetAuthObjectInfo();
					pageShowTree = authObj.doGetAuthObjectInfoAsync(node);
					if (pageShowTree != null) {
						for (int i = 0; i < pageShowTree.size(); i++) {
							(pageShowTree.get(i)).setIcon(this.getRequest().getContextPath() + "/" + (pageShowTree.get(i)).getIcon());
						}
					}
				}
			}
		}
		return pageShowTree;
	}

	// 获取用户与授权对象关系
	@RequestMapping("/getObjUserRel.*")
	@ResponseBody
	public List<BioneAuthObjUserRel> getObjUserRel(String objId) {
		if (objId != null && !"".equals(objId)) {
			BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
			return this.relBS.getObjUserRelByUserId(
					userInfo.getCurrentLogicSysNo(), objId);
		}
		return null;
	}

	@RequestMapping(value = "/userRelation.*")
	@ResponseBody
	public Map<String, Object> getObjUserRelationList(Pager pager, String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 获取结果列表
		List<BioneAuthObjUserRelPK> bioneAuthObjUserRelPKList = getBioneAuthObjUserRelList(pager, id);
		result.put("Total", bioneAuthObjUserRelPKList.size());
		result.put("Rows", bioneAuthObjUserRelPKList);
		return result;
	}

	/**
	 *  获取结果列表
	 * @param pager 分页参数
	 * @param id id
	 * @return list
	 */
	private List<BioneAuthObjUserRelPK> getBioneAuthObjUserRelList(Pager pager, String id) {
		BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
		List<BioneAuthObjUserRel> list = this.relBS.getObjUserRelByUserId(
				userInfo.getCurrentLogicSysNo(), id);
		return this.relBS.getBioneAuthObjUserRelList(pager, list);
	}

	@RequestMapping(value = "/roleUserRelation.*")
	@ResponseBody
	public Map<String, Object> roleUserRelation(Pager pager, String id) {
		return this.relBS.getRoleRelationMap(id);
	}

	/**
	 * 获取用户授权对象类型
	 * @return
	 */
	@RequestMapping(value = "/getObjUserRelationType.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getObjUserRelationType(){
        List<BioneAuthObjDef> bioneAuthObjDefList = this.authBS.getAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		if(CollectionsUtils.isNotEmpty(bioneAuthObjDefList)){
			for(BioneAuthObjDef bioneAuthObjDef : bioneAuthObjDefList){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(bioneAuthObjDef.getObjDefNo());
				node.setText(bioneAuthObjDef.getObjDefName());
				nodes.add(node);
			}
		}

		return nodes;
	}

	// 保存
	@RequestMapping("/saveObjUserRel")
	public void saveObjUserRel(String relObjs, String userOrgNo) {
		if (relObjs != null && !"".equals(relObjs)) {
			// 当前用户信息
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			JSONObject rels = JSON.parseObject(relObjs);
			String userId = rels.getString("userId");
			JSONArray objs = rels.getJSONArray("objs");
			// 新关系集合
			List<BioneAuthObjUserRel> newRels = new ArrayList<BioneAuthObjUserRel>();
			// 获取旧关系
			List<BioneAuthObjUserRel> oldRels = this.relBS
					.getObjUserRelByUserId(userObj.getCurrentLogicSysNo(),
							userId);
			for (int k = 0; k < objs.size(); k ++) {
				JSONObject oi = objs.getJSONObject(k);
				String objDefNo = oi.getString("objDefNo");
				String objIds = oi.getString("objIds");
				if (objIds != "" && !"".equals(objIds)) {
					String[] objIdsArray = StringUtils.split(objIds, ',');
					for (int i = 0; i < objIdsArray.length; i++) {
						BioneAuthObjUserRel newRel = new BioneAuthObjUserRel();
						BioneAuthObjUserRelPK pk = new BioneAuthObjUserRelPK();
						pk.setLogicSysNo(userObj.getCurrentLogicSysNo());
						pk.setObjDefNo(objDefNo);
						pk.setUserId(userId);
						pk.setObjId(objIdsArray[i]);
						newRel.setId(pk);

						newRels.add(newRel);
					}
				}
			}
			this.relBS.updateRelBatch(oldRels, newRels, userOrgNo);
		}
	}

}
