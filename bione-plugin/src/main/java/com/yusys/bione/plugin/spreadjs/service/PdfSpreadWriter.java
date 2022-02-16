/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.yusys.bione.plugin.spreadjs.entity.DataTablePropertyProperty;
import com.yusys.bione.plugin.spreadjs.entity.SheetSize;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.Span;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;
import com.yusys.bione.plugin.spreadjs.entity.Style;
import com.yusys.bione.plugin.spreadjs.htz.core.ChineseFont;
import com.yusys.bione.plugin.spreadjs.htz.pagesetting.PageSetting;
import com.yusys.bione.plugin.spreadjs.htz.pdfevent.PageEvent;
import com.yusys.bione.plugin.spreadjs.htz.pdfpag.PdfPageSize;

/**
 * @author tanxu
 * 
 */
public class PdfSpreadWriter implements ISpreadWriter {

	private Integer size = 50;

	/** document属性 */
	protected Document document = null;
	/** 设置pdf页面大小、默认为A4 */
	public Rectangle pageSize = PdfPageSize.A4;

	public PageEvent event = new PageEvent();
	/** pdf输出文件 */
	protected File pdfFile = null;

	protected Map<String, Style> stylesMapping = new HashMap<String, Style>();

	protected SpreadSchema createSpreadSchema(String jsonString)
			throws SpreadExportException {
		return JSON.parseObject(jsonString, SpreadSchema.class);
	}

	public PdfSpreadWriter(File file, Rectangle pageSize,
			PageSetting pageSetting, Integer size) throws FileNotFoundException {
		pdfFile = file;
		document = new Document(null, 30, 30, 25, 25);
		this.pageSize = pageSize;
		event.setFooter(pageSetting.getFooter());
		event.setHeader(pageSetting.getHeader());
		event.setPageNumberSize(pageSetting.getFooter().getFontSize());
		event.setPageNumberStyle(pageSetting.getFooter().getPageNumberStyle());
		if (size != null) {
			this.size = size;
		}
	}

	private void createStyle(SpreadSchema spreadSchema, SheetsProperty sp) {
		stylesMapping = new HashMap<String, Style>();
		if (spreadSchema.getNamedStyles() != null
				&& spreadSchema.getNamedStyles().size() > 0)
			for (Style style : spreadSchema.getNamedStyles()) {
				stylesMapping.put(style.getName(), style);
			}
		if (sp.getNamedStyles() != null && sp.getNamedStyles().size() > 0)
			for (Style style : sp.getNamedStyles()) {
				stylesMapping.put(style.getName(), style);
			}
	}

