package com.yusys.bione.plugin.datashow.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimFilter;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimFilterPK;

public interface FavIdxDimFilterDao extends JpaSpecificationExecutor<RptFavIdxDimFilter>,
		PagingAndSortingRepository<RptFavIdxDimFilter, RptFavIdxDimFilterPK> {

}
