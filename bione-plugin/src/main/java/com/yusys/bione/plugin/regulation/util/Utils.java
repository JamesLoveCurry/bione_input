package com.yusys.bione.plugin.regulation.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class Utils {

	/**
	 * 获取单元格字符串值
	 * 
	 * @param cell 单元格对象
	 * @return 字符串值
	 */
	public static String getStringValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		CellType cellType = cell.getCellTypeEnum();
		switch (cellType) {
		case BOOLEAN:
			return cell.getBooleanCellValue() ? "Y" : "N";
		case FORMULA:
			String formula = StringUtils.trimToEmpty(cell.getCellFormula());
			if (formula.startsWith("HYPERLINK(")) {
				int idx = formula.indexOf(',');
				if (idx >= 0) {
					idx = formula.indexOf('"', idx);
					if (idx >= 0) {
						int idx2 = formula.indexOf('"', idx + 1);
						if (idx2 >= 0) {
							return formula.substring(idx + 1, idx2).trim();
						}
					}
				}
			}
			return formula;
		case NUMERIC:
			double d = cell.getNumericCellValue();
			int i = (int)d;
			if (Math.abs(d - i) <= 0.000001) {
				return Integer.toString(i);
			}
			return Double.toString(d);
		case STRING:
			return StringUtils.trimToEmpty(cell.getStringCellValue());
		default:
			return "";
		}
	}
}