	@Override
	public void write(String jsonString,String templatePath, List<Map<String, Object>> cellSpanList) throws Exception {
		// TODO Auto-generated method stub
		SpreadSchema spreadSchema = createSpreadSchema(jsonString);
		OutputStream output = null;
		try {
			output = new FileOutputStream(pdfFile);
			PdfWriter writer = PdfWriter.getInstance(document, output);
			writer.setPageEvent(event);// 设置页面事件
			document.setPageSize(pageSize);// 设置pdf页面的大小
			document.open();// 打开document对象
			Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
			//构造合并单元格信息
			List<Span> spans = new ArrayList<Span>();
			if(null != cellSpanList && cellSpanList.size() > 0){
				for(Map<String, Object> map : cellSpanList){
					Span span = new Span();
					span.setCol((int)map.get("beginCol"));
					span.setRow((int)map.get("beginRow"));
					span.setColCount((int)map.get("colSpan"));
					span.setRowCount((int)map.get("rowSpan"));
					spans.add(span);
				}
			}
			// 添加所有的样式
			for (String sheetIndex : sps.keySet()) {
				SheetsProperty sp = sps.get(sheetIndex);
				createStyle(spreadSchema, sp);
				List<Map<String, Object>> columns = this.getSize(sp.getColumns(),
						sp.getDefaults().getColWidth());
				List<Map<String, Object>> rows = this.getSize(sp.getRows(), sp
						.getDefaults().getRowHeight());
				spans.addAll(sp.getSpans());
				sp.setSpans(spans);
				Map<String, String> spanMaps = this.getSpanInfo(sp.getSpans(),
						columns, rows);
				int rowNum = getNums(sp.getRowCount(), rows);
				int colNum = getNums(sp.getColumnCount(), columns);
				int rowActNum = sp.getRowCount();
				int colActNum = sp.getColumnCount();
				float[] colWidths = new float[colNum];
				for(int f=0;f<colWidths.length;f++){
					colWidths[f] = Float.parseFloat(sp.getDefaults().getColWidth().toString());
				}
				int cw = 0;
				for (int c = 0; c < columns.size(); c++) {
					if (!columns.get(c).get("visible").equals(false)) {
						if (columns.get(c).get("size") != null){
							colWidths[cw] = Float.parseFloat(columns.get(c)
									.get("size").toString());
						}
						cw++;
					}
				}
				int n = (rowActNum - 1) / size + 1;
				for (int k = 0; k < n; k++) {
					PdfPTable table = null;
					table = new PdfPTable(colNum);// 创建含有cols列的表格
					table.setWidths(colWidths);
					int rowEnd = ((k + 1) * size <= rowActNum) ? (k + 1) * size
							: rowNum;
					for (int i = k * size; i < rowEnd; i++) {
						if (i < rows.size()
								&& rows.get(i).get("visible").equals(false))
							continue;
						for (int j = 0; j < colActNum; j++) {
							if (j < columns.size()
									&& columns.get(j).get("visible").equals(false))
								continue;
							if (spanMaps.get(i + "," + j) != null) {
								PdfPCell pcell = new PdfPCell();
								Phrase phrase = null;
								DataTablePropertyProperty dpp = this.getCell(sp, i,
										j);
								if (dpp != null) {
									String content = dpp.getValue().toString();
									boolean chineseFlag = Pattern
											.compile("[\u0391-\uFFE5]+")
											.matcher(content).find();
									Style style = getCellStyle(dpp);
									phrase = new Phrase(content, setFont(style,
											chineseFlag));
									pcell.addElement(phrase);

									setStyle(sp, pcell, j, i, style);

								}
								
								String info[] = StringUtils.split(
										spanMaps.get(i + "," + j), ",");
								pcell.setRowspan(Integer.parseInt(info[0]));
								pcell.setColspan(Integer.parseInt(info[1]));
								table.addCell(pcell);

							} else {
								PdfPCell pcell = new PdfPCell();
								Phrase phrase = null;
								DataTablePropertyProperty dpp = this.getCell(sp, i,
										j);
								if (dpp != null) {
									String content = String
											.valueOf(dpp.getValue() != null ? dpp
													.getValue() : "");
									boolean chineseFlag = Pattern
											.compile("[\u0391-\uFFE5]+")
											.matcher(content).find();
									Style style = getCellStyle(dpp);
									phrase = new Phrase(content, setFont(style,
											chineseFlag));
									pcell.addElement(phrase);
									setStyle(sp, pcell, j, i, style);
								}
								table.addCell(pcell);
							}
						}
					}
					document.add(table);
					if(k<n-1)
						document.newPage();
				}
				//document.newPage();
			}
		} catch (DocumentException e) {
			System.err.println("-->创建Document对象失败!");
			e.printStackTrace();
		} finally {
			this.destory();
			IOUtils.closeQuietly(output);
		}
	}

	public int getNums(int num, List<Map<String, Object>> sizes) {
		for (int i = 0; i < num; i++) {
			if (i < sizes.size() && sizes.get(i).get("visible").equals(false))
				num--;
		}
		return num;
	}

	public DataTablePropertyProperty getCell(SheetsProperty sp, int row, int col) {
		try {
			return (DataTablePropertyProperty) sp.getData().getDataTable()
					.getAdditionalProperties().get(String.valueOf(row))
					.getAdditionalProperties().get(String.valueOf(col));
		} catch (Exception e) {
			return null;
		}

	}

	public Style getCellStyle(DataTablePropertyProperty dpp) {
		try {
			Style style = null;
			if (dpp.getStyle() instanceof String) {
				style = this.stylesMapping.get(StringUtils.remove(dpp.getStyle().toString(), '"'));
			}
			if (dpp.getStyle() instanceof Style) {
				style = (Style) dpp.getStyle();
			}
			return style;
		} catch (Exception e) {
			return null;
		}

	}

	public List<Map<String, Object>> getSize(List<SheetSize> columns,
			double defaultInfo) {
		List<Map<String, Object>> cols = new ArrayList<Map<String, Object>>();
		if (columns != null) {
			for (int i = 0; i < columns.size(); i++) {
				Map<String, Object> column = new HashMap<String, Object>();
				SheetSize size = columns.get(i);

				column.put("size", defaultInfo);
				column.put("visible", true);
				if (size != null) {
					if (size.getVisible() != null && ! size.getVisible()) {
						column.put("visible", false);
					}
					if (size.getSize() != null)
						column.put("size", size.getSize());
				}
				cols.add(column);

			}
		}
		return cols;
	}

