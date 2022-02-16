/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.yusys.bione.comp.utils.ReflectionUtils;

/**
 * @author tanxu
 * 
 */
public class SpreadWriterFactory {
	
	public static ISpreadWriter createWriter(String type, File file) throws IOException, InvalidFormatException {
		if (! file.exists()) {
			file.createNewFile();
        }
		if (StringUtils.equals(type, "xls")) {
			return ReflectionUtils.newInstance(SpreadWriterFactory.class.getClassLoader(),
					"com.yusys.bione.plugin.spreadjs.service.FrsXlsExcelSpreadWriter", file);
		} else if (StringUtils.equals(type, "xlsx")) {
			return new XlsxExcelSpreadWriter(file);
		} else if (StringUtils.equals(type, "pdf")) {
			return new PdfSpreadWriter(file, null, null, null);
		} else {
			return new XlsxExcelSpreadWriter(file);
		}
	}
}
