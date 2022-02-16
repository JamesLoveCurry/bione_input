
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 名称区域
 * 
 */
public class NamesProperty {

    /**
     * 该区域的名称. eg:'工资表'
     * 
     */
    private String name;
    /**
     * 引用位置字符串. eg:'$B$20:$E$22 表示区域从B20单元格到E22单元格'
     * 
     */
    private String formula;
    /**
     * 起始的行
     * 
     */
    private Integer row;
    /**
     * 起始的列
     * 
     */
    private Integer col;
    /**
     * 区域包含的列
     * 
     */
    private Integer colCount;

    /**
     * 该区域的名称. eg:'工资表'
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 该区域的名称. eg:'工资表'
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 引用位置字符串. eg:'$B$20:$E$22 表示区域从B20单元格到E22单元格'
     * 
     */
    public String getFormula() {
        return formula;
    }

    /**
     * 引用位置字符串. eg:'$B$20:$E$22 表示区域从B20单元格到E22单元格'
     * 
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * 起始的行
     * 
     */
    public Integer getRow() {
        return row;
    }

    /**
     * 起始的行
     * 
     */
    public void setRow(Integer row) {
        this.row = row;
    }

    /**
     * 起始的列
     * 
     */
    public Integer getCol() {
        return col;
    }

    /**
     * 起始的列
     * 
     */
    public void setCol(Integer col) {
        this.col = col;
    }

    /**
     * 区域包含的列
     * 
     */
    public Integer getColCount() {
        return colCount;
    }

    /**
     * 区域包含的列
     * 
     */
    public void setColCount(Integer colCount) {
        this.colCount = colCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

}
