package com.yusys.bione.plugin.design.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfoPK;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDimTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDsVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcFormulaVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcTextVO;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

/**
 * <pre>
 * Title: 明细模板查询
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author weijx weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class DetailDesignSrcAnalysis extends AbstractDesignSrcAnalysis {

	public DetailDesignSrcAnalysis(SpreadSchema schema, RptDesignTmpInfo tmp,
			List<RptDesignCellInfo> cells, List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> searchParams, String dataDate,String unit,
			String busiType, List<String> cellNos,
			Map<String, String> validates, String fileName, boolean isExtend,
			boolean isDownLoad, boolean isInit,boolean isCache) {
		// TODO Auto-generated constructor stub
		super(schema, tmp, cells, null, dsCells, formulaCells, textCells,
				idxTabCells, dimTabCells, searchParams, dataDate, unit,busiType,
				cellNos, validates, fileName, isExtend, isDownLoad, isInit,isCache);
	}

	public DetailDesignSrcAnalysis(SpreadSchema schema,
			Map<String, Map<String, Object>> cellExtInfos) {
		// TODO Auto-generated constructor stub
		super(schema, cellExtInfos);
	}

	@Override
	protected void createSelectJson() {
		// TODO Auto-generated method stub
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> page = new HashMap<String, Object>();
		param.put("TemplateId", this.getTmp().getId().getTemplateId());
		param.put("VerId", this.getTmp().getId().getVerId());
		param.put("QueryType", "dataset");
		if (StringUtils.isNotEmpty(this.getDataDate())) {
			param.put("DataDate", this.getDataDate());
		}
		if(isInit){
			List<String> sumColumn = new ArrayList<String>();
			if(this.dsCellList != null && dsCellList.size()>0){
				int i = 0;
				for(RptDesignSrcDsVO dsVo : dsCellList){
					if(i == 0){
						this.fCellNo = dsVo.getId().getCellNo();
					}
					if(GlobalConstants4plugin.DATA_SUM_SUM.equals(dsVo.getSumMode())){
						sumColumn.add(dsVo.getId().getCellNo());
					}
					i++;
				}
				param.put("SumColumn", sumColumn);
			}
		}
		Map<String, Object> start = null;
		Map<String, Object> step = null;
		Map<String, Object> date = null;
		if (this.getSearchParams() != null && this.getSearchParams().size() > 0) {
			for (Map<String, Object> info : this.getSearchParams()) {
				if ("start".equals(info.get("DimNo"))) {
					page.put("Start", info.get("Value"));
					start = info;
				} else if ("step".equals(info.get("DimNo"))) {
					page.put("Step", info.get("Value"));
					this.extIndex = Integer.parseInt(info.get("Value")
							.toString());
					step = info;
				} else if ("DATE".equals(info.get("DimNo"))) {
					date = info;
				}
			}
		}
		if (start != null)
			this.getSearchParams().remove(start);
		if (step != null)
			this.getSearchParams().remove(step);
		if (date != null)
			this.getSearchParams().remove(date);
		if (isDownLoad) {
			param.put("IsDownload", "Y");
		}
		// 添加机构权限
		List<Map<String, Object>> searchArg = setOrgValidate(this.getSearchParams());
		if (searchArg != null) {
			this.getSearchParams().addAll(searchArg);
		}
		param.put("SearchArg", this.getSearchParams());
		if (page.get("Start") != null && page.get("Step") != null) {
			param.put("Paging", page);
		}
		List<String> noTranColumn = new ArrayList<String>();
		for (RptDesignCellInfoPK id : this.dsCellMaps.keySet()) {
			if ("N".equals(this.dsCellMaps.get(id).getIsConver())) {
				noTranColumn.add(id.getCellNo());
			}
		}
		if (noTranColumn != null && noTranColumn.size() > 0) {
			param.put("NoTranColumn", noTranColumn);
		}
		if(isCache){
			param.put("ExecNode", "11");
		}
		String json = JSON.toJSONString(param);
		this.setDsSelectParams(json);

	}

}
