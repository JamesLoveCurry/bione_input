package com.yusys.bione.plugin.access.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

@ExcelSheet(index="0",name="指标访问统计")
public class IdxUserAccessVO {
	
	private String indexNo;    	//指标编号
	
	private String indexVerId;
	
	@ExcelColumn(index="A",name="指标名称")
	private String indexNm; 	//指标名称
	
//	@ExcelColumn(index="B",name="指标类别")
//	private String indexType; 	//指标类别
	
//	@ExcelColumn(index="C",name="定义部门")
//	private String defDept;       //定义部门
//	
//	@ExcelColumn(index="D",name="频度")
//	private String calcCycle;    //频度
	
//	@ExcelColumn(index="E",name="启用时间")
//	private String startDate;   //启用时间
	
	@ExcelColumn(index="B",name="点击人数")
	private String uv;    //点击人数
	
	@ExcelColumn(index="C",name="点击次数")
	private String pv;         //点击次数
	
//	@ExcelColumn(index="G",name="访问频度（天）")
//	private String freper;		//访问频度（天）
	
	public String getIndexNo() {
		return indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public String getIndexNm() {
		return indexNm;
	}
	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}
	public String getUv() {
		return uv;
	}
	public void setUv(String uv) {
		this.uv = uv;
	}
	public String getPv() {
		return pv;
	}
	public void setPv(String pv) {
		this.pv = pv;
	}
	public String getIndexVerId() {
		return indexVerId;
	}
	public void setIndexVerId(String indexVerId) {
		this.indexVerId = indexVerId;
	}
	
}
