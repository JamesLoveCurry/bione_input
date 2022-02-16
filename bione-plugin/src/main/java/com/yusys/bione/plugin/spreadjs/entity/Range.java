
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 未测
 * 
 */
public class Range {

    /**
     * 未测
     * 
     */
    private Integer row;
    /**
     * 未测
     * 
     */
    private Integer col;
    /**
     * 未测
     * 
     */
    private Integer rowCount;
    /**
     * 未测
     * 
     */
    private Integer colCount;

    /**
     * 未测
     * 
     */
    public Integer getRow() {
        return row;
    }

    /**
     * 未测
     * 
     */
    public void setRow(Integer row) {
        this.row = row;
    }

    /**
     * 未测
     * 
     */
    public Integer getCol() {
        return col;
    }

    /**
     * 未测
     * 
     */
    public void setCol(Integer col) {
        this.col = col;
    }

    /**
     * 未测
     * 
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 未测
     * 
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 未测
     * 
     */
    public Integer getColCount() {
        return colCount;
    }

    /**
     * 未测
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
