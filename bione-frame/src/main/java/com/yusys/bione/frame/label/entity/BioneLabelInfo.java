package com.yusys.bione.frame.label.entity;

import java.io.Serializable;

import javax.persistence.*;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.annotation.BioneFieldValid;
import com.yusys.bione.frame.validator.common.AnnotationValidable;


/**
 * The persistent class for the BIONE_LABEL_INFO database table.
 * 
 */
@Entity
@Table(name="BIONE_LABEL_INFO")
@NamedQuery(name="BioneLabelInfo.findAll", query="SELECT b FROM BioneLabelInfo b")
@ExcelSheet(index = "0",name="标签信息")
public class BioneLabelInfo implements Serializable,AnnotationValidable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="LABEL_ID")
	private String labelId;

	@Column(name="LABEL_NAME")
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "A", name = "标签名称")
	private String labelName;
	
	@Column(name="UP_ID")
	@ExcelColumn(index = "B", name = "上级标签")
	private String upId;

	@Column(name="LABEL_OBJ_ID")
	@BioneFieldValid(nullable=false)
	@ExcelColumn(index = "C", name = "标签类型",relDs={"com.yusys.bione.frame.label.entity.BioneLabelObjInfo","labelObjId","labelObjName","LOGIC_SYS_NO = 'CEMR'"})
	private String labelObjId;
	
	@ExcelColumn(index = "D", name = "备注")
	private String remark;
	
	@Transient
	private Integer excelRowNo;
	
	@Transient
	private String sheetName;

	public BioneLabelInfo() {
	}

	public String getLabelId() {
		return this.labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public String getLabelName() {
		return this.labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getLabelObjId() {
		return this.labelObjId;
	}

	public void setLabelObjId(String labelObjId) {
		this.labelObjId = labelObjId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpId() {
		return this.upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
	}

	public Integer getExcelRowNo() {
		return excelRowNo;
	}

	public void setExcelRowNo(Integer excelRowNo) {
		this.excelRowNo = excelRowNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

}