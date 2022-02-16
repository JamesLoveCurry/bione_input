package com.yusys.biapp.input.inputTable.util;

import java.util.List;

import com.yusys.biapp.input.template.entity.RptInputRewriteFieldInfo;

public interface  IReWriteSql {

	
	public void init(String dsId,String schemaName, String tableName,
			String sourceTable, List<RptInputRewriteFieldInfo> infoList,String taskInstanceId);
	/**
	 * 存在的更新,不存在的新增
	 */
	public String updAndIns() throws Exception;
	/**
	 * 完全替换
	 */
	public String replace()throws Exception;
	/**
	 * 只更新存在的数据
	 */
	public String updateExists()throws Exception;
	/**
	 * 只新增不存在的数据
	 */
	public String insertNotExists()throws Exception;
	
}
