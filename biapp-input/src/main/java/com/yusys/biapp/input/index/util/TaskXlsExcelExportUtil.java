package com.yusys.biapp.input.index.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.collect.Maps;
import com.yusys.biapp.input.common.utils.ExportUtil;
import com.yusys.biapp.input.index.web.vo.DataInputExportTableVO;

public class TaskXlsExcelExportUtil {
	private OutputStream output;
	private Workbook wb = null;
	// private List<Map<String,Object>> infoList=null;
	// private LinkedHashMap<String,String> columnInfo=null;

	private List<DataInputExportTableVO> contents;

	private String path = null;
	private HttpServletResponse response;
	private Map<String, Integer> colMap = new HashMap<String, Integer>();;

	public TaskXlsExcelExportUtil(HttpServletResponse response,
			List<DataInputExportTableVO> contents, String path) {
		this.response = response;
		this.contents = contents;
		this.path = path;
	}

	public void exportFile() {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			output = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		wb = new HSSFWorkbook();
		try {
			Map<String,Integer>nameMap = Maps.newHashMap();
			for (DataInputExportTableVO content : contents) {
				String sheetName = content.getSheetName();
				String showSheetNm;
				if(nameMap.containsKey(sheetName))
				{
					int index = nameMap.get(sheetName).intValue()+1;
					showSheetNm=sheetName+index;
					nameMap.put(sheetName,index );
				}else{
					showSheetNm = sheetName;
					nameMap.put(sheetName, 1);
				}
				Sheet sheet = wb.createSheet(showSheetNm);
				creatHeader(sheet,content.getColumnInfo());
				creatBody(sheet,content.getInfoList());
			}
			wb.write(output);
			if (output != null) {
				output.close();
			}
			ExportUtil.download(response, file, "application/octet-stream");
			file.delete();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void creatHeader(Sheet sheet,LinkedHashMap<String, String> columnInfo) {
		Row row = sheet.createRow(0);
		if (columnInfo.size() > 0) {
			int col = 0;
			for (String key : columnInfo.keySet()) {
				Cell cell = row.createCell(col);
				cell.setCellValue(columnInfo.get(key));
				colMap.put(key, col);
				col++;
			}
		}
	}

	private void creatBody(Sheet sheet,List<Map<String, Object>> infoList) {
		if (infoList.size() > 0) {
			int rol = 1;
			for (Map<String, Object> info : infoList) {
				Row row = sheet.createRow(rol);
				creatValue(row, info);
				rol++;
			}
		}
	}

	private void creatValue(Row row, Map<String, Object> infos) {
		if (infos.size() > 0) {
			for (String key : infos.keySet()) {
				int col = this.colMap.get(key);
				Cell cell = row.createCell(col);
				setCellValue(cell, infos.get(key));
			}
		}
	}

	private void setCellValue(Cell cell, Object value) {
		if (value == null || cell == null) {
			return;
		} else if (value instanceof Number) {
			double doubleValue = ((Number) value).doubleValue();
			cell.setCellValue(doubleValue);
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof Calendar) {
			cell.setCellValue((Calendar) value);
		} else if (isFormulaDefinition(value)) {
			cell.setCellFormula(getFormula(value));
		} else {
			cell.setCellValue(value.toString());
		}
	}

	private boolean isFormulaDefinition(Object obj) {
		if (obj instanceof String) {
			String str = (String) obj;
			if (str.length() < 2) {
				return false;
			} else {
				return ((String) obj).charAt(0) == '=';
			}
		} else {
			return false;
		}
	}

	private String getFormula(Object obj) {
		return ((String) obj).substring(1);
	}

}
