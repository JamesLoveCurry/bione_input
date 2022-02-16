package com.yusys.bione.plugin.spreadjs.service;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import com.yusys.bione.frame.util.excel.WorkbookWrapper;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;
import com.yusys.bione.plugin.spreadjs.entity.Style;

public class XlsCellStyleCreator extends AbstractCellStyleCreator {

	public XlsCellStyleCreator(Workbook workbook, SpreadSchema spreadSchema) {
		this.workbook = workbook;
		super.workbook=workbook;
		this.spreadSchema = spreadSchema;
		init();
	}
	
	public XlsCellStyleCreator(Workbook workbook, SpreadSchema spreadSchema,SheetsProperty sp ) {
		this.workbook = workbook;
		super.workbook=workbook;
		this.spreadSchema = spreadSchema;
		init(sp);
	}
	
	private void init() {
		Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
		// 添加所有的样式
		addStyles(spreadSchema.getNamedStyles());
		for (String sheetIndex : sps.keySet()) {
			SheetsProperty sp = sps.get(sheetIndex);
			// 添加sheet中对应的样式
			addStyles(sp.getNamedStyles());
		}
	}
	
	private void init(SheetsProperty sp ) {
		if(sp.getNamedStyles()!=null&&sp.getNamedStyles().size()>0){
			addStyles(sp.getNamedStyles());
		}
		else{
			addStyles(spreadSchema.getNamedStyles());
		}
		
	}

	public CellStyle createCellStyle(String key, Style style) {
		namesMapping.put(style.getName(), key);
		stylesMapping.put(key, style);
		CellStyle xcs = this.workbook.createCellStyle();
		setCellStyle(xcs, style);
		cellStyles.put(key, xcs);
		return xcs;
	}
	
	protected Short createColorIndex(String hexString) {
		HSSFPalette pale = ((WorkbookWrapper)workbook).getCustomPalette();
		String[] colorThemes;
		if (StringUtils.indexOf(hexString, '#') >= 0) {
			if(hexString.length() == 4) {//有#FFF这样的，转化成#FFFFFF这样的格式
				hexString = hexString + StringUtils.substring(hexString, 1, 4);
			}
			if(hexString.length() != 7) {
				hexString = "#000000";//这种属于颜色码值有问题的，统一设置成黑色便于发现调整
			}
			HSSFColor color = pale
					.findSimilarColor(
							Integer.parseInt(
									StringUtils.substring(hexString, 1, 3), 16),
							Integer.parseInt(
									StringUtils.substring(hexString, 3, 5), 16),
							Integer.parseInt(
									StringUtils.substring(hexString, 5, 7), 16));
			
			short idx = color.getIndex();
			pale.setColorAtIndex(idx, (byte)Integer.parseInt(
									StringUtils.substring(hexString, 1, 3), 16), (byte)Integer.parseInt(
									StringUtils.substring(hexString, 3, 5), 16), (byte)Integer.parseInt(
											StringUtils.substring(hexString, 5, 7), 16));
			return idx;
		}else if ((colorThemes = StringUtils.split(hexString)).length == 3) {
			if(StringUtils.indexOf(colorThemes[0], "rgb") >= 0) {//如果颜色是rgb(0, 0, 0)这样格式的
				HSSFColor color = null;
				hexString = StringUtils.substring(hexString, 4, hexString.length()-1);
				if ((colorThemes = StringUtils.split(hexString, ", ")).length == 3) {
					int a= Integer.parseInt(colorThemes[0]);
					int b= Integer.parseInt(colorThemes[1]);
					int c= Integer.parseInt(colorThemes[2]);
					color = pale.findSimilarColor(a, b, c);
				}
				short idx = color.getIndex();
				return idx;
			}else {
				return HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex();	
			}
		} else {
			return HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex();
		}
	}

