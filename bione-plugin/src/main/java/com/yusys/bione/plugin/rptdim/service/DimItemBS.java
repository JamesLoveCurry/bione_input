package com.yusys.bione.plugin.rptdim.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;

@Service
@Transactional
public class DimItemBS extends BaseBS<RptDimItemInfo> {
	public List<RptDimItemInfo> findById(String dimItemNo, String dimTypeNo) {
		StringBuilder stb = new StringBuilder(1000);
		Map<String, Object> values = Maps.newHashMap();
		values.put("dimTypeNo", dimTypeNo);
		stb.append("select t from RptDimItemInfo t where t.id.dimTypeNo=:dimTypeNo ");
		if (StringUtils.isNotEmpty(dimItemNo)) {
			if (StringUtils.contains(dimItemNo, ',')) {
				String[] itemNos = StringUtils.split(dimItemNo, ',');
				stb.append(" and t.id.dimItemNo in :dimItemNos");
				values.put("dimItemNos", Arrays.asList(itemNos));
			} else {
				stb.append(" and t.id.dimItemNo=:dimItemNo");
				values.put("dimItemNo", dimItemNo);
			}
		}
		return this.baseDAO.findWithNameParm(stb.toString(), values);
	}
}
