package com.yusys.bione.plugin.rptdim.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.frame.base.web.BaseController;

/**
 * 
 * <pre>
 * Title:
 * Description: 
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/dimCatalog/preview")
public class RptDimPreviewController extends BaseController {
	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String   indexNm) {
	    return new ModelAndView("/plugin/rptdim/rpt-dim-catalog-index", "preview", "preview");
	}
}