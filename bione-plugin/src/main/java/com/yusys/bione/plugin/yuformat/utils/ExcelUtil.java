package com.yusys.bione.plugin.yuformat.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel工具类,可以将Excel数据导出，也可将数据输出成Excel
 * 
 * @author bfk
 * 
 */
public class ExcelUtil {

	private YuFormatUtil bsUtil = new YuFormatUtil(); //

	//读取Excel文件..
	public String[][] getExcelFileData(String _filename) {
		return getExcelFileData(_filename, 0); //
	}

	//读取Excel数据
	public String[][] getExcelFileData(String _filename, int _sheetIndex, boolean _delNullrow, boolean _delNullcol) {
		String[][] str_data = getExcelFileData(_filename, _sheetIndex); //
		str_data = trimNullRowCol(str_data, _delNullrow, _delNullcol);
		return str_data; //
	}

	//读取Excel文件第几处页签..
	public String[][] getExcelFileData(String _filename, int _sheetIndex) {
		FileInputStream ins = null; //
		try {
			if (_filename.toLowerCase().endsWith(".xlsx")) {
				ins = new FileInputStream(_filename); //
				return getExcelFileData(ins, _sheetIndex, true); //
			} else if (_filename.toLowerCase().endsWith(".xls")) {
				ins = new FileInputStream(_filename); //
				return getExcelFileData(ins, _sheetIndex, false); //
			} else {
				System.err.println("文件不是Excel文件无法处理,后辍名既不是xls,也不是xlsx!"); //
				return null; //
			}
		} catch (Exception e) {
			System.err.println("读取Excel中的了发生错误:"); //
			e.printStackTrace(); //
			return null;
		} finally {
			try {
				if (ins != null) {
					ins.close(); // 先关闭
				}
			} catch (Exception e) {
			}
		}
	}

	//直接通过文件读取.
	public String[][] getExcelFileData(File _file, int _sheetIndex) {
		FileInputStream ins = null; //
		try {
			String str_fileName = _file.getName(); //
			if (str_fileName.toLowerCase().endsWith(".xlsx")) {
				ins = new FileInputStream(_file); //
				return getExcelFileData(ins, _sheetIndex, true); //
			} else if (str_fileName.toLowerCase().endsWith(".xls")) {
				ins = new FileInputStream(_file); //
				return getExcelFileData(ins, _sheetIndex, false); //
			} else {
				System.err.println("文件不是Excel文件无法处理,后辍名既不是xls,也不是xlsx!"); //
				return null; //
			}
		} catch (Exception e) {
			System.err.println("读取Excel中的了发生错误:"); //
			e.printStackTrace(); //
			return null;
		} finally {
			try {
				if (ins != null) {
					ins.close(); // 先关闭
				}
			} catch (Exception e) {
			}
		}
	}

	//直接将一个文件内容传过来,这用在BS端处理,即先在UI端把Excel文件读成byte[],然后传到后台处理
	public String[][] getExcelFileData(byte[] _bys, int _sheetIndex, boolean _isX) {
		InputStream ins = null; //
		try {
			ins = new ByteArrayInputStream(_bys); //
			return getExcelFileData(ins, _sheetIndex, _isX); //
		} catch (Exception e) {
			System.err.println("读取Excel中的了发生错误:"); //
			e.printStackTrace(); //
			return null;
		} finally {
			try {
				ins.close(); // 先关闭
			} catch (Exception e) {
			}
		}
	}

	//看有多少个页签!
	public int getSheetCount(byte[] _bys) throws Exception {
		InputStream ins = new ByteArrayInputStream(_bys); //
		Workbook wb = new HSSFWorkbook(ins); //
		int li_count = wb.getNumberOfSheets();//
		ins.close(); //
		return li_count; //
	}

	//获得某个页签名称
	public String getSheetName(byte[] _bys, int _index) throws Exception {
		InputStream ins = new ByteArrayInputStream(_bys); //
		Workbook wb = new HSSFWorkbook(ins); //
		String str_name = wb.getSheetName(_index); //
		ins.close(); //
		return str_name; //
	}

	/**
	 * 将Excel中的数据导出成二维数组
	 * 
	 * @param _filename D:/123/aaa.xls
	 * @return
	 */

