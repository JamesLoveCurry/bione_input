/**
 * 
 */
package com.yusys.bione.frame.mainpage.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mainpage.entity.BioneMpDesignFunc;
import com.yusys.bione.frame.mainpage.service.MainpageDesignBS;
import com.yusys.bione.frame.mainpage.service.MainpageRelBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:基础首页相关维护功能controller
 * Description: 基础首页相关维护功能controller
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
@RequestMapping("/bione/mainpage/rel")
public class MainpageRelController extends BaseController {
	protected static Logger log = LoggerFactory
			.getLogger(MainpageRelController.class);
	private String moduleTreeIcon = "/images/classics/icons/bricks.png";
	@Autowired
	private AuthBS authBS;
	
	@Autowired
	private MainpageDesignBS mainpageDesignBS;
	
	@Autowired
	private MainpageRelBS mainpageRelBS;
	/**
	 * 公共首页用户关系配置
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		List<BioneAuthObjDef> authObjDefList = this.authBS.getAuthObjDefBySys(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		// 将授权对象定义传至页面
		return new ModelAndView("/frame/mainpage/mainpage-rel-index", "authObjDefs", 
				StringUtils2.javaScriptEncode(JSON.toJSONString(authObjDefList)));
	}
	
	@RequestMapping(value = "getMainPageTree",method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getMainPageTree() {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		List<BioneMpDesignFunc> funcs =  this.mainpageDesignBS.getMainPageTree();
		if(funcs != null && funcs.size() > 0){
			for(BioneMpDesignFunc func : funcs){
				CommonTreeNode node = new CommonTreeNode();
				node.setId(func.getDesignId());
				node.setText(func.getDesignNm());
				node.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
				node.setIcon( this.getContextPath()+ moduleTreeIcon);
				nodes.add(node);
			}
		}
		return nodes;
	}
	/**
	 * 保存公共首页关系
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void save(String userIds,String designId) {
		String userId[] = StringUtils.split(userIds,",");
		this.mainpageRelBS.saveRel(userId, designId);
	}
	
	

}
