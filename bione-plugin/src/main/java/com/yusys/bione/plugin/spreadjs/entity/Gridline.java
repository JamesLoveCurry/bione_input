
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 表格线
 * 
 */
public class Gridline {

    /**
     * 颜色的RGBA字符串，eg:'rgba(209,21,33,22)'
     * 
     */
    private String color;
    /**
     * 是否显示水平表格线
     * 
     */
    private Boolean showHorizontalGridline;
    /**
     * 是否显示竖直表格线
     * 
     */
    private Boolean showVerticalGridline;

    /**
     * 颜色的RGBA字符串，eg:'rgba(209,21,33,22)'
     * 
     */
    public String getColor() {
        return color;
    }

    /**
     * 颜色的RGBA字符串，eg:'rgba(209,21,33,22)'
     * 
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * 是否显示水平表格线
     * 
     */
    public Boolean getShowHorizontalGridline() {
        return showHorizontalGridline;
    }

    /**
     * 是否显示水平表格线
     * 
     */
    public void setShowHorizontalGridline(Boolean showHorizontalGridline) {
        this.showHorizontalGridline = showHorizontalGridline;
    }

    /**
     * 是否显示竖直表格线
     * 
     */
    public Boolean getShowVerticalGridline() {
        return showVerticalGridline;
    }

    /**
     * 是否显示竖直表格线
     * 
     */
    public void setShowVerticalGridline(Boolean showVerticalGridline) {
        this.showVerticalGridline = showVerticalGridline;
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
