/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

import java.util.List;
import java.util.Map;

public interface IFrsSpreadWriter extends ISpreadWriter {

	public void write(SpreadSchema schema) throws SpreadExportException, Exception;
	
	public void write(List<String> jsonString,List<Map<String,Object>> rptInfos,String showType, List<Map<String, Object>> cellSpanList) throws SpreadExportException;

	public void writeAll(List<String> jsonString, List<Map<String, Object>> rptInfos, String showType, List<Map<String, Object>> cellSpanList) throws SpreadExportException;
}