	private String[][] getExcelFileData(InputStream _ins, int _sheetIndex, boolean _isX) throws Exception {
		Workbook wb = null; // 打开面板
		if (_isX) { //判断是否是2010的格式,即xlsx,则好象要这样取一下??
			wb = new XSSFWorkbook(_ins); //可能要使用XSSFWorkbook
		} else {
			wb = new HSSFWorkbook(_ins); //
		}
		_ins.close(); //
		Sheet sheet = wb.getSheetAt(_sheetIndex); // 取得第一个页签
		//String str_sheetName = sheet.getSheetName(); //
		int li_firstrow = sheet.getFirstRowNum();
		int li_lastrow = sheet.getLastRowNum();
		int max = 0;
		String[][] str_data = null;
		ArrayList rowsList = new ArrayList();
		for (int i = li_firstrow; i <= li_lastrow; i++) {
			ArrayList colsList = new ArrayList();
			Row row = sheet.getRow(i); // 取得第一行
			if (row != null) { // 如果某一行不为空
				int li_firstcol = row.getFirstCellNum(); // 第一列的序号
				int li_lastcol = row.getLastCellNum(); // 最后一列的序号
				if (li_lastcol >= max) {
					max = li_lastcol; //
				}
				for (int j = 0; j <= li_lastcol; j++) {
					Cell cell = row.getCell((short) j); // 取到一个格子!
					if (cell != null) { // 如果某个格子不为空!
						int li_cellType = cell.getCellType(); //
						if (li_cellType == Cell.CELL_TYPE_STRING) { // 如果格子中的类型是字符型
							// cell.setEncoding((short) 1); // 设置编码,只有设成这样,中文才不会乱码,否则会出现中文乱码的情况!!!
							String str_value = cell.getStringCellValue(); //
							colsList.add(str_value);
						} else if (li_cellType == Cell.CELL_TYPE_NUMERIC) {
							String str_value = "";
							if (DateUtil.isCellDateFormatted(cell)) {
								Date d = cell.getDateCellValue();
								DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
								str_value = formater.format(d);
								if (str_value.substring(0, 4).equals("1900")) {
									str_value = String.valueOf((long) cell.getNumericCellValue());
								}
							} else {
								double ld_value = cell.getNumericCellValue(); //
								str_value = bsUtil.getDoubleToString(ld_value);
							}
							colsList.add(str_value);
						} else if (li_cellType == Cell.CELL_TYPE_FORMULA) {
							String str_value = null; //
							try {
								str_value = "" + cell.getStringCellValue(); //
							} catch (Exception ex) {
								//System.err.println("Excel文件第[" + i + "]行[" + j + "]列的格子的公式值转字符出错!"); //
								try {
									str_value = "" + cell.getNumericCellValue(); //
								} catch (Exception exx) {
									//System.err.println("Excel文件第第[" + i + "]行[" + j + "]列的格子的公式值转数字出错!"); //
								}
							}
							colsList.add(str_value == null ? "" : str_value);
						} else if (li_cellType == Cell.CELL_TYPE_ERROR) {
							String str_value = "" + cell.getErrorCellValue(); //
							colsList.add(str_value);
						} else {
							String str_value = cell.getStringCellValue(); //
							if (str_value == null) {
								str_value = "";
							}
							colsList.add(str_value);
						}
					} else {
						colsList.add("");
					}
				}
			}
			rowsList.add(colsList); //
		}

		str_data = new String[rowsList.size()][max]; //
		for (int i = 0; i < rowsList.size(); i++) {
			ArrayList itemrows = (ArrayList) rowsList.get(i); //
			for (int j = 0; j < str_data[i].length; j++) { //
				if (itemrows.size() > j) { //
					str_data[i][j] = (String) itemrows.get(j); //
				}
			}
		}

		return str_data; //
	}

	/**
	 * 完成EXCEL数据向二维数组的转换，拓展了导出的数据，建议以后转换时使用此函数！
	 * 
	 * @param fileName
	 *            输入要导入的文件名：全路径。例如：c:/import.xls
	 * @return 返回结果是一个二维字符串数组。
	 */
	public String[][] getExcelFileDataToStringArray(String fileName) {
		FileInputStream input = null;
		POIFSFileSystem fileSystem = null;
		HSSFWorkbook workBook = null;
		HSSFSheet sheet = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		List rowList = null;
		List columnList = null;
		int beginRow = 0;
		int endRow = 0;
		int beginColumn = 0;
		int endColumn = 0;
		int maxColumns = 0;
		String values[][] = null;
		try {
			input = new FileInputStream(fileName);

			fileSystem = new POIFSFileSystem(input); //
			workBook = new HSSFWorkbook(fileSystem);
			int cout = workBook.getNumberOfSheets();
			rowList = new ArrayList();
			for (int n = 0; n < cout; n++) {
				sheet = null;
				sheet = workBook.getSheetAt(n);
				beginRow = sheet.getFirstRowNum();
				endRow = sheet.getLastRowNum();
				for (int i = beginRow + 1; i <= endRow; i++) {
					row = sheet.getRow(i);
					if (row != null) {
						beginColumn = row.getFirstCellNum();
						endColumn = row.getLastCellNum();
						if (endColumn > maxColumns) {
							maxColumns = endColumn;
						}
						columnList = new ArrayList();
						for (int j = beginColumn; j < endColumn; j++) {
							cell = row.getCell((short) j);
							if (cell != null) {
								if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
									columnList.add(cell.getStringCellValue());
								} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									String number = "";
									if (DateUtil.isCellDateFormatted(cell)) {
										Date d = cell.getDateCellValue();
										DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
										number = formater.format(d);
										if (number.substring(0, 4).equals("1900")) {
											number = String.valueOf((long) cell.getNumericCellValue());
										}
									} else {
										DecimalFormat df = new DecimalFormat("0.00");
										number = df.format(cell.getNumericCellValue());
									}
									// if (cell.getNumericCellValue() - ((long) cell.getNumericCellValue()) != 0) { // 目前运算不准确，以后偏差在0.01左右，还可以接受
									// DecimalFormat df = new DecimalFormat("0.00");
									// number = df.format(cell.getNumericCellValue());
									// } else {
									// Date d = cell.getDateCellValue();
									// DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
									// number = formater.format(d);
									// if (number.substring(0, 4).equals("1900")) {
									// number = String.valueOf((long) cell.getNumericCellValue());
									// }
									// }
									columnList.add(number);
								} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
									columnList.add(String.valueOf(cell.getBooleanCellValue()));
								} else {
									columnList.add("");
								}
							} else {
								columnList.add("");
							}
						}
					}
					if (columnList.size() != 0) {
						rowList.add(columnList);
					}
				}
				values = new String[rowList.size()][maxColumns + 1];
				for (int i = 0; i < rowList.size(); i++) {
					columnList = (List) rowList.get(i);
					int j = 0;
					for (; j < columnList.size(); j++) {
						if (columnList.get(j) != null) {
							values[i][j] = new String(columnList.get(j).toString());
						} else {
							values[i][j] = null;
						}
					}
					// if (j <= maxColumns) {
					// for (int k = j; k < maxColumns; k++) {
					// values[i][k] = null;
					// }
					// }
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}

