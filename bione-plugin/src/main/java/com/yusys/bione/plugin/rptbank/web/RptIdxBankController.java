package com.yusys.bione.plugin.rptbank.web;

import java.util.HashMap;
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
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfo;
import com.yusys.bione.plugin.rptbank.entity.RptIdxThemeInfo;
import com.yusys.bione.plugin.rptbank.service.RptIdxBankBS;
import com.yusys.bione.plugin.rptbank.web.vo.RptIdxBankVO;
/**
* <pre>
* Title: 指标库配置功能Controller
* Description: 提供指标库配置功能的后台处理入口
* </pre>
* 
* @author donglt
* @version 1.00.00
* 
*          <pre>
* 修改记录
*    修改后版本:     修改人：  修改日期:     修改内容:
* </pre>
*/
@Controller
@RequestMapping("/frame/idx/bank")
public class RptIdxBankController extends BaseController {
	
	@Autowired
	private RptIdxBankBS idxBankBs;
	
	/**
	 * 跳转到指标库配置功能首页
	 * @return 指标库配置功能页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/rptbank/idx-bank-index");
	}
	/**
	 * 跳转到修改主题功能页面
	 * @param themeId 主题ID
	 * @return 主题修改功能页面
	 */
	@RequestMapping(value = "editTheme",method = RequestMethod.GET)
	public ModelAndView editTheme(String themeId) {
		themeId = StringUtils2.javaScriptEncode(themeId);
		return new ModelAndView("/plugin/rptbank/idx-theme-edit", "themeId", themeId);
	}
	/**
	 * 跳转到指标库信息修改页面
	 * @param themeId 主题ID
	 * @param indexId 指标ID
	 * @param upNo 上级编号
	 * @return 指标库信息修改页面
	 */
	@RequestMapping(value = "editBank",method = RequestMethod.GET)
	public ModelAndView editBank(String themeId,String indexId,String upNo,String currNm,String upIndexLevel) {
		ModelMap map = new ModelMap();
		map.put("themeId", StringUtils2.javaScriptEncode(themeId));
		map.put("indexId", StringUtils2.javaScriptEncode(indexId));
		map.put("upNo", StringUtils2.javaScriptEncode(upNo));
		map.put("currNm", StringUtils2.javaScriptEncode(currNm));
		map.put("upIndexLevel", StringUtils2.javaScriptEncode(upIndexLevel));
		return new ModelAndView("/plugin/rptbank/idx-bank-edit",map);
	}
	
	/**
	 * 跳转指标选择页面
	 * @return 指标选择页面
	 */
	@RequestMapping(value = "mainIdxWin",method = RequestMethod.GET)
	public ModelAndView mainIdxWin() {
		return new ModelAndView("/plugin/rptbank/idx-bank-idx");
	}
	
	/**
	 * 跳转到指标库列表页面
	 * @param themeId 主题ID
	 * @return 指标库列表页面
	 */
	@RequestMapping(value = "list",method = RequestMethod.GET)
	public ModelAndView bank(String themeId) {
		themeId = StringUtils2.javaScriptEncode(themeId);
		return new ModelAndView("/plugin/rptbank/idx-bank-list", "themeId", themeId);
	}
	/**
	 * 获取指标主题树
	 * @return 指标主题树
	 */
	@RequestMapping(value ="getThemeTree")
	@ResponseBody
	public List<CommonTreeNode> getThemeTree() {
		return this.idxBankBs.getThemeTree(this.getContextPath(), false);
	}
	
	/**
	 *  获取指标主题信息
	 * @param themeId 主题ID
	 * @return 指标主题信息
	 */
	@RequestMapping(value ="getThemeInfo")
	@ResponseBody
	public RptIdxThemeInfo getThemeInfo(String themeId) {
		return this.idxBankBs.getEntityById(RptIdxThemeInfo.class, themeId);
	}
	/**
	 * 获取指标信息
	 * @param themeId 主题ID
	 * @param indexId 指标ID
	 * @return 指标信息
	 */
	@RequestMapping(value ="getBankInfo")
	@ResponseBody
	public RptIdxBankVO getBankInfo(String themeId,String indexId) {
		return this.idxBankBs.getBankInfo(themeId,indexId);
	}
	/**
	 * 获取某个指标主题下的指标库信息
	 * @param pager 分页对象
	 * @param themeId 主题ID
	 * @return
	 */
	@RequestMapping(value ="getBankInfoByTheme")
	@ResponseBody
	public Map<String,Object> getBankInfoByTheme(Pager pager,String themeId) {
		return this.idxBankBs.getBankInfoByTheme(pager.getSortname(), pager.getSortorder(), pager.getSearchCondition(),themeId);
	}
	
