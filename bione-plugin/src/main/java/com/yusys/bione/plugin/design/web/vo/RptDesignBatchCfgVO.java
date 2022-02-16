/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;

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
public class RptDesignBatchCfgVO implements Serializable{

	private static final long serialVersionUID = 2087099772459762548L;

	private String objType;
	private Integer rowId;
	private Integer colId;
	private JSONArray filtInfos;	
	
	
	/**
	 * @return 返回 objType。
	 */
	public String getObjType() {
		return objType;
	}
	/**
	 * @param objType 设置 objType。
	 */
	public void setObjType(String objType) {
		this.objType = objType;
	}
	/**
	 * @return 返回 rowId。
	 */
	public Integer getRowId() {
		return rowId;
	}
	/**
	 * @param rowId 设置 rowId。
	 */
	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}
	/**
	 * @return 返回 colId。
	 */
	public Integer getColId() {
		return colId;
	}
	/**
	 * @param colId 设置 colId。
	 */
	public void setColId(Integer colId) {
		this.colId = colId;
	}
	/**
	 * @return 返回 filtInfos。
	 */
	public JSONArray getFiltInfos() {
		return filtInfos;
	}
	/**
	 * @param filtInfos 设置 filtInfos。
	 */
	public void setFiltInfos(JSONArray filtInfos) {
		this.filtInfos = filtInfos;
	}
	
}