	public Map<String, String> getSpanInfo(List<Span> spans,
			List<Map<String, Object>> columns, List<Map<String, Object>> rows) {
		Map<String, String> spanMaps = new HashMap<String, String>();
		for (Span span : spans) {
			spanMaps.put(
					span.getRow() + "," + span.getCol(),
					getCount(span.getRow(), span.getRowCount(), rows)
							+ ","
							+ getCount(span.getCol(), span.getColCount(),
									columns));
		}
		return spanMaps;
	}

	public int getCount(int start, int count, List<Map<String, Object>> columns) {
		for (int i = 0; i < count; i++) {
			if (columns.get(start + i).get("visible").equals(false))
				count--;
		}
		return count;
	}

	@Override
	public void destory() {
		if (document != null && document.isOpen()) {
			document.close();
		}
		document = null;
	}

	@Override
	public void write(List<String> jsonString,
			List<Map<String, String>> rptInfos) throws SpreadExportException {
		// TODO Auto-generated method stub

	}

	protected Font setFont(Style style, boolean chineseFlag) {
		int fontStyle = Font.NORMAL;
		Font font = null;
		BaseColor fontColor = BaseColor.BLACK;
		Short fontHeight = null;
		String fontName = null;
		FontFamily fontFamily = FontFamily.TIMES_ROMAN;
		int fontSize = 10;
		Boolean isItalic = false, isBold = false, isStrikeout = null;
		Byte underline = null;
		if (style == null) {
			return new Font(fontFamily, fontSize, fontStyle,
					fontColor);
		}
		if (StringUtils.isNotBlank(style.getForeColor())) {
			fontColor = convertColour(style.getForeColor(), BaseColor.BLACK);
		}
		if (StringUtils.isNotEmpty(style.getFont())) {
			String[] fontAttrs = StringUtils.split(style.getFont(), " ");
			for (int i = 0; i < fontAttrs.length; i++) {
				if (fontAttrs[i].equals("normal")) {

				} else if (fontAttrs[i].equals("italic")) {
					isItalic = true;
				} else if (fontAttrs[i].equals("bold")) {
					isBold = true;
				} else if (fontAttrs[i].equals("700")) {
					isBold = true;
				} else if (fontAttrs[i].indexOf("px") > 0) {
					String fontH = StringUtils.split(fontAttrs[i], "////")[0];
					fontHeight = (short) (UnitsUtil.pixelsToPoints(NumberUtils
							.toDouble(StringUtils.substring(fontH, 0,
									fontH.length() - 2)), true));
				} else if (fontAttrs[i].indexOf("pt") > 0) {
					String fontH = fontAttrs[i];
					fontHeight = NumberUtils.toShort(StringUtils.substring(
							fontH, 0, fontH.length() - 2));
				} else {
					fontName = fontAttrs[i];
				}
			}
		}

		if (style.getTextDecoration() != null && style.getTextDecoration() == 1
				&& style.getTextDecoration() == 3) {
			switch (style.getTextDecoration()) {
			case 1:
				underline = Byte.valueOf(String.valueOf(style
						.getTextDecoration()));
				break;
			case 2:
				isStrikeout = true;
				break;
			case 3:
				underline = Byte.valueOf(String.valueOf(style
						.getTextDecoration()));
				isStrikeout = true;
				break;
			default:
				break;
			}
		}

		if (fontColor != null || fontHeight != null || fontName != null
				|| isItalic != null || isBold != null || isStrikeout != null
				|| underline != null) {
			// default font:boldWeight(400), color(8), fontHeight(220),
			// name(Calibri), italic(false), strikeout(false), typeOffset(0),
			// underline(0));
			if (isBold != null && isBold) {
				
				if (fontHeight != null) {
					fontSize = fontHeight;
				}
				if (isItalic != null) {
					fontStyle |= Font.ITALIC;
				}
				if (fontName != null) {
					if (ChineseFont.BASE_CHINESE_FONT != null
							&& ChineseFont.containsChinese(fontName)
							|| chineseFlag) {
						font = new Font(ChineseFont.BASE_CHINESE_FONT,
								fontSize, fontStyle, fontColor);
					} else {
						String s = fontName;
						if (s.indexOf("courier") >= 0) // "courier new".equals(s)
														// ||
														// "courier".equals(s))
							fontFamily = FontFamily.COURIER;
						else if (s.indexOf("times") >= 0)
							fontFamily = FontFamily.TIMES_ROMAN;
						else
							fontFamily = FontFamily.HELVETICA;
					}
				}
				if (isStrikeout != null) {
					fontStyle |= Font.STRIKETHRU;
				}
				if (isBold != null) {
					fontStyle |= Font.BOLD;
				}

				if (underline != null) {
					fontStyle |= Font.BOLD;
				}
			} else {

			}
		}
		font = new Font(fontFamily, fontSize, fontStyle,
				fontColor);
		return font;
	}

