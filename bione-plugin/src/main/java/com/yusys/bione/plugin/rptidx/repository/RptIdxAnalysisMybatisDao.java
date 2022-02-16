package com.yusys.bione.plugin.rptidx.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxInfoRelVO;
import com.yusys.bione.plugin.rptmgr.web.vo.RptMgrInfoVO;

@MyBatisRepository
public interface RptIdxAnalysisMybatisDao {

	
	
	public List<RptIdxInfoRelVO> listIdxByRptId(String rptId);
	
	public List<RptDimTypeInfo> listDimByRptId(String rptId);
	
	public List<RptMgrInfoVO> listRptByRptDim(String dimNo);
	
	public List<RptMgrInfoVO> listRptByRptCfg(Map<String,Object> params);
	
	public List<RptMgrInfoVO> listRptByRptIdx(Map<String,Object> params);
	
	public List<String> listRptIdxByRptId(String rptId);
	
	public List<RptIdxInfoRelVO> getRptIdxByRptId(Map<String,Object> params);
}