	/**
	 * 去除Excel中全部是空白的行和列
	 * @param _strs
	 * @return
	 */
	public String[][] trimNullRowCol(String[][] _strs, boolean _delNullrow, boolean _delNullcol) {
		if (!_delNullrow && !_delNullcol) {//不去空白行和列
			return _strs;
		}
		if (_strs == null || _strs.length == 0) {
			return _strs;

		}

		HashMap<Integer, String> rowmap = new HashMap<Integer, String>();
		if (_delNullrow) {
			for (int i = 0; i < _strs.length; i++) {//遍历行是否都是有数据的
				for (int j = 0; j < _strs[i].length; j++) {
					if (!YuFormatUtil.isEmpty(_strs[i][j])) {//一行中某个单元格有数据
						rowmap.put(i, null);
						continue;
					}
				}
			}
		}

		HashMap<Integer, String> colmap = new HashMap<Integer, String>();
		if (_delNullcol) {//去空白列
			for (int i = 0; i < _strs.length; i++) {//遍历列是否都是有数据的
				for (int j = 0; j < _strs[i].length; j++) {
					if (!YuFormatUtil.isEmpty(_strs[i][j])) {//一列中某个单元格有数据
						colmap.put(j, null);
					}
				}
			}
		}

		List<String[]> dataList = new ArrayList<String[]>();
		for (int i = 0; i < _strs.length; i++) {
			if (_delNullrow && !rowmap.containsKey(i)) {//这一行是空行
				continue;
			}
			List<String> rowList = new ArrayList<String>();
			for (int j = 0; j < _strs[i].length; j++) {
				if (!_delNullcol || (_delNullcol && colmap.containsKey(j))) {
					rowList.add(_strs[i][j]);
				}
			}
			dataList.add(rowList.toArray(new String[0]));
		}

		int ll_row = _strs.length;
		int ll_col = _strs[0].length;
		if (_delNullrow) {
			ll_row = rowmap.size();
		}
		if (_delNullcol) {
			ll_col = colmap.size();
		}

		String[][] retstrs = new String[ll_row][ll_col];
		for (int i = 0; i < retstrs.length; i++) {
			retstrs[i] = dataList.get(i);
		}
		return retstrs;
	}

	//导出Excel数据至byte[]
	public byte[] exportExcelByHashVOs(HashVO[] _hvs, String sheetName, String header) throws Exception {
		HSSFWorkbook book = exportExcelByHashVOsAsBook(_hvs, sheetName, header);
		return convertBookToBytes(book); //
	}

	public HSSFWorkbook exportExcelByHashVOsAsBook(HashVO[] _hvs, String sheetName, String header) throws Exception {
		String[] str_keys = null;

		if (header == null) {
			str_keys = _hvs[0].getKeys();
		} else {
			str_keys = header.split(",");
		}
		return exportExcelByHashVOsAsBook(_hvs, sheetName, str_keys, null);
	}

	//导出Excel..
	public HSSFWorkbook exportExcelByHashVOsAsBook(HashVO[] _hvs, String sheetName, String[] str_keys, String[] str_col_aligns) throws Exception {
		String[][] str_data = new String[_hvs.length + 1][str_keys.length];

		//输出表头
		for (int j = 0; j < str_keys.length; j++) {
			str_data[0][j] = str_keys[j];
		}

		if (_hvs != null && _hvs.length > 0) {
			for (int i = 0; i < _hvs.length; i++) {
				for (int j = 0; j < str_keys.length; j++) {
					str_data[i + 1][j] = _hvs[i].getStringValue(j);
				}
			}
		}

		return exportExcelByStrArrayAsBook(str_data, null, sheetName, str_col_aligns);
	}
	
