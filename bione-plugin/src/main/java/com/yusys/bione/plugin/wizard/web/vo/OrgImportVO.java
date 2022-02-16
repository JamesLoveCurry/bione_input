package com.yusys.bione.plugin.wizard.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;

import java.io.Serializable;

@SuppressWarnings("serial")
@ExcelSheet(index = "0", name = "报表机构")
public class OrgImportVO implements Serializable, AnnotationValidable {
    @ExcelColumn(index = "A", name = "对应行内机构（与机构编码一致）")
    private String mgrOrgNo;
    @BioneFieldValid(nullable = false)
    @ExcelColumn(index = "B", name = "机构名称")
    private String orgNm;
    @BioneFieldValid(nullable = false)
    @ExcelColumn(index = "C", name = "机构编码")
    private String orgNo;
    @ExcelColumn(index = "D", name = "机构类型", relDs={"com.yusys.bione.frame.variable.entity.BioneParamInfo","paramValue","paramName"," PARAM_TYPE_NO ='reportorgtype'"})
    private String orgType;
    @ExcelColumn(index = "E", name = "上级机构编码")
    private String upOrgNo;
    @ExcelColumn(index = "F", name = "金融机构编码(银监会)")
    private String financeOrgNo;
    /*@BioneFieldValid(nullable=false,comboVals={"01", "02"})
    @ExcelColumn(index = "D", name = "机构汇总类型", value = { "01", "02"}, text = { "基础行", "汇总行"})
    private String orgSumType;*/
    @ExcelColumn(index = "G", name = "是否参与汇总", value = {"01", "02"}, text = {"是", "否"})
    private String orgSumType;
    @ExcelColumn(index = "H", name = "是否虚拟机构",value = {"Y","N"},text = {"是","否"})
    private String isVirtualOrg;
    @ExcelColumn(index = "I", name = "机构定义类型")
    private String orgClass;
    @ExcelColumn(index="J",name = "机构层级")
    private String orgLevel;
    @ExcelColumn(index = "K",name = "是否是报送机构",value = {"Y","N"},text = {"是","否"})
    private String isOrgReport;
    @ExcelColumn(index = "L",name = "金融机构LEI编码")
    private String leiNo;
    @ExcelColumn(index = "M",name = "关联归属地",relDs={"com.yusys.bfd.codeConfig.entity.BfdRptStdCodeInfo","item","itemName"," rpt_type ='04' and code_type='EAST_ADDR'"})
    private String addr;
    @ExcelColumn(index = "N",name = "父机构编号")
    private String namespace;
    @ExcelColumn(index = "O",name = "地区名称(人行)")
    private String rhOrgNm;
    @ExcelColumn(index = "P",name = "地区编码(人行)")
    private String rhOrgNo;
    @ExcelColumn(index = "Q",name = "人行机构编码(人行)")
    private String dtrctNo;

    private int excelRowNo;
    private String sheetName;



    public String getRhOrgNm() {
        return rhOrgNm;
    }

    public void setRhOrgNm(String rhOrgNm) {
        this.rhOrgNm = rhOrgNm;
    }

    public String getRhOrgNo() {
        return rhOrgNo;
    }

    public void setRhOrgNo(String rhOrgNo) {
        this.rhOrgNo = rhOrgNo;
    }

    public String getDtrctNo() {
        return dtrctNo;
    }

    public void setDtrctNo(String dtrctNo) {
        this.dtrctNo = dtrctNo;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getOrgNm() {
        return orgNm;
    }

    public void setOrgNm(String orgNm) {
        this.orgNm = orgNm;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getUpOrgNo() {
        return upOrgNo;
    }

    public void setUpOrgNo(String upOrgNo) {
        this.upOrgNo = upOrgNo;
    }

    public String getMgrOrgNo() {
        return mgrOrgNo;
    }

    public void setMgrOrgNo(String mgrOrgNo) {
        this.mgrOrgNo = mgrOrgNo;
    }

    public String getFinanceOrgNo() {
        return financeOrgNo;
    }

    public void setFinanceOrgNo(String financeOrgNo) {
        this.financeOrgNo = financeOrgNo;
    }

    public String getOrgSumType() {
        return orgSumType;
    }

    public void setOrgSumType(String orgSumType) {
        this.orgSumType = orgSumType;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getIsVirtualOrg() {
        return isVirtualOrg;
    }

    public void setIsVirtualOrg(String isVirtualOrg) {
        this.isVirtualOrg = isVirtualOrg;
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

    public String getOrgClass() {
        return orgClass;
    }

    public void setOrgClass(String orgClass) {
        this.orgClass = orgClass;
    }

    public String getIsOrgReport() {
        return isOrgReport;
    }

    public void setIsOrgReport(String isOrgReport) {
        this.isOrgReport = isOrgReport;
    }

    public String getLeiNo() {
        return leiNo;
    }

    public void setLeiNo(String leiNo) {
        this.leiNo = leiNo;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(String orgLevel) {
        this.orgLevel = orgLevel;
    }


}

