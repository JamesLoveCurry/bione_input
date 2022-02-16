package com.yusys.bione.plugin.datashow.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validtype.entity.BioneValidTypeDef;
import com.yusys.bione.plugin.datashow.web.vo.RptEngineCheckStsVO;
import com.yusys.bione.plugin.datashow.web.vo.RptValidResultLogicVO;

@Service
@Transactional(readOnly = true)
public class ValidShowBS extends BaseBS<Object>{
	
	public List<BioneValidTypeDef> getValidTypeDef() {
		Map<String,Object> param = Maps.newHashMap();
		List<String> defNoList = new ArrayList<String>();
		defNoList.add("01");
		defNoList.add("02");
		defNoList.add("05");
		String sql = "select def from BioneValidTypeDef def,BioneValidTypeSysRel rel "
				+ " where rel.id.logicSysNo = :logicSysNo "
				+ " and def.objDefNo = rel.id.objDefNo "
				+ " and def.objDefNo in(:defNoType)"
				+ " order by def.objDefNo";
		param.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		param.put("defNoType", defNoList);
		return this.baseDAO.findWithNameParm(sql, param);
	}
	
	/**
	 * 根据条件查找指标界限
	 * 
	 * @param pageFirstIndex
	 * @param pageSize
	 * @param sortName
	 * @param sortOrder
	 * @param indexCatalogNo
	 * @param indexNo
	 * @param indexVerId
	 * @param indexNm
	 * @param indexType
	 * @param defSrc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<RptEngineCheckStsVO> getCheckInfos(int pageFirstIndex,
			int pageSize, String sortName, String sortOrder, 
			Map<String, Object> conditionMap, String tabId, 
			String indexNo, String indexNm) {
		SearchResult<Object[]> tmpResultList = null;
		SearchResult<RptEngineCheckStsVO> resultList = new SearchResult<RptEngineCheckStsVO>();
		Map<String, String> paramMap =new HashMap<String, String>();
		String sql = "";
		
		if(StringUtils.isNotEmpty(tabId)){
			tabId = tabId.substring(3, tabId.length());
			paramMap.put("tabId", tabId);
			paramMap.put("indexNo", indexNo);
		    sql = "select t1.Data_Date,t1.Rpt_Template_ID,t1.Org_No,"
		    		+ " t1.Check_Type,t1.Check_Sts,t2.ORG_NM from RPT_ENGINE_CHECK_STS t1 "
		    		+ " left join RPT_ORG_INFO t2 on t2.ORG_NO = t1.ORG_NO "
		    		+ " where  t1.Check_Type = :tabId and t1.Rpt_Template_Id "
		    		+ " in(select t.VALID_GID from RPT_VALID_GROUP_INFO t where t.INDEX_NO = :indexNo)";
		}
		
		//搜索查询
		if (!conditionMap.get("jql").equals("")) {
			String tmpjql = (String)conditionMap.get("jql");
			Map<String, Object> tmpParam = (Map<String, Object>) conditionMap.get("params");
			
			String sj[] = StringUtils.split(tmpjql, "and");  //通过and标识截断语句
			if(sj.length == 3){   //指标名称及类型均有参数
				String sp0 = (String)tmpParam.get("param0");
				String sp1 = (String)tmpParam.get("param1");
				String sp2 = (String)tmpParam.get("param2");
				sql += " and t2.ORG_NM like '" + sp0 + "' ";
				sql += " and t1.DATA_DATE >= '" + sp1 + "' ";
				sql += " and t1.DATA_DATE <= '" + sp2 + "' ";
			}else if(sj.length == 2){ 
				String sp0 = (String)tmpParam.get("param0");
				String sp1 = (String)tmpParam.get("param1");
				if(sj[0].trim().equals("ORG_NM like:param0")){
					sql += " and t2.ORG_NM like '" + sp0 + "' ";
				}
				if(sj[0].trim().equals("data_date >=:param0")){
					sql += " and t1.DATA_DATE >= '" + sp0 + "' ";
				}
				if(sj[0].trim().equals("data_date <=:param0")){
					sql += " and t1.DATA_DATE <= '" + sp0 + "' ";
				}
				if(sj[1].trim().equals("ORG_NM like:param1")){
					sql += " and t2.ORG_NM like '" + sp1 + "' ";
				}
				if(sj[1].trim().equals("data_date >=:param1")){
					sql += " and t1.DATA_DATE >= '" + sp1 + "' ";
				}
				if(sj[1].trim().equals("data_date <=:param1")){
					sql += " and t1.DATA_DATE <= '" + sp1 + "' ";
				}
			}else{   //指标名称与类型只有一个有参数
				String sp0 = (String)tmpParam.get("param0");
				if(sj[0].trim().equals("ORG_NM like:param0")){
					sql += " and t2.ORG_NM like '" + sp0 + "' ";
				}
				if(sj[0].trim().equals("data_date >=:param0")){
					sql += " and t1.DATA_DATE >= '" + sp0 + "' ";
				}
				if(sj[0].trim().equals("data_date <=:param0")){
					sql += " and t1.DATA_DATE <= '" + sp0 + "' ";
				}
			}
		}
		/*//搜索查询
		if (!conditionMap.get("jql").equals("")) {
			String tmpsql = (String)conditionMap.get("jql");
			Map<String, Object> tmpParam = (Map<String, Object>) conditionMap.get("params");
			
			String sj[] = tmpsql.split("and");  //通过and标识截断语句
			if(sj.length == 2){   //指标名称及类型均有参数
				String sp0 = (String)tmpParam.get("param0");
				String sp1 = (String)tmpParam.get("param1");
				sql += " and t1.DATA_DATE >= '" + sp0 + "' ";
				sql += " and t1.DATA_DATE <= '" + sp1 + "' ";
			}else if(sj.length == 1){   //指标名称与类型只有一个有参数
				String sp0 = (String)tmpParam.get("param0");
				if(sj[0].trim().equals("data_date >=:param0")){
					sql += " and t1.DATA_DATE　>= '" + sp0 + "'";
				}else if(sj[0].trim().equals("data_date <=:param0")){
					sql += " and t1.DATA_DATE　<= '" + sp0 + "'";
			    }
		    }
		}*/
		//排序
	   if(StringUtils.isNotEmpty(sortName)){
			if(sortName.equals("dataDate")){
				sql += "order by t1.DATA_DATE " + sortOrder;
			}
		}

