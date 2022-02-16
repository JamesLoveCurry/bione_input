package com.yusys.bione.plugin.access.web;

import java.io.IOException;
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
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.XlsExcelExporter;
import com.yusys.bione.plugin.access.service.RptAccessUserBS;
import com.yusys.bione.plugin.access.web.vo.RptByUserAccessVO;

/**
 * 
 * <pre>
 * Description: 报表访问统计 ： 按照用户统计
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
@RequestMapping("/rpt/frame/accuser")
public class RptAccessUserController {
	@Autowired
	private RptAccessUserBS rptAccessUserBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index(){
		return "/plugin/access/access-by-user-index";
	}
	
	@RequestMapping(value = "/info.*" , method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getInfo(Pager page,String userNo, String userName,String orgName, String startAccess,String endAccess){
		return this.rptAccessUserBS.getInfo(page,userNo, userName, orgName ,startAccess, endAccess);
	}
	
	@RequestMapping(value="/detail",method = RequestMethod.GET)
	public ModelAndView detail(String userId){
		userId = StringUtils2.javaScriptEncode(userId);
		return new ModelAndView("/plugin/access/access-by-user-grid", "userId", userId); 
	}
	
	@RequestMapping(value="/search",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> findDetail(Pager pager, String userId){
		return this.rptAccessUserBS.findInfo(pager, userId);
	}
	
	@RequestMapping(value="/exportFile")
	@ResponseBody
	public void exportFile(HttpServletResponse response,String userNo, String userName,String orgName, String startAccess,String endAccess){
		List<RptByUserAccessVO> vos=new ArrayList<RptByUserAccessVO>();
		if (userName != null)
			userName = EncodeUtils.urlDecode(userName);
		if (orgName != null)
			orgName = EncodeUtils.urlDecode(orgName);
			
		vos = this.rptAccessUserBS.export(userNo, userName,orgName, startAccess, endAccess);
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
