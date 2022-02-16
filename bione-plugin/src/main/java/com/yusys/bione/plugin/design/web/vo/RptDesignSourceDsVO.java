/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.design.entity.RptDesignSourceDs;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class RptDesignSourceDsVO extends RptDesignSourceDs{

	private static final long serialVersionUID = 2115971337847692184L;
	
	private String dsName;
	private String columnName;
	private String isPk;
	
	/**
	 * 
	 */
	public RptDesignSourceDsVO() {
		super();
	}
	
	public String getIsPk() {
		return isPk;
	}

	public void setIsPk(String isPk) {
		this.isPk = isPk;
	}

	/**
	 * @return 返回 dsName。
	 */
	public String getDsName() {
		return dsName;
	}
	
	/**
	 * @param dsName 设置 dsName。
	 */
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	/**
	 * @return 返回 columnName。
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * @param columnName 设置 columnName。
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
}
