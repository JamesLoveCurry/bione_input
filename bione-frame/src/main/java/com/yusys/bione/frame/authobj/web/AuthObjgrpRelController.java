package com.yusys.bione.frame.authobj.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.entity.BioneAuthObjgrpObjRel;
import com.yusys.bione.frame.authobj.service.AuthObjgrpRelBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.IAuthObject;

/**
 * <pre>
 * Title:对象组与其他授权对象关系维护
 * Description: 对象组与其他授权对象关系维护Action
 * </pre>
 * 
 * @author fanll fanll@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/objgrpRelManage")
public class AuthObjgrpRelController extends BaseController {

	@Autowired
	private AuthBS authBS;

	@Autowired
	private AuthObjgrpRelBS relBS;

	/**
	 * 跳转授权对象组管理页面
	 */
	@RequestMapping("objgrpRelManage")
	public ModelAndView objgrpRelManage(String objgrpId, String logicSysNo) {
		// 动态决定授权对象tab
		/*List<BioneAuthObjDef> authObjDefList = BioneSecurityUtils.getCurrentUserInfo().isSuperUser() ? 
				this.authBS.getAuthObjDefWithNoUsr() : 
				this.authBS.getAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());*/
		List<BioneAuthObjDef> authObjDefList =  
				this.authBS.getAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		List<BioneAuthObjDef> authObjDefList_ = Lists.newArrayList(authObjDefList);
		for (BioneAuthObjDef authObjDef : authObjDefList) {
			if (GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP.equals(authObjDef.getObjDefNo())) {
				authObjDefList_.remove(authObjDef);
			}
		}
		ModelMap mm = new ModelMap();
		mm.put("objgrpId", StringUtils2.javaScriptEncode(objgrpId));
		mm.put("logicSysNo", StringUtils2.javaScriptEncode(logicSysNo));
		mm.put("authObjDefs", StringUtils2.javaScriptEncode(JSON.toJSONString(authObjDefList_)));
		return new ModelAndView("/frame/auth/auth-objgrp-rel-manage", mm);
	}

	/**
	 * 根据不同授权对象类型获取授权对象树
	 */
	@RequestMapping("/getAuthObjTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthObjTree(String objDefNo) {
		List<CommonTreeNode> authObjTree = new ArrayList<CommonTreeNode>();
		if (objDefNo == null || "".equals(objDefNo))
			return authObjTree;
		List<String> beanNames = this.authBS.findAuthObjBeanNameByType(objDefNo);
		if (beanNames != null && beanNames.size() > 0) {
			//通过名称获取授权对象实现类
			String beanName = beanNames.get(0);
			IAuthObject authObj = SpringContextHolder.getBean(beanName);
			if (authObj != null) {
				authObjTree = authObj.doGetAuthObjectInfo();

				if (authObjTree != null && authObjTree.size() > 0) {
					//处理图标
					for (CommonTreeNode node : authObjTree) {
						node.setIcon(this.getRequest().getContextPath() + "/" + (node.getIcon()));
					}
				}
			}
		}
		return authObjTree;
	}

	/**
	 * 获取当前授权对象组与指定授权对象的关系集合
	 */
	@RequestMapping("/findRelByAuthObjgrpAndAuthObj.*")
	@ResponseBody
	public List<BioneAuthObjgrpObjRel> findRelByAuthObjgrpAndAuthObj(String objgrpId, String objDefNo, String logicSysNo) {
		if (objgrpId != null && !"".equals(objgrpId)) {
			return relBS.getAuthObjgrpRelByObjgrpId(logicSysNo, objgrpId, objDefNo);
		}
		return null;

	}

	/**
	 * 保存授权对象组关系
	 */
	@RequestMapping("/saveObjgrpRels")
	@ResponseBody
	public void saveObjgrpRels(String logicSysNo, String objgrpId, String relsJson) {
		//将JSON转为java对象集合
		JSONObject jsonobject = JSON.parseObject(relsJson);

		JSONArray jsonArray = jsonobject.getJSONArray("relsJson");
		List<BioneAuthObjgrpObjRel> relList = new ArrayList<BioneAuthObjgrpObjRel>();

		for (int i = 0; i < jsonArray.size(); i++) {
			BioneAuthObjgrpObjRel enumitemVO = jsonArray.getObject(i, BioneAuthObjgrpObjRel.class);
			if (enumitemVO != null) {
				relList.add(enumitemVO);
			}
		}
		this.relBS.updateAuthObjgrpRelBatch(logicSysNo, objgrpId, relList);
	}

}
