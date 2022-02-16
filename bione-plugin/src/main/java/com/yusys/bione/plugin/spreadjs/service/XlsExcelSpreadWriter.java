/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;
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
public class XlsExcelSpreadWriter extends AbstractSpreadWriter {

	protected String filePath;
	
	public XlsExcelSpreadWriter(File file) throws FileNotFoundException {
		this.filePath = file.getAbsolutePath();
	}
	
	@Override
	public void write(String jsonString,String templatePath, List<Map<String, Object>> cellSpanList) throws SpreadExportException {
		SpreadSchema spreadSchema = createSpreadSchema(jsonString);
		Workbook hwb = null;
		try {
			if(StringUtils.isEmpty(templatePath)) {
				hwb = WorkbookWrapper.create(filePath, false);
			}else {
				//以原表样为模板，重新生成sheet页 20190524
				try {
					hwb = WorkbookWrapper.open(templatePath, filePath);
					for(int i=0;i<hwb.getNumberOfSheets();i++){
						hwb.removeSheetAt(i);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			cscreater = new XlsCellStyleCreator(hwb, spreadSchema);
			Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
			//构造合并单元格信息
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
			//添加所有的样式
			List<Sheet> sheetlist=new ArrayList<Sheet>();
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				//添加sheet中对应的样式
				Sheet ssheet = hwb.createSheet(sp.getName());
				sheetlist.add(ssheet);
				spans2.addAll(sp.getSpans());
				sp.setSpans(spans2);
				setSheetSpan(ssheet, sp);
				setSheetData(ssheet, sp, spans);
			}
			setFormula(hwb);
			hwb.getCreationHelper().createFormulaEvaluator().evaluateAll();
			int i=0;
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				Sheet ssheet = sheetlist.get(i);
				setColumnsWidth(ssheet, sp);
				setRowsHeight(ssheet, sp);
				i++;
			}
		} finally {
			IOUtils.closeQuietly(hwb);
		}
	}
	
	public void write(List<String> jsonStrings,List<Map<String,String>> rptInfos) throws SpreadExportException {
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
		Workbook hwb = null;
		try {
			hwb = WorkbookWrapper.create(filePath, false);
			Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
			//添加所有的样式
			List<Sheet> sheetlist=new ArrayList<Sheet>();
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				cscreater = new XlsCellStyleCreator(hwb, spreadSchema,sp);
				//添加sheet中对应的样式
				Sheet ssheet = hwb.createSheet(sp.getName());
				sheetlist.add(ssheet);
				setSheetSpan(ssheet, sp);
				setSheetData(ssheet, sp, null);
				setRowsHeight(ssheet, sp);
			}
			//setFormula(hwb);
			hwb.getCreationHelper().createFormulaEvaluator().evaluateAll();
			int i=0;
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				Sheet ssheet = sheetlist.get(i);
				setColumnsWidth(ssheet, sp);
				setRowsHeight(ssheet, sp);
				i++;
			}
		} finally {
			IOUtils.closeQuietly(hwb);
		}
	}
	
	protected void createBody(Sheet ssheet, DataTable dataTable, List<Span> spans) {
		Map<String, DataTableProperty> dtps = dataTable.getAdditionalProperties();
		for (String rowIndex : dtps.keySet()) {
			Row row = ssheet.createRow(NumberUtils.toInt(rowIndex));
			Map<String, Object> dtpps = dtps.get(rowIndex).getAdditionalProperties();
			for (String colIndex : dtpps.keySet()) {
				if(null != spans && spans.size() > 0){//目前只处理行合并
					for(int i=0; i < spans.size(); i++){
						int firstRow = spans.get(i).getRow();
						int lastRow = spans.get(i).getRow() + spans.get(i).getRowCount() - 1;
						int col = spans.get(i).getCol();
						if(col == Integer.parseInt(colIndex) && Integer.parseInt(rowIndex) > firstRow && Integer.parseInt(rowIndex) <= lastRow){//合并单元格第一个单元格赋值，其他单元格为空
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
								setCellValue(cell, dtpp.getValue());
								if(dtpp.getFormula()!=null){
									formulaMap.put(ssheet.getSheetName()+"//"+rowIndex+"//"+colIndex, dtpp.getFormula());
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
						setCellValue(cell, dtpp.getValue());
						if(dtpp.getFormula()!=null){
							formulaMap.put(ssheet.getSheetName()+"//"+rowIndex+"//"+colIndex, dtpp.getFormula());
						}
					}
				}
			}
		}
		ssheet.setForceFormulaRecalculation(true);
		// 添加下拉框单元格
		// resetComboxCell(ssheet, columnBean, firstRow, entitis.size());
	}

	@Override
	public void destory() {
	}

	@Override
	public void write(SpreadSchema schema) throws SpreadExportException,
			Exception {
		SpreadSchema spreadSchema = schema;
		Workbook hwb = null;
		try {
			hwb = WorkbookWrapper.create(filePath, false);
			cscreater = new XlsCellStyleCreator(hwb, spreadSchema);
			Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
			//添加所有的样式
			List<Sheet> sheetlist=new ArrayList<Sheet>();
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				//添加sheet中对应的样式
				Sheet ssheet = hwb.createSheet(sp.getName());
				sheetlist.add(ssheet);
				setSheetSpan(ssheet, sp);
				setSheetData(ssheet, sp, null);
			}
			setFormula(hwb);
			try{
				hwb.getCreationHelper().createFormulaEvaluator().evaluateAll();
			}
			catch(Exception e){
			}
			int i=0;
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				Sheet ssheet = sheetlist.get(i);
				setColumnsWidth(ssheet, sp);
				setRowsHeight(ssheet, sp);
				i++;
			}
		} finally {
			IOUtils.closeQuietly(hwb);
		}
	}

	@Override
	public void run() throws BioneExporterException {
	}

	@Override
	public void write(String jsonString, List<List<?>> allEntitis)
			throws SpreadExportException, IOException {
	}

	@Override
	public void write(String jsonString, Map<String, String> titles, List<List<?>> allEntitis) throws SpreadExportException, IOException {

	}

	@Override
	public void write(List<String> jsonString,
			List<Map<String, Object>> rptInfos, String showType)
			throws SpreadExportException {
		// TODO Auto-generated method stub
		
	}
}
