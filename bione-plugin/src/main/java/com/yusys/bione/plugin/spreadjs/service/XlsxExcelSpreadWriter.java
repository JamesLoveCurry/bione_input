/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yusys.bione.plugin.design.web.vo.*;
import com.yusys.bione.plugin.regulation.enums.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.ClassMapping;
import com.yusys.bione.frame.excel.ColumnBean;
import com.yusys.bione.frame.excel.ExcelAnnotationUtil;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.spreadjs.entity.DataTable;
import com.yusys.bione.plugin.spreadjs.entity.DataTableProperty;
import com.yusys.bione.plugin.spreadjs.entity.DataTablePropertyProperty;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.Span;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

/**
 * @author tanxu
 *
 */
public class XlsxExcelSpreadWriter extends AbstractSpreadWriter {
	
	private static Logger logger = LoggerFactory.getLogger(XlsxExcelSpreadWriter.class);

	private String filePath;

	public XlsxExcelSpreadWriter(File file) throws FileNotFoundException {
		this.filePath = file.getAbsolutePath();
	}
	
	@Override
	public void write(String jsonString,String templatePath, List<Map<String, Object>> cellSpanList) throws SpreadExportException {
		SpreadSchema spreadSchema = createSpreadSchema(jsonString);
		XSSFWorkbook workbook = null;
		OutputStream out = null;
		try {
			workbook = new XSSFWorkbook();
			cscreater = new XlsxCellStyleCreator(workbook, spreadSchema);
			Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
			//???????????????????????????
			List<Span> spans = new ArrayList<Span>();
			List<Span> spans2 = new ArrayList<Span>();
			if(null != cellSpanList && cellSpanList.size() > 0){
				for(Map<String, Object> map : cellSpanList){
					Span span = new Span();
					span.setCol((int)map.get("beginCol"));
					span.setRow((int)map.get("beginRow"));
					span.setColCount((int)map.get("colSpan"));
					span.setRowCount((int)map.get("rowSpan"));
					spans.add(span);
					spans2.add(span);
				}
			}
			//?????????????????????
			List<Sheet> sheetlist=new ArrayList<Sheet>();
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				sp.getColumns();
				//??????sheet??????????????????
				Sheet ssheet = workbook.createSheet(sp.getName());
				sheetlist.add(ssheet);
				spans2.addAll(sp.getSpans());
				sp.setSpans(spans2);
				setSheetSpan(ssheet, sp);
				setSheetData(ssheet, sp, spans);
			}
			//setFormula(swb);
			workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
			int i=0;
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				Sheet ssheet = sheetlist.get(i);
				setColumnsWidth(ssheet, sp);
				setRowsHeight(ssheet, sp);
				i++;
			}
			out = new FileOutputStream(filePath);
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(workbook);
		}
	}
	
	@Override
	public void destory() {
	}
	
	protected void createBody(Sheet ssheet, DataTable dataTable, List<Span> spans) {
		Map<String, DataTableProperty> dtps = dataTable.getAdditionalProperties();
		for (String rowIndex : dtps.keySet()) {
			Row row = ssheet.createRow(NumberUtils.toInt(rowIndex));
			Map<String, Object> dtpps = dtps.get(rowIndex).getAdditionalProperties();
			for (String colIndex : dtpps.keySet()) {
				if(null != spans && spans.size() > 0){//????????????????????????
					for(int i=0; i < spans.size(); i++){
						int firstRow = spans.get(i).getRow();
						int lastRow = spans.get(i).getRow() + spans.get(i).getRowCount() - 1;
						int col = spans.get(i).getCol();
						if(col == Integer.parseInt(colIndex) && Integer.parseInt(rowIndex) > firstRow && Integer.parseInt(rowIndex) <= lastRow){//???????????????????????????????????????????????????????????????
							Object obj = dtpps.get(colIndex);
							if (obj != null && obj instanceof DataTablePropertyProperty) {
								DataTablePropertyProperty dtpp = (DataTablePropertyProperty) obj;
								Cell cell = row.createCell(NumberUtils.toInt(colIndex));
								setCellStyle(cell , dtpp.getStyle());
							}
							break;
						}else{
							Object obj = dtpps.get(colIndex);
							if (obj != null && obj instanceof DataTablePropertyProperty) {
								DataTablePropertyProperty dtpp = (DataTablePropertyProperty) obj;
								Cell cell = row.createCell(NumberUtils.toInt(colIndex));
								setCellStyle(cell , dtpp.getStyle());
								if (StringUtils.isBlank(dtpp.getFormula())) {
									setCellValue(cell, dtpp.getValue());
								} else {
									String formula = dtpp.getFormula();
									cell.setCellType(CellType.FORMULA);
									try {
										if (formula.startsWith("=")) {
											cell.setCellFormula(formula.substring(1));
										} else {
											cell.setCellFormula(formula);
										}
										cell.setCellValue(cell.getCellFormula());
									} catch (FormulaParseException e) {
										cell.setCellFormula(null);
										StringBuilder sb = new StringBuilder();
										sb.append("Illegal Formula???[").append(filePath).append("][");
										sb.append(cell.getAddress().toString());
										sb.append("]").append(formula);
										logger.error(sb.toString());
										e.printStackTrace();
									}
								}
							}
						}
					}
				}else{
					Object obj = dtpps.get(colIndex);
					if (obj != null && obj instanceof DataTablePropertyProperty) {
						DataTablePropertyProperty dtpp = (DataTablePropertyProperty) obj;
						Cell cell = row.createCell(NumberUtils.toInt(colIndex));
						setCellStyle(cell , dtpp.getStyle());
						if (StringUtils.isBlank(dtpp.getFormula())) {
							setCellValue(cell, dtpp.getValue());
						} else {
							String formula = dtpp.getFormula();
							cell.setCellType(CellType.FORMULA);
							try {
								if (formula.startsWith("=")) {
									cell.setCellFormula(formula.substring(1));
								} else {
									cell.setCellFormula(formula);
								}
								cell.setCellValue(cell.getCellFormula());
							} catch (FormulaParseException e) {
								cell.setCellFormula(null);
								StringBuilder sb = new StringBuilder();
								sb.append("Illegal Formula???[").append(filePath).append("][");
								sb.append(cell.getAddress().toString());
								sb.append("]").append(formula);
								logger.error(sb.toString());
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		ssheet.setForceFormulaRecalculation(true);
		// ????????????????????????
		// resetComboxCell(ssheet, columnBean, firstRow, entitis.size());
	}

	@Override
	public void write(List<String> jsonStrings,
			List<Map<String, String>> rptInfos) throws SpreadExportException {
		// TODO Auto-generated method stub
		SpreadSchema spreadSchema  = new SpreadSchema();
		if(jsonStrings!=null&&jsonStrings.size()>0){
			spreadSchema=createSpreadSchema(jsonStrings.get(0));
			spreadSchema.getSheetsProperties().clear();
			for(int i=0;i<jsonStrings.size();i++){
				SpreadSchema schema = createSpreadSchema(jsonStrings.get(i));
				String key=schema.getSheetsProperties().keySet().iterator().next();
				Map<String,String> map=rptInfos.get(i);
				String rptNum=map.get("rptNum");
				//String rptNm=StringUtils.substring(map.get("rptNm"),0, 31-String.valueOf(i).length()-map.get("org").length()-map.get("dataDate").length()-map.get("rptNum").length()-1);
				String sheetNm=rptNum+"_"+map.get("org")+"_"+map.get("dataDate");
				schema.getSheetsProperties().get(key).setName(sheetNm);
				SheetsProperty sheets=schema.getSheetsProperties().get(key);
				sheets.setNamedStyles(schema.getNamedStyles());
				spreadSchema.getSheetsProperties().put(sheetNm, sheets);
			}
		}
		XSSFWorkbook workbook = null;
		OutputStream out = null;
		try {
			workbook = new XSSFWorkbook();
			Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
			//?????????????????????
			List<Sheet> sheetlist=new ArrayList<Sheet>();
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				cscreater = new XlsxCellStyleCreator(workbook, spreadSchema,sp);
				//??????sheet??????????????????
				Sheet ssheet = workbook.createSheet(sp.getName());
				setSheetSpan(ssheet, sp);
				setSheetData(ssheet, sp, null);
			}
			//setFormula(hwb);
			workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
			int i=0;
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				Sheet ssheet = sheetlist.get(i);
				setColumnsWidth(ssheet, sp);
				setRowsHeight(ssheet, sp);
				i++;
			}
			out = new FileOutputStream(filePath);
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(workbook);
		}
	}

	@Override
	public void run() throws BioneExporterException {
	}

	@Override
	public void write(SpreadSchema schema) throws SpreadExportException,
			IOException {
		SpreadSchema spreadSchema = schema;
		XSSFWorkbook workbook = null;
		OutputStream out = null;
		try {
			workbook = new XSSFWorkbook();
			cscreater = new XlsxCellStyleCreator(workbook, spreadSchema);
			Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
			//?????????????????????
			List<Sheet> sheetlist=new ArrayList<Sheet>();
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				sp.getColumns();
				//??????sheet??????????????????
				Sheet ssheet = workbook.createSheet(sp.getName());
				setSheetSpan(ssheet, sp);
				setSheetData(ssheet, sp, null);
				sheetlist.add(ssheet);
			}
			workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
			int i=0;
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				Sheet ssheet = sheetlist.get(i);
				setColumnsWidth(ssheet, sp);
				setRowsHeight(ssheet, sp);
				i++;
			}
			out = new FileOutputStream(filePath);
			workbook.write(out);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(workbook);
		}
	}

	@Override
	public void write(String jsonString, List<List<?>> allEntitis)
			throws SpreadExportException, IOException {
		// TODO Auto-generated method stub
		SpreadSchema spreadSchema = createSpreadSchema(jsonString);
		XSSFWorkbook workbook = null;
		OutputStream out = null;
		try {
			workbook = new XSSFWorkbook();
			cscreater = new XlsxCellStyleCreator(workbook, spreadSchema);
			Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
			//?????????????????????
			List<Sheet> sheetlist=new ArrayList<Sheet>();
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				sp.getColumns();
				//??????sheet??????????????????
				Sheet ssheet = workbook.createSheet("????????????");
				tmpsh = ssheet;
				sheetlist.add(ssheet);
				setSheetSpan(ssheet, sp);
				setSheetData(ssheet, sp, null);
			}
			//setFormula(swb);
			int i=0;
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				Sheet ssheet = sheetlist.get(i);
				setColumnsWidth(ssheet, sp);
				setRowsHeight(ssheet, sp);
				i++;
			}
			for (List<?> entitis :  allEntitis) {
				if(entitis.size()>0){
					Class<?> clazz = getEntitisClass(entitis);
					ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
					if(excelSheet.extType().equals(GlobalConstants4plugin.EXT_V)){
						this.runV(workbook, entitis);
					}
					if(excelSheet.extType().equals(GlobalConstants4plugin.EXT_H)){
						this.runH(workbook, entitis);
					}
				}
			}
			workbook.setSheetOrder("????????????", 1);
			workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
			out = new FileOutputStream(filePath);
			workbook.write(out);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(workbook);
		}
	}

	public void runV(XSSFWorkbook swb, List<?> entitis) throws SpreadExportException {
		try {
			ExcelSheet excelSheet = null;
			Class<?> clazz;
			List<ColumnBean> columnBeans = new ArrayList<ColumnBean>();
			if (entitis.size() > 0
					&& (clazz = getEntitisClass(entitis)) != null
					&& clazz.isAnnotationPresent(ExcelSheet.class)) {
				excelSheet = clazz.getAnnotation(ExcelSheet.class);
				columnBeans.add(ClassMapping.getInstance().getMappingColumn(
						clazz));
				while (!clazz.getSuperclass().equals(Object.class)) {
					clazz = clazz.getSuperclass();
					columnBeans.add(ClassMapping.getInstance()
							.getMappingColumn(clazz));
				}
				Sheet ssheet = null;
				String insertType = excelSheet.insertType();
				int col = 0;
				if(GlobalConstants4plugin.EXCEL_INSERT_TYPE.equals(insertType)){
					if(swb.getSheetIndex(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet))>=0){
						ssheet = swb.getSheetAt(swb.getSheetIndex(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet)));
						col = ssheet.getRow(0).getLastCellNum();
					}
					else{
						ssheet = swb.createSheet(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet));
						col = 0;
					}
				}
				else{
					ssheet = swb.createSheet(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet));
				}
				createHead(ssheet, columnBeans,excelSheet.titleFlag(),excelSheet.firstRow(),col, CsTitle(swb),CsFirst(swb),excelSheet.insertType());
				createBody(ssheet, columnBeans, entitis, excelSheet.firstRow(),col,CsTitle(swb),CsOdd(swb),CsEven(swb),excelSheet.relInfo());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SpreadExportException(e);
		}
	}
	
	public void runH(XSSFWorkbook swb, List<?> entitis) throws SpreadExportException {
		try {
			ExcelSheet excelSheet = null;
			Class<?> clazz;
			List<ColumnBean> columnBeans = new ArrayList<ColumnBean>();
			if (entitis.size() > 0 && (clazz = getEntitisClass(entitis)) != null && clazz.isAnnotationPresent(ExcelSheet.class)) {
				excelSheet = clazz.getAnnotation(ExcelSheet.class);
				columnBeans.add(ClassMapping.getInstance().getMappingColumn(clazz));
				while(!clazz.getSuperclass().equals(Object.class)){
					clazz = clazz.getSuperclass();
					columnBeans.add(ClassMapping.getInstance().getMappingColumn(clazz));
				}
				Sheet ssheet = null;
				String insertType = excelSheet.insertType();
				int row = 0;
				if(GlobalConstants4plugin.EXCEL_INSERT_TYPE.equals(insertType)){
					if(swb.getSheetIndex(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet))>=0){
						ssheet = swb.getSheetAt(swb.getSheetIndex(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet)));
						row = ssheet.getLastRowNum()+1;
					}
					else{
						ssheet = swb.createSheet(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet));
						row = 0;
					}
				}
				else{
					ssheet = swb.createSheet(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet));
				}
				createHHead(ssheet, columnBeans,row,excelSheet.firstCol(),excelSheet.titleFlag(),CsTitle(swb),CsName(swb),CsFirst(swb));
				createHBody(ssheet, columnBeans, entitis, row,excelSheet.firstCol(),CsOdd(swb),CsEven(swb),CsFirst(swb),excelSheet.relInfo());
				swb.setSheetOrder(ExcelAnnotationUtil.getRealExcelSheetName(excelSheet), Integer.parseInt(excelSheet.index()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SpreadExportException(e);
		}
	}

	public XSSFCellStyle CsFirst(XSSFWorkbook swb){
		XSSFCellStyle cs = swb.createCellStyle();
		cs.setBorderTop(BorderStyle.THIN);
		cs.setTopBorderColor(createColor("#000000", false));
		
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setLeftBorderColor(createColor("#000000", false));
		
		cs.setBorderRight(BorderStyle.THIN);
		cs.setRightBorderColor(createColor("#000000", false));
		
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBottomBorderColor(createColor("#000000", false));
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(createColor("#BDD7EE", false));
		return cs;
	}
	
	public XSSFCellStyle CsOdd(XSSFWorkbook swb){
		XSSFCellStyle cs = swb.createCellStyle();
		cs.setBorderTop(BorderStyle.THIN);
		cs.setTopBorderColor(createColor("#000000", false));
		
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setLeftBorderColor(createColor("#000000", false));
		
		cs.setBorderRight(BorderStyle.THIN);
		cs.setRightBorderColor(createColor("#000000", false));
		
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBottomBorderColor(createColor("#000000", false));
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(createColor("#E2EFDA", false));
		return cs;
	}
	
	public XSSFCellStyle CsEven(XSSFWorkbook swb){
		XSSFCellStyle cs = swb.createCellStyle();
		cs.setBorderTop(BorderStyle.THIN);
		cs.setTopBorderColor(createColor("#000000", false));
		
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setLeftBorderColor(createColor("#000000", false));
		
		cs.setBorderRight(BorderStyle.THIN);
		cs.setRightBorderColor(createColor("#000000", false));
		
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBottomBorderColor(createColor("#000000", false));
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(createColor("#FFF2CC", false));
		return cs;
	}

	public XSSFCellStyle CsTitle(XSSFWorkbook swb){
		XSSFCellStyle cs = swb.createCellStyle();
		cs.setBorderTop(BorderStyle.MEDIUM);
		cs.setTopBorderColor(createColor("#000000", false));
		
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setLeftBorderColor(createColor("#000000", false));
		
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setRightBorderColor(createColor("#000000", false));
		
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBottomBorderColor(createColor("#000000", false));
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setFillForegroundColor(createColor("#BDD7EE", false));
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setAlignment(HorizontalAlignment.CENTER);
		return cs;
	}
	
	public XSSFCellStyle CsName(XSSFWorkbook swb){
		XSSFCellStyle cs = swb.createCellStyle();
		cs.setBorderTop(BorderStyle.THIN);
		cs.setTopBorderColor(createColor("#000000", false));
		
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setLeftBorderColor(createColor("#000000", false));
		
		cs.setBorderRight(BorderStyle.THIN);
		cs.setRightBorderColor(createColor("#000000", false));
		
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBottomBorderColor(createColor("#000000", false));
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		cs.setFillForegroundColor(createColor("#E2EFDA", false));
		return cs;
	}
	
	public XSSFColor createColor(String hexString,boolean flag) {
		XSSFColor color = null;
		if (StringUtils.indexOf(hexString, '#') >= 0) {
			if(flag){
				if (hexString.equals("#000000"))
					color = new XSSFColor(new Color(255, 255, 255));
				else if (hexString.equals("#ffffff")||hexString.equals("#FFFFFF"))
					color = new XSSFColor(new Color(0, 0, 0));
				else {
					color = new XSSFColor(new Color(Integer.parseInt(
							StringUtils.substring(hexString, 1, 3), 16),
							Integer.parseInt(
									StringUtils.substring(hexString, 3, 5), 16),
							Integer.parseInt(
									StringUtils.substring(hexString, 5, 7), 16)));
				}
			}
			else{
				color = new XSSFColor(new Color(Integer.parseInt(
						StringUtils.substring(hexString, 1, 3), 16),
						Integer.parseInt(
								StringUtils.substring(hexString, 3, 5), 16),
						Integer.parseInt(
								StringUtils.substring(hexString, 5, 7), 16)));
			}
		}
		return color;
	}

	@Override
	public void write(List<String> jsonString,
			List<Map<String, Object>> rptInfos, String showType)
			throws SpreadExportException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ????????????????????????
	 **/
	public void write(String jsonString, Map<String, String> titles, List<List<?>> allEntitis)
			throws SpreadExportException, IOException {
		SpreadSchema spreadSchema = createSpreadSchema(jsonString);
		XSSFWorkbook workbook = null;
		OutputStream out = null;
		try {
			workbook = new XSSFWorkbook();
			cscreater = new XlsxCellStyleCreator(workbook, spreadSchema);
			Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
			//?????????????????????
			Map<String, Sheet> sheetMap = new HashMap<>();
			for (String title : titles.keySet()) {
				for (String sheetIndex : sps.keySet()) {
					SheetsProperty sp = sps.get(sheetIndex);
					//sp.getColumns();
					//??????sheet??????????????????
					Sheet ssheet = workbook.createSheet(titles.get(title));
					sheetMap.put(title, ssheet);
					setSheetSpan(ssheet, sp);
					setSheetData(ssheet, sp, null);
					//????????????
					setColumnsWidth(ssheet, sp);
					setRowsHeight(ssheet, sp);
				}
			}
			for (List<?> entitis : allEntitis) {
				if (entitis.size() > 0) {
					Class<?> clazz = getEntitisClass(entitis);
					ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
					if (excelSheet.extType().equals(GlobalConstants4plugin.EXT_H)) {
						this.runH(workbook, entitis);
					}
					//??????????????????
					setCellValue(entitis, sheetMap, workbook);
				}
			}
			workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
			out = new FileOutputStream(filePath);
			workbook.write(out);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(workbook);
		}
	}

	/**
	 * ??????????????????????????????sheet???????????????
	 *
	 * @param entitis  ????????????
	 * @param sheetMap sheet??????
	 **/
	private void setCellValue(List<?> entitis, Map<String, Sheet> sheetMap, Workbook workbook) {
		//?????????????????????
		if (getEntitisClass(entitis) == RptDesignModuleCellVO.class) {
			for (Object object : entitis) {
				for (String key : sheetMap.keySet()) {
					RptDesignModuleCellVO rptDesignModuleCellVO = (RptDesignModuleCellVO) object;
					if (StringUtils.isNotBlank(rptDesignModuleCellVO.getCellNo())) {
						Cell cell = getCell(rptDesignModuleCellVO.getCellNo(), sheetMap, key);
						String cellValue = "";
						if (key.equals("rptCell")) {
							cellValue = "?????????????????????";
						}
						if (key.equals("rptBusiNo")) {
							cellValue = rptDesignModuleCellVO.getBusiNo();
						}
						if (key.equals("rptSource")) {
							cellValue = rptDesignModuleCellVO.getDsName() + "." + rptDesignModuleCellVO.getColumnName();
						}
						if (key.equals("rptCellNm")) {
							cellValue = rptDesignModuleCellVO.getCellNm();
						}
						if (key.equals("idxSumUpt")) {
							cellValue = "--&&--&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsUpt()) ? "?????????" : "????????????");
						}
						if (key.equals("cellDisplayUnit")) {
							//????????????
							cellValue = DisplayFormat.get(rptDesignModuleCellVO.getDisplayFormat()).toCString() +
									//????????????
									"&&" + DataUnit.get(rptDesignModuleCellVO.getDataUnit()).toCString() +
									//????????????
									"&&" + rptDesignModuleCellVO.getDataPrecision();
						}
						if (key.equals("detailed")) {
							cellValue = GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsExt()) ? "??????" : "?????????";
							if (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsExt())) {
								cellValue += "&&" + ExtendDirection.get(rptDesignModuleCellVO.getExtDirection()).toCString();
								cellValue += "&&" + ExtendMode.get(rptDesignModuleCellVO.getExtMode()).toCString();
							} else {
								cellValue += "&&--&&--";
							}
							cellValue += "&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsSort()) ? "??????" : "?????????");
							if (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsSort())) {
								cellValue += "&&" + (SortMode.get(rptDesignModuleCellVO.getSortMode()).toCString());
								cellValue += "&&" + (rptDesignModuleCellVO.getSortOrder());
							} else {
								cellValue += "&&--";
								cellValue += "&&--";
							}
							cellValue += "&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsConver()) ? "??????" : "?????????");
							cellValue += "&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsMerge()) ? "??????" : "?????????");
							cellValue += "&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignModuleCellVO.getIsMergeCol()) ? "???????????????" : "??????????????????");
						}
						if (key.equals("cellCaliberExplain")) {
							cellValue = rptDesignModuleCellVO.getCaliberExplain();
						}
						if (key.equals("cellCaliberTechnology")) {
							cellValue = rptDesignModuleCellVO.getCaliberTechnology();
						}
						cellValue = StringUtils.remove(cellValue, '"');
						cell.setCellType(CellType.STRING);
						cell.setCellValue(cellValue);
					}
				}
			}
		}
		//????????????????????????
		if (getEntitisClass(entitis) == RptDesignComcellInfoVO.class) {
			for (Object object : entitis) {
				for (String key : sheetMap.keySet()) {
					RptDesignComcellInfoVO rptDesignComcellInfoVO = (RptDesignComcellInfoVO) object;
					//???????????????????????????
					if (rptDesignComcellInfoVO.getTypeId().equals("05")) {
						if (StringUtils.isNotBlank(rptDesignComcellInfoVO.getCellNo())) {
							Cell cell = getCell(rptDesignComcellInfoVO.getCellNo(), sheetMap, key);
							String cellValue;
							if (key.equals("rptCell")) {
								cellValue = "??????????????????";
								CellStyle cellStyle = workbook.createCellStyle();
								cellStyle.cloneStyleFrom(cell.getCellStyle());
								cellStyle.setAlignment(HorizontalAlignment.RIGHT); //?????????????????????????????????
								cell.setCellStyle(cellStyle);
							} else {
								cellValue = rptDesignComcellInfoVO.getContent();
							}
							cellValue = StringUtils.remove(cellValue, '"');
							cell.setCellType(CellType.STRING);
							cell.setCellValue(cellValue);
						}
					}
				}
			}
		}
		//????????????????????????
		if (getEntitisClass(entitis) == RptDesignStaticCellVO.class) {
			for (Object object : entitis) {
				for (String key : sheetMap.keySet()) {
					RptDesignStaticCellVO rptDesignStaticCellVO = (RptDesignStaticCellVO) object;
					if (StringUtils.isNotBlank(rptDesignStaticCellVO.getCellNo())) {
						Cell cell = getCell(rptDesignStaticCellVO.getCellNo(), sheetMap, key);
						String cellValue = "";
						if (key.equals("rptIdx") || key.equals("rptSource")) {
							cellValue = rptDesignStaticCellVO.getExpression();
						}
						if (key.equals("rptCell")) {
							cellValue = "?????????";
						}
						cellValue = StringUtils.remove(cellValue, '"');
						cell.setCellType(CellType.STRING);
						cell.setCellValue(cellValue);
					}
				}
			}
		}
		//???????????????????????????
		if (getEntitisClass(entitis) == RptIdxFilterVO.class) {
			for (Object object : entitis) {
				for (String key : sheetMap.keySet()) {
					RptIdxFilterVO rptIdxFilterVO = (RptIdxFilterVO) object;
					if (StringUtils.isNotBlank(rptIdxFilterVO.getCellNo())) {
						Cell cell = getCell(rptIdxFilterVO.getCellNo(), sheetMap, key);
						String cellValue;
						if (key.equals("rptFilter")) {
							if (StringUtils.isNotBlank(cell.getStringCellValue())) {
								cellValue = cell.getStringCellValue() + "&&[" + rptIdxFilterVO.getDimTypeNm() + "(" + rptIdxFilterVO.getDimNo() + ")" + "=" + rptIdxFilterVO.getFilterVal() + "]";
							} else {
								cellValue = "[" + rptIdxFilterVO.getDimTypeNm() + "(" + rptIdxFilterVO.getDimNo() + ")" + "=" + rptIdxFilterVO.getFilterVal() + "]";
							}
							cellValue = StringUtils.remove(cellValue, '"');
							cell.setCellType(CellType.STRING);
							cell.setCellValue(cellValue);
						}
					}
				}
			}
		}
		//?????????????????????
		if (getEntitisClass(entitis) == RptDesignIdxCellVO.class) {
			for (Object object : entitis) {
				for (String key : sheetMap.keySet()) {
					RptDesignIdxCellVO rptDesignIdxCellVO = (RptDesignIdxCellVO) object;
					if (StringUtils.isNotBlank(rptDesignIdxCellVO.getCellNo())) {
						Cell cell = getCell(rptDesignIdxCellVO.getCellNo(), sheetMap, key);
						String cellValue = "";
						if (key.equals("rptIdx")) {
							cellValue = rptDesignIdxCellVO.getRealIndexNo();
						}
						if (key.equals("rptBusiNo")) {
							cellValue = rptDesignIdxCellVO.getBusiNo();
						}
						if (key.equals("rptCell")) {
							cellValue = "???????????????";
						}
						if (key.equals("rptSource")) {
							cellValue = rptDesignIdxCellVO.getIndexNm();
						}
						if (key.equals("rptCellNm")) {
							cellValue = rptDesignIdxCellVO.getCellNm();
						}
						if (key.equals("idxSumUpt")) {
							cellValue = (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignIdxCellVO.getIsSum()) || StringUtils.isBlank(rptDesignIdxCellVO.getIsSum()) ? "???????????????" : "??????????????????") +
									"&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignIdxCellVO.getIsFillSum()) || StringUtils.isBlank(rptDesignIdxCellVO.getIsFillSum()) ? "???????????????" : "??????????????????") +
									"&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignIdxCellVO.getIsUpt()) ? "?????????" : "????????????");
						}
						if (key.equals("cellDisplayUnit")) {
							//????????????
							cellValue = DisplayFormat.get(rptDesignIdxCellVO.getDisplayFormat()).toCString() +
									//????????????
									"&&" + DataUnit.get(rptDesignIdxCellVO.getDataUnit()).toCString() +
									//????????????
									"&&" + rptDesignIdxCellVO.getDataPrecision();
						}
						if (key.equals("cellCaliberExplain")) {
							cellValue = rptDesignIdxCellVO.getCaliberExplain();
						}
						if (key.equals("cellCaliberTechnology")) {
							cellValue = rptDesignIdxCellVO.getCaliberTechnology();
						}
						cellValue = StringUtils.remove(cellValue, '"');
						cell.setCellType(CellType.STRING);
						cell.setCellValue(cellValue);
					}
				}
			}
		}
		//???????????????????????????
		if (getEntitisClass(entitis) == RptDesignIdxCalcCellVO.class) {
			for (Object object : entitis) {
				for (String key : sheetMap.keySet()) {
					RptDesignIdxCalcCellVO rptDesignIdxCalcCellVO = (RptDesignIdxCalcCellVO) object;
					if (StringUtils.isNotBlank(rptDesignIdxCalcCellVO.getCellNo())) {
						Cell cell = getCell(rptDesignIdxCalcCellVO.getCellNo(), sheetMap, key);
						String cellValue = "";
						if (key.equals("rptIdx")) {
							cellValue = rptDesignIdxCalcCellVO.getRealIndexNo();
						}
						if (key.equals("rptBusiNo")) {
							cellValue = rptDesignIdxCalcCellVO.getBusiNo();
						}
						if (key.equals("rptCell")) {
							cellValue = "?????????????????????";
						}
						if (key.equals("rptSource")) {
							cellValue = rptDesignIdxCalcCellVO.getFormulaDesc();
						}
						if (key.equals("rptCellNm")) {
							cellValue = rptDesignIdxCalcCellVO.getCellNm();
						}
						if (key.equals("idxSumUpt")) {
							cellValue = (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignIdxCalcCellVO.getIsSum()) || StringUtils.isBlank(rptDesignIdxCalcCellVO.getIsSum()) ? "???????????????" : "??????????????????") +
									"&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignIdxCalcCellVO.getIsFillSum()) || StringUtils.isBlank(rptDesignIdxCalcCellVO.getIsFillSum()) ? "???????????????" : "??????????????????") +
									"&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignIdxCalcCellVO.getIsUpt()) ? "?????????" : "????????????");
						}
						if (key.equals("cellDisplayUnit")) {
							//????????????
							cellValue = DisplayFormat.get(rptDesignIdxCalcCellVO.getDisplayFormat()).toCString() +
									//????????????
									"&&" + DataUnit.get(rptDesignIdxCalcCellVO.getDataUnit()).toCString() +
									//????????????
									"&&" + rptDesignIdxCalcCellVO.getDataPrecision();
						}
						if (key.equals("cellCaliberExplain")) {
							cellValue = rptDesignIdxCalcCellVO.getCaliberExplain();
						}
						if (key.equals("cellCaliberTechnology")) {
							cellValue = rptDesignIdxCalcCellVO.getCaliberTechnology();
						}
						cellValue = StringUtils.remove(cellValue, '"');
						cell.setCellType(CellType.STRING);
						cell.setCellValue(cellValue);
					}
				}
			}
		}
		//Excel?????????????????????
		if (getEntitisClass(entitis) == RptDesignFormulaCellVO.class) {
			for (Object object : entitis) {
				for (String key : sheetMap.keySet()) {
					RptDesignFormulaCellVO rptDesignFormulaCellVO = (RptDesignFormulaCellVO) object;
					if (StringUtils.isNotBlank(rptDesignFormulaCellVO.getCellNo())) {
						Cell cell = getCell(rptDesignFormulaCellVO.getCellNo(), sheetMap, key);
						String cellValue = "";
						if (key.equals("rptIdx")) {
							cellValue = rptDesignFormulaCellVO.getRealIndexNo();
						}
						if (key.equals("rptBusiNo")) {
							cellValue = rptDesignFormulaCellVO.getBusiNo();
						}
						if (key.equals("rptCell")) {
							cellValue = "Excel?????????";
						}
						if (key.equals("rptCellNm")) {
							cellValue = rptDesignFormulaCellVO.getCellNm();
						}
						if (key.equals("idxSumUpt")) {
							cellValue = (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignFormulaCellVO.getIsSum()) || StringUtils.isBlank(rptDesignFormulaCellVO.getIsSum()) ? "???????????????" : "??????????????????") +
									"&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignFormulaCellVO.getIsFillSum()) || StringUtils.isBlank(rptDesignFormulaCellVO.getIsFillSum()) ? "???????????????" : "??????????????????") +
									"&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignFormulaCellVO.getIsUpt()) ? "?????????" : "????????????");
						}
						if (key.equals("cellDisplayUnit")) {
							//????????????
							cellValue = DisplayFormat.get(rptDesignFormulaCellVO.getDisplayFormat()).toCString() +
									//????????????
									"&&" + DataUnit.get(rptDesignFormulaCellVO.getDataUnit()).toCString() +
									//????????????
									"&&" + rptDesignFormulaCellVO.getDataPrecision();
						}
						if (key.equals("cellCaliberExplain")) {
							cellValue = rptDesignFormulaCellVO.getCaliberExplain();
						}
						if (key.equals("cellCaliberTechnology")) {
							cellValue = rptDesignFormulaCellVO.getCaliberTechnology();
						}
						if (key.equals("excelFormula")) {
							cellValue = (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignFormulaCellVO.getIsRptIndex()) ? "????????????" : "??????????????????")
									+ "&&" + (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignFormulaCellVO.getIsAnalyseExt()) ? "??????" : "?????????");
							if (GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(rptDesignFormulaCellVO.getIsAnalyseExt())) {
								cellValue += "&&" + ExtendType.get(rptDesignFormulaCellVO.getAnalyseExtType()).toCString();
							}else {
								cellValue += "&&--";
							}
						}
						if (!key.equals("rptInfo") && !key.equals("rptSource") && !key.equals("rptFilter")) {
							cellValue = StringUtils.remove(cellValue, '"');
							cell.setCellType(CellType.STRING);
							cell.setCellValue(cellValue);
						}
					}
				}
			}
		}
	}

	/**
	 * ??????cell
	 *
	 * @param cellNo   ???????????????
	 * @param key      sheet??????
	 * @param sheetMap sheetMap
	 **/
	private Cell getCell(String cellNo, Map<String, Sheet> sheetMap, String key) {
		int col = lettrToCell(cellNo).get("col") - 1;
		int row = lettrToCell(cellNo).get("row") - 1;
		Row trow = sheetMap.get(key).getRow(row);
		if (trow == null) {
			trow = sheetMap.get(key).createRow(row);
		}
		Cell cell = trow.getCell(col);
		if (cell == null) {
			cell = trow.createCell(col);
		}
		return cell;
	}
}
