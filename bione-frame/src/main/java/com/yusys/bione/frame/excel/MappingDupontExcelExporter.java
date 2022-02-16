package com.yusys.bione.frame.excel;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.common.CommonDupontNode;


public class MappingDupontExcelExporter extends AbstractExcelExporter {
	
	private static final int ROW_ACCESS_SIZE = -1;
	
	private CommonDupontNode node;
	
	private OutputStream output;
	
	private int col = 0;
	
	private SXSSFWorkbook swb = null;

	public MappingDupontExcelExporter(String path, CommonDupontNode node) throws FileNotFoundException {
		this(new File(path), node);
	}
	
	public MappingDupontExcelExporter(File file, CommonDupontNode node) throws FileNotFoundException {
		this(new FileOutputStream(file), node);
	}
	
	public MappingDupontExcelExporter(OutputStream output, CommonDupontNode node) {
		this.output = output;
		this.node = node;
	}
	
	public MappingDupontExcelExporter(HttpServletResponse response, String fileName, CommonDupontNode node) throws BioneExporterException {
		try {
			response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(StringUtils.replaceEachRepeatedly(fileName, new String[] {":", " ", "/", ";"},  new String[] {"_", "_", "_", "_"}), "UTF-8"));
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
			this.output = new BufferedOutputStream(response.getOutputStream());
			this.node = node;
		} catch (UnsupportedEncodingException e) {
			throw new BioneExporterException(e);
		} catch (IOException e) {
			throw new BioneExporterException(e);
		}
	}
	
	@Override
	public void run() throws BioneExporterException {
		try {
			swb = new SXSSFWorkbook(ROW_ACCESS_SIZE);
			SXSSFSheet ssheet = (SXSSFSheet) swb.createSheet();
			setTitleData(ssheet,node,0);
			setData(ssheet,node,1,0);
			if(node.getChildren() != null && node.getChildren().size() > 0){
				if(node.getParams().get("dupontType").equals("srcTable")
						|| node.getParams().get("dupontType").equals("targetTable")){
					createExcel(ssheet,2,2,node.getChildren());
				}else if(node.getParams().get("dupontType").equals("srcField")
						|| node.getParams().get("dupontType").equals("targetField")){
					createExcel(ssheet,3,2,node.getChildren());
				}else{
					createExcel(ssheet,0,1,node.getChildren());
				}
				
			}
			for(int i=0 ; i<=col;i++){
				ssheet.autoSizeColumn(i);
			}
			swb.write(output);
		} catch (IOException e) {
			throw new BioneExporterException(e);
		}
	}
																								
	private void setTitleData(SXSSFSheet ssheet,CommonDupontNode node,int colNum){
		Row row = ssheet.createRow(0);
		List<CommonDupontNode> nodes = new ArrayList<CommonDupontNode>();
		nodes.add(node);
		setTitleInfo(ssheet,nodes,colNum,row);
	}
	private void setTitleInfo(SXSSFSheet ssheet,List<CommonDupontNode> nodes,int colNum,Row row){
		for(CommonDupontNode node :nodes){
				if (node.getParams().get("dupontType").equals("srcTable")) {
					Cell cell1 = row.createCell(colNum);
					cell1.setCellValue("来源表系统");
					cell1.setCellStyle(CsTable());
					Cell cell2 = row.createCell(colNum + 1);
					cell2.setCellValue("来源表");
					cell2.setCellStyle(CsTable());
				}else if(node.getParams().get("dupontType").equals("targetTable")){
						Cell cell1= row.createCell(colNum);
						cell1.setCellValue("目标表系统");
						cell1.setCellStyle(CsTable());
						Cell cell2 = row.createCell(colNum + 1);
						cell2.setCellValue("目标表");
						cell2.setCellStyle(CsTable());
				}else if(node.getParams().get("dupontType").equals("srcField")){
					Cell cell1 = row.createCell(colNum);
					cell1.setCellValue("来源字段系统");
					cell1.setCellStyle(CsTable());
					Cell cell2 = row.createCell(colNum + 1);
					cell2.setCellValue("来源字段表");
					cell2.setCellStyle(CsTable());
					Cell cell3 = row.createCell(colNum + 2);
					cell3.setCellValue("来源字段");
					cell3.setCellStyle(CsTable());
				}else if(node.getParams().get("dupontType").equals("targetField")){
					Cell cell1 = row.createCell(colNum);
					cell1.setCellValue("目标字段系统");
					cell1.setCellStyle(CsTable());
					Cell cell2 = row.createCell(colNum + 1);
					cell2.setCellValue("目标字段表");
					cell2.setCellStyle(CsTable());
					Cell cell3 = row.createCell(colNum + 2);
					cell3.setCellValue("目标字段");
					cell3.setCellStyle(CsTable());
				}
			if(node.getChildren()!=null&&node.getChildren().size()>0){
				if(node.getParams().get("dupontType").equals("srcTable")||node.getParams().get("dupontType").equals("targetTable")){
					setTitleInfo(ssheet,node.getChildren(),colNum+2,row);
				}else if(node.getParams().get("dupontType").equals("srcField")||node.getParams().get("dupontType").equals("targetField")){
					setTitleInfo(ssheet,node.getChildren(),colNum+3,row);
				}else{
					setTitleInfo(ssheet,node.getChildren(),colNum,row);
				}
				
			}
		}
	}

	private void setData(SXSSFSheet ssheet, CommonDupontNode node, int rowNum,
			int colNum) {
		Row row = ssheet.createRow(rowNum);
		if (node.getParams().get("dupontType") != null) {
			if (node.getParams().get("dupontType").equals("srcTable")
					|| node.getParams().get("dupontType").equals("targetTable")) {
				String str = node.getValue();
				JSONObject json = JSONObject.parseObject(str);
				Cell cell1 = row.createCell(colNum);
				cell1.setCellValue(json.getString("systemInfo").toString());
				XSSFCellStyle xcs1 = (XSSFCellStyle) swb.createCellStyle();
				xcs1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				xcs1.setFillForegroundColor(createColor(node.getColor(), false));
				XSSFFont font = (XSSFFont) swb.createFont();
				font.setFontName("仿宋");
				xcs1.setFont(font);
				cell1.setCellStyle(xcs1);
				Cell cell2 = row.createCell(colNum + 1);
				cell2.setCellValue(json.getString("tableInfo").toString());
				XSSFCellStyle xcs2 = (XSSFCellStyle) swb.createCellStyle();
				xcs2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				xcs2.setFillForegroundColor(createColor(node.getColor(), false));
				xcs2.setFont(font);
				cell2.setCellStyle(xcs2);
				if (colNum + 1 > col) {
					this.col = colNum + 1;
				}
			} else if (node.getParams().get("dupontType").equals("srcField")
					|| node.getParams().get("dupontType").equals("targetField")) {
				String str = node.getValue();
				JSONObject json = JSONObject.parseObject(str);
				Cell cell1 = row.createCell(colNum);
				cell1.setCellValue(json.getString("systemInfo").toString());
				XSSFCellStyle xcs1 = (XSSFCellStyle) swb.createCellStyle();
				xcs1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				xcs1.setFillForegroundColor(createColor(node.getColor(), false));
				XSSFFont font = (XSSFFont) swb.createFont();
				font.setFontName("仿宋");
				xcs1.setFont(font);
				cell1.setCellStyle(xcs1);
				Cell cell2 = row.createCell(colNum + 1);
				cell2.setCellValue(json.getString("tableInfo").toString());
				XSSFCellStyle xcs2 = (XSSFCellStyle) swb.createCellStyle();
				xcs2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				xcs2.setFillForegroundColor(createColor(node.getColor(), false));
				xcs2.setFont(font);
				cell2.setCellStyle(xcs2);
				Cell cell3 = row.createCell(colNum + 2);
				cell3.setCellValue(json.getString("fieldInfo").toString());
				XSSFCellStyle xcs3 = (XSSFCellStyle) swb.createCellStyle();
				xcs3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				xcs3.setFillForegroundColor(createColor(node.getColor(), false));
				xcs3.setFont(font);
				cell3.setCellStyle(xcs3);
				if (colNum + 2 > col) {
					this.col = colNum + 2;
				}
			}
		}
	}
	
	protected XSSFColor createColor(String hexString,boolean flag) {
		if (StringUtils.isEmpty(hexString))
			return null;
		XSSFColor color = null;
		if(flag){
			if (hexString.equals("#000000"))
				color = new XSSFColor(new Color(255, 255, 255));
			else if (hexString.equals("#ffffff")||hexString.equals("#FFFFFF"))
				color = new XSSFColor(new Color(0, 0, 0));
			else {
				color = new XSSFColor(new Color(Integer.parseInt(
						StringUtils.substring(hexString, 1, 3), 16),
						Integer.parseInt(
								StringUtils.substring(hexString, 3, 5), 16),
						Integer.parseInt(
								StringUtils.substring(hexString, 5, 7), 16)));
			}
		}
		else{
			color = new XSSFColor(new Color(Integer.parseInt(
					StringUtils.substring(hexString, 1, 3), 16),
					Integer.parseInt(
							StringUtils.substring(hexString, 3, 5), 16),
					Integer.parseInt(
							StringUtils.substring(hexString, 5, 7), 16)));
		}
		
		return color;
	}

	protected int createExcel(SXSSFSheet ssheet, int col, int row,
			List<CommonDupontNode> nodes) {
		for (CommonDupontNode node : nodes) {
			setData(ssheet, node, row, col);
			if (node.getChildren() != null && node.getChildren().size() > 0) {
				if (node.getParams().get("dupontType").equals("srcTable")
						|| node.getParams().get("dupontType")
								.equals("targetTable")) {
					row = createExcel(ssheet, col + 2, row + 1,
							node.getChildren());
				} else if (node.getParams().get("dupontType")
						.equals("srcField")
						|| node.getParams().get("dupontType")
								.equals("targetField")) {
					row = createExcel(ssheet, col + 3, row + 1,
							node.getChildren());
				}
			} else {
				row++;
			}
		}
		return row;
	}
	
	@Override
	public void destory() throws BioneExporterException{
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				throw new BioneExporterException(e);
			}
		}
		if (swb != null) {
			swb.dispose();
		}
	}

	private XSSFCellStyle CsTable() {
		XSSFCellStyle xcs1 = (XSSFCellStyle) swb.createCellStyle();
		xcs1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		xcs1.setFillForegroundColor(createColor("#9BC2E6", false));
		XSSFFont font = (XSSFFont) swb.createFont();
		font.setBold(true);
		xcs1.setFont(font);
		xcs1.setVerticalAlignment(VerticalAlignment.CENTER);
		xcs1.setAlignment(HorizontalAlignment.CENTER);
		xcs1.setTopBorderColor(createColor("#000000", false));
		xcs1.setLeftBorderColor(createColor("#000000", false));
		xcs1.setRightBorderColor(createColor("#000000", false));
		xcs1.setBottomBorderColor(createColor("#000000", false));
		return xcs1;
	}
}
