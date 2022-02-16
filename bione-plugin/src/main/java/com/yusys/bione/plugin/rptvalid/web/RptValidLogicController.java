package com.yusys.bione.plugin.rptvalid.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaFunc;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaSymbol;
import com.yusys.bione.plugin.rptmgr.service.RptMgrBS;
import com.yusys.bione.plugin.rptvalid.service.RptValidLogicBS;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.web.vo.CfgextLogicVO;

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
@RequestMapping("/report/frame/rptvalid/logic")
public class RptValidLogicController extends BaseController {

	@Autowired
	private RptValidLogicBS logicBs;

	@Autowired
	public RptMgrBS rptMgrBS;

	/**
	 * 首页
	 * 
	 * @return
	 */
	
    //显示首页
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(){
		ModelMap mm = new ModelMap();
		mm.put("treeRootNo", GlobalConstants4frame.TREE_ROOT_NO);
		mm.put("rootIcon", GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		return new ModelAndView("/plugin/rptvalid/rpt-valid-cfg-index", mm);
	}
	
	/**
	 * 跳转到某个报表下的逻辑校验grid
	 * 
	 * @param rptId
	 * @return
	 */
	//展示右侧grid内容--转向
	@RequestMapping(value = "/tab", method = RequestMethod.GET)
	public ModelAndView logicTab(String indexCatalogNo, String indexNo, 
			String indexVerId, String defSrc) {
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
		if (StringUtils.isNotEmpty(defSrc)) { 
			mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		}
		
		return new ModelAndView("/plugin/rptvalid/rpt-valid-logic-index", mm);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newEdit(String indexCatalogNo,String indexNo,String indexNm, String defSrc,String checkId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		map.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));

		return new ModelAndView("/plugin/rptvalid/rpt-valid-logic-infoFrame", map);
	}

	@RequestMapping(value = "/newLogic", method = RequestMethod.GET)
	public ModelAndView newLogic(String indexCatalogNo,String indexNo,String indexNm,String defSrc,String checkId) {
		Map<String, String> map = new HashMap<String, String>();
		//map = this.logicBs.getRptAndLine(templateId, rptId);
		map.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		map.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		map.put("treeRootNo", GlobalConstants4frame.TREE_ROOT_NO);
		map.put("rootIcon", GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		
		return new ModelAndView("/plugin/rptvalid/rpt-valid-cfgext-logic-edit-new",map);
	}

	@RequestMapping(value = "/basicInfo", method = RequestMethod.GET)
	public ModelAndView basicInfo(String checkId) {
		checkId = StringUtils2.javaScriptEncode(checkId);
		return new ModelAndView("/plugin/rptvalid/rpt-valid-cfgext-basic-info",
				"checkId", checkId);
	}
	
	//修改跳转
	@RequestMapping(value = "/editDim", method = RequestMethod.GET)
	public ModelAndView editDim(String checkId,String indexNos) {		
		ModelMap mm = new ModelMap();
		mm.put("checkId", StringUtils2.javaScriptEncode(checkId));
		mm.put("indexNos", StringUtils2.javaScriptEncode(indexNos));
		return new ModelAndView("/plugin/rptvalid/rpt-valid-logic-dim", mm);
	}
	
	//获取维度树
	@RequestMapping(value = "/listDimTree.*")
	@ResponseBody
	public List<CommonTreeNode> listDimTree(String indexNos,String checkId) {
		
		List<RptDimTypeInfo> types = new ArrayList<RptDimTypeInfo>();
		if (!StringUtils.isEmpty(indexNos)) {
			List<String> nos = new ArrayList<String>();
			String[] noStrs = StringUtils.split(indexNos, ',');
			for (String noStrTmp : noStrs) {
				if(noStrTmp.contains(".")){
					noStrTmp = noStrTmp + ".";
					String compNo[] = StringUtils.split(noStrTmp, ".");
					noStrTmp = compNo[0];
				}
				if (!nos.contains(noStrTmp)) {
					nos.add(noStrTmp);
				}
			}
			types = this.logicBs.getDimTypesByNos(nos);
		}
		
		return this.logicBs.listDimTree(types,indexNos,checkId);
	}
	//保存选择维度信息
	@RequestMapping(value = "/selectDim")
	@ResponseBody
	public void selectDim(String checkId,String ids,String dimTypes) {
		this.logicBs.saveDimInfo(checkId,ids,dimTypes);
	}
	
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager, String indexCatalogNo, 
			String indexNo, String indexVerId, String defSrc) {
		return this.logicBs.list(pager, indexCatalogNo,indexNo,defSrc);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public CfgextLogicVO getInfo(@PathVariable("id") String checkId) {
		return this.logicBs.getInfo(checkId);
	}

	@RequestMapping(value = "/{ids}", method = RequestMethod.POST)
	@ResponseBody
	public void delete(@PathVariable("ids") String checkIds) {
		this.logicBs.delete(checkIds);
	}

	@RequestMapping(value = "/getFuncAll.*", method = RequestMethod.POST)
	@ResponseBody
	public List<RptIdxFormulaFunc> getFuncAll() {
		return this.logicBs.getFuncAll();
	}

	@RequestMapping(value = "/getFuncTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getFuncTree() {
		return this.logicBs.getFuncTree();
	}

	@RequestMapping(value = "/getSymbolAll.*", method = RequestMethod.POST)
	@ResponseBody
	public List<RptIdxFormulaSymbol> getSymbolAll() {
		return this.logicBs.getSymbolAll();
	}

	@RequestMapping(value = "/getSymbolTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getSymbolTree() {
		return this.logicBs.getSymbolTree();
	}

	@RequestMapping(method = RequestMethod.POST)
	public void save(RptValidCfgextLogic logic,String leftFormulaIndex, String rightFormulaIndex) {
		this.logicBs.saveLogic(logic, leftFormulaIndex,
				rightFormulaIndex);
	}

	@RequestMapping(value = "/replaceExpression", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> replaceExpression(String leftExpression,
			String rightExpression, String expression) {
		
		Map<String, Object> map = this.logicBs.replaceExpression(
				leftExpression, rightExpression, expression);
		
		if (map.get("message") == null && map.get("formulaIndx") != null
				&& !StringUtils.isEmpty(map.get("formulaIndex").toString())) {
			Map<String, Object> result = this.logicBs.checkCycleRef(map.get(
					"formulaIndex").toString());
			if (result == null) {
				return map;
			}
			return result;
		}
		return map;
	}

	@RequestMapping(value = "/checkCycleRef")
	@ResponseBody
	public Map<String, Object> checkCycleRef(String formulaIndex) {
		return this.logicBs.checkCycleRef(formulaIndex);
	}

	@RequestMapping(value = "/validExpression", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> validExpression(String expression,
			String expressionDesc) {
		return this.logicBs.validExpression(expression, expressionDesc);
	}
	
	/**
	 * 判断校验公式名称是否重复
	 * 
	 * @return
	 */
	@RequestMapping(value = "/testSameCheckNm", method = RequestMethod.POST)
	@ResponseBody
	public Boolean testSameCheckNm(String checkNm,String checkId) {
		return this.logicBs.testSameCheckNm(checkNm,checkId);
	}
}