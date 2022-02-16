package com.yusys.bione.plugin.access.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

@ExcelSheet(index="0",name="报表访问统计")
public class DeptUserAccessVO {

	
	private String rptId;    	//报表Id
	
	@ExcelColumn(index="A",name="报表名称")
	private String rptNm;       //报表名称
	
	@ExcelColumn(index="B",name="访问次数")
	private String pv;
	
	@ExcelColumn(index="C",name="访问人数")
	private String uv;

	public String getRptId() {
		return rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getRptNm() {
		return rptNm;
	}

	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}

	public String getUv() {
		return uv;
	}

	public void setUv(String uv) {
		this.uv = uv;
	}
	
	
	
}
