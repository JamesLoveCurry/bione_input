package com.yusys.bione.frame.label.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;










import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.label.entity.BioneLabelInfo;
import com.yusys.bione.frame.label.entity.BioneLabelObjInfo;
import com.yusys.bione.frame.label.service.LabelBS;
import com.yusys.bione.frame.label.service.LabelObjBS;
/**
 * <pre>
 * Title:标签配置
 * Description:
 * </pre>
 * 
 * @author weijx weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：		  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/label/labelConfig")
public class labelConfigController extends BaseController {
	@Autowired
	private LabelBS labelBS;
	@Autowired
	private LabelObjBS labelObjBS;
	/**
	 * 主页
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/label/label-config-index";
	}
	
	/**
	 * 标签检索页面
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(){
		return "/frame/label/label-search-index";
	}
	
	/**
	 * 标签配置页面
	 * @return
	 */
	@RequestMapping(value = "/rel",method = RequestMethod.GET)
	public ModelAndView rel(String id,String labelObjNo) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("labelObjNo", StringUtils2.javaScriptEncode(labelObjNo));
		return new ModelAndView("/frame/label/label-rel-config",map);
	}
	
	
	/**
	 * 获取全部标签
	 * @return
	 */
	@RequestMapping(value = "/getLabelInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getLabelInfo(String id,String labelObjNo) {
		Map<String,Object>  result = new HashMap<String, Object>();
		result.put("allLabel", this.labelBS.getLabelInfo(labelObjNo));
		result.put("objLabel", this.labelBS.getObjLabelInfo(id,labelObjNo));
		return result;
	}
	
	
	/**
	 * 异步树
	 * @param realId
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/treeNode.json", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> treeNode(String realId, String labelObjNo, String type) {
		return labelBS.getTreeNode(this.getContextPath(),realId, type ,labelObjNo);
	}
	
	/**
	 * 内容列表
	 * @param pager
	 * @param realId
	 * @param type
	 * @return
	 */
	@RequestMapping("/list.json")
	@ResponseBody
	public Map<String, Object> list(Pager pager, String realId, String labelObjId) {
		Map<String, Object> page = Maps.newHashMap();
		SearchResult<BioneLabelInfo> rs = labelBS.findLabelPage(
				pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSearchCondition(), realId,labelObjId);
		page.put("Rows", rs.getResult());
		page.put("Total", rs.getTotalCount());
		return page;
	}
	/**
	 * 新增跳转
	 * @param type
	 * @param realId
	 * @return
	 */
	@RequestMapping("/new")
	public ModelAndView editNew(String realId,String labelObjId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("upId", StringUtils2.javaScriptEncode(realId));
		mm.addAttribute("isNew", true);
		mm.addAttribute("labelObjId", StringUtils2.javaScriptEncode(labelObjId));
		return new ModelAndView("/frame/label/label-edit", mm);
	}
	/**
	 * 修改跳转
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping("/{id}/edit")
	public ModelAndView edit(@PathVariable String id, String type) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("isNew", false);
		return new ModelAndView("/frame/label/label-edit", mm);
	}
	/**
	 * 删除
	 * @param id
	 * @param type
	 */
	@RequestMapping(value = "/{type}/{id}", method = RequestMethod.POST)
	public void destroy(@PathVariable("id") String id, @PathVariable("type") String type) {
		labelBS.deleteLabel(id);
	}
	/**
	 * 新增标签对象
	 * @param model
	 */
	@RequestMapping(value = "/obj", method = RequestMethod.POST)
	public void createObj(BioneLabelObjInfo model) {
		if (StringUtils.isEmpty(model.getLabelObjId())) {
			model.setLabelObjId(RandomUtils.uuid2());
		}
		labelObjBS.updateEntity(model);
	}
	/**
	 * 新增标签
	 * @param model
	 */
	@RequestMapping(value = "/label", method = RequestMethod.POST)
	public void createLabel(BioneLabelInfo model) {
		if (StringUtils.isEmpty(model.getLabelId())) {
			model.setLabelId(RandomUtils.uuid2());
		}
		labelBS.saveOrUpdateEntity(model);
	}
	
	// 验证
	
	/**
	 * 验证标签对象标识
	 * @param labelObjNo
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/checkObj", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkObj(String labelObjNo, String id) {
		return labelBS.isContainObj(labelObjNo, id) ? false : true;
	}
	
	/**
	 * 验证标签对象名称
	 * @param labelObjName
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/checkObjName", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkObjName(String labelObjName, String id) {
		return labelBS.isContainObjName(labelObjName, id) ? false : true;
	}
	
	/**
	 * 验证标签类型名称
	 * @param id
	 * @param typeName
	 * @param realId
	 * @return
	 */
	@RequestMapping(value = "/checkTypeName")
	@ResponseBody
	public boolean checkTypeName(String id, String typeName, String realId) {
		return labelBS.isContainTypeName(id, typeName, realId) ? false : true;
	}
	
	/**
	 * 验证标签名称
	 * @param id
	 * @param labelName
	 * @param realId
	 * @return
	 */
	@RequestMapping(value = "/checkLabelName")
	@ResponseBody
	public boolean checkLabelName(String id, String labelName, String realId) {
		return labelBS.isContainLabelName(id, labelName, realId) ? false : true;
	}
	
	/**
	 * 自动填表内容
	 * @param type
	 * @param id
	 * @return
	 */
	@RequestMapping("/info.json")
	@ResponseBody
	public Object info(String type, String id) {
		if (id != null) {
			if ("obj".equals(type)) {
				return labelObjBS.getEntityById(id);
			}
			if ("label".equals(type)) {
				return labelBS.getEntityById(BioneLabelInfo.class, id);
			}
		}
		return null;
	}
	
	/**
	 * 获取对象标签关系
	 * @return
	 */
	@RequestMapping(value = "/getLabelRel")
	@ResponseBody
	public Map<String,Object> getLabelRel(String id, String labelObjNo) {
		return labelBS.getLabelRel( id, labelObjNo);
	}
	
	/**
	 * 获取标签信息
	 * @return
	 */
	@RequestMapping(value = "/getLabelMap")
	@ResponseBody
	public Map<String,Map<String,String>> getLabelMap() {
		return labelBS.getLabelMap(null);
	}
	
	/**
	 * 保存标签关系
	 * @return
	 */
	@RequestMapping(value = "/saveLabelRel")
	@ResponseBody
	public Map<String,Object> saveLabelRel(String id, String labelObjNo, String labelIds) {
		Map<String,Object> result = new HashMap<String, Object>();
		labelBS.saveLabelRel( id, labelObjNo, labelIds);
		return result;
	}
	
	/**
	 * 同步配置为监管业务标签
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/configLabel")
	@ResponseBody
	public void configLabel(String paramValue, String paramName) {
		labelBS.configLabel(paramValue, paramName);
	}
	
	/**
	 * 删除监管业务标签
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteLabel")
	@ResponseBody
	public void deleteLabel() {
		labelBS.deleteLabel(null);
	}
}
