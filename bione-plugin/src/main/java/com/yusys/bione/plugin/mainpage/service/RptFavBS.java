package com.yusys.bione.plugin.mainpage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.mainpage.web.vo.ModuleMyFavVO;
import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;

@Service
@Transactional(readOnly = true)
public class RptFavBS extends BaseBS<RptFavQueryins> {
	public List<ModuleMyFavVO> findWithSize(int size) {
		String jql = "select new com.yusys.bione.plugin.mainpage.web.vo.ModuleMyFavVO(r, q) from "
				+ "RptFavQueryins q, RptFavFolderInsRel r where q.instanceId=r.id.instanceId "
				+ "order by q.createTime desc";
		SearchResult<ModuleMyFavVO> sr = this.baseDAO.findPageIndexParam(0,
				size, jql);
		return sr.getResult();
	}
}
