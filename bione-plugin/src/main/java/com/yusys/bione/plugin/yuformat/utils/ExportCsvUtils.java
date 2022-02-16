package com.yusys.bione.plugin.yuformat.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

public class ExportCsvUtils {

	public static void simpleExport(boolean quoteAllFields, String lineSeparator, String[] heads, List<Object[]> data,
			String fileName, OutputStream outputStream, int onerun) throws UnsupportedEncodingException {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setQuoteAllFields(quoteAllFields);
		// 分割线使用系统默认
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(heads);
		
		OutputStream csvResult = outputStream;
		CsvWriter writer = new CsvWriter(new OutputStreamWriter(csvResult, "GBK"), settings);
		
		writer.writeHeaders();
		writer.writeRows(data);	
		
		writer.flush();
		writer.close();
		
		try {
			csvResult.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
