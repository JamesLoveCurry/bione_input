package com.yusys.bione.plugin.draw.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.draw.entity.RptDrawLogInfo;
import com.yusys.bione.plugin.draw.entity.RptIdxDrawInfo;
import com.yusys.bione.plugin.draw.entity.RptRptDrawInfo;

/**
 * <pre>
 * Title:指标/报表翻牌的功能
 * Description: 提供指标报表翻牌信息的展示/手工翻牌/查看翻牌日志等功能，提供事务控制
 * </pre>
 * 
 * @author hubing hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptDrawBS extends BaseBS<Object>{
	@Autowired
	private ExcelBS excelBs;
	/**
	 * 指标翻牌数据查询
	 * @param firstResult
	 *            第一条记录
	 * @param pageSize
	 *            每页记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            参数列表
	 * @return 返回指标的翻牌结果集
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<Object[]> getIdxDrawList(int firstResult,int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		String jql = "select idx.INDEX_NO,idx.INDEX_NM,pid.DRAW_DATE from RPT_IDX_INFO idx left join RPT_IDX_DRAW_INFO pid on idx.index_no = pid.index_no where 1=1 and idx.END_DATE =:endDt and idx.INDEX_STS =:idxSts and idx.IS_RPT_INDEX =:isRptIdx ";

		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by " + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		values.put("endDt", "29991231");
		values.put("idxSts", "Y");
		values.put("isRptIdx", "N");

		SearchResult<Object[]> idxDrawList = this.baseDAO
				.findPageWithNameParamByNativeSQL(firstResult, pageSize, jql, values);
		return idxDrawList;
	}
	
/**
	 * 报表翻牌数据查询
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<Object[]> getRptDrawList(int firstResult,int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		String jql = "select rpt.RPT_ID,rpt.RPT_NM,rpt.RPT_NUM,prd.DRAW_DATE from RPT_MGR_REPORT_INFO rpt left join RPT_RPT_DRAW_INFO prd on rpt.RPT_ID = prd.RPT_ID where 1=1 ";

		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by " + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
//		values.put("endDt", "29991231");
//		values.put("idxSts", "Y");

		SearchResult<Object[]> drawList = this.baseDAO
				.findPageWithNameParamByNativeSQL(firstResult, pageSize, jql, values);
		return drawList;
	}


	/**
	 * 手工翻牌
	 * @param arr 指标编号数组
	 * @param dt 翻牌日期
	 * @return 返回翻牌成功失败的提示信息
	 */
	@Transactional(readOnly = false)
	public Map<String ,Object> idxDraw(String[] arr ,String dt) {
		Map<String ,Object> res = Maps.newHashMap();
		try{
			List<RptDrawLogInfo> logs = Lists.newArrayList();
			for(int i=0;i < arr.length;i++){
				if(StringUtils.isNotBlank(arr[i])){
					RptIdxDrawInfo pec = new RptIdxDrawInfo();
					RptDrawLogInfo log = new RptDrawLogInfo();
					pec.setIndexNo(arr[i]);
					pec.setDrawDate(dt);
					log.setDrawDate(dt);
					log.setDrawObj(GlobalConstants4plugin.DRAW_IDX);
					log.setLogId(RandomUtils.uuid2());
					log.setDrawTime(new Timestamp(System.currentTimeMillis()));
					log.setObjId(arr[i]);
					log.setUserId(BioneSecurityUtils.getCurrentUserId());
					this.saveOrUpdateEntity(pec);
					logs.add(log);
				}
			}
			this.excelBs.saveEntityJdbcBatch(logs);
			res.put("msg", "ok");
		}
		catch(Exception e){
			e.printStackTrace();
			res.put("msg", "error");
		}
		return res;
	}
	
	/**
	 * 手工翻牌-报表
	 * @param arr 报表编号数组
	 * @param dt 翻牌日期
	 * @return 返回翻牌成功失败的提示信息
	 */
	@Transactional(readOnly = false)
	public Map<String ,Object> rptDraw(String[] arr ,String dt) {
		Map<String ,Object> res = Maps.newHashMap();
		try{
			List<RptDrawLogInfo> logs = Lists.newArrayList();
			for(int i=0;i < arr.length;i++){
				if(StringUtils.isNotBlank(arr[i])){
					RptRptDrawInfo pec = new RptRptDrawInfo();
					RptDrawLogInfo log = new RptDrawLogInfo();
					pec.setRptId(arr[i]);
					pec.setDrawDate(dt);
					log.setDrawDate(dt);
					log.setDrawObj(GlobalConstants4plugin.DRAW_RPT);
					log.setLogId(RandomUtils.uuid2());
					log.setDrawTime(new Timestamp(System.currentTimeMillis()));
					log.setObjId(arr[i]);
					log.setUserId(BioneSecurityUtils.getCurrentUserId());
					this.saveOrUpdateEntity(pec);
					logs.add(log);
				}
			}
			this.excelBs.saveEntityJdbcBatch(logs);
			res.put("msg", "ok");
		}
		catch(Exception e){
			e.printStackTrace();
			res.put("msg", "error");
		}
		return res;
		
	}
	
	/**
	 * 翻牌日志表的查询
	 * @param firstResult
	 *            第一条记录
	 * @param pageSize
	 *            每页记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            参数列表
	 * @param indexNo 指标编号
	 * @return 返回指标的翻牌日志结果集
	 */
	public SearchResult<Object[]> getIdxDrawLogList(int firstResult,int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap,String indexNo) {
		String jql = "select lg.DRAW_DATE,usr.USER_ID,usr.USER_NAME,lg.DRAW_TIME from RPT_DRAW_LOG_INFO lg left join BIONE_USER_INFO usr on lg.USER_ID = usr.USER_ID where lg.OBJ_ID =:indexNo and lg.DRAW_OBJ =:drawObj ";

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by " + orderBy + " " + orderType;
		}
		
		Map<String, Object> values = Maps.newHashMap();
		values.put("indexNo", indexNo);
		values.put("drawObj", GlobalConstants4plugin.DRAW_IDX);

		SearchResult<Object[]> idxDrawLogList = this.baseDAO
				.findPageWithNameParamByNativeSQL(firstResult, pageSize, jql, values);
		return idxDrawLogList;
	}
	/**
	 * 翻牌日志表的查询
	 * @param firstResult
	 *            第一条记录
	 * @param pageSize
	 *            每页记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            参数列表
	 * @param rptId 报表编号
	 * @return 返回报表的翻牌日志结果集
	 */
	public SearchResult<Object[]> getRptDrawLogList(int firstResult,int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap,String rptId) {
		String jql = "select lg.DRAW_DATE,usr.USER_ID,usr.USER_NAME,lg.DRAW_TIME from RPT_DRAW_LOG_INFO lg left join BIONE_USER_INFO usr on lg.USER_ID = usr.USER_ID where lg.OBJ_ID =:rptId and lg.DRAW_OBJ =:drawObj ";

		if (!StringUtils.isEmpty(orderBy)) {
			jql += "order by " + orderBy + " " + orderType;
		}
		
		Map<String, Object> values = Maps.newHashMap();
		values.put("rptId", rptId);
		values.put("drawObj", GlobalConstants4plugin.DRAW_RPT);

		SearchResult<Object[]> rptDrawLogList = this.baseDAO
				.findPageWithNameParamByNativeSQL(firstResult, pageSize, jql, values);
		return rptDrawLogList;
	}
	
}
