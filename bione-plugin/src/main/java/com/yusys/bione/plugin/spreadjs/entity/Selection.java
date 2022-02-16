
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Selection {

    /**
     * row
     * 
     */
    private Integer row;
    /**
     * col
     * 
     */
    private Integer col;
    /**
     * rowCount
     * 
     */
    private Integer rowCount;
    /**
     * colCount
     * 
     */
    private Integer colCount;

    /**
     * row
     * 
     */
    public Integer getRow() {
        return row;
    }

    /**
     * row
     * 
     */
    public void setRow(Integer row) {
        this.row = row;
    }

    /**
     * col
     * 
     */
    public Integer getCol() {
        return col;
    }

    /**
     * col
     * 
     */
    public void setCol(Integer col) {
        this.col = col;
    }

    /**
     * rowCount
     * 
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * rowCount
     * 
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * colCount
     * 
     */
    public Integer getColCount() {
        return colCount;
    }

    /**
     * colCount
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
