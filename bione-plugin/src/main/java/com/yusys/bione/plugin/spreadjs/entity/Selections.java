
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 单元格选中部分
 * 
 */
public class Selections {

    /**
     * length
     * 
     */
    private Integer length;
    /**
     * activeSelectedRangeIndex
     * 
     */
    private Integer activeSelectedRangeIndex;
    /**
     * 未测
     * 
     */
    private Integer selectionPolicy;
    /**
     * 未测
     * 
     */
    private Integer selectionUnit;

    /**
     * length
     * 
     */
    public Integer getLength() {
        return length;
    }

    /**
     * length
     * 
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * activeSelectedRangeIndex
     * 
     */
    public Integer getActiveSelectedRangeIndex() {
        return activeSelectedRangeIndex;
    }

    /**
     * activeSelectedRangeIndex
     * 
     */
    public void setActiveSelectedRangeIndex(Integer activeSelectedRangeIndex) {
        this.activeSelectedRangeIndex = activeSelectedRangeIndex;
    }

    /**
     * 未测
     * 
     */
    public Integer getSelectionPolicy() {
        return selectionPolicy;
    }

    /**
     * 未测
     * 
     */
    public void setSelectionPolicy(Integer selectionPolicy) {
        this.selectionPolicy = selectionPolicy;
    }

    /**
     * 未测
     * 
     */
    public Integer getSelectionUnit() {
        return selectionUnit;
    }

    /**
     * 未测
     * 
     */
    public void setSelectionUnit(Integer selectionUnit) {
        this.selectionUnit = selectionUnit;
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
