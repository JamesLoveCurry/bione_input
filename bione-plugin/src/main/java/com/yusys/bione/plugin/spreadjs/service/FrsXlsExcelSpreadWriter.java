/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.spreadjs.entity.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrsXlsExcelSpreadWriter extends XlsExcelSpreadWriter implements IFrsSpreadWriter {

	public FrsXlsExcelSpreadWriter(File file) throws FileNotFoundException {
		super(file);
	}
	
	@Override
	public void write(SpreadSchema schema) throws SpreadExportException,
			Exception {
	}

	@SuppressWarnings("unchecked")
	public void write(List<String> jsonStrings,List<Map<String,Object>> rptInfos,String showType, List<Map<String, Object>> cellSpanList) throws SpreadExportException {SpreadSchema spreadSchema  = new SpreadSchema();
		String templatePath = null;
		// 制度表样所需的自定义属性
		Map<String, Object> param = new HashMap<String, Object>();
		if(jsonStrings!=null&&jsonStrings.size()>0){
			spreadSchema=createSpreadSchema(jsonStrings.get(0));
			spreadSchema.getSheetsProperties().clear();
			for(int i=0;i<jsonStrings.size();i++){
				SpreadSchema schema = createSpreadSchema(jsonStrings.get(i));
				String key=schema.getSheetsProperties().keySet().iterator().next();
				Map<String,Object> map=rptInfos.get(i);
	//			String sheetNm = map.get("rptNum")+"-"+map.get("org")+"-"+map.get("dataDate");
				String sheetNm = map.get("rptNum") + "";
				schema.getSheetsProperties().get(key).setName(sheetNm);
				SheetsProperty sheets=schema.getSheetsProperties().get(key);
				sheets.setNamedStyles(schema.getNamedStyles());
				//模板路径 20190524 wf
				templatePath = (String) map.get("templatePath");
				
				//循环所有单元格信息，找到可编辑单元格进行赋值，因为明细报表需要进行数据扩展，可编辑单元格会被顶下去
				Map<String, Map<String, Object>> cellExtInfos = (Map<String, Map<String, Object>>) map.get("cellInfo");
				if(null != cellExtInfos) {
					for (Entry<String, Map<String, Object>> entry : cellExtInfos.entrySet()) {
						Map<String, Object> cellObj = entry.getValue();
						if(null != cellObj) {
							if(GlobalConstants4plugin.RPT_CELL_SOURCE_COMMON_EDIT.equals(cellObj.get("type"))) {//可编辑单元格
								Map<String,Integer> cellInfo = lettrToCell(entry.getKey());
								int rowId = cellInfo.get("row");
								int colId = cellInfo.get("col");
								DataTableProperty  dp = sheets.getData().getDataTable().getAdditionalProperties().get(rowId + "");
								if(dp!=null){
									DataTablePropertyProperty dpp = (DataTablePropertyProperty) dp.getAdditionalProperties().get(colId + "");
									dpp.setValue(cellObj.get("value"));
								}
							}
						}
					}  
				}
				
				//明细数据导出支持 add by chenl 2017年1月13日
				Map<String,Object>detailDataMap = (Map<String,Object>) map.get("detailDataMap");
				if(detailDataMap!=null&&!detailDataMap.isEmpty()){
					Iterator<String>itr = detailDataMap.keySet().iterator();
	
					while(itr.hasNext()){
						String cellNo = itr.next();
						Map<String, Object> returnMap = ExcelAnalyseUtils.getRowColByCellPosi(cellNo);
	
						DataTableProperty  dp = sheets.getData().getDataTable().getAdditionalProperties().get(returnMap.get("row")+"");
						if(dp!=null){
							DataTablePropertyProperty dpp = (DataTablePropertyProperty) dp.getAdditionalProperties().get(returnMap.get("col")+"");
							dpp.setValue(detailDataMap.get(cellNo));
						}
					}
					
				}
				spreadSchema.getSheetsProperties().put(sheetNm, sheets);

				// 制度表样所需的自定义属性(缺一不可)
				if(StringUtils.isNotBlank((String)map.get("tmpVersionId")) && StringUtils.isNotBlank((String)map.get("repId"))){
					param.put("tmpVersionId", map.get("tmpVersionId"));
					param.put("repId", map.get("repId"));
				}
			}
		}
		Workbook hwb = null;
		//构造合并单元格信息
		List<Span> spans = new ArrayList<Span>();
		List<Span> spans2 = new ArrayList<Span>();//包含原表样的合并单元格信息
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
		try {
			if(StringUtils.isEmpty(templatePath)) {
				if(MapUtils.isNotEmpty(param)){
					hwb = WorkbookWrapper.create(filePath, false, param);
				}else{
					hwb = WorkbookWrapper.create(filePath, false);
				}
				Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
				//添加所有的样式
				List<Sheet> sheetlist=new ArrayList<Sheet>();
				for (String sheetIndex : sps.keySet()) {
					SheetsProperty sp = sps.get(sheetIndex);
					cscreater = new XlsCellStyleCreator(hwb, spreadSchema,sp);
					//添加sheet中对应的样式
					Sheet ssheet = hwb.createSheet(sp.getName());
					HSSFPrintSetup ps1 = (HSSFPrintSetup) ssheet.getPrintSetup();
					ps1.setPaperSize(PrintSetup.A4_PAPERSIZE);
					sheetlist.add(ssheet);
					spans2.addAll(sp.getSpans());
					sp.setSpans(spans2);
					setSheetSpan(ssheet, sp);
					setSheetData(ssheet, sp, spans);
					setRowsHeight(ssheet, sp);
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
			}else {
				if("16".equals(rptInfos.get(0).get("busiType"))){//理财登记替换单元格
					//有银监表样的直接往表样里写值，这样就能保证导出表样的样式和银监表样完全一致
					try {
						hwb = WorkbookWrapper.open(templatePath, filePath);
						Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
						for (String sheetNm : sps.keySet()) {
							SheetsProperty sp = sps.get(sheetNm);
							Sheet ssheet = hwb.getSheet(sheetNm);
							if(null == ssheet) {
								ssheet = hwb.getSheetAt(0);
							}
							Data data = sp.getData();
							if(null != ssheet) {
								if ((null != data) && (null != data.getDataTable())) {
									setSheetOnlyData(ssheet, data.getDataTable());
								}
								HSSFPrintSetup ps1 = (HSSFPrintSetup) ssheet.getPrintSetup();
								ps1.setPaperSize(PrintSetup.A4_PAPERSIZE);
								ssheet.setForceFormulaRecalculation(true);
							}
						}
						hwb.getCreationHelper().createFormulaEvaluator().evaluateAll();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					//以原表样为模板，重新生成sheet页 20190524
					try {
						hwb = WorkbookWrapper.open(templatePath, filePath);
						for(int i=0;i<hwb.getNumberOfSheets();i++){
							hwb.removeSheetAt(i);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
					//添加所有的样式
					List<Sheet> sheetlist=new ArrayList<Sheet>();
					for (String sheetIndex : sps.keySet()) {
						SheetsProperty sp = sps.get(sheetIndex);
						cscreater = new XlsCellStyleCreator(hwb, spreadSchema,sp);
						//添加sheet中对应的样式
						Sheet ssheet = hwb.createSheet(sp.getName());
						HSSFPrintSetup ps1 = (HSSFPrintSetup) ssheet.getPrintSetup();
						ps1.setPaperSize(PrintSetup.A4_PAPERSIZE);
						sheetlist.add(ssheet);
						spans2.addAll(sp.getSpans());
						sp.setSpans(spans2);
						setSheetSpan(ssheet, sp);
						setSheetData(ssheet, sp, spans);
						setRowsHeight(ssheet, sp);
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
				}
			}
		} finally {
			IOUtils.closeQuietly(hwb);
		}
	}
	
	/**
	 * 只往银监表样sheet页中写值
	 * @param ssheet
	 * @param dataTable
	 */
	private void setSheetOnlyData(Sheet ssheet, DataTable dataTable) {
		Map<String, DataTableProperty> dtps = dataTable.getAdditionalProperties();
		for (String rowIndex : dtps.keySet()) {
			Row row = ssheet.getRow(NumberUtils.toInt(rowIndex));
			if(null != row) {
				Map<String, Object> dtpps = dtps.get(rowIndex).getAdditionalProperties();
				for (String colIndex : dtpps.keySet()) {
					Object obj = dtpps.get(colIndex);
					if (obj != null && obj instanceof DataTablePropertyProperty) {
						DataTablePropertyProperty dtpp = (DataTablePropertyProperty) obj;
						Cell cell = row.getCell(NumberUtils.toInt(colIndex));
						if(null != cell) {
							onlySetCellValue(cell, dtpp.getValue());
						}
					}
				}
			}
		}
	}
	
	/**
	 * 往银监表样sheet页的单元格中写值
	 * @param cell
	 * @param value
	 */
	@SuppressWarnings("deprecation")
	private void onlySetCellValue(Cell cell, Object value) {
		if (value == null || cell == null) {
			return;
		}else if(Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
			return;
		} 
		else if (value instanceof Number) {
			double doubleValue = ((Number) value).doubleValue();
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(doubleValue);
			
		} else if (value instanceof Date) {
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue((Date) value);
			
		} else if (value instanceof Calendar) {
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue((Calendar) value);
			
		} else {
			if(Pattern.matches("/OADate\\(\\d+(.\\d+)?\\)/", value.toString())){
				Pattern p=Pattern.compile("\\d+(.\\d+)?");
				Matcher m=p.matcher(value.toString());
				if(m.find()){
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(m.group(0));
				}
			}else{
				if(cell.getCellStyle().getDataFormatString().equals("")||cell.getCellStyle().getDataFormatString().equals("General")||cell.getCellStyle().getDataFormatString().equals("@")){
					cell.setCellValue(value.toString());
					cell.setCellType(CellType.STRING);
				}else{
					try{
						double doubleValue = new BigDecimal(value.toString()).doubleValue();
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(doubleValue);
					}catch(Exception e){
						cell.setCellType(CellType.STRING);
						cell.setCellValue(value.toString());
					}
				}
			}		
		}
	}
	
	/**
	 * 单元格编号转对应的行号和列号
	 * @param letter
	 * @return
	 */
	public Map<String,Integer> lettrToCell(String letter){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int col = letterToInt(StringUtils.substring(letter, 0, i));
		int row = Integer.parseInt(StringUtils.substring(letter, i, letter.length()));
		Map<String,Integer> cellInfo = new HashMap<String, Integer>();
		cellInfo.put("row", row -1);
		cellInfo.put("col", col -1);
		return cellInfo;
	}
	
	/**
	 * 字母转数字
	 * @param letter
	 * @return
	 */
	private int letterToInt(String letter){
		int length=letter.length();
		int result=0;
		for(int i=0;i<length;i++){
			int num=letter.charAt(i)-64;
			result+=num*Math.pow(26, length-i-1);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void writeAll(List<String> jsonStrings,List<Map<String,Object>> rptInfos,String showType, List<Map<String, Object>> cellSpanList) throws SpreadExportException {SpreadSchema spreadSchema  = new SpreadSchema();
		String templatePath = null;
		// 制度表样所需的自定义属性
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String,String> nameMap = new HashMap<>();
		if(jsonStrings!=null&&jsonStrings.size()>0){
			spreadSchema=createSpreadSchema(jsonStrings.get(0));
			spreadSchema.getSheetsProperties().clear();

			for(int i=0;i<jsonStrings.size();i++){
				SpreadSchema schema = createSpreadSchema(jsonStrings.get(i));
				String key=schema.getSheetsProperties().keySet().iterator().next();
				Map<String,Object> map=rptInfos.get(i);
				//			String sheetNm = map.get("rptNum")+"-"+map.get("org")+"-"+map.get("dataDate");
				String sheetNm = map.get("rptNum") + "";
				schema.getSheetsProperties().get(key).setName(sheetNm);
				SheetsProperty sheets=schema.getSheetsProperties().get(key);
				sheets.setNamedStyles(schema.getNamedStyles());
				//模板路径 20190524 wf
				templatePath = (String) map.get("templatePath");

				//循环所有单元格信息，找到可编辑单元格进行赋值，因为明细报表需要进行数据扩展，可编辑单元格会被顶下去
				Map<String, Map<String, Object>> cellExtInfos = (Map<String, Map<String, Object>>) map.get("cellInfo");
				if(null != cellExtInfos) {
					for (Entry<String, Map<String, Object>> entry : cellExtInfos.entrySet()) {
						Map<String, Object> cellObj = entry.getValue();
						if(null != cellObj) {
							if(GlobalConstants4plugin.RPT_CELL_SOURCE_COMMON_EDIT.equals(cellObj.get("type"))) {//可编辑单元格
								Map<String,Integer> cellInfo = lettrToCell(entry.getKey());
								int rowId = cellInfo.get("row");
								int colId = cellInfo.get("col");
								DataTableProperty  dp = sheets.getData().getDataTable().getAdditionalProperties().get(rowId + "");
								if(dp!=null){
									DataTablePropertyProperty dpp = (DataTablePropertyProperty) dp.getAdditionalProperties().get(colId + "");
									dpp.setValue(cellObj.get("value"));
								}
							}
						}
					}
				}

				//明细数据导出支持 add by chenl 2017年1月13日
				Map<String,Object>detailDataMap = (Map<String,Object>) map.get("detailDataMap");
				if(detailDataMap!=null&&!detailDataMap.isEmpty()){
					Iterator<String>itr = detailDataMap.keySet().iterator();

					while(itr.hasNext()){
						String cellNo = itr.next();
						Map<String, Object> returnMap = ExcelAnalyseUtils.getRowColByCellPosi(cellNo);

						DataTableProperty  dp = sheets.getData().getDataTable().getAdditionalProperties().get(returnMap.get("row")+"");
						if(dp!=null){
							DataTablePropertyProperty dpp = (DataTablePropertyProperty) dp.getAdditionalProperties().get(returnMap.get("col")+"");
							dpp.setValue(detailDataMap.get(cellNo));
						}
					}

				}
				if (nameMap.containsKey(sheetNm)) {
					nameMap.put(sheetNm, (Integer.parseInt(nameMap.get(sheetNm))+1)+"");
				} else {
					nameMap.put(sheetNm, 0 + "");
				}
				if (nameMap.containsKey(sheetNm) && !"0".equals(nameMap.get(sheetNm))) {
					spreadSchema.getSheetsProperties().put(sheetNm + "" + nameMap.get(sheetNm), sheets);
				} else {
					spreadSchema.getSheetsProperties().put(sheetNm, sheets);
				}
				// 制度表样所需的自定义属性(缺一不可)
				if(StringUtils.isNotBlank((String)map.get("tmpVersionId")) && StringUtils.isNotBlank((String)map.get("repId"))){
					param.put("tmpVersionId", map.get("tmpVersionId"));
					param.put("repId", map.get("repId"));
				}
			}
		}
		Workbook hwb = null;
		//构造合并单元格信息
		List<Span> spans = new ArrayList<Span>();
		List<Span> spans2 = new ArrayList<Span>();//包含原表样的合并单元格信息
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
		try {
			Map<String,String> nameMaps = new HashMap<>();
			if(StringUtils.isEmpty(templatePath)) {
				if(MapUtils.isNotEmpty(param)){
					hwb = WorkbookWrapper.create(filePath, false, param);
				}else{
					hwb = WorkbookWrapper.create(filePath, false);
				}
				Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
				//添加所有的样式
				List<Sheet> sheetlist=new ArrayList<Sheet>();
				int a = 0;

				for (String sheetIndex : sps.keySet()) {
					SheetsProperty sp = sps.get(sheetIndex);
					cscreater = new XlsCellStyleCreator(hwb, spreadSchema, sp);
					//添加sheet中对应的样式
					Sheet ssheet = null;
					String name = sp.getName();
					if (nameMaps.containsKey(name)) {
						nameMaps.put(name, (Integer.parseInt(nameMaps.get(name))+1)+"");
					} else {
						nameMaps.put(name, 0 + "");
					}
					if (nameMaps.containsKey(sp.getName()) && !"0".equals(nameMaps.get(sp.getName()))) {
						name = name + ""+nameMaps.get(sp.getName());
						ssheet = hwb.createSheet(name);
					} else {
						ssheet = hwb.createSheet(sp.getName());
					}
					HSSFPrintSetup ps1 = (HSSFPrintSetup) ssheet.getPrintSetup();
					ps1.setPaperSize(PrintSetup.A4_PAPERSIZE);
					sheetlist.add(ssheet);
					spans2.addAll(sp.getSpans());
					sp.setSpans(spans2);
					setSheetSpans(ssheet, sp);
					setSheetData(ssheet, sp, spans);
					setRowsHeight(ssheet, sp);
					a++;
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
			}else {
				if("16".equals(rptInfos.get(0).get("busiType"))){//理财登记替换单元格
					//有银监表样的直接往表样里写值，这样就能保证导出表样的样式和银监表样完全一致
					try {
						hwb = WorkbookWrapper.open(templatePath, filePath);
						Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
						for (String sheetNm : sps.keySet()) {
							SheetsProperty sp = sps.get(sheetNm);
							Sheet ssheet = hwb.getSheet(sheetNm);
							if(null == ssheet) {
								ssheet = hwb.getSheetAt(0);
							}
							Data data = sp.getData();
							if(null != ssheet) {
								if ((null != data) && (null != data.getDataTable())) {
									setSheetOnlyData(ssheet, data.getDataTable());
								}
								HSSFPrintSetup ps1 = (HSSFPrintSetup) ssheet.getPrintSetup();
								ps1.setPaperSize(PrintSetup.A4_PAPERSIZE);
								ssheet.setForceFormulaRecalculation(true);
							}
						}
						hwb.getCreationHelper().createFormulaEvaluator().evaluateAll();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					//以原表样为模板，重新生成sheet页 20190524
					try {
						hwb = WorkbookWrapper.open(templatePath, filePath);
						for(int i=0;i<hwb.getNumberOfSheets();i++){
							hwb.removeSheetAt(i);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					Map<String, SheetsProperty> sps  = spreadSchema.getSheetsProperties();
					//添加所有的样式
					List<Sheet> sheetlist=new ArrayList<Sheet>();
					int b =0;
					for (String sheetIndex : sps.keySet()) {
						SheetsProperty sp = sps.get(sheetIndex);
						cscreater = new XlsCellStyleCreator(hwb, spreadSchema,sp);
						//添加sheet中对应的样式
						Sheet ssheet = hwb.createSheet(sp.getName());
						HSSFPrintSetup ps1 = (HSSFPrintSetup) ssheet.getPrintSetup();
						ps1.setPaperSize(PrintSetup.A4_PAPERSIZE);
						sheetlist.add(ssheet);
						spans2.addAll(sp.getSpans());
						sp.setSpans(spans2);

						setSheetSpans(ssheet, sp);
						setSheetData(ssheet, sp, spans);
						setRowsHeight(ssheet, sp);
						b++;
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
				}
			}
		} finally {
			IOUtils.closeQuietly(hwb);
		}
	}
}
