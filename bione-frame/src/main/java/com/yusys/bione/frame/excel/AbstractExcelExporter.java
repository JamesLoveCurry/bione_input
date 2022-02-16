/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Case;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;

import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.excel.annotations.ExcelColumn;

/**
 * @author tanxu
 *
 */
public abstract class AbstractExcelExporter implements IExporter {

	protected HashMap<String,CellStyle> styleMap=new HashMap<String, CellStyle>();
	
	protected static final int ADDITION_LINE = 1;
	
	protected Workbook wb;

	protected Sheet tmpsh;

	protected Class<?> getEntitisClass(List<?> entitis){
		return entitis.get(0).getClass();
	}
	
//	protected Map<Integer, Field> createFieldMapping(Map<Integer, Field> fieldMapping, Class<?> clazz) {
//		Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
//		for (Field field : allFields) {
//			// 将有注解的field存放到map中.
//			if (field.isAnnotationPresent(ExcelColumn.class)) {
//				ExcelColumn colmun = field.getAnnotation(ExcelColumn.class);
//				if (colmun.isExport()) {
//					field.setAccessible(true);// 设置类的私有字段属性可访问.
//					fieldMapping.put(ExcelAnnotationUtil.getExcelCol(colmun.index()), field);
//				}
//			} else if (field.isAnnotationPresent(EmbeddedId.class)) {
//				createFieldMapping(fieldMapping, field.getType());
//			}
//		}
//		return fieldMapping;
//	}
	
	protected void resetComboxCell(Sheet ssheet, List<ColumnBean> columnBeans, int firstRow, int length){
		FieldBean fieldBean;
		for(ColumnBean columnBean : columnBeans){
			for (String colIndex : columnBean.fieldIndexes()) {
				fieldBean = columnBean.getFieldBean(colIndex);
				setHSSFValidation(ssheet , fieldBean.getExcelColumnAnnotation().combox(), firstRow , firstRow + length + ADDITION_LINE - 1, fieldBean.getIndex(), fieldBean.getIndex());
			}
		}
	}
	
