/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

/**
 * @author tanxu
 *
 */
public interface ISpreadWriter {

	public void write(String jsonString,String templatePath, List<Map<String, Object>> cellSpanList) throws SpreadExportException, Exception;
	
	public void write(SpreadSchema schema) throws SpreadExportException, Exception;
	
	public void write(List<String> jsonString,List<Map<String,String>> rptInfos) throws SpreadExportException;

	public void write(String jsonString, List<List<?>> allEntitis) throws SpreadExportException, IOException;

	public void write(String jsonString,Map<String, String> titles, List<List<?>> allEntitis) throws SpreadExportException, IOException;

	public void write(List<String> jsonString,List<Map<String,Object>> rptInfos,String showType) throws SpreadExportException;

	public void destory() throws SpreadExportException;
}
