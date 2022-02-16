package com.yusys.bione.plugin.spreadjs.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import com.yusys.bione.comp.utils.ReflectionUtils;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;
import com.yusys.bione.plugin.spreadjs.entity.Style;

public abstract class AbstractCellStyleCreator {
	protected Map<String, CellStyle> cellStyles = new HashMap<String, CellStyle>();

	protected Map<String, String> namesMapping = new HashMap<String, String>();

	protected Map<String, Style> stylesMapping = new HashMap<String, Style>();

	protected Map<String, Style> stylesMaps = new HashMap<String, Style>();

	protected Workbook workbook;

	protected SpreadSchema spreadSchema;

	protected void addStyles(Iterable<Style> styles) {
		if (styles == null)
			return;
		for (Style style : styles) {
			addStyle(style,styles);
		}
	}

	private Style getStyle(String name,Iterable<Style> styles){
		if (styles == null)
			return null;
		for (Style style : styles) {
			if(style.getName().equals(name)){
				return style;
			}
		}
		return null;
	}
	
	private Style getAllStyle(Style style,Iterable<Style> styles){
		if(StringUtils.isNotBlank(style.getParentName())){
			Style pstyle=getStyle(style.getParentName(),styles);
			if(pstyle!=null){
				this.copy(pstyle, style);
				style.setParentName(pstyle.getParentName());
				getAllStyle(style,styles);
			}
			else{
				return style;
			}
		}
		return style;
	}

	private void copy(Object a, Object b) {
		Field f[] = a.getClass().getDeclaredFields();
		for (int i = 0; i < f.length; i++) {
			if (ReflectionUtils.getFieldValue(b, f[i].getName()) == null) {
				ReflectionUtils.setFieldValue(b, f[i].getName(),
						ReflectionUtils.getFieldValue(a, f[i].getName()));
			}
		}
	}

	protected void addStyle(Style style,Iterable<Style> styles) {
		if (style == null) {
			return;
		}
		boolean flag = true;
		String key = getStyleUnique(style);
		for (String name : stylesMapping.keySet()) {
			if (style.equals(stylesMapping.get(name))) {
				flag = false;
				key = name;
				break;
			}
		}
		if (flag) {
			if (cellStyles.get(key) == null) {
				createCellStyle(key, getAllStyle(style,styles));
			}
		} else {
			namesMapping.put(style.getName(), key);
		}

	}

	private String getStyleUnique(Style style) {
		if (style == null)
			return "";
		String s = style.toString();
		s = s.replaceAll(
				"com.yusys.bione.plugin.spreadjs.entity.Style[^\\[]+", "");
		s = s.replaceAll(
				"com.yusys.bione.plugin.spreadjs.entity.StyleBorder[^\\[]+",
				"");
		return s;
	}

	protected short toShort(Integer num) {
		if (num == null)
			return 0;
		return Short.parseShort(String.valueOf(num));
	}

	protected void setBaseStyle(CellStyle cs, Style style) {
		if (style.getVAlign() != null) {
			if (style.getVAlign() == 0) {
				cs.setVerticalAlignment(VerticalAlignment.TOP);
			} else if (style.getVAlign() == 1) {
				cs.setVerticalAlignment(VerticalAlignment.CENTER);
			} else if (style.getVAlign() == 2) {
				cs.setVerticalAlignment(VerticalAlignment.BOTTOM);
			} else if (style.getVAlign() == 3) {
				cs.setVerticalAlignment(VerticalAlignment.BOTTOM);
			}
		}
		if (style.getHAlign() != null) {
			if (style.getHAlign() == 0) {
				cs.setAlignment(HorizontalAlignment.LEFT);
			} else if (style.getHAlign() == 1) {
				cs.setAlignment(HorizontalAlignment.CENTER);
			} else if (style.getHAlign() == 2) {
				cs.setAlignment(HorizontalAlignment.RIGHT);
			} else if (style.getHAlign() == 3) {
				cs.setAlignment(HorizontalAlignment.GENERAL);
			}
		}
		if (style.getLocked() != null && style.getLocked()) {
			cs.setLocked(true);
		} else {
			cs.setLocked(false);
		}

		if (style.getWordWrap() != null && style.getWordWrap()) {
			cs.setWrapText(true);
		} else {
			cs.setWrapText(false);
		}

		if (StringUtils.isNotEmpty(style.getFormatter())) {
			cs.setDataFormat(workbook.createDataFormat().getFormat(
					(style.getFormatter())));
		}
		//设置单元格缩进
		if(style.getTextIndent() != null) {
			cs.setIndention(style.getTextIndent().shortValue());
		}
	}

	public CellStyle getCellStyle(String name) {
		if(name.lastIndexOf("\"")>=0){
			name = name.substring(1, name.length() - 1);
		}
		
		String key = namesMapping.get(name);
		if (StringUtils.isEmpty(key)) {
			return null;
		} else {
			return cellStyles.get(key);
		}
	}

	public CellStyle createCellStyle(Style style) {
		if (style == null)
			return null;
		String key = namesMapping.get(style.getName());
		if (StringUtils.isEmpty(key)) {
			key = getStyleUnique(style);
			return createCellStyle(key, style);
		} else {
			return cellStyles.get(key);
		}
	}

	abstract public CellStyle createCellStyle(String key, Style style);

	abstract protected Short createColorIndex(String hexString);

	abstract protected void setCellStyle(CellStyle xcs, Style style);

}
