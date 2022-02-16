package com.yusys.bione.plugin.idxana.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;

@Service
@Transactional(readOnly = true)
public class RptIdxDimRelBS extends BaseBS<RptIdxDimRel>{

	/**
	 * 根据index_no查找对象
	 * @param index_no
	 * @return
	 */
	public List<RptIdxDimRel> getRptIdxDimRelByIdxno(String propertyName,String index_no){
		List<RptIdxDimRel> list = findEntityListByProperty(propertyName, index_no);
		return list;
	}
}
