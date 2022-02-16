
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 表格样式
 * 
 */
public class TableProperty {

    /**
     * 目前猜想是自动生成的，例如是第一个表就是：表1;含2表的话，名字分别是表1，表2
     * 
     */
    private String name;
    /**
     * 起始单元格的行数，不包括表头，计数从0开始
     * 
     */
    private Integer row;
    /**
     * 起始单元格的列数，不包括表头，计数从0开始
     * 
     */
    private Integer col;
    /**
     * 表格包含的行数
     * 
     */
    private Integer rowCount;
    /**
     * 表格包含的列数
     * 
     */
    private Integer colCount;
    /**
     * 是否显示标题
     * 
     */
    private Boolean showHeader;
    /**
     * 是否显示结尾
     * 
     */
    private Boolean showFooter;
    /**
     * 是否高亮第一列
     * 
     */
    private Boolean highlightFirstColumn;
    /**
     * 是否高亮最后一列
     * 
     */
    private Boolean highlightLastColumn;
    /**
     * 未测
     * 
     */
    private Boolean bandRows;
    /**
     * 未测
     * 
     */
    private Boolean bandColumns;
    /**
     * 行过滤器
     * 
     */
    @Valid
    private RowFilter rowFilter;
    /**
     * tableColums
     * 
     */
    @Valid
    private List<Object> columns = new ArrayList<Object>();

    /**
     * 目前猜想是自动生成的，例如是第一个表就是：表1;含2表的话，名字分别是表1，表2
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 目前猜想是自动生成的，例如是第一个表就是：表1;含2表的话，名字分别是表1，表2
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 起始单元格的行数，不包括表头，计数从0开始
     * 
     */
    public Integer getRow() {
        return row;
    }

    /**
     * 起始单元格的行数，不包括表头，计数从0开始
     * 
     */
    public void setRow(Integer row) {
        this.row = row;
    }

    /**
     * 起始单元格的列数，不包括表头，计数从0开始
     * 
     */
    public Integer getCol() {
        return col;
    }

    /**
     * 起始单元格的列数，不包括表头，计数从0开始
     * 
     */
    public void setCol(Integer col) {
        this.col = col;
    }

    /**
     * 表格包含的行数
     * 
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 表格包含的行数
     * 
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 表格包含的列数
     * 
     */
    public Integer getColCount() {
        return colCount;
    }

    /**
     * 表格包含的列数
     * 
     */
    public void setColCount(Integer colCount) {
        this.colCount = colCount;
    }

    /**
     * 是否显示标题
     * 
     */
    public Boolean getShowHeader() {
        return showHeader;
    }

    /**
     * 是否显示标题
     * 
     */
    public void setShowHeader(Boolean showHeader) {
        this.showHeader = showHeader;
    }

    /**
     * 是否显示结尾
     * 
     */
    public Boolean getShowFooter() {
        return showFooter;
    }

    /**
     * 是否显示结尾
     * 
     */
    public void setShowFooter(Boolean showFooter) {
        this.showFooter = showFooter;
    }

    /**
     * 是否高亮第一列
     * 
     */
    public Boolean getHighlightFirstColumn() {
        return highlightFirstColumn;
    }

    /**
     * 是否高亮第一列
     * 
     */
    public void setHighlightFirstColumn(Boolean highlightFirstColumn) {
        this.highlightFirstColumn = highlightFirstColumn;
    }

    /**
     * 是否高亮最后一列
     * 
     */
    public Boolean getHighlightLastColumn() {
        return highlightLastColumn;
    }

    /**
     * 是否高亮最后一列
     * 
     */
    public void setHighlightLastColumn(Boolean highlightLastColumn) {
        this.highlightLastColumn = highlightLastColumn;
    }

    /**
     * 未测
     * 
     */
    public Boolean getBandRows() {
        return bandRows;
    }

    /**
     * 未测
     * 
     */
    public void setBandRows(Boolean bandRows) {
        this.bandRows = bandRows;
    }

    /**
     * 未测
     * 
     */
    public Boolean getBandColumns() {
        return bandColumns;
    }

    /**
     * 未测
     * 
     */
    public void setBandColumns(Boolean bandColumns) {
        this.bandColumns = bandColumns;
    }

    /**
     * 行过滤器
     * 
     */
    public RowFilter getRowFilter() {
        return rowFilter;
    }

    /**
     * 行过滤器
     * 
     */
    public void setRowFilter(RowFilter rowFilter) {
        this.rowFilter = rowFilter;
    }

    /**
     * tableColums
     * 
     */
    public List<Object> getColumns() {
        return columns;
    }

    /**
     * tableColums
     * 
     */
    public void setColumns(List<Object> columns) {
        this.columns = columns;
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
