package com.yusys.biapp.input.index.util.vo;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( { FIELD })
@Retention(RUNTIME)
public @interface TableColumn {
	

	String columnName () default "";
	
}