		tmpResultList = this.baseDAO.findPageWithNameParamByNativeSQL(pageFirstIndex, pageSize, 
				sql,paramMap );
		
		//由于采用native语言查询，因而需要将取得的值传到VO中
		List<RptEngineCheckStsVO> checkInfoList = new ArrayList<RptEngineCheckStsVO>();
		Map<String,RptEngineCheckStsVO> stsMap = new HashMap<String, RptEngineCheckStsVO>();
		for(int i=0; i<tmpResultList.getResult().size(); i++){
			RptEngineCheckStsVO checkInfoVO = new RptEngineCheckStsVO();
			Object[] obj = tmpResultList.getResult().get(i);
			
			checkInfoVO.setDataDate(String.valueOf(obj[0]));
			checkInfoVO.setRptTemplateId(String.valueOf(obj[1]));
			checkInfoVO.setOrgNo(String.valueOf(obj[2]));
			checkInfoVO.setCheckType(String.valueOf(obj[3]));
			checkInfoVO.setCheckSts(obj[4] != null?String.valueOf(obj[4]):"");
			checkInfoVO.setOrgNm(obj[5] != null?String.valueOf(obj[5]):"");
			checkInfoVO.setIndexNo(indexNo);
			checkInfoVO.setIndexNm(indexNm);
			String key = checkInfoVO.getDataDate()+"-"+checkInfoVO.getOrgNo();
			if(stsMap.get(key)!= null){
				RptEngineCheckStsVO kvo = stsMap.get(key);
				if(!kvo.getCheckSts().equals("02")){
					if(!checkInfoVO.getCheckSts().equals("05")){
						if(checkInfoVO.getCheckSts().equals("01")||checkInfoVO.getCheckSts().equals("02")){
							kvo.setCheckSts("02");
						}
						else{
							kvo.setCheckSts("06");
						}
					}
				}
				
			}else{
				stsMap.put(key,checkInfoVO);
				checkInfoList.add(checkInfoVO);
			}
			
		}
		
