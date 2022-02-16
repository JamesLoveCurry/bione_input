package com.yusys.bione.plugin.detail.download.util;

import com.yusys.bione.comp.utils.CollectionsUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.plugin.yuformat.utils.BillCellItemVO;
import com.yusys.bione.plugin.yuformat.utils.BillCellVO;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类,可以将Excel数据导出，也可将数据输出成Excel
 *
 * @author bfk
 *
 */
public class ExcelUtil {

	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private static final String EXCEL_UPDATE_SHEET = "修改";
	private static final String EXCEL_INSERT_SHEET = "新增";
	private static final String PRIMARY_KEY = "数据主键";
	private static PropertiesUtils dataBaseUtil = PropertiesUtils.get("database.properties");
	
	/**
	 * 查看有多少页签
	 *
	 * @param _bys
	 * @return int
	 * @Date 2021/5/18 19:39
	 * @author baifk
	 **/
	public int getSheetCount(byte[] _bys) throws Exception {
		InputStream ins = new ByteArrayInputStream(_bys); //
		Workbook wb = new XSSFWorkbook(ins); //
		int li_count = wb.getNumberOfSheets();//
		ins.close(); //
		return li_count; //
	}

	/**
	 * 将一个BillCellVO导出
	 *
	 * @param _billCellVO
	 * @param _isConvertDouble
	 * @param sheetName
	 * @return org.apache.poi.xssf.streaming.SXSSFWorkbook
	 * @Date 2021/5/18 19:39
	 * @author baifk
	 **/
	public SXSSFWorkbook exportExcelByBillCellVOAsBook(BillCellVO _billCellVO, boolean _isConvertDouble, String sheetName) throws Exception {
		int li_rowCount = _billCellVO.getRowlength(); //几行
		int li_colCount = _billCellVO.getCollength(); //几列
		BillCellItemVO[][] itemVOs = _billCellVO.getCellItemVOs(); //
		// 函数内变量初始化
		SXSSFWorkbook  book = new SXSSFWorkbook ();// 创建工作簿
		if (StringUtils.isEmpty(sheetName)) {
			sheetName = "Sheet1";
		}
		SXSSFSheet sheet = book.createSheet(sheetName);// 创建sheet页签
		sheet.protectSheet("123456");
		//风格本身通过Map实现重用,字体与颜色也是通过Map实现重用
		//即实现了自定义颜色的清准,也解决了字体过多的报错,同时也解决了style太多文件过大的问题
		//总之,这才是一个完美的将BillCellVO导出成Excel的工具方法,以前多个人写的方法都有各种瑕疵,不是最终完美的方法
		//存储自定义风格的Map,后来发现Style没必须一个格子搞一个,因为许多格子的风格其实一样,只要搞一个,这样Excel文件会小许多
		HashMap<String, CellStyle> styleMap = new HashMap<String, CellStyle>(); //
		//空格子的Style,因为遇到一些BillCellItem为null,以前什么都不处理,结果边框不对,所以需要创建一个空格了,只要边框为黑线就行
		CellStyle nullCellStyle = book.createCellStyle(); //创建一个新的风格
		DataFormat  format = book.createDataFormat();
		nullCellStyle.setDataFormat(format.getFormat("@"));
		setCellStyleBorder(nullCellStyle); //设置边框
		//遍历整个表格，取出数据
		for (int i = 0; i < li_rowCount; i++) { //遍历各行
			SXSSFRow row = sheet.createRow(i); //创建一行
			for (int j = 0; j < li_colCount; j++) {
				BillCellItemVO itemVO = itemVOs[i][j]; //
				//有可能某处格子是被别人合并的,是null,所以跳过!
				if (itemVO == null) {
					Cell nullCell = row.createCell(j);//创建单元格
					nullCell.setCellStyle(nullCellStyle); //设置为空格固定的Style,即只有边框线
					//曾经遇到过第一行或第一列就是null,因为计算行高与列宽后面只是取第1行/1列,所以这里要特别判断一下!
					if (j == 0) {
						row.setHeightInPoints((float) ((22 / 1.33) + 10)); //1磅=1.33像素
					}
					if (i == 0) {
						sheet.setColumnWidth(j, (short) (100 * 35.7)); //设置列宽!
					}
					continue; //直接跳过!
				}
				//创建Excel单元格
				SXSSFCell cell = row.createCell(j);//创建单元格
				//设置格子中的值!!
				String str_cellValue = itemVO.getCellvalue(); //格子中的值!
				cell.setCellValue(str_cellValue); //
				//背景色
				String str_bgColor = itemVO.getBackground(); //
				str_bgColor = (str_bgColor == null ? "" : str_bgColor); //

				//横向纵高排列
				int li_halign = itemVO.getHalign();
				int li_valign = itemVO.getValign();
				String isEdit = itemVO.getIseditable();
				isEdit = (isEdit == null ? "" : isEdit);
				//整个Style的key
				String str_styleKey = "[bgcolor]=[" + str_bgColor + "][align]=[" + li_halign + "][valign]=[" + li_valign + "][isEdit]=[" + isEdit +"]";
				CellStyle cellStyle = null; //
				//以前是每个格子创建一个新的CellStyle对象，后来发现其实许多格子的style一样，完全可以共用一个,所以搞了个Map
				//根据风格的样式进行存储,然后实现重用!经过实验,一个50*20的Excel，在重用前生成Excel文件43.5K,重用后生成有Excel文件是13K，只有原来的30%的大小!
				if (styleMap.containsKey(str_styleKey)) {
					cellStyle = styleMap.get(str_styleKey); //
					cell.setCellStyle(cellStyle); //
				} else { //如果没有存储
					cellStyle = book.createCellStyle(); //创建一个新的风格
					cellStyle.setWrapText(true); //自动换行!
					cellStyle.setDataFormat(format.getFormat("@"));
					setCellStyleBorder(cellStyle); //设置边框
					//如果定义了颜色!
					if (str_bgColor != null && !str_bgColor.equals("")) {
						/*cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());*/
						cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						if(i == 0) {
							cellStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
						} else {
							cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
						}
					}
					//横向,纵向排列
					setCellStyleHalign(cellStyle, li_halign); //
					setCellStyleValign(cellStyle, li_valign);
					if (j == 0) {
						cellStyle.setLocked(true);
					} else {
						cellStyle.setLocked(false);
					}
					//绑定
					cell.setCellStyle(cellStyle); //
					styleMap.put(str_styleKey, cellStyle); //放入Map，这样后面遇到有一样的格式,则不要重复创建了,提高性能,减小Excel的大小
				}
				//处理合并
				setSpan(book, sheet, i, j, itemVO.getSpan());
				//设置行高,就第一列的行高为准!
				if (j == 0) {
					String str_rowHeight = itemVO.getRowheight(); //
					int li_rowHeight = (str_rowHeight == null ? 22 : Integer.parseInt(str_rowHeight)); //行高!
					row.setHeightInPoints((float) ((li_rowHeight / 1.33) + 10)); //1磅=1.33像素
				}
				//处理列宽,就以第一行的宽为准!
				if (i == 0) {
					String str_colWidth = itemVO.getColwidth(); //
					int li_colWidth = (str_colWidth == null ? 100 : Integer.parseInt(str_colWidth)); //列宽
					sheet.setColumnWidth(j, (short) (li_colWidth * 35.7)); //设置列宽!
				}
			}
		}

		return book; //
	}

