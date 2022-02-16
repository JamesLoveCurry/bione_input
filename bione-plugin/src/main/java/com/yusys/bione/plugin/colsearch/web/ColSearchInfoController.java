package com.yusys.bione.plugin.colsearch.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.colsearch.service.ColSearchInfoBS;
import com.yusys.bione.plugin.colsearch.web.vo.RptColSearchResultVO;

@Controller
@RequestMapping("/rpt/frame/colsearch")
public class ColSearchInfoController extends BaseController {
	@Autowired
	private ColSearchInfoBS colSearchInfoBS;

	//显示首页
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String searchNm){
		searchNm = StringUtils2.javaScriptEncode(searchNm);
		return new ModelAndView("/plugin/colsearch/rpt-col-search-list", "searchNm", searchNm);
	}
	
	//根据条件查找数据
	@RequestMapping(value = "/getColSearchInfoList.*")
	@ResponseBody
	public Map<String, Object> getColSearchInfoList(Pager pager, String searchNm, String checkNum) {
		
		List<RptColSearchResultVO> searchResult = this.colSearchInfoBS.getColSearchInfos(pager.getSortname(),pager.getSortorder(), 
                pager.getSearchCondition(), searchNm,checkNum);
		
		Map<String, Object> rowData = new HashMap<String, Object>();
		rowData.put("Rows", searchResult);
		return rowData;
	}
}
