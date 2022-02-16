package com.yusys.bione.frame.syslog.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.XlsExcelExporter;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.syslog.entity.BioneLogFunc;
import com.yusys.bione.frame.syslog.service.BioneLogFuncBS;
import com.yusys.bione.frame.syslog.web.vo.BioneLogFuncVO;

/**
 * <pre>
 * Title:系统功能访问日志Controller
 * Description: 负责系统功能访问日志的记录和展示
 * </pre>
 * 
 * @author aman
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/syslog/func")
public class BioneLogFuncController extends BaseController {
	protected static Logger log = LoggerFactory
			.getLogger(BioneLogFuncController.class);
	@Autowired
	private BioneLogFuncBS logFuncBS;

	/**
	 * 系统功能访问日志展示页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/frame/access/access-logfunc-index");
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ModelAndView detail(String menuId) {
		menuId = StringUtils2.javaScriptEncode(menuId);
		return new ModelAndView("/frame/access/access-logfunc-detail", "menuId", menuId);
	}

	/**
	 * 功能访问统计列表
	 */
	@RequestMapping(value = "/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager, String funcName, String startDate, String endDate) {
		Map<String, Object> result = this.logFuncBS.list(pager, funcName, startDate, endDate);
		return result;
	}
	
	@RequestMapping(value = "/menuList.*")
	@ResponseBody
	public Map<String, Object> menuList(Pager pager, String menuId){
		return this.logFuncBS.menuList(pager, menuId);
	}

	/***
	 * 保存系统功能访问日志
	 * 
	 * @param menuId
	 */
	@RequestMapping(value = "/saveLog", method = RequestMethod.POST)
	@ResponseBody
	public void createRptIdxInfo(HttpServletRequest request, String menuId) {
		Date date = new Date();
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();

		BioneLogFunc bioneLogFunc = new BioneLogFunc();

		bioneLogFunc.setLoginIp(request.getRemoteHost());
		bioneLogFunc.setLogId(RandomUtils.uuid2());
		bioneLogFunc.setMenuId(menuId);
		bioneLogFunc.setUserId(user.getUserId());
		bioneLogFunc.setOccurTime(new Timestamp(date.getTime()));
		bioneLogFunc
				.setOccurDate(new SimpleDateFormat("yyyyMMdd").format(date));

		logFuncBS.saveLogFunc(bioneLogFunc);
	}

	public BioneLogFuncBS getLogFuncBS() {
		return logFuncBS;
	}

	public void setLogFuncBS(BioneLogFuncBS logFuncBS) {
		this.logFuncBS = logFuncBS;
	}
	
	/**
	 * 导出文件
	 * 
	 * @return
	 */
	@RequestMapping(value = "/exportFile")
	public void exportFile(HttpServletResponse response, String funcName,String startDate,String endDate) {
		List<BioneLogFuncVO> vos=new ArrayList<BioneLogFuncVO>();
		try {
			funcName=URLDecoder.decode(funcName,"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		vos=this.logFuncBS.export(funcName, startDate, endDate);
		if(vos == null || vos.size() == 0){
			BioneLogFuncVO v = new BioneLogFuncVO();
			vos.add(v);
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelExporter fe=null;
		try {
			fe=new XlsExcelExporter(response, "SysFuncUsingAccessInfo.xls",
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
