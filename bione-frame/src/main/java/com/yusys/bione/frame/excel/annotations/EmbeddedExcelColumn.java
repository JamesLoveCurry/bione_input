/**
 * 
 */
package com.yusys.bione.frame.excel.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author tanxu
 * 
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface EmbeddedExcelColumn {

}
