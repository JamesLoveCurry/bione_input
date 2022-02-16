package com.yusys.bione.frame.auth.web;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
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
 * @author pengwei pengwei@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/authObjUsrRel")
public class AuthObjUsrRelController extends BaseController {

	@Autowired
	private AuthBS authBS;

	@Autowired
	private AuthUsrRelBS relBS;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		List<BioneAuthObjDef> authObjDefList = this.authBS.getAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		// 将授权对象定义传至页面
		return new ModelAndView("/frame/auth/auth-obj-usr-rel-index", "authObjDefs", 
				StringUtils2.javaScriptEncode(JSON.toJSONString(authObjDefList)));
	}

	// 获取用户树
	@RequestMapping("/getUserTree.*")
	@ResponseBody
	public List<CommonTreeNode> getUserTree(String searchText) {
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
					pageShowTree = authObj.doGetAuthObjectInfo(searchText);
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
			List<String> beanNames = this.authBS
					.findAuthObjBeanNameByType(objDefNo);
			if (beanNames != null && beanNames.size() > 0) {
				// 存在至少一个授权对象实现类申明
				String beanName = beanNames.get(0);
				IAuthObject authObj = SpringContextHolder.getBean(beanName);
				if (authObj != null) {
//					pageShowTree = authObj.doGetAuthObjectInfo();
					pageShowTree = authObj.doGetAuthObjectInfoAsync(node);
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

	// 获取用户与授权对象关系
	@RequestMapping("/getObjUserRel.*")
	@ResponseBody
	public List<BioneAuthObjUserRel> getObjUserRel(String objId) {
		if (objId != null && !"".equals(objId)) {
			BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
			return this.relBS.getObjUserRelByObjId(
					userInfo.getCurrentLogicSysNo(), objId);
		}
		return null;
	}

	// 保存对象用户关系
	@RequestMapping("/saveObjUserRel")
	public void saveObjUserRel(String relObjs) {
		if (relObjs != null && !"".equals(relObjs)) {
			// 当前用户信息
			BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
			JSONObject rels = JSON.parseObject(relObjs);
			String objDefNo = rels.getString("objDefNo");
			String objId = rels.getString("objId");
			String userIds = rels.getString("userIds");
			// 新关系集合
			List<BioneAuthObjUserRel> newRels = new ArrayList<BioneAuthObjUserRel>();
			// 获取旧关系
			List<BioneAuthObjUserRel> oldRels = this.relBS.getObjUserRelByObjId(userObj.getCurrentLogicSysNo(), objId);
			for (String  userId : StringUtils.split(userIds, ',')) {
				BioneAuthObjUserRel newRel = new BioneAuthObjUserRel();
				BioneAuthObjUserRelPK pk = new BioneAuthObjUserRelPK();
				pk.setLogicSysNo(userObj.getCurrentLogicSysNo());
				pk.setObjDefNo(objDefNo);
				pk.setUserId(userId);
				pk.setObjId(objId);
				newRel.setId(pk);
				
				newRels.add(newRel);
			}
			this.relBS.updateRelBatch(oldRels, newRels, null);
		}
	}

}
