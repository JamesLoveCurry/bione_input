package com.yusys.biapp.input.input.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title: 程序名称
 * Description: 功能描述
 * </pre>
 * @author xuguangyuan  xugy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */
@Controller
@RequestMapping("/rpt/input/inputtask")
public class InputTaskController extends BaseController {
	private final String PATH = "/input/input/";
	
	@Autowired
	private TempleBS templeBS;

	/**
	 * 数据补录添加跳转
	 */
	@RequestMapping("/taskTempleInput")
	public ModelAndView taskTempleInput(String paramStr, String templeId, String caseId, String rowindex) {
		ModelMap mm = new ModelMap();
		RptInputLstTempleInfo temp = templeBS.getEntityById(templeId);
		if (temp != null && StringUtils.isNotBlank(temp.getOrgColumn()) && "no".equals(temp.getAllowInputLower())) {
			mm.addAttribute("orgOwn", StringUtils2.javaScriptEncode(BioneSecurityUtils .getCurrentUserInfo().getOrgNo()));
			mm.addAttribute("orgColumn", StringUtils2.javaScriptEncode(temp.getOrgColumn()));
		} else if (temp != null && StringUtils.isNotBlank(temp.getOrgColumn()) && StringUtils.isNotBlank(temp.getAllowInputLower())) {
			mm.addAttribute("orgOwn", StringUtils2.javaScriptEncode(BioneSecurityUtils .getCurrentUserInfo().getOrgNo()));
		}
		mm.addAttribute("paramStr", StringUtils2.javaScriptEncode(EncodeUtils.urlDecode(paramStr)));
		mm.addAttribute("templeId", StringUtils2.javaScriptEncode(templeId));
		mm.addAttribute("caseId", StringUtils2.javaScriptEncode(caseId));
		mm.addAttribute("rowindex", StringUtils2.javaScriptEncode(rowindex));
		return new ModelAndView(PATH + "task-case-temple-write", mm);
	}
}
