package com.yusys.bione.plugin.design.web;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author 方娟   fangjuan@mail.yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.SqlValidateUtils;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.design.service.DimParamtmpBS;

@Controller
@RequestMapping("/report/frame/design/paramtmp")
public class DimParamtmpController extends BaseController {
	
	@Autowired
	private DimParamtmpBS dimParamtmpBs;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(){
		return new ModelAndView("/plugin/design/template/dim-param-temp-edit");
	}
	
	@RequestMapping(value = "/getDimTree" , method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getDimTree(String publicDim, String dimName){
		return this.dimParamtmpBs.getDimTree(publicDim, dimName);
	}
	
	@RequestMapping(value = "/getTreeDimItems")
	@ResponseBody
	public List<CommonTreeNode> getTreeDimItems(String dimTypeNo,String searchName){
		if(StringUtils.isNotBlank(searchName)){
			searchName = EncodeUtils.urlDecode(searchName);
		}
		//2020 lcy 【后台管理】sql注入 代码优化
		if(SqlValidateUtils.validateStr(searchName)) {
			searchName = SqlValidateUtils.replaceValue(searchName);
		}
		return this.dimParamtmpBs.getTreeDimItems(dimTypeNo,searchName);
	}
	
	@RequestMapping(value = "/getSelectDimItems")
	@ResponseBody
	public List<CommonComboBoxNode> getSelectDimItems(String dimTypeNo){
		return this.dimParamtmpBs.getSelectDimItems(dimTypeNo);
	}
	
	@RequestMapping(value = "/getOpers")
	@ResponseBody
	public List<CommonComboBoxNode> getOpers(String type){
		return this.dimParamtmpBs.getOpers(type);
	}
	
}
