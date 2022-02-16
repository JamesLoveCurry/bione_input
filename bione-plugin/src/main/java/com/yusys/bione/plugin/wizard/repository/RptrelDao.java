package com.yusys.bione.plugin.wizard.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.wizard.web.vo.RptrelImportVO;

@MyBatisRepository
public interface RptrelDao  {
	public List<RptMgrReportInfo> findReportInfo(Map<String, Object> params);
	public List<RptSysModuleInfo> findDatasetInfo(Map<String, Object> params);
	public List<RptSysModuleCol> findDatasetItemInfo(Map<String, Object> params);
	public List<RptDimTypeInfo> findDimInfo(Map<String, Object> params);
	public List<RptDimItemInfo> findDimItem(Map<String, Object> params);
	public List<RptDimItemInfo> findDimItemInfo(Map<String, Object> params);
	public List<RptIdxInfo> findIdx(Map<String, Object> params);
	public List<RptrelImportVO> getExcelVO(Map<String, Object> params);
	public Long checkIdx(Map<String, Object> params);
}
