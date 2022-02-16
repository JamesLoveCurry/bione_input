package com.yusys.bione.plugin.rptmgr.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptmgr.entity.RptConcernOrg;
import com.yusys.bione.plugin.rptmgr.entity.RptConcernRpt;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrModuleIdxRelVO;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrModuleRelVO;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrRptitemIdxRelVO;

/**
 * 
 * <pre>
 * Title: 报表管理Dao层
 * Description:
 * </pre>
 * 
 * @author weijiaxiang weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
public interface FrsRptMgrInfoMybatisDao {
	
	public List<RptMgrModuleRelVO> getRptModuleRelByRptId(String rptId);
	
	public List<RptMgrModuleIdxRelVO> getRptMgrModuleIdxRelByParams(Map<String, Object> params);
	
	public List<RptMgrRptitemIdxRelVO> getIdxByParams(Map<String, Object> params);
	
	public void saveRptConcernOrg(RptConcernOrg rptConcernOrg);
	
	public void saveRptConcernRpt(RptConcernRpt rptConcernRpt);
	
	public void deleteRptConcernOrgOfUser(Map<String, Object> params);
	
	public void deleteRptConcernRptOfUser(Map<String, Object> params);
	
	public List<RptConcernOrg> getRptConcernOrgOfUser(Map<String, Object> params);
	
	public List<RptConcernRpt> getRptConcernRptOfUser(Map<String, Object> params);
	
	public List<RptMgrReportInfo> getEastRptTreeNodes(Map<String, Object> paramMap);
	
	public RptMgrReportInfo getRptInfoByParams(Map<String,Object> params);

}
