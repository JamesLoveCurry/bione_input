package com.yusys.bione.frame.syslog.web.vo;

import java.math.BigDecimal;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;

@ExcelSheet(index="0",name="系统功能使用统计")
public class BioneLogFuncVO {
	@ExcelColumn(index="A",name="功能模块")
	private String funcName;
	@ExcelColumn(index="B",name="访问用户数")
	private BigDecimal uv;
	private String menuId;
	@ExcelColumn(index="C",name="本年累计访问次数")
	private BigDecimal pv;
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	public BigDecimal getUv() {
		return uv;
	}
	public void setUv(BigDecimal uv) {
		this.uv = uv;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public BigDecimal getPv() {
		return pv;
	}
	public void setPv(BigDecimal pv) {
		this.pv = pv;
	}
	
}