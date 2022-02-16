package com.yusys.biapp.input.template.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.inputTable.entity.RptInputListTableConstraint;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableInfo;
import com.yusys.biapp.input.inputTable.service.InputTableBS;
import com.yusys.biapp.input.inputTable.service.TableConstrainBS;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;

@Service
@Transactional(readOnly = true)
public class TempleConstraintBS extends BaseBS<RptInputLstTempleConst> {
	@Autowired
	private InputTableBS tableBS;
	@Autowired
	private TableConstrainBS priIndexBS;
	/**
	 * 保存主键信息
	 * @param templePrimaryList
	 */
	@Transactional(readOnly = false)
	public void saveTempleKey(String templePrimaryList) {
		RptInputLstTempleConst templeConst = new RptInputLstTempleConst();
		String[] templeColumns = StringUtils.split(templePrimaryList, ',');
		templeConst.setKeyId(RandomUtils.uuid2());
		templeConst.setTempleId(templeColumns[0]);
		templeConst.setKeyName(templeColumns[1]);
		templeConst.setKeyType(templeColumns[2]);
		templeConst.setKeyColumn(templeColumns[3]);
		this.saveEntity(templeConst);
	}
	
	/**
	 * 保存模板主键索引信息
	 * 
	 * @param dsId
	 * @param tableId
	 * @param templeId
	 */
	@Transactional(readOnly = false)
	public void setTempleKeyIndex(String dsId, String tableId, String templeId) {
		String[] propVal = { dsId, tableId };
		String[] propName = { "dsId", "tableEnName" };
		List<RptInputListTableInfo> tableInfo = tableBS.findByPropertys(RptInputListTableInfo.class, propName, propVal);
		if (CollectionUtils.isNotEmpty(tableInfo)) {
			String[] propVal1 = { tableInfo.get(0).getTableId(), "primary" };
			String[] propName1 = { "tableId", "keyType" };
			List<RptInputListTableConstraint> udipPriIndexInfo = priIndexBS.findByPropertys(RptInputListTableConstraint.class, propName1, propVal1);
			if (CollectionUtils.isNotEmpty(udipPriIndexInfo)) {
				RptInputLstTempleConst templePrimary = new RptInputLstTempleConst();
				templePrimary.setKeyId(RandomUtils.uuid2());
				templePrimary.setTempleId(templeId);
				templePrimary.setKeyName("主键");
				templePrimary.setKeyType("primary");
				templePrimary.setKeyColumn(udipPriIndexInfo.get(0).getKeyColumn());
				this.saveEntity(templePrimary);
			}
		}
	}
}
