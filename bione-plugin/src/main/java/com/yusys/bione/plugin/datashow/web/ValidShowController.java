package com.yusys.bione.plugin.datashow.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.validtype.entity.BioneValidTypeDef;
import com.yusys.bione.plugin.datashow.service.ValidShowBS;
import com.yusys.bione.plugin.datashow.web.vo.RptEngineCheckStsVO;
import com.yusys.bione.plugin.datashow.web.vo.RptValidResultLogicVO;
import com.yusys.bione.plugin.valid.IValidTypeImpl;

@Controller
@RequestMapping("/report/frame/datashow/valid")
public class ValidShowController extends BaseController {
	
	@Autowired
	private ValidShowBS validBS;
	
	/**
	 * 首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String idx){
		idx = StringUtils2.javaScriptEncode(idx);
		return new ModelAndView("/plugin/datashow/validshow/valid-show-index", "idx", idx);
	}
	
	/**
	 * 查tab页的授权项
	 * @return
	 */
	@RequestMapping(value = "/getValidTypeTabs.*")
	@ResponseBody
	public Map<String,Object> getValidTypeTabs(){
		Map<String,Object> validRel = new HashMap<String, Object>();
		List<BioneValidTypeDef> defList = this.validBS.getValidTypeDef();
		if (defList != null && defList.size() > 0) {
			List<Map<String,String>> subList = new ArrayList<Map<String,String>>();
			for (BioneValidTypeDef obj : defList) {
				Map<String, String> resMap = new HashMap<String, String>();
				resMap.put("objDefNo", obj.getObjDefNo());
				resMap.put("objDefName", obj.getObjDefName().substring(0, obj.getObjDefName().length()-2));
				resMap.put("beanName", obj.getBeanName());
				subList.add(resMap);
			}
			validRel.put("data", subList);
		}
		return validRel;
	}
	
	/**
	 * 查各个tab页的指标树
	 * @return
	 *//*
	@RequestMapping(value = "/getIdxAsyncTree.*")
	@ResponseBody
	public List<CommonTreeNode> getIdxAsyncTree(String beanName,String nodeType,String indexVerId,CommonTreeNode node){
		List<CommonTreeNode> idxTreeNodes = new ArrayList<CommonTreeNode>();
		IValidTypeImpl valid = SpringContextHolder.getBean(beanName);
		idxTreeNodes = valid.doGetValidIdxAsync(node,this.getContextPath(), nodeType, indexVerId);
		return idxTreeNodes;
	}
*/	
	/**
	 * 查各个tab页的指标树
	 * @return
	 */
	@RequestMapping(value = "/getIdxAsyncTree.*")
	@ResponseBody
	public List<CommonTreeNode> getIdxAsyncTree(String groupId, String grpType,String validType, String beanName, String indexVerId, CommonTreeNode node){
		List<CommonTreeNode> idxTreeNodes = new ArrayList<CommonTreeNode>();
		IValidTypeImpl valid = SpringContextHolder.getBean(beanName);
		idxTreeNodes = valid.doGetValidIdxAsync(groupId, grpType, validType, node, this.getContextPath(), indexVerId);
		return idxTreeNodes;
	}
	
	//根据条件查找指标
	@RequestMapping(value = "/getCheckInfoList.*")
	@ResponseBody
	public Map<String, Object> getCheckInfoLists(Pager pager, String tabId, 
			String indexNo, String indexNm) {
		
		SearchResult<RptEngineCheckStsVO> searchResult = this.validBS.getCheckInfos(
				pager.getPageFirstIndex(), pager.getPagesize(),	pager.getSortname(),
				pager.getSortorder(), pager.getSearchCondition(), tabId, 
				indexNo, indexNm);
		
		Map<String, Object> rowData = new HashMap<String, Object>();
		rowData.put("Rows", searchResult.getResult());
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
	@RequestMapping(value = "logicDetail", method = RequestMethod.GET)
	public ModelAndView logicDetail(String indexNo,String indexNm,String dataDate,String orgNo,String checkType ) {
		ModelMap mm = new ModelMap();
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		mm.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("checkType", StringUtils2.javaScriptEncode(checkType));
		return new ModelAndView("/plugin/datashow/validshow/valid-show-logic-detail", mm);
	}
	
	@RequestMapping(value = "warnDetail", method = RequestMethod.GET)
	public ModelAndView warnDetail(String indexNo,String dataDate,String orgNo,String checkType ) {
		ModelMap mm = new ModelMap();
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("checkType", StringUtils2.javaScriptEncode(checkType));
		return new ModelAndView("/plugin/datashow/validshow/valid-show-warn-detail", mm);
	}
	
	@RequestMapping(value = "logicTree", method = RequestMethod.GET)
	public ModelAndView logicTree(String tabId,String nodeType,String checkId ,String checkNm ) {
		ModelMap mm = new ModelMap();
		tabId = tabId.substring(3, tabId.length());
		String url = ""; 
		if("01".equals(tabId)){
			url = "/plugin/datashow/validshow/valid-show-logic-detail";
		}else if("02".equals(tabId)){
			url = "/plugin/datashow/validshow/valid-show-warn-detail";
		}else{
			url = "/plugin/datashow/validshow/valid-show-logic-detail";
		}
		mm.put("tabId", StringUtils2.javaScriptEncode(tabId));
		mm.put("nodeType", StringUtils2.javaScriptEncode(nodeType));
		mm.put("checkId", StringUtils2.javaScriptEncode(checkId));
		mm.put("checkNm", StringUtils2.javaScriptEncode(checkNm));
		return new ModelAndView(url, mm);
	}
	@RequestMapping(value = "/checkDetail")
	@ResponseBody
	public Map<String, Object> checkDetail(Pager pager, String indexNo,String indexNm,String dataDate,String orgNo,String checkType,String tabId,String nodeType,String checkId,String checkNm){
		SearchResult<RptValidResultLogicVO> searchResult = new SearchResult<RptValidResultLogicVO>();
		if(nodeType == null || "".equals(nodeType)){
			 searchResult = this.validBS.checkDetail(
						pager.getPageFirstIndex(), pager.getPagesize(),	pager.getSortname(),
						pager.getSortorder(), pager.getSearchCondition(), indexNo,indexNm,dataDate,orgNo,checkType);
		}else if("check".equals(nodeType)){
			 searchResult = this.validBS.checkTreeDetail(
						pager.getPageFirstIndex(), pager.getPagesize(),	pager.getSortname(),
						pager.getSortorder(), pager.getSearchCondition(), tabId,checkId,checkNm);
		}
		Map<String, Object> rowData = new HashMap<String, Object>();
		rowData.put("Rows", searchResult.getResult());
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
}
