package com.yusys.bione.plugin.datashow.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.plugin.rptmgr.entity.RptMgrOuterCfg;

public interface RptOuterCfgDao extends
		PagingAndSortingRepository<RptMgrOuterCfg, String>,
		JpaSpecificationExecutor<RptMgrOuterCfg> {
	public RptMgrOuterCfg findByCfgId(String cfgId);
}
