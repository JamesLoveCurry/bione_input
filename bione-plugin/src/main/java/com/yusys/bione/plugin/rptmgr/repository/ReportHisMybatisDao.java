package com.yusys.bione.plugin.rptmgr.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportHis;

/**
 * 
 * <pre>
 * Title: 报表历史Dao层
 * Description:
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
public interface ReportHisMybatisDao {
	public List<RptMgrReportHis> list(Map<String, Object> map);
	public void save(RptMgrReportHis his);
	public void update(RptMgrReportHis his);
	public void delete(Map<String, Object> map);
}
