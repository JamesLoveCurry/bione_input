package com.yusys.bione.plugin.access.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.XlsExcelExporter;
import com.yusys.bione.plugin.access.service.RptAccessReportBS;
import com.yusys.bione.plugin.access.web.vo.DeptUserAccessVO;
/**
 * 
 * <pre>
 * Description: 报表访问统计 ： 按照报表统计
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/access")
public class RptAccessReportController {
	
	@Autowired
	private RptAccessReportBS RptAccessReportBs;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index(){
		return "/plugin/access/access-by-report-index";
	}
	@RequestMapping(value = "/chart",method = RequestMethod.GET)
	public String showChart(){
		return "/plugin/access/access-by-report-chart";
	}
	
	@RequestMapping(value = "/info.*" , method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getInfo(Pager page, String rptNm, String startAccess, String endAccess) throws ParseException{
		return this.RptAccessReportBs.getInfo(page, rptNm, startAccess, endAccess);
	}
	
	@RequestMapping(value="/detail",method = RequestMethod.GET)
	public ModelAndView detail(String rptId){
		rptId = StringUtils2.javaScriptEncode(rptId);
		return new ModelAndView("/plugin/access/access-by-report-grid", "rptId", rptId); 
	}
	
	@RequestMapping(value="/search",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> findDetail(Pager pager ,String rptId){
		return this.RptAccessReportBs.findInfo(pager, rptId);
	}
	
	@RequestMapping(value="/exportFile")
	@ResponseBody
	public void exportFile(HttpServletResponse response,String rptNm,String startAccess,String endAccess){
		try {
			rptNm = URLDecoder.decode(rptNm, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		List<DeptUserAccessVO> vos=new ArrayList<DeptUserAccessVO>();
		vos = this.RptAccessReportBs.export(rptNm,startAccess,endAccess);
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelExporter fe=null;
		try {
			fe=new XlsExcelExporter(response, "RptUserAccessInfo.xls",
					list);
			fe.run();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.reset();
				response.setContentType("text/html;charset=UTF-8");
				response.getOutputStream().write(
						"<script>parent.BIONE.tip('导出失败')</script>".getBytes());
			} catch (IOException e1) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (fe != null) {
					fe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		
	}
}
