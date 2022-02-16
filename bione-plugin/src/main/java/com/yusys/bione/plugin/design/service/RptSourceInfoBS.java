package com.yusys.bione.plugin.design.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.JpaEntityUtils;
import com.yusys.bione.comp.utils.ReflectionUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.entity.RptDesignCellInfo;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.repository.RptTmpDAO;
import com.yusys.bione.plugin.design.util.DesignAnalysisFactory;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDimTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcDsVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcFormulaVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxTabVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcIdxVO;
import com.yusys.bione.plugin.design.web.vo.RptDesignSrcTextVO;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.web.vo.RptTmpDataInfoVO;

/**
 * <pre>
 * Title:报表数据查询BS
 * Description:
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
@Service
@Transactional(readOnly = true)
public class RptSourceInfoBS extends BaseBS<Object> {

	@Autowired
	public RptTmpDAO rptTmpDAO;
	@Autowired
	public RptMgrInfoMybatisDao rptInfoDao;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 * @param rptId
	 *            报表Id
	 * @param params
	 *            查询参数 [{dimNo: 'dim1', op: '=', value: 'xxx'}, {dimNo: 'dim2',
	 *            op: 'like', value: '010%'}]
	 * @param dataDate
	 *            查询日期
	 * @param busiLineId
	 *            业务线Id
	 * @param cellNos
	 *            有权限的单元格Id List<String>
	 * @param validates
	 *            校验未通过的单元格Map {cellNo1: #FFFFFF,cellNo2: #FFFFFF}
	 * @param isExtend
	 *            是否补录
	 * @return Json excelJson cellInfo 单元格信息 error 错误信息
	 */
	public Map<String, Object> getSourceInfo(String rptId,
			List<Map<String, Object>> params, String dataDate,
			String busiLineId, List<String> cellNos,
			Map<String, String> validates, String fileName, String orgNm,
			boolean isExtend,boolean isDownLoad,boolean isInit) {
		long second = System.currentTimeMillis();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> rptParam = new HashMap<String, Object>();
		rptParam.put("rptId", rptId);
//		rptParam.put("rptType", GlobalConstants.RPT_TYPE_DESIGN);
		rptParam.put("dataDate", dataDate);
		List<RptTmpDataInfoVO> infos = rptInfoDao.getRptTmpParams(rptParam);
		if (infos != null && infos.size() > 0) {
			Map<String, RptTmpDataInfoVO> tmpMap = new HashMap<String, RptTmpDataInfoVO>();
			RptDesignTmpInfo tmp = null;
			RptTmpDataInfoVO maintmp = null;
			for (RptTmpDataInfoVO vo : infos) {
				tmpMap.put(vo.getRptId() + " " + vo.getLineId(), vo);
			}
			maintmp = tmpMap.get(rptId + " " + "null");
			if (maintmp != null) {
				if (busiLineId != null && !busiLineId.equals("")
						&& !busiLineId.equals("*")
						&& !busiLineId.equals("null")) {
					tmp = tmpMap.get(rptId + " " + busiLineId);
					if (tmp != null) {
						if (tmp.getTemplateContentjson() == null
								|| tmp.getTemplateContentjson().equals(""))
							tmp.setTemplateContentjson(maintmp
									.getTemplateContentjson());
					} else {
						result.put("error", "该报表在指定的数据日期内未配置模板信息");
						return result;
					}
				} else {
					tmp = maintmp;
				}
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("templateId", tmp.getId().getTemplateId());
				param.put("verId", tmp.getId().getVerId());
				param.put("dataDate", dataDate);
				param.put("lineId", busiLineId);
				Map<String,Object> condition = new HashMap<String, Object>();
				condition.put("templateId", maintmp.getId().getTemplateId());
				condition.put("verId", maintmp.getId().getVerId());
				List<RptDesignCellInfo> cells = this.rptTmpDAO.getTmpCells(param);
				List<RptDesignSrcIdxVO> idxCells = this.rptTmpDAO
						.getIdxCellsVO(param);
				List<RptDesignSrcDsVO> dsCells = this.rptTmpDAO
						.getDsCellsVO(param);
				List<RptDesignSrcFormulaVO> formulaCells = this.rptTmpDAO
						.getFormulaCellsVO(param);
				List<RptDesignSrcTextVO> textCells = this.rptTmpDAO
						.getTextCellsVO(param);
				List<RptDesignSrcIdxTabVO> idxTabCells = this.rptTmpDAO
						.getIdxTabCellsVO(param);
				List<RptDesignSrcDimTabVO> dimTabCells = this.rptTmpDAO
						.getDimTabCellsVO(param);
				/*List<RptDesignComcellInfo> comCells = this.rptTmpDAO
						.getTmpComCells(param);*/
				logger.debug("引擎交互前校验耗时："
						+ (System.currentTimeMillis() - second) + "ms");
				result = DesignAnalysisFactory.createAnalysis(tmp, cells,
						idxCells, dsCells, formulaCells, textCells,idxTabCells, dimTabCells,params,
						dataDate, "00",maintmp.getBusiType(), cellNos, validates, fileName,orgNm,isExtend,isDownLoad,isInit)
						.analysisSourceInfo();
			} else {
				result.put("error", "该报表在指定的数据日期内未配置模板信息");
				return result;
			}

		} else {
			result.put("error", "该报表并非设计平台报表");
		}
		return result;
	}

	/**
	 * 
	 * @param rptId
	 *            报表Id
	 * @param params
	 *            查询参数 [{dimNo: 'dim1', op: '=', value: 'xxx'}, {dimNo: 'dim2',
	 *            op: 'like', value: '010%'}]
	 * @param dataDate
	 *            查询日期
	 * @param busiLineId
	 *            业务线Id
	 * @param cellNos
	 *            有权限的单元格Id
	 * @return Json excelJson cellInfo 单元格信息 error 错误信息
	 */
	public Map<String, Object> getSourceInfo(String rptId,
			List<Map<String, Object>> params, String dataDate,
			String busiLineId, List<String> cellNos,boolean isDownLoad) {
		long second = System.currentTimeMillis();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> rptParam = new HashMap<String, Object>();
		rptParam.put("rptId", rptId);
		rptParam.put("rptType", GlobalConstants4plugin.RPT_TYPE_DESIGN);
		rptParam.put("dataDate", dataDate);
		List<RptTmpDataInfoVO> infos = rptInfoDao.getRptTmpParams(rptParam);
		if (infos != null && infos.size() > 0) {
			Map<String, RptTmpDataInfoVO> tmpMap = new HashMap<String, RptTmpDataInfoVO>();
			RptDesignTmpInfo tmp = null;
			RptTmpDataInfoVO maintmp = null;
			for (RptTmpDataInfoVO vo : infos) {
				tmpMap.put(vo.getRptId() + " " + vo.getLineId(), vo);
			}
			maintmp = tmpMap.get(rptId + " " + "null");
			if (maintmp != null) {
				if (busiLineId != null && !busiLineId.equals("")
						&& !busiLineId.equals("*")
						&& !busiLineId.equals("null")) {
					tmp = tmpMap.get(rptId + " " + busiLineId);
					if (tmp != null) {
						if (tmp.getTemplateContentjson() == null
								|| tmp.getTemplateContentjson().equals(""))
							tmp.setTemplateContentjson(maintmp
									.getTemplateContentjson());
					} else {
						result.put("error", "该报表在指定的数据日期内未配置模板信息");
						return result;
					}
				} else {
					tmp = maintmp;
				}
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("templateId", tmp.getId().getTemplateId());
				param.put("verId", tmp.getId().getVerId());
				param.put("dataDate", dataDate);
				param.put("lineId", busiLineId);
				Map<String,Object> condition = new HashMap<String, Object>();
				condition.put("templateId", maintmp.getId().getTemplateId());
				condition.put("verId", maintmp.getId().getVerId());
				List<RptDesignCellInfo> cells = this.rptTmpDAO.getTmpCells(param);
				List<RptDesignSrcIdxVO> idxCells = this.rptTmpDAO
						.getIdxCellsVO(param);
				List<RptDesignSrcDsVO> dsCells = this.rptTmpDAO
						.getDsCellsVO(param);
				List<RptDesignSrcFormulaVO> formulaCells = this.rptTmpDAO
						.getFormulaCellsVO(param);
				List<RptDesignSrcTextVO> textCells = this.rptTmpDAO
						.getTextCellsVO(param);
				List<RptDesignSrcIdxTabVO> idxTabCells = this.rptTmpDAO
						.getIdxTabCellsVO(param);
				List<RptDesignSrcDimTabVO> dimTabCells = this.rptTmpDAO
						.getDimTabCellsVO(param);
			/*	List<RptDesignComcellInfo> comCells = this.rptTmpDAO
						.getTmpComCells(param);*/
				logger.debug("引擎交互前校验耗时："
						+ (System.currentTimeMillis() - second) + "ms");
				result = DesignAnalysisFactory.createAnalysis(tmp, cells,
						idxCells, dsCells, formulaCells, textCells, idxTabCells,dimTabCells,params,
						dataDate, "00",maintmp.getBusiType(), cellNos,isDownLoad,true)
						.analysisSourceInfo();
			} else {
				result.put("error", "该报表在指定的数据日期内未配置模板信息");
			}
		} else {
			result.put("error", "该报表并非设计平台报表");
		}
		return result;
	}

	public Map<String, Object> dealExtendInfo(String json,
			Map<String, Map<String, Object>> cellInfo, String type) {
		Map<String, Object> result = new HashMap<String, Object>();
		result = DesignAnalysisFactory.dealExtendInfo(json, cellInfo, type)
				.dealExtendInfo();
		return result;
	}

	public void copy(Map<String, Object> map, Object obj)  {
		Class<?> saveClass = obj.getClass();
		Map<String, String> fieldNms = JpaEntityUtils
				.getColumnsByEntity(saveClass);
		Iterator<String> it = fieldNms.keySet().iterator();
		while (it.hasNext()) {
			try {
			String fieldNm = it.next();
			if(fieldNm.indexOf(".")>=0){
				Object oInfo=ReflectionUtils.invokeGetter(obj, "id");
				if(oInfo==null){
					oInfo=saveClass.getDeclaredField("id").getType().newInstance();
					ReflectionUtils.invokeSetter(obj, "id", oInfo);
				}
			}
			ReflectionUtils._invokeSetterMethod(obj, fieldNm,
					map.get(fieldNms.get(fieldNm)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

}
