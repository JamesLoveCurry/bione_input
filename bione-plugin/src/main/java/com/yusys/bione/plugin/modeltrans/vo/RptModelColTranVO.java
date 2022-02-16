package com.yusys.bione.plugin.modeltrans.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.modeltrans.entity.RptSysModuleColTran;

@SuppressWarnings("serial")
public class RptModelColTranVO extends RptSysModuleColTran{
	
	private String setId;
	
	private String tableEnNm;
	
	private String setNm;
	
	private String colEnNm;
	
	private String srcSetId;
	
	private String srcTableEnNm;
	
	private String srcSetNm;
	
	private String srcColEnNm;
	
	private String srcDataFilterCond;
	
	
	
	public RptModelColTranVO(String tableEnNm,String colEnNm,String srcColEnNm,String srcSetId,RptSysModuleColTran info){
		BeanUtils.copy(info, this);
		this.setTableEnNm(tableEnNm);
		this.setColEnNm(colEnNm);
		this.setSrcColEnNm(srcColEnNm);
		this.setSrcSetId(srcSetId);
	}
	
	public RptModelColTranVO(String setId,String tableEnNm,String setNm,String srcSetId,String srcTableEnNm,String srcSetNm,String srcDataFilterCond){
		this.setSetId(setId);
		this.setTableEnNm(tableEnNm);
		this.setSetNm(setNm);
		this.setSrcSetId(srcSetId);
		this.setSrcTableEnNm(srcTableEnNm);
		this.setSrcSetNm(srcSetNm);
		this.setSrcDataFilterCond(srcDataFilterCond);
	}

	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getTableEnNm() {
		return tableEnNm;
	}

	public void setTableEnNm(String tableEnNm) {
		this.tableEnNm = tableEnNm;
	}

	public String getSetNm() {
		return setNm;
	}

	public void setSetNm(String setNm) {
		this.setNm = setNm;
	}

	public String getColEnNm() {
		return colEnNm;
	}

	public void setColEnNm(String colEnNm) {
		this.colEnNm = colEnNm;
	}

	public String getSrcSetId() {
		return srcSetId;
	}

	public void setSrcSetId(String srcSetId) {
		this.srcSetId = srcSetId;
	}

	public String getSrcTableEnNm() {
		return srcTableEnNm;
	}

	public void setSrcTableEnNm(String srcTableEnNm) {
		this.srcTableEnNm = srcTableEnNm;
	}

	public String getSrcSetNm() {
		return srcSetNm;
	}

	public void setSrcSetNm(String srcSetNm) {
		this.srcSetNm = srcSetNm;
	}

	public String getSrcColEnNm() {
		return srcColEnNm;
	}

	public void setSrcColEnNm(String srcColEnNm) {
		this.srcColEnNm = srcColEnNm;
	}

	public String getSrcDataFilterCond() {
		return srcDataFilterCond;
	}

	public void setSrcDataFilterCond(String srcDataFilterCond) {
		this.srcDataFilterCond = srcDataFilterCond;
	}
	
}
