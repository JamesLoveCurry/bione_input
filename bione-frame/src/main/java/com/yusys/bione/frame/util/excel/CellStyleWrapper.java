package com.yusys.bione.frame.util.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class CellStyleWrapper implements CellStyle {

	private Workbook workbook;

	private CellStyle cellStyle;

	private FontWrapper fontWrapper;

	public CellStyleWrapper(Workbook workbook, CellStyle cellStyle) {
		this.workbook = workbook;
		this.cellStyle = cellStyle;
	}

	public void refresh(Workbook workbook) {
		if (this.workbook == workbook) {
			if (fontWrapper != null && fontWrapper.getWorkbook() != workbook) {
				cellStyle.setFont(fontWrapper.refresh(workbook).getFont());
			}
		} else {
			this.workbook = workbook;
			CellStyle newCellStyle = workbook.createCellStyle();
			if (cellStyle != null) {
				newCellStyle.setAlignment(cellStyle.getAlignmentEnum());
				newCellStyle.setBorderBottom(cellStyle.getBorderBottomEnum());
				newCellStyle.setBorderLeft(cellStyle.getBorderLeftEnum());
				newCellStyle.setBorderRight(cellStyle.getBorderRightEnum());
				newCellStyle.setBorderTop(cellStyle.getBorderTopEnum());
				newCellStyle.setBottomBorderColor(cellStyle.getBottomBorderColor());
				newCellStyle.setDataFormat(cellStyle.getDataFormat());
				newCellStyle.setFillBackgroundColor(cellStyle.getFillBackgroundColor());
				newCellStyle.setFillForegroundColor(cellStyle.getFillForegroundColor());
				newCellStyle.setFillPattern(cellStyle.getFillPatternEnum());
				if (fontWrapper != null) {
					newCellStyle.setFont(fontWrapper.refresh(workbook).getFont());
				}
				newCellStyle.setHidden(cellStyle.getHidden());
				newCellStyle.setIndention(cellStyle.getIndention());
				newCellStyle.setLeftBorderColor(cellStyle.getLeftBorderColor());
				newCellStyle.setLocked(cellStyle.getLocked());
				newCellStyle.setQuotePrefixed(cellStyle.getQuotePrefixed());
				newCellStyle.setRightBorderColor(cellStyle.getRightBorderColor());
				newCellStyle.setRotation(cellStyle.getRotation());
				newCellStyle.setShrinkToFit(cellStyle.getShrinkToFit());
				newCellStyle.setTopBorderColor(cellStyle.getTopBorderColor());
				newCellStyle.setVerticalAlignment(cellStyle.getVerticalAlignmentEnum());
				newCellStyle.setWrapText(cellStyle.getWrapText());
			}
			cellStyle = newCellStyle;
		}
	}

	@Override
	public void cloneStyleFrom(CellStyle source) {
		cellStyle.cloneStyleFrom(source);
	}

	@Deprecated
	@Override
	public short getAlignment() {
		return cellStyle.getAlignment();
	}

	@Override
	public HorizontalAlignment getAlignmentEnum() {
		return cellStyle.getAlignmentEnum();
	}

	@Deprecated
	@Override
	public short getBorderBottom() {
		return cellStyle.getBorderBottom();
	}

	@Override
	public BorderStyle getBorderBottomEnum() {
		return cellStyle.getBorderBottomEnum();
	}

	@Deprecated
	@Override
	public short getBorderLeft() {
		return cellStyle.getBorderLeft();
	}

	@Override
	public BorderStyle getBorderLeftEnum() {
		return cellStyle.getBorderLeftEnum();
	}

	@Deprecated
	@Override
	public short getBorderRight() {
		return cellStyle.getBorderRight();
	}

	@Override
	public BorderStyle getBorderRightEnum() {
		return cellStyle.getBorderRightEnum();
	}

	@Deprecated
	@Override
	public short getBorderTop() {
		return cellStyle.getBorderTop();
	}

	@Override
	public BorderStyle getBorderTopEnum() {
		return cellStyle.getBorderTopEnum();
	}

	@Override
	public short getBottomBorderColor() {
		return cellStyle.getBottomBorderColor();
	}

	@Override
	public short getDataFormat() {
		return cellStyle.getDataFormat();
	}

	@Override
	public String getDataFormatString() {
		return cellStyle.getDataFormatString();
	}

	@Override
	public short getFillBackgroundColor() {
		return cellStyle.getFillBackgroundColor();
	}

	@Override
	public Color getFillBackgroundColorColor() {
		return cellStyle.getFillBackgroundColorColor();
	}

	@Override
	public short getFillForegroundColor() {
		return cellStyle.getFillForegroundColor();
	}

	@Override
	public Color getFillForegroundColorColor() {
		return cellStyle.getFillForegroundColorColor();
	}

	@Deprecated
	@Override
	public short getFillPattern() {
		return cellStyle.getFillPattern();
	}

	@Override
	public FillPatternType getFillPatternEnum() {
		return cellStyle.getFillPatternEnum();
	}

	@Override
	public short getFontIndex() {
		return cellStyle.getFontIndex();
	}

	@Override
	public boolean getHidden() {
		return cellStyle.getHidden();
	}

	@Override
	public short getIndention() {
		return cellStyle.getIndention();
	}

	@Override
	public short getIndex() {
		return cellStyle.getIndex();
	}

	@Override
	public short getLeftBorderColor() {
		return cellStyle.getLeftBorderColor();
	}

	@Override
	public boolean getLocked() {
		return cellStyle.getLocked();
	}

	@Override
	public boolean getQuotePrefixed() {
		return cellStyle.getQuotePrefixed();
	}

	@Override
	public short getRightBorderColor() {
		return cellStyle.getRightBorderColor();
	}

	@Override
	public short getRotation() {
		return cellStyle.getRotation();
	}

	@Override
	public boolean getShrinkToFit() {
		return cellStyle.getShrinkToFit();
	}

	@Override
	public short getTopBorderColor() {
		return cellStyle.getTopBorderColor();
	}

	@Deprecated
	@Override
	public short getVerticalAlignment() {
		return cellStyle.getVerticalAlignment();
	}

	@Override
	public VerticalAlignment getVerticalAlignmentEnum() {
		return cellStyle.getVerticalAlignmentEnum();
	}

	@Override
	public boolean getWrapText() {
		return cellStyle.getWrapText();
	}

	@Override
	public void setAlignment(HorizontalAlignment align) {
		cellStyle.setAlignment(align);
	}

	@Override
	public void setBorderBottom(BorderStyle border) {
		cellStyle.setBorderBottom(border);
	}

	@Override
	public void setBorderLeft(BorderStyle border) {
		cellStyle.setBorderLeft(border);
	}

	@Override
	public void setBorderRight(BorderStyle border) {
		cellStyle.setBorderRight(border);
	}

	@Override
	public void setBorderTop(BorderStyle border) {
		cellStyle.setBorderTop(border);
	}

	@Override
	public void setBottomBorderColor(short color) {
		cellStyle.setBottomBorderColor(color);
	}

	@Override
	public void setDataFormat(short fmt) {
		cellStyle.setDataFormat(fmt);
	}

	@Override
	public void setFillBackgroundColor(short bg) {
		cellStyle.setFillBackgroundColor(bg);
	}

	@Override
	public void setFillForegroundColor(short fg) {
		cellStyle.setFillForegroundColor(fg);
	}

	@Override
	public void setFillPattern(FillPatternType fp) {
		cellStyle.setFillPattern(fp);
	}

	@Override
	public void setFont(Font font) {
		fontWrapper = (FontWrapper)font;
		cellStyle.setFont(fontWrapper.getFont());
	}

	@Override
	public void setHidden(boolean hidden) {
		cellStyle.setHidden(hidden);
	}

	@Override
	public void setIndention(short indent) {
		cellStyle.setIndention(indent);
	}

	@Override
	public void setLeftBorderColor(short color) {
		cellStyle.setLeftBorderColor(color);
	}

	@Override
	public void setLocked(boolean locked) {
		cellStyle.setLocked(locked);
	}

	@Override
	public void setQuotePrefixed(boolean quotePrefix) {
		cellStyle.setQuotePrefixed(quotePrefix);
	}

	@Override
	public void setRightBorderColor(short color) {
		cellStyle.setRightBorderColor(color);
	}

	@Override
	public void setRotation(short rotation) {
		cellStyle.setRotation(rotation);
	}

	@Override
	public void setShrinkToFit(boolean shrinkToFit) {
		cellStyle.setShrinkToFit(shrinkToFit);
	}

	@Override
	public void setTopBorderColor(short color) {
		cellStyle.setTopBorderColor(color);
	}

	@Override
	public void setVerticalAlignment(VerticalAlignment align) {
		cellStyle.setVerticalAlignment(align);
	}

	@Override
	public void setWrapText(boolean wrapped) {
		cellStyle.setWrapText(wrapped);
	}

	public CellStyle getCellStyle() {
		return cellStyle;
	}
}
