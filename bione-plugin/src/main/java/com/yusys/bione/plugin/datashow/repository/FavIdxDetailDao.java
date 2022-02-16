package com.yusys.bione.plugin.datashow.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDetail;

public interface FavIdxDetailDao extends JpaSpecificationExecutor<RptFavIdxDetail>,
		PagingAndSortingRepository<RptFavIdxDetail, String> {
}
