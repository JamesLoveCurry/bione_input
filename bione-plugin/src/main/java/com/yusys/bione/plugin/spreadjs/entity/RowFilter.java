
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * 行过滤器
 * 
 */
public class RowFilter {

    /**
     * 未测
     * 
     */
    @Valid
    private Range range;
    /**
     * 未测
     * 
     */
    @Valid
    private List<Object> filteredColMap = new ArrayList<Object>();
    /**
     * 未测
     * 
     */
    @Valid
    private List<Object> filterItemMap = new ArrayList<Object>();

    /**
     * 未测
     * 
     */
    public Range getRange() {
        return range;
    }

    /**
     * 未测
     * 
     */
    public void setRange(Range range) {
        this.range = range;
    }

    /**
     * 未测
     * 
     */
    public List<Object> getFilteredColMap() {
        return filteredColMap;
    }

    /**
     * 未测
     * 
     */
    public void setFilteredColMap(List<Object> filteredColMap) {
        this.filteredColMap = filteredColMap;
    }

    /**
     * 未测
     * 
     */
    public List<Object> getFilterItemMap() {
        return filterItemMap;
    }

    /**
     * 未测
     * 
     */
    public void setFilterItemMap(List<Object> filterItemMap) {
        this.filterItemMap = filterItemMap;
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
