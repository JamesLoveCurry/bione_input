package com.yusys.bione.plugin.mainpage.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.mainpage.web.vo.MpDetailInfoVO;
import com.yusys.bione.plugin.mainpage.web.vo.ReportHistoryVO;
import com.yusys.bione.plugin.mainpage.web.vo.RptMainpageVO;
@MyBatisRepository
public interface RptMainpageMybatisDao {
	/**
	 * 获取指定系统、用户下，所对应的基本布局信息
	 * @param map
	 * @return
	 */
	public List<RptMainpageVO> getLayoutByUser(Map<String, Object> map);
	/**
	 * 布局明细
	 * @param designId
	 * @return
	 */
	public List<MpDetailInfoVO> getDetailById(String designId);
	/**
	 * 报表访问历史
	 * @param map
	 * @return
	 */
	public List<ReportHistoryVO> getReportHistory(Map<String, Object> map);
	/**
	 * 报表访问历史详细信息
	 * @param map
	 * @return
	 */
	public List<ReportHistoryVO> getReportList(Map<String, Object> map);
	
}
