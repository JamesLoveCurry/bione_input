package com.yusys.bione.plugin.engine.repository.mybatis;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Primary;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.plugin.engine.entity.RptEngineIdxRel;
import com.yusys.bione.plugin.engine.entity.RptEngineIdxSt;
import com.yusys.bione.plugin.engine.entity.RptEngineTsk;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.valid.entitiy.RptEngineReportSts;

/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author aman aman@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
@Primary
public interface EngineDao {
	public List<RptEngineIdxSt> getEngineIdxStsList(Map<String, Object> params);

	public List<RptEngineReportSts> getEngineRptStsList(
			Map<String, Object> params);

	public List<RptIdxInfo> getRptIdxInfosByParams(Map<String, Object> params);

	public List<RptMgrReportInfo> getRptMgrReportInfosByParams(Map<String, Object> params);

	public void saveEngineIdxRel(RptEngineIdxRel rel);

	public void updateRptEngineIdxSts(Map<String, Object> params);
	
	public void updateRptEngineReportSts(Map<String, Object> params);
	
	public List<RptEngineTsk> getEngineInfo(Map<String, Object> params);
	
	public void updateRptEngineReportCheckeSts(Map<String, Object> params);

	public void updateRptEngineReportSumSts(Map<String, Object> map);

	public PageMyBatis<RptTaskInstanceInfo> getEngineRptList(
			Map<String, Object> map);

	public PageMyBatis<RptTaskInstanceInfo> getEngineIdxList(
			Map<String, Object> map);

	List<Map<String, Object>> getRptSts(Map<String, Object> map);

	PageMyBatis<RptTaskInstanceInfo> getEngineChildIdxStsList(
			Map<String, Object> param);
}