	//导出Excel（多sheet）
	public HSSFWorkbook exportExcelByHashVOsAsBook1(List<HashVO[]> _hvs, String sheetName, String header) throws Exception {
		List<String[][]> list = new ArrayList<String[][]>();
		String[] str_headers = header.split(";");

		int ii = 0;
		for (HashVO[] hvs : _hvs) {
			String[] str_keys = null;

			if (str_headers == null || str_headers.length <= 0) {
				str_keys = hvs[0].getKeys();
			} else {
				str_keys = str_headers[ii].split(",");
				ii++;
			}
			String[][] str_data = new String[hvs.length + 1][str_keys.length];

			//输出表头
			for (int j = 0; j < str_keys.length; j++) {
				str_data[0][j] = str_keys[j];
			}

			if (hvs != null && hvs.length > 0) {
				for (int i = 0; i < hvs.length; i++) {
					for (int j = 0; j < str_keys.length; j++) {
						str_data[i + 1][j] = hvs[i].getStringValue(j);
					}
				}
			}
			list.add(str_data);
		}

		return exportExcelByStrArrayAsBook1(list, null, sheetName);
	}

	//把Excel的Book对象转换成byte[]
	public byte[] exportExcelByStrArray(String[][] _data, int[] _colWidths, String sheetName) throws Exception {
		HSSFWorkbook book = exportExcelByStrArrayAsBook(_data, _colWidths, sheetName, null);
		return convertBookToBytes(book);
	}

