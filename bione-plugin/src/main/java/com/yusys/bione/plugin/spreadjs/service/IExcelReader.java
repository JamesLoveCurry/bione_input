/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.util.Map;

import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

/**
 * @author tanxu
 *
 */
public interface IExcelReader {
	
	SpreadSchema read() throws SpreadImportException;
	
	Map<String,Object> readString() throws SpreadImportException;
	
	String readTmpToString() throws SpreadImportException;
}
