package com.yusys.bione.plugin.rptidx.service;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonDupontNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.repository.mybatis.RptDataSetDao;
import com.yusys.bione.plugin.datamodel.vo.RptSqlFilterVO;
import com.yusys.bione.plugin.design.entity.RptDesignQueryDim;
import com.yusys.bione.plugin.idxana.util.FetchDataUtils;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.repository.mybatis.RptDimDao;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;
import com.yusys.bione.plugin.rptidx.repository.IdxInfoMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.RptIdxAnalysisMybatisDao;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxInfoRelVO;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrInfoVO;

@Service
@Transactional(readOnly = true)
public class RptIdxAnalysisBS extends BaseBS<Object> {

	@Autowired
	private RptDataSetDao rptDataSetDao;

	@Autowired
	private RptIdxAnalysisMybatisDao rptAlyDao;

	@Autowired
	private IdxInfoMybatisDao idxInfoDao;

	@Autowired
	private RptDimDao rptDimDao;

	@Autowired
	private RptMgrInfoMybatisDao rptMgrDao;

	private enum RelType {
		idx, rpt, dim,model
	}
	
	private static final String ORACLE_DATA_SOURCE = "1";// 数据源为Oracle标识
	

	private static final String H2_DATA_SOURCE = "3";// 数据源为H2标识
	
	private static final String HIVE_DATA_SOURCE = "6";// 数据源为Oracle标识

	private final String IDX_COLOR = "#56A2F5";// 数据源为Oracle标识
	private final String RPT_COLOR = "#F5568E";// 数据源为Oracle标识
	private final String SYS_COLOR = "#56F58E";// 数据源为Oracle标识
	private final String DIM_COLOR = "#F5A756";// 数据源为Oracle标识


	private void addIdxNos(List<CommonDupontNode> vos, List<String> idxNos) {
		if (vos != null && vos.size() > 0) {
			for (CommonDupontNode vo : vos) {
				if(vo.getData() instanceof RptIdxInfoRelVO) {
					idxNos.add(vo.getId());
					addIdxNos(vo.getChildren(), idxNos);
				}
			}
		}
	}

