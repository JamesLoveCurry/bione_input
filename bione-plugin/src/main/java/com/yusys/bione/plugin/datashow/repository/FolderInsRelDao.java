package com.yusys.bione.plugin.datashow.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRel;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRelPK;

public interface FolderInsRelDao extends
		PagingAndSortingRepository<RptFavFolderInsRel, RptFavFolderInsRelPK>,
		JpaSpecificationExecutor<RptFavFolderInsRel> {
}
