package com.yusys.bione.plugin.rptshow.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datashow.repository.RptInnerShowMybatisDao;
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
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportHis;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.web.vo.RptTmpDataInfoVO;

@Service
@Transactional(readOnly = true)
public class RptPlatShowBS extends BaseBS<Object> {

	@Autowired
	private RptInnerShowMybatisDao rptShowDao;

	@Autowired
	public RptTmpDAO rptTmpDAO;
	@Autowired
	public RptMgrInfoMybatisDao rptInfoDao;

	/**
	 * 获取用户的报表定义来源
	 * 
	 * @return
	 */
	public List<String> getRptDefSrc() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("defOrg", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		map.put("defUser", BioneSecurityUtils.getCurrentUserId());
		List<String> defSrcList = this.rptShowDao.getRptDefSrc(map);
		if (defSrcList == null) {
			defSrcList = Lists.newArrayList();
		}
		defSrcList.add(GlobalConstants4plugin.INDEX_DEF_SRC_LIB);
		return defSrcList;
	}

	public String getRptParamInfo(String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		return this.rptShowDao.getRptParamInfo(params);
	}

	public RptDesignTmpInfo getRptTmpInfo(String rptId) {
		String jql = "select d from RptDesignTmpInfo d,RptMgrReportInfo r where r.rptId=?0 and r.cfgId=d.id.templateId";
		List<RptDesignTmpInfo> types = this.baseDAO.findWithIndexParam(jql, rptId);
		if (types != null && types.size() > 0) {
			return types.get(0);
		}
		return null;
	}

