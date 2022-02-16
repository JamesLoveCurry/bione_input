package com.yusys.bione.frame.util.excel;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.AutoFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PaneInformation;

public class SheetWrapper implements Sheet {

	private WorkbookWrapper workbookWrapper;

	private Sheet sheet;

	public SheetWrapper(WorkbookWrapper workbookWrapper, Sheet sheet) {
		this.workbookWrapper = workbookWrapper;
		this.sheet = sheet;
	}

	@Override
	public Iterator<Row> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int addMergedRegion(CellRangeAddress region) {
		return sheet.addMergedRegion(region);
	}

	@Override
	public int addMergedRegionUnsafe(CellRangeAddress region) {
		return sheet.addMergedRegionUnsafe(region);
	}

	@Override
	public void addValidationData(DataValidation dataValidation) {
		sheet.addValidationData(dataValidation);
	}

	@Override
	public void autoSizeColumn(int column) {
		sheet.autoSizeColumn(column);
	}

	@Override
	public void autoSizeColumn(int column, boolean useMergedCells) {
		sheet.autoSizeColumn(column, useMergedCells);
	}

	@Override
	public Drawing<?> createDrawingPatriarch() {
		return sheet.createDrawingPatriarch();
	}

	@Override
	public void createFreezePane(int colSplit, int rowSplit) {
		sheet.createFreezePane(colSplit, rowSplit);
	}

