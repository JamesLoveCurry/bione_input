/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.yusys.bione.comp.common.CommonDupontNode;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;

/**
 * @author tanxu
 *
 */
public class DupontExcelExporter extends AbstractExcelExporter {
	
	private String filePath;
	
	private CommonDupontNode node;
	
	private int col = 0;
	
	public DupontExcelExporter(String filePath, CommonDupontNode node) throws FileNotFoundException {
		this.filePath = filePath;
		this.node = node;
	}
	
	@Override
	public void run() throws BioneExporterException {
		Workbook workbook = null;
		try {
			workbook = WorkbookWrapper.create(filePath, false);
			Sheet sheet = workbook.createSheet();
			setData(workbook, sheet, node, 0, 0);
			if(node.getChildren() != null && node.getChildren().size() > 0){
				createExcel(workbook, sheet, 2, 1, node.getChildren());
			}
			for(int i=0 ; i<=col;i++){
				sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
			}
		} finally {
			IOUtils.closeQuietly(workbook);
		}
	}
	
	private void setData(Workbook workbook, Sheet sheet, CommonDupontNode node, int rowNum, int colNum){
		Row row = sheet.createRow(rowNum);
		Cell cell1 = row.createCell(colNum);
		cell1.setCellValue(node.getId());
		XSSFCellStyle xcs1 = (XSSFCellStyle)workbook.createCellStyle();
		xcs1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		xcs1.setFillForegroundColor(createColor(node.getColor(),false));
		cell1.setCellStyle(xcs1);
		Cell cell2 = row.createCell(colNum+1);
		cell2.setCellValue(node.getText());
		XSSFCellStyle xcs2 = (XSSFCellStyle)workbook.createCellStyle();
		xcs2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		xcs2.setFillForegroundColor(createColor(node.getColor(),false));
		cell2.setCellStyle(xcs2);
		if(colNum+1 > col){
			this.col = colNum +1;
		}
	}
	
	protected XSSFColor createColor(String hexString,boolean flag) {
		if (StringUtils.isEmpty(hexString))
			return null;
		XSSFColor color = null;
		if(flag){
			if (hexString.equals("#000000")){
				color = new XSSFColor(new Color(255, 255, 255));
			}else if (hexString.equals("#ffffff")||hexString.equals("#FFFFFF")){
				color = new XSSFColor(new Color(0, 0, 0));
			}else {
				color = new XSSFColor(new Color(Integer.parseInt(hexString.substring(1, 3), 16),
						Integer.parseInt(hexString.substring(3, 5), 16),
						Integer.parseInt(hexString.substring(5, 7), 16)));
			}
		}else{
			color = new XSSFColor(new Color(Integer.parseInt(hexString.substring(1, 3), 16),
					Integer.parseInt(hexString.substring(3, 5), 16),
					Integer.parseInt(hexString.substring(5, 7), 16)));
		}
		return color;
	}

	protected int createExcel(Workbook workbook, Sheet sheet, int col,int row,List<CommonDupontNode> nodes) {
		for(CommonDupontNode node : nodes){
			setData(workbook, sheet, node, row, col);
			if(node.getChildren() != null && node.getChildren().size()>0){
				row = createExcel(workbook, sheet, col+2, row+1, node.getChildren());
			}
			else{
				row ++;
			}
		}
		return row;
	}
	
	@Override
	public void destory() throws BioneExporterException{
	}
}
