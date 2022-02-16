/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.frame.excel.AbstractExcelExporter;
import com.yusys.bione.plugin.spreadjs.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tanxu
 *
 */
public abstract class AbstractSpreadWriter  extends AbstractExcelExporter implements ISpreadWriter {
	protected AbstractCellStyleCreator cscreater = null;
	protected Map<String,String> formulaMap=new HashMap<String, String>();
	protected SpreadSchema createSpreadSchema(String jsonString) throws SpreadExportException {
		return JSON.parseObject(jsonString, SpreadSchema.class);
	}
	
	public void setCellValue(Cell cell, Object value) {
		if (value == null || cell == null) {
			cell.setCellType(CellType.BLANK);
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
			
		} else if (isFormulaDefinition(value)) {
			cell.setCellType(CellType.FORMULA);
			cell.setCellFormula(getFormula(value));
		
		} else {
			if(Pattern.matches("/OADate\\(\\d+(.\\d+)?\\)/", value.toString())){
				Pattern p=Pattern.compile("\\d+(.\\d+)?");
				Matcher m=p.matcher(value.toString());
				if(m.find()){
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(m.group(0));
				}
			}
			else{
				if(cell.getCellStyle().getDataFormatString().equals("")||cell.getCellStyle().getDataFormatString().equals("General")||cell.getCellStyle().getDataFormatString().equals("@")){
					cell.setCellValue(value.toString());
					cell.setCellType(CellType.STRING);
				}
				else{
					try{
						double doubleValue = new BigDecimal(value.toString()).doubleValue();
						cell.setCellType(CellType.NUMERIC);
						cell.setCellValue(doubleValue);
					}
					catch(Exception e){
						cell.setCellType(CellType.STRING);
						cell.setCellValue(value.toString());
						
					}
				}
				
			}		
		}
	}
	
	protected void setSheetSpan(Sheet ssheet, SheetsProperty sp) {
		if(null != sp.getSpans()) {
			for(Span span : sp.getSpans()){
				if (span.getRowCount() == 1 && span.getColCount() == 1) {
					continue;
				}
				ssheet.addMergedRegion(new CellRangeAddress(span.getRow(), span.getRow() + span.getRowCount() - 1, span.getCol(), span.getCol() + span.getColCount() - 1));
			}
		}
	}

	protected void setSheetSpans(Sheet ssheet, SheetsProperty sp) {
		if(null != sp.getSpans()) {
			for(Span span : sp.getSpans()){
				if (span.getRowCount() == 1 && span.getColCount() == 1) {
					continue;
				}
				ssheet.addMergedRegionUnsafe(new CellRangeAddress(span.getRow(), span.getRow() + span.getRowCount() - 1, span.getCol(), span.getCol() + span.getColCount() - 1));
			}
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
	
	protected void setSheetData(Sheet ssheet, SheetsProperty sp, List<Span> spans) {
		Data data = sp.getData();
		if (data != null&&data.getDataTable()!=null) {
			createBody(ssheet, data.getDataTable(), spans);
		}
		
	}
	
	abstract protected void createBody(Sheet ssheet, DataTable dataTable, List<Span> spans);
	
	protected void setFormula(Workbook wb){
		for(String keyset:formulaMap.keySet()){
			String index[]=StringUtils.split(keyset,"////");
			String formula=formulaMap.get(keyset);
			if(org.apache.commons.lang3.StringUtils.substring(formula,0, 1).equals("=")){
				formula=org.apache.commons.lang3.StringUtils.substring(formula, 1,formula.length());
			}
			if(!formula.equals(""))
				wb.getSheet(index[0]).getRow(NumberUtils.toInt(index[1])).getCell(NumberUtils.toInt(index[2])).setCellFormula(formula);
		}
	}
	protected void setCellStyle(Cell cell, Object obj) {
		CellStyle cs = null;
		if (obj instanceof String && StringUtils.isNotEmpty(obj.toString()) && (cs = cscreater.getCellStyle(obj.toString())) != null) {
			cell.setCellStyle(cs);
		} else if (obj instanceof Style && (cs = cscreater.createCellStyle((Style)obj)) != null) {
			cell.setCellStyle(cs);
		}
	}
	
	protected void setColumnsWidth(Sheet ssheet, SheetsProperty sp) {
		List<SheetSize> columns = sp.getColumns();
		if(columns!=null){
			for(int i = 0 ; i < columns.size() ; i++){
				SheetSize size=columns.get(i);
				if (size != null) {
					if(size.getVisible()!=null&&!size.getVisible()){
						ssheet.setColumnHidden(i, true);
						this.resetWidth(ssheet, i, 0);
						continue;
					}
					if(size.getSize()!=null){
						resetWidth(ssheet, i, size.getSize().doubleValue());
					}
				}
				else{
					ssheet.autoSizeColumn(i);
					int ow=ssheet.getColumnWidth(i);
					ssheet.setColumnWidth(i,ow+1024);
				}
			}
		}
		
	}
	
	protected void setRowsHeight(Sheet ssheet, SheetsProperty sp) {
		List<SheetSize> rows = sp.getRows();
		if(rows!=null){
			for(int i = 0 ; i < rows.size() ; i++){
				SheetSize size=rows.get(i);
				if (size != null) {
					if(size.getVisible()!=null&&!size.getVisible()){
						if( size.getSize()!=null)
							resetHeight(ssheet, i, size.getSize());
						if(size.getVisible()!=null){
							resetHeight(ssheet, i,0);
							if(ssheet.getRow(i)!=null)
								ssheet.getRow(i).setZeroHeight(true);
							continue;
						}
					}else {
						//增加else导出时设置行高
						if(null != size.getSize()) {
							resetHeight(ssheet, i, size.getSize());
						}
					}
						
				}
			}
		}
		
	}
	
	
	/**
	 * 重新设置指定列的宽度
	 * 
	 * @param ssheet	工作表
	 * @param colIndex 	列的序号
	 * @param width		宽度
	 */
	private void resetWidth(Sheet ssheet, int colIndex, double width) {
		if (width >= 0) {
			if(width < 255){
				ssheet.setColumnWidth(colIndex,(int) (width*32));  
	         }else{
	             ssheet.setColumnWidth(colIndex,6000 );
	         }
		}
	}
	
	private void resetHeight(Sheet ssheet, int rowIndex, double height) {
		if (height >=0) {
			if(ssheet.getRow(rowIndex)!=null){
				ssheet.getRow(rowIndex).setHeightInPoints(UnitsUtil.pixelsToPoints(height, false));
			}else {
				//增加else，空行时设置行高
				ssheet.createRow(rowIndex).setHeightInPoints(UnitsUtil.pixelsToPoints(height, false));
			}
		}
	}
	
}
