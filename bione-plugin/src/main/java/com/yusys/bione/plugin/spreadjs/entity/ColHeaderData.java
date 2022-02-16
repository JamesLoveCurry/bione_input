package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 列表头信息
 * 
 */
public class ColHeaderData {

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
     * 数据表单
     * 
     */
    @Valid
    private DataTable dataTable;
    /**
     * name
     * 
     */
    private String name;
    /**
     * rowDataArray
     * 
     */
    @Valid
    private List<Object> rowDataArray = new ArrayList<Object>();
    /**
     * columnDataArray
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
     * name
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * name
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * rowDataArray
     * 
     */
    public List<Object> getRowDataArray() {
        return rowDataArray;
    }

    /**
     * rowDataArray
     * 
     */
    public void setRowDataArray(List<Object> rowDataArray) {
        this.rowDataArray = rowDataArray;
    }

    /**
     * columnDataArray
     * 
     */
    public List<Object> getColumnDataArray() {
        return columnDataArray;
    }

    /**
     * columnDataArray
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
