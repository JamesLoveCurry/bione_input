package com.yusys.bione.plugin.businessline.web;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.mtool.service.DatasetBS;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiCfg;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine;
import com.yusys.bione.plugin.businessline.service.RptBusinessLineBS;
import com.yusys.bione.plugin.businessline.web.vo.RptMgrBusiCfgVO;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.service.RptDatasetBS;


/**
 * 
 * <pre>
 * Title:数据模型转换规则
 * Description: 提供对数据模型表的转换规则的维护
 * </pre>
 * 
 * @author hubing1@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

@Controller
@RequestMapping("/rpt/frame/businessline")
public class RptBusinessLineController {
	@Autowired
	private RptBusinessLineBS rptBusinessLineBS;
	
	@Autowired
	private DatasetBS dsetBS;
	
	@Autowired
	private RptDatasetBS dataSetBS;
	/**
	 * 首页
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/businessline/business-line-index";
	}
	
	/**
	 * 配置
	 * @return
	 */
	@RequestMapping(value = "config",method = RequestMethod.GET)
	public ModelAndView config(String lineId) {
		lineId = StringUtils2.javaScriptEncode(lineId);
		return new ModelAndView("/plugin/businessline/business-line-config", "lineId", lineId);
	}
	
	/**
	 * 物理表选择页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tablePage")
	public ModelAndView tablePage(String dsId,String type) {
		ModelMap mm = new ModelMap();
		mm.put("dsId", StringUtils2.javaScriptEncode(dsId));
		mm.put("type", StringUtils2.javaScriptEncode(type));
		return new ModelAndView("/plugin/businessline/business-line-table",mm);
	}
	
	/**
	 * grid 数据
	 * @param pager
	 * @return
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<RptMgrBusiLine> searchResult = rptBusinessLineBS.getBusinessLine(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> lineMap = new HashMap<String, Object>();
		lineMap.put("Rows", searchResult.getResult());
		lineMap.put("Total", searchResult.getTotalCount());
		return lineMap;
	}
	
	@RequestMapping("/addline")
	public ModelAndView addLine(){
		return new ModelAndView("/plugin/businessline/business-line-add");
	}
	
	@RequestMapping("/editline")
	public ModelAndView editTrans(String lineId){
		lineId = StringUtils2.javaScriptEncode(lineId);
		return new ModelAndView("/plugin/businessline/business-line-add", "lineId", lineId);
	}
	
	/**
	 * 获取业务条线信息
	 * @param lineId 条线ID
	 * @param lineNm 条线名称
	 * @return 业务条线信息
	 */
	@RequestMapping(value ="getLineModify")
	@ResponseBody
	public RptMgrBusiLine getLineModify(String lineId,String lineNm) {
		return this.rptBusinessLineBS.getLineModify(lineId);
	}
	
	@RequestMapping(value ="getLineCfg")
	@ResponseBody
	public RptMgrBusiCfgVO getLineCfg(String lineId) {
		return this.rptBusinessLineBS.getLineCfg(lineId);
	}
	
	
	
	@RequestMapping("/delline")
	public Map<String,Object> delline(String ids){
		Map<String,Object> returnMap = Maps.newHashMap();
		rptBusinessLineBS.delBusinessLine(StringUtils.split(ids, ","));
		returnMap.put("msg", "success");
		return returnMap;
	}
	
	/**
	 * 保存业务条线信息
	 * @return 保存情况
	 */
	@RequestMapping(value ="saveBusinessLine",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveBusinessLine(RptMgrBusiLine info) {
		Map<String,Object> res = new HashMap<String, Object>();
		this.rptBusinessLineBS.saveOrUpdateEntity(info);
		res.put("msg", "ok");
		return res;
	}
	
	/**
	 * 保存业务条线信息
	 * @return 保存情况
	 */
	@RequestMapping(value ="saveBusinessCfg",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> saveBusinessCfg(RptMgrBusiCfg info,String idxSourceId,String rptSourceId) {
		Map<String,Object> res = new HashMap<String, Object>();
		String rptSetTable = info.getRptSetId();
		String idxSetTable = info.getIdxSetId();
		RptSysModuleInfo idxInfo = this.dataSetBS.getInfoByInfo(idxSourceId, idxSetTable, "idx");
		RptSysModuleInfo rptInfo = this.dataSetBS.getInfoByInfo(rptSourceId, rptSetTable, "rpt");
		if(idxInfo == null){
			idxInfo = new RptSysModuleInfo();
			idxInfo.setSetId(RandomUtils.uuid2());
			idxInfo.setCatalogId("1");
			idxInfo.setSetNm(idxSetTable);
			idxInfo.setSetType("idx");
			idxInfo.setSourceId(idxSourceId);
			idxInfo.setTableEnNm(idxSetTable);
			idxInfo.setRemark("");
			RptSysModuleCol col = new RptSysModuleCol();
			col.setColId(RandomUtils.uuid2());
			col.setSetId(idxInfo.getSetId());
			col.setColType("02");
			col.setCnNm("数据日期");
			col.setEnNm("DATA_DATE");
			col.setDbType("01");
			col.setLen(new BigDecimal(0));
			col.setOrderNum(new BigDecimal(1));
			col.setIsNull("N");
			col.setIsUse("Y");
			col.setIsPk("Y");
			col.setDimTypeNo("DATE");
			this.dataSetBS.saveModuleCol(col);
			this.dataSetBS.saveModuleInfo(idxInfo);
		}
		if(rptInfo == null){
			rptInfo = new RptSysModuleInfo();
			rptInfo.setSetId(RandomUtils.uuid2());
			rptInfo.setCatalogId("1");
			rptInfo.setSetNm(rptSetTable);
			rptInfo.setSetType("rpt");
			rptInfo.setSourceId(rptSourceId);
			rptInfo.setTableEnNm(rptSetTable);
			rptInfo.setRemark("");
			RptSysModuleCol col = new RptSysModuleCol();
			col.setColId(RandomUtils.uuid2());
			col.setSetId(rptInfo.getSetId());
			col.setColType("02");
			col.setCnNm("数据日期");
			col.setEnNm("DATA_DATE");
			col.setDbType("01");
			col.setLen(new BigDecimal(0));
			col.setOrderNum(new BigDecimal(1));
			col.setIsNull("N");
			col.setIsUse("Y");
			col.setIsPk("Y");
			col.setDimTypeNo("DATE");
			this.dataSetBS.saveModuleCol(col);
			this.dataSetBS.saveModuleInfo(rptInfo);
		}
		info.setIdxSetId(idxInfo.getSetId());
		info.setRptSetId(rptInfo.getSetId());
		this.rptBusinessLineBS.saveOrUpdateEntity(info);
		res.put("msg", "ok");
		return res;
	}
	
	
	@RequestMapping(value ="lineCheck",method = RequestMethod.GET)
	@ResponseBody
	public Map<String,String> lineCheck(String lineId) {
		return this.rptBusinessLineBS.lineCheck(lineId);
	}
	
	/**
	 * 获取数据源列表
	 * 
	 *            数据集Id
	 * @return
	 */
	@RequestMapping(value = "/dsList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dsList() {
		return this.dsetBS.getDataSources();
	}
	
	/**
	 *  判断同一数据源下面是否有使用了已知表名的数据集
	 * @return result
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/checkTableValid.*", method = RequestMethod.POST)
	@ResponseBody
	public String checkTableValid(String dsId,String tableName,String type) {
		Map<String,Object> map = this.dataSetBS.getFieldsOfTable(dsId, tableName);
		List<RptSysModuleCol> cols = (List<RptSysModuleCol>) map.get("Rows");
		List<String> filedNms = new ArrayList<String>();
		if(cols != null && cols.size()>0){
			for(RptSysModuleCol col : cols){
				filedNms.add(col.getEnNm());
			}
		}
		String error = "";
		if(!filedNms.contains("DATA_DATE")){
			error += "DATA_DATE,";
		}
		if(!filedNms.contains("INDEX_NO")){
			error += "INDEX_NO,";
		}
		if(!filedNms.contains("ORG_NO")){
			error += "ORG_NO,";
		}
		if(!filedNms.contains("CURRENCY")){
			error += "CURRENCY,";
		}
		int dimL = 10;
		for(int i = 1;i<= dimL;i++){
			if(!filedNms.contains("DIM"+i)){
				error += "DIM"+i+",";
			}
		}
		if(type.equals("rpt")){
			if(!filedNms.contains("TEMPLATE_ID")){
				error += "TEMPLATE_ID,";
			}
		}
		return error;
	}
}

