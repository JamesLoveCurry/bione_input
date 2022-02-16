package com.yusys.bione.frame.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.repository.AuthMybatisDao;

@Service
@Transactional(readOnly = true)
public class AuthObjDefBS extends BaseBS<BioneAuthObjDef> {
	@Autowired
	private AuthMybatisDao authMybatisDao;

	public SearchResult<BioneAuthObjDef> getAuthObjDefList(Pager pager) {
		// jpa例子
		// Page<BioneAuthObjDef> list =
		// authDefDao.findAll(this.buildSpecification(pager.getSearchFilters()),
		// pager.getPageRequest());
		// // 可以直接返回Page,以下代码是为了适应老的controller
		// SearchResult<BioneAuthObjDef> results = new
		// SearchResult<BioneAuthObjDef>();
		// results.setTotalCount(list.getTotalElements());
		// results.setResult(list.getContent());

		// mybatis例子
		PageHelper.startPage(pager);
		PageMyBatis<BioneAuthObjDef> page = (PageMyBatis<BioneAuthObjDef>) authMybatisDao.search();
		SearchResult<BioneAuthObjDef> results = new SearchResult<BioneAuthObjDef>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}
}
