package com.yusys.bione.frame.util.excel;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

public class FontWrapper implements Font {

	private Workbook workbook;

	private Font font;
	
	public FontWrapper(Workbook workbook, Font font) {
		this.workbook = workbook;
		this.font = font;
	}

	public FontWrapper refresh(Workbook workbook) {
		if (this.workbook != workbook) {
			this.workbook = workbook;
			Font newFont = workbook.createFont();
			if (font != null) {
				newFont.setBold(font.getBold());
				newFont.setCharSet(font.getCharSet());
				newFont.setColor(font.getColor());
				newFont.setFontHeight(font.getFontHeight());
				newFont.setFontHeightInPoints(font.getFontHeightInPoints());
				newFont.setFontName(font.getFontName());
				newFont.setItalic(font.getItalic());
				newFont.setStrikeout(font.getStrikeout());
				newFont.setTypeOffset(font.getTypeOffset());
				newFont.setUnderline(font.getUnderline());
			}
			font = newFont;
		}
		return this;
	}

	@Override
	public boolean getBold() {
		return font.getBold();
	}

	@Override
	public int getCharSet() {
		return font.getCharSet();
	}

	@Override
	public short getColor() {
		return font.getColor();
	}

	@Override
	public short getFontHeight() {
		return font.getFontHeight();
	}

	@Override
	public short getFontHeightInPoints() {
		return font.getFontHeightInPoints();
	}

	@Override
	public String getFontName() {
		return font.getFontName();
	}

	@Override
	public short getIndex() {
		return font.getIndex();
	}

	@Override
	public boolean getItalic() {
		return font.getItalic();
	}

	@Override
	public boolean getStrikeout() {
		return font.getStrikeout();
	}

	@Override
	public short getTypeOffset() {
		return font.getTypeOffset();
	}

	@Override
	public byte getUnderline() {
		return font.getUnderline();
	}

	@Override
	public void setBold(boolean bold) {
		font.setBold(bold);
	}

	@Override
	public void setCharSet(byte charset) {
		font.setCharSet(charset);
	}

	@Override
	public void setCharSet(int charset) {
		font.setCharSet(charset);
	}

	@Override
	public void setColor(short color) {
		font.setColor(color);
	}

	@Override
	public void setFontHeight(short height) {
		font.setFontHeight(height);
	}

	@Override
	public void setFontHeightInPoints(short height) {
		font.setFontHeightInPoints(height);
	}

	@Override
	public void setFontName(String name) {
		font.setFontName(name);
	}

	@Override
	public void setItalic(boolean italic) {
		font.setItalic(italic);
	}

	@Override
	public void setStrikeout(boolean strikeout) {
		font.setStrikeout(strikeout);
	}

	@Override
	public void setTypeOffset(short offset) {
		font.setTypeOffset(offset);
	}

	@Override
	public void setUnderline(byte underline) {
		font.setUnderline(underline);
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public Font getFont() {
		return font;
	}
}
