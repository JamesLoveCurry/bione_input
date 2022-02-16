package com.yusys.bione.frame.util.excel;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class RowWrapper implements Row {

	private Workbook workbook;

	private Row row;

	public RowWrapper(Workbook workbook, Row row) {
		this.workbook = workbook;
		this.row = row;
	}

	@Override
	public Iterator<Cell> iterator() {
		return row.iterator();
	}

	@Override
	public Iterator<Cell> cellIterator() {
		return row.cellIterator();
	}

	@Override
	public Cell createCell(int column) {
		return new CellWrapper(workbook, row.createCell(column));
	}

	@Deprecated
	@Override
	public Cell createCell(int column, int type) {
		return new CellWrapper(workbook, this, column, type);
	}

	@Override
	public Cell createCell(int column, CellType type) {
		return new CellWrapper(workbook, this, column, type);
	}

	@Override
	public Cell getCell(int cellnum) {
		return row.getCell(cellnum);
	}

	@Override
	public Cell getCell(int cellnum, MissingCellPolicy policy) {
		return row.getCell(cellnum, policy);
	}

	@Override
	public short getFirstCellNum() {
		return row.getFirstCellNum();
	}

	@Override
	public short getHeight() {
		return row.getHeight();
	}

	@Override
	public float getHeightInPoints() {
		return row.getHeightInPoints();
	}

	@Override
	public short getLastCellNum() {
		return row.getLastCellNum();
	}

	@Override
	public int getOutlineLevel() {
		return row.getOutlineLevel();
	}

	@Override
	public int getPhysicalNumberOfCells() {
		return row.getPhysicalNumberOfCells();
	}

	@Override
	public int getRowNum() {
		return row.getRowNum();
	}

	@Override
	public CellStyle getRowStyle() {
		return row.getRowStyle();
	}

	@Override
	public Sheet getSheet() {
		return row.getSheet();
	}

	@Override
	public boolean getZeroHeight() {
		return row.getZeroHeight();
	}

	@Override
	public boolean isFormatted() {
		return row.isFormatted();
	}

	@Override
	public void removeCell(Cell cell) {
		row.removeCell(cell);
	}

	@Override
	public void setHeight(short height) {
		row.setHeight(height);
	}

	@Override
	public void setHeightInPoints(float height) {
		row.setHeightInPoints(height);
	}

	@Override
	public void setRowNum(int rowNum) {
		row.setRowNum(rowNum);
	}

	@Override
	public void setRowStyle(CellStyle style) {
		row.setRowStyle(style);
	}

	@Override
	public void setZeroHeight(boolean zHeight) {
		row.setZeroHeight(zHeight);
	}
}
