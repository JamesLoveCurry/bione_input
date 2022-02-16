/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;
import com.yusys.bione.plugin.spreadjs.entity.Style;
import com.yusys.bione.plugin.spreadjs.entity.StyleBorder;

/**
 * @author tanxu
 * 
 */
public class ExcelReader extends AbstractExcelReader {

	private String filePath;

	private Workbook workbook;

	public ExcelReader(File file) {
		if(file != null)
			filePath = file.getAbsolutePath();
	}

	/**
	 * 
	 * @param side
	 *            0-左；1-右；2-上；3-下
	 * @param cellStyle
	 * @return
	 */
	private StyleBorder getBorderInfo(Workbook workbook, int side, CellStyle cellStyle) {
		StyleBorder border = null;
		switch (side) {
		case 0:
			if(cellStyle.getBorderLeftEnum()!=BorderStyle.NONE) {
				border = new StyleBorder();
				border.setStyle((int) cellStyle.getBorderLeftEnum().getCode());
				border.setColor(getBorderColorRGB(workbook, cellStyle, CellUtil.BORDER_LEFT));
			}
			break;
		case 1:
			if(cellStyle.getBorderRightEnum()!=BorderStyle.NONE) {
				border = new StyleBorder();
				border.setStyle((int) cellStyle.getBorderRightEnum().getCode());
				border.setColor(getBorderColorRGB(workbook, cellStyle, CellUtil.BORDER_RIGHT));
			}
			break;
		case 2:
			if(cellStyle.getBorderTopEnum()!=BorderStyle.NONE) {
				border = new StyleBorder();
				border.setStyle((int) cellStyle.getBorderTopEnum().getCode());
				border.setColor(getBorderColorRGB(workbook, cellStyle, CellUtil.BORDER_TOP));
			}
			break;
		case 3:
			if(cellStyle.getBorderBottomEnum()!=BorderStyle.NONE) {
				border = new StyleBorder();
				border.setStyle((int) cellStyle.getBorderBottomEnum().getCode());
				border.setColor(getBorderColorRGB(workbook, cellStyle, CellUtil.BORDER_BOTTOM));
			}
			break;
		}
		return border;
	}

	/**
	 * 获取索引颜色的RGB值
	 */
	private String getIndexedColorRGB(HSSFWorkbook HSSFWorkbook, int colorIdx, HSSFColorPredefined defaultColor) {
		if (colorIdx == HSSFColorPredefined.AUTOMATIC.getIndex()) {
			colorIdx = defaultColor.getIndex();
		}
		return getColorRGB(HSSFWorkbook.getCustomPalette().getColor(colorIdx), defaultColor);
	}

