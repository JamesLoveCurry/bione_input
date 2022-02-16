package com.yusys.bione.frame.util.excel;

import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

public class CellWrapper implements Cell {

	private Workbook workbook;

	private Cell cell;

	public CellWrapper(Workbook workbook, Cell cell) {
		this.workbook = workbook;
		this.cell = cell;
	}

	@Deprecated
	public CellWrapper(Workbook workbook, Row row, int column, int type) {
		this.workbook = workbook;
		cell = row.createCell(column, type);
	}

	public CellWrapper(Workbook workbook, Row row, int column, CellType type) {
		this.workbook = workbook;
		cell = row.createCell(column, type);
	}

	@Override
	public CellAddress getAddress() {
		return cell.getAddress();
	}

	@Override
	public CellRangeAddress getArrayFormulaRange() {
		return cell.getArrayFormulaRange();
	}

	@Override
	public boolean getBooleanCellValue() {
		return cell.getBooleanCellValue();
	}

	@Deprecated
	@Override
	public int getCachedFormulaResultType() {
		return cell.getCachedFormulaResultType();
	}

	@Override
	public CellType getCachedFormulaResultTypeEnum() {
		return cell.getCachedFormulaResultTypeEnum();
	}

	@Override
	public Comment getCellComment() {
		return cell.getCellComment();
	}

	@Override
	public String getCellFormula() {
		return cell.getCellFormula();
	}

	@Override
	public CellStyle getCellStyle() {
		return cell.getCellStyle();
	}

	@Deprecated
	@Override
	public int getCellType() {
		return cell.getCellType();
	}

	@Override
	public CellType getCellTypeEnum() {
		return cell.getCellTypeEnum();
	}

	@Override
	public int getColumnIndex() {
		return cell.getColumnIndex();
	}

	@Override
	public Date getDateCellValue() {
		return cell.getDateCellValue();
	}

	@Override
	public byte getErrorCellValue() {
		return cell.getErrorCellValue();
	}

	@Override
	public Hyperlink getHyperlink() {
		return cell.getHyperlink();
	}

	@Override
	public double getNumericCellValue() {
		return cell.getNumericCellValue();
	}

	@Override
	public RichTextString getRichStringCellValue() {
		return cell.getRichStringCellValue();
	}

	@Override
	public Row getRow() {
		return cell.getRow();
	}

	@Override
	public int getRowIndex() {
		return cell.getRowIndex();
	}

	@Override
	public Sheet getSheet() {
		return cell.getSheet();
	}

	@Override
	public String getStringCellValue() {
		return cell.getStringCellValue();
	}

	@Override
	public boolean isPartOfArrayFormulaGroup() {
		return cell.isPartOfArrayFormulaGroup();
	}

	@Override
	public void removeCellComment() {
		cell.removeCellComment();
	}

	@Override
	public void removeHyperlink() {
		cell.removeHyperlink();
	}

	@Override
	public void setAsActiveCell() {
		cell.setAsActiveCell();
	}

	@Override
	public void setCellComment(Comment comment) {
		cell.setCellComment(comment);
	}

	@Override
	public void setCellErrorValue(byte value) {
		cell.setCellErrorValue(value);
	}

	@Override
	public void setCellFormula(String formula) throws FormulaParseException {
		cell.setCellFormula(formula);
	}

	@Override
	public void setCellStyle(CellStyle style) {
		CellStyleWrapper cellStyleWrapper = (CellStyleWrapper)style;
		cellStyleWrapper.refresh(workbook);
		cell.setCellStyle(cellStyleWrapper.getCellStyle());
	}

	@Deprecated
	@Override
	public void setCellType(int cellType) {
		cell.setCellType(cellType);
	}

	@Override
	public void setCellType(CellType cellType) {
		cell.setCellType(cellType);
	}

	@Override
	public void setCellValue(double value) {
		cell.setCellValue(value);
	}

	@Override
	public void setCellValue(Date value) {
		cell.setCellValue(value);
	}

	@Override
	public void setCellValue(Calendar value) {
		cell.setCellValue(value);
	}

	@Override
	public void setCellValue(RichTextString value) {
		cell.setCellValue(value);
	}

	@Override
	public void setCellValue(String value) {
		cell.setCellValue(value);
	}

	@Override
	public void setCellValue(boolean value) {
		cell.setCellValue(value);
	}

	@Override
	public void setHyperlink(Hyperlink link) {
		cell.setHyperlink(link);
	}
}