	protected void createHead(Sheet ssheet, List<ColumnBean> columnBeans) {
		Row row = ssheet.createRow(0);// 产生一行，目前只支持一行头
		FieldBean fieldBean = null;
		for(ColumnBean columnBean : columnBeans){
			for (String colIndex : columnBean.fieldIndexes()) {
				fieldBean = columnBean.getFieldBean(colIndex);
				ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
				//调整列的宽度
				resetWidth(ssheet, fieldBean.getIndex(), excelColumn.width());
				setCellValue(row.createCell(fieldBean.getIndex()), excelColumn.name(),"text");
			}
		}
	}
	protected void createHead(Sheet ssheet, List<ColumnBean> columnBeans,boolean titleFlag,int firstRow,int firstCol,CellStyle cs0,CellStyle cs,String insertType) {
		Row row = null;
		ssheet.createFreezePane(0,firstRow);
		row = ssheet.getRow(0);// 产 生一行，目前只支持一行头
		if(row == null){
			row = ssheet.createRow(0);
		}
		FieldBean fieldBean = null;
		if(titleFlag){
			int colnum = 0;
			for(ColumnBean columnBean : columnBeans){
				for (String colIndex : columnBean.fieldIndexes()) {
					fieldBean = columnBean.getFieldBean(colIndex);
					ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
					Cell cell = row.createCell(fieldBean.getIndex()+firstCol);
					setCellValue(cell, excelColumn.colTitle(),"text");
					if(cs0 != null){
						cell.setCellStyle(cs0);
					}
					colnum++;
				}
			}
			ssheet.addMergedRegion(new CellRangeAddress(0, 0, firstCol, firstCol+colnum-1));
			row = ssheet.getRow(1);// 产 生一行，目前只支持一行头
			if(row == null){
				row = ssheet.createRow(1);
			}
		}
		for(ColumnBean columnBean : columnBeans){
			for (String colIndex : columnBean.fieldIndexes()) {
				fieldBean = columnBean.getFieldBean(colIndex);
				ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
				Cell cell = row.createCell(fieldBean.getIndex()+firstCol);
				setCellValue(cell, excelColumn.name(),"text");
				if(cs != null){
					cell.setCellStyle(cs);
				}
			}
		}
	}
	protected void createHHead(Sheet ssheet, List<ColumnBean> columnBeans,int firstRow,int firstCol,boolean titleFlag,CellStyle cs1,CellStyle cs2,CellStyle cs3) {
		ssheet.createFreezePane(firstCol, 0);
		FieldBean fieldBean = null;
		Map<String,Map<String,Integer>> colMerge = new HashMap<String,Map<String,Integer>>();
		for(ColumnBean columnBean : columnBeans){
			for (String colIndex : columnBean.fieldIndexes()) {
				fieldBean = columnBean.getFieldBean(colIndex);
				ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
				Row row= null;
				if(ssheet.getRow(firstRow+fieldBean.getIndex())!= null){
					row = ssheet.getRow(firstRow+fieldBean.getIndex());
				}
				else{
					row = ssheet.createRow(firstRow+fieldBean.getIndex());
				}
				if(titleFlag){
					Cell cell = null;
					if(row.getCell(0)!= null){
						cell = row.getCell(0);
					}
					else{
						cell = row.createCell(0);
					}
					setCellValue(cell, excelColumn.colTitle(),"text");
					if(colMerge.get(excelColumn.colTitle())!= null){
						Map<String,Integer> map = colMerge.get(excelColumn.colTitle());
						if(fieldBean.getIndex() < map.get("start")){
							map.put("start",fieldBean.getIndex());
						}
						if(fieldBean.getIndex() > map.get("end")){
							map.put("end",fieldBean.getIndex());
						}
					}
					else{
						Map<String,Integer> map = new HashMap<String, Integer>();
						map.put("start", fieldBean.getIndex());
						map.put("end", fieldBean.getIndex());
						colMerge.put(excelColumn.colTitle(), map);
					}
					cell.setCellStyle(cs1);
					if(row.getCell(1)!= null){
						cell = row.getCell(1);
					}
					else{
						cell = row.createCell(1);
					}
					setCellValue(cell, excelColumn.name(),"text");
					if(fieldBean.getIndex() == 0){
						cell.setCellStyle(cs3);
					}
					else{
						cell.setCellStyle(cs2);
					}
					resetWidth(ssheet, 0, 20);
					resetWidth(ssheet, 1, 20);
				}
				else{
					Cell cell = null;
					if(row.getCell(0)!= null){
						cell = row.getCell(0);
					}
					else{
						row.createCell(0);
					}
					setCellValue(cell,  excelColumn.name(),"text");
					if(fieldBean.getIndex() == 0){
						cell.setCellStyle(cs3);
					}
					else{
						cell.setCellStyle(cs2);
					}
					resetWidth(ssheet, 0, 20);
				}
			}
		}
		if(colMerge.size() >0 ){
			for(String key : colMerge.keySet()){
				Map<String,Integer> map = colMerge.get(key);
				ssheet.addMergedRegion(new CellRangeAddress(firstRow+map.get("start"), firstRow+map.get("end"), 0, 0));
			}
		}
	}
	
