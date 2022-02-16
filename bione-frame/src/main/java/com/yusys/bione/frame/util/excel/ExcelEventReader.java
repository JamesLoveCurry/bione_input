package com.yusys.bione.frame.util.excel;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.utils.EhcacheUtils;

public class ExcelEventReader extends DefaultHandler implements Closeable {

	/**
	 * 单元格中的数据可能的数据类型
	 */
	enum CellDataType {
		BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
	}
	
	enum ParsingMode {
		NORMAL, SCAN_FOR_SPAN, GET_SHEET_HEAD_ONLY, GET_SHEET_DATA, GET_SHEET_JSON
	}

	private String filePath;

	private OPCPackage opcPackage;

	private XSSFReader xssfReader;

	private XMLReader parser;

	private ParsingMode parsingMode;

	/**
	 * 风格表
	 */
	private StylesTable stylesTable;

	/**
	 * 共享字符串表
	 */
	private SharedStringsTable sharedStringsTable;

	/**
	 * T元素标识
	 */
	private boolean isTElement;

	/**
	 * 单元格数据类型，默认为数字类型
	 */
	private CellDataType dataType = CellDataType.SSTINDEX;

	/**
	 * 单元格数字和日期格式的索引
	 */
	private short formatIndex;

	/**
	 * 数字和日期格式字符串
	 */
	private String formatString;

	private final DataFormatter dataFormatter = new DataFormatter();

	private StringBuilder elementBuffer = new StringBuilder();

	private StringBuilder sheetBuffer;

	private String sheetName;

	/**
	 * SPAN单元格列表，列表中单元格是"A18:C19"格式
	 */
	private List<String> spanRegionOriginalList = new ArrayList<String>();

	/**
	 * SPAN单元格列表，列表中数组内容是开始行索引、结束行索引、开始列索引、结束列索引
	 */
	private List<int[]> spanRegionList = new ArrayList<int[]>();

	/**
	 * 行数据缓存，列索引=>单元格值
	 */
	private Map<Integer, String> rowDataMap;

	/**
	 * sheet数据缓存，单元格号=>单元格值
	 */
	private Map<String, String> sheetDataMap;

	/**
	 * 行高缓存，行号=>行高值
	 */
	private Map<Integer, Long> rowHeightMap;

	/**
	 * 列宽缓存，列号=>列宽值
	 */
	private Map<Integer, Long> columnWidthMap;

	/**
	 * style的JSON描述=>新style名称
	 */
	private Map<String, Integer> styleJsonToNameMap;

	/**
	 * 原style名称=>新style名称
	 */
	private Map<Integer, Integer> styleNameOldToNewMap;

	/**
	 * style表缓存
	 */
	private StringBuilder nameStyleBuffer;

	/**
	 * 公式值缓存
	 */
	private String cellFormula;

	/**
	 * 单元格值缓存
	 */
	private String cellValue;

	/**
	 * 单元格编号缓存
	 */
	private String cellNo;
	
	/**
	 * 单元格共享数据缓存
	 */
	private String sharedStrings;
	
	/**
	 * 当前单元格是否包含公式
	 */
	private boolean haveFormula;

	/**
	 * sheet页大小
	 */
	private int[] sheetSize;

	/**
	 * GET_SHEET_HEAD_ONLY模式下读取sheet时，需要读取的行数
	 */
	private int readRowCount;
	
	/**
	 * excel公式集合<cellNo, cellFormula>
	 */
	private Map<String, String> sharedStringsMap = new HashMap<String, String>();

	private int[] rowColumnIdx = new int[2];

	private ExcelEventListener excelEventListener;
	
	//excel公式集合缓存Key
	private String EXCEL_FORMULA_KEY = "excel_formula_key";
	
