package com.yusys.bione.plugin.design.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDimTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcFormulaVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcTextVO;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

/**
 * <pre>
 * Title: 单元格模板查询
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

public class CellDesignSrcAnalysis extends AbstractDesignSrcAnalysis {

	public CellDesignSrcAnalysis(SpreadSchema schema, RptDesignTmpInfo tmp,
			List<RptDesignCellInfo> cells, List<RptDesignSrcIdxVO> idxCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> searchParams, String dataDate,String unit,
			String busiType, List<String> cellNos,
			Map<String, String> validates,String fileName,boolean isExtend,boolean isCache) {
		super(schema, tmp, cells, idxCells, null, formulaCells, textCells,idxTabCells,dimTabCells,
				searchParams, dataDate,unit, busiType, cellNos, validates, fileName,isExtend,null,true,isCache);

	}

	public CellDesignSrcAnalysis(SpreadSchema schema,
			Map<String, Map<String, Object>> cellExtInfos) {
		// TODO Auto-generated constructor stub
		super(schema, cellExtInfos);
	}

	@Override
	protected void createSelectJson() {
		// TODO Auto-generated method stub
		if(this.getSearchParams()!=null&&this.getSearchParams().size()>0){
			for(Map<String,Object> info: this.getSearchParams()){
				if(info.get("DimNo").equals("DATE")){
					this.getSearchParams().remove(info);
					break;
				}
			}
		}
		List<String> dimNos = creatDimNos(this.getSearchParams());
		Map<String,Object> param=new HashMap<String, Object>();
		param.put("TemplateId", this.getTmp().getId().getTemplateId());
		param.put("VerId", this.getTmp().getId().getVerId());
		param.put("QueryType", "report");
		param.put("DimNo",dimNos);
		param.put("DataDate", this.getDataDate());
		//添加机构权限
		if(dimNos!=null && dimNos.size()>0){
			for(String dimNo : dimNos){
				if(dimNo.equals("ORG")){
					List<Map<String,Object>> searchArg=setOrgValidate(this.getSearchParams());
					if(searchArg!=null){
						this.getSearchParams().addAll(searchArg);
					}
					break;
				}
			}
		}
		if(isCache){
			param.put("ExecNode", "11");
		}
		//
		param.put("SearchArg",this.getSearchParams() );
		this.setIdxSelectParams(JSON.toJSONString(param));
	}

	

}