	/**
	 * 设置合并单元格
	 *
	 * @param _book
	 * @param _sheet
	 * @param _row
	 * @param _col
	 * @param _span
	 * @return void
	 * @Date 2021/5/18 19:39
	 * @author baifk
	 **/
	private void setSpan(SXSSFWorkbook _book, SXSSFSheet _sheet, int _row, int _col, String _span) {
		if (_span == null || _span.trim().equals("") || _span.trim().equals("1,1")) {
			return; //
		}
		String[] str_spans = bsUtil.split(_span, ",");
		int li_merge_rows = Integer.parseInt(str_spans[0]); //
		int li_merge_cols = Integer.parseInt(str_spans[1]); //

		//如果是被合并的,则不处理
		if (li_merge_rows <= 0 || li_merge_cols <= 0) {
			return; //
		}

		int li_beginRow = _row;
		int li_endRow = li_beginRow + li_merge_rows - 1; //
		int li_beginCol = _col;
		int li_endCol = li_beginCol + li_merge_cols - 1; //

		//创建一个合并的区域,并加入sheet中
		CellRangeAddress area = new CellRangeAddress(li_beginRow, li_endRow, li_beginCol, li_endCol); //2,8,3,5表示从第2行,到第8行,从第3列，至第5列
		_sheet.addMergedRegion(area);

		//合并框的四周都是黑线.
		RegionUtil.setBorderBottom(BorderStyle.THIN, area, _sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, area, _sheet); // 左边框
		RegionUtil.setBorderRight(BorderStyle.THIN, area, _sheet); // 有边框
		RegionUtil.setBorderTop(BorderStyle.THIN, area, _sheet); // 上边框
	}



