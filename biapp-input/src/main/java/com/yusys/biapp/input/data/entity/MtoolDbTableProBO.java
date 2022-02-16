package com.yusys.biapp.input.data.entity;

import java.io.Serializable;

/**
 * <pre>
 * Title:数据库表的相关信息类
 * Description: 数据库表的相关信息类
 * </pre>
 * @author 
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class MtoolDbTableProBO implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -683923851243450368L;
    //列名
    private String tableName;
    //列中文名
    private String tableRemarks;
    //列类型
    private String tableType;
    //列长度
    private String tableSize;
    //列长度有小数位
    private String tableDecimalDigits;
    //列默认值
    private String tableDef;
    //列是否主键
    private String tableKey;
    //列是否可以为Null
    private String tableNull;
    //列的顺序
    private String tablePosition;
    
    //
    private int decimalDigits;
    

    public String getTableDecimalDigits() {
		return tableDecimalDigits;
	}

	public void setTableDecimalDigits(String tableDecimalDigits) {
		this.tableDecimalDigits = tableDecimalDigits;
	}

    /**
     * 返回 tableDef。
     * @return String 
     */
    public String getTableDef() {
        return tableDef;
    }

    /**
     * @param tableDef 设置 tableDef。
     */
    public void setTableDef(String tableDef) {
        this.tableDef = tableDef;
    }

    /**
     * 返回 tableKey。
     * @return String 
     */
    public String getTableKey() {
        return tableKey;
    }

    /**
     * @param tableKey 设置 tableKey。
     */
    public void setTableKey(String tableKey) {
        this.tableKey = tableKey;
    }

    /**
     * 返回 tableName。
     * @return String 
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName 设置 tableName。
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 返回 tableNull。
     * @return String 
     */
    public String getTableNull() {
        return tableNull;
    }

    /**
     * @param tableNull 设置 tableNull。
     */
    public void setTableNull(String tableNull) {
        this.tableNull = tableNull;
    }

    /**
     * 返回 tablePosition。
     * @return String 
     */
    public String getTablePosition() {
        return tablePosition;
    }

    /**
     * @param tablePosition 设置 tablePosition。
     */
    public void setTablePosition(String tablePosition) {
        this.tablePosition = tablePosition;
    }

    /**
     * 返回 tableRemarks。
     * @return String 
     */
    public String getTableRemarks() {
        return tableRemarks;
    }

    /**
     * @param tableRemarks 设置 tableRemarks。
     */
    public void setTableRemarks(String tableRemarks) {
        this.tableRemarks = tableRemarks;
    }

    /**
     * 返回 tableSize。
     * @return String 
     */
    public String getTableSize() {
        return tableSize;
    }

    /**
     * @param tableSize 设置 tableSize。
     */
    public void setTableSize(String tableSize) {
        this.tableSize = tableSize;
    }

    /**
     * 返回 tableType。
     * @return String 
     */
    public String getTableType() {
        return tableType;
    }

    /**
     * @param tableType 设置 tableType。
     */
    public void setTableType(String tableType) {
        this.tableType = tableType;
    }



	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

}
