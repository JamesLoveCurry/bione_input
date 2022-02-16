package com.yusys.bione.plugin.regulation.vo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Data {

	/**
	 * 合法的业务条线名称=>业务条线ID
	 */
	private Map<String, String> busiLineMap;

	/**
	 * 合法的一般单元格单元格类型=>一般单元格单元格类型ID
	 */
	private Map<String, String> comCellTypeMap;

	/**
	 * 合法的维度类型名称=>维度类型号
	 */
	private Map<String, String> dimTypeMap;

	/**
	 * 合法的维度类型号=>维度项编号=>维度项名称（因为目前有相同维度名称不同维度编号的维度项存在）
	 */
	private Map<String, Map<String, String>> dimItemMap;

	/**
	 * 指标计算规则名称 => 指标计算规则ID
	 */
	private Map<String, String> idxCalcRuleMap = new HashMap<String, String>();
	
	/**
	 * 指标时间度量名称 => 指标时间度量ID
	 */
	private Map<String, String> idxTimeMeasureMap = new HashMap<String, String>();
	
	/**
	 * 指标取值方式名称 => 指标取值方式ID
	 */
	private Map<String, String> idxValTypeMap = new HashMap<String, String>();

	/**
	 * rptNm、rptNum=>template
	 */
	private Map<String, Template> templateMap;

	/**
	 * 指标单元格、指标列表单元格、报表指标类公式单元格，template=>cellNo=>IndexBaseCell
	 */
	private Map<Template, Map<String, IndexBaseCell>> cellNoToCellMap;

	/**
	 * 指标单元格、指标列表单元格、报表指标类公式单元格，template=>cellNm=>IndexBaseCell
	 */
	private Map<Template, Map<String, IndexBaseCell>> cellNmToCellMap;

	/**
	 * 度量集合
	 */
	private Set<String> measureNoSet = new HashSet<String>();

	/**
	 * catalogNm=>catalogId
	 */
	private Map<String, String> rptCataLogMap= new HashMap<String, String>();
	
	/**
	 * 合法的业务条线名称=>业务条线ID
	 */
	public Map<String, String> getBusiLineMap() {
		return busiLineMap;
	}

	/**
	 * 合法的业务条线名称=>业务条线ID
	 */
	public void setBusiLineMap(Map<String, String> busiLineMap) {
		this.busiLineMap = busiLineMap;
	}

	/**
	 * 合法的一般单元格单元格类型=>一般单元格单元格类型ID
	 */
	public Map<String, String> getComCellTypeMap() {
		return comCellTypeMap;
	}

	/**
	 * 合法的一般单元格单元格类型=>一般单元格单元格类型ID
	 */
	public void setComCellTypeMap(Map<String, String> comCellTypeMap) {
		this.comCellTypeMap = comCellTypeMap;
	}

	/**
	 * 合法的维度类型名称=>维度类型号
	 */
	public Map<String, String> getDimTypeMap() {
		return dimTypeMap;
	}

	/**
	 * 合法的维度类型名称=>维度类型号
	 */
	public void setDimTypeMap(Map<String, String> dimTypeMap) {
		this.dimTypeMap = dimTypeMap;
	}

	/**
	 * 合法的维度类型号=>维度项名称=>维度项编号（因为目前有相同维度名称不同维度编号的维度项存在）
	 */
	public Map<String, Map<String, String>> getDimItemMap() {
		return dimItemMap;
	}

	/**
	 * 合法的维度类型号=>维度项名称=>维度项编号（因为目前有相同维度名称不同维度编号的维度项存在）
	 */
	public void setDimItemMap(Map<String, Map<String, String>> dimItemMap) {
		this.dimItemMap = dimItemMap;
	}

	/**
	 * 指标计算规则名称 => 指标计算规则ID
	 */
	public Map<String, String> getIdxCalcRuleMap() {
		return idxCalcRuleMap;
	}

	/**
	 * 指标计算规则名称 => 指标计算规则ID
	 */
	public void setIdxCalcRuleMap(Map<String, String> idxCalcRuleMap) {
		this.idxCalcRuleMap = idxCalcRuleMap;
	}

	/**
	 * 指标时间度量名称 => 指标时间度量ID
	 */
	public Map<String, String> getIdxTimeMeasureMap() {
		return idxTimeMeasureMap;
	}

	/**
	 * 指标时间度量名称 => 指标时间度量ID
	 */
	public void setIdxTimeMeasureMap(Map<String, String> idxTimeMeasureMap) {
		this.idxTimeMeasureMap = idxTimeMeasureMap;
	}

	/**
	 * 指标取值方式名称 => 指标取值方式ID
	 */
	public Map<String, String> getIdxValTypeMap() {
		return idxValTypeMap;
	}

	/**
	 * 指标取值方式名称 => 指标取值方式ID
	 */
	public void setIdxValTypeMap(Map<String, String> idxValTypeMap) {
		this.idxValTypeMap = idxValTypeMap;
	}

	/**
	 * rptNm、rptNum=>template
	 */
	public Map<String, Template> getTemplateMap() {
		return templateMap;
	}

	/**
	 * rptNm、rptNum=>template
	 */
	public void setTemplateMap(Map<String, Template> templateMap) {
		this.templateMap = templateMap;
	}

	/**
	 * 指标单元格、指标列表单元格、报表指标类公式单元格，template=>cellNo=>IndexBaseCell
	 */
	public Map<Template, Map<String, IndexBaseCell>> getCellNoToCellMap() {
		return cellNoToCellMap;
	}

	/**
	 * 指标单元格、指标列表单元格、报表指标类公式单元格，template=>cellNo=>IndexBaseCell
	 */
	public void setCellNoToCellMap(Map<Template, Map<String, IndexBaseCell>> cellNoToCellMap) {
		this.cellNoToCellMap = cellNoToCellMap;
	}

	/**
	 * 指标单元格、指标列表单元格、报表指标类公式单元格，template=>cellNm=>IndexBaseCell
	 */
	public Map<Template, Map<String, IndexBaseCell>> getCellNmToCellMap() {
		return cellNmToCellMap;
	}

	/**
	 * 指标单元格、指标列表单元格、报表指标类公式单元格，template=>cellNm=>IndexBaseCell
	 */
	public void setCellNmToCellMap(Map<Template, Map<String, IndexBaseCell>> cellNmToCellMap) {
		this.cellNmToCellMap = cellNmToCellMap;
	}

	/**
	 * 度量集合
	 */
	public Set<String> getMeasureNoSet() {
		return measureNoSet;
	}

	/**
	 * 度量集合
	 */
	public void setMeasureNoSet(Set<String> measureNoSet) {
		this.measureNoSet = measureNoSet;
	}

	public Map<String, String> getRptCataLogMap() {
		return rptCataLogMap;
	}

	public void setRptCataLogMap(Map<String, String> rptCataLogMap) {
		this.rptCataLogMap = rptCataLogMap;
	}
	
	
}
