package com.yusys.bione.plugin.datashow.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.plugin.rptfav.entity.RptFavFolder;

public interface FavFolderDao extends
		PagingAndSortingRepository<RptFavFolder, String>,
		JpaSpecificationExecutor<RptFavFolder> {
}
