
package com.yusys.bione.plugin.colsearch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.colsearch.web.vo.RptColSearchResultVO;


@Service("colSearchInfoBS")
@Transactional(readOnly = true)
public class ColSearchInfoBS extends BaseBS<RptColSearchResultVO> {
	protected static Logger log = LoggerFactory.getLogger(ColSearchInfoBS.class);

	/**
	 * 根据条件查找指标界限
	 * 
	 * @param pageFirstIndex
	 * @param pageSize
	 * @param sortName
	 * @param sortOrder
	 * @param searchNm
	 * @return
	 */
	public List<RptColSearchResultVO> getColSearchInfos(String sortName, String sortOrder, 
			Map<String, Object> conditionMap,String searchNm,String checkNum) {
		List<Object[]> tmpResultList = null;
		List<RptColSearchResultVO> colSearchInfoList = new ArrayList<RptColSearchResultVO>();
		String[] sql = new String[4];
		String sqlAll = " ";
		String sqlSearch = " ";
		List<String> setIdxs = new ArrayList<String>();
		List<String> setRpts = new ArrayList<String>();
		List<String> setDetails = new ArrayList<String>();
		List<String> setModels = new ArrayList<String>();
		List<String> setAll = new ArrayList<String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isNotEmpty(checkNum)){
			String checkNums[] = StringUtils.split(checkNum, ',');
			for(int n=0; n<checkNums.length;n++){
				checkMap.put("check"+checkNums[n], checkNums[n]);
			}
		}
		if(StringUtils.isNotEmpty(searchNm)){
			sql[0] = " select t.index_no as search_id,t.index_no as search_no,t.index_nm as search_name, '1' as search_Type from RPT_IDX_INFO t "
					+ " where t.is_rpt_index =:isRptIndex and t.end_date =:endDate and t.index_nm like :searchNm ";
			sql[1] = " select t.rpt_id as search_id,t.rpt_num as search_no,t.rpt_nm as search_name, '2' as search_Type from Rpt_Mgr_Report_Info t "
					+ " where t.rpt_type =:isRptType and t.def_src =:defSrc and t.rpt_nm like :searchNm ";
			sql[2] = " select t.template_id as search_id,t.template_id as search_no,t.template_nm as search_name, '3' as search_Type from rpt_detail_tmp_info t "
					+ " where 1=1 and t.template_nm like :searchNm ";
			sql[3] = " select t.set_id as search_id,t.table_en_nm as search_no,t.set_nm as search_name, '4' as search_Type from Rpt_Sys_Module_Info t "
					+ " where t.set_type =:setType and t.set_nm like :searchNm ";
			
			for(int i=0;i<4;i++){
				if(checkMap.get("check"+i)!=null&&!"".equals(checkMap.get("check"+i))){
					paramMap.put("searchNm", "%"+searchNm+"%");
			        if(i==0){
				        paramMap.put("isRptIndex", "N");
				        paramMap.put("endDate", "29991231");
			        }
			        if(i==1){
			        	paramMap.put("isRptType", "02");
					    paramMap.put("defSrc", "01");
			        }
			        if(i==2){
			        	
			        }
			        if(i==3){
			        	paramMap.put("setType", "00");
			        }
			        sqlAll +=  sql[i]+"union all";
			     }
				
		     }
			if(checkMap.size()>0){
				if (sqlAll.endsWith("union all")) {
					sqlAll = sqlAll.substring(0, sqlAll.length() - 9);
				}
				 sqlSearch = " select * from(" +sqlAll+") ";
				//排序
		        if(StringUtils.isNotEmpty(sortName)){
		        	if(sortName.equals("searchType")){
		        		sqlSearch += " order by search_Type " + sortOrder;
		        	}
		        }
			}else{
				sqlSearch = sql[2];
				paramMap.put("searchNm", "");
			}
			tmpResultList = this.baseDAO.findByNativeSQLWithNameParam(sqlSearch,paramMap);
	        paramMap.clear();
	     // 获取指标、报表、明细模板、明细模型的权限控制
			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				setIdxs = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4plugin.IDX_RES_NO);
				setRpts = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4plugin.RPTVIEW_RES_NO);
				setDetails = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4plugin.DETAIL_RES_NO);
				setModels = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4plugin.MODEL_RES_NO);
			
		    	if(setIdxs != null && setIdxs.size() > 0){
		    		for(String searchId : setIdxs){
		    			setAll.add(searchId);
		    		}
		    	}
		    	if(setRpts != null && setRpts.size() > 0){
		    		for(String searchId : setRpts){
		    			setAll.add(searchId);
		    		}
		    	}
		    	if(setDetails != null && setDetails.size() > 0){
		    		for(String searchId : setDetails){
		    			setAll.add(searchId);
		    		}
		    	}
		    	if(setModels != null && setModels.size() > 0){
		    		for(String searchId : setModels){
		    			setAll.add(searchId);
		    		}
		    	}
			}
			Map<String, Object> param = new HashMap<String, Object>();
			if(setAll != null && setAll.size() > 0){
				for(String searchId : setAll){
					param.put(searchId, searchId);
				}
			}else{
				param.put("emp", "emp");
			}
	        //由于采用native语言查询，因而需要将取得的值传到VO中
	        for(int j=0; j<tmpResultList.size(); j++){
	        	RptColSearchResultVO colSearchInfoVO = new RptColSearchResultVO();
	        	Object[] obj = (Object[])tmpResultList.get(j);
	        	if((param.get(String.valueOf(obj[0]))!=null&&!"".equals(String.valueOf(obj[0]))) || BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
	        		colSearchInfoVO.setSearchId(String.valueOf(obj[0]));
		        	colSearchInfoVO.setSearchNo(String.valueOf(obj[1]));
		        	colSearchInfoVO.setSearchName(String.valueOf(obj[2]));  
		        	colSearchInfoVO.setSearchType(String.valueOf(obj[3]));
		        	colSearchInfoList.add(colSearchInfoVO);
	        	}
	        }
		}
		return colSearchInfoList;
	}
