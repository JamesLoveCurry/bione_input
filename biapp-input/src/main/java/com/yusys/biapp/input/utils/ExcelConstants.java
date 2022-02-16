package com.yusys.biapp.input.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@Component(value = "excelConstants")
public class ExcelConstants {
	private final Log log = LogFactory.getLog(ExcelConstants.class);
	private static String DATA_TYPE = "0：新增，1：更新，2：删除";

	// 根据列生成excel工作对象,只用于模版的excel生成
	@SuppressWarnings("resource")
	public HSSFWorkbook getWorkBook(List<String> listColumns, List<String> listDetail,List<String> colorList,String tmpId) {
		/** EXCEL 工作区对象 */
		HSSFWorkbook workBook = new HSSFWorkbook();
		/** 工作表 (支持中文) */
		HSSFSheet sheetOne = workBook.createSheet("sheet1");
		workBook = createTalbeHead(workBook, sheetOne, listColumns, listDetail,colorList, tmpId);
		return workBook;
	}

	// 根据列生成excel工作对象,生成数据字典的excel模版
	public HSSFWorkbook getWorkBookForLib(List<com.yusys.bione.comp.common.CommonTreeNode> listColumns) {
		/** EXCEL 工作区对象 */
		HSSFWorkbook workBook = new HSSFWorkbook();
		/** 工作表 (支持中文) */
		HSSFSheet sheet = workBook.createSheet("sheet1");
		/** 为了工作表能支持中文 */
		workBook.setSheetName(0, "数据字典内容");
		sheet.setDefaultColumnWidth(25);
		/** 产生一行 */
		HSSFRow row = sheet.createRow( 0); // 设置动态标题,字体加粗
		/** 产生一个单元格 */
		HSSFCell cell = row.createCell( 0);
		/** 设置单元格内容为字符串型 */
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue((String) listColumns.get(0).getText());
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		HSSFFont font = workBook.createFont();
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//		font.setBold(true);
		font.setColor(HSSFColor.BLACK.index);
		cellStyle.setFont(font);
//		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		cellStyle.setLocked(true);
		cellStyle.setHidden(false);
		/** 自动换行 (否则会出现混乱挤占问题) */
		cellStyle.setWrapText(true);
		cell.setCellStyle(cellStyle);

		HSSFCell cell1 = row.createCell( 1);
		cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell1.setCellValue((String) listColumns.get(0).getId());
		cell1.setCellStyle(cellStyle);

		if (listColumns.get(0).getUpId() == null || "".equals(listColumns.get(0).getUpId())) { // 如果没有上级节点
			HSSFCellStyle cellStyleContent = workBook.createCellStyle();
			HSSFFont font1 = workBook.createFont();
			cellStyleContent.setFont(font1);
			cellStyleContent.setWrapText(true);
//			cellStyle.setAlignment(HorizontalAlignment.LEFT);// 内容靠左
			cellStyleContent.setHidden(false);
			for (int i = 1; i < listColumns.size(); i++) {// 设置内容,字体不加粗
				HSSFRow rows = sheet.createRow( i);
				HSSFCell cells = rows.createCell( 0);
				cells.setCellType(HSSFCell.CELL_TYPE_STRING);
				cells.setCellValue((String) listColumns.get(i).getText());
				cells.setCellStyle(cellStyleContent);

				HSSFCell cells1 = rows.createCell( 1);
				cells1.setCellType(HSSFCell.CELL_TYPE_STRING);
				cells1.setCellValue((String) listColumns.get(i).getId());
				cells1.setCellStyle(cellStyleContent);
			}
		} else {
			HSSFCell cell2 = row.createCell( 2);
			cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell2.setCellValue((String) listColumns.get(0).getUpId());
			cell2.setCellStyle(cellStyle);
			HSSFCellStyle cellStyleContent = workBook.createCellStyle();
			HSSFFont font1 = workBook.createFont();
			cellStyleContent.setFont(font1);
			cellStyleContent.setWrapText(true);
			sheet.setDefaultColumnWidth(35);
//			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			cellStyleContent.setHidden(false);
			for (int i = 1; i < listColumns.size(); i++) {// 设置内容,字体不加粗
				HSSFRow rows = sheet.createRow( i);
				HSSFCell cells = rows.createCell( 0);
				cells.setCellType(HSSFCell.CELL_TYPE_STRING);
				cells.setCellValue((String) listColumns.get(i).getText());
				cells.setCellStyle(cellStyleContent);

				HSSFCell cells1 = rows.createCell( 1);
				cells1.setCellType(HSSFCell.CELL_TYPE_STRING);
				cells1.setCellValue((String) listColumns.get(i).getId());
				cells1.setCellStyle(cellStyleContent);

				HSSFCell cells2 = rows.createCell( 2);
				cells2.setCellType(HSSFCell.CELL_TYPE_STRING);
				cells2.setCellValue((String) listColumns.get(i).getUpId());
				cells2.setCellStyle(cellStyleContent);
			}
		}
		return workBook;
	}