	/**
	 * 颜色转换
	 * 
	 * @param colour
	 * @param defaultColor
	 * @return
	 */
	private BaseColor convertColour(String color, BaseColor defaultColor) {
		if (defaultColor == null)
			defaultColor = BaseColor.WHITE;

		if (!StringUtils.isNotBlank(color))
			return defaultColor;
		if (StringUtils.indexOf(color, '#') >= 0) {
			return new BaseColor(Integer.parseInt(
					StringUtils.substring(color, 1, 3), 16), Integer.parseInt(
					StringUtils.substring(color, 3, 5), 16), Integer.parseInt(
					StringUtils.substring(color, 5, 7), 16));
		} else {
			return BaseColor.BLACK;
		}

	}

	private void setStyle(SheetsProperty sp, PdfPCell cs, int col, int row,
			Style style) {
		if (style == null)
			return;
		cs.setHorizontalAlignment(style.getHAlign() != null ? style.getHAlign()
				: 0);
		cs.setVerticalAlignment(style.getVAlign() != null ? style.getVAlign()
				: 0);
		boolean top = false, left = false, right = true, bottom = true;
		if (col == 0) {
			left = true;
		} else if (getCellStyle(getCell(sp, row, col - 1)) == null
				|| getCellStyle(getCell(sp, row, col - 1)).getBorderRight() == null
				|| getCellStyle(getCell(sp, row, col - 1)).getBorderRight()
						.getStyle() == null
				|| getCellStyle(getCell(sp, row, col - 1)).getBorderRight()
						.getStyle().equals(0)) {
			left = true;
		}
		if (row % size == 0) {
			top = true;
		} else if (getCellStyle(getCell(sp, row - 1, col)) == null
				|| getCellStyle(getCell(sp, row - 1, col)).getBorderBottom() == null
				|| getCellStyle(getCell(sp, row - 1, col)).getBorderBottom()
						.getStyle() == null
				|| getCellStyle(getCell(sp, row - 1, col)).getBorderBottom()
						.getStyle().equals(0)) {
			top = true;
		}
		if (top && style.getBorderTop() != null) {
			cs.setBorderColorTop(convertColour(style.getBorderTop().getColor(),
					BaseColor.BLACK));
			cs.setBorderWidthTop(convertBorderStyle(style.getBorderTop()
					.getStyle(), false));
		}
		if (left && style.getBorderLeft() != null) {
			cs.setBorderColorLeft(convertColour(style.getBorderLeft()
					.getColor(), BaseColor.BLACK));
			cs.setBorderWidthLeft(convertBorderStyle(style.getBorderLeft()
					.getStyle(), false));
		}
		if (bottom && style.getBorderBottom() != null) {
			cs.setBorderColorBottom(convertColour(style.getBorderBottom()
					.getColor(), BaseColor.BLACK));
			cs.setBorderWidthBottom(convertBorderStyle(style.getBorderBottom()
					.getStyle(), true));
		}
		if (right && style.getBorderRight() != null) {
			cs.setBorderColorRight(convertColour(style.getBorderRight()
					.getColor(), BaseColor.BLACK));
			cs.setBorderWidthRight(convertBorderStyle(style.getBorderRight()
					.getStyle(), true));
		}
	}

	/**
	 * 转换边框样式
	 * 
	 * @param style
	 * @return
	 */
	private float convertBorderStyle(Integer style, boolean flag) {
		if (style == null)
			return 0.0f;

		float w = 0.0f;
		if (style == 0) {
			// 默认全部使用边框，边框大小 0.5f
			w = 0.0f;
		} else if (style == 1) {
			w = 1.0f;
		} else if (style == 5) {
			w = 2.0f;
		} else if (style == 2) {
			w = 1.5f;
		} else if (style == 6) {
			w = 3.0f;
		} else {
			w = 0.0f;
		}
		if (flag && w != 0.0f) {
			w += 0.5;
		}
		return w;
	}

	@Override
	public void write(SpreadSchema schema) throws SpreadExportException,
			Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String jsonString, List<List<?>> allEntitis) throws SpreadExportException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String jsonString, Map<String, String> titles, List<List<?>> allEntitis) throws SpreadExportException, IOException {

	}

	@Override
	public void write(List<String> jsonString,
			List<Map<String, Object>> rptInfos, String showType)
			throws SpreadExportException {
		// TODO Auto-generated method stub
		
	}

}
