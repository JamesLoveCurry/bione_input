package com.yusys.bione.plugin.mainpage.web;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.authres.service.MenuBS;
import com.yusys.bione.frame.authres.web.vo.BioneMenuInfoVO;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mainpage.entity.BioneMpModuleInfo;
import com.yusys.bione.frame.mainpage.service.MainpageBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.mainpage.service.RptMainpageBS;

@Controller
@RequestMapping("/rpt/frame/mainpage")
public class RptMainPageController extends BaseController {
	
	@Autowired
	private RptMainpageBS rptMainpageBS;
	@Autowired
	private MenuBS menuBS;
	@Autowired
	private MainpageBS mainpageBS;
	
	
	/**
	 * 跳转：首页-公告
	 * @return
	 */
	@RequestMapping(value="annoucement",method = RequestMethod.GET)
	public String index() {
		return "/plugin/mainpage/mainpage-module-annoucement";
	}
	
	/**
	 * 跳转：首页-机构变动提醒
	 * @return
	 */
	@RequestMapping(value="org",method = RequestMethod.GET)
	public String org() {
		return "/plugin/mainpage/mainpage-module-orgchange";
	}
	
	/**
	 * 跳转：首页-科目变动提醒
	 * @return
	 */
	@RequestMapping(value="item",method = RequestMethod.GET)
	public String item() {
		return "/plugin/mainpage/mainpage-module-itemchange";
	}
	
	/**
	 * 跳转：首页-我的收藏页
	 * @return
	 */
	@RequestMapping(value="myfav",method = RequestMethod.GET)
	public ModelAndView myfav(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-fav");
	}
	
	/**
	 * 跳转：首页-历史访问报表页
	 * @return
	 */
	@RequestMapping("/showReport")
	public ModelAndView showReport(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-report");
	}
	
	/**
	 * 跳转：首页-我的收藏TAB页
	 * @return
	 */
	@RequestMapping("myfav/frame")
	public String frame() {
		return "/plugin/mainpage/mainpage-module-fav-frame";
	}
	
	/**
	 * 跳转：首页-我的收藏报表指标页
	 * @return
	 */
	@RequestMapping("myfav/rptTab")
	public String rptTab() {
		return "/plugin/mainpage/mainpage-module-fav-report";
	}
	
	/**
	 * 跳转：首页-我的收藏指标查询页
	 * @return
	 */
	@RequestMapping("myfav/idxTab")
	public String idxTab() {
		return "/plugin/mainpage/mainpage-module-fav-index";
	}
	
	/**
	 * 获取报表历史访问信息
	 * @return
	 */
	@RequestMapping("/getReportHistory")
	@ResponseBody
	public Map<String,Object> getReportHistory(Pager pager,String size,String flag){
		String userId = BioneSecurityUtils.getCurrentUserId();
		if(flag==null||flag.equals("")){
			if(StringUtils.isNotBlank(size))
				pager.setPagesize(Integer.valueOf(size));
		}
		return this.rptMainpageBS.getReportHistory(pager,userId);
	}
	
	/**
	 * 更多历史访问报表
	 * @return
	 */
	@RequestMapping("/getMoreHistory")
	public ModelAndView getMoreHistory(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-report-more");
	}
	
	/**
	 * 历史访问报表
	 * @return
	 */
	@RequestMapping("/showMoreHistoryRpt")
	public ModelAndView showMoreHistoryRpt(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-report-more-rpt");
	}
	
	/**
	 * 历史访问报表
	 * @return
	 */
	@RequestMapping("/showMoreRptList")
	public ModelAndView showMoreRptList(String rptId){
		rptId = StringUtils2.javaScriptEncode(rptId);
		return new ModelAndView("/plugin/mainpage/more-report-grid", "rptId", rptId);
	}
	
	@RequestMapping("/getReportList")
	@ResponseBody
	public Map<String,Object> getReportList(Pager pager,String rptId){
		String userId = BioneSecurityUtils.getCurrentUserId();
		return this.rptMainpageBS.getReportList(pager,userId,rptId);
	}
	