	// 根据列生成excel工作对象,生成日志管理的excel模版
//	public HSSFWorkbook getWorkBookForLog(List<BioneLogInfoVO> listColumns) {
//		/** EXCEL 工作区对象 */
//		HSSFWorkbook workBook = new HSSFWorkbook();
//		/** 工作表 (支持中文) */
//		HSSFSheet sheet = workBook.createSheet("sheet1");
//		/** 为了工作表能支持中文 */
//		workBook.setSheetName(0, "日志管理内容");
//		HSSFCellStyle cellStyle = workBook.createCellStyle();
//		HSSFFont font = workBook.createFont();
//		font.setColor(HSSFColor.BLACK.index);
//		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
//		cellStyle.setLocked(true);
//		cellStyle.setHidden(false);
//		/** 自动换行 (否则会出现混乱挤占问题) */
//		cellStyle.setWrapText(true);
//
//		if (listColumns != null && listColumns.size() > 0) {
//			for (int i = 0; i < listColumns.size(); i++) {// 设置内容,字体不加粗
//				HSSFRow row = sheet.createRow( i);
//				if (i == 0) {
//					font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//				} else {
//					font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
//				}
//				cellStyle.setFont(font);
//				HSSFCell cell = row.createCell( 0);
//				if (listColumns.size() > 1) {
//					String logicSysNo = listColumns.get(1).getLogicSysNo().trim();
//					sheet.setColumnWidth(0, logicSysNo.getBytes().length * 200);
//				}
//				/** 设置单元格内容为字符串型 */
//				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell.setCellValue(listColumns.get(i).getLogicSysNo());
//				cell.setCellStyle(cellStyle);
//
//				HSSFCell cell1 = row.createCell( 1);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(1, (listColumns.get(1).getOperUser() + "    ").getBytes().length * 256);
//				}
//				cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell1.setCellValue(listColumns.get(i).getOperUser());
//				cell1.setCellStyle(cellStyle);
//
//				HSSFCell cell2 = row.createCell( 2);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(2, (listColumns.get(1).getLoginIP() + "    ").getBytes().length * 256);
//				}
//				cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell2.setCellValue(listColumns.get(i).getLoginIP());
//				cell2.setCellStyle(cellStyle);
//
//				HSSFCell cell3 = row.createCell( 3);
//				sheet.setColumnWidth(3, 15000);
//				cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell3.setCellValue(listColumns.get(i).getLogEvent());
//				cell3.setCellStyle(cellStyle);
//
//				HSSFCell cell4 = row.createCell( 4);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(4, (listColumns.get(1).getOccurTime()).getBytes().length * 256);
//				}
//				cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell4.setCellValue(listColumns.get(i).getOccurTime());
//				cell4.setCellStyle(cellStyle);
//			}
//		}
//
//		return workBook;
//	}

//	/**
//	 * 生成任务统计模板
//	 * @param listColumns
//	 * @return
//	 */
//	public HSSFWorkbook getWorkBookForTaskCount(List<UdipTaskCountVO> listColumns) {
//		/** EXCEL 工作区对象 */
//		HSSFWorkbook workBook = new HSSFWorkbook();
//		/** 工作表 (支持中文) */
//		HSSFSheet sheet = workBook.createSheet("sheet1");
//		/** 为了工作表能支持中文 */
//		workBook.setSheetName(0, "补录任务统计");
//		HSSFCellStyle cellStyle = workBook.createCellStyle();
//		HSSFFont font = workBook.createFont();
//		font.setColor(HSSFColor.BLACK.index);
//		cellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
//		cellStyle.setLocked(true);
//		cellStyle.setHidden(false);
//		/** 自动换行 (否则会出现混乱挤占问题) */
//		cellStyle.setWrapText(true);
//
//		if (listColumns != null && listColumns.size() > 0) {
//			for (int i = 0; i < listColumns.size(); i++) {// 设置内容,字体不加粗
//				HSSFRow row = sheet.createRow( i);
//				if (i == 0) {
//					font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//				} else {
//					font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
//				}
//				cellStyle.setFont(font);
//				HSSFCell cell = row.createCell(0);
//				if (listColumns.size() > 1) {
//					String taskName = listColumns.get(1).getTaskName().trim();
//					sheet.setColumnWidth(0, taskName.getBytes().length * 256 * 2);
//				}
//				/** 设置单元格内容为字符串型 */
//				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell.setCellValue(listColumns.get(i).getTaskName());
//				cell.setCellStyle(cellStyle);
//
//				HSSFCell cell2 = row.createCell(1);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(1, (listColumns.get(1).getCreateDate() + "		").getBytes().length * 256);
//				}
//				cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell2.setCellValue(listColumns.get(i).getCreateDate());
//				cell2.setCellStyle(cellStyle);
//
//				HSSFCell cell3 = row.createCell(2);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(2, (listColumns.get(1).getDispatchs() + "		").getBytes().length * 256 * 3);
//				}
//				cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell3.setCellValue(listColumns.get(i).getDispatchs());
//				cell3.setCellStyle(cellStyle);
//
//				HSSFCell cell4 = row.createCell(3);
//				sheet.setColumnWidth(4, (listColumns.get(1).getSaves() + "		").getBytes().length * 256 * 4);
//				cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell4.setCellValue(listColumns.get(i).getSaves());
//				cell4.setCellStyle(cellStyle);
//
//				HSSFCell cell5 = row.createCell(4);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(4, (listColumns.get(1).getSumbits() + "		").getBytes().length * 256 * 4);
//				}
//				cell5.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell5.setCellValue(listColumns.get(i).getSumbits());
//				cell5.setCellStyle(cellStyle);
//
//				HSSFCell cell6 = row.createCell(5);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(5, (listColumns.get(1).getSuccess() + "		").getBytes().length * 256 * 4);
//				}
//				cell6.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell6.setCellValue(listColumns.get(i).getSuccess());
//				cell6.setCellStyle(cellStyle);
//
//				HSSFCell cell7 = row.createCell(6);
//				if (listColumns.size() > 1) {
//					sheet.setColumnWidth(6, (listColumns.get(1).getRefuses() + "		  ").getBytes().length * 256 * 3);
//				}
//				cell7.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell7.setCellValue(listColumns.get(i).getRefuses());
//				cell7.setCellStyle(cellStyle);
//			}
//		}
//
//		return workBook;
//	}

