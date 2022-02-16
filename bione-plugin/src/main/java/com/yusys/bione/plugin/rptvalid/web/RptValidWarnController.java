package com.yusys.bione.plugin.rptvalid.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptvalid.service.RptValidWarnBS;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.web.vo.ValidCfgextWarnVO;

/**
 * 
 * <pre>
 * Title:
 * Description: 
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/report/frame/rptvalid/warn")
public class RptValidWarnController extends BaseController {

	@Autowired
	private RptValidWarnBS warnBs;
	
	//展示右侧grid内容--转向
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String indexCatalogNo, String indexNo, 
			String indexVerId, String indexNm,String defSrc) {
		ModelMap mm = new ModelMap();
		if(null==indexCatalogNo){
			indexCatalogNo="";
		}
		try {
			indexCatalogNo=URLDecoder.decode(indexCatalogNo,"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		
		if(StringUtils.isNotEmpty(indexCatalogNo)){
			mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		}
		if(StringUtils.isNotEmpty(indexNo)){
			mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		}
		if(StringUtils.isNotEmpty(indexVerId)){
			mm.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		}
		if(StringUtils.isNotEmpty(indexNm)){
			mm.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		}
		if (StringUtils.isNotEmpty(defSrc)) {
			mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		}
		
		return new ModelAndView("/plugin/rptvalid/rpt-valid-warn-index", mm);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newEdit(String indexCatalogNo,String indexNo,String indexNm, String editFlag,String checkId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		map.put("editFlag", StringUtils2.javaScriptEncode(editFlag));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		return new ModelAndView("/plugin/rptvalid/rpt-valid-warn-edit", map);
	}
	
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager, String indexCatalogNo, 
			String indexNo, String indexVerId, String defSrc){
		
		return this.warnBs.list(pager, indexCatalogNo,indexNo,defSrc);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ValidCfgextWarnVO getInfo(@PathVariable("id") String checkId, String indexNo){
		return this.warnBs.getInfo(checkId, indexNo);
	}
	
	@RequestMapping(value = "/{ids}", method = RequestMethod.POST)
	@ResponseBody
	public void delete(@PathVariable("ids") String checkIds){
		this.warnBs.delete(checkIds);
	}
	
	@RequestMapping(value = "/getLevelInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getLevelInfo(String checkId){
		return this.warnBs.getLevelInfo(checkId);
	}
	@RequestMapping(method = RequestMethod.POST)
	public void save(RptValidCfgextWarn warn,String levelInfo){
		this.warnBs.saveWarn(warn, levelInfo);
	}
	
	@RequestMapping(value = "/validStartDate")
	@ResponseBody
	public String validStartDate(String cfgId){
		return this.warnBs.validStartDate(cfgId);
	}
	
	/**
	 * 判断校验名称是否重复
	 * 
	 * @return
	 */
	@RequestMapping(value = "/testSameCheckNm", method = RequestMethod.POST)
	@ResponseBody
	public Boolean testSameCheckNm(String checkNm,String checkId) {
		return this.warnBs.testSameCheckNm(checkNm,checkId);
	}
	
	/**
	 * 计划值校验信息框架页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/infoFrame", method = RequestMethod.GET)
	public ModelAndView infoFrame(String indexNo,String indexNm, String checkId,String editFlag,String indexCatalogNo) {
		ModelMap mm = new ModelMap();
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		mm.put("editFlag", StringUtils2.javaScriptEncode(editFlag));
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("checkId", StringUtils2.javaScriptEncode(checkId));
		return new ModelAndView("/plugin/rptvalid/rpt-valid-warn-infoFrame", mm);
	}
	//修改跳转
	@RequestMapping(value = "/editDim", method = RequestMethod.GET)
	public ModelAndView editDim(String checkId,String indexNo,String editFlag) {		
		ModelMap mm = new ModelMap();
		mm.put("checkId", StringUtils2.javaScriptEncode(checkId));
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("editFlag", StringUtils2.javaScriptEncode(editFlag));
		return new ModelAndView("/plugin/rptvalid/rpt-valid-warn-dim", mm);
	}
	
	//获取维度树
	@RequestMapping(value = "/listDimTree.*")
	@ResponseBody
	public List<CommonTreeNode> listDimTree(String indexNo,String checkId) {
		return this.warnBs.listDimTree(indexNo,checkId);
	}
	//保存选择维度信息
	@RequestMapping(value = "/selectDim")
	@ResponseBody
	public void selectDim(String checkId, String indexNo,String ids,String dimTypes) {
		this.warnBs.saveDimInfo(checkId,indexNo,ids,dimTypes);
	}
}