	/**
	 * 跳转--待办事项
	 */
	@RequestMapping(value = "/blacklog",method = RequestMethod.GET)
	public ModelAndView blacklog(String taskType) {
		List<CommonTreeNode> menuInfoList = this.menuBS.buildMenuTree("FRS", "0", false);
		ModelMap map = new ModelMap();
		String mesuNmstr ="";
		String mesuIdstr ="";
		if(menuInfoList.size()>0){
			for (CommonTreeNode nodes : menuInfoList) {
				BioneMenuInfoVO menuInfos = (BioneMenuInfoVO) nodes.getData();
					mesuNmstr = mesuNmstr + "," + menuInfos.getFuncName();
					mesuIdstr = mesuIdstr + "," + menuInfos.getMenuId();
			}	
		}
		map.put("mesuNmstr", StringUtils2.javaScriptEncode(mesuNmstr));
		map.put("mesuIdstr", StringUtils2.javaScriptEncode(mesuIdstr));
		map.put("taskType", taskType);
		return new ModelAndView("/plugin/mainpage/mainpage-module-blacklog",map);
	}
	
	/**
	 * 跳转：首页-指标报表查询
	 */
	@RequestMapping("/search")
	public ModelAndView search() {
		return new ModelAndView("/plugin/mainpage/mainpage-search-index-report");
	}
	
	/**
	 * 跳转：首页-快速导航查询
	 */
	@RequestMapping("/imageSearch")
	public ModelAndView imageSearch() {
		return new ModelAndView("/plugin/mainpage/mainpage-image-search");
	}
	
