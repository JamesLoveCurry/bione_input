package com.yusys.bione.plugin.idxplan.web;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
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

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResult;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResultPK;
import com.yusys.bione.plugin.idxplan.service.IdxPlanvalInfoBS;
import com.yusys.bione.plugin.idxplan.web.vo.RptIdxPlanResultVO;

@Controller
@RequestMapping("/rpt/frame/idx/planval")
public class IdxPlanvalInfoController extends BaseController {
	@Autowired
	private IdxPlanvalInfoBS idxPlanvalInfoBS;

	//显示首页
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(){
		ModelMap mm = new ModelMap();
		mm.put("treeRootNo", GlobalConstants4frame.TREE_ROOT_NO);
		mm.put("rootIcon", GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		return new ModelAndView("/plugin/rptplanval/idx-planval-index", mm);
	}
	
	//展示树状列表
	@RequestMapping(value = "/getAsyncTreeIdxShow.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getAsyncTreeIdxShow(String searchNm, String isShowIdx, /*String isShowMeasure, */String idxNos,
			String isShowDim,String indexNo, /*String exSumAccoutIndex,*/String isEngine,String isAuth,
			String nodeType, String id, String indexVerId,String isPublish, String showEmptyFolder,String  isPreview,String defSrc){
		return this.idxPlanvalInfoBS.getAsyncTree(this.getContextPath(), searchNm, isShowIdx, /*isShowMeasure, */idxNos,
				 isShowDim, indexNo,  /*exSumAccoutIndex, */isEngine, isAuth,
				 nodeType, id, indexVerId, isPublish, showEmptyFolder, isPreview, defSrc);
	}

	//展示右侧grid内容--转向
	@RequestMapping(value = "/idxPlanvalInfos", method = RequestMethod.GET)
	public ModelAndView idxPlanvalInfos(String indexCatalogNo, String indexNo, 
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
		if (StringUtils.isNotEmpty(indexNm)) {
			mm.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		}
		
		return new ModelAndView("/plugin/rptplanval/idx-planval-list", mm);
	}
	
	/**
	 * 计划值校验信息框架页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/infoFrame", method = RequestMethod.GET)
	public ModelAndView infoFrame(String indexNo,String indexNm, String dataDate,String currency,
			String orgNo, String indexVal,String editFlag,String indexCatalogNo) {
		ModelMap mm = new ModelMap();
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		mm.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		mm.put("currency", StringUtils2.javaScriptEncode(currency));
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("indexVal", StringUtils2.javaScriptEncode(indexVal));
		mm.put("editFlag", StringUtils2.javaScriptEncode(editFlag));
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		return new ModelAndView("/plugin/rptvalid/rpt-valid-planval-infoFrame", mm);
	}
	
	//展示指标类型
	@RequestMapping(value="/indexTypeList.*", method=RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> indexTypeList(String situation, String defSrc){
		return this.idxPlanvalInfoBS.getIndexTypeList(situation, defSrc);
	}
	
	//根据条件查找指标
	@RequestMapping(value = "/getPlanvalInfoList.*")
	@ResponseBody
	public Map<String, Object> getPlanvalInfoLists(Pager pager, String indexCatalogNo, 
			String indexNo, String indexVerId, String defSrc) {
		
		SearchResult<RptIdxPlanResultVO> searchResult = this.idxPlanvalInfoBS.getPlanvalInfos(
				pager.getPageFirstIndex(), pager.getPagesize(),	pager.getSortname(),
				pager.getSortorder(), pager.getSearchCondition(), indexCatalogNo, 
				indexNo, defSrc);
		
		Map<String, Object> rowData = new HashMap<String, Object>();
		rowData.put("Rows", searchResult.getResult());
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
	
	//添加跳转
	@RequestMapping(value = "/idxInfoPlanvalAdd", method = RequestMethod.GET)
	public ModelAndView idxInfoFrame(String indexNo,String indexNm,String indexCatalogNo, String orgNo,String editFlag) {
		ModelMap mm = new ModelMap();

		try {
			indexCatalogNo = URLDecoder.decode(indexCatalogNo, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mm.addAttribute("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.addAttribute("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.addAttribute("indexNm", StringUtils2.javaScriptEncode(indexNm));
		mm.addAttribute("editFlag", StringUtils2.javaScriptEncode(editFlag));
		mm.addAttribute("orgNo", StringUtils2.javaScriptEncode(orgNo));
		
		return new ModelAndView("/plugin/rptplanval/idx-planval-editNew", mm);
	}
	
	//添加
	@RequestMapping(value="/add", method = RequestMethod.POST)
	public void create(RptIdxPlanResultVO model) {
		RptIdxPlanvalResultPK id = new RptIdxPlanvalResultPK();
		RptIdxPlanvalResult planvalInfo = new RptIdxPlanvalResult();
		String[] orgNos = StringUtils.split(model.getOrgNo(), ',');
		String orgNo;
		
		if(model.getCurrency()== null || model.getCurrency()== ""){
			model.setCurrency("-");
		}
		
		for(int i=0; i<orgNos.length; i++){
			orgNo = orgNos[i];
			id.setIndexNo(model.getIndexNo());	
			id.setDataDate(model.getDataDate());
			id.setCurrency(model.getCurrency());
			id.setPlanType("Y");
			id.setDim1("-");
			id.setDim2("-");
			id.setDim3("-");
			id.setDim4("-");
			id.setDim5("-");
			id.setDim6("-");
			id.setDim7("-");
			id.setDim8("-");
			id.setDim9("-");
			id.setDim10("-");
//			id.setIndexVerId(new Long(model.getIndexVerId()));
			id.setOrgNo(orgNo);
			planvalInfo.setId(id);
			planvalInfo.setIndexVal(model.getIndexVal());	
			
			this.idxPlanvalInfoBS.updateEntity(planvalInfo);
		}
	}
	
	//修改跳转
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(String indexNo, String dataDate,String currency,String orgNo, String indexVal,String editFlag) {		
		ModelMap mm = new ModelMap();
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		mm.put("currency", StringUtils2.javaScriptEncode(currency));
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("indexVal", StringUtils2.javaScriptEncode(indexVal));
		mm.put("editFlag", StringUtils2.javaScriptEncode(editFlag));
		return new ModelAndView("/plugin/rptplanval/idx-planval-edit", mm);
	}
	
	//修改跳转
	@RequestMapping(value = "/editDim", method = RequestMethod.GET)
	public ModelAndView editDim(String indexNo, String dataDate,String currency,String orgNo,String editFlag) {		
		ModelMap mm = new ModelMap();
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("dataDate", StringUtils2.javaScriptEncode(dataDate));
		mm.put("currency", StringUtils2.javaScriptEncode(currency));
		mm.put("orgNo", StringUtils2.javaScriptEncode(orgNo));
		mm.put("editFlag", StringUtils2.javaScriptEncode(editFlag));
		return new ModelAndView("/plugin/rptvalid/rpt-valid-planval-dim", mm);
	}
		
	//修改表单数据的加载
	@RequestMapping(value = "/getPlanvalInfoByParams", method = RequestMethod.GET)
	@ResponseBody
	public RptIdxPlanResultVO show(String indexNo, String dataDate, String orgNo,String currency,String indexVal) {
		RptIdxPlanResultVO planvalInfo = this.idxPlanvalInfoBS.getIdxPlanvalInfoByParams(indexNo, dataDate, orgNo,currency,indexVal);
		String dateYear = planvalInfo.getDataDate();
		planvalInfo.setDataDate(dateYear);
		return planvalInfo;
	}
	
	//修改
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public void modify(String orgNo,String currencyId,RptIdxPlanResultVO model){
		String indexNo=model.getIndexNo();
		String dataDate=model.getDataDate();
		BigDecimal indexVal=model.getIndexVal();
		this.idxPlanvalInfoBS.changePlanval(indexNo,dataDate, orgNo, currencyId,indexVal);
		
	}
	
	//删除
	@RequestMapping(value = "/idxPlanvalInfoDel", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> idxPlanvalInfoDel(String indexNos, String dataDates,String orgNos,String currencys,String indexVals) {
		Map<String,String> msgMap=Maps.newHashMap();
		if (indexNos != null && !indexNos.equals("")) {
			String[] indexNoArray = StringUtils.split(indexNos, ',');
			String[] dataDateArray = StringUtils.split(dataDates, ',');
			String[] orgNoArray = StringUtils.split(orgNos, ',');
			String[] currencysArray = StringUtils.split(currencys, ',');
			String[] indexValsArray = StringUtils.split(indexVals, ',');

			this.idxPlanvalInfoBS.deletePlanvalssByIds(indexNoArray, dataDateArray, orgNoArray,currencysArray,indexValsArray);
			msgMap.put("msg","0");
			return msgMap;
		}
		return msgMap; 
	}
	
	//机构树页面--跳转
	@RequestMapping(value = "/orgTree")
	public ModelAndView catalogTreeSelect(String editFlag,String indexNo) {
		ModelMap mm = new ModelMap();
		if (StringUtils.isNotEmpty(editFlag)) {
			mm.put("editFlag", StringUtils2.javaScriptEncode(editFlag));
		}
		if (StringUtils.isNotEmpty(indexNo)) {
			if(indexNo.contains(".")){
				indexNo = indexNo + ".";
				String compNo[] = StringUtils.split(indexNo, ".");
				indexNo = compNo[0];
			}
			mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		}
		return new ModelAndView("/plugin/rptplanval/idx-planval-org-selecttree",mm);
	}
	
	//获取机构树
	@RequestMapping(value = "/listOrgTree.*")
	@ResponseBody
	public List<CommonTreeNode> listOrgTree(String indexNo) {
		return this.idxPlanvalInfoBS.listOrgTree(indexNo);
	}
	
	//检测是否唯一
	@RequestMapping(value = "/testOrgNo")
	@ResponseBody
	public boolean testOrgNo(String indexNo, String indexVerId, String orgNo){
		return this.idxPlanvalInfoBS.chechIsOrgNo(indexNo, indexVerId, orgNo);
	}
	
	//检测对应指标是否有币种维度
	@RequestMapping(value = "/testDimData")
	@ResponseBody
	public boolean testDimData(String indexNo){
		return this.idxPlanvalInfoBS.checkIsDimData(indexNo);
	}
	//获取维度树
	@RequestMapping(value = "/listDimTree.*")
	@ResponseBody
	public List<CommonTreeNode> listDimTree(String indexNo) {
		return this.idxPlanvalInfoBS.listDimTree(indexNo);
	}
	//保存选择维度信息
	@RequestMapping(value = "/selectDim")
	@ResponseBody
	public void selectDim(String indexNo,String ids,String dimTypes) {
		this.idxPlanvalInfoBS.saveDimInfo(indexNo,ids,dimTypes);
	}
}
