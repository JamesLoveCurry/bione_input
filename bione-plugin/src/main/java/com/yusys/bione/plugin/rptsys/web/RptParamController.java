package com.yusys.bione.plugin.rptsys.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.frame.variable.entity.BioneParamTypeInfo;
import com.yusys.bione.plugin.rptsys.service.RptParamBS;
import com.yusys.bione.plugin.rptsys.service.RptParamTypeBS;

/**
 * <pre>
 * Title:参数处理Action类
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
@RequestMapping("/rpt/variable/param")
public class RptParamController extends BaseController {
	@Autowired
	private RptParamBS paramBS;
	@Autowired
	private RptParamTypeBS paramTypeBS;

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew(String paramTypeNo, String upNo) {
		BioneParamTypeInfo paramtype = paramTypeBS
				.findParamTypeInfoByNo(paramTypeNo);
		ModelMap mm = new ModelMap();
		mm.put("paramTypeId", StringUtils2.javaScriptEncode(paramtype.getParamTypeId()));
		mm.put("paramTypeNo", StringUtils2.javaScriptEncode(paramTypeNo));
		mm.put("paramTypeName", StringUtils2.javaScriptEncode(paramtype.getParamTypeName()));
		mm.put("upNo", StringUtils2.javaScriptEncode(upNo));
		return new ModelAndView("/plugin/rptparam/param-editNew", mm);

	}

	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneParamInfo param) {
		param.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		if (param.getParamId() == null || "".equals(param.getParamId())) {
			param.setParamId(RandomUtils.uuid2());
			paramBS.saveParam(param);
		}
		else{
			paramBS.updateParam(param);
		}
		
		
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void destroy(@PathVariable("id") String id) {
		this.paramBS.removeEntityBatch(id);
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		BioneParamInfo param = this.paramBS.getParamById(id);
		String paramTypeName = paramTypeBS.findParamTypeInfoByNo(
				param.getParamTypeNo()).getParamTypeName();
		String paramTypeId = paramTypeBS.findParamTypeInfoByNo(
				param.getParamTypeNo()).getParamTypeId();
		ModelMap mm = new ModelMap();
		mm.put("id", StringUtils2.javaScriptEncode(id));
		mm.put("paramTypeId", StringUtils2.javaScriptEncode(paramTypeId));
		mm.put("paramTypeName", StringUtils2.javaScriptEncode(paramTypeName));
		/*BioneParamInfo p = this.paramBS.getEntityById(id);*/
		return new ModelAndView("/plugin/rptparam/param-edit", mm);
	}

	/**
	 * 查询参数并转化为页面显示数据
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneParamInfo show(@PathVariable("id") String id) {
		return this.paramBS.getParamById(id);
	}

	/**
	 * 验证参数值是否存在
	 */
	@RequestMapping("/testParamValue")
	@ResponseBody
	public boolean testParamValue(String paramTypeId, String paramValue,
			@RequestParam(value = "id", required = false) String id) {
		String paramTypeNo = paramTypeBS.getParamTypeById(paramTypeId)
				.getParamTypeNo();
		List<BioneParamInfo> paramList = (List<BioneParamInfo>) paramBS
				.findParamByParamTypeNo(paramTypeNo);
		for (BioneParamInfo param : paramList) {
			if (!param.getParamId().equals(id)) {
				if (param.getParamValue().equals(paramValue)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 初始化Grid
	 */
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager, String paramTypeNo) {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		String cod = pager.getCondition();
		if ((cod != null && cod.indexOf("paramTypeNo") >= 0)
				|| (paramTypeNo != null && (!"".equals(paramTypeNo)))) {
			Map<String, Object> map = this.paramBS.searchParamsAsTree(
					pager, logicSysNo, paramTypeNo);
			return map;

		} else {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/rptparam/param-index";
	}

	/**
	 * 根据paramTypeNo查询所有代码
	 * 
	 * @param paramTypeNo
	 * @return
	 */
	@RequestMapping(value = "/{paramTypeNo}/listAll.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> listAll(
			@PathVariable("paramTypeNo") String paramTypeNo) {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		List<BioneParamInfo> all = paramBS.findParamBySysAndType(logicSysNo,
				paramTypeNo);
		Map<String, Object> map = Maps.newHashMap();
		map.put("Rows", all);
		map.put("Total", all.size());
		return map;
	}

	/**
	 * 获取某参数类型下的参数树形结构
	 * 
	 * @param paramType
	 * @return CommonTreeNode 集合
	 */
	@RequestMapping(value = "/getParamsTreeByType.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getParamsTreeByType(String paramType,
			HttpServletRequest request) {
		return this.paramBS.getParamsTreeByType(paramType,
				request.getContextPath());
	}

	/**
	 * 启用/停用代码
	 * 
	 * @param paramId
	 *            代码Id
	 * @param paramSts
	 *            代码状态
	 * @return
	 */
	@RequestMapping(value = "/changeParamSts.*", method = RequestMethod.POST)
	@ResponseBody
	public String changeParamSts(String paramId, String paramSts) {
		if (paramId != null
				&& !"".equals(paramId)
				&& (GlobalConstants4frame.COMMON_STATUS_VALID.equals(paramSts) || GlobalConstants4frame.COMMON_STATUS_INVALID
						.equals(paramSts))) {
			BioneParamInfo param = this.paramBS.getParamById(paramId);
			if (param != null) {
				this.paramBS.updateParam(param);
				return "TRUE";
			}else{
				return "代码不存在。";
			}
		}
		return "修改失败。";
	}
	
	/**
	 * 通过系统参数表,将参数代码转换为参数名称
	 * 
	 * @param paramTypeNo 参数类型标识
	 * @return 
	 */
	@RequestMapping("/getParamName")
	@ResponseBody
	public String getParamName(String paramTypeNo, String paramValue) {
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("paramTypeNo", paramTypeNo);
		params.put("paramValue", paramValue);
		params.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		BioneParamInfo info=this.paramBS.getParamByParam(params);
		String result=info!=null?info.getParamName():null;
		if (result == null) {
			return paramValue;
		} else {
			return result;
		}
	}
}
