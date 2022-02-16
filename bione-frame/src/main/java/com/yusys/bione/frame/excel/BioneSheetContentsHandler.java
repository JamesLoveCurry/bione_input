/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.util.List;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import com.google.common.collect.Lists;

/**
 * @author tanxu
 *
 */
public class BioneSheetContentsHandler implements SheetContentsHandler {
	
	private Class<?> clazz;
	
	private List<Object> data = Lists.newArrayList();
	
	private Object instance;
	
	private ColumnBean columnBean;
	
	private int firstRow;
	
	private boolean hasNewCell = false;
	
	public BioneSheetContentsHandler(Class<?> clazz) {
		this.clazz = clazz;
		this.columnBean = ClassMapping.getInstance().getMappingColumn(clazz);
		this.firstRow = this.columnBean.getExcelSheet().firstRow();
	}

	@Override
	public void startRow(int rowNum) {
		try {
			if (rowNum >= firstRow) {
				this.instance = clazz.newInstance();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endRow(int rowNum) {
		if (this.instance != null && this.hasNewCell == true) {
			data.add(this.instance);
			this.hasNewCell = false;
			this.instance = null;
		}
	}

	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
		if (this.instance != null) {
			this.hasNewCell = true;
			FieldBean fieldBean = this.columnBean
					.getFieldBean(ExcelAnnotationUtil
							.getExcelColName(cellReference));
			if (fieldBean != null) {
				fieldBean.set(this.instance, formattedValue);
			}
		}
	}

	@Override
	public void headerFooter(String text, boolean isHeader, String tagName) {
	}
	
	public List<Object> getData() {
		return this.data;
	}
}