	@Override
	protected void setCellStyle(CellStyle cs, Style style) {
		// TODO Auto-generated method stub
		setFont(cs, style);
		setBaseStyle(cs,style);
		if (style.getBorderLeft() != null) {
			if (StringUtils.isNotEmpty(style.getBorderLeft().getColor())) {
				cs.setLeftBorderColor(createColorIndex(style.getBorderLeft()
						.getColor()));
			}
			if (style.getBorderLeft().getStyle() != null) {
				cs.setBorderLeft(BorderStyle.valueOf(toShort(style.getBorderLeft().getStyle())));
			}
		}

		if (style.getBorderRight() != null) {
			if (StringUtils.isNotEmpty(style.getBorderRight().getColor())) {
				cs.setRightBorderColor(createColorIndex(style.getBorderRight()
						.getColor()));
			}
			if (style.getBorderRight().getStyle() != null) {
				cs.setBorderRight(BorderStyle.valueOf(toShort(style.getBorderRight().getStyle())));
			}
		}

		if (style.getBorderTop() != null) {
			if (StringUtils.isNotEmpty(style.getBorderTop().getColor())) {
				cs.setTopBorderColor(createColorIndex(style.getBorderTop()
						.getColor()));
			}
			if (style.getBorderTop().getStyle() != null) {
				cs.setBorderTop(BorderStyle.valueOf(toShort(style.getBorderTop().getStyle())));
			}
		}

		if (style.getBorderBottom() != null) {
			if (StringUtils.isNotEmpty(style.getBorderBottom().getColor())) {
				cs.setBottomBorderColor(createColorIndex(style.getBorderBottom()
						.getColor()));
			}
			if (style.getBorderBottom().getStyle() != null) {
				cs.setBorderBottom(BorderStyle.valueOf(toShort(style.getBorderBottom().getStyle())));
			}
		}
		if (StringUtils.isNotEmpty(style.getFormatter())) {
			cs.setDataFormat(workbook.createDataFormat().getFormat(
					(style.getFormatter())));
		}
		if (StringUtils.isNotEmpty(style.getBackColor())) {
			cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cs.setFillForegroundColor(createColorIndex(style.getBackColor()));
		}
	}
	/**
	 * 设置加粗，字体大小，字体名称
	 */
	protected void setFont(CellStyle xcs, Style style) {
		Short colorIndex=null;
		Short fontHeight = null, typeOffset = 0;
		String fontName = null;
		Boolean isItalic = false, isBold = false, isStrikeout = null;
		Byte underline = null;
		if (StringUtils.isNotEmpty(style.getForeColor())) {
			colorIndex = createColorIndex(style.getForeColor());	
		}
		if (StringUtils.isNotEmpty(style.getFont())) {
			String[] splFonts = {"Times New Roman"};//带空格的特殊字体数组(后期优化到配置文件)
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
					String fontH = StringUtils.split(fontAttrs[i], "////")[0].trim();
					fontHeight = (short) (UnitsUtil.pixelsToPoints(NumberUtils
							.toDouble(StringUtils.substring(fontH, 0,
									fontH.length() - 2)), true));
				} else if (fontAttrs[i].indexOf("pt") > 0) {
					String fontH = fontAttrs[i].trim();
					fontHeight = (short) NumberUtils
							.toDouble(StringUtils.substring(fontH, 0,
									fontH.length() - 2));
				} else {
					//特殊处理带空格的字体
					for(String font :splFonts) {
						if(style.getFont().indexOf(font) > 0) {
							fontName = font;
							break;
						};
					}
					if(fontName == null) {
						fontName = fontAttrs[i];
					}else {
						break;
					}
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

		if (colorIndex != null || fontHeight != null || fontName != null
				|| isItalic != null || isBold != null || isStrikeout != null
				|| underline != null) {
			// default font:boldWeight(400), color(8), fontHeight(220),
			// name(Calibri), italic(false), strikeout(false), typeOffset(0),
			// underline(0));

			boolean boldWeight = BooleanUtils.isTrue(isBold);
			
			Font font = workbook.findFont(boldWeight, colorIndex==null?8:colorIndex,
					(short) (fontHeight == null ? 220 : fontHeight*20),
					fontName == null ? "Calibri" : fontName,
					isItalic == null ? false : isItalic,
					isStrikeout == null ? false : isStrikeout, typeOffset,
					underline == null ? 0 : underline);
			if (font == null) {
				font = workbook.createFont();
				if (colorIndex != null) {
					font.setColor(colorIndex);
				}
				if (fontHeight != null) {
					font.setFontHeightInPoints(fontHeight);
				}
				if (isItalic != null) {
					font.setItalic(isItalic);
				}
				if (fontName != null) {
					font.setFontName(fontName);
				}
				if (isStrikeout != null) {
					font.setStrikeout(isStrikeout);
				}
				if (isBold != null) {
					font.setBold(boldWeight);
				}

				if (underline != null) {
					font.setUnderline(underline);
				}
			}
			else{
				
			}
			xcs.setFont(font);
		}
	}
}