/*	public SearchResult<RptColSearchResultVO> getColSearchInfos(int pageFirstIndex,
			int pageSize, String sortName, String sortOrder, 
			Map<String, Object> conditionMap,String searchNm,String checkNum) {
		SearchResult<Object> tmpResultList = null;
		SearchResult<RptColSearchResultVO> resultList = new SearchResult<RptColSearchResultVO>();
		List<RptColSearchResultVO> colSearchInfoList = new ArrayList<RptColSearchResultVO>();
		String[] sql = new String[4];
		String sqlAll = " ";
		String sqlSearch = " ";
		List<String> setIdxs = new ArrayList<String>();
		List<String> setRpts = new ArrayList<String>();
		List<String> setDetails = new ArrayList<String>();
		List<String> setModels = new ArrayList<String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, String> checkMap = new HashMap<String, String>();
		if(StringUtils.isNotEmpty(checkNum)){
			String checkNums[] = checkNum.split(",");
			for(int n=0; n<checkNums.length;n++){
				checkMap.put("check"+checkNums[n], checkNums[n]);
			}
		}
		// 获取指标、报表、明细模板、明细模型的权限控制
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			setIdxs = BioneSecurityUtils.getResIdListOfUser(GlobalConstants.IDX_RES_NO);
			setRpts = BioneSecurityUtils.getResIdListOfUser(GlobalConstants.RPTVIEW_RES_NO);
			setDetails = BioneSecurityUtils.getResIdListOfUser(GlobalConstants.DETAIL_RES_NO);
			setModels = BioneSecurityUtils.getResIdListOfUser(GlobalConstants.MODEL_RES_NO);
		}

		if(StringUtils.isNotEmpty(searchNm)){
			sql[0] = " select t.index_no as search_id,t.index_no as search_no,t.index_nm as search_name, '1' as search_Type from RPT_IDX_INFO t "
					+ " where t.is_rpt_index =:isRptIndex and t.end_date =:endDate and t.index_nm like :searchNm ";
			sql[1] = " select t.rpt_id as search_id,t.rpt_num as search_no,t.rpt_nm as search_name, '2' as search_Type from Rpt_Mgr_Report_Info t "
					+ " where t.rpt_type =:isRptType and t.def_src =:defSrc and t.rpt_nm like :searchNm and t.rpt_id in (:setRpts) ";
			sql[2] = " select t.template_id as search_id,t.template_id as search_no,t.template_nm as search_name, '3' as search_Type from rpt_detail_tmp_info t "
					+ " where 1=1 and t.template_nm like :searchNm and t.template_id in (:setDetails) ";
			sql[3] = " select t.set_id as search_id,t.table_en_nm as search_no,t.set_nm as search_name, '4' as search_Type from Rpt_Sys_Module_Info t "
					+ " where t.set_type =:setType and t.set_nm like :searchNm and t.set_id in (:setModels) ";
			
			for(int i=0;i<4;i++){
				if(checkMap.get("check"+i)!=null&&!"".equals(checkMap.get("check"+i))){
					paramMap.put("searchNm", "%"+searchNm+"%");
					if(i==0 && setIdxs.size()<1){
						continue;
					}
					if(i==1 && setRpts.size()<1){
						continue;
					}
					if(i==2 && setDetails.size()<1){
						continue;
					}
					if(i==3 && setModels.size()<1){
						continue;
					}
			        if(i==0 && setIdxs.size()>0){
				        paramMap.put("isRptIndex", "N");
				        paramMap.put("endDate", "29991231");
				        if(setIdxs != null){
							if(setIdxs.size() > 0){
								List<List<?>> indexNoLists = ReBuildParam.splitLists(setIdxs);
								sql[0] += " and ( ";
								int j = 0;
								for(List<?> indexNoList : indexNoLists){
									sql[0] += " t.index_no in (:indexNoList"+j+") ";
									paramMap.put("indexNoList"+j, indexNoList);
									if(j < indexNoLists.size() - 1){
										sql[0] += " or ";
									}
									else{
										sql[0] += " ) ";
									}
									j++;
								}
							}
						}
			        }
			        if(i==1 && setRpts.size()>0){
			        	paramMap.put("isRptType", "02");
					    paramMap.put("defSrc", "01");
						paramMap.put("setRpts", setRpts);
			        }
			        if(i==2 && setDetails.size()>0){
						paramMap.put("setDetails", setDetails);
			        }
			        if(i==3 && setModels.size()>0){
			        	paramMap.put("setType", "00");
						paramMap.put("setModels", setModels);
			        }
			        sqlAll +=  sql[i]+"union all";
			     }
				
		     }
			if(checkMap.size()>0){
				if (sqlAll.endsWith("union all")) {
					sqlAll = sqlAll.substring(0, sqlAll.length() - 9);
				}
				 sqlSearch = " select * from(" +sqlAll+") ";
				//排序
		        if(StringUtils.isNotEmpty(sortName)){
		        	if(sortName.equals("searchType")){
		        		sqlSearch += " order by search_Type " + sortOrder;
		        	}
		        }
			}else{
				sqlSearch = sql[2];
				paramMap.put("searchNm", "");
				paramMap.put("setDetails", "");
			}
			tmpResultList = this.baseDAO.findPageWithNameParamByNativeSQL(pageFirstIndex, pageSize,sqlSearch,paramMap);
	        paramMap.clear();
	        //由于采用native语言查询，因而需要将取得的值传到VO中
	        for(int j=0; j<tmpResultList.getResult().size(); j++){
	        	RptColSearchResultVO colSearchInfoVO = new RptColSearchResultVO();
	        	Object[] obj = (Object[])tmpResultList.getResult().get(j);
	        	colSearchInfoVO.setSearchId(String.valueOf(obj[0]));
	        	colSearchInfoVO.setSearchNo(String.valueOf(obj[1]));
	        	colSearchInfoVO.setSearchName(String.valueOf(obj[2]));  
	        	colSearchInfoVO.setSearchType(String.valueOf(obj[3]));
	        	colSearchInfoList.add(colSearchInfoVO);
	        	}
			}
		resultList.setResult(colSearchInfoList);
		resultList.setTotalCount(tmpResultList.getTotalCount());
		return resultList;
	}*/
}
