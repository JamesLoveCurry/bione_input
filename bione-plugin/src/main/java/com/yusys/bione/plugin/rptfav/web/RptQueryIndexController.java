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
 * Title:我的报表
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
@RequestMapping("/rpt/frame/rptfav/index")
public class RptQueryIndexController {
	
	@Autowired
	private RptQueryReportBS rptQueryReportBS;
	
	@RequestMapping()
	public ModelAndView index(String folderId,String type,String state){
		if(type == null ||type.equals("")){
			state = StringUtils2.javaScriptEncode(state);
			return new ModelAndView("/plugin/rptfav/query-inde-index", "state", state);
		}else{
			Map<String,Object> map = new HashMap<String,Object>();
			if(type.equals("idx")){
				map.put("folderId", StringUtils2.javaScriptEncode(folderId));
				map.put("type", "idx");
				map.put("state", StringUtils2.javaScriptEncode(state));
			}if(type.equals("catalog")){
				map.put("folderId", StringUtils2.javaScriptEncode(folderId));
				map.put("state", StringUtils2.javaScriptEncode(state));
			}
			return new ModelAndView("/plugin/rptfav/query-inde-index",map);
		}
	}
	
	@RequestMapping(value="/getInfo")
	@ResponseBody
	public Map<String,Object> getInfo(Pager pager,String folderId,String instanceType,String type){
		if(type == null ||type.equals("")){
			return this.rptQueryReportBS.reportInfo(pager,folderId,instanceType);
		}else if(type.equals("idx")){
			return this.rptQueryReportBS.reportInfoIdx(pager,folderId,instanceType);
		}else{
			return this.rptQueryReportBS.reportInfo(pager,folderId,instanceType);
		}
		
	}
	@RequestMapping(value="del")
	@ResponseBody
	public String delteindex(String instanceId,String folderId){
		return this.rptQueryReportBS.delindex(instanceId,folderId);
	}
	
}