	/**
	 * 验证主题编号唯一性
	 * @param themeId 主题ID
	 * @param themeNo 主题编号
	 * @return 是否唯一
	 */
	@RequestMapping(value ="validateThemeNo")
	@ResponseBody
	public Boolean validateThemeNo(String themeId,String themeNo) {
		return this.idxBankBs.validateThemeNo(themeId, themeNo);
	}
	
	/**
	 * 验证主题名称唯一性
	 * @param themeId 主题ID
	 * @param themeNm 主题名称
	 * @return 是否唯一
	 */
	@RequestMapping(value ="validateThemeNm")
	@ResponseBody
	public Boolean validateThemeNm(String themeId,String themeNm) {
		return this.idxBankBs.validateThemeNm(themeId, themeNm);
	}
	
	/**
	 * 验证指标名称唯一性
	 * @param themeId 主题ID
	 * @param indexId 指标ID
	 * @param indexNm 指标名称
	 * @return 是否唯一
	 */
	@RequestMapping(value ="validateIndexNm")
	@ResponseBody
	public Boolean validateIndexNm(String themeId,String indexId,String indexNm) {
		return this.idxBankBs.validateIndexNm(themeId, indexId,indexNm);
	}
	
	/**
	 * 保存主题信息
	 * @param info 指标主题信息
	 * @return 保存情况
	 */
	@RequestMapping(value ="saveTheme",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveTheme(RptIdxThemeInfo info) {
		Map<String,Object> res = new HashMap<String, Object>();
		if(!StringUtils.isNotBlank(info.getThemeId())){
			info.setThemeId(RandomUtils.uuid2());
		}
		info.setIsDefault("N");
		this.idxBankBs.saveOrUpdateEntity(info);
		res.put("msg", "ok");
		return res;
	}
	
	/**
	 * 保存指标信息
	 * @param info 指标信息
	 * @return 保存情况
	 */
	@RequestMapping(value ="saveIdx",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveIdx(RptIdxBankInfo info) {
		Map<String,Object> res = new HashMap<String, Object>();
		if(!StringUtils.isNotBlank(info.getId().getIndexId())){
			info.getId().setIndexId(RandomUtils.uuid2());
		}
		if(!StringUtils.isNotBlank(info.getPartNo())){
			info.setPartNo(info.getMainNo());
		}
		this.idxBankBs.saveOrUpdateEntity(info);
		res.put("msg", "ok");
		return res;
	}
	
	/**
	 * 删除主题信息
	 * @param themeId 主题Id
	 * @return 删除情况
	 */
	@RequestMapping(value ="deleteTheme",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> deleteTheme(String themeId) {
		return this.idxBankBs.deleteTheme(themeId);
	}
	
	/**
	 * 删除指标信息
	 * @param themeId 主题ID
	 * @param indexId 指标ID
	 * @return 删除情况
	 */
	@RequestMapping(value ="deleteIdx",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> deleteIdx(String themeId,String indexId) {
		return this.idxBankBs.deleteIdx(themeId,indexId);
	}
	/**
	 * 获取全部币种
	 */
	@RequestMapping(value = "/getCurrType.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getCurrType() {
		return this.idxBankBs.getCurrType();
	}
	
	/**
	 * 设置默认模版
	 * @param tempId
	 * @return
	 */
	@RequestMapping(value = "/defaultTemp")
	@ResponseBody
	public Map<String,Object> defaultTemp(String themeId) {
		Map<String,Object> returnMap = new HashMap<String, Object>();
		returnMap.put("rptAnaTmpInfo", (Object)idxBankBs.defaultTemp(themeId));
		return returnMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
