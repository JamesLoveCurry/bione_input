/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.design.entity.RptDesignQueryDetail;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@ExcelSheet(index="2",name="查询条件",firstRow=1,insertType ="01",extType = "01")
public class RptDesignQueryDetailVO implements Serializable{

	private static final long serialVersionUID = -5600649708596306002L;
	
	private String templateId;
	private BigDecimal verId;
	private BigDecimal orderNum; 
	private String dsId;
	private String columnId;
	@ExcelColumn(index = "3", name = "控件类型",value={"01","02","03","04","05","06","07","08","11"},text={"文本","数字","单选下拉框","弹出复选框","时间点","时间区间","复选下拉框","数字区间","弹出单选框"})
	private String elementType;
	@ExcelColumn(index = "1", name = "英文字段名")
	private String enNm;
	private String cnNm;
	@ExcelColumn(index = "2", name = "维度类型")
	private String dimTypeNo;
	private String dbType;
	private String dimTypeStruct;
	
	@ExcelColumn(index = "0", name = "查询名称")
	private String display;
	@ExcelColumn(index = "5", name = "是否必填",value={"true","false"},text={"是","否"})
	private String requied;
	@ExcelColumn(index = "4", name = "默认值")
	private String value;
	
	/**
	 * 
	 */
	public RptDesignQueryDetailVO() {
		super();
	}
	
	/**
	 * 
	 */
	public RptDesignQueryDetailVO(RptDesignQueryDetail detail , RptSysModuleCol col , RptDimTypeInfo type) {
		BeanUtils.copy(detail, this);
		BeanUtils.copy(col, this);
		this.setTemplateId(detail.getId().getTemplateId());
		this.setVerId(detail.getId().getVerId());
		this.setOrderNum(detail.getId().getOrderNum());
		if(type != null){
			this.setDimTypeStruct(type.getDimTypeStruct());
		}
	}


	/**
	 * @return 返回 templateId。
	 */
	public String getTemplateId() {
		return templateId;
	}
	/**
	 * @param templateId 设置 templateId。
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	/**
	 * @return 返回 verId。
	 */
	public BigDecimal getVerId() {
		return verId;
	}
	/**
	 * @param verId 设置 verId。
	 */
	public void setVerId(BigDecimal verId) {
		this.verId = verId;
	}
	/**
	 * @return 返回 orderNum。
	 */
	public BigDecimal getOrderNum() {
		return orderNum;
	}
	/**
	 * @param orderNum 设置 orderNum。
	 */
	public void setOrderNum(BigDecimal orderNum) {
		this.orderNum = orderNum;
	}
	/**
	 * @return 返回 dsId。
	 */
	public String getDsId() {
		return dsId;
	}
	/**
	 * @param dsId 设置 dsId。
	 */
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}
	/**
	 * @return 返回 columnId。
	 */
	public String getColumnId() {
		return columnId;
	}
	/**
	 * @param columnId 设置 columnId。
	 */
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	/**
	 * @return 返回 elementType。
	 */
	public String getElementType() {
		return elementType;
	}
	/**
	 * @param elementType 设置 elementType。
	 */
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	/**
	 * @return 返回 enNm。
	 */
	public String getEnNm() {
		return enNm;
	}
	/**
	 * @param enNm 设置 enNm。
	 */
	public void setEnNm(String enNm) {
		this.enNm = enNm;
	}
	/**
	 * @return 返回 cnNm。
	 */
	public String getCnNm() {
		return cnNm;
	}
	/**
	 * @param cnNm 设置 cnNm。
	 */
	public void setCnNm(String cnNm) {
		this.cnNm = cnNm;
	}
	/**
	 * @return 返回 dimTypeNo。
	 */
	public String getDimTypeNo() {
		return dimTypeNo;
	}
	/**
	 * @param dimTypeNo 设置 dimTypeNo。
	 */
	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}
	/**
	 * @return 返回 dbType。
	 */
	public String getDbType() {
		return dbType;
	}
	/**
	 * @param dbType 设置 dbType。
	 */
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	/**
	 * @return 返回 dimTypeStruct。
	 */
	public String getDimTypeStruct() {
		return dimTypeStruct;
	}

	/**
	 * @param dimTypeStruct 设置 dimTypeStruct。
	 */
	public void setDimTypeStruct(String dimTypeStruct) {
		this.dimTypeStruct = dimTypeStruct;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getRequied() {
		return requied;
	}

	public void setRequied(String requied) {
		this.requied = requied;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