	//把二维数组导出Excel
	public HSSFWorkbook exportExcelByStrArrayAsBook(String[][] _data, int[] _colWidths, String sheetName, String[] str_col_aligns) throws Exception {
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
				if (i != 0) {
					if (str_col_aligns != null && str_col_aligns.length > 0) {
						String al = str_col_aligns[j];
						if (StringUtils.isNotBlank(al)) {
							if (al.equals("左")) {
								cellItemVOs[i][j].setHalign(1);
							} else if (al.equals("中")) {
								cellItemVOs[i][j].setHalign(2);
							} else if (al.equals("右")) {
								cellItemVOs[i][j].setHalign(3);
							}
						}
					}
				}
				
				if (i == 0) {
					cellItemVOs[i][j].setBackground("216,255,226"); //
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

	//把二维数组导出Excel（多sheet）
	public HSSFWorkbook exportExcelByStrArrayAsBook1(List<String[][]> _data, int[] _colWidths, String sheetName) throws Exception {
		List<BillCellVO> list = new ArrayList<BillCellVO>();

		for (String[][] data : _data) {
			BillCellVO cellVO = new BillCellVO();
			cellVO.setTempletcode("导出Excel");
			cellVO.setTempletname("导出Excel");

			int li_rows = data.length;
			int li_cols = data[0].length;

			cellVO.setRowlength(li_rows); //多少行
			cellVO.setCollength(li_cols); //多少列

			//创建实体数据
			BillCellItemVO[][] cellItemVOs = new BillCellItemVO[li_rows][li_cols];
			for (int i = 0; i < li_rows; i++) {
				for (int j = 0; j < li_cols; j++) {
					cellItemVOs[i][j] = new BillCellItemVO();
					cellItemVOs[i][j].setCellrow(i); //第几行
					cellItemVOs[i][j].setCellcol(j); //第几列
					cellItemVOs[i][j].setCelltype("TEXT"); //文本
					cellItemVOs[i][j].setCellvalue(data[i][j]); //设置内容值
					cellItemVOs[i][j].setFont("宋体", 10);
					if (i == 0) {
						cellItemVOs[i][j].setBackground("216,255,226");
					}

					cellItemVOs[i][j].setHalign(2); //横向居中

					//设置行高与列宽
					cellItemVOs[i][j].setRowheight("20");
					if (_colWidths != null) {
						cellItemVOs[i][j].setColwidth("" + _colWidths[j]);
					} else {
						cellItemVOs[i][j].setColwidth("200");
					}
				}
			}
			cellVO.setCellItemVOs(cellItemVOs); //设置内容
			list.add(cellVO);
		}

		return exportExcelByBillCellVOAsBook1(list, false, sheetName);
	}

	//将一下BillCellVO数据导出成Excel
	public byte[] exportExcelByBillCellVO(BillCellVO _billCellVO, String sheetName) throws Exception {
		return exportExcelByBillCellVO(_billCellVO, true, sheetName);
	}

	//输出数据对象.
	public byte[] exportExcelByBillCellVO(BillCellVO _billCellVO, boolean _isConvertDouble, String sheetName) throws Exception {
		HSSFWorkbook book = exportExcelByBillCellVOAsBook(_billCellVO, _isConvertDouble, sheetName);
		return convertBookToBytes(book); //
	}

	//将一个BillCellVO导出.
	public HSSFWorkbook exportExcelByBillCellVOAsBook(BillCellVO _billCellVO, boolean _isConvertDouble, String sheetName) throws Exception {
		int li_rowCount = _billCellVO.getRowlength(); //几行
		int li_colCount = _billCellVO.getCollength(); //几列
		BillCellItemVO[][] itemVOs = _billCellVO.getCellItemVOs(); //

		// 函数内变量初始化
		HSSFWorkbook book = new HSSFWorkbook();// 创建工作簿
		if (StringUtils.isEmpty(sheetName)) {
			sheetName = "sheet1";
		}
		HSSFSheet sheet = book.createSheet(sheetName);// 创建sheet页签
		sheet.setActive(true);//取消受保护的视图

		//用于创建图片的根
		HSSFPatriarch drawRoot = null; //

		//风格本身通过Map实现重用,字体与颜色也是通过Map实现重用
		//即实现了自定义颜色的清准,也解决了字体过多的报错,同时也解决了style太多文件过大的问题
		//总之,这才是一个完美的将BillCellVO导出成Excel的工具方法,以前多个人写的方法都有各种瑕疵,不是最终完美的方法

		//存储自定义风格的Map,后来发现Style没必须一个格子搞一个,因为许多格子的风格其实一样,只要搞一个,这样Excel文件会小许多
		HashMap<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>(); //

		//存储自定义颜色的Map
		HSSFPalette palette = book.getCustomPalette(); //调色板
		int li_palette_begin = 8; //自定义调色板的起始号,只有8-64之间有效!!
		HashMap<String, Short> custColorMap = new HashMap<String, Short>(); //

		//存储自定义字体以前是一个格子创建一个字体，结果字体的数量太多后，Excel会报错!!! 所以相同的字段应该只搞一个!
		HashMap<String, HSSFFont> fontMap = new HashMap<String, HSSFFont>();

		//空格子的Style,因为遇到一些BillCellItem为null,以前什么都不处理,结果边框不对,所以需要创建一个空格了,只要边框为黑线就行
		HSSFCellStyle nullCellStyle = book.createCellStyle(); //创建一个新的风格
		setCellStyleBorder(nullCellStyle); //设置边框

		//遍历整个表格，取出数据
		for (int i = 0; i < li_rowCount; i++) { //遍历各行
			HSSFRow row = sheet.createRow(i); //创建一行
			for (int j = 0; j < li_colCount; j++) {
				BillCellItemVO itemVO = itemVOs[i][j]; //

				//有可能某处格子是被别人合并的,是null,所以跳过!
				if (itemVO == null) {
					HSSFCell nullCell = row.createCell(j);//创建单元格
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
				HSSFCell cell = row.createCell(j);//创建单元格

				//设置格子中的值!!
				String str_cellValue = itemVO.getCellvalue(); //格子中的值!
				String sr_cellType = itemVO.getCelltype(); //格子类型
				boolean isForceStr = itemVO.isForceStr(); //

				if (str_cellValue != null) {
					if ("IMAGE".equals(sr_cellType)) { //如果是图片!
						//cell.setCellValue("此处应是图片!"); //
						String str_64Code = itemVO.getCellimage(); //
						if (!YuFormatUtil.isEmpty(str_64Code)) {
							byte[] imageBys = bsUtil.convert64CodeToBytes(str_64Code); //转成Byte[]
							if (drawRoot == null) {
								drawRoot = sheet.createDrawingPatriarch(); //
							}
							HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 1023, 254, (short) j, (short) i, (short) j, (short) i); //short col1, int row1, short col2, int row2
							drawRoot.createPicture(anchor, book.addPicture(imageBys, HSSFWorkbook.PICTURE_TYPE_PNG)); //创建图片
						}
					} else {
						try { //如果是金额,则转金额试试
							if (_isConvertDouble && !isForceStr) { //如果转double,则进行转换
								double ld_cellValue = Double.parseDouble(str_cellValue);
								cell.setCellValue(ld_cellValue); //必须是Double，否则Excel每个格子左上角总有个错误提示，说数据格式不对!!!
							} else {
								cell.setCellType(HSSFCell.CELL_TYPE_STRING); //
								cell.setCellValue(str_cellValue); //所有的都是文本
							}
						} catch (Exception _ex) {
							cell.setCellValue(str_cellValue); //
						}
					}
				}

				//处理格子的风格，即字体,背景色,横向/纵向居中，这4种情况
				//处理字体与字的颜色!字体遇到一个问题，就是不能每个格子都创建一个字段,否则会太多而报错!
				String str_fontType = itemVO.getFonttype(); //字体
				str_fontType = (str_fontType == null ? "新宋体" : str_fontType);
				String str_fontSize = itemVO.getFontsize(); //
				str_fontSize = (str_fontSize == null ? "12" : str_fontSize);
				int li_fontSize = Integer.parseInt(str_fontSize); //
				String str_foreground = itemVO.getForeground(); //字的颜色,把RGB颜色转换成Excel的颜色!
				str_foreground = (str_foreground == null ? "" : str_foreground);

				String str_fontKey = str_fontType + "," + str_fontSize + "," + str_foreground; //

				//背景色
				String str_bgColor = itemVO.getBackground(); //
				str_bgColor = (str_bgColor == null ? "" : str_bgColor); //

				//横向纵高排列
				int li_halign = itemVO.getHalign();
				int li_valign = itemVO.getValign();

				//整个Style的key
				String str_styleKey = "[font]=[" + str_fontKey + "][bgcolor]=[" + str_bgColor + "][align]=[" + li_halign + "][valign]=[" + li_valign + "]";
				HSSFCellStyle cellStyle = null; //

				//以前是每个格子创建一个新的CellStyle对象，后来发现其实许多格子的style一样，完全可以共用一个,所以搞了个Map
				//根据风格的样式进行存储,然后实现重用!经过实验,一个50*20的Excel，在重用前生成Excel文件43.5K,重用后生成有Excel文件是13K，只有原来的30%的大小!
				if (styleMap.containsKey(str_styleKey)) {
					cellStyle = styleMap.get(str_styleKey); //
					cell.setCellStyle(cellStyle); //
				} else { //如果没有存储
					cellStyle = book.createCellStyle(); //创建一个新的风格
					cellStyle.setWrapText(true); //自动换行!
					setCellStyleBorder(cellStyle); //设置边框

					//字体Map存储的标记key.				
					if (fontMap.containsKey(str_fontKey)) {
						HSSFFont _font = fontMap.get(str_fontKey); //
						cellStyle.setFont(_font);
					} else {
						HSSFFont _font = book.createFont(); //创建字体!
						_font.setFontName(str_fontType);
						_font.setFontHeightInPoints((short) li_fontSize); //

						//如果定义了颜色!
						if (str_foreground != null && !str_foreground.equals("")) {
							if (custColorMap.containsKey(str_foreground)) {
								Short colorIndex = custColorMap.get(str_foreground);
								_font.setColor(colorIndex.shortValue());
							} else {
								li_palette_begin = li_palette_begin + 1; //创建新的索引号只有8-64之间有效!!! 其他的都是不准，但也不报错!
								String[] str_rgb = bsUtil.split(str_foreground, ",");
								int li_r = Integer.parseInt(str_rgb[0]);
								int li_g = Integer.parseInt(str_rgb[1]);
								int li_b = Integer.parseInt(str_rgb[2]);

								palette.setColorAtIndex((short) li_palette_begin, (byte) li_r, (byte) li_g, (byte) li_b); //新加一个调色
								_font.setColor((short) li_palette_begin);

								custColorMap.put(str_foreground, new Short((short) li_palette_begin)); //把颜色与调色序号存储起来
							}
						}

						cellStyle.setFont(_font);
						fontMap.put(str_fontKey, _font); //存起来,否则字体太多，会Excel报错，说是一个受保护的视图!!
					}

					//设置背景颜色
					if (str_bgColor != null && !str_bgColor.equals("")) {
						if (custColorMap.containsKey(str_bgColor)) {
							Short colorIndex = custColorMap.get(str_bgColor);
							cellStyle.setFillForegroundColor(colorIndex.shortValue()); //设置对应的颜色!!
							cellStyle.setFillPattern(FillPatternType.NO_FILL);//设置单元格填充样式
						} else {
							li_palette_begin = li_palette_begin + 1; //创建新的索引号只有8-64之间有效!!!
							String[] str_rgb = bsUtil.split(str_bgColor, ",");
							int li_r = Integer.parseInt(str_rgb[0]);
							int li_g = Integer.parseInt(str_rgb[1]);
							int li_b = Integer.parseInt(str_rgb[2]);

							palette.setColorAtIndex((short) li_palette_begin, (byte) li_r, (byte) li_g, (byte) li_b); //新加一个调色
							cellStyle.setFillForegroundColor((short) li_palette_begin); //设置对应的颜色!!
							cellStyle.setFillPattern(FillPatternType.NO_FILL);//设置单元格填充样式

							custColorMap.put(str_bgColor, new Short((short) li_palette_begin)); //把颜色与调色序号存储起来
						}
					}

					//横向,纵向排列
					setCellStyleHalign(cellStyle, li_halign); //
					setCellStyleValign(cellStyle, li_valign);

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

	public HSSFWorkbook exportExcelByBillCellVOAsBook1(List<BillCellVO> _billCellVOs, boolean _isConvertDouble, String sheetName) throws Exception {
		// 函数内变量初始化
		HSSFWorkbook book = new HSSFWorkbook();// 创建工作簿
		String[] strArray = sheetName.split(",");

		for (int k = 0; k < _billCellVOs.size(); k++) {
			int li_rowCount = _billCellVOs.get(k).getRowlength(); //几行
			int li_colCount = _billCellVOs.get(k).getCollength(); //几列
			BillCellItemVO[][] itemVOs = _billCellVOs.get(k).getCellItemVOs();

			HSSFSheet sheet = book.createSheet(strArray[k]);// 创建sheet页签
			sheet.setActive(true);//取消受保护的视图

			//用于创建图片的根
			HSSFPatriarch drawRoot = null;

			//风格本身通过Map实现重用,字体与颜色也是通过Map实现重用
			//即实现了自定义颜色的清准,也解决了字体过多的报错,同时也解决了style太多文件过大的问题
			//总之,这才是一个完美的将BillCellVO导出成Excel的工具方法,以前多个人写的方法都有各种瑕疵,不是最终完美的方法

			//存储自定义风格的Map,后来发现Style没必须一个格子搞一个,因为许多格子的风格其实一样,只要搞一个,这样Excel文件会小许多
			HashMap<String, HSSFCellStyle> styleMap = new HashMap<String, HSSFCellStyle>(); //

			//存储自定义颜色的Map
			HSSFPalette palette = book.getCustomPalette(); //调色板
			int li_palette_begin = 8; //自定义调色板的起始号,只有8-64之间有效!!
			HashMap<String, Short> custColorMap = new HashMap<String, Short>(); //

			//存储自定义字体以前是一个格子创建一个字体，结果字体的数量太多后，Excel会报错!!! 所以相同的字段应该只搞一个!
			HashMap<String, HSSFFont> fontMap = new HashMap<String, HSSFFont>();

			//空格子的Style,因为遇到一些BillCellItem为null,以前什么都不处理,结果边框不对,所以需要创建一个空格了,只要边框为黑线就行
			HSSFCellStyle nullCellStyle = book.createCellStyle(); //创建一个新的风格
			setCellStyleBorder(nullCellStyle); //设置边框

			//遍历整个表格，取出数据
			for (int i = 0; i < li_rowCount; i++) { //遍历各行
				HSSFRow row = sheet.createRow(i); //创建一行
				for (int j = 0; j < li_colCount; j++) {
					BillCellItemVO itemVO = itemVOs[i][j]; //

					//有可能某处格子是被别人合并的,是null,所以跳过!
					if (itemVO == null) {
						HSSFCell nullCell = row.createCell(j);//创建单元格
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
					HSSFCell cell = row.createCell(j);//创建单元格

					//设置格子中的值!!
					String str_cellValue = itemVO.getCellvalue(); //格子中的值!
					String sr_cellType = itemVO.getCelltype(); //格子类型
					boolean isForceStr = itemVO.isForceStr(); //

					if (str_cellValue != null) {
						if ("IMAGE".equals(sr_cellType)) { //如果是图片!
							//cell.setCellValue("此处应是图片!"); //
							String str_64Code = itemVO.getCellimage(); //
							if (!YuFormatUtil.isEmpty(str_64Code)) {
								byte[] imageBys = bsUtil.convert64CodeToBytes(str_64Code); //转成Byte[]
								if (drawRoot == null) {
									drawRoot = sheet.createDrawingPatriarch(); //
								}
								HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 1023, 254, (short) j, (short) i, (short) j, (short) i); //short col1, int row1, short col2, int row2
								drawRoot.createPicture(anchor, book.addPicture(imageBys, HSSFWorkbook.PICTURE_TYPE_PNG)); //创建图片
							}
						} else {
							try { //如果是金额,则转金额试试
								if (_isConvertDouble && !isForceStr) { //如果转double,则进行转换
									double ld_cellValue = Double.parseDouble(str_cellValue);
									cell.setCellValue(ld_cellValue); //必须是Double，否则Excel每个格子左上角总有个错误提示，说数据格式不对!!!
								} else {
									cell.setCellType(HSSFCell.CELL_TYPE_STRING); //
									cell.setCellValue(str_cellValue); //所有的都是文本
								}
							} catch (Exception _ex) {
								cell.setCellValue(str_cellValue); //
							}
						}
					}

					//处理格子的风格，即字体,背景色,横向/纵向居中，这4种情况
					//处理字体与字的颜色!字体遇到一个问题，就是不能每个格子都创建一个字段,否则会太多而报错!
					String str_fontType = itemVO.getFonttype(); //字体
					str_fontType = (str_fontType == null ? "新宋体" : str_fontType);
					String str_fontSize = itemVO.getFontsize(); //
					str_fontSize = (str_fontSize == null ? "12" : str_fontSize);
					int li_fontSize = Integer.parseInt(str_fontSize); //
					String str_foreground = itemVO.getForeground(); //字的颜色,把RGB颜色转换成Excel的颜色!
					str_foreground = (str_foreground == null ? "" : str_foreground);

					String str_fontKey = str_fontType + "," + str_fontSize + "," + str_foreground; //

					//背景色
					String str_bgColor = itemVO.getBackground(); //
					str_bgColor = (str_bgColor == null ? "" : str_bgColor); //

					//横向纵高排列
					int li_halign = itemVO.getHalign();
					int li_valign = itemVO.getValign();

					//整个Style的key
					String str_styleKey = "[font]=[" + str_fontKey + "][bgcolor]=[" + str_bgColor + "][align]=[" + li_halign + "][valign]=[" + li_valign + "]";
					HSSFCellStyle cellStyle = null; //

					//以前是每个格子创建一个新的CellStyle对象，后来发现其实许多格子的style一样，完全可以共用一个,所以搞了个Map
					//根据风格的样式进行存储,然后实现重用!经过实验,一个50*20的Excel，在重用前生成Excel文件43.5K,重用后生成有Excel文件是13K，只有原来的30%的大小!
					if (styleMap.containsKey(str_styleKey)) {
						cellStyle = styleMap.get(str_styleKey); //
						cell.setCellStyle(cellStyle); //
					} else { //如果没有存储
						cellStyle = book.createCellStyle(); //创建一个新的风格
						cellStyle.setWrapText(true); //自动换行!
						setCellStyleBorder(cellStyle); //设置边框

						//字体Map存储的标记key.				
						if (fontMap.containsKey(str_fontKey)) {
							HSSFFont _font = fontMap.get(str_fontKey); //
							cellStyle.setFont(_font);
						} else {
							HSSFFont _font = book.createFont(); //创建字体!
							_font.setFontName(str_fontType);
							_font.setFontHeightInPoints((short) li_fontSize); //

							//如果定义了颜色!
							if (str_foreground != null && !str_foreground.equals("")) {
								if (custColorMap.containsKey(str_foreground)) {
									Short colorIndex = custColorMap.get(str_foreground);
									_font.setColor(colorIndex.shortValue());
								} else {
									li_palette_begin = li_palette_begin + 1; //创建新的索引号只有8-64之间有效!!! 其他的都是不准，但也不报错!
									String[] str_rgb = bsUtil.split(str_foreground, ",");
									int li_r = Integer.parseInt(str_rgb[0]);
									int li_g = Integer.parseInt(str_rgb[1]);
									int li_b = Integer.parseInt(str_rgb[2]);

									palette.setColorAtIndex((short) li_palette_begin, (byte) li_r, (byte) li_g, (byte) li_b); //新加一个调色
									_font.setColor((short) li_palette_begin);

									custColorMap.put(str_foreground, new Short((short) li_palette_begin)); //把颜色与调色序号存储起来
								}
							}

							cellStyle.setFont(_font);
							fontMap.put(str_fontKey, _font); //存起来,否则字体太多，会Excel报错，说是一个受保护的视图!!
						}

						//设置背景颜色
						if (str_bgColor != null && !str_bgColor.equals("")) {
							if (custColorMap.containsKey(str_bgColor)) {
								Short colorIndex = custColorMap.get(str_bgColor);
								cellStyle.setFillForegroundColor(colorIndex.shortValue()); //设置对应的颜色!!
								cellStyle.setFillPattern(FillPatternType.NO_FILL);//设置单元格填充样式
							} else {
								li_palette_begin = li_palette_begin + 1; //创建新的索引号只有8-64之间有效!!!
								String[] str_rgb = bsUtil.split(str_bgColor, ",");
								int li_r = Integer.parseInt(str_rgb[0]);
								int li_g = Integer.parseInt(str_rgb[1]);
								int li_b = Integer.parseInt(str_rgb[2]);

								palette.setColorAtIndex((short) li_palette_begin, (byte) li_r, (byte) li_g, (byte) li_b); //新加一个调色
								cellStyle.setFillForegroundColor((short) li_palette_begin); //设置对应的颜色!!
								cellStyle.setFillPattern(FillPatternType.NO_FILL);//设置单元格填充样式

								custColorMap.put(str_bgColor, new Short((short) li_palette_begin)); //把颜色与调色序号存储起来
							}
						}

						//横向,纵向排列
						setCellStyleHalign(cellStyle, li_halign); //
						setCellStyleValign(cellStyle, li_valign);

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
		}

		return book;
	}

	//把Excel的Book对象转换成byte[]
	private byte[] convertBookToBytes(HSSFWorkbook _book) throws Exception {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); //
		_book.write(byteOut);
		byteOut.close(); //
		byte[] bytes = byteOut.toByteArray(); //
		return bytes;
	}

	//设置合并单元格!
	private void setSpan(HSSFWorkbook _book, HSSFSheet _sheet, int _row, int _col, String _span) {
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
	private void setCellStyleBorder(HSSFCellStyle _cellStyle) {
		_cellStyle.setBorderTop(BorderStyle.THIN);
		_cellStyle.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderRight(BorderStyle.THIN);
		_cellStyle.setRightBorderColor(HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderBottom(BorderStyle.THIN);
		_cellStyle.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());

		_cellStyle.setBorderLeft(BorderStyle.THIN);
		_cellStyle.setLeftBorderColor(HSSFColorPredefined.BLACK.getIndex());
	}

	//横向居左/居中/居右
	private void setCellStyleHalign(HSSFCellStyle cellStyle, int _align) {
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
	private void setCellStyleValign(HSSFCellStyle cellStyle, int _valign) {
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
}
