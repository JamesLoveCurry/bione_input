package com.yusys.bione.plugin.draw.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.draw.service.RptDrawBS;
/**
 * <pre>
 * Title:报表翻牌
 * Description: 提供报表翻牌信息的展示/手工翻牌/查看翻牌日志等功能
 * </pre>
 * 
 * @author hubing hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/rpt/draw")
public class RptRptDrawController extends BaseController{
	
	@Autowired
	private RptDrawBS pecDrawBs;
	
	/**
	 * 首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/draw/rpt-draw-index");
	}
	
	/**
	 * 手工翻牌
	 */
	@RequestMapping(value="edit" ,method = RequestMethod.GET)
	public ModelAndView edit() {
		return new ModelAndView("/plugin/draw/rpt-draw-edit");
	}
	/**
	 * 查看日志
	 */
	@RequestMapping(value="view" ,method = RequestMethod.GET)
	public ModelAndView view(String rptId) {
		rptId = StringUtils2.javaScriptEncode(rptId);
		return new ModelAndView("/plugin/draw/rpt-draw-view", "rptId", rptId);
	}
	
	/**
	 * 首页grid数据查询
	 * @param pager 分页对象
	 * @return 报表数据对象
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<Object[]> searchResult = pecDrawBs.getRptDrawList(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> draw = new HashMap<String, Object>();
		List<Object[]> drawList = searchResult.getResult();
		List<Map<String,Object>> drawMapList = Lists.newArrayList();
		if(drawList != null && drawList.size()>0){
			for(Object[] objs : drawList){
				Map<String,Object> drawMap = Maps.newHashMap();
				drawMap.put("rptId", objs[0]);
				drawMap.put("rptNm", objs[1]);
				drawMap.put("rptNum", objs[2]);
				drawMap.put("drawDate", objs[3]);
				drawMapList.add(drawMap);
			}
		}
		draw.put("Rows", drawMapList);
		draw.put("Total", searchResult.getTotalCount());
		return draw;
	}
	/**
	 * 手工翻牌
	 * @param ids 报表ids
	 * @param dt 翻牌日期
	 * @return 翻牌状态对象
	 */
	@RequestMapping("/rptDraw")
	@ResponseBody
	public Map<String,Object> rptDraw(String ids,String dt) {
		String[] arr = StringUtils.split(ids, ',');
		return pecDrawBs.rptDraw(arr,dt);
	}
	/**
	 * 翻牌日志查看
	 * @param pager 分页对象
	 * @param rptId 报表编号
	 * @return 翻牌日志对象
	 */
	@RequestMapping("/viewlist.*")
	@ResponseBody
	public Map<String, Object> viewList(Pager pager,String rptId) {
		SearchResult<Object[]> searchResult = pecDrawBs.getRptDrawLogList(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition(),rptId);
		Map<String, Object> rptDrawlog = new HashMap<String, Object>();
		List<Object[]> rptDrawLogList = searchResult.getResult();
		List<Map<String,Object>> rptDrawLogMapList = Lists.newArrayList();
		if(rptDrawLogList != null && rptDrawLogList.size()>0){
			for(Object[] objs : rptDrawLogList){
				Map<String,Object> drawLogMap = Maps.newHashMap();
				drawLogMap.put("drawDate", objs[0]);
				drawLogMap.put("userId", objs[1]);
				drawLogMap.put("userName", objs[2]);
				drawLogMap.put("drawTime", objs[3]);
				rptDrawLogMapList.add(drawLogMap);
			}
		}
		rptDrawlog.put("Rows", rptDrawLogMapList);
		rptDrawlog.put("Total", searchResult.getTotalCount());
		return rptDrawlog;
	}
}
