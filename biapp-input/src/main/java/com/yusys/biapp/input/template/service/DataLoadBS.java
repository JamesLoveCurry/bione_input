package com.yusys.biapp.input.template.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.template.entity.RptInputLstDataLoadInfo;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
@Service
@Transactional(readOnly = true)
public class DataLoadBS extends BaseBS<RptInputLstDataLoadInfo> {
	/**
	 * 保存预加载信息
	 * @param templePrimaryList
	 * @param templeId
	 */
	@Transactional(readOnly = false)
	public void saveDataload(String templePrimaryList,String templeId) {
		RptInputLstDataLoadInfo ruleItem = new RptInputLstDataLoadInfo();
		String[] templeColumns = templePrimaryList.split(",");
		ruleItem.setId(RandomUtils.uuid2());
		ruleItem.setTempleId(templeId);
		ruleItem.setConditionColumn(templeColumns[1]);
		ruleItem.setConditionVal(templeColumns[2]);
		this.saveEntity(ruleItem);
	}

}
