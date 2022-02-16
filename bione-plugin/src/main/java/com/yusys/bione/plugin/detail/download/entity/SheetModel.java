package com.yusys.bione.plugin.detail.download.entity;

import java.util.List;

/**
 * 解析结果封装类
 * 
 * @Date 2021/5/18 21:22       
 * @author baifk
 **/
public class SheetModel {
    // 多少行
    public long numberOfRows;
    // 多少列
    public long numberOfColumns;
    // 解析多久时间
    public long timeToProcess;
    // 解析结果
    public List<String[]> list;
    // 解析sheet页名称
    public String curSheetName;

    public String getCurSheetName() {
        return curSheetName;
    }

    public void setCurSheetName(String curSheetName) {
        this.curSheetName = curSheetName;
    }

    public List<String[]> getList() {
        return list;
    }

    public void setList(List<String[]> list) {
        this.list = list;
    }

    public long getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(long numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public long getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(long numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public long getTimeToProcess() {
        return timeToProcess;
    }

    public void setTimeToProcess(long timeToProcess) {
        this.timeToProcess = timeToProcess;
    }
}
