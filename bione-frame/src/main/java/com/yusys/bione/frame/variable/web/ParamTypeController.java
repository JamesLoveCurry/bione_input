package com.yusys.bione.frame.variable.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneParamTypeInfo;
import com.yusys.bione.frame.variable.service.ParamTypeBS;

/**
 * <pre>
 * Title:参数类型处理Action类
 * Description: 实现参数视图中对应的增删改查功能，以及树形图展示。
 * </pre>
 * 
 * @author yangyuhui yangyh4@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/variable/paramType")
public class ParamTypeController extends BaseController {
	@Autowired
	private ParamTypeBS paramTypeBS;

	@RequestMapping("/list.*")
	@ResponseBody
	public List<CommonTreeNode> list(String nodeId) {
		return paramTypeBS.buildParamTypeTree(nodeId);
	}

	/**
	 *  判断是否为根节点,转入新建参数类型页面
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew(String upNo) {
		String upParamType = "";
		ModelMap mm = new ModelMap();
		if (!upNo.equals("0")) {
			BioneParamTypeInfo bioneParamType = paramTypeBS.findParamTypeInfoByNo(upNo);
			if(null != bioneParamType)
			upParamType = bioneParamType.getParamTypeName();
		} else {
			upParamType = "根节点";
		}
		mm.put("upNo", StringUtils2.javaScriptEncode(upNo));
		mm.put("upParamType", StringUtils2.javaScriptEncode(upParamType));
		return new ModelAndView("/frame/param/param-type-editNew", mm);
	}

	/**
	 * 验证参数类型标识是否存在
	 */
	@RequestMapping("/testParamTypeNo")
	@ResponseBody
	public boolean testParamTypeNo(String paramTypeNo) {
		boolean flag = this.paramTypeBS.checkIsParamTypeExist(paramTypeNo);
		if (flag) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断是否为根节点,转入修改参数类型
	 * @return
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		BioneParamTypeInfo bioneParamType = paramTypeBS.findParamTypeInfoByNo(id);
		ModelMap mm = new ModelMap();
		if (!bioneParamType.getUpNo().equals("0")) {
//			if(null!=bioneParamType.getUpNo())
			BioneParamTypeInfo upBioneParamType = paramTypeBS.findParamTypeInfoByNo(bioneParamType.getUpNo());
			mm.put("upParamType", StringUtils2.javaScriptEncode(upBioneParamType.getParamTypeName()));
		} else {
			mm.put("upParamType", "根节点");
		}
		mm.put("id", StringUtils2.javaScriptEncode(bioneParamType.getParamTypeId()));
		return new ModelAndView("/frame/param/param-type-edit", mm);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneParamTypeInfo paramType) {
		paramType.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		paramTypeBS.saveOrUpdateEntity(paramType);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> destroy(@PathVariable("id") String id) {
		Map<String, String> returnMap = new HashMap<String, String>();
		if(id != null){			
			paramTypeBS.delParamTypeListByNo(id);
		}
		return returnMap;
	}

	/**
	 * 通过标识查询参数类型并转化为页面显示数据
	 * @return 
	 */

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneParamTypeInfo show(@PathVariable("id") String id) {
		return this.paramTypeBS.getEntityById(id);
	}

}
