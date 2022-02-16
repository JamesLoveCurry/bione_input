package com.yusys.biapp.input.dict.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;

/**
 * @author tobe
 */
public class ColumnType {
	public static final String INTEGER = "INTEGER";
	public static final String NUMERIC = "NUMERIC";
	public static final String NUMBER = "NUMBER";
	public static final String DECIMAL = "DECIMAL";
	public static final String VARCHAR = "VARCHAR";
	public static final String VARCHAR2 = "VARCHAR2";
	public static final String CHAR = "CHAR";
	public static final String DATE = "DATE";
	public static final String TIME = "TIME";
	public static final String TIMESTAMP = "TIMESTAMP";
	public static final String BOOLEAN = "BOOLEAN";
	public static final String LONG = "LONG";
	
	/** 精确数值型 **/
	public static final List<String> NumberType = Lists.newArrayList();

	static {
		NumberType.add("NUMERIC");
		NumberType.add("NUMBER");
		NumberType.add("DECIMAL");
	}

	/** 常量数值型 **/
	public static final List<String> IntType = Lists.newArrayList();

	static {
		IntType.add("TIMESTAMP");
		IntType.add("DATE");
		IntType.add("LONG");
		IntType.add("INTEGER");
	}

	public void checkINTEGER(RptInputLstTempleField utc, Object v, String type, int rownum) {

	}
}
