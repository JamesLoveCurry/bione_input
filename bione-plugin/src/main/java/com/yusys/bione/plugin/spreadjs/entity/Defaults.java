
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 缺省单元格属性配置
 * 
 */
public class Defaults {

    /**
     * 行高
     * 
     */
    private Double rowHeight;
    /**
     * 列宽
     * 
     */
    private Double colWidth;
    /**
     * 列表头高度
     * 
     */
    private Double colHeaderRowHeight;
    /**
     * 行表头高度
     * 
     */
    private Double rowHeaderColWidth;

    /**
     * 行高
     * 
     */
    public Double getRowHeight() {
        return rowHeight;
    }

    /**
     * 行高
     * 
     */
    public void setRowHeight(Double rowHeight) {
        this.rowHeight = rowHeight;
    }

    /**
     * 列宽
     * 
     */
    public Double getColWidth() {
        return colWidth;
    }

    /**
     * 列宽
     * 
     */
    public void setColWidth(Double colWidth) {
        this.colWidth = colWidth;
    }

    /**
     * 列表头高度
     * 
     */
    public Double getColHeaderRowHeight() {
        return colHeaderRowHeight;
    }

    /**
     * 列表头高度
     * 
     */
    public void setColHeaderRowHeight(Double colHeaderRowHeight) {
        this.colHeaderRowHeight = colHeaderRowHeight;
    }

    /**
     * 行表头高度
     * 
     */
    public Double getRowHeaderColWidth() {
        return rowHeaderColWidth;
    }

    /**
     * 行表头高度
     * 
     */
    public void setRowHeaderColWidth(Double rowHeaderColWidth) {
        this.rowHeaderColWidth = rowHeaderColWidth;
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
