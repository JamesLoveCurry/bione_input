package com.yusys.bione.plugin.datashow.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrOuterCfg;
/**
 * <pre>
 * Title: RptshowMybatisDao
 * Description: 
 * </pre>
 * 
 * @author kanglg kanglg@yuchengtech.com
 * @version 1.00.00
 */
@MyBatisRepository
public interface RptShowMybatisDao {
	
	public List<RptMgrOuterCfg> findOuterCfgInfo(Map<String, Object> params);
	
	public List<RptMgrOuterCfg> findCfgByRptId(String rptId);
	public Long getStoreCount(Map<String, Object> params);
}
