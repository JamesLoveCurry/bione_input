package com.yusys.bione.plugin.modeltrans.web;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.modeltrans.entity.RptSysModuleColTran;
import com.yusys.bione.plugin.modeltrans.entity.RptSysModuleColTranPK;
import com.yusys.bione.plugin.modeltrans.service.RptModelColTranBS;
import com.yusys.bione.plugin.modeltrans.vo.RptModelColTranVO;

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
@RequestMapping("/rpt/frame/modelcoltrans")
public class RptModelColTranController {
	@Autowired
	private RptModelColTranBS rptModelColTranBS;
	
	@Autowired
	private ExcelBS excelBS;
	
	/**
	 * 首页
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/modeltrans/model-col-trans-index";
	}
	/**
	 * grid 数据
	 * @param pager
	 * @return
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		SearchResult<RptModelColTranVO> searchResult = rptModelColTranBS.getModelColTran(pager.getPageFirstIndex(), pager.getPagesize(),
				pager.getSortname(), pager.getSortorder(), pager.getSearchCondition());
		Map<String, Object> transMap = new HashMap<String, Object>();
		transMap.put("Rows", searchResult.getResult());
		transMap.put("Total", searchResult.getTotalCount());
		return transMap;
	}
	
	@RequestMapping("/addtrans")
	public ModelAndView addTrans(){
		return new ModelAndView("/plugin/modeltrans/model-col-trans-add");
	}
	
	@RequestMapping("/edittrans")
	public ModelAndView editTrans(String setId,String srcSetId){
		Map<String,String> paramMap = Maps.newHashMap();
		paramMap.put("setId", StringUtils2.javaScriptEncode(setId));
		paramMap.put("srcSetId", StringUtils2.javaScriptEncode(srcSetId));
		return new ModelAndView("/plugin/modeltrans/model-col-trans-edit",paramMap);
	}
	
	@RequestMapping("/deltrans")
	public Map<String,Object> delTrans(String ids){
		Map<String,Object> returnMap = Maps.newHashMap();
		rptModelColTranBS.delAndModifyTransByIds(StringUtils.split(ids, ","));
		returnMap.put("msg", "success");
		return returnMap;
	}
	
	@RequestMapping("/querydstmodel")
	public Map<String,Object> queryDstModel(String dstTabNm){
		Map<String,Object> paramMap = Maps.newHashMap();
		paramMap.put("dst", rptModelColTranBS.getModelByNm(dstTabNm));
		return paramMap;
	}
	
	@RequestMapping("/querysrcmodel")
	public Map<String,Object> querySrcModel(String srcTabNm){
		Map<String,Object> paramMap = Maps.newHashMap();
		paramMap.put("src", rptModelColTranBS.getModelByNm(srcTabNm));
		return paramMap;
	}
	
	@RequestMapping("/translist")
	public Map<String,Object> getTransByNm(String setId,String srcSetId){
		return rptModelColTranBS.getTransById(setId, srcSetId);
	}
	
	@RequestMapping("/checkDstUsed")
	public Map<String,String> checkDstUsed(String dstTabNm){
		return rptModelColTranBS.checkDstUsed(dstTabNm);
	}
	
	@RequestMapping("/savetransmodel")
	public Map<String,String> saveTransModel(String dstTabNm,String srcTabNm,String whereSrcf,
			String dstArr,String srcArr,String ruleArr){
		Map<String,String> returnMap = Maps.newHashMap();
		Map<String,String> dstMap = Maps.newHashMap();
		Map<String,String> dstCnNmMap = Maps.newHashMap();
		Map<String,String> srcMap = Maps.newHashMap();
		List<RptSysModuleColTran> transList = Lists.newArrayList();
		List<RptSysModuleCol> dstList = rptModelColTranBS.getModelByNm(dstTabNm);
		List<RptSysModuleCol> srcList = rptModelColTranBS.getModelByNm(srcTabNm);
		String[] dstArrs = StringUtils.split(dstArr, ",");
		String[] srcArrs = StringUtils.split(srcArr, ",");
		String[] ruleArrs = StringUtils.split(ruleArr, ",");
		if(null != dstList && dstList.size() > 0 && null != srcList && srcList.size() > 0 &&
				dstArrs.length > 0 && srcArrs.length > 0 && dstArrs.length == dstArrs.length){
			for(int i=0;i<dstList.size();i++){
				dstMap.put(dstList.get(i).getEnNm(), dstList.get(i).getColId());
				dstCnNmMap.put(dstList.get(i).getEnNm(), dstList.get(i).getCnNm());
			}
			for(int i=0;i<srcList.size();i++){
				srcMap.put(srcList.get(i).getEnNm(), srcList.get(i).getColId());
			}
			for(int j=0;j<dstArrs.length;j++){
				RptSysModuleColTran tran = new RptSysModuleColTran();
				RptSysModuleColTranPK pk = new RptSysModuleColTranPK();
				pk.setSetId(dstList.get(0).getSetId());
				pk.setColId(dstMap.get(dstArrs[j]));
				tran.setId(pk);
				tran.setSrcColId(srcMap.get(srcArrs[j]));
				tran.setTransRule("null".equals(ruleArrs[j]) ? "" : ruleArrs[j]);
				tran.setRemark(dstCnNmMap.get(dstArrs[j]) == null ? "null" : dstCnNmMap.get(dstArrs[j]));
				transList.add(tran);
			}
			List<String> transField = new ArrayList<String>();
			transField.add("id.setId");
			excelBS.deleteEntityJdbcBatch(transList, transField);
			excelBS.saveEntityJdbcBatch(transList);
			rptModelColTranBS.updateModelInfo(dstList.get(0).getSetId(),srcList.get(0).getSetId(),whereSrcf);
			returnMap.put("msg", "success");
		}else{
			returnMap.put("msg", "error");
		}
		return returnMap;
	}
	
	@RequestMapping("/addColCheck")
	public Map<String,Object> addColCheck(String setId,String enNms) {
		Map<String,Object> rMap = Maps.newHashMap();
		if(StringUtils.isNotBlank(enNms)){
			List<String> namelist = ArrayUtils.asList(enNms, ",");
			List<String> rlist = this.rptModelColTranBS.addColCheck(setId, namelist);
			rMap.put("data", "");
			if(null != rlist && rlist.size() > 0){
				rMap.put("data", this.rptModelColTranBS.addColCheck(setId, namelist));
			}
		}
		return rMap;
	}
}
