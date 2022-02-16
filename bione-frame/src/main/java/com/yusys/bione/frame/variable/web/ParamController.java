package com.yusys.bione.frame.variable.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.yusys.bione.frame.authobj.entity.BioneRoleInfoExt;
import com.yusys.bione.frame.authobj.service.RoleExtBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.frame.variable.entity.BioneParamTypeInfo;
import com.yusys.bione.frame.variable.service.ParamBS;
import com.yusys.bione.frame.variable.service.ParamTypeBS;

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
@RequestMapping("/bione/variable/param")
public class ParamController extends BaseController {
	@Autowired
	private ParamBS paramBS;
	@Autowired
	private ParamTypeBS paramTypeBS;
	@Autowired
	private RoleExtBS roleExtBS;
	/**
	 * 右tab页面加载
	 * @return
	 */
	@RequestMapping(value="/toGrid")
	public ModelAndView toGrid(String paramTypeNo,String paramTypeName){
		ModelMap mm = new ModelMap();
		mm.put("paramTypeNo", StringUtils2.javaScriptEncode(paramTypeNo));
		mm.put("paramTypeName", StringUtils2.javaScriptEncode(paramTypeName));
		return  new ModelAndView("/frame/param/param-grid",mm);
	}
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew(String paramTypeNo, String upNo) {
		ModelMap mm = new ModelMap();
		BioneParamTypeInfo paramtype = paramTypeBS
				.findParamTypeInfoByNo(paramTypeNo);
		if(!"0".equals(upNo)){
			BioneParamInfo param = this.paramBS.getEntityById(upNo);
			if(param!=null){
				mm.put("upParamName", StringUtils2.javaScriptEncode(param.getParamName()));
			}
		}else{
			mm.put("upParamName", "无");
		}
		mm.put("paramTypeId", StringUtils2.javaScriptEncode(paramtype.getParamTypeId()));
		mm.put("paramTypeNo", StringUtils2.javaScriptEncode(paramTypeNo));
		mm.put("paramTypeName", StringUtils2.javaScriptEncode(paramtype.getParamTypeName()));
		mm.put("upNo", StringUtils2.javaScriptEncode(upNo));
		return new ModelAndView("/frame/param/param-editNew", mm);

	}

	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public void create(BioneParamInfo param) {
		if (param.getParamId() == null || "".equals(param.getParamId())) {
			param.setParamId(RandomUtils.uuid2());
		}
		param.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if(GlobalConstants4frame.TREE_PARAM_TYPE.equals(param.getParamTypeNo())) {//如果是监管机构，同步新建平台角色
			BioneRoleInfoExt roleExt = new BioneRoleInfoExt();
			roleExt.setCode(param.getParamValue());
			roleExt.setCodeDesc(param.getParamName() + "角色");
			BioneParamInfo oldParam = paramBS.getEntityById(param.getParamId());
			if(null != oldParam) {
				BioneRoleInfoExt roleInfo = roleExtBS.getEntityById(oldParam.getParamValue());
				if(null != roleInfo) {
					roleExtBS.removeEntityById(oldParam.getParamValue());
				}
			}
			roleExtBS.saveEntity(roleExt);
		}
		paramBS.saveOrUpdateEntity(param);
	}

	/**
	 * 参数删除
	 * @param id
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public void destroy(@PathVariable("id") String id) {
		this.paramBS.removeEntityBatch(id);
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		BioneParamInfo param = this.paramBS.getEntityById(id);
		String paramTypeName = paramTypeBS.findParamTypeInfoByNo(
				param.getParamTypeNo()).getParamTypeName();
		String paramTypeId = paramTypeBS.findParamTypeInfoByNo(
				param.getParamTypeNo()).getParamTypeId();
		ModelMap mm = new ModelMap();
		mm.put("id", StringUtils2.javaScriptEncode(id));
		mm.put("paramTypeId", StringUtils2.javaScriptEncode(paramTypeId));
		mm.put("paramTypeName", StringUtils2.javaScriptEncode(paramTypeName));
		/*BioneParamInfo p = this.paramBS.getEntityById(id);*/
		return new ModelAndView("/frame/param/param-edit", mm);
	}

	/**
	 * 查询参数并转化为页面显示数据
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneParamInfo show(@PathVariable("id") String id) {
		return this.paramBS.getEntityById(id);
	}

	/**
	 * 验证参数值是否存在
	 */
	@RequestMapping("/testParamValue")
	@ResponseBody
	public boolean testParamValue(String paramTypeId, String paramValue,
			@RequestParam(value = "id", required = false) String id) {
		String paramTypeNo = paramTypeBS.getEntityById(paramTypeId)
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
			Map<String, Object> map = this.paramBS.searchParamsAsTree(pager, logicSysNo, paramTypeNo);
			return map;

		} else {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/param/param-index";
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
			BioneParamInfo param = this.paramBS.getEntityById(paramId);
			if (param != null) {
				this.paramBS.updateEntity(param);
				return "TRUE";
			}else{
				return "代码不存在。";
			}
		}
		return "修改失败。";
	}
	
	/**
	 * 通过类别获取参数
	 * add by kanglg on 2015/07/08
	 * @param typeNo
	 * @return
	 */
	@RequestMapping("/find")
	@ResponseBody
	public List<BioneParamInfo> find(String typeNo) {
		return paramBS.findParamByParamTypeNo(typeNo);
	}
	
	
}