	public boolean getCheckOrgInfo(String rptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rptId", rptId);
		List<String> types = this.rptShowDao.getRptType(params);
		if (types != null && types.size() > 0) {
			String type = types.get(0);
			if (type.equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_V)
					|| type.equals(GlobalConstants4plugin.RPT_TMP_TYPE_IDXTAB_H)) {
				params.put("dimTypeNo", "ORG");
				return this.rptShowDao.getCheckOrgInfo(params).size() > 0 ? true
						: false;
			} else
				return false;
		} else
			return false;

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
	 * @param cellNos
	 *            有权限的单元格Id List<String>
	 * @param validates
	 *            校验未通过的单元格Map {cellNo1: #FFFFFF,cellNo2: #FFFFFF}
	 * @param isExtend
	 *            是否补录
	 * @return Json excelJson cellInfo 单元格信息 error 错误信息
	 */
	/**
	 * 
	 * @param rptId
	 *            报表Id
	 * @param params
	 *            查询参数 [{dimNo: 'dim1', op: '=', value: 'xxx'}, {dimNo: 'dim2',
	 *            op: 'like', value: '010%'}]
	 * @param dataDate
	 *            查询日期
	 * @param isDownLoad
	 *            是否下载（明细报表特有）
	 * @param isInit
	 *            是否初始化（明细报表特有）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSourceInfo(String rptId,
			List<Map<String, Object>> params, String dataDate,String unit,
			boolean isExtend, boolean isDownLoad, boolean isInit,boolean isCache) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> rptParam = new HashMap<String, Object>();
		rptParam.put("rptId", rptId);
		if (StringUtils.isNotEmpty(dataDate)) {
			rptParam.put("dataDate", dataDate);
		}
		List<RptTmpDataInfoVO> infos = rptInfoDao.getRptTmpParams(rptParam);
		if (infos != null && infos.size() > 0) {
			Map<String, RptTmpDataInfoVO> tmpMap = new HashMap<String, RptTmpDataInfoVO>();
			RptTmpDataInfoVO maintmp = null;
			for (RptTmpDataInfoVO vo : infos) {
				tmpMap.put(vo.getRptId(), vo);
			}
			maintmp = tmpMap.get(rptId);
			if (maintmp != null) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("templateId", maintmp.getId().getTemplateId());
				param.put("verId", maintmp.getId().getVerId());
				if (StringUtils.isNotEmpty(dataDate)) {
					rptParam.put("dataDate", dataDate);
				}
				Map<String, Object> tmpMapInfo = new HashMap<String, Object>();
				List<RptDesignCellInfo> cells = new ArrayList<RptDesignCellInfo>();
				List<RptDesignSrcIdxVO> idxCells = new ArrayList<RptDesignSrcIdxVO>();
				List<RptDesignSrcDsVO> dsCells = new ArrayList<RptDesignSrcDsVO>();
				List<RptDesignSrcFormulaVO> formulaCells = new ArrayList<RptDesignSrcFormulaVO>();
				List<RptDesignSrcTextVO> textCells = new ArrayList<RptDesignSrcTextVO>();
				List<RptDesignSrcIdxTabVO> idxTabCells = new ArrayList<RptDesignSrcIdxTabVO>();
				List<RptDesignSrcDimTabVO> dimTabCells = new ArrayList<RptDesignSrcDimTabVO>();
//				List<RptDesignComcellInfo> comCells = new ArrayList<RptDesignComcellInfo>();
				
				Object tmpInfo = EhcacheUtils.get(maintmp.getId()
						.getTemplateId() + "-" + maintmp.getId().getVerId(),
						"tmpInfo");
				if (tmpInfo != null) {
					tmpMapInfo = (Map<String, Object>) tmpInfo;
					cells = (List<RptDesignCellInfo>) tmpMapInfo.get("cells");
//					comCells = (List<RptDesignComcellInfo>) tmpMapInfo.get("comCells");
					idxCells = (List<RptDesignSrcIdxVO>) tmpMapInfo
							.get("idxCells");
					dsCells = (List<RptDesignSrcDsVO>) tmpMapInfo
							.get("dsCells");
					formulaCells = (List<RptDesignSrcFormulaVO>) tmpMapInfo
							.get("formulaCells");
					textCells = (List<RptDesignSrcTextVO>) tmpMapInfo
							.get("textCells");
					idxTabCells = (List<RptDesignSrcIdxTabVO>) tmpMapInfo
							.get("idxTabCells");
					dimTabCells = (List<RptDesignSrcDimTabVO>) tmpMapInfo
							.get("dimTabCells");
				} else {
					cells = this.rptTmpDAO.getTmpCells(param);
					idxCells = this.rptTmpDAO.getIdxCellsVO(param);
					dsCells = this.rptTmpDAO.getDsCellsVO(param);
					formulaCells = this.rptTmpDAO.getFormulaCellsVO(param);
					textCells = this.rptTmpDAO.getTextCellsVO(param);
					idxTabCells = this.rptTmpDAO.getIdxTabCellsVO(param);
					dimTabCells = this.rptTmpDAO.getDimTabCellsVO(param);
//					comCells = this.rptTmpDAO.getTmpComCells(param);
					Map<String,Object> condition = new HashMap<String, Object>();
					condition.put("templateId", maintmp.getId().getTemplateId());
					condition.put("verId", maintmp.getId().getVerId());
					tmpMapInfo.put("cells", cells);
					tmpMapInfo.put("idxCells", idxCells);
					tmpMapInfo.put("dsCells", dsCells);
					tmpMapInfo.put("formulaCells", formulaCells);
					tmpMapInfo.put("textCells", textCells);
					tmpMapInfo.put("idxTabCells", idxTabCells);
					tmpMapInfo.put("dimTabCells", dimTabCells);
//					tmpMapInfo.put("comCells", comCells);
					EhcacheUtils
							.put(maintmp.getId().getTemplateId() + "-"
									+ maintmp.getId().getVerId(), "tmpInfo",
									tmpMapInfo);
				}
				long time = System.currentTimeMillis();
				result = DesignAnalysisFactory.createAnalysis(maintmp, cells,
						idxCells, dsCells, formulaCells, textCells,
						idxTabCells, dimTabCells, params, dataDate, unit,isExtend,
						isDownLoad, isInit,isCache).analysisSourceInfo();
				System.out.println("-------------------------createAnalysis---"
						+ (System.currentTimeMillis() - time)
						+ "------------------------");
				
				result.put("dealtime", System.currentTimeMillis() - time);  //把计算时间也返回到前台,方便监控

			} else {
				result.put("error", "用户所选的查询日期早于当前报表的启用日期");
				return result;
			}

		} else {
			result.put("error", "用户所选的查询日期早于当前报表的启用日期");
		}
		return result;
	}

	public String getRptTypeInfo(String rptId) {
		String jql = "select d.templateType from RptDesignTmpInfo d,RptMgrReportInfo r where r.rptId=?0 and r.cfgId=d.id.templateId";
		List<String> types = this.baseDAO.findWithIndexParam(jql, rptId);
		if (types != null && types.size() > 0) {
			return types.get(0);
		}
		return "";
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
	 * @param cellNos
	 *            有权限的单元格Id List<String>
	 * @param validates
	 *            校验未通过的单元格Map {cellNo1: #FFFFFF,cellNo2: #FFFFFF}
	 * @param isExtend
	 *            是否补录
	 * @return Json excelJson cellInfo 单元格信息 error 错误信息
	 */
	/**
	 * 
	 * @param rptId
	 *            报表Id
	 * @param params
	 *            查询参数 [{dimNo: 'dim1', op: '=', value: 'xxx'}, {dimNo: 'dim2',
	 *            op: 'like', value: '010%'}]
	 * @param dataDate
	 *            查询日期
	 * @param isDownLoad
	 *            是否下载（明细报表特有）
	 * @param isInit
	 *            是否初始化（明细报表特有）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getDataInfo(String rptId,
			List<Map<String, Object>> params, String dataDate,String unit,
			boolean isExtend, boolean isDownLoad, boolean isInit,boolean isCache) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> rptParam = new HashMap<String, Object>();
		rptParam.put("rptId", rptId);
		if (StringUtils.isNotEmpty(dataDate)) {
			rptParam.put("dataDate", dataDate);
		}
		List<RptTmpDataInfoVO> infos = rptInfoDao.getRptTmpParams(rptParam);
		if (infos != null && infos.size() > 0) {
			Map<String, RptTmpDataInfoVO> tmpMap = new HashMap<String, RptTmpDataInfoVO>();
			RptTmpDataInfoVO maintmp = null;
			for (RptTmpDataInfoVO vo : infos) {
				tmpMap.put(vo.getRptId() + " " + vo.getLineId(), vo);
			}
			maintmp = tmpMap.get(rptId + " " + "null");
			if (maintmp != null) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("templateId", maintmp.getId().getTemplateId());
				param.put("verId", maintmp.getId().getVerId());
				if (StringUtils.isNotEmpty(dataDate)) {
					rptParam.put("dataDate", dataDate);
				}
				Map<String, Object> tmpMapInfo = new HashMap<String, Object>();
				List<RptDesignCellInfo> cells = new ArrayList<RptDesignCellInfo>();
				List<RptDesignSrcIdxVO> idxCells = new ArrayList<RptDesignSrcIdxVO>();
				List<RptDesignSrcDsVO> dsCells = new ArrayList<RptDesignSrcDsVO>();
				List<RptDesignSrcFormulaVO> formulaCells = new ArrayList<RptDesignSrcFormulaVO>();
				List<RptDesignSrcTextVO> textCells = new ArrayList<RptDesignSrcTextVO>();
				List<RptDesignSrcIdxTabVO> idxTabCells = new ArrayList<RptDesignSrcIdxTabVO>();
				List<RptDesignSrcDimTabVO> dimTabCells = new ArrayList<RptDesignSrcDimTabVO>();
//				List<RptDesignComcellInfo> comCells = new ArrayList<RptDesignComcellInfo>();
				Object tmpInfo = EhcacheUtils.get(maintmp.getId()
						.getTemplateId() + "-" + maintmp.getId().getVerId(),
						"tmpInfo");
				if (tmpInfo != null) {
					tmpMapInfo = (Map<String, Object>) tmpInfo;
					cells = (List<RptDesignCellInfo>) tmpMapInfo.get("cells");
//					comCells = (List<RptDesignComcellInfo>) tmpMapInfo.get("comCells");
					idxCells = (List<RptDesignSrcIdxVO>) tmpMapInfo
							.get("idxCells");
					dsCells = (List<RptDesignSrcDsVO>) tmpMapInfo
							.get("dsCells");
					formulaCells = (List<RptDesignSrcFormulaVO>) tmpMapInfo
							.get("formulaCells");
					textCells = (List<RptDesignSrcTextVO>) tmpMapInfo
							.get("textCells");
					idxTabCells = (List<RptDesignSrcIdxTabVO>) tmpMapInfo
							.get("idxTabCells");
					dimTabCells = (List<RptDesignSrcDimTabVO>) tmpMapInfo
							.get("dimTabCells");
				} else {
					cells = this.rptTmpDAO.getTmpCells(param);
					idxCells = this.rptTmpDAO.getIdxCellsVO(param);
					dsCells = this.rptTmpDAO.getDsCellsVO(param);
					formulaCells = this.rptTmpDAO.getFormulaCellsVO(param);
					textCells = this.rptTmpDAO.getTextCellsVO(param);
					idxTabCells = this.rptTmpDAO.getIdxTabCellsVO(param);
					dimTabCells = this.rptTmpDAO.getDimTabCellsVO(param);
//					comCells =  this.rptTmpDAO.getTmpComCells(param);
					Map<String,Object> condition = new HashMap<String, Object>();
					condition.put("templateId", maintmp.getId().getTemplateId());
					condition.put("verId", maintmp.getId().getVerId());
					tmpMapInfo.put("cells", cells);
					tmpMapInfo.put("idxCells", idxCells);
					tmpMapInfo.put("dsCells", dsCells);
					tmpMapInfo.put("formulaCells", formulaCells);
					tmpMapInfo.put("textCells", textCells);
					tmpMapInfo.put("idxTabCells", idxTabCells);
					tmpMapInfo.put("dimTabCells", dimTabCells);
					EhcacheUtils
							.put(maintmp.getId().getTemplateId() + "-"
									+ maintmp.getId().getVerId(), "tmpInfo",
									tmpMapInfo);
				}
				long time = System.currentTimeMillis();
				result = DesignAnalysisFactory.createAnalysis(maintmp, cells,
						idxCells, dsCells, formulaCells, textCells,
						idxTabCells, dimTabCells, params, dataDate, unit,isExtend,
						isDownLoad, isInit,isCache).getDataInfo();
				System.out.println("-------------------------createAnalysis---"
						+ (System.currentTimeMillis() - time)
						+ "------------------------");
				
				result.put("dealtime", System.currentTimeMillis() - time);  //把计算时间也返回到前台,方便监控

			} else {
				result.put("error", "用户所选的查询日期早于当前报表的启用日期");
				return result;
			}

		} else {
			result.put("error", "用户所选的查询日期早于当前报表的启用日期");
		}
		return result;
	}
	
	public String getAutoAdjByRptId(String rptId) {
		String jql = "select tmp.isAutoAdj from RptDesignTmpInfo tmp,RptMgrReportInfo rpt where rpt.rptId = ?0 and tmp.verEndDate = ?1 and rpt.cfgId = tmp.id.templateId";
		String isAutoAdj = this.baseDAO.findUniqueWithIndexParam(jql, rptId,
				"29991231");
		if (!StringUtils.isNotBlank(isAutoAdj)) {
			isAutoAdj = "N";
		}
		return isAutoAdj;
	}
	
	public List<String> getAllCellById(String rptId,String dataDt,String rptType) {
		String jql = "select idx.id.cellNo from RptMgrReportInfo mgr,RptDesignSourceIdx idx,RptIdxReportMark par where mgr.rptId = ?0 and par.id.dataDt = ?1 and mgr.cfgId = idx.id.templateId and idx.indexNo = par.id.idxNo";
		if("04".equals(rptType) || "05".equals(rptType)){//指标列表纵、横
			jql = "select idx.id.cellNo from RptMgrReportInfo mgr,RptDesignSourceTabidx idx,RptIdxReportMark par where mgr.rptId = ?0 and par.id.dataDt = ?1 and mgr.cfgId = idx.id.templateId and idx.indexNo = par.id.idxNo";
		}
		return this.baseDAO.findWithIndexParam(jql, rptId,dataDt);
	}
	
	public String getRptDrawDate(String rptId,String type) {
		String sql = "select draw.rpt_id,draw.draw_date from RPT_RPT_DRAW_INFO draw where draw.rpt_Id = ?0 ";
		List<Object[]> lists = this.baseDAO.findByNativeSQLWithIndexParam(sql, rptId);
		if(lists != null && lists.size() > 0){
			if(lists.get(0)[1]!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				String date = lists.get(0)[1].toString();
				try {
					date = sdf1.format(sdf.parse(date));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					return "";
				}
				return date; 
			}
			else{
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				if("01".equals(type))
					return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
				else if("02".equals(type))
					return sdf1.format(DateUtils.lastDateOfMonth(sdf.format(new Date())));
				else if("03".equals(type))
					return sdf1.format(DateUtils.lastDateOfSeason(sdf.format(new Date())));
				else if("04".equals(type))
					return sdf1.format(DateUtils.lastDateOfYear(sdf.format(new Date())));
				else
					return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
			}
		}
		else{
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			if("01".equals(type))
				return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
			else if("02".equals(type))
				return sdf1.format(DateUtils.lastDateOfMonth(sdf.format(new Date())));
			else if("03".equals(type))
				return sdf1.format(DateUtils.lastDateOfSeason(sdf.format(new Date())));
			else if("04".equals(type))
				return sdf1.format(DateUtils.lastDateOfYear(sdf.format(new Date())));
			else
				return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
		}	
	}
	
	@Transactional(readOnly=false)
	public void saveReportHis(RptMgrReportHis his){
		this.saveOrUpdateEntity(his);
	}
}
