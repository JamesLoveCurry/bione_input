/**
 * 
 */
package com.yusys.bione.frame.excel;

/**
 * @author tanxu
 *
 */
public interface IExporter {
	
	void run() throws BioneExporterException;
	
	void destory() throws BioneExporterException;
	
}
