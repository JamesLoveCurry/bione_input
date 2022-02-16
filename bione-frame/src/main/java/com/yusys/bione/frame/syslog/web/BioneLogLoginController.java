package com.yusys.bione.frame.syslog.web;

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
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.XlsExcelExporter;
import com.yusys.bione.frame.syslog.service.BioneLogLoginBS;
import com.yusys.bione.frame.syslog.web.vo.BioneLogLoginVO;

@Controller
@RequestMapping("/bione/syslog/login")
public class BioneLogLoginController extends BaseController{
	@Autowired
	private BioneLogLoginBS loginBS; 
	/**
	 * 用户活跃度统计展示页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/frame/access/access-by-userinfo");
	}
	
	@RequestMapping(value = "detail", method = RequestMethod.GET)
	public ModelAndView detail(String userId) {
		userId = StringUtils2.javaScriptEncode(userId);
		return new ModelAndView("/frame/access/access-by-userinfo-detail", "userId", userId);
	}
	
	@RequestMapping(value = "/userDetail.*")
	@ResponseBody
	public Map<String, Object> userDetail(Pager pager, String userId){
		return this.loginBS.userDetail(pager, userId);
	}
	
	/**
	 * 获取用户活跃度信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager, String userName, String orgName, String startDate, String endDate) {
		return this.loginBS.list(pager, userName, orgName, startDate, endDate);
	}
	
	/**
	 * 导出文件
	 * 
	 * @return
	 */
	@RequestMapping(value = "/exportFile")
	public void exportFile(HttpServletResponse response, String userName, String orgName, String startDate, String endDate) {
		userName = EncodeUtils.urlDecode(userName);
		orgName = EncodeUtils.urlDecode(orgName);
		List<BioneLogLoginVO> vos=new ArrayList<BioneLogLoginVO>();
		vos=this.loginBS.export(userName, orgName, startDate, endDate);
		if(vos == null || vos.size() == 0){
			BioneLogLoginVO v = new BioneLogLoginVO();
			vos.add(v);
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelExporter fe=null;
		try {
			fe=new XlsExcelExporter(response, "UserActiveAccessInfo.xls",
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
				e1.printStackTrace();
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
