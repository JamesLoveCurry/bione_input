
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 数据存储对象
 * 
 */
public class Data {

    /**
     * 行
     * 
     */
    private Integer rowCount;
    /**
     * 列
     * 
     */
    private Integer colCount;
    /**
     * 标签页名称
     * 
     */
    private String name;
    /**
     * 数据表单
     * 
     */
    @Valid
    private DataTable dataTable;
    /**
     * 行数据样式集合
     * 
     */
    @Valid
    private List<Object> rowDataArray = new ArrayList<Object>();
    /**
     * 列数据样式集合
     * 
     */
    @Valid
    private List<Object> columnDataArray = new ArrayList<Object>();
    /**
     * 缺省节点样式集合
     * 
     */
    @JSONField(name="_defaultDataNode")
    private DefaultDataNode defaultDataNode;

    /**
     * 行
     * 
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 行
     * 
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 列
     * 
     */
    public Integer getColCount() {
        return colCount;
    }

    /**
     * 列
     * 
     */
    public void setColCount(Integer colCount) {
        this.colCount = colCount;
    }

    /**
     * 标签页名称
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 标签页名称
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 数据表单
     * 
     */
    public DataTable getDataTable() {
        return dataTable;
    }

    /**
     * 数据表单
     * 
     */
    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    /**
     * 行数据样式集合
     * 
     */
    public List<Object> getRowDataArray() {
        return rowDataArray;
    }

    /**
     * 行数据样式集合
     * 
     */
    public void setRowDataArray(List<Object> rowDataArray) {
        this.rowDataArray = rowDataArray;
    }

    /**
     * 列数据样式集合
     * 
     */
    public List<Object> getColumnDataArray() {
        return columnDataArray;
    }

    /**
     * 列数据样式集合
     * 
     */
    public void setColumnDataArray(List<Object> columnDataArray) {
        this.columnDataArray = columnDataArray;
    }

    /**
     * 缺省节点样式集合
     * 
     */
    public DefaultDataNode getDefaultDataNode() {
        return defaultDataNode;
    }

    /**
     * 缺省节点样式集合
     * 
     */
    public void setDefaultDataNode(DefaultDataNode defaultDataNode) {
        this.defaultDataNode = defaultDataNode;
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