		resultList.setResult(checkInfoList);
		resultList.setTotalCount(tmpResultList.getTotalCount());
				
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public SearchResult<RptValidResultLogicVO> checkDetail(int pageFirstIndex,
			int pageSize, String sortName, String sortOrder, 
			Map<String, Object> conditionMap, String indexNo,String indexNm,String dataDate,String orgNo,String checkType) {
		SearchResult<Object[]> tmpResultList = null;
		SearchResult<RptValidResultLogicVO> resultList = new SearchResult<RptValidResultLogicVO>();
		Map<String, String> paramMap =new HashMap<String, String>(); 
		Map<Integer, String> param =new HashMap<Integer, String>(); 
		String storeCol = "";
		String sql = "";
		String sql1 = "";
		Integer dimNum = 0;
		
		if(StringUtils.isNotEmpty(checkType)){
			paramMap.put("indexNo", indexNo);
			paramMap.put("dataDate", dataDate);
			paramMap.put("orgNo", orgNo);
			paramMap.put("checkType", checkType);
			if("01".equals(checkType)||"05".equals(checkType)){
				sql = " select t1.DIM_NO,t1.STORE_COL from RPT_VALID_DIM_REL t1 "
						+ " where t1.VALID_TYPE = :checkType and t1.Dim_Type <> '01' and t1.Dim_Type <> '02' and t1.CHECK_ID in"
						+ " (select distinct t.CHECK_ID from RPT_VALID_RESULT_LOGIC t "
						+ " where  t.DATA_DATE = :dataDate  and t.ORG_NO = :orgNo "
						+ " and t.VALID_TYPE = :checkType and t.VALID_GID "
						+ " in(select t.VALID_GID from RPT_VALID_GROUP_INFO t where t.INDEX_NO = :indexNo)) "; 
			}
			if("02".equals(checkType)){
				sql = " select t1.DIM_NO,t1.STORE_COL from RPT_VALID_DIM_REL t1 "
						+ " where t1.VALID_TYPE = :checkType and t1.Dim_Type <> '01' and t1.Dim_Type <> '02' and t1.CHECK_ID in"
						+ " (select distinct t.CHECK_ID from RPT_VALID_RESULT_WARN t "
						+ " where  t.DATA_DATE = :dataDate  and t.ORG_NO = :orgNo "
						+ " and t.VALID_GID "
						+ " in(select t.VALID_GID from RPT_VALID_GROUP_INFO t where t.INDEX_NO = :indexNo)) "; 
			}
			List<Object[]> dimList = this.baseDAO.findByNativeSQLWithNameParam(sql, paramMap);
			if(dimList.size()>0){
				for(Object[] dim : dimList){
					int i = 2;
					param.put(i, dim[0].toString());
					storeCol += "t."+dim[1].toString()+",";
				}
				dimNum = dimList.size();
			}
			if("01".equals(checkType)){
				sql1 = " select  t.CHECK_TIME,t1.ORG_NM,"+storeCol+"t.D_VAL,t2.CHECK_NM,t.IS_PASS from RPT_VALID_RESULT_LOGIC t "
						+ " left join RPT_ORG_INFO t1 on t1.ORG_NO = t.ORG_NO "
						+ " left join RPT_VALID_CFGEXT_LOGIC t2 on t2.CHECK_ID =t.CHECK_ID"
						+ " where t.VALID_GID in(select t.VALID_GID from RPT_VALID_GROUP_INFO t "
						+ " where t.INDEX_NO = :indexNo)"
						+ " and t.DATA_DATE = :dataDate "
						+ " and t.ORG_NO = :orgNo and t.VALID_TYPE = :checkType";
			}else if("02".equals(checkType)){
				paramMap.clear();
				paramMap.put("indexNo", indexNo);
				paramMap.put("dataDate", dataDate);
				paramMap.put("orgNo", orgNo);
				sql1 = " select  t.CHECK_TIME,t1.ORG_NM,"+storeCol+"t2.LEVEL_NM,t.CURR_VAL,t.COMPARE_VAL,t3.CHECK_NM,t.IS_PASS "
						+ " from RPT_VALID_RESULT_WARN t "
						+ " left join RPT_ORG_INFO t1 on t1.ORG_NO = t.ORG_NO "
						+ " left join RPT_VALID_WARN_LEVEL t2 on t2.LEVEL_NUM = t.LEVEL_NUM "
						+ " left join RPT_VALID_CFGEXT_WARN t3 on t3.CHECK_ID =t.CHECK_ID"
						+ " where t.VALID_GID in(select t.VALID_GID from RPT_VALID_GROUP_INFO t "
						+ " where t.INDEX_NO = :indexNo)"
						+ " and t.DATA_DATE = :dataDate "
						+ " and t.ORG_NO = :orgNo ";
			}else if("05".equals(checkType)){
				sql1 = " select  t.CHECK_TIME,t1.ORG_NM,"+storeCol+"t.D_VAL,t.ORG_NO,t.IS_PASS from RPT_VALID_RESULT_LOGIC t "
						+ " left join RPT_ORG_INFO t1 on t1.ORG_NO = t.ORG_NO "
						+ " where t.VALID_GID in(select t.VALID_GID from RPT_VALID_GROUP_INFO t "
						+ " where t.INDEX_NO = :indexNo)"
						+ " and t.DATA_DATE = :dataDate "
						+ " and t.ORG_NO = :orgNo and t.VALID_TYPE = :checkType";
			}
			
		}
		
		//搜索查询
		if (!conditionMap.get("jql").equals("")) {
			String tmpsql = (String)conditionMap.get("jql");
			Map<String, Object> tmpParam = (Map<String, Object>) conditionMap.get("params");
			
			String sj[] = StringUtils.split(tmpsql, "and");  //通过and标识截断语句
			if(sj.length == 2){   //指标名称及类型均有参数
				String sp0 = (String)tmpParam.get("param0");
				String sp1 = (String)tmpParam.get("param1");
				sp0 = sp0.substring(0,4)+"-"+sp0.substring(4, 6)+"-"+sp0.substring(6, 8);
				sp1 = sp1.substring(0,4)+"-"+sp1.substring(4, 6)+"-"+sp1.substring(6, 8);
				sql1 += " and t.CHECK_TIME >= to_timestamp('" + sp0 + "','yyyy-MM-dd')";
				sql1 += " and t.CHECK_TIME <= to_timestamp('" + sp1 + "','yyyy-MM-dd') ";
			}else if(sj.length == 1){   //指标名称与类型只有一个有参数
				String sp0 = (String)tmpParam.get("param0");
				sp0 = sp0.substring(0,4)+"-"+sp0.substring(4, 6)+"-"+sp0.substring(6, 8);
				if(sj[0].trim().equals("check_time >=:param0")){
					sql1 += " and t.CHECK_TIME　>= to_timestamp('" + sp0 + "','yyyy-MM-dd')";
				}else if(sj[0].trim().equals("check_time <=:param0")){
					sql1 += " and t.CHECK_TIME　<= to_timestamp('" + sp0 + "','yyyy-MM-dd')";
			    }
		    }
		}
		//排序
	   if(StringUtils.isNotEmpty(sortName)){
			if(sortName.equals("checkTime")){
				sql1 += " order by t.CHECK_TIME " + sortOrder;
			}
		}

		tmpResultList = this.baseDAO.findPageWithNameParamByNativeSQL(pageFirstIndex, pageSize, 
				sql1,paramMap );
		
		//由于采用native语言查询，因而需要将取得的值传到VO中
		List<RptValidResultLogicVO> checkInfoList = new ArrayList<RptValidResultLogicVO>();
		for(int i=0; i<tmpResultList.getResult().size(); i++){
			RptValidResultLogicVO checkInfoVO = new RptValidResultLogicVO();
			Object[] obj = tmpResultList.getResult().get(i);
			String comp = String.valueOf(obj[1])+"-";
		
			for(int j=2; j<dimNum+2; j++){
				String sql2 = " select t.DIM_ITEM_NM from RPT_DIM_ITEM_INFO t where t.DIM_ITEM_NO =?0 and t.DIM_TYPE_NO =?1";
				List<Object[]> objList = this.baseDAO.findByNativeSQLWithIndexParam(sql2, obj[j],param.get(j));
				if(objList.size()>0){
					for(Object dim : objList){
						comp += dim+"-";
					}
				}else if(!"-".equals(obj[j])){
					comp += obj[j]+"-";
				}
			}
			comp = comp.substring(0,comp.length()-1);
			checkInfoVO.setCheckTime(Timestamp.valueOf(String.valueOf(obj[0])));
			checkInfoVO.setDimCheck(comp);
			if("01".equals(checkType)){
				checkInfoVO.setdVal(new BigDecimal(obj[dimNum+2].toString()));
				checkInfoVO.setCheckNm(obj[dimNum+3].toString());
				checkInfoVO.setIsPass(obj[dimNum+4].toString());
			}
			if("02".equals(checkType)){
				checkInfoVO.setLevelNum(obj[dimNum+2].toString());
				checkInfoVO.setCurrVal(new BigDecimal(obj[dimNum+3].toString()));
				checkInfoVO.setCompareVal(new BigDecimal(obj[dimNum+4].toString()));
				checkInfoVO.setCheckNm(obj[dimNum+5].toString());
				checkInfoVO.setIsPass(obj[dimNum+6].toString());
			}
			if("05".equals(checkType)){
				checkInfoVO.setdVal(new BigDecimal(obj[dimNum+2].toString()));
				checkInfoVO.setCheckNm(indexNm);
				checkInfoVO.setIsPass(obj[dimNum+4].toString());
			}
			checkInfoList.add(checkInfoVO);
		}
		
		resultList.setResult(checkInfoList);
		resultList.setTotalCount(tmpResultList.getTotalCount());
				
		return resultList;
	}
	@SuppressWarnings("unchecked")
	public SearchResult<RptValidResultLogicVO> checkTreeDetail(int pageFirstIndex,
			int pageSize, String sortName, String sortOrder, 
			Map<String, Object> conditionMap, String tabId,String checkId,String checkNm) {
		SearchResult<Object[]> tmpResultList = null;
		SearchResult<RptValidResultLogicVO> resultList = new SearchResult<RptValidResultLogicVO>();
		Map<String, String> paramMap =new HashMap<String, String>(); 
		Map<Integer, String> param =new HashMap<Integer, String>(); 
		String storeCol = "";
		String sql = "";
		String sql1 = "";
		Integer dimNum = 0;
		
		if(StringUtils.isNotEmpty(tabId)){
			paramMap.put("tabId", tabId);
			paramMap.put("checkId", checkId);
			sql = " select t1.DIM_NO,t1.STORE_COL from RPT_VALID_DIM_REL t1 "
					+ " where t1.VALID_TYPE = :tabId and t1.Dim_Type <> '01' and t1.Dim_Type <> '02' "
					+ " and t1.CHECK_ID = :checkId"; 
			List<Object[]> dimList = this.baseDAO.findByNativeSQLWithNameParam(sql, paramMap);
			if(dimList.size()>0){
				for(Object[] dim : dimList){
					int i = 2;
					param.put(i, dim[0].toString());
					storeCol += "t."+dim[1].toString()+",";
				}
				dimNum = dimList.size();
			}
			if("01".equals(tabId)){
				sql1 = " select  t.CHECK_TIME,t1.ORG_NM,"+storeCol+"t.D_VAL,t2.CHECK_NM,t.IS_PASS,t2.EXPRESSION_DESC from RPT_VALID_RESULT_LOGIC t "
						+ " left join RPT_ORG_INFO t1 on t1.ORG_NO = t.ORG_NO "
						+ " left join RPT_VALID_CFGEXT_LOGIC t2 on t2.CHECK_ID =t.CHECK_ID"
						+ " where t.VALID_TYPE = :tabId and t.CHECK_ID = :checkId";
			}else if("02".equals(tabId)){
				paramMap.clear();
				paramMap.put("checkId", checkId);
				sql1 = " select  t.CHECK_TIME,t1.ORG_NM,"+storeCol+"t2.LEVEL_NM,t.CURR_VAL,t.COMPARE_VAL,t3.CHECK_NM,t.IS_PASS "
						+ " from RPT_VALID_RESULT_WARN t "
						+ " left join RPT_ORG_INFO t1 on t1.ORG_NO = t.ORG_NO "
						+ " left join RPT_VALID_WARN_LEVEL t2 on t2.LEVEL_NUM = t.LEVEL_NUM "
						+ " left join RPT_VALID_CFGEXT_WARN t3 on t3.CHECK_ID =t.CHECK_ID"
						+ " where  t.CHECK_ID = :checkId";
			}
			
		}
		
		//搜索查询
		if (!conditionMap.get("jql").equals("")) {
			String tmpsql = (String)conditionMap.get("jql");
			Map<String, Object> tmpParam = (Map<String, Object>) conditionMap.get("params");
			
			String sj[] = StringUtils.split(tmpsql, "and");  //通过and标识截断语句
			if(sj.length == 2){   //指标名称及类型均有参数
				String sp0 = (String)tmpParam.get("param0");
				String sp1 = (String)tmpParam.get("param1");
				sp0 = sp0.substring(0,4)+"-"+sp0.substring(4, 6)+"-"+sp0.substring(6, 8);
				sp1 = sp1.substring(0,4)+"-"+sp1.substring(4, 6)+"-"+sp1.substring(6, 8);
				sql1 += " and t.CHECK_TIME >= to_timestamp('" + sp0 + "','yyyy-MM-dd')";
				sql1 += " and t.CHECK_TIME <= to_timestamp('" + sp1 + "','yyyy-MM-dd') ";
			}else if(sj.length == 1){   //指标名称与类型只有一个有参数
				String sp0 = (String)tmpParam.get("param0");
				sp0 = sp0.substring(0,4)+"-"+sp0.substring(4, 6)+"-"+sp0.substring(6, 8);
				if(sj[0].trim().equals("check_time >=:param0")){
					sql1 += " and t.CHECK_TIME　>= to_timestamp('" + sp0 + "','yyyy-MM-dd')";
				}else if(sj[0].trim().equals("check_time <=:param0")){
					sql1 += " and t.CHECK_TIME　<= to_timestamp('" + sp0 + "','yyyy-MM-dd')";
			    }
		    }
		}
		//排序
	   if(StringUtils.isNotEmpty(sortName)){
			if(sortName.equals("checkTime")){
				sql1 += " order by t.CHECK_TIME " + sortOrder;
			}
		}

		tmpResultList = this.baseDAO.findPageWithNameParamByNativeSQL(pageFirstIndex, pageSize, 
				sql1,paramMap );
		
		//由于采用native语言查询，因而需要将取得的值传到VO中
		List<RptValidResultLogicVO> checkInfoList = new ArrayList<RptValidResultLogicVO>();
		for(int i=0; i<tmpResultList.getResult().size(); i++){
			RptValidResultLogicVO checkInfoVO = new RptValidResultLogicVO();
			Object[] obj = tmpResultList.getResult().get(i);
			String comp = String.valueOf(obj[1])+"-";
			for(int j=2; j<dimNum+2; j++){
				String sql2 = " select t.DIM_ITEM_NM from RPT_DIM_ITEM_INFO t where t.DIM_ITEM_NO =?0 and t.DIM_TYPE_NO =?1";
				List<Object[]> objList = this.baseDAO.findByNativeSQLWithIndexParam(sql2, obj[j],param.get(j));
				if(objList.size()>0){
					for(Object dim : objList){
						comp += dim+"-";
					}
				}else if(!"-".equals(obj[j])){
					comp += obj[j]+"-";
				}
			}
			comp = comp.substring(0,comp.length()-1);
			checkInfoVO.setCheckTime(Timestamp.valueOf(String.valueOf(obj[0])));
			checkInfoVO.setDimCheck(comp);
			if("01".equals(tabId)){
				checkInfoVO.setdVal(new BigDecimal(obj[dimNum+2].toString()));
				checkInfoVO.setCheckNm(obj[dimNum+3].toString());
				checkInfoVO.setIsPass(obj[dimNum+4].toString());
				checkInfoVO.setExpressionDesc(obj[6].toString());
			}
			if("02".equals(tabId)){
				checkInfoVO.setLevelNum(obj[dimNum+2].toString());
				checkInfoVO.setCurrVal(new BigDecimal(obj[dimNum+3].toString()));
				checkInfoVO.setCompareVal(new BigDecimal(obj[dimNum+4].toString()));
				checkInfoVO.setCheckNm(obj[dimNum+5].toString());
				checkInfoVO.setIsPass(obj[dimNum+6].toString());
			}
			checkInfoList.add(checkInfoVO);
		}
		
		resultList.setResult(checkInfoList);
		resultList.setTotalCount(tmpResultList.getTotalCount());
				
		return resultList;
	}
}