	private void setCellValue(Cell cell, Object value,String type) {
		if(type.equals("text")){
			cell.setCellType(CellType.STRING);
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
		        	try{
		        		 cell.setCellFormula(getFormula(value));
		        	}
		        	catch(Exception e){
		        		cell.setCellValue(value.toString());
		        	}
		           
		        } else {
		            cell.setCellValue(value.toString());
		        }
		}
		else if(type.equals("currency")){
			double doubleValue = 0;
			try{
				doubleValue = ((Number) value).doubleValue();
			}
			catch(Exception e){
			}
	        cell.setCellValue(doubleValue);
	        setCellFormate(cell, "#,##0.00");
		}
		else{
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
		        	try{
		        		 cell.setCellFormula(getFormula(value));
		        	}
		        	catch(Exception e){
		        		cell.setCellValue(value.toString());
		        	}
		           
		        } else {
		            cell.setCellValue(value.toString());
		        }
		}
    }
	
	private void setCellFormate(Cell cell, String formate) {
		CellStyle cs = this.styleMap.get(formate);
		if(cs != null){
			cell.setCellStyle(cs);
		}
		else{
			cs =  wb.createCellStyle();
			cs.setDataFormat(wb.createDataFormat().getFormat(formate));
			cell.setCellStyle(cs);
			styleMap.put(formate, cs);
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
	
	/**
	 * 重新设置指定列的宽度
	 * 
	 * @param ssheet	工作表
	 * @param colIndex 	列的序号
	 * @param width		宽度
	 */
	protected void resetWidth(Sheet ssheet, int colIndex, int width) {
		if (width > 0) {
			ssheet.setColumnWidth(colIndex, width * 256);
		}
		else{
			ssheet.setColumnWidth(colIndex, 15 * 256);
		}
	}
	
	protected void createBody(Sheet ssheet, List<ColumnBean> columnBeans, List<?> entitis, int firstRow) {
		FieldBean fieldBean;
		Map<String,Map<Object,Object>> dsmap=new HashMap<String,Map<Object,Object>>();
		for (int j = 0 ; j < entitis.size()  ; j++) {
			Row row = ssheet.createRow(j + firstRow);
			for(ColumnBean columnBean : columnBeans){
				for (String colIndex : columnBean.fieldIndexes()) {
					fieldBean = columnBean.getFieldBean(colIndex);
					ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
					Object value=fieldBean.get(entitis.get(j));
					if(excelColumn!=null){
						if(excelColumn.type().equals("text"))
						{
							if(excelColumn.value().length>0&&excelColumn.text().length>0){
								if(value instanceof String){
									String combInfo="";
									String vlas[]=StringUtils.split(value.toString().trim(), ";");
									for(int h=0;h<vlas.length;h++){
										for(int k=0;k<excelColumn.text().length;k++){
											if(vlas[h].equals(excelColumn.value()[k])){
												combInfo+=excelColumn.text()[k]+",";
											}
										}
									}
									value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
								}
							}
						}
						if("date".equals(excelColumn.type())){
							if(value instanceof Timestamp){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Timestamp)value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Date){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Date) value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Long){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=new Date((Long)value);
									value=sdf.format(ds);
								}
							}
						}
						if(excelColumn.relDs().length>=3){
							ExcelBS ebs=SpringContextHolder.getBean("excelBS");
							String conditon="";
							Map<Object,Object> map=dsmap.get(excelColumn.relDs()[0]);
							if(excelColumn.relDs().length>=4){
								conditon=excelColumn.relDs()[3];
							}
							if(map==null){
								try {
									map=ebs.getValue(Class.forName(excelColumn.relDs()[0]), excelColumn.relDs()[2], excelColumn.relDs()[1], conditon);
									dsmap.put(excelColumn.relDs()[0], map);
								} catch (Exception e) {
									dsmap.put(excelColumn.relDs()[0], new HashMap<Object, Object>());
								} 
							}
							String vlas[]=StringUtils.split(value.toString().trim(), ";");
							String combInfo="";
							for(int k=0;k<vlas.length;k++){
								Object info;
								info = map.get(vlas[k]);
								combInfo+=(info==null)?"":info+",";
							}
							if(combInfo.length()>0)
								value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
						}
					}
					setCellValue(row.createCell(fieldBean.getIndex()), value,excelColumn.type()); 
				}
			}
		}
		resetComboxCell(ssheet, columnBeans, firstRow, entitis.size());
	}
	
	protected void createBody(Sheet ssheet, List<ColumnBean> columnBeans, List<?> entitis, int firstRow, Workbook workbook) {
		FieldBean fieldBean;
		Map<String,Map<Object,Object>> dsmap=new HashMap<String,Map<Object,Object>>();
		wb = workbook;
		CellStyle cellStyle= this.setCellStyle(); // 创建单元格样式
		Font font = wb.createFont();
		for (int j = 0 ; j < entitis.size()  ; j++) {
			Row row = ssheet.createRow(j + firstRow);
			for(ColumnBean columnBean : columnBeans){
				for (String colIndex : columnBean.fieldIndexes()) {
					fieldBean = columnBean.getFieldBean(colIndex);
					ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
					Object value=fieldBean.get(entitis.get(j));
					if(excelColumn!=null){
						if(excelColumn.type().equals("text"))
						{
							if(excelColumn.value().length>0&&excelColumn.text().length>0){
								if(value instanceof String){
									String combInfo="";
									String vlas[]=StringUtils.split(value.toString().trim(), ";");
									for(int h=0;h<vlas.length;h++){
										for(int k=0;k<excelColumn.text().length;k++){
											if(vlas[h].equals(excelColumn.value()[k])){
												combInfo+=excelColumn.text()[k]+",";
											}
										}
									}
									value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
								}
							}
						}
						if("date".equals(excelColumn.type())){
							if(value instanceof Timestamp){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Timestamp)value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Date){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Date) value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Long){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=new Date((Long)value);
									value=sdf.format(ds);
								}
							}
						}
						if(excelColumn.relDs().length>=3){
							ExcelBS ebs=SpringContextHolder.getBean("excelBS");
							String conditon="";
							Map<Object,Object> map=dsmap.get(excelColumn.relDs()[0]);
							if(excelColumn.relDs().length>=4){
								conditon=excelColumn.relDs()[3];
							}
							if(map==null){
								try {
									map=ebs.getValue(Class.forName(excelColumn.relDs()[0]), excelColumn.relDs()[2], excelColumn.relDs()[1], conditon);
									dsmap.put(excelColumn.relDs()[0], map);
								} catch (Exception e) {
									dsmap.put(excelColumn.relDs()[0], new HashMap<Object, Object>());
								} 
							}
							String vlas[]=StringUtils.split(value.toString().trim(), ";");
							String combInfo="";
							for(int k=0;k<vlas.length;k++){
								Object info;
								info = map.get(vlas[k]);
								combInfo+=(info==null)?"":info+",";
							}
							if(combInfo.length()>0)
								value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
						}
						//调整字体等列单元格样式
						setColCellStyle(font, cellStyle, excelColumn);
					}
					Cell cell = row.createCell(fieldBean.getIndex());
					setCellValue(cell, value,excelColumn.type()); 
					if((null != cell) && (null != cellStyle)) {
						cell.setCellStyle(cellStyle);
					}
				}
			}
		}
		resetComboxCell(ssheet, columnBeans, firstRow, entitis.size());
	}

	/**
	 * 按照字段类调整单元格样式
	 * @param font 
	 *
	 * @param cellStyle   单元格样式
	 * @param excelColumn Excel列属性
	 **/
	private void setColCellStyle(Font font, CellStyle cellStyle, ExcelColumn excelColumn) {
		if (null != wb) {
			font.setFontHeightInPoints(excelColumn.fontHeightInPoint());
			if (StringUtils.isNotBlank(excelColumn.fontName())) {
				font.setFontName(excelColumn.fontName());
			}
			cellStyle.setFont(font);
			cellStyle.setAlignment(excelColumn.alignment());
		}
	}

	protected void createBody(Sheet ssheet, List<ColumnBean> columnBeans, List<?> entitis, int firstRow,int firstCol,CellStyle cs0,CellStyle cs1,CellStyle cs2,String[] relName) {
		FieldBean fieldBean;
		Map<String,Map<Object,Object>> dsmap=new HashMap<String,Map<Object,Object>>();
		CreationHelper createHelper = ssheet.getWorkbook().getCreationHelper(); 
		for (int j = 0 ; j < entitis.size()  ; j++) {
			Row srow = ssheet.getRow(j + firstRow);
			if(srow == null){
				srow = ssheet.createRow(j + firstRow);
			}
			String colNo = "";
			String hcolNo = "";
			String hvalue = "";
			int crnum = 0;
			for(ColumnBean columnBean : columnBeans){
				for (String colIndex : columnBean.fieldIndexes()) {
					fieldBean = columnBean.getFieldBean(colIndex);
					ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
					Object value=fieldBean.get(entitis.get(j));
					if(excelColumn!=null){
						if(excelColumn.type().equals("text"))
						{
							if(excelColumn.value().length>0&&excelColumn.text().length>0){
								if(value instanceof String){
									String combInfo="";
									String vlas[]=StringUtils.split(value.toString().trim(), ";");
									for(int h=0;h<vlas.length;h++){
										for(int k=0;k<excelColumn.text().length;k++){
											if(vlas[h].equals(excelColumn.value()[k])){
												combInfo+=excelColumn.text()[k]+",";
											}
										}
									}
									value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
								}
							}
						}
						if("date".equals(excelColumn.type())){
							if(value instanceof Timestamp){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Timestamp)value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Date){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Date) value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Long){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=new Date((Long)value);
									value=sdf.format(ds);
								}
							}
						}
						if(excelColumn.relDs().length>=3){
							ExcelBS ebs=SpringContextHolder.getBean("excelBS");
							String conditon="";
							Map<Object,Object> map=dsmap.get(excelColumn.relDs()[0]);
							if(excelColumn.relDs().length>=4){
								conditon=excelColumn.relDs()[3];
							}
							if(map==null){
								try {
									map=ebs.getValue(Class.forName(excelColumn.relDs()[0]), excelColumn.relDs()[2], excelColumn.relDs()[1], conditon);
									dsmap.put(excelColumn.relDs()[0], map);
								} catch (Exception e) {
									dsmap.put(excelColumn.relDs()[0], new HashMap<Object, Object>());
								} 
							}
							String vlas[]=StringUtils.split(value.toString().trim(), ";");
							String combInfo="";
							for(int k=0;k<vlas.length;k++){
								Object info;
								info = map.get(vlas[k]);
								combInfo+=(info==null)?"":info+",";
							}
							if(combInfo.length()>0)
								value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
						}
					}
					Cell cell = srow.createCell(firstCol+fieldBean.getIndex());
					if(j%2 ==1){
						cell.setCellStyle(cs1);
					}
					else{
						cell.setCellStyle(cs2);
					}
					if(relName.length>0){
						if(fieldBean.getName().equals(relName[0])){
							colNo = value.toString();
							hcolNo = String.valueOf(j + firstRow+1);
						}
						if(relName.length==2){
							if(fieldBean.getName().equals(relName[1])){
								hvalue =  value.toString();
							}
						}
					}
					setCellValue(cell, value,excelColumn.type()); 
					crnum++;
				}
				for (String colIndex : columnBean.fieldIndexes()) {
					fieldBean = columnBean.getFieldBean(colIndex);
					ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
					resetWidth(ssheet, fieldBean.getIndex()+firstCol, excelColumn.width());
				}
				if(StringUtils.isNotBlank(colNo)){
					int col = lettrToCell(colNo).get("col")-1;
					int row = lettrToCell(colNo).get("row")-1;
					Row trow = this.tmpsh.getRow(row);
					if(trow == null){
						this.tmpsh.createRow(row);
					}
					Cell cell = trow.getCell(col);
					if(cell == null){
						cell = trow.createCell(col);
					}
					if (StringUtils.isBlank(hvalue)) {
						if (cell.getCellTypeEnum() == CellType.FORMULA) {
							hvalue = StringUtils.trimToEmpty(cell.getCellFormula());
						} else if(cell.getCellTypeEnum() == CellType.NUMERIC){
							hvalue = cell.getNumericCellValue() +"";
						} else {
							hvalue = cell.getStringCellValue();
						}
					}
					if (hvalue.startsWith("=")) {
						hvalue = hvalue.substring(1);
					}
					StringBuilder sb1 = new StringBuilder();
					intToLetter(firstCol+1, sb1);
					StringBuilder sb2 = new StringBuilder();
					intToLetter(firstCol+crnum, sb2);
					hcolNo = sb1.toString()+hcolNo+":"+sb2.toString()+hcolNo;
					Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
					link.setAddress(ssheet.getSheetName() + "!" + hcolNo);
					cell.setHyperlink(link);
					if (cell.getCellTypeEnum() != CellType.FORMULA) {
						hvalue = StringUtils.remove(hvalue, '"');
						cell.setCellType(CellType.STRING);
						cell.setCellValue(hvalue);
					}
				}
			}
		}
		//添加下拉框单元格
		resetComboxCell(ssheet, columnBeans, firstRow, entitis.size());
	}
	
	protected void createHBody(Sheet ssheet, List<ColumnBean> columnBeans, List<?> entitis, int firstRow,int firstCol,CellStyle cs1,CellStyle cs2,CellStyle cs3,String[] relName) {
		FieldBean fieldBean;
		Map<String,Map<Object,Object>> dsmap=new HashMap<String,Map<Object,Object>>();
		CreationHelper createHelper = ssheet.getWorkbook().getCreationHelper(); 
		for (int j = 0 ; j < entitis.size()  ; j++) {
			resetWidth(ssheet, firstCol+j, 20);
			String colNo = "";
			String hcolNo = "";
			String hvalue = "";
			int crnum = 0;
			for(ColumnBean columnBean : columnBeans){
				for (String colIndex : columnBean.fieldIndexes()) {
					fieldBean = columnBean.getFieldBean(colIndex);
					ExcelColumn excelColumn = columnBean.getFieldBean(colIndex).getExcelColumnAnnotation();
					Object value=fieldBean.get(entitis.get(j));
					if(excelColumn!=null){
						if(excelColumn.type().equals("text"))
						{
							if(excelColumn.value().length>0&&excelColumn.text().length>0){
								if(value instanceof String){
									String combInfo="";
									String vlas[]=StringUtils.split(value.toString().trim(), ";");
									for(int h=0;h<vlas.length;h++){
										for(int k=0;k<excelColumn.text().length;k++){
											if(vlas[h].equals(excelColumn.value()[k])){
												combInfo+=excelColumn.text()[k]+",";
											}
										}
									}
									if(vlas.length <= 0){
										for(int k=0;k<excelColumn.text().length;k++){
											if("".equals(excelColumn.value()[k])){
												combInfo+=excelColumn.text()[k]+",";
											}
										}
									}
									value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
								}
							}
						}
						if("date".equals(excelColumn.type())){
							if(value instanceof Timestamp){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Timestamp)value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Date){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=(Date) value;
									value=sdf.format(ds);
								}
							}
							if(value instanceof Long){
								if(!"".equals(excelColumn.formate())){
									SimpleDateFormat sdf=new SimpleDateFormat(excelColumn.formate());
									Date ds=new Date((Long)value);
									value=sdf.format(ds);
								}
							}
						}
						if(excelColumn.relDs().length>=3){
							ExcelBS ebs=SpringContextHolder.getBean("excelBS");
							String conditon="";
							Map<Object,Object> map=dsmap.get(excelColumn.relDs()[0]);
							if(excelColumn.relDs().length>=4){
								conditon=excelColumn.relDs()[3];
							}
							if(map==null){
								try {
									map=ebs.getValue(Class.forName(excelColumn.relDs()[0].trim()), excelColumn.relDs()[2], excelColumn.relDs()[1], conditon);
									dsmap.put(excelColumn.relDs()[0], map);
								} catch (Exception e) {
									e.printStackTrace();
									dsmap.put(excelColumn.relDs()[0], new HashMap<Object, Object>());
								} 
							}
							String vlas[]=StringUtils.split(value.toString().trim(), ";");
							String combInfo="";
							for(int k=0;k<vlas.length;k++){
								Object info;
								info = map.get(vlas[k]);
								combInfo+=(info==null)?"":info+",";
							}
							if(vlas.length <= 0){
								Object info;
								info = map.get("");
								combInfo+=(info==null)?"":info+",";
							}
							if(combInfo.length()>0)
								value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
						}
					}
					Row row = ssheet.getRow(fieldBean.getIndex() + firstRow);
					Cell cell = row.createCell(j+firstCol);
					if(fieldBean.getIndex() == 0){
						cell.setCellStyle(cs3);
					}
					else{
						if(j%2 ==1){
							cell.setCellStyle(cs1);
						}
						else{
							cell.setCellStyle(cs2);
						}
					}
					if(relName.length>0){
						if(fieldBean.getName().equals(relName[0])){
							colNo = value.toString();
							StringBuilder sb = new StringBuilder("");
							intToLetter(j+firstCol+1, sb);
							hcolNo = sb.toString();
						}
						if(relName.length==2){
							if(fieldBean.getName().equals(relName[1])){
								hvalue =  value.toString();
							}
						}
					}
					setCellValue(cell, value,excelColumn.type()); 
					crnum++;
				}
				if(StringUtils.isNotBlank(colNo)){
					int col = lettrToCell(colNo).get("col")-1;
					int row = lettrToCell(colNo).get("row")-1;
					Row trow = this.tmpsh.getRow(row);
					if(trow == null){
						this.tmpsh.createRow(row);
					}
					Cell cell = trow.getCell(col);
					if(cell == null){
						cell = trow.createCell(col);
					}
					if(!StringUtils.isNotBlank(hvalue)){
						hvalue = cell.getStringCellValue();
					}
					if (hvalue.startsWith("=")) {
						hvalue = hvalue.substring(1);
					}
					hcolNo = hcolNo+(firstRow+1)+":"+hcolNo+(firstRow+crnum);
					Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
					link.setAddress(ssheet.getSheetName() + "!" + hcolNo);
					cell.setHyperlink(link);
					hvalue = StringUtils.remove(hvalue, '"');
					cell.setCellType(CellType.STRING);
					cell.setCellValue(hvalue);
				}
			}
		}
	}
	
	private void intToLetter(int num,StringBuilder letter){
		int z=num/26;
		int y=num%26;
		if(y==0){
			y=26;
			z--;
		}
		if(z>26){
			letter.insert(0, (char)(y+64));
			intToLetter(z,letter);
		}
		else{
			letter.insert(0, (char)(y+64));
			if(z>0){
				letter.insert(0, (char)(z+64));
			}
		}
	}
	private int letterToInt(String letter){
		int length=letter.length();
		int result=0;
		for(int i=0;i<length;i++){
			int num=letter.charAt(i)-64;
			result+=num*Math.pow(26, length-i-1);
		}
		return result;
	}
	public Map<String,Integer> lettrToCell(String letter){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int col = letterToInt(StringUtils.substring(
				letter, 0, i));
		int row=Integer.parseInt(StringUtils.substring(
				letter, i, letter.length()));
		Map<String,Integer> cellInfo=new HashMap<String, Integer>();
		cellInfo.put("row", row);
		cellInfo.put("col", col);
		return cellInfo;
//			}
	}
