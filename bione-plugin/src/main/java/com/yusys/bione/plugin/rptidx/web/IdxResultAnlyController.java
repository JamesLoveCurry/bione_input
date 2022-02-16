package com.yusys.bione.plugin.rptidx.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptidx.service.IdxResultAnlyBS;

@Controller
@RequestMapping("rpt/frame/idx/result/analysis")
public class IdxResultAnlyController extends BaseController {

	@Autowired
	private IdxResultAnlyBS anlyBs;
	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/idxresultanly/idx-result-analysis-index");
	}
	
	/**
	 * 查询某个指标
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tab", method = RequestMethod.GET)
	public ModelAndView showIdx() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("currentOrgNo", StringUtils2.javaScriptEncode(BioneSecurityUtils.getCurrentUserInfo().getOrgNo()));
		map.put("currentOrgNm", StringUtils2.javaScriptEncode(this.anlyBs.getOrgNm(BioneSecurityUtils.getCurrentUserInfo().getOrgNo())));
		List<String> childOrgs = this.anlyBs.getChildOrg(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		StringBuilder orgs = new StringBuilder("");
		
		for(String tmp: childOrgs){
			orgs.append(tmp + ",");
		}
		map.put("childOrgs", StringUtils2.javaScriptEncode((orgs.toString().endsWith(",")?orgs.toString().substring(0, orgs.toString().length() - 1): orgs.toString())));
		return new ModelAndView("/plugin/idxresultanly/idx-result-analysis-tab", map);
	}

	/**
	 * 跳转到指标概要展示页面
	 * */
	@RequestMapping(value = "/indexSummary")
	public ModelAndView indexSummary(String indexNo, String indexVerId){
		List<CommonComboBoxNode> list = this.anlyBs.getDimOfIndex(indexNo, indexVerId);
		String data = StringUtils2.javaScriptEncode(JSON.toJSONString(list));
		return new ModelAndView("/plugin/idxresultanly/idx-result-analysis-indexsummary", "data", data);
	}
	
	@RequestMapping(value="/getDimSum")
	@ResponseBody
	public List<RptDimItemInfo> getDimSum(String dimTypeNo){
		return this.anlyBs.getDimSum(dimTypeNo);
	}
	
	/**
	 * 跳转到机构信息展示页面
	 * @return
	 */
	@RequestMapping(value = "/orgInfo")
	public ModelAndView orgInfo(){
		return new ModelAndView("/plugin/idxresultanly/idx-result-analysis-tab-orgInfo");
	}
	
	/**
	 * 跳转到结构解析页面
	 */
	@RequestMapping(value = "/structAnly")
	public ModelAndView structAnly(String indexNo, String indexVerId){
		List<CommonComboBoxNode> list = this.anlyBs.getDimOfIndex(indexNo, indexVerId);//参考
		String data = StringUtils2.javaScriptEncode(JSON.toJSONString(list));
		return new ModelAndView("/plugin/idxresultanly/idx-result-analysis-tab-structAnly", "data", data);
	}
	
	@RequestMapping(value = "/getDimItems")
	@ResponseBody
	public List<RptDimItemInfo> getDimItems(String dimTypeNo){
		return this.anlyBs.getDimItems(dimTypeNo);
	}
	
	/**
	 * 趋势分析
	 * 
	 * @return
	 */
	@RequestMapping(value = "/trendAnly", method = RequestMethod.GET)
	public ModelAndView trendAnly() {
		String orgNo = StringUtils2.javaScriptEncode(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		return new ModelAndView("/plugin/idxresultanly/idx-result-analysis-trendAnly", "currentOrgNo", orgNo);
	}
	
	@RequestMapping(value = "/chooseOrg")
	public ModelAndView chooseOrg(){
		return new ModelAndView("/plugin/idxresultanly/idx-result-analysis-org");
	}
}
