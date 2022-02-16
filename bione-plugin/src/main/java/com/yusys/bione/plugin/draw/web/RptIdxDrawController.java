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
 * Title:指标翻牌
 * Description: 提供指标翻牌信息的展示/手工翻牌/查看翻牌日志等功能
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
@RequestMapping("/rpt/idx/draw")
public class RptIdxDrawController extends BaseController{
	
	@Autowired
	private RptDrawBS pecDrawBs;
	
	/**
	 * 首页
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/draw/idx-draw-index");
	}
	
	/**
	 * 手工翻牌
	 */
	@RequestMapping(value="edit" ,method = RequestMethod.GET)
	public ModelAndView edit() {
		return new ModelAndView("/plugin/draw/idx-draw-edit");
	}
	/**
	 * 查看日志
	 */
	@RequestMapping(value="view" ,method = RequestMethod.GET)
	public ModelAndView view(String indexNo) {
		indexNo = StringUtils2.javaScriptEncode(indexNo);
		return new ModelAndView("/plugin/draw/idx-draw-view", "indexNo", indexNo);
	}
	
	/**
	 * 首页grid数据查询
	 * @param pager 分页对象
	 * @return 指标数据对象
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<Object[]> searchResult = pecDrawBs.getIdxDrawList(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> idxDraw = new HashMap<String, Object>();
		List<Object[]> idxDrawList = searchResult.getResult();
		List<Map<String,Object>> idxDrawMapList = Lists.newArrayList();
		if(idxDrawList != null && idxDrawList.size()>0){
			for(Object[] objs : idxDrawList){
				Map<String,Object> drawMap = Maps.newHashMap();
				drawMap.put("indexNo", objs[0]);
				drawMap.put("indexNm", objs[1]);
				drawMap.put("drawDate", objs[2]);
				idxDrawMapList.add(drawMap);
			}
		}
		idxDraw.put("Rows", idxDrawMapList);
		idxDraw.put("Total", searchResult.getTotalCount());
		return idxDraw;
	}
	/**
	 * 翻牌日志查看
	 * @param pager 分页对象
	 * @param indexNo 指标编号
	 * @return 翻牌日志对象
	 */
	@RequestMapping("/viewlist.*")
	@ResponseBody
	public Map<String, Object> viewList(Pager pager,String indexNo) {
		SearchResult<Object[]> searchResult = pecDrawBs.getIdxDrawLogList(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition(),indexNo);
		Map<String, Object> idxDrawlog = new HashMap<String, Object>();
		List<Object[]> idxDrawLogList = searchResult.getResult();
		List<Map<String,Object>> idxDrawLogMapList = Lists.newArrayList();
		if(idxDrawLogList != null && idxDrawLogList.size()>0){
			for(Object[] objs : idxDrawLogList){
				Map<String,Object> drawLogMap = Maps.newHashMap();
				drawLogMap.put("drawDate", objs[0]);
				drawLogMap.put("userId", objs[1]);
				drawLogMap.put("userName", objs[2]);
				drawLogMap.put("drawTime", objs[3]);
				idxDrawLogMapList.add(drawLogMap);
			}
		}
		idxDrawlog.put("Rows", idxDrawLogMapList);
		idxDrawlog.put("Total", searchResult.getTotalCount());
		return idxDrawlog;
	}
	/**
	 * 手工翻牌
	 * @param ids 指标ids
	 * @param dt 翻牌日期
	 * @return 翻牌状态对象
	 */
	@RequestMapping("/idxDraw")
	@ResponseBody
	public Map<String,Object> idxDraw(String ids,String dt) {
		String[] arr = StringUtils.split(ids, ',');
		return pecDrawBs.idxDraw(arr,dt);
	}
}
