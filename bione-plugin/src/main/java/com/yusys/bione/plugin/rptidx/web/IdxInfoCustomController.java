package com.yusys.bione.plugin.rptidx.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;

/**
 * 
 * <pre>
 * Title:
 * Description: 
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/report/frame/idx/custom")
public class IdxInfoCustomController extends BaseController {
	@Autowired
	private UserBS userBS;
	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() { 
		ModelMap mm = new ModelMap();
      	mm.put("treeRootNo", GlobalConstants4frame.TREE_ROOT_NO);
      	mm.put("INDEX_DEF_SRC_ORG",GlobalConstants4plugin.INDEX_DEF_SRC_ORG);
      	mm.put("INDEX_DEF_SRC_USER",GlobalConstants4plugin.INDEX_DEF_SRC_USER);
      	mm.put("rootIcon", GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
      	BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
		userInfo.setUserNo(userBS.getUserNo(BioneSecurityUtils.getCurrentUserId()));
		mm.put("userInfo", JSON.toJSON(userInfo));
      	return new ModelAndView("/plugin/rptidx/idx-info-custom-index",mm);
	}
}