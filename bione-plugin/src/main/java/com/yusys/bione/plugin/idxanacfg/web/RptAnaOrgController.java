package com.yusys.bione.plugin.idxanacfg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.plugin.idxanacfg.service.RptAnaOrgBS;
/**
 * <pre>
 * Title:机构信息配置
 * Description: 
 * </pre>
 * 
 * @author yangyf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/cabin/analysis/orggroup")
public class RptAnaOrgController {
	
	@Autowired
	private RptAnaOrgBS cabinAnaOrgBS;
	
	/**
	 * 跳转到机构选择页面
	 * @param chartId
	 * @return
	 */
	@RequestMapping(value = "/chooseOrg")
	public ModelAndView chooseOrg(String chartId) {
		chartId = StringUtils2.javaScriptEncode(chartId);
		return new ModelAndView("/plugin/idxanacfg/idx-analysis-orgTree", "chartId", chartId);
	}
	
	/**
	 * 获取机构组父节点数据
	 * @return
	 */
	@RequestMapping("/getorgGrpCombo")
	@ResponseBody
	public List<CommonComboBoxNode> getorgGrpCombo(String orgType){
		return cabinAnaOrgBS.getorgGrpCombo(orgType);
	}
	
	/**
	 * 获取机构组子节点数据
	 * @param grpId
	 * @return
	 */
	@RequestMapping("/getGrpOrgNos")
	@ResponseBody
	public List<String> getGrpOrgNos(String grpId){
		return cabinAnaOrgBS.getGrpOrgNos(grpId);
	}
	
	/**
	 * 维度机构树
	 * @return
	 */
	@RequestMapping(value = "/getOrgTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getOrgTree() {
		return this.cabinAnaOrgBS.getOrgTree("0");
	}
}