	/**
	 * 获取字体颜色
	 */
	private String getFontColorRGB(Workbook workbook, Font font) {
		if (workbook instanceof WorkbookWrapper) {
			workbook = ((WorkbookWrapper)workbook).getWorkbook();
		}
		if (workbook instanceof HSSFWorkbook) {
			return getIndexedColorRGB((HSSFWorkbook)workbook, font.getColor(), HSSFColorPredefined.BLACK);
		}
		if (font instanceof XSSFFont) {
			return getColorRGB(((XSSFFont)font).getXSSFColor(), HSSFColorPredefined.BLACK);
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * 获取边框颜色
	 */
	private String getBorderColorRGB(Workbook workbook, CellStyle cellStyle, String borderSide) {
		if (workbook instanceof WorkbookWrapper) {
			workbook = ((WorkbookWrapper)workbook).getWorkbook();
		}
		if (workbook instanceof HSSFWorkbook) {
			if (CellUtil.BORDER_TOP.equals(borderSide)) {
				return getIndexedColorRGB((HSSFWorkbook)workbook, cellStyle.getTopBorderColor(), HSSFColorPredefined.BLACK);
			}
			if (CellUtil.BORDER_RIGHT.equals(borderSide)) {
				return getIndexedColorRGB((HSSFWorkbook)workbook, cellStyle.getRightBorderColor(), HSSFColorPredefined.BLACK);
			}
			if (CellUtil.BORDER_BOTTOM.equals(borderSide)) {
				return getIndexedColorRGB((HSSFWorkbook)workbook, cellStyle.getBottomBorderColor(), HSSFColorPredefined.BLACK);
			}
			if (CellUtil.BORDER_LEFT.equals(borderSide)) {
				return getIndexedColorRGB((HSSFWorkbook)workbook, cellStyle.getLeftBorderColor(), HSSFColorPredefined.BLACK);
			}
		}
		if (cellStyle instanceof XSSFCellStyle) {
			if (CellUtil.BORDER_TOP.equals(borderSide)) {
				return getColorRGB(((XSSFCellStyle)cellStyle).getTopBorderXSSFColor(), HSSFColorPredefined.BLACK);
			}
			if (CellUtil.BORDER_RIGHT.equals(borderSide)) {
				return getColorRGB(((XSSFCellStyle)cellStyle).getRightBorderXSSFColor(), HSSFColorPredefined.BLACK);
			}
			if (CellUtil.BORDER_BOTTOM.equals(borderSide)) {
				return getColorRGB(((XSSFCellStyle)cellStyle).getBottomBorderXSSFColor(), HSSFColorPredefined.BLACK);
			}
			if (CellUtil.BORDER_LEFT.equals(borderSide)) {
				return getColorRGB(((XSSFCellStyle)cellStyle).getLeftBorderXSSFColor(), HSSFColorPredefined.BLACK);
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * 获取背景色
	 */
	private String getBackgroundColor(Workbook workbook, CellStyle cellStyle) {
		if (workbook instanceof WorkbookWrapper) {
			workbook = ((WorkbookWrapper)workbook).getWorkbook();
		}
		if (workbook instanceof HSSFWorkbook) {
			return getIndexedColorRGB((HSSFWorkbook)workbook, cellStyle.getFillForegroundColor(), HSSFColorPredefined.WHITE);
		} else {
			return getColorRGB(cellStyle.getFillForegroundColorColor(), HSSFColorPredefined.WHITE);
		}
	}

	@Override
	public SpreadSchema read() throws SpreadImportException {
		try {
			workbook = WorkbookWrapper.openExcel(filePath);
			SpreadSchema schema = createBaseSchema(workbook);
			schema.setUseWijmoTheme(true);
			int sheetnum=1;
			if(this.sheetCount>0){
				sheetnum=workbook.getNumberOfSheets()<=sheetCount?workbook.getNumberOfSheets():sheetCount;
			}
			for (int i = 0; i < sheetnum; i++) {// 循环读取工作簿数据
				Sheet sheet = workbook.getSheetAt(i);
				SheetsProperty st = createSheetBaseInfo(sheet);// 工作簿参数
				st.setDefaults(getDefaultInfo(sheet));
				st.setGridline(getSheetGridLineInfo(sheet));// 设置工作薄网格线信息
				st.setSpans(getSheetSpanInfo(sheet));// 设置合并单元格区域
				st.setData(getSheetDataInfo(sheet, false));// 设置工作薄数据集合
				st.setIndex(i);// 设置工作簿的索引
				st.setVisible(true);// 设置工作簿的可见性
				st.setRowCount(st.getData().getRowCount());
				st.setColumnCount(st.getData().getColCount());
				st.setColumns(getColumnSize(sheet, st.getColumnCount()));
				st.setRows(getRowSize(sheet, st.getRowCount()));
				schema.getSheetsProperties().put(sheet.getSheetName(), st);// 设置工作簿的工作簿参数
			}
			schema.setNamedStyles(getNamedStyles().values());
			return schema;
		} catch (IOException e) {
			throw new SpreadImportException(e);
		} finally {
			IOUtils.closeQuietly(workbook);
			workbook = null;
		}
	}
	
	public String readTmpToString() throws SpreadImportException {
		try {
			workbook = WorkbookWrapper.openExcel(filePath);
			SpreadSchema schema = createBaseSchema(workbook);
			schema.setUseWijmoTheme(true);
			int sheetnum=1;
			if(workbook.getNumberOfSheets() < 2){
				return null;
			}
			Sheet sheet = workbook.getSheetAt(sheetnum);
			if(sheet.getLastRowNum() == 0){//由于表样可能是空，根据（5-单元格属性）获取最后一行
				sheet = workbook.getSheetAt(4); 
			}
			SheetsProperty st = createSheetBaseInfo(sheet);// 工作簿参数
			st.setDefaults(getDefaultInfo(sheet));
			st.setGridline(getSheetGridLineInfo(sheet));// 设置工作薄网格线信息
			st.setSpans(getSheetSpanInfo(sheet));// 设置合并单元格区域
			st.setData(getSheetDataInfo(sheet, false));// 设置工作薄数据集合
			st.setIndex(0);// 设置工作簿的索引
			st.setVisible(true);// 设置工作簿的可见性
			st.setRowCount(st.getData().getRowCount());
			st.setColumnCount(st.getData().getColCount());
			st.setColumns(getColumnSize(sheet, st.getColumnCount()));
			st.setRows(getRowSize(sheet, st.getRowCount()));
			schema.getSheetsProperties().put(sheet.getSheetName(), st);// 设置工作簿的工作簿参数
			schema.setNamedStyles(getNamedStyles().values());
			return JSON.toJSONString(schema, SerializerFeature.DisableCircularReferenceDetect);
		} catch (IOException e) {
			throw new SpreadImportException(e);
		} finally {
			IOUtils.closeQuietly(workbook);
			workbook = null;
		}
	}
	
	/**
	 * 根据单元格样式创建样式对象
	 * 
	 * @param cellStyle
	 * @return
	 */
	@Override
	protected Style createNameStyle(Workbook workbook, CellStyle cellStyle, boolean flag) {
		Style namedstyle = new Style();
		if (flag) {
			namedstyle.setName(createStyleName());
		}
		Font font = workbook.getFontAt(cellStyle.getFontIndex());
		if (font != null) {
			namedstyle.setFont(getFontInfo(font));
			namedstyle.setTextDecoration(getUnderLineInfo(font.getUnderline(),
					font.getStrikeout()));
			if(font.getColor() != Font.COLOR_NORMAL){
				namedstyle.setForeColor(getFontColorRGB(workbook, font));
			}
		}
		if (cellStyle.getDataFormatString() != null && ! cellStyle.getDataFormatString().equals("General")) {
			namedstyle.setFormatter(cellStyle.getDataFormatString());
		}
		if (cellStyle.getIndention() != 0) {
			namedstyle.setTextIndent((int) cellStyle.getIndention());
		}
		namedstyle.setHAlign(getHorizontalAlignType(cellStyle.getAlignmentEnum().getCode()));
		namedstyle.setVAlign(getVerticalAlignType(cellStyle.getVerticalAlignmentEnum().getCode()));
		namedstyle.setLocked(cellStyle.getLocked());
		//舍去白色Background避免遮挡默认边框线  
		if(!"#ffffff".equals(getBackgroundColor(workbook, cellStyle))) {
			namedstyle.setBackColor(getBackgroundColor(workbook, cellStyle));
		}
		namedstyle.setWordWrap(cellStyle.getWrapText());
		namedstyle.setBorderLeft(getBorderInfo(workbook, 0, cellStyle));
		namedstyle.setBorderRight(getBorderInfo(workbook, 1, cellStyle));
		namedstyle.setBorderTop(getBorderInfo(workbook, 2, cellStyle));
		namedstyle.setBorderBottom(getBorderInfo(workbook, 3, cellStyle));
		if (flag) {
			setStyleMap(cellStyle, namedstyle.getName());
		}
		return namedstyle;
	}

}
