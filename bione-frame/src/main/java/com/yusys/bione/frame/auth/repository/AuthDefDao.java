package com.yusys.bione.frame.auth.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;

public interface AuthDefDao extends PagingAndSortingRepository<BioneAuthObjDef, String>,
		JpaSpecificationExecutor<BioneAuthObjDef> {
	BioneAuthObjDef findByObjDefNo(String objDefNo);
}