	@Override
	public void createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
		sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
	}

	@Override
	public Row createRow(int rownum) {
		while (rownum > workbookWrapper.getEndRow()) {
			try {
				workbookWrapper.refresh();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Sheet newSheet = workbookWrapper.getWorkbook().createSheet(sheet.getSheetName());
			newSheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth());
			newSheet.setDefaultRowHeight(sheet.getDefaultRowHeight());
			for (int columnIdx = 0; columnIdx < 256; columnIdx ++) {
				newSheet.setColumnWidth(columnIdx, sheet.getColumnWidth(columnIdx));
				newSheet.setColumnHidden(columnIdx, sheet.isColumnHidden(columnIdx));
			}
			sheet = newSheet;
		}
		rownum -= workbookWrapper.getStartRow();
		return new RowWrapper(workbookWrapper.getWorkbook(), sheet.createRow(rownum));
	}

	@Override
	public void createSplitPane(int xSplitPos, int ySplitPos, int leftmostColumn, int topRow, int activePane) {
		sheet.createSplitPane(xSplitPos, ySplitPos, leftmostColumn, topRow, activePane);
	}

	@Override
	public CellAddress getActiveCell() {
		return sheet.getActiveCell();
	}

	@Override
	public boolean getAutobreaks() {
		return sheet.getAutobreaks();
	}

	@Override
	public Comment getCellComment(CellAddress ref) {
		return sheet.getCellComment(ref);
	}

	@Override
	public Map<CellAddress, ? extends Comment> getCellComments() {
		return sheet.getCellComments();
	}

	@Override
	public int[] getColumnBreaks() {
		return sheet.getColumnBreaks();
	}

	@Override
	public int getColumnOutlineLevel(int columnIndex) {
		return sheet.getColumnOutlineLevel(columnIndex);
	}

	@Override
	public CellStyle getColumnStyle(int column) {
		return sheet.getColumnStyle(column);
	}

	@Override
	public int getColumnWidth(int columnIndex) {
		return sheet.getColumnWidth(columnIndex);
	}

	@Override
	public float getColumnWidthInPixels(int columnIndex) {
		return sheet.getColumnWidthInPixels(columnIndex);
	}

	@Override
	public DataValidationHelper getDataValidationHelper() {
		return sheet.getDataValidationHelper();
	}

	@Override
	public List<? extends DataValidation> getDataValidations() {
		return sheet.getDataValidations();
	}

	@Override
	public int getDefaultColumnWidth() {
		return sheet.getDefaultColumnWidth();
	}

	@Override
	public short getDefaultRowHeight() {
		return sheet.getDefaultRowHeight();
	}

	@Override
	public float getDefaultRowHeightInPoints() {
		return sheet.getDefaultRowHeightInPoints();
	}

	@Override
	public boolean getDisplayGuts() {
		return sheet.getDisplayGuts();
	}

	@Override
	public Drawing<?> getDrawingPatriarch() {
		return sheet.getDrawingPatriarch();
	}

	@Override
	public int getFirstRowNum() {
		return sheet.getFirstRowNum();
	}

	@Override
	public boolean getFitToPage() {
		return sheet.getFitToPage();
	}

	@Override
	public Footer getFooter() {
		return sheet.getFooter();
	}

	@Override
	public boolean getForceFormulaRecalculation() {
		return sheet.getForceFormulaRecalculation();
	}

	@Override
	public Header getHeader() {
		return sheet.getHeader();
	}

	@Override
	public boolean getHorizontallyCenter() {
		return sheet.getHorizontallyCenter();
	}

	@Override
	public Hyperlink getHyperlink(CellAddress addr) {
		return sheet.getHyperlink(addr);
	}

	@Override
	public Hyperlink getHyperlink(int row, int column) {
		return sheet.getHyperlink(row, column);
	}

	@Override
	public List<? extends Hyperlink> getHyperlinkList() {
		return sheet.getHyperlinkList();
	}

	@Override
	public int getLastRowNum() {
		throw new UnsupportedOperationException();
	}

	@Override
	public short getLeftCol() {
		return sheet.getLeftCol();
	}

	@Override
	public double getMargin(short margin) {
		return sheet.getMargin(margin);
	}

	@Override
	public CellRangeAddress getMergedRegion(int index) {
		return sheet.getMergedRegion(index);
	}

	@Override
	public List<CellRangeAddress> getMergedRegions() {
		return sheet.getMergedRegions();
	}

	@Override
	public int getNumMergedRegions() {
		return sheet.getNumMergedRegions();
	}

	@Override
	public PaneInformation getPaneInformation() {
		return sheet.getPaneInformation();
	}

	@Override
	public int getPhysicalNumberOfRows() {
		return sheet.getPhysicalNumberOfRows();
	}

	@Override
	public PrintSetup getPrintSetup() {
		return sheet.getPrintSetup();
	}

	@Override
	public boolean getProtect() {
		return sheet.getProtect();
	}

	@Override
	public CellRangeAddress getRepeatingColumns() {
		return sheet.getRepeatingColumns();
	}

	@Override
	public CellRangeAddress getRepeatingRows() {
		return sheet.getRepeatingRows();
	}

	@Override
	public Row getRow(int rownum) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] getRowBreaks() {
		return sheet.getRowBreaks();
	}

	@Override
	public boolean getRowSumsBelow() {
		return sheet.getRowSumsBelow();
	}

	@Override
	public boolean getRowSumsRight() {
		return sheet.getRowSumsRight();
	}

	@Override
	public boolean getScenarioProtect() {
		return sheet.getScenarioProtect();
	}

	@Override
	public SheetConditionalFormatting getSheetConditionalFormatting() {
		return sheet.getSheetConditionalFormatting();
	}

	@Override
	public String getSheetName() {
		return sheet.getSheetName();
	}

	@Override
	public short getTopRow() {
		return sheet.getTopRow();
	}

	@Override
	public boolean getVerticallyCenter() {
		return sheet.getVerticallyCenter();
	}

	@Override
	public Workbook getWorkbook() {
		return workbookWrapper;
	}

	@Override
	public void groupColumn(int fromColumn, int toColumn) {
		sheet.groupColumn(fromColumn, toColumn);
	}

	@Override
	public void groupRow(int fromRow, int toRow) {
		sheet.groupRow(fromRow, toRow);
	}

	@Override
	public boolean isColumnBroken(int column) {
		return sheet.isColumnBroken(column);
	}

	@Override
	public boolean isColumnHidden(int columnIndex) {
		return sheet.isColumnHidden(columnIndex);
	}

	@Override
	public boolean isDisplayFormulas() {
		return sheet.isDisplayFormulas();
	}

	@Override
	public boolean isDisplayGridlines() {
		return sheet.isDisplayGridlines();
	}

	@Override
	public boolean isDisplayRowColHeadings() {
		return sheet.isDisplayRowColHeadings();
	}

	@Override
	public boolean isDisplayZeros() {
		return sheet.isDisplayZeros();
	}

	@Override
	public boolean isPrintGridlines() {
		return sheet.isPrintGridlines();
	}

	@Override
	public boolean isPrintRowAndColumnHeadings() {
		return sheet.isPrintRowAndColumnHeadings();
	}

	@Override
	public boolean isRightToLeft() {
		return sheet.isRightToLeft();
	}

	@Override
	public boolean isRowBroken(int row) {
		return sheet.isRowBroken(row);
	}

	@Override
	public boolean isSelected() {
		return sheet.isSelected();
	}

	@Override
	public void protectSheet(String password) {
		sheet.protectSheet(password);
	}

	@Override
	public CellRange<? extends Cell> removeArrayFormula(Cell cell) {
		return sheet.removeArrayFormula(cell);
	}

	@Override
	public void removeColumnBreak(int column) {
		sheet.removeColumnBreak(column);
	}

	@Override
	public void removeMergedRegion(int index) {
		sheet.removeMergedRegion(index);
	}

	@Override
	public void removeMergedRegions(Collection<Integer> indices) {
		sheet.removeMergedRegions(indices);
	}

	@Override
	public void removeRow(Row row) {
		sheet.removeRow(row);
	}

	@Override
	public void removeRowBreak(int row) {
		sheet.removeRowBreak(row);
	}

	@Override
	public Iterator<Row> rowIterator() {
		return sheet.rowIterator();
	}

	@Override
	public void setActiveCell(CellAddress address) {
		sheet.setActiveCell(address);
	}

	@Override
	public CellRange<? extends Cell> setArrayFormula(String formula, CellRangeAddress range) {
		return sheet.setArrayFormula(formula, range);
	}

	@Override
	public AutoFilter setAutoFilter(CellRangeAddress range) {
		return sheet.setAutoFilter(range);
	}

	@Override
	public void setAutobreaks(boolean value) {
		sheet.setAutobreaks(value);
	}

	@Override
	public void setColumnBreak(int column) {
		sheet.setColumnBreak(column);
	}

	@Override
	public void setColumnGroupCollapsed(int columnNumber, boolean collapsed) {
		sheet.setColumnGroupCollapsed(columnNumber, collapsed);
	}

	@Override
	public void setColumnHidden(int columnIndex, boolean hidden) {
		sheet.setColumnHidden(columnIndex, hidden);
	}

	@Override
	public void setColumnWidth(int columnIndex, int width) {
		sheet.setColumnWidth(columnIndex, width);
	}

	@Override
	public void setDefaultColumnStyle(int column, CellStyle style) {
		sheet.setDefaultColumnStyle(column, style);
	}

	@Override
	public void setDefaultColumnWidth(int width) {
		sheet.setDefaultColumnWidth(width);
	}

	@Override
	public void setDefaultRowHeight(short height) {
		sheet.setDefaultRowHeight(height);
	}

	@Override
	public void setDefaultRowHeightInPoints(float height) {
		sheet.setDefaultRowHeightInPoints(height);
	}

	@Override
	public void setDisplayFormulas(boolean show) {
		sheet.setDisplayFormulas(show);
	}

	@Override
	public void setDisplayGridlines(boolean show) {
		sheet.setDisplayGridlines(show);
	}

	@Override
	public void setDisplayGuts(boolean value) {
		sheet.setDisplayGuts(value);
	}

	@Override
	public void setDisplayRowColHeadings(boolean show) {
		sheet.setDisplayRowColHeadings(show);
	}

	@Override
	public void setDisplayZeros(boolean value) {
		sheet.setDisplayZeros(value);
	}

	@Override
	public void setFitToPage(boolean value) {
		sheet.setFitToPage(value);
	}

	@Override
	public void setForceFormulaRecalculation(boolean value) {
		sheet.setForceFormulaRecalculation(value);
	}

	@Override
	public void setHorizontallyCenter(boolean value) {
		sheet.setHorizontallyCenter(value);
	}

	@Override
	public void setMargin(short margin, double size) {
		sheet.setMargin(margin, size);
	}

	@Override
	public void setPrintGridlines(boolean show) {
		sheet.setPrintGridlines(show);
	}

	@Override
	public void setPrintRowAndColumnHeadings(boolean show) {
		sheet.setPrintRowAndColumnHeadings(show);
	}

	@Override
	public void setRepeatingColumns(CellRangeAddress columnRangeRef) {
		sheet.setRepeatingColumns(columnRangeRef);
	}

	@Override
	public void setRepeatingRows(CellRangeAddress rowRangeRef) {
		sheet.setRepeatingRows(rowRangeRef);
	}

	@Override
	public void setRightToLeft(boolean value) {
		sheet.setRightToLeft(value);
	}

	@Override
	public void setRowBreak(int row) {
		sheet.setRowBreak(row);
	}

	@Override
	public void setRowGroupCollapsed(int row, boolean collapse) {
		sheet.setRowGroupCollapsed(row, collapse);
	}

	@Override
	public void setRowSumsBelow(boolean value) {
		sheet.setRowSumsBelow(value);
	}

	@Override
	public void setRowSumsRight(boolean value) {
		sheet.setRowSumsRight(value);
	}

	@Override
	public void setSelected(boolean value) {
		sheet.setSelected(value);
	}

	@Override
	public void setVerticallyCenter(boolean value) {
		sheet.setVerticallyCenter(value);
	}

	@Override
	public void setZoom(int scale) {
		sheet.setZoom(scale);
	}

	@Override
	public void shiftRows(int startRow, int endRow, int n) {
		sheet.shiftRows(startRow, endRow, n);
	}

	@Override
	public void shiftRows(int startRow, int endRow, int n, boolean copyRowHeight, boolean resetOriginalRowHeight) {
		sheet.shiftRows(startRow, endRow, n, copyRowHeight, resetOriginalRowHeight);
	}

	@Override
	public void showInPane(int toprow, int leftcol) {
		sheet.showInPane(toprow, leftcol);
	}

	@Override
	public void ungroupColumn(int fromColumn, int toColumn) {
		sheet.ungroupColumn(fromColumn, toColumn);
	}

	@Override
	public void ungroupRow(int fromRow, int toRow) {
		sheet.ungroupRow(fromRow, toRow);
	}

	@Override
	public void validateMergedRegions() {
		sheet.validateMergedRegions();
	}
}
