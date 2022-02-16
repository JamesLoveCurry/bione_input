package com.yusys.bione.plugin.paramtmp.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpInfo;

public interface ParamtmpDao extends PagingAndSortingRepository<RptParamtmpInfo, String>,
		JpaSpecificationExecutor<RptParamtmpInfo> {
}
