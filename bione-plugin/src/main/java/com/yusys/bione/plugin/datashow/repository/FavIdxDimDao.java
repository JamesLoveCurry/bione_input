package com.yusys.bione.plugin.datashow.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDim;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimPK;

public interface FavIdxDimDao extends JpaSpecificationExecutor<RptFavIdxDim>,
		PagingAndSortingRepository<RptFavIdxDim, RptFavIdxDimPK> {
}
