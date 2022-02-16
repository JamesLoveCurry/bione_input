
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 表格样式管理对象集合
 * 
 */
public class TableManager {

    /**
     * 表格样式数组
     * 
     */
    @Valid
    private List<TableProperty> tables = new ArrayList<TableProperty>();

    /**
     * 表格样式数组
     * 
     */
    public List<TableProperty> getTables() {
        return tables;
    }

    /**
     * 表格样式数组
     * 
     */
    public void setTables(List<TableProperty> tables) {
        this.tables = tables;
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
