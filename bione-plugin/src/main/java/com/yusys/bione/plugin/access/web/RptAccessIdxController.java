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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.XlsExcelExporter;
import com.yusys.bione.plugin.access.service.RptAccessIdxBS;
import com.yusys.bione.plugin.access.web.vo.IdxUserAccessVO;
/**
 * 
 * <pre>
 * Title:指标访问统计 
 * Description: 指标访问统计  ： 按指标统计
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
@RequestMapping("/rpt/frame/access/idx")
public class RptAccessIdxController {
	@Autowired
	private RptAccessIdxBS RptAccessIdxBS;
	
	/**
	 * 跳转到首页 指标访问统计： 按照指标统计
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(){
		return "/plugin/access/access-by-idx-index";
	}
	
	@RequestMapping(value = "/info.*" , method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getInfo(Pager page,String indexNm, String startAccess,String endAccess) throws ParseException{
		return this.RptAccessIdxBS.getInfo(page, indexNm, startAccess, endAccess);
	}
	
	@RequestMapping(value="/detail",method = RequestMethod.GET)
	public ModelAndView detail(String indexNo,String indexVerId){
		ModelMap model = new ModelMap();
		model.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		model.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		return new ModelAndView("/plugin/access/access-by-idx-grid",model); 
	}
	
	@RequestMapping(value="/exportFile")
	@ResponseBody
	public void exportFile(HttpServletResponse response,String indexNm,String startAccess,String endAccess){
		try {
			indexNm = URLDecoder.decode(indexNm, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		List<IdxUserAccessVO> vos=new ArrayList<IdxUserAccessVO>();
		vos = this.RptAccessIdxBS.export(indexNm,startAccess,endAccess);
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelExporter fe=null;
		try {
			fe=new XlsExcelExporter(response, "IdxUserAccessInfo.xls",
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
	
	@RequestMapping(value="/search.*",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> findDetail(Pager pager ,String indexNo,String indexVerId){
		return this.RptAccessIdxBS.findInfo(pager,indexNo,indexVerId);
	}
	
	/**
	 * 跳转到首页 指标访问统计： 按照用户统计
	 * @return
	 */
	@RequestMapping(value = "/user",method = RequestMethod.GET)
	public String userIndex(){
		return "/plugin/access/access-by-idx-user-index";
	}
	@RequestMapping(value="/user/detail",method = RequestMethod.GET)
	public ModelAndView userDetail(String userId){
		userId = StringUtils2.javaScriptEncode(userId);
		return new ModelAndView("/plugin/access/access-by-idx-user-grid", "userId", userId); 
	}
	@RequestMapping(value = "/user/info.*" , method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> userGetInfo(Pager page,String userNo, String userName,String orgName, String startAccess,String endAccess){
		return this.RptAccessIdxBS.getUserGetInfo(page, userNo, userName, orgName ,startAccess, endAccess);
	}
	@RequestMapping(value="/user/search",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> findDetail(Pager pager, String userId){
		return this.RptAccessIdxBS.findUserDetail(pager, userId);
	}
	@RequestMapping(value = "/user/exportFile")
	@ResponseBody
	public void userExportFile(HttpServletResponse response,String userNo, String userName,String orgName, String startAccess,String endAccess){
		try {
			userName = URLDecoder.decode(userName, "UTF-8");
			orgName = URLDecoder.decode(orgName, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		List<IdxUserAccessVO> vos=new ArrayList<IdxUserAccessVO>();
		vos = this.RptAccessIdxBS.userExport( userNo, userName, orgName, startAccess,endAccess);
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelExporter fe=null;
		try {
			fe=new XlsExcelExporter(response, "IdxUserAccessInfo.xls",
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
