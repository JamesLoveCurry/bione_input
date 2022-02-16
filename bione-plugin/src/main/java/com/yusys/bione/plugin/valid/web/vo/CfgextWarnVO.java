package com.yusys.bione.plugin.valid.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @项目名称： 统一监管报送
 * @类名称： CfgextWarnVO
 * @类描述:
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2021/07/09 14:23
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
@SuppressWarnings("serial")
@ExcelSheet(index = "0", name = "预警校验公式", firstRow = 1)
public class CfgextWarnVO implements Serializable, AnnotationValidable {

    @BioneFieldValid(length = 100)
    @ExcelColumn(index = "A", name = "报表名称", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String rptNm;

    @BioneFieldValid(length = 100)
    @ExcelColumn(index = "B", name = "校验ID", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String checkId;

    @BioneFieldValid(length = 100)
    @ExcelColumn(index = "C", name = "校验名称", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String checkNm;

    @BioneFieldValid(length = 100)
    @ExcelColumn(index = "D", name = "指标单元格", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String indexNo;

    @BioneFieldValid(length = 2)
    @ExcelColumn(index = "E", name = "预警类型", value={"01","02","03","04","05","06","07","08","09"},
            text={"环比","同比","较上日","较月初","较上月末","较季初","较上季末","较年初","较上年末"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String compareType;

    @BioneFieldValid(length = 2)
    @ExcelColumn(index = "F", name = "幅度类型", value={"01","02"},
            text={"数字","百分比"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String rangeType;

    @BioneFieldValid(length = 2)
    @ExcelColumn(index = "G", name = "单位", value={"01","02","03","04","05"},
            text={"元","百","千","万","亿"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String unit;

    @BioneFieldValid(length = 100)
    @ExcelColumn(index = "H", name = "预警波动下限", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private BigDecimal minusRangeVal;

    @BioneFieldValid(length = 100)
    @ExcelColumn(index = "I", name = "预警波动上限", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private BigDecimal postiveRangeVal;

    @BioneFieldValid(length = 2)
    @ExcelColumn(index = "J", name = "监管要求", value={"01","02"}, text={"是","否"}, fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String isFrs;

    @BioneFieldValid(length = 20)
    @ExcelColumn(index = "K", name = "开始时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String startDate;

    @BioneFieldValid(length = 20)
    @ExcelColumn(index = "L", name = "结束时间", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String endDate;

    @ExcelColumn(index = "M", name = "备注", fontHeightInPoint = 10, alignment = HorizontalAlignment.LEFT)
    private String remark;

    private String cfgId;

    private String busiType;

    private String rptCycle;

    private int excelRowNo;

    private String sheetName;

    public String getRptNm() {
        return rptNm;
    }

    public void setRptNm(String rptNm) {
        this.rptNm = rptNm;
    }

    public String getCfgId() {
        return cfgId;
    }

    public void setCfgId(String cfgId) {
        this.cfgId = cfgId;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public String getCheckNm() {
        return checkNm;
    }

    public void setCheckNm(String checkNm) {
        this.checkNm = checkNm;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getCompareType() {
        return compareType;
    }

    public void setCompareType(String compareType) {
        this.compareType = compareType;
    }

    public BigDecimal getMinusRangeVal() {
        return minusRangeVal;
    }

    public void setMinusRangeVal(BigDecimal minusRangeVal) {
        this.minusRangeVal = minusRangeVal;
    }

    public BigDecimal getPostiveRangeVal() {
        return postiveRangeVal;
    }

    public void setPostiveRangeVal(BigDecimal postiveRangeVal) {
        this.postiveRangeVal = postiveRangeVal;
    }

    public String getIsFrs() {
        return isFrs;
    }

    public void setIsFrs(String isFrs) {
        this.isFrs = isFrs;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getRptCycle() {
        return rptCycle;
    }

    public void setRptCycle(String rptCycle) {
        this.rptCycle = rptCycle;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getExcelRowNo() {
        return excelRowNo;
    }

    public void setExcelRowNo(int excelRowNo) {
        this.excelRowNo = excelRowNo;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getRangeType() {
        return rangeType;
    }

    public void setRangeType(String rangeType) {
        this.rangeType = rangeType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
