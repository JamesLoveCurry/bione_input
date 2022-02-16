package com.yusys.bione.plugin.rptsys.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptsys.service.RptSysVarBS;
/**
 * 
 * <pre>
 * Title:系统参数设置
 * Description:
 * </pre>
 * 
 * @author weijiaxiang weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("rpt/frame/rptsys/set")
public class RptSysSetController extends BaseController{
	
	@Autowired
	private RptSysVarBS sysVarBS;
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView(
				"/plugin/rptsys/sys-var-set");
	}
	
	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager,String type) {
		return this.sysVarBS.getSysVarList(pager,type);
	}
	
}