	private void putIdxNos(List<CommonDupontNode> vos, Map<String, Object> result, String dataUnit, String dataPrecision) {
		if (vos != null && vos.size() > 0) {
			for (CommonDupontNode vo : vos) {
				if(vo.getData() instanceof RptIdxInfoRelVO) {
					Object val = result.get(vo.getId());
					if (val == null) {
						vo.setValue("0");
						vo.setUnit(this.getUnit(dataUnit));
					}
					else{
						BigDecimal value = new BigDecimal(val.toString()).divide(FetchDataUtils.getdataChange(dataUnit));
						BigDecimal dataAccuracy = new BigDecimal(dataPrecision);
						value = value.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP);
						vo.setValue(value.toString());
						vo.setUnit(this.getUnit(dataUnit));
					}
					putIdxNos(vo.getChildren(), result, dataUnit, dataPrecision);
				}
			}
		}
	}

	public Map<String, Object> getIdxDetailList(Pager pager, String id,
			List<Map<String, Object>> searchArgs) {

		Map<String, Object> preView = Maps.newHashMap();
		RptIdxMeasureRel rel = this.getEntityByProperty(RptIdxMeasureRel.class,
				"id.indexNo", id);
		if (rel != null) {
			String datasetId = rel.getId().getDsId();
			RptSysModuleInfo dataset = this.rptDataSetDao
					.findModuleInfoById(datasetId);
			if (dataset == null) {
				return preView;
			}
			String indexStolCol = rel.getStoreCol();
			// 数据源
			BioneDsInfo ds = this.rptDataSetDao.findDataSourceById(dataset
					.getSourceId() == null ? "" : dataset.getSourceId());
			if (ds == null) {
				preView.put("errorMsg", "数据源不存在。");
				return preView;
			}
			// 数据集合法
			if (!notEmpty(dataset.getTableEnNm())) {
				preView.put("errorMsg", "未正确配置物理表。");
				return preView;
			}
			// 数据项
			Map<String, Object> params = Maps.newHashMap();
			params.put("setId", datasetId);
			params.put("isUse", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			List<RptSysModuleCol> cols = this.rptDataSetDao
					.getGridStruct(params);
			Map<String,RptSysModuleCol> colInfoMap = new HashMap<String, RptSysModuleCol>();
			if(cols!=null && cols.size()>0){
				for(RptSysModuleCol col : cols){
					colInfoMap.put(col.getEnNm(), col);
				}
			}
			if (!notEmpty(cols)) {
				preView.put("errorMsg", "数据集未配置有效数据项。");
				return preView;
			}
			StringBuilder colNamesBuff = new StringBuilder();// 字段名集合
			colNamesBuff.append("id");
			colNamesBuff.append(",");
			colNamesBuff.append(indexStolCol);
			String colNames = colNamesBuff.toString();

			// 查询数据
			Connection conn = this.getConnOfDs(ds);
			if (conn == null) {
				preView.put("errorMsg", "数据源配置不正确。");
				return preView;
			}
			String sql = "";
			int start = pager.getPageFirstIndex()+1;
			int end = start + pager.getPagesize()-1;
			// 加工过滤条件
			
			List<RptIdxDimRel> dimrels = this.getEntityListByProperty(RptIdxDimRel.class,
					"id.indexNo", id);
			
			Map<String,RptSysModuleCol> colMaps = new HashMap<String, RptSysModuleCol>();
			
			if(dimrels!=null && dimrels.size()>0){
				for(RptIdxDimRel dimrel : dimrels){
					colMaps.put(dimrel.getId().getDimNo(), colInfoMap.get(dimrel.getStoreCol()));
				}
			}

			PreparedStatement stm = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			RptSqlFilterVO vo = createFilter(colMaps, searchArgs);
			// 数据库表型
			sql = this.getSql(ds.getDriverId(), colNames,
					dataset.getTableEnNm(), start, end, vo.getFilterSql());
			try {
				stm = conn.prepareStatement(sql);
				for (int i = 0; i < vo.getParams().size(); i++) {
					stm.setObject(i+1, vo.getParams().get(i));
				}
				rs = stm.executeQuery();
				
			} catch (SQLException e) {
				e.printStackTrace();
				if (conn != null)
					JdbcUtils.closeConnection(conn);
				preView.put("errorMsg", "数据库中相关表结构发生变化。");
				return preView;
			}
			List<Map<String, String>> grid = Lists.newArrayList();
			try {
				int rnum =0;
				while (rs.next()) {
					if(ds.getDriverId().equals(HIVE_DATA_SOURCE)){
						if(rnum<=start && rnum>end)
							continue;
					}
					Map<String, String> row = Maps.newHashMap();
					row.put("name",
							rs.getString(1) == null ? "" : rs
									.getString(1));
					row.put("value",
							rs.getString(2) == null ? "" : rs
									.getString(2));
					grid.add(row);
				}
				preView.put("Rows", grid);
			} catch (SQLException e) {
				if (conn != null)
					JdbcUtils.closeConnection(conn);
				return preView;
			}
			try {
				String countsql = "select count(1) from " + dataset.getTableEnNm() +" where"+ vo.getFilterSql(); 
				pstm = conn.prepareStatement(countsql);
				for (int i = 0; i < vo.getParams().size(); i++) {
					pstm.setObject(i+1, vo.getParams().get(i));
				}
				rs = pstm.executeQuery();
				
			} catch (SQLException e) {
				e.printStackTrace();
				if (conn != null)
					JdbcUtils.closeConnection(conn);
				preView.put("errorMsg", "数据库中相关表结构发生变化。");
				return preView;
			}
			try {
				while (rs.next()) {
					String count = rs.getString(1) == null ? "" : rs
							.getString(1);
					preView.put("Total", count);
				}
			} catch (SQLException e) {
				if (conn != null)
					JdbcUtils.closeConnection(conn);
				return preView;
			}
		
			
			// 释放连接
			if (conn != null)
				JdbcUtils.closeConnection(conn);
		}
		return preView;
	}

	
	@SuppressWarnings({ "unchecked" })
	public Map<String, Object> getIdxList(Pager pager, String id, String type,
			String flag, List<Map<String, Object>> searchArgs, String json, String dataDate, String dataUnit, String dataPrecision)
			throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		switch (RelType.valueOf(type)) {
		case idx: {
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.endDate = ?1";
			RptIdxInfo info = this.baseDAO.findUniqueWithIndexParam(jql, id,"29991231");
			if (info != null) {
				CommonDupontNode vo = new CommonDupontNode();
				vo.setId(info.getId().getIndexNo());
				vo.setText(info.getIndexNm());
				vo.setIsExpand(true);
				vo.setData(info);
				vo.setColor(this.IDX_COLOR);
				Map<String,String> pars = new HashMap<String, String>();
				pars.put("type", "idx");
				vo.setParams(pars);
				Map<String, Object> map = getIdxRelMap(info, dataDate);
				Map<String, List<String>> idxRelMap = (Map<String, List<String>>) map.get("idxRelMap");
				Map<String, Set<String>> idxInfMap = (Map<String, Set<String>>) map.get("idxInfMap");
				Map<String, Object> idxInfoMap = (Map<String, Object>) map.get("idxInfoMap");
				List<CommonDupontNode> vos = new ArrayList<CommonDupontNode>();
				Map<String, Integer> IdxNos = new HashMap<String, Integer>();
				IdxNos.put(id, -1);
				if (flag.equals("inf")) {
					vos = getIdxInfInfo(id, idxInfMap, idxInfoMap, IdxNos, 0);
				} else {
					List<RptSysModuleInfo> syslists = this.getAllEntityList(RptSysModuleInfo.class, "setId", false);
					Map<String,RptSysModuleInfo> sysMap = new HashMap<String, RptSysModuleInfo>();
					if(syslists != null && syslists.size()>0){
						for(RptSysModuleInfo sys :syslists){
							sysMap.put(sys.getSetId(),sys);
						}
					}
					vos = getIdxRelInfo(id, idxRelMap, idxInfoMap, IdxNos, sysMap,0);
				}
				if(searchArgs != null){
					List<String> idxNos = new ArrayList<String>();
					idxNos.add(info.getId().getIndexNo());
					addIdxNos(vos, idxNos);
					Map<String, Object> result = createJson(json, idxNos, searchArgs);
					Object val = result.get(vo.getId());
					if (val == null) {
						vo.setValue("0");
						vo.setUnit(this.getUnit(dataUnit));
					}else{
						BigDecimal value = new BigDecimal(val.toString()).divide(FetchDataUtils.getdataChange(dataUnit));
						BigDecimal dataAccuracy = new BigDecimal(dataPrecision);
						value = value.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP);
						vo.setValue(value.toString());
						vo.setUnit(this.getUnit(dataUnit));
					}
					putIdxNos(vos, result, dataUnit, dataPrecision);
				}
				vo.setChildren(vos);
				params.put("idx", info);
				params.put("Rows", vo);
			}
			break;
		}
		case rpt: {
			Map<String, Object> paramss = new HashMap<String, Object>();
			paramss.put("rptId", id);
			RptMgrReportInfo rpt = this.rptMgrDao.getRptInfoByParams(paramss);
			if (rpt != null) {
				if (rpt.getRptType().equals(GlobalConstants4plugin.RPT_TYPE_OUTER)) {
					List<RptIdxInfoRelVO> list = this.rptAlyDao
							.listIdxByRptId(id);
					CommonDupontNode node = new CommonDupontNode();
					node.setId(rpt.getRptNum());
					node.setText(rpt.getRptNm());
					node.setData(rpt);
					node.setColor(this.RPT_COLOR);
					node.setIsExpand(true);
					Map<String,String> prs = new HashMap<String, String>();
					prs.put("type", "rpt");
					node.setParams(prs);
					List<CommonDupontNode> vos = new ArrayList<CommonDupontNode>();
					for(RptIdxInfo  rel : list){
						CommonDupontNode vo = new CommonDupontNode();
						vo.setId(rel.getId().getIndexNo());
						vo.setText(rel.getIndexNm());
						vo.setData(rel);
						vo.setIsExpand(false);
						Map<String,String> prss = new HashMap<String, String>();
						prss.put("type", "idx");
						vo.setParams(prss);
						vos.add(vo);
					}
					node.setChildren(vos);
					params.put("Rows", node);
				} else {
					CommonDupontNode node = new CommonDupontNode();
					node.setId(rpt.getRptNum());
					node.setText(rpt.getRptNm());
					node.setData(rpt);
					node.setColor(this.RPT_COLOR);
					node.setIsExpand(true);
					Map<String,String> prs = new HashMap<String, String>();
					prs.put("type", "rpt");
					node.setParams(prs);
					Map<String, Object> map = getRptIdxRelMap();
					Map<String, Set<String>> idxRelMap = (Map<String, Set<String>>) map
							.get("idxRelMap");
					Map<String, Object> map1 = getIdxRelMap(null, null);
					Map<String, Object> idxInfoMap = (Map<String, Object>) map1
							.get("idxInfoMap");
					Map<String, List<String>> idxRelMap1 = (Map<String, List<String>>) map1
							.get("idxRelMap");
					List<String> rptIdxs = this.rptAlyDao.listRptIdxByRptId(id);
					List<CommonDupontNode> vos = new ArrayList<CommonDupontNode>();
					List<String> idx = new ArrayList<String>();
					if (rptIdxs != null && rptIdxs.size() > 0) {
						for (String rptIdx : rptIdxs) {
							Set<String> idxNos = idxRelMap.get(rptIdx);
							if(idxNos != null && idxNos.size() > 0){
								for(String idxNo : idxNos){
									idx.add(idxNo);
								}
							}
						}
					}
					Map<String, Object> valuemap = new HashMap<String, Object>();
					if (idx.size() <= 0) {
						idx = null;
					}
					valuemap.put("idxNos", ReBuildParam.toDbList(idx));
					List<RptIdxInfoRelVO> lists = this.rptAlyDao
							.getRptIdxByRptId(valuemap);
					
					if(lists !=null && lists.size() > 0){
						for(RptIdxInfo  rel : lists){
							CommonDupontNode vo = new CommonDupontNode();
							vo.setId(rel.getId().getIndexNo());
							vo.setText(rel.getIndexNm());
							vo.setData(rel);
							vo.setColor(this.IDX_COLOR);
							vo.setIsExpand(false);
							Map<String,String> prss = new HashMap<String, String>();
							prss.put("type", "idx");
							vo.setParams(prss);
							List<CommonDupontNode> cvos = new ArrayList<CommonDupontNode>();
							Map<String, Integer> IdxNos = new HashMap<String, Integer>();
							IdxNos.put(rel.getId().getIndexNo(), -1);
							List<RptSysModuleInfo> syslists = this.getAllEntityList(RptSysModuleInfo.class, "setId", false);
							Map<String,RptSysModuleInfo> sysMap = new HashMap<String, RptSysModuleInfo>();
							if(syslists != null && syslists.size()>0){
								for(RptSysModuleInfo sys :syslists){
									sysMap.put(sys.getSetId(),sys);
								}
							}
							cvos = getIdxRelInfo(rel.getId().getIndexNo(), idxRelMap1, idxInfoMap, IdxNos, sysMap,0);
							vo.setChildren(cvos);
							vos.add(vo);
						}
					}
					node.setChildren(vos);
					params.put("rpt", rpt);
					params.put("Rows", node);

				}
			}
			break;
		}
		case dim: {
			break;
		}
		case model: {
			RptSysModuleInfo sys = this.getEntityById(RptSysModuleInfo.class, id);
			if(sys != null){
				CommonDupontNode vo = new CommonDupontNode();
				vo.setId(sys.getTableEnNm());
				vo.setText(sys.getSetNm());
				vo.setIsExpand(true);
				vo.setData(sys);
				vo.setColor(this.SYS_COLOR);
				Map<String,String> pars = new HashMap<String, String>();
				pars.put("type", "sys");
				vo.setParams(pars);
				String jql = "select distinct idx.id.indexNo,idx.indexNm from RptIdxInfo idx ,RptIdxMeasureRel rel where idx.endDate = ?0 and idx.isRptIndex = ?1 and rel.id.indexNo = idx.id.indexNo and rel.id.indexVerId = idx.id.indexVerId and rel.id.dsId = ?2";
				List<Object[]> idxs = this.baseDAO.findWithIndexParam(jql, "29991231","N",id);
				if(idxs != null && idxs.size() > 0){
					List<CommonDupontNode> nodes = new ArrayList<CommonDupontNode>();
					Map<String, Object> map = getIdxRelMap(null, null);
					Map<String, Set<String>> idxInfMap = (Map<String, Set<String>>) map
							.get("idxInfMap");
					Map<String, Object> idxInfoMap = (Map<String, Object>) map
							.get("idxInfoMap");
					for(Object[] idx : idxs){
						CommonDupontNode cnode = new CommonDupontNode();
						cnode.setId(idx[0].toString());
						cnode.setData(idx);
						cnode.setText(idx[1].toString());
						cnode.setColor(this.IDX_COLOR);
						cnode.setIsExpand(false);
						Map<String,String> parss = new HashMap<String, String>();
						parss.put("type", "idx");
						cnode.setParams(parss);
						Map<String, Integer> IdxNos = new HashMap<String, Integer>();
						IdxNos.put(idx[0].toString(), -1);
						List<CommonDupontNode> vos = getIdxInfInfo(idx[0].toString(), idxInfMap, idxInfoMap, IdxNos, 0);
						cnode.setChildren(vos);
						nodes.add(cnode); 
					}
					vo.setChildren(nodes);
				}
				params.put("sys", sys);
				params.put("Rows", vo);
			}
			break;
		}
		}
		return params;
	}
	
	@SuppressWarnings("unchecked")
	private RptSqlFilterVO createFilter(Map<String,RptSysModuleCol> colMaps,
			List<Map<String, Object>> searchArgs) {
		RptSqlFilterVO vo =new RptSqlFilterVO();
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String filter = " 1=1 ";
		if (searchArgs != null) {
			List<Map<String, Object>> argArr = searchArgs;
			for (int i = 0; i < argArr.size(); i++) {
				Map<String, Object> arrObjTmp = argArr.get(i);
				boolean likeFlag = false;
				String filterSql=colMaps.get(arrObjTmp.get("name")).getEnNm() + " = ?";
				if (arrObjTmp.containsKey("begin")) {
					filterSql = colMaps.get(arrObjTmp.get("name")).getEnNm() + " >= ?";
				}
				if (arrObjTmp.containsKey("end")) {
					filterSql = colMaps.get(arrObjTmp.get("name")).getEnNm() + " <= ?";
				}
				if (arrObjTmp.containsKey("op")) {
					if(arrObjTmp.get("op").equals("like"))
						likeFlag = true;
					filterSql += colMaps.get(arrObjTmp.get("name")).getEnNm() + " " + arrObjTmp.get("op") + " ? ";
				}
				String t= colMaps.get(arrObjTmp.get("name")).getDbType();
				if (arrObjTmp.get("value") instanceof String) {
					Object p = null;
					String value = arrObjTmp.get("value").toString();
					try {
						if (GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE.equals(t)) {
							long tt = sdf.parse(value).getTime();
							p = new Timestamp(tt);
						} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER.equals(t)) {
							p = new BigDecimal(value);
						} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_TEXT.equals(t)) {
							p = likeFlag ? ("%" + value + "%") : value;
						} else {
							p = value;
						}
					} catch (Exception e) {
						if (GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE.equals(t)) {
							p = new Timestamp(new Date().getTime());
						} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER.equals(t)) {
							p = new BigDecimal(0);
						} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_TEXT.equals(t)) {
							p = likeFlag ? ("%" + value + "%") : value;
						} else {
							p = value;
						}
					}
					filter += " and " +filterSql;
					vo.addParam(p);
				} else {
					List<String> values = (List<String>) arrObjTmp.get("value");
					if (values != null && values.size() > 0){
						int nu=0;
						boolean hasFlag = false;
						String subFilter = "";
						for (String value : values){
							if(notEmpty(value)){
								hasFlag =true;
								Object p = null;
								try {
									if (GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE.equals(t)) {
										long tt = sdf.parse(value).getTime();
										p = new Timestamp(tt);
									} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER.equals(t)) {
										p = new BigDecimal(value);
									} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_TEXT.equals(t)) {
										p = likeFlag ? ("%" + value + "%") : value;
									} else {
										p = value;
									}
								} catch (Exception e) {
									if (GlobalConstants4plugin.LOGIC_DATA_TYPE_DATE.equals(t)) {
										p = new Timestamp(new Date().getTime());
									} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_NUMBER.equals(t)) {
										p = new BigDecimal(0);
									} else if (GlobalConstants4plugin.LOGIC_DATA_TYPE_TEXT.equals(t)) {
										p = likeFlag ? ("%" + value + "%") : value;
									} else {
										p = value;
									}
								}
								vo.addParam(p);
								if(nu<values.size()-1)
									subFilter += "( "+ filterSql + ") or ";
								else
									subFilter += "( "+ filterSql + ")";
							}
							
							nu++;
						}
						if(hasFlag){
							filter += " and ( "+ subFilter + " )";
						}
						
					}
				}
			}
		}
		vo.setFilterSql(filter);
		return vo;
		
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> createJson(String json, List<String> idxNos,
			List<Map<String, Object>> searchArgs) {
		Map<String, Object> result = new HashMap<String, Object>();
		String returnJson = "";
		JSONObject jsonObject = new JSONObject();
		List<Map<String, Object>> columnMaps = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> searchParams = searchArgs;
		if (idxNos != null && idxNos.size() > 0) {
			for (String idxNo : idxNos) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ColumNo", idxNo);
				params.put("IndexNo", idxNo);
				params.put("SearchArg", JSON.toJSON(searchParams).toString());
				columnMaps.add(params);
			}
		}
		if (StringUtils.isNotBlank(json)) {
			jsonObject = JSON.parseObject(json);
		} else {
			boolean orgDimFlag = false;
			Set<String> dimNos = new HashSet<String>();
			if (searchArgs != null && searchArgs.size() > 0) {
				for (Map<String, Object> arg : searchArgs) {
					dimNos.add(arg.get("DimNo").toString());
					if (arg.get("DimNo").equals("ORG")) {
						List<String> values = new ArrayList<String>();
						if(arg.get("Value") instanceof String){
							values.add(arg.get("Value").toString());
						}
						else{
							values = (List<String>) arg.get("Value");
						}
						orgDimFlag = true;
						if (values != null && values.size() > 0) {
							for (String value : values) {
								if (StringUtils.isNotBlank(value)) {
									orgDimFlag = false;
									break;
								}
							}
						}
					}
				}
			}
			jsonObject.put("DimNo", dimNos);
			jsonObject.put("QueryType", "index");
			if (orgDimFlag) {
				List<Map<String, Object>> searchs = new ArrayList<Map<String, Object>>();
				Map<String, Object> search = new HashMap<String, Object>();
				search.put("DimNo", "ORG");
				search.put("Op", "IN");
				search.put("Value", "getChildDimVal('ORG','"
						+ BioneSecurityUtils.getCurrentUserInfo().getOrgNo()
						+ "')");
				searchs.add(search);
				jsonObject.put("SearchArg", searchs);
			}

		}
		jsonObject.put("Colums", columnMaps);
		if (columnMaps.size() <= 0) {
			return result;
		}
		json = jsonObject.toJSONString();
		Object obj = null;
		try {
			obj = CommandRemote.sendSync(json, CommandRemoteType.QUERY);
		} catch (Throwable e) {
			return result;
		}
		if (obj instanceof String)
			returnJson = obj.toString();
		else {
			return result;
		}
		try {
			JSONObject jsonMap = JSON.parseObject(returnJson);
			String rs = jsonMap.get("Code").toString();
			if ("0000".equals(rs)) {
				List<Map<String, Object>> tabIdxLists = (List<Map<String, Object>>) jsonMap
						.get("Msg");
				if (tabIdxLists != null && tabIdxLists.size() > 0) {
					Map<String, Object> tabIdx = tabIdxLists.get(0);
					for (String key : tabIdx.keySet()) {
						if (idxNos.contains(key)) {
							result.put(key, tabIdx.get(key));
						}
					}
				}
			}
		} catch (Exception e) {
			return result;
		}
		return result;
	}

	/**
	 * 根据传入的指标编号，进行血缘查询来源指标
	 * @param idxNo
	 * @param dataDate
	 * @param idxs
	 * @return
	 */
	private List<RptIdxInfo> getIdxBySrc(String idxNo, String dataDate, List<RptIdxInfo> idxs){
		if(StringUtils.isNotBlank(idxNo) && StringUtils.isNotBlank(dataDate)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("indexNo", idxNo);
			map.put("dataDate", dataDate);
			RptIdxInfo info = this.idxInfoDao.getIdxDsInfo(map);
			if(null != info) {
				idxs.add(info);
				if(StringUtils.isNotBlank(info.getSrcIndexNo())) {
					String index[] = StringUtils.split(info.getSrcIndexNo(), ",");
					for (int i = 0; i < index.length; i++) {
						idxs = getIdxBySrc(index[i], dataDate, idxs);
					}
				}
			}
		}
		return idxs;
	}
	
	
	/**
	 * 查询指标间关系
	 * 
	 * @return idxRelMap：INDEX_NO=>SRC_INDEX_NO的List的Map<br>
	 *         idxInfMap：INDEX_NO=>以KEY为SRC_INDEX_NO的INDEX_NO的Set的Map<br>
	 *         idxInfoMap：INDEX_NO=>RptIdxInfo对象的Map
	 */
	private Map<String, Object> getIdxRelMap(RptIdxInfo info, String dataDate) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, List<String>> idxRelMap = new HashMap<String, List<String>>();
		Map<String, Object> idxInfoMap = new HashMap<String, Object>();
		Map<String, Set<String>> idxInfMap = new HashMap<String, Set<String>>();
		List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
		if((null != info) && StringUtils.isNotBlank(dataDate)) {//给定指标向下追溯
			idxs = getIdxBySrc(info.getId().getIndexNo(), dataDate, idxs);
		}else {
			idxs = this.idxInfoDao.listIdxDsInfo(null);
		}
		if (CollectionUtils.isNotEmpty(idxs)) {
			for (RptIdxInfo idx : idxs) {
				idxInfoMap.put(idx.getId().getIndexNo(), idx);
				String index[] = StringUtils.split(idx.getSrcIndexNo(), ",");
				if (ArrayUtils.isNotEmpty(index)) {
					Set<String> indexNos = new HashSet<String>();
					for (int i = 0; i < index.length; i++) {
						indexNos.add(index[i]);
						Set<String> relindex = idxInfMap.get(index[i]);
						if (relindex != null) {
							if(!relindex.contains(idx.getId().getIndexNo())){
								relindex.add(idx.getId().getIndexNo());
							}
						} else {
							relindex = new HashSet<String>();
							relindex.add(idx.getId().getIndexNo());
							idxInfMap.put(index[i], relindex);
						}
					}
					idxRelMap.put(idx.getId().getIndexNo(), new ArrayList<String>(indexNos));
				}
			}
		}
		map.put("idxRelMap", idxRelMap);
		map.put("idxInfMap", idxInfMap);
		map.put("idxInfoMap", idxInfoMap);
		return map;
	}

	/**
	 * 查询指标和模板间关系
	 * 
	 * @return idxRelMap：INDEX_NO=>SRC_INDEX_NO的Set的Map<br>
	 *         idxInfMap：INDEX_NO=>以KEY为SRC_INDEX_NO的INDEX_NO的Set的Map<br>
	 *         idxInfoMap：INDEX_NO=>(INDEXNO, SRCINDEXNO, TEMPLATEID的Set)的Map
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getRptIdxRelMap() throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Set<String>> idxRelMap = new HashMap<String, Set<String>>();
		Map<String, Set<String>> idxInfMap = new HashMap<String, Set<String>>();
		Map<String, Object> idxInfoMap = new HashMap<String, Object>();
		List<Map<String, Object>> idxs = this.jdbcBaseDAO
				.find("select obj.INDEX_NO as indexNo,obj.SRC_INDEX_NO as srcIndexNo, obj.TEMPLATE_ID as templateId from RPT_IDX_INFO obj",
						new Object[] {});
		if (CollectionUtils.isNotEmpty(idxs)) {
			for (Map<String, Object> idx : idxs) {
				String templateId = String.valueOf(idx.get("TEMPLATEID"));
				if (templateId == null || "null".equals(templateId)) {
					idx.remove("TEMPLATEID");
				} else {
					Set<String> templateIdSet = new HashSet<String>();
					templateIdSet.add(templateId);
					idx.put("TEMPLATEID", templateIdSet);
				}
				idxInfoMap.put(String.valueOf(idx.get("INDEXNO")), idx);
				String index[] = StringUtils.split(
						String.valueOf(idx.get("SRCINDEXNO")), ",");
				if (ArrayUtils.isNotEmpty(index)) {
					Set<String> indexNos = new HashSet<String>();
					for (int i = 0; i < index.length; i++) {
						indexNos.add(index[i]);
						Set<String> relindex = idxInfMap.get(index[i]);
						if (relindex != null) {
							if(!relindex.contains(idx.get("INDEXNO"))){
								relindex.add(String.valueOf(idx.get("INDEXNO")));
							}
						} else {
							relindex = new HashSet<String>();
							relindex.add(String.valueOf(idx.get("INDEXNO")));
							idxInfMap.put(index[i], relindex);
						}
					}
					idxRelMap.put(String.valueOf(idx.get("INDEXNO")), indexNos);
				}
			}
		}
		idxs = this.jdbcBaseDAO
				.find("select obj.INDEX_NO as indexNo,obj2.CFG_ID as templateId from RPT_MGR_MODULE_IDX_REL obj,RPT_MGR_REPORT_INFO obj2 where obj.RPT_ID=obj2.RPT_ID and obj2.RPT_TYPE='01' and obj2.CFG_ID is not null",
						new Object[] {});
		for (int i = 0; i < idxs.size(); i ++) {
			Map<String, Object> idx = (Map<String, Object>)idxInfoMap.get(idxs.get(i).get("INDEXNO"));
			if (idx == null) {
				continue;
			}
			Set<String> templateIdSet = (Set<String>)idx.get("TEMPLATEID");
			if (templateIdSet == null) {
				templateIdSet = new HashSet<String>();
				idx.put("TEMPLATEID", templateIdSet);
			}
			templateIdSet.add(String.valueOf(idxs.get(i).get("TEMPLATEID")));
		}
		map.put("idxRelMap", idxRelMap);
		map.put("idxInfMap", idxInfMap);
		map.put("idxInfoMap", idxInfoMap);
		return map;
	}

	private List<CommonDupontNode> getIdxRelInfo(String id,
			Map<String, List<String>> idxRelMap,
			Map<String, Object> idxInfoMap, Map<String, Integer> relIdxNos,Map<String,RptSysModuleInfo> sysMap,
			int level) {
		List<CommonDupontNode> vos = new ArrayList<CommonDupontNode>();
		List<String> idxNos = idxRelMap.get(id);
		if (idxNos != null && idxNos.size() > 0) {
			for (String idxNo : idxNos) {
				if (relIdxNos.get(idxNo) == null
						|| relIdxNos.get(idxNo) >= level) {
					relIdxNos.put(idxNo, level);
				}
			}
			for (String idxNo : idxNos) {
				if (relIdxNos.get(idxNo) == null
						|| relIdxNos.get(idxNo) >= level) {
					CommonDupontNode vo = new CommonDupontNode();
					RptIdxInfoRelVO rel = new RptIdxInfoRelVO();
					Object vorel = idxInfoMap.get(idxNo);
					if (vorel != null) {
						BeanUtils.copy(vorel, rel);
						vo.setId(rel.getId().getIndexNo());
						vo.setText(rel.getIndexNm());
						vo.setData(rel);
						vo.setIsExpand(false);
						vo.setColor(this.IDX_COLOR);
						Map<String,String> pars = new HashMap<String, String>();
						pars.put("type", "idx");
						vo.setParams(pars);
						vo.setChildren(getIdxRelInfo(idxNo, idxRelMap,
								idxInfoMap, relIdxNos, sysMap ,level + 1));
						vos.add(vo);
					}
				}
			}
		}
		else{
			RptIdxInfo info = new RptIdxInfo();
			Object vorel = idxInfoMap.get(id);
			BeanUtils.copy(vorel, info);
			CommonDupontNode vo = new CommonDupontNode();
			RptSysModuleInfo sys = sysMap.get(info.getRemark());
			if(null != sys) {
				vo.setId(sys.getTableEnNm());
				vo.setText(sys.getSetNm());
				vo.setIsExpand(false);
				vo.setData(sys);
				vo.setColor(this.SYS_COLOR);
				Map<String,String> pars = new HashMap<String, String>();
				pars.put("type", "sys");
				vo.setParams(pars);
				vos.add(vo);
			}
		}
		return vos;
	}

	/**
	 * 构造以id为根的指标树
	 * 
	 * @param id 为根的指标
	 * @param idxInfMap INDEX_NO=>以KEY为SRC_INDEX_NO的INDEX_NO的List的Map<br>
	 * @param idxInfoMap INDEX_NO=>RptIdxInfo对象的Map<br>
	 * @param infIdxNos 返回的INDEX_NO=>指标树中级别的Map
	 * @param level 当前指标树中级别
	 * @return 指标树
	 */
	private List<CommonDupontNode> getIdxInfInfo(String id,
			Map<String, Set<String>> idxInfMap,
			Map<String, Object> idxInfoMap, Map<String, Integer> infIdxNos,
			int level) {
		List<CommonDupontNode> vos = new ArrayList<CommonDupontNode>();
		Set<String> idxNos = idxInfMap.get(id);
		if (idxNos != null && idxNos.size() > 0) {
			for (String idxNo : idxNos) {
				if (infIdxNos.get(idxNo) == null
						|| infIdxNos.get(idxNo) >= level) {
					infIdxNos.put(idxNo, level);
				}
			}
			for (String idxNo : idxNos) {
				if (infIdxNos.get(idxNo) == null
						|| infIdxNos.get(idxNo) >= level) {
					CommonDupontNode vo = new CommonDupontNode();
					Object voInfo = idxInfoMap.get(idxNo);
					if (voInfo != null) {
						RptIdxInfo rel = new RptIdxInfo();
						BeanUtils.copy(idxInfoMap.get(idxNo), rel);
						if (rel.getIsRptIndex() != null
								&& rel.getIsRptIndex().equals("N")) {
							vo.setId(rel.getId().getIndexNo());
							vo.setText(rel.getIndexNm());
							vo.setData(rel);
							vo.setIsExpand(false);
							vo.setColor(this.IDX_COLOR);
							vo.setChildren(getIdxInfInfo(idxNo, idxInfMap,
									idxInfoMap, infIdxNos, level + 1));
							Map<String,String> pars = new HashMap<String, String>();
							pars.put("type", "idx");
							vo.setParams(pars);
							vos.add(vo);
						}
					}
				}
			}
		}
		return vos;
	}

	/**
	 * 
	 * 构造(INDEX_NO、SRC_INDEX_NO、TEMPLATE_ID、children)列表
	 * 
	 * @param id 为根的指标
	 * @param idxInfMap INDEX_NO=>以KEY为SRC_INDEX_NO的INDEX_NO的Set的Map<br>
	 * @param idxInfoMap INDEX_NO=>(INDEX_NO、SRC_INDEX_NO、TEMPLATE_ID)的Map
	 * @param infIdxNos 返回的INDEX_NO=>指标树中级别的Map
	 * @param level 当前指标树中级别
	 * @return (INDEX_NO、SRC_INDEX_NO、TEMPLATE_ID、children)列表
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getRptIdxInfInfo(String id,
			Map<String, Set<String>> idxInfMap,
			Map<String, Object> idxInfoMap, Map<String, Integer> infIdxNos,
			int level) {
		List<Map<String, Object>> vos = new ArrayList<Map<String, Object>>();
		Set<String> idxNos = idxInfMap.get(id);
		if (CollectionUtils.isNotEmpty(idxNos)) {
			for (String idxNo : idxNos) {
				if (infIdxNos.get(idxNo) == null
						|| infIdxNos.get(idxNo) >= level) {
					infIdxNos.put(idxNo, level);
				}
			}
			for (String idxNo : idxNos) {
				if (infIdxNos.get(idxNo) == null
						|| infIdxNos.get(idxNo) >= level) {
					Map<String, Object> voInfo = (Map<String, Object>) idxInfoMap
							.get(idxNo);
					if (voInfo != null) {
						voInfo.put(
								"children",
								getRptIdxInfInfo(idxNo, idxInfMap, idxInfoMap,
										infIdxNos, level + 1));
						vos.add(voInfo);
					}
				}
			}
		}
		return vos;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private List<Map<String, Object>> getRptIdxRelInfo(String id,
			Map<String, List<String>> idxRelMap,
			Map<String, Object> idxInfoMap, Map<String, Integer> relIdxNos,
			int level) {
		List<Map<String, Object>> vos = new ArrayList<Map<String, Object>>();
		List<String> idxNos = idxRelMap.get(id);
		if (idxNos != null && idxNos.size() > 0) {
			for (String idxNo : idxNos) {
				if (relIdxNos.get(idxNo) == null
						|| relIdxNos.get(idxNo) >= level) {
					relIdxNos.put(idxNo, level);
				}
			}
			for (String idxNo : idxNos) {
				if (relIdxNos.get(idxNo) == null
						|| relIdxNos.get(idxNo) >= level) {
					Map<String, Object> vorel = (Map<String, Object>) idxInfoMap
							.get(idxNo);
					if (vorel != null) {
						vorel.put(
								"children",
								getRptIdxRelInfo(idxNo, idxRelMap, idxInfoMap,
										relIdxNos, level + 1));
						vos.add(vorel);
					}
				}
			}
		}
		return vos;
	}

	@SuppressWarnings("incomplete-switch")
	public Map<String, Object> getDimList(String id, String type) {
		Map<String, Object> params = new HashMap<String, Object>();

		switch (RelType.valueOf(type)) {
		case idx: {

			break;
		}
		case rpt: {
			Map<String, Object> paramss = new HashMap<String, Object>();
			paramss.put("rptId", id);
			RptMgrReportInfo rpt = this.rptMgrDao.getRptInfoByParams(paramss);
			
			if (rpt != null) {
				CommonDupontNode node = new CommonDupontNode();
				node.setId(rpt.getRptId());
				node.setText(rpt.getRptNm());
				node.setData(rpt);
				node.setIsExpand(true);
				Map<String,String> prs = new HashMap<String, String>();
				prs.put("type", "rpt");
				node.setColor(this.RPT_COLOR);
				node.setParams(prs);
				if (rpt.getRptType().equals(GlobalConstants4plugin.RPT_TYPE_OUTER)) {
					List<RptDimTypeInfo> page = this.rptAlyDao
							.listDimByRptId(id);
					if(page != null && page.size()>0){
						List<CommonDupontNode> nodes = new ArrayList<CommonDupontNode>();
						for(RptDimTypeInfo dim : page){
							CommonDupontNode cnode = new CommonDupontNode();
							cnode.setId(dim.getDimTypeNo());
							cnode.setText(dim.getDimTypeNm());
							cnode.setColor(this.DIM_COLOR);
							cnode.setIsExpand(false);
							cnode.setData(dim);
							Map<String,String> prss = new HashMap<String, String>();
							prss.put("type", "dim");
							cnode.setParams(prss);
							nodes.add(cnode);
						}
						node.setChildren(nodes);
					}
					params.put("Rows", node);
					params.put("rpt", rpt);
				} else {
					String jql = "select dim from RptDesignQueryDim dim,RptMgrReportInfo info where info.cfgId = dim.id.templateId and info.rptId = ?0";
					List<RptDesignQueryDim> dims = this.baseDAO.findWithIndexParam(jql, id);
					List<String> dimNos  = new ArrayList<String>();
					if(dims!=null && dims.size()>0){
						RptDesignQueryDim dim = dims.get(0);
						if(StringUtils.isNotBlank(dim.getQueryDim()))
							dimNos = com.yusys.bione.comp.utils.ArrayUtils.asList(dim.getQueryDim(), ",");
					}
					jql = "select distinct dim.dimTypeNo from RptDesignSourceTabdim dim,RptMgrReportInfo info where info.cfgId = dim.id.templateId and info.rptId = ?0";
					List<String> tabdims = this.baseDAO.findWithIndexParam(jql, id);
					dimNos.addAll(tabdims);
					if(dimNos.size()>0){
						List<RptDimTypeInfo> page = this.rptDimDao
								.findDimTypeInfoByIds(dimNos);
						params.put("rpt", rpt);
						List<CommonDupontNode> nodes = new ArrayList<CommonDupontNode>();
						for(RptDimTypeInfo dim : page){
							CommonDupontNode cnode = new CommonDupontNode();
							cnode.setId(dim.getDimTypeNo());
							cnode.setText(dim.getDimTypeNm());
							cnode.setColor(this.DIM_COLOR);
							cnode.setIsExpand(false);
							cnode.setData(dim);
							Map<String,String> prss = new HashMap<String, String>();
							prss.put("type", "dim");
							cnode.setParams(prss);
							nodes.add(cnode);
						}
						node.setChildren(nodes);
						params.put("Rows", node);
					}
					else{
						params.put("rpt", rpt);
						params.put("Rows", node);
					}
					
				}
			}

			break;
		}
		case dim: {
			break;
		}
		}
		return params;
	}

	@SuppressWarnings({ "unchecked", "incomplete-switch" })
	public Map<String, Object> getRptList(String id, String type)
			throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		switch (RelType.valueOf(type)) {
		case idx: {
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.endDate = ?1";
			RptIdxInfo idx = this.baseDAO.findUniqueWithIndexParam(jql, id,"29991231");
			CommonDupontNode node = new CommonDupontNode();
			node.setId(idx.getId().getIndexNo());
			node.setText(idx.getIndexNm());
			node.setIsExpand(true);
			node.setData(idx);
			node.setColor(IDX_COLOR);
			Map<String,String> prs = new HashMap<String, String>();
			prs.put("type", "idx");
			node.setParams(prs);
			
			/*
			Map<String, Object> map = getIdxRelMap();
			Map<String, List<String>> idxInfMap = (Map<String, List<String>>) map
					.get("idxInfMap");
			Map<String, Object> idxInfoMap = (Map<String, Object>) map
					.get("idxInfoMap");
			Map<String, Integer> IdxNos = new HashMap<String, Integer>();
			IdxNos.put(id, -1);
			getIdxInfInfo(id, idxInfMap, idxInfoMap, IdxNos, 0);
			IdxNos.remove(id);
			*/
			boolean flag = false;
			Map<String, Object> map = getRptIdxRelMap();
			Map<String, Set<String>> idxInfMap = (Map<String, Set<String>>) map.get("idxInfMap");
			Map<String, Object> idxInfoMap = (Map<String, Object>) map.get("idxInfoMap");
			Map<String, Integer> rptIdxNos = new HashMap<String, Integer>();
			rptIdxNos.put(id, -1);
			getRptIdxInfInfo(id, idxInfMap, idxInfoMap, rptIdxNos, 0);
			Map<String, Object> paramss = new HashMap<String, Object>();
			List<String> rptIdxs = new ArrayList<String>();
			/*
			List<String> ids = new ArrayList<String>();
			if (IdxNos.keySet().size() > 0) {
				ids = new ArrayList<String>(IdxNos.keySet());
				flag = true;
			}
			if (ids.size() <= 0) {
				ids = null;
			}
			paramss.put("idxNos", ReBuildParam.toDbList(ids));
			*/
			Set<String> templateIds = new HashSet<String>();
			if (MapUtils.isNotEmpty(rptIdxNos)) {
				rptIdxs = new ArrayList<String>(rptIdxNos.keySet());
				for (String rptIdx : rptIdxs) {
					Map<String, Object> info = (Map<String, Object>) idxInfoMap
							.get(rptIdx);
					if (info.containsKey("TEMPLATEID")) {
						templateIds.addAll((Set<String>)info.get("TEMPLATEID"));
					}
				}
				flag = true;
			}
			if (templateIds.size() <= 0) {
				templateIds = null;
			}
			paramss.put("idxNos", ReBuildParam.toDbList(rptIdxs));
			paramss.put("templateIds", templateIds);
			List<RptMgrInfoVO> page =  this.rptAlyDao
					.listRptByRptIdx(paramss);
			if (flag) {
				params.put("idx", idx);
				List<CommonDupontNode> nodes = new ArrayList<CommonDupontNode>();
				for(RptMgrInfoVO pa : page){
					CommonDupontNode cnode = new CommonDupontNode();
					cnode.setId(pa.getRptNum());
					cnode.setText(pa.getRptNm());
					cnode.setData(pa);
					cnode.setIsExpand(false);
					cnode.setColor(this.RPT_COLOR);
					Map<String,String> pars = new HashMap<String, String>();
					pars.put("type", "rpt");
					cnode.setParams(pars);
					nodes.add(cnode);
				}
				node.setChildren(nodes);
				params.put("Rows", node);
				
			} else {
				params.put("idx", idx);
				params.put("Rows", node);
			}

			break;
		}
		case rpt: {
			break;
		}
		case dim: {
			RptDimTypeInfo info = this.getEntityByProperty(RptDimTypeInfo.class,
					"dimTypeNo", id);
			CommonDupontNode node = new CommonDupontNode();
			node.setId(info.getDimTypeNo());
			node.setText(info.getDimTypeNm());
			node.setIsExpand(true);
			node.setData(info);
			node.setColor(this.DIM_COLOR);
			Map<String,String> pars = new HashMap<String, String>();
			pars.put("type", "dim");
			node.setParams(pars);
			List<RptMgrInfoVO> page =  this.rptAlyDao
					.listRptByRptDim(id);
			page.addAll(this.getAllMgrInfo(id));
			if(page != null && page.size() > 0){
				List<CommonDupontNode> nodes = new ArrayList<CommonDupontNode>();
				for(RptMgrInfoVO vo : page){
					CommonDupontNode cnode = new CommonDupontNode();
					cnode.setId(vo.getRptNum());
					cnode.setText(vo.getRptNm());
					cnode.setIsExpand(false);
					cnode.setData(vo);
					cnode.setColor(this.RPT_COLOR);
					Map<String,String> parss = new HashMap<String, String>();
					parss.put("type", "rpt");
					cnode.setParams(parss);
					nodes.add(cnode);
				}
				node.setChildren(nodes);
			}
			params.put("dim", info);
			params.put("Rows", node);
			break;
		}
		}
		return params;
	}
	
	private List<RptMgrInfoVO> getAllMgrInfo(String id){
		List<RptMgrInfoVO> page = new ArrayList<RptMgrInfoVO>();
		List<String> cfgIds = new ArrayList<String>();
		String jql ="select tmp from RptDesignQueryDim tmp where tmp.queryDim like ?0";
		List<RptDesignQueryDim> tmps = this.baseDAO.findWithIndexParam(jql, "%"+id+"%");
		if(tmps != null && tmps.size() > 0){
			for(RptDesignQueryDim tmp : tmps){
				String queryDim = tmp.getQueryDim();
				List<String> dim = com.yusys.bione.comp.utils.ArrayUtils.asList(queryDim, ",");
				if(dim.contains(id)){
					cfgIds.add(tmp.getId().getTemplateId());
				}
			}
		}
		jql ="select distinct tmp.id.templateId from RptDesignSourceTabdim tmp where dimTypeNo = ?0 ";
		List<String> tmpIds = this.baseDAO.findWithIndexParam(jql, id);
		cfgIds.addAll(tmpIds);
		Map<String,Object> pars = new HashMap<String, Object>();
		if(cfgIds != null && cfgIds.size() > 0){
			pars.put("cfgIds", ReBuildParam.splitLists(cfgIds));
			page = this.rptAlyDao.listRptByRptCfg(pars);
		}
		return page; 
		
	}

	/**
	 * 获取指定数据源的一个连接
	 * 
	 * @param ds
	 *            数据源
	 * @return
	 */
	private Connection getConnOfDs(BioneDsInfo ds) {
		if (ds == null)
			return null;
		BioneDriverInfo dv = this.rptDataSetDao.findDriverInfoById(ds
				.getDriverId());
		if (dv == null)
			return null;
		// 获取数据源连接
		String driverName = dv.getDriverName();
		String url = ds.getConnUrl();
		String user = ds.getConnUser();
		String pwd = ds.getConnPwd();
		Connection conn = null;
		if (notEmpty(driverName) && notEmpty(url) && notEmpty(user)) {
			try {
				// 注册驱动
				Driver driver = (Driver) Class.forName(driverName)
						.newInstance();
				Properties p = new Properties();
				p.put("user", user);
				p.put("password", pwd);
				// 获取连接
				conn = driver.connect(url, p);
				if (conn != null) {
					return conn;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	// 非空判断
	private boolean notEmpty(String temp) {
		if (temp == null || "".equals(temp) || "null".equals(temp))
			return false;
		else
			return true;
	}

	private boolean notEmpty(List<RptSysModuleCol> temp) {
		if (temp == null || temp.size() == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * oracle/db2的分页SQL
	 * 
	 * @param dbType
	 *            数据库类型(oracle/db2)
	 * @param tableName
	 *            表名
	 * @param cols
	 *            查询的字段字符串
	 * @param start
	 *            分页开始
	 * @param end
	 *            分页结束
	 * @return
	 */
	private String getSql(String dbType, String cols, String sourceTable,
			int start, int end, String filter) {
		StringBuilder sqlBuff = new StringBuilder();
		if (ORACLE_DATA_SOURCE.equals(dbType) || H2_DATA_SOURCE.equals(dbType)) {
			sqlBuff.append("SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM (SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(",ROWNUM AS RN FROM ");
			sqlBuff.append("(SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
			sqlBuff.append(")");
			sqlBuff.append(" WHERE ROWNUM<=");
			sqlBuff.append(end);
			sqlBuff.append(")WHERE RN>=");
			sqlBuff.append(start);
		} else {
			sqlBuff.append("SELECT ");
			sqlBuff.append(cols);
			sqlBuff.append(" FROM ");
			sqlBuff.append(sourceTable);
			sqlBuff.append(" WHERE 1=1 ");
			if (notEmpty(filter)) {
				sqlBuff.append(" AND ");
				sqlBuff.append(filter);
			}
		}
		return sqlBuff.toString();
	}
	
	/**
	 * 获取单位
	 * @param AnaTmpInfo
	 * @return
	 */
	private String getUnit(String dataUnit){
		if(StringUtils.isNotBlank(dataUnit)){
			if(("01").equals(dataUnit)){
				dataUnit = "个";
			}else if(("02").equals(dataUnit)){
				dataUnit = "百";
			}else if(("03").equals(dataUnit)){
				dataUnit = "千";
			}else if(("04").equals(dataUnit)){
				dataUnit = "万";
			}else if(("05").equals(dataUnit)){
				dataUnit = "亿";
			}
		}
		return dataUnit;	
	}
}
