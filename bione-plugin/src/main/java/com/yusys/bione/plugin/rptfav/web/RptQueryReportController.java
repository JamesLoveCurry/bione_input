package com.yusys.bione.plugin.rptfav.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.plugin.rptfav.service.RptQueryReportBS;

/**
 * 
 * <pre>
 * Title:指标查询
 * Description: 程序功能的描述 
 * </pre>
 * @author sunym  sunym@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/rptfav/report")
public class RptQueryReportController {
	
	@Autowired
	private RptQueryReportBS rptQueryReportBS;
	
	@RequestMapping
	public ModelAndView index(String folderId,String type){
		if(type==null||type.equals("")){
			return new ModelAndView("/plugin/rptfav/query-report-index");
		}else{
			Map<String,Object> map = new HashMap<String,Object>();
			if(type.equals("report")){
				map.put("folderId", StringUtils2.javaScriptEncode(folderId));
				map.put("type", "report");
			}if(type.equals("catalog")){
				map.put("folderId", StringUtils2.javaScriptEncode(folderId));
			}
			return new ModelAndView("/plugin/rptfav/query-report-index",map);
		}
		
	}
	
	@RequestMapping(value="/getInfo")
	@ResponseBody
	public Map<String,Object> getInfo(Pager pager,String folderId,String instanceType,String type){
		if(type==null||type.equals("")){
			return this.rptQueryReportBS.reportInfo(pager,folderId,instanceType);
		}else if(type.equals("report")){
			return this.rptQueryReportBS.reportInfoIdx(pager,folderId,instanceType);
		}else{
			return this.rptQueryReportBS.reportInfo(pager,folderId,instanceType);
		}
		
	}
	
	@RequestMapping(value="del")
	@ResponseBody
	public String delte(String instanceId,String folderId){
		return this.rptQueryReportBS.del(instanceId,folderId);
	}
}
