package com.yusys.biapp.input.inputTable.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.biapp.input.inputTable.entity.RptInputListTableConstraint;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableFieldInf;
import com.yusys.bione.comp.service.BaseBS;

/**
 * @author guojiangping
 * 
 */
@Service(value = "tableColBS")
@Transactional(readOnly = false)
public class TableFieldBS extends BaseBS<RptInputListTableFieldInf> {

	/**
	 * 通过表英文名获取表字段信息
	 * @param tableEnName
	 * @return
	 */
	public List<RptInputListTableFieldInf> getTableFieldByDsIdAndTableEnName(String dsId, String tableEnName) {
		StringBuilder jql = new StringBuilder(200);
		jql.append("select f from RptInputListTableFieldInf f, RptInputListTableInfo i " +
				"where f.tableId = i.tableId and i.dsId = ?0 and i.tableEnName = ?1");
		return this.baseDAO.findWithIndexParam(jql.toString(), dsId, tableEnName.toUpperCase());
	}
	
	/**
	 * 获取表约束信息
	 * @param dsId
	 * @param tableEnName
	 * @return
	 */
	public List<RptInputListTableConstraint> getTableConstraint(String dsId, String tableEnName) {
		StringBuilder jql = new StringBuilder(200);
		jql.append("select c from RptInputListTableConstraint c, RptInputListTableInfo i " +
				"where c.tableId = i.tableId and i.dsId = ?0 and i.tableEnName = ?1");
		return this.baseDAO.findWithIndexParam(jql.toString(), dsId, tableEnName);
	}
}
