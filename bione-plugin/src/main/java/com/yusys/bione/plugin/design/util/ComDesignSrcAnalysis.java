package com.yusys.bione.plugin.design.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDimTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDsVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcFormulaVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcTextVO;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

public class ComDesignSrcAnalysis extends AbstractDesignSrcAnalysis{


	public ComDesignSrcAnalysis(SpreadSchema scheme, RptDesignTmpInfo tmp,
			List<RptDesignCellInfo> cells,List<RptDesignSrcIdxVO> idxCells,
			List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> searchParams, String dataDate,String unit,
			String busiType, List<String> cellNos,
			Map<String, String> validates,String fileName,boolean isExtend, boolean isDownLoad, boolean isInit,boolean isCache) {
		super(scheme, tmp, cells,idxCells, dsCells, formulaCells, textCells,idxTabCells,dimTabCells,
				searchParams, dataDate,unit, busiType, cellNos, validates, fileName,isExtend,isDownLoad,isInit,isCache);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createSelectJson() {
		// TODO Auto-generated method stub
		Map<String,Object> param=new HashMap<String, Object>();
		Map<String,Object> page=new HashMap<String, Object>();
		Map<String,Object> start=null;
		Map<String,Object> step=null;
		Map<String,Object> date=null;
		if(this.getSearchParams()!=null&&this.getSearchParams().size()>0){
			for(Map<String,Object> info: this.getSearchParams()){
				if(info.get("DimNo").equals("start")){
					page.put("Start", info.get("Value"));
					start=info;
				}
				else if(info.get("DimNo").equals("step")){
					page.put("Step", info.get("Value"));
					step=info;
				}
				else if(info.get("DimNo").equals("DATE")){
					date=info;
				}
			}
		}
		if(start!=null)
			this.getSearchParams().remove(start);
		if(step!=null)
			this.getSearchParams().remove(step);
		if(date!=null)
			this.getSearchParams().remove(date);
		param.put("TemplateId", this.getTmp().getId().getTemplateId());
		param.put("VerId", this.getTmp().getId().getVerId());
		param.put("QueryType", this.getTmp().getId().getVerId());
		param.put("DimNo", creatDimNos(this.getSearchParams()));
		param.put("DataDate", this.getDataDate());
		param.put("SearchArg", this.getSearchParams());
		if(isCache){
			param.put("ExecNode", "11");
		}
		this.setIdxSelectParams(JSON.toJSONString(param));
		
		param=new HashMap<String, Object>();
		param.put("TemplateId",this.getTmp().getId().getTemplateId());
		param.put("VerId",this.getTmp().getId().getVerId());
		param.put("QueryType","dataset");
		param.put("DataDate",this.getDataDate());
		param.put("SearchArg",this.getSearchParams());
		if(isCache){
			param.put("ExecNode", "11");
		}
		if(page.get("Start")!=null&&page.get("Step")!=null){
			param.put("Paging",page);
		}
		this.setDsSelectParams(JSON.toJSONString(param));
	}

}
