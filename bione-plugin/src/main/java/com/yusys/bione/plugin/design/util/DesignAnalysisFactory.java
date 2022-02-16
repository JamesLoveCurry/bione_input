package com.yusys.bione.plugin.design.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDimTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDsVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcFormulaVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcTextVO;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

/**
 * <pre>
 * Title: 报表查询工厂类
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

public class DesignAnalysisFactory {
	public static AbstractDesignSrcAnalysis createAnalysis(
			RptDesignTmpInfo tmp, List<RptDesignCellInfo> cells,
			List<RptDesignSrcIdxVO> idxCells, List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> selectParams, String dataDate,String unit,
			String busiType, List<String> cellNos,
			Map<String, String> validates,String fileName,String orgNm,boolean isExtend,boolean isDownLoad,boolean isInit) {
		SpreadSchema schema = JSON.parseObject(tmp.getTemplateContentjson(), SpreadSchema.class);
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)) {
			return new DetailDesignSrcAnalysis(schema, tmp, cells, dsCells,
					formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					validates,fileName,isExtend,isDownLoad,isInit,false);
		}
		
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CELL)) {
			return new CellDesignSrcAnalysis(schema, tmp, cells,idxCells,
					formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					validates,fileName, isExtend,false);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_COM)) {
			return new ComDesignSrcAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					validates, fileName,isExtend,isDownLoad,isInit,false);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V) || tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)) {
			return new IdxTabDesignAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					validates, fileName,isExtend,false);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CROSS)) {
			return new CrossDesignAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					validates, fileName,isExtend,false);
		}
		return null;
	}

	public static AbstractDesignSrcAnalysis createAnalysis(
			RptDesignTmpInfo tmp, List<RptDesignCellInfo> cells,
			List<RptDesignSrcIdxVO> idxCells, List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> selectParams, String dataDate,String unit,
			String busiType, List<String> cellNos,boolean isDownLoad,boolean isInit) {
		SpreadSchema schema = JSON.parseObject(tmp.getTemplateContentjson(), SpreadSchema.class);
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)) {
			return new DetailDesignSrcAnalysis(schema, tmp, cells, dsCells,
					formulaCells, textCells,idxTabCells , dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					null, null,false,isDownLoad,isInit,false);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CELL)) {
			return new CellDesignSrcAnalysis(schema, tmp, cells, idxCells,
					formulaCells, textCells, idxTabCells , dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					null,null,false,false);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_COM)) {
			return new ComDesignSrcAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells,idxTabCells , dimTabCells, selectParams, dataDate,unit,busiType, cellNos,
					null,null,false,isDownLoad,isInit,false);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V) || tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)) {
			return new IdxTabDesignAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					null,null,false,false);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CROSS)) {
			return new CrossDesignAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,busiType, cellNos,
					null, null,false,false);
		}
		return null;
	}
	
	public static AbstractDesignSrcAnalysis createAnalysis(
			RptDesignTmpInfo tmp, List<RptDesignCellInfo> cells,
			List<RptDesignSrcIdxVO> idxCells, List<RptDesignSrcDsVO> dsCells,
			List<RptDesignSrcFormulaVO> formulaCells,
			List<RptDesignSrcTextVO> textCells,
			List<RptDesignSrcIdxTabVO> idxTabCells,
			List<RptDesignSrcDimTabVO> dimTabCells,
			List<Map<String, Object>> selectParams, String dataDate,String unit,boolean isExtend,
			boolean isDownLoad,boolean isInit,boolean isCache) {
		SpreadSchema schema = JSON.parseObject(tmp.getTemplateContentjson(), SpreadSchema.class);
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)) {
			return new DetailDesignSrcAnalysis(schema, tmp, cells, dsCells,
					formulaCells, textCells,idxTabCells , dimTabCells,selectParams, dataDate,unit,"01", null,
					null, null,isExtend,isDownLoad,isInit,isCache);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CELL)) {
			return new CellDesignSrcAnalysis(schema, tmp, cells, idxCells,
					formulaCells, textCells, idxTabCells , dimTabCells,selectParams, dataDate,unit,"01", null,
					null,null,isExtend,isCache);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_COM)) {
			return new ComDesignSrcAnalysis(schema, tmp, cells,idxCells,
					dsCells, formulaCells, textCells,idxTabCells , dimTabCells, selectParams, dataDate,unit,"01", null,
					null,null,isExtend,isDownLoad,isInit,isCache);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V) || tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)){
			return new IdxTabDesignAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,"01", null,
					null,null,isExtend,isCache);
		}
		if (tmp.getTemplateType().equals(GlobalConstants4plugin.RPT_TMP_TYPE_CROSS)) {
			return new CrossDesignAnalysis(schema, tmp, cells, idxCells,
					dsCells, formulaCells, textCells, idxTabCells,dimTabCells,selectParams, dataDate,unit,"01", null,
					null,null,isExtend,isCache);
		}
		return null;
	}

	public static AbstractDesignSrcAnalysis dealExtendInfo(String json,
			Map<String, Map<String, Object>> cellInfo, String type) {
		SpreadSchema schema = JSON.parseObject(json, SpreadSchema.class);
		if (type.equals(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL)) {
			return new DetailDesignSrcAnalysis(schema, cellInfo);
		}
		if (type.equals(GlobalConstants4plugin.RPT_TMP_TYPE_CELL)) {
			return new CellDesignSrcAnalysis(schema, cellInfo);
		}
		return null;
	}
}
