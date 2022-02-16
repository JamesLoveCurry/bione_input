package com.yusys.bione.frame.util.excel;

import java.util.Map;

public interface ExcelEventListener {

	/**
	 * 一行Excel数据读取完毕。对各种单元格类型，返回值如下：<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;布尔类型：返回"Y"或者"N"；<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;字符串类型：原值返回；<br>
	 * 
	 * @param excelEventReader ExcelEventReader对象
	 * @param rowIdx 行索引值，从0开始
	 * @param rowDataMap 一行Excel数据的"列索引值=>单元格值"的Map，列索引值从0开始；读取完毕时rowDataMap为null
	 */
	public void rowDataReady(ExcelEventReader excelEventReader, int rowIdx, Map<Integer, String> rowDataMap);
}
