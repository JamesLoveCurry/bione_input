package com.yusys.bione.plugin.mainpage.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.frame.mainpage.web.vo.MpDetailInfoVO;
import com.yusys.bione.plugin.mainpage.repository.RptMainpageMybatisDao;
import com.yusys.bione.plugin.mainpage.web.vo.ReportHistoryVO;
@Service
@Transactional(readOnly=true)
public class RptMainpageBS extends BaseBS<Object> {
	// 报表指标首页
	final String RPTIDXMAINPAGEID = "04c14c886c6a4ba0a3e40de8563cyu98";
	// 图表展示首页
	final String CHARTSMAINPAGEID = "04c14c886c6a4ba0a3e40de8563cyu99";
	// 收藏报表首页
	final String MYREPORTMAINPAGEID = "04c14c886c6a4ba0a3e40de8563cy100";
	// 报表指标首页
	final String RPTIDXMAINPAGENAME = "报表指标首页";
	// 图表展示首页
	final String CHARTSMAINPAGENAME = "图表展示首页";
	// 收藏报表首页
	final String MYREPORTMAINPAGENAME = "收藏报表首页";
	@Autowired
	private RptMainpageMybatisDao mainpageDao;
	
	/**
	 * 
	 * @param designId
	 * @return
	 */
	public List<MpDetailInfoVO> getDetailById(String designId) {
		return this.mainpageDao.getDetailById(designId);
	}
	

	
	/**
	 * 获取报表访问历史
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getReportHistory(Pager pager,String userId) {
		Map<String,Object> map = Maps.newHashMap();
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String startAccess = ft.format(DateUtils.getPastDate(60));
		String endAccess =ft.format(date);
		map.put("userId", userId);
		map.put("startAccess", startAccess);
		map.put("endAccess", endAccess);
		//map.put("pageSize", pageSize);
		if(pager.getSortname()==null||"".equals(pager.getSortname())){
			pager.setSortname("accessTime");
			pager.setSortorder("desc");
		}
		PageHelper.startPage(pager);
		PageMyBatis<ReportHistoryVO> rh = (PageMyBatis<ReportHistoryVO>)this.mainpageDao.getReportHistory(map);
		//List<RptMgrReportInfo> rh = this.mainpageDao.getReportHistory(map);
		if(rh!=null&&rh.size()>0){
			map.clear();
			map.put("Rows",rh.getResult());
			map.put("Total",rh.getTotalCount());
			return map;
		}
		return null;
	}
	
	/**
	 * 获取报表访问历史
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getReportList(Pager pager,String userId,String rptId) {
		Map<String,Object> map = Maps.newHashMap();
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String startAccess = ft.format(DateUtils.getPastDate(7));
		String endAccess =ft.format(date);
		map.put("userId", userId);
		map.put("rptId", rptId);
		map.put("startAccess", startAccess);
		map.put("endAccess", endAccess);
		//map.put("pageSize", pageSize);
		PageHelper.startPage(pager);
		PageMyBatis<ReportHistoryVO> rh = (PageMyBatis<ReportHistoryVO>)this.mainpageDao.getReportList(map);
		//List<RptMgrReportInfo> rh = this.mainpageDao.getReportHistory(map);
		if(rh!=null&&rh.size()>0){
			map.clear();
			map.put("Rows",rh.getResult());
			map.put("Total",rh.getTotalCount());
			return map;
		}
		return null;
	}
	/**
	 * 返回数据：首页-图表分析-月新增指标数
	 * @return
	 */
	public List<String[]> getIdxData(String day){
		String jql = " select count(t.createDate),t.createDate from RptIdxInfo t "
				+ " where t.createDate IS NOT NULL and t.createDate >= ?0 "
				+ " and t.isRptIndex = ?1 and t.id.indexVerId = ?2 group by t.createDate order by t.createDate ";
		return this.baseDAO.findWithIndexParam(jql,day,"N",Long.valueOf("1"));
	}
	
