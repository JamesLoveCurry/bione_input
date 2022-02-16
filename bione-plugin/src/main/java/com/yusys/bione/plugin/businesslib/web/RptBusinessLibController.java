package com.yusys.bione.plugin.businesslib.web;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo;
import com.yusys.bione.plugin.businesslib.service.RptBusinessLibBS;

/**
 * <pre>
 * Title: 业务分库管理 模块
 * Description:  可根据不同业务 分不同的数据库 存储 指标结果值
 * </pre>
 * @author lizy6 
 * @version 1.00.00
 */
@Controller
@RequestMapping("/rpt/frame/businesslib")
public class RptBusinessLibController extends BaseController{

	@Autowired
	private RptBusinessLibBS busiLibBS;
	
	/**
	 * 首页
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/businesslib/business-lib-index";
	}
	
	/**
	 * 跳转 新增、修改页面
	 * @param id
	 * @return
	 */
	@RequestMapping("/edit")
	public ModelAndView busiLibEidt(String id){
		ModelMap map=new ModelMap();
		if(StringUtils.isNotBlank(id)) {
			map.put("id",id);
		}
		return new ModelAndView("/plugin/businesslib/business-lib-edit",map);
	}
	
	/**
	 * 新增、修改页面
	 * 跳转 事实表弹出框
	 * @param dsId
	 * @return
	 */
	@RequestMapping(value = "/selectTable")
	public ModelAndView selectTable(String dsId) {
		ModelMap mm = new ModelMap();
		mm.put("dsId", StringUtils2.javaScriptEncode(dsId));
		return new ModelAndView("/plugin/businesslib/business-lib-table",mm);
	}
	
	/**
	 * 根据id获取entity
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getBusiLibById", method = RequestMethod.GET)
	@ResponseBody
	public RptMgrBusiLibInfo getBusiLibById(String id) {
		return this.busiLibBS.getEntityById(RptMgrBusiLibInfo.class, id);
	}
	
	/**
	 * 保存entity
	 * @param RptMgrBusiLibInfo
	 */
	@RequestMapping(value = "/saveBusiLib", method = RequestMethod.POST)
	public void saveBusiLib(RptMgrBusiLibInfo busiLib){
		if(StringUtils.isBlank(busiLib.getBusiLibId())) {
			busiLib.setBusiLibId(RandomUtils.uuid2());
		}
		this.busiLibBS.saveOrUpdateEntity(busiLib);
	}
	
	/**
	 * 批量删除 entity
	 * @param ids
	 */
	@RequestMapping(value = "/deleteBusiLib", method = RequestMethod.POST)
	@ResponseBody	
	public void deleteBusiLib(String ids){
		this.busiLibBS.batchRemoveEntitys(ids);
	}
	
	/**
	 * 根据业务参数类型标识 获取下拉框数据 
	 * @param typeNo 业务参数类型标识
	 * @return
	 */
	@RequestMapping(value = "/getComboInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getComboInfoByParamTypeNo(String typeNo) {
		return this.busiLibBS.getComboxInfoByTypeNo(typeNo);
	}
	
	/**
	 * grid 数据
	 * @param pager
	 * @return
	 */
	@RequestMapping("/gridList.*")
	@ResponseBody
	public Map<String, Object> gridList(Pager pager) {
		Map<String, Object> libMap = this.busiLibBS.getBusinessLibList(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		return libMap;
	}
	
	/**
	 * 获取数据源中物理表列表
	 * 
	 * @param dsId
	 *            数据源Id
	 * @return
	 */
	@RequestMapping(value = "/tables.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> tables(Pager pager, String dsId) {
		return this.busiLibBS.getRptSysModuleInfoList(dsId,pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
	}
	
	/**
	 * 唯一性验证
	 * @param id
	 * @param field
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/validateValue", method = RequestMethod.POST)
	@ResponseBody
	public boolean validateValue(String id, String busiLibNo, String busiLibNm){
		return this.busiLibBS.validateValue(id, busiLibNo, busiLibNm);
	}
	
	/**
	 * 列表中 数据源字段反显
	 * @return
	 */
	@RequestMapping(value = "/getDsData", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getDsData(){
		Map<String,Object> data = this.busiLibBS.getDsData();
		return data;
	}
	
}
