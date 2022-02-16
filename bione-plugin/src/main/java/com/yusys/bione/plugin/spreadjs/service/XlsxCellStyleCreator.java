/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBaseStyles;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;

import com.yusys.bione.plugin.spreadjs.entity.ColorList;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;
import com.yusys.bione.plugin.spreadjs.entity.Style;
import com.yusys.bione.plugin.spreadjs.entity.Theme;

/**
 * @author tanxu
 * 
 */
public class XlsxCellStyleCreator extends AbstractCellStyleCreator{
	
	private XSSFWorkbook workbook;
	private StylesTable stylesTable;
	private int minDistance;
	private HSSFColorPredefined retColor;
	private Map<String, Short> colorMap = new HashMap<String, Short>();

	public XlsxCellStyleCreator(XSSFWorkbook workbook, SpreadSchema spreadSchema) {
		this.workbook=workbook;
		super.workbook=workbook;
		this.spreadSchema = spreadSchema;
		init();
	}

	public XlsxCellStyleCreator(XSSFWorkbook workbook, SpreadSchema spreadSchema,SheetsProperty sp ) {
		this.workbook = workbook;
		super.workbook=workbook;
		this.spreadSchema = spreadSchema;
		init(sp);
	}

	private void init() {
		Map<String, SheetsProperty> sps = spreadSchema.getSheetsProperties();
		this.stylesTable = workbook.getStylesSource();
		for (String sheetIndex : sps.keySet()) {
			addTheme(sps.get(sheetIndex).getTheme());
		}
		
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
	
	private void addTheme(Theme theme) {
		if (theme == null)
			return;
		ThemeDocument themeDocument = ThemeDocument.Factory.newInstance();
		CTOfficeStyleSheet coss = themeDocument.addNewTheme();
		coss.setName(theme.getName());
		CTBaseStyles cbs = coss.addNewThemeElements();
		CTColorScheme cs = cbs.addNewClrScheme();
		setThemeColors(cs, theme.getThemeColor().getColorList());
		stylesTable.setTheme(new ThemesTable(themeDocument));
	}
	
	/**
	 * SpreadJs11
	 * @param theme
	 */
	private void addTheme(String theme) {
		if (theme == null)
			return;
		ThemeDocument themeDocument = ThemeDocument.Factory.newInstance();
		CTOfficeStyleSheet coss = themeDocument.addNewTheme();
		coss.setName(theme);
		CTBaseStyles cbs = coss.addNewThemeElements();
		CTColorScheme cs = cbs.addNewClrScheme();
		setThemeColors(cs, null);
		stylesTable.setTheme(new ThemesTable(themeDocument));
	}

	private void setThemeColors(CTColorScheme cs, List<ColorList> colors) {
		if (colors == null || colors.size() < 12)
			return;
		cs.setLt1(createCTSystemColor(
				cs.addNewLt1(),
				rgbToBytes(colors.get(0).getR(), colors.get(0).getG(), colors
						.get(0).getB())));
		cs.setLt2(createCTSRgbColor(
				cs.addNewLt2(),
				rgbToBytes(colors.get(1).getR(), colors.get(1).getG(), colors
						.get(1).getB())));
		cs.setDk1(createCTSystemColor(
				cs.addNewDk1(),
				rgbToBytes(colors.get(2).getR(), colors.get(2).getG(), colors
						.get(2).getB())));
		cs.setDk2(createCTSRgbColor(
				cs.addNewDk2(),
				rgbToBytes(colors.get(3).getR(), colors.get(3).getG(), colors
						.get(3).getB())));
		cs.setAccent1(createCTSRgbColor(
				cs.addNewAccent1(),
				rgbToBytes(colors.get(4).getR(), colors.get(4).getG(), colors
						.get(4).getB())));
		cs.setAccent2(createCTSRgbColor(
				cs.addNewAccent2(),
				rgbToBytes(colors.get(5).getR(), colors.get(5).getG(), colors
						.get(5).getB())));
		cs.setAccent3(createCTSRgbColor(
				cs.addNewAccent3(),
				rgbToBytes(colors.get(6).getR(), colors.get(6).getG(), colors
						.get(6).getB())));
		cs.setAccent4(createCTSRgbColor(
				cs.addNewAccent4(),
				rgbToBytes(colors.get(7).getR(), colors.get(7).getG(), colors
						.get(7).getB())));
		cs.setAccent5(createCTSRgbColor(
				cs.addNewAccent5(),
				rgbToBytes(colors.get(8).getR(), colors.get(8).getG(), colors
						.get(8).getB())));
		cs.setAccent6(createCTSRgbColor(
				cs.addNewAccent6(),
				rgbToBytes(colors.get(9).getR(), colors.get(9).getG(), colors
						.get(9).getB())));
		cs.setHlink(createCTSRgbColor(
				cs.addNewHlink(),
				rgbToBytes(colors.get(10).getR(), colors.get(10).getG(), colors
						.get(10).getB())));
		cs.setFolHlink(createCTSRgbColor(
				cs.addNewFolHlink(),
				rgbToBytes(colors.get(11).getR(), colors.get(11).getG(), colors
						.get(11).getB())));
	}

	private CTColor createCTSystemColor(CTColor ctc, byte[] cs) {
		CTSystemColor csc = ctc.addNewSysClr();
		csc.setLastClr(cs);
		ctc.setSysClr(csc);
		return ctc;
	}

	private CTColor createCTSRgbColor(CTColor ctc, byte[] cs) {
		CTSRgbColor src = ctc.addNewSrgbClr();
		src.setVal(cs);
		ctc.setSrgbClr(src);
		return ctc;
	}

	private byte[] rgbToBytes(int r, int g, int b) {
		byte[] bs = new byte[3];
		bs[0] = (byte) (r & 0xff);
		bs[1] = (byte) (g & 0xff);
		bs[2] = (byte) (b & 0xff);
		return bs;
	}	

	protected void setCellStyle(CellStyle cs, Style style) {
		// workbook.getXSSFWorkbook().getTheme().getThemeColor(IndexedColors);
		// stylesTable.getTheme().getThemeColor(idx);
		XSSFCellStyle xcs = (XSSFCellStyle)cs;
		setFont(xcs, style);
		setBaseStyle(xcs,style);
		
		if (style.getBorderLeft() != null) {
			if (StringUtils.isNotEmpty(style.getBorderLeft().getColor())) {
				xcs.setLeftBorderColor(createColor(style.getBorderLeft().getColor()));
			}
			if (style.getBorderLeft().getStyle() != null) {
				xcs.setBorderLeft(BorderStyle.valueOf(toShort(style.getBorderLeft().getStyle())));
			}
		}

		if (style.getBorderRight() != null) {
			if (StringUtils.isNotEmpty(style.getBorderRight().getColor())) {
				xcs.setRightBorderColor(createColor(style.getBorderRight().getColor()));
			}
			if (style.getBorderRight().getStyle() != null) {
				xcs.setBorderRight(BorderStyle.valueOf(toShort(style.getBorderRight().getStyle())));
			}
		}

		if (style.getBorderTop() != null) {
			if (StringUtils.isNotEmpty(style.getBorderTop().getColor())) {
				xcs.setTopBorderColor(createColor(style.getBorderTop().getColor()));
			}
			if (style.getBorderTop().getStyle() != null) {
				xcs.setBorderTop(BorderStyle.valueOf(toShort(style.getBorderTop().getStyle())));
			}
		}

		if (style.getBorderBottom() != null) {
			if (StringUtils.isNotEmpty(style.getBorderBottom().getColor())) {
				xcs.setBottomBorderColor(createColor(style.getBorderBottom().getColor()));
			}
			if (style.getBorderBottom().getStyle() != null) {
				xcs.setBorderBottom(BorderStyle.valueOf(toShort(style.getBorderBottom().getStyle())));
			}
		}
		if (StringUtils.isNotEmpty(style.getFormatter())) {
			xcs.setDataFormat(workbook.createDataFormat().getFormat(
					(style.getFormatter())));
		}
		if (StringUtils.isNotEmpty(style.getBackColor())) {
			xcs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			xcs.setFillForegroundColor(createColor(style.getBackColor()));
		}
	}
	
	private boolean calColorDistance(HSSFColorPredefined predefinedColor, XSSFColor color) {
		short[] rgb1 = predefinedColor.getTriplet();
		byte[] rgb2 = color.getRGB();
		int distanceR = rgb1[0] - (rgb2[0] & 0x00FF);
		int distanceG = rgb1[1] - (rgb2[1] & 0x00FF);
		int distanceB = rgb1[2] - (rgb2[2] & 0x00FF);
		int distance = distanceR * distanceR + distanceG * distanceG + distanceB * distanceB;
		if (distance == 0) {
			minDistance = distance;
			retColor = predefinedColor;
			return true;
		}
		if (minDistance > distance) {
			minDistance = distance;
			retColor = predefinedColor;
		}
		return false;
	}

	@Override
	protected Short createColorIndex(String hexString) {
		Short colorIndex = colorMap.get(hexString);
		if (colorIndex != null) {
			return colorIndex;
		}
		XSSFColor color = createColor(hexString);	
		minDistance = Integer.MAX_VALUE;
		retColor = null;
		if (calColorDistance(HSSFColorPredefined.BLACK, color) ||
			calColorDistance(HSSFColorPredefined.BLUE, color) ||
			calColorDistance(HSSFColorPredefined.BLUE_GREY, color) ||
			calColorDistance(HSSFColorPredefined.BRIGHT_GREEN, color) ||
			calColorDistance(HSSFColorPredefined.BROWN, color) ||
			calColorDistance(HSSFColorPredefined.CORAL, color) ||
			calColorDistance(HSSFColorPredefined.CORNFLOWER_BLUE, color) ||
			calColorDistance(HSSFColorPredefined.DARK_BLUE, color) ||
			calColorDistance(HSSFColorPredefined.DARK_GREEN, color) ||
			calColorDistance(HSSFColorPredefined.DARK_RED, color) ||
			calColorDistance(HSSFColorPredefined.DARK_TEAL, color) ||
			calColorDistance(HSSFColorPredefined.DARK_YELLOW, color) ||
			calColorDistance(HSSFColorPredefined.GOLD, color) ||
			calColorDistance(HSSFColorPredefined.GREEN, color) ||
			calColorDistance(HSSFColorPredefined.GREY_25_PERCENT, color) ||
			calColorDistance(HSSFColorPredefined.GREY_40_PERCENT, color) ||
			calColorDistance(HSSFColorPredefined.GREY_50_PERCENT, color) ||
			calColorDistance(HSSFColorPredefined.GREY_80_PERCENT, color) ||
			calColorDistance(HSSFColorPredefined.INDIGO, color) ||
			calColorDistance(HSSFColorPredefined.LAVENDER, color) ||
			calColorDistance(HSSFColorPredefined.LEMON_CHIFFON, color) ||
			calColorDistance(HSSFColorPredefined.LIGHT_BLUE, color) ||
			calColorDistance(HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE, color) ||
			calColorDistance(HSSFColorPredefined.LIGHT_GREEN, color) ||
			calColorDistance(HSSFColorPredefined.LIGHT_ORANGE, color) ||
			calColorDistance(HSSFColorPredefined.LIGHT_TURQUOISE, color) ||
			calColorDistance(HSSFColorPredefined.LIGHT_YELLOW, color) ||
			calColorDistance(HSSFColorPredefined.LIME, color) ||
			calColorDistance(HSSFColorPredefined.MAROON, color) ||
			calColorDistance(HSSFColorPredefined.OLIVE_GREEN, color) ||
			calColorDistance(HSSFColorPredefined.ORANGE, color) ||
			calColorDistance(HSSFColorPredefined.ORCHID, color) ||
			calColorDistance(HSSFColorPredefined.PALE_BLUE, color) ||
			calColorDistance(HSSFColorPredefined.PINK, color) ||
			calColorDistance(HSSFColorPredefined.PLUM, color) ||
			calColorDistance(HSSFColorPredefined.RED, color) ||
			calColorDistance(HSSFColorPredefined.ROSE, color) ||
			calColorDistance(HSSFColorPredefined.ROYAL_BLUE, color) ||
			calColorDistance(HSSFColorPredefined.SEA_GREEN, color) ||
			calColorDistance(HSSFColorPredefined.SKY_BLUE, color) ||
			calColorDistance(HSSFColorPredefined.TAN, color) ||
			calColorDistance(HSSFColorPredefined.TEAL, color) ||
			calColorDistance(HSSFColorPredefined.TURQUOISE, color) ||
			calColorDistance(HSSFColorPredefined.VIOLET, color) ||
			calColorDistance(HSSFColorPredefined.WHITE, color) ||
			calColorDistance(HSSFColorPredefined.YELLOW, color));
		colorIndex = Short.valueOf(retColor.getIndex());
		colorMap.put(hexString, colorIndex);
		return colorIndex;
	}

	protected void setFont(XSSFCellStyle xcs, Style style) {
		Short colorIndex = null;
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
					String fontH = StringUtils.split(fontAttrs[i], "////")[0];
					fontHeight = (short) (UnitsUtil.pixelsToPoints(NumberUtils
							.toDouble(StringUtils.substring(fontH, 0,
									fontH.length() - 2)), true));
				}else if (fontAttrs[i].indexOf("pt") > 0) {
					String fontH = fontAttrs[i];
					fontHeight = NumberUtils
							.toShort(StringUtils.substring(fontH, 0,
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
			XSSFFont font = stylesTable.findFont(boldWeight,
					colorIndex == null ? HSSFColorPredefined.BLACK.getIndex() : colorIndex.shortValue(),
					(short)(fontHeight == null ? 220 : fontHeight*20),
					fontName == null ? "Calibri" : fontName,
					isItalic == null ? false : isItalic,
					isStrikeout == null ? false : isStrikeout, typeOffset,
					underline == null ? 0 : underline);
			if (font == null) {
				font = workbook.createFont();
				if (colorIndex != null) {
					font.setColor(colorIndex.shortValue());
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
			xcs.setFont(font);	
		}
	}
	
	public CellStyle createCellStyle(String key, Style style) {
		namesMapping.put(style.getName(), key);
		stylesMapping.put(key, style);
		XSSFCellStyle xcs = this.workbook.createCellStyle();
		setCellStyle(xcs, style);
		cellStyles.put(key, xcs);
		return xcs;
	}
	
	private XSSFColor createColor(String hexString) {
		if (StringUtils.isEmpty(hexString))
			return null;
		String[] colorThemes;
		XSSFColor color = null;
		if (StringUtils.indexOf(hexString, '#') >= 0) {
			if(hexString.length() == 4) {//有#FFF这样的，转化成#FFFFFF这样的格式
				hexString = hexString + StringUtils.substring(hexString, 1, 4);
			}
			if(hexString.length() != 7) {
				hexString = "#000000";//这种属于颜色码值有问题的，统一设置成黑色便于发现调整
			}
			color = new XSSFColor(new Color(Integer.parseInt(
					StringUtils.substring(hexString, 1, 3), 16),
					Integer.parseInt(
							StringUtils.substring(hexString, 3, 5), 16),
					Integer.parseInt(
							StringUtils.substring(hexString, 5, 7), 16)));
		} else if ((colorThemes = StringUtils.split(hexString)).length == 3) {
			if(StringUtils.indexOf(colorThemes[0], "rgb") >= 0) {//如果颜色是rgb(0, 0, 0)这样格式的
				hexString = StringUtils.substring(hexString, 4, hexString.length()-1);
				if ((colorThemes = StringUtils.split(hexString, ", ")).length == 3) {
					int a= Integer.parseInt(colorThemes[0]);
					int b= Integer.parseInt(colorThemes[1]);
					int c= Integer.parseInt(colorThemes[2]);
					color = new XSSFColor(new Color(a, b, c));
				}
			} else if (StringUtils.equals(colorThemes[0], "Text")) {
				if (StringUtils.equals(colorThemes[1], "1")) {
					color = stylesTable.getTheme().getThemeColor(1);
				} else if (StringUtils.equals(colorThemes[1], "2")) {
					color = stylesTable.getTheme().getThemeColor(3);
				}
			} else if (StringUtils.equals(colorThemes[0], "Background")) {
				if (StringUtils.equals(colorThemes[1], "1")) {
					color = stylesTable.getTheme().getThemeColor(0);
				} else if (StringUtils.equals(colorThemes[1], "2")) {
					color = stylesTable.getTheme().getThemeColor(2);
				}
			} else if (StringUtils.equals(colorThemes[0], "Accent")) {
				if (StringUtils.equals(colorThemes[1], "1")) {
					color = stylesTable.getTheme().getThemeColor(4);
				} else if (StringUtils.equals(colorThemes[1], "2")) {
					color = stylesTable.getTheme().getThemeColor(5);
				} else if (StringUtils.equals(colorThemes[1], "3")) {
					color = stylesTable.getTheme().getThemeColor(2);
				} else if (StringUtils.equals(colorThemes[1], "4")) {
					color = stylesTable.getTheme().getThemeColor(7);
				} else if (StringUtils.equals(colorThemes[1], "5")) {
					color = stylesTable.getTheme().getThemeColor(8);
				} else if (StringUtils.equals(colorThemes[1], "6")) {
					color = stylesTable.getTheme().getThemeColor(9);
				}
			} else if (StringUtils
					.equalsIgnoreCase(colorThemes[0], "HYPERLINK")) {
				color = stylesTable.getTheme().getThemeColor(10);
			} else if (StringUtils.equalsIgnoreCase(colorThemes[0],
					"FHYPERLINK")) {
				color = stylesTable.getTheme().getThemeColor(11);
			}
			if (color != null) {
				color = resetTint(color, colorThemes[2],
						Integer.parseInt(colorThemes[1]));
			}
		}
		return color;
	}

	private XSSFColor resetTint(XSSFColor xc, String tint, int theme) {
		if (StringUtils.isEmpty(tint))
			return xc;
		double t = Double.valueOf(tint);
		if (t != 0 && t % 10 == 0) {
			xc = new XSSFColor(xc.getRGB(), null);
			stylesTable.getTheme().inheritFromThemeAsRequired(xc);
			// TODO 无效
			xc.setTint(t/100);
		} else if (t > 0 && t < 100) {
			xc = new XSSFColor(xc.getRGB(), null);
			stylesTable.getTheme().inheritFromThemeAsRequired(xc);
			// TODO 无效
			xc.setTint((t + 1)/100);
		} else if (t < 0 && t > -100) {
			xc = new XSSFColor(xc.getRGB(), null);
			stylesTable.getTheme().inheritFromThemeAsRequired(xc);
			// TODO 无效
			xc.setTint((t - 1)/100);
		}
		return xc;
	}
}
