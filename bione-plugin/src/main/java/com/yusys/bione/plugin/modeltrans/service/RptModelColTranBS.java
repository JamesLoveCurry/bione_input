package com.yusys.bione.plugin.modeltrans.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
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
@Service
@Transactional(readOnly = true)
public class RptModelColTranBS extends BaseBS<Object>{
	/**
	 * grid查询数据
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<RptModelColTranVO> getModelColTran(int firstResult,
			int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		String jql = " select new com.yusys.bione.plugin.modeltrans.vo.RptModelColTranVO(info.setId,info.tableEnNm,info.setNm,info.srcSetId,srcInfo.tableEnNm,srcInfo.setNm,info.srcDataFilterCond) from RptSysModuleInfo info,RptSysModuleInfo srcInfo where info.srcSetId is not null and info.srcSetId = srcInfo.setId ";

		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by " + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		SearchResult<RptModelColTranVO> transList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql, values);
		return transList;
	}
	
	public Map<String,String> checkDstUsed(String tabNm) {
		Map<String,String> returnMap = Maps.newHashMap();
		String jql = " select trans from RptSysModuleInfo info,RptSysModuleColTran trans where info.tableEnNm = ?0 and info.setId = trans.id.setId ";
		List<RptSysModuleCol> isExsiList = this.baseDAO.findWithIndexParam(jql, tabNm);
		if(null != isExsiList && isExsiList.size() > 0){
			returnMap.put("isExsi", "Y");
		}else{
			returnMap.put("isExsi", "N");
		}
		return returnMap;
	}
	
	public List<RptSysModuleCol> getModelById(String setId) {
		String jql = " select col from RptSysModuleCol col where col.setId = ?0 order by col.orderNum";
		List<RptSysModuleCol> dstModel = this.baseDAO.findWithIndexParam(jql, setId);
		return dstModel;
	}
	
	public List<RptSysModuleCol> getModelByNm(String tabNm) {
		String jql = " select col from RptSysModuleInfo info,RptSysModuleCol col where info.tableEnNm = ?0 and info.setId = col.setId order by col.orderNum";
		List<RptSysModuleCol> dstModel = this.baseDAO.findWithIndexParam(jql, tabNm);
		return dstModel;
	}
	
	public void updateModelInfo(String setId,String srcSetId,String whereSrcf) {
		String jql = " update RptSysModuleInfo info set info.srcSetId = ?0, info.srcDataFilterCond = ?1 where info.setId = ?2 ";
		this.baseDAO.batchExecuteWithIndexParam(jql, srcSetId,whereSrcf,setId);
	}
	
	public Map<String,Object> getTransById(String setId,String srcSetId) {
		Map<String,Object> returnMap = Maps.newHashMap();
		String jql = " select dst.tableEnNm,src.tableEnNm,dst.srcDataFilterCond,col.enNm as dstEnNm,srcCol.enNm as srcEnNm,trans.transRule from RptSysModuleColTran trans,RptSysModuleInfo dst,RptSysModuleInfo src,RptSysModuleCol col,RptSysModuleCol srcCol where dst.setId = ?0 and trans.id.setId = dst.setId and dst.srcSetId = src.setId and trans.id.colId = col.colId and trans.srcColId = srcCol.colId order by col.orderNum ";
		returnMap.put("srcCol", this.getModelById(srcSetId));
		returnMap.put("trans", this.baseDAO.findWithIndexParam(jql, setId));
		return returnMap;
	}
	
	public void delAndModifyTransByIds(String[] ids) {
		String delSql = " delete from RptSysModuleColTran trans where trans.id.setId in (?0) ";
		String updSql = " update RptSysModuleInfo info set info.srcSetId = '', info.srcDataFilterCond = '' where info.setId in (?0) ";
		List<String> idsList = Arrays.asList(ids);
		this.baseDAO.batchExecuteWithIndexParam(delSql, idsList);//删除
		this.baseDAO.batchExecuteWithIndexParam(updSql, idsList);//改状态
		
	}
	
	public List<String> addColCheck(String setId,List<String> enNms) {
		List<String> rlist = Lists.newArrayList();
		List<RptSysModuleCol> colist = Lists.newArrayList();
		if(null != enNms && enNms.size() > 0){
			String jql = "select col from RptSysModuleCol col where col.setId = ?0 and col.enNm not in (?1)";
			colist = this.baseDAO.findWithIndexParam(jql, setId, enNms);
		}
		if(null != colist && colist.size() > 0){
			for(RptSysModuleCol col : colist){
				rlist.add(col.getEnNm());
			}
		}
		return rlist;
	}
}