	/**
	 * /横向居左/居中/居右
	 *
	 * @param cellStyle
	 * @param _align
	 * @return void
	 * @Date 2021/5/18 19:39
	 * @author baifk
	 **/
	private void setCellStyleHalign(CellStyle cellStyle, int _align) {
		if (_align == 1) {
			cellStyle.setAlignment(HorizontalAlignment.LEFT);// 居左
		} else if (_align == 2) {
			cellStyle.setAlignment(HorizontalAlignment.CENTER);//居中
		} else if (_align == 3) {
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);//居右
		} else {
			cellStyle.setAlignment(HorizontalAlignment.LEFT);//居中
		}
	}

	/**
	 * /纵向
	 *
	 * @param cellStyle
	 * @param _valign
	 * @return void
	 * @Date 2021/5/18 19:39
	 * @author baifk
	 **/
	private void setCellStyleValign(CellStyle cellStyle, int _valign) {
		if (_valign == 1) {
			cellStyle.setVerticalAlignment(VerticalAlignment.TOP);// 居顶
		} else if (_valign == 2) {
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//居中
		} else if (_valign == 3) {
			cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);//置底
		} else {
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//居中
		}
	}
	//设置边框
	private void setCellStyleBorder(CellStyle _cellStyle) {
		_cellStyle.setBorderTop(BorderStyle.THIN);
		_cellStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderRight(BorderStyle.THIN);
		_cellStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderBottom(BorderStyle.THIN);
		_cellStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderLeft(BorderStyle.THIN);
		_cellStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
	}



	//把二维数组导出Excel
	public SXSSFWorkbook exportExcelByStrArrayAsBook1(String[][] _data, int[] _colWidths, Map<String, String> map, String source) throws Exception {
		BillCellVO cellVO = new BillCellVO(); //
		cellVO.setTempletcode("导出Excel");
		cellVO.setTempletname("导出Excel");

		int li_rows = _data.length;
		int li_cols = _data[0].length; //

		cellVO.setRowlength(li_rows); //多少行
		cellVO.setCollength(li_cols); //多少列

		//创建实体数据
		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols];
		for (int i = 0; i < li_rows; i++) {
			for (int j = 0; j < li_cols; j++) { //
				cellItemVOs[i][j] = new BillCellItemVO(); //
				cellItemVOs[i][j].setCellrow(i); //第几行
				cellItemVOs[i][j].setCellcol(j); //第几列
				cellItemVOs[i][j].setCelltype("TEXT"); //文本
				cellItemVOs[i][j].setCellvalue(_data[i][j]); //设置内容值
				cellItemVOs[i][j].setFont("宋体", 10);
				cellItemVOs[i][j].setHalign(2); //横向居中


				if (i == 0) {
					cellItemVOs[i][j].setBackground("216,255,226"); //
					cellItemVOs[i][j].setIseditable("true");
				}
				if (j == 0) {
					cellItemVOs[i][j].setIseditable("true");
				}
				if (map != null && map.containsKey(Integer.toString(i))) {
					String errorNumStr = map.get(Integer.toString(i));
					String[] errorNums = errorNumStr.split(",");
					for (String errorNum: errorNums) {
						if (Integer.parseInt(errorNum) == j) {
							// 设置错误背景色为黄色
							cellItemVOs[i][j].setBackground("255,255,0"); //
						}
					}
				}
				//设置行高与列宽
				cellItemVOs[i][j].setRowheight("20"); //
				if (_colWidths != null) {
					cellItemVOs[i][j].setColwidth("" + _colWidths[j]); //
				} else {
					cellItemVOs[i][j].setColwidth("200"); //
				}
			}
		}
		cellVO.setCellItemVOs(cellItemVOs); //设置内容

		return exportExcelByBillCellVOAsBook1(cellVO, source); //
	}

	//将一个BillCellVO导出.
	public SXSSFWorkbook exportExcelByBillCellVOAsBook1(BillCellVO _billCellVO, String source) throws Exception {
		int li_rowCount = _billCellVO.getRowlength(); //几行
		int li_colCount = _billCellVO.getCollength(); //几列
		BillCellItemVO[][] itemVOs = _billCellVO.getCellItemVOs(); //
		// 函数内变量初始化
		SXSSFWorkbook  book = new SXSSFWorkbook ();// 创建工作簿

		String[] shename = null;
		if ("1".equals(source)) {
			shename = new String[] {EXCEL_INSERT_SHEET};
		} else {
			shename = new String[] {EXCEL_INSERT_SHEET, EXCEL_UPDATE_SHEET};
		}

		for (int k = 0; k < shename.length; k++) {

			SXSSFSheet sheet = book.createSheet(shename[k]);// 创建sheet页签
			if(EXCEL_UPDATE_SHEET.equals(shename[k])) {
				sheet.protectSheet("123456");
			}

			// 设置单元格格式默认为文本
			CellStyle textStyle = book.createCellStyle();
			DataFormat  format = book.createDataFormat();
			textStyle.setDataFormat(format.getFormat("@"));
			for (int i=0; i<li_colCount; i++) {

				sheet.setDefaultColumnStyle(i, textStyle);
			}
			//风格本身通过Map实现重用,字体与颜色也是通过Map实现重用
			//即实现了自定义颜色的清准,也解决了字体过多的报错,同时也解决了style太多文件过大的问题
			//总之,这才是一个完美的将BillCellVO导出成Excel的工具方法,以前多个人写的方法都有各种瑕疵,不是最终完美的方法
			//存储自定义风格的Map,后来发现Style没必须一个格子搞一个,因为许多格子的风格其实一样,只要搞一个,这样Excel文件会小许多
			HashMap<String, CellStyle> styleMap = new HashMap<String, CellStyle>(); //
			//空格子的Style,因为遇到一些BillCellItem为null,以前什么都不处理,结果边框不对,所以需要创建一个空格了,只要边框为黑线就行
			CellStyle nullCellStyle = book.createCellStyle(); //创建一个新的风格
			nullCellStyle.setDataFormat(format.getFormat("@"));
			setCellStyleBorder(nullCellStyle); //设置边框
			//遍历整个表格，取出数据

			for (int i = 0; i < li_rowCount; i++) { //遍历各行
				if(EXCEL_INSERT_SHEET.equals(shename[k]) && i>=1) {
					continue;
				}
				SXSSFRow row = sheet.createRow(i); //创建一行
				for (int j = 0; j < li_colCount; j++) {
					BillCellItemVO itemVO = itemVOs[i][j]; //
					if(EXCEL_INSERT_SHEET.equals(shename[k])) {
						if(PRIMARY_KEY.equals(itemVO.getCellvalue())) {
							continue; //直接跳过!
						}
					}

					//有可能某处格子是被别人合并的,是null,所以跳过!
					if (itemVO == null) {
						Cell nullCell = null;
						if(EXCEL_INSERT_SHEET.equals(shename[k])) {
							nullCell = row.createCell(j-1);//创建单元格
						}else {
							nullCell = row.createCell(j);//创建单元格
						}

						nullCellStyle.setDataFormat(format.getFormat("@"));
						nullCell.setCellStyle(nullCellStyle); //设置为空格固定的Style,即只有边框线
						//曾经遇到过第一行或第一列就是null,因为计算行高与列宽后面只是取第1行/1列,所以这里要特别判断一下!
						if (j == 0) {
							row.setHeightInPoints((float) ((22 / 1.33) + 10)); //1磅=1.33像素
						}
						if (i == 0) {
							sheet.setColumnWidth(j, (short) (100 * 35.7)); //设置列宽!
						}
						continue; //直接跳过!
					}
					//创建Excel单元格
					SXSSFCell cell = null;//创建单元格
					if("新增".equals(shename[k])) {
						cell = row.createCell(j-1);//创建单元格
					}else {
						cell = row.createCell(j);//创建单元格
					}
					//设置格子中的值!!
					String str_cellValue = itemVO.getCellvalue(); //格子中的值!
					cell.setCellValue(str_cellValue); //
					//背景色
					String str_bgColor = itemVO.getBackground(); //
					str_bgColor = (str_bgColor == null ? "" : str_bgColor); //

					//横向纵高排列
					int li_halign = itemVO.getHalign();
					int li_valign = itemVO.getValign();
					String isEdit = itemVO.getIseditable();
					isEdit = (isEdit == null ? "" : isEdit);
					//整个Style的key
					String str_styleKey = "[bgcolor]=[" + str_bgColor + "][align]=[" + li_halign + "][valign]=[" + li_valign + "][isEdit]=[" + isEdit +"]";
					CellStyle cellStyle = null; //
					//以前是每个格子创建一个新的CellStyle对象，后来发现其实许多格子的style一样，完全可以共用一个,所以搞了个Map
					//根据风格的样式进行存储,然后实现重用!经过实验,一个50*20的Excel，在重用前生成Excel文件43.5K,重用后生成有Excel文件是13K，只有原来的30%的大小!
					if (styleMap.containsKey(str_styleKey)) {
						cellStyle = styleMap.get(str_styleKey); //
						cell.setCellStyle(cellStyle); //
					} else { //如果没有存储
						cellStyle = book.createCellStyle(); //创建一个新的风格
						cellStyle.setWrapText(true); //自动换行!
						cellStyle.setDataFormat(format.getFormat("@"));
						setCellStyleBorder(cellStyle); //设置边框
						//如果定义了颜色!
						if (str_bgColor != null && !str_bgColor.equals("")) {
    						/*cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    						cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());*/
							cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							if(i == 0) {
								cellStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
							} else {
								cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
							}
						}
						//横向,纵向排列
						setCellStyleHalign(cellStyle, li_halign); //
						setCellStyleValign(cellStyle, li_valign);
						if (j == 0) {
							cellStyle.setLocked(true);
						} else {
							cellStyle.setLocked(false);
						}
						//绑定
						cell.setCellStyle(cellStyle); //
						styleMap.put(str_styleKey, cellStyle); //放入Map，这样后面遇到有一样的格式,则不要重复创建了,提高性能,减小Excel的大小
					}
					//处理合并
					setSpan(book, sheet, i, j, itemVO.getSpan());
					//设置行高,就第一列的行高为准!
					if(EXCEL_INSERT_SHEET.equals(shename[k])) {
						if (j == 1) {
							String str_rowHeight = itemVO.getRowheight(); //
							int li_rowHeight = (str_rowHeight == null ? 22 : Integer.parseInt(str_rowHeight)); //行高!
							row.setHeightInPoints((float) ((li_rowHeight / 1.33) + 10)); //1磅=1.33像素
						}

						//处理列宽,就以第一行的宽为准!
						if (i == 0) {
							String str_colWidth = itemVO.getColwidth(); //
							int li_colWidth = (str_colWidth == null ? 100 : Integer.parseInt(str_colWidth)); //列宽
							sheet.setColumnWidth(j-1, (short) (li_colWidth * 35.7)); //设置列宽!
						}
					}else {
						if (j == 0) {
							String str_rowHeight = itemVO.getRowheight(); //
							int li_rowHeight = (str_rowHeight == null ? 22 : Integer.parseInt(str_rowHeight)); //行高!
							row.setHeightInPoints((float) ((li_rowHeight / 1.33) + 10)); //1磅=1.33像素
						}

						//处理列宽,就以第一行的宽为准!
						if (i == 0) {
							String str_colWidth = itemVO.getColwidth(); //
							int li_colWidth = (str_colWidth == null ? 100 : Integer.parseInt(str_colWidth)); //列宽
							sheet.setColumnWidth(j, (short) (li_colWidth * 35.7)); //设置列宽!
						}
					}
				}
			}
		}


		return book; //
	}

	//设置合并单元格!
	private void setSpan1(HSSFWorkbook _book, HSSFSheet _sheet, int _row, int _col, String _span) {
		if (_span == null || _span.trim().equals("") || _span.trim().equals("1,1")) {
			return; //
		}
		String[] str_spans = bsUtil.split(_span, ",");
		int li_merge_rows = Integer.parseInt(str_spans[0]); //
		int li_merge_cols = Integer.parseInt(str_spans[1]); //

		//如果是被合并的,则不处理
		if (li_merge_rows <= 0 || li_merge_cols <= 0) {
			return; //
		}

		int li_beginRow = _row;
		int li_endRow = li_beginRow + li_merge_rows - 1; //
		int li_beginCol = _col;
		int li_endCol = li_beginCol + li_merge_cols - 1; //

		//创建一个合并的区域,并加入sheet中
		CellRangeAddress area = new CellRangeAddress(li_beginRow, li_endRow, li_beginCol, li_endCol); //2,8,3,5表示从第2行,到第8行,从第3列，至第5列
		_sheet.addMergedRegion(area);

		//合并框的四周都是黑线.
		RegionUtil.setBorderBottom(BorderStyle.THIN, area, _sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, area, _sheet); // 左边框
		RegionUtil.setBorderRight(BorderStyle.THIN, area, _sheet); // 有边框
		RegionUtil.setBorderTop(BorderStyle.THIN, area, _sheet); // 上边框
	}

	//设置边框
	private void setCellStyleBorder1(HSSFCellStyle _cellStyle) {
		_cellStyle.setBorderTop(BorderStyle.THIN);
		_cellStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderRight(BorderStyle.THIN);
		_cellStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderBottom(BorderStyle.THIN);
		_cellStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderLeft(BorderStyle.THIN);
		_cellStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
	}

	//横向居左/居中/居右
	private void setCellStyleHalign1(HSSFCellStyle cellStyle, int _align) {
		if (_align == 1) {
			cellStyle.setAlignment(HorizontalAlignment.LEFT);// 居左
		} else if (_align == 2) {
			cellStyle.setAlignment(HorizontalAlignment.CENTER);//居中
		} else if (_align == 3) {
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);//居右
		} else {
			cellStyle.setAlignment(HorizontalAlignment.LEFT);//居中
		}
	}

	//纵向
	private void setCellStyleValign1(HSSFCellStyle cellStyle, int _valign) {
		if (_valign == 1) {
			cellStyle.setVerticalAlignment(VerticalAlignment.TOP);// 居顶
		} else if (_valign == 2) {
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//居中
		} else if (_valign == 3) {
			cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);//置底
		} else {
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//居中
		}
	}
	/**
	 *
	 * <pre>
	 * Title: 把二维数组导出Excel
	 * </pre>
	 * @author miaokx
	 * @version 1.00.00
	 * @date 11:37 2020/10/22
	 */
	public SXSSFWorkbook exportExcelByStrArrayAsBook(String[][] _data, int[] _colWidths, String sheetName, Map<String, String> map, List<Integer> noChangeList) throws Exception {
		BillCellVO cellVO = new BillCellVO(); //
		cellVO.setTempletcode("导出Excel");
		cellVO.setTempletname("导出Excel");
		int li_rows = _data.length;
		int li_cols = _data[0].length ; //
		cellVO.setRowlength(li_rows); //多少行
		cellVO.setCollength(li_cols); //多少列
		//创建实体数据
		BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols];
		for (int i = 0; i < li_rows; i++) {
			for (int j = 0; j < li_cols; j++) { //
				cellItemVOs[i][j] = new BillCellItemVO(); //
				cellItemVOs[i][j].setCellrow(i); //第几行
				cellItemVOs[i][j].setCellcol(j); //第几列
				cellItemVOs[i][j].setCelltype("TEXT"); //文本
				cellItemVOs[i][j].setCellvalue(_data[i][j]); //设置内容值
				cellItemVOs[i][j].setFont("宋体", 10);
				cellItemVOs[i][j].setHalign(2); //横向居中
				if (i == 0) {
					cellItemVOs[i][j].setBackground("216,255,226"); //
					cellItemVOs[i][j].setIseditable("true");
				}
				if (CollectionsUtils.isNotEmpty(noChangeList) && noChangeList.contains(j)) {
					cellItemVOs[i][j].setIseditable("true");
				}
				if (map != null && map.containsKey(Integer.toString(i))) {
					String errorNumStr = map.get(Integer.toString(i));
					String[] errorNums = errorNumStr.split(",");
					for (String errorNum: errorNums) {
						if (Integer.parseInt(errorNum) == j) {
							// 设置错误背景色为黄色
							cellItemVOs[i][j].setBackground("255,255,0"); //
						}
					}
				}
				//设置行高与列宽
				cellItemVOs[i][j].setRowheight("20"); //
				if (_colWidths != null) {
					cellItemVOs[i][j].setColwidth("" + _colWidths[j]); //
				} else {
					cellItemVOs[i][j].setColwidth("200"); //
				}
			}
		}
		cellVO.setCellItemVOs(cellItemVOs); //设置内容

		return exportExcelByBillCellVOAsBook(cellVO, false, sheetName); //
	}

	public  String getBusinessTableDsName(String tableName) {
		HashVO[] hvs_tab = bsUtil.getHashVOs("select ds_name AS dsName from east_cr_tab where tab_name_en = '" + tableName + "'");
		if (hvs_tab.length > 0) {
			return hvs_tab[0].getStringValue("dsName");
		}
		// 没有设置则使用默认
		return dataBaseUtil.getProperty("jdbc.username");
	}
}