	private void init(String filePath) throws IOException {
		if (! filePath.equals(this.filePath)) {
			close();
		}
		if (xssfReader == null) {
			File f = new File(filePath);
			try {
				parsingMode = ParsingMode.NORMAL;
				if (parser == null) {
					parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
					parser.setContentHandler(this);
				}
				opcPackage = OPCPackage.open(f, PackageAccess.READ);
				xssfReader = new XSSFReader(opcPackage);
				stylesTable = xssfReader.getStylesTable();
				sharedStringsTable = xssfReader.getSharedStringsTable();
				this.filePath = filePath;
			} catch (OpenXML4JException e) {
				throw new IOException(e);
			} catch (SAXException e) {
				throw new IOException(e);
			} finally {
				if (this.filePath == null) {
					close();
				}
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(opcPackage);
		opcPackage = null;
		xssfReader = null;
		stylesTable = null;
		sharedStringsTable = null;
		spanRegionOriginalList = null;
		rowDataMap = null;
		sheetDataMap = null;
		filePath = null;
		sharedStringsMap = null;
	}

	public int getSheetCount() throws IOException {
		if (xssfReader == null) {
			return 0;
		}
		int count = 0;
		InputStream in = null;
		try {
			XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
			while (sheets.hasNext()) {
				in = sheets.next();
				count ++;
				IOUtils.closeQuietly(in);
				in = null;
			}
		} catch (InvalidFormatException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return count;
	}

	private void startReadSheetImpl(String filePath, String sheetName) throws IOException, SAXException {
		init(filePath);
		if (xssfReader == null) {
			return;
		}
		InputStream in = null;
		try {
			XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
			while (sheets.hasNext()) {
				in = sheets.next();
				if (sheetName.equals(sheets.getSheetName())) {
					this.sheetName = sheetName;
					InputSource inputSource = new InputSource(in);
					parser.parse(inputSource);
				}
				IOUtils.closeQuietly(in);
				in = null;
			}
		} catch (InvalidFormatException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void startReadSheet(String filePath, String sheetName) throws IOException {
		parsingMode = ParsingMode.NORMAL;
		try {
			startReadSheetImpl(filePath, sheetName);
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

	/**
	 * 获取SPAN单元格列表，SPAN单元格是"A18:C19"格式
	 */
	public List<String> getSpanRegion(String filePath, String sheetName) throws IOException {
		parsingMode = ParsingMode.SCAN_FOR_SPAN;
		try {
			startReadSheetImpl(filePath, sheetName);
		} catch (ExcelEventStopParsingException e) {
			List<String> retList = spanRegionOriginalList;
			return retList;
		} catch (SAXException e) {
			throw new IOException(e);
		} finally {
			spanRegionOriginalList = null;
		}
		return null;
	}

	public void startReadSheet(String filePath, String sheetName, int count) throws IOException {
		parsingMode = ParsingMode.GET_SHEET_HEAD_ONLY;
		this.readRowCount = count;
		try {
			startReadSheetImpl(filePath, sheetName);
		} catch (ExcelEventStopParsingException e) {
		} catch (SAXException e) {
			throw new IOException(e);
		}
	}

	public Map<String, String> getSheetData(String filePath, String sheetName) throws IOException {
		parsingMode = ParsingMode.GET_SHEET_DATA;
		sheetDataMap = new HashMap<String, String>();
		try {
			startReadSheetImpl(filePath, sheetName);
			Map<String, String> retMap = sheetDataMap;
			return retMap;
		} catch (SAXException e) {
			throw new IOException(e);
		} finally {
			sheetDataMap = null;
		}
	}

	private static String encodeHex(int value) {
		StringBuilder buf = new StringBuilder(2);
		value &= 0x00FF;
		if (value < 0x10) {
			buf.append("0");
		}
		buf.append(Integer.toString(value, 16));
		return buf.toString();
	}
	
	/**
	 * 获取Color对象的RGB值
	 * 
	 * @param color
	 * @return
	 */
	private static String getColorRGB(XSSFColor color, HSSFColorPredefined defaultColor) {
		if (color != null) {
			byte[] rgb = color.getRGB();
			if (rgb != null) {
				return "#" + encodeHex(rgb[0]) + encodeHex(rgb[1]) + encodeHex(rgb[2]);
			}
		}
		short[] rgb = defaultColor.getTriplet();
		return "#" + encodeHex(rgb[0]) + encodeHex(rgb[1]) + encodeHex(rgb[2]);
	}

	private static Map<String, Object> getBorderInfo(BorderStyle borderStyle, XSSFColor color) {
		if (borderStyle == null || borderStyle.getCode() == BorderStyle.NONE.getCode()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		String colorRGB = getColorRGB(color, HSSFColorPredefined.BLACK);
		if (! "#000000".equals(colorRGB)) {
			map.put("color", colorRGB);
		}
		map.put("style", Short.valueOf(borderStyle.getCode()));
		return map;
	}

	private String getStyleJson(Integer styleIdx) {
		XSSFCellStyle cellStyle = stylesTable.getStyleAt(styleIdx.intValue());
		StringBuilder jsonBuffer = new StringBuilder();
		jsonBuffer.append("\"locked\":").append(cellStyle.getLocked());
		jsonBuffer.append(",\"wordWrap\":").append(cellStyle.getWrapText());
		XSSFFont font = stylesTable.getFontAt(cellStyle.getFontIndex());
		if (font != null) {
			jsonBuffer.append(",\"font\":\"");
			if (font.getItalic()) {
				jsonBuffer.append("italic ");
			}
			if (font.getBold()) {
				jsonBuffer.append("bold ");
			}
			jsonBuffer.append(font.getFontHeightInPoints()).append("pt ").append(font.getFontName()).append("\"");
			int underLineStrike = 0;
			if (font.getUnderline() == 1) {
				underLineStrike += 1;
			}
			if (font.getStrikeout()) {
				underLineStrike += 2;
			}
			if (underLineStrike > 0) {
				jsonBuffer.append(",\"textDecoration\":").append(underLineStrike);
			}
			String foreColor = getColorRGB(font.getXSSFColor(), HSSFColorPredefined.BLACK);
			if (! "#000000".equals(foreColor)) {
				jsonBuffer.append(",\"foreColor\":\"").append(foreColor).append('"');
			}
		}
		String formatString = cellStyle.getDataFormatString();
		if ((formatString != null) && (! formatString.equals("General")) && (formatString.indexOf("\\") < 0)) {
			jsonBuffer.append(",\"formatter\":\"").append(formatString).append('"');
		}
		if (cellStyle.getIndention() != 0) {
			jsonBuffer.append(",\"textIndent\":").append(cellStyle.getIndention());
		}
		switch (cellStyle.getAlignmentEnum()) {
		case LEFT:
			jsonBuffer.append(",\"hAlign\":").append(0);
			break;
		case CENTER:
			jsonBuffer.append(",\"hAlign\":").append(1);
			break;
		case RIGHT:
			jsonBuffer.append(",\"hAlign\":").append(2);
			break;
		default:
			break;
		}
		switch (cellStyle.getVerticalAlignmentEnum()) {
		case TOP:
			jsonBuffer.append(",\"vAlign\":").append(0);
			break;
		case CENTER:
			jsonBuffer.append(",\"vAlign\":").append(1);
			break;
		case BOTTOM:
			jsonBuffer.append(",\"vAlign\":").append(2);
			break;
		default:
			break;
		}
		String backColor = getColorRGB(cellStyle.getFillForegroundColorColor(), HSSFColorPredefined.WHITE);
		if (! "#ffffff".equals(backColor)) {
			jsonBuffer.append(",\"backColor\":\"").append(backColor).append('"');
		}
		Map<String, Object> borderInfo = getBorderInfo(cellStyle.getBorderLeftEnum(), cellStyle.getLeftBorderXSSFColor());
		if (borderInfo != null) {
			jsonBuffer.append(",\"borderLeft\":").append(JSON.toJSONString(borderInfo));
		}
		borderInfo = getBorderInfo(cellStyle.getBorderRightEnum(), cellStyle.getRightBorderXSSFColor());
		if (borderInfo != null) {
			jsonBuffer.append(",\"borderRight\":").append(JSON.toJSONString(borderInfo));
		}
		borderInfo = getBorderInfo(cellStyle.getBorderTopEnum(), cellStyle.getTopBorderXSSFColor());
		if (borderInfo != null) {
			jsonBuffer.append(",\"borderTop\":").append(JSON.toJSONString(borderInfo));
		}
		borderInfo = getBorderInfo(cellStyle.getBorderBottomEnum(), cellStyle.getBottomBorderXSSFColor());
		if (borderInfo != null) {
			jsonBuffer.append(",\"borderBottom\":").append(JSON.toJSONString(borderInfo));
		}
		return jsonBuffer.toString();
	}

	public String getSheetJson(String filePath, String sheetName) throws IOException {
		List<String> retSpanRegionList = getSpanRegion(filePath, sheetName);
		if(null != retSpanRegionList) {
			spanRegionList = new ArrayList<int[]>(retSpanRegionList.size());
			for (int i = 0; i < retSpanRegionList.size(); i ++) {
				String region = retSpanRegionList.get(i);
				int pos = region.indexOf(':');
				int[] regions = new int[4];
				int[] rowColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(region.substring(0, pos));
				regions[0] = rowColumnIdx[0];
				regions[2] = rowColumnIdx[1];
				rowColumnIdx = ExcelAnalyseUtils.getRowNoColumnIdx(region.substring(pos + 1));
				regions[1] = rowColumnIdx[0];
				regions[3] = rowColumnIdx[1];
				spanRegionList.add(regions);
			}
		}
		retSpanRegionList = null;

		parsingMode = ParsingMode.GET_SHEET_JSON;
		sheetBuffer = new StringBuilder();
		sheetSize = new int[2];
		sheetBuffer.append('{');
		sheetBuffer.append("\"version\":\"2.0\"");
		sheetBuffer.append(",\"useWijmoTheme\":true");
		sheetBuffer.append(",\"sheets\":{");
		sheetBuffer.append(JSON.toJSONString(sheetName)).append(":{");
		sheetBuffer.append("\"name\":").append(JSON.toJSONString(sheetName));
		sheetBuffer.append(",\"index\":0");
		sheetBuffer.append(",\"allowCellOverflow\":false");
		
		styleJsonToNameMap = new HashMap<String, Integer>();
		styleNameOldToNewMap = new HashMap<Integer, Integer>();
		nameStyleBuffer = new StringBuilder();
		try {
			startReadSheetImpl(filePath, sheetName);
		} catch (ExcelEventStopParsingException e) {
		} catch (SAXException e) {
			throw new IOException(e);
		}
		sheetBuffer.append(",\"rows\":[");
		for (int i = 1; i <= sheetSize[0]; i ++) {
			if (i > 1) {
				sheetBuffer.append(',');
			}
			Integer rowNo = Integer.valueOf(i);
			if (rowHeightMap.containsKey(rowNo)) {
				sheetBuffer.append("{\"size\":").append(rowHeightMap.get(rowNo)).append("}");
			} else {
				sheetBuffer.append("{}");
			}
		}
		sheetBuffer.append(']');
		sheetBuffer.append(",\"columns\":[");
		for (int i = 1; i <= sheetSize[1]; i ++) {
			if (i > 1) {
				sheetBuffer.append(',');
			}
			Integer columnNo = Integer.valueOf(i);
			if (columnWidthMap.containsKey(columnNo)) {
				sheetBuffer.append("{\"size\":").append(columnWidthMap.get(columnNo)).append("}");
			} else {
				sheetBuffer.append("{}");
			}
		}
		sheetBuffer.append(']');
		sheetBuffer.append(",\"spans\":[");
		for (int i = 0; i < spanRegionList.size(); i ++) {
			if (i > 0) {
				sheetBuffer.append(',');
			}
			int[] regions = spanRegionList.get(i);
			sheetBuffer.append("{\"row\":").append(regions[0]);
			sheetBuffer.append(",\"rowCount\":").append(regions[1] - regions[0] + 1);
			sheetBuffer.append(",\"col\":").append(regions[2]);
			sheetBuffer.append(",\"colCount\":").append(regions[3] - regions[2] + 1);
			sheetBuffer.append('}');
		}
		sheetBuffer.append(']');
		sheetBuffer.append("}}");
		sheetBuffer.append(",\"namedStyles\":[").append(nameStyleBuffer).append("]}");
		sheetSize = null;
		spanRegionList = null;
		rowHeightMap = null;
		columnWidthMap = null;
		styleJsonToNameMap = null;
		styleNameOldToNewMap = null;
		nameStyleBuffer = null;
		String ret = sheetBuffer.toString();
		sheetBuffer = null;
		return ret;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (parsingMode == ParsingMode.SCAN_FOR_SPAN) {
			if ("mergeCells".equals(qName)) {
				if (attributes.getValue("count") == null) {
					spanRegionOriginalList = new ArrayList<String>();
				} else {
					spanRegionOriginalList = new ArrayList<String>(Integer.parseInt(attributes.getValue("count")));
				}
			} else if ("mergeCell".equals(qName)) {
				spanRegionOriginalList.add(attributes.getValue("ref"));
			}
			return;
		}
		if ("c".equals(qName)) {
			// c => 单元格
			// 设定单元格类型
			setDataType(attributes);
		} else if ("f".equals(qName)) {
			haveFormula = true;
			if("shared".equals(attributes.getValue("t"))) {
				sharedStrings = attributes.getValue("ref");
			}
		}
		if ((parsingMode == ParsingMode.GET_SHEET_DATA || parsingMode == ParsingMode.GET_SHEET_JSON) && "dimension".equals(qName)) {
			String region = attributes.getValue("ref");
			int pos = region.indexOf(':');
			if (pos < 0) {
				throw new SAXException("dimension format error");
			}
			int[] retValues = ExcelAnalyseUtils.getRowNoColumnIdx(region.substring(pos + 1));
			if (retValues == null) {
				throw new SAXException("dimension format error");
			}
			if (parsingMode == ParsingMode.GET_SHEET_DATA) {
				sheetDataMap.put("rowCount", Integer.toString(retValues[0] + 1));
				sheetDataMap.put("columnCount", Integer.toString(retValues[1] + 1));
			} else {
				sheetSize[0] = retValues[0] + 1;
				sheetSize[1] = retValues[1] + 1;
				sheetBuffer.append(",\"rowCount\":").append(sheetSize[0]);
				sheetBuffer.append(",\"columnCount\":").append(sheetSize[1]);
			}
		} else if (parsingMode == ParsingMode.GET_SHEET_JSON) {
			if ("sheetFormatPr".equals(qName)) {
				sheetBuffer.append(",\"defaults\":{");
				String value = attributes.getValue("defaultColWidth");
				sheetBuffer.append("\"colWidth\":").append(
						StringUtils.isEmpty(value) ? 64 : Math.round(Double.parseDouble(value) * 8));
				value = attributes.getValue("defaultRowHeight");
				sheetBuffer.append(",\"rowHeight\":").append(
						StringUtils.isEmpty(value) ? 20 : Math.round(Double.parseDouble(value) / 72 * 96));
				value = attributes.getValue("defaultRowHeaderColWidth");
				sheetBuffer.append(",\"rowHeaderColWidth\":").append(
						StringUtils.isEmpty(value) ? 40 : Math.round(Double.parseDouble(value) * 8));
				value = attributes.getValue("defaultColHeaderRowHeight");
				sheetBuffer.append(",\"colHeaderRowHeight\":").append(
						StringUtils.isEmpty(value) ? 20 : Math.round(Double.parseDouble(value) / 72 * 96));
				sheetBuffer.append('}');
			} else if ("cols".equals(qName)) {
				columnWidthMap = new HashMap<Integer, Long>();
			} else if ("col".equals(qName)) {
				int from = Integer.parseInt(attributes.getValue("min"));
				int to = Integer.parseInt(attributes.getValue("max"));
				for (int i = from; i <= to; i ++) {
					columnWidthMap.put(Integer.valueOf(i), Long.valueOf(Math.round(Double.parseDouble(attributes.getValue("width")) * 8)));
				}
			} else if ("sheetData".equals(qName)) {
				sheetBuffer.append(",\"data\":{");
				sheetBuffer.append("\"dataTable\":{");
				rowHeightMap = new HashMap<Integer, Long>();
			} else if ("row".equals(qName)) {
				if (sheetBuffer.charAt(sheetBuffer.length() - 1) == '}') {
					sheetBuffer.append(',');
				}
				sheetBuffer.append('"').append(Integer.parseInt(attributes.getValue("r")) - 1).append("\":{");
				if (StringUtils.isNotEmpty(attributes.getValue("ht"))) {
					rowHeightMap.put(Integer.valueOf(Integer.parseInt(attributes.getValue("r"))),
							Long.valueOf(Math.round(Double.parseDouble(attributes.getValue("ht")) / 72 * 96)));
				}
			} else if ("c".equals(qName) && dataType != CellDataType.NULL) {
				// c => 单元格
				if (sheetBuffer.charAt(sheetBuffer.length() - 1) == '}') {
					sheetBuffer.append(',');
				}
				sheetBuffer.append('"').append(rowColumnIdx[1]).append("\":{");
				if (StringUtils.isNotEmpty(attributes.getValue("s"))) {
					Integer styleName = Integer.valueOf(Integer.parseInt(attributes.getValue("s")));
					Integer newStyleName = styleNameOldToNewMap.get(styleName);
					if (newStyleName == null) {
						String styleJson = getStyleJson(styleName);
						newStyleName = styleJsonToNameMap.get(styleJson);
						if (newStyleName == null) {
							newStyleName = Integer.valueOf(styleJsonToNameMap.size() + 1);
							styleJsonToNameMap.put(styleJson, newStyleName);
							if (nameStyleBuffer.length() > 0) {
								nameStyleBuffer.append(',');
							}
							nameStyleBuffer.append('{');
							nameStyleBuffer.append("\"name\":\"").append(newStyleName.intValue()).append("\",");
							nameStyleBuffer.append(styleJson).append('}');
						}
						styleNameOldToNewMap.put(styleName, newStyleName);
					}
					sheetBuffer.append("\"style\":\"").append(newStyleName.intValue()).append('"');
				}
			} else if ("t".equals(qName)) {
				// 当元素为t时
				isTElement = true;
			}
		} else if ("row".equals(qName)) {
			rowDataMap = new HashMap<Integer, String>();
		} else if ("t".equals(qName)) {
			// 当元素为t时
			isTElement = true;
		} else if ("cols".equals(qName)) {
			columnWidthMap = new HashMap<Integer, Long>();
		} else if ("col".equals(qName)) {
			int from = Integer.parseInt(attributes.getValue("min"));
			int to = Integer.parseInt(attributes.getValue("max"));
			for (int i = from; i <= to; i ++) {
				columnWidthMap.put(Integer.valueOf(i), Long.valueOf(Math.round(Double.parseDouble(attributes.getValue("width")) * 8)));
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (parsingMode == ParsingMode.SCAN_FOR_SPAN) {
			return;
		}
		elementBuffer.append(ch, start, length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		sharedStringsMap = (Map<String, String>) EhcacheUtils.get(EXCEL_FORMULA_KEY, EXCEL_FORMULA_KEY);
		switch (parsingMode) {
		case SCAN_FOR_SPAN:
			if ("mergeCells".equals(qName)) {
				throw new ExcelEventStopParsingException();
			}
			return;
		case GET_SHEET_DATA:
			if ("f".equals(qName)) {
				cellFormula = elementBuffer.toString();
				//除去excel公式校验，这块构造逻辑始终会有缺陷，就不要了，改成从缓存里获取公式
/*				if(StringUtils.isNotBlank(sharedStrings)) {//单元格共享数据缓存不为空，需要构造公式
					strSharedFormat(cellNo, sharedStrings, cellFormula);
					sharedStrings = null;
				}
				if(StringUtils.isBlank(cellFormula)) {//公式为空，可能是公式存在共享变量区域
					if(null != sharedStringsMap) {
						cellFormula = sharedStringsMap.get(cellNo);
					}
				}*/
				if(StringUtils.isBlank(cellFormula)) {//公式为空，需要从缓存里获取
					Map<String, String> cellMap = this.analysisCell(cellNo);
					int rowNo = Integer.parseInt(cellMap.get("rowNo"))-1;
					int colNo = excelColStrToNum(cellNo.replaceAll("[^a-z^A-Z]", ""))-1;
					cellFormula = sharedStringsMap.get(rowNo + ","+ colNo);
				}
				elementBuffer.setLength(0);
				cellNo = null;
			} else if ("v".equals(qName)) {
				// v => 单元格的值，如果单元格是字符串，则v标签的值为该字符串在SST中的索引
				cellValue = getDataValue(elementBuffer.toString());
				elementBuffer.setLength(0);
			} else if ("c".equals(qName) && dataType != CellDataType.NULL) {
				String value;
				if (! haveFormula && dataType != CellDataType.FORMULA) {
					value = StringUtils.defaultString(cellValue);
				} else {
					value = StringUtils.defaultString(cellFormula);
					if (StringUtils.isNotEmpty(value)) {
						value = "=" + value;
					}
				}
				sheetDataMap.put(ExcelAnalyseUtils.toABC(rowColumnIdx[0], rowColumnIdx[1]), value);
				cellFormula = null;
				cellValue = null;
				haveFormula = false;
				elementBuffer.setLength(0);
			} else if (isTElement) {
				// t元素也包含字符串
				sheetDataMap.put(ExcelAnalyseUtils.toABC(rowColumnIdx[0], rowColumnIdx[1]), elementBuffer.toString());
				elementBuffer.setLength(0);
				isTElement = false;
			}
			break;
		case GET_SHEET_JSON:
			if ("sheetData".equals(qName)) {
				sheetBuffer.append("}}");
			} else if ("row".equals(qName)) {
				sheetBuffer.append('}');
			} else if ("f".equals(qName)) {
				cellFormula = elementBuffer.toString();
				if(StringUtils.isBlank(cellFormula)) {//公式为空，可能是公式存在共享变量区域
					if(null != sharedStringsMap) {
						cellFormula = sharedStringsMap.get(cellNo);
					}
				}
				elementBuffer.setLength(0);
			} else if ("v".equals(qName)) {
				// v => 单元格的值，如果单元格是字符串，则v标签的值为该字符串在SST中的索引
				cellValue = getDataValue(elementBuffer.toString());
				elementBuffer.setLength(0);
			} else if ("c".equals(qName) && dataType != CellDataType.NULL) {
				if (sheetBuffer.charAt(sheetBuffer.length() - 1) != '{') {
					sheetBuffer.append(',');
				}
				if (haveFormula || dataType == CellDataType.FORMULA) {
					sheetBuffer.append("\"value\":");
					if (StringUtils.isEmpty(cellValue)) {
						sheetBuffer.append('0');
					} else if (NumberUtils.isNumber(cellValue) || dataType == CellDataType.BOOL) {
						sheetBuffer.append("\"" + cellValue + "\"");
					} else {
						sheetBuffer.append(JSON.toJSONString(cellValue));
					}
					String formula = JSON.toJSONString(StringUtils.defaultString(cellFormula));
					sheetBuffer.append(",\"formula\":").append(formula);
				} else if (dataType == CellDataType.NUMBER) {
					sheetBuffer.append("\"value\":").append(StringUtils.isEmpty(cellValue) ? "\"\"" : "\"" +  cellValue + "\"");
				} else {
					sheetBuffer.append("\"value\":").append(JSON.toJSONString(StringUtils.defaultString(cellValue)));
				}
				sheetBuffer.append('}');
				cellFormula = null;
				cellValue = null;
				haveFormula = false;
				elementBuffer.setLength(0);
			} else if (isTElement) {
				if (elementBuffer.length() > 0) {
					if (sheetBuffer.charAt(sheetBuffer.length() - 1) != '{') {
						sheetBuffer.append(',');
					}
					sheetBuffer.append("\"value\":").append(JSON.toJSONString(elementBuffer.toString()));
					elementBuffer.setLength(0);
				}
				isTElement = false;
			}
			break;
		default:
			if ("v".equals(qName)) {
				// v => 单元格的值，如果单元格是字符串，则v标签的值为该字符串在SST中的索引
				rowDataMap.put(Integer.valueOf(rowColumnIdx[1]), getDataValue(elementBuffer.toString()));
				elementBuffer.setLength(0);
			} else if (isTElement) {
				// t元素也包含字符串
				rowDataMap.put(Integer.valueOf(rowColumnIdx[1]), elementBuffer.toString());
				elementBuffer.setLength(0);
				isTElement = false;
			} else if ("row".equals(qName)) {
				// 如果标签名称为row，这说明已到行尾
				if (excelEventListener != null) {
					excelEventListener.rowDataReady(this, rowColumnIdx[0], rowDataMap);
				}
				rowDataMap = null;
				if (parsingMode == ParsingMode.GET_SHEET_HEAD_ONLY && rowColumnIdx[0] == readRowCount - 1) {
					throw new ExcelEventStopParsingException();
				}
			}
			break;
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		if (parsingMode == ParsingMode.GET_SHEET_DATA) {
			if (! sheetDataMap.containsKey("rowCount")) {
				throw new SAXException("dimension format error");
			}
		} else if (parsingMode == ParsingMode.NORMAL) {
			excelEventListener.rowDataReady(this, 0, null);
		}
	}

	/**
	 * 设置单元格数据类型和单元格位置
	 * 
	 * @param attributes XML属性
	 */
	private void setDataType(Attributes attributes) {
		cellNo = attributes.getValue("r");
		ExcelAnalyseUtils.getRowNoColumnIdx(attributes.getValue("r"), rowColumnIdx);
		if (parsingMode == ParsingMode.GET_SHEET_JSON) {
			for (int i = 0; i < spanRegionList.size(); i ++) {
				int[] regions = spanRegionList.get(i);
				if (rowColumnIdx[0] >= regions[0] && rowColumnIdx[0] <= regions[1] &&
					rowColumnIdx[1] >= regions[2] && rowColumnIdx[1] <= regions[3] &&
					(rowColumnIdx[0] > regions[0] || rowColumnIdx[1] > regions[2])) {
					dataType = CellDataType.NULL;
					return;
				}
			}
		}
		dataType = CellDataType.NUMBER;
		formatIndex = -1;
		formatString = null;
		String cellType = attributes.getValue("t"); //单元格类型

		if ("b".equals(cellType)) {
			//处理布尔值
			dataType = CellDataType.BOOL;
		} else if ("e".equals(cellType)) {
			// 处理错误
			dataType = CellDataType.ERROR;
		} else if ("inlineStr".equals(cellType)) {
			dataType = CellDataType.INLINESTR;
		} else if ("s".equals(cellType)) {
			// 处理字符串
			dataType = CellDataType.SSTINDEX;
		} else if ("str".equals(cellType)) {
			dataType = CellDataType.FORMULA;
		} else {
			dataType = CellDataType.NUMBER;
		}
		
		String cellStyleStr = attributes.getValue("s");
		if (cellStyleStr != null) {
			// 处理日期
			int styleIndex = Integer.parseInt(cellStyleStr);
			XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
			formatString = style.getDataFormatString();
			if (formatString == null) {
				formatIndex = style.getDataFormat();
				formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
			} else if (formatString.contains("m/d/yy")) {
				dataType = CellDataType.DATE;
				formatString = "yyyy-MM-dd hh:mm:ss";
			}
		}
	}

	/**
	 * 根据共享变量构造excel公式
	 * @param deginCellNo
	 * @param sharedFormat
	 * @param cellFormula
	 */
	private void strSharedFormat(String deginCellNo, String sharedFormat, String cellFormula) {
		String[] sharedFormats = sharedFormat.split(":");
		if(sharedFormats.length == 2) {
			String endCell = sharedFormats[1];
			Map<String, String> deginCellMap = this.analysisCell(deginCellNo);
			String deginRowNo = deginCellMap.get("rowNo");//数字
			String deginColNo = deginCellMap.get("colNo");//字母
			Map<String, String> endCellMap = this.analysisCell(endCell);
			String endRowNo = endCellMap.get("rowNo");
			String endColNo = endCellMap.get("colNo");
			if(StringUtils.isNotBlank(deginRowNo) && StringUtils.isNotBlank(deginColNo) && StringUtils.isNotBlank(endRowNo) && StringUtils.isNotBlank(endColNo)) {
				if(deginRowNo.equals(endRowNo)) {//纵向扩展
					int deginColNoInt = excelColStrToNum(deginColNo.replaceAll("[^a-z^A-Z]", ""));
					int endColNoInt = excelColStrToNum(endColNo.replaceAll("[^a-z^A-Z]", ""));
					String newCellFormula = cellFormula;
					for(int i = deginColNoInt; i <= endColNoInt; i++) {
						String deginColNoStr = excelColIndexToStr(deginColNoInt);
						String iStr = excelColIndexToStr(i);
						if(cellFormula.indexOf(deginColNoStr) >= 0) {//证明要扩展的公式在本单元格下方
							newCellFormula = cellFormula.replaceAll(deginColNoStr + "", iStr + "");
						}else {//要扩展的公式不在本单元格下方，目前就按照原公式延后一格进行处理
							//先构造列头公式
							String cellFormulaSub = newCellFormula;//公式中间变量
							cellFormulaSub = cellFormulaSub.replaceAll("=", "");//去除等号
							String[] cellFormulas = cellFormulaSub.split(deginRowNo);//用行号去做分割
							if(cellFormulas.length > 1) {//证明有运算符
								String operator = cellFormulas[1].substring(0, 1);//找到运算符
								try {
									cellFormulaSub = cellFormulaSub.replaceAll(operator, "");//去除运算符
								} catch (Exception e) {
									cellFormulaSub = cellFormulaSub.replaceAll('\\' + operator, "");//转义一下去除运算符
								}
								cellFormulas = cellFormulaSub.split(deginRowNo);//用行号去做分割（现在得到的就是列头了）
								String[] cellFormulasSub = cellFormulaSub.split(deginRowNo);//用行号去做分割（现在得到的就是列头了）
								if(i != deginColNoInt) {//第一次不做替换
									for(int num = 0; num < cellFormulasSub.length; num++) {
										char ColNo = cellFormulasSub[num].charAt(0);
										ColNo++;
										cellFormulasSub[num] = ColNo + "";
										newCellFormula = newCellFormula.replaceAll(cellFormulas[num], cellFormulasSub[num]);
									}
								}
							}
						}
						if(null == sharedStringsMap) {
							sharedStringsMap = new HashMap<String, String>();
						}
						sharedStringsMap.put(iStr + deginRowNo, newCellFormula);
					}
				}else if(deginColNo.equals(endColNo)) {//横向扩展
					int deginRowNoNum = Integer.parseInt(deginRowNo);
					int endRowNoNum = Integer.parseInt(endRowNo);
					for(int i = deginRowNoNum; i <= endRowNoNum; i++) {
						String newCellFormula = cellFormula.replaceAll(deginRowNoNum + "", i + "");
						if(null == sharedStringsMap) {
							sharedStringsMap = new HashMap<String, String>();
						}
						sharedStringsMap.put(deginColNo + i, newCellFormula);
					}
				}else {//这种情况比较特殊，属于双向扩展(R7:S12)，目前先这样处理
					//先构造列头公式
					char deginColNoChar = deginColNo.charAt(0);
					char endColNoChar = endColNo.charAt(0);
					String cellFormulaSub = cellFormula;//公式中间变量
					cellFormulaSub = cellFormulaSub.replaceAll("=", "");//去除等号
					String[] cellFormulas = cellFormulaSub.split(deginRowNo);//用行号去做分割
					if(cellFormulas.length > 1) {//证明有运算符
						String operator = cellFormulas[1].substring(0, 1);//找到运算符
						try {
							cellFormulaSub = cellFormulaSub.replaceAll(operator, "");//去除运算符
						} catch (Exception e) {
							cellFormulaSub = cellFormulaSub.replaceAll('\\' + operator, "");//转义一下去除运算符
						}
					}
					cellFormulas = cellFormulaSub.split(deginRowNo);//用行号去做分割（现在得到的就是列头了）
					String[] cellFormulasSub = cellFormulaSub.split(deginRowNo);//用行号去做分割（现在得到的就是列头了）
					if(null == sharedStringsMap) {
						sharedStringsMap = new HashMap<String, String>();
					}
					sharedStringsMap.put(deginCellNo, cellFormula);
					for(char i = ++deginColNoChar; i <= endColNoChar; i++) {
						String newCellFormula = cellFormula;
						for(int num = 0; num < cellFormulasSub.length; num++) {
							char ColNo = cellFormulasSub[num].charAt(0);
							ColNo++;
							cellFormulasSub[num] = ColNo + "";
							newCellFormula = newCellFormula.replaceAll(cellFormulas[num], cellFormulasSub[num]);
						}
						sharedStringsMap.put(i + deginRowNo, newCellFormula);
					}
					//最后进行公式构造
					deginColNoChar = deginColNo.charAt(0);
					for(char i = deginColNoChar; i <= endColNoChar; i++) {
						int deginRowNoNum = Integer.parseInt(deginRowNo);
						int endRowNoNum = Integer.parseInt(endRowNo);
						for(int j = deginRowNoNum; j <= endRowNoNum; j++) {
							cellFormula = sharedStringsMap.get(i + "" + deginRowNoNum);//找到列头公式
							String newCellFormula = cellFormula.replaceAll(deginRowNoNum + "", j + "");
							sharedStringsMap.put(i + "" + j, newCellFormula);
						}
					}
				}
			}
		}
	}
	
	private int excelColStrToNum(String column) {
        int num = 0;
        int result = 0;
        int length =column.length(); 
        for(int i = 0; i < length; i++) {
            char ch = column.charAt(length - i - 1);
            num = (int)(ch - 'A' + 1) ;
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }
	
	private String excelColIndexToStr(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }
	
	/**
	 * 解析单元格信息
	 * @param cellNo
	 * @return
	 */
	private Map<String, String> analysisCell(String cellNo) {
		Map<String, String> cellMap = new HashMap<String, String>();
		if(StringUtils.isNotBlank(cellNo)) {
			cellNo = cellNo.trim();
			String rowNo = "";
			String colNo = "";
			if(cellNo != null && !"".equals(cellNo)){
				for(int i=0;i<cellNo.length();i++){
					if(cellNo.charAt(i)>=48 && cellNo.charAt(i)<=57){
						rowNo += cellNo.charAt(i);
					}else {
						colNo += cellNo.charAt(i);
					}
				}
			}
			cellMap.put("rowNo", rowNo);
			cellMap.put("colNo", colNo);
		}
		return cellMap;
	}
	
	private String getDataValue(String value) {
		if (StringUtils.isEmpty(value)) {
			return "";
		}
		switch (dataType) {
		case BOOL:
			char first = value.charAt(0);
			return first == '0' ? "false" : "true";
		case ERROR:
			return "";
		case FORMULA:
			return value;
		case INLINESTR:
			XSSFRichTextString rts = new XSSFRichTextString(value);
			return rts.toString();
		case SSTINDEX:
			try {
				int idx = Integer.parseInt(value);
				rts = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
				return rts.toString();
			} catch (NumberFormatException e) {
				return value;
			}
		case NUMBER:
			if (formatString != null) {
				value = dataFormatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
			}
			// return StringUtils.remove(value, '_');
			return value;
		case DATE:
			value = dataFormatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
			// 对日期字符串作特殊处理，去掉T
			// return value.replace('T', ' ');
			return value;
		default:
			return null;
		}
	}

	public void setExcelEventListener(ExcelEventListener excelEventListener) {
		this.excelEventListener = excelEventListener;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getSheetName() {
		return sheetName;
	}
}
