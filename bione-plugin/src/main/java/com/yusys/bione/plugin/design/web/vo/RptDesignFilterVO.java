/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import java.io.Serializable;

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
public class RptDesignFilterVO implements Serializable{

	private static final long serialVersionUID = 1068108074170862547L;

	private String indexNo;
	private String dimNo;
	private String filterVal;
	private String filterMode;
	
	/**
	 * @return 返回 dimNo。
	 */
	public String getDimNo() {
		return dimNo;
	}
	/**
	 * @param dimNo 设置 dimNo。
	 */
	public void setDimNo(String dimNo) {
		this.dimNo = dimNo;
	}
	/**
	 * @return 返回 filterVal。
	 */
	public String getFilterVal() {
		return filterVal;
	}
	/**
	 * @param filterVal 设置 filterVal。
	 */
	public void setFilterVal(String filterVal) {
		this.filterVal = filterVal;
	}
	/**
	 * @return 返回 filterMode。
	 */
	public String getFilterMode() {
		return filterMode;
	}
	/**
	 * @param filterMode 设置 filterMode。
	 */
	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}
	/**
	 * @return 返回 indexNo。
	 */
	public String getIndexNo() {
		return indexNo;
	}
	/**
	 * @param indexNo 设置 indexNo。
	 */
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	
}
