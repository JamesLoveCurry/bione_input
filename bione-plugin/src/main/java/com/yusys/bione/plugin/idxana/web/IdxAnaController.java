package com.yusys.bione.plugin.idxana.web;

import java.util.ArrayList;
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

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.idxana.service.RptAnaTextCfgBS;
import com.yusys.bione.plugin.idxana.service.RptIdxAnaShowBS;
import com.yusys.bione.plugin.idxana.service.RptIdxDimRelBS;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;

/**
 * <pre>
 * Title:报表平台指标分析
 * Description: 
 * </pre>
 * 
 * @author yangyf
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

@Controller
@RequestMapping("/idx/analysis/show")
public class IdxAnaController extends BaseController {

	@Autowired
	private RptIdxAnaShowBS idxAnaShowBS;
	
	@Autowired
	private RptAnaTextCfgBS anaTextCfgBS;
	
	@Autowired
	private RptIdxDimRelBS rptIdxDimRelBS;
	
	@Autowired
	private RptOrgInfoBS rptOrgInfoBS;
	
	@Autowired
	private IdxInfoBS idxInfoBS;
	
	/**
	 * 指标分析首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView("/plugin/idxresultanly/idx-analysis-index");
	}
	
	/**
	 * 获取指标树
	 * @param searchNm
	 * @param isShowIdx
	 * @param isShowMeasure
	 * @param idxNos
	 * @param isShowDim
	 * @param indexNo
	 * @param indexNm
	 * @param exSumAccoutIndex
	 * @param isEngine
	 * @param isAuth
	 * @param nodeType
	 * @param id
	 * @param indexVerId
	 * @param isPublish
	 * @param showEmptyFolder
	 * @param isPreview
	 * @param defSrc
	 * @param isExpire
	 * @param isCabin
	 * @return
	 */
	@RequestMapping(value = "/getAsyncTreeIdxShow.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getAsyncTreeIdxShow(String searchNm,
			String isShowIdx, String isShowMeasure, String idxNos,
			String isShowDim, String indexNo,String indexNm, String exSumAccoutIndex,
			String isEngine, String isAuth, String nodeType, String id,
			String indexVerId, String isPublish, String showEmptyFolder,
			String isPreview, String defSrc, String isCabin) {
		return this.idxInfoBS.getAsyncTree(this.getContextPath(), searchNm,
				isShowIdx, isShowMeasure, idxNos, isShowDim, indexNo, indexNm,
				exSumAccoutIndex, isEngine, isAuth, nodeType, id, indexVerId,
				isPublish, showEmptyFolder, isPreview, defSrc, null, isCabin, null);
	}
	
	/**
	 * 指标维度获取
	 * @param idxno 指标编号
	 * @return
	 */
	@RequestMapping(value = "/getIdxDim")
	@ResponseBody
	public List<RptIdxDimRel> getIdxDim(String idxNo){
		String[] idxNolist = StringUtils.split(idxNo, ".");
		List<RptIdxDimRel> rptIdxDimRellist =  rptIdxDimRelBS.findEntityListByProperty("id.indexNo", idxNolist[0]);
		return rptIdxDimRellist;
	}
	
	/**
	 * 币种获取
	 * @return
	 */
	@RequestMapping(value = "/initcurrency")
	@ResponseBody
	public List<RptDimItemInfo> initcurrency(){
		return this.idxAnaShowBS.getDim(GlobalConstants4plugin.DIM_TYPE_CURRENCY_NAME);
	}

	/**
	 * 获取指标翻牌日期
	 * @return
	 */
	@RequestMapping(value = "/initDrawDate")
	@ResponseBody
	public Map<String,String> initDrawDate(String idxNo){
		Map<String,String> ment = new HashMap<String, String>();
		ment.put("drawDate", idxAnaShowBS.getDrawDate(idxNo));
		return ment;
	}
	
	/**
	 * 跳转机构树弹出页
	 * 
	 * */
	@RequestMapping(value = "/initOrgTree")
	public ModelAndView initOrgTree(String idxBelongType) {
		idxBelongType = StringUtils2.javaScriptEncode(idxBelongType);
		return new ModelAndView("/plugin/idxresultanly/rpt-analysis-orgTree", "idxBelongType", idxBelongType);
	}
	
	/**
	 * 跳转对比组弹出页
	 * 
	 * */
	@RequestMapping(value = "/initGrpTree")
	public ModelAndView initGrpTree(String idxNo) {
		idxNo = StringUtils2.javaScriptEncode(idxNo);
		return new ModelAndView("/plugin/idxresultanly/rpt-analysis-grpTree", "idxNo", idxNo);
	}
	
	/**
	 * 构建指标对比组的树
	 */
	@RequestMapping(value = "/initGrpTreeData")
	@ResponseBody
	public List<CommonTreeNode> initGrpTreeData(String idxNo){
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		treeNodes = idxAnaShowBS.getRptGrp(idxNo, "");
		return treeNodes;
	}
	
	/**
	 * 获取机构信息
	 */
	@RequestMapping(value = "/initOrgTreeData")
	@ResponseBody
	public List<CommonTreeNode> getOrgInfoGroup(String idxBelongType){
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		resultList.addAll(rptOrgInfoBS.getInnerOrgTree("/rpt-web", idxBelongType));
		return resultList;
	}
	
	/**
	 * 显示具体指标分析页面
	 * @return
	 */
	@RequestMapping(value = "/indexTime")
	@ResponseBody
	public Map<String,Object> indexTime(String idxNo, String date, String org, String currency) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap = this.idxAnaShowBS.initTitle(idxNo, date, org, currency, null);
		return resultMap;
	}
	
	/**
	 * 提前查询趋势分析图
	 * @return
	 */
	@RequestMapping(value = "/aheadTre")
	public void aheadTre(String idxNo, String chartId, String date, String org, String currency) {
		//this.idxAnaShowBS.aheadTre(idxno, chartId, date, org, currency);
	}
	
	/**
	 * 查询所有图表
	 * @return
	 */
	@RequestMapping(value = "/initchartList")
	@ResponseBody
	public Map<String,Object> initchartList(String idxNo) {
		return this.idxAnaShowBS.initchartList(idxNo);
	}
	
	/**
	 * 构建指标分析的图表
	 */
	@RequestMapping(value = "/getCharts")
	@ResponseBody
	public Map<String,Object> getCharts(String idxNo, String chartId, String chartType, String showType, String idxNm, String dataUnit, String date, String org, String currency, String unit, String userOrgNo, String orgType){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("echart", this.idxAnaShowBS.initEchart(idxNo, chartId, chartType, showType, idxNm, dataUnit, date, org, currency, unit, orgType));
		return resultMap;
	}
	
	/**
	 * 获取指标和指标同类组所对应的维度
	 */
	@RequestMapping(value = "/getDim")
	@ResponseBody
	public List<RptDimTypeInfo> queryIdxDim(String idxNo){
		List<RptDimTypeInfo> RptDimTypeInfolist = new ArrayList<RptDimTypeInfo>();
		RptDimTypeInfolist = idxAnaShowBS.queryIdxDim(idxNo);
		return RptDimTypeInfolist;
	}
	
	/**
	 * 构建结构解析杜邦图
	 */
	@RequestMapping(value = "/initReldupont")
	@ResponseBody
	public Map<String,Object> initReldupont(String idxNo, String idxNm, String date, String org, String currency, String chartId, String dataUnit, String returnDimkey, String unit){
		Map<String,Object> DupontInfoGroup = new HashMap<String,Object>();
		if(returnDimkey != null && returnDimkey.length() > 0){
			String[] idxDimlist = StringUtils.split(returnDimkey, ";");
			if(idxDimlist.length == 2){
				DupontInfoGroup.put("dupontnodes", idxAnaShowBS.initReldupont(idxDimlist[1], idxNm, date, org, currency, chartId, dataUnit, idxDimlist[0], unit));
			}
		}
		return DupontInfoGroup;
	}
	
	/**
	 * 构建关系解析杜邦图
	 */
	@RequestMapping(value = "/initStrdupont")
	@ResponseBody
	public Map<String,Object> initStrdupont(String idxNo, String chartId, String idxNm, String date, String org, String currency, String unit, String orgType){
		Map<String,Object> DupontInfoGroup = new HashMap<String,Object>();
		DupontInfoGroup.put("dupontnodes", idxAnaShowBS.initStrdupont(idxNo, chartId, idxNm, date, org, currency, unit, orgType));
		return DupontInfoGroup;
	}
	
	/**
	 * 构建指标分析的文本
	 */
	@RequestMapping(value = "/getText")
	@ResponseBody
	public Map<String,Object> getText(String idxNo, String chartId, String chartType, String idxNm, String dataUnit, String date, String org, String currency, String unit, String orgType){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("chartText", this.anaTextCfgBS.initChartText(idxNo, chartId, chartType, idxNm, dataUnit, date, org, currency, unit, orgType));
		return resultMap;
	}
	
}