//		return -1;
//	}
	
	private void setHSSFValidation(Sheet ssheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
		if (textlist.length > 0) {
			// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
			CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
			DataValidationHelper helper = ssheet.getDataValidationHelper();
			DataValidationConstraint constraint = helper.createExplicitListConstraint(textlist);
			DataValidation validation = helper.createValidation(constraint, regions);
			ssheet.addValidationData(validation);
		}
	}
	
	protected void resetTable(XSSFSheet xsheet, int talbeLength) {
		String ref;
		for(XSSFTable xt : xsheet.getTables()){
			ref = xt.getCTTable().getRef();
			if (StringUtils.startsWith(ref, "A1")) {
				xt.getCTTable().setRef(StringUtils.substringBeforeLast(ref, String.valueOf(xt.getEndCellReference().getRow() + 1)) + talbeLength);
			}
		}
	}
	
	/**
	 * 设置默认单元格样式
	 * @return
	 */
	private CellStyle setCellStyle() {
		if(null != wb) {
	        CellStyle cellStyle= wb.createCellStyle(); // 创建单元格样式
	        cellStyle.setAlignment(HorizontalAlignment.CENTER);  // 设置单元格水平方向对其方式为填充
	        cellStyle.setBorderTop(BorderStyle.THIN);
	        cellStyle.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
	        cellStyle.setBorderRight(BorderStyle.THIN);
	        cellStyle.setRightBorderColor(HSSFColorPredefined.BLACK.getIndex());
	        cellStyle.setBorderBottom(BorderStyle.THIN);
	        cellStyle.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
	        cellStyle.setBorderLeft(BorderStyle.THIN);
	        cellStyle.setLeftBorderColor(HSSFColorPredefined.BLACK.getIndex());
	        return cellStyle;
		}
		return null;
	}
	
}