	/***
	 * 判断该用户是否具有报文备份、明细数据查询、指标追溯分析、明细灵活查询、指标灵活查询权限
	 * 
	 * @param userId
	 * @return result
	 */
	@RequestMapping(value = "/isHasInfoRights.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> isHasInfoRights() {
		Map<String, String> param = new HashMap<String, String>();
		Map<String, String> menus = new HashMap<String, String>();
		Map<String, String> judge = new HashMap<String, String>();
		param.put("rpt","c1349acf17404fb29096b26ecb3ef368");
		param.put("idxAna","c1349acf17404fb29096b26ecb3ef368");
		param.put("idxDeA","58135d51c4194c0f981c98287abe8109");
		param.put("detailSe","12ca466992fb4f37b76c6498343047cb");
		param.put("idxSe","d27a2788018b4534aa809c7dfdce911e");
		
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> setMenus = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4frame.AUTH_RES_DEF_ID_MENU);
			for(String menuId : setMenus){
				menus.put(menuId, menuId);
			}
		    if(menus.get(param.get("rpt")) != null&&!"".equals(menus.get(param.get("rpt")))){
		    	judge.put("rpt", "1");
		    }else{
		    	judge.put("rpt", "0");
		    }
		    if(menus.get(param.get("idxAna")) != null&&!"".equals(menus.get(param.get("idxAna")))){
		    	judge.put("idxAna", "1");
		    }else{
		    	judge.put("idxAna", "0");
		    }
		    if(menus.get(param.get("idxDeA")) != null&&!"".equals(menus.get(param.get("idxDeA")))){
		    	judge.put("idxDeA", "1");
		    }else{
		    	judge.put("idxDeA", "0");
		    }
		    if(menus.get(param.get("detailSe")) != null&&!"".equals(menus.get(param.get("detailSe")))){
		    	judge.put("detailSe", "1");
		    }else{
		    	judge.put("detailSe", "0");
		    }
		    if(menus.get(param.get("idxSe")) != null&&!"".equals(menus.get(param.get("idxSe")))){
		    	judge.put("idxSe", "1");
		    }else{
		    	judge.put("idxSe", "0");
		    }
		}
		else{
			judge.put("rpt", "1");
			judge.put("idxAna", "1");
			judge.put("idxDeA", "1");
			judge.put("detailSe", "1");
			judge.put("idxSe", "1");
		}
		return judge;
	}

	/**
	 * 跳转：首页-图表分析
	 * @return
	 */
	@RequestMapping(value="/analysis",method = RequestMethod.GET)
	public ModelAndView analysis(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-chart-analysis");
	}
	
	/**
	 * 跳转：首页-指标分析TAB页
	 * @return
	 */
	@RequestMapping(value="/analysis/frame",method = RequestMethod.GET)
	public ModelAndView analysisFrame(String tabNo){
		tabNo = StringUtils2.javaScriptEncode(tabNo);
		return new ModelAndView("/plugin/mainpage/mainpage-module-analysis-frame", "tabNo", tabNo);
	}
	
	/**
	 * 跳转：首页-图表分析-Tab页跳转
	 * @return
	 */
	@RequestMapping(value="/charts",method = RequestMethod.GET)
	public ModelAndView charts(String tabNo){
		String url = null;
		if(tabNo.equals("1")){
			url="/plugin/mainpage/mainpage-module-rpt-use";
		}else if(tabNo.equals("2")){
			url="/plugin/mainpage/mainpage-module-idx-use";
		}else if(tabNo.equals("3")){
			url="/plugin/mainpage/mainpage-module-idx-month";
		}else if(tabNo.equals("4")){
			url="/plugin/mainpage/mainpage-module-rpt-num";
		}else if(tabNo.equals("5")){
			url="/plugin/mainpage/mainpage-module-idx-num";
		}
		return new ModelAndView(url);
	}
	/**
	 * 跳转：首页-图表分析-月新增指标数
	 * @return
	 */
	@RequestMapping(value="/idxMonth",method = RequestMethod.GET)
	public ModelAndView idxMonth(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-idx-month");
	}
	/**
	 * 返回数据：首页-图表分析-月新增指标数
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getIdxData")
	@ResponseBody
	public Map<String,Object> getIdxData(Pager pager) throws ParseException{
		Map<String,Object> resultMap= new HashMap<String, Object>();
		//日期处理，获取X轴月份范围，规则：当前月份及之前12个月
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String date = df.format(new Date());
		int year = Integer.parseInt(date.substring(0,4))-1;
		int month = Integer.parseInt(date.substring(5,7))+1;
		if(month > 12){
			month =month-12;
			year = year+1;
		} 
		if(month<10){
			date =  String.valueOf(year)+"-0"+String.valueOf(month);
		}else{
			date =  String.valueOf(year)+"-"+String.valueOf(month);
		}	
		List<String[]> idxList = this.rptMainpageBS.getIdxData(date);//获取统计月份及相应月份的新增指标数
		List<String> xAxis= Lists.newArrayList();
		List<Integer> series= Lists.newArrayList();
		Map<String,String> map = Maps.newHashMap();
		for(Object[] idx : idxList){
			map.put(idx[1].toString(), idx[0].toString());
		}
		//对统计月份没有新增指标数的数据自动赋值为0
		for(int i= month;i<month+12;i++){
			String day;
			if(i>12){
				day =  String.valueOf(year+1)+"-0"+String.valueOf(i-12);
			}else if(i<10){
				day =  String.valueOf(year)+"-0"+String.valueOf(i);
			}else{
				day =  String.valueOf(year)+"-"+String.valueOf(i);
			}	
			xAxis.add(day);
			if(map.get(day)!=null){
				try{
					series.add(Integer.parseInt(map.get(day).toString()));
				}
				catch(Exception e){
					series.add(0);
				}
			}
			else{
				series.add(0);
			}
			
		}
		resultMap.put("xAxis", xAxis);
		resultMap.put("series", series);
		return resultMap;
	}
	
	/**
	 * 跳转：首页-图表分析-报表数量统计
	 * @return
	 */
	@RequestMapping(value="/rptNum",method = RequestMethod.GET)
	public ModelAndView rptNum(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-rpt-num");
	}

	/**
	 * 返回数据：首页-图表分析-报表数量统计
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getRptNum")
	@ResponseBody
	public Map<String,Object> getRptNum(Pager pager) throws ParseException{
		Map<String,Object> resultMap= new HashMap<String, Object>();
		List<Object[]> rptList = this.rptMainpageBS.getRptNum(); //根据业务条线分组并获取对应报表数量
		List<String> xAxis= Lists.newArrayList();
		List<Integer> series= Lists.newArrayList();
		Map<String,String> map = Maps.newHashMap();
		for(Object[] rpt : rptList){
			map.put(rpt[1].toString(), rpt[0].toString());
		}
		
		//统计结果存放至resultMap中，向前台传递
		List<Object[]> rptLines = this.rptMainpageBS.getDepLineList();
		for(Object[] rptLine : rptLines){
			xAxis.add(rptLine[1].toString());
			if(map.get(rptLine[0].toString())!=null){
				try{
					series.add(Integer.parseInt(map.get(rptLine[0].toString())));
				}
				catch(Exception e){
					System.out.print("数据类型转换有误！");
				}
			}else{
				series.add(0);
			}
		}
		resultMap.put("xAxis", xAxis);
		resultMap.put("series", series);
		return resultMap;
	}
	
	/**
	 * 跳转：首页-图表分析-指标数量统计
	 * @return
	 */
	@RequestMapping(value="/idxNum",method = RequestMethod.GET)
	public ModelAndView idxNum(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-idx-num");
	}

	/**
	 * 返回数据：首页-图表分析-指标数量统计
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getIdxNum")
	@ResponseBody
	public Map<String,Object> getIdxNum(Pager pager) throws ParseException{
		Map<String,Object> resultMap= new HashMap<String, Object>();
		List<Object[]> idxList = this.rptMainpageBS.getIdxNum();//根据业务条线分组并获取对应指标数量
		List<String> xAxis= Lists.newArrayList();
		List<Integer> series= Lists.newArrayList();
		Map<String,String> map = Maps.newHashMap();
		for(Object[] idx : idxList){
			map.put(idx[1].toString(), idx[0].toString());
		}
		
		//统计结果存放至resultMap中，向前台传递
		List<Object[]> idxLines = this.rptMainpageBS.getDepLineList();
		for(Object[] idxLine : idxLines){
			xAxis.add(idxLine[1].toString());
			if(map.get(idxLine[0].toString())!=null){
				try{
					series.add(Integer.parseInt(map.get(idxLine[0].toString())));
				}
				catch(Exception e){
					System.out.print("数据类型转换有误！");
				}
			}else{
				series.add(0);
			}
		}
		resultMap.put("xAxis", xAxis);
		resultMap.put("series", series);
		return resultMap;
	}
	
	/**
	 * 跳转：首页-图表分析-报表使用情况统计
	 * @return
	 */
	@RequestMapping(value="/rptUse",method = RequestMethod.GET)
	public ModelAndView rptUse(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-rpt-use");
	}
	/**
	 * 返回数据：首页-图表分析-报表使用情况统计（图1）
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getRptUse")
	@ResponseBody
	public Map<String,Object> getRptUse(Pager pager) throws ParseException{
		Map<String,Object> resultMap= new HashMap<String, Object>();
		List<Object[]> rptUseLists = this.rptMainpageBS.getRptLineData();//根据业务条线分组并获取对应用户使用数
		List<String> xAxis= Lists.newArrayList();
		List<Integer> series= Lists.newArrayList();
		Map<String,String> map = Maps.newHashMap();
		for(Object[] rptUseList : rptUseLists){
			map.put(rptUseList[1].toString(), rptUseList[0].toString());
		}
		
		//统计结果存放至resultMap中，向前台传递
		List<Object[]> rptLines = this.rptMainpageBS.getDepLineList();
		for(Object[] rptLine : rptLines){
			xAxis.add(rptLine[1].toString());
			if(map.get(rptLine[0].toString())!=null){
				try{
					series.add(Integer.parseInt(map.get(rptLine[0].toString())));
				}
				catch(Exception e){
					System.out.print("数据类型转换有误！");
				}
			}else{
				series.add(0);
			}
		}
		resultMap.put("xAxis", xAxis);
		resultMap.put("series", series);
		return resultMap;
	}
	/**
	 * 返回数据：首页-图表分析-报表使用情况统计（图2）
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getRptUse1")
	@ResponseBody
	public Map<String,Object> getRptUse1(Pager pager) throws ParseException{
		Map<String,Object> resultMap= new HashMap<String, Object>();
		List<Object[]> rptUseLists = this.rptMainpageBS.getRptUse1();//根据经营单位分组并获取对应用户使用数
		List<String> xAxis= Lists.newArrayList();
		List<Integer> series= Lists.newArrayList();
		Map<String,String> map = Maps.newHashMap();
		for(Object[] rptUseList : rptUseLists){
			map.put(rptUseList[1].toString(), rptUseList[0].toString());
		}
		//获取所有非一级、二级的机构目录，用于对比
		List<Object[]> rptOrgLists = this.rptMainpageBS.getOrgNo();                                                  
		Map<String,String> orgNo = Maps.newHashMap();
		for(Object[] orgList : rptOrgLists){
			orgNo.put(orgList[0].toString(), orgList[1].toString());
		}
		//对非一级、二级机构目录下的机构追溯至其二级目录
		for(Object[] rptOrgList : rptUseLists){
			List<String> orgId = this.rptMainpageBS.getRptOrgUp(rptOrgList[1].toString());
			if(orgId.size()>0){
				while(orgNo.get(orgId.get(0))!=null){
					orgId = this.rptMainpageBS.getRptOrgUp(orgId.get(0));
				}
			}
			//对一个二级目录其下所有子目录拥有的用户使用数进行累计相加
			try{
				Integer count;
				if(map.get(orgId.get(0))!=null&&map.get(rptOrgList[1].toString())!=null){
					count = Integer.parseInt(map.get(orgId.get(0)))+Integer.parseInt(map.get(rptOrgList[1].toString()));
					}else if(map.get(orgId.get(0))==null&&map.get(rptOrgList[1].toString())!=null){
						count = 0+Integer.parseInt(map.get(rptOrgList[1].toString()));
					}else if(map.get(orgId.get(0))!=null&&map.get(rptOrgList[1].toString())==null){
						count = Integer.parseInt(map.get(orgId.get(0)))+0;
					}else{
						count = 0;
					}
				String sum = String.valueOf(count);
				map.put(orgId.get(0),sum);
			}
			catch(Exception e){
				System.out.print("数据类型转换有误1！");
			}
		}
		//统计结果存放至resultMap中，向前台传递
		List<Object[]> rptOrgs = this.rptMainpageBS.getRptOrg();
		for(Object[] rptOrg : rptOrgs){
			xAxis.add(rptOrg[1].toString());
			if(map.get(rptOrg[0].toString())!=null){
				try{
					series.add(Integer.parseInt(map.get(rptOrg[0].toString())));
				}
				catch(Exception e){
					System.out.print("数据类型转换有误2！");
				}
			}else{
				series.add(0);
			}
		}
		resultMap.put("xAxis", xAxis);
		resultMap.put("series", series);
		return resultMap;
	}
	
	/**
	 * 跳转：首页-图表分析-指标使用情况统计
	 * @return
	 */
	@RequestMapping(value="/idxUse",method = RequestMethod.GET)
	public ModelAndView idxUse(){
		return new ModelAndView("/plugin/mainpage/mainpage-module-idx-use");
	}
	
	/**
	 * 返回数据：首页-图表分析-指标使用情况统计
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getIdxUse")
	@ResponseBody
	public Map<String,Object> getIdxUse(Pager pager) throws ParseException{
		Map<String,Object> resultMap= new HashMap<String, Object>();
		List<Object[]> idxList = this.rptMainpageBS.getIdxLineData();//根据业务条线分组并获取对应用户使用数
		List<String> xAxis= Lists.newArrayList();
		List<Integer> series= Lists.newArrayList();
		Map<String,String> map = Maps.newHashMap();
		for(Object[] idx : idxList){
			map.put(idx[1].toString(), idx[0].toString());
		}	
		//统计结果存放至resultMap中，向前台传递
		List<Object[]> idxLines = this.rptMainpageBS.getDepLineList();
		for(Object[] idxLine : idxLines){
			xAxis.add(idxLine[1].toString());
			if(map.get(idxLine[0].toString())!=null){
				try{
					series.add(Integer.parseInt(map.get(idxLine[0].toString())));
				}
				catch(Exception e){
					System.out.print("数据类型转换有误！");
				}
			}else{
				series.add(0);
			}
		}
		resultMap.put("xAxis", xAxis);
		resultMap.put("series", series);
		return resultMap;
	}
	
	/**
	 * 返回数据：首页-图表分析-报表使用情况统计（图2）
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getIdxUse1")
	@ResponseBody
	public Map<String,Object> getIdxUse1(Pager pager) throws ParseException{
		Map<String,Object> resultMap= new HashMap<String, Object>();
		List<Object[]> idxUseLists = this.rptMainpageBS.getIdxUse1();//根据经营单位分组并获取对应用户使用数
		List<String> xAxis= Lists.newArrayList();
		List<Integer> series= Lists.newArrayList();
		Map<String,String> map = Maps.newHashMap();
		for(Object[] idxUseList : idxUseLists){
			map.put(idxUseList[1].toString(), idxUseList[0].toString());
		}
		//获取所有非一级、二级的机构目录，用于对比
		List<Object[]> idxOrgLists = this.rptMainpageBS.getOrgNo();                                                  
		Map<String,String> orgNo = Maps.newHashMap();
		for(Object[] orgList : idxOrgLists){
			orgNo.put(orgList[0].toString(), orgList[1].toString());
		}
		//对非一级、二级机构目录下的机构追溯至其二级目录
		for(Object[] idxOrgList : idxUseLists){
			List<String> orgId = this.rptMainpageBS.getRptOrgUp(idxOrgList[1].toString());
			if(orgId.size()>0){
				while(orgNo.get(orgId.get(0))!=null){
					orgId = this.rptMainpageBS.getRptOrgUp(orgId.get(0));
				}
			}
			//对一个二级目录其下所有子目录拥有的用户使用数进行累计相加
			try{
				Integer count;
				if(map.get(orgId.get(0))!=null&&map.get(idxOrgList[1].toString())!=null){
					count = Integer.parseInt(map.get(orgId.get(0)))+Integer.parseInt(map.get(idxOrgList[1].toString()));
					}else if(map.get(orgId.get(0))==null&&map.get(idxOrgList[1].toString())!=null){
						count = 0+Integer.parseInt(map.get(idxOrgList[1].toString()));
					}else if(map.get(orgId.get(0))!=null&&map.get(idxOrgList[1].toString())==null){
						count = Integer.parseInt(map.get(orgId.get(0)))+0;
					}else{
						count = 0;
					}
				String sum = String.valueOf(count);
				map.put(orgId.get(0),sum);
			}
			catch(Exception e){
				System.out.print("数据类型转换有误1！");
			}
		}
		//统计结果存放至resultMap中，向前台传递
		List<Object[]> idxOrgs = this.rptMainpageBS.getRptOrg();
		for(Object[] idxOrg : idxOrgs){
			xAxis.add(idxOrg[1].toString());
			if(map.get(idxOrg[0].toString())!=null){
				try{
					series.add(Integer.parseInt(map.get(idxOrg[0].toString())));
				}
				catch(Exception e){
					System.out.print("数据类型转换有误2！");
				}
			}else{
				series.add(0);
			}
		}
		resultMap.put("xAxis", xAxis);
		resultMap.put("series", series);
		return resultMap;
	}
	
	/**
	 * 跳转：首页-快速导航查询
	 */
	@RequestMapping("/controlPanel")
	public ModelAndView controlPanel() {
		return new ModelAndView("/plugin/mainpage/mainpage-control-panel");
	}
	
	/**
	 * 跳转：首页-每月新增指标统计
	 */
	@RequestMapping("/newIndexStatistics")
	public ModelAndView newIndexStatistics() {
		return new ModelAndView("/plugin/mainpage/mainpage-new-statistics");
	}
	
	/**
	 * 跳转：首页-每月填报任务完成情况
	 */
	@RequestMapping("/fillTaskCompletion")
	public ModelAndView fillTaskCompletion() {
		return new ModelAndView("/plugin/mainpage/mainpage-fillTask-completion");
	}
	
	/**
	 * 跳转：首页-填报详细信息
	 */
	@RequestMapping("/fillDetails")
	public ModelAndView fillDetails() {
		return new ModelAndView("/plugin/mainpage/mainpage-fill-details");
	}
	
	/**
	 * 
	 * @param moduleId
	 * @return
	 */
	@RequestMapping("/commonLayout/{moduleId}")
	public ModelAndView commonLayout(@PathVariable String moduleId) {
		BioneMpModuleInfo moduleInfo = (BioneMpModuleInfo) mainpageBS.getEntityById(BioneMpModuleInfo.class, moduleId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("moduleType", moduleInfo.getModuleType());
		result.put("chartType", moduleInfo.getChartType());
		//poc图表
		if("01".equals(moduleInfo.getModuleType())) {
			result.put("moduleDefine", moduleInfo.getModuleDefine());
		}
		//sql图表
		else if("02".equals(moduleInfo.getModuleType())) {
			String sql = moduleInfo.getModuleDefine();
			if (StringUtils.isEmpty(sql)) {
				throw new NullPointerException("empty sql");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			if("03".equals(moduleInfo.getChartType())) {
				List<Object[]> list= rptMainpageBS.commonLayout(sql);
				List<YAxis> tmpList = new ArrayList<YAxis>();
				for(Object[] obj : list) {
					YAxis yAxis = new YAxis();
					for(int i=0; i<obj.length; i++) {
						if(i == 0) {
							yAxis.setName((String) obj[i]);
						}
						else {
							if(obj[i] == null) {
								yAxis.setData("0");
							}
							else {
								yAxis.setData((String) obj[i]);
							}
						}
					}
					tmpList.add(yAxis);
				}
				map.put("yAxis", tmpList);
			}
			else {
				//解析x轴数据
				String xAxisStr = sql.substring(sql.indexOf("'"), sql.lastIndexOf("'")+1);
				List<String> xAxis = new ArrayList<String>();
				for(String str : xAxisStr.split(",")) {
					xAxis.add(str.substring(1,str.length()-1));
				}
				String exeSql = sql.substring(0, sql.indexOf("'")) + sql.substring(sql.lastIndexOf("'")+2);
				List<Object[]> list= rptMainpageBS.commonLayout(exeSql);
				List<YAxisArr> tmpList = new ArrayList<YAxisArr>();
				for(Object[] obj : list) {
					YAxisArr yAxisArr = new YAxisArr();
					yAxisArr.setData(new ArrayList<Integer>());
					for(int i=0; i<obj.length; i++) {
						if(i == 0) {
							yAxisArr.setName((String) obj[i]);
						}
						else {
							if(obj[i] == null) {
								yAxisArr.getData().add(0);
							}
							else {
								yAxisArr.getData().add(Integer.parseInt((String)obj[i]));
							}
						}
					}
					tmpList.add(yAxisArr);
				}
				map.put("xAxis", xAxis);
				map.put("yAxis", tmpList);
			}
			map.put("title", moduleInfo.getModuleName());
			JSONObject params = (JSONObject)JSON.toJSON(map);
			result.put("moduleDefine", params);
			return new ModelAndView("/plugin/mainpage/mainpage-module-common", result);
		}
		//sql表格
		else if("11".equals(moduleInfo.getModuleType())) {
			Map<String, Object> map = new HashMap<String, Object>();
			String sql = moduleInfo.getModuleDefine();
			if (StringUtils.isEmpty(sql)) {
				throw new NullPointerException("empty sql");
			}
			//获取表头
			String xAxisStr = sql.substring(sql.indexOf("'"), sql.lastIndexOf("'")+1);
			List<String> xAxis = new ArrayList<String>();
			for(String str : xAxisStr.split(",")) {
				xAxis.add(str.substring(1,str.length()-1));
			}
			String exeSql = sql.substring(0, sql.indexOf("'")) + sql.substring(sql.lastIndexOf("'")+2);
			List<Object[]> list= rptMainpageBS.commonLayout(exeSql);
			//表头对应英文
			String[] columnsTmp = exeSql.substring(exeSql.indexOf("select ")+7, exeSql.indexOf("from")).split(",");
			List<String> columns = new ArrayList<String>();
			for(String str : columnsTmp) {
				columns.add(str.trim());
			}
			//获取数据
			List<Map<String, Object>> yAxis = new ArrayList<Map<String, Object>>();
			for(Object[] obj : list) {
				Map<String, Object> mapObj = new HashMap<String, Object>();
				for(int j=0;j<columns.size();j++) {
					mapObj.put(columns.get(j), obj[j]);
				}
				yAxis.add(mapObj);
			}
			map.put("xAxis", xAxis);//表头
			map.put("columns", columns);//表头对应英文
			map.put("yAxis", yAxis);//数据
			map.put("title", moduleInfo.getModuleName());
			JSONObject params = (JSONObject)JSON.toJSON(map);
			result.put("moduleDefine", params);//没有用到
			return new ModelAndView("/plugin/mainpage/mainpage-module-common", result);
		}
		return new ModelAndView("/plugin/mainpage/mainpage-module-common", result);
	}
	//封装y轴数据
	class YAxis{
		private String name;
		private String data;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
	}
	//封装y轴数据
	class YAxisArr{
		private String name;
		private List<Integer> data;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<Integer> getData() {
			return data;
		}
		public void setData(List<Integer> data) {
			this.data = data;
		}
	}
}