	 /**
	  * 设置单元格
	  * @param workBook		excel工作对象
	  * @param cellStyle	单元格格式
	  * @param i			位于每行的第几列
	  * @param row0			表格的那一行
	  * @param concent		单元格的值
	  * @param rowchanger	列的长度
	  * @param sheet		工作表
	  * @param longNum		长度倍数
	  */
	private void createCell(HSSFWorkbook workBook,HSSFCellStyle cellStyle ,int i, HSSFRow row0, String concent, Boolean rowchanger,
			HSSFSheet sheet,int longNum) {
		if (rowchanger) {
			sheet.setDefaultColumnStyle(i, cellStyle);
			sheet.setColumnWidth(i, (concent.getBytes().length * 256 + 100)*longNum);
		}
		
		HSSFCell cell = row0.createCell( i);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(concent);
		cell.setCellStyle(cellStyle);
		

	}
	/**
	 * 设置单元格格式
	 * @param workBook
	 * @param font
	 * @return
	 */
	public HSSFCellStyle getCellStyle(HSSFWorkbook workBook, HSSFFont font){
		HSSFDataFormat format = workBook.createDataFormat();
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setDataFormat(format.getFormat("@"));
//		cellStyle.setAlignment(HorizontalAlignment.GENERAL);
		cellStyle.setFont(font);
		cellStyle.setLocked(false);
		cellStyle.setHidden(false);
		/** 自动换行 (否则会出现混乱挤占问题) */
		cellStyle.setWrapText(true);
		
		return cellStyle;
	}
	/**
	 * 设置单元格字体
	 * @param workBook
	 * @param bold
	 * @param color
	 * @return
	 */
	public HSSFFont getFont(HSSFWorkbook workBook,Boolean bold, String color){
		HSSFFont font = workBook.createFont();
		if(bold){
//		    font.setBold(true);
//			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		}
		if (color.equals(UdipConstants.HSSF_COLOR_BLUE)) {
			font.setColor(HSSFColor.BLUE.index);
		} else if(color.equals(UdipConstants.HSSF_COLOR_BLACK)){
			font.setColor(HSSFColor.BLACK.index);
		} else if(color.equals(UdipConstants.HSSF_COLOR_RED)){
			font.setColor(HSSFColor.RED.index);
		} else if(color.equals(UdipConstants.HSSF_COLOR_BROWN)){
			font.setColor(HSSFColor.BROWN.index);
		} else if(color.equals(UdipConstants.HSSF_COLOR_DARK_RED)){
			font.setColor(HSSFColor.DARK_RED.index);
		} else if(color.equals(UdipConstants.HSSF_COLOR_DARK_GREEN)){
			font.setColor(HSSFColor.DARK_GREEN.index);
		}
		
		return font;
	}
	/**
	 * 根据excel文件返回文件的list
	 * @param file
	 * @return
	 */
	public Map<String, Object> getList(File file, Long orderNo) {
		Map<String, Object> mapList = Maps.newHashMap();

		List<Map<String, Object>> addList = Lists.newArrayList();
		List<Map<String, Object>> udpList = Lists.newArrayList();
		List<Map<String, Object>> delList = Lists.newArrayList();
		FileInputStream is = null;
		HSSFWorkbook wb = null;
		try {
		    is = new FileInputStream(file);
			wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getLastRowNum();
			// 获取列数
			HSSFRow row = sheet.getRow(1);
			List<String> cellSize = Lists.newArrayList();
			if (row == null) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("typeWrrong", "读取文件出错，请检查模板是否正确。");
				return map;
			}
			
			int colIndex = 0;
			while(row.getCell(colIndex) != null){
                String cell = row.getCell(colIndex++).getStringCellValue();
                String cell1 = cell.split("#")[0];
                if (cell.indexOf("操作类型") == -1) {
                    cellSize.add(cell1);
                }
            
			}
			
			int k = 0 ;
			// 循环获取每行数据
			for (int i = 2; i <= rows; i++) {
				k = 0;
				row = sheet.getRow(i);
				if (null == row)
					continue;
				
				Map<String, Object> map = Maps.newHashMap();
				for (int j = 0; j < cellSize.size(); j++) {
					if (getCellValue(row.getCell(j)) != null) {
						map.put(cellSize.get(j), (getCellValue(row.getCell(j)).toString().replaceAll("", "")));
					} else {
						map.put(cellSize.get(j), "");
					}
					if (null == getCellValue(row.getCell(j)) || "".equals(getCellValue(row.getCell(j)))) {
						k = k + 1;
					}
				}
				//放开代码，保存补录表中最大的sys_order_no+1 20190819
				map.put(UdipConstants.TAB_ORDER_NO, ++orderNo);
				if (k == cellSize.size()) {
					break;
				}
				Object oper = getCellValue(row.getCell(cellSize.size()));
				if(oper != null){
				    String o = String.valueOf(oper);
				    if("0".equals(o)){
				        addList.add(map);
				    }else if("1".equals(o)){
				        udpList.add(map);
				    }else if("2".equals(o)){
				        delList.add(map);
                    }
				}
			}
			mapList.put("addList", addList);
			mapList.put("udpList", udpList);
			mapList.put("delList", delList);
			return mapList;
		} catch (FileNotFoundException e) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("file", "目标文件数据不存在，可能已上传，可导入同名文件进行更新。");
			return map;
		} catch (IOException e) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("wrrong", "读取文件出错。");
			return map;
		}finally{
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(wb);
		}
	}

	// 获取单元格内容
	private Object getCellValue(HSSFCell cell) {
		Object cellValue = null;
		if (cell != null) {
			int cellType = cell.getCellType();
			switch (cellType) {
			case HSSFCell.CELL_TYPE_NUMERIC: // 数值型
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是date类型则 ，获取该cell的date值
					cellValue = DateUtils.getDateFormatTime(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()), "yyyy-MM-dd");
				} else {// 纯数字
					Double value = cell.getNumericCellValue();
					DecimalFormat dec = new DecimalFormat("0.##############");
					cellValue = dec.format(value);
				}
				break;
			/* 此行表示单元格的内容为string类型 */
			case HSSFCell.CELL_TYPE_STRING: // 字符串型
				cellValue = cell.getStringCellValue().trim();
				break;
			case HSSFCell.CELL_TYPE_FORMULA:// 公式型
				// 读公式计算值
				try {
					cellValue = String.valueOf(cell.getNumericCellValue()).trim();
				} catch (IllegalStateException e) {
					return null;
				}
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:// 布尔
				cellValue = "" + cell.getBooleanCellValue();
				break;
			/* 此行表示该单元格值为空 */
			case HSSFCell.CELL_TYPE_BLANK: // 空值
				cellValue = "";
				break;
			case HSSFCell.CELL_TYPE_ERROR: // 故障
				cellValue = "";
				break;
			default:
				cellValue = cell.getStringCellValue().toString().trim();
			}
			return cellValue;
		} else {
			return null;
		}

	}

	/**
	 * 根据补录数据生成excel
	 * @param dataList
	 * @param colName
	 * @return
	 */
	@SuppressWarnings("resource")
	public HSSFWorkbook getWorkBookForListMap(List<Map<String, Object>> dataList, List<String> colName,
			List<String> colNameCN, List<String> colNameDetail,List<String> keyCols) {
		/** EXCEL 工作区对象 */
		HSSFWorkbook workBook = new HSSFWorkbook();
		/** 工作表 (支持中文) */
		HSSFSheet sheetOne = workBook.createSheet("sheet1");
		List<String> colorList = Lists.newArrayList();
		String concent = "";
		/** 生成表头 */
		List<String> listColumns = Lists.newArrayList();
		for (int i = 0; i < colName.size(); i++) {
			if (StringUtils.isNotBlank(colNameCN.get(i))) {
				listColumns.add(colName.get(i) + "#" + colNameCN.get(i));
			} else {
				listColumns.add(colName.get(i));
			}
			if(keyCols.contains(colName.get(i))){
				colorList.add(UdipConstants.HSSF_COLOR_RED);
			}else{
				colorList.add(UdipConstants.HSSF_COLOR_BLACK);
			}
		}
		
		workBook = createTalbeHead(workBook, sheetOne, listColumns, colNameDetail,colorList);

		HSSFFont font = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyle = getCellStyle(workBook, font);
		
		for (int i = 0; i < dataList.size(); i++) {
			HSSFRow row = sheetOne.createRow( i + 3);
			for (int j = 0; j < colName.size(); j++) {
				if (null == dataList.get(i).get(colName.get(j))) {
					concent = "";
				} else {
					concent = dataList.get(i).get(colName.get(j)).toString();
				}
				createCell(workBook, cellStyle, j, row, concent, false, sheetOne, 1);
//				createCell(workBook,cellStyle,format,font, j, row, concent, false, sheetOne, UdipConstants.HSSF_COLOR_BLACK,false,1);
			}
		}

		return workBook;
	}
	
	@SuppressWarnings("resource")
	public HSSFWorkbook getWorkBookForListMap(List<Map<String, Object>> dataList, List<String> colName,
			List<String> colNameCN, List<String> colNameDetail,List<String> keyCols, String title, String title2) {
		/** EXCEL 工作区对象 */
		HSSFWorkbook workBook = new HSSFWorkbook();
		/** 工作表 (支持中文) */
		HSSFSheet sheetOne = workBook.createSheet("sheet1");
		List<String> colorList = Lists.newArrayList();
		String concent = "";
		/** 生成表头 */
		List<String> listColumns = Lists.newArrayList();
		for (int i = 0; i < colName.size(); i++) {
			if (!UdipConstants.TAB_ORDER_NO.equals(colName.get(i) + "")) {
				if (StringUtils.isNotBlank(colNameCN.get(i))) {
					listColumns.add(colName.get(i) + "#" + colNameCN.get(i));
				} else {
					listColumns.add(colName.get(i));
				}
				if (keyCols.contains(colName.get(i))) {
					colorList.add(UdipConstants.HSSF_COLOR_RED);
				} else {
					colorList.add(UdipConstants.HSSF_COLOR_BLACK);
				}
			}
		}
		
		workBook = createTalbeHead(workBook, sheetOne, listColumns, colNameDetail,colorList, title, title2);

		HSSFFont font = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyle = getCellStyle(workBook, font);
		
		for (int i = 0; i < dataList.size(); i++) {
			HSSFRow row = sheetOne.createRow( i + 4);
			for (int j = 0; j < colName.size(); j++) {
				if (null == dataList.get(i).get(colName.get(j))) {
					concent = "";
				} else {
					concent = dataList.get(i).get(colName.get(j)).toString();
				}
				createCell(workBook, cellStyle, j, row, concent, false, sheetOne, 1);
//				createCell(workBook,cellStyle,format,font, j, row, concent, false, sheetOne, UdipConstants.HSSF_COLOR_BLACK,false,1);
			}
		}

		return workBook;
	}

	/**
	 * 根据补录数据预览生成excel
	 * @param dataList
	 * @param colName
	 * @return
	 */
	@SuppressWarnings("resource")
	public HSSFWorkbook getWorkBookForListMap(List<Map<String, Object>> dataList, List<String> colName,
			List<String> colNameCN) {
		/** EXCEL 工作区对象 */
		HSSFWorkbook workBook = new HSSFWorkbook();
		/** 工作表 (支持中文) */
		HSSFSheet sheetOne = workBook.createSheet("sheet1");

		String concent = "";
		/** 生成表头 */
		List<String> listColumns = Lists.newArrayList();
		for (int i = 0; i < colName.size(); i++) {
			if (colNameCN!=null && colNameCN.size()>0 && StringUtils.isNotBlank(colNameCN.get(i))) {
				listColumns.add(colName.get(i) + "#" + colNameCN.get(i));
			} else {
				listColumns.add(colName.get(i));
			}
		}
		workBook = createTalbeHead(workBook, sheetOne, listColumns);
		HSSFFont font = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyle = getCellStyle(workBook, font);
		for (int i = 0; i < dataList.size(); i++) {
			HSSFRow row = sheetOne.createRow( i + 1);
			for (int j = 0; j < colName.size(); j++) {
				if (null == dataList.get(i).get(colName.get(j).trim())) {
					concent = "";
				} else {
					concent = dataList.get(i).get(colName.get(j).trim()).toString();
				}
				createCell(workBook, cellStyle, j, row, concent, false, sheetOne, 1);
			}
		}

		return workBook;
	}

	/**
	 * 生成模板固定表头
	 * @param workBook
	 * @param sheetOne
	 * @param listColumns
	 * @param listDetail
	 * @return
	 */
	public HSSFWorkbook createTalbeHead(HSSFWorkbook workBook, HSSFSheet sheetOne, List<String> listColumns,
			List<String> listDetail,List<String> colorList) {
        
		/** 行 */
		HSSFRow row0 = sheetOne.createRow( 0);
		HSSFRow row1 = sheetOne.createRow( 1);
		sheetOne.addMergedRegion(new CellRangeAddress(0,(short)0,0,(short)12));
		
		HSSFFont fontBrown = getFont(workBook, true, UdipConstants.HSSF_COLOR_BROWN);
		HSSFCellStyle cellStyleBrown = getCellStyle(workBook, fontBrown);
		createCell(workBook, cellStyleBrown, 0, row0, "说明：1、请在每列的数据后注明操作类型；2、数据请从第3行开始填写；3、日期类型如未说明格式，请参照yyyyMMdd格式", false, sheetOne, 1);
		
		
		HSSFFont fontRedB = getFont(workBook, true, UdipConstants.HSSF_COLOR_RED);
		HSSFCellStyle cellStyleRedB = getCellStyle(workBook, fontRedB );
		HSSFFont fontBlack = getFont(workBook, true, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyleBlack = getCellStyle(workBook, fontBlack );
		
		/** 生成表头 */
		for (int i = 0; i < listColumns.size(); i++) {
			createCell(workBook, colorList.get(i).equals(UdipConstants.HSSF_COLOR_RED)?cellStyleRedB:cellStyleBlack, i, row1, (String) listColumns.get(i), true, sheetOne, 1);
		}
		createCell(workBook,cellStyleBlack, listColumns.size(), row1, "操作类型", true, sheetOne, 1);
		HSSFRow row2 = sheetOne.createRow( 2);

		/** 生成表头 */
		HSSFFont fontRed = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLUE);
		HSSFCellStyle cellStyleRed = getCellStyle(workBook, fontRed );
		for (int i = 0; i < listDetail.size(); i++) {
			createCell(workBook,cellStyleRed, i, row2, StringUtils.isNotBlank((String) listDetail.get(i))?(String) listDetail.get(i):"空", false, sheetOne,1);
		}
		createCell(workBook,cellStyleRed, listColumns.size(), row2, DATA_TYPE, false, sheetOne,1);
		return workBook;
	}
	
	/**
     * 生成模板固定表头
     * @param workBook
     * @param sheetOne
     * @param listColumns
     * @param listDetail
     * @return
     */
    public HSSFWorkbook createTalbeHead(HSSFWorkbook workBook, HSSFSheet sheetOne, List<String> listColumns,
            List<String> listDetail,List<String> colorList,String tmpId) {
//        sheetOne.protectSheet(tmpId);
        
        /** 行 */
        HSSFRow row0 = sheetOne.createRow( 0);
        
        HSSFRow row1 = sheetOne.createRow( 1);
        sheetOne.addMergedRegion(new CellRangeAddress(0,(short)0,0,(short)12));
        
        HSSFFont fontBrown = getFont(workBook, true, UdipConstants.HSSF_COLOR_BROWN);
        HSSFCellStyle cellStyleBrown = getCellStyle(workBook, fontBrown);
        cellStyleBrown.setLocked(true);
        createCell(workBook, cellStyleBrown, 0, row0, "说明：1、请在每列的数据后注明操作类型；2、数据请从第3行开始填写；3、日期类型如未说明格式，请参照yyyyMMdd格式", false, sheetOne, 1);
        
        
        HSSFFont fontRedB = getFont(workBook, true, UdipConstants.HSSF_COLOR_RED);
        HSSFCellStyle cellStyleRedB = getCellStyle(workBook, fontRedB );
        cellStyleRedB.setLocked(true);
        HSSFFont fontBlack = getFont(workBook, true, UdipConstants.HSSF_COLOR_BLACK);
        HSSFCellStyle cellStyleBlack = getCellStyle(workBook, fontBlack );
        cellStyleBlack.setLocked(true);
        
        /** 生成表头 */
        for (int i = 0; i < listColumns.size(); i++) {
            createCell(workBook, colorList.get(i).equals(UdipConstants.HSSF_COLOR_RED)?cellStyleRedB:cellStyleBlack, i, row1, (String) listColumns.get(i), true, sheetOne, 1);
        }
        createCell(workBook,cellStyleBlack, listColumns.size(), row1, "操作类型#" + DATA_TYPE, true, sheetOne, 1);
        HSSFRow row2 = sheetOne.createRow( 2);

        /** 生成表头 */
        HSSFFont fontRed = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLUE);
        HSSFCellStyle cellStyleRed = getCellStyle(workBook, fontRed );
        cellStyleRed.setLocked(true);
        for (int i = 0; i < listDetail.size(); i++) {
            createCell(workBook,cellStyleRed, i, row2, StringUtils.isNotBlank((String) listDetail.get(i))?(String) listDetail.get(i):"空", false, sheetOne,1);
        }
        cellStyleRed.setLocked(false);
        createCell(workBook,cellStyleRed, listColumns.size(), row2, DATA_TYPE, false, sheetOne,1);
        
        /**
         * 解锁默认单元格
         */
        HSSFCellStyle defaultCellStyleBlack = getCellStyle(workBook, fontBlack );
        defaultCellStyleBlack.setLocked(false);
        for(int i = 0; i <= listColumns.size(); i++){
            sheetOne.setDefaultColumnStyle(i, defaultCellStyleBlack);
        }
        
        return workBook;
    }
	
	public HSSFWorkbook createTalbeHead(HSSFWorkbook workBook, HSSFSheet sheetOne, List<String> listColumns,
			List<String> listDetail,List<String> colorList, String title1, String title2) {
		/** 行 */
		HSSFRow row0 = sheetOne.createRow( 0);
		HSSFRow row1 = sheetOne.createRow( 1);
		HSSFRow row2 = sheetOne.createRow( 2);
		HSSFRow row3 = sheetOne.createRow( 3);
		sheetOne.addMergedRegion(new CellRangeAddress(2,(short)0,2,(short)12));
		HSSFFont fontTitle = getFont(workBook, true, UdipConstants.HSSF_COLOR_BLACK);
		createCell(workBook, getCellStyle(workBook, fontTitle), 0, row0,
				title1, false, sheetOne, 1);
		String[] t2s = StringUtils.split(title2, ";");
		HSSFFont fontTitle2 = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyleTitle2 = getCellStyle(workBook, fontTitle2);
		for (int i = 0; i < t2s.length; i++) {
			createCell(workBook, cellStyleTitle2, i, row1, t2s[i], false, sheetOne, 2);
		}
		HSSFFont fontBrown = getFont(workBook, true, UdipConstants.HSSF_COLOR_BROWN);
		HSSFCellStyle cellStyleBrown = getCellStyle(workBook, fontBrown);
		createCell(workBook, cellStyleBrown, 0, row2, "说明：1、请在每列的数据后注明操作类型；2、数据请从第5行开始填写；3、日期类型如未说明格式，请参照yyyyMMdd格式", false, sheetOne, 1);
		HSSFFont fontRedB = getFont(workBook, true, UdipConstants.HSSF_COLOR_RED);
		HSSFCellStyle cellStyleRedB = getCellStyle(workBook, fontRedB );
		HSSFFont fontBlack = getFont(workBook, true, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyleBlack = getCellStyle(workBook, fontBlack );
		/** 生成表头 */
		for (int i = 0; i < listColumns.size(); i++) {
			createCell(workBook, colorList.get(i).equals(UdipConstants.HSSF_COLOR_RED)?cellStyleRedB:cellStyleBlack, i, row3, (String) listColumns.get(i), true, sheetOne, 1);
		}
//		createCell(workBook,cellStyleBlack, listColumns.size(), row3, "操作类型", true, sheetOne, 1);
//		HSSFRow row4 = sheetOne.createRow( 4);

		/** 生成表头 */
//		HSSFFont fontRed = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLUE);
//		HSSFCellStyle cellStyleRed = getCellStyle(workBook, fontRed );
//		for (int i = 0; i < listDetail.size(); i++) {
//			createCell(workBook,cellStyleRed, i, row4, StringUtils.isNotBlank((String) listDetail.get(i))?(String) listDetail.get(i):"空", false, sheetOne,1);
//		}
//		createCell(workBook,cellStyleRed, listColumns.size(), row4, DATA_TYPE, false, sheetOne,1);
		return workBook;
	}

	/**
	 * 生成表头
	 * @param workBook
	 * @param sheetOne
	 * @param listColumns
	 * @return
	 */
	public HSSFWorkbook createTalbeHead(HSSFWorkbook workBook, HSSFSheet sheetOne, List<String> listColumns) {
		HSSFRow row0 = sheetOne.createRow( 0);
		/** 生成表头 */
		HSSFFont fontBlack = getFont(workBook, true, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyleBlack = getCellStyle(workBook, fontBlack );
		
		for (int i = 0; i < listColumns.size(); i++) {
			createCell(workBook, cellStyleBlack, i, row0, (String) listColumns.get(i), true, sheetOne,1);
		}
		return workBook;
	}

	/**
	 * 获取文件数据
	 * @param file
	 * @return
	 */
	public List<Map<String, Object>> getRoleList(File file) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getLastRowNum();
			// 获取列数
			HSSFRow row = sheet.getRow(1);
			List<String> cellSize = Lists.newArrayList();
			for (int i = 0; i < 100; i++) {
				if (row.getCell(i) != null) {
					cellSize.add(String.valueOf(i));
				} else {
					break;
				}
			}

			// 循环获取每行数据
			for (int i = 2; i <= rows; i++) {
				row = sheet.getRow(i);
				if (null == row)
					break;
				Map<String, Object> map = Maps.newHashMap();
				for (int j = 0; j < cellSize.size(); j++) {
					map.put(cellSize.get(j), getCellValue(row.getCell(j)));
				}
				mapList.add(map);
			}
			return mapList;
		} catch (IOException e) {
			log.error(e);
		} finally {
			IOUtils.closeQuietly(wb);
		}
		return null;
	}

	/**
	 * 校验结果导出
	 * @param ruleName
	 * @param validateResult
	 * @return
	 */
	public HSSFWorkbook getWorkBookForListMap(List<String> ruleName, List<String> validateResult) {
		/** EXCEL 工作区对象 */
		HSSFWorkbook workBook = new HSSFWorkbook();
		/** 工作表 (支持中文) */
		HSSFSheet sheetOne = workBook.createSheet("sheet1");
		HSSFRow row0 = sheetOne.createRow( 0);
		/** 生成表头 */
		
		HSSFFont fontBlack = getFont(workBook, true, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyleBlack = getCellStyle(workBook, fontBlack );
		
		HSSFFont fontBlackB = getFont(workBook, false, UdipConstants.HSSF_COLOR_BLACK);
		HSSFCellStyle cellStyleBlackB = getCellStyle(workBook, fontBlackB );
		
		createCell(workBook,cellStyleBlack, 0, row0, "校验名称", true, sheetOne, 2);
		createCell(workBook,cellStyleBlack, 1, row0, "校验结果", true, sheetOne, 6);
		for (int i = 0; i < ruleName.size(); i++) {
			HSSFRow row = sheetOne.createRow( i + 1);
			createCell(workBook,cellStyleBlackB, 0, row, ruleName.get(i), false, sheetOne, 1);
			createCell(workBook, cellStyleBlackB, 1, row, validateResult.get(i), false, sheetOne, 1);
		}
		return workBook;
	}

	/**
	 * 根据excel文件返回文件的list
	 * @param file
	 * @return
	 */
	public Map<String, Object> getAllList(File file, long orderNo) {
		Map<String, Object> mapList = Maps.newHashMap();

		List<Map<String, Object>> addList = Lists.newArrayList();
		List<Map<String, Object>> udpList = Lists.newArrayList();
		List<Map<String, Object>> delList = Lists.newArrayList();
		List<Map<String, Object>> list = Lists.newArrayList();
		FileInputStream is = null;
		HSSFWorkbook wb = null;
		try {
			is = new FileInputStream(file);
			wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getLastRowNum();
			// 获取列数
			HSSFRow row = sheet.getRow(0);
			List<String> cellSize = Lists.newArrayList();
			if (row == null) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("typeWrrong", "读取文件出错，请检查模板是否正确。");
				return map;
			}

			int colIndex = 0;
			while(row.getCell(colIndex) != null){
				String cell = row.getCell(colIndex++).getStringCellValue();
				String[] split = cell.split("#");
				if(split.length == 1){
					break;
				}
				String cell1 = split[0];
				cellSize.add(cell1);
			}
			int k;
			// 循环获取每行数据
			for (int i = 1; i <= rows; i++) {
				k = 0;
				row = sheet.getRow(i);
				if (null == row)
					continue;
				Map<String, Object> map = Maps.newHashMap();
				Map<String, Object> newmap = Maps.newHashMap();
				for (int j = 0; j < cellSize.size(); j++) {
					if(j == cellSize.size()-2 || j == cellSize.size() - 1){
						if (getCellValue(row.getCell(j)) != null) {
							newmap.put(cellSize.get(j), (getCellValue(row.getCell(j)).toString().replaceAll("", "")));
						} else {
							newmap.put(cellSize.get(j), "");
						}
					} else {
						if (getCellValue(row.getCell(j)) != null) {
							map.put(cellSize.get(j), (getCellValue(row.getCell(j)).toString().replaceAll("", "")));
							newmap.put(cellSize.get(j), (getCellValue(row.getCell(j)).toString().replaceAll("", "")));
						} else {
							map.put(cellSize.get(j), "");
							newmap.put(cellSize.get(j), "");
						}
					}
					if (null == getCellValue(row.getCell(j)) || "".equals(getCellValue(row.getCell(j)))) {
						k = k + 1;
					}
				}
				//放开代码，保存补录表中最大的sys_order_no+1 20190819
				map.put(UdipConstants.TAB_ORDER_NO, ++orderNo);
				newmap.put(UdipConstants.TAB_ORDER_NO, ++orderNo);
				udpList.add(map);
				list.add(newmap);
			}
			mapList.put("addList", addList);
			mapList.put("udpList", udpList);
			mapList.put("delList", delList);
			mapList.put("list", list);
			return mapList;
		} catch (FileNotFoundException e) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("file", "目标文件数据不存在，可能已上传，可导入同名文件进行更新。");
			return map;
		} catch (IOException e) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("wrrong", "读取文件出错。");
			return map;
		}finally{
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(wb);
		}
	}
	
}
