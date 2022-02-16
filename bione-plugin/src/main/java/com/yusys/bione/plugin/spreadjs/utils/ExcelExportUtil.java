package com.yusys.bione.plugin.spreadjs.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;

public class ExcelExportUtil {
	private Workbook workbook;
	private List<Map<String, Object>> infoList = null;
	private Map<String, String> columnInfo = null;
	private Map<String, String> units = null;
	private Map<String, String> formats = null;
	private Map<String, CellStyle> styleMap = new HashMap<String, CellStyle>();
	private String path = null;
	private HttpServletResponse response;
	private Map<String, Integer> colMap = new HashMap<String, Integer>();
	private int nextRow = 1;

	public ExcelExportUtil(HttpServletResponse response, List<Map<String, Object>> infoList,
			Map<String, String> columnInfo, String path) {
		this.response = response;
		this.columnInfo = columnInfo;
		this.infoList = infoList;
		this.path = path;
	}

	@SuppressWarnings("unchecked")
	public ExcelExportUtil(HttpServletResponse response, List<Map<String, Object>> infoList, String columnInfo,
			String format, String unit, String path) {
		this.response = response;
		this.infoList = infoList;
		this.columnInfo = (Map<String, String>)JSON.parseObject(columnInfo, LinkedHashMap.class);
		if (StringUtils.isNotBlank(unit)) {
			this.units = (Map<String, String>)JSON.parseObject(unit, Map.class);
		} else {
			this.units = new HashMap<String, String>();
		}
		if (StringUtils.isNotBlank(format)) {
			this.formats = (Map<String, String>)JSON.parseObject(format, Map.class);
		} else {
			this.formats = new HashMap<String, String>();
		}
		this.path = path;
	}
	
	
	public ExcelExportUtil(HttpServletResponse response, List<Map<String, Object>> infoList, LinkedHashMap<String, String> columnInfo, String path) {
		this.response = response;
		this.infoList = infoList;
		this.columnInfo = (Map<String, String>)columnInfo;
		this.units = new HashMap<String, String>();
		this.formats = new HashMap<String, String>();
		this.path = path;
	}

	public void exportFile(String sheetName) {
		try {
			workbook = WorkbookWrapper.create(path.substring(path.lastIndexOf('.')), false);
			Sheet sheet = workbook.createSheet(sheetName);
			creatHeader(sheet);
			creatBody(sheet);
			File file = new File(path);
			DownloadUtils.printHeader(response, file.getName(), null);
			workbook.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(workbook);
			workbook = null;
		}
	}

	public String createFile(String sheetName) {
		startCreate(sheetName);
		return finish();
	}

	public void startCreate(String sheetName) {
		workbook = WorkbookWrapper.create(path, false);
		Sheet sheet = workbook.createSheet(sheetName);
		creatHeader(sheet);
		creatBody(sheet);
	}

	public String finish(){
		IOUtils.closeQuietly(workbook);
		workbook = null;
		return path;
	}

	public void addRow(Map<String,Object> info){
		Row row = workbook.getSheetAt(0).createRow(nextRow ++);
		creatValue(row, info);
	}

	private void creatHeader(Sheet sheet) {
		Row row = sheet.createRow(0);
		if (columnInfo.size() > 0) {
			int col = 0;
			// 设置头部字体加粗
			CellStyle cellStyle = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			cellStyle.setFont(font);
			for (String key : columnInfo.keySet()) {
				Cell cell = row.createCell(col);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(columnInfo.get(key));
				colMap.put(key, col);
				col++;
			}
		}
	}

	private void creatBody(Sheet sheet) {
		if (infoList.size() > 0) {
			for (Map<String, Object> info : infoList) {
				Row row = sheet.createRow(nextRow ++);
				creatValue(row, info);
			}
		}
	}

	private void creatValue(Row row, Map<String, Object> infos) {
		if (infos.size() > 0) {
			for (String key : infos.keySet()) {
				if (this.colMap.get(key) != null) {
					int col = this.colMap.get(key);
					Cell cell = row.createCell(col);
					if (units != null && units.get(key) != null) {
						setCellValue(cell, getValue(infos.get(key), units.get(key)));
					} else {
						setCellValue(cell, infos.get(key));
					}
					if (formats != null) {
						setCellFormat(cell, formats.get(key));
					}
				}
			}
			//列表导出单元格未空默认显示0.00
/*			for (String key : this.colMap.keySet()) {
				int col = this.colMap.get(key);
				Cell cell = row.createCell(col);
				Object val = infos.get(key);
				if(null == val) {
					val = "0.00";
				}
				if (units != null && units.get(key) != null) {
					setCellValue(cell, getValue(val, units.get(key)));
				} else {
					setCellValue(cell, val);
				}
				if (formats != null) {
					setCellFormat(cell, formats.get(key));
				}
			}*/
		}
	}

	private BigDecimal getValue(Object value, String unit) {
		try {
			BigDecimal dec = new BigDecimal(String.valueOf(value));
			if (unit.equals("01")) { // 元
				return dec;
			} else if (unit.equals("02")) { // 百
				return dec.divide(new BigDecimal(100));
			} else if (unit.equals("03")) { // 千
				return dec.divide(new BigDecimal(1000));
			} else if (unit.equals("04")) { // 万
				return dec.divide(new BigDecimal(10000));
			} else if (unit.equals("05")) { // 亿
				return dec.divide(new BigDecimal(100000000));
			} else {
				return dec;
			}
		} catch (Exception e) {
			return new BigDecimal("0");
		}
	}

	private void setCellFormat(Cell cell, String format) {
		if (format == null) {
			format = "@";	// 默认格式
		}
		CellStyle cs = this.styleMap.get(format);
		if (cs != null) {
			cell.setCellStyle(cs);
		} else {
			cs = workbook.createCellStyle();
			cs.setDataFormat(workbook.createDataFormat().getFormat(format));
			cell.setCellStyle(cs);
			styleMap.put(format, cs);
		}
	}

	private void setCellValue(Cell cell, Object value) {
		if (value == null || cell == null) {
			return;
		} else if (value instanceof Number) {
			double doubleValue = ((Number) value).doubleValue();
			cell.setCellValue(doubleValue);
		} else if (value instanceof BigDecimal) {
			double doubleValue = ((BigDecimal) value).doubleValue();
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
