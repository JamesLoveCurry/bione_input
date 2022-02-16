/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author tanxu
 *
 */
public class ExcelReaderFactory {
	
	public static IExcelReader createReader(File file) throws IOException {
		if (! file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
    	return new ExcelReader(file);
	}
}
