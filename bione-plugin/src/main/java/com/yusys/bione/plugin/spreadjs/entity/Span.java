
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Span {

    /**
     * 合并起始行
     * 
     */
    private Integer row;
    /**
     * 合并起始列
     * 
     */
    private Integer col;
    /**
     * 合并行数
     * 
     */
    private Integer rowCount;
    /**
     * 合并列数
     * 
     */
    private Integer colCount;

    /**
     * 合并起始行
     * 
     */
    public Integer getRow() {
        return row;
    }

    /**
     * 合并起始行
     * 
     */
    public void setRow(Integer row) {
        this.row = row;
    }

    /**
     * 合并起始列
     * 
     */
    public Integer getCol() {
        return col;
    }

    /**
     * 合并起始列
     * 
     */
    public void setCol(Integer col) {
        this.col = col;
    }

    /**
     * 合并行数
     * 
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 合并行数
     * 
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 合并列数
     * 
     */
    public Integer getColCount() {
        return colCount;
    }

    /**
     * 合并列数
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