	/**
	 * 返回数据：首页-图表分析-报表数量统计
	 * @return
	 */
	public List<Object[]> getRptNum(){
		String jql = "  select count(t.rpt_id),t.rpt_line_no,t1.line_nm from RPT_MGR_FRS_EXT t "
				+ " left join RPT_MGR_FRS_LINE t1 on t1.line_id = t.rpt_line_no "
				+ " left join RPT_MGR_REPORT_INFO t2 on t.rpt_id = t2.rpt_id "
				+ " where t.rpt_line_no is not null and t2.def_src = ?0 "
				+ " group by t.rpt_line_no,t1.line_nm";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql,"01");
	}
	/**
	 * 返回数据：首页-图表分析-报表数量统计
	 * @return 所有报表一级目录
	 */
	public List<Object[]> getRptCatalog(){
		String jql = " select t.CATALOG_ID,t.CATALOG_NM from RPT_MGR_REPORT_CATALOG t where t.UP_CATALOG_ID = '0'";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	
	//返回所有报表非一级目录
	public List<Object[]> getRptCatalogNo(){
		String jql = " select t.CATALOG_ID,t.CATALOG_NM from RPT_MGR_REPORT_CATALOG t where t.UP_CATALOG_ID != '0' ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	//获取对应目录ID的上级目录ID
	public List<String> getRptCatalogUp(String catalogId){
		String jql = " select t.upCatalogId from RptMgrReportCatalog t where t.id.catalogId =?0 ";
		return this.baseDAO.findWithIndexParam(jql, catalogId);
	}
	
	/**
	 * 返回数据：首页-图表分析-指标数量统计
	 * @return
	 */
	public List<Object[]> getIdxNum(){
		String jql = " select sum(pv),t4.line_no,t4.line_nm from"
				+ " (select t1.pv,t2.line_no,t3.line_nm from (select count(t.index_no) pv,t.Belong_dept_id"
				+ " from RPT_IDX_INFO t where t.Belong_dept_id is not null and t.IS_RPT_INDEX = ?0 "
				+ " and t.END_DATE = ?1 group by t.Belong_dept_id）t1"
				+ " left join RPT_MGR_DEP_LINE t2 on t1.Belong_dept_id = t2.dep_no"
				+ " left join RPT_MGR_FRS_LINE t3 on t2.line_no = t3.line_id)t4   group by t4.line_no,t4.line_nm  ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql,"N","29991231");
	}
	
	/**
	 * 返回数据：首页-图表分析-指标数量统计
	 * @return 所有指标一级目录
	 */
	public List<Object[]> getIdxCatalog(){
		String jql = " select t.INDEX_CATALOG_NO,t.INDEX_CATALOG_NM from RPT_IDX_CATALOG t where t.UP_NO = '0'";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	//返回所有指标非一级目录
	public List<Object[]> getIdxCatalogNo(){
		String jql = " select t.INDEX_CATALOG_NO,t.INDEX_CATALOG_NM from RPT_IDX_CATALOG t where t.UP_NO != '0' ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	//获取对应目录ID的上级目录ID
	public List<String> getIdxCatalogUp(String catalogId){
		String jql = " select t.upNo from RptIdxCatalog t where t.id.indexCatalogNo =?0 ";
		return this.baseDAO.findWithIndexParam(jql, catalogId);
	}
	
	/**
	 * 返回数据：首页-图表分析-报表使用情况统计（图1）
	 * @return 
	 */
	//获取所有业务条线
	public List<Object[]> getDepLineList(){
		String jql = " select t.LINE_ID,t.LINE_NM from RPT_MGR_FRS_LINE t ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	//报表使用情况统计--按业务条线
	public List<Object[]> getRptLineData(){
		String jql = " select sum(t2.pv),t2.line_no,line.line_nm from"
				+ " (select t1.user_id,u.user_name ,u.dept_no,depLine.Line_No, pv "
				+ " from(SELECT user_id, COUNT(*) pv FROM RPT_MGR_REPORT_HIS his "
				+ " WHERE 1=1 GROUP BY user_id) t1 "
				+ " inner join BIONE_USER_INFO u on t1.user_id = u.user_id "
				+ " inner join RPT_MGR_DEP_LINE depLine on depLine.Dep_No = u.dept_no "
				+ " where depLine.Dep_No is not null)t2 "
				+ " left join RPT_MGR_FRS_LINE line on line.line_id = t2.line_no "
				+ " group by t2.line_no,line.line_nm ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	public List<Object[]> getRptUse(){
		String jql = " select sum(t2.pv),t2.catalog_id,cat.catalog_nm from (SELECT t1.rpt_id,report.rpt_nm,report.catalog_id,pv "
				+ " FROM ( SELECT rpt_id, COUNT(*) pv FROM RPT_MGR_REPORT_HIS his  where 1=1 GROUP BY  rpt_id) t1,"
				+ " RPT_MGR_REPORT_INFO report  where report.rpt_id = t1.rpt_id) t2 "
				+ " left join  RPT_MGR_REPORT_CATALOG cat on cat.catalog_id = t2.catalog_id group by t2.catalog_id,cat.catalog_nm ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
		
	/**
	 * 获取所有二级机构
	 * @return 
	 */
	public List<Object[]> getRptOrg(){
		String jql = " select t.ORG_NO,t.ORG_NM from RPT_CABIN_ORG_INFO t where t.org_type = '0' order by t.org_no desc";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	//返回所有非一级、二级的机构
	public List<Object[]> getOrgNo(){
		String jql = " select t.ORG_NO,t.ORG_NAME from BIONE_ORG_INFO t where t.UP_NO != '0' "
				+ " and t.UP_NO not in(select t1.UP_NO from BIONE_ORG_INFO t1 "
				+ " where t1.UP_NO in(select t.ORG_NO from BIONE_ORG_INFO t  where t.UP_NO = '0')) ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	//获取对应机构ID的上级机构ID
	public List<String> getRptOrgUp(String orgNo){
		String jql = " select t.upNo from BioneOrgInfo t where t.orgNo =?0 ";
		return this.baseDAO.findWithIndexParam(jql, orgNo);
	}
	/**
	 * 返回数据：首页-图表分析-报表使用情况统计（图2）
	 * @return 
	 */
	public List<Object[]> getRptUse1(){
		String jql = " select sum(t3.pv),t3.ORG_NO,t3.ORG_NAME from(select t.user_id,t1.user_name,t1.ORG_NO,t2.org_name,t.pv "
				+ " from(SELECT user_id, COUNT(*) pv FROM RPT_MGR_REPORT_HIS his  where 1=1 GROUP BY  user_id) t "
				+ " inner join BIONE_USER_INFO t1 on t.user_id = t1.user_id "
				+ " inner join BIONE_ORG_INFO t2 on t1.org_no = t2.org_no)t3 "
				+ " where t3.org_no not in(select t.ORG_NO from BIONE_ORG_INFO t where t.UP_NO = '0') group by t3.ORG_NO,t3.ORG_NAME ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	
	/**
	 * 返回数据：首页-图表分析-指标使用情况统计（图1）
	 * @return 
	 */
	
	//指标使用情况统计--按业务条线
	public List<Object[]> getIdxLineData(){
		String jql = " select sum(t2.pv),t2.line_no,line.line_nm from"
				+ " (select t1.user_id,u.user_name ,u.dept_no,depLine.Line_No, pv "
				+ " from(SELECT user_id, COUNT(*) pv FROM RPT_IDX_VISIT_HIS his "
				+ " WHERE 1=1 GROUP BY user_id) t1 "
				+ " inner join BIONE_USER_INFO u on t1.user_id = u.user_id "
				+ " inner join RPT_MGR_DEP_LINE depLine on depLine.Dep_No = u.dept_no "
				+ " where depLine.Dep_No is not null)t2 "
				+ " left join RPT_MGR_FRS_LINE line on line.line_id = t2.line_no "
				+ " group by t2.line_no,line.line_nm ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
		}
	
	public List<Object[]> getIdxUse(){
		String jql = " select sum(t2.pv),t2.index_catalog_no,cat.index_catalog_nm from ( SELECT t1.index_no,idx.index_nm,idx.index_catalog_no,pv "
				+ " FROM ( SELECT index_no, COUNT(*) pv FROM RPT_IDX_VISIT_HIS his  where 1=1 GROUP BY  index_no) t1,"
				+ " RPT_IDX_INFO idx  where idx.index_no = t1.index_no) t2 "
				+ " left join  RPT_IDX_CATALOG cat on cat.index_catalog_no = t2.index_catalog_no "
				+ " group by t2.index_catalog_no,cat.index_catalog_nm ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	
	/**
	 * 返回数据：首页-图表分析-指标使用情况统计（图2）
	 * @return 
	 */
	public List<Object[]> getIdxUse1(){
		String jql = " select sum(t3.pv),t3.ORG_NO,t3.ORG_NAME from(select t.user_id,t1.user_name,t1.ORG_NO,t2.org_name,t.pv "
				+ " from(SELECT user_id, COUNT(*) pv FROM RPT_IDX_VISIT_HIS his  where 1=1 GROUP BY  user_id) t "
				+ " inner join BIONE_USER_INFO t1 on t.user_id = t1.user_id "
				+ " inner join BIONE_ORG_INFO t2 on t1.org_no = t2.org_no)t3 "
				+ " where t3.org_no not in(select t.ORG_NO from BIONE_ORG_INFO t where t.UP_NO = '0') group by t3.ORG_NO,t3.ORG_NAME ";
		return this.baseDAO.findByNativeSQLWithIndexParam(jql);
	}
	
	
	public List<Object[]> commonLayout(String sql){
		if (StringUtils.isEmpty(sql)) {
			throw new NullPointerException("empty sql");
		}
		return this.baseDAO.findByNativeSQLWithIndexParam(sql);
	}
}
