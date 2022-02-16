package com.yusys.bione.plugin.rptidx.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptidx.service.RptIdxSimiGrpBS;

/**
 * <pre>
 * Title:同类组配置
 * Description: 提供查看同类组指标/配置同类组指标等功能
 * </pre>
 * 
 * @author hubing hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/idx/simigrp")
public class IdxSimiGrpController extends BaseController {
	@Autowired
	private RptIdxSimiGrpBS grpBS;
	
	/**
	 * 查看同类组指标
	 * @param indexNo 指标编号
	 * @return mv
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String indexNo) {
		indexNo = StringUtils2.javaScriptEncode(indexNo);
		return new ModelAndView("/plugin/rptidx/simigrp/idx-simigrp-edit", "indexNo", indexNo);
	}
	/**
	 * 查询同类组指标
	 * @param indexNo 指标编号
	 * @return 查询状态
	 */
	@RequestMapping("/getSimiGrpIndex.*")
	@ResponseBody
	public Map<String, Object> getSimiGrpIndex(String indexNo) {
		return this.grpBS.getSimiGrpIndex(indexNo);
	}
	/**
	 * 保存同类组指标配置
	 * @param ids 配置的指标群
	 * @param indexNo 被配置的指标编号
	 * @return 保存状态
	 */
	@RequestMapping("/saveSimiGrp")
	@ResponseBody
	public Map<String,String> saveSimiGrp(String ids,String indexNo,String curGrpId){
		return this.grpBS.saveSimiGrp(ids, indexNo ,curGrpId);
	}
	/**
	 * 获得指标数
	 * @param indexNo 指标编号
	 * @return 指标实体
	 */
	@RequestMapping("/getGrpIndex")
	@ResponseBody
	public List<CommonTreeNode> getGrpIndex(String indexNo){
		return this.grpBS.getGrpIndex(indexNo);
	}
	/**
	 * 检验选择的指标是否已存在于其他同类组中
	 * @param indexNo 指标编号
	 * @return 查询状态
	 */
	@RequestMapping("/checkIndexNo.*")
	@ResponseBody
	public Map<String, String> checkIndexNo(String indexNo) {
		return this.grpBS.checkIndexNo(indexNo);
	}
}
